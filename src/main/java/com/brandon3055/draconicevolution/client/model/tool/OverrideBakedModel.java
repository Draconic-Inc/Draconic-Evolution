package com.brandon3055.draconicevolution.client.model.tool;

import com.google.common.collect.ImmutableList;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.*;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

import java.util.List;

/**
 * Created by covers1624 on 5/13/2016.
 * Used as a wrapper to have a an OverrideList control the model. TODO Use CCL
 */
public class OverrideBakedModel implements IBakedModel {

    public OverrideBakedModel() {}

    @Override
    public List<BakedQuad> getQuads(IBlockState state, EnumFacing side, long rand) {
        return ImmutableList.of();
    }

    @Override
    public boolean isAmbientOcclusion() {
        return false;
    }

    @Override
    public boolean isGui3d() {
        return false;
    }

    @Override
    public boolean isBuiltInRenderer() {
        return false;
    }

    @Override
    public TextureAtlasSprite getParticleTexture() {
        return null;
    }

    @Override
    public ItemCameraTransforms getItemCameraTransforms() {
        return ItemCameraTransforms.DEFAULT;
    }

    @Override
    public ItemOverrideList getOverrides() {
        return new ItemOverrideList(ImmutableList.<ItemOverride>of()) {

            @Override
            public IBakedModel handleItemState(IBakedModel originalModel, ItemStack stack, World world, EntityLivingBase entity) {
                if (stack == null || !(stack.getItem() instanceof IDualModel)){
                    return originalModel;
                }

                IBakedModel model = ToolModelLoader.getModel((IDualModel) stack.getItem());

                if (model != null){
                    return model;
                }

                return originalModel;
            }
        };
    }
}