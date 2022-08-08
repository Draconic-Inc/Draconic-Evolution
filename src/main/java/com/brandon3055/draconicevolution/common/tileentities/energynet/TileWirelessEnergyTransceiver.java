package com.brandon3055.draconicevolution.common.tileentities.energynet;

import cofh.api.energy.IEnergyReceiver;
import com.brandon3055.brandonscore.common.utills.Utills;
import com.brandon3055.draconicevolution.DraconicEvolution;
import com.brandon3055.draconicevolution.client.render.particle.ParticleEnergyField;
import com.brandon3055.draconicevolution.client.render.particle.Particles;
import com.brandon3055.draconicevolution.common.handler.BalanceConfigHandler;
import com.brandon3055.draconicevolution.common.items.tools.Wrench;
import com.brandon3055.draconicevolution.common.lib.References;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraftforge.common.util.ForgeDirection;

/**
 * Created by Brandon on 10/02/2015.
 */
public class TileWirelessEnergyTransceiver extends TileRemoteEnergyBase {

    @SideOnly(Side.CLIENT)
    private ParticleEnergyField ring;

    public List<LinkedReceiver> receiverList = new ArrayList<LinkedReceiver>();

    public TileWirelessEnergyTransceiver() {}

    public TileWirelessEnergyTransceiver(int powerTier) {
        this.powerTier = powerTier;
        this.updateStorage();
    }

    @Override
    public int getCap() {
        return powerTier == 0
                ? BalanceConfigHandler.energyWirelessTransceiverBasicStorage
                : BalanceConfigHandler.energyWirelessTransceiverAdvancedStorage;
    }

    @Override
    public int getRec() {
        return powerTier == 0
                ? BalanceConfigHandler.energyWirelessTransceiverBasicMaxReceive
                : BalanceConfigHandler.energyWirelessTransceiverAdvancedMaxReceive;
    }

    @Override
    public int getExt() {
        return powerTier == 0
                ? BalanceConfigHandler.energyWirelessTransceiverBasicMaxExtract
                : BalanceConfigHandler.energyWirelessTransceiverAdvancedMaxExtract;
    }

