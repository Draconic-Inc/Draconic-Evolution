package com.brandon3055.draconicevolution.client.render.effect;

import codechicken.lib.math.MathHelper;
import codechicken.lib.vec.Vector3;
import com.brandon3055.brandonscore.lib.Vec3D;
import com.brandon3055.brandonscore.utils.BCProfiler;
import com.brandon3055.brandonscore.utils.MathUtils;
import com.brandon3055.brandonscore.utils.Utils;
import com.brandon3055.draconicevolution.DraconicEvolution;
import com.brandon3055.draconicevolution.client.handler.ClientEventHandler;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

/**
 * Created by brandon3055 on 29/4/21
 */
public class StaffBeamEffect extends Particle {
    private LivingEntity shooter;
    private Vector3 origin = null;
    private Vector3 targetPos;

    public StaffBeamEffect(ClientLevel world, LivingEntity shooter, Vector3 targetPos) {
        super(world, shooter.getX(), shooter.getY(), shooter.getZ());
        this.shooter = shooter;
        this.targetPos = targetPos;
        this.lifetime = 10;
    }

    @Override
    public boolean shouldCull() {
        return false;
    }

    @Override
    public ParticleRenderType getRenderType() {
        return renderType;
    }

    @Override
    public void tick() {
        if (this.age++ >= this.lifetime) {
            this.remove();
        }
    }

    @Override
    public void render(VertexConsumer buffer, Camera renderInfo, float partialTicks) {
//        if (origin == null) {
            boolean firstPerson = shooter == renderInfo.getEntity() && !renderInfo.isDetached();
            Vector3 shooterPos = MathUtils.interpolateVec3(new Vector3(shooter.xOld, shooter.yOld, shooter.zOld), new Vector3(shooter.position()), partialTicks);
            if (firstPerson) {
                origin = shooterPos.add(0, shooter.getEyeHeight() - 0.1125, 0);
                double rot = (shooter.getViewYRot(partialTicks) + 120) * MathHelper.torad;
                double offset = 0.4 * Math.sin((shooter.getXRot() + 90) * MathHelper.torad);
                origin.add(Math.cos(rot) * offset, Math.cos((shooter.getXRot() + 90) * MathHelper.torad) * 0.4, Math.sin(rot) * offset);
            } else {
//                origin = shooterPos.add(0, shooter.getBbHeight() / 1.8, 0);
                origin = shooterPos.add(0, shooter.getEyeHeight() - 0.65, 0);
                double rot = (shooter.getViewYRot(partialTicks) + 104) * MathHelper.torad;
                double offset = 0.9 * Math.sin((shooter.getXRot() + 90) * MathHelper.torad);
                origin.add(Math.cos(rot) * offset, Math.cos((shooter.getXRot() + 90) * MathHelper.torad) * 0.9, Math.sin(rot) * offset);
            }
//        }


        float progress = Math.min(1, (age + partialTicks) / lifetime);
        double scale = 0.05 * MathUtils.clampMap(progress, 0, 0.1, 0, 1) * MathUtils.clampMap(progress, 0.5, 1, 1, 0);

        Vector3 origin = this.origin.copy();//MathUtils.interpolateVec3(this.origin, this.targetPos, MathUtils.clampMap(progress, 2D/lifetime, 1, 0, 1));
//        Vector3 origin = MathUtils.interpolateVec3(this.origin, this.targetPos, MathUtils.clampMap(progress, 2D/lifetime, 1, 0, 1));
        Vector3 targetPos = this.targetPos.copy();//MathUtils.interpolateVec3(this.origin, this.targetPos, MathUtils.clampMap(progress, 0, 0.1, 0, 1));


        Vec3 viewVec = renderInfo.getPosition();
        Vector3 source = origin.subtract(viewVec);
        Vector3 target = targetPos.subtract(viewVec);
//        Vector3 source = new Vector3(x - viewVec.x, y - viewVec.y, z - viewVec.z);
        Vector3 dirVec = source.copy().subtract(target).normalize();
        Vector3 planeA = dirVec.copy().perpendicular().normalize();
        Vector3 planeB = dirVec.copy().crossProduct(planeA);
        Vector3 planeC = planeB.copy().rotate(45 * MathHelper.torad, dirVec).normalize();
        Vector3 planeD = planeB.copy().rotate(-45 * MathHelper.torad, dirVec).normalize();
        planeA.multiply(scale);
        planeB.multiply(scale);
        planeC.multiply(scale);
        planeD.multiply(scale);
        float dist = 0.2F * (float) Utils.getDistance(new Vec3D(source), new Vec3D(target));
        float anim = (ClientEventHandler.elapsedTicks + partialTicks) / -15F;

        Vector3 p1 = source.copy().add(planeA);
        Vector3 p2 = target.copy().add(planeA);
        Vector3 p3 = source.copy().subtract(planeA);
        Vector3 p4 = target.copy().subtract(planeA);
        bufferQuad(buffer, p1, p2, p3, p4, anim, dist);

        p1 = source.copy().add(planeB);
        p2 = target.copy().add(planeB);
        p3 = source.copy().subtract(planeB);
        p4 = target.copy().subtract(planeB);
        bufferQuad(buffer, p1, p2, p3, p4, anim, dist);

        p1 = source.copy().add(planeC);
        p2 = target.copy().add(planeC);
        p3 = source.copy().subtract(planeC);
        p4 = target.copy().subtract(planeC);
        bufferQuad(buffer, p1, p2, p3, p4, anim, dist);

        p1 = source.copy().add(planeD);
        p2 = target.copy().add(planeD);
        p3 = source.copy().subtract(planeD);
        p4 = target.copy().subtract(planeD);
        bufferQuad(buffer, p1, p2, p3, p4, anim, dist);
    }

    private void bufferQuad(VertexConsumer buffer, Vector3 p1, Vector3 p2, Vector3 p3, Vector3 p4, float anim, float dist) {
        BCProfiler.RENDER.start("buffer_quad");
        buffer.vertex(p1.x, p1.y, p1.z).uv(0.5F, anim).endVertex();
        buffer.vertex(p2.x, p2.y, p2.z).uv(0.5F, dist + anim).endVertex();
        buffer.vertex(p4.x, p4.y, p4.z).uv(1.0F, dist + anim).endVertex();
        buffer.vertex(p3.x, p3.y, p3.z).uv(1.0F, anim).endVertex();
        BCProfiler.RENDER.stop();
    }

    private static ParticleRenderType renderType = new ParticleRenderType() {
        private static ResourceLocation texture = new ResourceLocation(DraconicEvolution.MODID, "textures/particle/energy_beam_draconic.png");
        @Override
        public void begin(BufferBuilder builder, TextureManager textureManager) {
            textureManager.bindForSetup(texture);
            RenderSystem.disableCull();
            RenderSystem.depthMask(false);
            RenderSystem.enableBlend();
            RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE);
            builder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        }

        @Override
        public void end(Tesselator tessellator) {
            tessellator.end();
        }
    };
}
