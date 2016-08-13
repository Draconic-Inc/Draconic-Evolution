package com.brandon3055.draconicevolution.client.model.tool;

import codechicken.lib.model.loader.CCBakedModelLoader;
import codechicken.lib.util.ItemNBTUtils;
import com.brandon3055.draconicevolution.handlers.BowHandler;
import com.google.common.collect.ImmutableList;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemOverride;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

/**
 * Created by brandon3055 on 28/07/2016.
 */
public class BowModelOverrideList extends ItemOverrideList {

    public BowModelOverrideList() {
        super(ImmutableList.<ItemOverride>of());
    }

    @Override
    public IBakedModel handleItemState(IBakedModel originalModel, ItemStack stack, World world, EntityLivingBase entity) {
        ItemStack copy = stack.copy();

        if (entity instanceof EntityPlayer && entity.getActiveItemStack() == stack && entity.getItemInUseMaxCount() > 0) {
            BowHandler.BowProperties bowProperties = new BowHandler.BowProperties(copy, (EntityPlayer) entity);
            ItemNBTUtils.setInteger(copy, "DrawStage", (int)Math.min(((double)entity.getItemInUseMaxCount() / bowProperties.getDrawTicks() * 3D), 3)); //todo calculate the actual draw stage
        }

        IBakedModel model = CCBakedModelLoader.getModel(copy);
        if (model == null) {
            return originalModel;
        }
        return model;
    }
}
