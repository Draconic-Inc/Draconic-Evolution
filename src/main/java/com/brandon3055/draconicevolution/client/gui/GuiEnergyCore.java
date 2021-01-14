package com.brandon3055.draconicevolution.client.gui;

import codechicken.lib.math.MathHelper;
import com.brandon3055.brandonscore.client.gui.GuiButtonAHeight;
import com.brandon3055.brandonscore.client.gui.modulargui.GuiElementManager;
import com.brandon3055.brandonscore.client.gui.modulargui.ModularGuiContainer;
import com.brandon3055.brandonscore.client.utils.GuiHelper;
import com.brandon3055.brandonscore.inventory.ContainerBCTile;
import com.brandon3055.brandonscore.inventory.ContainerBCore;
import com.brandon3055.brandonscore.lib.datamanager.ManagedStack;
import com.brandon3055.brandonscore.utils.InfoHelper;
import com.brandon3055.brandonscore.utils.Utils;
import com.brandon3055.draconicevolution.blocks.tileentity.TileEnergyCore;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.IReorderingProcessor;
import net.minecraft.util.Util;
import net.minecraft.util.text.*;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by brandon3055 on 7/4/2016.
 */
public class GuiEnergyCore extends ModularGuiContainer<ContainerBCTile<TileEnergyCore>> {

    public PlayerEntity player;
    public TileEnergyCore tile;
    private Button activate;
    private Button tierUp;
    private Button tierDown;
    private Button toggleGuide;
    private Button assembleCore;
    private Button layerPlus;
    private Button layerMinus;
    public static int layer = -1;

    public GuiEnergyCore(ContainerBCTile<TileEnergyCore> container, PlayerInventory playerInventory, ITextComponent title) {
        super(container, playerInventory, title);
        this.tile = container.tile;
        this.xSize = 180;
        this.ySize = 200;
        this.player = playerInventory.player;
        dumbGui = true;
    }

    @Override
    public void addElements(GuiElementManager manager) {
        //TODO
        container.inventorySlots.forEach(slot -> {
            if (slot.slotNumber < 9) {
                slot.xPos += 10;
                slot.yPos += 174;
            }
            else {
                slot.xPos += 10;
                slot.yPos += 98;
            }
        });
    }


