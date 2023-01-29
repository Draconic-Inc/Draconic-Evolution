package com.brandon3055.draconicevolution.common.tileentities.energynet;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraftforge.common.util.ForgeDirection;

import com.brandon3055.brandonscore.common.utills.DataUtills;
import com.brandon3055.brandonscore.common.utills.ItemNBTHelper;
import com.brandon3055.brandonscore.common.utills.Utills;
import com.brandon3055.draconicevolution.DraconicEvolution;
import com.brandon3055.draconicevolution.common.ModItems;
import com.brandon3055.draconicevolution.common.handler.BalanceConfigHandler;
import com.brandon3055.draconicevolution.common.items.tools.Wrench;
import com.brandon3055.draconicevolution.common.lib.References;
import com.brandon3055.draconicevolution.common.tileentities.TileObjectSync;
import com.brandon3055.draconicevolution.common.utills.EnergyStorage;
import com.brandon3055.draconicevolution.common.utills.LogHelper;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/**
 * Created by Brandon on 10/02/2015.
 */
public abstract class TileRemoteEnergyBase extends TileObjectSync implements IRemoteEnergyHandler { // todo optimize
                                                                                                    // (add cutoff,
                                                                                                    // explore possible
                                                                                                    // cach
                                                                                                    // optimization)

    /**
     * The tier of the relay (0-1)
     */
    protected int powerTier;

    protected EnergyStorage storage = new EnergyStorage(0);
    public List<LinkedEnergyDevice> linkedDevices = new ArrayList<LinkedEnergyDevice>();
    protected int lastTickEnergy;
    private int tick = 0;

    public byte inView = 0;

    public void updateStorage() {
        storage.setCapacity(getCap());
        storage.setMaxExtract(getExt());
        storage.setMaxReceive(getRec());
    }

    public abstract int getCap();

    public abstract int getRec();

    public abstract int getExt();

    @Override
    public void updateEntity() {
        tick++;
        updateLinkedDevices();
        if (linkedDevices.size() == 0) {
            detectAndSendChanges(-1);
        }
        if (worldObj.isRemote && inView > 0) {
            --inView;
        }
    }

    // **********Distribution Logic**********

    private void updateLinkedDevices() {
        for (LinkedEnergyDevice device : linkedDevices) {
            if (!device.isValid(worldObj)) {
                if (worldObj.getChunkFromBlockCoords(device.xCoord, device.zCoord).isChunkLoaded) {
                    linkedDevices.remove(device);
                    updateLinkedDevices();
                    return;
                } else {
                    continue;
                }
            }

            IRemoteEnergyHandler remoteTile = device.getEnergyTile(worldObj);

            if (worldObj.isRemote) {
                int rType = 0;
                if (this instanceof TileEnergyRelay || this instanceof TileWirelessEnergyTransceiver) {
                    if (remoteTile instanceof TileEnergyTransceiver) rType = 2;
                    else rType = 3;
                } else if (this instanceof TileEnergyTransceiver) {
                    if (remoteTile instanceof TileEnergyRelay || remoteTile instanceof TileWirelessEnergyTransceiver)
                        rType = 1;
                    else rType = 0;
                }
                device.beam = DraconicEvolution.proxy.energyBeam(
                        worldObj,
                        getBeamX(),
                        getBeamY(),
                        getBeamZ(),
                        remoteTile.getBeamX(),
                        remoteTile.getBeamY(),
                        remoteTile.getBeamZ(),
                        (int) device.energyFlow,
                        getPowerTier() == 1,
                        device.beam,
                        true,
                        rType);
            } else {
                double difference = getCapacity() - remoteTile.getCapacity();
                double energyToEqual = Math
                        .round(difference / 100D * (double) remoteTile.getStorage().getMaxEnergyStored() / 2.01D);
                double maxFlow = Math.min(
                        energyToEqual,
                        (double) Math.min(
                                storage.getMaxExtract(),
                                remoteTile.getStorage().getMaxEnergyStored()
                                        - remoteTile.getStorage().getEnergyStored()));

                device.energyFlow = getFlow(getCapacity(), remoteTile.getCapacity());
                int flow = (int) ((device.energyFlow / 100D) * maxFlow);
                int transfered = storage.extractEnergy(
                        remoteTile.getStorage().receiveEnergy(storage.extractEnergy(flow, true), false),
                        false);

                device.energyFlow = Math.min(((double) transfered / 10000D) * 100D, 100D);

                detectAndSendChanges(linkedDevices.indexOf(device));
            }
        }
    }

