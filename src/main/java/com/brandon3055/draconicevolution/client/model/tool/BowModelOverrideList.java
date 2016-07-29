package com.brandon3055.draconicevolution.client.model.tool;

import codechicken.lib.model.loader.CCBakedModelLoader;
import codechicken.lib.util.ItemNBTUtils;
import com.google.common.collect.ImmutableList;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemOverride;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.entity.EntityLivingBase;
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

        if (entity != null && entity.getActiveItemStack() == stack) {
            ItemNBTUtils.setInteger(copy, "DrawStage", Math.min(entity.getItemInUseMaxCount() / 10, 3)); //todo calculate the actual draw stage
        }

        IBakedModel model = CCBakedModelLoader.getModel(copy);
        if (model == null) {
            return originalModel;
        }
        return model;
    }
}
