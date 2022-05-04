package com.brandon3055.draconicevolution.integration.equipment;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import top.theillusivec4.curios.api.type.capability.ICurio;

import java.util.List;

/**
 * Created by brandon3055 on 6/1/21
 * Will pass though more functions as needed
 */
public class CurioWrapper implements ICurio {
    private IDEEquipment item;
    private ItemStack stack;

    public CurioWrapper(ItemStack stack) {
        this.item = (IDEEquipment) stack.getItem();
        this.stack = stack;
    }

    @Override
    public void curioTick(String identifier, int index, LivingEntity livingEntity) {
        item.equipmentTick(stack, livingEntity);
    }

//    @Override
//    public void curioAnimate(String identifier, int index, LivingEntity livingEntity) {
//
//    }

//    @Override
//    public boolean canUnequip(String identifier, LivingEntity livingEntity) {
//        return false;
//    }


    @Override
    public boolean canEquip(String identifier, LivingEntity livingEntity) {
        return item.canEquip(livingEntity, identifier);
    }

    @Override
    public List<ITextComponent> getTagsTooltip(List<ITextComponent> tagTooltips) {
        return item.getTagsTooltip(stack, tagTooltips);
    }

//    @Override
//    public void playRightClickEquipSound(LivingEntity livingEntity) {
//
//    }

    @Override
    public boolean canRightClickEquip() {
        return item.canRightClickEquip(stack);
    }

//    @Override
//    public void curioBreak(ItemStack stack, LivingEntity livingEntity) {
//
//    }

//    @Nonnull
//    @Override
//    public DropRule getDropRule(LivingEntity livingEntity) {
//        return DropRule.DEFAULT;
//    }
//
//    @Override
//    public boolean showAttributesTooltip(String identifier) {
//        return false;
//    }

//    @Override
//    public boolean canRender(String identifier, int index, LivingEntity livingEntity) {
//        return false;
//    }
//
    @Override
    public void render(String identifier, int index, MatrixStack matrixStack, IRenderTypeBuffer renderTypeBuffer, int light, LivingEntity livingEntity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
//        item.renderEquipment(matrixStack, renderTypeBuffer, light, light, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch);
    }
}
