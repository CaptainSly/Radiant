package com.captainsly.radiant.core.render.gl.model;

import java.util.ArrayList;
import java.util.List;

import com.captainsly.radiant.core.entity.Entity;
import com.captainsly.radiant.core.impl.Disposable;

public class Model implements Disposable {

	private final String modelId;
	private List<Entity> entitiesList;
	private List<Material> materialList;

	public Model(String entityId, List<Material> materialList) {
		this.modelId = entityId;
		this.materialList = materialList;
		entitiesList = new ArrayList<>();
	}

	public String getModelId() {
		return modelId;
	}

	public List<Entity> getEntitiesList() {
		return entitiesList;
	}

	public List<Material> getMaterialList() {
		return materialList;
	}

	@Override
	public void onDispose() {
		materialList.stream().forEach(Material::onDispose);
	}

}
