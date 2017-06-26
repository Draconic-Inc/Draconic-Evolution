package com.brandon3055.draconicevolution.client.gui;

import com.brandon3055.brandonscore.client.utils.GuiHelper;
import com.brandon3055.draconicevolution.DEFeatures;
import com.brandon3055.draconicevolution.blocks.tileentity.TileGrinder;
import com.brandon3055.draconicevolution.helpers.ResourceHelperDE;
import com.brandon3055.draconicevolution.inventory.ContainerGrinder;
import com.brandon3055.draconicevolution.utils.DETextures;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.ArrayList;

/**
 * Created by brandon3055 on 30/3/2016.
 */
@SideOnly(Side.CLIENT)
public class GuiGrinder extends GuiContainer {

    public EntityPlayer player;
    private TileGrinder tile;

    public GuiGrinder(InventoryPlayer invPlayer, TileGrinder tile) {
        super(new ContainerGrinder(invPlayer, tile));

        xSize = 176;
        ySize = 162;

        this.tile = tile;
        this.player = invPlayer.player;
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float f, int X, int Y) {
        GlStateManager.color(1, 1, 1, 1);

        ResourceHelperDE.bindTexture(DETextures.GUI_GRINDER);
        drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);

        float power = (float) tile.energySync.value / (float) tile.getMaxEnergyStored(EnumFacing.DOWN) * -1 + 1;
        drawTexturedModalRect(guiLeft + 68, guiTop + 12 + (int) (power * 40), xSize, (int) (power * 40), 12, 40 - (int) (power * 40));//Power bar

        drawCenteredString(fontRendererObj, I18n.format(DEFeatures.grinder.getUnlocalizedName() + ".name"), guiLeft + xSize / 2 + 2, guiTop, 0x00FFFF);

        int x = X - guiLeft;
        int y = Y - guiTop;
        if (GuiHelper.isInRect(68, 10, 12, 40, x, y)) {
            ArrayList<String> internal = new ArrayList<>();
            internal.add(I18n.format("info.de.energyBuffer.txt"));
            internal.add("" + TextFormatting.DARK_BLUE + tile.energySync.value + "/" + tile.getMaxEnergyStored(EnumFacing.UP));
            drawHoveringText(internal, x + guiLeft, y + guiTop, fontRendererObj);
        }
    }
}
