package com.brandon3055.draconicevolution.common.handler;

import com.brandon3055.brandonscore.common.utills.ItemNBTHelper;
import com.brandon3055.draconicevolution.common.ModBlocks;
import com.brandon3055.draconicevolution.common.ModItems;
import com.brandon3055.draconicevolution.common.utills.LogHelper;
import com.brandon3055.draconicevolution.common.utills.ShapedOreEnergyRecipe;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;

public class CraftingHandler {
    public static void init() {
        // todo In 1.8-1.9 when the feature modals are implemented add a dependency system to automatically disable a
        // recipe if a required component is disabled

        ItemStack mobSoul = new ItemStack(ModItems.mobSoul);
        ItemNBTHelper.setString(mobSoul, "Name", "Any");

        // Components Key[C=Corner, S=Side, M=Middle ]
        addOre(
                ModItems.draconicCore,
                "CSC",
                "SMS",
                "CSC",
                'C',
                "ingotGold",
                'S',
                ModItems.draconiumIngot,
                'M',
                "gemDiamond");
        add(
                ModItems.wyvernCore,
                "CSC",
                "SMS",
                "CSC",
                'C',
                ModItems.draconiumIngot,
                'S',
                ModItems.draconicCore,
                'M',
                Items.nether_star);
        addOre(ModItems.awakenedCore, "CSC", "SCS", "CSC", 'C', "ingotDraconiumAwakened", 'S', ModItems.wyvernCore);
        addOre(
                ModItems.chaoticCore,
                "ACA",
                "CSC",
                "ACA",
                'A',
                "ingotDraconiumAwakened",
                'C',
                ModItems.awakenedCore,
                'S',
                ModItems.chaosShard);
        add(
                ModItems.wyvernEnergyCore,
                "CSC",
                "SMS",
                "CSC",
                'C',
                ModItems.draconiumIngot,
                'S',
                Blocks.redstone_block,
                'M',
                ModItems.draconicCore);
        addOre(
                ModItems.draconicEnergyCore,
                "CSC",
                "SMS",
                "CSC",
                'C',
                "ingotDraconiumAwakened",
                'S',
                ModItems.wyvernEnergyCore,
                'M',
                ModItems.wyvernCore);
        // addOre(ModItems.draconiumBlend, " D ", "DID", " D ", 'I', "ingotIron", 'D', ModItems.draconiumDust);

        addShaplessOre(getStack(ModItems.draconicIngot, 9, 0), "blockDraconiumAwakened");
        addShaplessOre(getStack(ModItems.draconiumIngot, 9, 0), "blockDraconium");
        addShaplessOre(getStack(ModItems.nugget, 9, 0), "ingotDraconium");
        addShaplessOre(getStack(ModItems.nugget, 9, 1), "ingotDraconiumAwakened");
        addOre(ModItems.draconiumIngot, "III", "III", "III", 'I', "nuggetDraconium");
        addOre(ModItems.draconicIngot, "III", "III", "III", 'I', "nuggetDraconiumAwakened");

        add(getStack(ModItems.chaosFragment, 1, 1), "III", "III", "III", 'I', getStack(ModItems.chaosFragment, 1, 0));
        add(getStack(ModItems.chaosFragment, 1, 2), "III", "III", "III", 'I', getStack(ModItems.chaosFragment, 1, 1));
        add(ModItems.chaosShard, "III", "III", "III", 'I', getStack(ModItems.chaosFragment, 1, 2));

        if (ModItems.isEnabled(ModItems.draconiumIngot) && ModItems.isEnabled(ModItems.draconiumBlend))
            GameRegistry.addSmelting(ModItems.draconiumBlend, getStack(ModItems.draconiumIngot, 2, 0), 1.0f);
        if (ModItems.isEnabled(ModItems.draconiumIngot) && ModItems.isEnabled(ModItems.draconiumDust))
            GameRegistry.addSmelting(
                    new ItemStack(ModItems.draconiumDust), getStack(ModItems.draconiumIngot, 1, 0), 1.0f);

        // Reactor
        addOre(
                ModItems.partStabFrame,
                "III",
                "CD ",
                "III",
                'I',
                "ingotIron",
                'C',
                ModItems.wyvernCore,
                'D',
                "ingotDraconiumAwakened");
        addOre(
                ModItems.partStabRotorInner,
                "   ",
                "III",
                "CWW",
                'I',
                "ingotDraconiumAwakened",
                'W',
                "ingotDraconium",
                'C',
                ModItems.draconicCore);
        addOre(
                ModItems.partStabRotorOuter,
                "   ",
                "III",
                "CWW",
                'I',
                "gemDiamond",
                'W',
                "ingotDraconium",
                'C',
                ModItems.draconicCore);
        addOre(
                ModItems.partStabRotorAssembly,
                " IO",
                "CWW",
                " IO",
                'I',
                ModItems.partStabRotorInner,
                'O',
                ModItems.partStabRotorOuter,
                'C',
                ModItems.wyvernCore,
                'W',
                "ingotDraconium");
        addOre(
                ModItems.partStabRing,
                "GDG",
                "DCD",
                "GDG",
                'G',
                "ingotGold",
                'D',
                "gemDiamond",
                'C',
                ModItems.wyvernCore);
        addOre(
                ModBlocks.reactorStabilizer,
                "ICI",
                "FSR",
                "IEI",
                'I',
                "ingotDraconiumAwakened",
                'C',
                ModItems.chaoticCore,
                'F',
                ModItems.partStabFrame,
                'S',
                ModItems.partStabRotorAssembly,
                'R',
                ModItems.partStabRing,
                'E',
                ModItems.draconicEnergyCore);
        addOre(
                ModBlocks.reactorEnergyInjector,
                "IRI",
                "RCR",
                "IRI",
                'I',
                "ingotDraconium",
                'R',
                ModItems.partStabRotorInner,
                'C',
                ModItems.wyvernCore);
        addOre(ModBlocks.reactorCore, " I ", "ISI", " I ", 'I', "ingotDraconiumAwakened", 'S', ModItems.chaosShard);

        // Wyvern tools
        add(
                ModItems.wyvernFluxCapacitor,
                "CSC",
                "SMS",
                "CSC",
                'C',
                ModItems.draconiumIngot,
                'S',
                ModItems.wyvernEnergyCore,
                'M',
                ModItems.wyvernCore);
        // tool
        add(
                ModItems.wyvernPickaxe,
                " W ",
                "ITI",
                " E ",
                'W',
                ModItems.wyvernCore,
                'I',
                ModItems.draconiumIngot,
                'T',
                Items.diamond_pickaxe,
                'E',
                ModItems.wyvernEnergyCore);
        add(
                ModItems.wyvernShovel,
                " W ",
                "ITI",
                " E ",
                'W',
                ModItems.wyvernCore,
                'I',
                ModItems.draconiumIngot,
                'T',
                Items.diamond_shovel,
                'E',
                ModItems.wyvernEnergyCore);
        add(
                ModItems.wyvernSword,
                " W ",
                "ITI",
                " E ",
                'W',
                ModItems.wyvernCore,
                'I',
                ModItems.draconiumIngot,
                'T',
                Items.diamond_sword,
                'E',
                ModItems.wyvernEnergyCore);
        add(
                ModItems.wyvernBow,
                " W ",
                "ITI",
                " E ",
                'W',
                ModItems.wyvernCore,
                'I',
                ModItems.draconiumIngot,
                'T',
                Items.bow,
                'E',
                ModItems.wyvernEnergyCore);
        addOre(
                new ItemStack(ModItems.magnet, 1, 0),
                "RII",
                "  C",
                "RII",
                'R',
                Blocks.redstone_block,
                'I',
                "ingotIron",
                'C',
                ModItems.teleporterMKI);

        // armor
        add(
                ModItems.wyvernHelm,
                "IWI",
                "IAI",
                "IEI",
                'W',
                ModItems.wyvernCore,
                'I',
                ModItems.draconiumIngot,
                'A',
                Items.diamond_helmet,
                'E',
                ModItems.wyvernEnergyCore);
        add(
                ModItems.wyvernChest,
                "IWI",
                "IAI",
                "IEI",
                'W',
                ModItems.wyvernCore,
                'I',
                ModItems.draconiumIngot,
                'A',
                Items.diamond_chestplate,
                'E',
                ModItems.wyvernEnergyCore);
        add(
                ModItems.wyvernLeggs,
                "IWI",
                "IAI",
                "IEI",
                'W',
                ModItems.wyvernCore,
                'I',
                ModItems.draconiumIngot,
                'A',
                Items.diamond_leggings,
                'E',
                ModItems.wyvernEnergyCore);
        add(
                ModItems.wyvernBoots,
                "IWI",
                "IAI",
                "IEI",
                'W',
                ModItems.wyvernCore,
                'I',
                ModItems.draconiumIngot,
                'A',
                Items.diamond_boots,
                'E',
                ModItems.wyvernEnergyCore);

        // Draconic Tools
        addEnergy(
                ModItems.draconicFluxCapacitor,
                "CMC",
                "SPS",
                "CSC",
                'C',
                "ingotDraconiumAwakened",
                'S',
                ModItems.draconicEnergyCore,
                'M',
                ModItems.awakenedCore,
                'P',
                ModItems.wyvernFluxCapacitor);
        // tools
        addEnergy(
                ModItems.draconicPickaxe,
                " C ",
                "ITI",
                " E ",
                'C',
                ModItems.awakenedCore,
                'I',
                "ingotDraconiumAwakened",
                'T',
                ModItems.wyvernPickaxe,
                'E',
                ModItems.draconicEnergyCore);
        addEnergy(
                ModItems.draconicShovel,
                " C ",
                "ITI",
                " E ",
                'C',
                ModItems.awakenedCore,
                'I',
                "ingotDraconiumAwakened",
                'T',
                ModItems.wyvernShovel,
                'E',
                ModItems.draconicEnergyCore);
        addOre(
                ModItems.draconicAxe,
                " C ",
                "ITI",
                " E ",
                'C',
                ModItems.awakenedCore,
                'I',
                "ingotDraconiumAwakened",
                'T',
                Items.diamond_axe,
                'E',
                ModItems.draconicEnergyCore);
        addOre(
                ModItems.draconicHoe,
                " C ",
                "ITI",
                " E ",
                'C',
                ModItems.awakenedCore,
                'I',
                "ingotDraconiumAwakened",
                'T',
                Items.diamond_hoe,
                'E',
                ModItems.draconicEnergyCore);
        addEnergy(
                ModItems.draconicSword,
                " C ",
                "ITI",
                " E ",
                'C',
                ModItems.awakenedCore,
                'I',
                "ingotDraconiumAwakened",
                'T',
                ModItems.wyvernSword,
                'E',
                ModItems.draconicEnergyCore);
        addOre(
                ModItems.draconicBow,
                " C ",
                "ITI",
                " E ",
                'C',
                ModItems.awakenedCore,
                'I',
                "ingotDraconiumAwakened",
                'T',
                ModItems.wyvernBow,
                'E',
                ModItems.draconicEnergyCore);
        addEnergy(
                ModItems.draconicDestructionStaff,
                "IAI",
                "PIS",
                "IWI",
                'I',
                "ingotDraconiumAwakened",
                'A',
                ModItems.awakenedCore,
                'P',
                ModItems.draconicPickaxe,
                'S',
                ModItems.draconicShovel,
                'W',
                ModItems.draconicSword);
        addOre(
                new ItemStack(ModItems.magnet, 1, 1),
                "RII",
                "  C",
                "RII",
                'R',
                new ItemStack(ModBlocks.draconiumBlock, 1, 2),
                'I',
                "ingotDraconiumAwakened",
                'C',
                new ItemStack(ModItems.magnet, 1, 0));

        // armor
        addEnergy(
                ModItems.draconicHelm,
                "IWI",
                "IAI",
                "IEI",
                'W',
                ModItems.awakenedCore,
                'I',
                "ingotDraconiumAwakened",
                'A',
                ModItems.wyvernHelm,
                'E',
                ModItems.draconicEnergyCore);
        addEnergy(
                ModItems.draconicChest,
                "IWI",
                "IAI",
                "IEI",
                'W',
                ModItems.awakenedCore,
                'I',
                "ingotDraconiumAwakened",
                'A',
                ModItems.wyvernChest,
                'E',
                ModItems.draconicEnergyCore);
        addEnergy(
                ModItems.draconicLeggs,
                "IWI",
                "IAI",
                "IEI",
                'W',
                ModItems.awakenedCore,
                'I',
                "ingotDraconiumAwakened",
                'A',
                ModItems.wyvernLeggs,
                'E',
                ModItems.draconicEnergyCore);
        addEnergy(
                ModItems.draconicBoots,
                "IWI",
                "IAI",
                "IEI",
                'W',
                ModItems.awakenedCore,
                'I',
                "ingotDraconiumAwakened",
                'A',
                ModItems.wyvernBoots,
                'E',
                ModItems.draconicEnergyCore);

        // Blocks
        // storage
        addOre(ModBlocks.draconiumBlock, "III", "III", "III", 'I', "ingotDraconium");
        addOre(ModBlocks.draconicBlock, "III", "III", "III", 'I', "ingotDraconiumAwakened");

        // aesthetic
        add(
                ModBlocks.particleGenerator,
                "RBR",
                "BCB",
                "RBR",
                'R',
                Blocks.redstone_block,
                'B',
                Items.blaze_rod,
                'C',
                ModItems.draconicCore);
        addOre(
                new ItemStack(ModBlocks.infusedObsidian, 4),
                "BOB",
                "ODO",
                "BOB",
                'B',
                Items.blaze_powder,
                'O',
                Blocks.obsidian,
                'D',
                "dustDraconium");

        // machines
        addOre(
                ModBlocks.potentiometer,
                "ITI",
                "QCQ",
                "IRI",
                'I',
                "ingotIron",
                'T',
                Blocks.redstone_torch,
                'Q',
                "gemQuartz",
                'C',
                Items.comparator,
                'R',
                Blocks.redstone_block);
        addOre(
                ModBlocks.rainSensor,
                "   ",
                "RBR",
                "SPS",
                'R',
                "dustRedstone",
                'B',
                Items.bucket,
                'S',
                Blocks.stone_slab,
                'P',
                Blocks.heavy_weighted_pressure_plate);
        addOre(
                ModBlocks.teleporterStand,
                " P ",
                " S ",
                "HBH",
                'P',
                Blocks.stone_pressure_plate,
                'S',
                "stone",
                'H',
                new ItemStack(Blocks.stone_slab, 1, 0),
                'B',
                Items.blaze_powder);
        addOre(
                ModBlocks.dislocatorReceptacle,
                "ICI",
                " O ",
                "ISI",
                'I',
                "ingotIron",
                'C',
                ModItems.draconicCore,
                'O',
                ModBlocks.infusedObsidian,
                'S',
                ModBlocks.teleporterStand);
        addOre(
                ModBlocks.upgradeModifier,
                "   ",
                "DCD",
                "III",
                'I',
                "ingotIron",
                'D',
                "ingotDraconium",
                'C',
                ModItems.draconicCore);

        // machines adv
        addOre(
                ModBlocks.resurrectionStone,
                "CSC",
                "SMS",
                "CSC",
                'C',
                mobSoul,
                'S',
                ModItems.wyvernCore,
                'M',
                "blockDraconium");
        addOre(
                ModBlocks.energyStorageCore,
                "CCC",
                "SMS",
                "CCC",
                'C',
                "ingotDraconium",
                'S',
                ModItems.wyvernEnergyCore,
                'M',
                ModItems.wyvernCore);
        addOre(
                ModBlocks.weatherController,
                "RIR",
                "TPT",
                "IEI",
                'R',
                Items.blaze_rod,
                'T',
                Blocks.tnt,
                'P',
                ModItems.draconicCore,
                'I',
                "ingotDraconium",
                'E',
                Blocks.enchanting_table);
        addOre(
                ModBlocks.playerDetectorAdvanced,
                "ISI",
                "EDE",
                "ICI",
                'I',
                "ingotDraconium",
                'E',
                Items.ender_eye,
                'S',
                new ItemStack(Items.skull, 1, 1),
                'C',
                Items.compass,
                'D',
                ModBlocks.playerDetector);
        addOre(
                ModBlocks.energyInfuser,
                "IPI",
                "CEC",
                "ICI",
                'I',
                "ingotDraconium",
                'P',
                ModBlocks.particleGenerator,
                'C',
                ModItems.draconicCore,
                'E',
                Blocks.enchanting_table);
        addOre(
                ModBlocks.draconiumChest,
                "IFI",
                "SCS",
                "IWI",
                'I',
                "ingotDraconium",
                'C',
                ModItems.draconicCore,
                'S',
                getChest(),
                'W',
                Blocks.crafting_table,
                'F',
                Blocks.furnace);
        addOre(
                getStack(ModBlocks.energyPylon, 2, 0),
                "IEI",
                "MCM",
                "IDI",
                'I',
                "ingotDraconium",
                'E',
                Items.ender_eye,
                'C',
                ModItems.draconicCore,
                'D',
                "gemDiamond",
                'M',
                "gemEmerald");
        addOre(
                getStack(ModBlocks.grinder, 1, 3),
                "IXI",
                "DCD",
                "IFI",
                'I',
                "ingotIron",
                'X',
                "ingotDraconium",
                'D',
                Items.diamond_sword,
                'C',
                ModItems.draconicCore,
                'F',
                Blocks.furnace);
        addOre(
                ModBlocks.playerDetector,
                "ITI",
                "CEC",
                "IDI",
                'I',
                "ingotIron",
                'E',
                Items.ender_eye,
                'T',
                Blocks.redstone_torch,
                'C',
                Items.comparator,
                'D',
                ModItems.draconicCore);
        addOre(
                getStack(ModBlocks.generator, 1, 3),
                "NIN",
                "IFI",
                "NCN",
                'N',
                Items.netherbrick,
                'I',
                "ingotIron",
                'F',
                Blocks.furnace,
                'C',
                ModItems.draconicCore);
        addOre(
                ModBlocks.dissEnchanter,
                "PIP",
                "ETE",
                "CBC",
                'P',
                Items.ender_eye,
                'I',
                Items.enchanted_book,
                'E',
                "gemEmerald",
                'T',
                Blocks.enchanting_table,
                'C',
                ModItems.draconicCore,
                'B',
                Items.book);
        addOre(
                getStack(ModBlocks.energyCrystal, 4, 0),
                "IDI",
                "DCD",
                "IDI",
                'I',
                "ingotDraconium",
                'D',
                "gemDiamond",
                'C',
                ModItems.draconicCore);
        addOre(
                getStack(ModBlocks.energyCrystal, 4, 1),
                "CRC",
                "RWR",
                "CRC",
                'R',
                getStack(ModBlocks.energyCrystal, 1, 0),
                'W',
                ModItems.wyvernCore,
                'C',
                ModItems.draconicCore);
        addShaplessOre(
                getStack(ModBlocks.energyCrystal, 1, 0),
                getStack(ModBlocks.energyCrystal, 1, 2),
                getStack(ModBlocks.energyCrystal, 1, 2));
        addShaplessOre(getStack(ModBlocks.energyCrystal, 2, 2), getStack(ModBlocks.energyCrystal, 1, 0));
        addShaplessOre(
                getStack(ModBlocks.energyCrystal, 1, 1),
                getStack(ModBlocks.energyCrystal, 1, 3),
                getStack(ModBlocks.energyCrystal, 1, 3));
        addShaplessOre(getStack(ModBlocks.energyCrystal, 2, 3), getStack(ModBlocks.energyCrystal, 1, 1));
        addOre(
                getStack(ModBlocks.energyCrystal, 1, 4),
                "PGP",
                "ECE",
                "PGP",
                'P',
                Items.ender_pearl,
                'G',
                ModBlocks.particleGenerator,
                'E',
                Items.ender_eye,
                'C',
                getStack(ModBlocks.energyCrystal, 1, 0));
        addOre(
                getStack(ModBlocks.energyCrystal, 1, 5),
                "PGP",
                "ECE",
                "PGP",
                'P',
                Items.ender_pearl,
                'G',
                ModBlocks.particleGenerator,
                'E',
                Items.ender_eye,
                'C',
                getStack(ModBlocks.energyCrystal, 1, 1));

        if (Loader.isModLoaded("ThermalDynamics")) {
            addOre(
                    getStack(ModBlocks.flowGate, 1, 0),
                    "ICI",
                    "RSR",
                    "ICI",
                    'I',
                    "ingotDraconium",
                    'C',
                    getStack(ModItems.draconicCore, 1, 0),
                    'R',
                    Items.comparator,
                    'S',
                    new ItemStack(GameRegistry.findBlock("ThermalDynamics", "ThermalDynamics_0"), 1, 1));
            addOre(
                    getStack(ModBlocks.flowGate, 1, 6),
                    "ICI",
                    "RSR",
                    "ICI",
                    'I',
                    "ingotDraconium",
                    'C',
                    getStack(ModItems.draconicCore, 1, 0),
                    'R',
                    Items.comparator,
                    'S',
                    new ItemStack(GameRegistry.findBlock("ThermalDynamics", "ThermalDynamics_16"), 1, 2));
        } else {
            addOre(
                    getStack(ModBlocks.flowGate, 1, 0),
                    "ICI",
                    "RSR",
                    "ICI",
                    'I',
                    "ingotDraconium",
                    'C',
                    getStack(ModItems.draconicCore, 1, 0),
                    'R',
                    Items.comparator,
                    'S',
                    getStack(ModItems.draconiumEnergyCore, 1, 0));
            addOre(
                    getStack(ModBlocks.flowGate, 1, 6),
                    "ICI",
                    "RSR",
                    "ICI",
                    'I',
                    "ingotDraconium",
                    'C',
                    getStack(ModItems.draconicCore, 1, 0),
                    'R',
                    Items.comparator,
                    'S',
                    Items.bucket);
        }

        // Tools
        addOre(
                ModItems.teleporterMKII,
                "IEI",
                "ETE",
                "IWI",
                'I',
                "ingotDraconium",
                'E',
                Items.ender_pearl,
                'T',
                ModItems.teleporterMKI,
                'W',
                ModItems.wyvernCore);
        addOre(
                ModItems.teleporterMKI,
                "CSC",
                "SMS",
                "CSC",
                'C',
                Items.blaze_powder,
                'S',
                "dustDraconium",
                'M',
                Items.ender_eye);
        addOre(getStack(ModItems.safetyMatch, 1, 1000), " O ", " S ", "   ", 'O', "dyeOrange", 'S', "stickWood");
        add(ModItems.safetyMatch, "MMM", "MMM", "MMM", 'M', getStack(ModItems.safetyMatch, 1, 1000));
        addOre(
                ModItems.wrench,
                " ID",
                " RI",
                "C  ",
                'I',
                "ingotDraconium",
                'D',
                "gemDiamond",
                'R',
                Items.blaze_rod,
                'C',
                ModItems.draconicCore);

        // Other
        addOre(ModItems.infoTablet, "SSS", "SDS", "SSS", 'S', "stone", 'D', "dustDraconium");
        addShaplessOre(getStack(ModItems.enderArrow, 1, 0), Items.arrow, Items.ender_pearl);
        addShaplessOre(
                new ItemStack(Blocks.dirt),
                Item.getItemFromBlock(Blocks.sand),
                Items.rotten_flesh,
                "treeSapling",
                "treeSapling",
                "treeSapling");
        addShaplessOre(
                new ItemStack(Blocks.dirt),
                Item.getItemFromBlock(Blocks.sand),
                Items.rotten_flesh,
                "treeLeaves",
                "treeLeaves",
                "treeLeaves");

        // Disable able
        // if(ConfigHandler.disableSunDial == 0)
        addOre(
                ModBlocks.sunDial,
                "IAI",
                "CDC",
                "IEI",
                'I',
                "ingotDraconium",
                'A',
                ModItems.awakenedCore,
                'C',
                ModItems.draconicCore,
                'E',
                Blocks.enchanting_table,
                'D',
                Blocks.dragon_egg);
        // if(ConfigHandler.disableXrayBlock == 0)
        addOre(
                getStack(ModBlocks.xRayBlock, 4, 0),
                "SGS",
                "GDG",
                "SGS",
                'S',
                Items.nether_star,
                'G',
                "blockGlassColorless",
                'D',
                "gemDiamond");
    }

