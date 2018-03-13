package com.brandon3055.draconicevolution.blocks.reactor.tileentity;

import cofh.redstoneflux.api.IEnergyProvider;
import com.brandon3055.brandonscore.lib.EnergyHelper;
import com.brandon3055.draconicevolution.integration.funkylocomotion.IMovableStructure;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;

/**
 * Created by brandon3055 on 18/01/2017.
 */
public class TileReactorStabilizer extends TileReactorComponent implements IEnergyProvider, IMovableStructure {

    @Override
    public void update() {
        super.update();

        if (world.isRemote) {
            return;
        }

        TileReactorCore tile = getCachedCore();

        if (tile != null && tile.reactorState.value == TileReactorCore.ReactorState.RUNNING) {
            TileEntity output = world.getTileEntity(pos.offset(facing.value.getOpposite()));
            if (output != null && EnergyHelper.canReceiveEnergy(output)) {
                int sent = EnergyHelper.insertEnergy(output, tile.saturation.value, facing.value, false);
                tile.saturation.value -= sent;
            }
        }

//        if (!world.isRemote && master instanceof TileReactorCore) {
//            rs = ((TileReactorCore) master).getComparatorOutput(redstoneMode);
//            if (rs != rsCach) {
//                rsCach = rs;
//                Utills.updateNeabourBlocks(world, xCoord, yCoord, zCoord);
//            }
//        }

    }

    @Override
    public int extractEnergy(EnumFacing from, int maxExtract, boolean simulate) {
        return 0;
    }
}
