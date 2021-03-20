package de.nerogar.noise.oldGame.core.events;

import de.nerogar.noiseInterface.event.IEvent;
import de.nerogar.noise.oldGame.SystemSyncParameter;

public class SystemSyncEvent implements IEvent {

	private SystemSyncParameter syncParameter;

	public SystemSyncEvent(SystemSyncParameter syncParameter) {
		this.syncParameter = syncParameter;
	}

	public SystemSyncParameter getSyncParameter() {
		return syncParameter;
	}
}
