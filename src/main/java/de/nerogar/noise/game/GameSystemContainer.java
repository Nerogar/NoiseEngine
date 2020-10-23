package de.nerogar.noise.game;

import de.nerogar.noise.event.EventManager;
import de.nerogar.noiseInterface.game.IGameSystem;
import de.nerogar.noiseInterface.game.IGameSystemContainer;
import de.nerogar.noiseInterface.game.InjectionMethod;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class GameSystemContainer implements IGameSystemContainer {

	private final EventManager                                   eventManager;
	private final Map<Class<? extends IGameSystem>, IGameSystem> gameSystems;
	private final Map<Class<?>, Object>                          injectionObjects;

	public GameSystemContainer(EventManager eventManager) {
		this.eventManager = eventManager;
		gameSystems = new HashMap<>();
		injectionObjects = new HashMap<>();
	}

	public void addGameSystem(IGameSystem gameSystem) {
		gameSystems.put(gameSystem.getClass(), gameSystem);
		gameSystem.setSystemContainer(this);
	}

	public void addInjectionObject(Object object) {
		injectionObjects.put(object.getClass(), object);
	}

	private void addDefaultInjectionObjects() {
		addInjectionObject(this);
	}

	public void startInjection() {
		addDefaultInjectionObjects();

		for (IGameSystem gameSystem : gameSystems.values()) {
			for (Method method : gameSystem.getClass().getDeclaredMethods()) {
				if (method.isAnnotationPresent(InjectionMethod.class)) {
					Object[] params = new Object[method.getParameterCount()];
					Class<?>[] parameterTypes = method.getParameterTypes();

					for (int i = 0; i < parameterTypes.length; i++) {
						if (gameSystems.containsKey(parameterTypes[i])) {
							params[i] = gameSystems.get(parameterTypes[i]);
						} else if (injectionObjects.containsKey(parameterTypes[i])) {
							params[i] = injectionObjects.get(parameterTypes[i]);
						}
					}

					try {
						method.invoke(gameSystem, params);
					} catch (IllegalAccessException | InvocationTargetException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}

	@SuppressWarnings("unchecked")
	public <T extends IGameSystem> T getGameSystem(Class<T> gameSystemClass) {
		return (T) gameSystems.get(gameSystemClass);
	}

	public EventManager getEventManager() {
		return eventManager;
	}

}
