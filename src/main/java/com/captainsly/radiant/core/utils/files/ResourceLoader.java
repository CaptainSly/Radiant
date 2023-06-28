package com.captainsly.radiant.core.utils.files;

import java.util.ArrayList;

import com.captainsly.radiant.core.Radiant;
import com.captainsly.radiant.core.render.mesh.Mesh;
import com.captainsly.radiant.core.render.mesh.Vertex;
import com.captainsly.radiant.core.render.shaders.ShaderProgram;

public class ResourceLoader {

	/**
	 * Always assumes that the shader's fragment and vertex files will be named like
	 * such, shaderName.vs, shaderName.fs
	 * 
	 * @param shaderName - The Shader to load
	 * @return the ShaderProgram for this Shader
	 */
	public ShaderProgram loadShader(String shaderName) {
		ShaderProgram shader = new ShaderProgram(Radiant.files.getFileContents(shaderName + ".vs"),
				Radiant.files.getFileContents(shaderName + ".fs"));
		return shader;
	}

	/**
	 * Creates a mesh object from an OBJ Mesh of meshName
	 * 
	 * @param meshName - The OBJ File to create a mesh of
	 * @return - The mesh of the OBJ File
	 */
	public Mesh loadOBJMesh(String meshName) {
        Mesh mesh = null;
        String objFile = Radiant.files.getFileContents(meshName + ".obj");
        String[] objFileContents = objFile.split("\n");

        ArrayList<Vertex> vertices = new ArrayList<>();
        ArrayList<Integer> indices = new ArrayList<>();

        for (String line : objFileContents) {
            if (line.isEmpty() || line.startsWith("#")) {
                continue;
            }

            String[] tokens = line.split("\\s+");

            if (tokens.length == 0) {
                continue;
            }

            switch (tokens[0]) {
                case "v":
                    float x = Float.parseFloat(tokens[1]);
                    float y = Float.parseFloat(tokens[2]);
                    float z = Float.parseFloat(tokens[3]);
                    vertices.add(new Vertex(x, y, z));
                    break;
                case "f":
                    for (int i = 1; i < tokens.length; i++) {
                        String[] vertexIndices = tokens[i].split("/");
                        int index = Integer.parseInt(vertexIndices[0]) - 1;
                        indices.add(index);
                    }
                    break;
                default:
                    continue;
            }
        }

        Vertex[] vertexData = vertices.toArray(new Vertex[0]);
        int[] indexData = indices.stream().mapToInt(Integer::intValue).toArray();

        mesh = new Mesh(vertexData, indexData);
        return mesh;
	}
	
}
