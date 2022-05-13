package com.brandon3055.draconicevolution.client.render.particle;

import codechicken.lib.vec.Vector3;
import com.brandon3055.brandonscore.utils.MathUtils;
import com.brandon3055.draconicevolution.client.DETextures;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.TextureSheetParticle;

/**
 * Created by brandon3055 on 17/07/2016.
 */
public class ParticlePortal extends TextureSheetParticle {

    public Vector3 target;
    public Vector3 start;
    public float baseScale;

    public ParticlePortal(ClientLevel worldIn, Vector3 pos, Vector3 target) {
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
    public ParticleRenderType getRenderType() {
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