    @Override
    public void init(Minecraft mc, int width, int height) {
        super.init(mc, width, height);

        addButton(activate = new GuiButtonAHeight(guiLeft + 9, guiTop + 99, 162, 12, "Activate-L", (b) -> tile.sendPacketToServer(output -> {}, 0)));
        addButton(tierUp = new GuiButtonAHeight(guiLeft + 91, guiTop + 86, 80, 12, I18n.format("button.de.tierUp.txt"), (b) -> tile.sendPacketToServer(output -> {}, 1)));
        addButton(tierDown = new GuiButtonAHeight(guiLeft + 9, guiTop + 86, 80, 12, I18n.format("button.de.tierDown.txt"), (b) -> tile.sendPacketToServer(output -> {}, 2)));
        addButton(toggleGuide = new GuiButtonAHeight(guiLeft + 9, guiTop + 73, 162, 12, I18n.format("button.de.buildGuide.txt"), (b) -> tile.sendPacketToServer(output -> {}, 3)));
        addButton(assembleCore = new GuiButtonAHeight(guiLeft + 9, guiTop + 99, 162, 12, I18n.format("button.de.assembleCore.txt"), (b) -> tile.sendPacketToServer(output -> {}, 4)));

        addButton(layerMinus = new GuiButtonAHeight(guiLeft + 5, guiTop - 13, 70, 12, "Layer-", (b) -> layer(-1)));
        addButton(layerPlus = new GuiButtonAHeight(guiLeft + 105, guiTop - 13, 70, 12, "Layer+", (b) -> layer(1)));
        layerPlus.visible = tile.buildGuide.get();
        layerMinus.visible = tile.buildGuide.get();

        updateButtonStates();
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(MatrixStack mStack, float partialTicks, int mouseX, int mouseY) {
        GuiHelper.drawGuiBaseBackground(this, guiLeft, guiTop, xSize, ySize);
        GuiHelper.drawPlayerSlots(this, guiLeft + (xSize / 2), guiTop + 115, true);
        drawCenteredString(mStack, font, I18n.format("gui.de.energyStorageCore.name", tile.tier.get()), guiLeft + (xSize / 2), guiTop + 5, InfoHelper.GUI_TITLE);

        if (tile.active.get()) {
            GuiHelper.drawCenteredString(font, I18n.format("gui.de.capacity.txt"), guiLeft + xSize / 2, guiTop + 16, 0xFFAA00, true);
            String capText = tile.tier.get() == 8 ? I18n.format("gui.de.almostInfinite.txt") : Utils.formatNumber(tile.getExtendedCapacity());
            GuiHelper.drawCenteredString(font, capText, guiLeft + xSize / 2, guiTop + 27, 0x555555, false);

            DecimalFormat energyValue = new DecimalFormat("###.###");
            double percent = (double) tile.getExtendedStorage() / (double) tile.getExtendedCapacity() * 100D;
            GuiHelper.drawCenteredString(font, I18n.format("info.bc.charge.txt"), guiLeft + xSize / 2, guiTop + 38, 0xFFAA00, true);
            GuiHelper.drawCenteredString(font, Utils.formatNumber(tile.getExtendedStorage()) + " OP [" + energyValue.format(percent) + "%]", guiLeft + xSize / 2, guiTop + 49, 0x555555, false);

            int coreColour = tile.transferRate.get() > 0 ? 0x00FF00 : tile.transferRate.get() < 0 ? 0xFF0000 : 0x222222;
            String transfer = (tile.transferRate.get() > 0 ? "+" : tile.transferRate.get() < 0 ? "-" : "") + Utils.formatNumber(Math.abs(tile.transferRate.get())) + " OP/t";
            GuiHelper.drawCenteredString(font, I18n.format("gui.de.transfer.txt"), guiLeft + xSize / 2, guiTop + 59, 0xFFAA00, true);
            GuiHelper.drawCenteredString(font, transfer, guiLeft + xSize / 2, guiTop + 70, coreColour, tile.transferRate.get() > 0);
        } else {
            int stabColour = tile.stabilizersOK.get() ? 0x00FF00 : 0xFF0000;
            String stabText = I18n.format("gui.de.stabilizers.txt") + ": " + (tile.stabilizersOK.get() ? I18n.format("gui.de.valid.txt") : I18n.format("gui.de.invalid.txt"));
            GuiHelper.drawCenteredString(font, stabText, guiLeft + xSize / 2, guiTop + 18, stabColour, tile.stabilizersOK.get());
            if (tile.tier.get() >= 5) {
                GuiHelper.drawCenteredString(font, I18n.format("gui.de.advancedStabilizersRequired.txt"), guiLeft + xSize / 2, guiTop + 28, 0x777777, false);
            }

            int coreColour = tile.coreValid.get() ? 0x00FF00 : 0xFF0000;
            String coreText = I18n.format("gui.de.core.txt") + ": " + (tile.coreValid.get() ? I18n.format("gui.de.valid.txt") : I18n.format("gui.de.invalid.txt"));
            GuiHelper.drawCenteredString(font, coreText, guiLeft + xSize / 2, guiTop + 36, coreColour, tile.coreValid.get());
            if (!tile.coreValid.get()) {
//                GuiHelper.drawCenteredSplitString(font, tile.invalidMessage.get(), guiLeft + xSize / 2, guiTop + 46, 180, coreColour, tile.coreValid.get());
            }
        }
    }

    @Override
    public void render(MatrixStack mStack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(mStack);
        super.render(mStack, mouseX, mouseY, partialTicks);

        if (tile.active.get()) {
//            GuiHelper.drawEnergyBar(this, guiLeft + 5, guiTop + 82, 170, true, tile.getExtendedStorage(), tile.getExtendedCapacity(), true, mouseX, mouseY);

            if (GuiHelper.isInRect(guiLeft + 40, guiTop + 27, xSize - 80, 8, mouseX, mouseY)) {
                renderTooltip(mStack, new StringTextComponent(TextFormatting.GRAY + "[" + Utils.addCommas(tile.getExtendedCapacity()) + " OP]"), mouseX, mouseY);
            }

            if (GuiHelper.isInRect(guiLeft + 40, guiTop + 48, xSize - 80, 8, mouseX, mouseY)) {
                renderTooltip(mStack, new StringTextComponent(TextFormatting.GRAY + "[" + Utils.addCommas(tile.getExtendedStorage()) + " OP]"), mouseX, mouseY);
            }
        }

        if (tile.buildGuide.get()) {
            drawCenteredString(mStack, font, layer == -1 ? "All" : layer + "", guiLeft + (xSize / 2), guiTop - 10, 0xFFFFFF);
        }

        this.renderHoveredTooltip(mStack, mouseX, mouseY);
    }

    @Override
    public void tick() {
        super.tick();
        updateButtonStates();
    }

    private void updateButtonStates() {
        if (tile.active.get()) {
            activate.setMessage(new TranslationTextComponent("button.de.deactivate.txt"));
        } else {
            activate.setMessage(new TranslationTextComponent("button.de.activate.txt"));
            toggleGuide.setMessage(new StringTextComponent(I18n.format("button.de.buildGuide.txt") + " " + (tile.buildGuide.get() ? I18n.format("gui.de.active.txt") : I18n.format("gui.de.inactive.txt"))));
            tierUp.active = tile.tier.get() < 8;
            tierDown.active = tile.tier.get() > 1;
        }


        tierUp.visible = tierDown.visible = toggleGuide.visible = !tile.active.get();
        assembleCore.visible = !tile.coreValid.get();
        activate.visible = tile.coreValid.get();

        layerPlus.visible = tile.buildGuide.get();
        layerMinus.visible = tile.buildGuide.get();
    }

    protected void layer(int add) {
        layer = MathHelper.clip(layer + add, -1, 6);
    }
}
