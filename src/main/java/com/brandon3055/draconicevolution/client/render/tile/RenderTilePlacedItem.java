package com.brandon3055.draconicevolution.client.render.tile;

import codechicken.lib.raytracer.IndexedCuboid6;
import codechicken.lib.vec.Vector3;
import com.brandon3055.brandonscore.client.render.TESRBase;
import com.brandon3055.draconicevolution.blocks.tileentity.TilePlacedItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;

/**
 * Created by brandon3055 on 25/07/2016.
 */
public class RenderTilePlacedItem extends TESRBase<TilePlacedItem> {

    @Override
    public void renderTileEntityAt(TilePlacedItem te, double x, double y, double z, float partialTicks, int destroyStage) {
        GlStateManager.pushMatrix();
        GlStateManager.translate(x + 0.5, y + 0.5, z + 0.5);

        ItemStack[] stacks = new ItemStack[] {te.getStackInSlot(0), te.getStackInSlot(1), te.getStackInSlot(2), te.getStackInSlot(3)};
        int index = 0;
        for (IndexedCuboid6 cuboid : te.getIndexedCuboids()) {
            ItemStack stack = stacks[(Integer)cuboid.data - 1];
            if (stack != null) {
                GlStateManager.pushMatrix();
                Vector3 center = cuboid.center().copy().sub(new Vector3(te.getPos()));
                GlStateManager.translate(center.x - 0.5, center.y - 0.5, center.z - 0.5);

                if (te.facing.getAxis() == EnumFacing.Axis.Y){
                    GlStateManager.rotate(90, te.facing.getFrontOffsetY(), 0, 0);
                }
                else if (te.facing.getAxis() == EnumFacing.Axis.X) {
                    GlStateManager.rotate(90, 0, -te.facing.getFrontOffsetX(), 0);
                }
                else if (te.facing == EnumFacing.SOUTH) {
                    GlStateManager.rotate(180, 0, 1, 0);
                }

                GlStateManager.rotate((float)te.rotation[index].value * 22.5F, 0F, 0F, -1F);

                if (stack.getItem().isItemTool(stack) && te.getIndexedCuboids().size() == 1) {
                    GlStateManager.scale(0.8F, 0.8F, 0.8F);
                    Minecraft.getMinecraft().getRenderItem().renderItem(stack, ItemCameraTransforms.TransformType.FIXED);
                }
                else if (stack.getItem() instanceof ItemBlock) {
                    float f = 0.72F;
                    GlStateManager.scale(f, f, f);
                    GlStateManager.rotate(90, 1, 0, 0);
                    Minecraft.getMinecraft().getRenderItem().renderItem(stack, ItemCameraTransforms.TransformType.FIXED);
                }
                else {
                    GlStateManager.scale(0.45F, 0.45F, 0.45F);
                    Minecraft.getMinecraft().getRenderItem().renderItem(stack, ItemCameraTransforms.TransformType.FIXED);
                }

                GlStateManager.popMatrix();
            }
            index++;
        }

        GlStateManager.popMatrix();
    }
}
