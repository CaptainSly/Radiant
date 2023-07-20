package com.captainsly.radiant.core.utils.files;

import static org.lwjgl.assimp.Assimp.*;

import java.io.File;
import java.nio.IntBuffer;
import java.util.*;

import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector4f;
import org.lwjgl.PointerBuffer;
import org.lwjgl.assimp.*;
import org.lwjgl.system.MemoryStack;

import com.captainsly.radiant.core.Radiant;
import com.captainsly.radiant.core.impl.Disposable;
import com.captainsly.radiant.core.render.gl.Texture;
import com.captainsly.radiant.core.render.gl.mesh.Mesh;
import com.captainsly.radiant.core.render.gl.model.Material;
import com.captainsly.radiant.core.render.gl.model.Model;
import com.captainsly.radiant.core.render.gl.model.Model.AnimatedFrame;
import com.captainsly.radiant.core.render.gl.model.Model.Animation;
import com.captainsly.radiant.core.render.gl.model.Node;
import com.captainsly.radiant.core.render.gl.shaders.ShaderProgram;
import com.captainsly.radiant.core.utils.Utils;

public class ResourceLoader implements Disposable {

	public static final String DEFAULT_TEXTURE = "resources/textures/default.png";
	public static final int MAX_BONES = 4098;
	private static final Matrix4f IDENTITY_MATRIX = new Matrix4f();

	public record AnimMeshData(float[] weights, int[] boneIds) {
	}

	private record Bone(int boneId, String boneName, Matrix4f offsetMatrix) {
	}

	private record VertexWeight(int boneId, int vertexId, float weight) {
	}

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
		shaderName = "resources/shaders/" + shaderName;

		ShaderProgram shader = new ShaderProgram(Radiant.files.getFileContents(shaderName + ".vert"),
				Radiant.files.getFileContents(shaderName + ".frag"));
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

	public Model getModel(String modelId, String modelPath, boolean animation) {
		return loadModel(modelId, modelPath,
				aiProcess_GenSmoothNormals | aiProcess_JoinIdenticalVertices | aiProcess_Triangulate
						| aiProcess_FixInfacingNormals | aiProcess_CalcTangentSpace | aiProcess_LimitBoneWeights
						| (animation ? 0 : aiProcess_PreTransformVertices));

	}

	private Model loadModel(String modelId, String modelPath, int flags) {
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
		List<Bone> boneList = new ArrayList<>();
		for (int i = 0; i < numMeshes; i++) {
			AIMesh aiMesh = AIMesh.create(aiMeshes.get());
			Mesh mesh = processMesh(aiMesh, boneList);

			int materialIdx = aiMesh.mMaterialIndex();
			Material material;
			if (materialIdx >= 0 && materialIdx < materialList.size()) {
				material = materialList.get(materialIdx);
			} else {
				material = defaultMaterial;
			}

			material.getMaterialMeshList().add(mesh);
		}

		List<Animation> animations = new ArrayList<>();
		int numAnimations = aiScene.mNumAnimations();
		if (numAnimations > 0) {
			Node rootNode = buildNodesTree(aiScene.mRootNode(), null);
			Matrix4f globalInverseTransformation = toMatrix(aiScene.mRootNode().mTransformation()).invert();
			animations = processAnimations(aiScene, boneList, rootNode, globalInverseTransformation);
		}

		if (!defaultMaterial.getMaterialMeshList().isEmpty())
			materialList.add(defaultMaterial);

		aiReleaseImport(aiScene);
		return new Model(modelId, materialList, animations);
	}

	private Node buildNodesTree(AINode aiNode, Node parentNode) {
		String nodeName = aiNode.mName().dataString();
		Node node = new Node(nodeName, parentNode, toMatrix(aiNode.mTransformation()));

		int numChildren = aiNode.mNumChildren();
		PointerBuffer aiChildren = aiNode.mChildren();
		for (int i = 0; i < numChildren; i++) {
			AINode aiChildNode = AINode.create(aiChildren.get(i));
			Node childNode = buildNodesTree(aiChildNode, node);
			node.addChild(childNode);
		}

		return node;
	}

