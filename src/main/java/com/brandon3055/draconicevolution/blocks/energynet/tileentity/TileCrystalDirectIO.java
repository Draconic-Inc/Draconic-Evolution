package com.brandon3055.draconicevolution.blocks.energynet.tileentity;

import com.brandon3055.brandonscore.capability.CapabilityOP;
import com.brandon3055.brandonscore.lib.Vec3D;
import com.brandon3055.brandonscore.lib.datamanager.ManagedBool;
import com.brandon3055.brandonscore.lib.datamanager.ManagedEnum;
import com.brandon3055.brandonscore.utils.EnergyUtils;
import com.brandon3055.draconicevolution.blocks.energynet.EnergyCrystal;
import com.brandon3055.draconicevolution.client.render.effect.CrystalFXIO;
import com.brandon3055.draconicevolution.client.render.effect.CrystalGLFXBase;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.List;

import static com.brandon3055.brandonscore.lib.datamanager.DataFlags.SAVE_BOTH_SYNC_TILE;
import static com.brandon3055.brandonscore.lib.datamanager.DataFlags.SAVE_NBT_SYNC_TILE;

/**
 * Created by brandon3055 on 19/11/2016.
 */
public class TileCrystalDirectIO extends TileCrystalBase   {

    public final ManagedEnum<EnumFacing> facing = dataManager.register(new ManagedEnum<>("facing", EnumFacing.DOWN, SAVE_NBT_SYNC_TILE));
    public final ManagedBool outputMode = dataManager.register(new ManagedBool("outputMode", SAVE_BOTH_SYNC_TILE));

    public TileCrystalDirectIO() {
    }

    //region Update Energy IO

    @Override
    public void update() {
        super.update();

        if (world.isRemote) {
            return;
        }

        TileEntity tile = world.getTileEntity(pos.offset(facing.get()));

        if (outputMode.get() && tile != null) {
            opStorage.extractOP(EnergyUtils.insertEnergy(tile, opStorage.extractOP(opStorage.getMaxExtract(), true), facing.get().getOpposite(), false), false);
        }
    }

    //endregion

    //region EnergyIO

//    @Override
//    public int receiveEnergy(EnumFacing from, int maxReceive, boolean simulate) {
//        return from != null && !outputMode.get() && from.equals(facing.get()) ? energyStorage.receiveEnergy(maxReceive, simulate) : 0;
//    }
//
//    @Override
//    public int extractEnergy(EnumFacing from, int maxExtract, boolean simulate) {
//        return from != null && outputMode.get() && from.equals(facing.get()) ? energyStorage.extractEnergy(maxExtract, simulate) : 0;
//    }
//
//    @Override
//    public boolean canConnectEnergy(EnumFacing from) {
//        return from != null && from.equals(facing.get());
//    }

    //endregion

    @Override
    public boolean onBlockActivated(IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
        if (player.isSneaking()) {
            outputMode.invert();
            return true;
        }
        return super.onBlockActivated(state, player, hand, side, hitX, hitY, hitZ);
    }

    //region Rendering

    @Override
    public EnergyCrystal.CrystalType getType() {
        return EnergyCrystal.CrystalType.CRYSTAL_IO;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public CrystalGLFXBase createStaticFX() {
        return new CrystalFXIO(world, this);
    }

    @Override
    public Vec3D getBeamLinkPos(BlockPos linkTo) {
        return Vec3D.getCenter(pos);
    }

    @Override
    public boolean renderBeamTermination() {
        return false;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void addDisplayData(List<String> displayList) {
        super.addDisplayData(displayList);
        TextFormatting colour = outputMode.get() ? TextFormatting.GOLD : TextFormatting.DARK_AQUA;
        displayList.add(I18n.format("eNet.de.IOOutput_" + outputMode.get() + ".info", colour));
    }

    //endregion

    //region Misc

    @Override
    public void onTilePlaced(World world, BlockPos pos, EnumFacing placedAgainst, float hitX, float hitY, float hitZ, EntityPlayer placer, ItemStack stack) {
        super.onTilePlaced(world, pos, placedAgainst, hitX, hitY, hitZ, placer, stack);
        facing.set(placedAgainst.getOpposite());
    }

    //endregion


    @Override
    public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing) {
        if (facing == this.facing.get() && (capability == CapabilityEnergy.ENERGY || capability == CapabilityOP.OP)) {
            return true;
        }

        return super.hasCapability(capability, facing);
    }

    @Nullable
    @Override
    public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing) {
        if (facing == this.facing.get()) {
            if (capability == CapabilityEnergy.ENERGY) {
                return CapabilityEnergy.ENERGY.cast(opStorage);
            }
            else if (capability == CapabilityOP.OP) {
                return CapabilityOP.OP.cast(opStorage);
            }
        }

        return super.getCapability(capability, facing);
    }
}
