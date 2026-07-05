package com.brandon3055.draconicevolution.integration.computers;

import com.brandon3055.draconicevolution.blocks.tileentity.TileFusionCraftingCore;
import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.peripheral.GenericPeripheral;
import dan200.computercraft.api.peripheral.PeripheralType;

import java.util.Locale;

public class PeripheralFusionCraftingCore implements GenericPeripheral {

    @Override
    public String id() {
        return "draconicevolution:fusion_crafting_core";
    }

    @Override
    public PeripheralType getType() {
        return PeripheralType.ofType("fusion_crafting_core");
    }

    @LuaFunction(mainThread = true)
    public final boolean startCraft(TileFusionCraftingCore tile) {
        boolean wasCrafting = tile.isCrafting();

        tile.startCraft();

        return !wasCrafting && tile.isCrafting();
    }

    @LuaFunction(mainThread = true)
    public final boolean isCrafting(TileFusionCraftingCore tile) {
        return tile.isCrafting();
    }

    @LuaFunction(mainThread = true)
    public final boolean cancelCraft(TileFusionCraftingCore tile) {
        if (!tile.isCrafting()) {
            return false;
        }

        tile.cancelCraft();
        return !tile.isCrafting();
    }

    @LuaFunction(mainThread = true)
    public final String getFusionState(TileFusionCraftingCore tile) {
        return tile.getFusionState()
                .name()
                .toLowerCase(Locale.ROOT);
    }

    @LuaFunction(mainThread = true)
    public final double getFusionProgress(TileFusionCraftingCore tile) {
        return tile.getFusionProgress();
    }

    @LuaFunction(mainThread = true)
    public final String getFusionStatus(TileFusionCraftingCore tile) {
        var status = tile.getFusionStatus();

        return status == null ? "" : status.getString();
    }

    @LuaFunction(mainThread = true)
    public final int getInjectorCount(TileFusionCraftingCore tile) {
        return tile.getInjectors().size();
    }

    @LuaFunction(mainThread = true)
    public final String getMinimumTier(TileFusionCraftingCore tile) {
        return tile.getMinimumTier().getSerializedName();
    }

    @LuaFunction(mainThread = true)
    public final String getActiveRecipeId(TileFusionCraftingCore tile) {
        var recipe = tile.getActiveRecipe();

        return recipe == null ? "" : recipe.id().toString();
    }
}