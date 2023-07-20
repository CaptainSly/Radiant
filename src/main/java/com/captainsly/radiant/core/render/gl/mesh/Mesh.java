package com.captainsly.radiant.core.render.gl.mesh;

import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glDeleteVertexArrays;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.GL30;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

import com.captainsly.radiant.core.impl.Disposable;

public class Mesh implements Disposable {

	public static final int MAX_WEIGHTS = 4;

	private int numVertices;
	private int vaoId;
	private List<Integer> vboIdList;

	public Mesh(float[] positions, float[] normals, float[] tangents, float[] bitangents, float[] textCoords,
			int[] indices) {
		this(positions, normals, tangents, bitangents, textCoords, indices,
				new int[Mesh.MAX_WEIGHTS * positions.length / 3], new float[Mesh.MAX_WEIGHTS * positions.length / 3]);
	}

	public Mesh(float[] positions, float[] normals, float[] tangents, float[] bitangents, float[] textCoords,
			int[] indices, int[] boneIndices, float[] weights) {
		try (MemoryStack stack = MemoryStack.stackPush()) {
			numVertices = indices.length;
			vboIdList = new ArrayList<>();

			vaoId = glGenVertexArrays();
			glBindVertexArray(vaoId);

			// Positions VBO
			int vboId = glGenBuffers();
			vboIdList.add(vboId);
			FloatBuffer positionsBuffer = MemoryUtil.memCallocFloat(positions.length);
			positionsBuffer.put(0, positions);
			glBindBuffer(GL_ARRAY_BUFFER, vboId);
			glBufferData(GL_ARRAY_BUFFER, positionsBuffer, GL_STATIC_DRAW);
			glEnableVertexAttribArray(0);
			glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);

			// Normals VBO
			vboId = glGenBuffers();
			vboIdList.add(vboId);
			FloatBuffer normalsBuffer = MemoryUtil.memCallocFloat(normals.length);
			normalsBuffer.put(0, normals);
			glBindBuffer(GL_ARRAY_BUFFER, vboId);
			glBufferData(GL_ARRAY_BUFFER, normalsBuffer, GL_STATIC_DRAW);
			glEnableVertexAttribArray(1);
			glVertexAttribPointer(1, 3, GL_FLOAT, false, 0, 0);

			// Tangents VBO
			vboId = glGenBuffers();
			vboIdList.add(vboId);
			FloatBuffer tangentsBuffer = MemoryUtil.memCallocFloat(tangents.length);
			tangentsBuffer.put(0, tangents);
			glBindBuffer(GL_ARRAY_BUFFER, vboId);
			glBufferData(GL_ARRAY_BUFFER, tangentsBuffer, GL_STATIC_DRAW);
			glEnableVertexAttribArray(2);
			glVertexAttribPointer(2, 3, GL_FLOAT, false, 0, 0);

			// Bitangents VBO
			vboId = glGenBuffers();
			vboIdList.add(vboId);
			FloatBuffer bitangentsBuffer = MemoryUtil.memCallocFloat(bitangents.length);
			bitangentsBuffer.put(0, bitangents);
			glBindBuffer(GL_ARRAY_BUFFER, vboId);
			glBufferData(GL_ARRAY_BUFFER, bitangentsBuffer, GL_STATIC_DRAW);
			glEnableVertexAttribArray(3);
			glVertexAttribPointer(3, 3, GL_FLOAT, false, 0, 0);

			// Texture coordinates VBO
			vboId = glGenBuffers();
			vboIdList.add(vboId);
			FloatBuffer textCoordsBuffer = MemoryUtil.memAllocFloat(textCoords.length);
			textCoordsBuffer.put(0, textCoords);
			glBindBuffer(GL_ARRAY_BUFFER, vboId);
			glBufferData(GL_ARRAY_BUFFER, textCoordsBuffer, GL_STATIC_DRAW);
			glEnableVertexAttribArray(4);
			glVertexAttribPointer(4, 2, GL_FLOAT, false, 0, 0);

			// Bone weights
			vboId = glGenBuffers();
			vboIdList.add(vboId);
			FloatBuffer weightsBuffer = MemoryUtil.memAllocFloat(weights.length);
			weightsBuffer.put(weights).flip();
			glBindBuffer(GL_ARRAY_BUFFER, vboId);
			glBufferData(GL_ARRAY_BUFFER, weightsBuffer, GL_STATIC_DRAW);
			glEnableVertexAttribArray(5);
			glVertexAttribPointer(5, 4, GL_FLOAT, false, 0, 0);

			// Bone indices
			vboId = glGenBuffers();
			vboIdList.add(vboId);
			IntBuffer boneIndicesBuffer = MemoryUtil.memAllocInt(boneIndices.length);
			boneIndicesBuffer.put(boneIndices).flip();
			glBindBuffer(GL_ARRAY_BUFFER, vboId);
			glBufferData(GL_ARRAY_BUFFER, boneIndicesBuffer, GL_STATIC_DRAW);
			glEnableVertexAttribArray(6);
			glVertexAttribPointer(6, 4, GL_FLOAT, false, 0, 0);

			// Index VBO
			vboId = glGenBuffers();
			vboIdList.add(vboId);
			IntBuffer indicesBuffer = MemoryUtil.memCallocInt(indices.length);
			indicesBuffer.put(0, indices);
			glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, vboId);
			glBufferData(GL_ELEMENT_ARRAY_BUFFER, indicesBuffer, GL_STATIC_DRAW);

			glBindBuffer(GL_ARRAY_BUFFER, 0);
			glBindVertexArray(0);
		}
	}

	public void draw() {
		glBindVertexArray(vaoId);
		glDrawElements(GL_TRIANGLES, getNumVertices(), GL_UNSIGNED_INT, 0);
		glBindVertexArray(0);
	}

	@Override
	public void onDispose() {
		vboIdList.forEach(GL30::glDeleteBuffers);
		glDeleteVertexArrays(vaoId);
	}

	public int getNumVertices() {
		return numVertices;
	}

	public final int getVaoId() {
		return vaoId;
	}
}
