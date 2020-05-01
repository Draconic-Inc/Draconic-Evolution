package com.brandon3055.draconicevolution.client.render.item;

import codechicken.lib.render.item.IItemRenderer;
import codechicken.lib.texture.TextureUtils;
import codechicken.lib.util.TransformUtils;
import com.brandon3055.brandonscore.utils.ItemNBTHelper;
import com.brandon3055.draconicevolution.client.render.tile.RenderTileDraconiumChest;
import com.google.common.collect.ImmutableMap;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.TransformationMatrix;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;

/**
 * Created by brandon3055 on 18/04/2017.
 */
public class RenderItemDraconiumChest implements IItemRenderer {

    public RenderItemDraconiumChest() {
    }

    //region Unused

    @Override
    public boolean isAmbientOcclusion() {
        return true;
    }

    @Override
    public boolean isGui3d() {
        return true;
    }

    @Override
    public TextureAtlasSprite getParticleTexture() {
        return TextureUtils.getTexture("draconicevolution:blocks/draconium_block");
    }

    //endregion

    @Override
    public void renderItem(ItemStack stack, ItemCameraTransforms.TransformType transformType, MatrixStack mStack, IRenderTypeBuffer getter, int packedLight, int packedOverlay) {
        int colour = ItemNBTHelper.getInteger(stack, "ChestColour", 0x640096);
//        LogHelperBC.logNBT(stack);
        RenderTileDraconiumChest.render(Direction.NORTH, colour, 0, 0, 0, 0, 0, -1);
    }

    @Override
    public ImmutableMap<ItemCameraTransforms.TransformType, TransformationMatrix> getTransforms() {
        return TransformUtils.DEFAULT_BLOCK;
    }

    @Override
    public boolean func_230044_c_() {
        return false;
    }
}
