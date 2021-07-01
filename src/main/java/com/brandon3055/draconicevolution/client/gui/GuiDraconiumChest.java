package com.brandon3055.draconicevolution.client.gui;

import com.brandon3055.brandonscore.client.BCSprites;
import com.brandon3055.brandonscore.client.gui.GuiToolkit;
import com.brandon3055.brandonscore.client.gui.modulargui.GuiElement;
import com.brandon3055.brandonscore.client.gui.modulargui.GuiElementManager;
import com.brandon3055.brandonscore.client.gui.modulargui.ModularGuiContainer;
import com.brandon3055.brandonscore.client.gui.modulargui.guielements.GuiEnergyBar;
import com.brandon3055.brandonscore.client.gui.modulargui.guielements.GuiSlotRender;
import com.brandon3055.brandonscore.client.gui.modulargui.guielements.GuiTexture;
import com.brandon3055.brandonscore.client.gui.modulargui.templates.TGuiBase;
import com.brandon3055.draconicevolution.blocks.tileentity.TileDraconiumChest;
import com.brandon3055.draconicevolution.inventory.ContainerDraconiumChest;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.text.ITextComponent;

/**
 * Created by Werechang on 27/6/21
 */
public class GuiDraconiumChest extends ModularGuiContainer<ContainerDraconiumChest> {

    protected GuiToolkit<GuiDraconiumChest> toolkit = new GuiToolkit<>(this, 488, 288).setTranslationPrefix("gui.draconicevolution.draconium_chest");
    private final TileDraconiumChest tile;

    public GuiDraconiumChest(ContainerDraconiumChest container, PlayerInventory inv, ITextComponent titleIn) {
        super(container, inv, titleIn);
        tile = container.tile;
    }

    @Override
    public void addElements(GuiElementManager manager) {
        // Create and load dynamic background
        TGuiBase template = new TGuiBase(this);
        template.background = GuiTexture.newDynamicTexture(xSize(), ySize(), () -> BCSprites.getThemed("background_dynamic"));
        template.background.onReload(guiTex -> guiTex.setPos(guiLeft(), guiTop()));
        toolkit.loadTemplate(template);

        // Add player inventory to gui
        template.addPlayerSlots(true, false, false);

        // Main storage
        GuiElement storage = toolkit.createSlots(template.background, 26, 10, 0);
        storage.setPos(guiLeft() + 10, guiTop() + 14);

        // Furnace inventory
        GuiElement furnace = toolkit.createSlots(template.background, 5, 1, 0);
        furnace.setPos(guiLeft() + 44, guiTop() + 206);

        // EnergyBar
        GuiEnergyBar energyBar = toolkit.createEnergyBar(template.background, tile.opStorage);
        energyBar.setHorizontal(true);
        energyBar.setPos(guiLeft() + 31, guiTop() + 247);
        energyBar.setYSize(14).setMaxXPos(toolkit.guiLeft() + 100, true).setXSize(120);
        template.energyBar = energyBar;

        // Capacitor slot
        GuiSlotRender powerSlot = new GuiSlotRender(energyBar.xPos() - 21, energyBar.yPos() + 17);
        template.background.addChild(powerSlot);
        // Capacitor sprite
        GuiTexture bgTexture = new GuiTexture(16, 16, BCSprites.get("slots/energy")).setPos(powerSlot.xPos() + 1, powerSlot.yPos() + 1);
        powerSlot.addChild(bgTexture);
        // Arrow; points to the right because x-size is negative
        GuiTexture toggleTex = new GuiTexture(-14, 14, BCSprites.get("item_charge/right_discharge"));
        toggleTex.setPos(powerSlot.xPos() + 18, powerSlot.yPos() - toggleTex.ySize() - 1);
        powerSlot.addChild(toggleTex);

        // TODO Werechang: BrandonsCore sprite (slots/core)
        GuiElement core = toolkit.createSlots(template.background, 1, 1, 0);
        core.setPos(guiLeft() + 16, guiTop() + 206);

        GuiElement craftingMatrix = toolkit.createSlots(template.background, 3, 3, 0);
        craftingMatrix.setPos(guiLeft() + 333, guiTop() + 206);

        GuiElement craftingResult = toolkit.createSlots(template.background, 1, 1, 0);
        craftingResult.setPos(guiLeft() + 427, guiTop() + 224);
    }
}
