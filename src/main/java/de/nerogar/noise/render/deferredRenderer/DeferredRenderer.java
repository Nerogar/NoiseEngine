package de.nerogar.noise.render.deferredRenderer;

import de.nerogar.noise.Noise;
import de.nerogar.noise.render.*;
import de.nerogar.noise.util.*;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static org.lwjgl.opengl.GL11.*;

/**
 * A renderer implementing a deferred rendering pipeline.
 * <ul>
 * <li>Lights can be managed with a {@link LightContainer LightContainer}.</li>
 * <li>Effects can be managed with an {@link EffectContainer EffectContainer}.</li>
 * </ul>
 */

public class DeferredRenderer {

	private static final int TEXTURE_ATTACHMENT_GBUFFER_COLOR  = 0;
	private static final int TEXTURE_ATTACHMENT_GBUFFER_NORMAL = 1;
	private static final int TEXTURE_ATTACHMENT_GBUFFER_LIGHT  = 2;
	private static final int TEXTURE_ATTACHMENT_GBUFFER_DEPTH  = -1;
	private static final int TEXTURE_ATTACHMENT_LIGHTS         = 0;
	private static final int TEXTURE_ATTACHMENT_EFFECTS        = 0;
	private static final int TEXTURE_ATTACHMENT_FINAL          = 0;
	private static final int TEXTURE_ATTACHMENT_FILTER         = 0;


	private static final int TEXTURE_SLOT_COLOR           = 0;
	private static final int TEXTURE_SLOT_NORMAL          = 1;
	private static final int TEXTURE_SLOT_LIGHT           = 2;
	private static final int TEXTURE_SLOT_LIGHTS          = 3;
	private static final int TEXTURE_SLOT_EFFECTS         = 4;
	private static final int TEXTURE_SLOT_REFLECTION_CUBE = 5;
	private static final int TEXTURE_SLOT_DEPTH           = 6;

	private static final int TEXTURE_SLOT_FINAL = 0;

	private Map<DeferredContainer, VboContainer> vboMap;
	private VertexBufferObjectIndexed            fullscreenQuad;
	private DeferredRendererProfiler             profiler;

	// gBuffer
	private Shader            gBufferShader;
	private Shader            gBufferShaderSingle;
	private FrameBufferObject gBuffer;

	// lights
	private LightContainer              lightContainer;
	private VertexBufferObjectInstanced lightVbo;
	private Shader                      lightShader;
	private FrameBufferObject           lightFrameBuffer;

	// effects
	private EffectContainer   effectContainer;
	private FrameBufferObject effectFrameBuffer;

	// final pass
	private Shader            finalShader;
	private FrameBufferObject finalFrameBuffer;

	// getFiltered
	private Shader            filterShader;
	private FrameBufferObject filterFrameBuffer;

	// settings
	private Map<String, String> settingsParameter;

	private int width;
	private int height;

	private TextureCubeMap reflectionTexture;
	private Color          sunLightColor;
	private Vector3f       sunLightDirection;
	private float          sunLightBrightness;

	private float minAmbientBrightness;

	private boolean ambientOcclusionEnabled;
	private float   ambientOcclusionSize;
	private float   ambientOcclusionStrength;

	private boolean antiAliasingEnabled;

	// debug
	private static final boolean SHOW_AXIS = Noise.getSettings().getObject("deferredRenderer").getBoolean("showAxis");
	private DeferredRenderable originAxis;

