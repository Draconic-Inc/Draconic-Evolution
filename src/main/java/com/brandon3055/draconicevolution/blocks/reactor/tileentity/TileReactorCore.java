package com.brandon3055.draconicevolution.blocks.reactor.tileentity;

import com.brandon3055.brandonscore.blocks.TileBCBase;
import com.brandon3055.brandonscore.lib.Vec3I;
import com.brandon3055.brandonscore.network.wrappers.SyncableVec3I;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by brandon3055 on 6/11/2016.
 */
public class TileReactorCore extends TileBCBase implements ITickable{

    private final SyncableVec3I[] stabilizerPositions = new SyncableVec3I[4];

    public TileReactorCore() {
        for (int i = 0; i < 4; i++) {
            registerSyncableObject(stabilizerPositions[i] = new SyncableVec3I(new Vec3I(0, 0, 0), true, false));
        }
    }

    //region Rendering

    @Override
    public boolean shouldRenderInPass(int pass) {
        return true;
    }

    //endregion

    //region Update Logic

    @Override
    public void update() {

    }

    //endregion

    //region ################# Multi-block #################

    //region Initialization


    //endregion

    //region Structure Validation

    public void validateStructure() {}

    private boolean checkStabilizers() {
        return true;
    }

    //endregion

    //region Getters & Setters

    public List<BlockPos> getStabilizerPositions() {
        ArrayList<BlockPos> list = new ArrayList<>();

        for (int i = 0; i < 4; i++) {
            if (stabilizerValid(i)) {
                list.add(getOffsetPos(stabilizerPositions[i].vec));
            }
        }

        return list;
    }

    public boolean addStabilizerPoz(BlockPos stabPos) {
        for (int i = 0; i < 4; i++) {
            if (!stabilizerValid(i)) {
                stabilizerPositions[i].vec = getOffsetVec(stabPos);
                return true;
            }
        }
        return false;
    }

    public boolean stabilizerValid(int index) {
        if (index < 0 || index >= 4) {
            return false;
        }
        else {
            return stabilizerPositions[index].vec.sum() != 0;
        }
    }

    private BlockPos getOffsetPos(Vec3I vec){
        return pos.subtract(vec.getPos());
    }

    private Vec3I getOffsetVec(BlockPos corePos) {
        return new Vec3I(pos.subtract(corePos));
    }

    //endregion

    //endregion ############################################
}
