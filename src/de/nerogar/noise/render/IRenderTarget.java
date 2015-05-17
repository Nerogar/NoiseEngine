package de.nerogar.noise.render;

public interface IRenderTarget {

	/**
	 * Binds this target. All OpenGL calls will draw to this target now. 
	 */
	public void bind();

}