	/**
	 * @param width  initial width of the target {@link FrameBufferObject FrameBufferObject}
	 * @param height initial height of the target {@link FrameBufferObject FrameBufferObject}
	 */
	public DeferredRenderer(int width, int height) {
		this.width = width;
		this.height = height;

		profiler = new DeferredRendererProfiler();
		Noise.getDebugWindow().addProfiler(profiler);
		vboMap = new HashMap<>();
		fullscreenQuad = new VertexBufferObjectIndexed(
				new int[] { 2, 2 },
				6,
				4,
				new int[] { 0, 1, 2, 2, 3, 0 },
				new float[] { 0.0f, 0.0f,/**/0.0f, 1.0f, /**/1.0f, 1.0f,/**/1.0f, 0.0f },
				new float[] { 0.0f, 0.0f,/**/0.0f, 1.0f, /**/1.0f, 1.0f,/**/1.0f, 0.0f }
		);

		sunLightColor = new Color(1.0f, 1.0f, 0.9f, 0.0f);
		sunLightDirection = new Vector3f(-1.0f);
		sunLightBrightness = 1.5f;
		minAmbientBrightness = 0.3f;

		ambientOcclusionEnabled = true;
		ambientOcclusionSize = 0.3f;
		ambientOcclusionStrength = 2.0f;

		antiAliasingEnabled = true;

		lightContainer = new LightContainer();
		effectContainer = new EffectContainer();
		Mesh sphere = WavefrontLoader.loadObject("<icoSphere.obj>");
		lightVbo = new VertexBufferObjectInstanced(new int[] { 3 }, sphere.getIndexCount(), sphere.getVertexCount(), sphere.getIndexArray(), sphere.getPositionArray());

		gBuffer = new FrameBufferObject(width, height, true,
		                                Texture2D.DataType.BGRA_8_8_8_8I, // color
		                                Texture2D.DataType.BGRA_10_10_10_2, // normal
		                                Texture2D.DataType.BGRA_8_8_8_8I // light
		);

		lightFrameBuffer = new FrameBufferObject(width, height, false, Texture2D.DataType.BGRA_16_16_16F);

		effectFrameBuffer = new FrameBufferObject(width, height, false, Texture2D.DataType.BGRA_8_8_8_8I);
		effectFrameBuffer.attachTexture(-1, gBuffer.getTextureAttachment(-1));

		finalFrameBuffer = new FrameBufferObject(width, height, false, Texture2D.DataType.BGRA_8_8_8_8I);

		filterFrameBuffer = new FrameBufferObject(width, height, false, Texture2D.DataType.BGRA_8_8_8_8I);

		loadShaders();
		setFrameBufferResolution(width, height);

		//debug features

		if (SHOW_AXIS) {
			loadOriginAxis();
			addObject(originAxis);
		}
	}

	private void loadOriginAxis() {
		DeferredContainer axisContainer = new DeferredContainer(
				WavefrontLoader.loadObject("<deferredRenderer/originAxis.obj>"),
				null,
				Texture2DLoader.loadTexture("<deferredRenderer/originAxis/color.png>"),
				Texture2DLoader.loadTexture("<deferredRenderer/originAxis/normal.png>"),
				Texture2DLoader.loadTexture("<deferredRenderer/originAxis/light.png>")
		);

		originAxis = new DeferredRenderable(axisContainer, new RenderProperties3f());
	}

	/**
	 * @param object a {@link DeferredRenderable DeferredRenderable} to add
	 */
	public void addObject(DeferredRenderable object) {
		VboContainer container = vboMap.get(object.getContainer());
		if (container == null) {
			if (object.getContainer().getOptimization() == DeferredContainer.OptimizationStrategy.OPTIMIZATION_ONE) {
				container = new VboContainerOne(profiler, object.getContainer(), gBufferShaderSingle);
			} else if (object.getContainer().getOptimization() == DeferredContainer.OptimizationStrategy.OPTIMIZATION_FEW) {
				container = new VboContainerFew(profiler, object.getContainer(), gBufferShader);
			} else if (object.getContainer().getOptimization() == DeferredContainer.OptimizationStrategy.OPTIMIZATION_MANY) {
				container = new VboContainerMany(profiler, object.getContainer(), gBufferShader);
			} else {
				container = new VboContainerMany(profiler, object.getContainer(), gBufferShader);
			}

			if (object.getContainer().isDeleteMesh()) {
				object.getContainer().clearMesh();
			}

			vboMap.put(object.getContainer(), container);
		}

		object.update();
		object.addListener(container.getRenderableListener());

		container.addObject(object);

		profiler.incrementValue(DeferredRendererProfiler.OBJECT_COUNT);
	}

	/**
	 * @param objects a collection of {@link DeferredRenderable DeferredRenderables} to add
	 */
	public void addAllObjects(Collection<DeferredRenderable> objects) {
		for (DeferredRenderable object : objects) {
			addObject(object);
		}
	}

