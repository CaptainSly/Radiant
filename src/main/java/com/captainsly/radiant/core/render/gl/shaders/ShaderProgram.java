package com.captainsly.radiant.core.render.gl.shaders;

import static org.lwjgl.opengl.GL20.*;

import java.util.HashMap;
import java.util.Map;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import com.captainsly.radiant.core.impl.Disposable;
import com.captainsly.radiant.core.utils.Buffers;

public class ShaderProgram implements Disposable {

	private int programId;
	private int vertexShader, fragmentShader;

	private Map<String, Integer> uniformMap;

	public ShaderProgram(String vSource, String fSource) {
		uniformMap = new HashMap<>();

		programId = glCreateProgram();
		if (programId == 0)
			throw new RuntimeException("Could not create Shader");

		vertexShader = createShader(vSource, GL_VERTEX_SHADER);
		fragmentShader = createShader(fSource, GL_FRAGMENT_SHADER);
		link();
	}

	public void addUniform(String uniformName) {
		int uniformLocation = glGetUniformLocation(programId, uniformName);

		if (uniformLocation < 0)
			throw new RuntimeException("Error: Could not find Uniform: " + uniformName);

		uniformMap.put(uniformName, uniformLocation);
	}

	public void setUniform(String uniformName, int value) {
		glUniform1i(uniformMap.get(uniformName), value);
	}

	public void setUniform(String uniformName, float value) {
		glUniform1f(uniformMap.get(uniformName), value);
	}

	public void setUniform(String uniformName, Vector3f value) {
		glUniform3f(uniformMap.get(uniformName), value.x, value.y, value.z);
	}

	public void setUniform(String uniformName, Vector4f value) {
		glUniform4f(uniformMap.get(uniformName), value.x, value.y, value.z, value.w);
	}

	public void setUniform(String uniformName, Matrix4f value) {
		glUniformMatrix4fv(uniformMap.get(uniformName), false, Buffers.createFlippedMatrixBuffer(value));
	}

	private int createShader(String shaderSource, int shaderType) {
		int shaderId = glCreateShader(shaderType);
		if (shaderId == 0)
			throw new RuntimeException("Could not create shader type: " + getShaderType(shaderType));

		glShaderSource(shaderId, shaderSource);
		glCompileShader(shaderId);

		if (glGetShaderi(shaderId, GL_COMPILE_STATUS) == 0)
			throw new RuntimeException("Error compiling Shader Code: " + glGetShaderInfoLog(shaderId, 2048));

		glAttachShader(programId, shaderId);
		return shaderId;
	}

	private void link() {
		glLinkProgram(programId);
		if (glGetProgrami(programId, GL_LINK_STATUS) == 0)
			throw new RuntimeException("Error linking shader code: " + glGetProgramInfoLog(programId, 2048));

		glValidateProgram(programId);
		if (glGetProgrami(programId, GL_VALIDATE_STATUS) == 0)
			throw new RuntimeException("Error Validating shader code: " + glGetProgramInfoLog(programId, 2048));

		glDetachShader(programId, vertexShader);
		glDetachShader(programId, fragmentShader);
		glDeleteShader(vertexShader);
		glDeleteShader(fragmentShader);
	}

	public static String getShaderType(int shaderType) {
		switch (shaderType) {
		default:
		case GL_VERTEX_SHADER:
			return "Vertex Shader";
		case GL_FRAGMENT_SHADER:
			return "Fragment Shader";
		}
	}

	public void bind() {
		glUseProgram(programId);
	}

	public void unbind() {
		glUseProgram(0);
	}

	@Override
	public void onDispose() {
		unbind();
		if (programId != 0)
			glDeleteProgram(programId);
	}

}