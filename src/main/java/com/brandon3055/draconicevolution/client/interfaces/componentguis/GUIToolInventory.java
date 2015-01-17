package com.brandon3055.draconicevolution.client.interfaces.componentguis;

import com.brandon3055.draconicevolution.client.interfaces.guicomponents.ComponentTexturedRect;
import com.brandon3055.draconicevolution.client.interfaces.guicomponents.ComponentCollection;
import com.brandon3055.draconicevolution.client.interfaces.guicomponents.ComponentSlotBackground;
import com.brandon3055.draconicevolution.client.interfaces.guicomponents.GUIBase;
import com.brandon3055.draconicevolution.common.container.ContainerAdvTool;
import com.brandon3055.draconicevolution.common.lib.References;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

/**
 * Created by Brandon on 13/01/2015.
 */
public class GUIToolInventory extends GUIBase {

	private static final ResourceLocation inventoryTexture = new ResourceLocation(References.RESOURCESPREFIX + "textures/gui/ToolConfig.png");

	private ContainerAdvTool container;
	private String inventoryName;

	public GUIToolInventory(EntityPlayer player, ContainerAdvTool container) {
		super(container, 198, 130);
		this.container = container;
		this.container.setSlotsActive(true);
		this.inventoryName = container.inventoryTool.getInventoryName();
		addDependentComponents();
	}

	@Override
	protected ComponentCollection assembleComponents() {
		ComponentCollection c = new ComponentCollection(0, 0, xSize, ySize, this);
		c.addComponent(new ComponentTexturedRect(0, 130 - 89, 198, 89, inventoryTexture)).setGroup("BACKGROUND");
		c.addComponent(new ComponentTexturedRect(0, 0, 198, 80, inventoryTexture)).setGroup("BACKGROUND");

		for (int i = 0; i < 5; i++)
		{
			c.addComponent(new ComponentSlotBackground(172, 18 + i * 21)).setGroup("INVENTORY");
			c.addComponent(new ComponentTexturedRect(173, 20 + i * 21, 0, 89, 16, 14, inventoryTexture, true)).setGroup("INVENTORY");
		}
		return c;
	}

	@Override
	protected void addDependentComponents() {
		for (int x = 0; x < container.inventoryTool.size; x++) collection.addComponent(new ComponentSlotBackground(7 + x * 18, 18)).setGroup("INVENTORY");
		ComponentSlotBackground.addInventorySlots(collection, 7, 44, container.inventoryItemSlot);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float f, int mouseX, int mouseY) {
		super.drawGuiContainerBackgroundLayer(f, mouseX, mouseY);
		GL11.glColor3f(1f, 1f, 1f);
		fontRendererObj.drawString(inventoryName, guiLeft + 5, guiTop + 5, 0x555555);
	}
}
