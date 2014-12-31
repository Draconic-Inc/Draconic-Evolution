package com.brandon3055.draconicevolution.client.interfaces.guicomponents;

import com.brandon3055.draconicevolution.common.lib.References;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

/**
 * Created by Brandon on 31/12/2014.
 */
public class ComponentItemRenderer extends ComponentBase {
	private static final ResourceLocation texture = new ResourceLocation(References.RESOURCESPREFIX + "textures/gui/Widgets.png");

	ItemStack stack;
	public ComponentItemRenderer(int x, int y, ItemStack stack) {
		super(x, y);
		this.stack = stack;
	}

	@Override
	public int getWidth() {
		return 20;
	}

	@Override
	public int getHeight() {
		return 20;
	}

	@Override
	public void renderBackground(Minecraft minecraft, int offsetX, int offsetY, int mouseX, int mouseY) {

	}

	@Override
	public void renderForground(Minecraft minecraft, int offsetX, int offsetY, int mouseX, int mouseY) {
		Minecraft.getMinecraft().renderEngine.bindTexture(texture);
		drawTexturedModalRect(x, y, 118, 0, getWidth(), getHeight());
		drawItemStack(stack, x + 2, y + 2, "null");
	}

	@Override
	public void renderFinal(Minecraft minecraft, int offsetX, int offsetY, int mouseX, int mouseY) {
		if (isMouseOver(mouseX, mouseY)) {
			renderToolTip(stack, mouseX + offsetX, mouseY + offsetY);
		}
	}
}
