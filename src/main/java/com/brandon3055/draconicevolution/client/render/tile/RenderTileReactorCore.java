package com.brandon3055.draconicevolution.client.render.tile;

import codechicken.lib.render.CCModel;
import codechicken.lib.render.CCOBJParser;
import codechicken.lib.render.CCRenderState;
import codechicken.lib.render.RenderUtils;
import codechicken.lib.render.shader.ShaderProgram;
import codechicken.lib.render.state.GlStateManagerHelper;
import codechicken.lib.vec.Matrix4;
import codechicken.lib.vec.Rotation;
import codechicken.lib.vec.Vector3;
import com.brandon3055.brandonscore.client.render.TESRBase;
import com.brandon3055.draconicevolution.blocks.reactor.tileentity.TileReactorCore;
import com.brandon3055.draconicevolution.client.handler.ClientEventHandler;
import com.brandon3055.draconicevolution.client.render.shaders.DEShaders;
import com.brandon3055.draconicevolution.helpers.ResourceHelperDE;
import com.brandon3055.draconicevolution.utils.DETextures;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraftforge.client.MinecraftForgeClient;
import org.lwjgl.opengl.GL11;

import java.util.Map;

/**
 * Created by brandon3055 on 6/11/2016.
 */
public class RenderTileReactorCore extends TESRBase<TileReactorCore> {
    private CCModel model;

    public RenderTileReactorCore() {
        Map<String, CCModel> map = CCOBJParser.parseObjModels(ResourceHelperDE.getResource("models/block/obj_models/reactor_core.obj"));
        model = CCModel.combine(map.values());
    }

    @Override
    public void renderTileEntityAt(TileReactorCore te, double x, double y, double z, float partialTicks, int destroyStage) {
        GlStateManager.pushMatrix();
        GlStateManagerHelper.pushState();
        GlStateManager.disableLighting();
        setLighting(200);
        float scale = 2;

        if (MinecraftForgeClient.getRenderPass() == 0) {
            renderCore(te, x, y, z, partialTicks, 1, scale, DEShaders.useShaders());
        }
        else {
            renderShield(te, x, y, z, partialTicks, 1, scale, DEShaders.useShaders());
        }

        resetLighting();
        GlStateManagerHelper.popState();
        GlStateManager.popMatrix();
    }

    private void renderCore(TileReactorCore te, double x, double y, double z, float partialTicks, float intensity, float scale, boolean useShader) {
        ResourceHelperDE.bindTexture(DETextures.REACTOR_CORE);
        if (useShader) {
            DEShaders.reactorOp.setIntensity(intensity);
            DEShaders.reactorOp.setAnimation((ClientEventHandler.elapsedTicks + partialTicks) / 50F);
            DEShaders.reactor.freeBindShader();
        }


        CCRenderState ccrs = CCRenderState.instance();
        ccrs.startDrawing(GL11.GL_TRIANGLES, DefaultVertexFormats.POSITION_TEX);
        Matrix4 mat = RenderUtils.getMatrix(new Vector3(x + 0.5, y + 0.5, z + 0.5), new Rotation((ClientEventHandler.elapsedTicks + partialTicks) / 400F, 0, 1, 0), scale);
        model.render(ccrs, mat);
        ccrs.draw();

        if (useShader) {
            ShaderProgram.unbindShader();
        }
    }

    private void renderShield(TileReactorCore te, double x, double y, double z, float partialTicks, float intensity, float scale, boolean useShader) {
        ResourceHelperDE.bindTexture(DETextures.REACTOR_SHIELD);
        if (useShader) {
            DEShaders.reactorOp.setIntensity((1F + (float) Math.sin((ClientEventHandler.elapsedTicks + partialTicks) / 50F)) / 2F);
            DEShaders.reactorShield.freeBindShader();
        }

        GlStateManager.enableBlend();
        GlStateManager.alphaFunc(GL11.GL_GREATER, 0);
        CCRenderState ccrs = CCRenderState.instance();
        ccrs.startDrawing(GL11.GL_TRIANGLES, DefaultVertexFormats.POSITION_TEX);
        Matrix4 mat = RenderUtils.getMatrix(new Vector3(x + 0.5, y + 0.5, z + 0.5), new Rotation((ClientEventHandler.elapsedTicks + partialTicks) / 400F, 0, 1, 0), scale * 1.05);
        model.render(ccrs, mat);
        ccrs.draw();
        GlStateManager.alphaFunc(GL11.GL_GREATER, 0.1F);

        if (useShader) {
            ShaderProgram.unbindShader();
        }
    }

}
