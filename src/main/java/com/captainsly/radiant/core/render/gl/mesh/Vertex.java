package com.captainsly.radiant.core.render.gl.mesh;

import org.joml.Vector3f;

public class Vertex {

	public static final int SIZE = 3;

	private Vector3f vertexPosition;

	public Vertex(Vector3f vertexPosition) {
		this.vertexPosition = vertexPosition;
	}

	public Vertex(float x, float y, float z) {
		this.vertexPosition = new Vector3f(x, y, z);
	}

	public Vector3f getVertexPosition() {
		return vertexPosition;
	}

}
