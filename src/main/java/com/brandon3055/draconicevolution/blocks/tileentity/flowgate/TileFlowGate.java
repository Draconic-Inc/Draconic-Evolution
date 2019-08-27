package com.brandon3055.draconicevolution.blocks.tileentity.flowgate;

import codechicken.lib.data.MCDataInput;
import com.brandon3055.brandonscore.blocks.TileBCBase;
import com.brandon3055.brandonscore.lib.IChangeListener;
import com.brandon3055.brandonscore.lib.datamanager.ManagedBool;
import com.brandon3055.brandonscore.lib.datamanager.ManagedByte;
import com.brandon3055.brandonscore.lib.datamanager.ManagedInt;
import com.brandon3055.draconicevolution.blocks.machines.FlowGate;
import com.brandon3055.draconicevolution.integration.computers.ArgHelper;
import com.brandon3055.draconicevolution.integration.computers.IDEPeripheral;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import static com.brandon3055.brandonscore.lib.datamanager.DataFlags.SAVE_BOTH_SYNC_TILE;
import static com.brandon3055.brandonscore.lib.datamanager.DataFlags.SAVE_NBT_SYNC_TILE;

/**
 * Created by brandon3055 on 15/11/2016.
 */
public abstract class TileFlowGate extends TileBCBase implements ITickable, IChangeListener, IDEPeripheral {

    protected int transferThisTick = 0;

    public final ManagedInt minFlow = register(new ManagedInt("minFlow", SAVE_BOTH_SYNC_TILE ));
    public final ManagedInt maxFlow = register(new ManagedInt("maxFlow", SAVE_BOTH_SYNC_TILE));
    public final ManagedInt flowOverride = register(new ManagedInt("flowOverride", SAVE_NBT_SYNC_TILE));
    public final ManagedBool flowOverridden = register(new ManagedBool("flowOverridden", SAVE_NBT_SYNC_TILE));
    public final ManagedByte rsSignal = register(new ManagedByte("rsSignal", (byte) -1, SAVE_NBT_SYNC_TILE));

    public TileFlowGate() {
        setShouldRefreshOnBlockChange();
    }

    @Override
    public void update() {
        super.update();
        transferThisTick = 0;
    }

    //region Gate

    public String getName() {
        return "tile.draconicevolution:" + (this instanceof TileFluxGate ? "flux_gate" : "fluid_gate") + ".name";
    }

    public abstract String getUnits();

    @SideOnly(Side.CLIENT)
    public void setMin(String value) {
        sendPacketToServer(output -> output.writeString(value), 0);
    }

    @SideOnly(Side.CLIENT)
    public void setMax(String value) {
        sendPacketToServer(output -> output.writeString(value), 1);
    }

    public int getFlow() {
        if (flowOverridden.get()) {
            return flowOverride.get();
        }
        if (rsSignal.get() == -1) {
            rsSignal.set((byte) world.isBlockIndirectlyGettingPowered(pos));
        }
        return minFlow.get() + (int) (((double) rsSignal.get() / 15D) * (double) (maxFlow.get() - minFlow.get()));
    }

    //endregion


    @Override
    public void receivePacketFromClient(MCDataInput data, EntityPlayerMP client, int id) {
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

    public TileEntity getTarget() {
        return world.getTileEntity(pos.offset(getDirection()));
    }

    public EnumFacing getDirection() {
        IBlockState state = getState(getBlockType());
        return state.getValue(FlowGate.FACING);
    }

    @Override
    public void onNeighborChange(BlockPos neighbor) {
        rsSignal.set((byte) world.isBlockIndirectlyGettingPowered(pos));
    }

    //region Peripheral

    @Override
    public String[] getMethodNames() {
        return new String[]{"getFlow", "setOverrideEnabled", "getOverrideEnabled", "setFlowOverride", "setSignalHighFlow", "getSignalHighFlow", "setSignalLowFlow", "getSignalLowFlow"};
    }

    @Override
    public Object[] callMethod(String method, ArgHelper args) {
        switch (method) {
            case "getFlow":
                return new Object[]{getFlow()};
            case "setOverrideEnabled":
                flowOverridden.set(args.checkBoolean(0));
                break;
            case "getOverrideEnabled":
                return new Object[]{flowOverridden};
            case "setFlowOverride":
                flowOverride.set(args.checkInteger(0));
                break;
            case "setSignalHighFlow":
                maxFlow.set(args.checkInteger(0));
                break;
            case "getSignalHighFlow":
                return new Object[]{maxFlow.get()};
            case "setSignalLowFlow":
                minFlow.set(args.checkInteger(0));
                break;
            case "getSignalLowFlow":
                return new Object[]{minFlow.get()};
        }

        return new Object[0];
    }

    //endregion
}
