package com.brandon3055.draconicevolution.client.interfaces;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.inventory.IInventory;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import com.brandon3055.draconicevolution.common.container.ContainerReader;
import com.brandon3055.draconicevolution.common.core.utills.InventoryReader;

@SideOnly(Side.CLIENT)
public class GUIReader extends GuiContainer {

	private static final ResourceLocation TEXTURE = new ResourceLocation("books", "textures/gui/binder.png");
	protected final InventoryReader storage;	
	public GUIReader(IInventory inventoryPlayer, InventoryReader itemInventory) {
		super(new ContainerReader(inventoryPlayer, itemInventory));
		this.storage = itemInventory;
		this.ySize += 10;
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float f, int i, int j) {
		GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
		this.mc.renderEngine.bindTexture(TEXTURE);
		int x = (width - xSize) / 2;
		int y = (height - ySize) / 2;
		this.drawTexturedModalRect(x, y, 0, 0, xSize, ySize);
	}
}
