package com.brandon3055.draconicevolution.client.render.item;

import codechicken.lib.math.MathHelper;
import codechicken.lib.model.PerspectiveModelState;
import codechicken.lib.render.item.IItemRenderer;
import codechicken.lib.util.TransformUtils;
import com.brandon3055.brandonscore.api.TimeKeeper;
import com.brandon3055.draconicevolution.init.DEContent;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by brandon3055 on 18/04/2017.
 */
public class RenderItemMobSoul implements IItemRenderer {

    private static Set<Entity> brokenMobs = new HashSet<>();

    public RenderItemMobSoul() {
    }

    //region Unused

    @Override
    public boolean useAmbientOcclusion() {
        return false;
    }

    @Override
    public boolean isGui3d() {
        return true;
    }

    //endregion


//    //    Remember GuiInventory.drawEntityOnScreen
//    @Override
//    public void renderItem(ItemStack item, ItemCameraTransforms.TransformType transformType) {

//
//        try {
//            RenderSystem.pushMatrix();

//
//            if (transformType != ItemCameraTransforms.TransformType.GROUND && transformType != ItemCameraTransforms.TransformType.FIXED) {
//                RenderSystem.rotatef((float) Math.sin((ClientEventHandler.elapsedTicks + Minecraft.getInstance().getRenderPartialTicks()) / 50F) * 15F, 1, 0, -0.5F);
//                RenderSystem.rotated((ClientEventHandler.elapsedTicks + Minecraft.getInstance().getRenderPartialTicks()) * 3, 0, 1, 0);
//            }
//
//            EntityRendererManager rendermanager = Minecraft.getInstance().getRenderManager();
//            rendermanager.renderEntity(mob, 0.0D, 0.0D, 0.0D, 0.0F, 1.0F, false);
//
//            if (transformType != ItemCameraTransforms.TransformType.GROUND && transformType != ItemCameraTransforms.TransformType.FIXED) {
////                RenderSystem.enableRescaleNormal();
////                RenderSystem.setActiveTexture(OpenGlHelper.lightmapTexUnit);
////                RenderSystem.disableTexture();
////                RenderSystem.setActiveTexture(OpenGlHelper.defaultTexUnit);
////                RenderSystem.disableLighting();
//
////                RenderHelper.disableStandardItemLighting();
////                RenderSystem.disableRescaleNormal();
////                RenderSystem.activeTexture(GLX.GL_TEXTURE1);
////                RenderSystem.disableTexture();
////                RenderSystem.activeTexture(GLX.GL_TEXTURE0);
//            }
//
//            //Some entities like the ender dragon modify the blend state which if not corrected like this breaks inventory rendering.
//            RenderSystem.enableBlend();
//            RenderSystem.blendFuncSeparate(RenderSystem.SourceFactor.SRC_ALPHA, RenderSystem.DestFactor.ONE_MINUS_SRC_ALPHA, RenderSystem.SourceFactor.ONE, RenderSystem.DestFactor.ZERO);
//            RenderSystem.popMatrix();
//        }
//        catch (Throwable e) {
//            if (MobSoul.randomDisplayList != null) {
//                MobSoul.randomDisplayList.remove(mob.getType().getRegistryName().toString());
//            } else {
//                brokenMobs.add(mob);
//                LogHelper.error("Error rendering mob soul! " + mob);
//                e.printStackTrace();
//            }
//        }
//    }

    @Override
    public void renderItem(ItemStack stack, ItemDisplayContext transformType, PoseStack mStack, MultiBufferSource getter, int packedLight, int packedOverlay) {
        Entity mob = DEContent.MOB_SOUL.get().getRenderEntity(stack);
        if (brokenMobs.contains(mob)) return;

        float scale = 1F / Math.max(mob.getBbWidth(), mob.getBbHeight());
        mStack.translate(0.5, 0, 0.5);
        mStack.scale(scale, scale, scale);

        if (transformType != ItemDisplayContext.GROUND && transformType != ItemDisplayContext.FIXED) {
            float rotA = (float) Math.sin((TimeKeeper.getClientTick() + Minecraft.getInstance().getFrameTime()) / 50F) * 15F;
            float rotB = (TimeKeeper.getClientTick() + Minecraft.getInstance().getFrameTime()) * 3;

            mStack.mulPose(new Quaternionf().rotationXYZ(1 * rotA * (float) MathHelper.torad, 0, -0.5F * rotA * (float) MathHelper.torad));
            mStack.mulPose(new Quaternionf().rotationXYZ(0, 1 * rotB * (float) MathHelper.torad, 0));
        }

        EntityRenderDispatcher manager = Minecraft.getInstance().getEntityRenderDispatcher();
        manager.render(mob, 0, 0, 0, 0, 0, mStack, getter, packedLight);
    }

    @Override
    public @Nullable PerspectiveModelState getModelState() {
        return TransformUtils.DEFAULT_BLOCK;
    }

    @Override
    public boolean usesBlockLight() {
        return false;
    }
}