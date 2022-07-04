package de.nerogar.noise.render.deferredRenderer;

import de.nerogar.noise.file.FileUtil;
import de.nerogar.noise.render.*;
import de.nerogar.noise.render.camera.IReadOnlyCamera;
import de.nerogar.noiseInterface.render.deferredRenderer.*;
import de.nerogar.noiseInterface.render.deferredRenderer.enums.*;
import org.lwjgl.opengl.GL11;

import java.util.*;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL14.GL_CLAMP_TO_EDGE;

public class Renderer implements IRenderer {

	private static final int GBUFFER_DEPTH_SLOT    = -1;
	private static final int GBUFFER_ALBEDO_SLOT   = 0;
	private static final int GBUFFER_NORMAL_SLOT   = 1;
	private static final int GBUFFER_MATERIAL_SLOT = 2;
	private static final int GBUFFER_LIGHTS_SLOT   = 3;
	private static final int LBUFFER_LIGHTS_SLOT   = 0;

	private static final int BLOOM_LEVEL_COUNT = 6;

	private static final Shader             blitShader;
	private static final Shader             gBufferCombineShader;
	private static final Shader             downSampleBloomShader;
	private static final Shader             downSampleBloomNShader;
	private static final Shader             blurShader;
	private static final VertexBufferObject fullscreenQuad;

	private final FrameBufferObject     gBuffer;
	private final FrameBufferObject     lightBuffer;
	private final FrameBufferObject[][] downSampleBloomBuffers;

	private final List<IRenderPass> renderPasses;

	private final MapByClass<IRenderableGeometry> geometryMapByClass;
	private final MapByClass<ILight>              lightMapByClass;

