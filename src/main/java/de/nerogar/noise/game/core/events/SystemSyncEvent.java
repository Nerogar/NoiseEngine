package de.nerogar.noise.game.core.events;

import de.nerogar.noise.event.Event;
import de.nerogar.noise.game.SystemSyncParameter;

public class SystemSyncEvent implements Event {

	private SystemSyncParameter syncParameter;

	public SystemSyncEvent(SystemSyncParameter syncParameter) {
		this.syncParameter = syncParameter;
	}

	public SystemSyncParameter getSyncParameter() {
		return syncParameter;
	}
}
