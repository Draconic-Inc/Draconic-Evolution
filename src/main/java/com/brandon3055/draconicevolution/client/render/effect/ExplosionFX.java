package com.brandon3055.draconicevolution.client.render.effect;

import codechicken.lib.math.MathHelper;
import codechicken.lib.render.CCModel;
import codechicken.lib.render.CCRenderState;
import codechicken.lib.render.RenderUtils;
import codechicken.lib.render.model.OBJParser;
import codechicken.lib.render.shader.ShaderObject;
import codechicken.lib.render.shader.ShaderProgram;
import codechicken.lib.render.shader.ShaderProgramBuilder;
import codechicken.lib.render.shader.UniformType;
import codechicken.lib.vec.Matrix4;
import codechicken.lib.vec.Rotation;
import codechicken.lib.vec.Scale;
import codechicken.lib.vec.Vector3;
import com.brandon3055.draconicevolution.DraconicEvolution;
import com.brandon3055.draconicevolution.client.DEShaders;
import com.brandon3055.draconicevolution.client.handler.ClientEventHandler;
import com.brandon3055.draconicevolution.handlers.DESounds;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource.BufferSource;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.phys.Vec3;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Random;

import static com.brandon3055.draconicevolution.DraconicEvolution.MODID;
import static net.minecraft.client.renderer.RenderStateShard.COLOR_WRITE;

/**
 * Created by brandon3055 on 12/02/2017.
 */
public class ExplosionFX extends Particle {
    public static CCModel model;
    public static CCModel model_inv;
    public CoreEffect coreEffect;
    private LinkedList<EffectPart> effectParts = new LinkedList<>();
    private static final Random rand = new Random();

    public static ShaderProgram leadingWaveProgram = ShaderProgramBuilder.builder()
            .addShader("frag", shader -> shader
                    .type(ShaderObject.StandardShaderType.FRAGMENT)
                    .source(new ResourceLocation(DraconicEvolution.MODID, "shaders/explosion_leading_wave.frag"))
                    .uniform("time", UniformType.FLOAT)
                    .uniform("scale", UniformType.FLOAT)
                    .uniform("alpha", UniformType.FLOAT)
            )
            .build();

    public static ShaderProgram blastWaveProgram = ShaderProgramBuilder.builder()
            .addShader("frag", shader -> shader
                    .type(ShaderObject.StandardShaderType.FRAGMENT)
                    .source(new ResourceLocation(DraconicEvolution.MODID, "shaders/explosion_blast_wave.frag"))
                    .uniform("time", UniformType.FLOAT)
                    .uniform("scale", UniformType.FLOAT)
                    .uniform("alpha", UniformType.FLOAT)
            )
            .build();

    public static ShaderProgram coreEffectProgram = ShaderProgramBuilder.builder()
            .addShader("frag", shader -> shader
                    .type(ShaderObject.StandardShaderType.FRAGMENT)
                    .source(new ResourceLocation(DraconicEvolution.MODID, "shaders/explosion_core_effect.frag"))
                    .uniform("time", UniformType.FLOAT)
                    .uniform("scale", UniformType.FLOAT)
                    .uniform("alpha", UniformType.FLOAT)
            )
            .build();

    private static final RenderStateShard.DepthTestStateShard DISABLE_DEPTH = new RenderStateShard.DepthTestStateShard("none", 519) {
        @Override
        public void setupRenderState() {
            RenderSystem.disableDepthTest();
        }
    };

    public static RenderType EXPLOSION_TYPE = RenderType.create(MODID + ":explosion_shader", DefaultVertexFormat.POSITION_TEX, VertexFormat.Mode.QUADS, 256, false, true, RenderType.CompositeState.builder()
            .setTransparencyState(RenderStateShard.TRANSLUCENT_TRANSPARENCY)
            .setShaderState(new RenderStateShard.ShaderStateShard(() -> DEShaders.explosionShader))
            .setCullState(RenderStateShard.NO_CULL)
            .setWriteMaskState(COLOR_WRITE)
            .setDepthTestState(DISABLE_DEPTH)
            .createCompositeState(false)
    );


    public static RenderType FOG_TYPE = RenderType.create(MODID + ":explosion_fog", DefaultVertexFormat.POSITION_COLOR, VertexFormat.Mode.QUADS, 256, false, true, RenderType.CompositeState.builder()
                    .setTransparencyState(RenderStateShard.TRANSLUCENT_TRANSPARENCY)
                    .setShaderState(new RenderStateShard.ShaderStateShard(GameRenderer::getPositionColorShader))
                    .setCullState(RenderStateShard.NO_CULL)
                    .setWriteMaskState(COLOR_WRITE)
//          .setDepthTestState(DISABLE_DEPTH)
                    .createCompositeState(false)
    );