	public Renderer(int width, int height) {
		lightBuffer = new FrameBufferObject(width, height, false,
		                                    Texture2D.DataType.BGRA_16_16_16F
		);
		lightBuffer.getTextureAttachment(0).setWrapMode(GL_CLAMP_TO_EDGE);

		gBuffer = new FrameBufferObject(width, height, true);
		gBuffer.attachTexture(GBUFFER_ALBEDO_SLOT, new Texture2D("albedo", width, height, null, Texture2D.InterpolationType.LINEAR, Texture2D.DataType.BGRA_8_8_8_8I));
		gBuffer.getTextureAttachment(GBUFFER_ALBEDO_SLOT).setWrapMode(GL_CLAMP_TO_EDGE);
		gBuffer.attachTexture(GBUFFER_NORMAL_SLOT, new Texture2D("normal", width, height, null, Texture2D.InterpolationType.NEAREST, Texture2D.DataType.BGRA_16_16_16I));
		gBuffer.getTextureAttachment(GBUFFER_NORMAL_SLOT).setWrapMode(GL_CLAMP_TO_EDGE);
		gBuffer.attachTexture(GBUFFER_MATERIAL_SLOT, new Texture2D("material", width, height, null, Texture2D.InterpolationType.NEAREST, Texture2D.DataType.BGRA_8_8_8_8I));
		gBuffer.getTextureAttachment(GBUFFER_MATERIAL_SLOT).setWrapMode(GL_CLAMP_TO_EDGE);
		gBuffer.attachTexture(GBUFFER_LIGHTS_SLOT, new Texture2D("lights", width, height, null, Texture2D.InterpolationType.NEAREST, Texture2D.DataType.BGRA_16_16_16_16F));
		gBuffer.getTextureAttachment(GBUFFER_LIGHTS_SLOT).setWrapMode(GL_CLAMP_TO_EDGE);

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
				downSampleBloomBuffers[i][j].getTextureAttachment(0).setWrapMode(GL_CLAMP_TO_EDGE);
			}
		}

		renderPasses = new ArrayList<>();
		renderPasses.add(new RenderPass());

		geometryMapByClass = new MapByClass<>();
		lightMapByClass = new MapByClass<>();
	}

	@Override
	public void addRenderPass() {
		renderPasses.add(new RenderPass());
	}

	@Override
	public IRenderPass getRenderPass(int index) {
		return renderPasses.get(index);
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

	private void renderBloom() {
		// first down sample step
		downSampleBloomBuffers[0][0].bind();
		downSampleBloomShader.activate();
		downSampleBloomShader.setUniform1Handle("u_lightBuffer", lightBuffer.getTextureAttachment(LBUFFER_LIGHTS_SLOT).getHandle());
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

	private BlendMode blit(Texture2D texture, BlendMode activeBlendMode, BlendMode newBlendMode) {
		blitShader.activate();
		newBlendMode.activate(activeBlendMode);
		glBlendFunc(GL_ONE, GL_ONE_MINUS_SRC_ALPHA);
		blitShader.setUniform1Handle("u_inColor", texture.getHandle());
		fullscreenQuad.render();
		blitShader.deactivate();

		return newBlendMode;
	}

	@Override
	public void render(IRenderTarget renderTarget, IReadOnlyCamera camera) {
		IRenderContext renderContext = new RenderContext(camera, gBuffer.getWidth(), gBuffer.getHeight(),
		                                                 gBuffer.getTextureAttachment(GBUFFER_DEPTH_SLOT),
		                                                 gBuffer.getTextureAttachment(GBUFFER_ALBEDO_SLOT),
		                                                 gBuffer.getTextureAttachment(GBUFFER_NORMAL_SLOT),
		                                                 gBuffer.getTextureAttachment(GBUFFER_MATERIAL_SLOT),
		                                                 gBuffer.getTextureAttachment(GBUFFER_LIGHTS_SLOT),
		                                                 lightBuffer.getTextureAttachment(LBUFFER_LIGHTS_SLOT)
		);

		FaceCullMode faceCullMode = null;
		DepthTestMode depthTestMode = null;
		BlendMode blendMode = null;

		// clear buffers
		depthTestMode = DepthTestMode.READ_AND_WRITE.activate(depthTestMode);
		gBuffer.bind();
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
		lightBuffer.bind();
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);

		for (IRenderPass renderPass : renderPasses) {
			// collect objects
			geometryMapByClass.clear();
			renderPass.getContainer().getGeometry(renderContext, geometryMapByClass.getAdder());
			lightMapByClass.clear();
			renderPass.getContainer().getLights(renderContext, lightMapByClass.getAdder());

			// geometry pass
			gBuffer.bind();

			depthTestMode = DepthTestMode.READ_AND_WRITE.activate(depthTestMode);
			blendMode = BlendMode.NONE.activate(blendMode);
			GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);

			for (List<IRenderableGeometry> list : geometryMapByClass.getLists()) {
				for (IRenderableGeometry geometry : list) {
					faceCullMode = geometry.getFaceCullMode().activate(faceCullMode);
					depthTestMode = geometry.getDepthTestMode().activate(depthTestMode);

					geometry.renderGeometry(renderContext);
				}
			}

			// light pass
			lightBuffer.bind();

			blendMode = blit(gBuffer.getTextureAttachment(GBUFFER_LIGHTS_SLOT), blendMode, BlendMode.OVERLAY);

			depthTestMode = DepthTestMode.NONE.activate(depthTestMode);
			blendMode = BlendMode.ADD.activate(blendMode);
			faceCullMode = FaceCullMode.FRONT.activate(faceCullMode);

			for (List<ILight> list : lightMapByClass.getLists()) {
				if (!list.isEmpty()) {
					list.get(0).renderBatch(renderContext, list);
				}
			}
		}

		// bloom
		depthTestMode = DepthTestMode.NONE.activate(depthTestMode);
		faceCullMode = FaceCullMode.BACK.activate(faceCullMode);
		blendMode = BlendMode.NONE.activate(blendMode);
		renderBloom();

		// combine pass

		renderTarget.bind();

		gBufferCombineShader.activate();

		gBufferCombineShader.setUniform1Handle("u_lightBuffer", lightBuffer.getTextureAttachment(LBUFFER_LIGHTS_SLOT).getHandle());
		for (int i = 0; i < BLOOM_LEVEL_COUNT; i++) {
			gBufferCombineShader.setUniform1Handle("u_bloomBuffer" + (i + 1), downSampleBloomBuffers[0][i].getTextureAttachment(0).getHandle());
		}
		fullscreenQuad.render();

		gBufferCombineShader.deactivate();
	}

	private static Shader loadBlurShader() {
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

		return ShaderLoader.loadShader(FileUtil.get("<deferredRenderer/blur.vert>", FileUtil.SHADER_SUBFOLDER), FileUtil.get("<deferredRenderer/blur.frag>", FileUtil.SHADER_SUBFOLDER), parameters);
	}

	static {
		Map<String, String> gBufferCombineParameters = new HashMap<>();
		gBufferCombineParameters.put("BLOOM_TEXTURE_COUNT", "#define BLOOM_TEXTURE_COUNT " + BLOOM_LEVEL_COUNT);

		blitShader = ShaderLoader.loadShader(
				FileUtil.get("<deferredRenderer/blit.vert>", FileUtil.SHADER_SUBFOLDER),
				FileUtil.get("<deferredRenderer/blit.frag>", FileUtil.SHADER_SUBFOLDER),
				gBufferCombineParameters
		                                    );

		gBufferCombineShader = ShaderLoader.loadShader(
				FileUtil.get("<deferredRenderer/gBufferCombine.vert>", FileUtil.SHADER_SUBFOLDER),
				FileUtil.get("<deferredRenderer/gBufferCombine.frag>", FileUtil.SHADER_SUBFOLDER),
				gBufferCombineParameters
		                                              );

		downSampleBloomShader = ShaderLoader.loadShader(
				FileUtil.get("<deferredRenderer/downSampleBloom1.vert>", FileUtil.SHADER_SUBFOLDER),
				FileUtil.get("<deferredRenderer/downSampleBloom1.frag>", FileUtil.SHADER_SUBFOLDER)
		                                               );

		downSampleBloomNShader = ShaderLoader.loadShader(
				FileUtil.get("<deferredRenderer/downSampleBloomN.vert>", FileUtil.SHADER_SUBFOLDER),
				FileUtil.get("<deferredRenderer/downSampleBloomN.frag>", FileUtil.SHADER_SUBFOLDER)
		                                                );

		blurShader = loadBlurShader();

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
