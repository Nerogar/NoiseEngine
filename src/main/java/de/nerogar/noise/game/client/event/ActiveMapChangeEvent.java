package de.nerogar.noise.game.client.event;

import de.nerogar.noise.event.Event;
import de.nerogar.noise.game.CoreMap;

public class ActiveMapChangeEvent implements Event {

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
