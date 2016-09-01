package com.brandon3055.draconicevolution.client.render.tile;

import codechicken.lib.render.CCModel;
import codechicken.lib.render.CCOBJParser;
import codechicken.lib.render.CCRenderState;
import codechicken.lib.render.RenderUtils;
import codechicken.lib.vec.Matrix4;
import codechicken.lib.vec.Rotation;
import codechicken.lib.vec.Vector3;
import com.brandon3055.brandonscore.client.render.TESRBase;
import com.brandon3055.draconicevolution.blocks.tileentity.TileChaosCrystal;
import com.brandon3055.draconicevolution.client.handler.ClientEventHandler;
import com.brandon3055.draconicevolution.helpers.ResourceHelperDE;
import com.brandon3055.draconicevolution.utils.DETextures;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.opengl.GL11;

import java.util.Map;

/**
 * Created by brandon3055 on 24/9/2015.
 */
public class RenderTileChaosCrystal extends TESRBase<TileChaosCrystal> {
    private CCModel model;

    public RenderTileChaosCrystal() {
        Map<String, CCModel> map = CCOBJParser.parseObjModels(ResourceHelperDE.getResource("models/chaos_crystal.obj"));
        model = CCModel.combine(map.values());
    }

    @Override
    public void renderTileEntityAt(TileChaosCrystal te, double x, double y, double z, float partialTicks, int destroyStage) {

        ResourceHelperDE.bindTexture(DETextures.CHAOS_CRYSTAL);
        CCRenderState.startDrawing(GL11.GL_TRIANGLES, DefaultVertexFormats.POSITION_TEX);
        Matrix4 mat = RenderUtils.getMatrix(new Vector3(x + 0.5, y + 0.5, z + 0.5), new Rotation((ClientEventHandler.elapsedTicks + partialTicks) / 40F, 0, 1, 0), -1);
        model.render(mat);
        CCRenderState.draw();

        if (!te.guardianDefeated.value) {
            GlStateManager.pushMatrix();
            GlStateManager.translate(0, -4.5, 0);
            Tessellator tessellator = Tessellator.getInstance();
            VertexBuffer buffer = tessellator.getBuffer();
            ResourceHelperDE.bindTexture(ResourceHelperDE.getResourceRAW("textures/entity/beacon_beam.png"));
            GlStateManager.glTexParameterf(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, 10497.0F);
            GlStateManager.glTexParameterf(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, 10497.0F);
            GlStateManager.disableLighting();
            GlStateManager.disableCull();
            OpenGlHelper.glBlendFunc(770, 1, 1, 0);
            float f2 = (float) ClientEventHandler.elapsedTicks + partialTicks;
            float f3 = -f2 * 0.2F - (float) MathHelper.floor_float(-f2 * 0.1F);
            GlStateManager.enableBlend();
            OpenGlHelper.glBlendFunc(770, 771, 1, 0);
            GlStateManager.depthMask(false);

            double size = 0.7F;
            double d30 = 0.2D - size;
            double d4 = 0.2D - size;
            double d6 = 0.8D + size;
            double d8 = 0.2D - size;
            double d10 = 0.2D - size;
            double d12 = 0.8D + size;
            double d14 = 0.8D + size;
            double d16 = 0.8D + size;
            double d18 = 10.0D; //Height
            double d20 = 0.0D;
            double d22 = 1.0D;
            double d24 = (double) (-1.0F + f3);
            double d26 = d18 + d24;
            buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);
            buffer.pos(x + d30, y + d18, z + d4).tex(d22, d26).color(200, 0, 0, 62).endVertex();
            buffer.pos(x + d30, y, z + d4).tex(d22, d24).color(200, 0, 0, 62).endVertex();
            buffer.pos(x + d6, y, z + d8).tex(d20, d24).color(200, 0, 0, 62).endVertex();
            buffer.pos(x + d6, y + d18, z + d8).tex(d20, d26).color(200, 0, 0, 62).endVertex();
            buffer.pos(x + d14, y + d18, z + d16).tex(d22, d26).color(200, 0, 0, 62).endVertex();
            buffer.pos(x + d14, y, z + d16).tex(d22, d24).color(200, 0, 0, 62).endVertex();
            buffer.pos(x + d10, y, z + d12).tex(d20, d24).color(200, 0, 0, 62).endVertex();
            buffer.pos(x + d10, y + d18, z + d12).tex(d20, d26).color(200, 0, 0, 62).endVertex();
            buffer.pos(x + d6, y + d18, z + d8).tex(d22, d26).color(200, 0, 0, 62).endVertex();
            buffer.pos(x + d6, y, z + d8).tex(d22, d24).color(200, 0, 0, 62).endVertex();
            buffer.pos(x + d14, y, z + d16).tex(d20, d24).color(200, 0, 0, 62).endVertex();
            buffer.pos(x + d14, y + d18, z + d16).tex(d20, d26).color(200, 0, 0, 62).endVertex();
            buffer.pos(x + d10, y + d18, z + d12).tex(d22, d26).color(200, 0, 0, 62).endVertex();
            buffer.pos(x + d10, y, z + d12).tex(d22, d24).color(200, 0, 0, 62).endVertex();
            buffer.pos(x + d30, y, z + d4).tex(d20, d24).color(200, 0, 0, 62).endVertex();
            buffer.pos(x + d30, y + d18, z + d4).tex(d20, d26).color(200, 0, 0, 62).endVertex();
            tessellator.draw();
            GlStateManager.enableLighting();
            GlStateManager.depthMask(true);
            GlStateManager.popMatrix();
        }
    }
}
