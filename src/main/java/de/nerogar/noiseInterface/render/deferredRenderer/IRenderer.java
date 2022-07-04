package de.nerogar.noiseInterface.render.deferredRenderer;

import de.nerogar.noise.render.IRenderTarget;
import de.nerogar.noise.render.camera.IReadOnlyCamera;

public interface IRenderer {

	void addRenderPass();

	IRenderPass getRenderPass(int index);

	void setResolution(int width, int height);

	void render(IRenderTarget renderTarget, IReadOnlyCamera camera);


}
