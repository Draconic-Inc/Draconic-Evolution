package com.brandon3055.draconicevolution.blocks.tileentity;

import com.brandon3055.brandonscore.utils.FacingUtils;
import com.brandon3055.brandonscore.utils.Utils;
import com.brandon3055.draconicevolution.init.DEContent;
import com.brandon3055.draconicevolution.blocks.Portal;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;

import java.util.Random;

import static net.minecraft.util.Direction.Axis.*;

/**
 * Created by brandon3055 on 16/07/2016.
 */
public class TilePortalClient extends TilePortal implements ITickableTileEntity {

    public TilePortalClient() {
        super(DEContent.tile_portal_client);
    }

    @Override
    public void tick() {
        if (disabled) return;
        PlayerEntity player = Minecraft.getInstance().player;

        double distanceMod = Utils.getDistanceAtoB(worldPosition.getX() + 0.5, worldPosition.getY() + 0.5, worldPosition.getZ() + 0.5, player.getX(), player.getY(), player.getZ());
        if (level.random.nextInt(Math.max((int) (distanceMod * (distanceMod / 5D)), 1)) == 0) {
            BlockState state = level.getBlockState(worldPosition);
            if (state.getBlock() != DEContent.portal) {
                return;
            }
            Direction.Axis axis = state.getValue(Portal.AXIS);

            double rD1 = level.random.nextDouble();
            double rD2 = level.random.nextDouble();
            double rO1 = -0.1 + level.random.nextDouble() * 0.2;
            double rO2 = -0.1 + level.random.nextDouble() * 0.2;


            //TODO particles
            if (axis == Z && player.getZ() < worldPosition.getZ() + 0.5) {
//                BCEffectHandler.spawnFX(DEParticles.PORTAL, world, new Vec3D(pos).add(rD1, rD2, 0), new Vec3D(pos).add(rD1 + rO1, rD2 + rO2, 0.75), 256D);
            }
            else if (axis == Z && player.getZ() > worldPosition.getZ() + 0.5) {
//                BCEffectHandler.spawnFX(DEParticles.PORTAL, world, new Vec3D(pos).add(rD1, rD2, 1), new Vec3D(pos).add(rD1 + rO1, rD2 + rO2, 0.25), 256D);
            }
            else if (axis == X && player.getX() < worldPosition.getX() + 0.5) {
//                BCEffectHandler.spawnFX(DEParticles.PORTAL, world, new Vec3D(pos).add(0, rD1, rD2), new Vec3D(pos).add(0.75, rD1 + rO1, rD2 + rO2), 256D);
            }
            else if (axis == X && player.getX() > worldPosition.getX() + 0.5) {
//                BCEffectHandler.spawnFX(DEParticles.PORTAL, world, new Vec3D(pos).add(1, rD1, rD2), new Vec3D(pos).add(0.25, rD1 + rO1, rD2 + rO2), 256D);
            }
            else if (axis == Y && player.getY() + player.getEyeHeight() > worldPosition.getY() + 0.5) {
//                BCEffectHandler.spawnFX(DEParticles.PORTAL, world, new Vec3D(pos).add(rD1, 1, rD2), new Vec3D(pos).add(rD1 + rO1, 0.25, rD2 + rO2), 256D);
            }
            else if (axis == Y && player.getY() + player.getEyeHeight() < worldPosition.getY() + 0.5) {
//                BCEffectHandler.spawnFX(DEParticles.PORTAL, world, new Vec3D(pos).add(rD1, 0, rD2), new Vec3D(pos).add(rD1 + rO1, 0.75, rD2 + rO2), 256D);
            }
        }
    }


    @Override
    public void propRenderUpdate(long updateTime, boolean reignite) {
        this.updateTime = updateTime;

        BlockState state = level.getBlockState(worldPosition);
        if (state.getBlock() != DEContent.portal) {
            return;
        }
        Direction.Axis axis = state.getValue(Portal.AXIS);
        for (BlockPos offset : FacingUtils.getAroundAxis(axis)) {
            TileEntity tile = level.getBlockEntity(worldPosition.offset(offset));
            if (tile instanceof TilePortalClient && ((TilePortalClient) tile).updateTime != updateTime) {
                ((TilePortalClient) tile).propRenderUpdate(updateTime, reignite);
            }
        }

        if (reignite) {
            Random rand = level.random;
            level.addParticle(ParticleTypes.EXPLOSION, worldPosition.getX() + rand.nextDouble(), worldPosition.getY() + rand.nextDouble(), worldPosition.getZ() + rand.nextDouble(), rand.nextDouble() * 0.03, rand.nextDouble() * 0.03, rand.nextDouble() * 0.03);
//            DelayedTask.run(3, () -> world. markBlockRangeForRenderUpdate(pos, pos));//TODO
        }
        else {
//            world.markBlockRangeForRenderUpdate(pos, pos);
        }
    }
}
