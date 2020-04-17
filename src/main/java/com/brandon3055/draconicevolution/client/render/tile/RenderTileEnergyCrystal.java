package com.brandon3055.draconicevolution.client.render.tile;

import codechicken.lib.math.MathHelper;
import codechicken.lib.render.CCModel;
import codechicken.lib.render.CCRenderState;
import codechicken.lib.render.OBJParser;
import codechicken.lib.render.RenderUtils;
import codechicken.lib.render.shader.ShaderProgram;
import codechicken.lib.vec.Matrix4;
import codechicken.lib.vec.Rotation;
import codechicken.lib.vec.Scale;
import codechicken.lib.vec.Vector3;
import com.brandon3055.brandonscore.client.render.TESRBase;
import com.brandon3055.brandonscore.lib.Vec3D;
import com.brandon3055.brandonscore.utils.Utils;
import com.brandon3055.draconicevolution.DEConfig;
import com.brandon3055.draconicevolution.blocks.energynet.tileentity.TileCrystalBase;
import com.brandon3055.draconicevolution.blocks.energynet.tileentity.TileCrystalDirectIO;
import com.brandon3055.draconicevolution.client.handler.ClientEventHandler;
import com.brandon3055.draconicevolution.client.render.shaders.DEShaders;
import com.brandon3055.draconicevolution.utils.ResourceHelperDE;
import com.brandon3055.draconicevolution.utils.DETextures;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.math.BlockPos;
import org.lwjgl.opengl.GL11;

import java.util.Map;

/**
 * Created by brandon3055 on 6/11/2016.
 */
public class RenderTileEnergyCrystal extends TESRBase<TileCrystalBase> {

    private CCModel crystalFull;
    private CCModel crystalHalf;
    private CCModel crystalBase;

    private static ShaderProgram shaderProgram;

    public RenderTileEnergyCrystal() {
        Map<String, CCModel> map = OBJParser.parseModels(ResourceHelperDE.getResource("models/block/crystal.obj"));
        crystalFull = CCModel.combine(map.values());
        map = OBJParser.parseModels(ResourceHelperDE.getResource("models/block/crystal_half.obj"));
        crystalHalf = map.get("Crystal");
        crystalBase = map.get("Base");
    }

    @Override
    public void render(TileCrystalBase te, double x, double y, double z, float partialTicks, int destroyStage) {
        te.getFxHandler().renderCooldown = 5;
        GlStateManager.pushMatrix();
//        GlStateTracker.pushState();
        GlStateManager.disableLighting();
        setLighting(200);

        if (te instanceof TileCrystalDirectIO) {
            renderHalfCrystal((TileCrystalDirectIO) te, x, y, z, partialTicks, destroyStage, te.getTier());
        }
        else {
            renderCrystal(te, x, y, z, partialTicks, destroyStage, te.getTier());
        }

        resetLighting();
//        GlStateTracker.popState();
        GlStateManager.popMatrix();
    }

    public void renderCrystal(TileCrystalBase te, double x, double y, double z, float partialTicks, int destroyStage, int tier) {
        boolean trans = false;//MinecraftForgeClient.getRenderPass() == 1;
        CCRenderState ccrs = CCRenderState.instance();
        Matrix4 mat = RenderUtils.getMatrix(new Vector3(x + 0.5, y + 0.5, z + 0.5), new Rotation(180 * MathHelper.torad, 1, 0, 0), -0.5);
        mat.apply(new Rotation((ClientEventHandler.elapsedTicks + partialTicks) / 400F, 0, 1, 0));

        if (destroyStage >= 0) {
            bindTexture(DESTROY_STAGES[destroyStage]);
            ccrs.startDrawing(GL11.GL_TRIANGLES, DefaultVertexFormats.POSITION_TEX);
            crystalFull.render(ccrs, mat);
            ccrs.draw();
            return;
        }

        if (!trans) {
            //Render Crystal
            bindShader(te, x, y, z, partialTicks, tier);
            ccrs.startDrawing(GL11.GL_TRIANGLES, DefaultVertexFormats.POSITION_TEX);
            crystalFull.render(ccrs, mat);
            ccrs.draw();
            releaseShader();
        }
        else if (!(DEShaders.useShaders() && DEConfig.useCrystalShaders)) {
            //Render overlay if shaders are not supported
            ResourceHelperDE.bindTexture(DETextures.ENERGY_CRYSTAL_BASE);
            GlStateManager.enableBlend();
            ccrs.startDrawing(GL11.GL_TRIANGLES, DefaultVertexFormats.POSITION_TEX);
            crystalFull.render(ccrs, mat);
            ccrs.draw();
        }
    }