    static {
        Map<String, CCModel> map = new OBJParser(new ResourceLocation(DraconicEvolution.MODID, "models/block/reactor/reactor_core.obj")).quads().ignoreMtl().parse();
        model = CCModel.combine(map.values());
        model_inv = model.backfacedCopy();
    }

    private final Vector3 pos;
    public final int radius;

    public ExplosionFX(ClientLevel worldIn, Vector3 pos, int radius) {
        super(worldIn, pos.x, pos.y, pos.z);
        this.pos = pos;
        this.radius = radius;
        lifetime = 20 * 12;
        coreEffect = new CoreEffect(0);
    }

    @Override
    public boolean shouldCull() {
        return false;
    }

    @Override
    public void tick() {
        if (effectParts.size() > 0) {
            Iterator<EffectPart> i = effectParts.iterator();
            while (i.hasNext()) {
                EffectPart part = i.next();
                if (part.isDead()) {
                    i.remove();
                } else {
                    part.update();
                }
            }
        }

        coreEffect.update();

        if (age > lifetime) {
            remove();
        }

        if (age == 0) {
            coreEffect = new CoreEffect(0);
            ClientEventHandler.triggerExplosionEffect(pos.pos(), true);
        } else if (age == 3 || age == 8 || age == 13) {
            effectParts.addFirst(new LeadingWave(age));
        } else if (age > 30 && age <= 35) {
            effectParts.addFirst(new BlastWave(age - 30));
        }

        if (age == 10) {
            level.playLocalSound(x, y, z, DESounds.fusionExplosion, SoundSource.PLAYERS, 100, 0.9F, false);
        }

        this.age++;
    }

    @Override
    public void render(VertexConsumer b, Camera renderInfo, float partialTicks) {
        Vec3 viewVec = renderInfo.getPosition();
        Vector3 pos = new Vector3(x - viewVec.x, y - viewVec.y, z - viewVec.z);

        CCRenderState ccrs = CCRenderState.instance();
        ccrs.reset();
        ccrs.brightness = 240;

        BufferSource buffers = Minecraft.getInstance().renderBuffers().bufferSource();
        VertexConsumer buffer = buffers.getBuffer(FOG_TYPE);
        ccrs.bind(buffer, DefaultVertexFormat.POSITION_COLOR);

        float ttl = 1F - (((float) age + partialTicks) / (float) lifetime);
        ttl = Math.min(1, ttl * 5);
        double od = 1200;
        double id = radius / 100D;
        ccrs.baseColour = 0xFFFFFF00 | MathHelper.clip((int) (0xFF * (0.15F * ttl * Math.min(1, lifetime / 25F))), 0, 0xFF);

        for (double i = 0; i < 16; i += 1) {
            Matrix4 mat = RenderUtils.getMatrix(pos, new Rotation(0, 0, 1, 0), 1).apply(new Scale(od, id * i, od));
            model.render(ccrs, mat);
            od += id * i;
        }

        buffers.endBatch();

        ccrs.baseColour = 0xFFFFFFFF;

        if (!coreEffect.isDead()) {
            coreEffect.render(pos, ccrs, buffers, partialTicks);
        }

        for (EffectPart part : effectParts) {
            part.render(pos, ccrs, buffers, partialTicks);
        }

        ccrs.reset();
    }

    @Override
    public ParticleRenderType getRenderType() {
        return FX_HANDLER;
    }

    private static final ParticleRenderType FX_HANDLER = new FXHandler();

    public static class FXHandler implements ParticleRenderType {
        @Override
        public void begin(BufferBuilder builder, TextureManager p_217600_2_) {}

        @Override
        public void end(Tesselator tessellator) {}
    }

    private static abstract class EffectPart {
        private boolean isDead = false;
        protected int age = 0;
        protected int maxAge = 0;
        public double scale;
        public int randOffset;

        public EffectPart(double scale) {
            this.scale = scale;
            randOffset = rand.nextInt(3265324);
        }

        public abstract void update();

        public abstract void render(Vector3 pos, CCRenderState ccrs, BufferSource buffers, float partialTicks);

        public void setDead() {
            isDead = true;
        }

        public boolean isDead() {
            return isDead;
        }
    }

    private class LeadingWave extends EffectPart {

        public LeadingWave(double scale) {
            super(scale);
            maxAge = 40 + (int) scale;
        }

        @Override
        public void update() {
            if (age > maxAge) {
                setDead();
            }
            age++;
        }

