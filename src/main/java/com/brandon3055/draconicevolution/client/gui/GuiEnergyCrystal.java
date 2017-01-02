package com.brandon3055.draconicevolution.client.gui;

import com.brandon3055.brandonscore.client.gui.modulargui.MGuiElementBase;
import com.brandon3055.brandonscore.client.gui.modulargui.ModularGuiContainer;
import com.brandon3055.brandonscore.client.gui.modulargui.lib.EnumAlignment;
import com.brandon3055.brandonscore.client.gui.modulargui.lib.IMGuiListener;
import com.brandon3055.brandonscore.client.gui.modulargui.modularelements.*;
import com.brandon3055.brandonscore.client.particle.BCEffectHandler;
import com.brandon3055.brandonscore.lib.Vec3D;
import com.brandon3055.brandonscore.network.PacketTileMessage;
import com.brandon3055.brandonscore.utils.BlockHelper;
import com.brandon3055.brandonscore.utils.InfoHelper;
import com.brandon3055.brandonscore.utils.Utils;
import com.brandon3055.draconicevolution.blocks.energynet.tileentity.TileCrystalBase;
import com.brandon3055.draconicevolution.blocks.energynet.tileentity.TileCrystalWirelessIO;
import com.brandon3055.draconicevolution.client.render.effect.CrystalFXLink;
import com.brandon3055.draconicevolution.inventory.ContainerEnergyCrystal;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by brandon3055 on 21/12/2016.
 */
public class GuiEnergyCrystal extends ModularGuiContainer<ContainerEnergyCrystal> implements IMGuiListener {
    private final EntityPlayer player;
    private final TileCrystalBase tile;
    private MGuiList linkData;

    public GuiEnergyCrystal(EntityPlayer player, TileCrystalBase tile) {
        super(new ContainerEnergyCrystal(player, tile));
        this.player = player;
        this.tile = tile;
        this.xSize = 190;
        this.ySize = 170;
    }

    @Override
    public void initGui() {
        super.initGui();
        manager.clear();

//        manager.add(MGuiBackground.newGenericBackground(this, guiLeft, guiTop, xSize, ySize));
        manager.add(new MGuiBorderedRect(this, guiLeft, guiTop, xSize, ySize).setBorderColour(0xFF909090).setFillColour(0xFF000000));
        manager.add(new MGuiLabel(this, guiLeft, guiTop + 2, xSize, 12, I18n.format(tile.getUnlocalizedName())).setTextColour(InfoHelper.GUI_TITLE));
        manager.add(new MGuiBorderedRect(this, guiLeft, guiTop + 13, xSize, 1).setBorderColour(0xFF909090));
        manager.add(new MGuiLabel(this, guiLeft, guiTop + 15, xSize, 12, I18n.format("eNet.de.hudLinks.info") + ":").setTextColour(0x00BF00).setAlignment(EnumAlignment.LEFT));
        manager.add(new MGuiBorderedRect(this, guiLeft, guiTop + 26, xSize, 1).setBorderColour(0x30909090));
        manager.add(linkData = new MGuiList(this, guiLeft, guiTop + 26, xSize - 1, ySize - 26));
        linkData.lockScrollBar = true;

        manager.add(new MGuiButtonSolid(this, "CLEAR_L", guiLeft + xSize - 60, guiTop + 14, 60, 12, I18n.format("eNet.de.clearLinks.gui")).setColours(0xFF000000, 0xFFFF0000, 0xFF400090), 1);
        if (tile instanceof TileCrystalWirelessIO) {
            manager.add(new MGuiButtonSolid(this, "CLEAR_R", guiLeft + xSize - 150, guiTop + 14, 90, 12, I18n.format("eNet.de.clearReceivers.gui")).setColours(0xFF000000, 0xFFFF0000, 0xFF400090), 1);
        }
        manager.initElements();
    }

