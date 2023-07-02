package com.captainsly.radiant.core.entity;

import org.joml.Quaternionf;
import org.joml.Vector3f;

import com.captainsly.radiant.core.render.gl.Transform;

public abstract class Entity {

	private final String entityId;
	private final String modelId;
	private Transform modelTransform;

	public Entity(String entityId, String modelId) {
		this.entityId = entityId;
		this.modelId = modelId;
		modelTransform = new Transform();
	}

	public abstract void onInteract(Entity entity);

	public abstract void onCollide(Entity entity);

	public abstract void onUpdate(double delta);

	public void update(double delta) {
		modelTransform.updateTransformMatrix();
		onUpdate(delta);
	}

	public void setPosition(float x, float y, float z) {
		this.modelTransform.setPosition(x, y, z);
	}

	public void setPosition(Vector3f position) {
		this.modelTransform.setPosition(position);
	}

	public void setScale(float scale) {
		this.modelTransform.setScale(scale);
	}

	public void setScale(float x, float y, float z) {
		this.modelTransform.setScale(x, y, z);
	}

	public Transform rotateX(float angle) {
		return this.modelTransform.rotateX(angle);
	}

	public Transform rotateY(float angle) {
		return this.modelTransform.rotateY(angle);
	}

	public Transform rotateZ(float angle) {
		return this.modelTransform.rotateZ(angle);
	}

	public void clearRotation() {
		this.modelTransform.clearRotation();
	}

	public Vector3f getScale() {
		return modelTransform.getScale();
	}

	public Quaternionf getRotation() {
		return modelTransform.getRotation();
	}

	public Vector3f getPosition() {
		return modelTransform.getPosition();
	}

	public String getEntityId() {
		return entityId;
	}

	public String getModelId() {
		return modelId;
	}

	public Transform getModelTransform() {
		return modelTransform;
	}

}
