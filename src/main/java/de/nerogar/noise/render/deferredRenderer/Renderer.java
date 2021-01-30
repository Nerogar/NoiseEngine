package de.nerogar.noise.render.deferredRenderer;

import de.nerogar.noise.render.*;
import de.nerogar.noise.render.camera.IReadOnlyCamera;
import de.nerogar.noiseInterface.render.deferredRenderer.ILight;
import de.nerogar.noiseInterface.render.deferredRenderer.IRenderContext;
import de.nerogar.noiseInterface.render.deferredRenderer.IRenderable;
import de.nerogar.noiseInterface.render.deferredRenderer.IRenderer;
import org.lwjgl.opengl.GL11;

import java.util.*;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.GL_CLAMP_TO_BORDER;
import static org.lwjgl.opengl.GL14.GL_CLAMP_TO_EDGE;

/**
 * gBuffer textures:
 * 1. albedo:
 * rgb: (8 bit) base color
 * a: emission power
 * 2. normal:
 * rgb: (10 bit) -> xyz in world space
 * a: (2 bit) shadeless factor
 * 3. material: rgba (8 bit)
 * r: ambient occlusion
 * g: specular factor
 * b: specular exponent in logarithmic scale (0, 1)->(1, 128) or (exponent = 2 ^ (blue * 7))
 * a:
 */
public class Renderer implements IRenderer {

	private static final int GBUFFER_DEPTH_SLOT    = -1;
	private static final int GBUFFER_ALBEDO_SLOT   = 0;
	private static final int GBUFFER_NORMAL_SLOT   = 1;
	private static final int GBUFFER_MATERIAL_SLOT = 2;
	private static final int LIGHT_LIGHTS_SLOT     = 0;

	private static final int EMISSION_LEVEL_COUNT = 6;

	private static Shader             gBufferCombineShader;
	private static Shader             downSampleEmission1Shader;
	private static Shader             downSampleEmissionNShader;
	private static Shader             blurShader;
	private static VertexBufferObject fullscreenQuad;

	private final List<IRenderable>     renderables;
	private final FrameBufferObject     gBuffer;
	private final FrameBufferObject     lightBuffer;
	private final FrameBufferObject[][] downSampleEmissionBuffers;

	private final List<ILight>                  tempLightList;
	private final List<Class<? extends ILight>> tempProcessedLightsClasses;
	private final List<List<ILight>>            tempProcessedLights;

	public Renderer(int width, int height) {
		this.renderables = new ArrayList<>();

		gBuffer = new FrameBufferObject(width, height, true);
		gBuffer.attachTexture(0, new Texture2D("albedo", width, height, null, Texture2D.InterpolationType.LINEAR, Texture2D.DataType.BGRA_8_8_8_8I));
		gBuffer.getTextureAttachment(0).setWrapMode(GL_CLAMP_TO_EDGE);
		gBuffer.attachTexture(1, new Texture2D("normal", width, height, null, Texture2D.InterpolationType.NEAREST, Texture2D.DataType.BGRA_10_10_10_2));
		gBuffer.getTextureAttachment(1).setWrapMode(GL_CLAMP_TO_EDGE);
		gBuffer.attachTexture(2, new Texture2D("material", width, height, null, Texture2D.InterpolationType.NEAREST, Texture2D.DataType.BGRA_8_8_8_8I));
		gBuffer.getTextureAttachment(2).setWrapMode(GL_CLAMP_TO_EDGE);

		lightBuffer = new FrameBufferObject(width, height, false,
		                                    Texture2D.DataType.BGRA_16_16_16F
		);

		downSampleEmissionBuffers = new FrameBufferObject[2][EMISSION_LEVEL_COUNT];
		for (int i = 0; i < downSampleEmissionBuffers.length; i++) {
			for (int j = 0; j < downSampleEmissionBuffers[i].length; j++) {
				downSampleEmissionBuffers[i][j] = new FrameBufferObject(
						(int) Math.ceil((float) width / (2 << j)),
						(int) Math.ceil((float) height / (2 << j)),
						false
				);
				downSampleEmissionBuffers[i][j].attachTexture(0, new Texture2D(
						"albedo",
						downSampleEmissionBuffers[i][j].getWidth(), downSampleEmissionBuffers[i][j].getHeight(),
						null, Texture2D.InterpolationType.LINEAR, Texture2D.DataType.BGRA_16_16_16F
				));
				downSampleEmissionBuffers[i][j].getTextureAttachment(0).setWrapMode(GL_CLAMP_TO_BORDER);
			}
		}

		tempLightList = new ArrayList<>();
		tempProcessedLightsClasses = new ArrayList<>();
		tempProcessedLights = new ArrayList<>();
	}

