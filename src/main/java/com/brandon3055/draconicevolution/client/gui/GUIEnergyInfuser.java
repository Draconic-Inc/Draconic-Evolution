package com.brandon3055.draconicevolution.client.gui;

import java.util.ArrayList;
import java.util.Random;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;

import org.lwjgl.opengl.GL11;

import cofh.api.energy.IEnergyContainerItem;

import com.brandon3055.brandonscore.client.utills.GuiHelper;
import com.brandon3055.draconicevolution.common.container.ContainerEnergyInfuser;
import com.brandon3055.draconicevolution.common.lib.References;
import com.brandon3055.draconicevolution.common.tileentities.TileEnergyInfuser;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GUIEnergyInfuser extends GuiContainer {

    public EntityPlayer player;
    private TileEnergyInfuser tile;
    private float rotation = 0;

    public GUIEnergyInfuser(InventoryPlayer invPlayer, TileEnergyInfuser tile) {
        super(new ContainerEnergyInfuser(invPlayer, tile));

        xSize = 176;
        ySize = 140;

        this.tile = tile;
        this.player = invPlayer.player;
    }

    private static final ResourceLocation texture = new ResourceLocation(
            References.MODID.toLowerCase(),
            "textures/gui/EnergyInfuser.png");

    @Override
    protected void drawGuiContainerBackgroundLayer(float f, int x, int y) {
        GL11.glColor4f(1F, 1F, 1F, 1F);
        Minecraft.getMinecraft().getTextureManager().bindTexture(texture);
        drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);
        if (tile.getStackInSlot(0) == null) drawTexturedModalRect(guiLeft + 63, guiTop + 34, 36, ySize, 18, 18); // fuel
                                                                                                                 // box

        float power = (float) tile.energy.getEnergyStored() / (float) tile.energy.getMaxEnergyStored() * -1F + 1F;
        drawTexturedModalRect(
                guiLeft + 49,
                guiTop + 7 + (int) (power * 45),
                xSize,
                (int) (power * 45),
                8,
                45 - (int) (power * 45)); // Power bar

        if (tile.running && tile.getStackInSlot(0) != null
                && tile.getStackInSlot(0).getItem() instanceof IEnergyContainerItem) {
            IEnergyContainerItem item = (IEnergyContainerItem) tile.getStackInSlot(0).getItem();
            float charge = (float) item.getEnergyStored(tile.getStackInSlot(0))
                    / (float) item.getMaxEnergyStored(tile.getStackInSlot(0))
                    * -1F + 1F;
            drawTexturedModalRect(
                    guiLeft + 119,
                    guiTop + 7 + (int) (charge * 45),
                    xSize,
                    (int) (charge * 45),
                    8,
                    45 - (int) (charge * 45)); // Item Power bar
        }

        drawAnimatedParts();
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
    public void drawScreen(int x, int y, float p_73863_3_) {
        super.drawScreen(x, y, p_73863_3_);
        drawEnergyBarHoverText(x - guiLeft, y - guiTop);
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
        super.updateScreen();
    }

    private void drawEnergyBarHoverText(int x, int y) {
        if (GuiHelper.isInRect(48, 6, 9, 46, x, y)) {
            ArrayList<String> internal = new ArrayList<String>();
            internal.add(StatCollector.translateToLocal("gui.de.internalStorage.txt"));
            internal.add(
                    "" + EnumChatFormatting.DARK_BLUE
                            + tile.energy.getEnergyStored()
                            + "/"
                            + tile.energy.getMaxEnergyStored());
            GL11.glPushAttrib(GL11.GL_LIGHTING_BIT);
            drawHoveringText(internal, x + guiLeft, y + guiTop, fontRendererObj);
            GL11.glPopAttrib();
        }

        if (GuiHelper.isInRect(118, 6, 10, 46, x, y) && tile.running
                && tile.getStackInSlot(0) != null
                && tile.getStackInSlot(0).getItem() instanceof IEnergyContainerItem) {
            IEnergyContainerItem item = (IEnergyContainerItem) tile.getStackInSlot(0).getItem();
            ArrayList<String> internal = new ArrayList<String>();
            internal.add(StatCollector.translateToLocal("gui.de.itemStorage.txt"));
            internal.add(
                    "" + EnumChatFormatting.DARK_BLUE
                            + item.getEnergyStored(tile.getStackInSlot(0))
                            + "/"
                            + item.getMaxEnergyStored(tile.getStackInSlot(0)));
            GL11.glPushAttrib(GL11.GL_LIGHTING_BIT);
            drawHoveringText(internal, x + guiLeft, y + guiTop, fontRendererObj);
            GL11.glPopAttrib();
        }
    }

    private void drawAnimatedParts() {
        if (!tile.running) drawTexturedModalRect(guiLeft + 79, guiTop + 21, xSize, 45, 18, 18);
        if (tile.running) rotation += 0.2F;
        GL11.glPushMatrix();
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glTranslatef(guiLeft + 62, guiTop + 4, 0);
        { // Draw Ring
            GL11.glTranslatef(26, 26, 0);
            GL11.glRotatef(rotation, 0, 0, 1);
            GL11.glTranslatef(-26, -26, 0);
            drawTexturedModalRect(0, 0, xSize, 63, 52, 52);
        }
        if (tile.running && tile.transfer) {
            Random rand = tile.getWorldObj().rand;
            int boltL = rand.nextInt(4);
            int boltS = rand.nextInt(3);
            int boltT = rand.nextInt(10);
            drawTexturedModalRect(0, 0, xSize, 115, 52, 52);

            GL11.glColor4f(1F, 1F, 1F, 0.5F + (rand.nextFloat() / 2F));
            drawTexturedModalRect(0, 0, xSize, 167, 52, 52);
            GL11.glColor4f(1F, 1F, 1F, 1F);

            if (boltT == 5) {
                GL11.glTranslatef(26, 26, 0);
                GL11.glRotatef(boltL * 90, 0, 0, 1);
                GL11.glTranslatef(-26, -26, 0);

                if (boltS == 0) {
                    GL11.glTranslatef(-(guiLeft + 62), -(guiTop + 4), 0);
                    GL11.glTranslatef(guiLeft + 68, guiTop + 23, 0);
                    drawTexturedModalRect(0, 0, 0, ySize, 27, 15);
                }
                if (boltS == 1) {
                    GL11.glTranslatef(-(guiLeft + 62), -(guiTop + 4), 0);
                    GL11.glTranslatef(guiLeft + 68, guiTop + 26, 0);
                    drawTexturedModalRect(0, 0, 0, 156, 25, 8);
                }
                if (boltS == 2) {
                    GL11.glTranslatef(-(guiLeft + 62), -(guiTop + 4), 0);
                    GL11.glTranslatef(guiLeft + 68, guiTop + 27, 0);
                    drawTexturedModalRect(0, 0, 0, 165, 23, 7);
                }
                if (boltS == 3) {
                    GL11.glTranslatef(-(guiLeft + 62), -(guiTop + 4), 0);
                    GL11.glTranslatef(guiLeft + 68, guiTop + 26, 0);
                    drawTexturedModalRect(0, 0, 0, 173, 26, 8);
                }
            }
        }

        GL11.glDisable(GL11.GL_BLEND);
        GL11.glPopMatrix();
    }
}
