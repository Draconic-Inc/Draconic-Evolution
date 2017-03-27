package com.brandon3055.draconicevolution.client.render.effect;

import codechicken.lib.render.CCModel;
import codechicken.lib.render.CCOBJParser;
import codechicken.lib.render.CCRenderState;
import codechicken.lib.render.RenderUtils;
import codechicken.lib.render.shader.ShaderProgram;
import codechicken.lib.vec.Matrix4;
import codechicken.lib.vec.Rotation;
import codechicken.lib.vec.Scale;
import com.brandon3055.brandonscore.client.particle.BCParticle;
import com.brandon3055.brandonscore.client.particle.IGLFXHandler;
import com.brandon3055.brandonscore.lib.Vec3D;
import com.brandon3055.draconicevolution.client.handler.ClientEventHandler;
import com.brandon3055.draconicevolution.client.render.shaders.DEShaders;
import com.brandon3055.draconicevolution.helpers.ResourceHelperDE;
import com.brandon3055.draconicevolution.lib.DESoundHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.World;
import org.lwjgl.opengl.GL11;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;

/**
 * Created by brandon3055 on 12/02/2017.
 */
public class ExplosionFX extends BCParticle {
    public static CCModel model;
    public static CCModel model_inv;
    public CoreEffect coreEffect;
    private LinkedList<EffectPart> effectParts = new LinkedList<>();

    static {
        Map<String, CCModel> map = CCOBJParser.parseObjModels(ResourceHelperDE.getResource("models/block/obj_models/reactor_core.obj"));
        model = CCModel.combine(map.values());
        model_inv = model.backfacedCopy();
    }

    public final int radius;

    public ExplosionFX(World worldIn, Vec3D pos, int radius) {
        super(worldIn, pos);
        this.radius = radius;
        particleMaxAge = 20 * 12;
        coreEffect = new CoreEffect(0);
    }

    @Override
    public boolean isRawGLParticle() {
        return true;
    }

    @Override
    public void onUpdate() {
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

        if (particleAge > particleMaxAge) {
//            particleAge = 0;
//            effectParts.clear();
            setExpired();
        }

        int age = particleAge;
        if (age == 0) {
            coreEffect = new CoreEffect(0);
            ClientEventHandler.triggerExplosionEffect(getPos().getPos());
        }
        else if (age == 3 || age == 8 || age == 13) {
            effectParts.addFirst(new LeadingWave(age));
        }
        else if (age > 30 && age <= 35) {
            effectParts.addFirst(new BlastWave(age - 30));
        }

        if (age == 10) {
            worldObj.playSound(posX, posY, posZ, DESoundHandler.fusionExplosion, SoundCategory.PLAYERS, 100, 0.9F, false);
        }

        particleAge++;
    }

    @Override
    public void renderParticle(VertexBuffer buffer, Entity entity, float partialTicks, float rotationX, float rotationZ, float rotationYZ, float rotationXY, float rotationXZ) {
        Vec3D pos = new Vec3D(posX - interpPosX, posY - interpPosY, posZ - interpPosZ);
        CCRenderState ccrs = CCRenderState.instance();
        float ttl = 1F - (((float) particleAge + partialTicks) / (float) particleMaxAge);
        ttl = Math.min(1, ttl * 5);

        double od = 1200;
        double id = radius / 100D;

        GlStateManager.color(1F, 1F, 1F, 0.15F * ttl * Math.min(1, particleMaxAge / 25F));
        ccrs.startDrawing(GL11.GL_TRIANGLES, DefaultVertexFormats.POSITION_TEX);
        for (int i = 0; i < 8; i++) {
            Matrix4 mat = RenderUtils.getMatrix(pos.toVector3(), new Rotation(0, 0, 1, 0), 1).apply(new Scale(od, id * i, od));
            model_inv.render(ccrs, mat);
            model.render(ccrs, mat);
            od += id * i;
        }
        ccrs.draw();
        GlStateManager.color(1F, 1F, 1F, 1F);

        if (!coreEffect.isDead()) {
            coreEffect.render(pos, ccrs, partialTicks);
        }

        for (EffectPart part : effectParts) {
            part.render(pos, ccrs, partialTicks);
        }
    }

