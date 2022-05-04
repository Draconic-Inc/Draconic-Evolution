package com.brandon3055.draconicevolution.blocks.tileentity.flowgate;

import codechicken.lib.util.SneakyUtils;
import com.brandon3055.brandonscore.inventory.ContainerBCTile;
import com.brandon3055.draconicevolution.init.DEContent;
import com.brandon3055.draconicevolution.inventory.GuiLayoutFactories;
import com.brandon3055.draconicevolution.lib.WTFException;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fml.network.NetworkHooks;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Created by brandon3055 on 15/11/2016.
 */
public class TileFluidGate extends TileFlowGate {

    private FlowHandler inputHandler = new FlowHandler(this, true);
    private FlowHandler outputHandler = new FlowHandler(this, false);

    public TileFluidGate() {
        super(DEContent.tile_fluid_gate);
    }

    @Override
    public String getUnits() {
        return "MB/t";
    }

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


    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> capability, @Nullable Direction side) {
        if (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY && side != null && side.getAxis() == getDirection().getAxis()) {
            return side == getDirection() ? LazyOptional.of(() -> (T) outputHandler) : LazyOptional.of(() -> (T) inputHandler);
        }
        return super.getCapability(capability, side);
    }

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

    private class FlowHandler implements IFluidHandler {
        private final TileFluidGate gate;
        private final boolean isInput;

        public FlowHandler(TileFluidGate gate, boolean isInput) {
            this.gate = gate;
            this.isInput = isInput;
        }

        @Override
        public int getTanks() {
            if (isInput) {
                TileEntity tile = getTarget();
                if (tile != null) {
                    LazyOptional<IFluidHandler> fluidHandler = tile.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, getDirection().getOpposite());

                    if (fluidHandler.isPresent()) {
                        return fluidHandler.orElseThrow(WTFException::new).getTanks();
                    }
                }
            }
            return 1;
        }

        @Nonnull
        @Override
        public FluidStack getFluidInTank(int tank) {
            if (!isInput) {
                TileEntity tile = getSource();
                if (tile != null) {
                    LazyOptional<IFluidHandler> fluidHandler = tile.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, getDirection().getOpposite());

                    if (fluidHandler.isPresent()) {
                        return  fluidHandler.orElseThrow(WTFException::new).getFluidInTank(tank);
                    }
                }
            }

            return FluidStack.EMPTY;
        }

        @Override
        public int getTankCapacity(int tank) {
            if (isInput) {
                TileEntity tile = getTarget();
                if (tile != null) {
                    LazyOptional<IFluidHandler> fluidHandler = tile.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, getDirection().getOpposite());

                    if (fluidHandler.isPresent()) {
                        return fluidHandler.orElseThrow(WTFException::new).getTankCapacity(tank);
                    }
                }
            }
            return 0;
        }

        @Override
        public boolean isFluidValid(int tank, @Nonnull FluidStack stack) {
            if (isInput) {
                TileEntity tile = getTarget();
                if (tile != null) {
                    LazyOptional<IFluidHandler> fluidHandler = tile.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, getDirection().getOpposite());

                    if (fluidHandler.isPresent()) {
                        return fluidHandler.orElseThrow(WTFException::new).isFluidValid(tank, stack);
                    }
                }
            }
            return false;
        }

        @Override
        public int fill(FluidStack resource, FluidAction action) {
            if (isInput) {
                TileEntity tile = getTarget();
                if (tile != null) {
                    LazyOptional<IFluidHandler> fluidHandler = tile.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, getDirection().getOpposite());

                    if (fluidHandler.isPresent()) {
                        IFluidHandler handler = fluidHandler.orElseThrow(WTFException::new);

                        int transfer = (int) Math.min(getFlow(), handler.fill(resource, FluidAction.SIMULATE));

                        if (transfer < resource.getAmount()) {
                            FluidStack newStack = resource.copy();
                            newStack.setAmount(transfer);
//                            resource.shrink(transfer);
                            return handler.fill(newStack, action);
                        }
                        return handler.fill(resource, action);
                    }
                }
            }
            return 0;
        }

        @Nonnull
        @Override
        public FluidStack drain(FluidStack resource, FluidAction action) {
            if (!isInput) {
                if (resource.getAmount() > getFlow()) {
                    resource.setAmount((int)getFlow());
                }
                TileEntity tile = getSource();
                if (tile != null) {
                    LazyOptional<IFluidHandler> fluidHandler = tile.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, getDirection().getOpposite());

                    if (fluidHandler.isPresent()) {
                        return  fluidHandler.orElseThrow(WTFException::new).drain(resource, action);
                    }
                }
            }
            return FluidStack.EMPTY;
        }

        @Nonnull
        @Override
        public FluidStack drain(int maxDrain, FluidAction action) {
            if (!isInput) {
                TileEntity tile = getSource();
                if (tile != null) {
                    if (maxDrain > getFlow()) maxDrain = (int) getFlow();
                    LazyOptional<IFluidHandler> fluidHandler = tile.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, getDirection().getOpposite());

                    if (fluidHandler.isPresent()) {
                        return  fluidHandler.orElseThrow(WTFException::new).drain(maxDrain, action);
                    }
                }
            }
            return FluidStack.EMPTY;
        }
    }
}
