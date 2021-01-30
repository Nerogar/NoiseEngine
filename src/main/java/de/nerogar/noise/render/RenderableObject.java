package de.nerogar.noise.render;

import de.nerogar.noise.render.animation.Skeleton;

public class RenderableObject {

	private Mesh     mesh;
	private Material material;
	private Skeleton skeleton;

	public RenderableObject(Mesh mesh, Material material, Skeleton skeleton) {
		this.mesh = mesh;
		this.material = material;
		this.skeleton = skeleton;
	}

	public Mesh getMesh()                      { return mesh; }

	public void setMesh(Mesh mesh)             { this.mesh = mesh; }

	public Material getMaterial()              { return material; }

	public void setMaterial(Material material) { this.material = material; }

	public Skeleton getSkeleton()              { return skeleton; }

	public void setSkeleton(Skeleton skeleton) { this.skeleton = skeleton; }

}
