package com.brandon3055.draconicevolution.common.tileentities.energynet;

import cofh.api.energy.IEnergyProvider;
import cofh.api.energy.IEnergyReceiver;
import com.brandon3055.draconicevolution.DraconicEvolution;
import com.brandon3055.draconicevolution.client.render.particle.ParticleEnergyField;
import com.brandon3055.draconicevolution.common.handler.BalanceConfigHandler;
import com.brandon3055.draconicevolution.common.items.tools.Wrench;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;

/**
 * Created by Brandon on 16/02/2015.
 */
public class TileEnergyTransceiver extends TileRemoteEnergyBase {

    public int facing = 0; // 0=up, 1=down, 3=north, 2=south, 4=east, 5=west
    private boolean input = false;
    public boolean transferBoost = false;

    @SideOnly(Side.CLIENT)
    private ParticleEnergyField particle;

    public TileEnergyTransceiver() {}

    public TileEnergyTransceiver(int powerTier) {
        this.powerTier = powerTier;
        this.updateStorage();
    }

    @Override
    public void updateEntity() {
        super.updateEntity();

        if (!worldObj.isRemote) {
            ForgeDirection direction = ForgeDirection.getOrientation(facing).getOpposite();
            TileEntity adjacentTile = worldObj.getTileEntity(
                    xCoord + direction.offsetX, yCoord + direction.offsetY, zCoord + direction.offsetZ);
            if (!input && adjacentTile instanceof IEnergyReceiver) {
                IEnergyReceiver handler = (IEnergyReceiver) adjacentTile;
                storage.extractEnergy(
                        handler.receiveEnergy(
                                direction.getOpposite(), storage.extractEnergy(storage.getMaxExtract(), true), false),
                        false);
                if (transferBoost) {
                    for (int i = 0; i < 4; i++)
                        storage.extractEnergy(
                                handler.receiveEnergy(
                                        direction.getOpposite(),
                                        storage.extractEnergy(storage.getMaxExtract(), true),
                                        false),
                                false);
                }
            } else if (input && adjacentTile instanceof IEnergyProvider) {
                IEnergyProvider handler = (IEnergyProvider) adjacentTile;
                storage.receiveEnergy(
                        handler.extractEnergy(
                                direction.getOpposite(), storage.receiveEnergy(storage.getMaxExtract(), true), false),
                        false);
                if (transferBoost) {
                    for (int i = 0; i < 4; i++)
                        storage.receiveEnergy(
                                handler.extractEnergy(
                                        direction.getOpposite(),
                                        storage.receiveEnergy(storage.getMaxExtract(), true),
                                        false),
                                false);
                }
            }
        }

        if (worldObj.isRemote)
            particle = DraconicEvolution.proxy.energyField(
                    worldObj, getBeamX(), getBeamY(), getBeamZ(), 1, powerTier == 1, particle, inView > 0);
    }

    @Override
    public boolean handleOther(EntityPlayer player, ItemStack wrench) {
        String mode = Wrench.getMode(wrench);

        if (mode.equals(Wrench.MODE_SWITCH)) {
            if (powerTier == 0) {
                input = !input;
                worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
            } else {
                if (!transferBoost) transferBoost = true;
                else {
                    input = !input;
                    transferBoost = false;
                }
            }
        }

        return true;
    }

    public boolean getInput() {
        return input;
    }

    public void setInput(boolean input) {
        this.input = input;
    }

    @Override
    public double getBeamX() {
        return xCoord + 0.5;
    }

    @Override
    public double getBeamY() {
        return yCoord + 0.5;
    }

    @Override
    public double getBeamZ() {
        return zCoord + 0.5;
    }

    @Override
    public int getCap() {
        return powerTier == 0
                ? BalanceConfigHandler.energyTransceiverBasicStorage
                : BalanceConfigHandler.energyTransceiverAdvancedStorage;
    }

    @Override
    public int getRec() {
        return powerTier == 0
                ? BalanceConfigHandler.energyTransceiverBasicMaxReceive
                : BalanceConfigHandler.energyTransceiverAdvancedMaxReceive;
    }

    @Override
    public int getExt() {
        return powerTier == 0
                ? BalanceConfigHandler.energyTransceiverBasicMaxExtract
                : BalanceConfigHandler.energyTransceiverAdvancedMaxExtract;
    }

    @Override
    public void writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        compound.setInteger("Facing", facing);
        compound.setBoolean("Input", input);
        compound.setBoolean("TransferBoost", transferBoost);
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        facing = compound.getInteger("Facing");
        input = compound.getBoolean("Input");
        transferBoost = compound.getBoolean("TransferBoost");
    }

    @Override
    public boolean canConnectEnergy(ForgeDirection from) {
        return from == ForgeDirection.getOrientation(facing).getOpposite();
    }

    @Override
    public int receiveEnergy(ForgeDirection from, int maxReceive, boolean simulate) {
        return from == ForgeDirection.getOrientation(facing).getOpposite() && input
                ? storage.receiveEnergy(maxReceive, simulate)
                : 0;
    }

    @Override
    public int extractEnergy(ForgeDirection from, int maxExtract, boolean simulate) {
        return from == ForgeDirection.getOrientation(facing).getOpposite() && !input
                ? storage.extractEnergy(maxExtract, simulate)
                : 0;
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
    public int getMaxConnections() {
        return powerTier == 0 ? 2 : 4;
    }
}
