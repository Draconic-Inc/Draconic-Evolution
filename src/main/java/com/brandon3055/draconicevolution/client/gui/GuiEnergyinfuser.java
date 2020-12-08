package com.brandon3055.draconicevolution.client.gui;

import com.brandon3055.draconicevolution.blocks.tileentity.TileEnergyInfuser;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.util.text.ITextComponent;

/**
 * Created by brandon3055 on 31/05/2016.
 */
public class GuiEnergyinfuser extends ContainerScreen {

    public PlayerEntity player;
    public TileEnergyInfuser tile;
    private float rotation = 0;

    public GuiEnergyinfuser(Container screenContainer, PlayerInventory inv, TileEnergyInfuser tile, ITextComponent titleIn) {
        super(screenContainer, inv, titleIn);
        this.tile = tile;
        this.xSize = 176;
        this.ySize = 140;
        this.player = inv.player;
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(MatrixStack matrixStack, float partialTicks, int x, int y) {

    }

    //    public GuiEnergyinfuser(PlayerEntity player, TileEnergyInfuser tile) {
//        super(new ContainerEnergyInfuser(player, tile));
//
//    }


//    @Override
//    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
//        //GuiHelper.drawGuiBaseBackground(this, guiLeft, guiTop, xSize, ySize);
//        //GuiHelper.drawPlayerSlots(this, guiLeft + (xSize / 2), guiTop + 115, true);
//        drawCenteredString(fontRenderer, DEFeatures.energyInfuser.getLocalizedName(), guiLeft + (xSize / 2), guiTop + 5, InfoHelper.GUI_TITLE);
//
//        RenderSystem.color(1, 1, 1, 1);
//        ResourceHelperDE.bindTexture(DETextures.GUI_ENERGY_INFUSER);
//        drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);
//
//        if (tile.itemHandler.getStackInSlot(0).isEmpty()) {
//            drawTexturedModalRect(guiLeft + 63, guiTop + 34, 36, ySize, 18, 18);
//        }
//
//        float power = (float) tile.opStorage.getOPStored() / (float) tile.opStorage.getMaxOPStored() * -1F + 1F;
//        drawTexturedModalRect(guiLeft + 49, guiTop + 7 + (int) (power * 45), xSize, (int) (power * 45), 8, 45 - (int) (power * 45));//Power bar
//
//        if (tile.running.get() && tile.itemHandler.getStackInSlot(0) != null && tile.itemHandler.getStackInSlot(0).getItem() instanceof IEnergyContainerItem) {
//            IEnergyContainerItem item = (IEnergyContainerItem) tile.itemHandler.getStackInSlot(0).getItem();
//            float charge = (float) item.getEnergyStored(tile.itemHandler.getStackInSlot(0)) / (float) item.getMaxEnergyStored(tile.itemHandler.getStackInSlot(0)) * -1F + 1F;
//            drawTexturedModalRect(guiLeft + 119, guiTop + 7 + (int) (charge * 45), xSize, (int) (charge * 45), 8, 45 - (int) (charge * 45));//Item Power bar
//        }
//
//        drawAnimatedParts(partialTicks);
//    }
//
//    @Override
//    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
//        super.drawGuiContainerForegroundLayer(mouseX, mouseY);
//        drawEnergyBarHoverText(mouseX - guiLeft, mouseY - guiTop);
//    }
//
//    private void drawEnergyBarHoverText(int x, int y) {
//        if (GuiHelper.isInRect(48, 6, 9, 46, x, y)) {
//            ArrayList<String> internal = new ArrayList<>();
//            internal.add(I18n.translateToLocal("gui.de.internalStorage.txt"));
//            internal.add("" + TextFormatting.DARK_BLUE + Utils.addCommas(tile.opStorage.getOPStored()) + "/" + Utils.addCommas(tile.opStorage.getMaxOPStored()));
//            drawHoveringText(internal, x, y, fontRenderer);
//        }
//
//        if (GuiHelper.isInRect(118, 6, 10, 46, x, y) && tile.running.get() && tile.itemHandler.getStackInSlot(0) != null && tile.itemHandler.getStackInSlot(0).getItem() instanceof IEnergyContainerItem) {
//            IEnergyContainerItem item = (IEnergyContainerItem) tile.itemHandler.getStackInSlot(0).getItem();
//            ArrayList<String> internal = new ArrayList<>();
//            internal.add(I18n.translateToLocal("gui.de.itemStorage.txt"));
//            internal.add("" + TextFormatting.DARK_BLUE + Utils.addCommas(item.getEnergyStored(tile.itemHandler.getStackInSlot(0))) + "/" + Utils.addCommas(item.getMaxEnergyStored(tile.itemHandler.getStackInSlot(0))));
//            drawHoveringText(internal, x, y, fontRenderer);
//        }
//    }
//
//    private void drawAnimatedParts(float partial) {
//        if (!tile.running.get()) drawTexturedModalRect(guiLeft + 79, guiTop + 21, xSize, 45, 18, 18);
//        if (tile.running.get()) rotation += 0.2F;
//        RenderSystem.pushMatrix();
//        RenderSystem.enableBlend();
//        RenderSystem.translate(guiLeft + 62, guiTop + 4, 0);
//        {//Draw Ring
//            RenderSystem.translate(26, 26, 0);
//            RenderSystem.rotate(rotation + (partial * 0.2F), 0, 0, 1);
//            RenderSystem.translate(-26, -26, 0);
//            drawTexturedModalRect(0, 0, xSize, 63, 52, 52);
//        }
//        if (tile.running.get() && tile.charging.get()) {
//            Random rand = tile.getWorld().rand;
//            int boltL = rand.nextInt(4);
//            int boltS = rand.nextInt(3);
//            int boltT = rand.nextInt(10);
//            drawTexturedModalRect(0, 0, xSize, 115, 52, 52);
//
//            RenderSystem.color(1F, 1F, 1F, 0.5F + (rand.nextFloat() / 2F));
//            drawTexturedModalRect(0, 0, xSize, 167, 52, 52);
//            RenderSystem.color(1F, 1F, 1F, 1F);
//
//            if (boltT == 5) {
//                RenderSystem.translate(26, 26, 0);
//                RenderSystem.rotate(boltL * 90, 0, 0, 1);
//                RenderSystem.translate(-26, -26, 0);
//
//                if (boltS == 0) {
//                    RenderSystem.translate(-(guiLeft + 62), -(guiTop + 4), 0);
//                    RenderSystem.translate(guiLeft + 68, guiTop + 23, 0);
//                    drawTexturedModalRect(0, 0, 0, ySize, 27, 15);
//                }
//                if (boltS == 1) {
//                    RenderSystem.translate(-(guiLeft + 62), -(guiTop + 4), 0);
//                    RenderSystem.translate(guiLeft + 68, guiTop + 26, 0);
//                    drawTexturedModalRect(0, 0, 0, 156, 25, 8);
//                }
//                if (boltS == 2) {
//                    RenderSystem.translate(-(guiLeft + 62), -(guiTop + 4), 0);
//                    RenderSystem.translate(guiLeft + 68, guiTop + 27, 0);
//                    drawTexturedModalRect(0, 0, 0, 165, 23, 7);
//                }
//                if (boltS == 3) {
//                    RenderSystem.translate(-(guiLeft + 62), -(guiTop + 4), 0);
//                    RenderSystem.translate(guiLeft + 68, guiTop + 26, 0);
//                    drawTexturedModalRect(0, 0, 0, 173, 26, 8);
//                }
//            }
//
//        }
//
//
//        RenderSystem.disableBlend();
//        RenderSystem.popMatrix();
//    }
//
//    @Override
//    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
//        this.drawDefaultBackground();
//        super.drawScreen(mouseX, mouseY, partialTicks);
//        this.renderHoveredToolTip(mouseX, mouseY);
//    }
}