	private List<Animation> processAnimations(AIScene aiScene, List<Bone> boneList, Node rootNode,
			Matrix4f globalInverseTransformation) {
		List<Animation> animations = new ArrayList<>();

		// Process all animations
		int numAnimations = aiScene.mNumAnimations();
		PointerBuffer aiAnimations = aiScene.mAnimations();
		for (int i = 0; i < numAnimations; i++) {
			AIAnimation aiAnimation = AIAnimation.create(aiAnimations.get(i));
			int maxFrames = calcAnimationMaxFrames(aiAnimation);

			List<AnimatedFrame> frames = new ArrayList<>();
			Animation animation = new Animation(aiAnimation.mName().dataString(), aiAnimation.mDuration(), frames);
			animations.add(animation);

			for (int j = 0; j < maxFrames; j++) {
				Matrix4f[] boneMatrices = new Matrix4f[MAX_BONES];
				Arrays.fill(boneMatrices, IDENTITY_MATRIX);
				AnimatedFrame animatedFrame = new AnimatedFrame(boneMatrices);
				buildFrameMatrices(aiAnimation, boneList, animatedFrame, j, rootNode, rootNode.getNodeTransformation(),
						globalInverseTransformation);
				frames.add(animatedFrame);
			}
		}
		return animations;
	}

	private int calcAnimationMaxFrames(AIAnimation aiAnimation) {
		int maxFrames = 0;
		int numNodeAnims = aiAnimation.mNumChannels();
		PointerBuffer aiChannels = aiAnimation.mChannels();
		for (int i = 0; i < numNodeAnims; i++) {
			AINodeAnim aiNodeAnim = AINodeAnim.create(aiChannels.get(i));
			int numFrames = Math.max(Math.max(aiNodeAnim.mNumPositionKeys(), aiNodeAnim.mNumScalingKeys()),
					aiNodeAnim.mNumRotationKeys());
			maxFrames = Math.max(maxFrames, numFrames);
		}

		return maxFrames;
	}

	private Matrix4f toMatrix(AIMatrix4x4 aiMatrix4x4) {
		Matrix4f result = new Matrix4f();
		result.m00(aiMatrix4x4.a1());
		result.m10(aiMatrix4x4.a2());
		result.m20(aiMatrix4x4.a3());
		result.m30(aiMatrix4x4.a4());
		result.m01(aiMatrix4x4.b1());
		result.m11(aiMatrix4x4.b2());
		result.m21(aiMatrix4x4.b3());
		result.m31(aiMatrix4x4.b4());
		result.m02(aiMatrix4x4.c1());
		result.m12(aiMatrix4x4.c2());
		result.m22(aiMatrix4x4.c3());
		result.m32(aiMatrix4x4.c4());
		result.m03(aiMatrix4x4.d1());
		result.m13(aiMatrix4x4.d2());
		result.m23(aiMatrix4x4.d3());
		result.m33(aiMatrix4x4.d4());

		return result;
	}

	private Material processMaterial(AIMaterial aiMaterial, String modelDir) {
		Material material = new Material();
		try (MemoryStack stack = MemoryStack.stackPush()) {
			AIColor4D color = AIColor4D.create();

			int result = aiGetMaterialColor(aiMaterial, AI_MATKEY_COLOR_AMBIENT, aiTextureType_NONE, 0, color);
			if (result == aiReturn_SUCCESS)
				material.setAmbientColor(new Vector4f(color.r(), color.g(), color.b(), color.a()));

			result = aiGetMaterialColor(aiMaterial, AI_MATKEY_COLOR_DIFFUSE, aiTextureType_NONE, 0, color);
			if (result == aiReturn_SUCCESS)
				material.setDiffuseColor(new Vector4f(color.r(), color.g(), color.b(), color.a()));

			result = aiGetMaterialColor(aiMaterial, AI_MATKEY_COLOR_SPECULAR, aiTextureType_NONE, 0, color);
			if (result == aiReturn_SUCCESS)
				material.setSpecularColor(new Vector4f(color.r(), color.g(), color.b(), color.a()));

			float reflectance = 0.0f;
			float[] shininessFactor = new float[] { 0.0f };
			int[] pMax = new int[] { 1 };
			result = aiGetMaterialFloatArray(aiMaterial, AI_MATKEY_SHININESS_STRENGTH, aiTextureType_NONE, 0,
					shininessFactor, pMax);
			if (result != aiReturn_SUCCESS)
				reflectance = shininessFactor[0];
			material.setReflectance(reflectance);

			AIString aiTexturePath = AIString.calloc(stack);
			aiGetMaterialTexture(aiMaterial, aiTextureType_DIFFUSE, 0, aiTexturePath, (IntBuffer) null, null, null,
					null, null, null);
			String texturePath = aiTexturePath.dataString();
			if (texturePath != null && texturePath.length() > 0) {
				material.setTexturePath(modelDir + File.separator + new File(texturePath).getName());
				createTexture(material.getTexturePath());
				material.setDiffuseColor(Material.DEFAULT_COLOR);
			}

			AIString aiNormalMapPath = AIString.calloc(stack);
			aiGetMaterialTexture(aiMaterial, aiTextureType_NORMALS, 0, aiNormalMapPath, (IntBuffer) null, null, null,
					null, null, null);
			String normalMapPath = aiNormalMapPath.dataString();
			if (normalMapPath != null && normalMapPath.length() > 0) {
				material.setNormalMapPath(modelDir + File.separator + new File(normalMapPath).getName());
				createTexture(material.getNormalMapPath());
			}

			return material;
		}
	}

