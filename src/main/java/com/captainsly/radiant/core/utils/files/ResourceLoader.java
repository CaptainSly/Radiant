package com.captainsly.radiant.core.utils.files;

import static org.lwjgl.assimp.Assimp.*;

import java.io.File;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.joml.Vector4f;
import org.lwjgl.PointerBuffer;
import org.lwjgl.assimp.*;
import org.lwjgl.system.MemoryStack;

import com.captainsly.radiant.core.Radiant;
import com.captainsly.radiant.core.impl.Disposable;
import com.captainsly.radiant.core.render.gl.Texture;
import com.captainsly.radiant.core.render.gl.mesh.Mesh;
import com.captainsly.radiant.core.render.gl.mesh.Vertex;
import com.captainsly.radiant.core.render.gl.model.Material;
import com.captainsly.radiant.core.render.gl.model.Model;
import com.captainsly.radiant.core.render.gl.shaders.ShaderProgram;
import com.captainsly.radiant.core.utils.Utils;

public class ResourceLoader implements Disposable {

	public static final String DEFAULT_TEXTURE = "src/main/resources/textures/default.png";

	private Map<String, Texture> textureMap;

	public ResourceLoader() {
		textureMap = new HashMap<>();
		textureMap.put(DEFAULT_TEXTURE, new Texture(DEFAULT_TEXTURE));
	}

	/**
	 * Always assumes that the shader's fragment and vertex files will be named like
	 * such, shaderName.vs, shaderName.fs
	 * 
	 * @param shaderName - The Shader to load
	 * @return the ShaderProgram for this Shader
	 */
	public ShaderProgram getShader(String shaderName) {
		ShaderProgram shader = new ShaderProgram(Radiant.files.getFileContents(shaderName + ".vs"),
				Radiant.files.getFileContents(shaderName + ".fs"));
		return shader;
	}

	// -=-=-=-=-=-=-=-=-=-=-=-=-=-=-
	// Texture Stuff
	// -=-=-=-=-=-=-=-=-=-=-=-=-=-=-

	public Texture createTexture(String texturePath) {
		return textureMap.computeIfAbsent(texturePath, Texture::new);
	}

	public Texture getTexture(String texturePath) {
		Texture texture = null;
		if (texturePath != null)
			texture = textureMap.get(texturePath);

		if (texture == null)
			texture = textureMap.get(DEFAULT_TEXTURE);

		return texture;
	}

	// -=-=-=-=-=-=-=-=-=-=-=-=-=-=-
	// Models
	// -=-=-=-=-=-=-=-=-=-=-=-=-=-=-
	public Model loadModel(String modelId, String modelPath) {
		return loadModel(modelId, modelPath,
				aiProcess_GenSmoothNormals | aiProcess_JoinIdenticalVertices | aiProcess_Triangulate
						| aiProcess_FixInfacingNormals | aiProcess_CalcTangentSpace | aiProcess_LimitBoneWeights
						| aiProcess_PreTransformVertices);

	}

	public Model loadModel(String modelId, String modelPath, int flags) {
		File file = new File(modelPath);
		if (!file.exists())
			throw new RuntimeException("Model path does not exist [" + modelPath + "]");

		String modelDir = file.getParent();

		AIScene aiScene = aiImportFile(modelPath, flags);
		if (aiScene == null)
			throw new RuntimeException("Error loading model [modelPath: " + modelPath + "]");

		int numMaterials = aiScene.mNumMaterials();
		List<Material> materialList = new ArrayList<>();
		for (int i = 0; i < numMaterials; i++) {
			AIMaterial aiMaterial = AIMaterial.create(aiScene.mMaterials().get(i));
			materialList.add(processMaterial(aiMaterial, modelDir));
		}

		int numMeshes = aiScene.mNumMeshes();
		PointerBuffer aiMeshes = aiScene.mMeshes();
		Material defaultMaterial = new Material();
		for (int i = 0; i < numMeshes; i++) {
			AIMesh aiMesh = AIMesh.create(aiMeshes.get());
			Mesh mesh = processMesh(aiMesh);

			int materialIdx = aiMesh.mMaterialIndex();
			Material material;
			if (materialIdx >= 0 && materialIdx < materialList.size()) {
				material = materialList.get(materialIdx);
			} else {
				material = defaultMaterial;
			}

			material.getMaterialMeshList().add(mesh);
		}

		if (!defaultMaterial.getMaterialMeshList().isEmpty())
			materialList.add(defaultMaterial);

		return new Model(modelId, materialList);
	}

