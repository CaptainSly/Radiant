package com.captainsly.radiant.core.scene;

import java.util.ArrayList;
import java.util.List;

import org.joml.Vector3f;

import com.captainsly.radiant.core.render.gl.lights.AmbientLight;
import com.captainsly.radiant.core.render.gl.lights.DirectionalLight;
import com.captainsly.radiant.core.render.gl.lights.PointLight;
import com.captainsly.radiant.core.render.gl.lights.SpotLight;

public class SceneLights {

	private AmbientLight ambientLight;
	private DirectionalLight dirLight;
	private List<PointLight> pointLights;
	private List<SpotLight> spotLights;

	public SceneLights() {
		ambientLight = new AmbientLight();
		pointLights = new ArrayList<>();
		spotLights = new ArrayList<>();
		dirLight = new DirectionalLight(new Vector3f(1, 1, 1), new Vector3f(0, 1, 0), 1.0f);
	}

	public AmbientLight getAmbientLight() {
		return ambientLight;
	}

	public DirectionalLight getDirLight() {
		return dirLight;
	}

	public List<PointLight> getPointLights() {
		return pointLights;
	}

	public List<SpotLight> getSpotLights() {
		return spotLights;
	}

	public void setAmbientLight(AmbientLight ambientLight) {
		this.ambientLight = ambientLight;
	}

	public void setDirLight(DirectionalLight dirLight) {
		this.dirLight = dirLight;
	}

	public void setPointLights(List<PointLight> pointLights) {
		this.pointLights = pointLights;
	}

	public void setSpotLights(List<SpotLight> spotLights) {
		this.spotLights = spotLights;
	}

}