	private Mesh processMesh(AIMesh aiMesh, List<Bone> boneList) {
		float[] vertices = processVertices(aiMesh);

		float[] textCoords = processTextCoords(aiMesh);
		float[] normals = processNormals(aiMesh);
		float[] tangents = processTangents(aiMesh, normals);
		float[] bitangents = processBitangents(aiMesh, normals);

		AnimMeshData animMeshData = processBones(aiMesh, boneList);

		int[] indices = processIndices(aiMesh);

		// Texture coordinates may not have been populated. We need at least the empty
		// slots
		if (textCoords.length == 0) {
			int numElements = (vertices.length / 3) * 2;
			textCoords = new float[numElements];
		}

		return new Mesh(vertices, normals, tangents, bitangents, textCoords, indices, animMeshData.boneIds,
				animMeshData.weights);
	}

	private AnimMeshData processBones(AIMesh aiMesh, List<Bone> boneList) {
		List<Integer> boneIds = new ArrayList<>();
		List<Float> weights = new ArrayList<>();

		Map<Integer, List<VertexWeight>> weightSet = new HashMap<>();
		int numBones = aiMesh.mNumBones();
		PointerBuffer aiBones = aiMesh.mBones();
		for (int i = 0; i < numBones; i++) {
			AIBone aiBone = AIBone.create(aiBones.get());
			int id = boneList.size();
			Bone bone = new Bone(id, aiBone.mName().dataString(), toMatrix(aiBone.mOffsetMatrix()));
			boneList.add(bone);
			int numWeights = aiBone.mNumWeights();
			AIVertexWeight.Buffer aiWeights = aiBone.mWeights();
			for (int j = 0; j < numWeights; j++) {
				AIVertexWeight aiWeight = aiWeights.get(j);
				VertexWeight vw = new VertexWeight(bone.boneId, aiWeight.mVertexId(), aiWeight.mWeight());
				List<VertexWeight> vertexWeightList = weightSet.get(vw.vertexId);
				if (vertexWeightList == null) {
					vertexWeightList = new ArrayList<>();
					weightSet.put(vw.vertexId, vertexWeightList);
				}
				vertexWeightList.add(vw);
			}
		}

		int numVertices = aiMesh.mNumVertices();
		for (int i = 0; i < numVertices; i++) {
			List<VertexWeight> vertexWeightList = weightSet.get(i);
			int size = vertexWeightList != null ? vertexWeightList.size() : 0;
			for (int j = 0; j < Mesh.MAX_WEIGHTS; j++) {
				if (j < size) {
					VertexWeight vw = vertexWeightList.get(j);
					weights.add(vw.weight);
					boneIds.add(vw.boneId);
				} else {
					weights.add(0.0f);
					boneIds.add(0);
				}
			}
		}

		return new AnimMeshData(Utils.listFloatToArray(weights), Utils.listIntToArray(boneIds));
	}

	private float[] processTangents(AIMesh aiMesh, float[] normals) {
		AIVector3D.Buffer buffer = aiMesh.mTangents();
		float[] data = new float[buffer.remaining() * 3];
		int pos = 0;
		while (buffer.remaining() > 0) {
			AIVector3D aiTangent = buffer.get();
			data[pos++] = aiTangent.x();
			data[pos++] = aiTangent.y();
			data[pos++] = aiTangent.z();
		}

		// Assimp may not calculate tangents with models that do not have texture
		// coordinates. Just create empty values
		if (data.length == 0) {
			data = new float[normals.length];
		}
		return data;
	}

