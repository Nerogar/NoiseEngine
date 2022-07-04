package de.nerogar.noiseInterface.render.deferredRenderer;

import de.nerogar.noiseInterface.math.IReadOnlyTransformation;

import java.util.function.Consumer;

public interface IRenderableContainer {

	default void getGeometry(IRenderContext renderContext, Consumer<IRenderableGeometry> adder) {}

	default void getLights(IRenderContext renderContext, Consumer<ILight> adder)                {}

	void setTransformation(IReadOnlyTransformation transformation);

}
