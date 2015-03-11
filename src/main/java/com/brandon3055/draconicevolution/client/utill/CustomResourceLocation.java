package com.brandon3055.draconicevolution.client.utill;

import com.brandon3055.draconicevolution.client.handler.ResourceHandler;
import net.minecraft.util.ResourceLocation;

/**
 * Created by Brandon on 10/03/2015.
 */
public class CustomResourceLocation extends ResourceLocation {

	private int width;
	private int height;

	public CustomResourceLocation(String texturePath, int width, int height) {
		super(texturePath);
		this.width = width;
		this.height = height;
	}

	public void bind() { ResourceHandler.bindTexture(this); }

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}
}
