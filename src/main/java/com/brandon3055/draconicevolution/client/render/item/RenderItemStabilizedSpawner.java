package com.brandon3055.draconicevolution.client.render.item;

import codechicken.lib.model.PerspectiveModelState;
import codechicken.lib.render.item.IItemRenderer;
import codechicken.lib.util.TransformUtils;
import com.brandon3055.draconicevolution.init.DEContent;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;


/**
 * Created by brandon3055 on 18/04/2017.
 */
public class RenderItemStabilizedSpawner implements IItemRenderer {

    private static ItemStack[] CORE_RENDER_ITEMS = new ItemStack[]{new ItemStack(DEContent.CORE_DRACONIUM.get()), new ItemStack(DEContent.CORE_WYVERN.get()), new ItemStack(DEContent.CORE_AWAKENED.get()), new ItemStack(DEContent.CORE_CHAOTIC.get())};
    private BakedModel baseModel;

    public RenderItemStabilizedSpawner() {
//        new ModelResourceLocation(DEContent.stabilized_spawner.getRegistryName())
//        ClientProxy.modelHelper.registerPreBakeCallback(modelRegistry -> baseModel = modelRegistry.getModelLoader().);
//        syncableEnum.setIndex(0);
//        syncableStack.setIndex(1);
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

    @Override
    public void renderItem(ItemStack stack, ItemDisplayContext context, PoseStack mStack, MultiBufferSource getter, int packedLight, int packedOverlay) {

    }

    @Override
    public @Nullable PerspectiveModelState getModelState() {
        return TransformUtils.DEFAULT_BLOCK;
    }

    @Override
    public boolean usesBlockLight() {
        return false;
    }


    //Remember GuiInventory.drawEntityOnScreen
//    @Override
//    public void renderItem(ItemStack stack, ItemCameraTransforms.TransformType transformType) {
//        Entity entity = null;
//        SpawnerTier tier = SpawnerTier.BASIC;
//
//        if (stack.hasTagCompound() && stack.getOrCreateSubCompound(BlockBCore.BC_TILE_DATA_TAG).hasKey(BlockBCore.BC_MANAGED_DATA_FLAG, 10)) {
//            CompoundNBT dataTag = stack.getOrCreateSubCompound(BlockBCore.BC_TILE_DATA_TAG).getCompoundTag(BlockBCore.BC_MANAGED_DATA_FLAG);
//            int tierIndex = dataTag.getByte("spawnerTier");
//            if (tierIndex >= 0 && tierIndex < SpawnerTier.values().length) {
//                tier = SpawnerTier.values()[tierIndex];
//            }
//            String mobID = dataTag.getCompoundTag("mobSoul").getCompoundTag("tag").getString("EntityName");
//            entity = mobID.isEmpty() ? null : DEFeatures.mobSoul.getRenderEntity(mobID);
//        }
//
//        Minecraft.getInstance().getRenderItem().renderModel(baseModel, 0xFFFFFFFF);
//
//
//        RenderSystem.pushMatrix();
//        RenderSystem.translate(0.5F, 0, 0.5F);
//
//        if (entity != null) {
//            RenderSystem.pushMatrix();
//            float f = 0.53125F;
//            float f1 = Math.max(entity.width, entity.height);
//
//            if ((double) f1 > 1.0D) {
//                f /= f1;
//            }
//
//            float partialTicks = Minecraft.getInstance().getRenderPartialTicks();
//            RenderSystem.translate(0.0F, 0.4F, 0.0F);
//            RenderSystem.rotate((ClientEventHandler.elapsedTicks + partialTicks) * 10.0F, 0.0F, 1.0F, 0.0F);
//            RenderSystem.translate(0.0F, -0.2F, 0.0F);
//            RenderSystem.rotate(-30.0F, 1.0F, 0.0F, 0.0F);
//            RenderSystem.scale(f, f, f);
//            entity.setLocationAndAngles(0, 0, 0, 0.0F, 0.0F);
//            Minecraft.getInstance().getRenderManager().renderEntity(entity, 0.0D, 0.0D, 0.0D, 0.0F, partialTicks, false);
//            RenderSystem.popMatrix();
//
//            if (transformType != ItemCameraTransforms.TransformType.GROUND) {
//                RenderSystem.enableRescaleNormal();
//                RenderSystem.setActiveTexture(OpenGlHelper.lightmapTexUnit);
//                RenderSystem.disableTexture2D();
//                RenderSystem.setActiveTexture(OpenGlHelper.defaultTexUnit);
//                RenderSystem.disableLighting();
//            }
//
//            RenderSystem.enableBlend();
//            RenderSystem.tryBlendFuncSeparate(RenderSystem.SourceFactor.SRC_ALPHA, RenderSystem.DestFactor.ONE_MINUS_SRC_ALPHA, RenderSystem.SourceFactor.ONE, RenderSystem.DestFactor.ZERO);
//        }
//
//
//        ItemStack core = CORE_RENDER_ITEMS[tier.ordinal()];
//        IBakedModel bakedModel = TESRBase.getStackModel(core);
//        List<BakedQuad> quads = bakedModel.getQuads(null, null, 0);
//
//        RenderSystem.translate(-0.25, 1.225, -0.25);
//        RenderSystem.scale(0.5, 0.5, 0.5);
//        RenderSystem.rotate(90, 1, 0, 0);
//
//        float lastBrightnessX = OpenGlHelper.lastBrightnessX;
//        float lastBrightnessY = OpenGlHelper.lastBrightnessY;
//
//        TextureUtils.bindBlockTexture();
//
//        ModelUtils.renderQuads(quads);
//        RenderSystem.disableLighting();
//        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 200, 200);
//        renderEffect(quads);
//        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, lastBrightnessX, lastBrightnessY);
//        RenderSystem.enableLighting();
//
//        RenderSystem.translate(0, 0, 1.9);
//
//        ModelUtils.renderQuads(quads);
//        RenderSystem.disableLighting();
//        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 200, 200);
//        renderEffect(quads);
//        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, lastBrightnessX, lastBrightnessY);
//        RenderSystem.enableLighting();
//
//        RenderSystem.popMatrix();
//    }
}
