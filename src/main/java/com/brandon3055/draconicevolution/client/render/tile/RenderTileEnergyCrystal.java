package com.brandon3055.draconicevolution.client.render.tile;

import codechicken.lib.colour.Colour;
import codechicken.lib.math.MathHelper;
import codechicken.lib.render.CCModel;
import codechicken.lib.render.CCRenderState;
import codechicken.lib.render.OBJParser;
import codechicken.lib.render.shader.ShaderProgram;
import codechicken.lib.util.SneakyUtils;
import codechicken.lib.vec.Matrix4;
import codechicken.lib.vec.Rotation;
import codechicken.lib.vec.Vector3;
import com.brandon3055.brandonscore.lib.Vec3D;
import com.brandon3055.brandonscore.utils.Utils;
import com.brandon3055.draconicevolution.DEOldConfig;
import com.brandon3055.draconicevolution.DraconicEvolution;
import com.brandon3055.draconicevolution.blocks.energynet.tileentity.TileCrystalBase;
import com.brandon3055.draconicevolution.blocks.energynet.tileentity.TileCrystalDirectIO;
import com.brandon3055.draconicevolution.client.handler.ClientEventHandler;
import com.brandon3055.draconicevolution.client.render.shaders.DEShaders;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderState;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import org.lwjgl.opengl.GL11;

import java.util.Map;

/**
 * Created by brandon3055 on 6/11/2016.
 */
public class RenderTileEnergyCrystal extends TileEntityRenderer<TileCrystalBase> {

    private static final RenderType fallBackType = RenderType.makeType("fall_back_type", DefaultVertexFormats.POSITION_COLOR_TEX_LIGHTMAP, GL11.GL_QUADS, 256, RenderType.State.getBuilder()
            .texture(new RenderState.TextureState(new ResourceLocation(DraconicEvolution.MODID, "textures/models/crystal_no_shader.png"), false, false))
            .texturing(new RenderState.TexturingState("lighting", RenderSystem::disableLighting, SneakyUtils.none()))
            .build(false)
    );
    private static final RenderType fallBackOverlayType = RenderType.getEntityTranslucent(new ResourceLocation(DraconicEvolution.MODID, "textures/models/crystal_base.png"));
    private static final RenderType crystalBaseType = RenderType.getEntitySolid(new ResourceLocation(DraconicEvolution.MODID, "textures/models/crystal_base.png"));

    private final CCModel crystalFull;
    private final CCModel crystalHalf;
    private final CCModel crystalBase;

    private static ShaderProgram shaderProgram;


    public RenderTileEnergyCrystal(TileEntityRendererDispatcher rendererDispatcherIn) {
        super(rendererDispatcherIn);
        Map<String, CCModel> map = OBJParser.parseModels(new ResourceLocation(DraconicEvolution.MODID, "models/block/crystal.obj"), GL11.GL_QUADS, null);
        crystalFull = CCModel.combine(map.values());
        map = OBJParser.parseModels(new ResourceLocation(DraconicEvolution.MODID, "models/block/crystal_half.obj"), GL11.GL_QUADS, null);
        crystalHalf = map.get("Crystal");
        crystalBase = map.get("Base");
    }

    @Override
    public void render(TileCrystalBase te, float partialTicks, MatrixStack mStack, IRenderTypeBuffer getter, int packedLight, int packedOverlay) {
        te.getFxHandler().renderCooldown = 5;
        int tier = te.getTier();

        Matrix4 mat = new Matrix4(mStack);
        CCRenderState ccrs = CCRenderState.instance();
        ccrs.reset();
        ccrs.brightness = packedLight;
        ccrs.overlay = packedOverlay;

        if (te instanceof TileCrystalDirectIO) {
            ccrs.bind(crystalBaseType, getter);
            mat.translate(0.5, 1, 0.5);
            mat.scale(-0.5);
            mat.apply(Rotation.sideOrientation(((TileCrystalDirectIO) te).facing.get().getOpposite().getIndex(), 0).at(new Vector3(0, 1, 0)));
            crystalBase.render(ccrs, mat);

            mat.rotate((ClientEventHandler.elapsedTicks + partialTicks) / 400F, new Vector3(0, 1, 0));
            ccrs.baseColour = Colour.packRGBA(r[tier], g[tier], b[tier], 1F);
            ccrs.bind(fallBackType, getter);
            crystalHalf.render(ccrs, mat);

            ccrs.baseColour = -1;
            ccrs.bind(fallBackOverlayType, getter);
            crystalHalf.render(ccrs, mat);
        } else {
            ccrs.baseColour = Colour.packRGBA(r[tier], g[tier], b[tier], 1F);
            mat.translate(Vector3.CENTER);
            mat.rotate(180 * MathHelper.torad, new Vector3(1, 0, 0));
            mat.scale(-0.5);
            mat.rotate((ClientEventHandler.elapsedTicks + partialTicks) / 400F, new Vector3(0, 1, 0));
            ccrs.bind(fallBackType, getter);
            crystalFull.render(ccrs, mat);

            ccrs.baseColour = -1;
            ccrs.bind(fallBackOverlayType, getter);
            crystalFull.render(ccrs, mat);
        }
    }


