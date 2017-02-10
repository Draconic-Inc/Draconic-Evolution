package com.brandon3055.draconicevolution.client.render.item;

import codechicken.lib.render.item.IItemRenderer;
import codechicken.lib.render.state.GlStateManagerHelper;
import codechicken.lib.util.TransformUtils;
import com.brandon3055.draconicevolution.DEFeatures;
import com.brandon3055.draconicevolution.client.render.tile.RenderTileReactorComponent;
import com.brandon3055.draconicevolution.client.render.tile.RenderTileReactorCore;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.client.model.IPerspectiveAwareModel;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nullable;
import javax.vecmath.Matrix4f;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by brandon3055 on 21/11/2016.
 */
public class RenderItemReactorComponent implements IItemRenderer, IPerspectiveAwareModel {

    private static RenderTileReactorCore coreRenderer = new RenderTileReactorCore();

    public RenderItemReactorComponent() {}

    //region Unused
    @Override
    public List<BakedQuad> getQuads(@Nullable IBlockState state, @Nullable EnumFacing side, long rand) {
        return new ArrayList<>();
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
        return true;
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
        return ItemOverrideList.NONE;
    }

    //endregion

    @Override
    public Pair<? extends IBakedModel, Matrix4f> handlePerspective(ItemCameraTransforms.TransformType cameraTransformType) {
        return MapWrapper.handlePerspective(this, TransformUtils.DEFAULT_BLOCK.getTransforms(), cameraTransformType);
    }

    @Override
    public void renderItem(ItemStack item) {
        boolean isCore = item.getItem() == Item.getItemFromBlock(DEFeatures.reactorCore);
        boolean isPart = false;
        boolean isStabilizer = !isCore && item.getItemDamage() == 0;


        GlStateManager.pushMatrix();
        GlStateManagerHelper.pushState();

        if (isCore) {
            coreRenderer.renderItem();
        }
        else if (isPart) {
            //Render part
        }
        else if (isStabilizer) {
            GlStateManager.translate(0.5, 0.5, 0.5);
            RenderTileReactorComponent.renderStabilizer(25, 0, 1F, 0, true);
        }
        else {
            GlStateManager.translate(0.5, 0.5, 0.5);
            GlStateManager.rotate(90, 1, 0, 0);
            RenderTileReactorComponent.renderInjector(1F, 0, true);
        }

        GlStateManagerHelper.popState();
        GlStateManager.popMatrix();
    }
}
