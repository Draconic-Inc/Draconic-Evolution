package com.brandon3055.draconicevolution.handlers;

import codechicken.lib.render.CCModel;
import codechicken.lib.render.CCRenderState;
import codechicken.lib.render.RenderUtils;
import codechicken.lib.render.buffer.TransformingVertexConsumer;
import codechicken.lib.vec.Cuboid6;
import codechicken.lib.vec.Matrix4;
import com.brandon3055.brandonscore.lib.ChatHelper;
import com.brandon3055.brandonscore.utils.ItemNBTHelper;
import com.brandon3055.draconicevolution.api.energy.ICrystalBinder;
import com.brandon3055.draconicevolution.api.energy.ICrystalLink;
import com.brandon3055.draconicevolution.api.render.DERenderTypes;
import com.brandon3055.draconicevolution.blocks.energynet.tileentity.TileCrystalBase;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;
import java.util.OptionalDouble;

import static net.minecraft.client.renderer.RenderStateShard.*;

/**
 * Created by brandon3055 on 26/11/2016.
 */
public class BinderHandler {

    /**
     * Handles binding and un-binding an ICrystalLink to the tool.
     * Also passes the click through to the bound ICrystalLink#binderUsed method when appropriate.
     *
     * @return true if an operation occurred (Cancels the right click event)
     */
    public static boolean onBinderUse(Player player, InteractionHand hand, Level world, BlockPos blockClicked, @Nonnull ItemStack binder, Direction sideClicked) {
        BlockEntity tile = world.getBlockEntity(blockClicked);
        boolean isBound = isBound(binder);

        //If the tile is linkable and the player is sneaking bind the tile to the tool.
        if (tile instanceof ICrystalLink && player.isShiftKeyDown()) {
            bind(binder, blockClicked);
            if (world.isClientSide) {
                ChatHelper.sendIndexed(player, Component.translatable("gui.draconicevolution.energy_net.pos_saved_to_tool").withStyle(ChatFormatting.GREEN), TileCrystalBase.MSG_ID);
                player.swing(hand);
            }
            return true;
        }

        //If the tool is not bound but the player clicked on a linkable block then give them a hint.
        //Note: We don't want to do this if they did not click on a linkable block because that would break other mods that implement ICrystalBinder in their tools.
        if (tile instanceof ICrystalLink && !isBound) {
            ChatHelper.sendIndexed(player, Component.translatable("gui.draconicevolution.energy_net.tool_not_bound").withStyle(ChatFormatting.RED), TileCrystalBase.MSG_ID);
            return true;
        }

        //If the tool is bound then we now want to bass the call onto the bound ICrystalLink tile.
        if (isBound) {
            BlockPos boundLinkable = getBound(binder);
            if (boundLinkable.equals(blockClicked)) {
                ChatHelper.sendIndexed(player, Component.translatable("gui.draconicevolution.energy_net.link_to_self").withStyle(ChatFormatting.RED), TileCrystalBase.MSG_ID);
                return true;
            }
            BlockEntity boundTile = world.getBlockEntity(boundLinkable);
            if (boundTile instanceof ICrystalLink) {
                if (((ICrystalLink) boundTile).binderUsed(player, blockClicked, sideClicked)) {
                    player.swing(hand);
                }
            } else {
                ChatHelper.sendIndexed(player, Component.translatable("gui.draconicevolution.energy_net.bound_to_invalid").withStyle(ChatFormatting.RED), TileCrystalBase.MSG_ID);
            }
            return true;
        }

        return false;
    }

    private static boolean isBound(ItemStack stack) {
        return stack.hasTag() && stack.getTag().contains(ICrystalBinder.BINDER_TAG, 11);
    }

    private static void bind(ItemStack stack, BlockPos pos) {
        ItemNBTHelper.getCompound(stack).putIntArray(ICrystalBinder.BINDER_TAG, new int[]{pos.getX(), pos.getY(), pos.getZ()});
    }

    private static BlockPos getBound(ItemStack stack) {
        int[] intArray = stack.getTag().getIntArray(ICrystalBinder.BINDER_TAG);
        return new BlockPos(intArray[0], intArray[1], intArray[2]);
    }

    public static boolean clearBinder(Player player, @Nonnull ItemStack stack) {
        if (stack.hasTag() && stack.getTag().contains(ICrystalBinder.BINDER_TAG)) {
            stack.getTag().remove(ICrystalBinder.BINDER_TAG);
            ChatHelper.sendIndexed(player, Component.translatable("gui.draconicevolution.energy_net.pos_cleared"), TileCrystalBase.MSG_ID);
            return true;
        }
        return false;
    }

    public static Map<AABB, CCModel> modelCache = new HashMap<>();

    @OnlyIn(Dist.CLIENT)
    public static void renderWorldOverlay(LocalPlayer player, PoseStack pStack, Level world, ItemStack stack, Minecraft mc, float partialTicks) {
        if (!isBound(stack)) {
            return;
        }

        BlockPos pos = getBound(stack);
        boolean valid = world.getBlockEntity(pos) instanceof ICrystalLink;

        BlockState state = world.getBlockState(pos);
        VoxelShape shape = state.getShape(world, pos);
        if (shape.isEmpty()) {
            shape = Shapes.block();
        }
        Cuboid6 cuboid6 = new Cuboid6(shape.bounds());


        MultiBufferSource.BufferSource source = Minecraft.getInstance().renderBuffers().bufferSource();
        Camera camera = Minecraft.getInstance().gameRenderer.getMainCamera();
        Vec3 cameraPos = camera.getPosition();

        pStack.pushPose();
        pStack.translate(-cameraPos.x, -cameraPos.y, -cameraPos.z);
        pStack.translate(pos.getX(), pos.getY(), pos.getZ());

        RenderUtils.bufferCuboidSolid(
                new TransformingVertexConsumer(source.getBuffer(DERenderTypes.BOX_NO_DEPTH), pStack),
                cuboid6,
                valid ? 0 : 1, valid ? 1 : 0, 0, 0.5F
        );

        source.endBatch();

        RenderUtils.bufferCuboidOutline(
                new TransformingVertexConsumer(source.getBuffer(DERenderTypes.OUTLINE_TYPE), pStack),
                cuboid6,
                0, 0, 0, 1
        );

        source.endBatch();
        pStack.popPose();
    }

    private static CCModel modelForAABB(AABB aabb) {
        if (!modelCache.containsKey(aabb)) {
            modelCache.put(aabb, CCModel.newModel(VertexFormat.Mode.QUADS, 24).generateBlock(0, new Cuboid6(aabb)));
        }

        return modelCache.get(aabb);
    }
}
