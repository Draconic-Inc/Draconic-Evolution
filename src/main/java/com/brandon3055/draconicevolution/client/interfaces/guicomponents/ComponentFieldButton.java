package com.brandon3055.draconicevolution.client.interfaces.guicomponents;

import com.brandon3055.draconicevolution.common.lib.References;
import com.brandon3055.draconicevolution.common.utills.ItemConfigValue;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

/**
 * Created by Brandon on 31/12/2014.
 */
public class ComponentFieldButton extends ComponentBase {

	private static final ResourceLocation texture = new ResourceLocation(References.RESOURCESPREFIX + "textures/gui/Widgets.png");

	public EntityPlayer player;
	public int slot;
	public ItemStack stack;
	public ItemConfigValue field;

	public ComponentFieldButton(int x, int y, EntityPlayer player, ItemConfigValue field) {
		super(x, y);
		this.player = player;
		this.slot = field.slot;
		this.stack = player.inventory.getStackInSlot(slot);
		this.field = field;
	}

	@Override
	public int getWidth() {
		return 150;
	}

	@Override
	public int getHeight() {
		return 12;
	}

	@Override
	public void renderBackground(Minecraft minecraft, int offsetX, int offsetY, int mouseX, int mouseY) {
		Minecraft.getMinecraft().renderEngine.bindTexture(texture);

		if (!isMouseOver(mouseX - offsetX, mouseY - offsetY))
		{
			drawTexturedModalRect(x, y, 18, 0, getWidth() - 50, getHeight());
			drawTexturedModalRect(x, y+getHeight()-1, 18, 19, getWidth() - 50, 1);

			drawTexturedModalRect(x + 50, y, 19, 0, getWidth() - 50 - 1, getHeight());
			drawTexturedModalRect(x + 50, y+getHeight()-1, 19, 19, getWidth() - 50 - 1, 1);
		}
		else
		{
			drawTexturedModalRect(x, y, 18, 20, getWidth() - 50, getHeight());
			drawTexturedModalRect(x, y + getHeight() - 1, 18, 39, getWidth() - 50, 1);

			drawTexturedModalRect(x + 50, y, 19, 20, getWidth() - 50 - 1, getHeight());
			drawTexturedModalRect(x + 50, y+getHeight()-1, 19, 39, getWidth() - 50 - 1, 1);
		}
	}

	@Override
	public void renderForground(Minecraft minecraft, int offsetX, int offsetY, int mouseX, int mouseY) {
		drawString(fontRendererObj, field.name, x + offsetX + 2, y + offsetY + (getHeight() / 2) - (fontRendererObj.FONT_HEIGHT / 2), 0xffffff);
	}
}
