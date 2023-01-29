package com.brandon3055.draconicevolution.integration.nei;

import java.util.Collections;
import java.util.List;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.item.ItemStack;

import codechicken.nei.VisiblityData;
import codechicken.nei.api.INEIGuiHandler;
import codechicken.nei.api.TaggedInventoryArea;

import com.brandon3055.draconicevolution.client.gui.GUIUpgradeModifier;
import com.brandon3055.draconicevolution.client.gui.componentguis.GUIManual;
import com.brandon3055.draconicevolution.client.gui.componentguis.GUIToolConfig;

/**
 * Created by brandon3055 on 27/1/2016.
 */
public class DENEIGuiHandler implements INEIGuiHandler {

    public VisiblityData modifyVisiblity(GuiContainer gui, VisiblityData currentVisibility) {
        if (gui instanceof GUIUpgradeModifier && ((GUIUpgradeModifier) gui).inUse) currentVisibility.showNEI = false;
        else if (gui instanceof GUIToolConfig || gui instanceof GUIManual) currentVisibility.showNEI = false;
        return currentVisibility;
    }

    public Iterable<Integer> getItemSpawnSlots(GuiContainer gui, ItemStack item) {
        return Collections.emptyList();
    }

    public List<TaggedInventoryArea> getInventoryAreas(GuiContainer gui) {
        return null;
    }

    public boolean handleDragNDrop(GuiContainer gui, int mousex, int mousey, ItemStack draggedStack, int button) {
        return false;
    }

    public boolean hideItemPanelSlot(GuiContainer gui, int x, int y, int w, int h) {
        return false;
    }
}
