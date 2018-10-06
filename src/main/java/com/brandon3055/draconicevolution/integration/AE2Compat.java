package com.brandon3055.draconicevolution.integration;

import net.minecraftforge.fml.common.event.FMLInterModComms;

/**
 * Created by brandon3055 on 6/10/18.
 */
public class AE2Compat {

    public static void init() {
        FMLInterModComms.sendMessage("appliedenergistics2", "movabletile", "com.brandon3055.draconicevolution.blocks.tileentity.TileFluidGate");
        FMLInterModComms.sendMessage("appliedenergistics2", "movabletile", "com.brandon3055.draconicevolution.blocks.tileentity.TileFluxGate");
        FMLInterModComms.sendMessage("appliedenergistics2", "movabletile", "com.brandon3055.draconicevolution.blocks.tileentity.TileCraftingInjector");
        FMLInterModComms.sendMessage("appliedenergistics2", "movabletile", "com.brandon3055.draconicevolution.blocks.tileentity.TileCreativeRFCapacitor");
        FMLInterModComms.sendMessage("appliedenergistics2", "movabletile", "com.brandon3055.draconicevolution.blocks.tileentity.TileDislocatorPedestal");
        FMLInterModComms.sendMessage("appliedenergistics2", "movabletile", "com.brandon3055.draconicevolution.blocks.tileentity.TileDislocatorReceptacle");
        FMLInterModComms.sendMessage("appliedenergistics2", "movabletile", "com.brandon3055.draconicevolution.blocks.tileentity.TileDissEnchanter");
        FMLInterModComms.sendMessage("appliedenergistics2", "movabletile", "com.brandon3055.draconicevolution.blocks.tileentity.TileDraconiumChest");
        FMLInterModComms.sendMessage("appliedenergistics2", "movabletile", "com.brandon3055.draconicevolution.blocks.tileentity.TileEnergyCoreStabilizer");
        FMLInterModComms.sendMessage("appliedenergistics2", "movabletile", "com.brandon3055.draconicevolution.blocks.tileentity.TileEnergyInfuser");
        FMLInterModComms.sendMessage("appliedenergistics2", "movabletile", "com.brandon3055.draconicevolution.blocks.tileentity.TileEnergyPylon");
        FMLInterModComms.sendMessage("appliedenergistics2", "movabletile", "com.brandon3055.draconicevolution.blocks.tileentity.TileEnergyStorageCore");
        FMLInterModComms.sendMessage("appliedenergistics2", "movabletile", "com.brandon3055.draconicevolution.blocks.tileentity.TileEntityDetector");
        FMLInterModComms.sendMessage("appliedenergistics2", "movabletile", "com.brandon3055.draconicevolution.blocks.tileentity.TileFusionCraftingCore");
        FMLInterModComms.sendMessage("appliedenergistics2", "movabletile", "com.brandon3055.draconicevolution.blocks.tileentity.TileGenerator");
        FMLInterModComms.sendMessage("appliedenergistics2", "movabletile", "com.brandon3055.draconicevolution.blocks.tileentity.TileGrinder");
        FMLInterModComms.sendMessage("appliedenergistics2", "movabletile", "com.brandon3055.draconicevolution.blocks.tileentity.TileInvisECoreBlock");
        FMLInterModComms.sendMessage("appliedenergistics2", "movabletile", "com.brandon3055.draconicevolution.blocks.tileentity.TileParticleGenerator");
        FMLInterModComms.sendMessage("appliedenergistics2", "movabletile", "com.brandon3055.draconicevolution.blocks.tileentity.TilePlacedItem");
        FMLInterModComms.sendMessage("appliedenergistics2", "movabletile", "com.brandon3055.draconicevolution.blocks.tileentity.TilePortal");
        FMLInterModComms.sendMessage("appliedenergistics2", "movabletile", "com.brandon3055.draconicevolution.blocks.tileentity.TilePortalClient");
        FMLInterModComms.sendMessage("appliedenergistics2", "movabletile", "com.brandon3055.draconicevolution.blocks.tileentity.TilePotentiometer");
        FMLInterModComms.sendMessage("appliedenergistics2", "movabletile", "com.brandon3055.draconicevolution.blocks.tileentity.TileRainSensor");
        FMLInterModComms.sendMessage("appliedenergistics2", "movabletile", "com.brandon3055.draconicevolution.blocks.tileentity.TileStabilizedSpawner");
    }

}
