package com.brandon3055.draconicevolution.client.render.tile;

import codechicken.lib.texture.TextureUtils;
import com.brandon3055.brandonscore.api.render.GuiHelper;
import com.brandon3055.brandonscore.client.render.RenderUtils;
import com.brandon3055.brandonscore.client.render.TESRBase;
import com.brandon3055.brandonscore.utils.ModelUtils;
import com.brandon3055.brandonscore.utils.TargetPos;
import com.brandon3055.brandonscore.utils.Utils;
import com.brandon3055.draconicevolution.DEOldConfig;
import com.brandon3055.draconicevolution.blocks.tileentity.TileDislocatorPedestal;
import com.brandon3055.draconicevolution.client.handler.ClientEventHandler;
import com.brandon3055.draconicevolution.client.render.item.ToolRenderBase;
import com.brandon3055.draconicevolution.init.DEContent;
import com.brandon3055.draconicevolution.items.tools.Dislocator;
import com.brandon3055.draconicevolution.items.tools.DislocatorAdvanced;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraft.util.math.vector.Vector3i;
import org.lwjgl.opengl.GL11;

import java.util.List;

import static net.minecraft.client.renderer.model.ItemCameraTransforms.TransformType.FIXED;

/**
 * Created by brandon3055 on 27/09/2016.
 */
public class RenderTileDislocatorPedestal extends TileEntityRenderer<TileDislocatorPedestal> {

    public static List<BakedQuad> modelQuads = null;

    public RenderTileDislocatorPedestal(TileEntityRendererDispatcher rendererDispatcherIn) {
        super(rendererDispatcherIn);
    }

    @Override
    public void render(TileDislocatorPedestal tile, float partialTicks, MatrixStack mStack, IRenderTypeBuffer getter, int packedLight, int packedoverlay) {
        if (modelQuads == null) {
            modelQuads = Minecraft.getInstance().getBlockRenderer().getBlockModel(DEContent.dislocator_pedestal.defaultBlockState()).getQuads(DEContent.dislocator_pedestal.defaultBlockState(), null, ModelUtils.rand);
        }

        mStack.pushPose();
        mStack.translate(0.5, 0.5, 0.5);
        mStack.mulPose(new Quaternion(0, -tile.rotation.get() * 22.5F, 0, true));
        mStack.translate(-0.5, -0.5, -0.5);

        IVertexBuilder builder = getter.getBuffer(RenderType.solid());
        int i = 0;
        for (int j = modelQuads.size(); i < j; ++i) {
            BakedQuad bakedquad = modelQuads.get(i);
            builder.addVertexData(mStack.last(), bakedquad, 1F, 1F, 1F, 1F, packedLight, packedLight);
        }

        Minecraft mc = Minecraft.getInstance();
        ItemStack stack = tile.itemHandler.getStackInSlot(0);
        if (!stack.isEmpty()) {
            mStack.pushPose();
            mStack.translate(0.5, 0.79, 0.52);
            mStack.scale(0.5F, 0.5F, 0.5F);
            mStack.mulPose(new Quaternion(-67.5F, 0, 0, true));
            mc.getItemRenderer().renderStatic(stack, FIXED, packedLight, packedoverlay, mStack, getter);
            mStack.popPose();
        }
        ToolRenderBase.endBatch(getter);
        mStack.popPose();
        if (!stack.isEmpty()) {
            drawName(tile, stack, mStack, getter, partialTicks);
        }
    }

    private void drawName(TileDislocatorPedestal tile, ItemStack item, MatrixStack mStack, IRenderTypeBuffer getter, float partialTicks) {
        Minecraft mc = Minecraft.getInstance();
        ClientPlayerEntity player = mc.player;

        RayTraceResult mop = player.pick(10, partialTicks, true);
        boolean isCursorOver = mop instanceof BlockRayTraceResult && ((BlockRayTraceResult) mop).getBlockPos().equals(tile.getBlockPos());
        boolean isSneaking = player.isShiftKeyDown();

        if (!isCursorOver && (isSneaking != DEOldConfig.invertDPDSB)) {
            return;
        }

        String name = item.hasCustomHoverName() ? item.getHoverName().getString() : "";
        if (item.getItem() instanceof DislocatorAdvanced) {
            DislocatorAdvanced.DislocatorTarget location = ((DislocatorAdvanced) item.getItem()).getTargetPos(item, tile.getLevel());
            if (location != null) {
                name = location.getName();
            }
        }
        if (name.isEmpty()) {
            return;
        }

        mStack.pushPose();
        mStack.translate(0.5, 1.125, 0.5);
        mStack.scale(0.02F, 0.02F, 0.02F);
        mStack.mulPose(new Quaternion(0, -90, 180, true));

        double xDiff = player.getX() - (tile.getBlockPos().getX() + 0.5);
        double yDiff = (player.getY() + player.getEyeHeight()) - (tile.getBlockPos().getY() + 1.125);
        double zDiff = player.getZ() - (tile.getBlockPos().getZ() + 0.5);
        double yawAngle = Math.toDegrees(Math.atan2(zDiff, xDiff));
        double pitchAngle = Math.toDegrees(Math.atan2(yDiff, Utils.getDistanceAtoB(player.getX(), player.getY(), player.getZ(), tile.getBlockPos().getX() + 0.5, tile.getBlockPos().getY() + 0.5, tile.getBlockPos().getZ() + 0.5)));

        mStack.mulPose(new Quaternion(0, (float) yawAngle , 0, true));
        mStack.mulPose(new Quaternion((float) -pitchAngle, 0, 0, true));

        int textWidth = mc.font.width(name);
//        GuiHelper.drawRect(getter, mStack, -(textWidth/2F)-2, -1, textWidth + 4, 10, 0x70000000);
        mStack.translate(0, 0, -0.0125);
        mc.font.drawInBatch(name, -(textWidth/2F), 0, 0xffffff, true, mStack.last().pose(), getter, false, 0, 15728880);
        mStack.popPose();
    }