    public void renderCrystal(TileCrystalBase te, double x, double y, double z, float partialTicks, int destroyStage, int tier) {
//        boolean trans = false;//MinecraftForgeClient.getRenderPass() == 1;
//        CCRenderState ccrs = CCRenderState.instance();
//        Matrix4 mat = RenderUtils.getMatrix(new Vector3(x + 0.5, y + 0.5, z + 0.5), new Rotation(180 * MathHelper.torad, 1, 0, 0), -0.5);
//        mat.apply(new Rotation((ClientEventHandler.elapsedTicks + partialTicks) / 400F, 0, 1, 0));
//
//        if (destroyStage >= 0) {
//            bindTexture(DESTROY_STAGES[destroyStage]);
//            ccrs.startDrawing(GL11.GL_TRIANGLES, DefaultVertexFormats.POSITION_TEX);
//            crystalFull.render(ccrs, mat);
//            ccrs.draw();
//            return;
//        }
//
//        if (!trans) {
//            //Render Crystal
//            bindShader(te, x, y, z, partialTicks, tier);
//            ccrs.startDrawing(GL11.GL_TRIANGLES, DefaultVertexFormats.POSITION_TEX);
//            crystalFull.render(ccrs, mat);
//            ccrs.draw();
//            releaseShader();
//        }
//        else if (!(DEShaders.useShaders() && DEConfig.useCrystalShaders)) {
//            //Render overlay if shaders are not supported
//            ResourceHelperDE.bindTexture(DETextures.ENERGY_CRYSTAL_BASE);
//            RenderSystem.enableBlend();
//            ccrs.startDrawing(GL11.GL_TRIANGLES, DefaultVertexFormats.POSITION_TEX);
//            crystalFull.render(ccrs, mat);
//            ccrs.draw();
//        }
    }

    public void renderHalfCrystal(TileCrystalDirectIO te, double x, double y, double z, float partialTicks, int destroyStage, int tier) {

//        if (!trans) {
//            //Render Base
//            ccrs.startDrawing(GL11.GL_TRIANGLES, DefaultVertexFormats.POSITION_TEX);
//            crystalBase.render(ccrs, mat);
//            ccrs.draw();
//
//            //Apply Crystal Rotation
//            mat.apply(new Rotation((ClientEventHandler.elapsedTicks + partialTicks) / 400F, 0, 1, 0));
//
//            //Render Crystal
//            ResourceHelperDE.bindTexture(DETextures.REACTOR_CORE);
//            bindShader(te, x, y, z, partialTicks, tier);
//            ccrs.startDrawing(GL11.GL_TRIANGLES, DefaultVertexFormats.POSITION_TEX);
//            crystalHalf.render(ccrs, mat);
//            ccrs.draw();
//            releaseShader();
//        }
//        else if (!(DEShaders.useShaders() && DEConfig.useCrystalShaders)) {
//            //Render overlay if shaders are not supported
//            RenderSystem.enableBlend();
//            mat.apply(new Rotation((ClientEventHandler.elapsedTicks + partialTicks) / 400F, 0, 1, 0)).apply(new Scale(1.001));
//            ccrs.startDrawing(GL11.GL_TRIANGLES, DefaultVertexFormats.POSITION_TEX);
//            crystalHalf.render(ccrs, mat);
//            ccrs.draw();
//        }
    }

    private static float[] r = {0.0F, 0.55F, 1.0F};
    private static float[] g = {0.35F, 0.3F, 0.572F};
    private static float[] b = {0.65F, 0.9F, 0.172F};

    public void bindShader(TileCrystalBase te, double x, double y, double z, float partialTicks, int tier) {
        BlockPos pos = te == null ? new BlockPos(0, 0, 0) : te.getPos();
        double mm = MathHelper.clip((((x * x) + (y * y) + (z * z) - 5) / 512), 0, 1);
        if (DEShaders.useShaders() && DEOldConfig.useCrystalShaders && mm < 1) {

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
        } else {
//            ResourceHelperDE.bindTexture(DETextures.ENERGY_CRYSTAL_NO_SHADER);
//            RenderSystem.disableLighting();
//            RenderSystem.color4f(r[tier], g[tier], b[tier], 0.5F);
        }
    }

    private void releaseShader() {
        if (DEShaders.useShaders() && DEOldConfig.useCrystalShaders && shaderProgram != null) {
            shaderProgram.releaseShader();
        }
    }
}
