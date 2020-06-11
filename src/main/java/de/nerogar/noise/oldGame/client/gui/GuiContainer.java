package de.nerogar.noise.oldGame.client.gui;

import de.nerogar.noise.input.InputHandler;
import de.nerogar.noise.render.FrameBufferObject;
import de.nerogar.noise.render.Texture2D;
import de.nerogar.noise.util.Matrix4f;
import de.nerogar.noise.util.Matrix4fUtils;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import static org.lwjgl.opengl.GL11.*;

public class GuiContainer {

	private int               width;
	private int               height;
	private FrameBufferObject fbo;
	private Matrix4f          projectionMatrix;

	private Set<Gui> blendOutGuis;
	private Gui      activeGui;

	public GuiContainer(int width, int height) {
		this.width = width;
		this.height = height;

		blendOutGuis = new HashSet<>();

		fbo = new FrameBufferObject(0, 0, false, Texture2D.DataType.BGRA_8_8_8_8I);
		projectionMatrix = new Matrix4f();

		setFrameBufferResolution(width, height);
	}

	public void setActiveGui(Gui activeGui) {
		if (this.activeGui != null){
			blendOutGuis.add(this.activeGui);
			this.activeGui.startBlendOut();
		}

		this.activeGui = activeGui;
		activeGui.setContainer(this);
		activeGui.recalculatePosition(0, 0, width, height);
	}

	public void setFrameBufferResolution(int width, int height) {
		this.width = width;
		this.height = height;

		fbo.setResolution(width, height);
		Matrix4fUtils.setOrthographicProjection(projectionMatrix, 0, width, height, 0, 1, -1);

		if (activeGui != null) {
			activeGui.recalculatePosition(0, 0, width, height);
		}
	}

	public void update(InputHandler inputHandler, float timeDelta) {
		activeGui.update(inputHandler, timeDelta);
	}

	public void render() {
		fbo.bind();

		glClearColor(0.0f, 0.0f, 0.0f,  0.0f);
		glClear(GL_COLOR_BUFFER_BIT);

		activeGui.render(projectionMatrix);

		Iterator<Gui> iterator = blendOutGuis.iterator();
		while (iterator.hasNext()) {
			Gui next = iterator.next();
			if (next.isBlendOutDone()) {
				iterator.remove();
				next.cleanup();
				continue;
			}

			next.render(projectionMatrix);
		}
	}

	public Matrix4f getProjectionMatrix() {
		return projectionMatrix;
	}

	public Texture2D getColorOutput() {
		return fbo.getTextureAttachment(0);
	}

}