	@Override
	public void addObject(IRenderable renderable) {
		renderables.add(renderable);
	}

	@Override
	public void setResolution(int width, int height) {
		gBuffer.setResolution(width, height);
		lightBuffer.setResolution(width, height);

		for (int i = 0; i < downSampleEmissionBuffers.length; i++) {
			for (int j = 0; j < downSampleEmissionBuffers[i].length; j++) {
				downSampleEmissionBuffers[i][j].setResolution(
						(int) Math.ceil((float) width / (2 << j)),
						(int) Math.ceil((float) height / (2 << j))
				                                             );
			}
		}
	}

	private void renderLights(IRenderContext renderContext) {
		tempLightList.clear();
		for (List<ILight> tempProcessedLight : tempProcessedLights) {
			tempProcessedLight.clear();
		}

		// collect all lights
		for (IRenderable renderable : renderables) {
			renderable.getLights(tempLightList);
		}

		// build lists of lights sorted by their class
		for (ILight light : tempLightList) {
			boolean added = false;
			for (int i = 0; i < tempProcessedLightsClasses.size(); i++) {
				if (tempProcessedLightsClasses.get(i) == light.getClass()) {
					tempProcessedLights.get(i).add(light);
					added = true;
					break;
				}
			}

			if (!added) {
				tempProcessedLightsClasses.add(light.getClass());
				ArrayList<ILight> lights = new ArrayList<>();
				lights.add(light);
				tempProcessedLights.add(lights);
			}
		}

		// render the lights
		for (int i = 0; i < tempProcessedLightsClasses.size(); i++) {
			if (!tempProcessedLights.get(i).isEmpty()) {
				tempProcessedLights.get(i).get(0).renderBatch(renderContext, tempProcessedLights.get(i));
			}
		}
	}

	private void renderBloom(IRenderTarget renderTarget) {
		// first down sample step
		downSampleEmissionBuffers[0][0].bind();
		gBuffer.getTextureAttachment(GBUFFER_ALBEDO_SLOT).bind(0);
		downSampleEmission1Shader.activate();
		downSampleEmission1Shader.setUniform1i("u_albedoBuffer", 0);
		downSampleEmission1Shader.setUniform2f("u_inverseSourceResolution", 1f / gBuffer.getWidth(), 1f / gBuffer.getHeight());
		downSampleEmission1Shader.setUniform2f(
				"u_padSourceTexture",
				downSampleEmissionBuffers[0][0].getWidth() * 2 > gBuffer.getWidth() ? 1 : 0,
				downSampleEmissionBuffers[0][0].getHeight() * 2 > gBuffer.getHeight() ? 1 : 0
		                                      );
		fullscreenQuad.render();
		downSampleEmission1Shader.deactivate();

		// remaining down sample steps
		downSampleEmissionNShader.activate();
		downSampleEmissionNShader.setUniform1i("u_emissionBuffer", 0);
		for (int i = 1; i < downSampleEmissionBuffers[0].length; i++) {
			downSampleEmissionBuffers[0][i].bind();
			downSampleEmissionBuffers[0][i - 1].getTextureAttachment(0).bind(0);

			downSampleEmissionNShader.setUniform2f("u_inverseSourceResolution", 1f / downSampleEmissionBuffers[0][i - 1].getWidth(), 1f / downSampleEmissionBuffers[0][i - 1].getHeight());
			downSampleEmissionNShader.setUniform2f(
					"u_padSourceTexture",
					downSampleEmissionBuffers[0][i].getWidth() * 2 > downSampleEmissionBuffers[0][i - 1].getWidth() ? 1 : 0,
					downSampleEmissionBuffers[0][i].getHeight() * 2 > downSampleEmissionBuffers[0][i - 1].getHeight() ? 1 : 0
			                                      );
			fullscreenQuad.render();
		}
		downSampleEmissionNShader.deactivate();

		// blur
		blurShader.activate();
		blurShader.setUniform1i("u_sourceBuffer", 0);

		// x direction
		blurShader.setUniform2f("u_blurDirection", 1, 0);
		for (int i = 0; i < downSampleEmissionBuffers[0].length; i++) {
			downSampleEmissionBuffers[1][i].bind();
			downSampleEmissionBuffers[0][i].getTextureAttachment(0).bind(0);
			blurShader.setUniform2f("u_inverseResolution", 1f / downSampleEmissionBuffers[0][i].getWidth(), 1f / downSampleEmissionBuffers[0][i].getHeight());
			fullscreenQuad.render();
		}

		// y direction
		blurShader.setUniform2f("u_blurDirection", 0, 1);
		for (int i = 0; i < downSampleEmissionBuffers[0].length; i++) {
			downSampleEmissionBuffers[0][i].bind();
			downSampleEmissionBuffers[1][i].getTextureAttachment(0).bind(0);
			blurShader.setUniform2f("u_inverseResolution", 1f / downSampleEmissionBuffers[0][i].getWidth(), 1f / downSampleEmissionBuffers[0][i].getHeight());
			fullscreenQuad.render();
		}
		blurShader.deactivate();
	}

