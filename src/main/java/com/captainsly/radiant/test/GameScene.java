package com.captainsly.radiant.test;

import static org.lwjgl.glfw.GLFW.*;

import org.joml.Vector2f;

import com.captainsly.radiant.core.Game;
import com.captainsly.radiant.core.Radiant;
import com.captainsly.radiant.core.entity.Actor;
import com.captainsly.radiant.core.render.gl.model.Model;
import com.captainsly.radiant.core.scene.Scene;

public class GameScene extends Scene {

	private Actor cubeEntity;

	public GameScene(int width, int height, Game game) {
		super(width, height, game);
	}

	@Override
	public void onInit() {
		getGame().getCamera().setPosition(0, 1, 0);
		
		Model frogModel = Radiant.resources.loadModel("frog-model", "src/main/resources/scene.gltf");
		addModel(frogModel);
		cubeEntity = new Actor("frog-entity", frogModel);
		cubeEntity.setPosition(0, 0, -2);
		cubeEntity.setScale(0.0004f);
		addEntity(cubeEntity);
		
	}

	private static final float MOUSE_SENSITIVITY = 0.1f;
	private static final float MOVEMENT_SPEED = 5f;

	@Override
	public void onInput(double delta) {

		float speed = (float) (MOVEMENT_SPEED * delta);

		if (Radiant.input.isKeyDown(GLFW_KEY_LEFT_SHIFT))
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

		if (Radiant.input.isMouseButtonDown(GLFW_MOUSE_BUTTON_RIGHT)) {
			Vector2f displayVec = Radiant.input.getMouseDisplayVec();
			getGame().getCamera().addRotation((float) Math.toRadians(-displayVec.x * MOUSE_SENSITIVITY),
					(float) Math.toRadians(-displayVec.y * MOUSE_SENSITIVITY));
		}

	}

	float temp = 0.0f;

	@Override
	public void onUpdate(double delta) {
//		temp += delta;
//		float tempSin = (float) Math.sin(temp);
//		float tempCos = (float) Math.cos(temp);
	}

	@Override
	public void dispose() {
		getModelMap().values().stream().forEach(Model::onDispose);

	}

}
