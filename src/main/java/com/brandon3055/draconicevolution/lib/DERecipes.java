package com.brandon3055.draconicevolution.lib;

import com.brandon3055.draconicevolution.DEFeatures;
import com.brandon3055.draconicevolution.items.ToolUpgrade;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.GameRegistry;

import static com.brandon3055.draconicevolution.DEFeatures.*;
import static com.brandon3055.draconicevolution.lib.RecipeManager.RecipeDifficulty.*;
import static com.brandon3055.draconicevolution.lib.RecipeManager.*;
import static net.minecraft.init.Blocks.*;
import static net.minecraft.init.Items.*;
import static net.minecraft.init.Items.SKULL;

/**
 * Created by brandon3055 on 23/07/2016.
 */
public class DERecipes {

    public static void addRecipes() {

        /* ------------------ Blocks ------------------ */

        //region Simple Blocks
        addFusion(NORMAL, new ItemStack(draconicBlock, 4), new ItemStack(draconiumBlock, 4), 50000000, 1, draconicCore, draconicCore, draconicCore, dragonHeart, draconicCore, draconicCore, draconicCore);
        addFusion(HARD, new ItemStack(draconicBlock, 4), new ItemStack(draconiumBlock, 4), 100000000, 1, wyvernCore, wyvernCore, wyvernCore, dragonHeart, wyvernCore, wyvernCore, wyvernCore);

        //endregion

        //region Machines
        addShaped(ALL, generator, "ABA", "BCB", "ADA", 'A', "ingotBrickNether", 'B', "ingotIron", 'C', FURNACE, 'D', draconicCore);
        addShaped(NORMAL, grinder, "ABA", "CDC", "AEA", 'A', "ingotIron", 'B', "ingotDraconium", 'C', DIAMOND_SWORD, 'D', draconicCore, 'E', FURNACE);
        addShaped(HARD, grinder, "ABA", "CDC", "AEA", 'A', "ingotIron", 'B', "ingotDraconium", 'C', wyvernSword, 'D', draconicCore, 'E', FURNACE);
        addShaped(NORMAL, energyInfuser, "ABA", "CDC", "ACA", 'A', "ingotDraconium", 'B', new ItemStack(particleGenerator, 1, 2), 'C', draconicCore, 'D', ENCHANTING_TABLE);
        addShaped(HARD, energyInfuser, "ABA", "CDC", "AEA", 'A', "ingotDraconium", 'B', new ItemStack(particleGenerator, 1, 2), 'C', wyvernCore, 'D', ENCHANTING_TABLE, 'E', draconicCore);
        addShaped(ALL, new ItemStack(particleGenerator, 1, 0), "ABA", "BCB", "ABA", 'A', "blockRedstone", 'B', BLAZE_ROD, 'C', draconicCore);
        addShaped(NORMAL, new ItemStack(particleGenerator, 1, 2), "A A", " B ", "A A", 'A', "gemDiamond", 'B', new ItemStack(particleGenerator, 1, 0));
        addFusion(HARD, new ItemStack(particleGenerator, 1, 2), new ItemStack(particleGenerator), 80000, 1, "gemDiamond", wyvernCore, "gemDiamond", wyvernCore, "gemDiamond", "gemDiamond");
        addShaped(NORMAL, infusedObsidian, "ABA", "BCB", "ABA", 'A', BLAZE_POWDER, 'B', "obsidian", 'C', "dustDraconium");
        addFusion(HARD, new ItemStack(infusedObsidian), new ItemStack(OBSIDIAN), 32000, 1, "dustDraconium", "ingotDraconium", "gemDiamond", "ingotDraconium", "dustDraconium", "dustDraconium", BLAZE_POWDER, "gemDiamond", BLAZE_POWDER, "dustDraconium");
        addShaped(NORMAL, dislocatorReceptacle, "ABA", " C ", "A A", 'A', "ingotIron", 'B', draconicCore, 'C', infusedObsidian);
        addShaped(HARD, dislocatorReceptacle, "ABA", " C ", "A A", 'A', "ingotIron", 'B', wyvernCore, 'C', infusedObsidian);
        addShaped(ALL, dislocatorPedestal, " A ", " B ", "CDC", 'A', STONE_PRESSURE_PLATE, 'B', "stone", 'C', STONE_SLAB, 'D', BLAZE_POWDER);
        addShaped(NORMAL, energyStorageCore, "AAA", "BCB", "AAA", 'A', "ingotDraconium", 'B', wyvernEnergyCore, 'C', wyvernCore);
        addShaped(HARD, energyStorageCore, "AAA", "BCB", "AAA", 'A', "ingotDraconiumAwakened", 'B', draconicEnergyCore, 'C', awakenedCore);
        addShaped(NORMAL, new ItemStack(energyPylon, 2), "ABA", "CDC", "AEA", 'A', "ingotDraconium", 'B', ENDER_EYE, 'C', "gemEmerald", 'D', draconicCore, 'E', "gemDiamond");
        addShaped(HARD, new ItemStack(energyPylon, 2), "ABA", "CDC", "AEA", 'A', "ingotDraconium", 'B', ENDER_EYE, 'C', "gemEmerald", 'D', wyvernCore, 'E', "gemDiamond");
        addShaped(ALL, rainSensor, " A ", "BCB", "DDD", 'A', BUCKET, 'B', "dustRedstone", 'C', STONE_PRESSURE_PLATE, 'D', STONE_SLAB);
        addShaped(ALL, dissEnchanter, "ABA", "CDC", "EEE", 'A', "gemEmerald", 'B', draconicCore, 'C', ENCHANTED_BOOK, 'D', ENCHANTING_TABLE, 'E', BOOKSHELF);
        addShaped(NORMAL, celestialManipulator, "ABA", "CDC", "EFE", 'A', "blockRedstone", 'B', CLOCK, 'C', "ingotDraconium", 'D', "dragonEgg", 'E', "ingotIron", 'F', wyvernCore);
        addShaped(HARD, celestialManipulator, "ABA", "CDC", "EFE", 'A', "blockRedstone", 'B', CLOCK, 'C', "ingotDraconiumAwakened", 'D', "dragonEgg", 'E', "ingotIron", 'F', awakenedCore);
        addShaped(ALL, potentiometer, " A ", "BCB", "DDD", 'A', "plankWood", 'B', "dustRedstone", 'C', "dustDraconium", 'D', STONE_SLAB);
        addShaped(ALL, entityDetector, "ABA", "CDC", "EFE", 'A', new ItemStack(DYE, 1, 4), 'B', ENDER_EYE, 'C', "dustRedstone", 'D', "ingotDraconium", 'E', "ingotIron", 'F', draconicCore);
        addShaped(ALL, new ItemStack(entityDetector, 1, 1), "ABA", "CDC", "EFE", 'A', "blockRedstone", 'B', new ItemStack(SKULL, 1, 1), 'C', "blockLapis", 'D', "gemDiamond", 'E', "ingotDraconium", 'F', entityDetector);
        addShaped(ALL, new ItemStack(flowGate, 1, 8), "ABA", "CDC", "AEA", 'A', "ingotIron", 'B', potentiometer, 'C', BUCKET, 'D', draconicCore, 'E', COMPARATOR);
        addShaped(ALL, flowGate, "ABA", "CDC", "AEA", 'A', "ingotIron", 'B', potentiometer, 'C', "blockRedstone", 'D', draconicCore, 'E', COMPARATOR);
        addShaped(ALL, itemDislocationInhibitor, "AAA", "BCB", "AAA", 'A', "ingotIron", 'B', IRON_BARS, 'C', new ItemStack(magnet, 1, 0));

        //Fusion Crafting Blocks
        addShaped(NORMAL, fusionCraftingCore, "ABA", "BCB", "ABA", 'A', "blockLapis", 'B', "gemDiamond", 'C', draconicCore);
        addShaped(HARD, fusionCraftingCore, "ABA", "BCB", "ABA", 'A', "blockLapis", 'B', "netherStar", 'C', draconicCore);
        addShaped(NORMAL, craftingInjector, "ABA", "CDC", "CCC", 'A', "gemDiamond", 'B', draconicCore, 'C', "stone", 'D', "blockIron");
        addShaped(HARD, craftingInjector, "ABA", "CDC", "ECE", 'A', "gemDiamond", 'B', draconicCore, 'C', "blockDraconium", 'D', "blockIron", 'E', "stone");
        addFusion(NORMAL, new ItemStack(craftingInjector, 1, 1), new ItemStack(craftingInjector), 32000, 0, wyvernCore, "gemDiamond", draconicCore, "gemDiamond", draconicCore, "gemDiamond", "blockDraconium", "gemDiamond");
        addFusion(HARD, new ItemStack(craftingInjector, 1, 1), new ItemStack(craftingInjector), 256000, 0, wyvernCore, "netherStar", draconicCore, "netherStar", draconicCore, "netherStar", "blockDraconium", "netherStar");
        addFusion(NORMAL, new ItemStack(craftingInjector, 1, 2), new ItemStack(craftingInjector, 1, 1), 256000, 1, "gemDiamond", "gemDiamond", wyvernCore, "blockDraconiumAwakened", wyvernCore, "gemDiamond", "gemDiamond");
        addFusion(HARD, new ItemStack(craftingInjector, 1, 2), new ItemStack(craftingInjector, 1, 1), 1000000, 1, "netherStar", "netherStar", wyvernCore, "blockDraconiumAwakened", wyvernCore, "netherStar", "netherStar", "dragonEgg");
        addFusion(NORMAL, new ItemStack(craftingInjector, 1, 3), new ItemStack(craftingInjector, 1, 2), 8000000, 2, "gemDiamond", "gemDiamond", chaoticCore, "dragonEgg", "gemDiamond", "gemDiamond");
        addFusion(HARD, new ItemStack(craftingInjector, 1, 3), new ItemStack(craftingInjector, 1, 2), 23000000, 2, "netherStar", "netherStar", chaoticCore, "dragonEgg", chaoticCore, "netherStar", "netherStar", chaosShard);

        //Energy Net
        //Relay
        addShaped(NORMAL, new ItemStack(energyCrystal, 4), " A ", "ABA", " A ", 'A', "gemDiamond", 'B', wyvernEnergyCore);
        addShaped(HARD, new ItemStack(energyCrystal, 4), "ABA", "BCB", "ABA", 'A', "ingotDraconium", 'B', "gemDiamond", 'C', wyvernEnergyCore);

        addShaped(NORMAL, new ItemStack(energyCrystal, 4, 1), "ABA", "BCB", "ABA", 'A', wyvernEnergyCore, 'B', new ItemStack(energyCrystal, 1, 0), 'C', draconicCore);
        addShaped(HARD, new ItemStack(energyCrystal, 4, 1), "ABA", "BCB", "ABA", 'A', wyvernEnergyCore, 'B', new ItemStack(energyCrystal, 1, 0), 'C', wyvernCore);

        addFusion(NORMAL, new ItemStack(energyCrystal, 4, 2), new ItemStack(energyCrystal, 4, 1), 128000, 2, wyvernEnergyCore, "gemDiamond", wyvernCore, "gemDiamond", wyvernEnergyCore, wyvernEnergyCore, "gemDiamond", "gemDiamond", wyvernEnergyCore);
        addFusion(HARD, new ItemStack(energyCrystal, 4, 2), new ItemStack(energyCrystal, 4, 1), 512000, 2, wyvernEnergyCore, "gemDiamond", wyvernCore, "gemDiamond", wyvernEnergyCore, wyvernEnergyCore, "gemDiamond", draconicEnergyCore, "gemDiamond", wyvernEnergyCore);

        //I/O
        addShapeless(ALL, new ItemStack(energyCrystal, 2, 3), new ItemStack(energyCrystal, 1, 0));
        addShapeless(ALL, new ItemStack(energyCrystal, 2, 4), new ItemStack(energyCrystal, 1, 1));
        addShapeless(ALL, new ItemStack(energyCrystal, 2, 5), new ItemStack(energyCrystal, 1, 2));

        addShapeless(ALL, energyCrystal, new ItemStack(energyCrystal, 1, 3), new ItemStack(energyCrystal, 1, 3));
        addShapeless(ALL, new ItemStack(energyCrystal, 1, 1), new ItemStack(energyCrystal, 1, 4), new ItemStack(energyCrystal, 1, 4));
        addShapeless(ALL, new ItemStack(energyCrystal, 1, 2), new ItemStack(energyCrystal, 1, 5), new ItemStack(energyCrystal, 1, 5));

        //Wireless

        addShaped(NORMAL, new ItemStack(energyCrystal, 1, 6), "ABA", "CDC", "ABA", 'A', ENDER_PEARL, 'B', new ItemStack(particleGenerator, 1, 0), 'C', ENDER_EYE, 'D', new ItemStack(energyCrystal, 1, 0));
        addShaped(HARD, new ItemStack(energyCrystal, 1, 6), "ABA", "CDC", "ABA", 'A', draconicCore, 'B', new ItemStack(particleGenerator, 1, 0), 'C', ENDER_EYE, 'D', new ItemStack(energyCrystal, 1, 0));

        addShaped(NORMAL, new ItemStack(energyCrystal, 1, 7), "ABA", "CDC", "ABA", 'A', ENDER_PEARL, 'B', new ItemStack(particleGenerator, 1, 0), 'C', ENDER_EYE, 'D', new ItemStack(energyCrystal, 1, 1));
        addShaped(HARD, new ItemStack(energyCrystal, 1, 7), "ABA", "CDC", "ABA", 'A', draconicCore, 'B', new ItemStack(particleGenerator, 1, 0), 'C', ENDER_EYE, 'D', new ItemStack(energyCrystal, 1, 1));

        addShaped(NORMAL, new ItemStack(energyCrystal, 1, 8), "ABA", "CDC", "ABA", 'A', ENDER_PEARL, 'B', new ItemStack(particleGenerator, 1, 0), 'C', ENDER_EYE, 'D', new ItemStack(energyCrystal, 1, 2));
        addShaped(HARD, new ItemStack(energyCrystal, 1, 8), "ABA", "CDC", "ABA", 'A', draconicCore, 'B', new ItemStack(particleGenerator, 1, 0), 'C', ENDER_EYE, 'D', new ItemStack(energyCrystal, 1, 2));

        //endregion

        //region Advanced Machines

        //Reactor
        addShaped(NORMAL, reactorPart, "AAA", "BC ", "AAA", 'A', "ingotIron", 'B', wyvernCore, 'C', "ingotDraconiumAwakened");
        addShaped(HARD, reactorPart, "AAA", "BC ", "AAA", 'A', "ingotIron", 'B', awakenedCore, 'C', "ingotDraconiumAwakened");

        addShaped(NORMAL, new ItemStack(reactorPart, 1, 1), "   ", "AAA", "BCC", 'A', "ingotDraconiumAwakened", 'B', draconicCore, 'C', "ingotDraconium");
        addShaped(HARD, new ItemStack(reactorPart, 1, 1), "   ", "AAA", "BCC", 'A', "ingotDraconiumAwakened", 'B', wyvernCore, 'C', "ingotDraconium");

        addShaped(NORMAL, new ItemStack(reactorPart, 1, 2), "   ", "AAA", "BCC", 'A', "gemDiamond", 'B', draconicCore, 'C', "ingotDraconium");
        addShaped(HARD, new ItemStack(reactorPart, 1, 2), "   ", "AAA", "BCC", 'A', "gemDiamond", 'B', wyvernCore, 'C', "ingotDraconium");

        addShaped(NORMAL, new ItemStack(reactorPart, 1, 3), " AB", "CDD", " AB", 'A', new ItemStack(reactorPart, 1, 1), 'B', new ItemStack(reactorPart, 1, 2), 'C', wyvernCore, 'D', "ingotDraconium");
        addShaped(HARD, new ItemStack(reactorPart, 1, 3), " AB", "CDD", " AB", 'A', new ItemStack(reactorPart, 1, 1), 'B', new ItemStack(reactorPart, 1, 2), 'C', awakenedCore, 'D', "ingotDraconiumAwakened");

        addShaped(NORMAL, new ItemStack(reactorPart, 1, 4), "ABA", "BCB", "ABA", 'A', "ingotGold", 'B', "gemDiamond", 'C', wyvernCore);
        addShaped(HARD, new ItemStack(reactorPart, 1, 4), "ABA", "BCB", "ABA", 'A', "ingotGold", 'B', "gemDiamond", 'C', awakenedCore);

        addFusion(NORMAL, new ItemStack(reactorCore), new ItemStack(chaosShard), 64000000, 3, "ingotDraconiumAwakened", "ingotDraconium", "ingotDraconiumAwakened", "ingotDraconium", "ingotDraconiumAwakened", "ingotDraconium", "ingotDraconiumAwakened");
        addFusion(HARD, new ItemStack(reactorCore), new ItemStack(chaosShard), 128000000, 3, "ingotDraconiumAwakened", "ingotDraconium", "blockDraconiumAwakened", "ingotDraconium", "blockDraconiumAwakened", "ingotDraconium", "ingotDraconiumAwakened");

        addFusion(NORMAL, new ItemStack(reactorComponent), new ItemStack(reactorPart), 16000000, 3, "ingotDraconiumAwakened", draconicEnergyCore, new ItemStack(reactorPart, 1, 3), new ItemStack(reactorPart, 1, 4), "ingotDraconiumAwakened", "ingotDraconiumAwakened", chaoticCore, "ingotDraconiumAwakened");
        addFusion(HARD, new ItemStack(reactorComponent), new ItemStack(reactorPart), 64000000, 3, "ingotDraconiumAwakened", new ItemStack(reactorPart, 1, 3), new ItemStack(reactorPart, 1, 4), "ingotDraconiumAwakened", draconicEnergyCore, "ingotDraconiumAwakened", awakenedCore, chaoticCore, awakenedCore, "ingotDraconiumAwakened", draconicEnergyCore);

        addFusion(NORMAL, new ItemStack(reactorComponent, 1, 1), new ItemStack(wyvernCore), 16000000, 3, "ingotDraconium", new ItemStack(reactorPart, 1, 1), new ItemStack(reactorPart, 1, 1), new ItemStack(reactorPart, 1, 1), "ingotDraconium", "ingotDraconium", "ingotIron", new ItemStack(reactorPart, 1, 1), "ingotIron", "ingotDraconium");
        addFusion(HARD, new ItemStack(reactorComponent, 1, 1), new ItemStack(awakenedCore), 64000000, 3, "ingotDraconium", new ItemStack(reactorPart, 1, 1), new ItemStack(reactorPart, 1, 1), new ItemStack(reactorPart, 1, 1), "ingotDraconium", "ingotDraconium", "ingotIron", new ItemStack(reactorPart, 1, 1), "ingotIron", "ingotDraconium");

        addFusion(NORMAL, new ItemStack(draconiumChest), new ItemStack(CHEST), 2000000, 0, FURNACE, draconicCore, FURNACE, draconicCore, FURNACE, "workbench", FURNACE, "blockDraconium", FURNACE, "workbench");
        addFusion(HARD, new ItemStack(draconiumChest), new ItemStack(CHEST), 10000000, 1, FURNACE, draconicCore, FURNACE, draconicCore, FURNACE, "workbench", FURNACE, "blockDraconium", FURNACE, "workbench");

        //endregion

        //region Exotic Blocks

        addFusion(NORMAL, new ItemStack(enderEnergyManipulator), new ItemStack(SKULL, 1, 1), 12000000, 1, ENDER_EYE, ENDER_EYE, ENDER_EYE, ENDER_EYE, ENDER_EYE, ENDER_EYE, draconicCore, wyvernCore, draconicCore, ENDER_EYE);
        addFusion(HARD, new ItemStack(enderEnergyManipulator), new ItemStack(SKULL, 1, 1), 32000000, 1, draconicCore, wyvernCore, ENDER_EYE, wyvernCore, draconicCore, ENDER_EYE, draconicCore, wyvernCore, draconicCore, ENDER_EYE);

        //endregion

	    /* ------------------ Items ------------------ */

        //region Crafting Components / Base items
        //Nuggets, Ingots, Blocks and Shards
        addShapeless(ALL, new ItemStack(nugget, 9), "ingotDraconium");                          //Ingots to Nuggets
        addShapeless(ALL, new ItemStack(nugget, 9, 1), "ingotDraconiumAwakened");
        addShaped(ALL, draconiumIngot, "AAA", "AAA", "AAA", 'A', "nuggetDraconium");            //Nuggets to Ingots
        addShaped(ALL, draconicIngot, "AAA", "AAA", "AAA", 'A', new ItemStack(nugget, 1, 1));
        addShaped(ALL, draconiumBlock, "AAA", "AAA", "AAA", 'A', "ingotDraconium");             //Ingots to Blocks
        addShaped(ALL, draconicBlock, "AAA", "AAA", "AAA", 'A', "ingotDraconiumAwakened");
        addShapeless(ALL, new ItemStack(draconiumIngot, 9), "blockDraconium");                  //Blocks to Ingots
        addShapeless(ALL, new ItemStack(draconicIngot, 9), "blockDraconiumAwakened");
        addShaped(ALL, new ItemStack(chaosShard, 1, 2), "AAA", "AAA", "AAA", 'A', new ItemStack(chaosShard, 1, 3));
        addShaped(ALL, new ItemStack(chaosShard, 1, 1), "AAA", "AAA", "AAA", 'A', new ItemStack(chaosShard, 1, 2));
        addShaped(ALL, chaosShard, "AAA", "AAA", "AAA", 'A', new ItemStack(chaosShard, 1, 1));
        //Cores
        addShaped(ALL, draconicCore, "ABA", "BCB", "ABA", 'A', "ingotDraconium", 'B', "ingotGold", 'C', "gemDiamond");
        //addShaped(HARD, draconicCore, "ABA", "BCB", "ABA", 'A', "ingotDraconium", 'B', "gemDiamond", 'C', "netherStar");
        addShaped(NORMAL, wyvernCore, "ABA", "BCB", "ABA", 'A', "ingotDraconium", 'B', draconicCore, 'C', "netherStar");
        addFusion(HARD, new ItemStack(wyvernCore), new ItemStack(EMERALD_BLOCK), 1000000, 0, draconicCore, draconicCore, "blockDraconium", "netherStar", draconicCore, "netherStar", "blockDraconium", draconicCore, draconicCore);
        addFusion(NORMAL, new ItemStack(awakenedCore), new ItemStack(NETHER_STAR), 1000000, 1, wyvernCore, wyvernCore, "ingotDraconiumAwakened", "ingotDraconiumAwakened", "ingotDraconiumAwakened", "ingotDraconiumAwakened", "ingotDraconiumAwakened", wyvernCore, wyvernCore);
        addFusion(HARD, new ItemStack(awakenedCore), new ItemStack(NETHER_STAR), 10000000, 1, wyvernCore, wyvernCore, "blockDraconiumAwakened", "blockDraconiumAwakened", wyvernCore, "blockDraconiumAwakened", "blockDraconiumAwakened", wyvernCore, wyvernCore);
        addFusion(NORMAL, new ItemStack(chaoticCore), new ItemStack(chaosShard), 100000000, 2, "ingotDraconiumAwakened", "ingotDraconiumAwakened", awakenedCore, awakenedCore, "ingotDraconiumAwakened", awakenedCore, awakenedCore, "ingotDraconiumAwakened");
        addFusion(HARD, new ItemStack(chaoticCore), new ItemStack(chaosShard), 100000000, 2, chaosShard, awakenedCore, "blockDraconiumAwakened", "blockDraconiumAwakened", "blockDraconiumAwakened", awakenedCore, chaosShard, chaosShard, awakenedCore, "blockDraconiumAwakened", "blockDraconiumAwakened", "blockDraconiumAwakened", awakenedCore, chaosShard);
        //energy Cores
        addShaped(NORMAL, wyvernEnergyCore, "ABA", "BCB", "ABA", 'A', "ingotDraconium", 'B', "blockRedstone", 'C', draconicCore);
        addShaped(HARD, wyvernEnergyCore, "ABA", "BCB", "ABA", 'A', "blockDraconium", 'B', "blockRedstone", 'C', draconicCore);
        addShaped(NORMAL, draconicEnergyCore, "ABA", "BCB", "ABA", 'A', "ingotDraconiumAwakened", 'B', wyvernEnergyCore, 'C', wyvernCore);
        addFusion(HARD, new ItemStack(draconicEnergyCore), new ItemStack(wyvernEnergyCore), 10000000, 2, "ingotDraconiumAwakened", "ingotDraconiumAwakened", awakenedCore, "ingotDraconiumAwakened", "ingotDraconiumAwakened", "blockRedstone", "blockRedstone", "blockRedstone", "blockRedstone", "blockRedstone");
        //endregion

        //region Tools
        //Wyvern
        addShaped(NORMAL, wyvernPick, " A ", "BCB", " D ", 'A', wyvernCore, 'B', "ingotDraconium", 'C', DIAMOND_PICKAXE, 'D', wyvernEnergyCore);
        addShaped(HARD, wyvernPick, "ABA", "CDC", "AEA", 'A', "netherStar", 'B', wyvernCore, 'C', "blockDraconium", 'D', DIAMOND_PICKAXE, 'E', wyvernEnergyCore);
        addShaped(NORMAL, wyvernShovel, " A ", "BCB", " D ", 'A', wyvernCore, 'B', "ingotDraconium", 'C', DIAMOND_SHOVEL, 'D', wyvernEnergyCore);
        addShaped(HARD, wyvernShovel, "ABA", "CDC", "AEA", 'A', "netherStar", 'B', wyvernCore, 'C', "blockDraconium", 'D', DIAMOND_SHOVEL, 'E', wyvernEnergyCore);
        addShaped(NORMAL, wyvernAxe, " A ", "BCB", " D ", 'A', wyvernCore, 'B', "ingotDraconium", 'C', DIAMOND_AXE, 'D', wyvernEnergyCore);
        addShaped(HARD, wyvernAxe, "ABA", "CDC", "AEA", 'A', "netherStar", 'B', wyvernCore, 'C', "blockDraconium", 'D', DIAMOND_AXE, 'E', wyvernEnergyCore);
        addShaped(NORMAL, wyvernBow, " A ", "BCB", " D ", 'A', wyvernCore, 'B', "ingotDraconium", 'C', BOW, 'D', wyvernEnergyCore);
        addShaped(HARD, wyvernBow, "ABA", "CDC", "AEA", 'A', "netherStar", 'B', wyvernCore, 'C', "blockDraconium", 'D', BOW, 'E', wyvernEnergyCore);
        addShaped(NORMAL, wyvernSword, " A ", "BCB", " D ", 'A', wyvernCore, 'B', "ingotDraconium", 'C', DIAMOND_SWORD, 'D', wyvernEnergyCore);
        addShaped(HARD, wyvernSword, "ABA", "CDC", "AEA", 'A', "netherStar", 'B', wyvernCore, 'C', "blockDraconium", 'D', DIAMOND_SWORD, 'E', wyvernEnergyCore);

        //Draconic
        addFusionTool(NORMAL, new ItemStack(draconicPick), new ItemStack(wyvernPick), 16000, 2, awakenedCore, "ingotDraconiumAwakened", draconicEnergyCore, "ingotDraconiumAwakened");
        addFusionTool(HARD, new ItemStack(draconicPick), new ItemStack(wyvernPick), 512000, 2, draconicEnergyCore, awakenedCore, "blockDraconiumAwakened", "blockDraconiumAwakened");
        addFusionTool(NORMAL, new ItemStack(draconicShovel), new ItemStack(wyvernShovel), 16000, 2, awakenedCore, "ingotDraconiumAwakened", draconicEnergyCore, "ingotDraconiumAwakened");
        addFusionTool(HARD, new ItemStack(draconicShovel), new ItemStack(wyvernShovel), 512000, 2, draconicEnergyCore, awakenedCore, "blockDraconiumAwakened", "blockDraconiumAwakened");
        addFusionTool(NORMAL, new ItemStack(draconicAxe), new ItemStack(wyvernAxe), 16000, 2, awakenedCore, "ingotDraconiumAwakened", draconicEnergyCore, "ingotDraconiumAwakened");
        addFusionTool(HARD, new ItemStack(draconicAxe), new ItemStack(wyvernAxe), 512000, 2, draconicEnergyCore, awakenedCore, "blockDraconiumAwakened", "blockDraconiumAwakened");
        addFusionTool(NORMAL, new ItemStack(draconicBow), new ItemStack(wyvernBow), 16000, 2, awakenedCore, "ingotDraconiumAwakened", draconicEnergyCore, "ingotDraconiumAwakened");
        addFusionTool(HARD, new ItemStack(draconicBow), new ItemStack(wyvernBow), 512000, 2, draconicEnergyCore, awakenedCore, "blockDraconiumAwakened", "blockDraconiumAwakened");
        addFusionTool(NORMAL, new ItemStack(draconicSword), new ItemStack(wyvernSword), 16000, 2, awakenedCore, "ingotDraconiumAwakened", draconicEnergyCore, "ingotDraconiumAwakened");
        addFusionTool(HARD, new ItemStack(draconicSword), new ItemStack(wyvernSword), 512000, 2, draconicEnergyCore, awakenedCore, "blockDraconiumAwakened", "blockDraconiumAwakened");
        addFusionTool(NORMAL, new ItemStack(draconicHoe), new ItemStack(DIAMOND_HOE), 16000, 2, awakenedCore, "ingotDraconiumAwakened", draconicEnergyCore, "ingotDraconiumAwakened");
        addFusionTool(HARD, new ItemStack(draconicHoe), new ItemStack(DIAMOND_HOE), 512000, 2, draconicEnergyCore, awakenedCore, "blockDraconiumAwakened", "blockDraconiumAwakened");
        addFusionTool(NORMAL, new ItemStack(draconicStaffOfPower), new ItemStack(draconicPick), 16000, 2, "ingotDraconiumAwakened", "ingotDraconiumAwakened", "ingotDraconiumAwakened", "ingotDraconiumAwakened", "ingotDraconiumAwakened", awakenedCore, draconicShovel, draconicSword);
        addFusionTool(HARD, new ItemStack(draconicStaffOfPower), new ItemStack(draconicPick), 512000, 2, "blockDraconiumAwakened", "blockDraconiumAwakened", "blockDraconiumAwakened", "blockDraconiumAwakened", "blockDraconiumAwakened", awakenedCore, draconicShovel, draconicSword);

        addShaped(NORMAL, draconiumCapacitor, "ABA", "BCB", "ABA", 'A', "ingotDraconium", 'B', wyvernEnergyCore, 'C', wyvernCore);
        addShaped(HARD, draconiumCapacitor, "ABA", "BCB", "ABA", 'A', "blockDraconium", 'B', wyvernEnergyCore, 'C', wyvernCore);
        addShaped(NORMAL, new ItemStack(draconiumCapacitor, 1, 1), "ABA", "CDC", "ACA", 'A', draconicEnergyCore, 'B', awakenedCore, 'C', "ingotDraconiumAwakened", 'D', new ItemStack(draconiumCapacitor, 1, 0));
        addShaped(HARD, new ItemStack(draconiumCapacitor, 1, 1), "ABA", "CDC", "ACA", 'A', draconicEnergyCore, 'B', awakenedCore, 'C', "blockDraconiumAwakened", 'D', new ItemStack(draconiumCapacitor, 1, 0));

        addShaped(NORMAL, dislocator, "ABA", "BCB", "ABA", 'A', BLAZE_POWDER, 'B', "dustDraconium", 'C', ENDER_EYE);
        addShaped(HARD, dislocator, "ABA", "BCB", "ABA", 'A', BLAZE_POWDER, 'B', "dustDraconium", 'C', CHORUS_FLOWER);

        addFusion(NORMAL, new ItemStack(dislocatorAdvanced), new ItemStack(dislocator), 1000000, 1, "enderpearl", "ingotDraconium", "enderpearl", "ingotDraconium", "enderpearl", "ingotDraconium", wyvernCore, "ingotDraconium");
        addFusion(HARD, new ItemStack(dislocatorAdvanced), new ItemStack(dislocator), 10000000, 2, "enderpearl", "ingotDraconium", "enderpearl", "ingotDraconium", "enderpearl", "ingotDraconium", "dragonEgg", "ingotDraconium");

        //Other
        addShaped(ALL, crystalBinder, " AB", " CA", "D  ", 'A', "ingotDraconium", 'B', "gemDiamond", 'C', BLAZE_ROD, 'D', draconicCore);

        //endregion

        //region Armor

        addShaped(NORMAL, wyvernHelm, "ABA", "ACA", "ADA", 'A', "ingotDraconium", 'B', wyvernCore, 'C', DIAMOND_HELMET, 'D', wyvernEnergyCore);
        addShaped(HARD, wyvernHelm, "ABA", "CDC", "AEA", 'A', "blockDraconium", 'B', wyvernCore, 'C', "netherStar", 'D', DIAMOND_HELMET, 'E', wyvernEnergyCore);
        addShaped(NORMAL, wyvernChest, "ABA", "ACA", "ADA", 'A', "ingotDraconium", 'B', wyvernCore, 'C', DIAMOND_CHESTPLATE, 'D', wyvernEnergyCore);
        addShaped(HARD, wyvernChest, "ABA", "CDC", "AEA", 'A', "blockDraconium", 'B', wyvernCore, 'C', "netherStar", 'D', DIAMOND_CHESTPLATE, 'E', wyvernEnergyCore);
        addShaped(NORMAL, wyvernLegs, "ABA", "ACA", "ADA", 'A', "ingotDraconium", 'B', wyvernCore, 'C', DIAMOND_LEGGINGS, 'D', wyvernEnergyCore);
        addShaped(HARD, wyvernLegs, "ABA", "CDC", "AEA", 'A', "blockDraconium", 'B', wyvernCore, 'C', "netherStar", 'D', DIAMOND_LEGGINGS, 'E', wyvernEnergyCore);
        addShaped(NORMAL, wyvernBoots, "ABA", "ACA", "ADA", 'A', "ingotDraconium", 'B', wyvernCore, 'C', DIAMOND_BOOTS, 'D', wyvernEnergyCore);
        addShaped(HARD, wyvernBoots, "ABA", "CDC", "AEA", 'A', "blockDraconium", 'B', wyvernCore, 'C', "netherStar", 'D', DIAMOND_BOOTS, 'E', wyvernEnergyCore);

        addFusionTool(NORMAL, new ItemStack(draconicHelm), new ItemStack(wyvernHelm), 320000, 2, "ingotDraconiumAwakened", awakenedCore, "ingotDraconiumAwakened", draconicEnergyCore);
        addFusionTool(HARD, new ItemStack(draconicHelm), new ItemStack(wyvernHelm), 5000000, 2, "blockDraconiumAwakened", awakenedCore, draconicEnergyCore);
        addFusionTool(NORMAL, new ItemStack(draconicChest), new ItemStack(wyvernChest), 320000, 2, "ingotDraconiumAwakened", awakenedCore, "ingotDraconiumAwakened", draconicEnergyCore);
        addFusionTool(HARD, new ItemStack(draconicChest), new ItemStack(wyvernChest), 5000000, 2, "blockDraconiumAwakened", awakenedCore, draconicEnergyCore);
        addFusionTool(NORMAL, new ItemStack(draconicLegs), new ItemStack(wyvernLegs), 320000, 2, "ingotDraconiumAwakened", awakenedCore, "ingotDraconiumAwakened", draconicEnergyCore);
        addFusionTool(HARD, new ItemStack(draconicLegs), new ItemStack(wyvernLegs), 5000000, 2, "blockDraconiumAwakened", awakenedCore, draconicEnergyCore);
        addFusionTool(NORMAL, new ItemStack(draconicBoots), new ItemStack(wyvernBoots), 320000, 2, "ingotDraconiumAwakened", awakenedCore, "ingotDraconiumAwakened", draconicEnergyCore);
        addFusionTool(HARD, new ItemStack(draconicBoots), new ItemStack(wyvernBoots), 5000000, 2, "blockDraconiumAwakened", awakenedCore, draconicEnergyCore);

        //endregion

        //region Upgrade Keys
        addShaped(ALL, getKey(ToolUpgrade.RF_CAPACITY), "ABA", "CDC", "ABA", 'A', new ItemStack(DYE, 1, 4), 'B', draconicCore, 'C', "ingotDraconium", 'D', wyvernEnergyCore);
        addShaped(ALL, getKey(ToolUpgrade.DIG_SPEED), "ABA", "CDC", "ABA", 'A', new ItemStack(DYE, 1, 4), 'B', draconicCore, 'C', "ingotDraconium", 'D', GOLDEN_PICKAXE);
        addShaped(ALL, getKey(ToolUpgrade.DIG_AOE), "ABA", "CDC", "ABA", 'A', new ItemStack(DYE, 1, 4), 'B', draconicCore, 'C', "ingotDraconium", 'D', "enderpearl");
        addShaped(ALL, getKey(ToolUpgrade.ATTACK_DAMAGE), "ABA", "CDC", "ABA", 'A', new ItemStack(DYE, 1, 4), 'B', draconicCore, 'C', "ingotDraconium", 'D', GOLDEN_SWORD);
        addShaped(ALL, getKey(ToolUpgrade.ATTACK_AOE), "ABA", "CDC", "ABA", 'A', new ItemStack(DYE, 1, 4), 'B', draconicCore, 'C', "ingotDraconium", 'D', DIAMOND_SWORD);
        addShaped(ALL, getKey(ToolUpgrade.ARROW_DAMAGE), "ABC", "DED", "ABA", 'A', new ItemStack(DYE, 1, 4), 'B', draconicCore, 'C', "ingotGold", 'D', "ingotDraconium", 'E', ARROW);
        addShaped(ALL, getKey(ToolUpgrade.DRAW_SPEED), "ABA", "CDC", "ABE", 'A', new ItemStack(DYE, 1, 4), 'B', draconicCore, 'C', "ingotDraconium", 'D', BOW, 'E', "ingotGold");
        addShaped(ALL, getKey(ToolUpgrade.ARROW_SPEED), "ABC", "DED", "ABA", 'A', new ItemStack(DYE, 1, 4), 'B', draconicCore, 'C', "feather", 'D', "ingotDraconium", 'E', ARROW);
        addShaped(ALL, getKey(ToolUpgrade.SHIELD_CAPACITY), "ABA", "CDC", "ABA", 'A', new ItemStack(DYE, 1, 4), 'B', draconicCore, 'C', "ingotDraconium", 'D', DIAMOND_CHESTPLATE);
        addShaped(ALL, getKey(ToolUpgrade.SHIELD_RECOVERY), "ABA", "CDC", "ABA", 'A', new ItemStack(DYE, 1, 4), 'B', draconicCore, 'C', "ingotDraconium", 'D', GOLDEN_CHESTPLATE);
        addShaped(ALL, getKey(ToolUpgrade.MOVE_SPEED), "ABA", "CDC", "AEA", 'A', new ItemStack(DYE, 1, 4), 'B', draconicCore, 'C', "ingotDraconium", 'D', GOLDEN_BOOTS, 'E', "blockRedstone");
        addShaped(ALL, getKey(ToolUpgrade.JUMP_BOOST), "ABA", "CDC", "AEA", 'A', new ItemStack(DYE, 1, 4), 'B', draconicCore, 'C', "ingotDraconium", 'D', GOLDEN_BOOTS, 'E', "blockSlime");
        //endregion

        //region Misc

        addShaped(ALL, infoTablet, "AAA", "ABA", "AAA", 'A', "stone", 'B', draconiumDust);
        addShaped(NORMAL, magnet, "A A", "B B", "CDC", 'A', "dustRedstone", 'B', "ingotDraconium", 'C', "ingotIron", 'D', dislocator);
        addShaped(HARD, magnet, "A A", "B B", "BCB", 'A', "blockRedstone", 'B', "ingotDraconium", 'C', dislocator);
        addShaped(NORMAL, new ItemStack(magnet, 1, 1), "A A", "B B", "CDC", 'A', "ingotDraconium", 'B', "dustRedstone", 'C', "ingotDraconiumAwakened", 'D', magnet);
        addShaped(HARD, new ItemStack(magnet, 1, 1), "A A", "BCB", "DED", 'A', "ingotDraconium", 'B', "dustRedstone", 'C', magnet, 'D', "ingotDraconiumAwakened", 'E', dislocatorAdvanced);

        //endregion

        //region Exotic Items

        //endregion

        /* ------------------ Other ------------------ */
        if (RecipeManager.isEnabled(DEFeatures.draconiumDust) && RecipeManager.isEnabled(DEFeatures.draconiumIngot)) {
            GameRegistry.addSmelting(DEFeatures.draconiumDust, new ItemStack(DEFeatures.draconiumIngot), 0);
        }

        if (RecipeManager.isEnabled(DEFeatures.draconiumOre) && RecipeManager.isEnabled(DEFeatures.draconicIngot)) {
            GameRegistry.addSmelting(DEFeatures.draconiumOre, new ItemStack(DEFeatures.draconiumIngot), 0);
        }

        RecipeManager.addRecipe(new RecipeDislocatorClone().setRegistryName(new ResourceLocation("draconicevolution:recipe_dislocator_clone")));

        Item borkedSpawner = Item.REGISTRY.getObject(new ResourceLocation("enderio:itemBrokenSpawner"));
        if (borkedSpawner != null) {
            RecipeManager.addRecipe(new RecipeEIOStabilization(borkedSpawner).setRegistryName(new ResourceLocation("draconicevolution:eio_spawner")));
        }
    }

    //region Helpers

    public static ItemStack getKey(String name) {
        return new ItemStack(DEFeatures.toolUpgrade, 1, ToolUpgrade.NAME_TO_ID.get(name));
    }

    public static void addUpgradeKey(ItemStack input, String name) {
        addShaped(ALL, new ItemStack(DEFeatures.toolUpgrade, 1, ToolUpgrade.NAME_TO_ID.get(name)), "ABA", "CDC", "ABA", 'A', new ItemStack(DYE, 1, 4), 'B', draconicCore, 'C', "ingotDraconium", 'D', input);
    }


    //endregion
}
