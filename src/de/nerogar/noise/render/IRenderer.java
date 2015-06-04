package de.nerogar.noise.render;

import de.nerogar.noise.util.Matrix4f;

/**
 * @param <RenderableT> the type of renderables this renderer can render
 */
public interface IRenderer<RenderableT extends IRenderable> {

	/**
	 * Add an object to the renderer.
	 * 
	 * @param object the object you want to add to the scene
	 */
	public void addObject(RenderableT object);
	
	/**
	 * Remove an object from the renderer.
	 * 
	 * @param object the object you want to remove from the scene
	 */
	public void removeObject(RenderableT object);

	
	/**
	 * Set the resolution of the FrameBufferObject this renderer renders to.
	 * 
	 * @param width the new width
	 * @param height the new height
	 */
	public void setFrameBufferResolution(int width, int height);
	
	/**
	 * Renders into the FrameBufferObject returned by getRenderTarget().
	 * 
	 * @param viewMatrix the view Matrix
	 */
	public void render(Matrix4f viewMatrix);

	/**
	 * @return the FrameBufferObject containing the rendered image
	 */
	public FrameBufferObject getRenderTarget();

	/**
	 * Rebuilds objects like VertexBufferObjects. Depending on the implementation this can be slow.
	 */
	public void rebuild();
	
}
