package de.nerogar.noise.ui.ui2d;

import de.nerogar.noise.input.InputHandler;
import de.nerogar.noise.ui.ui2d.nodes.Ui2dContainer;
import de.nerogar.noiseInterface.render.deferredRenderer.IRenderContext;

public class Ui2d extends Ui2dContainer {

	// The node that receives keyboard input
	private Ui2dNode activeNode;

	// The node that receives mouse input
	private Ui2dNode hoveredNode;

	public Ui2d() {
		super();
	}

	public void update(InputHandler inputHandler) {

	}

	@Override
	public void renderGeometry(IRenderContext renderContext) {
		int width = renderContext.getGBufferWidth();
		int height = renderContext.getGBufferHeight();

		super.renderGeometry(renderContext);
	}
}
