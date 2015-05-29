package de.nerogar.noise.render;

import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.glViewport;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

import java.nio.IntBuffer;

import org.lwjgl.BufferUtils;

import static org.lwjgl.glfw.GLFW.*;
import de.nerogar.noise.log.Logger;
import de.nerogar.noise.render.Texture2D.DataType;
import de.nerogar.noise.render.Texture2D.InterpolationType;

public class FrameBufferObject implements IRenderTarget {

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

	private boolean initialized;

	private long glContext;
	
	private int framebufferID; //, colorTextureID, normalTextureID, depthTextureID;

	private int width, height;

	private boolean useDepthTexture;
	private Texture2D depthTexture;
	private Texture2D[] textures;

	public FrameBufferObject(int width, int height, boolean useDepthTexture, Texture2D.DataType... textures) {
		this.useDepthTexture = useDepthTexture;
		glContext = glfwGetCurrentContext();
		createTextures(textures);
		if (useDepthTexture) activateDepthTexture();

		setResolution(width, height);
	}

	private void createTextures(Texture2D.DataType[] textures) {
		this.textures = new Texture2D[textures.length];

		for (int i = 0; i < textures.length; i++) {
			this.textures[i] = new Texture2D("", 0, 0, null, InterpolationType.NEAREST, textures[i]);
		}
	}

	private void activateDepthTexture() {
		useDepthTexture = true;
		depthTexture = new Texture2D("depth", 0, 0, null, InterpolationType.NEAREST, DataType.DEPTH);
	}

	/**
	 * Retruns the specified texture attachment.
	 * 
	 * @param slot the slot of the texture, -1 for depth texture
	 * @return the texture
	 */
	public Texture2D getTextureAttachment(int slot) {
		if (slot == -1) return depthTexture;
		else return textures[slot];
	}

	@Override
	public void setResolution(int width, int height) {
		this.width = width;
		this.height = height;

		glfwMakeContextCurrent(glContext);
		
		if (initialized) cleanup();

		framebufferID = glGenFramebuffers();
		glBindFramebuffer(GL_FRAMEBUFFER, framebufferID);

		for (int i = 0; i < textures.length; i++) {
			Texture2D texture = textures[i];
			texture.setWidth(width);
			texture.setHeight(height);
			texture.createTexture(null);
			glFramebufferTexture2D(GL_FRAMEBUFFER, glColorAttachments[i], GL_TEXTURE_2D, texture.getID(), 0);
		}

		if (useDepthTexture) {
			depthTexture.setWidth(width);
			depthTexture.setHeight(height);
			depthTexture.createTexture(null);

			glFramebufferTexture2D(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, GL_TEXTURE_2D, depthTexture.getID(), 0);
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
			indices[i] = glColorAttachments[i];
		}
		IntBuffer indexBuffer = BufferUtils.createIntBuffer(indices.length);
		indexBuffer.put(indices);
		indexBuffer.flip();
		
		glDrawBuffers(indexBuffer);
	}

	@Override
	public void bind() {
		if (!initialized) throw new IllegalStateException("RenderScene not initialized");
		glfwMakeContextCurrent(glContext);
		glBindFramebuffer(GL_FRAMEBUFFER, framebufferID);
		glViewport(0, 0, width, height);
	}

	public static void bindDefault() {
		glBindFramebuffer(GL_FRAMEBUFFER, 0);
	}

	public int getFramebufferID() {
		return framebufferID;
	}

	public void cleanup() {
		if (useDepthTexture) {
			depthTexture.cleanup();
			useDepthTexture = false;
		}

		for (Texture2D texture : textures) {
			texture.cleanup();
		}

		glDeleteFramebuffers(framebufferID);
		initialized = false;
	}

	@Override
	protected void finalize() throws Throwable {
		if (initialized) Logger.log(Logger.WARNING, "render Target not cleaned up. id: " + framebufferID);
	}

}
