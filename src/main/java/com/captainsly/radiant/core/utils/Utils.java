package com.captainsly.radiant.core.utils;

import com.captainsly.radiant.core.render.gl.mesh.Vertex;

public class Utils {

	public static Vertex[] floatArrToVertexArr(float[] positions) {
		int vertexCount = positions.length / 3;
		Vertex[] vertices = new Vertex[vertexCount];

		for (int i = 0; i < vertexCount; i++) {
			int index = i * 3; // Compute the starting index of each vertex

			float x = positions[index];
			float y = positions[index + 1];
			float z = positions[index + 2];

			vertices[i] = new Vertex(x, y, z);
		}

		return vertices;
	}

}
