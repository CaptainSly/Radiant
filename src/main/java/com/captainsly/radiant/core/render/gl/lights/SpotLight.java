package com.captainsly.radiant.core.render.gl.lights;

import org.joml.Vector3f;

public class SpotLight {

	private Vector3f coneDirection;
	private float cutOff;
	private float cutOffAngle;
	private PointLight pointLight;

	public SpotLight(PointLight pointLight, Vector3f coneDirection, float cutOffAngle) {
		this.pointLight = pointLight;
		this.coneDirection = coneDirection;
		this.cutOffAngle = cutOffAngle;
	}

	public Vector3f getConeDirection() {
		return coneDirection;
	}

	public float getCutOff() {
		return cutOff;
	}

	public float getCutOffAngle() {
		return cutOffAngle;
	}

	public PointLight getPointLight() {
		return pointLight;
	}

	public void setConeDirection(Vector3f coneDirection) {
		this.coneDirection = coneDirection;
	}

	public void setCutOff(float cutOff) {
		this.cutOff = cutOff;
	}

	public void setCutOffAngle(float cutOffAngle) {
		this.cutOffAngle = cutOffAngle;
		cutOff = (float) Math.cos(Math.toRadians(cutOffAngle));
	}

	public void setPointLight(PointLight pointLight) {
		this.pointLight = pointLight;
	}

	public void setConeDirection(float x, float y, float z) {
		this.coneDirection.set(x, y, z);
	}

}
