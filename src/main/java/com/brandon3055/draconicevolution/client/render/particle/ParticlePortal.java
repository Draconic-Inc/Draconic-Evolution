package com.brandon3055.draconicevolution.client.render.particle;

import codechicken.lib.vec.Vector3;
import com.brandon3055.brandonscore.client.BCSprites;
import com.brandon3055.brandonscore.client.particle.BCParticle;
import com.brandon3055.brandonscore.client.particle.IBCParticleFactory;
import com.brandon3055.brandonscore.lib.Vec3D;
import com.brandon3055.brandonscore.utils.MathUtils;
import com.brandon3055.brandonscore.utils.Utils;
import com.brandon3055.draconicevolution.client.DESprites;
import com.brandon3055.draconicevolution.client.DETextures;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.*;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particles.BasicParticleType;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * Created by brandon3055 on 17/07/2016.
 */
public class ParticlePortal extends SpriteTexturedParticle {

    public Vector3 target;
    public Vector3 start;
    public float baseScale;

    public ParticlePortal(ClientWorld worldIn, Vector3 pos, Vector3 target) {
        super(worldIn, pos.x, pos.y, pos.z);
        this.start = pos;
        this.target = target;
        float speed = 0.12F + (random.nextFloat() * 0.2F);
        this.xd = (target.x - start.x) * speed;
        this.yd = (target.y - start.y) * speed;
        this.zd = (target.z - start.z) * speed;
        sprite = DETextures.PORTAL_PARTICLE;
        this.lifetime = 120;
        this.rCol = this.gCol = this.bCol = 1.0f;
        float baseSize = 0.05F + ((float) Math.sqrt(Minecraft.getInstance().player.distanceToSqr(pos.x, pos.y, pos.z))) * 0.007F;
        this.baseScale = (baseSize + (random.nextFloat() * (baseSize * 2F))) * 0.1F;
        this.quadSize = 0;
        this.hasPhysics = false;
    }

    @Override
    public IParticleRenderType getRenderType() {
        return DETextures.PARTICLE_SHEET_TRANSLUCENT;
    }

    @Override
    public void tick() {
        double distToTarget = MathUtils.distance(new Vector3(x, y, z), target);
        if (age >= lifetime || distToTarget < 0.15) {
            remove();
            return;
        }

        double startDist = MathUtils.distance(start, target);
        quadSize = ((float) (distToTarget / startDist)) * baseScale;
        age++;
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;
        this.move(this.xd, this.yd, this.zd);
    }

    @Override
    protected int getLightColor(float p_189214_1_) {
        return 0xF000F0;
    }
}