	/**
	 * @param object a {@link DeferredRenderable DeferredRenderable} to remove
	 */
	public void removeObject(DeferredRenderable object) {
		VboContainer container = vboMap.get(object.getContainer());
		if (container == null) return;
		container.removeObject(object);
		object.removeListener(container.getRenderableListener());

		if (container.isEmpty()) {
			container.cleanup();
			vboMap.remove(object.getContainer());
		}

		profiler.decrementValue(DeferredRendererProfiler.OBJECT_COUNT);
	}

	/**
	 * @param objects a collection of {@link DeferredRenderable DeferredRenderables} to remove
	 */
	public void removeAllObjects(Collection<DeferredRenderable> objects) {
		for (DeferredRenderable object : objects) {
			removeObject(object);
		}
	}

	/**
	 * removes all {@link DeferredRenderable DeferredRenderables} from this renderer
	 */
	public void clear() {
		for (VboContainer container : vboMap.values()) {
			container.cleanup();
		}

		vboMap.clear();

		profiler.setValue(DeferredRendererProfiler.OBJECT_COUNT, 0);

		if (SHOW_AXIS) {
			addObject(originAxis);
		}
	}

	/**
	 * the light container is used to manage lights
	 *
	 * @return the light container
	 */
	public LightContainer getLightContainer() {
		return lightContainer;
	}

	/**
	 * the effect container is used to manage effects
	 *
	 * @return the effect container
	 */
	public EffectContainer getEffectContainer() {
		return effectContainer;
	}

	private static final int[] lightInstanceComponents = new int[] { 3, 3, 1, 1 };

	private int rebuildLightVbo(Camera camera) {
		float[] position = new float[lightContainer.size() * 3];
		float[] color = new float[lightContainer.size() * 3];
		float[] reach = new float[lightContainer.size()];
		float[] intensity = new float[lightContainer.size()];

		int i = 0;
		for (Light light : lightContainer) {
			if (camera.getViewRegion().getPointDistance(light.position) < light.reach) {
				position[i * 3 + 0] = light.position.getX();
				position[i * 3 + 1] = light.position.getY();
				position[i * 3 + 2] = light.position.getZ();

				color[i * 3 + 0] = light.color.getR();
				color[i * 3 + 1] = light.color.getG();
				color[i * 3 + 2] = light.color.getB();

				reach[i] = light.reach;

				intensity[i] = light.intensity;

				i++;
			}
		}

		lightVbo.setInstanceData(i, lightInstanceComponents, position, color, reach, intensity);

		return i;
	}

