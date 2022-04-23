package de.nerogar.noise.ui.ui2d;

import de.nerogar.noise.math.Transformation;
import de.nerogar.noise.ui.ui2d.util.Ui2dTransformation;
import de.nerogar.noiseInterface.render.deferredRenderer.IRenderable;

public abstract class Ui2dNode implements IRenderable {

	@Override
	public abstract Ui2dTransformation getTransformation();

	@Override
	public void setParentTransformation(Transformation parentTransformation) {
		getTransformation().setParent(parentTransformation);
	}

}
