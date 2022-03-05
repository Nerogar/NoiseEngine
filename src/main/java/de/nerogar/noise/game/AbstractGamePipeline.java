package de.nerogar.noise.game;

import de.nerogar.noise.Noise;
import de.nerogar.noise.event.EventHub;
import de.nerogar.noise.util.*;
import de.nerogar.noiseInterface.event.IEvent;
import de.nerogar.noiseInterface.game.*;
import de.nerogar.noiseInterface.util.IDirectedGraph;

import java.lang.invoke.MethodHandles;
import java.lang.reflect.*;
import java.util.*;
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

			pipelineMethod.consumer.accept(event);

			long t1 = System.nanoTime();

			//System.out.println(pipelineMethod.name + ": " + ((t1 - t0) / 1_000_000d));
		}
	}

	private List<PipelineMethod> sortPipelineMethods(List<PipelineMethod> pipelineMethods) {
		IDirectedGraph<PipelineMethod> graph = new DirectedGraph<>();

		for (PipelineMethod pipelineMethod : pipelineMethods) {
			graph.addNode(pipelineMethod);
		}

		for (PipelineMethod producer : pipelineMethods) {
			for (PipelineMethod consumer : pipelineMethods) {
				for (Class<? extends IEvent> producedEvent : producer.producedEvents) {
					if (consumer.consumedEvents.contains(producedEvent)) {
						graph.addEdge(producer, consumer);
						break;
					}
				}
			}
		}

		if (!graph.isAcyclic()) {
			throw new RuntimeException("Pipeline could not be sorted. Cyclic event dependency detected.");
		}

		return graph.getTopologicalSort();
	}

	private void build() throws Throwable {
		List<PipelineMethod> pipelineMethods = new ArrayList<>();

		// create list of all pipeline methods
		for (Object object : systems) {
			for (Method method : object.getClass().getDeclaredMethods()) {
				if (method.isAnnotationPresent(Pipeline.class) && method.getAnnotation(Pipeline.class).value().equals(this.getClass())) {
					PipelineMethod pipelineMethod = new PipelineMethod();
					pipelineMethod.producedEvents = new HashSet<>();
					pipelineMethod.consumedEvents = new HashSet<>();
					pipelineMethod.name = object.getClass().getName() + "." + method.getName();
					pipelineMethod.consumer = createPipelineMethodNew(object, method, eventHub);

					Type[] parameterTypes = method.getGenericParameterTypes();
					for (int i = 0; i < parameterTypes.length - 1; i++) {
						if (parameterTypes[i].equals(IEventConsumer.class)) {
							Class<? extends IEvent> typeArgument = (Class<? extends IEvent>) ((ParameterizedType) parameterTypes[i]).getActualTypeArguments()[0];
							pipelineMethod.consumedEvents.add(typeArgument);
						} else if (parameterTypes[i].equals(IEventProducer.class)) {
							Class<? extends IEvent> typeArgument = (Class<? extends IEvent>) ((ParameterizedType) parameterTypes[i]).getActualTypeArguments()[0];
							pipelineMethod.producedEvents.add(typeArgument);
						}
					}

					pipelineMethods.add(pipelineMethod);
				}
			}
		}

		pipelineMethods = sortPipelineMethods(pipelineMethods);

		// finalize
		methods.clear();
		methods.addAll(pipelineMethods);
		isDirty = false;
	}

	private Consumer<T> createPipelineMethodNew(Object object, Method method, EventHub eventHub) throws Throwable {
		Type[] parameterTypes = method.getParameterTypes();
		Type[] genericParameterTypes = method.getGenericParameterTypes();

		Object[] parameters = new Object[genericParameterTypes.length - 1];

		for (int i = 0; i < genericParameterTypes.length - 1; i++) {
			if (parameterTypes[i].equals(IEventConsumer.class)) {
				Class<? extends IEvent> typeArgument = (Class<? extends IEvent>) ((ParameterizedType) genericParameterTypes[i]).getActualTypeArguments()[0];
				parameters[i] = eventHub.getQueue(typeArgument);
			} else if (parameterTypes[i].equals(IEventProducer.class)) {
				Class<? extends IEvent> typeArgument = (Class<? extends IEvent>) ((ParameterizedType) genericParameterTypes[i]).getActualTypeArguments()[0];
				parameters[i] = eventHub.getQueue(typeArgument);
			} else {
				throw new RuntimeException("Could not construct pipeline method. Unexpected parameter of type: " + genericParameterTypes[i]);
			}
		}

		return LambdaUtil.createLastParameterConsumer(MethodHandles.lookup(), object, method, parameters);
	}

	private class PipelineMethod {

		public Consumer<T>                      consumer;
		public HashSet<Class<? extends IEvent>> producedEvents;
		public HashSet<Class<? extends IEvent>> consumedEvents;
		public String                           name;
	}

}
