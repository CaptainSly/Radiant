package com.captainsly.radiant.test;

import com.captainsly.radiant.core.Radiant;
import com.captainsly.radiant.core.impl.GameLogic;
import com.captainsly.radiant.core.render.Transform;
import com.captainsly.radiant.core.render.mesh.Mesh;
import com.captainsly.radiant.core.render.shaders.ShaderProgram;

public class Game implements GameLogic {

	private Mesh mesh;
	private ShaderProgram shader;
	private Transform transform;

	@Override
	public void onInit() {

		transform = new Transform();

		shader = Radiant.resources.loadShader("src/main/resources/default");
		shader.addUniform("transform");

		mesh = Radiant.resources.loadOBJMesh("src/main/resources/untitled");
	}

	float temp = 0.0f;

	@Override
	public void onRender() {
		shader.bind();
		shader.setUniform("transform", transform.getProjectedTransformation());

		mesh.draw();
		shader.unbind();
	}

	@Override
	public void onUpdate(double delta) {
		temp += delta;

		transform.setProjection(70f, Radiant.window.getWindowWidth(), Radiant.window.getWindowHeight(), 0.1f, 1000f);

		float sinTemp = (float) Math.sin(temp);
		transform.setPosition(sinTemp, 0, -5);
		transform.setRotation(0, sinTemp * 180, 0);
	}

	@Override
	public void onInput(double delta) {
	}

	@Override
	public void onDispose() {
		mesh.onDispose();
	}

}
