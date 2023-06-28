package com.captainsly.radiant.core.render;

import org.joml.Matrix4f;
import org.joml.Vector3f;

public class Transform {

	private float zNear, zFar;
	private float width, height;
	private float fov;

	private Vector3f position, scale, rotation;

	public Transform() {
		position = new Vector3f();
		scale = new Vector3f(1, 1, 1);
		rotation = new Vector3f();
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

	public void setRotation(float x, float y, float z) {
		this.rotation.set(x, y, z);
	}

	public void setRotation(Vector3f rotation) {
		this.rotation.set(rotation);
	}

	public Vector3f getPosition() {
		return position;
	}

	public Vector3f getScale() {
		return scale;
	}

	public Vector3f getRotation() {
		return rotation;
	}

	public void setProjection(float fov, float width, float height, float zNear, float zFar) {
		this.fov = fov;
		this.width = width;
		this.height = height;
		this.zNear = zNear;
		this.zFar = zFar;
	}

	public Matrix4f getProjectedTransformation() {
		Matrix4f projectionMatrix = new Matrix4f();
		projectionMatrix.setPerspective(fov, (float) width / height, zNear, zFar);
		return projectionMatrix.mul(getTransformation());
	}

	public Matrix4f getTransformation() {
		Matrix4f transMat = new Matrix4f().identity();
		transMat.setTranslation(position);
		transMat.setRotationXYZ((float) Math.toRadians(rotation.x), (float) Math.toRadians(rotation.y),
				(float) Math.toRadians(rotation.z));
		transMat.scale(scale);

		return transMat;
	}

	public Matrix4f createRotationMatrix() {
		float rotX = (float) Math.toRadians(rotation.x);
		float rotY = (float) Math.toRadians(rotation.y);
		float rotZ = (float) Math.toRadians(rotation.z);

		Matrix4f rotMatX = new Matrix4f();
		rotMatX.identity();
		rotMatX.m11((float) Math.cos(rotX));
		rotMatX.m12((float) -Math.sin(rotX));
		rotMatX.m21((float) Math.sin(rotX));
		rotMatX.m22((float) Math.cos(rotX));

		Matrix4f rotMatY = new Matrix4f();
		rotMatY.identity();
		rotMatY.m00((float) Math.cos(rotY));
		rotMatY.m20((float) Math.sin(rotY));
		rotMatY.m02((float) -Math.sin(rotY));
		rotMatY.m22((float) Math.cos(rotY));

		Matrix4f rotMatZ = new Matrix4f();
		rotMatZ.identity();
		rotMatZ.m00((float) Math.cos(rotZ));
		rotMatZ.m01((float) -Math.sin(rotZ));
		rotMatZ.m10((float) Math.sin(rotZ));
		rotMatZ.m11((float) Math.cos(rotZ));

		Matrix4f rotMat = rotMatZ.mul(rotMatY.mul(rotMatX));

		return rotMat;
	}

}
