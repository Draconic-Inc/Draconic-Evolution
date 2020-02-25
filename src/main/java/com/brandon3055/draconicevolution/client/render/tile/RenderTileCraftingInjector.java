package com.brandon3055.draconicevolution.client.render.tile;

import com.brandon3055.brandonscore.client.render.TESRBase;
import com.brandon3055.draconicevolution.blocks.machines.CraftingInjector;
import com.brandon3055.draconicevolution.blocks.tileentity.TileCraftingInjector;
import com.brandon3055.draconicevolution.client.handler.ClientEventHandler;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.block.BlockState;
import net.minecraft.util.Direction;

public class RenderTileCraftingInjector extends TESRBase<TileCraftingInjector> {

    @Override
    public void render(TileCraftingInjector te, double x, double y, double z, float partialTicks, int destroyStage) {
        if (te.currentCraftingInventory != null && te.currentCraftingInventory.getCraftingStage() > 1000) {
            return;
        }

        if (!te.itemHandler.getStackInSlot(0).isEmpty()) {
            BlockState state = te.getWorld().getBlockState(te.getPos());
            GlStateManager.pushMatrix();
            Direction facing = state.get(CraftingInjector.FACING);
            GlStateManager.translated(x + 0.5 + (facing.getXOffset() * 0.45), y + 0.5 + (facing.getYOffset() * 0.45), z + 0.5 + (facing.getZOffset() * 0.45));
            GlStateManager.scalef(0.5F, 0.5F, 0.5F);

            if (facing.getAxis() == Direction.Axis.Y) {
                if (facing == Direction.DOWN) {
                    GlStateManager.rotated(180, 1, 0, 0);
                }
            }
            else {
                GlStateManager.rotated(90, facing.getZOffset(), 0, facing.getXOffset() * -1);
            }

            GlStateManager.rotatef((ClientEventHandler.elapsedTicks + partialTicks) * 0.8F, 0F, -1F, 0F);

            renderItem(te.itemHandler.getStackInSlot(0));

            GlStateManager.popMatrix();
        }
    }
}
