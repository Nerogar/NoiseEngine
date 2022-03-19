package de.nerogar.noise.render.deferredRenderer;

import de.nerogar.noise.file.FileUtil;
import de.nerogar.noise.render.*;
import de.nerogar.noise.render.camera.IReadOnlyCamera;
import de.nerogar.noiseInterface.render.deferredRenderer.*;
import org.lwjgl.opengl.GL11;

import java.util.*;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.GL_CLAMP_TO_BORDER;
import static org.lwjgl.opengl.GL14.GL_CLAMP_TO_EDGE;

/**
 * buffers:
 * <ul>
 *     <li> index - bit distribution - data </li>
 *     <li> -1 - (32)         - depth </li>
 *     <li>  0 - (8/8/8/8)    - albedo (rgb), emission power (a) </li>
 *     <li>  1 - (16/16/16)   - normal (rgb) </li>
 *     <li>  2 - (8/8/8/8)    - ambient occlusion (r), metalness (g), roughness (b), reflectance (a) </li>
 *     <li>  3 - (16/16/16)   - light (r/g/b) </li>
 *     <li> 4+ - (16/16/16)   - bloom passes (r/g/b) </li>
 * </ul>
 * <p>
 * passes:
 * <ol>
 *     <li>
 *         Base properties are copied from mesh surfaces into the base buffers. This includes these buffers: -1, 0, 1, 2
 *     </li>
 *     <li>
 *         Lights are calculated into buffer 3, based on the buffers -1, 0, 1, 2
 *     </li>
 *     <li>
 *         Emission is added to the buffer 3, based on buffer 0
 *     </li>
 *     <li>
 *         Bloom is calculated in buffers 4+, based on buffers 0, 3
 *     </li>
 *     <li>
 *         The final output is calculated and stored in the render target, based on buffers 3, 4+
 *     </li>
 * </ol>
 */
public class Renderer implements IRenderer {

	private static final int GBUFFER_DEPTH_SLOT    = -1;
	private static final int GBUFFER_ALBEDO_SLOT   = 0;
	private static final int GBUFFER_NORMAL_SLOT   = 1;
	private static final int GBUFFER_MATERIAL_SLOT = 2;
	private static final int LIGHT_LIGHTS_SLOT     = 0;

	private static final int BLOOM_LEVEL_COUNT = 6;

	private static Shader             gBufferCombineShader;
	private static Shader             downSampleBloomShader;
	private static Shader             downSampleBloomNShader;
	private static Shader             blurShader;
	private static VertexBufferObject fullscreenQuad;

	private final List<IRenderable>     renderables;
	private final FrameBufferObject     gBuffer;
	private final FrameBufferObject     lightBuffer;
	private final FrameBufferObject[][] downSampleBloomBuffers;

	private final List<ILight>                  tempLightList;
	private final List<Class<? extends ILight>> tempProcessedLightsClasses;
	private final List<List<ILight>>            tempProcessedLights;

