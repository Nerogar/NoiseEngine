package de.nerogar.noise.oldGame.core.components;

import de.nerogar.noise.math.Vector3f;
import de.nerogar.noise.oldGame.Component;
import de.nerogar.noise.oldGame.core.events.BoundingChangeEvent;
import de.nerogar.noise.oldGame.core.systems.GameObjectsSystem;
import de.nerogar.noise.oldGame.core.systems.PositionLookupSystem;
import de.nerogar.noise.serialization.NDSNodeObject;
import de.nerogar.noiseInterface.math.IVector3f;
import de.nerogar.noiseInterface.math.IBounding;
import de.nerogar.noise.math.BoundingAABB;
import de.nerogar.noise.math.BoundingSphere;

public class BoundingComponent extends Component<BoundingComponent> {

	private IBounding bounding;

	public BoundingComponent() {
	}

	public BoundingComponent(IBounding bounding) {
		this.bounding = bounding;
	}

	@Override
	protected void initSystems() {
		refreshBounding();

		getEntity().getMap().getSystem(PositionLookupSystem.class).registerEntity(getEntity());
	}

	@Override
	public void setData(GameObjectsSystem gameObjectsSystem, NDSNodeObject data) {
		String type = data.getStringUTF8("type");
		if (type.equals("sphere")) {
			bounding = new BoundingSphere(new Vector3f(), data.getFloat("radius"));
		} else if (type.equals("aabb")) {
			IVector3f size = new Vector3f(data.getFloat("sizeX"), data.getFloat("sizeY"), data.getFloat("sizeZ"));
			bounding = new BoundingAABB(new Vector3f(), size);
		}
	}

	public void refreshBounding() {
		PositionComponent positionComponent = getEntity().getComponent(PositionComponent.class);
		if (bounding instanceof BoundingSphere) {
			((BoundingSphere) bounding).setCenter(positionComponent.getX(), positionComponent.getY(), positionComponent.getZ());
		} if (bounding instanceof BoundingAABB) {
			((BoundingAABB) bounding).setPosition(positionComponent.getX(), positionComponent.getY(), positionComponent.getZ());
		}

		getEntity().getMap().getEventManager().trigger(new BoundingChangeEvent(this));
	}

	public IBounding getBounding() {
		return bounding;
	}

	@Override
	protected void cleanup() {
		getEntity().getMap().getSystem(PositionLookupSystem.class).unregisterEntity(getEntity());
	}

	@Override
	public BoundingComponent newInstance() {
		return new BoundingComponent();
	}

	@Override
	public void copyFrom(BoundingComponent other) {
		bounding = other.bounding.clone();
	}

}
