package com.brandon3055.draconicevolution.client.gui;

import com.brandon3055.brandonscore.client.BCSprites;
import com.brandon3055.brandonscore.client.gui.GuiToolkit;
import com.brandon3055.brandonscore.client.gui.modulargui.GuiElement;
import com.brandon3055.brandonscore.client.gui.modulargui.GuiElementManager;
import com.brandon3055.brandonscore.client.gui.modulargui.ModularGuiContainer;
import com.brandon3055.brandonscore.client.gui.modulargui.templates.TBasicMachine;
import com.brandon3055.brandonscore.inventory.ContainerBCTile;
import com.brandon3055.draconicevolution.blocks.tileentity.TileDraconiumChest;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.text.ITextComponent;

import static com.brandon3055.brandonscore.inventory.ContainerSlotLayout.SlotType.TILE_INV;

public class GuiDraconiumChest extends ModularGuiContainer<ContainerBCTile<TileDraconiumChest>> {

    private PlayerInventory playerInventory;
    private TileDraconiumChest tileDraconiumChest;

    protected GuiToolkit<GuiDraconiumChest> toolkit = new GuiToolkit<>(this, GuiToolkit.GuiLayout.EXTRA_WIDE_EXTRA_TALL).setTranslationPrefix("gui.draconicevolution.draconium_chest");

    public GuiDraconiumChest(ContainerBCTile<TileDraconiumChest> container, PlayerInventory inv, ITextComponent titleIn) {
        super(container, inv, titleIn);
        this.playerInventory = inv;
        this.tileDraconiumChest = container.tile;
    }

    @Override
    public void addElements(GuiElementManager manager) {
        TBasicMachine template = toolkit.loadTemplate(new TBasicMachine(this, tileDraconiumChest));

        //>GuiElement storageSlots = toolkit.createSlots(template.background, 26, 10, 0, (x, y) -> container.getSlotLayout().getSlotData(TILE_INV, x), BCSprites.get("slot"));
    }
}
