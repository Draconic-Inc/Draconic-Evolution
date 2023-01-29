package com.brandon3055.draconicevolution.client.render.block;

import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.IItemRenderer;

import org.lwjgl.opengl.GL11;

import com.brandon3055.draconicevolution.common.tileentities.energynet.TileEnergyRelay;
import com.brandon3055.draconicevolution.common.tileentities.energynet.TileEnergyTransceiver;
import com.brandon3055.draconicevolution.common.tileentities.energynet.TileWirelessEnergyTransceiver;

/**
 * Created by Brandon on 16/02/2015.
 */
public class RenderCrystal implements IItemRenderer {

    private TileEnergyRelay relay1;
    private TileEnergyRelay relay2;
    private TileEnergyTransceiver transceiver1;
    private TileEnergyTransceiver transceiver2;
    private TileWirelessEnergyTransceiver wirelessTransceiver1;
    private TileWirelessEnergyTransceiver wirelessTransceiver2;

    public RenderCrystal() {
        relay1 = new TileEnergyRelay(0);
        relay2 = new TileEnergyRelay(1);
        transceiver1 = new TileEnergyTransceiver(0);
        transceiver2 = new TileEnergyTransceiver(1);
        wirelessTransceiver1 = new TileWirelessEnergyTransceiver(0);
        wirelessTransceiver2 = new TileWirelessEnergyTransceiver(1);
        transceiver1.facing = 1;
        transceiver2.facing = 1;
    }

    @Override
    public boolean handleRenderType(ItemStack item, ItemRenderType type) {
        return true;
    }

    @Override
    public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item, ItemRendererHelper helper) {
        return true;
    }

    @Override
    public void renderItem(ItemRenderType type, ItemStack item, Object... data) {
        GL11.glPushMatrix();
        GL11.glScalef(1.8F, 1.8F, 1.8F);
        if (type == ItemRenderType.ENTITY) {
            GL11.glTranslatef(-0.5F, 0.0F, -0.5F);
        } else if (type == ItemRenderType.EQUIPPED) {
            GL11.glRotated(45, -1, 0, 1);
            if (item.getItemDamage() < 2) GL11.glTranslatef(0.0F, -0.3F, 0.0F);
            else GL11.glTranslatef(0.0F, -0.6F, 0.0F);
        }
        int meta = item.getItemDamage();
        TileEntityRendererDispatcher.instance.renderTileEntityAt(
                meta == 0 ? relay1
                        : meta == 1 ? relay2
                                : meta == 2 ? transceiver1
                                        : meta == 3 ? transceiver2
                                                : meta == 4 ? wirelessTransceiver1 : wirelessTransceiver2,
                0.0D,
                0.0D,
                0.0D,
                0.0F);
        GL11.glPopMatrix();
        RenderHelper.enableStandardItemLighting();
    }
}
