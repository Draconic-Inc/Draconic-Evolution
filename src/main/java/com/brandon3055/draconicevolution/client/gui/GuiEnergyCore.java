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
    private GuiButton activete;
    private GuiButton tierUp;
    private GuiButton tierDown;
    private GuiButton toggleGuide;

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
        buttonList.add(activete    = new GuiButton(0, guiLeft + 100, 0, 80, 20, "Activate-L"));
        buttonList.add(tierUp      = new GuiButton(1, guiLeft + 100, 20, 80, 20, "tierUp-L"));
        buttonList.add(tierDown    = new GuiButton(2, guiLeft + 100, 40, 80, 20, "tierDown-L"));
        buttonList.add(toggleGuide = new GuiButton(3, guiLeft + 100, 60, 80, 20, "toggleGuide-L"));
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
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {

        if (button.id == activete.id){
            tile.sendPacketToServer(new PacketTileMessage(tile, (byte)0, true, false));
        }
        else if (button.id == tierUp.id){
            tile.sendPacketToServer(new PacketTileMessage(tile, (byte)1, true, false));
        }
        else if (button.id == tierDown.id){
            tile.sendPacketToServer(new PacketTileMessage(tile, (byte)2, true, false));
        }
        else if (button.id == toggleGuide.id){
            tile.sendPacketToServer(new PacketTileMessage(tile, (byte)3, true, false));
        }
    }
}
