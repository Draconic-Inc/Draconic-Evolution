package com.brandon3055.draconicevolution.client.render.block;

import net.minecraft.client.model.ModelChest;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.IItemRenderer;

import org.lwjgl.opengl.GL11;

import com.brandon3055.draconicevolution.common.tileentities.TileDraconiumChest;
import com.brandon3055.draconicevolution.common.utills.ICustomItemData;

/**
 * Created by Brandon on 16/11/2014.
 */
public class RenderDraconiumChest implements IItemRenderer {

    ModelChest chest;

    public RenderDraconiumChest() {
        chest = new ModelChest();
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

        TileDraconiumChest tile = new TileDraconiumChest();
        if (item.hasTagCompound() && item.getTagCompound().hasKey(ICustomItemData.tagName))
            tile.readDataFromItem(item.getTagCompound().getCompoundTag(ICustomItemData.tagName), item);

        if (type == ItemRenderType.ENTITY) {
            GL11.glTranslated(-0.5D, 0, -0.5D);
        }

        TileEntityRendererDispatcher.instance.renderTileEntityAt(tile, 0.0D, 0.0D, 0.0D, 0.0F);

        GL11.glPopMatrix();
    }
}
