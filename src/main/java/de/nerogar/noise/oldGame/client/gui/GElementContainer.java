package de.nerogar.noise.oldGame.client.gui;

import de.nerogar.noise.input.InputHandler;
import de.nerogar.noise.math.Matrix4f;
import de.nerogar.noiseInterface.math.IMatrix4f;

import java.util.ArrayList;
import java.util.List;

public abstract class GElementContainer extends GElement {

	private class NewGElement {

		GElement element;
		int      alignmentX;
		int      alignmentY;
		int      distanceX;
		int      distanceY;

		public NewGElement(GElement element, int alignmentX, int alignmentY, int distanceX, int distanceY) {
			this.element = element;
			this.alignmentX = alignmentX;
			this.alignmentY = alignmentY;
			this.distanceX = distanceX;
			this.distanceY = distanceY;
		}
	}

	private List<GElement> elements;

	private boolean           updating;
	private List<NewGElement> newElements;
	private List<GElement>    removedElements;

	public GElementContainer() {
		elements = new ArrayList<>();

		newElements = new ArrayList<>();
		removedElements = new ArrayList<>();
	}

	public void addElement(GElement element, int alignmentX, int alignmentY, int distanceX, int distanceY) {
		if (updating) {
			newElements.add(new NewGElement(element, alignmentX, alignmentY, distanceX, distanceY));
		} else {
			element.setPosition(alignmentX, alignmentY, distanceX, distanceY);
			element.recalculatePosition(posX, posY, width, height);
			elements.add(element);
		}
	}

	public void removeElement(GElement element) {
		if (updating) {
			removedElements.add(element);
		} else {
			elements.remove(element);
		}
	}

	@Override
	protected void setContainer(GuiContainer container) {
		super.setContainer(container);
		for (GElement element : elements) {
			element.setContainer(container);
		}
	}

	@Override
	public void recalculatePosition(int parentX, int parentY, int parentWidth, int parentHeight) {
		super.recalculatePosition(parentX, parentY, parentWidth, parentHeight);
		for (GElement element : elements) {
			element.recalculatePosition(posX, posY, width, height);
		}
	}

	@Override
	public void update(InputHandler inputHandler, float timeDelta) {
		updating = true;
		for (GElement element : elements) {
			element.update(inputHandler, timeDelta);
		}
		super.update(inputHandler, timeDelta);
		updating = false;

		// add newElements
		for (NewGElement newElement : newElements) {
			addElement(newElement.element, newElement.alignmentX, newElement.alignmentY, newElement.distanceX, newElement.distanceY);
		}

		// remove removedElements
		for (GElement removedElement : removedElements) {
			removeElement(removedElement);
		}

		newElements.clear();
		removedElements.clear();
	}

	public void render(IMatrix4f projectionMatrix) {
		for (GElement element : elements) {
			element.render(projectionMatrix);
		}
	}

	public void clearElements() {
		for (GElement element : elements) {
			element.cleanup();
		}

		elements.clear();
	}

	@Override
	public void cleanup() {
		for (GElement element : elements) {
			element.cleanup();
		}
	}

}
