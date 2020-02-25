package com.brandon3055.draconicevolution.blocks.energynet.tileentity;

import com.brandon3055.brandonscore.lib.Vec3D;
import com.brandon3055.brandonscore.lib.datamanager.ManagedBool;
import com.brandon3055.brandonscore.lib.datamanager.ManagedEnum;
import com.brandon3055.brandonscore.utils.EnergyUtils;
import com.brandon3055.draconicevolution.blocks.energynet.EnergyCrystal;
import com.brandon3055.draconicevolution.client.render.effect.CrystalFXIO;
import com.brandon3055.draconicevolution.client.render.effect.CrystalGLFXBase;
import net.minecraft.block.BlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.List;

import static com.brandon3055.brandonscore.lib.datamanager.DataFlags.SAVE_BOTH_SYNC_TILE;
import static com.brandon3055.brandonscore.lib.datamanager.DataFlags.SAVE_NBT_SYNC_TILE;

/**
 * Created by brandon3055 on 19/11/2016.
 */
public class TileCrystalDirectIO extends TileCrystalBase   {

    public final ManagedEnum<Direction> facing = dataManager.register(new ManagedEnum<>("facing", Direction.DOWN, SAVE_NBT_SYNC_TILE));
    public final ManagedBool outputMode = dataManager.register(new ManagedBool("outputMode", SAVE_BOTH_SYNC_TILE));

    public TileCrystalDirectIO() {
    }

    //region Update Energy IO

    @Override
    public void tick() {
        super.tick();

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
//    public int receiveEnergy(Direction from, int maxReceive, boolean simulate) {
//        return from != null && !outputMode.get() && from.equals(facing.get()) ? energyStorage.receiveEnergy(maxReceive, simulate) : 0;
//    }
//
//    @Override
//    public int extractEnergy(Direction from, int maxExtract, boolean simulate) {
//        return from != null && outputMode.get() && from.equals(facing.get()) ? energyStorage.extractEnergy(maxExtract, simulate) : 0;
//    }
//
//    @Override
//    public boolean canConnectEnergy(Direction from) {
//        return from != null && from.equals(facing.get());
//    }

    //endregion

    @Override
    public boolean onBlockActivated(BlockState state, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
        if (player.isSneaking()) {
            outputMode.invert();
            return true;
        }
        return super.onBlockActivated(state, player, handIn, hit);
    }

    //region Rendering

    @Override
    public EnergyCrystal.CrystalType getCrystalType() {
        return EnergyCrystal.CrystalType.CRYSTAL_IO;
    }

    @OnlyIn(Dist.CLIENT)
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

    @OnlyIn(Dist.CLIENT)
    @Override
    public void addDisplayData(List<String> displayList) {
        super.addDisplayData(displayList);
        TextFormatting colour = outputMode.get() ? TextFormatting.GOLD : TextFormatting.DARK_AQUA;
        displayList.add(I18n.format("eNet.de.IOOutput_" + outputMode.get() + ".info", colour));
    }

    //endregion

    //region Misc


    @Override
    public void onTilePlaced(BlockItemUseContext context, BlockState state) {
        super.onTilePlaced(context, state);
        facing.set(context.getFace().getOpposite());
    }

    //endregion


//    @Override
//    public boolean hasCapability(Capability<?> capability, @Nullable Direction facing) {
//        if (facing == this.facing.get() && (capability == CapabilityEnergy.ENERGY || capability == CapabilityOP.OP)) {
//            return true;
//        }
//
//        return super.hasCapability(capability, facing);
//    }

//    @Nullable
//    @Override
//    public <T> T getCapability(Capability<T> capability, @Nullable Direction facing) {
//        if (facing == this.facing.get()) {
//            if (capability == CapabilityEnergy.ENERGY) {
//                return CapabilityEnergy.ENERGY.cast(opStorage);
//            }
//            else if (capability == CapabilityOP.OP) {
//                return CapabilityOP.OP.cast(opStorage);
//            }
//        }
//
//        return super.getCapability(capability, facing);
//    }
}
