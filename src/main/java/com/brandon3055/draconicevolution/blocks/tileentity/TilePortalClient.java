package com.brandon3055.draconicevolution.blocks.tileentity;

import codechicken.lib.vec.Vector3;
import com.brandon3055.brandonscore.BrandonsCore;
import com.brandon3055.brandonscore.utils.FacingUtils;
import com.brandon3055.brandonscore.utils.Utils;
import com.brandon3055.draconicevolution.client.DEParticles;
import com.brandon3055.draconicevolution.client.render.particle.ParticlePortal;
import com.brandon3055.draconicevolution.init.DEContent;
import com.brandon3055.draconicevolution.blocks.Portal;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.world.ClientWorld;
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

    private boolean hidden = false;
    private PlayerEntity player = null;
    public TilePortalClient() {
        super(DEContent.tile_portal_client);
    }

    @Override
    public void tick() {
        if (hidden && player != null) {
            BlockState state = getBlockState();
            if (state.getBlock() != DEContent.portal) {
                hidden = false;
                player = null;
                level.setBlock(getBlockPos(), state.setValue(Portal.VISIBLE, true), 0, 0);
                return;
            }
            Direction.Axis axis = state.getValue(Portal.AXIS);
            Vector3 vec = Vector3.fromTileCenter(this).subtract(Vector3.fromEntity(player));
            double dist = Math.abs(axis == X ? vec.x : axis == Y ? vec.y : vec.z);
            if (dist > 1.5) {
                hidden = false;
                player = null;
                level.setBlock(getBlockPos(), state.setValue(Portal.VISIBLE, true), 0, 0);
            }
            return;
        }
        Minecraft mc = Minecraft.getInstance();
        PlayerEntity player = mc.player;

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

            if (axis == Z && player.getZ() < worldPosition.getZ() + 0.5) {
                mc.particleEngine.add(new ParticlePortal((ClientWorld) level, Vector3.fromTile(this).add(rD1, rD2, 0), Vector3.fromTile(this).add(rD1 + rO1, rD2 + rO2, 0.75)));
            }
            else if (axis == Z && player.getZ() > worldPosition.getZ() + 0.5) {
                mc.particleEngine.add(new ParticlePortal((ClientWorld) level, Vector3.fromTile(this).add(rD1, rD2, 1), Vector3.fromTile(this).add(rD1 + rO1, rD2 + rO2, 0.25)));
            }
            else if (axis == X && player.getX() < worldPosition.getX() + 0.5) {
                mc.particleEngine.add(new ParticlePortal((ClientWorld) level, Vector3.fromTile(this).add(0, rD1, rD2), Vector3.fromTile(this).add(0.75, rD1 + rO1, rD2 + rO2)));
            }
            else if (axis == X && player.getX() > worldPosition.getX() + 0.5) {
                mc.particleEngine.add(new ParticlePortal((ClientWorld) level, Vector3.fromTile(this).add(1, rD1, rD2), Vector3.fromTile(this).add(0.25, rD1 + rO1, rD2 + rO2)));
            }
            else if (axis == Y && player.getY() + player.getEyeHeight() > worldPosition.getY() + 0.5) {
                mc.particleEngine.add(new ParticlePortal((ClientWorld) level, Vector3.fromTile(this).add(rD1, 1, rD2), Vector3.fromTile(this).add(rD1 + rO1, 0.25, rD2 + rO2)));
            }
            else if (axis == Y && player.getY() + player.getEyeHeight() < worldPosition.getY() + 0.5) {
                mc.particleEngine.add(new ParticlePortal((ClientWorld) level, Vector3.fromTile(this).add(rD1, 0, rD2), Vector3.fromTile(this).add(rD1 + rO1, 0.75, rD2 + rO2)));
            }
        }
    }

    public void clientArrived(PlayerEntity player) {
        BlockState state = getBlockState();
        if (state.getBlock() == DEContent.portal && level != null) {
            level.setBlock(getBlockPos(), state.setValue(Portal.VISIBLE, false), 0, 0);
            hidden = true;
            this.player = player;
        }
    }
}
