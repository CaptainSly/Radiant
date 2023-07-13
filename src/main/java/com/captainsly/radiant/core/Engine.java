package com.captainsly.radiant.core;

import static org.lwjgl.glfw.GLFW.*;

import java.util.Random;

import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL;

import com.captainsly.radiant.core.impl.Disposable;
import com.captainsly.radiant.core.impl.GameLogic;
import com.captainsly.radiant.core.utils.RenderUtils;
import com.captainsly.radiant.core.utils.files.ResourceLoader;

public class Engine implements Disposable {

	private boolean isRunning = true;
	private double FRAME_COUNT = 5000.0;

	private GameLogic gameLogic;

	public Engine(GameLogic gameLogic) {
		this.gameLogic = gameLogic;
	}

	public void run() {

		onInit();
		onLoop();
		onDispose();

	}

	private void onInit() {
		GLFWErrorCallback.createPrint(System.err).set();

		if (!glfwInit())
			throw new IllegalStateException("Unable to initialize GLFW");

		Radiant.rnJesus = new Random(System.currentTimeMillis());
		Radiant.window = new Window("Project Radiant", 1280, 720);
		Radiant.input = new Input();

		// GLFW Callbacks
		glfwSetKeyCallback(Radiant.window.getWindowPointer(), Input::key_callback);
		glfwSetCursorPosCallback(Radiant.window.getWindowPointer(), Input::cursor_position_callback);
		glfwSetMouseButtonCallback(Radiant.window.getWindowPointer(), Input::mouse_button_callback);
		glfwSetScrollCallback(Radiant.window.getWindowPointer(), Input::scroll_callback);

		glfwMakeContextCurrent(Radiant.window.getWindowPointer());
		glfwSwapInterval(1);

		glfwShowWindow(Radiant.window.getWindowPointer());

		GL.createCapabilities();
		Radiant.window.init();

		Radiant.resources = new ResourceLoader();

		gameLogic.onInit();
	}

	private void onLoop() {

		final double frameTime = 1.0 / FRAME_COUNT;
		int frames = 0;
		int updates = 0;
		long frameCounter = 0;

		long lastTime = Time.getTime();
		double unprocessedTime = 0;

		while (isRunning) {

			boolean render = false;

			long startTime = Time.getTime();
			long passedTime = startTime - lastTime;
			lastTime = startTime;

			unprocessedTime += passedTime / (double) Time.SECOND;
			frameCounter += passedTime;

			while (unprocessedTime > frameTime) {
				unprocessedTime -= frameTime;
				render = true;

				if (Radiant.window.shouldWindowClose())
					isRunning = false;

				if (Radiant.input.isKeyDown(GLFW_KEY_ESCAPE))
					Radiant.window.setWindowShouldClose(true);

				Time.setDelta(frameTime);

				Radiant.input.update();
				gameLogic.onInput(Time.getDelta());

				gameLogic.onUpdate(Time.getDelta());
				updates++;

				if (frameCounter >= Time.SECOND) {
					Radiant.window.appendWindowTitle("FPS: " + frames + "| UPS: " + updates);
					frames = 0;
					updates = 0;
					frameCounter = 0;
				}

			}

			if (render) {

				RenderUtils.clearBuffer();
				
				// Render Game
				gameLogic.onRender();
				
				// Render Ui
				Radiant.window.newFrame();
				gameLogic.onRenderUi();
				Radiant.window.endFrame();
				
				RenderUtils.renderBuffer();
				
				frames++;
			} else {
				try {
					Thread.sleep(1);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}

	}

	public static class Time {

		public static final long SECOND = 1000000000L;

		private static double delta;

		private Time() {
		}

		public static long getTime() {
			return System.nanoTime();
		}

		public static double getDelta() {
			return delta;
		}

		public static void setDelta(double delta) {
			Time.delta = delta;
		}

	}

	@Override
	public void onDispose() {
		gameLogic.onDispose();
		Radiant.resources.onDispose();
		Radiant.window.onDispose();
		glfwTerminate();
		glfwSetErrorCallback(null).free();
	}

}
