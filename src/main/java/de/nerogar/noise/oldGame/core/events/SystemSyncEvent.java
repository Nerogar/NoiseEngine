package de.nerogar.noise.oldGame.core.events;

import de.nerogar.noise.event.Event;
import de.nerogar.noise.oldGame.SystemSyncParameter;

public class SystemSyncEvent implements Event {

	private SystemSyncParameter syncParameter;

	public SystemSyncEvent(SystemSyncParameter syncParameter) {
		this.syncParameter = syncParameter;
	}

	public SystemSyncParameter getSyncParameter() {
		return syncParameter;
	}
}
