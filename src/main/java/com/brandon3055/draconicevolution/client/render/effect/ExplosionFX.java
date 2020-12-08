package com.brandon3055.draconicevolution.client.render.effect;

import codechicken.lib.render.CCModel;
import codechicken.lib.render.CCRenderState;
import codechicken.lib.render.OBJParser;
import codechicken.lib.render.RenderUtils;
import codechicken.lib.render.shader.ShaderProgram;
import codechicken.lib.render.shader.ShaderProgramBuilder;
import codechicken.lib.render.shader.UniformCache;
import codechicken.lib.render.shader.UniformType;
import codechicken.lib.vec.Matrix4;
import codechicken.lib.vec.Rotation;
import codechicken.lib.vec.Scale;
import com.brandon3055.brandonscore.lib.Vec3D;
import com.brandon3055.draconicevolution.DEConfig;
import com.brandon3055.draconicevolution.DraconicEvolution;
import com.brandon3055.draconicevolution.client.handler.ClientEventHandler;
import com.brandon3055.draconicevolution.handlers.DESoundHandler;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.particle.IParticleRenderType;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.vector.Vector3d;
import org.lwjgl.opengl.GL11;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Random;

import static codechicken.lib.render.shader.ShaderObject.StandardShaderType.FRAGMENT;
import static com.brandon3055.draconicevolution.DraconicEvolution.MODID;

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
                    .type(FRAGMENT)
                    .source(new ResourceLocation(MODID, "shaders/explosion_leading_wave.frag"))
                    .uniform("time", UniformType.FLOAT)
                    .uniform("scale", UniformType.FLOAT)
                    .uniform("alpha", UniformType.FLOAT)
            )
            .build();

    public static ShaderProgram blastWaveProgram = ShaderProgramBuilder.builder()
            .addShader("frag", shader -> shader
                    .type(FRAGMENT)
                    .source(new ResourceLocation(MODID, "shaders/explosion_blast_wave.frag"))
                    .uniform("time", UniformType.FLOAT)
                    .uniform("scale", UniformType.FLOAT)
                    .uniform("alpha", UniformType.FLOAT)
            )
            .build();

    public static ShaderProgram coreEffectProgram = ShaderProgramBuilder.builder()
            .addShader("frag", shader -> shader
                    .type(FRAGMENT)
                    .source(new ResourceLocation(MODID, "shaders/explosion_core_effect.frag"))
                    .uniform("time", UniformType.FLOAT)
                    .uniform("scale", UniformType.FLOAT)
                    .uniform("alpha", UniformType.FLOAT)
            )
            .build();

    static {
        Map<String, CCModel> map = OBJParser.parseModels(new ResourceLocation(DraconicEvolution.MODID, "models/block/reactor/reactor_core.obj"));
        model = CCModel.combine(map.values());
        model_inv = model.backfacedCopy();
    }

    private final Vec3D pos;
    public final int radius;

    public ExplosionFX(ClientWorld worldIn, Vec3D pos, int radius) {
        super(worldIn, pos.x, pos.y, pos.z);
        this.pos = pos;
        this.radius = radius;
        maxAge = 20 * 12;
        coreEffect = new CoreEffect(0);
    }

    @Override
    public void tick() {
        if (effectParts.size() > 0) {
            Iterator<EffectPart> i = effectParts.iterator();
            while (i.hasNext()) {
                EffectPart part = i.next();
                if (part.isDead()) {
                    i.remove();
                }
                else {
                    part.update();
                }
            }
        }

        coreEffect.update();

        if (age > maxAge) {
            setExpired();
        }

        if (age == 0) {
            coreEffect = new CoreEffect(0);
            ClientEventHandler.triggerExplosionEffect(pos.getPos());
        }
        else if (age == 3 || age == 8 || age == 13) {
            effectParts.addFirst(new LeadingWave(age));
        }
        else if (age > 30 && age <= 35) {
            effectParts.addFirst(new BlastWave(age - 30));
        }

        if (age == 10) {
            world.playSound(posX, posY, posZ, DESoundHandler.fusionExplosion, SoundCategory.PLAYERS, 100, 0.9F, false);
        }

        this.age++;
    }

    @Override
    public void renderParticle(IVertexBuilder buffer, ActiveRenderInfo renderInfo, float partialTicks) {
        Vector3d viewVec = renderInfo.getProjectedView();
        Vec3D pos = new Vec3D(posX - viewVec.x, posY - viewVec.y, posZ - viewVec.z);

        CCRenderState ccrs = CCRenderState.instance();
        ccrs.reset();
        ccrs.baseColour = 0xFFFFFFFF;

        float ttl = 1F - (((float) age + partialTicks) / (float) maxAge);
        ttl = Math.min(1, ttl * 5);
        double od = 1200;
        double id = radius / 100D;


        RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE);
        RenderSystem.color4f(1F, 1F, 1F, 0.15F * ttl * Math.min(1, maxAge / 25F));
        ccrs.startDrawing(GL11.GL_TRIANGLES, DefaultVertexFormats.POSITION_TEX);
        for (double i = 0; i < 16; i+= 1) {
            Matrix4 mat = RenderUtils.getMatrix(pos.toVector3(), new Rotation(0, 0, 1, 0), 1).apply(new Scale(od, id * i, od));
            model.render(ccrs, mat);
            od += id * i;
        }
        ccrs.draw();
        RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        RenderSystem.color4f(1F, 1F, 1F, 1F);

        if (!coreEffect.isDead()) {
            coreEffect.render(pos, ccrs, partialTicks);
        }

        for (EffectPart part : effectParts) {
            part.render(pos, ccrs, partialTicks);
        }
    }

    @Override
    public IParticleRenderType getRenderType() {
        return FX_HANDLER;
    }

    private static final IParticleRenderType FX_HANDLER = new FXHandler();

    public static class FXHandler implements IParticleRenderType {
        @Override
        public void beginRender(BufferBuilder builder, TextureManager p_217600_2_) {
            RenderSystem.color4f(1F, 1F, 1F, 1F);
            RenderSystem.depthMask(false);
            RenderSystem.alphaFunc(GL11.GL_GREATER, 0F);
            RenderSystem.disableTexture();
            RenderSystem.disableCull();
            RenderSystem.enableBlend();

            if (!DEConfig.reactorShaders) {
                RenderSystem.texParameter(3553, 10242, 10497);
                RenderSystem.texParameter(3553, 10243, 10497);
            }
        }

        @Override
        public void finishRender(Tessellator tessellator) {
            RenderSystem.alphaFunc(GL11.GL_GREATER, 0.1F);
            RenderSystem.enableTexture();
            RenderSystem.enableCull();

            if (!DEConfig.reactorShaders) {
                RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
            }
        }
    };

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

        public abstract void render(Vec3D pos, CCRenderState ccrs, float partialTicks);

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
        public void render(Vec3D pos, CCRenderState ccrs, float partialTicks) {
            float ttl = (((float) age + partialTicks) / (float) maxAge);
            double scale = radius * ttl * 2;

            UniformCache uniforms = leadingWaveProgram.pushCache();
            uniforms.glUniform1f("time", (ClientEventHandler.elapsedTicks + partialTicks + randOffset) / 10F);
            uniforms.glUniform1f("scale", ttl * 3);
            uniforms.glUniform1f("alpha", 1 - ttl);
            leadingWaveProgram.use();
            leadingWaveProgram.popCache(uniforms);

            ccrs.startDrawing(GL11.GL_TRIANGLES, DefaultVertexFormats.POSITION_TEX);
            Matrix4 mat = RenderUtils.getMatrix(pos.toVector3(), new Rotation(0, 0, 1, 0), 1).apply(new Scale(scale, 1, scale));
            model_inv.render(ccrs, mat);
            model.render(ccrs, mat);
            ccrs.draw();

            leadingWaveProgram.release();
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
        public void render(Vec3D pos, CCRenderState ccrs, float partialTicks) {
            float ttl = (((float) age + partialTicks) / (float) maxAge);
            double scale = radius * ttl * 2;
            double vScale = (radius / 5D) * ttl * this.scale;
            float a = age + partialTicks;

            UniformCache uniforms = blastWaveProgram.pushCache();
            uniforms.glUniform1f("time", (ClientEventHandler.elapsedTicks + partialTicks + randOffset) / 10F);
            uniforms.glUniform1f("scale", ttl);
            uniforms.glUniform1f("alpha", a < 40 ? (a - 20) / 20F : 1 - ttl);
            blastWaveProgram.use();
            blastWaveProgram.popCache(uniforms);

            ccrs.startDrawing(GL11.GL_TRIANGLES, DefaultVertexFormats.POSITION_TEX);
            Matrix4 mat = RenderUtils.getMatrix(pos.toVector3(), new Rotation(0, 0, 1, 0), 1).apply(new Scale(scale, vScale, scale));
            model_inv.render(ccrs, mat);
            model.render(ccrs, mat);
            ccrs.draw();

            blastWaveProgram.release();
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
        public void render(Vec3D pos, CCRenderState ccrs, float partialTicks) {
            float ttl = (((float) age + partialTicks) / (float) maxAge);
            double scale = (radius / 6D) * ttl * 2;
            float a = age + partialTicks;

            UniformCache uniforms = coreEffectProgram.pushCache();
            uniforms.glUniform1f("time", (ClientEventHandler.elapsedTicks + partialTicks + randOffset) / 10F);
            uniforms.glUniform1f("scale", a > 35 ? ((a - 35F) / 20F) * 2 : 0);
            uniforms.glUniform1f("alpha", a > 50 ? 1 - ((a - 50F) / 10F) : 1);
            coreEffectProgram.use();
            coreEffectProgram.popCache(uniforms);

            ccrs.startDrawing(GL11.GL_TRIANGLES, DefaultVertexFormats.POSITION_TEX);
            Matrix4 mat = RenderUtils.getMatrix(pos.toVector3(), new Rotation(0, 0, 1, 0), 1).apply(new Scale(scale, scale / 2, scale));
            model_inv.render(ccrs, mat);
            model.render(ccrs, mat);
            ccrs.draw();

            coreEffectProgram.release();
        }
    }
}
