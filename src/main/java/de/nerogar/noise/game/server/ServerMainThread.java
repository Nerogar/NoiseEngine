package de.nerogar.noise.game.server;

import de.nerogar.noise.event.EventManager;
import de.nerogar.noise.game.*;
import de.nerogar.noise.game.core.MapLoader;
import de.nerogar.noise.game.core.events.StartGameEvent;
import de.nerogar.noise.game.core.events.UpdateEvent;
import de.nerogar.noise.game.core.network.packets.*;
import de.nerogar.noise.network.Connection;
import de.nerogar.noise.network.INetworkAdapter;
import de.nerogar.noise.network.Packet;
import de.nerogar.noise.network.ServerThread;
import de.nerogar.noise.util.Color;
import de.nerogar.noise.util.Logger;
import de.nerogar.noise.util.Timer;

import java.net.BindException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class ServerMainThread<
		MAP_T extends CoreMap,
		GAME_SYSTEM_CONTAINER_T extends GameSystemContainer,
		MAP_SYSTEM_CONTAINER_T extends MapSystemContainer<MAP_T>,
		FACTION_SYSTEM_CONTAINER_T extends FactionSystemContainer,
		FACTION_MAP_SYSTEM_CONTAINER_T extends FactionMapSystemContainer
		> extends Thread {

	private final int   TICK_RATE;
	private       float speedFactor = 1.0f;

	private boolean serverRunning;

	private boolean isRunning;

	private EventManager eventManager;

	// network
	private ServerThread     serverThread;
	private INetworkAdapter  networkAdapter;
	private List<Connection> connections;

	// current active game session
	private GAME_SYSTEM_CONTAINER_T                    gameSystemContainer;
	private List<MAP_SYSTEM_CONTAINER_T>               mapSystemContainers;
	private List<FACTION_SYSTEM_CONTAINER_T>           factionSystemContainers;
	private List<List<FACTION_MAP_SYSTEM_CONTAINER_T>> factionMapSystemContainers;
	private List<MAP_T>                                currentMaps;
	private List<Faction>                              factions;

	// loading
	private Map<Connection, ClientMapLoadProgress> clientMapLoadProgressMap;
	private boolean                                serverMapLoadDone;
	private MapLoader                              serverMapLoader;

	public ServerMainThread(int tickRate) {
		this.TICK_RATE = tickRate;

		try {
			serverThread = new ServerThread(34543);
			networkAdapter = serverThread.getNetworkAdapter(NoiseGame.NETWORK_ADAPTER_DEFAULT);
		} catch (BindException e) {
			e.printStackTrace();
		}

		eventManager = new EventManager("ServerMain");

		connections = new ArrayList<>();
		clientMapLoadProgressMap = new HashMap<>();

		setName("ServerMainThread");
		setDaemon(true);
		serverRunning = true;
		start();
	}

	public float getSpeedFactor() {
		return speedFactor;
	}

	public void setSpeedFactor(float speedFactor) {
		this.speedFactor = speedFactor;
	}

	protected abstract MapLoader createMapLoader(List<MAP_T> currentMaps, String mapID, ServerThread serverThread, List<Faction> factions);

	protected abstract GAME_SYSTEM_CONTAINER_T createGameSystemContainer(EventManager eventManager, INetworkAdapter networkAdapter);

	protected abstract MAP_SYSTEM_CONTAINER_T createMapSystemContainer(MAP_T map);

	protected abstract FACTION_SYSTEM_CONTAINER_T createFactionSystemContainer(EventManager eventManager, Faction faction);

	protected abstract FACTION_MAP_SYSTEM_CONTAINER_T createFactionMapSystemContainer(MAP_T map, Faction faction);

	private void loop(float timeDelta) {
		processMapLoader();
		processPackets();

		if (currentMaps != null && isRunning) {
			eventManager.trigger(new UpdateEvent(timeDelta * speedFactor));
		}

		for (Connection connection : connections) {
			connection.flushPackets();
		}
	}

	private void processMapLoader() {
		if (serverMapLoader != null) {
			if (serverMapLoader.isDone()) {
				serverMapLoader.finalizeLoad();
				initSystemsWithData();
				serverMapLoadDone = true;
				serverMapLoader = null;
			}
		}

		if (serverMapLoadDone) {

			boolean done = true;
			for (Connection c : connections) {
				ClientMapLoadProgress clientMapLoadProgress = clientMapLoadProgressMap.get(c);
				if (clientMapLoadProgress == null || !clientMapLoadProgress.isDone()) done = false;
			}

			if (done) {
				sendStart();
				serverMapLoadDone = false;
			}

		}

	}

	private void processPackets() {
		connections.removeIf(Connection::isClosed);

		if (currentMaps == null) {
			connections.addAll(serverThread.getNewConnections());
		} else {
			// game is running, close all new connections
			for (Connection connection : serverThread.getNewConnections()) {
				connection.close();
			}
		}

		for (Connection connection : connections) {
			// TODO maybe some packets can get lost here on the first poll
			connection.pollPackets(false);

			for (Packet p : networkAdapter.getPackets(NoiseGame.CONTROL_PACKET_CHANNEL)) {

				if (p instanceof ClientPacket) {
					ClientPacket packet = (ClientPacket) p;

					int command = packet.getCommand();

					if (command == ClientPacket.COMMAND_START) {
						startCommand(packet.getMapID());
					} else if (command == ClientPacket.COMMAND_STOP) {
						stopCommand();
					} else if (command == ClientPacket.COMMAND_PAUSE) {
						pauseCommand();
					} else if (command == ClientPacket.COMMAND_RESUME) {
						resumeCommand();
					}
				} else if (p instanceof ClientMapLoadProgress) {
					ClientMapLoadProgress packet = (ClientMapLoadProgress) p;

					clientMapLoadProgressMap.put(connection, packet);
				}
			}
		}

	}

	private void initSystemsWithData() {
		gameSystemContainer.initSystemsWithData();
		for (MapSystemContainer mapSystemContainer : mapSystemContainers) {
			mapSystemContainer.initSystemsWithData();
		}
		for (FactionSystemContainer factionSystemContainer : factionSystemContainers) {
			factionSystemContainer.initSystemsWithData();
		}
		for (int i = 0; i < factions.size(); i++) {
			for (int j = 0; j < currentMaps.size(); j++) {
				factionMapSystemContainers.get(i).get(j).initSystemsWithData();
			}
		}
	}

	private void startCommand(String mapID) {
		if (currentMaps != null) {
			NoiseGame.logger.log(Logger.WARNING, "start packet received while game is running");
		}

		// TODO for now, only one faction exists, everyone is in that faction
		factions = new ArrayList<>();
		factions.add(new Faction((byte) 1, new Color(0f, 0f, 1f, 1f)));
		for (Connection connection : connections) {
			factions.get(0).addNetworkAdapter(connection.getNetworkAdapter(NoiseGame.NETWORK_ADAPTER_DEFAULT));
		}

		currentMaps = new ArrayList<>();

		// load map meta
		serverMapLoader = createMapLoader(currentMaps, mapID, serverThread, factions);
		serverMapLoader.loadMeta();
		for (MAP_T currentMap : currentMaps) {
			eventManager.addTriggerChild(currentMap.getEventManager());
		}
/*
		// create system containers
		// game system
		gameSystemContainer = new ServerGameSystemContainer(eventManager, networkAdapter, mapSystemContainers);

		// map systems
		mapSystemContainers = new ServerMapSystemContainer[currentMaps.size()];
		for (int i = 0; i < mapSystemContainers.length; i++) {
			mapSystemContainers[i] = new ServerMapSystemContainer(currentMaps.get(i));
			currentMaps.get(i).setSystemContainer(mapSystemContainers[i], gameSystemContainer);
		}

		// faction systems
		factionSystemContainers = new StrategyFactionSystemContainer[factions.length];
		for (int i = 0; i < factions.length; i++) {
			ServerFactionSystemContainer factionSystemContainer = new ServerFactionSystemContainer(eventManager, networkAdapter, factions[i]);
			factions[i].setSystemContainer(factionSystemContainer, gameSystemContainer);

			factionSystemContainers[i] = factionSystemContainer;
		}

		// faction-map systems
		factionMapSystemContainers = new StrategyFactionMapSystemContainer[factions.length][currentMaps.size()];
		for (int i = 0; i < factions.length; i++) {
			for (int j = 0; j < currentMaps.size(); j++) {
				ServerFactionMapSystemContainer factionMapSystemContainer = new ServerFactionMapSystemContainer(currentMaps.get(j), factions[i], j);

				factionMapSystemContainers[i][j] = factionMapSystemContainer;
			}
		}
*/

		// create system containers
		// game system
		gameSystemContainer = createGameSystemContainer(eventManager, networkAdapter);

		// map systems
		mapSystemContainers = new ArrayList<>(currentMaps.size());
		for (int i = 0; i < currentMaps.size(); i++) {
			mapSystemContainers.add(createMapSystemContainer(currentMaps.get(i)));
			currentMaps.get(i).setSystemContainer(mapSystemContainers.get(i), gameSystemContainer);
		}

		// faction systems
		factionSystemContainers = new ArrayList<>(factions.size());
		for (int i = 0; i < factions.size(); i++) {
			FACTION_SYSTEM_CONTAINER_T factionSystemContainer = createFactionSystemContainer(eventManager, factions.get(i));
			factions.get(i).setSystemContainer(factionSystemContainer, gameSystemContainer);

			factionSystemContainers.add(factionSystemContainer);
		}

		// faction-map systems
		factionMapSystemContainers = new ArrayList<>(factions.size());
		for (int i = 0; i < factions.size(); i++) {
			factionMapSystemContainers.add(new ArrayList<>(currentMaps.size()));
			for (int j = 0; j < currentMaps.size(); j++) {
				factionMapSystemContainers.get(i).add(createFactionMapSystemContainer(currentMaps.get(j), factions.get(i)));
			}
		}

		// send faction info
		networkAdapter.send(new FactionInfoPacket(factions, (byte) 0));

		// tell clients which map to load
		networkAdapter.send(new MapInfoPacket(mapID));

		// tell clients to create systems
		networkAdapter.send(new ServerPacket(ServerPacket.COMMAND_CREATE_SYSTEMS));

		// initialize all systems and synchronize them with clients
		gameSystemContainer.initSystems();
		for (MapSystemContainer mapSystemContainer : mapSystemContainers) {
			mapSystemContainer.initSystems();
		}
		for (FactionSystemContainer factionSystemContainer : factionSystemContainers) {
			factionSystemContainer.initSystems();
		}
		for (int i = 0; i < factions.size(); i++) {
			for (int j = 0; j < currentMaps.size(); j++) {
				factionMapSystemContainers.get(i).get(j).initSystems();
			}
		}

		// tell clients to load the map
		networkAdapter.send(new ServerPacket(ServerPacket.COMMAND_LOAD_MAP));

		// load the map
		serverMapLoader.startLoading();
	}

	private void sendStart() {
		// tell clients to initialize the controller
		networkAdapter.send(new ServerPacket(ServerPacket.COMMAND_INIT_CONTROLLER));

		// start the game
		networkAdapter.send(new ServerPacket(ServerPacket.COMMAND_START));
		eventManager.trigger(new StartGameEvent());

		isRunning = true;
	}

	private void stopCommand() {
		NoiseGame.logger.log(Logger.WARNING, "stop command received, not implemented yet");
	}

	private void pauseCommand() {
		NoiseGame.logger.log(Logger.WARNING, "pause command received, not implemented yet");
	}

	private void resumeCommand() {
		NoiseGame.logger.log(Logger.WARNING, "resume command received, not implemented yet");
	}

	@Override
	public void run() {
		Timer timer = new Timer();

		while (serverRunning) {
			timer.update(1f / TICK_RATE);

			loop(timer.getDelta());
		}
	}

}
