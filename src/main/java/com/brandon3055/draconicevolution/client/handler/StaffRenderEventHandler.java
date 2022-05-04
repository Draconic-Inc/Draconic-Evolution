package com.brandon3055.draconicevolution.client.handler;

import com.brandon3055.draconicevolution.items.equipment.ModularStaff;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.HandSide;
import net.minecraft.util.math.MathHelper;
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
        if (event.getHand() == Hand.MAIN_HAND && stack.getItem() instanceof ModularStaff) {
            event.setCanceled(true);
            Minecraft mc = Minecraft.getInstance();
            HandSide handside = mc.player.getMainArm();

            boolean rightHand = handside == HandSide.RIGHT;
            float swingProgress = event.getSwingProgress(); //Going to need something custom when
            float equippedProgress = 0 /* event.getEquipProgress() GET FUCKED RECHARGE ANIMATION!!!!*/;
            MatrixStack mStack = event.getMatrixStack();
            mStack.pushPose();

//            float f5 = -0.4F * MathHelper.sin(MathHelper.sqrt(swingProgress) * (float)Math.PI);
            float f5 = -0.3F * MathHelper.sin(MathHelper.sqrt(swingProgress) * (float)Math.PI); //Shift Left
//            float f6 = 0.2F * MathHelper.sin(MathHelper.sqrt(swingProgress) * ((float)Math.PI * 2F));
            float f6 = 0.05F * MathHelper.sin(MathHelper.sqrt(swingProgress) * ((float)Math.PI * 2F));
//            float f10 = -0.2F * MathHelper.sin(swingProgress * (float)Math.PI);
            float f10 = -0.3F * MathHelper.sin(swingProgress * (float)Math.PI);//Shift Forward

            int l = rightHand ? 1 : -1;
            mStack.translate((float)l * f5, f6, f10);
            transformSideFirstPerson(mStack, handside, equippedProgress);

            mc.getItemInHandRenderer().renderItem(mc.player, stack, rightHand ? ItemCameraTransforms.TransformType.FIRST_PERSON_RIGHT_HAND : ItemCameraTransforms.TransformType.FIRST_PERSON_LEFT_HAND, !rightHand, mStack, event.getBuffers(), event.getLight());
            mStack.popPose();
        }
    }

    private static void transformSideFirstPerson(MatrixStack mStack, HandSide handSide, float equippedProg) {
        int i = handSide == HandSide.RIGHT ? 1 : -1;
        mStack.translate((float)i * 0.56F, -0.52F + equippedProg * -0.6F, -0.72F);
    }
}
