package de.nerogar.noise.oldGame.client;

import de.nerogar.noiseInterface.event.IEventListener;
import de.nerogar.noise.event.EventManager;
import de.nerogar.noise.oldGame.*;
import de.nerogar.noise.oldGame.client.event.ActiveMapChangeEvent;
import de.nerogar.noise.oldGame.client.event.RenderEvent;
import de.nerogar.noise.oldGame.client.event.WindowSizeChangeEvent;
import de.nerogar.noise.oldGame.client.gui.GuiContainer;
import de.nerogar.noise.oldGame.client.systems.RenderSystem;
import de.nerogar.noise.oldGame.core.MapLoader;
import de.nerogar.noise.oldGame.core.events.StartGameEvent;
import de.nerogar.noise.oldGame.core.events.SystemSyncEvent;
import de.nerogar.noise.oldGame.core.events.UpdateEvent;
import de.nerogar.noise.oldGame.core.network.packets.*;
import de.nerogar.noise.network.Connection;
import de.nerogar.noise.network.INetworkAdapter;
import de.nerogar.noise.network.Packet;
import de.nerogar.noise.render.GLWindow;
import de.nerogar.noise.render.RenderHelper;
import de.nerogar.noise.util.Color;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL11.*;

public abstract class Client<
		MAP_T extends CoreMap,
		GAME_SYSTEM_CONTAINER_T extends GameSystemContainer,
		MAP_SYSTEM_CONTAINER_T extends MapSystemContainer<MAP_T>,
		FACTION_SYSTEM_CONTAINER_T extends FactionSystemContainer,
		FACTION_MAP_SYSTEM_CONTAINER_T extends FactionMapSystemContainer
		> {

	private Connection      connection;
	private INetworkAdapter networkAdapter;
	private INetworkAdapter wildcardNetworkAdapter;
	private GLWindow        window;
	private EventManager    eventManager;

	private boolean isRunning;

	// current active game session
	private GAME_SYSTEM_CONTAINER_T              gameSystemContainer;
	private List<MAP_SYSTEM_CONTAINER_T>         mapSystemContainers;
	private FACTION_SYSTEM_CONTAINER_T           factionSystemContainer;
	private List<FACTION_MAP_SYSTEM_CONTAINER_T> factionMapSystemContainers;
	private List<MAP_T>                          currentMaps;
	private int                                  activeMapId;
	private List<Faction>                        factions;
	private Faction                              ownFaction;
	private Controller                           controller;

	private IEventListener<ActiveMapChangeEvent> activeMapChangeListener;

	// gui
	private GuiContainer guiContainer;

	private IEventListener<WindowSizeChangeEvent> windowSizeChangeListener;

	private MapLoader clientMapLoader;
	private boolean   clientMapLoaderDone;
	private boolean   systemLoadDone;

	public Client(Connection connection, GLWindow window, EventManager eventManager, GuiContainer guiContainer) {
		this.connection = connection;
		this.networkAdapter = connection.getNetworkAdapter(NoiseGame.NETWORK_ADAPTER_DEFAULT);
		this.wildcardNetworkAdapter = connection.getWildcardNetworkAdapter();
		this.window = window;
		this.eventManager = eventManager;

		this.guiContainer = guiContainer;

		windowSizeChangeListener = this::onWindowResize;
		eventManager.register(WindowSizeChangeEvent.class, windowSizeChangeListener);

		activeMapChangeListener = this::onActiveMapChange;
		eventManager.register(ActiveMapChangeEvent.class, activeMapChangeListener);
	}

	protected abstract MapLoader createMapLoader(List<MAP_T> currentMaps, String mapID, Connection connection, List<Faction> factions);

	protected abstract Controller createController(GLWindow window, EventManager eventManager, List<MAP_T> currentMaps, Faction ownFaction, INetworkAdapter networkAdapter, GuiContainer guiContainer);

	protected abstract GAME_SYSTEM_CONTAINER_T createGameSystemContainer(EventManager eventManager, INetworkAdapter networkAdapter);

	protected abstract MAP_SYSTEM_CONTAINER_T createMapSystemContainer(MAP_T map);

	protected abstract FACTION_SYSTEM_CONTAINER_T createFactionSystemContainer(EventManager eventManager, Faction ownFaction);

	protected abstract FACTION_MAP_SYSTEM_CONTAINER_T createFactionMapSystemContainer(MAP_T map, Faction ownFaction);

	private void onWindowResize(WindowSizeChangeEvent event) {
	}

	private void onActiveMapChange(ActiveMapChangeEvent event) {
		activeMapId = event.getNewMap().getId();
	}

	public void update(float timeDelta) {
		connection.pollPackets(false);

		// gui
		guiContainer.update(window.getInputHandler(), timeDelta);

		// client
		processInit();
		updateServerPackets();
		updateSystemsPackets();
		if (currentMaps != null && isRunning) {
			updateClient(timeDelta);
		}

	}

	private void processInit() {
		if (clientMapLoader != null && clientMapLoader.isDone()) {
			clientMapLoader.finalizeLoad();
			initSystemsWithData();
			clientMapLoader = null;
			clientMapLoaderDone = true;
		}

		if (currentMaps == null) return;
		if (clientMapLoaderDone && !systemLoadDone) {
			boolean loadDone = true;

			for (MAP_SYSTEM_CONTAINER_T mapSystemContainer : mapSystemContainers) {
				for (LogicSystem system : mapSystemContainer.getSystems()) {
					system.doLoadStep();
					if (system.getLoadProgress() < 1) loadDone = false;
				}
			}

			for (LogicSystem system : gameSystemContainer.getSystems()) {
				system.doLoadStep();
				if (system.getLoadProgress() < 1) loadDone = false;
			}

			for (LogicSystem system : factionSystemContainer.getSystems()) {
				system.doLoadStep();
				if (system.getLoadProgress() < 1) loadDone = false;
			}

			for (FACTION_MAP_SYSTEM_CONTAINER_T factionMapSystemContainer : factionMapSystemContainers) {
				for (LogicSystem system : factionMapSystemContainer.getSystems()) {
					system.doLoadStep();
					if (system.getLoadProgress() < 1) loadDone = false;
				}
			}

			if (loadDone) {
				networkAdapter.send(new ClientMapLoadProgress(0, true));
				systemLoadDone = true;
			}
		}
	}

	public void render(float timeDelta) {

		// render map
		if (currentMaps != null) {
			window.bind();
			eventManager.trigger(new RenderEvent());
		}

		// render gui
		guiContainer.render();

		// blit
		window.bind();

		glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

		if (isRunning) displayMap();
		RenderHelper.overlayPremultipliedTexture(guiContainer.getColorOutput());
	}

	private void displayMap() {
		for (int i = 0; i < mapSystemContainers.size(); i++) {
			RenderSystem renderSystem = mapSystemContainers.get(i).getSystem(RenderSystem.class);
			if (renderSystem != null) {
				if (renderSystem.isActive()) {
					RenderHelper.overlayTexture(renderSystem.getRenderer().getColorOutput());
				}
			}

		}
	}

	private void updateServerPackets() {
		List<Packet> packets = wildcardNetworkAdapter.getPackets(NoiseGame.CONTROL_PACKET_CHANNEL);

		for (Packet p : packets) {
			if (p instanceof InitSystemPacket) {
				InitSystemPacket packet = (InitSystemPacket) p;
				initSystem(packet);
				invokeInitEvents();

			} else if (p instanceof InitSystemContainerPacket) {
				InitSystemContainerPacket packet = (InitSystemContainerPacket) p;
				initSystemContainer(packet);

			} else if (p instanceof FactionInfoPacket) {
				FactionInfoPacket packet = (FactionInfoPacket) p;

				factions = new ArrayList<>();
				for (int i = 0; i < packet.getFactionIDs().length; i++) {
					factions.add(new Faction(packet.getFactionIDs()[i], new Color(packet.getFactionColors()[i])));
				}

				ownFaction = factions.get(packet.getOwnFaction());

			} else if (p instanceof MapInfoPacket) {
				MapInfoPacket packet = (MapInfoPacket) p;
				createMap(packet.getMapID());

			} else if (p instanceof ServerPacket) {
				ServerPacket packet = (ServerPacket) p;

				switch (packet.getCommand()) {
					case ServerPacket.COMMAND_CREATE_SYSTEMS:
						createSystems();
						break;
					case ServerPacket.COMMAND_LOAD_MAP:
						loadMap();
						break;
					case ServerPacket.COMMAND_INIT_CONTROLLER:
						setupController();
						break;
					case ServerPacket.COMMAND_START:
						isRunning = true;
						eventManager.trigger(new StartGameEvent());
						break;
				}
			}
		}

	}

	private void initSystemContainer(InitSystemContainerPacket packet) {
		gameSystemContainer.initContainer(packet);
		for (MAP_SYSTEM_CONTAINER_T mapSystemContainer : mapSystemContainers) {
			mapSystemContainer.initContainer(packet);
		}
		for (FACTION_MAP_SYSTEM_CONTAINER_T factionMapSystemContainer : factionMapSystemContainers) {
			factionMapSystemContainer.initContainer(packet);
		}
		factionSystemContainer.initContainer(packet);
	}

	private void initSystem(InitSystemPacket packet) {
		gameSystemContainer.initSystem(packet);
		for (MAP_SYSTEM_CONTAINER_T mapSystemContainer : mapSystemContainers) {
			mapSystemContainer.initSystem(packet);
		}
		for (FACTION_MAP_SYSTEM_CONTAINER_T factionMapSystemContainer : factionMapSystemContainers) {
			factionMapSystemContainer.initSystem(packet);
		}
		factionSystemContainer.initSystem(packet);
	}

	private void initSystemsWithData() {
		gameSystemContainer.initSystemsWithData();
		for (MAP_SYSTEM_CONTAINER_T mapSystemContainer : mapSystemContainers) {
			mapSystemContainer.initSystemsWithData();
		}
		factionSystemContainer.initSystemsWithData();
		for (int i = 0; i < currentMaps.size(); i++) {
			factionMapSystemContainers.get(i).initSystemsWithData();
		}
	}

	private void updateSystemsPackets() {
		List<Packet> packets = wildcardNetworkAdapter.getPackets(NoiseGame.SYSTEMS_PACKET_CHANNEL);

		for (Packet p : packets) {
			if (p instanceof SystemSyncParameter) {
				eventManager.trigger(new SystemSyncEvent((SystemSyncParameter) p));
			}
		}

	}

	private void createSystems() {
		// game system
		gameSystemContainer = createGameSystemContainer(eventManager, networkAdapter);

		// map systems
		mapSystemContainers = new ArrayList<>(currentMaps.size());
		for (int i = 0; i < currentMaps.size(); i++) {
			mapSystemContainers.add(createMapSystemContainer(currentMaps.get(i)));
			currentMaps.get(i).setSystemContainer(mapSystemContainers.get(i), gameSystemContainer);
		}

		// faction system
		factionSystemContainer = createFactionSystemContainer(eventManager, ownFaction);
		ownFaction.setSystemContainer(factionSystemContainer, gameSystemContainer);

		// faction-map systems
		factionMapSystemContainers = new ArrayList<>(currentMaps.size());
		for (int i = 0; i < currentMaps.size(); i++) {
			factionMapSystemContainers.add(createFactionMapSystemContainer(currentMaps.get(i), ownFaction));
		}

	}

	protected void invokeInitEvents() {
		eventManager.trigger(new WindowSizeChangeEvent(window.getWidth(), window.getHeight()));
	}

	private void createMap(String mapID) {
		currentMaps = new ArrayList<>();
		clientMapLoader = createMapLoader(currentMaps, mapID, connection, factions);
		clientMapLoader.loadMeta();

		for (MAP_T currentMap : currentMaps) {
			eventManager.addTriggerChild(currentMap.getEventManager());
		}

	}

	private void loadMap() {
		clientMapLoader.startLoading();
	}

	private void setupController() {
		controller = createController(window, eventManager, currentMaps, ownFaction, networkAdapter, guiContainer);
		//controller = new PlayerController(window, eventManager, currentMaps, ownFaction, networkAdapter, guiContainer);
		//controller = new EditorController(window, eventManager, currentMaps, networkAdapter, guiContainer);
	}

	private void closeMap() {
		for (int i = 0; i < currentMaps.size(); i++) {
			currentMaps.get(i).cleanup();
			eventManager.removeTriggerChild(currentMaps.get(i).getEventManager());
		}

		currentMaps = null;
	}

	private void updateClient(float timeDelta) {
		// input
		controller.update(timeDelta);

		eventManager.trigger(new UpdateEvent(timeDelta));
	}

	public Connection getConnection() {
		return connection;
	}

	private void cleanup() {
		gameSystemContainer.cleanup();
		eventManager.unregister(WindowSizeChangeEvent.class, windowSizeChangeListener);
		eventManager.unregister(ActiveMapChangeEvent.class, activeMapChangeListener);
		closeMap();
	}

}