	private Material processMaterial(AIMaterial aiMaterial, String modelDir) {
		Material material = new Material();
		try (MemoryStack stack = MemoryStack.stackPush()) {
			AIColor4D color = AIColor4D.create();

			int result = aiGetMaterialColor(aiMaterial, AI_MATKEY_COLOR_DIFFUSE, aiTextureType_NONE, 0, color);
			if (result == aiReturn_SUCCESS) {
				material.setDiffuseColor(new Vector4f(color.r(), color.g(), color.b(), color.a()));
			}

			AIString aiTexturePath = AIString.calloc(stack);
			aiGetMaterialTexture(aiMaterial, aiTextureType_DIFFUSE, 0, aiTexturePath, (IntBuffer) null, null, null,
					null, null, null);
			String texturePath = aiTexturePath.dataString();
			if (texturePath != null && texturePath.length() > 0) {
				material.setTexturePath(modelDir + File.separator + new File(texturePath).getName());
				createTexture(material.getTexturePath());
				material.setDiffuseColor(Material.DEFAULT_COLOR);
			}

			return material;
		}
	}

	private Mesh processMesh(AIMesh aiMesh) {
		Vertex[] vertices = processVertices(aiMesh);
		float[] textCoords = processTextCoords(aiMesh);
		int[] indices = processIndices(aiMesh);

		// Texture coordinates may not have been populated. We need at least the empty
		// slots
		if (textCoords.length == 0) {
			int numElements = (vertices.length / 3) * 2;
			textCoords = new float[numElements];
		}

		return new Mesh(vertices, textCoords, indices);
	}

	private int[] processIndices(AIMesh aiMesh) {
		List<Integer> indices = new ArrayList<>();
		int numFaces = aiMesh.mNumFaces();
		AIFace.Buffer aiFaces = aiMesh.mFaces();
		for (int i = 0; i < numFaces; i++) {
			AIFace aiFace = aiFaces.get(i);
			IntBuffer buffer = aiFace.mIndices();
			while (buffer.remaining() > 0) {
				indices.add(buffer.get());
			}
		}
		return indices.stream().mapToInt(Integer::intValue).toArray();
	}

	private float[] processTextCoords(AIMesh aiMesh) {
		AIVector3D.Buffer buffer = aiMesh.mTextureCoords(0);
		if (buffer == null) {
			return new float[] {};
		}
		float[] data = new float[buffer.remaining() * 2];
		int pos = 0;
		while (buffer.remaining() > 0) {
			AIVector3D textCoord = buffer.get();
			data[pos++] = textCoord.x();
			data[pos++] = 1 - textCoord.y();
		}
		return data;
	}

	private Vertex[] processVertices(AIMesh aiMesh) {
		AIVector3D.Buffer buffer = aiMesh.mVertices();
		float[] data = new float[buffer.remaining() * 3];
		int pos = 0;
		while (buffer.remaining() > 0) {
			AIVector3D textCoord = buffer.get();
			data[pos++] = textCoord.x();
			data[pos++] = textCoord.y();
			data[pos++] = textCoord.z();
		}
		return Utils.floatArrToVertexArr(data);
	}

	@Override
	public void onDispose() {
		textureMap.values().stream().forEach(Texture::onDispose);
	}

}
