package com.brandon3055.draconicevolution.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import com.brandon3055.draconicevolution.common.container.ContainerTemplate;
import com.brandon3055.draconicevolution.common.lib.References;
import com.brandon3055.draconicevolution.common.tileentities.TileContainerTemplate;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GUIContainerTemplate extends GuiContainer {

    public EntityPlayer player;
    private TileContainerTemplate tile;

    public GUIContainerTemplate(InventoryPlayer invPlayer, TileContainerTemplate tile) {
        super(new ContainerTemplate(invPlayer, tile));

        xSize = 176;
        ySize = 140;

        this.tile = tile;
        this.player = invPlayer.player;
    }

    private static final ResourceLocation texture = new ResourceLocation(
            References.MODID.toLowerCase(),
            "textures/gui/.png");

    @Override
    protected void drawGuiContainerBackgroundLayer(float f, int x, int y) {
        GL11.glColor4f(1F, 1F, 1F, 1F);
        Minecraft.getMinecraft().getTextureManager().bindTexture(texture);
        drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int x, int y) {
        fontRendererObj.drawStringWithShadow("Energy Infuser", 49, -9, 0x00FFFF);
        // bindTexture(texture);

        // fontRendererObj.drawString("Charges: " + charges, 90, 25, 0x000000);
        // drawCenteredString(fontRendererObj, "Charges: " + charges, 117, 25, 0x000000);
        // drawCenteredString(fontRendererObj, "Weather Controller", xSize/2, -15, 0x2a4ed0);

    }

    @Override
    public void initGui() {
        super.initGui();
    }

    @Override
    protected void actionPerformed(GuiButton button) {}

    @Override
    public void updateScreen() {
        super.updateScreen();
    }
}
