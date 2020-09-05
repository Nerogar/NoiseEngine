package de.nerogar.noise.oldGame.core.systems;

import de.nerogar.noise.math.Vector3f;
import de.nerogar.noise.oldGame.Entity;
import de.nerogar.noise.oldGame.LogicSystem;
import de.nerogar.noise.oldGame.core.components.BoundingComponent;
import de.nerogar.noise.oldGame.core.components.PositionComponent;
import de.nerogar.noise.math.BoundingSphere;
import de.nerogar.noise.util.SpaceOctree;
import de.nerogar.noiseInterface.math.IVector3f;
import de.nerogar.noiseInterface.math.IBounding;

import java.util.ArrayList;
import java.util.Collection;

public class PositionLookupSystem extends LogicSystem {

	private SpaceOctree<BoundingComponent> components;

	private BoundingSphere sphereInstance;

	public PositionLookupSystem() {
		components = new SpaceOctree<>(BoundingComponent::getBounding);

		sphereInstance = new BoundingSphere(new Vector3f(), 0);
	}

	public void registerEntity(Entity entity) {
		BoundingComponent boundingComponent = entity.getComponent(BoundingComponent.class);
		if (boundingComponent != null) {
			boundingComponent.refreshBounding();
			components.add(boundingComponent);
		}
	}

	public void updateEntity(Entity entity) {
		BoundingComponent boundingComponent = entity.getComponent(BoundingComponent.class);
		if (boundingComponent != null) {
			boundingComponent.refreshBounding();
			components.update(boundingComponent);
		}
	}

	public void unregisterEntity(Entity entity) {
		BoundingComponent boundingComponent = entity.getComponent(BoundingComponent.class);
		if (boundingComponent != null) {
			components.remove(boundingComponent);
		}
	}

	public Collection<BoundingComponent> getBoundings(IBounding bounding) {
		return components.getFilteredExact(new ArrayList<>(), bounding);
	}

	public Collection<BoundingComponent> getBoundingsAround(IVector3f center, float radius) {
		sphereInstance.setCenter(center);
		sphereInstance.setRadius(radius);

		return getBoundings(sphereInstance);
	}

	public Collection<BoundingComponent> getBoundingsAround(Entity entity, float radius) {
		PositionComponent positionComponent = entity.getComponent(PositionComponent.class);
		sphereInstance.setCenter(positionComponent.getX(), positionComponent.getY(), positionComponent.getZ());
		sphereInstance.setRadius(radius);

		return getBoundings(sphereInstance);
	}

}
