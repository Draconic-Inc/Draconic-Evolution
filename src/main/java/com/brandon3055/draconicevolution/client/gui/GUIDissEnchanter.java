package com.brandon3055.draconicevolution.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;

import org.lwjgl.opengl.GL11;

import com.brandon3055.draconicevolution.DraconicEvolution;
import com.brandon3055.draconicevolution.common.container.ContainerDissEnchanter;
import com.brandon3055.draconicevolution.common.lib.References;
import com.brandon3055.draconicevolution.common.network.ButtonPacket;
import com.brandon3055.draconicevolution.common.tileentities.TileDissEnchanter;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GUIDissEnchanter extends GuiContainer {

    public EntityPlayer player;
    private TileDissEnchanter tile;
    private boolean cachRecipeValid = false;
    private int cachCost = 0;

    public GUIDissEnchanter(InventoryPlayer invPlayer, TileDissEnchanter tile) {
        super(new ContainerDissEnchanter(invPlayer, tile));

        xSize = 176;
        ySize = 142;

        this.tile = tile;
        this.player = invPlayer.player;
    }

    private static final ResourceLocation texture = new ResourceLocation(
            References.MODID.toLowerCase(),
            "textures/gui/DissEnchanter.png");

    @Override
    protected void drawGuiContainerBackgroundLayer(float f, int x, int y) {
        GL11.glColor4f(1F, 1F, 1F, 1F);
        Minecraft.getMinecraft().getTextureManager().bindTexture(texture);
        drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int x, int y) {
        drawCenteredString(
                fontRendererObj,
                StatCollector.translateToLocal("tile.draconicevolution:dissEnchanter.name"),
                88,
                4,
                0x00FFFF);

        fontRendererObj.drawString("Item", 5, 40, 0x0000ff);
        fontRendererObj.drawString("Damage: " + (40 - tile.bookPower) + "%", 5, 49, 0x0000ff);
    }

    @Override
    public void initGui() {
        super.initGui();
        buttonList.clear();
        int posX = (this.width - xSize) / 2;
        int posY = (this.height - ySize) / 2;
        buttonList.add(new GuiButtonAHeight(0, posX + 108, posY + 45, 60, 12, ""));
        updateButtonState();
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        if (button.id == 0) DraconicEvolution.network.sendToServer(new ButtonPacket((byte) 1, true));
    }

    @Override
    public void updateScreen() {
        super.updateScreen();
        if (cachRecipeValid != tile.isValidRecipe) {
            cachRecipeValid = tile.isValidRecipe;
            updateButtonState();
        }
        if (cachCost != tile.dissenchantCost) {
            cachCost = tile.dissenchantCost;
            updateButtonState();
        }
    }

    private void updateButtonState() {
        boolean flag = cachRecipeValid;
        if (flag && player.experienceLevel < tile.dissenchantCost && !player.capabilities.isCreativeMode) flag = false;
        ((GuiButtonAHeight) buttonList.get(0)).enabled = flag;
        ((GuiButtonAHeight) buttonList.get(0)).packedFGColour = cachRecipeValid ? 0x000000 : 0xdf0000;
        ((GuiButtonAHeight) buttonList.get(0)).displayString = "Cost: " + tile.dissenchantCost;
    }
}
