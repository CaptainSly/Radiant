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

	private Vector4f diffuseColor;

	public Material() {
		meshList = new ArrayList<>();
		diffuseColor = DEFAULT_COLOR;
	}

	public void setTexturePath(String texturePath) {
		this.texturePath = texturePath;
	}

	public void setDiffuseColor(Vector4f diffuseColor) {
		this.diffuseColor = diffuseColor;
	}

	public void setDiffuseColor(float x, float y, float z, float w) {
		diffuseColor.set(x, y, z, w);
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
