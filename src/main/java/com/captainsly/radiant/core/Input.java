package com.captainsly.radiant.core;

import static org.lwjgl.glfw.GLFW.GLFW_PRESS;
import static org.lwjgl.glfw.GLFW.GLFW_RELEASE;

import java.util.ArrayList;
import java.util.List;

import org.joml.Vector2f;

public class Input {

	private static List<Integer> currentKeys;
	private static List<Integer> currentMouseBtns;
	private static Vector2f currentMousePosition, lastMousePosition, mouseDeltaPosition, mouseScrollOffset;

	public Input() {
		currentKeys = new ArrayList<>();
		currentMouseBtns = new ArrayList<>();

		currentMousePosition = new Vector2f();
		lastMousePosition = new Vector2f();
		mouseDeltaPosition = new Vector2f();
		mouseScrollOffset = new Vector2f();
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
		lastMousePosition.set(currentMousePosition);
		currentMousePosition.set(xPos, yPos);
		mouseDeltaPosition.set(currentMousePosition.sub(lastMousePosition));
	}

	// Mouse Button Callback
	public static void mouse_button_callback(long windowPointer, int button, int action, int mods) {
		if (action == GLFW_PRESS) {
			if (!currentMouseBtns.contains(button))
				currentMouseBtns.add(button);
		} else if (action == GLFW_RELEASE)
			currentMouseBtns.remove((Integer) button);
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
		return currentMousePosition;
	}

	public Vector2f getLastMousePosition() {
		return lastMousePosition;
	}

	public Vector2f getMouseDeltaPosition() {
		return mouseDeltaPosition;
	}

}
