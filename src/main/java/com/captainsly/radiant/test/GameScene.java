package com.captainsly.radiant.test;

import static org.lwjgl.glfw.GLFW.*;

import org.joml.Vector2f;
import org.joml.Vector3f;

import com.captainsly.radiant.core.Game;
import com.captainsly.radiant.core.Radiant;
import com.captainsly.radiant.core.entity.Actor;
import com.captainsly.radiant.core.render.gl.lights.AmbientLight;
import com.captainsly.radiant.core.render.gl.lights.DirectionalLight;
import com.captainsly.radiant.core.render.gl.lights.PointLight;
import com.captainsly.radiant.core.render.gl.model.Model;
import com.captainsly.radiant.core.scene.Scene;
import com.captainsly.radiant.core.scene.SceneLights;

public class GameScene extends Scene {
	private static final float MOUSE_SENSITIVITY = 0.1f;
	private static final float MOVEMENT_SPEED = 1f;

	public GameScene(int width, int height, Game game) {
		super(width, height, game);

	}

	@Override
	public void onInit() {
		getGame().getCamera().setPosition(0, .1f, 0);

		Actor terrainActor = new Actor("terrain_actor",
				Radiant.resources.getModel("terrain_model", "src/main/resources/models/quad/quad.obj"));
		terrainActor.setPosition(0, 0, 0);
		terrainActor.setScale(100f);
		addActor(terrainActor);

		Actor frogActor = new Actor("frog_actor",
				Radiant.resources.getModel("frog_model", "src/main/resources/models/frog/frog.fbx"));
		frogActor.setScale(0.00008f);
		frogActor.setPosition(0, 0.12f, -0.1895f);
		addActor(frogActor);

		SceneLights sceneLights = new SceneLights();
		AmbientLight ambientLight = sceneLights.getAmbientLight();
		ambientLight.setIntensity(0.5f);
		ambientLight.setColor(0.3f, 0.3f, 0.3f);

		PointLight pointLight = new PointLight(new Vector3f(1, 1, 1), 5f, new Vector3f(0, 0.15f, -0.1895f));
		sceneLights.getPointLights().add(pointLight);

		DirectionalLight dirLight = sceneLights.getDirLight();
		dirLight.setPosition(0, 1, 0);
		dirLight.setIntensity(1.0f);
		setSceneLights(sceneLights);

	}

	@Override
	public void onInput(double delta) {

		float speed = (float) (MOVEMENT_SPEED * delta);

		if (Radiant.input.isKeyDown(GLFW_KEY_LEFT_CONTROL))
			speed *= 5;

		if (Radiant.input.isKeyDown(GLFW_KEY_W)) {
			getGame().getCamera().moveForward(speed);
		}

		if (Radiant.input.isKeyDown(GLFW_KEY_S)) {
			getGame().getCamera().moveBackwards(speed);
		}

		if (Radiant.input.isKeyDown(GLFW_KEY_A)) {
			getGame().getCamera().moveLeft(speed);
		}

		if (Radiant.input.isKeyDown(GLFW_KEY_D)) {
			getGame().getCamera().moveRight(speed);
		}

		if (Radiant.input.isKeyDown(GLFW_KEY_LEFT_SHIFT))
			getGame().getCamera().moveUp(-speed);

		if (Radiant.input.isKeyDown(GLFW_KEY_SPACE))
			getGame().getCamera().moveUp(speed);

		if (Radiant.input.isMouseButtonDown(GLFW_MOUSE_BUTTON_RIGHT)) {
			Vector2f displayVec = Radiant.input.getMouseDisplayVec();
			getGame().getCamera().addRotation((float) Math.toRadians(-displayVec.x * MOUSE_SENSITIVITY),
					(float) Math.toRadians(-displayVec.y * MOUSE_SENSITIVITY));
		}

	}

	@Override
	public void onRender() {
	}

	public void onRenderGui() {
	}

	float temp = 0.0f;

	@Override
	public void onUpdate(double delta) {
		temp += delta * 0.5f;
//		float tempSin = (float) Math.sin(temp);
//		float tempCos = (float) Math.cos(temp);

	}

	@Override
	public void dispose() {
		getModelMap().values().stream().forEach(Model::onDispose);

	}

}
