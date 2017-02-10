package com.brandon3055.draconicevolution.blocks.reactor.tileentity;

/**
 * Created by brandon3055 on 18/01/2017.
 */
public class TileReactorStabilizer extends TileReactorComponent {

    //region Initialization

//    @Override
//    public boolean checkForMaster() {
//        for (int i = 1; i < 10; i++) {
//            BlockPos checkPos = pos.offset(facing.value, i);
//            if (!worldObj.isAirBlock(checkPos)) {
//                TileEntity tile = worldObj.getTileEntity(checkPos);
//                if (tile instanceof TileReactorCore && ((TileReactorCore) tile).getStabilizerPositions().size() < 4) { //todo add check reactor side to make sure this aligns with other stabilizers
//                    TileReactorCore core = (TileReactorCore) tile;
//                    core.addStabilizerPoz(pos);
//                    coreOffset.vec = getCoreOffset(checkPos);
//                    isBound.value = true;
//                    ((TileReactorCore) tile).validateStructure();
//                    return true;
//                }
//                else {
//                    isBound.value = false;
//                    return false;
//                }
//            }
//        }
//        isBound.value = false;
//        return false;
//    }

    //endregion
}
