package com.captainsly.radiant.core.render.mesh;

import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_INT;
import static org.lwjgl.opengl.GL11.glDrawElements;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.glDisableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.glBindVertexArray;

import com.captainsly.radiant.core.impl.Disposable;
import com.captainsly.radiant.core.utils.Buffers;

public class Mesh implements Disposable {

	private int vbo, ibo;
	private int size;

	public Mesh(Vertex[] vertices, int[] indices) {
		vbo = glGenBuffers();
		ibo = glGenBuffers();
		size = indices.length;

		// VBO
		glBindBuffer(GL_ARRAY_BUFFER, vbo);
		glBufferData(GL_ARRAY_BUFFER, Buffers.createFlippedVertexBuffer(vertices), GL_STATIC_DRAW);

		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ibo);
		glBufferData(GL_ELEMENT_ARRAY_BUFFER, Buffers.createFlippedIntegerBuffer(indices), GL_STATIC_DRAW);

		glVertexAttribPointer(0, 3, GL_FLOAT, false, Vertex.SIZE * Float.BYTES, 0);
		glBindBuffer(GL_ARRAY_BUFFER, 0);
		glBindVertexArray(0);
	}

	public void draw() {
		glEnableVertexAttribArray(0);

		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ibo);
		glDrawElements(GL_TRIANGLES, size, GL_UNSIGNED_INT, 0);

		glDisableVertexAttribArray(0);

	}

	@Override
	public void onDispose() {
		glDeleteBuffers(vbo);
		glDeleteBuffers(ibo);
	}
	
	

}
