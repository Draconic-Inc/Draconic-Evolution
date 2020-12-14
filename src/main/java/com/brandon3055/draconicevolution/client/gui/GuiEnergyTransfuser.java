package com.brandon3055.draconicevolution.client.gui;

import com.brandon3055.brandonscore.capability.CapabilityOP;
import com.brandon3055.brandonscore.client.BCSprites;
import com.brandon3055.brandonscore.client.gui.GuiToolkit;
import com.brandon3055.brandonscore.client.gui.GuiToolkit.Palette;
import com.brandon3055.brandonscore.client.gui.modulargui.GuiElement;
import com.brandon3055.brandonscore.client.gui.modulargui.GuiElementManager;
import com.brandon3055.brandonscore.client.gui.modulargui.ModularGuiContainer;
import com.brandon3055.brandonscore.client.gui.modulargui.baseelements.GuiButton;
import com.brandon3055.brandonscore.client.gui.modulargui.guielements.GuiBorderedRect;
import com.brandon3055.brandonscore.client.gui.modulargui.guielements.GuiEnergyBar;
import com.brandon3055.brandonscore.client.gui.modulargui.guielements.GuiTexture;
import com.brandon3055.brandonscore.client.gui.modulargui.templates.TBasicMachine;
import com.brandon3055.brandonscore.client.gui.modulargui.templates.TGuiBase;
import com.brandon3055.brandonscore.inventory.ContainerBCTile;
import com.brandon3055.brandonscore.utils.EnergyUtils;
import com.brandon3055.draconicevolution.blocks.tileentity.TileEnergyTransfuser;
import com.brandon3055.draconicevolution.client.DESprites;
import com.brandon3055.draconicevolution.client.render.tile.RenderTileEnergyTransfuser;
import com.brandon3055.draconicevolution.inventory.GuiLayoutFactories;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.text.ITextComponent;

import java.awt.*;

import static com.brandon3055.brandonscore.client.gui.GuiToolkit.GuiLayout.*;
import static com.brandon3055.brandonscore.client.gui.GuiToolkit.Palette.*;
import static com.brandon3055.brandonscore.inventory.ContainerSlotLayout.SlotType.TILE_INV;

/**
 * Created by brandon3055 on 12/12/2020.
 */
public class GuiEnergyTransfuser extends ModularGuiContainer<ContainerBCTile<TileEnergyTransfuser>> {

    public PlayerEntity player;
    private TileEnergyTransfuser tile;

    protected GuiToolkit<GuiEnergyTransfuser> toolkit = new GuiToolkit<>(this, 218, 215).setTranslationPrefix("gui.draconicevolution.transfuser");

    public GuiEnergyTransfuser(ContainerBCTile<TileEnergyTransfuser> container, PlayerInventory inv, ITextComponent title) {
        super(container, inv, title);
        this.tile = container.tile;
        this.player = playerInventory.player;
    }

    @Override
    public void addElements(GuiElementManager manager) {
        TBasicMachine temp = new TBasicMachine(this, tile, false);
        temp.background = GuiTexture.newDynamicTexture(xSize(), ySize(), () -> BCSprites.getThemed("background_dynamic"));
        temp.background.onReload(guiTex -> guiTex.setPos(guiLeft(), guiTop()));
        toolkit.loadTemplate(temp);

        temp.addPlayerSlots(true, true, true);

        int padding = 3;
        int ySpace = (temp.playerSlots.yPos() - temp.title.maxYPos()) - (padding * 2);
        int slotWidth = 24;
        int barWidth = 18;
        int totalWidth = 18 * 9;

        GuiElement<?> holder = new GuiElement<>();
        int xOffset = 0;
        for (int i = 0; i < 4; i++) {
            int fi = i;
            GuiBorderedRect column = new GuiBorderedRect()
                    .set3DGetters(() -> Ctrl.fill(false), () -> Ctrl.accentLight(false), () -> Ctrl.accentDark(false))
                    .setSize(slotWidth, ySpace)
                    .setXPos(xOffset);

            GuiElement<?> slot = toolkit.createSlots(column, 1, 1, 10, (col, row) -> container.getSlotLayout().getSlotData(TILE_INV, fi), null)
                    .setXPos(xOffset + (slotWidth - 18) / 2)
                    .setMaxYPos(column.maxYPos() - (slotWidth - 18) / 2, false);

            GuiButton button = toolkit.createButton("", column, true, 0)
                    .setSize(16, 16)
                    .setXPos(xOffset + (slotWidth - 16) / 2)
                    .setMaxYPos(slot.yPos() - 1, false)
                    .setHoverText(e -> toolkit.i18n(tile.ioModes[fi].get().getName()))
                    .onButtonPressed(btn -> tile.ioModes[fi].set(tile.ioModes[fi].get().nextMode(Screen.hasShiftDown() == (btn == 0))));

            button.addChild(new GuiTexture(16, 16, () -> DESprites.get(tile.ioModes[fi].get().getSpriteName()))
                    .setRelPos(0, 0));

            toolkit.createEnergyBar(column)
                    .setItemSupplier(() -> tile.itemsCombined.getStackInSlot(fi))
                    .setDisabled(() -> !EnergyUtils.isEnergyItem(tile.itemsCombined.getStackInSlot(fi)))
                    .setShaderEnabled(() -> tile.itemsCombined.getStackInSlot(fi).getCapability(CapabilityOP.OP).isPresent())
                    .setDrawHoveringText(() -> EnergyUtils.isEnergyItem(tile.itemsCombined.getStackInSlot(fi)))
                    .setXPos(xOffset + (slotWidth - barWidth) / 2)
                    .setYPos(column.yPos() + (slotWidth - barWidth) / 2)
                    .setXSize(barWidth)
                    .setMaxYPos(button.yPos() - 1, true);

            column.addChild(new GuiElement(){
                @Override
                public void renderElement(Minecraft minecraft, int mouseX, int mouseY, float partialTicks) {
                    MatrixStack mStack = new MatrixStack();
                    mStack.translate(xPos() + (fi == 0 ? 5 : fi == 2 ? 7 : 4)  , yPos() + 2, 0);
                    mStack.scale(2, 2, 2);
                    fontRenderer.func_238422_b_(mStack, RenderTileEnergyTransfuser.TEXT[fi].func_241878_f(), 0, 0, tile.ioModes[fi].get().getColour());
                }
            }.setPosAndSize(slot));

            holder.addChild(column);
            xOffset += slotWidth + (totalWidth - (slotWidth * 4)) / 3;
        }
        Rectangle rect = holder.getEnclosingRect();
        holder.setRawPos(rect.x, rect.y);
        holder.setSize(rect);
        temp.background.addChild(holder);
        toolkit.placeInside(holder, temp.background, GuiToolkit.LayoutPos.TOP_CENTER, 0, 0);
        holder.setYPos(temp.title.maxYPos() + padding);

        GuiButton button = toolkit.createButton("", temp.background, true, 0)
                .setSize(20, 20)
                .setMaxXPos(holder.xPos() - 2, false)
                .setMaxYPos(holder.maxYPos(), false)
                .setHoverText(e -> toolkit.i18n(tile.balancedMode.get() ? "balanced_charge" : "sequential_charge"))
                .onPressed(() -> tile.balancedMode.invert());

        button.addChild(new GuiTexture(18, 18, () -> DESprites.get("transfuser/" + (tile.balancedMode.get() ? "balanced_charge" : "sequential_charge")))
                .setRelPos(1, 1));
    }
}


/* I/O modes.
Input       Charge Item
Output      Discharge Item
Buffer      Item is a buffer
*/