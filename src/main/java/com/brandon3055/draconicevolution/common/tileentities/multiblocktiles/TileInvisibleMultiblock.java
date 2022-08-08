package com.brandon3055.draconicevolution.common.tileentities.multiblocktiles;

import com.brandon3055.draconicevolution.common.blocks.multiblock.MultiblockHelper.TileLocation;
import com.brandon3055.draconicevolution.common.handler.BalanceConfigHandler;
import com.brandon3055.draconicevolution.common.utills.LogHelper;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;

/**
 * Created by Brandon on 26/07/2014.
 */
public class TileInvisibleMultiblock extends TileEntity {
    public TileLocation master = new TileLocation();

    @Override
    public boolean canUpdate() {
        return false;
    }

    public boolean isMasterOnline() {
        TileEnergyStorageCore tile =
                (worldObj.getTileEntity(master.getXCoord(), master.getYCoord(), master.getZCoord()) != null
                                && worldObj.getTileEntity(master.getXCoord(), master.getYCoord(), master.getZCoord())
                                        instanceof TileEnergyStorageCore)
                        ? (TileEnergyStorageCore)
                                worldObj.getTileEntity(master.getXCoord(), master.getYCoord(), master.getZCoord())
                        : null;
        if (tile == null) {
            return false;
        }
        return tile.online;
    }

    public TileEnergyStorageCore getMaster() {
        if (master == null) return null;
        TileEnergyStorageCore tile =
                (worldObj.getTileEntity(master.getXCoord(), master.getYCoord(), master.getZCoord()) != null
                                && worldObj.getTileEntity(master.getXCoord(), master.getYCoord(), master.getZCoord())
                                        instanceof TileEnergyStorageCore)
                        ? (TileEnergyStorageCore)
                                worldObj.getTileEntity(master.getXCoord(), master.getYCoord(), master.getZCoord())
                        : null;
        return tile;
    }

    @Override
    public void writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        // if (master != null)
        master.writeToNBT(compound, "Key");
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        // if (master != null)
        master.readFromNBT(compound, "Key");
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

    public void isStructureStillValid() {
        if (getMaster() == null) {
            LogHelper.error("{Tile} Master = null reverting!");
            revert();
            return;
        }
        if (!getMaster().isOnline()) revert();
    }

    private void revert() {
        int meta = worldObj.getBlockMetadata(xCoord, yCoord, zCoord);
        if (meta == 0) {
            worldObj.setBlock(
                    xCoord,
                    yCoord,
                    zCoord,
                    BalanceConfigHandler.energyStorageStructureOuterBlock,
                    BalanceConfigHandler.energyStorageStructureOuterBlockMetadata,
                    3);
        } else if (meta == 1) {
            worldObj.setBlock(
                    xCoord,
                    yCoord,
                    zCoord,
                    BalanceConfigHandler.energyStorageStructureBlock,
                    BalanceConfigHandler.energyStorageStructureBlockMetadata,
                    3);
        }
    }
}
