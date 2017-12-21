package com.brandon3055.draconicevolution.client.render.item;

import codechicken.lib.model.ModelRegistryHelper;
import codechicken.lib.render.item.IItemRenderer;
import codechicken.lib.texture.TextureUtils;
import codechicken.lib.util.TransformUtils;
import com.brandon3055.brandonscore.client.render.TESRBase;
import com.brandon3055.brandonscore.utils.ModelUtils;
import com.brandon3055.draconicevolution.DEFeatures;
import com.brandon3055.draconicevolution.blocks.tileentity.TileStabilizedSpawner.SpawnerTier;
import com.brandon3055.draconicevolution.client.handler.ClientEventHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.registry.IRegistry;
import net.minecraftforge.common.model.IModelState;

import java.util.List;
import java.util.function.Function;

import static com.brandon3055.draconicevolution.client.render.tile.RenderTileStabilizedSpawner.renderEffect;

/**
 * Created by brandon3055 on 18/04/2017.
 */
public class RenderItemStabilizedSpawner implements IItemRenderer {

    private static ItemStack[] CORE_RENDER_ITEMS = new ItemStack[]{new ItemStack(DEFeatures.draconicCore), new ItemStack(DEFeatures.wyvernCore), new ItemStack(DEFeatures.awakenedCore), new ItemStack(DEFeatures.chaoticCore)};
    private IBakedModel baseModel;

    public RenderItemStabilizedSpawner(Function<IRegistry<ModelResourceLocation, IBakedModel>, IBakedModel> getter) {
        ModelRegistryHelper.registerPreBakeCallback(modelRegistry -> baseModel = getter.apply(modelRegistry));
//        syncableEnum.setIndex(0);
//        syncableStack.setIndex(1);
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
        return TransformUtils.DEFAULT_BLOCK;
    }

    //Remember GuiInventory.drawEntityOnScreen
    @Override
    public void renderItem(ItemStack stack, ItemCameraTransforms.TransformType transformType) {
        Entity entity = null;
        SpawnerTier tier = SpawnerTier.BASIC;

        if (stack.hasTagCompound() && stack.getOrCreateSubCompound("BCTileData").hasKey("BCManagedData", 10)) {
            NBTTagCompound dataTag = stack.getOrCreateSubCompound("BCTileData").getCompoundTag("BCManagedData");
            int tierIndex = dataTag.getByte("spawnerTier");
            if (tierIndex >= 0 && tierIndex < SpawnerTier.values().length) {
                tier = SpawnerTier.values()[tierIndex];
            }
            String mobID = dataTag.getCompoundTag("mobSoul").getCompoundTag("tag").getString("EntityName");
            entity = mobID.isEmpty() ? null : DEFeatures.mobSoul.getRenderEntity(mobID);
        }

        Minecraft.getMinecraft().getRenderItem().renderModel(baseModel, 0xFFFFFFFF);


        GlStateManager.pushMatrix();
        GlStateManager.translate(0.5F, 0, 0.5F);

        if (entity != null) {
            GlStateManager.pushMatrix();
            float f = 0.53125F;
            float f1 = Math.max(entity.width, entity.height);

            if ((double) f1 > 1.0D) {
                f /= f1;
            }

            float partialTicks = Minecraft.getMinecraft().getRenderPartialTicks();
            GlStateManager.translate(0.0F, 0.4F, 0.0F);
            GlStateManager.rotate((ClientEventHandler.elapsedTicks + partialTicks) * 10.0F, 0.0F, 1.0F, 0.0F);
            GlStateManager.translate(0.0F, -0.2F, 0.0F);
            GlStateManager.rotate(-30.0F, 1.0F, 0.0F, 0.0F);
            GlStateManager.scale(f, f, f);
            entity.setLocationAndAngles(0, 0, 0, 0.0F, 0.0F);
            Minecraft.getMinecraft().getRenderManager().renderEntity(entity, 0.0D, 0.0D, 0.0D, 0.0F, partialTicks, false);
            GlStateManager.popMatrix();

            if (transformType != ItemCameraTransforms.TransformType.GROUND) {
                GlStateManager.enableRescaleNormal();
                GlStateManager.setActiveTexture(OpenGlHelper.lightmapTexUnit);
                GlStateManager.disableTexture2D();
                GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
                GlStateManager.disableLighting();
            }

            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        }


        ItemStack core = CORE_RENDER_ITEMS[tier.ordinal()];
        IBakedModel bakedModel = TESRBase.getStackModel(core);
        List<BakedQuad> quads = bakedModel.getQuads(null, null, 0);

        GlStateManager.translate(-0.25, 1.225, -0.25);
        GlStateManager.scale(0.5, 0.5, 0.5);
        GlStateManager.rotate(90, 1, 0, 0);

        float lastBrightnessX = OpenGlHelper.lastBrightnessX;
        float lastBrightnessY = OpenGlHelper.lastBrightnessY;

        TextureUtils.bindBlockTexture();

        ModelUtils.renderQuads(quads);
        GlStateManager.disableLighting();
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 200, 200);
        renderEffect(quads);
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, lastBrightnessX, lastBrightnessY);
        GlStateManager.enableLighting();

        GlStateManager.translate(0, 0, 1.9);

        ModelUtils.renderQuads(quads);
        GlStateManager.disableLighting();
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 200, 200);
        renderEffect(quads);
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, lastBrightnessX, lastBrightnessY);
        GlStateManager.enableLighting();

        GlStateManager.popMatrix();
    }
}
