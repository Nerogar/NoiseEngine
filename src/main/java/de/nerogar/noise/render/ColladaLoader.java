package de.nerogar.noise.render;

import de.nerogar.noise.Noise;
import de.nerogar.noise.file.ResourceDescriptor;
import de.nerogar.noise.math.Matrix4f;
import de.nerogar.noise.render.animation.Skeleton;
import de.nerogar.noise.util.Color;
import de.nerogar.noise.util.Logger;
import de.nerogar.noiseInterface.math.IMatrix4f;
import org.lwjgl.BufferUtils;
import org.lwjgl.PointerBuffer;
import org.lwjgl.assimp.*;
import org.lwjgl.system.MemoryStack;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ColladaLoader {

	private static AIPropertyStore aiPropertyStore;

	private static ByteBuffer readFile(ResourceDescriptor file) {
		byte[] fileBytes;
		try {
			fileBytes = file.asStream().readAllBytes();
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}

		ByteBuffer fileByteBuffer = BufferUtils.createByteBuffer(fileBytes.length + 1);
		fileByteBuffer.put(fileBytes);
		fileByteBuffer.put((byte) 0);
		fileByteBuffer.flip();

		return fileByteBuffer;
	}

	private static Material[] loadMaterials(AIScene aiScene) {
		Material[] materialArray = new Material[aiScene.mNumMaterials()];

		PointerBuffer materialBuffer = aiScene.mMaterials();

		try (MemoryStack stack = MemoryStack.stackPush()) {

			AIString aiString = AIString.callocStack(stack);
			AIColor4D aiColor4D = AIColor4D.callocStack(stack);

			for (int i = 0; i < aiScene.mNumMaterials(); i++) {
				AIMaterial aiMaterial = AIMaterial.create(materialBuffer.get(i));
				Material material = new Material();

				Assimp.aiGetMaterialString(aiMaterial, Assimp.AI_MATKEY_NAME, 0, 0, aiString);
				material.setName(aiString.dataString());

				Assimp.aiGetMaterialColor(aiMaterial, Assimp.AI_MATKEY_COLOR_EMISSIVE, 0, 0, aiColor4D);
				material.setEmissionColor(new Color(aiColor4D.r(), aiColor4D.g(), aiColor4D.b(), aiColor4D.a()));

				Assimp.aiGetMaterialColor(aiMaterial, Assimp.AI_MATKEY_COLOR_DIFFUSE, 0, 0, aiColor4D);
				material.setAlbedoColor(new Color(aiColor4D.r(), aiColor4D.g(), aiColor4D.b(), aiColor4D.a()));

				materialArray[i] = material;
			}
		}

		return materialArray;
	}

	private static AINode findAiNode(AINode aiNode, String name) {
		if (aiNode.mName().dataString().equals(name)) {
			return aiNode;
		}

		if (aiNode.mNumChildren() > 0) {
			PointerBuffer childrenBuffer = aiNode.mChildren();
			for (int child = 0; child < aiNode.mNumChildren(); child++) {
				AINode aiChildNode = AINode.create(childrenBuffer.get(child));

				AINode result = findAiNode(aiChildNode, name);
				if (result != null) {
					return result;
				}
			}
		}

		return null;
	}

	private static void getAllChildren(AINode aiNode, List<AINode> children) {
		if (aiNode.mNumChildren() > 0) {
			PointerBuffer childrenBuffer = aiNode.mChildren();
			for (int child = 0; child < aiNode.mNumChildren(); child++) {
				AINode aiChildNode = AINode.create(childrenBuffer.get(child));
				children.add(aiChildNode);
				getAllChildren(aiChildNode, children);
			}
		}
	}

	private static void getAllChildNames(AINode aiNode, Set<String> names) {
		if (aiNode.mNumChildren() > 0) {
			PointerBuffer childrenBuffer = aiNode.mChildren();
			for (int child = 0; child < aiNode.mNumChildren(); child++) {
				AINode aiChildNode = AINode.create(childrenBuffer.get(child));
				names.add(aiChildNode.mName().dataString());
				getAllChildNames(aiChildNode, names);
			}
		}
	}

	private static AINode findSkeletonRootBone(AIScene aiScene, String[] boneNames) {
		// this algorithm finds the common root bone of all bones in boneNames by going up the tree
		// from one of the bone nodes and searching for the first node that has all known bones as
		// a child.

		AINode aiNode = findAiNode(aiScene.mRootNode(), boneNames[0]);
		AINode rootNode = null;

		do {
			Set<String> childNames = new HashSet<>();
			getAllChildNames(aiNode, childNames);
			childNames.add(aiNode.mName().dataString());

			boolean containsAllBones = true;
			for (String boneName : boneNames) {
				if (!childNames.contains(boneName)) {
					containsAllBones = false;
					break;
				}
			}

			if (containsAllBones) {
				rootNode = aiNode;
			}

			aiNode = aiNode.mParent();
		} while (aiNode != null && rootNode == null);

		return rootNode;
	}

	private static Skeleton loadSkeleton(AINode aiRootNode) {
		List<AINode> bones = new ArrayList<>();
		bones.add(aiRootNode);
		getAllChildren(aiRootNode, bones);

		String[] boneNames = new String[bones.size()];
		int[] parents = new int[bones.size()];
		IMatrix4f[] bindPose = new IMatrix4f[bones.size()];

		for (int bone = 0; bone < bones.size(); bone++) {
			AINode aiNode = bones.get(bone);

			// name
			boneNames[bone] = aiNode.mName().dataString();

			// bind pose todo: apply parent transform
			AIMatrix4x4 offsetMatrix = aiNode.mTransformation();
			bindPose[bone] = new Matrix4f(
					offsetMatrix.a1(), offsetMatrix.a2(), offsetMatrix.a3(), offsetMatrix.a4(),
					offsetMatrix.b1(), offsetMatrix.b2(), offsetMatrix.b3(), offsetMatrix.b4(),
					offsetMatrix.c1(), offsetMatrix.c2(), offsetMatrix.c3(), offsetMatrix.c4(),
					offsetMatrix.d1(), offsetMatrix.d2(), offsetMatrix.d3(), offsetMatrix.d4()
			);

			// parent
			parents[bone] = -1;
			AINode aiParentNode = aiNode.mParent();
			if (aiParentNode != null) {
				String parentBoneName = aiParentNode.mName().dataString();
				for (int i = 0; i < bone; i++) {
					if (parentBoneName.equals(boneNames[i])) {
						parents[bone] = i;
						bindPose[bone].multiplyLeft(bindPose[i]);
						break;
					}
				}
			}
		}

		return new Skeleton(boneNames, parents, bindPose);
	}

	private static void loadMesh(AIMesh aiMesh, int[] indexArray, float[] positionArray, float[] normalArray, float[] uvArray) {
		AIVector3D.Buffer positionBuffer = aiMesh.mVertices();
		AIVector3D.Buffer normalBuffer = aiMesh.mNormals();
		AIVector3D.Buffer uvBuffer = aiMesh.mTextureCoords(0);

		AIFace.Buffer facesBuffer = aiMesh.mFaces();

		for (int vertex = 0; vertex < aiMesh.mNumVertices(); vertex++) {
			AIVector3D position = positionBuffer.get(vertex);
			positionArray[vertex * 3] = position.x();
			positionArray[vertex * 3 + 1] = position.y();
			positionArray[vertex * 3 + 2] = position.z();

			AIVector3D normal = normalBuffer.get(vertex);
			normalArray[vertex * 3] = normal.x();
			normalArray[vertex * 3 + 1] = normal.y();
			normalArray[vertex * 3 + 2] = normal.z();

			if (uvBuffer != null) {
				AIVector3D uv = uvBuffer.get(vertex);
				uvArray[vertex * 2] = uv.x();
				uvArray[vertex * 2 + 1] = uv.y();
			}
		}

		for (int face = 0; face < aiMesh.mNumFaces(); face++) {
			IntBuffer indexBuffer = facesBuffer.get(face).mIndices();

			indexArray[face * 3] = indexBuffer.get(0);
			indexArray[face * 3 + 1] = indexBuffer.get(1);
			indexArray[face * 3 + 2] = indexBuffer.get(2);
		}
	}

	private static void loadWeights(AIMesh aiMesh, Skeleton skeleton, int[] weightsCountArray, float[] boneWeightsArray, float[] boneIndexArray) {
		PointerBuffer bonesBuffer = aiMesh.mBones();

		for (int bone = 0; bone < aiMesh.mNumBones(); bone++) {
			AIBone aiBone = AIBone.create(bonesBuffer.get(bone));

			AIVertexWeight.Buffer weightsBuffer = aiBone.mWeights();

			for (int weight = 0; weight < aiBone.mNumWeights(); weight++) {
				AIVertexWeight aiVertexWeight = weightsBuffer.get(weight);

				int boneIndex = skeleton.getBoneIndex(aiBone.mName().dataString());

				boneIndexArray[aiVertexWeight.mVertexId() * 4 + weightsCountArray[aiVertexWeight.mVertexId()]] = Float.intBitsToFloat(boneIndex);
				boneWeightsArray[aiVertexWeight.mVertexId() * 4 + weightsCountArray[aiVertexWeight.mVertexId()]] = aiVertexWeight.mWeight();
				weightsCountArray[aiVertexWeight.mVertexId()]++;
			}
		}
	}

	public static List<RenderableObject> load(ResourceDescriptor file, Skeleton parameterSkeleton) {
		ByteBuffer fileByteBuffer = readFile(file);

		AIScene aiScene = Assimp.aiImportFileFromMemoryWithProperties(
				fileByteBuffer,
				Assimp.aiProcess_Triangulate | Assimp.aiProcess_LimitBoneWeights | Assimp.aiProcess_CalcTangentSpace,
				(ByteBuffer) null,
				aiPropertyStore
		                                                             );

		debugPrintScene(aiScene);

		List<RenderableObject> objects = new ArrayList<>();

		Material[] materialArray = loadMaterials(aiScene);

		for (int i = 0; i < aiScene.mNumMeshes(); i++) {
			AIMesh aiMesh = AIMesh.create(aiScene.mMeshes().get(i));

			int[] indexArray = new int[aiMesh.mNumFaces() * 3];

			float[] positionArray = new float[aiMesh.mNumVertices() * 3];
			float[] normalArray = new float[aiMesh.mNumVertices() * 3];
			float[] uvArray = new float[aiMesh.mNumVertices() * 2];

			int[] weightsCountArray = new int[aiMesh.mNumVertices()];
			float[] boneWeightsArray = new float[aiMesh.mNumVertices() * 4];
			float[] boneIndexArray = new float[aiMesh.mNumVertices() * 4];

			// mesh
			loadMesh(aiMesh, indexArray, positionArray, normalArray, uvArray);

			// skeleton
			Skeleton skeleton = parameterSkeleton;

			if (skeleton == null) {
				PointerBuffer bonesBuffer = aiMesh.mBones();
				String[] boneNames = new String[aiMesh.mNumBones()];
				for (int bone = 0; bone < boneNames.length; bone++) {
					boneNames[bone] = AIBone.create(bonesBuffer.get(bone)).mName().dataString();
				}

				if (boneNames.length > 0) {
					AINode skeletonRootBone = findSkeletonRootBone(aiScene, boneNames);
					skeleton = loadSkeleton(skeletonRootBone);
				}
			}

			// weights
			if (skeleton != null) {
				loadWeights(aiMesh, skeleton, weightsCountArray, boneWeightsArray, boneIndexArray);
			}

			// create the mesh
			Mesh mesh = new Mesh(
					indexArray.length, positionArray.length / 3,
					indexArray, positionArray, uvArray, normalArray
			);

			if (skeleton != null) {
				mesh.setBoneIndexArray(boneIndexArray);
				mesh.setBoneWeightArray(boneWeightsArray);
			}

			objects.add(new RenderableObject(
					mesh, materialArray[aiMesh.mMaterialIndex()], skeleton
			));
		}

		Assimp.aiReleaseImport(aiScene);

		return objects;
	}

	private static void debugPrintNode(AINode aiNode, int depth) {
		StringBuilder sb = new StringBuilder();

		for (int i = 0; i < depth; i++) {
			sb.append('\t');
		}

		sb.append(aiNode.mName().dataString());
		Noise.getLogger().log(Logger.DEBUG, sb.toString());

		if (aiNode.mNumChildren() > 0) {
			PointerBuffer childrenBuffer = aiNode.mChildren();

			for (int child = 0; child < aiNode.mNumChildren(); child++) {
				debugPrintNode(AINode.create(childrenBuffer.get(child)), depth + 1);
			}
		}
	}

	private static void debugPrintScene(AIScene aiScene) {
		debugPrintNode(aiScene.mRootNode(), 0);
	}

	static {
		aiPropertyStore = Assimp.aiCreatePropertyStore();
		Assimp.aiSetImportPropertyInteger(aiPropertyStore, Assimp.AI_CONFIG_IMPORT_COLLADA_USE_COLLADA_NAMES, 1);
	}

}
