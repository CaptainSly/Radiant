package com.captainsly.radiant.core.scene;

import static org.lwjgl.opengl.GL13.*;
import static org.lwjgl.opengl.GL13.glActiveTexture;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import com.captainsly.radiant.core.Game;
import com.captainsly.radiant.core.Radiant;
import com.captainsly.radiant.core.entity.Actor;
import com.captainsly.radiant.core.entity.Entity;
import com.captainsly.radiant.core.impl.Disposable;
import com.captainsly.radiant.core.render.gl.Fog;
import com.captainsly.radiant.core.render.gl.Projection;
import com.captainsly.radiant.core.render.gl.SkyBox;
import com.captainsly.radiant.core.render.gl.Texture;
import com.captainsly.radiant.core.render.gl.lights.*;
import com.captainsly.radiant.core.render.gl.mesh.Mesh;
import com.captainsly.radiant.core.render.gl.model.Material;
import com.captainsly.radiant.core.render.gl.model.Model;

public abstract class Scene implements Disposable {

	private SceneLights sceneLights;
	private SkyBox sceneSkyBox;
	private Fog fog;
	private Map<String, Model> modelMap;
	private Projection projection;
	private Game game;

	private static final int MAX_POINT_LIGHTS = 5;
	private static final int MAX_SPOT_LIGHTS = 5;

	public Scene(int width, int height, Game game) {
		this.game = game;
		modelMap = new HashMap<>();
		projection = new Projection(width, height);

		sceneLights = new SceneLights();

		sceneSkyBox = new SkyBox("src/main/resources/models/skybox/skybox.obj");
		sceneSkyBox.getSkyBoxEntity().setScale(50);

		fog = new Fog(true, new Vector3f(0.5f, 0.5f, 0.5f), 0.5f);

	}

	public abstract void onInit();

	public abstract void onRender();

	public abstract void onRenderGui();

	public abstract void onInput(double delta);

	public abstract void onUpdate(double delta);

	public abstract void dispose();

	private void updateLights() {
		Matrix4f viewMatrix = getGame().getCamera().getViewMatrix();
		AmbientLight ambientLight = sceneLights.getAmbientLight();
		game.getShader().setUniform("ambientLight.factor", ambientLight.getIntensity());
		game.getShader().setUniform("ambientLight.color", ambientLight.getColor());

		DirectionalLight dirLight = sceneLights.getDirLight();
		Vector4f auxDir = new Vector4f(dirLight.getDirection(), 0);
		auxDir.mul(viewMatrix);
		Vector3f dir = new Vector3f(auxDir.x, auxDir.y, auxDir.z);
		game.getShader().setUniform("dirLight.color", dirLight.getColor());
		game.getShader().setUniform("dirLight.direction", dir);
		game.getShader().setUniform("dirLight.intensity", dirLight.getIntensity());

		List<PointLight> pointLights = sceneLights.getPointLights();
		int numPointLights = pointLights.size();
		PointLight pointLight;
		for (int i = 0; i < MAX_POINT_LIGHTS; i++) {
			if (i < numPointLights) {
				pointLight = pointLights.get(i);
			} else {
				pointLight = null;
			}
			String name = "pointLights[" + i + "]";
			updatePointLight(pointLight, name, viewMatrix);
		}

		List<SpotLight> spotLights = sceneLights.getSpotLights();
		int numSpotLights = spotLights.size();
		SpotLight spotLight;
		for (int i = 0; i < MAX_SPOT_LIGHTS; i++) {
			if (i < numSpotLights) {
				spotLight = spotLights.get(i);
			} else {
				spotLight = null;
			}
			String name = "spotLights[" + i + "]";
			updateSpotLight(spotLight, name, viewMatrix);
		}
	}

	private void updatePointLight(PointLight pointLight, String prefix, Matrix4f viewMatrix) {
		Vector4f aux = new Vector4f();
		Vector3f lightPosition = new Vector3f();
		Vector3f color = new Vector3f();
		float intensity = 0.0f;
		float constant = 0.0f;
		float linear = 0.0f;
		float exponent = 0.0f;
		if (pointLight != null) {
			aux.set(pointLight.getPosition(), 1);
			aux.mul(viewMatrix);
			lightPosition.set(aux.x, aux.y, aux.z);
			color.set(pointLight.getColor());
			intensity = pointLight.getIntensity();
			Attenuation attenuation = pointLight.getAttenuation();
			constant = attenuation.getConstant();
			linear = attenuation.getLinear();
			exponent = attenuation.getExponent();
		}
		game.getShader().setUniform(prefix + ".position", lightPosition);
		game.getShader().setUniform(prefix + ".color", color);
		game.getShader().setUniform(prefix + ".intensity", intensity);
		game.getShader().setUniform(prefix + ".att.constant", constant);
		game.getShader().setUniform(prefix + ".att.linear", linear);
		game.getShader().setUniform(prefix + ".att.exponent", exponent);
	}

