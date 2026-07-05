package com.brandon3055.draconicevolution.integration.computers;

import com.brandon3055.draconicevolution.blocks.tileentity.TileFusionCraftingInjector;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.peripheral.GenericPeripheral;
import dan200.computercraft.api.peripheral.PeripheralType;

import java.util.Locale;

public class PeripheralFusionCraftingInjector implements GenericPeripheral {

    @Override
    public String id() {
        return "draconicevolution:fusion_crafting_injector";
    }

    @Override
    public PeripheralType getType() {
        return PeripheralType.ofType("fusion_crafting_injector");
    }

    @LuaFunction(mainThread = true)
    public final boolean isSingleItemMode(TileFusionCraftingInjector tile) {
        return tile.isSingleItemMode();
    }

    @LuaFunction(mainThread = true)
    public final boolean setSingleItemMode(
            TileFusionCraftingInjector tile,
            boolean singleItem
    ) {
        tile.setSingleItemMode(singleItem);
        return tile.isSingleItemMode();
    }

    @LuaFunction(mainThread = true)
    public final String getInjectorTier(TileFusionCraftingInjector tile) {
        return tile.getInjectorTier().getSerializedName();
    }

    @LuaFunction(mainThread = true)
    public final long getInjectorEnergy(TileFusionCraftingInjector tile) {
        return tile.getInjectorEnergy();
    }

        @LuaFunction(mainThread = true)
    public final long getEnergyRequirement(TileFusionCraftingInjector tile) {
        return tile.getEnergyRequirement();
    }

    @LuaFunction(mainThread = true)
    public final boolean isLinkedToCore(TileFusionCraftingInjector tile) {
        return tile.getCore() != null;
    }
}
