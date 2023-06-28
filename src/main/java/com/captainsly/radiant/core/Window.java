package com.captainsly.radiant.core;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.glViewport;

import java.nio.IntBuffer;

import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

import com.captainsly.radiant.core.impl.Disposable;

public class Window implements Disposable {

	private final long windowPointer;

	private String windowTitle;
	private int windowWidth, windowHeight;

	public Window(String windowTitle, int windowWidth, int windowHeight) {
		this.windowTitle = windowTitle;
		this.windowWidth = windowWidth;
		this.windowHeight = windowHeight;

		// Configure GLFW
		glfwDefaultWindowHints();
		glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
		glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);

		windowPointer = glfwCreateWindow(windowWidth, windowHeight, windowTitle, MemoryUtil.NULL, MemoryUtil.NULL);
		if (windowPointer == MemoryUtil.NULL)
			throw new RuntimeException("Failed to create GLFW Window");

		glfwSetFramebufferSizeCallback(windowPointer, this::resize);

		try (MemoryStack stack = MemoryStack.stackPush()) {
			IntBuffer pWidth = stack.mallocInt(1);
			IntBuffer pHeight = stack.mallocInt(1);

			glfwGetWindowSize(windowPointer, pWidth, pHeight);

			GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());

			glfwSetWindowPos(windowPointer, (vidmode.width() - pWidth.get(0)) / 2,
					(vidmode.height() - pHeight.get(0)) / 2);
		}
	}

	private void resize(long windowPointer, int width, int height) {
		windowWidth = width;
		windowHeight = height;
		glViewport(0, 0, windowWidth, windowHeight);
	}

	public void setWindowTitle(String windowTitle) {
		this.windowTitle = windowTitle;
		glfwSetWindowTitle(windowPointer, windowTitle);
	}

	public void appendWindowTitle(String appenedTitle) {
		glfwSetWindowTitle(windowPointer, windowTitle + " " + appenedTitle);
	}

	public void setWindowShouldClose(boolean close) {
		glfwSetWindowShouldClose(windowPointer, close);
	}

	public boolean shouldWindowClose() {
		return glfwWindowShouldClose(windowPointer);
	}

	public long getWindowPointer() {
		return windowPointer;
	}

	public String getWindowTitle() {
		return windowTitle;
	}

	public int getWindowWidth() {
		return windowWidth;
	}

	public int getWindowHeight() {
		return windowHeight;
	}

	@Override
	public void onDispose() {
		glfwFreeCallbacks(windowPointer);
		glfwDestroyWindow(windowPointer);
	}

}