	public Renderer(int width, int height) {
		this.renderables = new ArrayList<>();

		gBuffer = new FrameBufferObject(width, height, true);
		gBuffer.attachTexture(0, new Texture2D("albedo", width, height, null, Texture2D.InterpolationType.LINEAR, Texture2D.DataType.BGRA_8_8_8_8I));
		gBuffer.getTextureAttachment(0).setWrapMode(GL_CLAMP_TO_EDGE);
		gBuffer.attachTexture(1, new Texture2D("normal", width, height, null, Texture2D.InterpolationType.NEAREST, Texture2D.DataType.BGRA_16_16_16I));
		gBuffer.getTextureAttachment(1).setWrapMode(GL_CLAMP_TO_EDGE);
		gBuffer.attachTexture(2, new Texture2D("material", width, height, null, Texture2D.InterpolationType.NEAREST, Texture2D.DataType.BGRA_8_8_8_8I));
		gBuffer.getTextureAttachment(2).setWrapMode(GL_CLAMP_TO_EDGE);

		lightBuffer = new FrameBufferObject(width, height, false,
		                                    Texture2D.DataType.BGRA_16_16_16F
		);

		downSampleBloomBuffers = new FrameBufferObject[2][BLOOM_LEVEL_COUNT];
		for (int i = 0; i < downSampleBloomBuffers.length; i++) {
			for (int j = 0; j < downSampleBloomBuffers[i].length; j++) {
				downSampleBloomBuffers[i][j] = new FrameBufferObject(
						(int) Math.ceil((float) width / (2 << j)),
						(int) Math.ceil((float) height / (2 << j)),
						false
				);
				downSampleBloomBuffers[i][j].attachTexture(0, new Texture2D(
						"albedo",
						downSampleBloomBuffers[i][j].getWidth(), downSampleBloomBuffers[i][j].getHeight(),
						null, Texture2D.InterpolationType.LINEAR, Texture2D.DataType.BGRA_16_16_16F
				));
				downSampleBloomBuffers[i][j].getTextureAttachment(0).setWrapMode(GL_CLAMP_TO_BORDER);
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

		for (int i = 0; i < downSampleBloomBuffers.length; i++) {
			for (int j = 0; j < downSampleBloomBuffers[i].length; j++) {
				downSampleBloomBuffers[i][j].setResolution(
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
		downSampleBloomBuffers[0][0].bind();
		downSampleBloomShader.activate();
		downSampleBloomShader.setUniform1Handle("u_lightBuffer", lightBuffer.getTextureAttachment(LIGHT_LIGHTS_SLOT).getHandle());
		downSampleBloomShader.setUniform2f("u_inverseSourceResolution", 1f / gBuffer.getWidth(), 1f / gBuffer.getHeight());
		downSampleBloomShader.setUniform2f(
				"u_padSourceTexture",
				downSampleBloomBuffers[0][0].getWidth() * 2 > gBuffer.getWidth() ? 1 : 0,
				downSampleBloomBuffers[0][0].getHeight() * 2 > gBuffer.getHeight() ? 1 : 0
		                                  );
		fullscreenQuad.render();
		downSampleBloomShader.deactivate();

		// remaining down sample steps
		downSampleBloomNShader.activate();
		for (int i = 1; i < downSampleBloomBuffers[0].length; i++) {
			downSampleBloomBuffers[0][i].bind();
			downSampleBloomNShader.setUniform1Handle("u_bloomBuffer", downSampleBloomBuffers[0][i - 1].getTextureAttachment(0).getHandle());

			downSampleBloomNShader.setUniform2f("u_inverseSourceResolution", 1f / downSampleBloomBuffers[0][i - 1].getWidth(), 1f / downSampleBloomBuffers[0][i - 1].getHeight());
			downSampleBloomNShader.setUniform2f(
					"u_padSourceTexture",
					downSampleBloomBuffers[0][i].getWidth() * 2 > downSampleBloomBuffers[0][i - 1].getWidth() ? 1 : 0,
					downSampleBloomBuffers[0][i].getHeight() * 2 > downSampleBloomBuffers[0][i - 1].getHeight() ? 1 : 0
			                                   );
			fullscreenQuad.render();
		}
		downSampleBloomNShader.deactivate();

		// blur
		blurShader.activate();

		// x direction
		blurShader.setUniform2f("u_blurDirection", 1, 0);
		for (int i = 0; i < downSampleBloomBuffers[0].length; i++) {
			downSampleBloomBuffers[1][i].bind();
			blurShader.setUniform1Handle("u_sourceBuffer", downSampleBloomBuffers[0][i].getTextureAttachment(0).getHandle());
			blurShader.setUniform2f("u_inverseResolution", 1f / downSampleBloomBuffers[0][i].getWidth(), 1f / downSampleBloomBuffers[0][i].getHeight());
			fullscreenQuad.render();
		}

		// y direction
		blurShader.setUniform2f("u_blurDirection", 0, 1);
		for (int i = 0; i < downSampleBloomBuffers[0].length; i++) {
			downSampleBloomBuffers[0][i].bind();
			blurShader.setUniform1Handle("u_sourceBuffer", downSampleBloomBuffers[1][i].getTextureAttachment(0).getHandle());
			blurShader.setUniform2f("u_inverseResolution", 1f / downSampleBloomBuffers[0][i].getWidth(), 1f / downSampleBloomBuffers[0][i].getHeight());
			fullscreenQuad.render();
		}
		blurShader.deactivate();
	}

	private Texture2D normalTexture = Texture2DLoader.loadTexture("dist/dirt_1/normal.png", Texture2D.InterpolationType.LINEAR_MIPMAP);

	@Override
	public void render(IRenderTarget renderTarget, IReadOnlyCamera camera) {
		IRenderContext renderContext = new RenderContext(camera, gBuffer.getWidth(), gBuffer.getHeight(),
		                                                 gBuffer.getTextureAttachment(GBUFFER_DEPTH_SLOT),
		                                                 gBuffer.getTextureAttachment(GBUFFER_ALBEDO_SLOT),
		                                                 gBuffer.getTextureAttachment(GBUFFER_NORMAL_SLOT),
		                                                 gBuffer.getTextureAttachment(GBUFFER_MATERIAL_SLOT)
		);

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

		renderLights(renderContext);

		GL11.glCullFace(GL_BACK);
		GL11.glDisable(GL11.GL_BLEND);

		// bloom
		renderBloom(renderTarget);

		// combine pass
		renderTarget.bind();

		gBufferCombineShader.activate();

		gBufferCombineShader.setUniform1Handle("u_lightBuffer", lightBuffer.getTextureAttachment(LIGHT_LIGHTS_SLOT).getHandle());
		for (int i = 0; i < BLOOM_LEVEL_COUNT; i++) {
			gBufferCombineShader.setUniform1Handle("u_bloomBuffer" + (i + 1), downSampleBloomBuffers[0][i].getTextureAttachment(0).getHandle());
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

		//System.out.println(Arrays.toString(normalDistributionKernel));
		//System.out.println(Arrays.toString(samplePositions));
		//System.out.println(Arrays.toString(sampleWeights));

		Map<String, String> parameters = new HashMap<>();
		parameters.put("SAMPLE_COUNT", "#define SAMPLE_COUNT " + samplePositions.length);

		StringBuilder samplePositionsString = new StringBuilder();
		for (int i = 0; i < samplePositions.length; i++) {
			if (i > 0) samplePositionsString.append(",");
			samplePositionsString.append(samplePositions[i]);
		}
		parameters.put("SAMPLE_POSITIONS", "#define SAMPLE_POSITIONS " + samplePositionsString);

		StringBuilder sampleWeightsString = new StringBuilder();
		for (int i = 0; i < sampleWeights.length; i++) {
			if (i > 0) sampleWeightsString.append(",");
			sampleWeightsString.append(sampleWeights[i]);
		}
		parameters.put("SAMPLE_WEIGHTS", "#define SAMPLE_WEIGHTS " + sampleWeightsString);

		blurShader = ShaderLoader.loadShader(FileUtil.get("<deferredRenderer/blur.vert>", FileUtil.SHADER_SUBFOLDER), FileUtil.get("<deferredRenderer/blur.frag>", FileUtil.SHADER_SUBFOLDER), parameters);
	}

	static {
		Map<String, String> gBufferCombineParameters = new HashMap<>();
		gBufferCombineParameters.put("BLOOM_TEXTURE_COUNT", "#define BLOOM_TEXTURE_COUNT " + BLOOM_LEVEL_COUNT);

		gBufferCombineShader = ShaderLoader.loadShader(
				FileUtil.get("<deferredRenderer/gBufferCombine.vert>", FileUtil.SHADER_SUBFOLDER), FileUtil.get("<deferredRenderer/gBufferCombine.frag>", FileUtil.SHADER_SUBFOLDER), gBufferCombineParameters);
		downSampleBloomShader = ShaderLoader.loadShader(FileUtil.get("<deferredRenderer/downSampleBloom1.vert>", FileUtil.SHADER_SUBFOLDER), FileUtil.get("<deferredRenderer/downSampleBloom1.frag>", FileUtil.SHADER_SUBFOLDER));
		downSampleBloomNShader = ShaderLoader.loadShader(FileUtil.get("<deferredRenderer/downSampleBloomN.vert>", FileUtil.SHADER_SUBFOLDER), FileUtil.get("<deferredRenderer/downSampleBloomN.frag>", FileUtil.SHADER_SUBFOLDER));
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
