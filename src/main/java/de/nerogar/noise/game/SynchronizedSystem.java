package de.nerogar.noise.game;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public abstract class SynchronizedSystem extends LogicSystem {

	private short id = -1;

	private Map<Class<? extends SystemSyncParameter>, Consumer<? extends SystemSyncParameter>> syncFunctions;

	public SynchronizedSystem() {
		syncFunctions = new HashMap<>();
	}

	public void setId(short id) {
		this.id = id;
	}

	public short getId() {
		return id;
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
