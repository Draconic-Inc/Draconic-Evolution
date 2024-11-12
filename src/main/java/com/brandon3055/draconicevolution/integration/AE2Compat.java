package com.brandon3055.draconicevolution.integration;

import com.brandon3055.draconicevolution.utils.LogHelper;
import net.neoforged.fml.InterModComms;
import net.neoforged.fml.ModList;

/**
 * Created by brandon3055 on 6/10/18.
 */
public class AE2Compat {

    public static void init() {
        if (ModList.get().isLoaded("appliedenergistics2")) {
            LogHelper.dev("Registering AE Compat");
            InterModComms.sendTo("appliedenergistics2", "whitelist-spatial", () -> "com.brandon3055.draconicevolution.blocks.tileentity.TileCelestialManipulator");
            InterModComms.sendTo("appliedenergistics2", "whitelist-spatial", () -> "com.brandon3055.draconicevolution.blocks.tileentity.TileChaosCrystal");
            InterModComms.sendTo("appliedenergistics2", "whitelist-spatial", () -> "com.brandon3055.draconicevolution.blocks.tileentity.TileCreativeOPCapacitor");
            InterModComms.sendTo("appliedenergistics2", "whitelist-spatial", () -> "com.brandon3055.draconicevolution.blocks.tileentity.TileDisenchanter");
            InterModComms.sendTo("appliedenergistics2", "whitelist-spatial", () -> "com.brandon3055.draconicevolution.blocks.tileentity.TileDislocatorPedestal");
            InterModComms.sendTo("appliedenergistics2", "whitelist-spatial", () -> "com.brandon3055.draconicevolution.blocks.tileentity.TileDislocatorReceptacle");
            InterModComms.sendTo("appliedenergistics2", "whitelist-spatial", () -> "com.brandon3055.draconicevolution.blocks.tileentity.TileEnergyCore");
            InterModComms.sendTo("appliedenergistics2", "whitelist-spatial", () -> "com.brandon3055.draconicevolution.blocks.tileentity.TileEnergyCoreStabilizer");
            InterModComms.sendTo("appliedenergistics2", "whitelist-spatial", () -> "com.brandon3055.draconicevolution.blocks.tileentity.TileEnergyPylon");
            InterModComms.sendTo("appliedenergistics2", "whitelist-spatial", () -> "com.brandon3055.draconicevolution.blocks.tileentity.TileEnergyTransfuser");
            InterModComms.sendTo("appliedenergistics2", "whitelist-spatial", () -> "com.brandon3055.draconicevolution.blocks.tileentity.TileEntityDetector");
            InterModComms.sendTo("appliedenergistics2", "whitelist-spatial", () -> "com.brandon3055.draconicevolution.blocks.tileentity.TileFusionCraftingCore");
            InterModComms.sendTo("appliedenergistics2", "whitelist-spatial", () -> "com.brandon3055.draconicevolution.blocks.tileentity.TileFusionCraftingInjector");
            InterModComms.sendTo("appliedenergistics2", "whitelist-spatial", () -> "com.brandon3055.draconicevolution.blocks.tileentity.TileGenerator");
            InterModComms.sendTo("appliedenergistics2", "whitelist-spatial", () -> "com.brandon3055.draconicevolution.blocks.tileentity.TileGrinder");
            InterModComms.sendTo("appliedenergistics2", "whitelist-spatial", () -> "com.brandon3055.draconicevolution.blocks.tileentity.TilePlacedItem");
            InterModComms.sendTo("appliedenergistics2", "whitelist-spatial", () -> "com.brandon3055.draconicevolution.blocks.tileentity.TilePortal");
            InterModComms.sendTo("appliedenergistics2", "whitelist-spatial", () -> "com.brandon3055.draconicevolution.blocks.tileentity.TilePotentiometer");
            InterModComms.sendTo("appliedenergistics2", "whitelist-spatial", () -> "com.brandon3055.draconicevolution.blocks.tileentity.TileRainSensor");
            InterModComms.sendTo("appliedenergistics2", "whitelist-spatial", () -> "com.brandon3055.draconicevolution.blocks.tileentity.TileStabilizedSpawner");
            InterModComms.sendTo("appliedenergistics2", "whitelist-spatial", () -> "com.brandon3055.draconicevolution.blocks.tileentity.TileStructureBlock");
        }
    }
}