    @Override
    public void updateEntity() {
        super.updateEntity();

        if (worldObj.isRemote) {
            ring = DraconicEvolution.proxy.energyField(
                    worldObj, xCoord + 0.5, yCoord + 0.5, zCoord + 0.5, 2, powerTier == 1, ring, inView > 0);

            for (LinkedReceiver receiver : receiverList) {
                int particleValue = 200;
                if (receiver.particleEnergyCounter > particleValue) {
                    receiver.particleEnergyCounter -= particleValue;

                    // todo detect box size
                    DraconicEvolution.proxy.spawnParticle(
                            new Particles.TransceiverParticle(
                                    worldObj,
                                    xCoord + 0.5,
                                    yCoord + 0.3 + (worldObj.rand.nextDouble() * 0.4),
                                    zCoord + 0.5,
                                    receiver.xCoord + worldObj.rand.nextDouble(),
                                    receiver.yCoord + worldObj.rand.nextDouble(),
                                    receiver.zCoord + worldObj.rand.nextDouble()),
                            64);
                }
            }
        } else {
            for (LinkedReceiver receiver : receiverList) {
                if (!receiver.isStillValid(worldObj)
                        && worldObj.getChunkFromBlockCoords(receiver.xCoord, receiver.zCoord).isChunkLoaded) {
                    receiverList.remove(receiver);
                    worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
                    break;
                }

                int sent = receiver.receiveEnergy(
                        worldObj,
                        storage.extractEnergy(
                                receiver.receiveEnergy(
                                        worldObj,
                                        powerTier == 0
                                                ? BalanceConfigHandler.energyWirelessTransceiverBasicMaxSend
                                                : BalanceConfigHandler.energyWirelessTransceiverAdvancedMaxSend,
                                        true),
                                false),
                        false);

                sent = (sent / 20);

                if (sent > Short.MAX_VALUE) sent = Short.MAX_VALUE;

                if (sent > 0)
                    sendObjectToClient(References.SHORT_ID, 10 + receiverList.indexOf(receiver), (short) sent);
            }
        }
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void receiveObjectFromServer(int index, Object object) {
        super.receiveObjectFromServer(index, object);
        if (index >= 10) {

            if (receiverList.size() > index - 10 && receiverList.get(index - 10).particleEnergyCounter < 10000)
                receiverList.get(index - 10).particleEnergyCounter += (Short) object;
        }
    }

    public void linkDevice(int x, int y, int z, int side, EntityPlayer player, String mode) {
        TileEntity target = worldObj.getTileEntity(x, y, z);
        if (!(target instanceof IEnergyReceiver)) {
            player.addChatComponentMessage(new ChatComponentText("Unknown Error occurred [Invalid Tile]"));
            return;
        }

        IEnergyReceiver receiver = (IEnergyReceiver) target;

        if (!receiver.canConnectEnergy(ForgeDirection.getOrientation(side))) {
            player.addChatComponentMessage(new ChatComponentTranslation("msg.de.wrongSide.txt"));
            return;
        }

        if (receiverList.size() >= getmaxWirelessConnections()) {
            player.addChatComponentMessage(new ChatComponentTranslation("msg.de.maxConnections.txt"));
            return;
        }

        if (Utills.getDistanceAtoB(xCoord, yCoord, zCoord, x, y, z) > (powerTier == 0 ? 15 : 30)) {
            player.addChatComponentMessage(new ChatComponentTranslation("msg.de.outOfRange.txt"));
            return;
        }

        if (mode.equals(Wrench.BIND_MODE)) {
            LinkedReceiver r = new LinkedReceiver(x, y, z, side);

            for (LinkedReceiver r2 : receiverList) {
                if (r.xCoord == r2.xCoord
                        && r.yCoord == r2.yCoord
                        && r.zCoord == r2.zCoord
                        && r.connectionSide == r2.connectionSide) {
                    player.addChatComponentMessage(new ChatComponentTranslation("msg.de.linked.txt"));
                    return;
                }
            }
            receiverList.add(r);
            player.addChatComponentMessage(new ChatComponentTranslation("msg.de.linked.txt"));
            worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
        } else if (mode.equals(Wrench.UNBIND_MODE)) {
            for (LinkedReceiver r2 : receiverList) {
                if (x == r2.xCoord && y == r2.yCoord && z == r2.zCoord && side == r2.connectionSide) {
                    player.addChatComponentMessage(new ChatComponentTranslation("msg.de.unLinked.txt"));
                    receiverList.remove(r2);
                    worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
                    return;
                }
            }
        }
    }

    @Override
    public void writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        compound.setInteger("Receiver_Count", receiverList.size());
        for (int i = 0; i < receiverList.size(); i++) receiverList.get(i).writeToNBT(compound, String.valueOf(i));
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        int count = compound.getInteger("Receiver_Count");
        receiverList.clear();
        for (int i = 0; i < count; i++) {
            LinkedReceiver r = new LinkedReceiver();
            r.readFromNBT(compound, String.valueOf(i));
            receiverList.add(r);
        }
    }

    //	@Override
    //	public double getCapacity() {
    //		return ((double) getEnergyStored(ForgeDirection.UNKNOWN) / (double) getMaxEnergyStored(ForgeDirection.UNKNOWN))
    // * 100D;
    //	}

    /**
     * Calculates the energy flow based on the local buffer
     * and the remote buffer
     * return double between 0 to 100
     */
    public double getFlow(double localCap, double remoteCap) {
        return Math.max(0, Math.min(100, (localCap - remoteCap) * 100D /*Flow Multiplier*/));
    }

    @Override
    public double getBeamX() {
        return xCoord + 0.5D;
    }

    @Override
    public double getBeamY() {
        return yCoord + 0.5D;
    }

    @Override
    public double getBeamZ() {
        return zCoord + 0.5D;
    }

    @Override
    public int getMaxConnections() {
        return powerTier == 0 ? 3 : 6;
    }

    public int getmaxWirelessConnections() {
        return powerTier == 0 ? 8 : 16;
    }
}
