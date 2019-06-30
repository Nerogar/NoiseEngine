package de.nerogar.noise.game.client;

import de.nerogar.noise.event.EventManager;
import de.nerogar.noise.game.CoreMap;
import de.nerogar.noise.game.client.gui.GuiContainer;
import de.nerogar.noise.input.InputHandler;
import de.nerogar.noise.network.INetworkAdapter;
import de.nerogar.noise.render.GLWindow;

import java.util.List;

public abstract class Controller<MAP_T extends CoreMap> {

	protected GLWindow        window;
	protected InputHandler    inputHandler;
	protected EventManager    eventManager;
	protected List<MAP_T>     maps;
	protected INetworkAdapter networkAdapter;
	protected GuiContainer    guiContainer;

	public Controller(GLWindow window, EventManager eventManager, List<MAP_T> maps, INetworkAdapter networkAdapter, GuiContainer guiContainer) {
		this.window = window;
		this.inputHandler = window.getInputHandler();
		this.eventManager = eventManager;
		this.maps = maps;
		this.networkAdapter = networkAdapter;
		this.guiContainer = guiContainer;
	}

	public abstract void update(float timeDelta);

}
