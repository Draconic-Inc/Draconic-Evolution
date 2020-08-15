package com.brandon3055.draconicevolution.handlers;

import codechicken.lib.render.CCModel;
import codechicken.lib.render.RenderUtils;
import codechicken.lib.vec.Cuboid6;
import com.brandon3055.brandonscore.lib.ChatHelper;
import com.brandon3055.brandonscore.utils.ItemNBTHelper;
import com.brandon3055.draconicevolution.api.energy.ICrystalBinder;
import com.brandon3055.draconicevolution.api.energy.ICrystalLink;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;

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
    public static boolean onBinderUse(PlayerEntity player, Hand hand, World world, BlockPos blockClicked, @Nonnull ItemStack binder, Direction sideClicked) {
        TileEntity tile = world.getTileEntity(blockClicked);
        boolean isBound = isBound(binder);

        //If the tile is linkable and the player is sneaking bind the tile to the tool.
        if (tile instanceof ICrystalLink && player.isSneaking()) {
            bind(binder, blockClicked);
            if (world.isRemote) {
                ChatHelper.indexedTrans(player, "eNet.de.posSavedToTool.info", TextFormatting.GREEN, -442611624);
                player.swingArm(hand);
            }
            return true;
        }

        //If the tool is not bound but the player clicked on a linkable block then give them a hint.
        //Note: We don't want to do this if they did not click on a linkable block because that would break other mods that implement ICrystalBinder in their tools.
        if (tile instanceof ICrystalLink && !isBound) {
            ChatHelper.indexedTrans(player, "eNet.de.toolNotBound.info", TextFormatting.RED, -442611624);
            return true;
        }

        //If the tool is bound then we now want to bass the call onto the bound ICrystalLink tile.
        if (isBound) {
            BlockPos boundLinkable = getBound(binder);
            if (boundLinkable.equals(blockClicked)) {
                ChatHelper.indexedTrans(player, "eNet.de.linkToSelf.info", TextFormatting.RED, -442611624);
                return true;
            }
            TileEntity boundTile = world.getTileEntity(boundLinkable);
            if (boundTile instanceof ICrystalLink) {
                if (((ICrystalLink) boundTile).binderUsed(player, blockClicked, sideClicked)) {
                    player.swingArm(hand);
                }
            }
            else {
                ChatHelper.indexedTrans(player, "eNet.de.boundToInvalid.info", TextFormatting.RED, -442611624);
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

    public static boolean clearBinder(PlayerEntity player, @Nonnull ItemStack stack) {
        if (stack.hasTag() && stack.getTag().contains(ICrystalBinder.BINDER_TAG)) {
            stack.getTag().remove(ICrystalBinder.BINDER_TAG);
            ChatHelper.indexedTrans(player, "eNet.de.posCleared.info", TextFormatting.GREEN, -442611624);
            return true;
        }
        return false;
    }

    public static Map<AxisAlignedBB, CCModel> modelCache = new HashMap<>();

    @OnlyIn(Dist.CLIENT)
    public static void renderWorldOverlay(ClientPlayerEntity player, World world, ItemStack stack, Minecraft mc, float partialTicks) {
        if (!isBound(stack)) {
            return;
        }

        BlockPos pos = getBound(stack);
        boolean valid = world.getTileEntity(pos) instanceof ICrystalLink;
        ActiveRenderInfo renderInfo = mc.gameRenderer.getActiveRenderInfo();
        double projectedX = renderInfo.getProjectedView().x;
        double projectedY = renderInfo.getProjectedView().y;
        double projectedZ = renderInfo.getProjectedView().z;

        BlockState state = world.getBlockState(pos);

        VoxelShape shape = state.getShape(world, pos);
        if (shape.isEmpty()) {
            shape = VoxelShapes.fullCube();
        }

        Cuboid6 cuboid6 = new Cuboid6(shape.getBoundingBox());

        RenderSystem.pushMatrix();
//        GlStateTracker.pushState();
        RenderSystem.translated((double)pos.getX() - projectedX, (double)pos.getY() - projectedY, (double)pos.getZ() - projectedZ);
        RenderSystem.disableTexture();
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        RenderSystem.color4f(valid ? 0 : 1, valid ? 1 : 0, 0, 0.5F);
        RenderSystem.disableDepthTest();

//        RenderUtils.drawCuboidOutline(cuboid6);

        RenderSystem.enableDepthTest();
        RenderSystem.disableBlend();
        RenderSystem.enableTexture();
//        GlStateTracker.popState();
        RenderSystem.popMatrix();
    }

    private static CCModel modelForAABB(AxisAlignedBB aabb) {
        if (!modelCache.containsKey(aabb)) {
            modelCache.put(aabb, CCModel.newModel(GL11.GL_QUADS, 24).generateBlock(0, new Cuboid6(aabb)));
        }

        return modelCache.get(aabb);
    }
}