	private void loadShaders() {
		if (settingsParameter == null) settingsParameter = new HashMap<>();
		settingsParameter.clear();
		settingsParameter.put("AO_ENABLED", "#define AO_ENABLED " + (ambientOcclusionEnabled ? 1 : 0));

		if (gBufferShader != null) gBufferShader.cleanup();
		if (gBufferShaderSingle != null) gBufferShaderSingle.cleanup();
		if (lightShader != null) lightShader.cleanup();
		if (finalShader != null) finalShader.cleanup();
		if (filterShader != null) filterShader.cleanup();

		Map<String, String> gBufferShaderParameters = new HashMap<>();
		gBufferShaderParameters.put("useUniforms", "#define UNIFORM_MATRICES 0");
		gBufferShader = ShaderLoader.loadShader("<deferredRenderer/gBuffer.vert>", "<deferredRenderer/gBuffer.frag>", gBufferShaderParameters);
		gBufferShader.activate();
		gBufferShader.setUniform1i("textureColor_N", TEXTURE_SLOT_COLOR);
		gBufferShader.setUniform1i("textureNormal_N", TEXTURE_SLOT_NORMAL);
		gBufferShader.setUniform1i("textureLight_N", TEXTURE_SLOT_LIGHT);
		gBufferShader.deactivate();

		gBufferShaderParameters.put("useUniforms", "#define UNIFORM_MATRICES 1");
		gBufferShaderSingle = ShaderLoader.loadShader("<deferredRenderer/gBuffer.vert>", "<deferredRenderer/gBuffer.frag>", gBufferShaderParameters);
		gBufferShaderSingle.activate();
		gBufferShaderSingle.setUniform1i("textureColor_N", TEXTURE_SLOT_COLOR);
		gBufferShaderSingle.setUniform1i("textureNormal_N", TEXTURE_SLOT_NORMAL);
		gBufferShaderSingle.setUniform1i("textureLight_N", TEXTURE_SLOT_LIGHT);
		gBufferShaderSingle.deactivate();

		lightShader = ShaderLoader.loadShader("<deferredRenderer/lights.vert>", "<deferredRenderer/lights.frag>");
		lightShader.activate();
		lightShader.setUniform1i("textureNormal", TEXTURE_SLOT_NORMAL);
		lightShader.setUniform1i("textureDepth", TEXTURE_SLOT_DEPTH);
		lightShader.setUniform2f("inverseResolution", 1.0f / width, 1.0f / height);
		lightShader.deactivate();

		finalShader = ShaderLoader.loadShader("<deferredRenderer/final.vert>", "<deferredRenderer/final.frag>", settingsParameter);
		finalShader.activate();
		finalShader.setUniform1i("textureColor", TEXTURE_SLOT_COLOR);
		finalShader.setUniform1i("textureNormal", TEXTURE_SLOT_NORMAL);
		finalShader.setUniform1i("textureLight", TEXTURE_SLOT_LIGHT);
		finalShader.setUniform1i("textureLights", TEXTURE_SLOT_LIGHTS);
		finalShader.setUniform1i("textureEffects", TEXTURE_SLOT_EFFECTS);
		finalShader.setUniform1i("textureReflection", TEXTURE_SLOT_REFLECTION_CUBE);
		finalShader.setUniform1i("textureDepth", TEXTURE_SLOT_DEPTH);
		finalShader.setUniformMat4f("projectionMatrix", Matrix4fUtils.getOrthographicProjection(0.0f, 1.0f, 1.0f, 0.0f, 1.0f, 0.0f).asBuffer());
		finalShader.setUniform2f("inverseResolution", 1.0f / width, 1.0f / height);
		finalShader.deactivate();

		filterShader = ShaderLoader.loadShader("<deferredRenderer/filter.vert>", "<deferredRenderer/filter.frag>");
		filterShader.activate();
		filterShader.setUniform1i("textureColor", TEXTURE_SLOT_COLOR);
		filterShader.setUniformMat4f("projectionMatrix", Matrix4fUtils.getOrthographicProjection(0.0f, 1.0f, 1.0f, 0.0f, 1.0f, 0.0f).asBuffer());
		filterShader.setUniform2f("inverseResolution", 1.0f / width, 1.0f / height);
		filterShader.deactivate();
	}

	/**
	 * resizes the {@link FrameBufferObject FrameBufferObjects} to match the new resolution
	 *
	 * @param width  the new width
	 * @param height the new height
	 */
	public void setFrameBufferResolution(int width, int height) {
		this.width = width;
		this.height = height;

		gBuffer.setResolution(width, height);
		lightFrameBuffer.setResolution(width, height);
		effectFrameBuffer.setResolution(width, height);
		finalFrameBuffer.setResolution(width, height);
		filterFrameBuffer.setResolution(width, height);

		lightShader.activate();
		lightShader.setUniform2f("inverseResolution", 1.0f / width, 1.0f / height);
		lightShader.deactivate();

		finalShader.activate();
		finalShader.setUniform2f("inverseResolution", 1.0f / width, 1.0f / height);
		finalShader.deactivate();

		filterShader.activate();
		filterShader.setUniform2f("inverseResolution", 1.0f / width, 1.0f / height);
		filterShader.deactivate();
	}

	/**
	 * a {@link TextureCubeMap TextureCubeMap} used for reflections
	 *
	 * @param reflectionTexture the reflection texture
	 */
	public void setReflectionTexture(TextureCubeMap reflectionTexture) {
		this.reflectionTexture = reflectionTexture;
	}

	/**
	 * @param sunLightColor the color of the sun light
	 */
	public void setSunLightColor(Color sunLightColor) {
		this.sunLightColor = sunLightColor;
	}

