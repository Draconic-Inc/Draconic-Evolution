package com.brandon3055.draconicevolution.client.gui;

import com.brandon3055.brandonscore.BCConfig;
import com.brandon3055.brandonscore.client.BCSprites;
import com.brandon3055.brandonscore.client.gui.GuiToolkit;
import com.brandon3055.brandonscore.client.gui.modulargui.*;
import com.brandon3055.brandonscore.client.gui.modulargui.baseelements.GuiButton;
import com.brandon3055.brandonscore.client.gui.modulargui.guielements.GuiEnergyBar;
import com.brandon3055.brandonscore.client.gui.modulargui.guielements.GuiSlotRender;
import com.brandon3055.brandonscore.client.gui.modulargui.guielements.GuiTexture;
import com.brandon3055.brandonscore.client.gui.modulargui.templates.TGuiBase;
import com.brandon3055.draconicevolution.DraconicEvolution;
import com.brandon3055.draconicevolution.blocks.tileentity.TileDraconiumChest;
import com.brandon3055.draconicevolution.client.DESprites;
import com.brandon3055.draconicevolution.inventory.ContainerDraconiumChest;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;

import java.awt.*;

import static net.minecraft.util.text.TextFormatting.GOLD;
import static net.minecraft.util.text.TextFormatting.GRAY;

/**
 * Created by Werechang on 27/6/21
 */
public class GuiDraconiumChest extends ModularGuiContainer<ContainerDraconiumChest> {

    protected GuiToolkit<GuiDraconiumChest> toolkit = new GuiToolkit<>(this, 500, 288).setTranslationPrefix("gui.draconicevolution.draconium_chest");
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
        toolkit.createSlots(template.background, 26, 10, 0).setPos(guiLeft() + 16, guiTop() + 14);

        // Furnace
        toolkit.createSlots(template.background, 5, 1, 0).setPos(guiLeft() + 50, guiTop() + 206);
        createFurnaceFlames(template.background, guiLeft() + 51, guiTop() + 226, tile);

        // EnergyBar
        template.energyBar = toolkit.createEnergyBar(template.background, tile.opStorage)
                .setHorizontal(true)
                .setPos(guiLeft() + 37, guiTop() + 247)
                .setYSize(14)
                .setMaxXPos(toolkit.guiLeft() + 100, true)
                .setXSize(120);

        // Capacitor slot
        createEnergyElements(template.background, template.energyBar.xPos() - 21, template.energyBar.yPos() + 17);

        // Core field
        createCoreField(template.background, guiLeft()+22, guiTop()+206);

        // Crafting
        toolkit.createSlots(template.background, 3, 3, 0)
                .setPos(guiLeft() + 339, guiTop() + 206);
        createCraftingSlot(template.background, guiLeft() + 429, guiTop() + 220);
        createCraftingArrow(template.background, guiLeft() + 400, guiTop() + 225);

        //Redstone button
        toolkit.createRSSwitch(template.background, tile)
                .setPos(template.themeButton.xPos(), template.themeButton.yPos() + 24);

        // AutoFill Button
        createAutofillButton(template.background, tile, template.themeButton.xPos(), template.themeButton.yPos() + 36);

        // Info Panel
        template.infoPanel = toolkit.createInfoPanel(template.background, false);
        template.infoPanel.setOrigin(() -> new Point(template.themeButton.xPos(), template.themeButton.maxYPos()));
        template.infoPanel.setEnabled(false);

        template.infoPanel.addLabeledValue(GOLD + toolkit.i18n("smelt_energy"), 6, 11, () -> GRAY + (tile.smeltEnergyPerTick.get() + " OP/t"), true);
        //template.infoPanel.addLabeledValue(GOLD + toolkit.i18n("smelting_speed"), 6, 11, () -> GRAY + ((tile.burnRate.get() * 100) + " %"), true);
    }

    public static void createCraftingSlot(GuiElement parent, int xPos, int yPos) {
        GuiTexture field = new GuiTexture(26, 26, () -> DESprites.get("draconium_chest/crafting_field_" + (BCConfig.darkMode ? "dark" : "light"))).setPos(xPos, yPos);
        parent.addChild(field);
    }

    public static void createCraftingArrow(GuiElement parent, int xPos, int yPos) {
        GuiTexture arrow = new GuiTexture(22, 15, () -> DESprites.get("draconium_chest/crafting_arrow_" + (BCConfig.darkMode ? "dark" : "light"))).setPos(xPos, yPos);
        parent.addChild(arrow);
    }

    public static void createCoreField(GuiElement parent, int xPos, int yPos) {
        GuiSlotRender slot = new GuiSlotRender(xPos, yPos);
        parent.addChild(slot);
        GuiTexture core = new GuiTexture(16, 16, DESprites.get("draconium_chest/core")).setPos(xPos+1, yPos+1);
        slot.addChild(core);
    }

    public static void createEnergyElements(GuiElement parent, int xPos, int yPos) {
        GuiSlotRender powerSlot = new GuiSlotRender(xPos, yPos);
        parent.addChild(powerSlot);
        // Capacitor sprite
        GuiTexture capacitor = new GuiTexture(16, 16, BCSprites.get("slots/energy")).setPos(powerSlot.xPos() + 1, powerSlot.yPos() + 1);
        powerSlot.addChild(capacitor);
        // Arrow; points to the right because x-size is negative
        GuiTexture dischargeTex = new GuiTexture(-14, 14, BCSprites.get("item_charge/right_discharge"));
        dischargeTex.setPos(powerSlot.xPos() + 18, powerSlot.yPos() - dischargeTex.ySize() - 1);
        powerSlot.addChild(dischargeTex);
    }

    public static void createFurnaceFlames(GuiElement parent, int xPos, int yPos, TileDraconiumChest tile) {
        GuiTexture flames = new GuiTexture(88, 15, () -> DESprites.get("draconium_chest/furnace_" + (tile.isSmelting.get() ? "on" : "off"))).setPos(xPos, yPos);
        parent.addChild(flames);
    }

    public static void createAutofillButton(GuiElement parent, TileDraconiumChest tile, int xPos, int yPos) {
        GuiButton button = new GuiButton().setPos(xPos, yPos);
        button.setHoverTextDelay(10);
        button.setSize(12, 12);
        GuiTexture icon = new GuiTexture(12, 12, () -> DESprites.get("draconium_chest/autofill_" + tile.autoSmeltMode.get().toString().toLowerCase())).setPos(xPos, yPos);
        icon.setYPosMod(button::yPos);
        //button.setDisplaySupplier(() -> I18n.get(tile.autoSmeltMode.get().unlocalizedName()));
        button.setHoverText(element -> TextFormatting.WHITE + I18n.get(tile.autoSmeltMode.get().unlocalizedName() + ".info"));
        button.onButtonPressed((pressed) -> tile.setAutoSmeltMode(tile.autoSmeltMode.get().next(hasShiftDown() || pressed == 1)));
        button.addChild(icon);
        GuiToolkit.addHoverHighlight(button);
        parent.addChild(button);
    }
}
