package com.captainsly.radiant.core.render.gl.lights;

import org.joml.Vector3f;

public class DirectionalLight {

	private Vector3f color, direction;
	private float intensity;

	public DirectionalLight(Vector3f color, Vector3f direction, float intensity) {
		this.color = color;
		this.direction = direction;
		this.intensity = intensity;
	}

	public Vector3f getColor() {
		return color;
	}

	public Vector3f getDirection() {
		return direction;
	}

	public float getIntensity() {
		return intensity;
	}

	public void setColor(Vector3f color) {
		this.color = color;
	}

	public void setColor(float r, float g, float b) {
		this.color.set(r, g, b);
	}

	public void setDirection(Vector3f direction) {
		this.direction = direction;
	}

	public void setPosition(float x, float y, float z) {
		this.direction.set(x, y, z);
	}

	public void setIntensity(float intensity) {
		this.intensity = intensity;
	}

}