	/**
	 * @param sunLightDirection the direction of the sun light
	 */
	public void setSunLightDirection(Vector3f sunLightDirection) {
		this.sunLightDirection = sunLightDirection.normalized();
	}

	/**
	 * @param sunLightBrightness the brightness of the sun light
	 */
	public void setSunLightBrightness(float sunLightBrightness) {
		this.sunLightBrightness = sunLightBrightness;
	}

	/**
	 * the minimum brightness of objects without sunlight
	 *
	 * @param minAmbientBrightness the brightness
	 */
	public void setMinAmbientBrightness(float minAmbientBrightness) {
		this.minAmbientBrightness = minAmbientBrightness;
	}

	public void setAmbientOcclusionEnabled(boolean ambientOcclusionEnabled) {
		this.ambientOcclusionEnabled = ambientOcclusionEnabled;
		loadShaders();
	}

	public void setAmbientOcclusionSize(float ambientOcclusionSize) {
		this.ambientOcclusionSize = ambientOcclusionSize;
	}

	public void setAmbientOcclusionStrength(float ambientOcclusionStrength) {
		this.ambientOcclusionStrength = ambientOcclusionStrength;
	}

	public void setAntiAliasingEnabled(boolean antiAliasingEnabled) {
		this.antiAliasingEnabled = antiAliasingEnabled;
	}

	private void setPositionReconstructionUniforms(Shader shader, Ray unitRayCenter, Ray unitRayRight, Ray unitRayTop, Matrix4f projectionMatrix) {
		shader.setUniform3f("unitRayCenterStart", unitRayCenter.getStart().getX(), unitRayCenter.getStart().getY(), unitRayCenter.getStart().getZ());
		shader.setUniform3f("unitRayCenterDir", unitRayCenter.getDir().getX(), unitRayCenter.getDir().getY(), unitRayCenter.getDir().getZ());
		shader.setUniform3f("unitRayRightStart", unitRayRight.getStart().getX(), unitRayRight.getStart().getY(), unitRayRight.getStart().getZ());
		shader.setUniform3f("unitRayRightDir", unitRayRight.getDir().getX(), unitRayRight.getDir().getY(), unitRayRight.getDir().getZ());
		shader.setUniform3f("unitRayTopStart", unitRayTop.getStart().getX(), unitRayTop.getStart().getY(), unitRayTop.getStart().getZ());
		shader.setUniform3f("unitRayTopDir", unitRayTop.getDir().getX(), unitRayTop.getDir().getY(), unitRayTop.getDir().getZ());
		shader.setUniform4f(
				"inverseDepthFunction",
				projectionMatrix.get(2, 2),
				projectionMatrix.get(2, 3),
				projectionMatrix.get(3, 2),
				projectionMatrix.get(3, 3)
		                        );
	}

