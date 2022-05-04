package com.brandon3055.draconicevolution.handlers;

import codechicken.lib.render.CCModel;
import codechicken.lib.render.CCRenderState;
import codechicken.lib.render.RenderUtils;
import codechicken.lib.render.buffer.TransformingVertexBuilder;
import codechicken.lib.vec.Cuboid6;
import codechicken.lib.vec.Matrix4;
import com.brandon3055.brandonscore.client.gui.modulargui.GuiElement;
import com.brandon3055.brandonscore.lib.ChatHelper;
import com.brandon3055.brandonscore.utils.ItemNBTHelper;
import com.brandon3055.draconicevolution.api.energy.ICrystalBinder;
import com.brandon3055.draconicevolution.api.energy.ICrystalLink;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Tessellator;
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
import net.minecraft.util.text.TranslationTextComponent;
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
        TileEntity tile = world.getBlockEntity(blockClicked);
        boolean isBound = isBound(binder);

        //If the tile is linkable and the player is sneaking bind the tile to the tool.
        if (tile instanceof ICrystalLink && player.isShiftKeyDown()) {
            bind(binder, blockClicked);
            if (world.isClientSide) {
                ChatHelper.sendIndexed(player, new TranslationTextComponent("gui.draconicevolution.energy_net.pos_saved_to_tool").withStyle(TextFormatting.GREEN), 99);
                player.swing(hand);
            }
            return true;
        }

        //If the tool is not bound but the player clicked on a linkable block then give them a hint.
        //Note: We don't want to do this if they did not click on a linkable block because that would break other mods that implement ICrystalBinder in their tools.
        if (tile instanceof ICrystalLink && !isBound) {
            ChatHelper.sendIndexed(player, new TranslationTextComponent("gui.draconicevolution.energy_net.tool_not_bound").withStyle(TextFormatting.RED), 99);
            return true;
        }

        //If the tool is bound then we now want to bass the call onto the bound ICrystalLink tile.
        if (isBound) {
            BlockPos boundLinkable = getBound(binder);
            if (boundLinkable.equals(blockClicked)) {
                ChatHelper.sendIndexed(player, new TranslationTextComponent("gui.draconicevolution.energy_net.link_to_self").withStyle(TextFormatting.RED), 99);
                return true;
            }
            TileEntity boundTile = world.getBlockEntity(boundLinkable);
            if (boundTile instanceof ICrystalLink) {
                if (((ICrystalLink) boundTile).binderUsed(player, blockClicked, sideClicked)) {
                    player.swing(hand);
                }
            }
            else {
                ChatHelper.sendIndexed(player, new TranslationTextComponent("gui.draconicevolution.energy_net.bound_to_invalid").withStyle(TextFormatting.RED), 99);
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
            ChatHelper.sendIndexed(player, new TranslationTextComponent("gui.draconicevolution.energy_net.pos_cleared"), 99);
            return true;
        }
        return false;
    }

    public static Map<AxisAlignedBB, CCModel> modelCache = new HashMap<>();

    @OnlyIn(Dist.CLIENT)
    public static void renderWorldOverlay(ClientPlayerEntity player, MatrixStack matrixStack, World world, ItemStack stack, Minecraft mc, float partialTicks) {
        if (!isBound(stack)) {
            return;
        }

        Matrix4 mat = new Matrix4(matrixStack);
        BlockPos pos = getBound(stack);
        boolean valid = world.getBlockEntity(pos) instanceof ICrystalLink;
        ActiveRenderInfo renderInfo = mc.gameRenderer.getMainCamera();
        double projectedX = renderInfo.getPosition().x;
        double projectedY = renderInfo.getPosition().y;
        double projectedZ = renderInfo.getPosition().z;

        BlockState state = world.getBlockState(pos);

        VoxelShape shape = state.getShape(world, pos);
        if (shape.isEmpty()) {
            shape = VoxelShapes.block();
        }

        IRenderTypeBuffer.Impl getter = IRenderTypeBuffer.immediate(Tessellator.getInstance().getBuilder());

        Cuboid6 cuboid6 = new Cuboid6(shape.bounds());
        RenderSystem.disableTexture();
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        RenderSystem.disableDepthTest();

        CCRenderState ccrs = CCRenderState.instance();
        mat.translate((double)pos.getX() - projectedX, (double)pos.getY() - projectedY, (double)pos.getZ() - projectedZ);

        IVertexBuilder builder = new TransformingVertexBuilder(getter.getBuffer(GuiElement.transColourType), mat);
        RenderUtils.bufferCuboidSolid(builder, cuboid6, valid ? 0 : 1, valid ? 1 : 0, 0, 0.5F);
        ccrs.draw();

        builder = new TransformingVertexBuilder(getter.getBuffer(RenderType.lines()), mat);
        RenderUtils.bufferCuboidOutline(builder, cuboid6, 0, 0, 0, 1);
        ccrs.draw();

        RenderSystem.enableDepthTest();
        RenderSystem.disableBlend();
        RenderSystem.enableTexture();
    }

    private static CCModel modelForAABB(AxisAlignedBB aabb) {
        if (!modelCache.containsKey(aabb)) {
            modelCache.put(aabb, CCModel.newModel(GL11.GL_QUADS, 24).generateBlock(0, new Cuboid6(aabb)));
        }

        return modelCache.get(aabb);
    }
}
