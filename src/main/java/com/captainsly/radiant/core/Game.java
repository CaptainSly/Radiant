package com.captainsly.radiant.core;

import java.util.Collection;
import java.util.List;

import com.captainsly.radiant.core.entity.Entity;
import com.captainsly.radiant.core.impl.GameLogic;
import com.captainsly.radiant.core.render.gl.Camera;
import com.captainsly.radiant.core.render.gl.SkyBoxRenderer;
import com.captainsly.radiant.core.render.gl.model.Model;
import com.captainsly.radiant.core.render.gl.shaders.ShaderProgram;
import com.captainsly.radiant.core.scene.Scene;
import com.captainsly.radiant.test.GameScene;

public class Game implements GameLogic {
	private ShaderProgram shader;
	private SkyBoxRenderer skyBoxRender;
	private Camera camera;
	private Scene currentScene;

	@Override
	public void onInit() {
		currentScene = new GameScene(Radiant.window.getWindowWidth(), Radiant.window.getWindowHeight(), this);

		camera = new Camera();

		shader = Radiant.resources.getShader("default");
		shader.addUniform("projectionMatrix");
		shader.addUniform("viewMatrix");
		shader.addUniform("modelMatrix");
		shader.addUniform("txtSampler");

		currentScene.onInit();
		skyBoxRender = new SkyBoxRenderer();
	}

	@Override
	public void onRender() {
		skyBoxRender.render(currentScene);

		shader.bind();
		shader.setUniform("projectionMatrix", currentScene.getProjection().getProjectionMatrix());
		shader.setUniform("viewMatrix", camera.getViewMatrix());
		shader.setUniform("txtSampler", 0);

		currentScene.render();

		shader.unbind();

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
