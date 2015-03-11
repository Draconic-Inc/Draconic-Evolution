package com.brandon3055.draconicevolution.client.gui.guicomponents;

import com.brandon3055.draconicevolution.client.gui.componentguis.ManualPage;
import com.brandon3055.draconicevolution.client.handler.ResourceHandler;
import cpw.mods.fml.common.registry.GameData;
import net.minecraft.client.Minecraft;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import org.lwjgl.opengl.GL11;

/**
 * Created by Brandon on 7/03/2015.
 */
public class ComponentIndexButton extends ComponentScrollingBase {

	private ManualPage page;
	private ItemStack stack;

	public ComponentIndexButton(int x, int y, GUIScrollingBase gui, ManualPage page) {
		super(x, y, gui);
		this.page = page;
		getStackFromName(page.name);
	}

	@Override
	public void handleScrollInput(int direction) {
		//this.y += direction * 10;
	}

	@Override
	public int getWidth() {
		return 200;
	}

	@Override
	public int getHeight() {
		return 20;
	}

	@Override
	public void renderBackground(Minecraft minecraft, int offsetX, int offsetY, int mouseX, int mouseY) {
		int sy = y - gui.scrollOffset;
		if (sy > 1 && sy + getHeight() < gui.getYSize())
		{
			boolean mouseOver = isMouseOver(mouseX, mouseY);

			fontRendererObj.drawString(page.getLocalizedName(), x + 19, sy, mouseOver ? 0xdd00ff : 0x000000);
			ResourceHandler.bindResource("textures/gui/Widgets.png");



			GL11.glColor4f(1f, 1f, 1f, 1f);
			if (mouseOver) {
				GL11.glColor4f(0f, 1f, 1f, 1f);
				drawTexturedModalRect(x - 2, sy - 2, 118, 0, 20, 20);
			}
			else drawTexturedModalRect(x-1, sy-1, 138, 0, 18, 18);

			if (stack != null && stack.getItem() != null) drawItemStack(stack, x, sy, "");
			else drawItemStack(new ItemStack(Items.writable_book), x, sy, "");

		}
	}

	@Override
	public void renderForground(Minecraft minecraft, int offsetX, int offsetY, int mouseX, int mouseY) {
	}

	private void getStackFromName(String name)
	{
		if (name.contains("tile."))
		{
			name = name.replace("draconicevolution", "DraconicEvolution").replace("tile.", "");
			stack = new ItemStack(GameData.getBlockRegistry().getObject(name), 1, page.meta);
		}
		if (name.contains("item."))
		{
			name = name.replace("draconicevolution", "DraconicEvolution").replace("item.", "");
			stack = new ItemStack(GameData.getItemRegistry().getObject(name), 1, page.meta);
		}
	}
}
