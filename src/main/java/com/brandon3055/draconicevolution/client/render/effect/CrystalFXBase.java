package com.brandon3055.draconicevolution.client.render.effect;

import com.brandon3055.brandonscore.lib.Vec3D;
import com.brandon3055.draconicevolution.api.energy.IENetEffectTile;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Quaternion;
import net.minecraft.client.renderer.Vector3f;
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

    protected void setPosition(Vec3D pos) {
        setPosition(pos.x, pos.y, pos.z);
    }

    protected Vector3f[] getRenderVectors(ActiveRenderInfo renderInfo, float viewX, float viewY, float viewZ, float scale) {
        Quaternion quaternion;
        quaternion = renderInfo.getRotation();
        Vector3f vector3f1 = new Vector3f(-1.0F, -1.0F, 0.0F);
        vector3f1.transform(quaternion);
        Vector3f[] renderVector = new Vector3f[]{new Vector3f(-1.0F, -1.0F, 0.0F), new Vector3f(-1.0F, 1.0F, 0.0F), new Vector3f(1.0F, 1.0F, 0.0F), new Vector3f(1.0F, -1.0F, 0.0F)};

        for (int i = 0; i < 4; ++i) {
            Vector3f vector3f = renderVector[i];
            vector3f.transform(quaternion);
            vector3f.mul(scale);
            vector3f.add(viewX, viewY, viewZ);
        }
        return renderVector;
    }
}
