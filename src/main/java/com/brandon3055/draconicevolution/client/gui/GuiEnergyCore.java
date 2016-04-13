package com.brandon3055.draconicevolution.client.gui;

import com.brandon3055.brandonscore.client.utills.GuiHelper;
import com.brandon3055.brandonscore.inventory.ContainerBCBase;
import com.brandon3055.brandonscore.network.PacketTileMessage;
import com.brandon3055.brandonscore.utills.InfoHelper;
import com.brandon3055.brandonscore.utills.Utills;
import com.brandon3055.draconicevolution.DEFeatures;
import com.brandon3055.draconicevolution.blocks.tileentity.TileEnergyStorageCore;
import com.brandon3055.draconicevolution.client.handler.ClientEventHandler;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.translation.I18n;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 *  Created by brandon3055 on 7/4/2016.
 */
public class GuiEnergyCore extends GuiContainer {

    public EntityPlayer player;
    public TileEnergyStorageCore tile;
    private GuiButton activate;
    private GuiButton tierUp;
    private GuiButton tierDown;
    private GuiButton toggleGuide;
    private GuiButton creativeBuild;

    public GuiEnergyCore(EntityPlayer player, TileEnergyStorageCore tile){
        super(new ContainerBCBase<TileEnergyStorageCore>(player, tile).addPlayerSlots(10, 116));
        this.tile = tile;
        this.xSize = 180;
        this.ySize = 200;
        this.player = player;
    }

    @Override
    public void initGui() {
        super.initGui();
        buttonList.clear();
        buttonList.add(activate      = new GuiButton(0, guiLeft + 150, 0, 80, 20, "Activate-L"));
        buttonList.add(tierUp        = new GuiButton(1, guiLeft + 150, 20, 80, 20, "tierUp-L"));
        buttonList.add(tierDown      = new GuiButton(2, guiLeft + 150, 40, 80, 20, "tierDown-L"));
        buttonList.add(toggleGuide   = new GuiButton(3, guiLeft + 150, 60, 80, 20, "toggleGuide-L"));
        buttonList.add(creativeBuild = new GuiButton(4, guiLeft + 150, 80, 80, 20, "creativeBuild-L"));
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        GuiHelper.drawGuiBaseBackground(this, guiLeft, guiTop, xSize, ySize);
        GuiHelper.drawPlayerSlots(this, guiLeft + (xSize/2), guiTop + 115, true);
        drawCenteredString(fontRendererObj, DEFeatures.energyStorageCore.getLocalizedName(), guiLeft + (xSize/2), guiTop + 4, InfoHelper.GUI_TITLE);

        fontRendererObj.drawString(I18n.translateToLocal("gui.de.tier.txt") + ": " + tile.tier.value, guiLeft + 4, guiTop + 15, 0x000000);
        fontRendererObj.drawString(I18n.translateToLocal("Active-L")+": " + tile.active.value, guiLeft + 4, guiTop + 24, 0x000000);
        fontRendererObj.drawString(I18n.translateToLocal("StabsValid-L")+": " + tile.stabilizersOK.value, guiLeft + 4, guiTop + 33, 0x000000);
        fontRendererObj.drawString(I18n.translateToLocal("StructureValid-L")+": " + tile.structureValid.value, guiLeft + 4, guiTop + 42, 0x000000);

        long max = Long.MAX_VALUE;
        long energy = (long)((ClientEventHandler.elapsedTicks % 100D / 100D) * max);

        GuiHelper.drawCenteredString(fontRendererObj, TextFormatting.DARK_BLUE +""+ TextFormatting.UNDERLINE + I18n.translateToLocal("gui.de.maxStorage.txt"), guiLeft + (xSize / 2), guiTop + 104, 0x000000, false);

        GuiHelper.drawEnergyBar(this, guiLeft + 5, guiTop + 88, 170, true, energy, max, false, mouseX, mouseY);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        super.drawGuiContainerForegroundLayer(mouseX, mouseY);
        if (GuiHelper.isInRect(guiLeft + 40, guiTop + 102, xSize - 80, 20, mouseX, mouseY)){
            List<String> list = new ArrayList<String>();
            list.add(InfoHelper.HITC() + Utills.formatNumber(Long.MAX_VALUE));
            list.add(TextFormatting.GRAY +"["+ Utills.addCommas(Long.MAX_VALUE) + " RF]");
            drawHoveringText(list, mouseX - guiLeft, mouseY - guiTop);
        }
    }

    @Override
    public void updateScreen() {
        super.updateScreen();
        tierUp.visible = tierDown.visible = toggleGuide.visible = !tile.active.value;
        creativeBuild.visible = player.capabilities.isCreativeMode && !tile.active.value;
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        tile.sendPacketToServer(new PacketTileMessage(tile, (byte)button.id, true, false));
    }
}
