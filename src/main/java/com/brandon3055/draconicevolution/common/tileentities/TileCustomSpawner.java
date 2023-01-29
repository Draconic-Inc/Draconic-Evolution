package com.brandon3055.draconicevolution.common.tileentities;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class TileCustomSpawner extends TileEntity {

    public boolean isSetToSpawn = false;
    public EntityPlayer owner;

    private final CustomSpawnerBaseLogic spawnerBaseLogic = new CustomSpawnerBaseLogic() {

        public void blockEvent(int par1) {
            worldObj.addBlockEvent(xCoord, yCoord, zCoord, Blocks.mob_spawner, par1, 0);
        }

        public World getSpawnerWorld() {
            return worldObj;
        }

        public int getSpawnerX() {
            return xCoord;
        }

        public int getSpawnerY() {
            return yCoord;
        }

        public int getSpawnerZ() {
            return zCoord;
        }
    };

    public void updateEntity() {
        if (isSetToSpawn) {
            spawnerBaseLogic.updateSpawner();
        }
        /*
         * else if (trySet && owner != null){ if (worldObj.isRemote) spawnParticles(false); if (!foundTarget && setTick
         * > 70 && target == null && setTick % 10 == 0) { foundTarget = findTargetEntity(); setTick = 71; } if
         * (foundTarget) { target.setHealth(10); if (setTick < 150) { target.setPosition(xCoord + 0.5, yCoord, zCoord +
         * 0.5); } else { target.setPosition(xCoord + 0.5, yCoord - 0.5, zCoord + 0.5); } spawnParticles(true); if
         * (setTick > 151){ spawnerBaseLogic.entityName = target.getCommandSenderName(); target.setDead(); setTick = 0;
         * trySet = false; isSetToSpawn = true; } } setTick++; if (setTick > 200){ setTick = 0; trySet = false; } } else
         * { trySet = false; }
         */
    }

    public void writeToNBT(NBTTagCompound tagCompound) {
        super.writeToNBT(tagCompound);
        spawnerBaseLogic.writeToNBT(tagCompound);
        tagCompound.setBoolean("Running", isSetToSpawn);
    }

    public void readFromNBT(NBTTagCompound tagCompound) {
        super.readFromNBT(tagCompound);
        spawnerBaseLogic.readFromNBT(tagCompound);
        isSetToSpawn = tagCompound.getBoolean("Running");
    }

    @Override
    public Packet getDescriptionPacket() {
        NBTTagCompound nbttagcompound = new NBTTagCompound();
        writeToNBT(nbttagcompound);
        return new S35PacketUpdateTileEntity(xCoord, yCoord, zCoord, 1, nbttagcompound);
    }

    @Override
    public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt) {
        readFromNBT(pkt.func_148857_g());
    }

    public CustomSpawnerBaseLogic getBaseLogic() {
        return spawnerBaseLogic;
    }
}
