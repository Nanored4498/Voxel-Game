package fr.coudert.game.entities;

import java.util.*;

public class EntitiesManager {

	private ArrayList<Integer> keys;
	private Map<Integer, Entity> entities;

	public EntitiesManager() {
		keys = new ArrayList<Integer>();
		entities = new HashMap<Integer, Entity>();
	}

	public void add(Entity e) {
		if(entities.put(e.id, e) == null)
			keys.add(e.id);
	}

	public void remove(int id) {
		keys.remove((Integer) id);
		entities.remove(id);
	}

	public void update() {
		for(int i = 0; i < keys.size(); i++) {
			Entity e = entities.get(keys.get(i));
			e.update();
			if(e.isDestroyed())
				remove(keys.get(i));
		}
	}

	public void render() {
		for(int i = 0; i < keys.size(); i++)
			entities.get(keys.get(i)).render();
	}

	public Entity get(int i) {
		return entities.get(i);
	}

}