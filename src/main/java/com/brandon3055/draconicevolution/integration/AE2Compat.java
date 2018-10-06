package com.brandon3055.draconicevolution.integration;

import com.brandon3055.draconicevolution.utils.LogHelper;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.event.FMLInterModComms;

/**
 * Created by brandon3055 on 6/10/18.
 */
public class AE2Compat {

    public static void init() {
        if (Loader.isModLoaded("appliedenergistics2")) {
            LogHelper.dev("Registering AE Compat");
            FMLInterModComms.sendMessage("appliedenergistics2", "whitelist-spatial", "com.brandon3055.draconicevolution.blocks.tileentity.flowgate.TileFluidGate");
            FMLInterModComms.sendMessage("appliedenergistics2", "whitelist-spatial", "com.brandon3055.draconicevolution.blocks.tileentity.flowgate.TileFluxGate");
            FMLInterModComms.sendMessage("appliedenergistics2", "whitelist-spatial", "com.brandon3055.draconicevolution.blocks.tileentity.TileCraftingInjector");
            FMLInterModComms.sendMessage("appliedenergistics2", "whitelist-spatial", "com.brandon3055.draconicevolution.blocks.tileentity.TileCreativeRFCapacitor");
            FMLInterModComms.sendMessage("appliedenergistics2", "whitelist-spatial", "com.brandon3055.draconicevolution.blocks.tileentity.TileDislocatorPedestal");
            FMLInterModComms.sendMessage("appliedenergistics2", "whitelist-spatial", "com.brandon3055.draconicevolution.blocks.tileentity.TileDislocatorReceptacle");
            FMLInterModComms.sendMessage("appliedenergistics2", "whitelist-spatial", "com.brandon3055.draconicevolution.blocks.tileentity.TileDissEnchanter");
            FMLInterModComms.sendMessage("appliedenergistics2", "whitelist-spatial", "com.brandon3055.draconicevolution.blocks.tileentity.TileDraconiumChest");
            FMLInterModComms.sendMessage("appliedenergistics2", "whitelist-spatial", "com.brandon3055.draconicevolution.blocks.tileentity.TileEnergyCoreStabilizer");
            FMLInterModComms.sendMessage("appliedenergistics2", "whitelist-spatial", "com.brandon3055.draconicevolution.blocks.tileentity.TileEnergyInfuser");
            FMLInterModComms.sendMessage("appliedenergistics2", "whitelist-spatial", "com.brandon3055.draconicevolution.blocks.tileentity.TileEnergyPylon");
            FMLInterModComms.sendMessage("appliedenergistics2", "whitelist-spatial", "com.brandon3055.draconicevolution.blocks.tileentity.TileEnergyStorageCore");
            FMLInterModComms.sendMessage("appliedenergistics2", "whitelist-spatial", "com.brandon3055.draconicevolution.blocks.tileentity.TileEntityDetector");
            FMLInterModComms.sendMessage("appliedenergistics2", "whitelist-spatial", "com.brandon3055.draconicevolution.blocks.tileentity.TileFusionCraftingCore");
            FMLInterModComms.sendMessage("appliedenergistics2", "whitelist-spatial", "com.brandon3055.draconicevolution.blocks.tileentity.TileGenerator");
            FMLInterModComms.sendMessage("appliedenergistics2", "whitelist-spatial", "com.brandon3055.draconicevolution.blocks.tileentity.TileGrinder");
            FMLInterModComms.sendMessage("appliedenergistics2", "whitelist-spatial", "com.brandon3055.draconicevolution.blocks.tileentity.TileInvisECoreBlock");
            FMLInterModComms.sendMessage("appliedenergistics2", "whitelist-spatial", "com.brandon3055.draconicevolution.blocks.tileentity.TileParticleGenerator");
            FMLInterModComms.sendMessage("appliedenergistics2", "whitelist-spatial", "com.brandon3055.draconicevolution.blocks.tileentity.TilePlacedItem");
            FMLInterModComms.sendMessage("appliedenergistics2", "whitelist-spatial", "com.brandon3055.draconicevolution.blocks.tileentity.TilePortal");
            FMLInterModComms.sendMessage("appliedenergistics2", "whitelist-spatial", "com.brandon3055.draconicevolution.blocks.tileentity.TilePotentiometer");
            FMLInterModComms.sendMessage("appliedenergistics2", "whitelist-spatial", "com.brandon3055.draconicevolution.blocks.tileentity.TileRainSensor");
            FMLInterModComms.sendMessage("appliedenergistics2", "whitelist-spatial", "com.brandon3055.draconicevolution.blocks.tileentity.TileStabilizedSpawner");
            FMLInterModComms.sendMessage("appliedenergistics2", "whitelist-spatial", "com.brandon3055.draconicevolution.blocks.energynet.tileentity.TileCrystalBase");
            FMLInterModComms.sendMessage("appliedenergistics2", "whitelist-spatial", "com.brandon3055.draconicevolution.blocks.reactor.tileentity.TileReactorComponent");
            FMLInterModComms.sendMessage("appliedenergistics2", "whitelist-spatial", "com.brandon3055.draconicevolution.blocks.reactor.tileentity.TileReactorCore");
        }
    }
}
