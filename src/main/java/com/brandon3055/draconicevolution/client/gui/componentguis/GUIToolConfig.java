package com.brandon3055.draconicevolution.client.gui.componentguis;

import com.brandon3055.brandonscore.client.gui.guicomponents.*;
import com.brandon3055.draconicevolution.client.gui.GuiHudConfig;
import com.brandon3055.draconicevolution.client.gui.guicomponents.ComponentConfigItemButton;
import com.brandon3055.draconicevolution.client.gui.guicomponents.ComponentFieldAdjuster;
import com.brandon3055.draconicevolution.client.gui.guicomponents.ComponentFieldButton;
import com.brandon3055.draconicevolution.common.container.ContainerAdvTool;
import com.brandon3055.draconicevolution.common.lib.References;
import com.brandon3055.draconicevolution.common.utills.IConfigurableItem;
import com.brandon3055.draconicevolution.common.utills.IInventoryTool;
import com.brandon3055.draconicevolution.common.utills.ItemConfigField;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;

/**
 * Created by Brandon on 26/12/2014.
 */
public class GUIToolConfig extends GUIBase {

	public EntityPlayer player;
	private static final ResourceLocation inventoryTexture = new ResourceLocation(References.RESOURCESPREFIX + "textures/gui/ToolConfig.png");
	private int screenLevel = 0;
	private ItemStack editingItem;
	private ContainerAdvTool container;

	public GUIToolConfig(EntityPlayer player, ContainerAdvTool container) {
		super(container, 198, 89);
		this.container = container;
		this.player = player;
		container.setSlotsActive(false);
		addDependentComponents();
	}

	@SuppressWarnings("unchecked")
	@Override
	public void initGui() {
		super.initGui();
	}

	@Override
	protected void actionPerformed(GuiButton button) {
		super.actionPerformed(button);
	}

	@Override
	protected ComponentCollection assembleComponents() {
		ComponentCollection c = new ComponentCollection(0, 0, xSize, ySize+14, this);
		c.addComponent(new ComponentTexturedRect(0, 0, 198, 89, inventoryTexture)).setGroup("BACKGROUND");
		c.addComponent(new ComponentButton(3, 26, 20, 12, 0, this, "<=", StatCollector.translateToLocal("gui.back"))).setGroup("BUTTONS").setName("BACK_BUTTON");
		c.addComponent(new ComponentButton(3, 39, 20, 12, 1, this, "Inv", StatCollector.translateToLocal("gui.de.itemInventory.txt"))).setGroup("BUTTONS").setName("INVENTORY_BUTTON");
		c.addComponent(new ComponentFieldAdjuster(4, 34, null, this)).setGroup("FIELD_BUTTONS").setName("FIELD_CONFIG_BUTTON_ARRAY");
		c.addComponent(new ComponentButton(0, ySize, xSize, 14, 2, this, StatCollector.translateToLocal("gui.de.configureGuiElements.txt")).setGroup("INV_SCREEN"));
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

		setLevel(0);
	}

	public void updateItemButtons()
	{
		for (ComponentBase component : collection.getComponents())
		{
			if (component instanceof ComponentConfigItemButton)
			{
				((ComponentConfigItemButton) component).refreshState();
			}
		}
	}

	@Override
	protected void mouseClicked(int x, int y, int button) {
		super.mouseClicked(x, y, button);

		if (buttonPressed) return;


		int fieldOffsetX = 24;
		int fieldOffsetY = 5;

		for (ComponentBase component : collection.getComponents()){
			if (component.isEnabled() && component instanceof ComponentConfigItemButton && component.isMouseOver(x - this.guiLeft, y - this.guiTop) && ((ComponentConfigItemButton) component).hasValidItem){
				ItemStack stack = player.inventory.getStackInSlot(((ComponentConfigItemButton) component).slot);
				if (stack == null || !(stack.getItem() instanceof IConfigurableItem)) return;
				buttonPressed = true;
				IConfigurableItem item = (IConfigurableItem)stack.getItem();

				setEditingItem(stack, ((ComponentConfigItemButton) component).slot);
				setLevel(1);
				for (ItemConfigField field : item.getFields(stack, ((ComponentConfigItemButton) component).slot)){
					collection.addComponent(new ComponentFieldButton(fieldOffsetX, fieldOffsetY, player, field, this)).setGroup("LIST_SCREEN");
					fieldOffsetY += 12;
				}

				collection.addComponent(new ComponentItemRenderer(3, 5, stack)).setGroup("LIST_SCREEN");
				break;
			}
		}
	}


	@Override
	public void buttonClicked(int id, int button) {
		super.buttonClicked(id, button);

		if (id == 0 && screenLevel > 0){//button back
			setLevel(screenLevel - 1);
		}
		else if (id == 1 && editingItem != null){//inventory button
			setLevel(3);
			Minecraft.getMinecraft().displayGuiScreen(new GUIToolInventory(player, container, this));
//			LogHelper.info("Pre send container " + Minecraft.getMinecraft().thePlayer.openContainer);
//			DraconicEvolution.network.sendToServer(new ButtonPacket(ButtonPacket.ID_TOOLINVENTORY, false));
		}
		else if (id == 2) {
			Minecraft.getMinecraft().displayGuiScreen(new GuiHudConfig(this));
		}
	}

	public void setLevel(int level){
		this.screenLevel = level;

		if (level == 0){//inv screen
			collection.schedulRemoval("LIST_SCREEN");
			collection.setOnlyGroupEnabled("INV_SCREEN");
			collection.setGroupEnabled("BACKGROUND", true);
			collection.setComponentEnabled("BACK_BUTTON", false);
		}
		else if (level == 1){//list screen
			collection.setOnlyGroupEnabled("LIST_SCREEN");
			collection.setGroupEnabled("BACKGROUND", true);
			collection.setComponentEnabled("BACK_BUTTON", true);
			if (editingItem != null && editingItem.getItem() instanceof IInventoryTool) collection.setComponentEnabled("INVENTORY_BUTTON", true);
			if (collection.getComponent("BACK_BUTTON") != null) collection.getComponent("BACK_BUTTON").setY(26);
		}
		else if (level == 2){//field screen
			collection.setOnlyGroupEnabled("FIELD_BUTTONS");
			collection.setGroupEnabled("BACKGROUND", true);
			collection.setComponentEnabled("BACK_BUTTON", true);
			if (collection.getComponent("BACK_BUTTON") != null) collection.getComponent("BACK_BUTTON").setY(3);
		}
		else if (level == 3){//inventory screen

		}
	}

	public void setFieldBeingEdited(ItemConfigField field){
		((ComponentFieldAdjuster) collection.getComponent("FIELD_CONFIG_BUTTON_ARRAY")).field = field;
		setLevel(2);
	}

	public void setEditingItem(ItemStack stack, int slot) {
		this.editingItem = stack;
		container.updateInventoryStack(slot);
	}

	@Override
	protected boolean checkHotbarKeys(int p_146983_1_) {
		return false;
	}
}
