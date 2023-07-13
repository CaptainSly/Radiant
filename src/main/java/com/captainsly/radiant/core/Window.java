package com.captainsly.radiant.core;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.glViewport;

import java.nio.IntBuffer;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

import com.captainsly.radiant.core.impl.Disposable;

import imgui.ImGui;
import imgui.flag.ImGuiConfigFlags;
import imgui.gl3.ImGuiImplGl3;
import imgui.glfw.ImGuiImplGlfw;

public class Window implements Disposable {

	private final long windowPointer;
	private final ImGuiImplGlfw imGuiGLFW = new ImGuiImplGlfw();
	private final ImGuiImplGl3 imGuiGL = new ImGuiImplGl3();

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

	public void init() {
		ImGui.createContext();
		imGuiGLFW.init(windowPointer, true);
		imGuiGL.init("#version 330");
	}
	
	public void newFrame() {
		imGuiGLFW.newFrame();
		ImGui.newFrame();
	}
	
	public void endFrame() {
		ImGui.render();
		imGuiGL.renderDrawData(ImGui.getDrawData());
		
		if (ImGui.getIO().hasConfigFlags(ImGuiConfigFlags.ViewportsEnable)) {
			final long backupWindowPtr = GLFW.glfwGetCurrentContext();
			ImGui.updatePlatformWindows();
			ImGui.renderPlatformWindowsDefault();
			GLFW.glfwMakeContextCurrent(backupWindowPtr);
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

	public ImGuiImplGlfw getImGuiGLFW() {
		return imGuiGLFW;
	}

	public ImGuiImplGl3 getImGuiGL() {
		return imGuiGL;
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
		imGuiGL.dispose();
		imGuiGLFW.dispose();
		ImGui.destroyContext();

		glfwFreeCallbacks(windowPointer);
		glfwDestroyWindow(windowPointer);
	}

}
