package com.brandon3055.draconicevolution.blocks.tileentity.flowgate;

import com.brandon3055.draconicevolution.init.DEContent;
import com.brandon3055.draconicevolution.inventory.FlowGateMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.NotNull;


/**
 * Created by brandon3055 on 15/11/2016.
 */
public class TileFluidGate extends TileFlowGate {

    private FlowHandler inputHandler = new FlowHandler(this, true);
    private FlowHandler outputHandler = new FlowHandler(this, false);

    public TileFluidGate(BlockPos pos, BlockState state) {
        super(DEContent.TILE_FLUID_GATE.get(), pos, state);
    }

    public static void register(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(Capabilities.FluidHandler.BLOCK, DEContent.TILE_FLUID_GATE.get(), (tile, side) -> {
            if (side != null && side.getAxis() == tile.getDirection().getAxis()) {
                return side == tile.getDirection() ? tile.outputHandler : tile.inputHandler;
            }
            return null;
        });
    }

    @Override
    public String getUnits() {
        return "MB/t";
    }

    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
        return new FlowGateMenu(id, player.getInventory(), this);
    }

    @Override
    public boolean onBlockActivated(BlockState state, Player player, InteractionHand handIn, BlockHitResult hit) {
        if (player instanceof ServerPlayer) {
            player.openMenu(this, worldPosition);
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
                    IFluidHandler fluidHandler = Capabilities.FluidHandler.BLOCK.getCapability(tile.getLevel(), tile.getBlockPos(), tile.getBlockState(), tile, getDirection().getOpposite());
                    if (fluidHandler != null) {
                        return fluidHandler.getTanks();
                    }
                }
            }
            return 1;
        }

        @NotNull
        @Override
        public FluidStack getFluidInTank(int tank) {
            if (!isInput) {
                BlockEntity tile = getSource();
                if (tile != null) {
                    IFluidHandler fluidHandler = Capabilities.FluidHandler.BLOCK.getCapability(tile.getLevel(), tile.getBlockPos(), tile.getBlockState(), tile, getDirection().getOpposite());
                    if (fluidHandler != null) {
                        return fluidHandler.getFluidInTank(tank);
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
                    IFluidHandler fluidHandler = Capabilities.FluidHandler.BLOCK.getCapability(tile.getLevel(), tile.getBlockPos(), tile.getBlockState(), tile, getDirection().getOpposite());
                    if (fluidHandler != null) {
                        return fluidHandler.getTankCapacity(tank);
                    }
                }
            }
            return 0;
        }

        @Override
        public boolean isFluidValid(int tank, @NotNull FluidStack stack) {
            if (isInput) {
                BlockEntity tile = getTarget();
                if (tile != null) {
                    IFluidHandler fluidHandler = Capabilities.FluidHandler.BLOCK.getCapability(tile.getLevel(), tile.getBlockPos(), tile.getBlockState(), tile, getDirection().getOpposite());
                    if (fluidHandler != null) {
                        return fluidHandler.isFluidValid(tank, stack);
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
                    IFluidHandler handler = Capabilities.FluidHandler.BLOCK.getCapability(tile.getLevel(), tile.getBlockPos(), tile.getBlockState(), tile, getDirection().getOpposite());
                    if (handler != null) {
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

        @NotNull
        @Override
        public FluidStack drain(FluidStack resource, FluidAction action) {
            if (!isInput) {
                if (resource.getAmount() > getFlow()) {
                    resource.setAmount((int) getFlow());
                }
                BlockEntity tile = getSource();
                if (tile != null) {
                    IFluidHandler fluidHandler = Capabilities.FluidHandler.BLOCK.getCapability(tile.getLevel(), tile.getBlockPos(), tile.getBlockState(), tile, getDirection().getOpposite());
                    if (fluidHandler != null) {
                        return fluidHandler.drain(resource, action);
                    }
                }
            }
            return FluidStack.EMPTY;
        }

        @NotNull
        @Override
        public FluidStack drain(int maxDrain, FluidAction action) {
            if (!isInput) {
                BlockEntity tile = getSource();
                if (tile != null) {
                    if (maxDrain > getFlow()) maxDrain = (int) getFlow();
                    IFluidHandler fluidHandler = Capabilities.FluidHandler.BLOCK.getCapability(tile.getLevel(), tile.getBlockPos(), tile.getBlockState(), tile, getDirection().getOpposite());
                    if (fluidHandler != null) {
                        return fluidHandler.drain(maxDrain, action);
                    }
                }
            }
            return FluidStack.EMPTY;
        }
    }
}
