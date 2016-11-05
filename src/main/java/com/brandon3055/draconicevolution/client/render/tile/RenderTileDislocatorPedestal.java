package com.brandon3055.draconicevolution.client.render.tile;

import codechicken.lib.texture.TextureUtils;
import com.brandon3055.brandonscore.client.render.TESRBase;
import com.brandon3055.brandonscore.utils.ModelUtils;
import com.brandon3055.brandonscore.utils.Teleporter;
import com.brandon3055.brandonscore.utils.Utils;
import com.brandon3055.draconicevolution.DEConfig;
import com.brandon3055.draconicevolution.DEFeatures;
import com.brandon3055.draconicevolution.blocks.tileentity.TileDislocatorPedestal;
import com.brandon3055.draconicevolution.items.tools.DislocatorAdvanced;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.RayTraceResult;
import org.lwjgl.opengl.GL11;

import java.util.List;

/**
 * Created by brandon3055 on 27/09/2016.
 */
public class RenderTileDislocatorPedestal extends TESRBase<TileDislocatorPedestal> {

    public static List<BakedQuad> modelQuads = null;

    @Override
    public void renderTileEntityAt(TileDislocatorPedestal te, double x, double y, double z, float partialTicks, int destroyStage) {
        if (modelQuads == null) {
            modelQuads = Minecraft.getMinecraft().getBlockRendererDispatcher().getModelForState(DEFeatures.dislocatorPedestal.getDefaultState()).getQuads(DEFeatures.dislocatorPedestal.getDefaultState(), null, 0);
        }


        GlStateManager.pushMatrix();
        GlStateManager.translate(x + 0.5, y, z + 0.5);

        if (te.getStackInSlot(0) != null) {
            GlStateManager.pushMatrix();
            GL11.glScalef(1F, -1F, -1F);
            drawNameString(te.getStackInSlot(0), 0, te, partialTicks);
            GlStateManager.popMatrix();
        }


        GlStateManager.rotate(-te.rotation.value * 22.5F, 0, 1, 0);
        GlStateManager.translate(-0.5, 0, -0.5);

        TextureUtils.bindBlockTexture();
        ModelUtils.renderQuads(modelQuads);


        if (te.getStackInSlot(0) != null) {
            GlStateManager.translate(0.5, 0.79, 0.52);
            GlStateManager.rotate(-67.5F, 1, 0, 0);
            GlStateManager.scale(0.5F, 0.5F, 0.5F);
            renderItem(te.getStackInSlot(0));
        }

        GlStateManager.popMatrix();

        //TODO render destination name. After i fix the advanced dislocator mess...
    }

    private void drawNameString(ItemStack item, float rotation, TileDislocatorPedestal te, float f) {
        EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;
        RayTraceResult mop = player.rayTrace(10, f);
        boolean isCursorOver = mop != null && mop.typeOfHit == RayTraceResult.Type.BLOCK && mop.getBlockPos().equals(te.getPos());
        boolean isSneaking = player.isSneaking();

        if (!isCursorOver && (isSneaking != DEConfig.invertDPDSB)) {
            return;
        }

        String s = item.hasDisplayName() ? item.getDisplayName() : "";
        if (item.getItem() instanceof DislocatorAdvanced) {
            Teleporter.TeleportLocation location = ((DislocatorAdvanced)item.getItem()).getLocation(item);
            if (location != null) {
                s = location.getName();
            }
        }
        if (s.isEmpty()) {
            return;
        }


        FontRenderer fontRenderer = Minecraft.getMinecraft().fontRendererObj;
        Tessellator tess = Tessellator.getInstance();

        GlStateManager.pushMatrix();
        GlStateManager.scale(0.02f, 0.02f, 0.02f);
        GlStateManager.rotate(180, 0, 1, 0);
        GlStateManager.translate(0, -55, 0);

        double xDiff = player.posX - (te.getPos().getX() + 0.5);
        double yDiff = (player.posY + player.eyeHeight) - (te.getPos().getY() + 0.5);
        double zDiff = player.posZ - (te.getPos().getZ() + 0.5);
        double yawAngle = Math.toDegrees(Math.atan2(zDiff, xDiff));
        double pitchAngle = Math.toDegrees(Math.atan2(yDiff, Utils.getDistanceAtoB(player.posX, player.posY, player.posZ, te.getPos().getX() + 0.5, te.getPos().getY() + 0.5, te.getPos().getZ() + 0.5)));

        GlStateManager.rotate((float) yawAngle + 90 - rotation, 0, 1, 0);
        GlStateManager.rotate((float) -pitchAngle, 1, 0, 0);

        int xmin = -1 - fontRenderer.getStringWidth(s) / 2;
        int xmax = 1 + fontRenderer.getStringWidth(s) / 2;
        int ymin = -1;
        int ymax = fontRenderer.FONT_HEIGHT;

        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GlStateManager.color(0f, 0f, 0f, 0.5f);
        GlStateManager.disableTexture2D();

        VertexBuffer buffer = tess.getBuffer();
        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
        buffer.pos(xmin, ymax, 0).tex(xmin / 64, 1).endVertex();
        buffer.pos(xmax, ymax, 0).tex(xmax / 64, 1).endVertex();
        buffer.pos(xmax, ymin, 0).tex(xmax / 64, 0.75).endVertex();
        buffer.pos(xmin, ymin, 0).tex(xmin / 64, 0.75).endVertex();
        tess.draw();

        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
        GlStateManager.translate(0, 0, -0.1);
        GlStateManager.color(1f, 1f, 1f, 1f);
        GlStateManager.disableLighting();

        fontRenderer.drawString(s, -fontRenderer.getStringWidth(s) / 2, 0, 0xffffff);

        GlStateManager.enableLighting();
        GlStateManager.popMatrix();

    }
}
