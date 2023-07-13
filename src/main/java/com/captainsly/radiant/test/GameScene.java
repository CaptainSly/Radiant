package com.captainsly.radiant.test;

import static org.lwjgl.glfw.GLFW.*;

import org.joml.Vector2f;
import org.joml.Vector3f;

import com.captainsly.radiant.core.Game;
import com.captainsly.radiant.core.Radiant;
import com.captainsly.radiant.core.entity.Actor;
import com.captainsly.radiant.core.entity.Entity;
import com.captainsly.radiant.core.render.gl.Camera;
import com.captainsly.radiant.core.render.gl.lights.PointLight;
import com.captainsly.radiant.core.render.gl.lights.SpotLight;
import com.captainsly.radiant.core.render.gl.model.Model;
import com.captainsly.radiant.core.scene.Scene;
import com.captainsly.radiant.core.scene.SceneLights;

public class GameScene extends Scene {

	private static final int NUM_CHUNKS = 4;

	private Entity[][] terrainEntities;
	private LightControl lc;

	public GameScene(int width, int height, Game game) {
		super(width, height, game);
	}

	@Override
	public void onInit() {
		createUniforms();

		getGame().getCamera().setPosition(0, .3f, 0);

		String quadModelId = "quad-model";
		Model quadModel = Radiant.resources.getModel(quadModelId, "src/main/resources/models/quad/quad.obj");

		int numRows = NUM_CHUNKS * 2 + 1;
		int numCols = numRows;
		terrainEntities = new Entity[numRows][numCols];
		for (int j = 0; j < numRows; j++) {
			for (int i = 0; i < numCols; i++) {
				Actor entity = new Actor("TERRAIN_" + j + "_" + i, quadModel);
				terrainEntities[j][i] = entity;
				addActor((Actor) terrainEntities[j][i]);
			}
		}

		SceneLights sceneLights = new SceneLights();
		sceneLights.getAmbientLight().setIntensity(0.3f);
		setSceneLights(sceneLights);
		sceneLights.getPointLights().add(new PointLight(new Vector3f(1, 1, 1), 1.0f, new Vector3f(0, 0, -1.4f)));

		Vector3f coneDir = new Vector3f(0, 0, -1);
		sceneLights.getSpotLights().add(
				new SpotLight(new PointLight(new Vector3f(1, 1, 1), 0.0f, new Vector3f(0, 0, -1.4f)), coneDir, 140.0f));

		lc = new LightControl(this);

	}

	private static final float MOUSE_SENSITIVITY = 0.1f;
	private static final float MOVEMENT_SPEED = 5f;

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

		lc.handleGuiInput(this);

	}

	@Override
	public void onRender() {
	}

	public void onRenderGui() {
		lc.drawGui();
	}

	float temp = 0.0f;

	@Override
	public void onUpdate(double delta) {
//		temp += delta;
//		float tempSin = (float) Math.sin(temp);
//		float tempCos = (float) Math.cos(temp);
		updateTerrain();
	}

	public void updateTerrain() {
		int cellSize = 10;
		Camera camera = getGame().getCamera();
		Vector3f cameraPos = camera.getPosition();
		int cellCol = (int) (cameraPos.x / cellSize);
		int cellRow = (int) (cameraPos.y / cellSize);

		int numRows = NUM_CHUNKS * 2 + 1;
		int numCols = numRows;
		int zOffset = -NUM_CHUNKS;
		float scale = cellSize / 2.0f;

		for (int j = 0; j < numRows; j++) {
			int xOffset = -NUM_CHUNKS;
			for (int i = 0; i < numCols; i++) {
				Entity entity = terrainEntities[j][i];
				entity.setScale(scale);
				entity.setPosition((cellCol + xOffset) * 2.0f, 0, (cellRow + zOffset) * 2.0f);
				xOffset++;
			}

			zOffset++;
		}
	}

	@Override
	public void dispose() {
		getModelMap().values().stream().forEach(Model::onDispose);

	}

}
