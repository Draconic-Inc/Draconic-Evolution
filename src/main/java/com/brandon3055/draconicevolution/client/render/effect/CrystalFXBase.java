package com.brandon3055.draconicevolution.client.render.effect;

import com.brandon3055.brandonscore.lib.Vec3D;
import com.brandon3055.draconicevolution.api.energy.IENetEffectTile;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.world.level.block.entity.BlockEntity;

/**
 * Created by brandon3055 on 29/11/2016.
 */
public abstract class CrystalFXBase<T extends BlockEntity & IENetEffectTile> extends Particle {

    protected final T tile;
    protected int ticksTillDeath = 0;
    protected float fxState;
    public boolean renderEnabled = true;
    private int ttl = 10;

    public CrystalFXBase(ClientLevel worldIn, T tile) {
        super(worldIn, tile.getBlockPos().getX() + 0.5, tile.getBlockPos().getY() + 0.5, tile.getBlockPos().getZ() + 0.5);
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
        if (ttl-- <= 0) {
            remove();
        }
    }

    @Override
    public void tick() {
        ttl = 10;
    }

    protected void setPosition(Vec3D pos) {
        setPos(pos.x, pos.y, pos.z);
    }

    protected Vector3f[] getRenderVectors(Camera renderInfo, float viewX, float viewY, float viewZ, float scale) {
        Quaternion quaternion;
        quaternion = renderInfo.rotation();
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
