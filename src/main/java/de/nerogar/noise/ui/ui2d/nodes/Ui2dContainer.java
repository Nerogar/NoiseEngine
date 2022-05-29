package de.nerogar.noise.ui.ui2d.nodes;

import de.nerogar.noise.ui.ui2d.Ui2dNode;
import de.nerogar.noise.ui.ui2d.util.Ui2dTransformation;
import de.nerogar.noiseInterface.math.ITransformation;

import java.util.ArrayList;
import java.util.List;

public class Ui2dContainer extends Ui2dNode {

	private final List<Ui2dNode>     children;
	private       Ui2dTransformation transformation;

	public Ui2dContainer() {
		this.children = new ArrayList<>();
		this.transformation = new Ui2dTransformation();
	}

	@Override
	public Ui2dTransformation getTransformation() {
		return transformation;
	}

	@Override
	public void setTransformation(ITransformation transformation) {
		if (transformation instanceof Ui2dTransformation ui2dTransformation) {
			this.transformation = ui2dTransformation;
		}
	}
}
