package de.nerogar.noise.render;

public interface IRenderTarget {

	/**Binds this target. All OpenGL calls will draw to this target after calling this method.*/
	public void bind();

	/**
	 * Sets the resolution of this render targe
	 * @param width the new width
	 * @param height the new height
	 */
	public void setResolution(int width, int height);

	/**@return the width of this render target*/
	public int getWidth();

	/**@return the height of this render target*/
	public int getHeight();

}
