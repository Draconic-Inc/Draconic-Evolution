package com.brandon3055.draconicevolution.integration.computers.cc;

import dan200.computercraft.api.ForgeComputerCraftAPI;
import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.api.peripheral.IPeripheralProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class DEPeripheralProvider implements IPeripheralProvider {
    private IPeripheral peripheral;
    private LazyOptional<IPeripheral> holderPeripheral;

    /**
     * Produce an peripheral implementation from a block location.
     *
     * @param world The world the block is in.
     * @param pos   The position the block is at.
     * @param side  The side to get the peripheral from.
     * @return A peripheral, or {@code null} if there is not a peripheral here you'd like to handle.
     * @see ForgeComputerCraftAPI#registerPeripheralProvider(IPeripheralProvider)
     */
    @Nullable
    @Override
    public LazyOptional<IPeripheral> getPeripheral(@Nonnull Level world, @Nonnull BlockPos pos, @Nonnull Direction side) {
        BlockEntity tile = world.getBlockEntity(pos);
        if (tile instanceof IPeripheral) {
            setPeripheral((IPeripheral)tile);
            return holderPeripheral;
        }
        return LazyOptional.empty();
    }

    protected void setPeripheral(IPeripheral peripheral) {
        this.peripheral = peripheral;
        this.holderPeripheral = LazyOptional.of(() -> peripheral);
    }
}