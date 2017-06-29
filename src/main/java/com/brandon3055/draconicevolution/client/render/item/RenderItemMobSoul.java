package com.brandon3055.draconicevolution.client.render.item;

import codechicken.lib.render.item.IItemRenderer;
import codechicken.lib.util.TransformUtils;
import com.brandon3055.draconicevolution.DEFeatures;
import com.brandon3055.draconicevolution.client.handler.ClientEventHandler;
import com.brandon3055.draconicevolution.items.MobSoul;
import com.brandon3055.draconicevolution.utils.LogHelper;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.client.model.IPerspectiveAwareModel;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nullable;
import javax.vecmath.Matrix4f;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by brandon3055 on 18/04/2017.
 */
public class RenderItemMobSoul implements IItemRenderer, IPerspectiveAwareModel {

    private ItemCameraTransforms.TransformType transformType;

    public RenderItemMobSoul() {
    }

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
        return true;
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
        transformType = cameraTransformType;
        return IPerspectiveAwareModel.MapWrapper.handlePerspective(this, TransformUtils.DEFAULT_ITEM.getTransforms(), cameraTransformType);
    }

    //Remember GuiInventory.drawEntityOnScreen
    @Override
    public void renderItem(ItemStack item) {
        Entity mob = DEFeatures.mobSoul.getRenderEntity(item);

        try {
            GlStateManager.pushMatrix();
            float height = mob.height;
            float scale = 0.6F / height;
            GlStateManager.translate(0.5, 0.175, 0.5);
            GlStateManager.scale(scale, scale, scale);

            if (transformType != ItemCameraTransforms.TransformType.GROUND) {
                GlStateManager.rotate((float) Math.sin((ClientEventHandler.elapsedTicks + Minecraft.getMinecraft().getRenderPartialTicks()) / 50F) * 15F, 1, 0, -0.5F);
                GlStateManager.rotate((ClientEventHandler.elapsedTicks + Minecraft.getMinecraft().getRenderPartialTicks()) * 3, 0, 1, 0);
            }

            RenderManager rendermanager = Minecraft.getMinecraft().getRenderManager();
            rendermanager.doRenderEntity(mob, 0.0D, 0.0D, 0.0D, 0.0F, 1.0F, false);

            if (transformType != ItemCameraTransforms.TransformType.GROUND) {
                GlStateManager.enableRescaleNormal();
                GlStateManager.setActiveTexture(OpenGlHelper.lightmapTexUnit);
                GlStateManager.disableTexture2D();
                GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
                GlStateManager.disableLighting();
            }

            //Some entities like the ender dragon modify the blend state which if not corrected like this breaks inventory rendering.
            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
            GlStateManager.popMatrix();
        }
        catch (Throwable e) {
            if (MobSoul.randomDisplayList != null) {
                MobSoul.randomDisplayList.remove(EntityList.getEntityString(mob));
            }
            else {
                LogHelper.error("Error rendering mob soul! " + mob);
                e.printStackTrace();
            }
        }
    }
}
