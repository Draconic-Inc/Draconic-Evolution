package com.brandon3055.draconicevolution.blocks.tileentity.flowgate;

import com.brandon3055.draconicevolution.init.DEContent;
import com.brandon3055.draconicevolution.inventory.ContainerDETile;
import com.brandon3055.draconicevolution.lib.WTFException;
import net.covers1624.quack.util.SneakyUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.network.NetworkHooks;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Created by brandon3055 on 15/11/2016.
 */
public class TileFluidGate extends TileFlowGate {

    private FlowHandler inputHandler = new FlowHandler(this, true);
    private FlowHandler outputHandler = new FlowHandler(this, false);

    public TileFluidGate(BlockPos pos, BlockState state) {
        super(DEContent.TILE_FLUID_GATE.get(), pos, state);
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
        if (capability == ForgeCapabilities.FLUID_HANDLER && side != null && side.getAxis() == getDirection().getAxis()) {
            return side == getDirection() ? LazyOptional.of(() -> (T) outputHandler) : LazyOptional.of(() -> (T) inputHandler);
        }
        return super.getCapability(capability, side);
    }

    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
        return new ContainerDETile<>(DEContent.MENU_FLOW_GATE.get(), id, player.getInventory(), this);
    }

    @Override
    public boolean onBlockActivated(BlockState state, Player player, InteractionHand handIn, BlockHitResult hit) {
        if (player instanceof ServerPlayer) {
            NetworkHooks.openScreen((ServerPlayer) player, this, worldPosition);
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
                BlockEntity tile = getTarget();
                if (tile != null) {
                    LazyOptional<IFluidHandler> fluidHandler = tile.getCapability(ForgeCapabilities.FLUID_HANDLER, getDirection().getOpposite());

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
                BlockEntity tile = getSource();
                if (tile != null) {
                    LazyOptional<IFluidHandler> fluidHandler = tile.getCapability(ForgeCapabilities.FLUID_HANDLER, getDirection().getOpposite());

                    if (fluidHandler.isPresent()) {
                        return fluidHandler.orElseThrow(WTFException::new).getFluidInTank(tank);
                    }
                }
            }

            return FluidStack.EMPTY;
        }

        @Override
        public int getTankCapacity(int tank) {
            if (isInput) {
                BlockEntity tile = getTarget();
                if (tile != null) {
                    LazyOptional<IFluidHandler> fluidHandler = tile.getCapability(ForgeCapabilities.FLUID_HANDLER, getDirection().getOpposite());

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
                BlockEntity tile = getTarget();
                if (tile != null) {
                    LazyOptional<IFluidHandler> fluidHandler = tile.getCapability(ForgeCapabilities.FLUID_HANDLER, getDirection().getOpposite());

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
                BlockEntity tile = getTarget();
                if (tile != null) {
                    LazyOptional<IFluidHandler> fluidHandler = tile.getCapability(ForgeCapabilities.FLUID_HANDLER, getDirection().getOpposite());

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
                    resource.setAmount((int) getFlow());
                }
                BlockEntity tile = getSource();
                if (tile != null) {
                    LazyOptional<IFluidHandler> fluidHandler = tile.getCapability(ForgeCapabilities.FLUID_HANDLER, getDirection().getOpposite());

                    if (fluidHandler.isPresent()) {
                        return fluidHandler.orElseThrow(WTFException::new).drain(resource, action);
                    }
                }
            }
            return FluidStack.EMPTY;
        }

        @Nonnull
        @Override
        public FluidStack drain(int maxDrain, FluidAction action) {
            if (!isInput) {
                BlockEntity tile = getSource();
                if (tile != null) {
                    if (maxDrain > getFlow()) maxDrain = (int) getFlow();
                    LazyOptional<IFluidHandler> fluidHandler = tile.getCapability(ForgeCapabilities.FLUID_HANDLER, getDirection().getOpposite());

                    if (fluidHandler.isPresent()) {
                        return fluidHandler.orElseThrow(WTFException::new).drain(maxDrain, action);
                    }
                }
            }
            return FluidStack.EMPTY;
        }
    }
}
