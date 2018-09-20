package de.nerogar.noise.game;

import de.nerogar.noise.event.EventListener;
import de.nerogar.noise.event.EventManager;
import de.nerogar.noise.game.core.events.SystemSyncEvent;
import de.nerogar.noise.game.core.network.packets.InitSystemContainerPacket;
import de.nerogar.noise.game.core.network.packets.InitSystemPacket;
import de.nerogar.noise.network.INetworkAdapter;
import de.nerogar.noise.serialization.NDSNodeObject;
import de.nerogar.noise.util.Logger;

import java.io.IOException;
import java.util.*;
import java.util.function.Consumer;

public abstract class SystemContainer implements Sided {

	private boolean initialized;

	private Map<Class<? extends LogicSystem>, LogicSystem> systemClassMap;
	private Map<String, SynchronizedSystem>                systemNameMap;
	private Map<Short, SynchronizedSystem>                 systemIdMap;
	private Set<LogicSystem>                               systemSet;

	private Map<Short, InitSystemPacket> systemInitPackets;
	private List<LogicSystem>            systemInitList;

	private INetworkAdapter networkAdapter;

	private EventManager                   eventManager;
	private EventListener<SystemSyncEvent> systemSyncListener;

	public SystemContainer(EventManager eventManager, INetworkAdapter networkAdapter) {
		this.networkAdapter = networkAdapter;
		this.eventManager = eventManager;

		systemClassMap = new HashMap<>();
		systemNameMap = new HashMap<>();
		systemInitPackets = new HashMap<>();
		systemSet = new HashSet<>();

		systemInitList = new ArrayList<>();

		systemSyncListener = this::systemSyncListenerFunction;
		eventManager.registerImmediate(SystemSyncEvent.class, systemSyncListener);
	}

	@SuppressWarnings("unchecked")
	public <C extends LogicSystem> C getSystem(Class<C> systemClass) {
		if (!initialized) throw new RuntimeException("System container (" + getName() + ") not initialized!");
		return (C) systemClassMap.get(systemClass);
	}

	protected abstract void addSystems();

	protected void addSystem(LogicSystem system) {
		system.setObjects(this, networkAdapter, eventManager);

		Class<?> systemClass = system.getClass();
		while (systemClass != null) {
			systemClassMap.put((Class<? extends LogicSystem>) systemClass, system);
			systemClass = systemClass.getSuperclass();
		}

		if (system instanceof SynchronizedSystem) {
			SynchronizedSystem synchronizedSystem = (SynchronizedSystem) system;
			systemNameMap.put(synchronizedSystem.getName(), synchronizedSystem);
		}
		systemSet.add(system);

		systemInitList.add(system);
	}

	@SuppressWarnings("unchecked")
	private <T extends SystemSyncParameter> void systemSyncListenerFunction(SystemSyncEvent event) {
		if (!systemIdMap.containsKey(event.getSyncParameter().getSystemId())) return;

		T parameter = (T) event.getSyncParameter();
		SynchronizedSystem system = systemIdMap.get(parameter.getSystemId());
		Consumer<T> syncFunction = system.getSyncFunction((Class<T>) parameter.getClass());
		syncFunction.accept(parameter);
	}

	public void initContainer(InitSystemContainerPacket packet) {
		if (!packet.getContainerName().equals(getName())) return;
		initialized = true;

		Map<String, Short> systemIdMap = packet.getSystemIdMap();

		addSystems();
		this.systemIdMap = new HashMap<>();
		for (Map.Entry<String, SynchronizedSystem> systemEntry : systemNameMap.entrySet()) {
			SynchronizedSystem synchronizedSystem = systemEntry.getValue();
			Short id = systemIdMap.get(systemEntry.getKey());
			this.systemIdMap.put(id, synchronizedSystem);
		}

		// init all systems (init + networkInit)
		for (LogicSystem system : systemInitList) {
			system.init();
			if (system instanceof SynchronizedSystem) {
				SynchronizedSystem synchronizedSystem = (SynchronizedSystem) system;
				if (systemInitPackets.containsKey(systemIdMap.get(synchronizedSystem.getName()))) {
					InitSystemPacket initSystemPacket = systemInitPackets.get(systemIdMap.get(synchronizedSystem.getName()));
					try {
						synchronizedSystem.networkInit(initSystemPacket.getInput());
					} catch (IOException e) {
						NoiseGame.logger.log(Logger.ERROR, "Could not initialize System: " + system);
						e.printStackTrace(NoiseGame.logger.getErrorStream());
					}
				}
			}
		}

	}

	public void initSystem(InitSystemPacket packet) {
		systemInitPackets.put(packet.getSystemId(), packet);
	}

	/**
	 * a String needed to uniquely identify system containers across the network
	 *
	 * @return a string for identification
	 */
	public abstract String getName();

	public void initSystems() {
		initialized = true;
		addSystems();
		for (LogicSystem logicSystem : systemSet) {
			logicSystem.init();
		}

		if (systemIdMap != null) return;

		systemIdMap = new HashMap<>();

		for (LogicSystem system : systemSet) {
			if (system instanceof SynchronizedSystem) {
				short id = generateID();
				SynchronizedSystem synchronizedSystem = (SynchronizedSystem) system;
				synchronizedSystem.setId(id);
				this.systemIdMap.put(id, synchronizedSystem);
			}
		}

		for (SynchronizedSystem synchronizedSystem : systemIdMap.values()) {
			InitSystemPacket initSystemPacket = new InitSystemPacket(synchronizedSystem);
			networkAdapter.send(initSystemPacket);
		}

		networkAdapter.send(new InitSystemContainerPacket(getName(), systemIdMap));
	}

	public void setSystemData(NDSNodeObject systemData) {
		for (LogicSystem logicSystem : systemSet) {
			logicSystem.setSystemData(systemData);
		}
	}

	public void initSystemsWithData() {
		for (LogicSystem logicSystem : systemSet) {
			logicSystem.initWithData();
		}
	}

	public void cleanup() {
		eventManager.unregister(SystemSyncEvent.class, systemSyncListener);

		for (LogicSystem system : systemSet) {
			system.cleanup();
		}
	}

	private static short MAX_ID = 0;

	private static short generateID() {
		return MAX_ID++;
	}

}
