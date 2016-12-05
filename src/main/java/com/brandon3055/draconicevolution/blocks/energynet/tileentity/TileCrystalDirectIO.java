package com.brandon3055.draconicevolution.blocks.energynet.tileentity;

import cofh.api.energy.IEnergyTransport;
import com.brandon3055.brandonscore.network.wrappers.SyncableEnum;
import com.brandon3055.draconicevolution.blocks.energynet.EnergyCrystal;
import com.brandon3055.draconicevolution.client.render.effect.CrystalFXRing;
import com.brandon3055.draconicevolution.client.render.effect.CrystalGLFXBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Created by brandon3055 on 19/11/2016.
 */
public class TileCrystalDirectIO extends TileCrystalBase implements IEnergyTransport {

    public final SyncableEnum<EnumFacing> facing = new SyncableEnum<>(EnumFacing.DOWN, true, false);
    public final SyncableEnum<IEnergyTransport.InterfaceType> transportState = new SyncableEnum<>(IEnergyTransport.InterfaceType.BALANCE, true, false);

    public TileCrystalDirectIO() {
        super();
        registerSyncableObject(facing, true);
        registerSyncableObject(transportState, true, true);
    }

    //region Update Energy IO

    @Override
    public void update() {
        super.update();

    }

    //endregion

    //region EnergyIO

    @Override
    public int receiveEnergy(EnumFacing from, int maxReceive, boolean simulate) {
        return from != null && from.equals(facing.value.getOpposite()) ? energyStorage.receiveEnergy(maxReceive, simulate) : 0;
    }

    @Override
    public int extractEnergy(EnumFacing from, int maxExtract, boolean simulate) {
        return from != null && from.equals(facing.value.getOpposite()) ? energyStorage.extractEnergy(maxExtract, simulate) : 0;
    }

    @Override
    public InterfaceType getTransportState(EnumFacing from) {
        return transportState.value;
    }

    @Override
    public boolean setTransportState(InterfaceType state, EnumFacing from) {
        transportState.value = state;
        return true;
    }

    @Override
    public boolean canConnectEnergy(EnumFacing from) {
        return from != null && from.equals(facing.value);
    }

    //endregion

    //region Rendering

    @Override
    public EnergyCrystal.CrystalType getType() {
        return EnergyCrystal.CrystalType.CRYSTAL_IO;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public CrystalGLFXBase createStaticFX() {
        return new CrystalFXRing(worldObj, this);
    }

    //endregion

    //region Misc

    @Override
    public void onTilePlaced(World world, BlockPos pos, EnumFacing placedAgainst, float hitX, float hitY, float hitZ, EntityPlayer placer, ItemStack stack) {
        super.onTilePlaced(world, pos, placedAgainst, hitX, hitY, hitZ, placer, stack);
        facing.value = placedAgainst.getOpposite();
    }

    //endregion
}