    public void renderHalfCrystal(TileCrystalDirectIO te, double x, double y, double z, float partialTicks, int destroyStage, int tier) {
        boolean trans = false;//MinecraftForgeClient.getRenderPass() == 1;
        ResourceHelperDE.bindTexture(DETextures.ENERGY_CRYSTAL_BASE);
        CCRenderState ccrs = CCRenderState.instance();
        Matrix4 mat = RenderUtils.getMatrix(new Vector3(x + 0.5, y + 1, z + 0.5), new Rotation(0, 0, 0, 0), -0.5);
        mat.apply(Rotation.sideOrientation(te.facing.get().getOpposite().getIndex(), 0).at(new Vector3(0, 1, 0)));

        if (destroyStage >= 0) {
            bindTexture(DESTROY_STAGES[destroyStage]);
            ccrs.startDrawing(GL11.GL_TRIANGLES, DefaultVertexFormats.POSITION_TEX);
            crystalBase.render(ccrs, mat);
            mat.apply(new Rotation((ClientEventHandler.elapsedTicks + partialTicks) / 400F, 0, 1, 0));
            crystalHalf.render(ccrs, mat);
            ccrs.draw();
            return;
        }


        if (!trans) {
            //Render Base
            ccrs.startDrawing(GL11.GL_TRIANGLES, DefaultVertexFormats.POSITION_TEX);
            crystalBase.render(ccrs, mat);
            ccrs.draw();

            //Apply Crystal Rotation
            mat.apply(new Rotation((ClientEventHandler.elapsedTicks + partialTicks) / 400F, 0, 1, 0));

            //Render Crystal
            ResourceHelperDE.bindTexture(DETextures.REACTOR_CORE);
            bindShader(te, x, y, z, partialTicks, tier);
            ccrs.startDrawing(GL11.GL_TRIANGLES, DefaultVertexFormats.POSITION_TEX);
            crystalHalf.render(ccrs, mat);
            ccrs.draw();
            releaseShader();
        }
        else if (!(DEShaders.useShaders() && DEConfig.useCrystalShaders)) {
            //Render overlay if shaders are not supported
            GlStateManager.enableBlend();
            mat.apply(new Rotation((ClientEventHandler.elapsedTicks + partialTicks) / 400F, 0, 1, 0)).apply(new Scale(1.001));
            ccrs.startDrawing(GL11.GL_TRIANGLES, DefaultVertexFormats.POSITION_TEX);
            crystalHalf.render(ccrs, mat);
            ccrs.draw();
        }
    }

    private static float[] r = {0.0F, 0.55F, 1.0F};
    private static float[] g = {0.35F, 0.3F, 0.572F};
    private static float[] b = {0.65F, 0.9F, 0.172F};

    public void bindShader(TileCrystalBase te, double x, double y, double z, float partialTicks, int tier) {
        BlockPos pos = te == null ? new BlockPos(0, 0, 0) : te.getPos();
        double mm = MathHelper.clip((((x * x) + (y * y) + (z * z) - 5) / 512), 0, 1);
        if (DEShaders.useShaders() && DEConfig.useCrystalShaders && mm < 1) {

            float xrot = (float) Math.atan2(x + 0.5, z + 0.5);
            float dist = (float) Utils.getDistanceAtoB(Vec3D.getCenter(pos).x, Vec3D.getCenter(pos).z, Minecraft.getInstance().player.posX, Minecraft.getInstance().player.posZ);
            float yrot = (float) net.minecraft.util.math.MathHelper.atan2(dist, y + 0.5);

            if (shaderProgram == null) {
                shaderProgram = new ShaderProgram();
                shaderProgram.attachShader(DEShaders.energyCrystal_V);
                shaderProgram.attachShader(DEShaders.energyCrystal_F);
            }

            shaderProgram.useShader(cache -> {
                cache.glUniform1F("time", (ClientEventHandler.elapsedTicks + partialTicks) / 50);
                cache.glUniform1F("mipmap", (float) mm);
                cache.glUniform1I("type", tier);
                cache.glUniform2F("angle", xrot / -3.125F, yrot / 3.125F);
            });
        }
        else {
            ResourceHelperDE.bindTexture(DETextures.ENERGY_CRYSTAL_NO_SHADER);
            GlStateManager.disableLighting();
            GlStateManager.color4f(r[tier], g[tier], b[tier], 0.5F);
        }
    }

    private void releaseShader() {
        if (DEShaders.useShaders() && DEConfig.useCrystalShaders && shaderProgram != null) {
            shaderProgram.releaseShader();
        }
    }
}