        @Override
        public void render(Vector3 pos, CCRenderState ccrs, BufferSource buffers, float partialTicks) {
            float ttl = (((float) age + partialTicks) / (float) maxAge);
            double scale = radius * ttl * 2;

            VertexConsumer buffer = buffers.getBuffer(EXPLOSION_TYPE);
            ccrs.bind(buffer, DefaultVertexFormat.POSITION_TEX);

            DEShaders.explosionTime.glUniform1f((ClientEventHandler.elapsedTicks + partialTicks + randOffset) / 10F);
            DEShaders.explosionScale.glUniform1f(ttl * 3);
            DEShaders.explosionAlpha.glUniform1f(1 - ttl);
            DEShaders.explosionType.glUniform1i(2);

//            UniformCache uniforms = leadingWaveProgram.pushCache();
//            uniforms.glUniform1f("time", (ClientEventHandler.elapsedTicks + partialTicks + randOffset) / 10F);
//            uniforms.glUniform1f("scale", ttl * 3);
//            uniforms.glUniform1f("alpha", 1 - ttl);
//            leadingWaveProgram.use();
//            leadingWaveProgram.popCache(uniforms);
//
//            ccrs.startDrawing(GL11.GL_TRIANGLES, DefaultVertexFormat.POSITION_TEX);
            Matrix4 mat = RenderUtils.getMatrix(pos, new Rotation(0, 0, 1, 0), 1).apply(new Scale(scale, 1, scale));
            model_inv.render(ccrs, mat);
            model.render(ccrs, mat);
//            ccrs.draw();
//
//            leadingWaveProgram.release();

            buffers.endBatch();
        }
    }

    private class BlastWave extends EffectPart {

        public BlastWave(double scale) {
            super(scale);
            maxAge = 150 + (int) (scale * 5);
        }

        @Override
        public void update() {
            if (age > maxAge) {
                setDead();
            }
            age++;
        }

        @Override
        public void render(Vector3 pos, CCRenderState ccrs, BufferSource buffers, float partialTicks) {
            float ttl = (((float) age + partialTicks) / (float) maxAge);
            double scale = radius * ttl * 2;
            double vScale = (radius / 5D) * ttl * this.scale;
            float a = age + partialTicks;

            VertexConsumer buffer = buffers.getBuffer(EXPLOSION_TYPE);
            ccrs.bind(buffer, DefaultVertexFormat.POSITION_TEX);


//            UniformCache uniforms = blastWaveProgram.pushCache();
            DEShaders.explosionTime.glUniform1f((ClientEventHandler.elapsedTicks + partialTicks + randOffset) / 10F);
            DEShaders.explosionScale.glUniform1f(ttl);
            DEShaders.explosionAlpha.glUniform1f(a < 40 ? (a - 20) / 20F : 1 - ttl);
            DEShaders.explosionType.glUniform1i(1);
//            blastWaveProgram.use();
//            blastWaveProgram.popCache(uniforms);
//
//            ccrs.startDrawing(GL11.GL_TRIANGLES, DefaultVertexFormat.POSITION_TEX);
            Matrix4 mat = RenderUtils.getMatrix(pos, new Rotation(0, 0, 1, 0), 1).apply(new Scale(scale, vScale, scale));
            model_inv.render(ccrs, mat);
            model.render(ccrs, mat);
//            ccrs.draw();
//
//            blastWaveProgram.release();

            buffers.endBatch();
        }
    }

    private class CoreEffect extends EffectPart {

        public CoreEffect(double scale) {
            super(scale);
            maxAge = 60;
        }

        @Override
        public void update() {
            if (age > maxAge) {
                setDead();
            }
            age++;
        }

        @Override
        public void render(Vector3 pos, CCRenderState ccrs, BufferSource buffers, float partialTicks) {
            float ttl = (((float) age + partialTicks) / (float) maxAge);
            double scale = (radius / 6D) * ttl * 2;
            float a = age + partialTicks;

            VertexConsumer buffer = buffers.getBuffer(EXPLOSION_TYPE);
            ccrs.bind(buffer, DefaultVertexFormat.POSITION_TEX);

//            UniformCache uniforms = coreEffectProgram.pushCache();
            DEShaders.explosionTime.glUniform1f((ClientEventHandler.elapsedTicks + partialTicks + randOffset) / 10F);
            DEShaders.explosionScale.glUniform1f(a > 35 ? ((a - 35F) / 20F) * 2 : 0);
            DEShaders.explosionAlpha.glUniform1f(a > 50 ? 1 - ((a - 50F) / 10F) : 1);
            DEShaders.explosionType.glUniform1i(0);
//            coreEffectProgram.use();
//            coreEffectProgram.popCache(uniforms);
//
//            ccrs.startDrawing(GL11.GL_TRIANGLES, DefaultVertexFormat.POSITION_TEX);
            Matrix4 mat = RenderUtils.getMatrix(pos, new Rotation(0, 0, 1, 0), 1).apply(new Scale(scale, scale / 2, scale));
            model_inv.render(ccrs, mat);
            model.render(ccrs, mat);
//            ccrs.draw();
//
//            coreEffectProgram.release();

            buffers.endBatch();
        }
    }
}