    public static final IGLFXHandler FX_HANDLER = new IGLFXHandler() {
        @Override
        public void preDraw(int layer, VertexBuffer vertexbuffer, Entity entityIn, float partialTicks, float rotationX, float rotationZ, float rotationYZ, float rotationXY, float rotationXZ) {
//            GlStateManager.disableCull();
            GlStateManager.color(1F, 1F, 1F, 1F);
            GlStateManager.depthMask(false);
            GlStateManager.alphaFunc(GL11.GL_GREATER, 0F);
            GlStateManager.disableTexture2D();
//            GlStateManager.shadeModel(GL11.GL_SMOOTH);
//            GlStateManager.matrixMode(GL11.GL_TEXTURE);

            if (!DEShaders.useShaders()) {
                GlStateManager.glTexParameterf(3553, 10242, 10497.0F);
                GlStateManager.glTexParameterf(3553, 10243, 10497.0F);
                GlStateManager.enableBlend();
                GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE);
            }
        }

        @Override
        public void postDraw(int layer, VertexBuffer vertexbuffer, Tessellator tessellator) {
            GlStateManager.alphaFunc(GL11.GL_GREATER, 0.1F);
//            GlStateManager.shadeModel(GL11.GL_FLAT);
//            GlStateManager.matrixMode(GL11.GL_MODELVIEW);

            GlStateManager.enableTexture2D();

            GlStateManager.enableCull();
            if (!DEShaders.useShaders()) {
                GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
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
            randOffset = Minecraft.getMinecraft().theWorld.rand.nextInt(3265324);
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
            double scale = radius * ttl * 2;//(ClientEventHandler.elapsedTicks + partialTicks) * 2 % 50;


            DEShaders.explosionWaveOp.setTime((ClientEventHandler.elapsedTicks + partialTicks + randOffset) / 10F);
            DEShaders.explosionWaveOp.setAlpha(1 - ttl);
            DEShaders.explosionWaveOp.setScale(ttl * 3);
            DEShaders.explosionLeadingWave.freeBindShader();

            ccrs.startDrawing(GL11.GL_TRIANGLES, DefaultVertexFormats.POSITION_TEX);
            Matrix4 mat = RenderUtils.getMatrix(pos.toVector3(), new Rotation(0, 0, 1, 0), 1).apply(new Scale(scale, 1, scale));
            model_inv.render(ccrs, mat);
            model.render(ccrs, mat);
            ccrs.draw();

            ShaderProgram.unbindShader();
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
            double scale = radius * ttl * 2;//(ClientEventHandler.elapsedTicks + partialTicks) * 2 % 50;
            double vScale = (radius / 5) * ttl * this.scale;

            float a = age + partialTicks;

            DEShaders.explosionWaveOp.setTime((ClientEventHandler.elapsedTicks + partialTicks + randOffset) / 10F);
            DEShaders.explosionWaveOp.setAlpha(a < 40 ? (a - 20) / 20F : 1 - ttl);
            DEShaders.explosionWaveOp.setScale(ttl);
            DEShaders.explosionBlastWave.freeBindShader();

            ccrs.startDrawing(GL11.GL_TRIANGLES, DefaultVertexFormats.POSITION_TEX);
            Matrix4 mat = RenderUtils.getMatrix(pos.toVector3(), new Rotation(0, 0, 1, 0), 1).apply(new Scale(scale, vScale, scale));
            model_inv.render(ccrs, mat);
            model.render(ccrs, mat);
            ccrs.draw();

            ShaderProgram.unbindShader();
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
            double scale = (radius / 6) * ttl * 2;//(ClientEventHandler.elapsedTicks + partialTicks) * 2 % 50;
//            double vScale = (radius / 5) * ttl * this.scale;
            float a = age + partialTicks;

            DEShaders.explosionWaveOp.setTime((ClientEventHandler.elapsedTicks + partialTicks + randOffset) / 10F);
            DEShaders.explosionWaveOp.setAlpha(a > 50 ? 1 - ((a - 50F) / 10F) : 1);
            DEShaders.explosionWaveOp.setScale(a > 35 ? ((a - 35F) / 20F) * 2 : 0);
            DEShaders.explosionCoreEffect.freeBindShader();

            ccrs.startDrawing(GL11.GL_TRIANGLES, DefaultVertexFormats.POSITION_TEX);
            Matrix4 mat = RenderUtils.getMatrix(pos.toVector3(), new Rotation(0, 0, 1, 0), 1).apply(new Scale(scale, scale / 2, scale));
            model_inv.render(ccrs, mat);
            model.render(ccrs, mat);
            ccrs.draw();

            ShaderProgram.unbindShader();
        }
    }
}