	private void buildFrameMatrices(AIAnimation aiAnimation, List<Bone> boneList, Model.AnimatedFrame animatedFrame,
			int frame, Node node, Matrix4f parentTransformation, Matrix4f globalInverseTransform) {
		String nodeName = node.getName();
		AINodeAnim aiNodeAnim = findAIAnimNode(aiAnimation, nodeName);
		Matrix4f nodeTransform = node.getNodeTransformation();
		if (aiNodeAnim != null) {
			nodeTransform = buildNodeTransformationMatrix(aiNodeAnim, frame);
		}
		Matrix4f nodeGlobalTransform = new Matrix4f(parentTransformation).mul(nodeTransform);

		List<Bone> affectedBones = boneList.stream().filter(b -> b.boneName().equals(nodeName)).toList();
		for (Bone bone : affectedBones) {
			Matrix4f boneTransform = new Matrix4f(globalInverseTransform).mul(nodeGlobalTransform)
					.mul(bone.offsetMatrix());
			animatedFrame.boneMatrices()[bone.boneId()] = boneTransform;
		}

		for (Node childNode : node.getChildren()) {
			buildFrameMatrices(aiAnimation, boneList, animatedFrame, frame, childNode, nodeGlobalTransform,
					globalInverseTransform);
		}
	}

	private Matrix4f buildNodeTransformationMatrix(AINodeAnim aiNodeAnim, int frame) {
		AIVectorKey.Buffer positionKeys = aiNodeAnim.mPositionKeys();
		AIVectorKey.Buffer scalingKeys = aiNodeAnim.mScalingKeys();
		AIQuatKey.Buffer rotationKeys = aiNodeAnim.mRotationKeys();

		AIVectorKey aiVecKey;
		AIVector3D vec;

		Matrix4f nodeTransform = new Matrix4f();
		int numPositions = aiNodeAnim.mNumPositionKeys();
		if (numPositions > 0) {
			aiVecKey = positionKeys.get(Math.min(numPositions - 1, frame));
			vec = aiVecKey.mValue();
			nodeTransform.translate(vec.x(), vec.y(), vec.z());
		}
		int numRotations = aiNodeAnim.mNumRotationKeys();
		if (numRotations > 0) {
			AIQuatKey quatKey = rotationKeys.get(Math.min(numRotations - 1, frame));
			AIQuaternion aiQuat = quatKey.mValue();
			Quaternionf quat = new Quaternionf(aiQuat.x(), aiQuat.y(), aiQuat.z(), aiQuat.w());
			nodeTransform.rotate(quat);
		}
		int numScalingKeys = aiNodeAnim.mNumScalingKeys();
		if (numScalingKeys > 0) {
			aiVecKey = scalingKeys.get(Math.min(numScalingKeys - 1, frame));
			vec = aiVecKey.mValue();
			nodeTransform.scale(vec.x(), vec.y(), vec.z());
		}

		return nodeTransform;
	}

	private AINodeAnim findAIAnimNode(AIAnimation aiAnimation, String nodeName) {
		AINodeAnim result = null;
		int numAnimNodes = aiAnimation.mNumChannels();
		PointerBuffer aiChannels = aiAnimation.mChannels();
		for (int i = 0; i < numAnimNodes; i++) {
			AINodeAnim aiNodeAnim = AINodeAnim.create(aiChannels.get(i));
			if (nodeName.equals(aiNodeAnim.mNodeName().dataString())) {
				result = aiNodeAnim;
				break;
			}
		}
		return result;
	}

	private float[] processBitangents(AIMesh aiMesh, float[] normals) {
		AIVector3D.Buffer buffer = aiMesh.mBitangents();
		float[] data = new float[buffer.remaining() * 3];
		int pos = 0;
		while (buffer.remaining() > 0) {
			AIVector3D aiBitangent = buffer.get();
			data[pos++] = aiBitangent.x();
			data[pos++] = aiBitangent.y();
			data[pos++] = aiBitangent.z();
		}

		// Assimp may not calculate tangents with models that do not have texture
		// coordinates. Just create empty values
		if (data.length == 0) {
			data = new float[normals.length];
		}
		return data;
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

	private float[] processNormals(AIMesh aiMesh) {
		AIVector3D.Buffer buffer = aiMesh.mNormals();

		float[] data = new float[buffer.remaining() * 3];
		int pos = 0;
		while (buffer.remaining() > 0) {
			AIVector3D normal = buffer.get();
			data[pos++] = normal.x();
			data[pos++] = normal.y();
			data[pos++] = normal.z();
		}

		return data;
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

	private float[] processVertices(AIMesh aiMesh) {
		AIVector3D.Buffer buffer = aiMesh.mVertices();
		float[] data = new float[buffer.remaining() * 3];
		int pos = 0;
		while (buffer.remaining() > 0) {
			AIVector3D textCoord = buffer.get();
			data[pos++] = textCoord.x();
			data[pos++] = textCoord.y();
			data[pos++] = textCoord.z();
		}
		return data;
	}

	@Override
	public void onDispose() {
		textureMap.values().stream().forEach(Texture::onDispose);
	}

}
