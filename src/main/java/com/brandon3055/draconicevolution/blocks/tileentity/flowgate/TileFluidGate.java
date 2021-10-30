package com.brandon3055.draconicevolution.blocks.tileentity.flowgate;

import codechicken.lib.util.SneakyUtils;
import dan200.computercraft.shared.Capabilities;

import com.brandon3055.brandonscore.inventory.ContainerBCTile;
import com.brandon3055.draconicevolution.init.DEContent;
import com.brandon3055.draconicevolution.integration.computers.PeripheralFlowGate;
import com.brandon3055.draconicevolution.integration.computers.PeripheralFluidGate;
import com.brandon3055.draconicevolution.inventory.GuiLayoutFactories;
import com.brandon3055.draconicevolution.lib.WTFException;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fml.network.NetworkHooks;

import javax.annotation.Nonnull;

import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;

/**
 * Created by brandon3055 on 15/11/2016.
 */
public class TileFluidGate extends TileFlowGate implements IFluidHandler {

    public TileFluidGate() {
        super(DEContent.tile_fluid_gate);
        capManager.set(Capabilities.CAPABILITY_PERIPHERAL, new PeripheralFluidGate(this));
    }

    //region Gate

    //TODO validate this logic

    @Override
    public String getUnits() {
        return "MB/t";
    }

    //endregion

    //region IFluidHandler

    @Override
    public int getTanks() {
        TileEntity tile = getTarget();
        if (tile != null) {
            LazyOptional<IFluidHandler> opHandler = tile.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, getDirection().getOpposite());

            if (opHandler.isPresent()) {
                opHandler.orElseThrow(WTFException::new).getTanks();
            }
        }
        return 0;
    }

    @Nonnull
    @Override
    public FluidStack getFluidInTank(int tank) {
        return FluidStack.EMPTY;
    }

    @Override
    public int getTankCapacity(int tank) {
        TileEntity tile = getTarget();
        if (tile != null) {
            LazyOptional<IFluidHandler> opHandler = tile.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, getDirection().getOpposite());

            if (opHandler.isPresent()) {
                opHandler.orElseThrow(WTFException::new).getTankCapacity(tank);
            }
        }
        return 0;
    }

    @Override
    public boolean isFluidValid(int tank, @Nonnull FluidStack stack) {
        return false;
    }

    @Override
    public int fill(FluidStack resource, FluidAction action) {
        TileEntity tile = getTarget();
        if (tile != null) {
            LazyOptional<IFluidHandler> opHandler = tile.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, getDirection().getOpposite());

            if (opHandler.isPresent()) {
                IFluidHandler handler = opHandler.orElseThrow(WTFException::new);

                int transfer = (int) Math.min(getFlow(), handler.fill(resource, action));

                if (transfer < resource.getAmount()) {
                    FluidStack newStack = resource.copy();
                    newStack.setAmount(transfer);
                    resource.shrink(transfer);
                    return handler.fill(newStack, action);
                }
                return handler.fill(resource, action);
            }
        }
        return 0;
    }

    @Nonnull
    @Override
    public FluidStack drain(FluidStack resource, FluidAction action) {
        return FluidStack.EMPTY;
    }

    @Nonnull
    @Override
    public FluidStack drain(int maxDrain, FluidAction action) {
        return FluidStack.EMPTY;
    }

    //endregion

//    @Override
//    public boolean hasCapability(Capability<?> capability, Direction facing) {
//        return (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY && facing == getDirection().getOpposite()) || super.hasCapability(capability, facing);
//    }
//
//    @Override
//    public <T> T getCapability(Capability<T> capability, Direction facing) {
//        if (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY && facing == getDirection().getOpposite()) {
//            return CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY.cast(this);
//        }
//
//        return super.getCapability(capability, facing);
//    }

    @Override
    public Container createMenu(int id, PlayerInventory inventory, PlayerEntity player) {
        return new ContainerBCTile<TileFlowGate>(DEContent.container_flow_gate, id, player.inventory, this, SneakyUtils.unsafeCast(GuiLayoutFactories.PLAYER_ONLY_LAYOUT));
    }

    @Override
    public ITextComponent getName() {
        return super.getName();
    }

    @Override
    public boolean onBlockActivated(BlockState state, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
        if (player instanceof ServerPlayerEntity) {
            NetworkHooks.openGui((ServerPlayerEntity) player, this, worldPosition);
        }
        return true;
    }
}