    protected void detectAndSendChanges(int index) {
        if (worldObj.isRemote) return;

        boolean forceSend = (tick + xCoord + yCoord + zCoord) % 100 == 0;

        if ((tick + xCoord + yCoord + zCoord) % 20 == 0 || forceSend) {
            if (index >= 0 && (linkedDevices.get(index).energyFlow != linkedDevices.get(index).lastTickEnergyFlow
                    || forceSend)) {
                sendObjectToClient(
                        References.INT_PAIR_ID,
                        0,
                        new DataUtills.IntPair(index, (int) linkedDevices.get(index).energyFlow));
                linkedDevices.get(index).lastTickEnergyFlow = linkedDevices.get(index).energyFlow;
            }

            if (storage.getEnergyStored() != lastTickEnergy || forceSend)
                sendObjectToClient(References.INT_ID, 1, storage.getEnergyStored());
        }
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void receiveObjectFromServer(int index, Object object) {
        if (index == 0 && object instanceof DataUtills.IntPair
                && linkedDevices.size() > ((DataUtills.IntPair) object).i1) {
            linkedDevices.get(((DataUtills.IntPair) object).i1).energyFlow = ((DataUtills.IntPair) object).i2;
        } else if (index == 1) storage.setEnergyStored((Integer) object);
    }

    @Override
    public double getCapacity() {
        return ((double) storage.getEnergyStored() / (double) storage.getMaxEnergyStored()) * 100D;
    }

    /**
     * Calculates the energy flow based on the local buffer and the remote buffer return double between 0 to 100
     */
    public double getFlow(double localCap, double remoteCap) {
        return Math.max(0, Math.min(100, (localCap - remoteCap) * 10D /* Flow Multiplier */));
    }

    public int getPowerTier() {
        return powerTier;
    }

    public void onBlockActivated(EntityPlayer player) {
        ItemStack wrench = player.getHeldItem();

        if (wrench != null && wrench.getItem() == Items.stick) {
            for (LinkedEnergyDevice ld : linkedDevices) {
                if (ld.beam != null) LogHelper.info(ld.beam.getFlow());
            }
        }

        if (wrench == null || wrench.getItem() != ModItems.wrench) return;
        String mode = Wrench.getMode(wrench);
        NBTTagCompound linkDat = null;
        if (wrench.hasTagCompound() && wrench.getTagCompound().hasKey("LinkData"))
            linkDat = wrench.getTagCompound().getCompoundTag("LinkData");

        if (mode.equals(Wrench.BIND_MODE)) {
            if (linkDat != null && linkDat.hasKey("Bound") && linkDat.getBoolean("Bound")) {
                // LogHelper.info("Bind");
                handleBinding(
                        player,
                        linkDat.getInteger("XCoord"),
                        linkDat.getInteger("YCoord"),
                        linkDat.getInteger("ZCoord"),
                        true);
                linkDat.setBoolean("Bound", false);
            } else {
                // LogHelper.info("Bind2");
                linkDat = new NBTTagCompound();
                linkDat.setInteger("XCoord", xCoord);
                linkDat.setInteger("YCoord", yCoord);
                linkDat.setInteger("ZCoord", zCoord);
                linkDat.setBoolean("Bound", true);
                ItemNBTHelper.getCompound(wrench).setTag("LinkData", linkDat);
                if (worldObj.isRemote)
                    player.addChatComponentMessage(new ChatComponentTranslation("msg.de.posSaved.txt"));
            }
        } else if (mode.equals(Wrench.UNBIND_MODE)) {
            if (linkDat != null && linkDat.hasKey("Bound") && linkDat.getBoolean("Bound")) {
                for (LinkedEnergyDevice ld : linkedDevices) {
                    if (ld.xCoord == linkDat.getInteger("XCoord") && ld.yCoord == linkDat.getInteger("YCoord")
                            && ld.zCoord == linkDat.getInteger("ZCoord")) {

                        if (!(ld.getTile(worldObj) instanceof TileRemoteEnergyBase)) {
                            LogHelper.error(
                                    "TileRemoteEnergyBase - UNBIND - Remote tile invalid (This should be reported)");
                            break;
                        }

                        for (LinkedEnergyDevice rld : ((TileRemoteEnergyBase) ld.getTile(worldObj)).linkedDevices) {
                            if (rld.xCoord == xCoord && rld.yCoord == yCoord && rld.zCoord == zCoord) {
                                ((TileRemoteEnergyBase) ld.getTile(worldObj)).linkedDevices.remove(rld);
                                break;
                            }
                        }

                        linkedDevices.remove(ld);
                        break;
                    }
                }

                linkDat.setBoolean("Bound", false);
            } else {
                linkDat = new NBTTagCompound();
                linkDat.setInteger("XCoord", xCoord);
                linkDat.setInteger("YCoord", yCoord);
                linkDat.setInteger("ZCoord", zCoord);
                linkDat.setBoolean("Bound", true);
                ItemNBTHelper.getCompound(wrench).setTag("LinkData", linkDat);
                if (worldObj.isRemote)
                    player.addChatComponentMessage(new ChatComponentTranslation("msg.de.posSaved.txt"));
            }
        } else if (mode.equals(Wrench.CLEAR_BINDINGS)) {
            if (this instanceof TileWirelessEnergyTransceiver)
                ((TileWirelessEnergyTransceiver) this).receiverList.clear();

            for (LinkedEnergyDevice ld : linkedDevices) {
                if (!(ld.getTile(worldObj) instanceof TileRemoteEnergyBase)) {
                    LogHelper.error("TileRemoteEnergyBase - UNBIND - Remote tile invalid (This should be reported)");
                    break;
                }

                for (LinkedEnergyDevice rld : ((TileRemoteEnergyBase) ld.getTile(worldObj)).linkedDevices) {
                    if (rld.xCoord == xCoord && rld.yCoord == yCoord && rld.zCoord == zCoord) {
                        ((TileRemoteEnergyBase) ld.getTile(worldObj)).linkedDevices.remove(rld);
                        break;
                    }
                }
            }

            linkedDevices.clear();
        } else {
            handleOther(player, wrench);
        }
    }

    @Override
    public boolean handleBinding(EntityPlayer player, int x, int y, int z, boolean callOther) {
        int range = powerTier == 0 ? BalanceConfigHandler.energyDeviceBasicLinkingRange
                : BalanceConfigHandler.energyDeviceAdvancedLinkingRange;
        IRemoteEnergyHandler tile = worldObj.getTileEntity(x, y, z) instanceof IRemoteEnergyHandler
                ? (IRemoteEnergyHandler) worldObj.getTileEntity(x, y, z)
                : null;

        if (tile == null || (x == xCoord && y == yCoord && z == zCoord)) {
            if (worldObj.isRemote)
                player.addChatComponentMessage(new ChatComponentTranslation("msg.de.invalidTile.txt"));
            return false;
        } else if (Utills.getDistanceAtoB(xCoord, yCoord, zCoord, x, y, z) > range) {
            if (worldObj.isRemote)
                player.addChatComponentMessage(new ChatComponentTranslation("msg.de.outOfRange.txt"));
            return false;
        } else if (linkedDevices.size() >= getMaxConnections()) {
            if (worldObj.isRemote)
                player.addChatComponentMessage(new ChatComponentTranslation("msg.de.maxConnections.txt"));
            return false;
        }

        if (callOther && !tile.handleBinding(player, xCoord, yCoord, zCoord, false)) {
            // LogHelper.info("other Invalid");
            return false;
        }

        if (callOther && worldObj.isRemote)
            player.addChatComponentMessage(new ChatComponentTranslation("msg.de.linked.txt"));

        for (LinkedEnergyDevice ld : linkedDevices) {
            if (ld.xCoord == x && ld.yCoord == y && ld.zCoord == z) return true;
        }

        linkedDevices.add(new LinkedEnergyDevice(x, y, z));
        worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);

        return true;
    }

