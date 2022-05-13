package com.brandon3055.draconicevolution.client.render.effect;

import codechicken.lib.colour.Colour;
import codechicken.lib.colour.ColourARGB;
import codechicken.lib.math.MathHelper;
import codechicken.lib.render.CCRenderState;
import codechicken.lib.render.shader.ShaderObject;
import codechicken.lib.render.shader.ShaderProgram;
import codechicken.lib.render.shader.ShaderProgramBuilder;
import codechicken.lib.render.shader.UniformType;
import com.brandon3055.brandonscore.lib.Vec3D;
import com.brandon3055.brandonscore.utils.Utils;
import com.brandon3055.draconicevolution.DEConfig;
import com.brandon3055.draconicevolution.DraconicEvolution;
import com.brandon3055.draconicevolution.blocks.reactor.tileentity.TileReactorCore;
import com.brandon3055.draconicevolution.client.DETextures;
import com.brandon3055.draconicevolution.client.handler.ClientEventHandler;
import com.brandon3055.draconicevolution.utils.ResourceHelperDE;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.lwjgl.opengl.GL11;

/**
 * Created by brandon3055 on 12/02/2017.
 */
public class ReactorBeamFX extends Particle {

    protected int ticksTillDeath = 0;
    protected float fxState;
    private float powerState;
    private TileReactorCore tile;
    private boolean isInjectorEffect;
    private Direction facing;
    private float dist;
    private static Colour fieldBeamColour = new ColourARGB(0x00B0FF);
    private static Colour extractBeamColour = new ColourARGB(0xff6600);
    private static Colour energyBeamColour = new ColourARGB(0xff0000);
    private int boltSeed = -1;
    private int ttl = 10;

    public static final RenderType fallBackType = RenderType.create("fall_back_type", DefaultVertexFormat.POSITION_COLOR_TEX_LIGHTMAP, VertexFormat.Mode.QUADS, 256, RenderType.CompositeState.builder()
            .setTextureState(new RenderStateShard.TextureStateShard(new ResourceLocation(DraconicEvolution.MODID, "textures/particle/reactor_energy_beam.png"), false, false))
//            .setTexturingState(new RenderStateShard.TexturingStateShard("lighting", RenderSystem::disableLighting, SneakyUtils.none()))
            .createCompositeState(false)
    );

    public static ShaderProgram beamShaderI = ShaderProgramBuilder.builder()
            .addShader("frag", shader -> shader
                    .type(ShaderObject.StandardShaderType.FRAGMENT)
                    .source(new ResourceLocation(DraconicEvolution.MODID, "shaders/reactor_beam_i.frag"))
                    .uniform("time", UniformType.FLOAT)
                    .uniform("fade", UniformType.FLOAT)
                    .uniform("power", UniformType.FLOAT)
                    .uniform("startup", UniformType.FLOAT)
            )
            .build();

    public static ShaderProgram beamShaderO = ShaderProgramBuilder.builder()
            .addShader("frag", shader -> shader
                    .type(ShaderObject.StandardShaderType.FRAGMENT)
                    .source(new ResourceLocation(DraconicEvolution.MODID, "shaders/reactor_beam_o.frag"))
                    .uniform("time", UniformType.FLOAT)
                    .uniform("fade", UniformType.FLOAT)
                    .uniform("power", UniformType.FLOAT)
                    .uniform("startup", UniformType.FLOAT)
            )
            .build();

    public static ShaderProgram beamShaderE = ShaderProgramBuilder.builder()
            .addShader("frag", shader -> shader
                    .type(ShaderObject.StandardShaderType.FRAGMENT)
                    .source(new ResourceLocation(DraconicEvolution.MODID, "shaders/reactor_beam_e.frag"))
                    .uniform("time", UniformType.FLOAT)
                    .uniform("fade", UniformType.FLOAT)
                    .uniform("power", UniformType.FLOAT)
                    .uniform("startup", UniformType.FLOAT)
            )
            .build();

    public ReactorBeamFX(ClientLevel worldIn, Vec3D pos, Direction facing, TileReactorCore tile, boolean isInjectorEffect) {
        super(worldIn, pos.x, pos.y, pos.z);
        this.tile = tile;
        this.facing = facing;
        this.isInjectorEffect = isInjectorEffect;
        this.dist = (float) Utils.getDistanceAtoB(pos, Vec3D.getCenter(tile.getBlockPos()));
        this.random.setSeed(worldIn.random.nextLong());
        setBoundingBox(new AABB(x, y, z, tile.getBlockPos().getX(), tile.getBlockPos().getY(), tile.getBlockPos().getZ()));
    }

