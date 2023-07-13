package com.captainsly.radiant.core.render.gl.lights;

public class Attenuation {

	private float constant, exponent, linear;

	public Attenuation(float constant, float exponent, float linear) {
		this.constant = constant;
		this.exponent = exponent;
		this.linear = linear;
	}

	public float getConstant() {
		return constant;
	}

	public float getExponent() {
		return exponent;
	}

	public float getLinear() {
		return linear;
	}

	public void setConstant(float constant) {
		this.constant = constant;
	}

	public void setExponent(float exponent) {
		this.exponent = exponent;
	}

	public void setLinear(float linear) {
		this.linear = linear;
	}

}
