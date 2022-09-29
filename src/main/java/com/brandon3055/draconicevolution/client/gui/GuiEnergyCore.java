package com.brandon3055.draconicevolution.client.gui;

import com.brandon3055.brandonscore.client.BCGuiSprites;
import com.brandon3055.brandonscore.client.gui.GuiToolkit;
import com.brandon3055.brandonscore.client.gui.modulargui.GuiElementManager;
import com.brandon3055.brandonscore.client.gui.modulargui.ModularGuiContainer;
import com.brandon3055.brandonscore.client.gui.modulargui.baseelements.GuiButton;
import com.brandon3055.brandonscore.client.gui.modulargui.guielements.GuiTexture;
import com.brandon3055.brandonscore.client.gui.modulargui.templates.TBasicMachine;
import com.brandon3055.brandonscore.inventory.ContainerBCTile;
import com.brandon3055.draconicevolution.blocks.tileentity.TileEnergyCore;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;

/**
 * Created by brandon3055 on 7/4/2016.
 */
public class GuiEnergyCore extends ModularGuiContainer<ContainerBCTile<TileEnergyCore>> {
    private GuiToolkit<GuiEnergyCore> toolkit = new GuiToolkit<>(this, 180, 200).setTranslationPrefix("gui.draconicevolution.energy_core");

    public Player player;
    public TileEnergyCore tile;

    public GuiEnergyCore(ContainerBCTile<TileEnergyCore> container, Inventory playerInventory, Component title) {
        super(container, playerInventory, title);
        this.tile = container.tile;
        this.player = playerInventory.player;

        /*
        * Plans
        * Overhaul the structure system and maybe make it data driven
        * Try to update and improve the rendering. Maybe test out some transparency alternatives?
        * Ether make this a basic screen or add charging slots
        *
        * */


//        Ok. So.
//                I'm thinking the re write will be driven by the gui.
//            So implement features in the Gui then actualloy add their associated functionality.
        /*
        * Next Steps...
        * - Re implement tier selection
        * - Structure validation
        * - Renderer that can handle the build guide and invalid block display
        * - Implement build guide
        * - Implement auto build function, Maybe some generic system that can work with the new structure system?
        * - next steps...
        * */
    }

