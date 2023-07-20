package com.captainsly.radiant.core.render.gl;

import com.captainsly.radiant.core.Radiant;
import com.captainsly.radiant.core.entity.Actor;
import com.captainsly.radiant.core.entity.Entity;
import com.captainsly.radiant.core.impl.Disposable;
import com.captainsly.radiant.core.render.gl.model.Model;

public class SkyBox implements Disposable {

	private Entity skyBoxEntity;
	private Model skyBoxModel;

	public SkyBox(String skyBoxModelPath) {
		skyBoxModel = Radiant.resources.getModel("skybox-model", skyBoxModelPath, false);
		skyBoxEntity = new Actor("skybox-entity", skyBoxModel);
	}

	public Entity getSkyBoxEntity() {
		return skyBoxEntity;
	}

	public Model getSkyBoxModel() {
		return skyBoxModel;
	}

	@Override
	public void onDispose() {
		skyBoxModel.onDispose();
	}

}
