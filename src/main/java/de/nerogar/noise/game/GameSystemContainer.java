package de.nerogar.noise.game;

import de.nerogar.noise.event.EventHub;
import de.nerogar.noiseInterface.game.*;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GameSystemContainer implements IGameSystemContainer {

	private final EventHub                                           eventHub;
	private final List<IGameSystem>                                  gameSystemList;
	private final Map<Class<? extends IGameSystem>, IGameSystem>     gameSystemMap;
	private final Map<Class<? extends IGamePipeline>, IGamePipeline> gamePipelineMap;
	private final Map<Class<?>, Object>                              injectionObjects;

	public GameSystemContainer(EventHub eventHub) {
		this.eventHub = eventHub;
		gameSystemList = new ArrayList<>();
		gameSystemMap = new HashMap<>();
		gamePipelineMap = new HashMap<>();
		injectionObjects = new HashMap<>();
	}

	public void addGameSystem(IGameSystem gameSystem) {
		gameSystemList.add(gameSystem);
		gameSystemMap.put(gameSystem.getClass(), gameSystem);
	}

	public void addGamePipeline(IGamePipeline<?> gamePipeline) {
		gamePipelineMap.put(gamePipeline.getClass(), gamePipeline);
	}

	public void addInjectionObject(Object object) {
		injectionObjects.put(object.getClass(), object);
	}

	private void addDefaultInjectionObjects() {
		addInjectionObject(this);
		addInjectionObject(eventHub);
	}

	public void startInjection() {
		addDefaultInjectionObjects();

		for (IGameSystem gameSystem : gameSystemList) {
			for (Method method : gameSystem.getClass().getDeclaredMethods()) {
				if (method.isAnnotationPresent(Inject.class)) {
					Object[] params = new Object[method.getParameterCount()];
					Class<?>[] parameterTypes = method.getParameterTypes();

					for (int i = 0; i < parameterTypes.length; i++) {
						if (gameSystemMap.containsKey(parameterTypes[i])) {
							params[i] = gameSystemMap.get(parameterTypes[i]);
						} else if (gamePipelineMap.containsKey(parameterTypes[i])) {
							params[i] = gamePipelineMap.get(parameterTypes[i]);
						} else if (injectionObjects.containsKey(parameterTypes[i])) {
							params[i] = injectionObjects.get(parameterTypes[i]);
						}
					}

					try {
						method.invoke(gameSystem, params);
					} catch (IllegalAccessException | InvocationTargetException e) {
						e.printStackTrace();
					}
				} else if (method.isAnnotationPresent(Pipeline.class)) {
					Object[] params = new Object[method.getParameterCount()];
					Class<?>[] parameterTypes = method.getParameterTypes();

					IGamePipeline<?> pipeline = gamePipelineMap.get(method.getAnnotation(Pipeline.class).value());
					pipeline.register(gameSystem);
				}
			}
		}

	}

	@SuppressWarnings("unchecked")
	public <T extends IGameSystem> T getGameSystem(Class<T> gameSystemClass) {
		return (T) gameSystemMap.get(gameSystemClass);
	}

	public EventHub getEventHub() {
		return eventHub;
	}

}
