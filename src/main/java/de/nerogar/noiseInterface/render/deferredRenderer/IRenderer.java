package de.nerogar.noiseInterface.render.deferredRenderer;

import de.nerogar.noise.render.IRenderTarget;
import de.nerogar.noise.render.camera.Camera;

public interface IRenderer {

	public void addObject(IRenderable renderable);

	public void setResolution(int width, int height);

	public void render(IRenderTarget renderTarget, Camera camera);

}
