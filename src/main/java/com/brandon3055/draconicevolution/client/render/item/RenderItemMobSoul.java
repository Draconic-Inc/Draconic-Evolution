package com.brandon3055.draconicevolution.client.render.item;

import codechicken.lib.render.item.IItemRenderer;
import codechicken.lib.util.TransformUtils;
import com.brandon3055.draconicevolution.init.DEContent;
import com.brandon3055.draconicevolution.client.handler.ClientEventHandler;
import com.brandon3055.draconicevolution.items.MobSoul;
import com.brandon3055.draconicevolution.utils.LogHelper;
import com.google.common.collect.ImmutableMap;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.TransformationMatrix;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;

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
    public boolean isAmbientOcclusion() {
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
//        Entity mob = DEContent.mob_soul.getRenderEntity(item);
//        if (brokenMobs.contains(mob)) return;
//
//        try {
//            RenderSystem.pushMatrix();
//            float scale = 0.6F / Math.max(mob.getWidth(), mob.getHeight());
//            RenderSystem.translated(0.5, 0.175, 0.5);
//            RenderSystem.scalef(scale, scale, scale);
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
    public void renderItem(ItemStack stack, ItemCameraTransforms.TransformType transformType, MatrixStack mStack, IRenderTypeBuffer getter, int packedLight, int packedOverlay) {

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