	/**
	 * Renders everything onto internal {@link FrameBufferObject FrameBufferObjects}.<br>
	 * Access them with
	 * <ul>
	 * <li>{@link DeferredRenderer#getColorOutput() getColorOutput()} for the final output.</li>
	 * <li> getXXXBuffer() for the different buffers.</li>
	 * </ul>
	 *
	 * @param camera the camera for viewing the scene
	 */
	public void render(Camera camera) {
		// render gBuffer
		gBuffer.bind();
		glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

		glEnable(GL_DEPTH_TEST);
		glDepthFunc(GL_LEQUAL);

		glEnable(GL_CULL_FACE);
		glCullFace(GL_BACK);

		gBufferShader.activate();
		gBufferShader.setUniformMat4f("viewMatrix_N", camera.getViewMatrix().asBuffer());
		gBufferShader.setUniformMat4f("projectionMatrix_N", camera.getProjectionMatrix().asBuffer());
		gBufferShader.deactivate();

		gBufferShaderSingle.activate();
		gBufferShaderSingle.setUniformMat4f("viewMatrix_N", camera.getViewMatrix().asBuffer());
		gBufferShaderSingle.setUniformMat4f("projectionMatrix_N", camera.getProjectionMatrix().asBuffer());
		gBufferShaderSingle.deactivate();

		for (VboContainer container : vboMap.values()) {
			boolean shouldRender = container.prepareRender(camera.getViewRegion());
			if (!shouldRender) continue;
			container.render(camera);
		}

		glDisable(GL_DEPTH_TEST);

		// bind gBuffer textures
		gBuffer.getTextureAttachment(TEXTURE_ATTACHMENT_GBUFFER_COLOR).bind(TEXTURE_SLOT_COLOR);
		gBuffer.getTextureAttachment(TEXTURE_ATTACHMENT_GBUFFER_NORMAL).bind(TEXTURE_SLOT_NORMAL);
		gBuffer.getTextureAttachment(TEXTURE_ATTACHMENT_GBUFFER_LIGHT).bind(TEXTURE_SLOT_LIGHT);
		gBuffer.getTextureAttachment(TEXTURE_ATTACHMENT_GBUFFER_DEPTH).bind(TEXTURE_SLOT_DEPTH);

		// calculate unit rays for depth -> position reconstruction
		Ray unitRayCenter = camera.unproject(0, 0);
		Ray unitRayRight = camera.unproject(1, 0);
		Ray unitRayTop = camera.unproject(0, 1);
		unitRayRight.getStart().subtract(unitRayCenter.getStart());
		unitRayRight.getDir().subtract(unitRayCenter.getDir());
		unitRayTop.getStart().subtract(unitRayCenter.getStart());
		unitRayTop.getDir().subtract(unitRayCenter.getDir());

		// render lights
		lightFrameBuffer.bind();
		glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

		glEnable(GL_BLEND);

		int lightCount = rebuildLightVbo(camera);

		if (lightCount > 0) {
			glCullFace(GL_FRONT);

			glBlendFunc(GL_ONE, GL_ONE);

			lightShader.activate();
			lightShader.setUniformMat4f("viewMatrix", camera.getViewMatrix().asBuffer());
			lightShader.setUniformMat4f("projectionMatrix", camera.getProjectionMatrix().asBuffer());
			setPositionReconstructionUniforms(lightShader, unitRayCenter, unitRayRight, unitRayTop, camera.getProjectionMatrix());

			lightVbo.render();
			lightShader.deactivate();

			profiler.addValue(DeferredRendererProfiler.LIGHT_RENDER_COUNT, lightCount);
		}

		glDisable(GL_CULL_FACE);

		lightFrameBuffer.getTextureAttachment(TEXTURE_ATTACHMENT_LIGHTS).bind(TEXTURE_SLOT_LIGHTS);
		if (reflectionTexture != null) reflectionTexture.bind(TEXTURE_SLOT_REFLECTION_CUBE);

		// render effects
		effectFrameBuffer.bind();
		glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
		glClear(GL_COLOR_BUFFER_BIT);

		glBlendFunc(GL_ONE, GL_ONE);

		glEnable(GL_DEPTH_TEST);
		glDepthFunc(GL_LEQUAL);
		glDepthMask(false);

		Vector3f point = new Vector3f();
		for (Effect effect : effectContainer) {
			if (!effect.getRenderProperties().isVisible()) continue;

			point.setX(effect.getRenderProperties().getX());
			point.setY(effect.getRenderProperties().getY());
			point.setZ(effect.getRenderProperties().getZ());

			if (camera.getViewRegion().getPointDistance(point) < effect.getBoundingRadius() * effect.getRenderProperties().getMaxScaleComponent()) {
				effect.render(camera.getViewMatrix(), camera.getProjectionMatrix());
				profiler.incrementValue(DeferredRendererProfiler.EFFECT_RENDER_COUNT);
			}
		}

		glDepthMask(true);
		glDisable(GL_DEPTH_TEST);
		glDisable(GL_BLEND);

		effectFrameBuffer.getTextureAttachment(TEXTURE_ATTACHMENT_EFFECTS).bind(TEXTURE_SLOT_EFFECTS);

		// final pass
		finalFrameBuffer.bind();

		finalShader.activate();
		finalShader.setUniform3f("sunLightColor", sunLightColor.getR(), sunLightColor.getG(), sunLightColor.getB());
		finalShader.setUniform3f("sunLightDirection", sunLightDirection.getX(), sunLightDirection.getY(), sunLightDirection.getZ());
		finalShader.setUniform1f("sunLightBrightness", sunLightBrightness);
		finalShader.setUniform1f("minAmbientBrightness", minAmbientBrightness);
		finalShader.setUniform1f("ssUnitSize", camera.getUnitSize() * height);
		finalShader.setUniform1f("aoSize", ambientOcclusionSize);
		finalShader.setUniform1f("aoStrength", ambientOcclusionStrength);

		setPositionReconstructionUniforms(finalShader, unitRayCenter, unitRayRight, unitRayTop, camera.getProjectionMatrix());

		fullscreenQuad.render();
		finalShader.deactivate();

		// getFiltered
		if (antiAliasingEnabled) {
			finalFrameBuffer.getTextureAttachment(TEXTURE_ATTACHMENT_FINAL).bind(TEXTURE_SLOT_FINAL);
			filterFrameBuffer.bind();
			filterShader.activate();
			fullscreenQuad.render();
			filterShader.deactivate();
		}

		profiler.addValue(DeferredRendererProfiler.LIGHT_COUNT, lightContainer.size());

		profiler.addValue(DeferredRendererProfiler.EFFECT_COUNT, effectContainer.size());

		profiler.reset();
	}