	private void updateSpotLight(SpotLight spotLight, String prefix, Matrix4f viewMatrix) {
		PointLight pointLight = null;
		Vector3f coneDirection = new Vector3f();
		float cutoff = 0.0f;
		if (spotLight != null) {
			coneDirection = spotLight.getConeDirection();
			cutoff = spotLight.getCutOff();
			pointLight = spotLight.getPointLight();
		}

		game.getShader().setUniform(prefix + ".conedir", coneDirection);
		game.getShader().setUniform(prefix + ".conedir", cutoff);
		updatePointLight(pointLight, prefix + ".pl", viewMatrix);
	}

	public void createUniforms() {
		// Lighting Uniforms
		game.getShader().addUniform("material.ambient");
		game.getShader().addUniform("material.diffuse");
		game.getShader().addUniform("material.specular");
		game.getShader().addUniform("material.reflectance");
		game.getShader().addUniform("material.hasNormalMap");

		game.getShader().addUniform("txtSampler");
		game.getShader().addUniform("normalSampler");

		game.getShader().addUniform("ambientLight.factor");
		game.getShader().addUniform("ambientLight.color");

		for (int i = 0; i < MAX_POINT_LIGHTS; i++) {
			String name = "pointLights[" + i + "]";
			game.getShader().addUniform(name + ".position");
			game.getShader().addUniform(name + ".color");
			game.getShader().addUniform(name + ".intensity");
			game.getShader().addUniform(name + ".att.constant");
			game.getShader().addUniform(name + ".att.linear");
			game.getShader().addUniform(name + ".att.exponent");
		}

		for (int i = 0; i < MAX_SPOT_LIGHTS; i++) {
			String name = "spotLights[" + i + "]";
			game.getShader().addUniform(name + ".pl.position");
			game.getShader().addUniform(name + ".pl.color");
			game.getShader().addUniform(name + ".pl.intensity");
			game.getShader().addUniform(name + ".pl.att.constant");
			game.getShader().addUniform(name + ".pl.att.linear");
			game.getShader().addUniform(name + ".pl.att.exponent");
			game.getShader().addUniform(name + ".conedir");
			game.getShader().addUniform(name + ".cutoff");
		}

		game.getShader().addUniform("dirLight.color");
		game.getShader().addUniform("dirLight.direction");
		game.getShader().addUniform("dirLight.intensity");

		game.getShader().addUniform("fog.color");
		game.getShader().addUniform("fog.density");
		game.getShader().addUniform("fog.turnonfog");
	}

	public void render() {
		updateLights();

		onRender();
		game.getShader().setUniform("txtSampler", 0);
		game.getShader().setUniform("normalSampler", 1);
		Collection<Model> models = getModelMap().values();
		for (Model model : models) {
			List<Entity> entities = model.getEntitiesList();

			for (Material material : model.getMaterialList()) {

				game.getShader().setUniform("material.ambient", material.getAmbientColor());
				game.getShader().setUniform("material.diffuse", material.getDiffuseColor());
				game.getShader().setUniform("material.specular", material.getSpecularColor());
				game.getShader().setUniform("material.reflectance", material.getReflectance());

				String normalMapPath = material.getNormalMapPath();
				boolean hasNormalMapPath = normalMapPath != null;
				game.getShader().setUniform("material.hasNormalMap", hasNormalMapPath ? 1 : 0);

				Texture texture = Radiant.resources.getTexture(material.getTexturePath());
				glActiveTexture(GL_TEXTURE0);
				texture.bind();

				if (hasNormalMapPath) {
					Texture normalMapTexture = Radiant.resources.getTexture(material.getNormalMapPath());
					glActiveTexture(GL_TEXTURE1);
					normalMapTexture.bind();
				}
				
				for (Mesh mesh : material.getMaterialMeshList()) {
					for (Entity entity : entities) {
						game.getShader().setUniform("modelMatrix",
								entity.getModelTransform().getTransformationMatrix());
						mesh.draw();
					}
				}

			}

		}

	}

	public void addEntity(Entity entity) {
		String modelId = entity.getModelId();
		Model model = modelMap.get(modelId);
		if (model == null)
			throw new RuntimeException("Could not find model [" + modelId + "]");

		model.getEntitiesList().add(entity);
	}

	public void addActor(Actor actor) {
		addModel(actor.getActorModel());
		addEntity(actor);
	}

	public void addModel(Model model) {
		modelMap.put(model.getModelId(), model);
	}

	public void setSceneLights(SceneLights sceneLights) {
		this.sceneLights = sceneLights;
	}

	public Map<String, Model> getModelMap() {
		return modelMap;
	}

	public Projection getProjection() {
		return projection;
	}

	public SkyBox getSceneSkybox() {
		return sceneSkyBox;
	}

	public SceneLights getSceneLights() {
		return sceneLights;
	}

	public Fog getFog() {
		return fog;
	}

	public Game getGame() {
		return game;
	}

	@Override
	public void onDispose() {
		sceneSkyBox.onDispose();
		modelMap.values().stream().forEach(Model::onDispose);
		dispose();
	}

}
