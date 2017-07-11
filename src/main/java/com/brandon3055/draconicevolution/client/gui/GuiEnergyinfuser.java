package com.brandon3055.draconicevolution.client.gui;

import cofh.redstoneflux.api.IEnergyContainerItem;
import com.brandon3055.brandonscore.client.utils.GuiHelper;
import com.brandon3055.brandonscore.utils.InfoHelper;
import com.brandon3055.brandonscore.utils.Utils;
import com.brandon3055.draconicevolution.DEFeatures;
import com.brandon3055.draconicevolution.blocks.tileentity.TileEnergyInfuser;
import com.brandon3055.draconicevolution.helpers.ResourceHelperDE;
import com.brandon3055.draconicevolution.inventory.ContainerEnergyInfuser;
import com.brandon3055.draconicevolution.utils.DETextures;
import net.minecraft.client.gui.inventory.GuiContainer;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.translation.I18n;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by brandon3055 on 31/05/2016.
 */
public class GuiEnergyinfuser extends GuiContainer {

    public EntityPlayer player;
    public TileEnergyInfuser tile;
    private float rotation = 0;

    public GuiEnergyinfuser(EntityPlayer player, TileEnergyInfuser tile) {
        super(new ContainerEnergyInfuser(player, tile));
        this.tile = tile;
        this.xSize = 176;
        this.ySize = 140;
        this.player = player;
    }


    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        //GuiHelper.drawGuiBaseBackground(this, guiLeft, guiTop, xSize, ySize);
        //GuiHelper.drawPlayerSlots(this, guiLeft + (xSize / 2), guiTop + 115, true);
        drawCenteredString(fontRenderer, DEFeatures.energyInfuser.getLocalizedName(), guiLeft + (xSize / 2), guiTop + 5, InfoHelper.GUI_TITLE);

        GlStateManager.color(1, 1, 1, 1);
        ResourceHelperDE.bindTexture(DETextures.GUI_ENERGY_INFUSER);
        drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);

        if (tile.getStackInSlot(0).isEmpty()) {
            drawTexturedModalRect(guiLeft + 63, guiTop + 34, 36, ySize, 18, 18);
        }

        float power = (float) tile.energySync.value / (float) tile.energyStorage.getMaxEnergyStored() * -1F + 1F;
        drawTexturedModalRect(guiLeft + 49, guiTop + 7 + (int) (power * 45), xSize, (int) (power * 45), 8, 45 - (int) (power * 45));//Power bar

        if (tile.running.value && tile.getStackInSlot(0) != null && tile.getStackInSlot(0).getItem() instanceof IEnergyContainerItem) {
            IEnergyContainerItem item = (IEnergyContainerItem) tile.getStackInSlot(0).getItem();
            float charge = (float) item.getEnergyStored(tile.getStackInSlot(0)) / (float) item.getMaxEnergyStored(tile.getStackInSlot(0)) * -1F + 1F;
            drawTexturedModalRect(guiLeft + 119, guiTop + 7 + (int) (charge * 45), xSize, (int) (charge * 45), 8, 45 - (int) (charge * 45));//Item Power bar
        }

        drawAnimatedParts(partialTicks);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        super.drawGuiContainerForegroundLayer(mouseX, mouseY);
        drawEnergyBarHoverText(mouseX - guiLeft, mouseY - guiTop);
    }

    private void drawEnergyBarHoverText(int x, int y) {
        if (GuiHelper.isInRect(48, 6, 9, 46, x, y)) {
            ArrayList<String> internal = new ArrayList<>();
            internal.add(I18n.translateToLocal("gui.de.internalStorage.txt"));
            internal.add("" + TextFormatting.DARK_BLUE + Utils.addCommas(tile.energySync.value) + "/" + Utils.addCommas(tile.energyStorage.getMaxEnergyStored()));
            drawHoveringText(internal, x, y, fontRenderer);
        }

        if (GuiHelper.isInRect(118, 6, 10, 46, x, y) && tile.running.value && tile.getStackInSlot(0) != null && tile.getStackInSlot(0).getItem() instanceof IEnergyContainerItem) {
            IEnergyContainerItem item = (IEnergyContainerItem) tile.getStackInSlot(0).getItem();
            ArrayList<String> internal = new ArrayList<>();
            internal.add(I18n.translateToLocal("gui.de.itemStorage.txt"));
            internal.add("" + TextFormatting.DARK_BLUE + Utils.addCommas(item.getEnergyStored(tile.getStackInSlot(0))) + "/" + Utils.addCommas(item.getMaxEnergyStored(tile.getStackInSlot(0))));
            drawHoveringText(internal, x, y, fontRenderer);
        }
    }

    private void drawAnimatedParts(float partial) {
        if (!tile.running.value) drawTexturedModalRect(guiLeft + 79, guiTop + 21, xSize, 45, 18, 18);
        if (tile.running.value) rotation += 0.2F;
        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();
        GlStateManager.translate(guiLeft + 62, guiTop + 4, 0);
        {//Draw Ring
            GlStateManager.translate(26, 26, 0);
            GlStateManager.rotate(rotation + (partial * 0.2F), 0, 0, 1);
            GlStateManager.translate(-26, -26, 0);
            drawTexturedModalRect(0, 0, xSize, 63, 52, 52);
        }
        if (tile.running.value && tile.charging.value) {
            Random rand = tile.getWorld().rand;
            int boltL = rand.nextInt(4);
            int boltS = rand.nextInt(3);
            int boltT = rand.nextInt(10);
            drawTexturedModalRect(0, 0, xSize, 115, 52, 52);

            GlStateManager.color(1F, 1F, 1F, 0.5F + (rand.nextFloat() / 2F));
            drawTexturedModalRect(0, 0, xSize, 167, 52, 52);
            GlStateManager.color(1F, 1F, 1F, 1F);

            if (boltT == 5) {
                GlStateManager.translate(26, 26, 0);
                GlStateManager.rotate(boltL * 90, 0, 0, 1);
                GlStateManager.translate(-26, -26, 0);

                if (boltS == 0) {
                    GlStateManager.translate(-(guiLeft + 62), -(guiTop + 4), 0);
                    GlStateManager.translate(guiLeft + 68, guiTop + 23, 0);
                    drawTexturedModalRect(0, 0, 0, ySize, 27, 15);
                }
                if (boltS == 1) {
                    GlStateManager.translate(-(guiLeft + 62), -(guiTop + 4), 0);
                    GlStateManager.translate(guiLeft + 68, guiTop + 26, 0);
                    drawTexturedModalRect(0, 0, 0, 156, 25, 8);
                }
                if (boltS == 2) {
                    GlStateManager.translate(-(guiLeft + 62), -(guiTop + 4), 0);
                    GlStateManager.translate(guiLeft + 68, guiTop + 27, 0);
                    drawTexturedModalRect(0, 0, 0, 165, 23, 7);
                }
                if (boltS == 3) {
                    GlStateManager.translate(-(guiLeft + 62), -(guiTop + 4), 0);
                    GlStateManager.translate(guiLeft + 68, guiTop + 26, 0);
                    drawTexturedModalRect(0, 0, 0, 173, 26, 8);
                }
            }

        }


        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
    }
}
