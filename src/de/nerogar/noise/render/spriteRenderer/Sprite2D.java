package de.nerogar.noise.render.spriteRenderer;

import de.nerogar.noise.render.*;
import de.nerogar.noise.util.Vector2f;
import de.nerogar.noise.util.Vector3f;

public class Sprite2D implements IRenderable {

	protected Vector3f[] pos;
	protected Vector2f[] uv;
	protected Texture2D texture;
	protected RenderProperties2f renderProperties;

	public Sprite2D(Vector3f pos1, Vector3f pos2, Vector3f pos3, Vector3f pos4,
			Vector2f uv1, Vector2f uv2, Vector2f uv3, Vector2f uv4,
			Texture2D texture) {

		pos = new Vector3f[] { pos1, pos2, pos3, pos4 };
		uv = new Vector2f[] { uv1, uv2, uv3, uv4 };
		this.texture = texture;
	}

	public Sprite2D(Vector3f pos, Vector3f size, Vector2f uv, Vector2f uvSize, Texture2D texture) {
		this(pos.clone(), pos.clone().addY(size.getY()), pos.clone().addX(size.getX()).addY(size.getY()), pos.clone().addX(size.getX()),
				uv.clone(), uv.clone().addY(uvSize.getY()), uv.added(uvSize), uv.clone().addX(uvSize.getX()),
				texture);
	}

	public void setRenderProperties(RenderProperties2f renderProperties) {
		this.renderProperties = renderProperties;
	}

	public RenderProperties2f getRenderProperties() {
		return renderProperties;
	}

}
