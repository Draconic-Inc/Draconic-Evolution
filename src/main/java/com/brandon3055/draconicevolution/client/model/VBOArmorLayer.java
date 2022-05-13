package com.brandon3055.draconicevolution.client.model;

import com.brandon3055.draconicevolution.integration.equipment.EquipmentManager;
import com.brandon3055.draconicevolution.items.equipment.ModularChestpiece;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.covers1624.quack.util.SneakyUtils;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.DyeableLeatherItem;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.ForgeHooksClient;

/**
 * Created by brandon3055 on 30/6/20
 */
public class VBOArmorLayer<T extends LivingEntity, M extends HumanoidModel<T>, A extends HumanoidModel<T>> extends HumanoidArmorLayer<T, M, A> {

    public VBOArmorLayer(LivingEntityRenderer<T, M> renderer, EntityModelSet modelSet, boolean slim) {
        super(renderer, (A) new HumanoidModel(modelSet.bakeLayer(slim ? ModelLayers.PLAYER_SLIM_INNER_ARMOR : ModelLayers.PLAYER_INNER_ARMOR)), (A) new HumanoidModel(modelSet.bakeLayer(slim ? ModelLayers.PLAYER_SLIM_OUTER_ARMOR : ModelLayers.PLAYER_OUTER_ARMOR)));
    }

    public VBOArmorLayer(LivingEntityRenderer<T, M> renderer, HumanoidArmorLayer<T, M, A> parent) {
        super(renderer, parent.innerModel, parent.outerModel);
    }

    protected void setPartVisibility(A model, EquipmentSlot slot) {
        this.setModelVisible(model);
        switch (slot) {
            case HEAD:
                model.head.visible = true;
                model.hat.visible = true;
                break;
            case CHEST:
                model.body.visible = true;
                model.rightArm.visible = true;
                model.leftArm.visible = true;
                break;
            case LEGS:
                model.body.visible = true;
                model.rightLeg.visible = true;
                model.leftLeg.visible = true;
                break;
            case FEET:
                model.rightLeg.visible = true;
                model.leftLeg.visible = true;
        }
    }

    protected void setModelVisible(A model) {
        model.setAllVisible(false);
    }

    @Override
    protected Model getArmorModelHook(T entity, ItemStack itemStack, EquipmentSlot slot, A model) {
        return ForgeHooksClient.getArmorModel(entity, itemStack, slot, model);
    }

    @Override
    public void render(PoseStack mStack, MultiBufferSource getter, int packedLightIn, T livingEntity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        this.renderArmorPiece(mStack, getter, livingEntity, EquipmentSlot.CHEST, packedLightIn, this.getArmorModel(EquipmentSlot.CHEST), false);
        this.renderArmorPiece(mStack, getter, livingEntity, EquipmentSlot.LEGS, packedLightIn, this.getArmorModel(EquipmentSlot.LEGS), false);
        this.renderArmorPiece(mStack, getter, livingEntity, EquipmentSlot.FEET, packedLightIn, this.getArmorModel(EquipmentSlot.FEET), false);
        this.renderArmorPiece(mStack, getter, livingEntity, EquipmentSlot.HEAD, packedLightIn, this.getArmorModel(EquipmentSlot.HEAD), false);

        ItemStack stack = EquipmentManager.findItem(e -> e.getItem() instanceof ModularChestpiece, livingEntity);
        if (!stack.isEmpty()) {
            this.renderArmorPiece(mStack, getter, livingEntity, EquipmentSlot.CHEST, packedLightIn, this.getArmorModel(EquipmentSlot.CHEST), !livingEntity.getItemBySlot(EquipmentSlot.CHEST).isEmpty());
        }
    }

    private void renderArmorPiece(PoseStack poseStack, MultiBufferSource source, T entity, EquipmentSlot slot, int packedlight, A armorModel, boolean onArmor) {
        ItemStack stack = entity.getItemBySlot(slot);
        if (stack.getItem() instanceof ArmorItem armorItem) {
            if (armorItem.getSlot() == slot) {
                this.getParentModel().copyPropertiesTo(armorModel);
                this.setPartVisibility(armorModel, slot);
                boolean innerModel = slot == EquipmentSlot.LEGS;
                boolean hasFoil = stack.hasFoil();

                Model model;
                if (armorItem instanceof ModularChestpiece) {
                    model = ((ModularChestpiece) armorItem).getChestPieceModel(entity, stack, slot, onArmor);
                } else {
                    model = getArmorModelHook(entity, stack, slot, armorModel); //This should be my custom armor model with all of its properties, rotations, etc copied from the default armor model
                }

                if (model instanceof VBOBipedModel) {
                    @SuppressWarnings("unchecked") VBOBipedModel<T> bpModel = (VBOBipedModel<T>) model;
                    bpModel.render(poseStack, source, entity, stack, packedlight, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
                }

                if (armorItem instanceof DyeableLeatherItem) {
                    int i = ((DyeableLeatherItem) armorItem).getColor(stack);
                    float f = (float) (i >> 16 & 255) / 255.0F;
                    float f1 = (float) (i >> 8 & 255) / 255.0F;
                    float f2 = (float) (i & 255) / 255.0F;
                    this.renderModel(poseStack, source, entity, stack, packedlight, hasFoil, model, f, f1, f2, this.getArmorResource(entity, stack, slot, null));
                    this.renderModel(poseStack, source, entity, stack, packedlight, hasFoil, model, 1.0F, 1.0F, 1.0F, this.getArmorResource(entity, stack, slot, "overlay"));
                } else {
                    this.renderModel(poseStack, source, entity, stack, packedlight, hasFoil, model, 1.0F, 1.0F, 1.0F, this.getArmorResource(entity, stack, slot, null));
                }
            }
        }
    }

    private void renderModel(PoseStack poseStack, MultiBufferSource source, T entity, ItemStack stack, int light, boolean hasFoil, Model model, float red, float green, float blue, ResourceLocation armorResource) {
        if (model instanceof VBOBipedModel<?> bpModel) {
            bpModel.render(poseStack, source, SneakyUtils.unsafeCast(entity), stack, light, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
        } else {
            VertexConsumer consumer = ItemRenderer.getArmorFoilBuffer(source, RenderType.armorCutoutNoCull(armorResource), false, hasFoil);
            model.renderToBuffer(poseStack, consumer, light, OverlayTexture.NO_OVERLAY, red, green, blue, 1.0F);
        }
    }
}
