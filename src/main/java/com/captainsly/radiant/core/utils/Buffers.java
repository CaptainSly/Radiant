package com.captainsly.radiant.core.utils;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import org.joml.Matrix4f;
import org.lwjgl.system.MemoryUtil;

import com.captainsly.radiant.core.render.gl.mesh.Vertex;

public class Buffers {

	private Buffers() {}
	
	public static FloatBuffer createFloatBuffer(int size) {
		return MemoryUtil.memCallocFloat(size);
	}

	public static IntBuffer createIntBuffer(int size) {
		return MemoryUtil.memCallocInt(size);
	}

	public static FloatBuffer createFlippedMatrixBuffer(Matrix4f value) {
		FloatBuffer buffer = createFloatBuffer(4 * 4);

		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 4; j++) {
				buffer.put(value.get(i, j));
			}
		}

		buffer.flip();
		return buffer;
	}

	public static FloatBuffer createFlippedVertexBuffer(Vertex[] vertices) {
		FloatBuffer buffer = createFloatBuffer(vertices.length * Vertex.SIZE);

		for (int i = 0; i < vertices.length; i++) {
			buffer.put(vertices[i].getVertexPosition().x);
			buffer.put(vertices[i].getVertexPosition().y);
			buffer.put(vertices[i].getVertexPosition().z);
		}

		buffer.flip();
		return buffer;

	}

	public static IntBuffer createFlippedIntegerBuffer(int[] intArray) {
		IntBuffer buffer = createIntBuffer(intArray.length);
		buffer.put(intArray);
		buffer.flip();
		return buffer;
	}

}
