package com.brandon3055.draconicevolution.client.gui;

import com.brandon3055.draconicevolution.blocks.tileentity.TileStorageCore;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.util.text.ITextComponent;

/**
 * Created by brandon3055 on 7/4/2016.
 */
public class GuiEnergyCore extends ContainerScreen {

    public PlayerEntity player;
    public TileStorageCore tile;
    private Button activate;
    private Button tierUp;
    private Button tierDown;
    private Button toggleGuide;
    private Button assembleCore;
    private Button layerPlus;
    private Button layerMinus;
    public static int layer = -1;

    public GuiEnergyCore(Container screenContainer, TileStorageCore tile, PlayerInventory inv, ITextComponent titleIn) {
        super(screenContainer, inv, titleIn);
        this.tile = tile;
        this.xSize = 180;
        this.ySize = 200;
        this.player = inv.player;
    }

    //    public GuiEnergyCore(PlayerEntity player, TileEnergyStorageCore tile) {
//        super(new ContainerBCBase<TileEnergyStorageCore>(player, tile).addPlayerSlots(10, 116));
//        this.tile = tile;
//        this.xSize = 180;
//        this.ySize = 200;
//        this.player = player;
//    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {

    }

    //    @Override
//    public void initGui() {
//        super.initGui();
//        buttonList.clear();
//        buttonList.add(activate = new GuiButtonAHeight(0, guiLeft + 9, guiTop + 99, 162, 12, "Activate-L"));
//        buttonList.add(tierUp = new GuiButtonAHeight(1, guiLeft + 91, guiTop + 86, 80, 12, I18n.format("button.de.tierUp.txt")));
//        buttonList.add(tierDown = new GuiButtonAHeight(2, guiLeft + 9, guiTop + 86, 80, 12, I18n.format("button.de.tierDown.txt")));
//        buttonList.add(toggleGuide = new GuiButtonAHeight(3, guiLeft + 9, guiTop + 73, 162, 12, I18n.format("button.de.buildGuide.txt")));
//        buttonList.add(assembleCore = new GuiButtonAHeight(4, guiLeft + 9, guiTop + 99, 162, 12, I18n.format("button.de.assembleCore.txt")));
//
//        buttonList.add(layerMinus = new GuiButtonAHeight(5, guiLeft + 5, guiTop - 13, 70, 12, "Layer-"));
//        buttonList.add(layerPlus = new GuiButtonAHeight(6, guiLeft + 105, guiTop - 13, 70, 12, "Layer+"));
//        layerPlus.visible = tile.buildGuide.get();
//        layerMinus.visible = tile.buildGuide.get();
//
//        updateButtonStates();
//    }
//
//    @Override
//    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
//        GuiHelper.drawGuiBaseBackground(this, guiLeft, guiTop, xSize, ySize);
//        GuiHelper.drawPlayerSlots(this, guiLeft + (xSize / 2), guiTop + 115, true);
//        drawCenteredString(fontRenderer, I18n.format("gui.de.energyStorageCore.name", tile.tier.toString()), guiLeft + (xSize / 2), guiTop + 5, InfoHelper.GUI_TITLE);
//
//        if (tile.active.get()) {
//            GuiHelper.drawCenteredString(fontRenderer, I18n.format("gui.de.capacity.txt"), guiLeft + xSize / 2, guiTop + 16, 0xFFAA00, true);
//            String capText = tile.tier.get() == 8 ? I18n.format("gui.de.almostInfinite.txt") : Utils.formatNumber(tile.getExtendedCapacity());
//            GuiHelper.drawCenteredString(fontRenderer, capText, guiLeft + xSize / 2, guiTop + 27, 0x555555, false);
//
//            DecimalFormat energyValue = new DecimalFormat("###.###");
//            double percent = (double) tile.getExtendedStorage() / (double) tile.getExtendedCapacity() * 100D;
//            GuiHelper.drawCenteredString(fontRenderer, I18n.format("info.bc.charge.txt"), guiLeft + xSize / 2, guiTop + 38, 0xFFAA00, true);
//            GuiHelper.drawCenteredString(fontRenderer, Utils.formatNumber(tile.getExtendedStorage()) + "RF [" + energyValue.format(percent) + "%]", guiLeft + xSize / 2, guiTop + 49, 0x555555, false);
//
//            int coreColour = tile.transferRate.get() > 0 ? 0x00FF00 : tile.transferRate.get() < 0 ? 0xFF0000 : 0x222222;
//            String transfer = (tile.transferRate.get() > 0 ? "+" : tile.transferRate.get() < 0 ? "-" : "") + Utils.formatNumber(Math.abs(tile.transferRate.get())) + " RF/t";
//            GuiHelper.drawCenteredString(fontRenderer, I18n.format("gui.de.transfer.txt"), guiLeft + xSize / 2, guiTop + 59, 0xFFAA00, true);
//            GuiHelper.drawCenteredString(fontRenderer, transfer, guiLeft + xSize / 2, guiTop + 70, coreColour, tile.transferRate.get() > 0);
//        }
//        else {
//            int stabColour = tile.stabilizersOK.get() ? 0x00FF00 : 0xFF0000;
//            String stabText = I18n.format("gui.de.stabilizers.txt") + ": " + (tile.stabilizersOK.get() ? I18n.format("gui.de.valid.txt") : I18n.format("gui.de.invalid.txt"));
//            GuiHelper.drawCenteredString(fontRenderer, stabText, guiLeft + xSize / 2, guiTop + 18, stabColour, tile.stabilizersOK.get());
//            if (tile.tier.get() >= 5) {
//                GuiHelper.drawCenteredString(fontRenderer, I18n.format("gui.de.advancedStabilizersRequired.txt"), guiLeft + xSize / 2, guiTop + 28, 0x777777, false);
//            }
//
//            int coreColour = tile.coreValid.get() ? 0x00FF00 : 0xFF0000;
//            String coreText = I18n.format("gui.de.core.txt") + ": " + (tile.coreValid.get() ? I18n.format("gui.de.valid.txt") : I18n.format("gui.de.invalid.txt"));
//            GuiHelper.drawCenteredString(fontRenderer, coreText, guiLeft + xSize / 2, guiTop + 36, coreColour, tile.coreValid.get());
//            if (!tile.coreValid.get()) {
//                GuiHelper.drawCenteredSplitString(fontRenderer, tile.invalidMessage.get(), guiLeft + xSize / 2, guiTop + 46, 180, coreColour, tile.coreValid.get());
//            }
//        }
//    }
//
//    @Override
//    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
//        super.drawGuiContainerForegroundLayer(mouseX, mouseY);
//
//
//    }
//
//    @Override
//    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
//        this.drawDefaultBackground();
//        super.drawScreen(mouseX, mouseY, partialTicks);
//
//        if (tile.active.get()) {
//            GuiHelper.drawEnergyBar(this, guiLeft + 5, guiTop + 82, 170, true, tile.getExtendedStorage(), tile.getExtendedCapacity(), true, mouseX, mouseY);
//
//            if (GuiHelper.isInRect(guiLeft + 40, guiTop + 27, xSize - 80, 8, mouseX, mouseY)) {
//                List<String> list = new ArrayList<String>();
//                list.add(TextFormatting.GRAY + "[" + Utils.addCommas(tile.getExtendedCapacity()) + " RF]");
//                drawHoveringText(list, mouseX, mouseY);
//            }
//
//            if (GuiHelper.isInRect(guiLeft + 40, guiTop + 48, xSize - 80, 8, mouseX, mouseY)) {
//                List<String> list = new ArrayList<String>();
//                list.add(TextFormatting.GRAY + "[" + Utils.addCommas(tile.getExtendedStorage()) + " RF]");
//                drawHoveringText(list, mouseX, mouseY);
//            }
//        }
//
//        if (tile.buildGuide.get()) {
//            drawCenteredString(fontRenderer, layer == -1 ? "All" : layer + "", guiLeft + (xSize / 2), guiTop - 10, 0xFFFFFF);
//        }
//
//        this.renderHoveredToolTip(mouseX, mouseY);
//    }
//
//    @Override
//    public void updateScreen() {
//        super.updateScreen();
//        updateButtonStates();
//    }
//
//    private void updateButtonStates() {
//        if (tile.active.get()) {
//            activate.displayString = I18n.format("button.de.deactivate.txt");
//        }
//        else {
//            activate.displayString = I18n.format("button.de.activate.txt");
//            toggleGuide.displayString = I18n.format("button.de.buildGuide.txt") + " " + (tile.buildGuide.get() ? I18n.format("gui.de.active.txt") : I18n.format("gui.de.inactive.txt"));
//            tierUp.enabled = tile.tier.get() < 8;
//            tierDown.enabled = tile.tier.get() > 1;
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
//    @Override
//    protected void actionPerformed(GuiButton button) throws IOException {
//        if (button.id < 5) {
//            tile.sendPacketToServer(output -> {
//            }, button.id);
//        }
//        else {
//            if (button == layerPlus) {
//                layer++;
//            }
//            else {
//                layer--;
//            }
//            layer = MathHelper.clip(layer, -1, 6);
//        }
//    }
}
