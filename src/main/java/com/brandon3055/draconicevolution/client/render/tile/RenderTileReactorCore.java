package com.brandon3055.draconicevolution.client.render.tile;

import codechicken.lib.render.CCModel;
import codechicken.lib.render.CCRenderState;
import codechicken.lib.render.shader.ShaderObject;
import codechicken.lib.render.shader.ShaderProgram;
import codechicken.lib.render.shader.ShaderProgramBuilder;
import codechicken.lib.render.shader.UniformType;
import codechicken.lib.vec.Matrix4;
import codechicken.lib.vec.Vector3;
import com.brandon3055.brandonscore.utils.MathUtils;
import com.brandon3055.draconicevolution.DraconicEvolution;
import com.brandon3055.draconicevolution.blocks.reactor.tileentity.TileReactorCore;
import com.brandon3055.draconicevolution.client.handler.ClientEventHandler;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

/**
 * Created by brandon3055 on 6/11/2016.
 */
public class RenderTileReactorCore implements BlockEntityRenderer<TileReactorCore> {

    private static CCModel model = null;
    private static CCModel model_no_shade;

    public static ShaderProgram coreShader = ShaderProgramBuilder.builder()
            .addShader("frag", shader -> shader
                    .type(ShaderObject.StandardShaderType.FRAGMENT)
                    .source(new ResourceLocation(DraconicEvolution.MODID, "shaders/reactor.frag"))
                    .uniform("time", UniformType.FLOAT)
                    .uniform("intensity", UniformType.FLOAT)
            )
            .build();

    public static ShaderProgram shieldShader = ShaderProgramBuilder.builder()
            .addShader("frag", shader -> shader
                    .type(ShaderObject.StandardShaderType.FRAGMENT)
                    .source(new ResourceLocation(DraconicEvolution.MODID, "shaders/reactor_shield.frag"))
                    .uniform("time", UniformType.FLOAT)
                    .uniform("intensity", UniformType.FLOAT)
            )
            .build();


    public static RenderType fallBackType = RenderType.create("fall_back_type", DefaultVertexFormat.POSITION_COLOR_TEX_LIGHTMAP, VertexFormat.Mode.QUADS, 256, RenderType.CompositeState.builder()
                    .setTextureState(new RenderStateShard.TextureStateShard(new ResourceLocation(DraconicEvolution.MODID, "textures/block/reactor/reactor_core.png"), false, false))
//            .setTexturingState(new RenderStateShard.TexturingStateShard("lighting", RenderSystem::disableLighting, SneakyUtils.none()))
                    .createCompositeState(false)
    );

    public static RenderType fallBackShieldType = RenderType.create("fall_back_shield_type", DefaultVertexFormat.POSITION_COLOR_TEX_LIGHTMAP, VertexFormat.Mode.QUADS, 256, RenderType.CompositeState.builder()
                    .setTransparencyState(RenderStateShard.TRANSLUCENT_TRANSPARENCY)
                    .setTextureState(new RenderStateShard.TextureStateShard(new ResourceLocation(DraconicEvolution.MODID, "textures/block/reactor/reactor_shield.png"), false, false))
//            .setTexturingState(new RenderStateShard.TexturingStateShard("lighting", RenderSystem::disableLighting, SneakyUtils.none()))
                    .createCompositeState(false)
    );

    public RenderTileReactorCore(BlockEntityRendererProvider.Context context) {
        if (model == null) {
//            Map<String, CCModel> map = OBJParser.parseModels(new ResourceLocation(DraconicEvolution.MODID, "models/block/reactor/reactor_core.obj"), GL11.GL_QUADS, null);
//            model = CCModel.combine(map.values());
//            map = OBJParser.parseModels(new ResourceLocation(DraconicEvolution.MODID, "models/block/reactor/reactor_core_model.obj"), GL11.GL_QUADS, null);
//            model_no_shade = CCModel.combine(map.values()).apply(new Scale(-0.5));
        }
    }


