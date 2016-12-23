package com.brandon3055.draconicevolution.client.gui;

import com.brandon3055.brandonscore.client.gui.modulargui.MGuiElementBase;
import com.brandon3055.brandonscore.client.gui.modulargui.ModularGuiContainer;
import com.brandon3055.brandonscore.client.gui.modulargui.lib.IMGuiListener;
import com.brandon3055.brandonscore.client.gui.modulargui.modularelements.MGuiBackground;
import com.brandon3055.draconicevolution.blocks.energynet.tileentity.TileCrystalBase;
import com.brandon3055.draconicevolution.inventory.ContainerEnergyCrystal;
import net.minecraft.entity.player.EntityPlayer;

/**
 * Created by brandon3055 on 21/12/2016.
 */
public class GuiEnergyCrystal extends ModularGuiContainer<ContainerEnergyCrystal> implements IMGuiListener {
    private final EntityPlayer player;
    private final TileCrystalBase tile;

    public GuiEnergyCrystal(EntityPlayer player, TileCrystalBase tile) {
        super(new ContainerEnergyCrystal(player, tile));
        this.player = player;
        this.tile = tile;
    }

    @Override
    public void initGui() {
        super.initGui();

        manager.add(MGuiBackground.newGenericBackground(this, guiLeft, guiTop, xSize, ySize));
        manager.initElements();
    }

    @Override
    public void onMGuiEvent(String eventString, MGuiElementBase eventElement) {

    }
}