    private static ItemStack getChest() {
        if (Loader.isModLoaded("IronChest")) {
            LogHelper.info("Adding Iron Chests Integration");
            return new ItemStack(GameRegistry.findBlock("IronChest", "BlockIronChest"), 1, 6);
        } else LogHelper.info("Iron Chests was not detected! using fallback chest recipe");
        return new ItemStack(Blocks.chest);
    }

    private static void addOre(Block result, Object... recipe) {
        addOre(new ItemStack(result), recipe);
    }

    private static void addOre(Item result, Object... recipe) {
        addOre(new ItemStack(result), recipe);
    }

    private static void addOre(ItemStack result, Object... recipe) {
        if (result == null) return;
        for (Object o : recipe) {
            if (o == null) return;
            String s = o instanceof Item
                    ? ((Item) o).getUnlocalizedName()
                    : o instanceof Block ? ((Block) o).getUnlocalizedName() : null;
            if (s != null && ConfigHandler.disabledNamesList.contains(s)) return;
        }

        GameRegistry.addRecipe(new ShapedOreRecipe(result, recipe));
    }

    private static void addShaplessOre(ItemStack result, Object... recipe) {
        if (result == null) return;
        for (Object o : recipe) {
            if (o == null) return;
            String s = o instanceof Item
                    ? ((Item) o).getUnlocalizedName()
                    : o instanceof Block ? ((Block) o).getUnlocalizedName() : null;
            if (s != null && ConfigHandler.disabledNamesList.contains(s)) return;
        }

        GameRegistry.addRecipe(new ShapelessOreRecipe(result, recipe));
    }

