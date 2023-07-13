package com.captainsly.radiant.core.render.gl.lights;

import org.joml.Vector3f;

public class PointLight {

	private Attenuation attenuation;
	private Vector3f color;
	private float intensity;
	private Vector3f position;

	public PointLight(Vector3f color, float intensity, Vector3f position) {
		this.attenuation = new Attenuation(0, 0, 1);
		this.color = color;
		this.intensity = intensity;
		this.position = position;
	}

	public Attenuation getAttenuation() {
		return attenuation;
	}

	public Vector3f getColor() {
		return color;
	}

	public float getIntensity() {
		return intensity;
	}

	public Vector3f getPosition() {
		return position;
	}

	public void setAttenuation(Attenuation attenuation) {
		this.attenuation = attenuation;
	}

	public void setColor(Vector3f color) {
		this.color = color;
	}

	public void setColor(float r, float g, float b) {
		this.color.set(r, g, b);
	}

	public void setIntensity(float intensity) {
		this.intensity = intensity;
	}

	public void setPosition(Vector3f position) {
		this.position = position;
	}

	public void setPosition(float x, float y, float z) {
		this.position.set(x, y, z);
	}

}
