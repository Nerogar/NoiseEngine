package de.nerogar.noise.oldGame;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Initialization steps:
 *   1. Constructor is called for client and server
 *   2. init() is called for client and server
 *       - data loading should be done here
 *       - sync functions can be registered here
 *   3. sendNetworkInit() is called on the server
 *   4. networkInit() is called on the client with the data sent by the server
 *   5. setSystemData() is called on the client and server with the map data
 *       - map data is loaded. Synchronization should now be done with sync functions.
 */
public abstract class SynchronizedSystem extends LogicSystem {

	private String name;
	private short id = -1;

	private Map<Class<? extends SystemSyncParameter>, Consumer<? extends SystemSyncParameter>> syncFunctions;

	public SynchronizedSystem(String name) {
		this.name = name;

		syncFunctions = new HashMap<>();
	}

	public void setId(short id) {
		this.id = id;
	}

	public short getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	@SuppressWarnings("unchecked")
	public <T extends SystemSyncParameter> Consumer<T> getSyncFunction(Class<T> syncParameterClass) { return (Consumer<T>) syncFunctions.get(syncParameterClass); }

	public abstract void sendNetworkInit(DataOutputStream out) throws IOException;

	public abstract void networkInit(DataInputStream in) throws IOException;

	public void callSyncFunction(SystemSyncParameter syncParameter) {
		syncParameter.setSystemId(getId());
		getNetworkAdapter().send(syncParameter);
	}

	public <T extends SystemSyncParameter> void registerSyncFunction(Class<T> parameterClass, Consumer<T> function) {
		syncFunctions.put(parameterClass, function);
	}

}
