package com.captainsly.radiant.core.entity;

import java.util.List;

import com.captainsly.radiant.core.impl.Disposable;
import com.captainsly.radiant.core.render.gl.model.Material;
import com.captainsly.radiant.core.render.gl.model.Model;

public class Actor extends Entity implements Disposable {

	private Model actorModel;

	public Actor(String entityId, List<Material> actorMaterials) {
		super(entityId, entityId + "_model");
		actorModel = new Model(this.getModelId(), actorMaterials);
	}

	public Actor(String entityId, Model actorModel) {
		super(entityId, actorModel.getModelId());
		this.actorModel = actorModel;
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
