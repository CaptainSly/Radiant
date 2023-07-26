package com.captainsly.radiant.core;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL14.GL_FUNC_ADD;
import static org.lwjgl.opengl.GL14.glBlendEquation;

import java.util.Collection;
import java.util.List;

import com.captainsly.radiant.core.entity.Entity;
import com.captainsly.radiant.core.impl.GameLogic;
import com.captainsly.radiant.core.render.gl.Camera;
import com.captainsly.radiant.core.render.gl.Fog;
import com.captainsly.radiant.core.render.gl.model.Model;
import com.captainsly.radiant.core.render.gl.shaders.ShaderProgram;
import com.captainsly.radiant.core.scene.Scene;
import com.captainsly.radiant.test.GameScene;

public class Game implements GameLogic {
	private ShaderProgram shader;
	private Camera camera;
	private Scene currentScene;

	@Override
	public void onInit() {

		camera = new Camera();

		currentScene = new GameScene(Radiant.window.getWindowWidth(), Radiant.window.getWindowHeight(), this);
		currentScene.onInit();

		shader = Radiant.resources.getShader("default");
		shader.addUniform("projectionMatrix");
		shader.addUniform("viewMatrix");
		shader.addUniform("modelMatrix");

		currentScene.createUniforms();

	}

	@Override
	public void onRender() {

		glEnable(GL_BLEND);
		glBlendEquation(GL_FUNC_ADD);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
		shader.bind();

		// Bind the Base Projection and View Matrixes as well as the TextureSampler
		shader.setUniform("projectionMatrix", currentScene.getProjection().getProjectionMatrix());
		shader.setUniform("viewMatrix", camera.getViewMatrix());

		// Fog
		Fog fog = currentScene.getSceneFog();
		shader.setUniform("fog.activeFog", fog.isActive() ? 1 : 0);
		shader.setUniform("fog.color", fog.getColor());
		shader.setUniform("fog.density", fog.getDensity());

		currentScene.render();
		
		shader.unbind();
		glDisable(GL_BLEND);

	}

	@Override
	public void onRenderUi() {
		currentScene.onRenderGui();
	}

	@Override
	public void onUpdate(double delta) {
		currentScene.onUpdate(delta);
		currentScene.getProjection().updateProjectionMatrix(Radiant.window.getWindowWidth(),
				Radiant.window.getWindowHeight());

		Collection<Model> models = currentScene.getModelMap().values();
		for (Model model : models) {
			List<Entity> entities = model.getEntitiesList();
			entities.forEach(entity -> entity.update(delta));
		}

	}

	@Override
	public void onInput(double delta) {
		currentScene.onInput(delta);
	}

	public Camera getCamera() {
		return camera;
	}

	public ShaderProgram getShader() {
		return shader;
	}

	@Override
	public void onDispose() {
		currentScene.onDispose();
	}

}
