package com.captainsly.radiant.core.utils;

import static org.lwjgl.glfw.GLFW.glfwPollEvents;
import static org.lwjgl.glfw.GLFW.glfwSwapBuffers;

import org.joml.Vector4f;
import org.lwjgl.opengl.GL32;

import com.captainsly.radiant.core.Radiant;

public class RenderUtils {

	public static Vector4f CLEAR_COLOR = new Vector4f(0.15f, 0.15f, 0.2f, 1.0f);

	public static void clearBuffer() {
		GL32.glClearColor(CLEAR_COLOR.x, CLEAR_COLOR.y, CLEAR_COLOR.z, CLEAR_COLOR.w);
		GL32.glClear(GL32.GL_COLOR_BUFFER_BIT | GL32.GL_DEPTH_BUFFER_BIT);
	}

	public static void clearBuffer(float x, float y, float z) {
		GL32.glClearColor(x, y, z, 1.0f);
		GL32.glClear(GL32.GL_COLOR_BUFFER_BIT | GL32.GL_DEPTH_BUFFER_BIT);
	}

	public static void renderBuffer() {
		glfwSwapBuffers(Radiant.window.getWindowPointer());
		glfwPollEvents();
	}

}
