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

	private List<MAP_T>   maps;
	private String        mapID;
	private List<Faction> factions;

	private NDSFile       saveFile;
	private List<NDSFile> mapFiles;

	public MapLoader(List<MAP_T> maps, String mapID, List<Faction> factions) {
		super("map loader: " + mapID);
		this.maps = maps;
		this.mapID = mapID;
		this.factions = factions;
	}

	protected abstract MAP_T newMap(int id, List<Faction> factions);

	public void loadMeta() {
		try {
			saveFile = NDSReader.readJsonFile("maps/" + mapID + "/map.json");
			NDSNodeRoot mapData = saveFile.getData();
			NDSNodeObject[] mapsObjects = mapData.getObjectArray("maps");

			for (int i = 0; i < mapsObjects.length; i++) {
				NDSNodeObject mapsObject = mapsObjects[i];
				String filename = mapsObject.getStringUTF8("file");

				MAP_T map = newMap(i, factions);
				maps.add(map);

				NDSFile metaFile;
				if (!isGenerator()) {
					metaFile = NDSReader.readFileShallow("maps/" + mapID + "/" + filename + ".map");
				} else {
					metaFile = new NDSFile();
					generate(i, metaFile);
				}

				loadMapMeta(map, metaFile);
			}

		} catch (FileNotFoundException e) {
			NoiseGame.logger.log(Logger.ERROR, "could not load map meta: " + mapID);
			e.printStackTrace(NoiseGame.logger.getErrorStream());
		}
	}

	protected boolean isGenerator()                  {return false;}

	protected void generate(int mapId, NDSFile file) {}

	protected abstract void loadMapMeta(MAP_T map, NDSFile metaFile);

	public void startLoading() {
		start();
	}

	@Override
	public void run() {
		try {
			mapFiles = new ArrayList<>();

			for (NDSNodeObject mapsObject : saveFile.getData().getObjectArray("maps")) {
				String filename = mapsObject.getStringUTF8("file");

				if (!isGenerator()) {
					NDSFile mapFile = NDSReader.readFile("maps/" + mapID + "/" + filename + ".map");
					mapFiles.add(mapFile);
				} else {
					mapFiles.add(new NDSFile());
				}
			}

			for (int i = 0; i < maps.size(); i++) {
				NDSFile mapFile = mapFiles.get(i);
				MAP_T map = maps.get(i);

				if (isGenerator()) {
					generate(i, mapFile);

					if (!mapFile.getData().contains("systems")) {
						mapFile.getData().addObject("systems", new NDSNodeObject());
					}

					if (!mapFile.getData().contains("entities")) {
						mapFile.getData().addObjectArray("entities", new NDSNodeObject[0]);
					}
				}

				loadMap(map, mapFile.getData());
			}

			loadGame(saveFile.getData());

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

	protected void loadGame(NDSNodeObject file) {
		NDSNodeObject systemDataNode = file.getObject("systems");

		maps.get(0).getGameSystemContainer().setSystemData(systemDataNode);
	}

	protected void loadMap(MAP_T map, NDSNodeObject file) {
		NDSNodeObject systemDataNode = file.getObject("systems");

		map.getSystemContainer().setSystemData(systemDataNode);
	}

	public void finalizeLoad() {
		for (int i = 0; i < maps.size(); i++) {
			MAP_T map = maps.get(i);
			NDSNodeObject ndsData = mapFiles.get(i).getData();

			loadEntities(map, ndsData.getObjectArray("entities"));
		}
	}

	private void loadEntities(MAP_T map, NDSNodeObject[] entitiesArray) {
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
