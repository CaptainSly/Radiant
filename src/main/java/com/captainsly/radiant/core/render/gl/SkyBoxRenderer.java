package com.captainsly.radiant.core.render.gl;

import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import static org.lwjgl.opengl.GL30.glBindVertexArray;

import org.joml.Matrix4f;

import com.captainsly.radiant.core.Radiant;
import com.captainsly.radiant.core.entity.Entity;
import com.captainsly.radiant.core.impl.Disposable;
import com.captainsly.radiant.core.render.gl.mesh.Mesh;
import com.captainsly.radiant.core.render.gl.model.Material;
import com.captainsly.radiant.core.render.gl.model.Model;
import com.captainsly.radiant.core.render.gl.shaders.ShaderProgram;
import com.captainsly.radiant.core.scene.Scene;
import com.captainsly.radiant.core.utils.files.ResourceLoader;

public class SkyBoxRenderer implements Disposable {

	private ShaderProgram skyBoxShader;
	private Matrix4f skyBoxViewMatrix;

	public SkyBoxRenderer() {
		skyBoxShader = new ShaderProgram(Radiant.files.getFileContents("src/main/resources/shaders/skybox.vs"),
				Radiant.files.getFileContents("src/main/resources/shaders/skybox.fs"));
		skyBoxViewMatrix = new Matrix4f();
		skyBoxShader.addUniform("projectionMatrix");
		skyBoxShader.addUniform("viewMatrix");
		skyBoxShader.addUniform("modelMatrix");
		skyBoxShader.addUniform("txtSampler");
		skyBoxShader.addUniform("diffuse");
		skyBoxShader.addUniform("hasTexture");
	}

	public void render(Scene scene) {
		SkyBox skyBox = scene.getSceneSkybox();
		if (skyBox == null)
			return;

		skyBoxShader.bind();
		skyBoxShader.setUniform("projectionMatrix", scene.getProjection().getProjectionMatrix());
		skyBoxViewMatrix.m30(0).m31(0).m32(0);
		skyBoxShader.setUniform("viewMatrix", skyBoxViewMatrix);
		skyBoxShader.setUniform("txtSampler", 0);

		Model skyBoxModel = skyBox.getSkyBoxModel();
		Entity skyBoxEntity = skyBox.getSkyBoxEntity();

		for (Material material : skyBoxModel.getMaterialList()) {
			Texture texture = Radiant.resources.getTexture(material.getTexturePath());
			glActiveTexture(GL_TEXTURE0);
			texture.bind();

			skyBoxShader.setUniform("diffuse", material.getDiffuseColor());
			skyBoxShader.setUniform("hasTexture",
					texture.getTexturePath().equals(ResourceLoader.DEFAULT_TEXTURE) ? 0 : 1);

			for (Mesh mesh : material.getMeshList()) {
				skyBoxShader.setUniform("modelMatrix", skyBoxEntity.getModelTransform().getTransformationMatrix());
				mesh.draw();				
			}

		}

		glBindVertexArray(0);		
		skyBoxShader.unbind();
	}

	@Override
	public void onDispose() {
		skyBoxShader.onDispose();
	}

}