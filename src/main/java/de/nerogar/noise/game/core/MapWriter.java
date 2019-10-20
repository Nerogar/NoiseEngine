package de.nerogar.noise.game.core;

import de.nerogar.noise.game.CoreMap;
import de.nerogar.noise.game.Entity;
import de.nerogar.noise.game.core.components.PositionComponent;
import de.nerogar.noise.serialization.NDSFile;
import de.nerogar.noise.serialization.NDSNodeObject;
import de.nerogar.noise.serialization.NDSWriter;

public abstract class MapWriter<MAP_T extends CoreMap> {

	public void save(MAP_T map, String mapID) {
		NDSFile file = new NDSFile();

		saveMapMeta(map, file.getData());
		saveMap(map, file.getData());

		NDSWriter.writeFile(file, "maps/" + mapID + "/testSave.map");
	}

	protected abstract void saveMapMeta(MAP_T map, NDSNodeObject metaFile);

	protected void saveEntities(MAP_T map, NDSNodeObject file) {
		NDSNodeObject[] entitiesArray = new NDSNodeObject[map.getEntityList().getEntities().size()];
		file.addObjectArray("entities", entitiesArray);

		int i = 0;
		for (Entity entity : map.getEntityList().getEntities()) {
			NDSNodeObject entityObject = new NDSNodeObject();

			entityObject.addShort("eID", entity.getEntityID());
			entityObject.addInt("id", entity.getID());

			PositionComponent position = entity.getComponent(PositionComponent.class);
			entityObject.addFloat("x", position.getX());
			entityObject.addFloat("y", position.getY());
			entityObject.addFloat("z", position.getZ());

			entitiesArray[i] = entityObject;
			i++;
		}
	}

	protected void saveMap(MAP_T map, NDSNodeObject file) {
		NDSNodeObject systemDataNode = new NDSNodeObject();
		file.addObject("systems", systemDataNode);

		map.getSystemContainer().saveSystemData(systemDataNode);

		saveEntities(map, file);
	}

	/*
	private static NDSNodeObject getEntityNode(Collection<Entity> entityCollection) {
		NDSNodeObject entityNode = new NDSNodeObject("entity");

		Set<Entity> entitySet = new HashSet<>(entityCollection);
		Set<Entity> buildingSet = new HashSet<>(entityCollection);

		entitySet.removeIf(e -> e.hasComponent(BuildingComponent.class));
		buildingSet.removeIf(e -> !e.hasComponent(BuildingComponent.class));

		NDSNodeObject[] entities = new NDSNodeObject[entitySet.size()];
		NDSNodeObject[] buildings = new NDSNodeObject[buildingSet.size()];

		int i;

		i = 0;
		for (Entity entity : entitySet) {
			NDSNodeObject node = entities[i] = new NDSNodeObject(null);
			PositionComponent position = entity.getComponent(PositionComponent.class);
			node.addShort("eID", entity.getEntityID());
			node.addInt("id", entity.getID());
			node.addFloat("x", position.getX());
			node.addFloat("y", position.getY());
			node.addFloat("z", position.getZ());

			i++;
		}

		i = 0;
		for (Entity building : buildingSet) {
			NDSNodeObject node = buildings[i] = new NDSNodeObject(null);
			BuildingComponent position = building.getComponent(BuildingComponent.class);
			node.addShort("eID", building.getEntityID());
			node.addInt("id", building.getID());
			node.addInt("x", position.getXb());
			node.addInt("y", position.getYb());
			node.addInt("z", position.getZb());

			i++;
		}

		entityNode.addObjectArray("entities", entities);
		entityNode.addObjectArray("buildings", buildings);
		return entityNode;
	}
*/
}
