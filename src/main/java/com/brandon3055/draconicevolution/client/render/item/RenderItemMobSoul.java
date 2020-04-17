package com.brandon3055.draconicevolution.client.render.item;

import codechicken.lib.render.item.IItemRenderer;
import codechicken.lib.util.TransformUtils;
import com.brandon3055.draconicevolution.init.DEContent;
import com.brandon3055.draconicevolution.client.handler.ClientEventHandler;
import com.brandon3055.draconicevolution.items.MobSoul;
import com.brandon3055.draconicevolution.utils.LogHelper;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.model.IModelState;

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

    @Override
    public IModelState getTransforms() {
        return TransformUtils.DEFAULT_ITEM;
    }


    //    Remember GuiInventory.drawEntityOnScreen
    @Override
    public void renderItem(ItemStack item, ItemCameraTransforms.TransformType transformType) {
        Entity mob = DEContent.mob_soul.getRenderEntity(item);
        if (brokenMobs.contains(mob)) return;

        try {
            GlStateManager.pushMatrix();
            float scale = 0.6F / Math.max(mob.getWidth(), mob.getHeight());
            GlStateManager.translated(0.5, 0.175, 0.5);
            GlStateManager.scalef(scale, scale, scale);

            if (transformType != ItemCameraTransforms.TransformType.GROUND && transformType != ItemCameraTransforms.TransformType.FIXED) {
                GlStateManager.rotatef((float) Math.sin((ClientEventHandler.elapsedTicks + Minecraft.getInstance().getRenderPartialTicks()) / 50F) * 15F, 1, 0, -0.5F);
                GlStateManager.rotated((ClientEventHandler.elapsedTicks + Minecraft.getInstance().getRenderPartialTicks()) * 3, 0, 1, 0);
            }

            EntityRendererManager rendermanager = Minecraft.getInstance().getRenderManager();
            rendermanager.renderEntity(mob, 0.0D, 0.0D, 0.0D, 0.0F, 1.0F, false);

            if (transformType != ItemCameraTransforms.TransformType.GROUND && transformType != ItemCameraTransforms.TransformType.FIXED) {
//                GlStateManager.enableRescaleNormal();
//                GlStateManager.setActiveTexture(OpenGlHelper.lightmapTexUnit);
//                GlStateManager.disableTexture();
//                GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
//                GlStateManager.disableLighting();

//                RenderHelper.disableStandardItemLighting();
//                GlStateManager.disableRescaleNormal();
//                GlStateManager.activeTexture(GLX.GL_TEXTURE1);
//                GlStateManager.disableTexture();
//                GlStateManager.activeTexture(GLX.GL_TEXTURE0);
            }

            //Some entities like the ender dragon modify the blend state which if not corrected like this breaks inventory rendering.
            GlStateManager.enableBlend();
            GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
            GlStateManager.popMatrix();
        }
        catch (Throwable e) {
            if (MobSoul.randomDisplayList != null) {
                MobSoul.randomDisplayList.remove(mob.getType().getRegistryName().toString());
            } else {
                brokenMobs.add(mob);
                LogHelper.error("Error rendering mob soul! " + mob);
                e.printStackTrace();
            }
        }
    }
}
//      GlStateManager.enableColorMaterial();
//              GlStateManager.pushMatrix();
//              GlStateManager.translatef((float)posX, (float)posY, 50.0F);
//              GlStateManager.scalef((float)(-scale), (float)scale, (float)scale);
//              GlStateManager.rotatef(180.0F, 0.0F, 0.0F, 1.0F);
//              float f = ent.renderYawOffset;
//              float f1 = ent.rotationYaw;
//              float f2 = ent.rotationPitch;
//              float f3 = ent.prevRotationYawHead;
//              float f4 = ent.rotationYawHead;
//              GlStateManager.rotatef(135.0F, 0.0F, 1.0F, 0.0F);
//              RenderHelper.enableStandardItemLighting();
//              GlStateManager.rotatef(-135.0F, 0.0F, 1.0F, 0.0F);
//              GlStateManager.rotatef(-((float)Math.atan((double)(mouseY / 40.0F))) * 20.0F, 1.0F, 0.0F, 0.0F);
//              ent.renderYawOffset = (float)Math.atan((double)(mouseX / 40.0F)) * 20.0F;
//              ent.rotationYaw = (float)Math.atan((double)(mouseX / 40.0F)) * 40.0F;
//              ent.rotationPitch = -((float)Math.atan((double)(mouseY / 40.0F))) * 20.0F;
//              ent.rotationYawHead = ent.rotationYaw;
//              ent.prevRotationYawHead = ent.rotationYaw;
//              GlStateManager.translatef(0.0F, 0.0F, 0.0F);
//              EntityRendererManager entityrenderermanager = Minecraft.getInstance().getRenderManager();
//              entityrenderermanager.setPlayerViewY(180.0F);
//              entityrenderermanager.setRenderShadow(false);
//              entityrenderermanager.renderEntity(ent, 0.0D, 0.0D, 0.0D, 0.0F, 1.0F, false);
//              entityrenderermanager.setRenderShadow(true);
//              ent.renderYawOffset = f;
//              ent.rotationYaw = f1;
//              ent.rotationPitch = f2;
//              ent.prevRotationYawHead = f3;
//              ent.rotationYawHead = f4;
//              GlStateManager.popMatrix();
//              RenderHelper.disableStandardItemLighting();
//              GlStateManager.disableRescaleNormal();
//              GlStateManager.activeTexture(GLX.GL_TEXTURE1);
//              GlStateManager.disableTexture();
//              GlStateManager.activeTexture(GLX.GL_TEXTURE0);