    @Override
    public void addElements(GuiElementManager manager) {
        TBasicMachine temp = new TBasicMachine(this, tile);
        temp.background = GuiTexture.newDynamicTexture(xSize(), ySize(), () -> BCGuiSprites.getThemed("background_dynamic"));
        temp.background.onReload(guiTex -> guiTex.setPos(guiLeft(), guiTop()));
        toolkit.loadTemplate(temp);
        temp.title.setDisplaySupplier(() -> toolkit.i18n("title", tile.tier.get()));

        GuiButton activate = toolkit.createButton(() -> tile.active.get() ? "deactivate" : "activate", temp.background)
                .setSize(temp.playerSlots.xSize(), 14)
                .setEnabledCallback(() -> tile.active.get() || tile.isStructureValid())
                .onPressed(() -> tile.sendPacketToServer(e -> {}, TileEnergyCore.MSG_TOGGLE_ACTIVATION));
        toolkit.placeOutside(activate, temp.playerSlots, GuiToolkit.LayoutPos.TOP_CENTER, 0, -3);

        GuiButton tierDown = toolkit.createButton("tier_down", temp.background)
                .setSize((temp.playerSlots.xSize() / 2) - 1, 14)
                .setXPos(activate.xPos())
                .setMaxYPos(activate.yPos() - 1, false)
                .setEnabledCallback(() -> !tile.active.get())
                .setDisabledStateSupplier(() -> tile.tier.get() <= 1)
                .onPressed(() -> tile.tier.dec());

        GuiButton tierUp = toolkit.createButton("tier_up", temp.background)
                .setSize((temp.playerSlots.xSize() / 2) - 1, 14)
                .setMaxXPos(activate.maxXPos(), false)
                .setMaxYPos(activate.yPos() - 1, false)
                .setEnabledCallback(() -> !tile.active.get())
                .setDisabledStateSupplier(() -> tile.tier.get() >= TileEnergyCore.MAX_TIER)
                .onPressed(() -> tile.tier.inc());

        GuiButton buildGuide = toolkit.createButton("build_guide", temp.background)
                .setToggleStateSupplier(() -> tile.buildGuide.get())
                .onPressed(() -> tile.buildGuide.invert())
                .setEnabledCallback(() -> !tile.active.get())
                .setSize(temp.playerSlots.xSize(), 14)
                .setXPos(tierDown.xPos())
                .setMaxYPos(tierDown.yPos() - 1, false);

        GuiButton assemble = toolkit.createButton("assemble", temp.background)
                .setPosAndSize(activate)
                .setEnabledCallback(() -> !activate.isEnabled())
                .onPressed(() -> tile.sendPacketToServer(e -> {}, TileEnergyCore.MSG_BUILD_CORE));
    }



//    @Override
//    public void init(Minecraft mc, int width, int height) {
//        super.init(mc, width, height);
//
//        addButton(activate = new GuiButtonAHeight(leftPos + 9, topPos + 99, 162, 12, "Activate-L", (b) -> tile.sendPacketToServer(output -> {}, 0)));
//        addButton(tierUp = new GuiButtonAHeight(leftPos + 91, topPos + 86, 80, 12, I18n.get("button.de.tierUp.txt"), (b) -> tile.sendPacketToServer(output -> {}, 1)));
//        addButton(tierDown = new GuiButtonAHeight(leftPos + 9, topPos + 86, 80, 12, I18n.get("button.de.tierDown.txt"), (b) -> tile.sendPacketToServer(output -> {}, 2)));
//        addButton(toggleGuide = new GuiButtonAHeight(leftPos + 9, topPos + 73, 162, 12, I18n.get("button.de.buildGuide.txt"), (b) -> tile.sendPacketToServer(output -> {}, 3)));
//        addButton(assembleCore = new GuiButtonAHeight(leftPos + 9, topPos + 99, 162, 12, I18n.get("button.de.assembleCore.txt"), (b) -> tile.sendPacketToServer(output -> {}, 4)));
//
//        addButton(layerMinus = new GuiButtonAHeight(leftPos + 5, topPos - 13, 70, 12, "Layer-", (b) -> layer(-1)));
//        addButton(layerPlus = new GuiButtonAHeight(leftPos + 105, topPos - 13, 70, 12, "Layer+", (b) -> layer(1)));
//        layerPlus.visible = tile.buildGuide.get();
//        layerMinus.visible = tile.buildGuide.get();
//
//        updateButtonStates();
//    }

//    @Override
//    protected void renderBg(PoseStack mStack, float partialTicks, int mouseX, int mouseY) {
//        GuiHelperOld.drawGuiBaseBackground(this, leftPos, topPos, imageWidth, imageHeight);
//        GuiHelperOld.drawPlayerSlots(this, leftPos + (imageWidth / 2), topPos + 115, true);
//        drawCenteredString(mStack, font, I18n.get("gui.de.energyStorageCore.name", tile.tier.get()), leftPos + (imageWidth / 2), topPos + 5, InfoHelper.GUI_TITLE);
//
//        if (tile.active.get()) {
//            GuiHelperOld.drawCenteredString(font, I18n.get("gui.de.capacity.txt"), leftPos + imageWidth / 2, topPos + 16, 0xFFAA00, true);
//            String capText = tile.tier.get() == 8 ? I18n.get("gui.de.almostInfinite.txt") : Utils.formatNumber(tile.getExtendedCapacity());
//            GuiHelperOld.drawCenteredString(font, capText, leftPos + imageWidth / 2, topPos + 27, 0x555555, false);
//
//            DecimalFormat energyValue = new DecimalFormat("###.###");
//            double percent = (double) tile.getExtendedStorage() / (double) tile.getExtendedCapacity() * 100D;
//            GuiHelperOld.drawCenteredString(font, I18n.get("info.bc.charge.txt"), leftPos + imageWidth / 2, topPos + 38, 0xFFAA00, true);
//            GuiHelperOld.drawCenteredString(font, Utils.formatNumber(tile.getExtendedStorage()) + " OP [" + energyValue.format(percent) + "%]", leftPos + imageWidth / 2, topPos + 49, 0x555555, false);
//
//            int transferColour = tile.transferRate.get() > 0 ? 0x00FF00 : tile.transferRate.get() < 0 ? 0xFF0000 : 0x222222;
//            String transfer = (tile.transferRate.get() > 0 ? "+" : tile.transferRate.get() < 0 ? "-" : "") + Utils.formatNumber(Math.abs(tile.transferRate.get())) + " OP/t";
//            GuiHelperOld.drawCenteredString(font, I18n.get("gui.de.transfer.txt"), leftPos + imageWidth / 2, topPos + 59, 0xFFAA00, true);
//            GuiHelperOld.drawCenteredString(font, transfer, leftPos + imageWidth / 2, topPos + 70, transferColour, tile.transferRate.get() > 0);
//
//
//            if (tile.transferRate.get() != 0) {
//                String time = "";
//                if (years > 0) {
//                    time += formatYear(years) + ", ";
//                    time += days % 365 + " Days";
//                } else if (days > 0) {
//                    time += days % 365 + " Days, ";
//                    time += (hours % 24 < 10 ? "0" : "") + hours % 24 + ":";
//                    time += (minutes % 60 < 10 ? "0" : "") + minutes % 60 + ":";
//                    time += (seconds % 60 < 10 ? "0" : "") + seconds % 60 + "." + (ticks % 20 < 10 ? "0" : "") + ticks % 20;
//                } else {
//                    time += (hours % 24 < 10 ? "0" : "") + hours % 24 + ":";
//                    time += (minutes % 60 < 10 ? "0" : "") + minutes % 60 + ":";
//                    time += (seconds % 60 < 10 ? "0" : "") + seconds % 60 + "." + (ticks % 20 < 10 ? "0" : "") + ticks % 20;
//                }
//
//                GuiHelperOld.drawCenteredString(font, time, leftPos + imageWidth / 2, topPos + 70 + 10, 0x555555, false);
//            }
//        } else {
//            int stabColour = tile.stabilizersOK.get() ? 0x00FF00 : 0xFF0000;
//            String stabText = I18n.get("gui.de.stabilizers.txt") + ": " + (tile.stabilizersOK.get() ? I18n.get("gui.de.valid.txt") : I18n.get("gui.de.invalid.txt"));
//            GuiHelperOld.drawCenteredString(font, stabText, leftPos + imageWidth / 2, topPos + 18, stabColour, tile.stabilizersOK.get());
//            if (tile.tier.get() >= 5) {
//                GuiHelperOld.drawCenteredString(font, I18n.get("gui.de.advancedStabilizersRequired.txt"), leftPos + imageWidth / 2, topPos + 28, 0x777777, false);
//            }
//
//            int coreColour = tile.coreValid.get() ? 0x00FF00 : 0xFF0000;
//            String coreText = I18n.get("gui.de.core.txt") + ": " + (tile.coreValid.get() ? I18n.get("gui.de.valid.txt") : I18n.get("gui.de.invalid.txt"));
//            GuiHelperOld.drawCenteredString(font, coreText, leftPos + imageWidth / 2, topPos + 36, coreColour, tile.coreValid.get());
//            if (!tile.coreValid.get()) {
////                GuiHelper.drawCenteredSplitString(font, tile.invalidMessage.get(), guiLeft + xSize / 2, guiTop + 46, 180, coreColour, tile.coreValid.get());
//            }
//        }
//    }

//    @Override
//    public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTicks) {
//        this.renderBackground(poseStack);
//        super.render(poseStack, mouseX, mouseY, partialTicks);
//
//        if (tile.active.get()) {
////            GuiHelper.drawEnergyBar(this, guiLeft + 5, guiTop + 82, 170, true, tile.getExtendedStorage(), tile.getExtendedCapacity(), true, mouseX, mouseY);
//
//            if (GuiHelperOld.isInRect(leftPos + 40, topPos + 27, imageWidth - 80, 8, mouseX, mouseY)) {
//                renderTooltip(poseStack, new TextComponent(ChatFormatting.GRAY + "[" + Utils.addCommas(tile.getExtendedCapacity()) + " OP]"), mouseX, mouseY);
//            }
//
//            if (GuiHelperOld.isInRect(leftPos + 40, topPos + 48, imageWidth - 80, 8, mouseX, mouseY)) {
//                renderTooltip(poseStack, new TextComponent(ChatFormatting.GRAY + "[" + Utils.addCommas(tile.getExtendedStorage()) + " OP]"), mouseX, mouseY);
//            }
//        }
//
//        if (tile.buildGuide.get()) {
//            drawCenteredString(poseStack, font, layer == -1 ? "All" : layer + "", leftPos + (imageWidth / 2), topPos - 10, 0xFFFFFF);
//        }
//
//        if (GuiHelper.isInRect(guiLeft(), guiTop() + 59, xSize(), 24, mouseX, mouseY) && tile.active.get()){
//            MutableComponent input = new TextComponent("IN: ").withStyle(ChatFormatting.GREEN).append(new TextComponent(Utils.formatNumber(Math.round(tile.inputRate.get())) + " OP/t").withStyle(ChatFormatting.GRAY));
//            MutableComponent out = new TextComponent("OUT: ").withStyle(ChatFormatting.DARK_RED).append(new TextComponent(Utils.formatNumber(Math.round(tile.outputRate.get())) + " OP/t").withStyle(ChatFormatting.GRAY));
//
//            renderTooltip(poseStack, Lists.newArrayList(input.getVisualOrderText(), out.getVisualOrderText()), mouseX, mouseY);
//        } else {
//            this.renderTooltip(poseStack, mouseX, mouseY);
//        }
//
//    }

//    @Override
//    public void tick() {
//        super.tick();
//        updateButtonStates();
//
//        if (tile.transferRate.get() != 0) {
//            long space = tile.transferRate.get() > 0 ? tile.getExtendedCapacity() - tile.getExtendedStorage() : tile.getExtendedStorage();
//            ticks = Math.abs(space / tile.transferRate.get());
//        }
//        else {
//            ticks = 0;
//        }
//        seconds = ticks / 20L;
//        minutes = seconds / 60L;
//        hours = minutes / 60L;
//        days = hours / 24L;
//        years = days / 365L;
//    }

//    private void updateButtonStates() {
//        if (tile.active.get()) {
//            activate.setMessage(new TranslatableComponent("button.de.deactivate.txt"));
//        } else {
//            activate.setMessage(new TranslatableComponent("button.de.activate.txt"));
//            toggleGuide.setMessage(new TextComponent(I18n.get("button.de.buildGuide.txt") + " " + (tile.buildGuide.get() ? I18n.get("gui.de.active.txt") : I18n.get("gui.de.inactive.txt"))));
//            tierUp.active = tile.tier.get() < 8;
//            tierDown.active = tile.tier.get() > 1;
//        }
//
//
//        tierUp.visible = tierDown.visible = toggleGuide.visible = !tile.active.get();
//        assembleCore.visible = !tile.coreValid.get();
//        activate.visible = tile.coreValid.get();
//
//        layerPlus.visible = tile.buildGuide.get();
//        layerMinus.visible = tile.buildGuide.get();
//    }
//
//    public static String formatYear(long value) {
//        if (value < 1000L) return value + " Years";
//        else if (value < 1000000L) return Math.round(value / 10D) / 100D + " Thousand Years";
//        else if (value < 1000000000L) return Math.round(value / 10000D) / 100D + " Million Years";
//        else if (value < 1000000000000L) return Math.round(value / 10000000D) / 100D + " Billion Years";
//        else if (value < 1000000000000000L) return Math.round(value / 10000000000D) / 100D + " Trillion Years";
//        else if (value < 1000000000000000000L) return Math.round(value / 10000000000000D) / 100D + " Quadrillion Years";
//        else if (value <= Long.MAX_VALUE) return Math.round(value / 10000000000000000D) / 100D + " Quintillion Years";
//        else return "Something is very broken!!!!";
//    }
//
//    protected void layer(int add) {
//        layer = MathHelper.clip(layer + add, -1, 6);
//    }
}
