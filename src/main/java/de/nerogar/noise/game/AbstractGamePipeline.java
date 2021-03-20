package de.nerogar.noise.game;

import de.nerogar.noise.Noise;
import de.nerogar.noiseInterface.event.IEvent;
import de.nerogar.noiseInterface.game.ConsumesEvent;
import de.nerogar.noiseInterface.game.IGamePipeline;
import de.nerogar.noiseInterface.game.Pipeline;
import de.nerogar.noiseInterface.game.ProducesEvent;

import java.lang.invoke.CallSite;
import java.lang.invoke.LambdaMetafactory;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public abstract class AbstractGamePipeline<T> implements IGamePipeline<T> {

	private List<Object> objects;

	private       boolean           isDirty;
	private final List<Consumer<T>> methods;

	public AbstractGamePipeline() {
		objects = new ArrayList<>();
		methods = new ArrayList<>();
	}

	@Override
	public void register(Object object) {
		if (objects.contains(object)) {
			return;
		}

		objects.add(object);
		isDirty = true;
	}

	@Override
	public void trigger(T t) {
		if (isDirty) {
			try {
				build();
			} catch (Throwable throwable) {
				throwable.printStackTrace(Noise.getLogger().getErrorStream());
				throw new RuntimeException("Could not construct pipeline " + this.getClass().getName());
			}
		}

		for (Consumer<T> method : methods) {
			method.accept(t);
		}
	}

	private void build() throws Throwable {
		List<PipelineMethod> pipelineMethods = new ArrayList<>();

		// create list of all pipeline methods
		for (Object object : objects) {
			for (Method method : object.getClass().getDeclaredMethods()) {
				if (method.isAnnotationPresent(Pipeline.class) && method.getAnnotation(Pipeline.class).value().equals(this.getClass())) {
					PipelineMethod pipelineMethod = new PipelineMethod();
					pipelineMethod.consumer = createConsumer(object, method);
					pipelineMethod.producedEvents = new HashSet<>();
					pipelineMethod.consumedEvents = new HashSet<>();

					if (method.isAnnotationPresent(ProducesEvent.class)) {
						pipelineMethod.producedEvents.addAll(Arrays.asList(method.getAnnotation(ProducesEvent.class).value()));
					}

					if (method.isAnnotationPresent(ConsumesEvent.class)) {
						pipelineMethod.producedEvents.addAll(Arrays.asList(method.getAnnotation(ConsumesEvent.class).value()));
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
			methods.add(pipelineMethod.consumer);
		}
		isDirty = false;
	}

	@SuppressWarnings("unchecked")
	private Consumer<T> createConsumer(Object object, Method method)
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

	private class PipelineMethod {

		public Consumer<T>                      consumer;
		public HashSet<Class<? extends IEvent>> producedEvents;
		public HashSet<Class<? extends IEvent>> consumedEvents;
	}

}