    private static void add(Block result, Object... recipe) {
        add(new ItemStack(result), recipe);
    }

    private static void add(Item result, Object... recipe) {
        add(new ItemStack(result), recipe);
    }

    private static void add(ItemStack result, Object... recipe) {
        if (result == null) return;
        for (Object o : recipe) {
            if (o == null) return;
            String s = o instanceof Item
                    ? ((Item) o).getUnlocalizedName()
                    : o instanceof Block ? ((Block) o).getUnlocalizedName() : null;
            if (s != null && ConfigHandler.disabledNamesList.contains(s)) return;
        }

        GameRegistry.addRecipe(result, recipe);
    }

    private static void addEnergy(Block result, Object... recipe) {
        addEnergy(new ItemStack(result), recipe);
    }

    private static void addEnergy(Item result, Object... recipe) {
        addEnergy(new ItemStack(result), recipe);
    }

    private static void addEnergy(ItemStack result, Object... recipe) {
        if (result == null) return;
        for (Object o : recipe) {
            if (o == null) return;
            String s = o instanceof Item
                    ? ((Item) o).getUnlocalizedName()
                    : o instanceof Block ? ((Block) o).getUnlocalizedName() : null;
            if (s != null && ConfigHandler.disabledNamesList.contains(s)) return;
        }

        GameRegistry.addRecipe(new ShapedOreEnergyRecipe(result, recipe));
    }

    private static ItemStack getStack(Block block, int count, int meta) {
        return ModBlocks.isEnabled(block) ? new ItemStack(block, count, meta) : null;
    }

    private static ItemStack getStack(Item item, int count, int meta) {
        return ModItems.isEnabled(item) ? new ItemStack(item, count, meta) : null;
    }
}
