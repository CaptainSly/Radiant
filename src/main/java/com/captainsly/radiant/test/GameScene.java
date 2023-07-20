package com.captainsly.radiant.test;

import static org.lwjgl.glfw.GLFW.*;

import org.joml.Vector2f;
import org.joml.Vector3f;

import com.captainsly.radiant.core.Game;
import com.captainsly.radiant.core.Radiant;
import com.captainsly.radiant.core.entity.Actor;
import com.captainsly.radiant.core.render.gl.AnimationData;
import com.captainsly.radiant.core.render.gl.Camera;
import com.captainsly.radiant.core.render.gl.Fog;
import com.captainsly.radiant.core.render.gl.lights.DirectionalLight;
import com.captainsly.radiant.core.render.gl.model.Model;
import com.captainsly.radiant.core.scene.Scene;
import com.captainsly.radiant.core.scene.SceneLights;

public class GameScene extends Scene {
	private static final float MOUSE_SENSITIVITY = 0.1f;
	private static final float MOVEMENT_SPEED = 1f;

	private AnimationData animationData;

	private float lightAngle;

	public GameScene(int width, int height, Game game) {
		super(width, height, game);

	}

	@Override
	public void onInit() {
		Actor frogActor = new Actor("frog_actor", "frog_model", "resources/models/frog/frog.fbx", true);
		frogActor.setPosition(0, 5, -2);
		frogActor.setScale(0.0008f);
		animationData = new AnimationData(frogActor.getActorModel().getAnimationList().get(0));
		frogActor.setAnimationData(animationData);
		addActor(frogActor);

		SceneLights sceneLights = new SceneLights();
		sceneLights.getAmbientLight().setIntensity(0.2f);
		DirectionalLight dirLight = sceneLights.getDirLight();
		dirLight.setPosition(1, 1, 0);
		dirLight.setIntensity(1.0f);
		setSceneLights(sceneLights);

		Camera camera = getGame().getCamera();
		camera.moveUp(5.0f);

		lightAngle = -35;

		setSceneFog(new Fog(false, new Vector3f(0.5f, 0.5f, 0.5f), 0.025f));
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

	public void onRenderGui() {
	}

	float temp = 0.0f;
	
	
	@Override
	public void onUpdate(double delta) {
		temp += delta * 1.5f;
//		float tempSin = (float) Math.sin(temp);
//		float tempCos = (float) Math.cos(temp);
//		animationData.nextFrame();
		animationData.nextFrame();
	}

	@Override
	public void dispose() {
		getModelMap().values().stream().forEach(Model::onDispose);

	}

}
