package com.brandon3055.draconicevolution.client.interfaces.guicomponents;

import com.brandon3055.draconicevolution.common.lib.References;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;

/**
 * Created by Brandon on 13/01/2015.
 */
public class ComponentSlotBackground extends ComponentBase{

	private static final ResourceLocation widgets = new ResourceLocation(References.RESOURCESPREFIX + "textures/gui/Widgets.png");

	public ComponentSlotBackground(int x, int y) {
		super(x, y);
	}

	@Override
	public int getWidth() {
		return 18;
	}

	@Override
	public int getHeight() {
		return 18;
	}

	@Override
	public void renderBackground(Minecraft minecraft, int offsetX, int offsetY, int mouseX, int mouseY) {
		minecraft.renderEngine.bindTexture(widgets);
		drawTexturedModalRect(x, y, 119, 1, getWidth(), getHeight());
	}

	@Override
	public void renderForground(Minecraft minecraft, int offsetX, int offsetY, int mouseX, int mouseY) {

	}

	public static ComponentCollection addInventorySlots(ComponentCollection c, int xOffset, int yOffset, int lockedSlot)
	{
		for (int x = 0; x < 9; x++) if (x != lockedSlot) c.addComponent(new ComponentSlotBackground(xOffset + x * 18, yOffset + 58)).setGroup("INVENTORY");

		for (int y = 0; y < 3; y++){
			for (int x = 0; x < 9; x++) if (x + y * 9 + 9 != lockedSlot) c.addComponent(new ComponentSlotBackground(xOffset + x * 18, yOffset + y * 18)).setGroup("INVENTORY");
		}

		return c;
	}
}
