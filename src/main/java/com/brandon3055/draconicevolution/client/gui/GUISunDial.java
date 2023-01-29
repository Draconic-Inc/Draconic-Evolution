package com.brandon3055.draconicevolution.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import com.brandon3055.draconicevolution.DraconicEvolution;
import com.brandon3055.draconicevolution.common.container.ContainerSunDial;
import com.brandon3055.draconicevolution.common.lib.References;
import com.brandon3055.draconicevolution.common.network.TeleporterPacket;
import com.brandon3055.draconicevolution.common.tileentities.TileSunDial;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GUISunDial extends GuiContainer {

    private int guiUpdateTick;

    public GUISunDial(InventoryPlayer invPlayer, TileSunDial te1) {
        super(new ContainerSunDial(invPlayer, te1));

        xSize = 176;
        ySize = 90;
    }

    private static final ResourceLocation texture = new ResourceLocation(References.MODID, "textures/gui/SunDial.png");

    @Override
    protected void drawGuiContainerBackgroundLayer(float f, int x, int y) {
        GL11.glColor4f(1, 1, 1, 1);

        Minecraft.getMinecraft().getTextureManager().bindTexture(texture);
        drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);
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
        buttonList.add(new GuiButton(0, guiLeft + 10, guiTop + 10, 85, 20, ""));
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        if (button.id == 0) {
            DraconicEvolution.network.sendToServer(new TeleporterPacket());
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
