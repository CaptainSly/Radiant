package com.captainsly.radiant.core.render.gl.model;

import java.util.ArrayList;
import java.util.List;

import org.joml.Matrix4f;

import com.captainsly.radiant.core.entity.Entity;
import com.captainsly.radiant.core.impl.Disposable;

public class Model implements Disposable {

	public record AnimatedFrame(Matrix4f[] boneMatrices) {}
	public record Animation(String name, double duration, List<AnimatedFrame> frames) {}
	
	private final String modelId;
	private List<Entity> entitiesList;
	private List<Material> materialList;
	private List<Animation> animationList;
	
	public Model(String entityId, List<Material> materialList, List<Animation> animationList) {
		this.modelId = entityId;
		this.materialList = materialList;
		this.animationList = animationList;
		entitiesList = new ArrayList<>();
	}

	public String getModelId() {
		return modelId;
	}

	public List<Entity> getEntitiesList() {
		return entitiesList;
	}

	public List<Animation> getAnimationList() {
		return animationList;
	}
	
	public List<Material> getMaterialList() {
		return materialList;
	}

	@Override
	public void onDispose() {
		materialList.stream().forEach(Material::onDispose);
	}

}
