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
import com.brandon3055.draconicevolution.common.container.ContainerGrinder;
import com.brandon3055.draconicevolution.common.lib.References;
import com.brandon3055.draconicevolution.common.tileentities.TileGrinder;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GUIGrinder extends GuiContainer {

    public EntityPlayer player;
    private TileGrinder tile;
    private int guiUpdateTick;

    public GUIGrinder(InventoryPlayer invPlayer, TileGrinder tile) {
        super(new ContainerGrinder(invPlayer, tile));

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
        if (!tile.isExternallyPowered()) drawTexturedModalRect(guiLeft + 63, guiTop + 34, 0, ySize, 18, 18); // fuel box
        drawTexturedModalRect(guiLeft + 97, guiTop + 34, 18, ySize, 18, 18); // flame box
        if (tile.getStackInSlot(0) == null && !tile.isExternallyPowered())
            drawTexturedModalRect(guiLeft + 63, guiTop + 34, 36, ySize, 18, 18); // fuel box

        float power;
        if (tile.isExternallyPowered()) {
            power = (float) tile.getEnergyStored(ForgeDirection.DOWN)
                    / (float) tile.getMaxEnergyStored(ForgeDirection.DOWN)
                    * -1 + 1;
        } else {
            power = (float) tile.getInternalBuffer().getEnergyStored()
                    / (float) tile.getInternalBuffer().getMaxEnergyStored()
                    * -1 + 1;
        }
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
        if (tile.isExternallyPowered()) drawTexturedModalRect(guiLeft + 100, guiTop + 37, xSize, 66, 13, 13);

        fontRendererObj.drawStringWithShadow("Grinder", guiLeft + 71, guiTop + 0, 0x00FFFF);

        int x = X - guiLeft;
        int y = Y - guiTop;
        if (GuiHelper.isInRect(83, 10, 12, 40, x, y)) {
            ArrayList<String> internal = new ArrayList<String>();
            internal.add("Energy Buffer");
            if (tile.isExternallyPowered()) {
                internal.add(
                        "" + EnumChatFormatting.DARK_BLUE
                                + tile.getEnergyStored(ForgeDirection.UP)
                                + "/"
                                + tile.getMaxEnergyStored(ForgeDirection.UP));
            } else {
                internal.add(
                        "" + EnumChatFormatting.DARK_BLUE
                                + tile.getInternalBuffer().getEnergyStored()
                                + "/"
                                + tile.getInternalBuffer().getMaxEnergyStored());
            }
            drawHoveringText(internal, x + guiLeft, y + guiTop, fontRendererObj);
        }
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int p_146979_1_, int p_146979_2_) {
        // fontRendererObj.drawString("Charges: " + charges, 90, 25, 0x000000);
        // drawCenteredString(fontRendererObj, "Charges: " + charges, 117, 25, 0x000000);
        // drawCenteredString(fontRendererObj, "Weather Controller", xSize/2, -15, 0x2a4ed0);

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
