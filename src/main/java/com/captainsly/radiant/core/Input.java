package com.captainsly.radiant.core;

import static org.lwjgl.glfw.GLFW.GLFW_PRESS;
import static org.lwjgl.glfw.GLFW.GLFW_RELEASE;

import java.util.ArrayList;
import java.util.List;

import org.joml.Vector2f;

public class Input {

	private static List<Integer> currentKeys;
	private static List<Integer> currentMouseBtns;
	private static Vector2f currentMousePos, displayVec, previousMousePos, mouseScrollOffset;
	private static boolean isInWindow = true;

	public Input() {
		currentKeys = new ArrayList<>();
		currentMouseBtns = new ArrayList<>();

		currentMousePos = new Vector2f();
		previousMousePos = new Vector2f(-1, -1);
		displayVec = new Vector2f();
		mouseScrollOffset = new Vector2f();
	}

	// -=-=-=-=-=-=-=-=-=-=-=-=-=-=-
	// Governing Methods
	// -=-=-=-=-=-=-=-=-=-=-=-=-=-=-

	public void update() {
		displayVec.x = 0;
		displayVec.y = 0;

		if (previousMousePos.x > 0 && previousMousePos.y > 0 && isInWindow) {
			double deltaX = currentMousePos.x - previousMousePos.x;
			double deltaY = currentMousePos.y - previousMousePos.y;
			boolean rotateX = deltaX != 0;
			boolean rotateY = deltaY != 0;

			if (rotateX)
				displayVec.y = (float) deltaX;
			if (rotateY)
				displayVec.x = (float) deltaY;
		}
		
		previousMousePos.x = currentMousePos.x;
		previousMousePos.y = currentMousePos.y;
	}

	// -=-=-=-=-=-=-=-=-=-=-=-=-=-=-
	// Callback Methods
	// -=-=-=-=-=-=-=-=-=-=-=-=-=-=-

	// Keyboard Callback
	public static void key_callback(long windowPointer, int key, int scanCode, int action, int mods) {

		if (action == GLFW_PRESS) {
			if (!currentKeys.contains(key))
				currentKeys.add(key);
		} else if (action == GLFW_RELEASE)
			currentKeys.remove((Integer) key);

	}

	// Mouse Cursor Position Callback
	public static void cursor_position_callback(long windowPointer, double xPos, double yPos) {
		previousMousePos.set(currentMousePos);
		currentMousePos.set(xPos, yPos);
	}

	// Mouse Button Callback
	public static void mouse_button_callback(long windowPointer, int button, int action, int mods) {
		if (action == GLFW_PRESS) {
			if (!currentMouseBtns.contains(button))
				currentMouseBtns.add(button);
		} else if (action == GLFW_RELEASE)
			currentMouseBtns.remove((Integer) button);
	}

	// Cursor Enter Callback
	public static void cursor_enter_callback(long windowPointer, boolean entered) {
		isInWindow = entered;
	}

	// Mouse Scroll Callback
	public static void scroll_callback(long windowPointer, double xOffset, double yOffset) {
		mouseScrollOffset.set(xOffset, yOffset);
	}

	// -=-=-=-=-=-=-=-=-=-=-=-=-=-=-
	// Public Getters
	// -=-=-=-=-=-=-=-=-=-=-=-=-=-=-

	public boolean isKeyDown(int key) {
		return currentKeys.contains((Integer) key);
	}

	public boolean isMouseButtonDown(int button) {
		return currentMouseBtns.contains((Integer) button);
	}

	public Vector2f getCurrentMousePosition() {
		return currentMousePos;
	}

	public Vector2f getPreviousMousePosition() {
		return previousMousePos;
	}

	public Vector2f getMouseDisplayVec() {
		return displayVec;
	}

}
