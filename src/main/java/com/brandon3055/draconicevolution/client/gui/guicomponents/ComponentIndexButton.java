package com.brandon3055.draconicevolution.client.gui.guicomponents;

import com.brandon3055.draconicevolution.client.gui.componentguis.ManualPage;
import com.brandon3055.draconicevolution.common.utills.LogHelper;
import cpw.mods.fml.common.registry.GameData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;

/**
 * Created by Brandon on 7/03/2015.
 */
public class ComponentIndexButton extends ComponentScrollingBase {

	private ManualPage page;
	private EntityItem item;

	public ComponentIndexButton(int x, int y, GUIScrollingBase gui, ManualPage page) {
		super(x, y, gui);
		this.page = page;
		if (page.name.contains(".tile")) item = new EntityItem(Minecraft.getMinecraft().theWorld, 0, 0, 0, new ItemStack(GameData.getBlockRegistry().getObject(page.name.substring(page.name.indexOf(".") + 1).replace("draconicevolution", "DraconicEvolution"))));
		if (page.name.contains(".item")) item = new EntityItem(Minecraft.getMinecraft().theWorld, 0, 0, 0, new ItemStack(GameData.getItemRegistry().getObject(page.name.substring(page.name.indexOf(".") + 1).replace("draconicevolution", "DraconicEvolution"))));
		LogHelper.info(item + " " + GameData.getBlockRegistry().getObject("DraconicEvolution:draconiumOre") + " " + page.name.substring(page.name.indexOf(".") + 1).replace("draconicevolution", "DraconicEvolution") +
		" " + new ItemStack(GameData.getBlockRegistry().getObject(page.name.substring(page.name.indexOf(".") + 1).replace("draconicevolution", "DraconicEvolution"))));
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
		return 16;
	}

	@Override
	public void renderBackground(Minecraft minecraft, int offsetX, int offsetY, int mouseX, int mouseY) {
		int sy = y - gui.scrollOffset;
		if (sy > 1 && sy + getHeight() < gui.getYSize())
		{
			fontRendererObj.drawString("!!!!!!!!!!! Test !!!!!!!!!! " + y/12, x, sy, 0xffffff);
		}
		if (item != null) {
			LogHelper.info("r");
			RenderManager.instance.renderEntityWithPosYaw(item, 0D, 0D, 0D, 0F, 0F);
		}
	}

	@Override
	public void renderForground(Minecraft minecraft, int offsetX, int offsetY, int mouseX, int mouseY) {
	}
}
