package de.nerogar.noise.game.systems;

import de.nerogar.noise.game.EntityContainer;
import de.nerogar.noise.game.componentSerializers.DefaultComponentSerializer;
import de.nerogar.noise.game.componentSerializers.IComponentSerializer;
import de.nerogar.noise.game.entityFactories.IEntityFactory;
import de.nerogar.noise.game.events.DespawnEntityEvent;
import de.nerogar.noise.game.events.SpawnEntityEvent;
import de.nerogar.noise.serialization.*;
import de.nerogar.noiseInterface.game.*;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class ComponentSerializeSystem implements IGameSystem {

	private EntityContainerSystem entities;
	private EntityFactorySystem   entityFactorySystem;

	private Map<Class<? extends IComponent>, IComponentSerializer<? extends IComponent>> customSerializers;
	private IComponentSerializer<IComponent>                                             defaultSerializer;

	public ComponentSerializeSystem() {
		this.customSerializers = new HashMap<>();
	}

	@Inject
	public void inject(EntityContainerSystem entities, EntityFactorySystem entityFactorySystem) {
		this.entities = entities;
		this.entityFactorySystem = entityFactorySystem;
		this.defaultSerializer = new DefaultComponentSerializer();
	}

	public <T extends IComponent> void registerCustomComponentSerializer(Class<T> componentClass, IComponentSerializer<T> serializer) {
		customSerializers.put(componentClass, serializer);
	}

	@SuppressWarnings("unchecked")
	private IComponentSerializer<IComponent> getComponentSerializer(Class<? extends IComponent> componentClass) {
		IComponentSerializer<IComponent> customSerializer = (IComponentSerializer<IComponent>) customSerializers.get(componentClass);
		return customSerializer != null ? customSerializer : defaultSerializer;
	}

	/**
	 * Saves the state of all components in a new file
	 *
	 * @param path the path of the new file
	 */
	public void saveComponents(Path path) {
		if (Files.exists(path)) {
			try {
				Files.delete(path);
			} catch (IOException ignored) { }
		}

		Collection<Long> entities = this.entities.getEntities();
		NDSNodeObject[] entityNodes = serializeEntities(entities);

		NDSFile file = new NDSFile();
		file.getData().addObjectArray("entities", entityNodes);

		NDSWriter.writeFile(file, path.toString());
	}

	private NDSNodeObject[] serializeEntities(Collection<Long> entities) {
		int entityCount = 0;
		for (Long entity : entities) {
			if (EntityContainer.getTypeId(entity) > 0) {
				entityCount++;
			}
		}

		NDSNodeObject[] entityNodes = new NDSNodeObject[entityCount];

		int i = 0;
		for (Long entity : entities) {
			if (EntityContainer.getTypeId(entity) > 0) {
				entityNodes[i] = serializeEntity(entity);
				i++;
			}
		}

		return entityNodes;
	}

	private NDSNodeObject serializeEntity(long entity) {
		NDSNodeObject entityNode = new NDSNodeObject();
		entityNode.addLong("entity", entity);

		Collection<IComponent> components = entities.get(entity);
		NDSNodeObject[] componentNodes = new NDSNodeObject[components.size()];
		entityNode.addObjectArray("components", componentNodes);

		int i = 0;
		for (IComponent component : components) {
			NDSNodeObject componentNode = new NDSNodeObject();
			componentNodes[i] = componentNode;

			componentNode.addStringUTF8("type", component.getClass().getName());
			componentNode.addObject("data", serializeComponent(component.getClass(), component));
			i++;
		}

		return entityNode;
	}

	private NDSNodeObject serializeComponent(Class<? extends IComponent> componentClass, IComponent component) {
		IComponentSerializer<IComponent> serializer = getComponentSerializer(componentClass);
		NDSNodeObject dataNode = new NDSNodeObject();
		serializer.serialize(dataNode, component);
		return dataNode;
	}

	/**
	 * Loads the state of all components from a file
	 *
	 * @param path                the path of the new file
	 * @param spawnEntityEvents   spawn events
	 * @param despawnEntityEvents despawn events
	 */
	public void loadComponents(Path path, IEventProducer<SpawnEntityEvent> spawnEntityEvents, IEventProducer<DespawnEntityEvent> despawnEntityEvents) {
		if (!Files.exists(path)) {
			return;
		}

		try {
			NDSFile ndsFile = NDSReader.readFile(path.toString());
			entities.clearEntities(despawnEntityEvents);
			deserializeEntities(ndsFile.getData().getObjectArray("entities"), spawnEntityEvents);
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		}

	}

	private void deserializeEntities(NDSNodeObject[] entityNodes, IEventProducer<SpawnEntityEvent> spawnEntityEvents) {
		for (NDSNodeObject entityNode : entityNodes) {
			initEntity(entityNode, spawnEntityEvents);
		}

		for (NDSNodeObject entityNode : entityNodes) {
			long entity = entityNode.getLong("entity");
			Collection<IComponent> components = entities.get(entity);
			deserializeEntity(entityNode, components, spawnEntityEvents);
		}
	}

	private void initEntity(NDSNodeObject entityNode, IEventProducer<SpawnEntityEvent> spawnEntityEvents) {
		long entity = entityNode.getLong("entity");
		short entityTypeId = EntityContainer.getTypeId(entity);

		IEntityFactory entityFactory = entityFactorySystem.getEntityFactory(entityTypeId);
		IComponent[] components = entityFactory.createComponents();
		entities.initEntity(entity, components, spawnEntityEvents);
	}

	private void deserializeEntity(NDSNodeObject entityNode, Collection<IComponent> components, IEventProducer<SpawnEntityEvent> spawnEntityEvents) {
		NDSNodeObject[] componentNodes = entityNode.getObjectArray("components");

		for (NDSNodeObject componentNode : componentNodes) {
			String componentType = componentNode.getStringUTF8("type");
			NDSNodeObject dataNode = componentNode.getObject("data");

			for (IComponent component : components) {
				if (component.getClass().getName().equals(componentType)) {
					IComponentSerializer<IComponent> serializer = getComponentSerializer(component.getClass());
					serializer.deserialize(dataNode, component, entities);
				}
			}
		}
	}

}
