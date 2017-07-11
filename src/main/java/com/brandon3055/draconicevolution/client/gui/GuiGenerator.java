package com.brandon3055.draconicevolution.client.gui;

import com.brandon3055.brandonscore.client.utils.GuiHelper;
import com.brandon3055.draconicevolution.DEFeatures;
import com.brandon3055.draconicevolution.blocks.tileentity.TileGenerator;
import com.brandon3055.draconicevolution.helpers.ResourceHelperDE;
import com.brandon3055.draconicevolution.inventory.ContainerGenerator;
import com.brandon3055.draconicevolution.utils.DETextures;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;

@SideOnly(Side.CLIENT)
public class GuiGenerator extends GuiContainer {

    public EntityPlayer player;
    private TileGenerator tile;
    private int guiUpdateTick;

    public GuiGenerator(EntityPlayer player, TileGenerator tile) {
        super(new ContainerGenerator(player, tile));

        xSize = 176;
        ySize = 162;

        this.tile = tile;
        this.player = player;
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float f, int X, int Y) {
        GL11.glColor4f(1, 1, 1, 1);

        ResourceHelperDE.bindTexture(DETextures.GUI_GENERATOR);
        drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);
        drawTexturedModalRect(guiLeft + 63, guiTop + 34, 0, ySize, 18, 18);//fuel box
        drawTexturedModalRect(guiLeft + 97, guiTop + 34, 18, ySize, 18, 18);//flame box
        if (tile.getStackInSlot(0).isEmpty()){
            drawTexturedModalRect(guiLeft + 63, guiTop + 34, 36, ySize, 18, 18);//fuel box
        }

        float power = (float) tile.energySync.value / (float) tile.getMaxEnergyStored(EnumFacing.DOWN) * -1 + 1;
        float fuel = tile.burnTimeRemaining.value / ((float) tile.burnTime.value) * -1 + 1;

        drawTexturedModalRect(guiLeft + 83, guiTop + 11 + (int) (power * 40), xSize, (int) (power * 40), 12, 40 - (int) (power * 40));//Power bar
        drawTexturedModalRect(guiLeft + 100, guiTop + 37 + (int) (fuel * 13), xSize, 40 + (int) (fuel * 13), 18, 18 - (int) (fuel * 13));//Power bar

        fontRenderer.drawStringWithShadow(I18n.format(DEFeatures.generator.getUnlocalizedName() + ".name"), guiLeft + 64, guiTop, 0x00FFFF);

        int x = X - guiLeft;
        int y = Y - guiTop;
        if (GuiHelper.isInRect(83, 10, 12, 40, x, y)) {
            ArrayList<String> internal = new ArrayList<>();
            internal.add(I18n.format("info.de.energyBuffer.txt"));
            internal.add("" + TextFormatting.DARK_BLUE + tile.energySync.value + "/" + tile.getMaxEnergyStored(EnumFacing.UP));
            drawHoveringText(internal, x + guiLeft, y + guiTop, fontRenderer);
        }
    }

    @Override
    public void updateScreen() {
        guiUpdateTick++;
        if (guiUpdateTick >= 10) {
            initGui();
            guiUpdateTick = 0;
        }
        super.updateScreen();

    }

}
