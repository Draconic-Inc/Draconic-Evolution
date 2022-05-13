package com.brandon3055.draconicevolution.client.handler;

import com.brandon3055.draconicevolution.items.equipment.ModularStaff;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.event.RenderHandEvent;
import net.minecraftforge.common.MinecraftForge;

/**
 * Created by brandon3055 on 9/2/21
 */
public class StaffRenderEventHandler {

    public static void init() {
        MinecraftForge.EVENT_BUS.addListener(StaffRenderEventHandler::renderHandEvent);
    }

    private static void renderHandEvent(RenderHandEvent event) {
        ItemStack stack = event.getItemStack();
        if (event.getHand() == InteractionHand.MAIN_HAND && stack.getItem() instanceof ModularStaff) {
            event.setCanceled(true);
            Minecraft mc = Minecraft.getInstance();
            HumanoidArm handside = mc.player.getMainArm();

            boolean rightHand = handside == HumanoidArm.RIGHT;
            float swingProgress = event.getSwingProgress(); //Going to need something custom when
            float equippedProgress = 0 /* event.getEquipProgress() GET FUCKED RECHARGE ANIMATION!!!!*/;
            PoseStack mStack = event.getPoseStack();
            mStack.pushPose();

//            float f5 = -0.4F * MathHelper.sin(MathHelper.sqrt(swingProgress) * (float)Math.PI);
            float f5 = -0.3F * Mth.sin(Mth.sqrt(swingProgress) * (float)Math.PI); //Shift Left
//            float f6 = 0.2F * MathHelper.sin(MathHelper.sqrt(swingProgress) * ((float)Math.PI * 2F));
            float f6 = 0.05F * Mth.sin(Mth.sqrt(swingProgress) * ((float)Math.PI * 2F));
//            float f10 = -0.2F * MathHelper.sin(swingProgress * (float)Math.PI);
            float f10 = -0.3F * Mth.sin(swingProgress * (float)Math.PI);//Shift Forward

            int l = rightHand ? 1 : -1;
            mStack.translate((float)l * f5, f6, f10);
            transformSideFirstPerson(mStack, handside, equippedProgress);

            mc.getItemInHandRenderer().renderItem(mc.player, stack, rightHand ? ItemTransforms.TransformType.FIRST_PERSON_RIGHT_HAND : ItemTransforms.TransformType.FIRST_PERSON_LEFT_HAND, !rightHand, mStack, event.getMultiBufferSource(), event.getPackedLight());
            mStack.popPose();
        }
    }

    private static void transformSideFirstPerson(PoseStack mStack, HumanoidArm handSide, float equippedProg) {
        int i = handSide == HumanoidArm.RIGHT ? 1 : -1;
        mStack.translate((float)i * 0.56F, -0.52F + equippedProg * -0.6F, -0.72F);
    }
}
