package com.brandon3055.draconicevolution.client.render.tile;

import codechicken.lib.raytracer.IndexedCuboid6;
import codechicken.lib.vec.Vector3;
import com.brandon3055.brandonscore.client.render.TESRBase;
import com.brandon3055.draconicevolution.blocks.tileentity.TilePlacedItem;
import com.brandon3055.draconicevolution.utils.LogHelper;
import com.google.common.base.Joiner;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;

import java.util.List;

/**
 * Created by brandon3055 on 25/07/2016.
 */
public class RenderTilePlacedItem extends TESRBase<TilePlacedItem> {

    public RenderTilePlacedItem(TileEntityRendererDispatcher rendererDispatcherIn) {
        super(rendererDispatcherIn);
    }

//    @Override
    public void render(TilePlacedItem te, double x, double y, double z, float partialTicks, int destroyStage) {
        RenderSystem.pushMatrix();
//        GlStateTracker.pushState();
        RenderSystem.translated(x + 0.5, y + 0.5, z + 0.5);

        ItemStack[] stacks = new ItemStack[]{te.inventory.getItem(0), te.inventory.getItem(1), te.inventory.getItem(2), te.inventory.getItem(3)};
        int index = 0;

        List<IndexedCuboid6> cuboids = te.getCachedRenderCuboids();
        for (IndexedCuboid6 cuboid : cuboids) {
            if (index == 4) {
                LogHelper.bigError("Detected illegal render state for placed item at " + te.getBlockPos() + " Index: " + index);
                LogHelper.error("Tile NBT Dump: " + te.save(new CompoundNBT()));
                LogHelper.error("Cuboid List: " + cuboids.size() + " " + Joiner.on(", ").join(cuboids));
                LogHelper.error("Thread: " + Thread.currentThread().getName());
                index = 3;
            }

            ItemStack stack = stacks[(Integer) cuboid.data - 1];
            if (!stack.isEmpty()) {
                RenderSystem.pushMatrix();
                Vector3 center = cuboid.center();//.copy().sub(new Vector3(te.getPos()));
                RenderSystem.translated(center.x - 0.5, center.y - 0.5, center.z - 0.5);

                if (te.facing.getAxis() == Direction.Axis.Y) {
                    RenderSystem.rotatef(90, te.facing.getStepY(), 0, 0);
                }
                else if (te.facing.getAxis() == Direction.Axis.X) {
                    RenderSystem.rotatef(90, 0, -te.facing.getStepX(), 0);
                }
                else if (te.facing == Direction.SOUTH) {
                    RenderSystem.rotatef(180, 0, 1, 0);
                }

                RenderSystem.rotatef((float) te.rotation[index].get() * 22.5F, 0F, 0F, -1F);

                if ((stack.getItem().isEnchantable(stack) || (te.altRenderMode.get() && !(stack.getItem() instanceof BlockItem))) && cuboids.size() == 1) {
                    RenderSystem.scalef(0.8F, 0.8F, 0.8F);
                    RenderSystem.rotatef(180, 0, 1, 0);
//                    Minecraft.getInstance().getItemRenderer().renderItem(stack, ItemCameraTransforms.TransformType.FIXED);
                }
                else if (stack.getItem() instanceof BlockItem) {
                    float f = 0.72F;
                    RenderSystem.scalef(f, f, f);
                    if (te.altRenderMode.get()) {
//                        RenderSystem.rotate(90, 1, 0, 0);
                        RenderSystem.translated(0, 0, -0.2);
                    }
                    else {
                        RenderSystem.rotatef(90, 1, 0, 0);
                    }
//                    Minecraft.getInstance().getItemRenderer().renderItem(stack, ItemCameraTransforms.TransformType.FIXED);
                }
                else {
                    RenderSystem.scalef(0.45F, 0.45F, 0.45F);
                    RenderSystem.rotatef(180, 0, 1, 0);
//                    Minecraft.getInstance().getItemRenderer().renderItem(stack, ItemCameraTransforms.TransformType.FIXED);
                }

                RenderSystem.popMatrix();
            }

            index++;
        }

//        GlStateTracker.popState();
        RenderSystem.popMatrix();
    }
}