	@Override
	public void render(IRenderTarget renderTarget, IReadOnlyCamera camera) {
		IRenderContext renderContext = new RenderContext(camera, gBuffer.getWidth(), gBuffer.getHeight());

		GL11.glEnable(GL_CULL_FACE);
		GL11.glCullFace(GL_BACK);

		// geometry pass
		gBuffer.bind();
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		for (IRenderable renderable : renderables) {
			renderable.renderGeometry(renderContext);
		}
		GL11.glDisable(GL11.GL_DEPTH_TEST);

		// light pass
		lightBuffer.bind();
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
		GL11.glEnable(GL11.GL_BLEND);
		glCullFace(GL_FRONT);
		glBlendFunc(GL_ONE, GL_ONE);

		gBuffer.getTextureAttachment(GBUFFER_DEPTH_SLOT).bind(ILight.DEPTH_BUFFER_SLOT);
		gBuffer.getTextureAttachment(GBUFFER_NORMAL_SLOT).bind(ILight.NORMAL_BUFFER_SLOT);
		gBuffer.getTextureAttachment(GBUFFER_MATERIAL_SLOT).bind(ILight.MATERIAL_BUFFER_SLOT);
		renderLights(renderContext);
		GL11.glCullFace(GL_BACK);
		GL11.glDisable(GL11.GL_BLEND);

		renderBloom(renderTarget);

		// combine pass
		renderTarget.bind();
		gBuffer.getTextureAttachment(GBUFFER_ALBEDO_SLOT).bind(0);
		gBuffer.getTextureAttachment(GBUFFER_NORMAL_SLOT).bind(1);
		gBuffer.getTextureAttachment(GBUFFER_MATERIAL_SLOT).bind(2);
		lightBuffer.getTextureAttachment(LIGHT_LIGHTS_SLOT).bind(3);

		for (int i = 0; i < EMISSION_LEVEL_COUNT; i++) {
			downSampleEmissionBuffers[0][i].getTextureAttachment(0).bind(4 + i);
		}

		gBufferCombineShader.activate();
		gBufferCombineShader.setUniform1i("u_albedoBuffer", 0);
		gBufferCombineShader.setUniform1i("u_normalBuffer", 1);
		gBufferCombineShader.setUniform1i("u_materialBuffer", 2);
		gBufferCombineShader.setUniform1i("u_lightBuffer", 3);
		for (int i = 0; i < EMISSION_LEVEL_COUNT; i++) {
			gBufferCombineShader.setUniform1i("u_emissionBuffer" + (i + 1), 4 + i);
		}

		fullscreenQuad.render();

		gBufferCombineShader.deactivate();
	}