	/**
	 * @return the final color output as a {@link Texture2D Texture2D}
	 */
	public Texture2D getColorOutput() {
		if (antiAliasingEnabled) {
			return filterFrameBuffer.getTextureAttachment(TEXTURE_ATTACHMENT_FINAL);
		} else {
			return finalFrameBuffer.getTextureAttachment(TEXTURE_ATTACHMENT_FILTER);
		}
	}

	/**
	 * @return the depth buffer as a {@link Texture2D Texture2D}
	 */
	public Texture2D getDepthBuffer() {
		return gBuffer.getTextureAttachment(TEXTURE_ATTACHMENT_GBUFFER_DEPTH);
	}

	/**
	 * @return the color buffer as a {@link Texture2D Texture2D}
	 */
	public Texture2D getColorBuffer() {
		return gBuffer.getTextureAttachment(TEXTURE_ATTACHMENT_GBUFFER_COLOR);
	}

	/**
	 * @return the normal buffer as a {@link Texture2D Texture2D}
	 */
	public Texture2D getNormalBuffer() {
		return gBuffer.getTextureAttachment(TEXTURE_ATTACHMENT_GBUFFER_NORMAL);
	}

	/**
	 * the light buffer contains light information
	 *
	 * @return the light buffer as a {@link Texture2D Texture2D}
	 */
	public Texture2D getLightBuffer() {
		return gBuffer.getTextureAttachment(TEXTURE_ATTACHMENT_GBUFFER_LIGHT);
	}

	/**
	 * the lights buffer contains lights from the {@link LightContainer LightContainer} returned by {@link DeferredRenderer#getLightContainer() getLightContainer()}
	 *
	 * @return the light buffer as a {@link Texture2D Texture2D}
	 */
	public Texture2D getLightsBuffer() {
		return lightFrameBuffer.getTextureAttachment(TEXTURE_ATTACHMENT_LIGHTS);
	}

	/**
	 * the effects buffer contains effects from the {@link EffectContainer EffectContainer} returned by {@link DeferredRenderer#getEffectContainer() getEffectContainer()}
	 *
	 * @return the depth buffer as a {@link Texture2D Texture2D}
	 */
	public Texture2D getEffectsBuffer() {
		return effectFrameBuffer.getTextureAttachment(TEXTURE_ATTACHMENT_EFFECTS);
	}

	/**
	 * Cleans all internal opengl resources. This renderer is unusable after this call.
	 */
	public void cleanup() {
		for (VboContainer container : vboMap.values()) {
			container.cleanup();
		}

		gBuffer.cleanup();
		lightFrameBuffer.cleanup();
		finalFrameBuffer.cleanup();
		filterFrameBuffer.cleanup();

		gBufferShader.cleanup();
		gBufferShaderSingle.cleanup();
		lightShader.cleanup();
		finalShader.cleanup();
		filterShader.cleanup();

		fullscreenQuad.cleanup();
		lightVbo.cleanup();

		Noise.getDebugWindow().removeProfiler(profiler);
	}

}
