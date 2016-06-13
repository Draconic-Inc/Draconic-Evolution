package com.brandon3055.draconicevolution.client.gui;

import com.brandon3055.brandonscore.client.utills.GuiHelper;
import com.brandon3055.draconicevolution.blocks.tileentity.TileFusionCraftingCore;
import com.brandon3055.draconicevolution.inventory.ContainerFusionCraftingCore;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.io.IOException;

@SideOnly(Side.CLIENT)
public class GuiFusionCraftingCore extends GuiContainer {

    private final EntityPlayer player;
    private final TileFusionCraftingCore tile;

    public GuiFusionCraftingCore(EntityPlayer player, TileFusionCraftingCore tile) {
        super(new ContainerFusionCraftingCore(player, tile));
        this.player = player;
        this.tile = tile;

        this.xSize = 180;
        this.ySize = 200;
    }

    @Override
    public void initGui() {
        super.initGui();
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        GuiHelper.drawGuiBaseBackground(this, guiLeft, guiTop, xSize, ySize);
        GuiHelper.drawPlayerSlots(this, guiLeft + (xSize / 2), guiTop + 115, true);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        super.drawGuiContainerForegroundLayer(mouseX, mouseY);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        super.actionPerformed(button);
    }
}
