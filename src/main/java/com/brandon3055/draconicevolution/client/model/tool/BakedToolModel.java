package com.brandon3055.draconicevolution.client.model.tool;

import codechicken.lib.render.TransformUtils;
import com.google.common.collect.ImmutableList;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.client.model.IPerspectiveAwareModel;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nullable;
import javax.vecmath.Matrix4f;
import java.util.List;

/**
 * Created by brandon3055 on 5/06/2016.
 */
public class BakedToolModel implements IPerspectiveAwareModel {

    private final ImmutableList<BakedQuad> quads;
    private final TextureAtlasSprite particle;
    private final IBakedModel objModel;

    public BakedToolModel(ImmutableList<BakedQuad> quads, TextureAtlasSprite particle, IBakedModel objModel){
        this.quads = quads;
        this.particle = particle;
        this.objModel = objModel;
    }

    //region IPerspectiveAwareModel

    @Override
    public Pair<? extends IBakedModel, Matrix4f> handlePerspective(ItemCameraTransforms.TransformType cameraTransformType) {
        if (cameraTransformType == ItemCameraTransforms.TransformType.GUI){
            return IPerspectiveAwareModel.MapWrapper.handlePerspective(this, TransformUtils.DEFAULT_TOOL.getTransforms(), cameraTransformType);
        }
        else if (cameraTransformType == ItemCameraTransforms.TransformType.GROUND) {
            return IPerspectiveAwareModel.MapWrapper.handlePerspective(objModel, TransformUtils.DEFAULT_ITEM.getTransforms(), cameraTransformType);
        }
        else {
            return IPerspectiveAwareModel.MapWrapper.handlePerspective(objModel, TransformUtils.DEFAULT_TOOL.getTransforms(), cameraTransformType);
        }
    }

    @Override
    public List<BakedQuad> getQuads(@Nullable IBlockState state, @Nullable EnumFacing side, long rand) {
        if (side == null) {
            return quads;
        }
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
        return particle;
    }

    @Override
    public ItemCameraTransforms getItemCameraTransforms() {
        return ItemCameraTransforms.DEFAULT;
    }

    @Override
    public ItemOverrideList getOverrides() {
        return ItemOverrideList.NONE;
    }

    //endregion
}
