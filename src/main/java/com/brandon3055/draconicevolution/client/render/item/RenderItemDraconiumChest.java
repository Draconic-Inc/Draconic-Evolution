package com.brandon3055.draconicevolution.client.render.item;

import codechicken.lib.model.PerspectiveModelState;
import codechicken.lib.render.item.IItemRenderer;
import codechicken.lib.texture.TextureUtils;
import codechicken.lib.util.TransformUtils;
import com.brandon3055.draconicevolution.client.render.tile.DraconiumChestTileRenderer;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

/**
 * Created by brandon3055 on 18/04/2017.
 */
public class RenderItemDraconiumChest implements IItemRenderer {
    public DraconiumChestTileRenderer renderer = new DraconiumChestTileRenderer(null);

    public RenderItemDraconiumChest() {}

    @Override
    public boolean useAmbientOcclusion() {
        return true;
    }

    @Override
    public boolean isGui3d() {
        return true;
    }

    @Override
    public TextureAtlasSprite getParticleIcon() {
        return TextureUtils.getTexture("draconicevolution:blocks/draconium_block");
    }

    @Override
    public void renderItem(ItemStack stack, ItemDisplayContext context, PoseStack mStack, MultiBufferSource getter, int packedLight, int packedOverlay) {
        int colour = 0x640096;
        if (stack.getTag() != null && stack.getTag().contains("bc_tile_data")) {
            colour = stack.getTag().getCompound("bc_tile_data").getCompound("bc_managed_data").getInt("colour");
        }
        renderer.renderChest(mStack, getter, 180, 0, packedLight, packedOverlay, colour);
    }

    @Override
    public @Nullable PerspectiveModelState getModelState() {
        return TransformUtils.DEFAULT_BLOCK;
    }

    @Override
    public boolean usesBlockLight() {
        return true;
    }
}