    public void updateFX(float fxState, float powerState) {
        this.fxState = fxState;
        if (powerState != this.powerState) {
            fieldBeamColour.r = (byte) (this.powerState * 0xFF);
            fieldBeamColour.g = (byte) ((1F - this.powerState) * 0xB0);
            fieldBeamColour.b = (byte) ((1F - this.powerState) * 0xFF);
        }
        this.powerState = powerState;
        ticksTillDeath = 4;
        if (ttl-- <= 0) {
            remove();
        }
    }

    @Override
    public void tick() {
        if (ticksTillDeath-- <= 0) {
            remove();
        }
        ttl = 10;
    }

    @Override
    public void render(VertexConsumer buffer, Camera renderInfo, float partialTicks) {
        if (tile.roller != null || !(buffer instanceof BufferBuilder)) {
            return;
        }
        CCRenderState ccrs = CCRenderState.instance();
        ccrs.reset();
        ccrs.bind(buffer, DefaultVertexFormat.POSITION_COLOR_TEX);
        ccrs.brightness = 240;

        Vec3 viewVec = renderInfo.getPosition();
        Vec3D pos1 = new Vec3D(x - viewVec.x, y - viewVec.y, z - viewVec.z).offset(facing, -0.35D);

        float texOffset = (ClientEventHandler.elapsedTicks + partialTicks) / -150F;
        float coreSize = (float) tile.getCoreDiameter() / 2.3F;

        if (!DEConfig.reactorShaders) {
            renderWithoutShaders(ccrs, buffer, pos1, coreSize, texOffset);
            return;
        }

        float animation = (ClientEventHandler.elapsedTicks + partialTicks) * 0.02F;

//        if (isInjectorEffect) {
//            UniformCache uniforms = beamShaderE.pushCache();
//            uniforms.glUniform1f("time", animation);
//            uniforms.glUniform1f("fade", 1F);
//            uniforms.glUniform1f("power", fxState);
//            uniforms.glUniform1f("startup", fxState);
//
//            beamShaderE.use();
//            beamShaderE.popCache(uniforms);
//            Vec3D pos2 = pos1.copy().offset(facing, 0.6D);
//            renderShaderBeam(ccrs, buffer, pos1, 0.1F, 0.1F, 0.6F, true, false);
//            ccrs.draw();
//            uniforms.glUniform1f("fade", 0F);
//            beamShaderE.popCache(uniforms);
//            renderShaderBeam(ccrs, buffer, pos2, 0.1F, coreSize / 1.5, dist - (coreSize * 1.3F), false, false);
//            ccrs.draw();
//            beamShaderE.release();
//        } else {
//            Vec3D pos2 = pos1.copy().offset(facing, 0.8D);
//
//            UniformCache uniforms = beamShaderO.pushCache();
//            uniforms.glUniform1f("time", animation);
//            uniforms.glUniform1f("fade", 1F);
//            uniforms.glUniform1f("power", (float) tile.animExtractState.get());
//            uniforms.glUniform1f("startup", (float) tile.animExtractState.get());
//
//            beamShaderO.use();
//            beamShaderO.popCache(uniforms);
//            renderShaderBeam(ccrs, buffer, pos1, 0.263F, 0.263F, 0.8F, true, false);
//            ccrs.draw();
//            uniforms.glUniform1f("fade", 0F);
//            beamShaderO.popCache(uniforms);
//            renderShaderBeam(ccrs, buffer, pos2, 0.263F, coreSize / 2, dist - (coreSize * 1.3F), false, false);
//            ccrs.draw();
//            beamShaderO.release();
//
//
//            //Draw Outer
//            uniforms = beamShaderI.pushCache();
//            uniforms.glUniform1f("time", animation);
//            uniforms.glUniform1f("fade", 1F);
//            uniforms.glUniform1f("power", fxState);
//            uniforms.glUniform1f("startup", fxState);
//
//            beamShaderI.use();
//            beamShaderI.popCache(uniforms);
//            renderShaderBeam(ccrs, buffer, pos1, 0.355F, 0.355F, 0.8F, true, false);
//            ccrs.draw();
//            uniforms.glUniform1f("fade", 0F);
//            beamShaderI.popCache(uniforms);
//            renderShaderBeam(ccrs, buffer, pos2, 0.355F, coreSize, dist - coreSize, false, true);
//            ccrs.draw();
//            beamShaderI.release();
//
//        }

    }

