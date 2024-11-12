package com.brandon3055.draconicevolution.blocks.energynet.tileentity;

import com.brandon3055.brandonscore.api.TechLevel;
import com.brandon3055.brandonscore.capability.CapabilityOP;
import com.brandon3055.brandonscore.lib.Vec3D;
import com.brandon3055.draconicevolution.blocks.energynet.EnergyCrystal;
import com.brandon3055.draconicevolution.blocks.tileentity.TileDislocatorReceptacle;
import com.brandon3055.draconicevolution.client.render.effect.CrystalFXBase;
import com.brandon3055.draconicevolution.client.render.effect.CrystalFXRing;
import com.brandon3055.draconicevolution.init.DEContent;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;

/**
 * Created by brandon3055 on 19/11/2016.
 */
public class TileCrystalRelay extends TileCrystalBase {

    public TileCrystalRelay(BlockPos pos, BlockState state) {
        super(DEContent.TILE_RELAY_CRYSTAL.get(), pos, state);
    }

    public TileCrystalRelay(TechLevel techLevel, BlockPos pos, BlockState state) {
        super(DEContent.TILE_RELAY_CRYSTAL.get(), techLevel, pos, state);
    }

    public static void register(RegisterCapabilitiesEvent event) {
        capability(event, DEContent.TILE_RELAY_CRYSTAL, CapabilityOP.BLOCK);
    }

    //region Rendering

    @Override
    public EnergyCrystal.CrystalType getCrystalType() {
        return EnergyCrystal.CrystalType.RELAY;
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public CrystalFXBase createStaticFX() {
        return new CrystalFXRing((ClientLevel)level, this);
    }

    @Override
    public Vec3D getBeamLinkPos(BlockPos linkTo) {
        Vec3D thisVec = Vec3D.getCenter(worldPosition);
        Vec3D targVec = Vec3D.getCenter(linkTo);
        BlockEntity target = level.getBlockEntity(linkTo);
        if (target instanceof TileDislocatorReceptacle) {
            targVec = ((TileDislocatorReceptacle) target).getBeamLinkPos(worldPosition);
        }
        double dist = thisVec.distXZ(targVec);
        double offM = 0.4D;

        if (dist == 0) {
            if (worldPosition.getY() > linkTo.getY()) {
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