    @Override
    public void render(TileReactorCore te, float partialTicks, PoseStack mStack, MultiBufferSource getter, int packedLight, int packedOverlay) {
//        if (true) return;
        Matrix4 mat = new Matrix4(mStack);
        CCRenderState ccrs = CCRenderState.instance();
        ccrs.reset();
        ccrs.brightness = packedLight;
        ccrs.overlay = packedOverlay;

//        if (HolidayHelper.isAprilFools()) {
//            Minecraft mc = Minecraft.getInstance();
//            Entity entity = mc.getRenderViewEntity();
//            double d0 = entity.lastTickPosX + (entity.getPosX() - entity.lastTickPosX) * (double) mc.getRenderPartialTicks();
//            double d1 = entity.lastTickPosY + (entity.getPosY() - entity.lastTickPosY) * (double) mc.getRenderPartialTicks();
//            double d2 = entity.lastTickPosZ + (entity.getPosZ() - entity.lastTickPosZ) * (double) mc.getRenderPartialTicks();
//            frustum.setPosition(d0, d1, d2);
//            te.inView = frustum.isBoundingBoxInFrustum(new AxisAlignedBB(te.roller == null ? te.getPos() : te.roller.pos.getPos()).grow(3, 3, 3));
//
//            if (te.roller != null) {
//                Vec3D pos = te.roller.pos;
//                Vec3D lastPos = te.roller.lastPos;
//                Vec3D tePos = Vec3D.getCenter(te);
//                double xOffset = (pos.x - tePos.x) + ((pos.x - lastPos.x) * partialTicks);
//                double yOffset = (pos.y - tePos.y) + ((pos.y - lastPos.y) * partialTicks);
//                double zOffset = (pos.z - tePos.z) + ((pos.z - lastPos.z) * partialTicks);
//
//                x += xOffset;
//                y += yOffset;
//                z += zOffset;
//
//                float travel = (float) Utils.getDistanceAtoB(te.getPos().getX(), te.getPos().getZ(), pos.x, pos.z);
//                RenderSystem.translated(x + 0.5, y + 0.5, z + 0.5);
//                RenderSystem.rotatef(travel * (360 / (float) (te.getCoreDiameter() * Math.PI)), (float) Math.sin(te.roller.direction), 0, (float) Math.cos(te.roller.direction) * -1);
//                RenderSystem.translated(-(x + 0.5), -(y + 0.5), -(z + 0.5));
//            }
//        }

        double diameter = te.getCoreDiameter();
        float t = (float) (te.temperature.get() / TileReactorCore.MAX_TEMPERATURE);
        float intensity = t <= 0.2 ? (float) MathUtils.map(t, 0, 0.2, 0, 0.3) : t <= 0.8 ? (float) MathUtils.map(t, 0.2, 0.8, 0.3, 1) : (float) MathUtils.map(t, 0.8, 1, 1, 1.3);
        float shieldPower = (float) (te.maxShieldCharge.get() > 0 ? te.shieldCharge.get() / te.maxShieldCharge.get() : 0);
        float animation = (te.coreAnimation + (partialTicks * (float) te.shaderAnimationState.get())) / 20F;

        mat.translate(0.5, 0.5, 0.5);
        mat.scale(diameter);
        mat.rotate((ClientEventHandler.elapsedTicks + partialTicks) / 400F, Vector3.Y_POS);

        renderCore(mat, ccrs, animation, te.shaderAnimationState.get(), intensity, shieldPower, partialTicks, getter);
    }

    public static void renderCore(Matrix4 mat, CCRenderState ccrs, float animation, double animState, float intensity, float shieldPower, float partialTicks, MultiBufferSource getter) {
//        DEConfig.reactorShaders = true;//System.currentTimeMillis() % 6000 > 3000;
//        if (DEConfig.reactorShaders) {
//            UniformCache uniforms = coreShader.pushCache();
//            uniforms.glUniform1f("time", animation);
//            uniforms.glUniform1f("intensity", intensity);
//            ccrs.bind(new ShaderRenderType(fallBackType, coreShader, uniforms), getter);
//            model.render(ccrs, mat);
//
//            mat.scale(1.05);
//            uniforms = shieldShader.pushCache();
//            uniforms.glUniform1f("time", animation);
//            uniforms.glUniform1f("intensity", (0.7F * shieldPower) - (float) (1 - animState));
//            ccrs.bind(new ShaderRenderType(fallBackShieldType, shieldShader, uniforms), getter);
//            model.render(ccrs, mat);
//        } else {
//            ccrs.bind(fallBackType, getter);
//            model_no_shade.render(ccrs, mat);
//            ToolRenderBase.endBatch(getter);
//
//            mat.scale(1.05);
//            mat.rotate((ClientEventHandler.elapsedTicks + partialTicks) / 400F, Vector3.X_NEG);
//            float r = shieldPower < 0.5F ? 1 - (shieldPower * 2) : 0;
//            float g = shieldPower > 0.5F ? (shieldPower - 0.5F) * 2 : 0;
//            float b = shieldPower * 2;
//            float a = shieldPower < 0.1F ? (shieldPower * 10) : 1;
//            ccrs.baseColour = ColourRGBA.packRGBA(r, g, b, a);
//            ccrs.bind(fallBackShieldType, getter);
//            model_no_shade.render(ccrs, mat);
//        }
    }

    public static void renderGUI(TileReactorCore te, int x, int y) {
//        RenderSystem.pushMatrix();
//        double diameter = 100;
//        float t = (float) (te.temperature.get() / TileReactorCore.MAX_TEMPERATURE);
//        float intensity = t <= 0.2 ? (float) MathUtils.map(t, 0, 0.2, 0, 0.3) : t <= 0.8 ? (float) MathUtils.map(t, 0.2, 0.8, 0.3, 1) : (float) MathUtils.map(t, 0.8, 1, 1, 1.3);
//        float animation = (te.coreAnimation + (0 * (float) te.shaderAnimationState.get())) / 20F;
//        float shieldPower = (float) (te.maxShieldCharge.get() > 0 ? te.shieldCharge.get() / te.maxShieldCharge.get() : 0);
//        PoseStack stack = new PoseStack();
//        Minecraft mc = Minecraft.getInstance();
//        MultiBufferSource.BufferSource getter = MultiBufferSource.immediate(Tesselator.getInstance().getBuilder());
//        Matrix4 mat = new Matrix4(stack);
//        mat.scale(diameter);
//        mat.rotate((ClientEventHandler.elapsedTicks + mc.getFrameTime()) / 400F, Vector3.Y_POS);
//        CCRenderState ccrs = CCRenderState.instance();
//        ccrs.reset();
//        RenderSystem.translated(x, y, 100);
//        RenderSystem.depthMask(false);
//        renderCore(mat, ccrs, animation, te.shaderAnimationState.get(), intensity, shieldPower, mc.getFrameTime(), getter);
//        getter.endBatch();
//        RenderSystem.depthMask(true);
//        RenderSystem.popMatrix();
    }

    @Override
    public boolean shouldRenderOffScreen(TileReactorCore te) {
        return true;
    }

    @Override
    public int getViewDistance() {
        return 256;
    }
}
