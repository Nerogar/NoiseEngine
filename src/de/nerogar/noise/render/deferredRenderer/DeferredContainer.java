package de.nerogar.noise.render.deferredRenderer;

import java.util.HashMap;
import java.util.Map;

import de.nerogar.noise.render.*;
import de.nerogar.noise.util.Logger;

/**
 * A container for the {@link DeferredRenderer DeferredRenderer} storing the following information:
 * 
 * <ul>
 * <li>a {@link Mesh Mesh} that defines the shape of the object
 * <li>a colorTexture that defines the color
 * <li>a normalTexture that defines the normals
 * <li>a lightTexture that defines the light information
 * <li>a {@link Shader Shader} that defines the surface shader
 * </ul>
 * 
 * the lightTexture should be in the following format:
 * <ul>
 * <li> red: ambient light
 * <li> green: reflection factor
 * <li> blue: specular factor
 * <li> alpha: specular power in logarithmic scale (0, 1)->(1, 128) or (power = 2 ^ (alpha * 7))
 * </ul>
 * 
 * the shader parameter has to be a surface shader created by {@link DeferredContainer#createSurfaceShader(String, String) DeferredContainer.createSurfaceShader}
 */
public class DeferredContainer {

	private static final Map<String, String> surfaceShaderParameters = new HashMap<String, String>();

	private static final int MAX_RESERVED_TEXTURE_SLOT = 3;

	private Mesh mesh;

	private boolean[] texturesUsed;
	private Texture[] textures;

	private Shader shader;

	/**
	 * all parameters are defined {@link DeferredContainer here}
	 * 
	 * @param mesh the mesh defining the shape of the object
	 * @param shader the surface shader or null if no surface shader should be applied
	 * @param colorTexture the texture defining the color
	 * @param normalTexture the texture defining the normals
	 * @param lightTexture the texture defining the light information
	 */
	public DeferredContainer(Mesh mesh, Shader shader, Texture2D colorTexture, Texture2D normalTexture, Texture2D lightTexture) {
		this.mesh = mesh;
		textures = new Texture2D[Texture2D.MAX_TEXTURE_COUNT];
		texturesUsed = new boolean[Texture2D.MAX_TEXTURE_COUNT];

		setColorTexture(colorTexture);
		setNormalTexture(normalTexture);
		setLightTexture(lightTexture);

		this.shader = shader;
	}

	public Mesh getMesh() {
		return mesh;
	}

	private void setInternalTexture(Texture texture, int slot) {
		if (slot <= MAX_RESERVED_TEXTURE_SLOT) {
			textures[slot] = texture;
			texturesUsed[slot] = true;
		}
	}

	public void setTexture(Texture2D texture, int slot) {
		if (slot > MAX_RESERVED_TEXTURE_SLOT) {
			textures[slot] = texture;
			texturesUsed[slot] = true;
		} else {
			Logger.log(Logger.ERROR, "Texture slots 0 to " + MAX_RESERVED_TEXTURE_SLOT + " are reserved for internal purposes in the deferred renderer");
		}
	}

	public void bindTextures() {
		for (int i = 0; i < textures.length; i++) {
			if (texturesUsed[i]) {
				textures[i].bind(i);
			}
		}
	}

	public Texture2D getColorTexture() {
		return (Texture2D) textures[0];
	}

	public void setColorTexture(Texture2D colorTexture) {
		setInternalTexture(colorTexture, 0);
	}

	public Texture2D getNormalTexture() {
		return (Texture2D) textures[1];
	}

	public void setNormalTexture(Texture2D normalTexture) {
		setInternalTexture(normalTexture, 1);
	}

	public Texture2D getLightTexture() {
		return (Texture2D) textures[2];
	}

	public void setLightTexture(Texture2D lightTexture) {
		setInternalTexture(lightTexture, 2);
	}

	public Shader getSurfaceShader() {
		return shader;
	}

	public void setSurfaceShader(Shader shader) {
		this.shader = shader;
	}

	/**
	 * Creates a new surface shader.<br>
	 * The shader consists of a vertex and a fragment shader.<br>
	 * All file paths are {@link ShaderLoader file IDs}.
	 * 
	 * <p>
	 * The entry points are:
	 * <ul>
	 * <li>For the vertex Shader:<br>
	 * {@code void mainSurface(inout vec2 uv, inout vec4 position, inout vec3 normal)}
	 * <p>
	 * <li>For the fragment Shader:<br>
	 * {@code void mainSurface(inout vec4 color, in vec2 uv, inout vec4 position, inout vec3 normal, inout vec4 light)}
	 * </ul>
	 * <p>
	 * where the light parameter is defined as in {@link DeferredContainer}
	 * 
	 * @param vertexFile path to the vertex shader file
	 * @param fragmentFile path to the fragment shader file
	 * @return the surface shader
	 */
	public static Shader createSurfaceShader(String vertexFile, String fragmentFile) {
		surfaceShaderParameters.clear();
		surfaceShaderParameters.put("surfaceShaderFragment", "(" + fragmentFile + ")");
		surfaceShaderParameters.put("surfaceShaderVertex", "(" + vertexFile + ")");

		Shader shader = ShaderLoader.loadShader("<deferredRenderer/gBufferSurface.vert>", "<deferredRenderer/gBufferSurface.frag>", surfaceShaderParameters);
		shader.activate();
		shader.setUniform1i("textureColor_N", 0);
		shader.setUniform1i("textureNormal_N", 1);
		shader.setUniform1i("textureLight_N", 2);
		shader.deactivate();

		return shader;
	}
}
