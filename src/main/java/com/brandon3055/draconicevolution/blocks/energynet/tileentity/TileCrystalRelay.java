package com.brandon3055.draconicevolution.blocks.energynet.tileentity;

import com.brandon3055.brandonscore.lib.Vec3D;
import com.brandon3055.draconicevolution.blocks.energynet.EnergyCrystal;
import com.brandon3055.draconicevolution.client.render.effect.CrystalFXRing;
import com.brandon3055.draconicevolution.client.render.effect.CrystalGLFXBase;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Created by brandon3055 on 19/11/2016.
 */
public class TileCrystalRelay extends TileCrystalBase {

    public TileCrystalRelay() {
        super();
    }

    //region Rendering

    @Override
    public EnergyCrystal.CrystalType getType() {
        return EnergyCrystal.CrystalType.RELAY;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public CrystalGLFXBase createStaticFX() {
        return new CrystalFXRing(world, this);
    }

    @Override
    public Vec3D getBeamLinkPos(BlockPos linkTo) {
        Vec3D thisVec = Vec3D.getCenter(pos);
        Vec3D targVec = Vec3D.getCenter(linkTo);
        double dist = thisVec.distXZ(targVec);
        double offM = 0.4D;

        if (dist == 0) {
            if (pos.getY() > linkTo.getY()) {
                return thisVec.subtract(0, 0.4, 0);
            }
            else {
                return thisVec.subtract(0, -0.4, 0);
            }
        }

        double xDist = thisVec.x - targVec.x;
        double zDist = thisVec.z - targVec.z;
        double xOff = xDist / dist;
        double zOff = zDist / dist;

        return thisVec.subtract(xOff * offM, 0, zOff * offM);
    }

    @Override
    public boolean renderBeamTermination() {
        return true;
    }

    //endregion

}
