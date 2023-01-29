package com.brandon3055.draconicevolution.client.gui;

import java.util.ArrayList;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.ForgeDirection;

import org.lwjgl.opengl.GL11;

import com.brandon3055.brandonscore.client.utills.GuiHelper;
import com.brandon3055.draconicevolution.common.container.ContainerGenerator;
import com.brandon3055.draconicevolution.common.lib.References;
import com.brandon3055.draconicevolution.common.tileentities.TileGenerator;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GUIGenerator extends GuiContainer {

    public EntityPlayer player;
    private TileGenerator tile;
    private int guiUpdateTick;

    public GUIGenerator(InventoryPlayer invPlayer, TileGenerator tile) {
        super(new ContainerGenerator(invPlayer, tile));

        xSize = 176;
        ySize = 162;

        this.tile = tile;
        this.player = invPlayer.player;
    }

    private static final ResourceLocation texture = new ResourceLocation(
            References.MODID.toLowerCase(),
            "textures/gui/GGrinder.png");

    @Override
    protected void drawGuiContainerBackgroundLayer(float f, int X, int Y) {
        GL11.glColor4f(1, 1, 1, 1);

        Minecraft.getMinecraft().getTextureManager().bindTexture(texture);
        drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);
        drawTexturedModalRect(guiLeft + 63, guiTop + 34, 0, ySize, 18, 18); // fuel box
        drawTexturedModalRect(guiLeft + 97, guiTop + 34, 18, ySize, 18, 18); // flame box
        if (tile.getStackInSlot(0) == null) drawTexturedModalRect(guiLeft + 63, guiTop + 34, 36, ySize, 18, 18); // fuel
                                                                                                                 // box

        float power = (float) tile.getEnergyStored(ForgeDirection.DOWN)
                / (float) tile.getMaxEnergyStored(ForgeDirection.DOWN)
                * -1 + 1;
        float fuel = tile.burnTimeRemaining / ((float) tile.burnTime) * -1 + 1;

        drawTexturedModalRect(
                guiLeft + 83,
                guiTop + 11 + (int) (power * 40),
                xSize,
                0 + (int) (power * 40),
                12,
                40 - (int) (power * 40)); // Power bar
        drawTexturedModalRect(
                guiLeft + 100,
                guiTop + 37 + (int) (fuel * 13),
                xSize,
                40 + (int) (fuel * 13),
                18,
                18 - (int) (fuel * 13)); // Power bar

        fontRendererObj.drawStringWithShadow("Generator", guiLeft + 64, guiTop + 0, 0x00FFFF);

        int x = X - guiLeft;
        int y = Y - guiTop;
        if (GuiHelper.isInRect(83, 10, 12, 40, x, y)) {
            ArrayList<String> internal = new ArrayList<String>();
            internal.add("Internal Storage");
            internal.add(
                    "" + EnumChatFormatting.DARK_BLUE
                            + tile.getEnergyStored(ForgeDirection.UP)
                            + "/"
                            + tile.getMaxEnergyStored(ForgeDirection.UP));
            drawHoveringText(internal, x + guiLeft, y + guiTop, fontRendererObj);
        }
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int X, int Y) {

        int x = X - guiLeft;
        int y = Y - guiTop;
        /*
         * if (GuiHelper.isInRect(83, 10, 95, 50, x, y)) { ArrayList<String> internal = new ArrayList<String>();
         * internal.add("Internal Storage"); internal.add("" + EnumChatFormatting.DARK_BLUE +
         * tile.getEnergyStored(ForgeDirection.UP) + "/" + tile.getMaxEnergyStored(ForgeDirection.UP));
         * drawHoveringText(internal, x, y, fontRendererObj); }
         */
    }

    @Override
    public void initGui() {
        super.initGui();

        // ID
        // buttonList.add(new GuiButton(0, guiLeft + 85, guiTop , 85, 20, text));
    }

    @Override
    protected void actionPerformed(GuiButton button) {}

    @Override
    public void updateScreen() {
        guiUpdateTick++;
        if (guiUpdateTick >= 10) {
            initGui();
            guiUpdateTick = 0;
        }
        super.updateScreen();
    }

    // ####Draw at############ read from size
    // drawTexturedModalRect(guiLeft + 37, guiTop + 4, 0, 143, 37, 38);
}
