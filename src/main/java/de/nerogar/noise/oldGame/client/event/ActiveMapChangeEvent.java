package de.nerogar.noise.oldGame.client.event;

import de.nerogar.noise.event.IEvent;
import de.nerogar.noise.oldGame.CoreMap;

public class ActiveMapChangeEvent implements IEvent {

	private CoreMap oldMap;
	private CoreMap newMap;

	public ActiveMapChangeEvent(CoreMap oldMap, CoreMap newMap) {
		this.oldMap = oldMap;
		this.newMap = newMap;
	}

	public CoreMap getOldMap() {
		return oldMap;
	}

	public CoreMap getNewMap() {
		return newMap;
	}

}
