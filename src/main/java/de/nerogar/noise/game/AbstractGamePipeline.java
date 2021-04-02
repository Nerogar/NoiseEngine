package de.nerogar.noise.game;

import de.nerogar.noise.Noise;
import de.nerogar.noise.event.EventHub;
import de.nerogar.noise.util.Logger;
import de.nerogar.noiseInterface.event.IEvent;
import de.nerogar.noiseInterface.event.IEventListener;
import de.nerogar.noiseInterface.game.*;

import java.lang.invoke.CallSite;
import java.lang.invoke.LambdaMetafactory;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Method;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public abstract class AbstractGamePipeline<T extends IEvent> implements IGamePipeline<T> {

	private List<IGameSystem> systems;

	private       boolean              isDirty;
	private final List<PipelineMethod> methods;
	private       PipelineMethod       activeMethod;

	private EventHub eventHub;

	public AbstractGamePipeline(EventHub eventHub) {
		this.eventHub = eventHub;

		systems = new ArrayList<>();
		methods = new ArrayList<>();
	}

	@Override
	public void register(IGameSystem system) {
		if (systems.contains(system)) {
			return;
		}

		systems.add(system);
		isDirty = true;
	}

	@Override
	public void trigger(T event) {
		if (isDirty) {
			try {
				build();
			} catch (Throwable throwable) {
				throwable.printStackTrace(Noise.getLogger().getErrorStream());
				Noise.getLogger().log(Logger.ERROR, "Could not construct pipeline " + this.getClass().getName());
				throw new RuntimeException("Could not construct pipeline " + this.getClass().getName());
			}
		}

		for (PipelineMethod pipelineMethod : methods) {
			activeMethod = pipelineMethod;

			long t0 = System.nanoTime();

			if (pipelineMethod.consumer != null) {
				pipelineMethod.consumer.accept(event);
			} else if (pipelineMethod.biConsumer != null) {
				pipelineMethod.biConsumer.accept(event, this);
			}

			long t1 = System.nanoTime();

			System.out.println(pipelineMethod.name + ": " + ((t1 - t0) / 1_000_000d));
		}
	}

	@Override
	public IEventTrigger getEventTrigger(Class<? extends IEvent> eventClass) {
		return activeMethod.eventTriggers.get(eventClass);
	}

	private void build() throws Throwable {
		List<PipelineMethod> pipelineMethods = new ArrayList<>();

		// create list of all pipeline methods
		for (Object object : systems) {
			for (Method method : object.getClass().getDeclaredMethods()) {
				if (method.isAnnotationPresent(Pipeline.class) && method.getAnnotation(Pipeline.class).value().equals(this.getClass())) {
					PipelineMethod pipelineMethod = new PipelineMethod();
					if (method.getParameterCount() == 1) {
						pipelineMethod.consumer = createPipelineMethod(object, method);
					} else {
						pipelineMethod.biConsumer = createPipelineMethodWithEvents(object, method);
					}

					pipelineMethod.eventTriggers = new HashMap<>();
					pipelineMethod.producedEvents = new HashSet<>();
					pipelineMethod.consumedEvents = new HashSet<>();
					pipelineMethod.name = object.getClass().getName() + "." + method.getName();

					if (method.isAnnotationPresent(ProducesEvent.class)) {
						pipelineMethod.producedEvents.addAll(Arrays.asList(method.getAnnotation(ProducesEvent.class).value()));
					}

					for (ConsumesEvent annotation : method.getAnnotationsByType(ConsumesEvent.class)) {
						pipelineMethod.producedEvents.add(annotation.event());

						boolean found = false;
						for (Method eventMethod : object.getClass().getMethods()) {
							if (eventMethod.getName().equals(annotation.method())) {
								found = true;
								pipelineMethod.eventTriggers.put(annotation.event(), new EventTrigger<>(annotation.event(), createEventListener(object, eventMethod)));
							}
						}
						if (!found) {
							throw new RuntimeException("Could not find event method " + annotation.method() + " in class " + object.getClass().getName() + ". Event methods must be public.");
						}
					}

					pipelineMethods.add(pipelineMethod);
				}
			}
		}

		// sort the list
		pipelineMethods.sort((a, b) -> {
			boolean aFirst = false;
			boolean bFirst = false;

			// if a produces an event that is consumed by b, a is evaluated first
			for (Class<? extends IEvent> producedEvent : a.producedEvents) {
				if (b.consumedEvents.contains(producedEvent)) {
					aFirst = true;
					break;
				}
			}

			// if b produces an event that is consumed by a, b is evaluated first
			for (Class<? extends IEvent> producedEvent : a.producedEvents) {
				if (b.consumedEvents.contains(producedEvent)) {
					bFirst = true;
					break;
				}
			}

			if (aFirst && bFirst) {
				throw new RuntimeException("Could not construct pipeline " + this.getClass().getName() + ". Cyclic event dependencies detected.");
			} else if (aFirst) {
				return -1;
			} else if (bFirst) {
				return 1;
			} else {
				return 0;
			}
		});

		// finalize
		methods.clear();
		for (PipelineMethod pipelineMethod : pipelineMethods) {
			methods.add(pipelineMethod);
		}
		isDirty = false;
	}

	@SuppressWarnings("unchecked")
	private <E extends IEvent> IEventListener<E> createEventListener(Object object, Method method)
			throws Throwable {

		MethodHandles.Lookup lookup = MethodHandles.lookup();
		CallSite callSite = LambdaMetafactory.metafactory(
				lookup,
				"accept",
				MethodType.methodType(BiConsumer.class),
				MethodType.methodType(void.class, Object.class, Object.class),
				lookup.findVirtual(object.getClass(), method.getName(), MethodType.methodType(void.class, method.getParameterTypes()[0])),
				MethodType.methodType(void.class, object.getClass(), method.getParameterTypes()[0])
		                                                 );

		BiConsumer<Object, E> biConsumer = (BiConsumer<Object, E>) callSite.getTarget().invokeExact();
		return (event) -> biConsumer.accept(object, event);
	}

	@SuppressWarnings("unchecked")
	private Consumer<T> createPipelineMethod(Object object, Method method)
			throws Throwable {

		MethodHandles.Lookup lookup = MethodHandles.lookup();
		CallSite callSite = LambdaMetafactory.metafactory(
				lookup,
				"accept",
				MethodType.methodType(BiConsumer.class),
				MethodType.methodType(void.class, Object.class, Object.class),
				lookup.findVirtual(object.getClass(), method.getName(), MethodType.methodType(void.class, method.getParameterTypes()[0])),
				MethodType.methodType(void.class, object.getClass(), method.getParameterTypes()[0])
		                                                 );

		BiConsumer<Object, T> biConsumer = (BiConsumer<Object, T>) callSite.getTarget().invokeExact();
		return (event) -> biConsumer.accept(object, event);
	}

	@SuppressWarnings("unchecked")
	private BiConsumer<T, IGamePipeline<T>> createPipelineMethodWithEvents(Object object, Method method)
			throws Throwable {

		MethodHandles.Lookup lookup = MethodHandles.lookup();
		CallSite callSite = LambdaMetafactory.metafactory(
				lookup,
				"accept",
				MethodType.methodType(TriConsumer.class),
				MethodType.methodType(void.class, Object.class, Object.class, Object.class),
				lookup.findVirtual(object.getClass(), method.getName(), MethodType.methodType(void.class, method.getParameterTypes()[0], method.getParameterTypes()[1])),
				MethodType.methodType(void.class, object.getClass(), method.getParameterTypes()[0], method.getParameterTypes()[1])
		                                                 );

		TriConsumer<Object, T, IGamePipeline<T>> triConsumer = (TriConsumer<Object, T, IGamePipeline<T>>) callSite.getTarget().invokeExact();
		return (T event, IGamePipeline<T> pipeline) -> triConsumer.accept(object, event, pipeline);
	}

	private interface TriConsumer<T1, T2, T3> {

		void accept(T1 t1, T2 t2, T3 t3);
	}

	private class PipelineMethod {

		public Consumer<T>                                 consumer;
		public BiConsumer<T, IGamePipeline<T>>             biConsumer;
		public HashSet<Class<? extends IEvent>>            producedEvents;
		public HashSet<Class<? extends IEvent>>            consumedEvents;
		public String                                      name;
		public Map<Class<? extends IEvent>, IEventTrigger> eventTriggers;
	}

	private class EventTrigger<E extends IEvent> implements IEventTrigger {

		private final Class<E>          eventClass;
		private final IEventListener<E> listener;

		public EventTrigger(Class<E> eventClass, IEventListener<E> listener) {
			this.eventClass = eventClass;
			this.listener = listener;
		}

		@Override
		public void trigger() {
			eventHub.triggerListener(eventClass, listener);
		}
	}

}
