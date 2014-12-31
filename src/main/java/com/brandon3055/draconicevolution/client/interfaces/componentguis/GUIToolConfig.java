package com.brandon3055.draconicevolution.client.interfaces.componentguis;

import com.brandon3055.draconicevolution.client.interfaces.guicomponents.*;
import com.brandon3055.draconicevolution.client.interfaces.manual.GuiButtonAHeight;
import com.brandon3055.draconicevolution.common.container.DummyContainer;
import com.brandon3055.draconicevolution.common.lib.References;
import com.brandon3055.draconicevolution.common.utills.IConfigurableItem;
import com.brandon3055.draconicevolution.common.utills.ItemConfigValue;
import com.brandon3055.draconicevolution.common.utills.LogHelper;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

/**
 * Created by Brandon on 26/12/2014.
 */
public class GUIToolConfig extends GUIBase {

	EntityPlayer player;
	private static final ResourceLocation inventoryTexture = new ResourceLocation(References.RESOURCESPREFIX + "textures/gui/ToolConfig.png");

	public GUIToolConfig(EntityPlayer player) {
		super(new DummyContainer(), 198, 89);
		this.player = player;
		addDependentComponents();
	}

	@SuppressWarnings("unchecked")
	@Override
	public void initGui() {
		super.initGui();
		buttonList.clear();
		buttonList.add(new GuiButtonAHeight(0, guiLeft + 3, guiTop + 25, 20, 12, "<="));
		((GuiButton)buttonList.get(0)).visible = false;
	}

	@Override
	protected void actionPerformed(GuiButton button) {
		super.actionPerformed(button);
		((GuiButton)buttonList.get(0)).visible = false;
		collection.removeGroup("LIST_SCREEN");
		collection.setOnlyGroupEnabled("INV_SCREEN");
		collection.setGroupEnabled("BACKGROUND", true);
		LogHelper.info("button");
	}

	@Override
	protected ComponentCollection assembleComponents() {
		ComponentCollection c = new ComponentCollection(0, 0, xSize, ySize);
		c.addComponent(new ComponentBackground(0, 0, 198, 89, inventoryTexture)).setGroup("BACKGROUND");
		return c;
	}

	protected void addDependentComponents(){
		for (int x = 0; x < 9; x++) {
			collection.addComponent(new ComponentConfigItemButton(29 + 18 * x, 64, x, player)).setGroup("INV_SCREEN");
		}

		for (int y = 0; y < 3; y++) {
			for (int x = 0; x < 9; x++) {
				collection.addComponent(new ComponentConfigItemButton(29 + 18 * x, 7 + y * 18, x + y * 9 + 9, player)).setGroup("INV_SCREEN");
			}
		}

		for (int y = 0; y < 4; y++){
			collection.addComponent(new ComponentConfigItemButton(6, 7 + y * 19, 39 - y, player)).setGroup("INV_SCREEN");
		}

		collection.setOnlyGroupEnabled("INV_SCREEN");
		collection.setGroupEnabled("BACKGROUND", true);
	}

	@Override
	protected void mouseClicked(int x, int y, int button) {
		super.mouseClicked(x, y, button);

		int fieldOffsetX = 24;
		int fieldOffsetY = 5;

		for (ComponentBase component : collection.getComponents()){
			if (component.isEnabled() && component instanceof ComponentConfigItemButton && component.isMouseOver(x - this.guiLeft, y - this.guiTop) && ((ComponentConfigItemButton) component).hasValidItem){
				ItemStack stack = player.inventory.getStackInSlot(((ComponentConfigItemButton) component).slot);
				if (stack == null || !(stack.getItem() instanceof IConfigurableItem)) return;
				IConfigurableItem item = (IConfigurableItem)stack.getItem();

				for (ItemConfigValue field : item.getFields(stack, ((ComponentConfigItemButton) component).slot)){
					collection.addComponent(new ComponentFieldButton(fieldOffsetX, fieldOffsetY, player, field)).setGroup("LIST_SCREEN");
					fieldOffsetY += 12;
				}

				collection.addComponent(new ComponentItemRenderer(3, 5, stack)).setGroup("LIST_SCREEN");
				collection.setOnlyGroupEnabled("LIST_SCREEN");
				collection.setGroupEnabled("BACKGROUND", true);
				((GuiButton)buttonList.get(0)).visible = true;
				break;
			}
		}
	}
}
