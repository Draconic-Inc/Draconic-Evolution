package com.brandon3055.draconicevolution.blocks.reactor.tileentity;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;

/**
 * Created by brandon3055 on 18/01/2017.
 */
public class TileReactorEnergyInjector extends TileReactorComponent {

    //region Initialization

    @Override
    public boolean checkForMaster() {
        for (int i = 1; i < 10; i++) {
            BlockPos checkPos = pos.offset(coreDir.value, i);
            if (!worldObj.isAirBlock(checkPos)) {
                TileEntity tile = worldObj.getTileEntity(checkPos);
                if (tile instanceof TileReactorCore) {
                    coreOffset.vec = getCoreOffset(checkPos);
                    isValid.value = true;
                    return true;
                }
                else {
                    isValid.value = false;
                    return false;
                }
            }
        }
        isValid.value = false;
        return false;
    }

    //endregion

}
