package com.brandon3055.draconicevolution.handlers;

import codechicken.lib.render.CCModel;
import codechicken.lib.render.RenderUtils;
import codechicken.lib.render.state.GlStateTracker;
import codechicken.lib.vec.Cuboid6;
import com.brandon3055.brandonscore.lib.ChatHelper;
import com.brandon3055.brandonscore.utils.ItemNBTHelper;
import com.brandon3055.draconicevolution.api.ICrystalBinder;
import com.brandon3055.draconicevolution.api.ICrystalLink;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
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
    public static boolean onBinderUse(EntityPlayer player, EnumHand hand, World world, BlockPos blockClicked, @Nonnull ItemStack binder, EnumFacing sideClicked) {
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
        return stack.hasTagCompound() && stack.getTagCompound().hasKey(ICrystalBinder.BINDER_TAG, 11);
    }

    private static void bind(ItemStack stack, BlockPos pos) {
        ItemNBTHelper.getCompound(stack).setIntArray(ICrystalBinder.BINDER_TAG, new int[]{pos.getX(), pos.getY(), pos.getZ()});
    }

    private static BlockPos getBound(ItemStack stack) {
        int[] intArray = stack.getTagCompound().getIntArray(ICrystalBinder.BINDER_TAG);
        return new BlockPos(intArray[0], intArray[1], intArray[2]);
    }

    public static boolean clearBinder(EntityPlayer player, @Nonnull ItemStack stack) {
        if (stack.hasTagCompound() && stack.getTagCompound().hasKey(ICrystalBinder.BINDER_TAG)) {
            stack.getTagCompound().removeTag(ICrystalBinder.BINDER_TAG);
            ChatHelper.indexedTrans(player, "eNet.de.posCleared.info", TextFormatting.GREEN, -442611624);
            return true;
        }
        return false;
    }

    public static Map<AxisAlignedBB, CCModel> modelCache = new HashMap<>();

    @SideOnly(Side.CLIENT)
    public static void renderWorldOverlay(EntityPlayerSP player, World world, ItemStack stack, Minecraft mc, float partialTicks) {
        if (!isBound(stack)) {
            return;
        }

        BlockPos pos = getBound(stack);
        boolean valid = world.getTileEntity(pos) instanceof ICrystalLink;
        double offsetX = player.prevPosX + (player.posX - player.prevPosX) * (double) partialTicks;
        double offsetY = player.prevPosY + (player.posY - player.prevPosY) * (double) partialTicks;
        double offsetZ = player.prevPosZ + (player.posZ - player.prevPosZ) * (double) partialTicks;

        IBlockState state = world.getBlockState(pos);
        Cuboid6 cuboid6 = new Cuboid6(state.getBlock().getBoundingBox(state, world, pos));

        GlStateManager.pushMatrix();
        GlStateTracker.pushState();
        GlStateManager.translate(pos.getX() - offsetX, pos.getY() - offsetY, pos.getZ() - offsetZ);
        GlStateManager.disableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        GlStateManager.color(valid ? 0 : 1, valid ? 1 : 0, 0, 0.5F);
        GlStateManager.disableDepth();

        RenderUtils.drawCuboidOutline(cuboid6);

        GlStateManager.enableDepth();
        GlStateManager.disableBlend();
        GlStateManager.enableTexture2D();
        GlStateTracker.popState();
        GlStateManager.popMatrix();
    }

    private static CCModel modelForAABB(AxisAlignedBB aabb) {
        if (!modelCache.containsKey(aabb)) {
            modelCache.put(aabb, CCModel.newModel(GL11.GL_QUADS, 24).generateBlock(0, new Cuboid6(aabb)));
        }

        return modelCache.get(aabb);
    }
}