    //    @Override
    public void render(TileDislocatorPedestal te, double x, double y, double z, float partialTicks, int destroyStage) {


        RenderSystem.pushMatrix();
        RenderSystem.translated(x + 0.5, y, z + 0.5);

        if (!te.itemHandler.getStackInSlot(0).isEmpty()) {
            RenderSystem.pushMatrix();
            RenderSystem.scalef(1F, -1F, -1F);
            drawNameString(te.itemHandler.getStackInSlot(0), 0, te, partialTicks);
            RenderSystem.popMatrix();
        }


        RenderSystem.rotatef(-te.rotation.get() * 22.5F, 0, 1, 0);
        RenderSystem.translated(-0.5, 0, -0.5);

        TextureUtils.bindBlockTexture();
//        ModelUtils.renderQuads(modelQuads);


        if (!te.itemHandler.getStackInSlot(0).isEmpty()) {
            RenderSystem.translated(0.5, 0.79, 0.52);
            RenderSystem.rotatef(-67.5F, 1, 0, 0);
            RenderSystem.scalef(0.5F, 0.5F, 0.5F);
//            renderItem(te.itemHandler.getStackInSlot(0));
        }

        RenderSystem.popMatrix();

        //TODO render destination name. After i fix the advanced dislocator mess...
    }

    private void drawNameString(ItemStack item, float rotation, TileDislocatorPedestal te, float f) {
        ClientPlayerEntity player = Minecraft.getInstance().player;
        RayTraceResult mop = player.pick(10, f, true);
        boolean isCursorOver = mop instanceof BlockRayTraceResult && ((BlockRayTraceResult) mop).getBlockPos().equals(te.getBlockPos());
        boolean isSneaking = player.isShiftKeyDown();

        if (!isCursorOver && (isSneaking != DEOldConfig.invertDPDSB)) {
            return;
        }

        String s = item.hasCustomHoverName() ? item.getHoverName().getString() : "";
        if (item.getItem() instanceof DislocatorAdvanced) {
            DislocatorAdvanced.DislocatorTarget location = ((DislocatorAdvanced) item.getItem()).getTargetPos(item, te.getLevel());
            if (location != null) {
                s = location.getName();
            }
        }
        if (s.isEmpty()) {
            return;
        }


        FontRenderer fontRenderer = Minecraft.getInstance().font;
        Tessellator tess = Tessellator.getInstance();

        RenderSystem.pushMatrix();
        RenderSystem.scalef(0.32f, 0.02f, 0.02f);
        RenderSystem.rotatef(180, 0, 1, 0);
        RenderSystem.translated(0, -55, 0);

        double xDiff = player.getX() - (te.getBlockPos().getX() + 0.5);
        double yDiff = (player.getY() + player.getEyeHeight()) - (te.getBlockPos().getY() + 0.5);
        double zDiff = player.getZ() - (te.getBlockPos().getZ() + 0.5);
        double yawAngle = Math.toDegrees(Math.atan2(zDiff, xDiff));
        double pitchAngle = Math.toDegrees(Math.atan2(yDiff, Utils.getDistanceAtoB(player.getX(), player.getY(), player.getZ(), te.getBlockPos().getX() + 0.5, te.getBlockPos().getY() + 0.5, te.getBlockPos().getZ() + 0.5)));

        RenderSystem.rotatef((float) yawAngle + 90 - rotation, 0, 1, 0);
        RenderSystem.rotatef((float) -pitchAngle, 1, 0, 0);

        int xmin = -1 - fontRenderer.width(s) / 2;
        int xmax = 1 + fontRenderer.width(s) / 2;
        int ymin = -1;
        int ymax = fontRenderer.lineHeight;

        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        RenderSystem.color4f(0f, 0f, 0f, 0.5f);
        RenderSystem.disableTexture();

        BufferBuilder buffer = tess.getBuilder();
        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
        buffer.vertex(xmin, ymax, 0).uv(xmin / 64F, 1).endVertex();
        buffer.vertex(xmax, ymax, 0).uv(xmax / 64F, 1).endVertex();
        buffer.vertex(xmax, ymin, 0).uv(xmax / 64F, 0.75F).endVertex();
        buffer.vertex(xmin, ymin, 0).uv(xmin / 64F, 0.75F).endVertex();
        tess.end();

        RenderSystem.enableTexture();
        RenderSystem.disableBlend();
        RenderSystem.translated(0, 0, -0.1);
        RenderSystem.color4f(1f, 1f, 1f, 1f);
        RenderSystem.disableLighting();

        fontRenderer.draw(new MatrixStack(), s, -fontRenderer.width(s) / 2F, 0, 0xffffff);

        RenderSystem.enableLighting();
        RenderSystem.popMatrix();
    }
}
