package com.brandon3055.draconicevolution.blocks.tileentity.flowgate;

import com.brandon3055.brandonscore.api.IDataRetainerTile;
import com.brandon3055.brandonscore.blocks.TileBCBase;
import com.brandon3055.brandonscore.lib.IChangeListener;
import com.brandon3055.brandonscore.network.PacketTileMessage;
import com.brandon3055.brandonscore.network.wrappers.SyncableBool;
import com.brandon3055.brandonscore.network.wrappers.SyncableByte;
import com.brandon3055.brandonscore.network.wrappers.SyncableInt;
import com.brandon3055.brandonscore.network.wrappers.SyncableObject;
import com.brandon3055.brandonscore.utils.Utils;
import com.brandon3055.draconicevolution.blocks.machines.FlowGate;
import com.brandon3055.draconicevolution.integration.computers.IDEPeripheral;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Created by brandon3055 on 15/11/2016.
 */
public abstract class TileFlowGate extends TileBCBase implements IDataRetainerTile, ITickable, IChangeListener, IDEPeripheral {

    protected int transferThisTick = 0;

    public final SyncableInt minFlow = new SyncableInt(0, true, false);
    public final SyncableInt maxFlow = new SyncableInt(0, true, false);
    public final SyncableInt flowOverride = new SyncableInt(0, true, false);
    public final SyncableBool flowOverridden = new SyncableBool(false, true, false);
    public final SyncableByte rsSignal = new SyncableByte((byte) -1, true, false);

    public TileFlowGate() {
        registerSyncableObject(minFlow, false);
        registerSyncableObject(maxFlow, false);
        registerSyncableObject(flowOverridden, true);
        registerSyncableObject(rsSignal, true);
        registerSyncableObject(flowOverride, true);
        setShouldRefreshOnBlockChange();
    }

    @Override
    public void update() {
        detectAndSendChanges();
        transferThisTick = 0;
    }

    //region Gate

    public String getName() {
        return "tile.draconicevolution:" + (this instanceof TileFluxGate ? "flux_gate" : "fluid_gate") + ".name";
    }

    public abstract String getUnits();

    @SideOnly(Side.CLIENT)
    public void setMin(String value) {
        sendPacketToServer(new PacketTileMessage(this, (byte) 0, value, false));
    }

    @SideOnly(Side.CLIENT)
    public void setMax(String value) {
        sendPacketToServer(new PacketTileMessage(this, (byte) 1, value, false));
    }

    public int getFlow() {
        if (flowOverridden.value) {
            return flowOverride.value;
        }
        if (rsSignal.value == -1) {
            rsSignal.value = (byte) worldObj.isBlockIndirectlyGettingPowered(pos);
        }
        return minFlow.value + (int) (((double) rsSignal.value / 15D) * (double) (maxFlow.value - minFlow.value));
    }

    //endregion

    @Override
    public void receivePacketFromClient(PacketTileMessage packet, EntityPlayerMP client) {
        PlayerInteractEvent.RightClickBlock event = new PlayerInteractEvent.RightClickBlock(client, EnumHand.MAIN_HAND, client.getHeldItemMainhand(), pos, EnumFacing.UP, Vec3d.ZERO);
        MinecraftForge.EVENT_BUS.post(event);
        if (flowOverridden.value || event.isCanceled() || packet.stringValue == null || packet.stringValue.equals("")) {
            return;
        }

        try {
            long l = Long.parseLong(packet.stringValue);
            if (l < 0) {
                l = 0;
            }
            else if (l > Integer.MAX_VALUE) {
                l = Integer.MAX_VALUE;
            }

            if (packet.getIndex() == 0) {
                minFlow.value = (int) l;
            }
            else if (packet.getIndex() == 1) {
                maxFlow.value = (int) l;
            }
        }
        catch (Exception ignored) {
        }
    }

    public TileEntity getTarget() {
        return worldObj.getTileEntity(pos.offset(getDirection()));
    }

    public EnumFacing getDirection() {
        IBlockState state = getState(getBlockType());
        return state.getValue(FlowGate.FACING);
    }

    @Override
    public void onNeighborChange() {
        rsSignal.value = (byte) worldObj.isBlockIndirectlyGettingPowered(pos);
    }

    //region Save

    @Override
    public void writeRetainedData(NBTTagCompound dataCompound) {
        for (SyncableObject syncableObject : syncableObjectMap.values()) {
            if (syncableObject != flowOverridden && syncableObject != rsSignal && syncableObject != flowOverride) {
                syncableObject.toNBT(dataCompound);
            }
        }
    }

    @Override
    public void readRetainedData(NBTTagCompound dataCompound) {
        for (SyncableObject syncableObject : syncableObjectMap.values()) {
            if (syncableObject != flowOverridden && syncableObject != rsSignal) {
                syncableObject.fromNBT(dataCompound);
            }
        }
    }

    //endregion

    //region Peripheral

    @Override
    public String[] getMethodNames() {
        return new String[]{"getFlow", "setOverrideEnabled", "getOverrideEnabled", "setFlowOverride", "setSignalHighFlow", "getSignalHighFlow", "setSignalLowFlow", "getSignalLowFlow"};
    }

    @Override
    public Object[] callMethod(String method, Object... args) {
        if (method.equals("getFlow")) {
            return new Object[]{getFlow()};
        }
        else if (method.equals("setOverrideEnabled")) {
            if (args.length == 0 || !(args[0] instanceof Boolean)) {
                throw new IllegalArgumentException("Expected Boolean got " + (args.length == 0 ? "nil" : args[0].getClass().getSimpleName()));
            }
            flowOverridden.value = (Boolean) args[0];
        }
        else if (method.equals("getOverrideEnabled")) {
            return new Object[]{flowOverridden};
        }
        else if (method.equals("setFlowOverride")) {
            if (args.length == 0 || !(args[0] instanceof Number)) {
                throw new IllegalArgumentException("Expected Number got " + (args.length == 0 ? "nil" : args[0].getClass().getSimpleName()));
            }
            flowOverride.value = Utils.toInt((Double) args[0]);
        }
        else if (method.equals("setSignalHighFlow")) {
            if (args.length == 0 || !(args[0] instanceof Number)) {
                throw new IllegalArgumentException("Expected Number got " + (args.length == 0 ? "nil" : args[0].getClass().getSimpleName()));
            }
            maxFlow.value = Utils.toInt((Double) args[0]);
        }
        else if (method.equals("getSignalHighFlow")) {
            return new Object[]{maxFlow.value};
        }
        else if (method.equals("setSignalLowFlow")) {
            if (args.length == 0 || !(args[0] instanceof Number)) {
                throw new IllegalArgumentException("Expected Number got " + (args.length == 0 ? "nil" : args[0].getClass().getSimpleName()));
            }
            minFlow.value = Utils.toInt((Double) args[0]);
        }
        else if (method.equals("getSignalLowFlow")) {
            return new Object[]{minFlow.value};
        }

        return new Object[0];
    }

    //endregion
}
