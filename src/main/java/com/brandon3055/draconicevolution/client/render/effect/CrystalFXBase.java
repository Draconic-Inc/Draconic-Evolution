package com.brandon3055.draconicevolution.client.render.effect;

import com.brandon3055.brandonscore.lib.Vec3D;
import com.brandon3055.draconicevolution.api.energy.IENetEffectTile;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

/**
 * Created by brandon3055 on 29/11/2016.
 */
public abstract class CrystalFXBase<T extends TileEntity & IENetEffectTile> extends Particle {

    protected final T tile;
    protected int ticksTillDeath = 0;
    protected float fxState;
    public boolean renderEnabled = true;

    public CrystalFXBase(World worldIn, T tile) {
        super(worldIn, tile.getPos().getX() + 0.5, tile.getPos().getY() + 0.5, tile.getPos().getZ() + 0.5);
        this.tile = tile;
    }

    //    public CrystalGLFXBase(World worldIn, T tile) {
//        super(worldIn, Vec3D.getCenter(tile.getPos()));
//        this.tile = tile;
//        this.ticksTillDeath = 4;
//        this.texturesPerRow = 8;
//        this.baseScale = 1.5F;
//    }


    public void updateFX(float fxState) {
        this.fxState = fxState;
        ticksTillDeath = 4;
    }

    @Override
    public abstract void tick();

    @Override
    public abstract void renderParticle(BufferBuilder buffer, ActiveRenderInfo entityIn, float partialTicks, float rotationX, float rotationZ, float rotationYZ, float rotationXY, float rotationXZ);

    protected void setPosition(Vec3D pos) {
        setPosition(pos.x, pos.y, pos.z);
    }
}
