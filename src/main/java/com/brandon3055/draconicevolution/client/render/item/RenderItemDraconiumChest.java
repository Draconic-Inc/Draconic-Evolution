package com.brandon3055.draconicevolution.client.render.item;

import codechicken.lib.render.item.IItemRenderer;
import codechicken.lib.texture.TextureUtils;
import codechicken.lib.util.TransformUtils;
import com.brandon3055.brandonscore.utils.ItemNBTHelper;
import com.brandon3055.draconicevolution.DraconicEvolution;
import com.brandon3055.draconicevolution.blocks.DraconiumChest;
import com.brandon3055.draconicevolution.client.DETextures;
import com.brandon3055.draconicevolution.client.model.ModelDraconiumChest;
import com.brandon3055.draconicevolution.utils.LogHelper;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.block.AbstractBlock;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.model.IModelTransform;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.ToolType;

import java.nio.charset.StandardCharsets;

import static net.minecraft.block.material.Material.METAL;
import static net.minecraft.block.material.MaterialColor.COLOR_GRAY;

/**
 * Created by brandon3055 on 18/04/2017.
 */
public class RenderItemDraconiumChest implements IItemRenderer {

    public RenderItemDraconiumChest() {
    }

    //region Unused

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

    //endregion

    @Override
    public void renderItem(ItemStack stack, ItemCameraTransforms.TransformType transformType, MatrixStack mStack, IRenderTypeBuffer getter, int packedLight, int packedOverlay) {
        ModelDraconiumChest chest = new ModelDraconiumChest(RenderType::entitySolid);
        int color = 0x640096;
        if (stack.getTag() != null) {
            color = ((CompoundNBT)stack.getTag().get("bc_tile_data")).getInt("ChestColour");
        }
        float red = (float) ((color >> 16) & 0xFF) / 255f;
        float green = (float) ((color >> 8) & 0xFF) / 255f;
        float blue = (float) (color & 0xFF) / 255f;
        chest.renderToBuffer(mStack, getter.getBuffer(chest.renderType(new ResourceLocation(DraconicEvolution.MODID, DETextures.DRACONIUM_CHEST))), packedLight, packedOverlay, red, green, blue, 1F);
    }

    @Override
    public IModelTransform getModelTransform() {
        return TransformUtils.DEFAULT_BLOCK;
    }

    @Override
    public boolean usesBlockLight() {
        return false;
    }
}
