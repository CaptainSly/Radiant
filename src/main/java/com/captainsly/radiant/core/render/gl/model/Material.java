package com.captainsly.radiant.core.render.gl.model;

import java.util.ArrayList;
import java.util.List;

import org.joml.Vector4f;

import com.captainsly.radiant.core.impl.Disposable;
import com.captainsly.radiant.core.render.gl.mesh.Mesh;

public class Material implements Disposable {

	public static final Vector4f DEFAULT_COLOR = new Vector4f(0.0f, 0.0f, 0.0f, 1.0f);

	private List<Mesh> meshList;
	private String texturePath;
	private String normalMapPath;

	private float reflectance;
	private Vector4f diffuseColor;
	private Vector4f ambientColor;
	private Vector4f specularColor;

	public Material() {
		meshList = new ArrayList<>();
		diffuseColor = DEFAULT_COLOR;
		ambientColor = DEFAULT_COLOR;
		specularColor = DEFAULT_COLOR;

	}
	
	public void setNormalMapPath(String normalMapPath) {
		this.normalMapPath = normalMapPath;
	}

	public void setTexturePath(String texturePath) {
		this.texturePath = texturePath;
	}

	public void setDiffuseColor(Vector4f diffuseColor) {
		this.diffuseColor = diffuseColor;
	}

	public List<Mesh> getMeshList() {
		return meshList;
	}

	public String getNormalMapPath() {
		return normalMapPath;
	}
	
	public float getReflectance() {
		return reflectance;
	}

	public Vector4f getAmbientColor() {
		return ambientColor;
	}

	public Vector4f getSpecularColor() {
		return specularColor;
	}

	public void setMeshList(List<Mesh> meshList) {
		this.meshList = meshList;
	}

	public void setReflectance(float reflectance) {
		this.reflectance = reflectance;
	}

	public void setAmbientColor(Vector4f ambientColor) {
		this.ambientColor = ambientColor;
	}

	public void setSpecularColor(Vector4f specularColor) {
		this.specularColor = specularColor;
	}
	
	public void setAmbientColor(float r, float g, float b) {
		ambientColor.set(r, g, b, 1.0);
	}
	
	public void setSpecularColor(float r, float g, float b) {
		specularColor.set(r, g, b, 1.0);
	}

	public void setDiffuseColor(float r, float g, float b) {
		diffuseColor.set(r, g, b, 1.0);
	}

	public List<Mesh> getMaterialMeshList() {
		return meshList;
	}

	public String getTexturePath() {
		return texturePath;
	}

	public Vector4f getDiffuseColor() {
		return diffuseColor;
	}

	@Override
	public void onDispose() {
		meshList.stream().forEach(Mesh::onDispose);
	}

}