    public boolean handleOther(EntityPlayer player, ItemStack wrench) {
        return false;
    }

    @Override
    public EnergyStorage getStorage() {
        return storage;
    }

    // **************************************

    @Override
    public int receiveEnergy(ForgeDirection from, int maxReceive, boolean simulate) {
        return 0;
    }

    @Override
    public int extractEnergy(ForgeDirection from, int maxExtract, boolean simulate) {
        return 0;
    }

    @Override
    public int getEnergyStored(ForgeDirection from) {
        return storage.getEnergyStored();
    }

    @Override
    public int getMaxEnergyStored(ForgeDirection from) {
        return storage.getMaxEnergyStored();
    }

    @Override
    public boolean canConnectEnergy(ForgeDirection from) {
        return false;
    }

    @Override
    public void writeToNBT(NBTTagCompound compound) {
        compound.setByte("Type", (byte) powerTier);
        int index = 0;
        compound.setInteger("LinkCount", linkedDevices.size());
        for (LinkedEnergyDevice ld : linkedDevices) {
            ld.writeToNBT(compound, "LinkedDevice_" + index);
            ++index;
        }
        super.writeToNBT(compound);
        storage.writeToNBT(compound);
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        powerTier = compound.getByte("Type");
        this.updateStorage();
        int linkCount = compound.getInteger("LinkCount");
        linkedDevices = new ArrayList<LinkedEnergyDevice>();
        for (int i = 0; i < linkCount; i++) {
            linkedDevices.add(new LinkedEnergyDevice().readFromNBT(compound, "LinkedDevice_" + i));
        }
        super.readFromNBT(compound);
        storage.readFromNBT(compound);
    }

    @Override
    public Packet getDescriptionPacket() {
        NBTTagCompound tagCompound = new NBTTagCompound();
        writeToNBT(tagCompound);
        return new S35PacketUpdateTileEntity(this.xCoord, this.yCoord, this.zCoord, 1, tagCompound);
    }

    @Override
    public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt) {
        readFromNBT(pkt.func_148857_g());
    }

    @Override
    public AxisAlignedBB getRenderBoundingBox() {
        return AxisAlignedBB.getBoundingBox(xCoord, yCoord, zCoord, xCoord + 1, yCoord + 1, zCoord + 1);
    }
}
