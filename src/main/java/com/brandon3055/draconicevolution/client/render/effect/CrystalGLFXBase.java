package com.brandon3055.draconicevolution.client.render.effect;

import com.brandon3055.brandonscore.client.particle.BCParticle;
import com.brandon3055.brandonscore.lib.Vec3D;
import com.brandon3055.draconicevolution.blocks.energynet.tileentity.TileCrystalBase;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;

/**
 * Created by brandon3055 on 29/11/2016.
 */
public abstract class CrystalGLFXBase<T extends TileCrystalBase> extends BCParticle {

    protected final T tile;
    protected int ticksTillDeath = 0;
    protected float fxState;

    public CrystalGLFXBase(World worldIn, T tile) {
        super(worldIn, Vec3D.getCenter(tile.getPos()));
        this.tile = tile;
        this.ticksTillDeath = 4;
        this.texturesPerRow = 8;
        this.particleScale = 1.5F;
    }

    @Override
    public boolean isRawGLParticle() {
        return true;
    }

    public void updateFX(float fxState) {
        this.fxState = fxState;
        ticksTillDeath = 4;
    }

    @Override
    public abstract void onUpdate();

    @Override
    public abstract void renderParticle(VertexBuffer vertexbuffer, Entity entity, float partialTicks, float rotationX, float rotationZ, float rotationYZ, float rotationXY, float rotationXZ);
}
