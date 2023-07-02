package com.captainsly.radiant.core.scene;

import java.util.HashMap;
import java.util.Map;

import com.captainsly.radiant.core.Game;
import com.captainsly.radiant.core.entity.Actor;
import com.captainsly.radiant.core.entity.Entity;
import com.captainsly.radiant.core.impl.Disposable;
import com.captainsly.radiant.core.render.gl.Projection;
import com.captainsly.radiant.core.render.gl.model.Model;

public abstract class Scene implements Disposable {

	private Map<String, Model> modelMap;
	private Projection projection;
	private Game game;

	public Scene(int width, int height, Game game) {
		this.game = game;
		modelMap = new HashMap<>();
		projection = new Projection(width, height);
	}

	public abstract void onInit();

	public abstract void onInput(double delta);

	public abstract void onUpdate(double delta);

	public abstract void dispose();

	public void addEntity(Entity entity) {
		String modelId = entity.getModelId();
		Model model = modelMap.get(modelId);
		if (model == null)
			throw new RuntimeException("Could not find model [" + modelId + "]");

		model.getEntitiesList().add(entity);
	}

	public void addActor(Actor actor) {
		addModel(actor.getActorModel());
		addEntity(actor);
	}

	public void addModel(Model model) {
		modelMap.put(model.getModelId(), model);
	}

	public Map<String, Model> getModelMap() {
		return modelMap;
	}

	public Projection getProjection() {
		return projection;
	}

	public Game getGame() {
		return game;
	}

	@Override
	public void onDispose() {
		modelMap.values().stream().forEach(Model::onDispose);
		dispose();
	}

}
