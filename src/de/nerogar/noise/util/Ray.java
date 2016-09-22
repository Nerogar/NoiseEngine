package de.nerogar.noise.util;

public class Ray {

	private Vector3f start;
	private Vector3f dir;

	public Ray(Vector3f start, Vector3f dir) {
		this.start = start;
		this.dir = dir;
	}

	public Vector3f getStart()           { return start; }

	public void setStart(Vector3f start) { this.start = start; }

	public Vector3f getDir()             { return dir; }

	public void setDir(Vector3f dir)     { this.dir = dir; }

}
