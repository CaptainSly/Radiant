package com.captainsly.radiant.test;

import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_LEFT;
import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_RIGHT;

import org.joml.Vector2f;
import org.joml.Vector3f;

import com.captainsly.radiant.core.Radiant;
import com.captainsly.radiant.core.render.gl.lights.AmbientLight;
import com.captainsly.radiant.core.render.gl.lights.DirectionalLight;
import com.captainsly.radiant.core.render.gl.lights.PointLight;
import com.captainsly.radiant.core.render.gl.lights.SpotLight;
import com.captainsly.radiant.core.scene.Scene;
import com.captainsly.radiant.core.scene.SceneLights;

import imgui.ImGui;
import imgui.ImGuiIO;
import imgui.flag.ImGuiCond;

public class LightControl {

	private float[] ambientColor;
	private float[] ambientFactor;
	private float[] dirConeX;
	private float[] dirConeY;
	private float[] dirConeZ;
	private float[] dirLightColor;
	private float[] dirLightIntensity;
	private float[] dirLightX;
	private float[] dirLightY;
	private float[] dirLightZ;
	private float[] pointLightColor;
	private float[] pointLightIntensity;
	private float[] pointLightX;
	private float[] pointLightY;
	private float[] pointLightZ;
	private float[] spotLightColor;
	private float[] spotLightCuttoff;
	private float[] spotLightIntensity;
	private float[] spotLightX;
	private float[] spotLightY;
	private float[] spotLightZ;

	public LightControl(Scene scene) {
		SceneLights sceneLights = scene.getSceneLights();
		AmbientLight ambientLight = sceneLights.getAmbientLight();
		Vector3f color = ambientLight.getColor();

		ambientFactor = new float[] { ambientLight.getIntensity() };
		ambientColor = new float[] { color.x, color.y, color.z };

		PointLight pointLight = sceneLights.getPointLights().get(0);
		color = pointLight.getColor();
		Vector3f pos = pointLight.getPosition();
		pointLightColor = new float[] { color.x, color.y, color.z };
		pointLightX = new float[] { pos.x };
		pointLightY = new float[] { pos.y };
		pointLightZ = new float[] { pos.z };
		pointLightIntensity = new float[] { pointLight.getIntensity() };

		SpotLight spotLight = sceneLights.getSpotLights().get(0);
		if (spotLight != null) {
		pointLight = spotLight.getPointLight();
		color = pointLight.getColor();
		pos = pointLight.getPosition();
		spotLightColor = new float[] { color.x, color.y, color.z };
		spotLightX = new float[] { pos.x };
		spotLightY = new float[] { pos.y };
		spotLightZ = new float[] { pos.z };
		spotLightIntensity = new float[] { pointLight.getIntensity() };
		spotLightCuttoff = new float[] { spotLight.getCutOffAngle() };
		Vector3f coneDir = spotLight.getConeDirection();
		dirConeX = new float[] { coneDir.x };
		dirConeY = new float[] { coneDir.y };
		dirConeZ = new float[] { coneDir.z };
		}
		DirectionalLight dirLight = sceneLights.getDirLight();
		color = dirLight.getColor();
		pos = dirLight.getDirection();
		dirLightColor = new float[] { color.x, color.y, color.z };
		dirLightX = new float[] { pos.x };
		dirLightY = new float[] { pos.y };
		dirLightZ = new float[] { pos.z };
		dirLightIntensity = new float[] { dirLight.getIntensity() };
	}

