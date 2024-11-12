package com.brandon3055.draconicevolution.blocks.tileentity.flowgate;

import codechicken.lib.data.MCDataInput;
import com.brandon3055.brandonscore.blocks.TileBCore;
import com.brandon3055.brandonscore.lib.IChangeListener;
import com.brandon3055.brandonscore.lib.IInteractTile;
import com.brandon3055.brandonscore.lib.datamanager.DataFlags;
import com.brandon3055.brandonscore.lib.datamanager.ManagedBool;
import com.brandon3055.brandonscore.lib.datamanager.ManagedByte;
import com.brandon3055.brandonscore.lib.datamanager.ManagedLong;
import com.brandon3055.draconicevolution.blocks.machines.FlowGate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

/**
 * Created by brandon3055 on 15/11/2016.
 */
public abstract class TileFlowGate extends TileBCore implements IChangeListener, MenuProvider, IInteractTile {

    protected long transferThisTick = 0;

    public final ManagedLong minFlow = register(new ManagedLong("min_flow", DataFlags.SAVE_BOTH_SYNC_TILE));
    public final ManagedLong maxFlow = register(new ManagedLong("max_flow", DataFlags.SAVE_BOTH_SYNC_TILE));
    public final ManagedLong flowOverride = register(new ManagedLong("flow_override", DataFlags.SAVE_NBT_SYNC_TILE));
    public final ManagedBool flowOverridden = register(new ManagedBool("flow_overridden", DataFlags.SAVE_NBT_SYNC_TILE));
    public final ManagedByte rsSignal = register(new ManagedByte("rs_signal", (byte) -1, DataFlags.SAVE_NBT_SYNC_TILE));

    private Direction rotationCache = null;

    public TileFlowGate(BlockEntityType<?> tileEntityTypeIn, BlockPos pos, BlockState state) {
        super(tileEntityTypeIn, pos, state);
    }

    @Override
    public void tick() {
        super.tick();
        transferThisTick = 0;
    }

    //region Gate

    public abstract String getUnits();

    @OnlyIn(Dist.CLIENT)
    public void setMin(String value) {
        sendPacketToServer(output -> output.writeString(value), 0);
    }

    @OnlyIn(Dist.CLIENT)
    public void setMax(String value) {
        sendPacketToServer(output -> output.writeString(value), 1);
    }

    public long getFlow() {
        if (flowOverridden.get()) {
            return flowOverride.get();
        }
        if (rsSignal.get() == -1) {
            rsSignal.set((byte) level.getBestNeighborSignal(worldPosition));
        }
        return minFlow.get() + (long) (((double) rsSignal.get() / 15D) * (double) (maxFlow.get() - minFlow.get()));
    }

    //endregion


    @Override
    public void receivePacketFromClient(MCDataInput data, ServerPlayer client, int id) {
        if (flowOverridden.get()) {
            return;
        }

        try {
            String value = data.readString();
            long l = Long.parseLong(value);
            if (l < 0) {
                l = 0;
            }
            else if (l > Integer.MAX_VALUE) {
                l = Integer.MAX_VALUE;
            }

            if (id == 0) {
                minFlow.set((int) l);
            }
            else if (id == 1) {
                maxFlow.set((int) l);
            }
        }
        catch (NumberFormatException ignored) {
        }
    }

    public BlockEntity getTarget() {
        return level.getBlockEntity(worldPosition.relative(getDirection()));
    }

    public BlockEntity getSource() {
        return level.getBlockEntity(worldPosition.relative(getDirection().getOpposite()));
    }

    @Override
    public void setBlockState(BlockState p_155251_) {
        super.setBlockState(p_155251_);
        rotationCache = null;
    }

    public Direction getDirection() {
        if (rotationCache == null) {
            rotationCache = getBlockState().getValue(FlowGate.FACING);
        }
        return rotationCache;
    }

    @Override
    public void onNeighborChange(BlockPos neighbor) {
        rsSignal.set((byte) level.getBestNeighborSignal(worldPosition));
    }
}
