package de.nerogar.noise.render.spriteRenderer;

import de.nerogar.noise.render.*;
import de.nerogar.noiseInterface.math.IVector2f;
import de.nerogar.noiseInterface.math.IVector3f;

public class Sprite2D {

	protected IVector3f[]        pos;
	protected IVector2f[]        uv;
	protected Texture2D          texture;
	protected RenderProperties2f renderProperties;

	public Sprite2D(IVector3f pos1, IVector3f pos2, IVector3f pos3, IVector3f pos4,
			IVector2f uv1, IVector2f uv2, IVector2f uv3, IVector2f uv4,
			Texture2D texture) {

		pos = new IVector3f[] { pos1, pos2, pos3, pos4 };
		uv = new IVector2f[] { uv1, uv2, uv3, uv4 };
		this.texture = texture;
	}

	public Sprite2D(IVector3f pos, IVector3f size, IVector2f uv, IVector2f uvSize, Texture2D texture) {
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
