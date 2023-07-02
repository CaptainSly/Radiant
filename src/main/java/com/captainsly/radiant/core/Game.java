package com.captainsly.radiant.core;

import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;

import java.util.Collection;
import java.util.List;

import com.captainsly.radiant.core.entity.Entity;
import com.captainsly.radiant.core.impl.GameLogic;
import com.captainsly.radiant.core.render.gl.Camera;
import com.captainsly.radiant.core.render.gl.Texture;
import com.captainsly.radiant.core.render.gl.mesh.Mesh;
import com.captainsly.radiant.core.render.gl.model.Material;
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
		currentScene = new GameScene(Radiant.window.getWindowWidth(), Radiant.window.getWindowHeight(), this);

		camera = new Camera();

		shader = Radiant.resources.getShader("src/main/resources/default");
		shader.addUniform("projection");
		shader.addUniform("view");
		shader.addUniform("model");
		shader.addUniform("txtSampler");
		shader.addUniform("material.diffuse");

		currentScene.onInit();
	}

	@Override
	public void onRender() {

		shader.bind();
		shader.setUniform("projection", currentScene.getProjection().getProjectionMatrix());
		shader.setUniform("view", camera.getViewMatrix());
		shader.setUniform("txtSampler", 0);

		Collection<Model> models = currentScene.getModelMap().values();
		for (Model model : models) {
			List<Entity> entities = model.getEntitiesList();

			for (Material material : model.getMaterialList()) {
				shader.setUniform("material.diffuse", material.getDiffuseColor());

				Texture texture = Radiant.resources.getTexture(material.getTexturePath());
				glActiveTexture(GL_TEXTURE0);
				texture.bind();

				for (Mesh mesh : material.getMaterialMeshList()) {
					for (Entity entity : entities) {
						shader.setUniform("model", entity.getModelTransform().getTransformationMatrix());
						mesh.draw();
					}
				}

			}

		}

		shader.unbind();
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