    @Override
    public void onMGuiEvent(String eventString, MGuiElementBase eventElement) {
        if (eventElement instanceof MGuiButton && (((MGuiButton) eventElement).buttonName.startsWith("LINK_") || ((MGuiButton) eventElement).buttonName.startsWith("RECEIVER_"))) {
            int popupXSize = 70;
            int popupYSize = 24;
            MGuiPopUpDialog popup = new MGuiPopUpDialog(this, getMouseX() + popupXSize > guiLeft + xSize ? guiLeft + xSize - popupXSize : getMouseX(), getMouseY() + popupYSize > guiTop + ySize ? getMouseY() - popupYSize : getMouseY(), popupXSize, popupYSize, eventElement);
            popup.id = ((MGuiButton) eventElement).buttonName;
            popup.addChild(new MGuiBorderedRect(this, popup.xPos + popupXSize - 5, popup.yPos - 5, 5, 5).setFillColour(0xFFFF0000).setBorderColour(0xFF000000));
            popup.addChild(new MGuiBorderedRect(this, popup.xPos, popup.yPos - 5, popupXSize - 5, 5).setFillColour(0xFF000000).setBorderColour(0xFF000000));

            popup.addChild(new MGuiButtonSolid(this, "IDENTIFY_", popup.xPos, popup.yPos, popup.xSize, 12, I18n.format("eNet.de.identify.gui")).setColours(0xFF000000, 0xFF00FF00, 0xFF0000FF));
            popup.addChild(new MGuiButtonSolid(this, "UNLINK_", popup.xPos, popup.yPos + 12, popup.xSize, 12, I18n.format("eNet.de.unlink.gui")).setColours(0xFF000000, 0xFF00FF00, 0xFF0000FF));

            manager.add(popup, 1);
        }
        else if (eventElement instanceof MGuiButton && eventElement.parent instanceof MGuiPopUpDialog) {
            String target = eventElement.parent.id;
            int index;
            boolean receiver = true;
            if (target.startsWith("LINK_")) {
                receiver = false;
                index = Utils.parseInt(target.replace("LINK_", ""));
            }
            else {
                index = Utils.parseInt(target.replace("RECEIVER_", ""));
            }

            List<BlockPos> list = null;

            if (receiver && tile instanceof TileCrystalWirelessIO) {
                list = ((TileCrystalWirelessIO) tile).getReceivers();
            }
            else if (!receiver){
                list = tile.getLinks();
            }

            if (list == null || index >= list.size()) {
                return;
            }

            if (((MGuiButton) eventElement).buttonName.startsWith("IDENTIFY_")) {
                CrystalFXLink linkFX;
                linkFX = new CrystalFXLink(mc.theWorld, tile, Vec3D.getCenter(list.get(index)));
                linkFX.timeout = 100;
                linkFX.setScale(2);
                BCEffectHandler.spawnGLParticle(linkFX.getFXHandler(), linkFX);
            }
            else if (((MGuiButton) eventElement).buttonName.startsWith("UNLINK_")) {
                if (receiver) {
                    tile.sendPacketToServer(new PacketTileMessage(tile, (byte) 11, index, false));
                }
                else {
                    tile.sendPacketToServer(new PacketTileMessage(tile, (byte) 10, index, false));
                }
                ((MGuiPopUpDialog) eventElement.parent).close();
            }
        }
        else if (eventElement instanceof MGuiButton && ((MGuiButton) eventElement).buttonName.equals("CLEAR_L")) {
            tile.sendPacketToServer(new PacketTileMessage(tile, (byte) 20, false, false));
        }
        else if (eventElement instanceof MGuiButton && ((MGuiButton) eventElement).buttonName.equals("CLEAR_R")) {
            tile.sendPacketToServer(new PacketTileMessage(tile, (byte) 21, false, false));
        }
    }

    @Override
    public void updateScreen() {

        if (true) {
            linkData.clear();
            List<BlockPos> positions = tile.getLinks();

            Iterator<Map.Entry<Integer, Integer>> i = tile.containerEnergyFlow.entrySet().iterator();

            while (i.hasNext()) {
                Map.Entry<Integer, Integer> next = i.next();
                if (next.getKey() >= tile.getLinks().size()) {
                    i.remove();
                }
            }

            for (int index : tile.containerEnergyFlow.keySet()) {
                MGuiListEntry entry = new MGuiListEntryWrapper(this, new MGuiButtonSolid(this, "LINK_" + index, 0, 0, linkData.xSize - 11, 25, "").setColours(0x50FFFFFF, 0, 0x90FFFFFF));
                entry.xSize = linkData.xSize - 11;

                String tileName = "Unknown Tile...";
                if (index < positions.size()) {
                    tileName = BlockHelper.getBlockName(positions.get(index), mc.theWorld);
                }

                entry.addChild(new MGuiLabel(this, -2, 1, entry.xSize, 12, index + ": " + tileName).setAlignment(EnumAlignment.LEFT).setTextColour(0xF08300).setTrim(true));

                String flow = I18n.format("eNet.de.output.info") + ": " + Utils.formatNumber(tile.containerEnergyFlow.get(index)) + " RF/t";
                entry.addChild(new MGuiLabel(this, -2, 12, entry.xSize, 12, flow).setAlignment(EnumAlignment.LEFT).setTextColour(0x00CCCC));

                linkData.addEntry(entry);
            }

            if (tile instanceof TileCrystalWirelessIO) {
                TileCrystalWirelessIO te = (TileCrystalWirelessIO) tile;
                positions = te.getReceivers();

                Iterator<Map.Entry<Integer, Integer>> i2 = te.containerReceiverFlow.entrySet().iterator();

                while (i2.hasNext()) {
                    Map.Entry<Integer, Integer> next = i2.next();
                    if (next.getKey() >= positions.size()) {
                        i2.remove();
                    }
                }

                for (int index : te.containerReceiverFlow.keySet()) {
                    MGuiListEntry entry = new MGuiListEntryWrapper(this, new MGuiButtonSolid(this, "RECEIVER_" + index, 0, 0, linkData.xSize - 11, 25, "").setColours(0x50FFFFFF, 0, 0x90FFFFFF));
                    entry.xSize = linkData.xSize - 11;

                    String tileName = "Unknown Tile...";
                    if (index < positions.size()) {
                        tileName = BlockHelper.getBlockName(positions.get(index), mc.theWorld);
                    }
                    entry.addChild(new MGuiLabel(this, -2, 1, entry.xSize, 12, index + ": " + tileName).setAlignment(EnumAlignment.LEFT).setTextColour(0xFF4000).setTrim(true));

                    String flow = I18n.format("eNet.de.output.info") + ": " + Utils.formatNumber(te.containerReceiverFlow.get(index)) + " RF/t";
                    entry.addChild(new MGuiLabel(this, -2, 12, entry.xSize, 12, flow).setAlignment(EnumAlignment.LEFT).setTextColour(0x00CCCC));


                    linkData.addEntry(entry);
                }
            }

        }




        super.updateScreen();
    }
}
