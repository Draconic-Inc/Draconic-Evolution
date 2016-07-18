package com.brandon3055.draconicevolution.blocks.tileentity;

import com.brandon3055.brandonscore.client.particle.BCEffectHandler;
import com.brandon3055.brandonscore.lib.Vec3D;
import com.brandon3055.brandonscore.utils.Utils;
import com.brandon3055.draconicevolution.blocks.Portal;
import com.brandon3055.draconicevolution.client.DEParticles;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;

import static net.minecraft.util.EnumFacing.Axis.X;
import static net.minecraft.util.EnumFacing.Axis.Y;
import static net.minecraft.util.EnumFacing.Axis.Z;

/**
 * Created by brandon3055 on 16/07/2016.
 */
public class TilePortalClient extends TilePortal implements ITickable{

    @Override
    public void update() {
        EntityPlayer player = Minecraft.getMinecraft().thePlayer;

        double distanceMod = Utils.getDistanceAtoB(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, player.posX, player.posY, player.posZ);
        if (worldObj.rand.nextInt(Math.max((int) (distanceMod * (distanceMod / 5D)), 1)) == 0) {
            EnumFacing.Axis axis = worldObj.getBlockState(pos).getValue(Portal.AXIS);

            double rD1 = worldObj.rand.nextDouble();
            double rD2 = worldObj.rand.nextDouble();
            double rO1 = -0.1 + worldObj.rand.nextDouble() * 0.2;
            double rO2 = -0.1 + worldObj.rand.nextDouble() * 0.2;


            if (axis == Z && player.posZ < pos.getZ() + 0.5){
                BCEffectHandler.spawnFX(DEParticles.PORTAL, worldObj, new Vec3D(pos).add(rD1, rD2, 0), new Vec3D(pos).add(rD1 + rO1, rD2 + rO2, 0.75), 256D);
            }
            else if (axis == Z && player.posZ > pos.getZ() + 0.5){
                BCEffectHandler.spawnFX(DEParticles.PORTAL, worldObj, new Vec3D(pos).add(rD1, rD2, 1), new Vec3D(pos).add(rD1 + rO1, rD2 + rO2, 0.25), 256D);
            }
            else if (axis == X && player.posX < pos.getX() + 0.5){
                BCEffectHandler.spawnFX(DEParticles.PORTAL, worldObj, new Vec3D(pos).add(0, rD1, rD2), new Vec3D(pos).add(0.75, rD1 + rO1, rD2 + rO2), 256D);
            }
            else if (axis == X && player.posX > pos.getX() + 0.5){
                BCEffectHandler.spawnFX(DEParticles.PORTAL, worldObj, new Vec3D(pos).add(1, rD1, rD2), new Vec3D(pos).add(0.25, rD1 + rO1, rD2 + rO2), 256D);
            }
            else if (axis == Y && player.posY > pos.getY() + 0.5){
                BCEffectHandler.spawnFX(DEParticles.PORTAL, worldObj, new Vec3D(pos).add(rD1, 1, rD2), new Vec3D(pos).add(rD1 + rO1, 0.25, rD2 + rO2), 256D);
            }
            else if (axis == Y && player.posY < pos.getY() + 0.5){
                BCEffectHandler.spawnFX(DEParticles.PORTAL, worldObj, new Vec3D(pos).add(rD1, 0, rD2), new Vec3D(pos).add(rD1 + rO1, 0.75, rD2 + rO2), 256D);
            }
        }
    }
}
