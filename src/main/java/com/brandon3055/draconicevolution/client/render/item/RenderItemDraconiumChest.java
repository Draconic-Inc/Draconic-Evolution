package com.brandon3055.draconicevolution.client.render.item;

import codechicken.lib.render.item.IItemRenderer;
import codechicken.lib.texture.TextureUtils;
import codechicken.lib.util.TransformUtils;
import com.brandon3055.brandonscore.utils.ItemNBTHelper;
import com.brandon3055.draconicevolution.client.render.tile.RenderTileDraconiumChest;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.client.model.IPerspectiveAwareModel;
import net.minecraftforge.common.model.IModelState;
import org.apache.commons.lang3.tuple.Pair;

import javax.vecmath.Matrix4f;

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
    public IModelState getTransforms() {
        return TransformUtils.DEFAULT_BLOCK;
    }

    //Remember GuiInventory.drawEntityOnScreen

    @Override
    public void renderItem(ItemStack stack, ItemCameraTransforms.TransformType transformType) {
        int colour = ItemNBTHelper.getInteger(stack, "ChestColour", 0x640096);
//        LogHelperBC.logNBT(stack);
        RenderTileDraconiumChest.render(EnumFacing.NORTH, colour, 0, 0, 0, 0, 0, -1);
    }

}
