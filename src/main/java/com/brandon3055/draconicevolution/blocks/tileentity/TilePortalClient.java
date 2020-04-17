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

        double distanceMod = Utils.getDistanceAtoB(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, player.posX, player.posY, player.posZ);
        if (world.rand.nextInt(Math.max((int) (distanceMod * (distanceMod / 5D)), 1)) == 0) {
            BlockState state = world.getBlockState(pos);
            if (state.getBlock() != DEContent.portal) {
                return;
            }
            Direction.Axis axis = state.get(Portal.AXIS);

            double rD1 = world.rand.nextDouble();
            double rD2 = world.rand.nextDouble();
            double rO1 = -0.1 + world.rand.nextDouble() * 0.2;
            double rO2 = -0.1 + world.rand.nextDouble() * 0.2;


            //TODO particles
            if (axis == Z && player.posZ < pos.getZ() + 0.5) {
//                BCEffectHandler.spawnFX(DEParticles.PORTAL, world, new Vec3D(pos).add(rD1, rD2, 0), new Vec3D(pos).add(rD1 + rO1, rD2 + rO2, 0.75), 256D);
            }
            else if (axis == Z && player.posZ > pos.getZ() + 0.5) {
//                BCEffectHandler.spawnFX(DEParticles.PORTAL, world, new Vec3D(pos).add(rD1, rD2, 1), new Vec3D(pos).add(rD1 + rO1, rD2 + rO2, 0.25), 256D);
            }
            else if (axis == X && player.posX < pos.getX() + 0.5) {
//                BCEffectHandler.spawnFX(DEParticles.PORTAL, world, new Vec3D(pos).add(0, rD1, rD2), new Vec3D(pos).add(0.75, rD1 + rO1, rD2 + rO2), 256D);
            }
            else if (axis == X && player.posX > pos.getX() + 0.5) {
//                BCEffectHandler.spawnFX(DEParticles.PORTAL, world, new Vec3D(pos).add(1, rD1, rD2), new Vec3D(pos).add(0.25, rD1 + rO1, rD2 + rO2), 256D);
            }
            else if (axis == Y && player.posY + player.getEyeHeight() > pos.getY() + 0.5) {
//                BCEffectHandler.spawnFX(DEParticles.PORTAL, world, new Vec3D(pos).add(rD1, 1, rD2), new Vec3D(pos).add(rD1 + rO1, 0.25, rD2 + rO2), 256D);
            }
            else if (axis == Y && player.posY + player.getEyeHeight() < pos.getY() + 0.5) {
//                BCEffectHandler.spawnFX(DEParticles.PORTAL, world, new Vec3D(pos).add(rD1, 0, rD2), new Vec3D(pos).add(rD1 + rO1, 0.75, rD2 + rO2), 256D);
            }
        }
    }


    @Override
    public void propRenderUpdate(long updateTime, boolean reignite) {
        this.updateTime = updateTime;

        BlockState state = world.getBlockState(pos);
        if (state.getBlock() != DEContent.portal) {
            return;
        }
        Direction.Axis axis = state.get(Portal.AXIS);
        for (BlockPos offset : FacingUtils.getAroundAxis(axis)) {
            TileEntity tile = world.getTileEntity(pos.add(offset));
            if (tile instanceof TilePortalClient && ((TilePortalClient) tile).updateTime != updateTime) {
                ((TilePortalClient) tile).propRenderUpdate(updateTime, reignite);
            }
        }

        if (reignite) {
            Random rand = world.rand;
            world.addParticle(ParticleTypes.EXPLOSION, pos.getX() + rand.nextDouble(), pos.getY() + rand.nextDouble(), pos.getZ() + rand.nextDouble(), rand.nextDouble() * 0.03, rand.nextDouble() * 0.03, rand.nextDouble() * 0.03);
//            DelayedTask.run(3, () -> world. markBlockRangeForRenderUpdate(pos, pos));//TODO
        }
        else {
//            world.markBlockRangeForRenderUpdate(pos, pos);
        }
    }
}
