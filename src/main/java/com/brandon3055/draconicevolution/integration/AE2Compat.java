package com.brandon3055.draconicevolution.integration;

import com.brandon3055.draconicevolution.utils.LogHelper;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.ModList;

/**
 * Created by brandon3055 on 6/10/18.
 */
public class AE2Compat {

    public static void init() {
        if (ModList.get().isLoaded("appliedenergistics2")) {
            LogHelper.dev("Registering AE Compat");
            InterModComms.sendTo("appliedenergistics2", "whitelist-spatial", () -> "com.brandon3055.draconicevolution.blocks.tileentity.flowgate.TileFluidGate");
            InterModComms.sendTo("appliedenergistics2", "whitelist-spatial", () -> "com.brandon3055.draconicevolution.blocks.tileentity.flowgate.TileFluxGate");
            InterModComms.sendTo("appliedenergistics2", "whitelist-spatial", () -> "com.brandon3055.draconicevolution.blocks.tileentity.TileCraftingInjector");
            InterModComms.sendTo("appliedenergistics2", "whitelist-spatial", () -> "com.brandon3055.draconicevolution.blocks.tileentity.TileCreativeRFCapacitor");
            InterModComms.sendTo("appliedenergistics2", "whitelist-spatial", () -> "com.brandon3055.draconicevolution.blocks.tileentity.TileDislocatorPedestal");
            InterModComms.sendTo("appliedenergistics2", "whitelist-spatial", () -> "com.brandon3055.draconicevolution.blocks.tileentity.TileDislocatorReceptacle");
            InterModComms.sendTo("appliedenergistics2", "whitelist-spatial", () -> "com.brandon3055.draconicevolution.blocks.tileentity.TileDissEnchanter");
            InterModComms.sendTo("appliedenergistics2", "whitelist-spatial", () -> "com.brandon3055.draconicevolution.blocks.tileentity.TileDraconiumChest");
            InterModComms.sendTo("appliedenergistics2", "whitelist-spatial", () -> "com.brandon3055.draconicevolution.blocks.tileentity.TileEnergyCoreStabilizer");
            InterModComms.sendTo("appliedenergistics2", "whitelist-spatial", () -> "com.brandon3055.draconicevolution.blocks.tileentity.TileEnergyInfuser");
            InterModComms.sendTo("appliedenergistics2", "whitelist-spatial", () -> "com.brandon3055.draconicevolution.blocks.tileentity.TileEnergyPylon");
            InterModComms.sendTo("appliedenergistics2", "whitelist-spatial", () -> "com.brandon3055.draconicevolution.blocks.tileentity.TileEnergyStorageCore");
            InterModComms.sendTo("appliedenergistics2", "whitelist-spatial", () -> "com.brandon3055.draconicevolution.blocks.tileentity.TileEntityDetector");
            InterModComms.sendTo("appliedenergistics2", "whitelist-spatial", () -> "com.brandon3055.draconicevolution.blocks.tileentity.TileFusionCraftingCore");
            InterModComms.sendTo("appliedenergistics2", "whitelist-spatial", () -> "com.brandon3055.draconicevolution.blocks.tileentity.TileGenerator");
            InterModComms.sendTo("appliedenergistics2", "whitelist-spatial", () -> "com.brandon3055.draconicevolution.blocks.tileentity.TileGrinder");
            InterModComms.sendTo("appliedenergistics2", "whitelist-spatial", () -> "com.brandon3055.draconicevolution.blocks.tileentity.TileInvisECoreBlock");
            InterModComms.sendTo("appliedenergistics2", "whitelist-spatial", () -> "com.brandon3055.draconicevolution.blocks.tileentity.TileParticleGenerator");
            InterModComms.sendTo("appliedenergistics2", "whitelist-spatial", () -> "com.brandon3055.draconicevolution.blocks.tileentity.TilePlacedItem");
            InterModComms.sendTo("appliedenergistics2", "whitelist-spatial", () -> "com.brandon3055.draconicevolution.blocks.tileentity.TilePortal");
            InterModComms.sendTo("appliedenergistics2", "whitelist-spatial", () -> "com.brandon3055.draconicevolution.blocks.tileentity.TilePotentiometer");
            InterModComms.sendTo("appliedenergistics2", "whitelist-spatial", () -> "com.brandon3055.draconicevolution.blocks.tileentity.TileRainSensor");
            InterModComms.sendTo("appliedenergistics2", "whitelist-spatial", () -> "com.brandon3055.draconicevolution.blocks.tileentity.TileStabilizedSpawner");
            InterModComms.sendTo("appliedenergistics2", "whitelist-spatial", () -> "com.brandon3055.draconicevolution.blocks.energynet.tileentity.TileCrystalBase");
            InterModComms.sendTo("appliedenergistics2", "whitelist-spatial", () -> "com.brandon3055.draconicevolution.blocks.reactor.tileentity.TileReactorComponent");
            InterModComms.sendTo("appliedenergistics2", "whitelist-spatial", () -> "com.brandon3055.draconicevolution.blocks.reactor.tileentity.TileReactorCore");
        }
    }
}