	private static void loadBlurShader() {
		// normal distribution (sigma=2)
		float sigma = 5;
		int tailSampleCount = 10;
		float[] normalDistributionKernel = new float[tailSampleCount + 1];
		float sum = 0;
		for (int x = 0; x < normalDistributionKernel.length; x++) {
			normalDistributionKernel[x] = (float) Math.pow(Math.E, -(((float) x * x) / (2 * sigma * sigma)));
			sum += x == 0 ? normalDistributionKernel[x] : 2 * normalDistributionKernel[x];
		}
		for (int x = 0; x < normalDistributionKernel.length; x++) {
			normalDistributionKernel[x] /= sum;
		}

		float[] samplePositions = new float[tailSampleCount + 1];
		float[] sampleWeights = new float[tailSampleCount + 1];
		int midPoint = tailSampleCount / 2;
		samplePositions[midPoint] = 0.0f;
		sampleWeights[midPoint] = normalDistributionKernel[0];
		for (int i = 1; i <= tailSampleCount / 2; i++) {
			float sampleSum = normalDistributionKernel[i * 2 - 1] + normalDistributionKernel[i * 2];
			samplePositions[midPoint + i] = normalDistributionKernel[i * 2] / sampleSum;
			samplePositions[midPoint - i] = -samplePositions[midPoint + i];
			samplePositions[midPoint + i] += (i - 1) * 2;
			samplePositions[midPoint - i] -= (i - 1) * 2;
			sampleWeights[midPoint + i] = sampleSum;
			sampleWeights[midPoint - i] = sampleSum;
		}

		System.out.println(Arrays.toString(normalDistributionKernel));
		System.out.println(Arrays.toString(samplePositions));
		System.out.println(Arrays.toString(sampleWeights));

		Map<String, String> parameters = new HashMap<>();
		parameters.put("SAMPLE_COUNT", "#define SAMPLE_COUNT " + samplePositions.length);

		StringBuilder samplePositionsString = new StringBuilder();
		for (int i = 0; i < samplePositions.length; i++) {
			if (i > 0) samplePositionsString.append(",");
			samplePositionsString.append(samplePositions[i]);
		}
		parameters.put("SAMPLE_POSITIONS", "#define SAMPLE_POSITIONS " + samplePositionsString.toString());

		StringBuilder sampleWeightsString = new StringBuilder();
		for (int i = 0; i < sampleWeights.length; i++) {
			if (i > 0) sampleWeightsString.append(",");
			sampleWeightsString.append(sampleWeights[i]);
		}
		parameters.put("SAMPLE_WEIGHTS", "#define SAMPLE_WEIGHTS " + sampleWeightsString.toString());

		blurShader = ShaderLoader.loadShader("<deferredRenderer/blur.vert>", "<deferredRenderer/blur.frag>", parameters);
	}

	static {
		Map<String, String> gBufferCombineParameters = new HashMap<>();
		gBufferCombineParameters.put("EMISSION_TEXTURE_COUNT", "#define EMISSION_TEXTURE_COUNT " + EMISSION_LEVEL_COUNT);

		gBufferCombineShader = ShaderLoader.loadShader("<deferredRenderer/gBufferCombine.vert>", "<deferredRenderer/gBufferCombine.frag>", gBufferCombineParameters);
		downSampleEmission1Shader = ShaderLoader.loadShader("<deferredRenderer/downSampleEmission1.vert>", "<deferredRenderer/downSampleEmission1.frag>");
		downSampleEmissionNShader = ShaderLoader.loadShader("<deferredRenderer/downSampleEmissionN.vert>", "<deferredRenderer/downSampleEmissionN.frag>");
		loadBlurShader();

		fullscreenQuad = new VertexBufferObjectIndexed(
				new int[] { 2, 2 },
				6,
				4,
				new int[] { 0, 2, 1, 2, 0, 3 },
				new float[] { -1.0f, -1.0f,/**/-1.0f, 1.0f, /**/1.0f, 1.0f,/**/1.0f, -1.0f },
				new float[] { 0.0f, 0.0f,/**/0.0f, 1.0f, /**/1.0f, 1.0f,/**/1.0f, 0.0f }
		);
	}

}