    public void renderBeam(CCRenderState ccrs, VertexConsumer buffer, Vec3D pos, float widthStart, float widthEnd, float length, float beamAnimation, float beamRotation, boolean fadeReverse, Colour colour) {
        ccrs.startDrawing(VertexFormat.Mode.TRIANGLE_STRIP, DefaultVertexFormat.POSITION_COLOR_TEX);
        float sides = 99;
        float r = (colour.r & 0xFF) / 255F, g = (colour.g & 0xFF) / 255F, b = (colour.b & 0xFF) / 255F;
        for (int i = 0; i < sides + 1; i++) {
            double angle = (i / sides) * (Math.PI * 2);// + beamRotation;
            float sin = (float) MathHelper.sin(angle);
            float cos = (float) MathHelper.cos(angle);
            float texX = i / sides;
            Vec3D point = pos.copy().radialOffset(facing.getAxis(), sin, cos, widthStart);
            buffer.vertex(point.x, point.y, point.z).color(r, g, b, fadeReverse ? 0F : fxState).uv(texX, 0 + beamAnimation).endVertex();
            point.offset(facing, length);
            point.radialOffset(facing.getAxis(), sin, cos, widthEnd - widthStart);
            buffer.vertex(point.x, point.y, point.z).color(r, g, b, fadeReverse ? fxState : 0F).uv(texX, 0.1F + beamAnimation).endVertex();
        }
        ccrs.draw();
    }

    public void renderWithoutShaders(CCRenderState ccrs, VertexConsumer buffer, Vec3D pos1, float coreSize, float texOffset) {
        Vec3D pos2;
        if (isInjectorEffect) {
            pos2 = pos1.copy().offset(facing, 0.6D);
            renderBeam(ccrs, buffer, pos1, 0.1F, 0.1F, 0.6F, texOffset, texOffset * 15, true, energyBeamColour);
            renderBeam(ccrs, buffer, pos2, 0.1F, coreSize / 1.5F, dist - (coreSize * 1.3F), texOffset + 0.1F, texOffset * 15F, false, energyBeamColour);
        } else {
            pos2 = pos1.copy().offset(facing, 0.8D);
            //Draw Outer
            renderBeam(ccrs, buffer, pos1, 0.355F, 0.355F, 0.8F, texOffset, 0, true, fieldBeamColour);
            renderBeam(ccrs, buffer, pos2, 0.355F, coreSize, dist - coreSize, texOffset + 0.1F, 0, false, fieldBeamColour);

            //Draw Inner
            renderBeam(ccrs, buffer, pos1, 0.263F, 0.263F, 0.8F, -texOffset, 0, true, extractBeamColour);
            renderBeam(ccrs, buffer, pos2, 0.263F, coreSize / 2, dist - (coreSize * 1.3F), -texOffset + 0.1F, 0, false, extractBeamColour);
        }
    }

    public void renderShaderBeam(CCRenderState ccrs, VertexConsumer buffer, Vec3D pos, double widthStart, double widthEnd, float length, boolean fadeReverse, boolean highRes) {
        ccrs.startDrawing(VertexFormat.Mode.TRIANGLE_STRIP, DefaultVertexFormat.POSITION_COLOR_TEX);
        float sides = highRes ? 599 : 99;
        for (int i = 0; i < sides + 1; i++) {
            double angle = (i / sides) * (Math.PI * 2);// + beamRotation;
            float sin = (float) MathHelper.sin(angle);
            float cos = (float) MathHelper.cos(angle);
            float texX = i / sides;
            Vec3D point = pos.copy().radialOffset(facing.getAxis(), sin, cos, widthStart);
            buffer.vertex(point.x, point.y, point.z).color(1F, 1F, 1F, fadeReverse ? 0F : fxState).uv(texX, (fadeReverse ? 0.1F : 1F)).endVertex();
            point.offset(facing, length);
            point.radialOffset(facing.getAxis(), sin, cos, widthEnd - widthStart);
            buffer.vertex(point.x, point.y, point.z).color(1F, 1F, 1F, fadeReverse ? fxState : 0F).uv(texX, 0).endVertex();
        }
//        ccrs.draw();
    }

    @Override
    public ParticleRenderType getRenderType() {
        return FX_HANDLER;
    }

    private static final ParticleRenderType FX_HANDLER = new FXHandler(DETextures.REACTOR_ENERGY_BEAM);

    public static class FXHandler implements ParticleRenderType {
        private ResourceLocation texture;

        public FXHandler(String texture) {
            this.texture = new ResourceLocation(DraconicEvolution.MODID, texture);
        }

        @Override
        public void begin(BufferBuilder builder, TextureManager p_217600_2_) {
            p_217600_2_.bindForSetup(texture);
            RenderSystem.disableCull();
            RenderSystem.depthMask(false);
//            RenderSystem.alphaFunc(GL11.GL_GREATER, 0F);
            RenderSystem.enableBlend();

            if (!DEConfig.reactorShaders) {
                RenderSystem.texParameter(3553, 10242, 10497);
                RenderSystem.texParameter(3553, 10243, 10497);
//                RenderSystem.shadeModel(GL11.GL_SMOOTH);
                RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE);
            } else {
                RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
            }
        }

        @Override
        public void end(Tesselator tessellator) {
//            RenderSystem.alphaFunc(GL11.GL_GREATER, 0.1F);
            RenderSystem.enableCull();
            if (!DEConfig.reactorShaders) {
//                RenderSystem.shadeModel(GL11.GL_FLAT);
                RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
            }
        }
    }
}
