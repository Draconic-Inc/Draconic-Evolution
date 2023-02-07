package com.brandon3055.draconicevolution.client.handler;

import com.brandon3055.draconicevolution.api.capability.DECapabilities;
import com.brandon3055.draconicevolution.api.capability.ModuleHost;
import com.brandon3055.draconicevolution.api.modules.lib.EntityOverridesItemUse;
import com.brandon3055.draconicevolution.api.modules.lib.ModuleEntity;
import com.brandon3055.draconicevolution.items.equipment.IModularItem;
import com.brandon3055.draconicevolution.items.equipment.ModularStaff;
import com.brandon3055.draconicevolution.lib.WTFException;
import com.mojang.blaze3d.vertex.PoseStack;
import net.covers1624.quack.util.CrashLock;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.event.RenderHandEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.LazyOptional;

/**
 * Created by brandon3055 on 9/2/21
 */
public class ModularItemRenderOverrideHandler {
    private static final CrashLock LOCK = new CrashLock("Already Initialized");

    public static void init() {
        LOCK.lock();

        MinecraftForge.EVENT_BUS.addListener(ModularItemRenderOverrideHandler::renderHandEvent);
    }

    private static void renderHandEvent(RenderHandEvent event) {
        Minecraft mc = Minecraft.getInstance();
        AbstractClientPlayer player = mc.player;
        if (player == null) return;
        ItemStack stack = event.getItemStack();

        InteractionHand hand = event.getHand();
        boolean renderingMainHand = hand == InteractionHand.MAIN_HAND;
        if (renderingMainHand && player.isUsingItem() && player.getUsedItemHand() != hand && player.getUseItemRemainingTicks() > 0) {
            ItemStack usingItem = player.getUseItem();
            if (usingItem.getItem() instanceof IModularItem) {
                LazyOptional<ModuleHost> opt = usingItem.getCapability(DECapabilities.MODULE_HOST_CAPABILITY);
                if (opt.isPresent()) {
                    ModuleHost host = opt.orElseThrow(WTFException::new);
                    for (ModuleEntity<?> entity : host.getModuleEntities()) {
                        if (entity instanceof EntityOverridesItemUse override && override.overrideUsingPose(usingItem)) {
                            event.setCanceled(true);
                            return;
                        }
                    }
                }
            }
        }

        if (event.getHand() == InteractionHand.MAIN_HAND && stack.getItem() instanceof ModularStaff) {
            modularItemRenderOverride(stack, event);
            if (event.isCanceled()) return;

            HumanoidArm handside = mc.player.getMainArm();

            boolean rightHand = handside == HumanoidArm.RIGHT;
            float swingProgress = event.getSwingProgress(); //Going to need something custom when
            float equippedProgress = 0;
            PoseStack mStack = event.getPoseStack();
            mStack.pushPose();

            float f5 = -0.3F * Mth.sin(Mth.sqrt(swingProgress) * (float) Math.PI); //Shift Left
            float f6 = 0.05F * Mth.sin(Mth.sqrt(swingProgress) * ((float) Math.PI * 2F));
            float f10 = -0.3F * Mth.sin(swingProgress * (float) Math.PI);//Shift Forward

            int l = rightHand ? 1 : -1;
            mStack.translate((float) l * f5, f6, f10);

            event.setCanceled(true);
            applyItemArmTransform(mStack, handside, equippedProgress);

            mc.getItemInHandRenderer().renderItem(mc.player, stack, rightHand ? ItemTransforms.TransformType.FIRST_PERSON_RIGHT_HAND : ItemTransforms.TransformType.FIRST_PERSON_LEFT_HAND, !rightHand, mStack, event.getMultiBufferSource(), event.getPackedLight());
            mStack.popPose();
        } else if (stack.getItem() instanceof IModularItem) {
            modularItemRenderOverride(stack, event);
        }
    }

    private static void applyItemArmTransform(PoseStack poseStack, HumanoidArm arm, float equippedProg) {
        int i = arm == HumanoidArm.RIGHT ? 1 : -1;
        poseStack.translate((float) i * 0.56F, -0.52F + equippedProg * -0.6F, -0.72F);
    }

    private static void modularItemRenderOverride(ItemStack stack, RenderHandEvent event) {
        Minecraft mc = Minecraft.getInstance();
        AbstractClientPlayer player = mc.player;
        if (player == null || player.isScoping() || !player.isUsingItem() || player.getUseItemRemainingTicks() <= 0 || player.getUsedItemHand() != event.getHand()) return;

        stack.getCapability(DECapabilities.MODULE_HOST_CAPABILITY).ifPresent(host -> {
            for (ModuleEntity<?> entity : host.getModuleEntities()) {
                if (entity instanceof EntityOverridesItemUse override) {
                    if (!override.overrideUsingPose(stack)) return;

                    event.setCanceled(true);

                    ItemInHandRenderer renderer = mc.getItemInHandRenderer();
                    if (event.getHand() == InteractionHand.MAIN_HAND) {
                        renderArmWithItem(event, override, renderer, mc.player, InteractionHand.MAIN_HAND, event.getItemStack(), event.getEquipProgress(), event.getPoseStack(), event.getMultiBufferSource(), event.getPackedLight());
                    } else {
                        renderArmWithItem(event, override, renderer, mc.player, InteractionHand.OFF_HAND, event.getItemStack(), event.getEquipProgress(), event.getPoseStack(), event.getMultiBufferSource(), event.getPackedLight());
                    }
                }
            }
        });
    }

    private static void renderArmWithItem(RenderHandEvent event, EntityOverridesItemUse override, ItemInHandRenderer renderer, AbstractClientPlayer clientPlayer, InteractionHand hand, ItemStack stack, float handHeight, PoseStack poseStack, MultiBufferSource getter, int packedLight) {
        boolean renderingMainHand = hand == InteractionHand.MAIN_HAND;
        HumanoidArm renderingArm = renderingMainHand ? clientPlayer.getMainArm() : clientPlayer.getMainArm().getOpposite();
        boolean rightHanded = renderingArm == HumanoidArm.RIGHT;

        poseStack.pushPose();
        applyItemArmTransform(poseStack, renderingArm, handHeight);

        override.modifyFirstPersonUsingPose(event, !rightHanded);

        renderer.renderItem(clientPlayer, stack, rightHanded ? ItemTransforms.TransformType.FIRST_PERSON_RIGHT_HAND : ItemTransforms.TransformType.FIRST_PERSON_LEFT_HAND, !rightHanded, poseStack, getter, packedLight);
        poseStack.popPose();
    }

    public static void modifyPlayerPose(LivingEntity livingEntity, PlayerModel<?> model) {
        ItemStack stack = livingEntity.getUseItem();
        if (!(livingEntity instanceof Player player) || !livingEntity.isUsingItem() || stack.isEmpty() || livingEntity.getUseItemRemainingTicks() <= 0 || !(stack.getItem() instanceof IModularItem)) return;
        boolean mainHand = player.getUsedItemHand() == InteractionHand.MAIN_HAND;
        HumanoidArm arm = mainHand ? player.getMainArm() : player.getMainArm().getOpposite();
        boolean leftHand = arm == HumanoidArm.LEFT;

        stack.getCapability(DECapabilities.MODULE_HOST_CAPABILITY).ifPresent(host -> {
            for (ModuleEntity<?> entity : host.getModuleEntities()) {
                if (entity instanceof EntityOverridesItemUse override) {
                    if (!override.overrideUsingPose(stack)) return;
                    override.modifyPlayerModelPose(player, model, leftHand);
                }
            }
        });
    }
}
