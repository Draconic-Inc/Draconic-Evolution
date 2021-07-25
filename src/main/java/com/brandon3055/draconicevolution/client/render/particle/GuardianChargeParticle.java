package com.brandon3055.draconicevolution.client.render.particle;

import codechicken.lib.vec.Vector3;
import com.brandon3055.brandonscore.utils.MathUtils;
import com.brandon3055.draconicevolution.client.DETextures;
import com.brandon3055.draconicevolution.entity.guardian.control.ChargeUpPhase;
import com.brandon3055.draconicevolution.entity.guardian.control.PhaseManager;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.particle.*;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particles.BasicParticleType;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Random;

@OnlyIn(Dist.CLIENT)
public class GuardianChargeParticle extends TexturedParticle {

    private Vector3 startPos;
    private Vector3 endPos;
    private double angularPos;
    private PhaseManager phaseManager;
    public TextureAtlasSprite sprite = DETextures.ORB_PARTICLE;

    public GuardianChargeParticle(ClientWorld world, Vector3 startPos, Vector3 endPos, double angularPos, int life, PhaseManager phaseManager) {
        super(world, startPos.x, startPos.y, startPos.z);
        this.startPos = startPos;
        this.endPos = endPos;
        this.angularPos = angularPos;
        this.phaseManager = phaseManager;
        lifetime = life * 4;
        setColor(0.75F, 0F, 0F);
        scale(5);
    }

    @Override
    public boolean shouldCull() {
        return false;
    }

    public void tick() {
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;
        if (!(phaseManager.getCurrentPhase() instanceof ChargeUpPhase)) {
            alpha -= 0.1;
        }
        if (this.age++ >= this.lifetime || alpha <= 0) {
            this.remove();
        }
    }

    @Override
    public void render(IVertexBuilder builder, ActiveRenderInfo renderInfo, float partialTicks) {
        if (age + partialTicks > lifetime) return;;
        Vector3d vector3d = renderInfo.getPosition();
        float anim = (age + partialTicks) / lifetime;
        Vector3 pos = MathUtils.interpolateVec3(startPos, endPos, anim);
        float radius = (anim * 2) + (MathHelper.sin(anim * (float) Math.PI) * 5);
        float x = (float)(pos.x - vector3d.x()) + (MathHelper.sin((float) (angularPos * Math.PI * 2) + anim) * radius);
        float y = (float)(pos.y - vector3d.y());
        float z = (float)(pos.z - vector3d.z()) + (MathHelper.cos((float) (angularPos * Math.PI * 2) + anim) * radius);
        Quaternion quaternion;
        if (this.roll == 0.0F) {
            quaternion = renderInfo.rotation();
        } else {
            quaternion = new Quaternion(renderInfo.rotation());
            float f3 = MathHelper.lerp(partialTicks, this.oRoll, this.roll);
            quaternion.mul(Vector3f.ZP.rotation(f3));
        }

        Vector3f vector3f1 = new Vector3f(-1.0F, -1.0F, 0.0F);
        vector3f1.transform(quaternion);
        Vector3f[] avector3f = new Vector3f[]{new Vector3f(-1.0F, -1.0F, 0.0F), new Vector3f(-1.0F, 1.0F, 0.0F), new Vector3f(1.0F, 1.0F, 0.0F), new Vector3f(1.0F, -1.0F, 0.0F)};
        float f4 = this.getQuadSize(partialTicks);

        for(int i = 0; i < 4; ++i) {
            Vector3f vector3f = avector3f[i];
            vector3f.transform(quaternion);
            vector3f.mul(f4);
            vector3f.add(x, y, z);
        }

        float uMin = this.getU0();
        float uMax = this.getU1();
        float vMin = this.getV0();
        float vMax = this.getV1();
        int j = 240;//this.getLightColor(partialTicks);
        builder.vertex(avector3f[0].x(), avector3f[0].y(), avector3f[0].z()).uv(uMax, vMax).color(this.rCol, this.gCol, this.bCol, this.alpha).uv2(j).endVertex();
        builder.vertex(avector3f[1].x(), avector3f[1].y(), avector3f[1].z()).uv(uMax, vMin).color(this.rCol, this.gCol, this.bCol, this.alpha).uv2(j).endVertex();
        builder.vertex(avector3f[2].x(), avector3f[2].y(), avector3f[2].z()).uv(uMin, vMin).color(this.rCol, this.gCol, this.bCol, this.alpha).uv2(j).endVertex();
        builder.vertex(avector3f[3].x(), avector3f[3].y(), avector3f[3].z()).uv(uMin, vMax).color(this.rCol, this.gCol, this.bCol, this.alpha).uv2(j).endVertex();
    }

    @Override
    public IParticleRenderType getRenderType() {
        return DETextures.PARTICLE_SHEET_TRANSLUCENT;
    }

    @Override
    protected float getU0() {
        return sprite.getU0();
    }

    @Override
    protected float getU1() {
        return sprite.getU1();
    }

    @Override
    protected float getV0() {
        return sprite.getV0();
    }

    @Override
    protected float getV1() {
        return sprite.getV1();
    }
}