package de.nerogar.noise.render;

import de.nerogar.noise.Noise;
import de.nerogar.noise.debug.ResourceProfiler;
import de.nerogar.noise.render.Texture2D.DataType;
import de.nerogar.noise.render.Texture2D.InterpolationType;
import de.nerogar.noise.util.Logger;
import de.nerogar.noise.util.NoiseResource;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;

import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.glViewport;
import static org.lwjgl.opengl.GL20.glDrawBuffers;
import static org.lwjgl.opengl.GL30.*;

public class FrameBufferObject extends NoiseResource implements IRenderTarget {

	private static final int[] glColorAttachments = {
			GL_COLOR_ATTACHMENT0, GL_COLOR_ATTACHMENT1,
			GL_COLOR_ATTACHMENT2, GL_COLOR_ATTACHMENT3,
			GL_COLOR_ATTACHMENT4, GL_COLOR_ATTACHMENT5,
			GL_COLOR_ATTACHMENT6, GL_COLOR_ATTACHMENT7,
			GL_COLOR_ATTACHMENT8, GL_COLOR_ATTACHMENT9,
			GL_COLOR_ATTACHMENT10, GL_COLOR_ATTACHMENT11,
			GL_COLOR_ATTACHMENT12, GL_COLOR_ATTACHMENT13,
			GL_COLOR_ATTACHMENT14, GL_COLOR_ATTACHMENT15,
	};

	private static final int MAX_COLOR_ATTACHEMENTS = GL11.glGetInteger(GL_MAX_COLOR_ATTACHMENTS);

	private boolean initialized;

	private long glContext;

	private int framebufferID;

	private int width, height;

	private Texture2D   depthTexture;
	private Texture2D[] textures;

	public FrameBufferObject(int width, int height, boolean useDepthTexture, Texture2D.DataType... textures) {
		glContext = GLWindow.getCurrentContext();
		framebufferID = glGenFramebuffers();

		createTextures(textures);
		if (useDepthTexture) createDepthTexture();

		setResolution(width, height);

		Noise.getResourceProfiler().incrementValue(ResourceProfiler.FRAMEBUFFER_COUNT);
	}

	private void createTextures(Texture2D.DataType[] textures) {
		this.textures = new Texture2D[MAX_COLOR_ATTACHEMENTS];

		for (int i = 0; i < textures.length; i++) {
			this.textures[i] = new Texture2D("", 0, 0, null, InterpolationType.NEAREST, textures[i]);
		}
	}

	private void createDepthTexture() {
		depthTexture = new Texture2D("depth", 0, 0, null, InterpolationType.NEAREST, DataType.DEPTH);
	}

	/**
	 * Returns the specified texture attachment.
	 *
	 * @param slot the slot of the texture, -1 for depth texture
	 * @return the texture
	 */
	public Texture2D getTextureAttachment(int slot) {
		if (slot == -1) return depthTexture;
		else return textures[slot];
	}

	public Texture2D detachTextureAttachement(int slot) {
		Texture2D tempTexture;

		if (slot == -1) {
			tempTexture = depthTexture;
			depthTexture = null;
		} else {
			tempTexture = textures[slot];
			textures[slot] = null;
		}

		setResolution(width, height);

		return tempTexture;
	}

	public void attachTexture(int slot, Texture2D texture) {
		if (slot == -1) {
			if (depthTexture != null) throw new IllegalArgumentException("depthTexture is already set");
			depthTexture = texture;
		} else {
			if (textures[slot] != null) throw new IllegalArgumentException("texture at slot " + slot + " is already set");
			textures[slot] = texture;
		}

		setResolution(width, height);
	}

	@Override
	public void setResolution(int width, int height) {
		this.width = width;
		this.height = height;

		GLWindow.makeContextCurrent(glContext);

		glBindFramebuffer(GL_FRAMEBUFFER, framebufferID);

		for (int i = 0; i < textures.length; i++) {
			Texture2D texture = textures[i];

			if (texture != null) {
				texture.setWidth(width);
				texture.setHeight(height);
				texture.createTexture(null);
				glFramebufferTexture2D(GL_FRAMEBUFFER, glColorAttachments[i], GL_TEXTURE_2D, texture.getID(), 0);
			} else {
				glFramebufferTexture2D(GL_FRAMEBUFFER, glColorAttachments[i], GL_TEXTURE_2D, 0, 0);
			}
		}

		if (depthTexture != null) {
			depthTexture.setWidth(width);
			depthTexture.setHeight(height);
			depthTexture.createTexture(null);

			glFramebufferTexture2D(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, GL_TEXTURE_2D, depthTexture.getID(), 0);
		} else {
			glFramebufferTexture2D(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, GL_TEXTURE_2D, 0, 0);
		}

		setDrawBuffers();
		initialized = true;
	}

	@Override
	public int getWidth() {
		return width;
	}

	@Override
	public int getHeight() {
		return height;
	}

	private void setDrawBuffers() {
		int[] indices = new int[textures.length];
		for (int i = 0; i < indices.length; i++) {
			indices[i] = textures[i] == null ? GL11.GL_NONE : glColorAttachments[i];
		}
		IntBuffer indexBuffer = BufferUtils.createIntBuffer(indices.length);
		indexBuffer.put(indices);
		indexBuffer.flip();

		glDrawBuffers(indexBuffer);
	}

	@Override
	public void bind() {
		if (!initialized) throw new IllegalStateException("RenderScene not initialized");
		GLWindow.makeContextCurrent(glContext);
		glBindFramebuffer(GL_FRAMEBUFFER, framebufferID);
		glViewport(0, 0, width, height);

		Noise.getResourceProfiler().incrementValue(ResourceProfiler.FRAMEBUFFER_BINDS);
	}

	public static void bindDefault() {
		glBindFramebuffer(GL_FRAMEBUFFER, 0);
	}

	public int getFramebufferID() {
		return framebufferID;
	}

	public boolean cleanup() {
		if (!super.cleanup()) return false;

		if (depthTexture != null) {
			depthTexture.cleanup();
		}

		for (Texture2D texture : textures) {
			if (texture != null) texture.cleanup();
		}

		glDeleteFramebuffers(framebufferID);
		initialized = false;

		Noise.getResourceProfiler().decrementValue(ResourceProfiler.FRAMEBUFFER_COUNT);

		return false;
	}

}