	public void drawGui() {
		ImGui.setNextWindowPos(0, 0, ImGuiCond.Always);
		ImGui.setNextWindowSize(450, 400);

		ImGui.begin("Lights controls");
		if (ImGui.collapsingHeader("Ambient Light")) {
			ImGui.sliderFloat("Ambient factor", ambientFactor, 0.0f, 1.0f, "%.2f");
			ImGui.colorEdit3("Ambient color", ambientColor);
		}

		if (ImGui.collapsingHeader("Point Light")) {
			ImGui.sliderFloat("Point Light - x", pointLightX, -10.0f, 10.0f, "%.2f");
			ImGui.sliderFloat("Point Light - y", pointLightY, -10.0f, 10.0f, "%.2f");
			ImGui.sliderFloat("Point Light - z", pointLightZ, -10.0f, 10.0f, "%.2f");
			ImGui.colorEdit3("Point Light color", pointLightColor);
			ImGui.sliderFloat("Point Light Intensity", pointLightIntensity, 0.0f, 1.0f, "%.2f");
		}

		if (ImGui.collapsingHeader("Spot Light")) {
			ImGui.sliderFloat("Spot Light - x", spotLightX, -10.0f, 10.0f, "%.2f");
			ImGui.sliderFloat("Spot Light - y", spotLightY, -10.0f, 10.0f, "%.2f");
			ImGui.sliderFloat("Spot Light - z", spotLightZ, -10.0f, 10.0f, "%.2f");
			ImGui.colorEdit3("Spot Light color", spotLightColor);
			ImGui.sliderFloat("Spot Light Intensity", spotLightIntensity, 0.0f, 1.0f, "%.2f");
			ImGui.separator();
			ImGui.sliderFloat("Spot Light cutoff", spotLightCuttoff, 0.0f, 360.0f, "%2.f");
			ImGui.sliderFloat("Dir cone - x", dirConeX, -1.0f, 1.0f, "%.2f");
			ImGui.sliderFloat("Dir cone - y", dirConeY, -1.0f, 1.0f, "%.2f");
			ImGui.sliderFloat("Dir cone - z", dirConeZ, -1.0f, 1.0f, "%.2f");
		}

		if (ImGui.collapsingHeader("Dir Light")) {
			ImGui.sliderFloat("Dir Light - x", dirLightX, -1.0f, 1.0f, "%.2f");
			ImGui.sliderFloat("Dir Light - y", dirLightY, -1.0f, 1.0f, "%.2f");
			ImGui.sliderFloat("Dir Light - z", dirLightZ, -1.0f, 1.0f, "%.2f");
			ImGui.colorEdit3("Dir Light color", dirLightColor);
			ImGui.sliderFloat("Dir Light Intensity", dirLightIntensity, 0.0f, 1.0f, "%.2f");
		}

		ImGui.end();
	}

	public boolean handleGuiInput(Scene scene) {
        ImGuiIO imGuiIO = ImGui.getIO();
        Vector2f mousePos = Radiant.input.getCurrentMousePosition();
        imGuiIO.setMousePos(mousePos.x, mousePos.y);
        imGuiIO.setMouseDown(0, Radiant.input.isMouseButtonDown(GLFW_MOUSE_BUTTON_LEFT));
        imGuiIO.setMouseDown(1, Radiant.input.isMouseButtonDown(GLFW_MOUSE_BUTTON_RIGHT));
        
        boolean consumed = imGuiIO.getWantCaptureMouse() || imGuiIO.getWantCaptureKeyboard();
        if (consumed) {
            SceneLights sceneLights = scene.getSceneLights();
            AmbientLight ambientLight = sceneLights.getAmbientLight();
            ambientLight.setIntensity(ambientFactor[0]);
            ambientLight.setColor(ambientColor[0], ambientColor[1], ambientColor[2]);

            PointLight pointLight = sceneLights.getPointLights().get(0);
            pointLight.setPosition(pointLightX[0], pointLightY[0], pointLightZ[0]);
            pointLight.setColor(pointLightColor[0], pointLightColor[1], pointLightColor[2]);
            pointLight.setIntensity(pointLightIntensity[0]);

            SpotLight spotLight = sceneLights.getSpotLights().get(0);
            pointLight = spotLight.getPointLight();
            pointLight.setPosition(spotLightX[0], spotLightY[0], spotLightZ[0]);
            pointLight.setColor(spotLightColor[0], spotLightColor[1], spotLightColor[2]);
            pointLight.setIntensity(spotLightIntensity[0]);
            spotLight.setCutOffAngle(spotLightColor[0]);
            spotLight.setConeDirection(dirConeX[0], dirConeY[0], dirConeZ[0]);

            DirectionalLight dirLight = sceneLights.getDirLight();
            dirLight.setPosition(dirLightX[0], dirLightY[0], dirLightZ[0]);
            dirLight.setColor(dirLightColor[0], dirLightColor[1], dirLightColor[2]);
            dirLight.setIntensity(dirLightIntensity[0]);
        }
        return consumed;
    }

}
