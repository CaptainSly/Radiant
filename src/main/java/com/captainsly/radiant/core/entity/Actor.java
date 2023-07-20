package com.captainsly.radiant.core.entity;

import com.captainsly.radiant.core.Radiant;
import com.captainsly.radiant.core.impl.Disposable;
import com.captainsly.radiant.core.render.gl.model.Model;

public class Actor extends Entity implements Disposable {

	private Model actorModel;

	public Actor(String entityId, Model actorModel) {
		super(entityId, actorModel.getModelId());
		this.actorModel = actorModel;
	}

	public Actor(String entityId, String modelId, String modelPath, boolean animated) {
		this(entityId, Radiant.resources.getModel(modelId, modelPath, animated));
	}

	public Model getActorModel() {
		return actorModel;
	}

	@Override
	public void onInteract(Entity entity) {
	}

	@Override
	public void onCollide(Entity entity) {
	}

	@Override
	public void onUpdate(double delta) {
	}

	@Override
	public void onDispose() {

	}

}
