package de.nerogar.noise.game.core;

import de.nerogar.noise.game.CoreMap;
import de.nerogar.noise.serialization.NDSFile;
import de.nerogar.noise.serialization.NDSWriter;

public class MapWriter {

	public static void save(CoreMap map, String mapID) {
		NDSFile file = new NDSFile();

		NDSWriter.writeFile(file, "maps/" + mapID + ".map");
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
