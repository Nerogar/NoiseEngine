package de.nerogar.noise.game.core;

import de.nerogar.noise.game.CoreMap;
import de.nerogar.noise.game.Faction;
import de.nerogar.noise.game.NoiseGame;
import de.nerogar.noise.game.core.systems.EntityFactorySystem;
import de.nerogar.noise.serialization.NDSFile;
import de.nerogar.noise.serialization.NDSNodeObject;
import de.nerogar.noise.serialization.NDSNodeRoot;
import de.nerogar.noise.serialization.NDSReader;
import de.nerogar.noise.util.Logger;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

public abstract class MapLoader<MAP_T extends CoreMap> extends Thread {

	private boolean done;

	private List<MAP_T> maps;
	private String      mapID;
	private Faction[]   factions;

	private NDSFile             mapFile;
	private List<NDSNodeObject> ndsDatas;

	public MapLoader(List<MAP_T> maps, String mapID, Faction[] factions) {
		super("map loader: " + mapID);
		this.maps = maps;
		this.mapID = mapID;
		this.factions = factions;
	}

	protected abstract MAP_T newMap(int id, Faction[] factions);

	public void loadMeta() {
		try {
			mapFile = NDSReader.readJsonFile("maps/" + mapID + "/map.json");
			NDSNodeRoot mapData = mapFile.getData();
			NDSNodeObject[] mapsObjects = mapData.getObjectArray("maps");

			for (int i = 0; i < mapsObjects.length; i++) {
				NDSNodeObject mapsObject = mapsObjects[i];
				String filename = mapsObject.getStringUTF8("file");

				MAP_T map = newMap(i, factions);
				maps.add(map);

				NDSFile metaFile = NDSReader.readFileShallow("maps/" + mapID + "/" + filename + ".map");

				loadMapMeta(map, metaFile);

			}

		} catch (FileNotFoundException e) {
			NoiseGame.logger.log(Logger.ERROR, "could not load map meta: " + mapID);
			e.printStackTrace(NoiseGame.logger.getErrorStream());
		}
	}

	protected abstract void loadMapMeta(MAP_T map, NDSFile metaFile);

	public void startLoading() {
		start();
	}

	@Override
	public void run() {
		try {
			ndsDatas = new ArrayList<>();

			for (NDSNodeObject mapsObject : mapFile.getData().getObjectArray("maps")) {
				String filename = mapsObject.getStringUTF8("file");

				NDSFile ndsData = NDSReader.readFile("maps/" + mapID + "/" + filename + ".map");
				ndsDatas.add(ndsData.getData());
			}

			for (int i = 0; i < maps.size(); i++) {
				NDSNodeObject ndsData = ndsDatas.get(i);
				CoreMap map = maps.get(i);
				loadMap(map, ndsData);
			}
		} catch (FileNotFoundException e) {
			NoiseGame.logger.log(Logger.ERROR, "could not load map data: " + mapID);
			e.printStackTrace(NoiseGame.logger.getErrorStream());
		} finally {
			done = true;
		}
	}

	public boolean isDone() {
		return done;
	}

	protected void loadMap(CoreMap map, NDSNodeObject file) {
		NDSNodeObject systemDataNode = file.getObject("systems");

		map.getSystemContainer().setSystemData(systemDataNode);
	}

	public void finalizeLoad() {
		for (int i = 0; i < maps.size(); i++) {
			CoreMap map = maps.get(i);
			NDSNodeObject ndsData = ndsDatas.get(i);

			loadEntities(map, ndsData.getObjectArray("entities"));
		}
	}

	private void loadEntities(CoreMap map, NDSNodeObject[] entitiesArray) {
		EntityFactorySystem entityFactory = map.getSystem(EntityFactorySystem.class);

		for (NDSNodeObject entityObject : entitiesArray) {
			entityFactory.createEntity(
					entityObject.getShort("eID"),
					entityObject.getInt("id"),
					entityObject.getFloat("x"),
					entityObject.getFloat("y"),
					entityObject.getFloat("z")
			                          );

		}

	}

}
