package de.nerogar.noise.game;

import de.nerogar.noise.event.EventManager;
import de.nerogar.noise.network.INetworkAdapter;

public abstract class CoreMap implements Sided {

	// north -> negative z
	// east  -> positive x
	public static final byte DIRECTION_N     = (byte) 0b0001_0000;
	public static final byte DIRECTION_S     = (byte) 0b0010_0000;
	public static final byte DIRECTION_W     = (byte) 0b0100_0000;
	public static final byte DIRECTION_E     = (byte) 0b1000_0000;
	public static final byte DIRECTION_NEG_Z = DIRECTION_N;
	public static final byte DIRECTION_POS_Z = DIRECTION_S;
	public static final byte DIRECTION_NEG_X = DIRECTION_W;
	public static final byte DIRECTION_POS_X = DIRECTION_E;
	public static final byte DIRECTION_ALL   = (byte) 0b1111_0000;
	public static final byte CORNER_NW       = (byte) 0b0000_0001;
	public static final byte CORNER_NE       = (byte) 0b0000_0010;
	public static final byte CORNER_SW       = (byte) 0b0000_0100;
	public static final byte CORNER_SE       = (byte) 0b0000_1000;
	public static final byte CORNER_ALL      = (byte) 0b0000_1111;

	private boolean initialized = false;

	private final int id;

	protected INetworkAdapter networkAdapter;
	private   EventManager    eventManager;

	private MapSystemContainer<?>  systemContainer;
	private GameSystemContainer<?> gameSystemContainer;

	private EntityList          entityList;

	/*
	 * 3d indices are calculated as:
	 * index = ((z * dimX + x) * dimY) + y
	 *
	 * 2d indices are calculated as:
	 * index = z * dimX + x
	 */

	public CoreMap(int id, INetworkAdapter networkAdapter, EventManager eventManager) {
		this.id = id;

		this.networkAdapter = networkAdapter;
		this.eventManager = eventManager;
	}

	public void setSystemContainer(MapSystemContainer<?> systemContainer, GameSystemContainer<?> gameSystemContainer) {
		this.systemContainer = systemContainer;
		this.gameSystemContainer = gameSystemContainer;
	}

	public void initMeta() {
		this.entityList = new EntityList(this);
	}

	public void init(short[] blocks, byte[] blockShape) {
		if (initialized) return;

		initialized = true;
	}

	public int getId()                                { return id; }

	public INetworkAdapter getNetworkAdapter()        { return networkAdapter; }

	public EventManager getEventManager()             { return eventManager; }

	public MapSystemContainer<?> getSystemContainer() { return systemContainer; }

	@SuppressWarnings("unchecked")
	public <C extends LogicSystem> C getSystem(Class<C> systemClass) {
		return systemContainer.getSystem(systemClass);
	}

	@SuppressWarnings("unchecked")
	public <C extends LogicSystem> C getGameSystem(Class<C> systemClass) {
		return gameSystemContainer.getSystem(systemClass);
	}

	public EntityList getEntityList()             { return entityList; }

	public Entity getEntity(int id) {
		return entityList.get(id);
	}

	public void removeEntity(int id) {
		entityList.remove(id);
	}

	public void cleanup(){

	}

}
