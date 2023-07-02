package com.captainsly.radiant.core.render.gl;

import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class Transform {

	private Vector3f position, scale;
	private Quaternionf rotation;
	private Matrix4f transformMatrix;

	public Transform() {
		position = new Vector3f();
		scale = new Vector3f(1, 1, 1);
		rotation = new Quaternionf();
		transformMatrix = new Matrix4f();
		updateTransformMatrix();
	}

	public void updateTransformMatrix() {
		transformMatrix.translationRotateScale(position, rotation, scale);
	}

	public void setPosition(float x, float y, float z) {
		position.set(x, y, z);
	}

	public void setPosition(Vector3f position) {
		this.position.set(position);
	}

	public void setScale(float scale) {
		this.scale.set(scale);
	}

	public void setScale(float x, float y, float z) {
		this.scale.set(x, y, z);
	}

	public void setScale(Vector3f scale) {
		this.scale.set(scale);
	}

	public Transform rotateX(float angle) {
		this.rotation.fromAxisAngleRad(1, 0, 0, angle);
		return this;
	}

	public Transform rotateY(float angle) {
		this.rotation.fromAxisAngleRad(0, 1, 0, angle);
		return this;
	}

	public Transform rotateZ(float angle) {
		this.rotation.fromAxisAngleRad(0, 0, 1, angle);
		return this;
	}

	public void clearRotation() {
		this.rotation.fromAxisAngleRad(1, 1, 1, 0);
	}

	public Matrix4f getTransformationMatrix() {
		return transformMatrix;
	}

	public Quaternionf getRotation() {
		return rotation;
	}

	public Vector3f getPosition() {
		return position;
	}

	public Vector3f getScale() {
		return scale;
	}

}
