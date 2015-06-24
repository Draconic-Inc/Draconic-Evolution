package com.brandon3055.draconicevolution.common.handler;

import com.brandon3055.draconicevolution.common.ModBlocks;
import com.brandon3055.draconicevolution.common.ModItems;
import com.brandon3055.brandonscore.common.utills.ItemNBTHelper;
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
	public static void init()
	{
		ItemStack mobSoul = new ItemStack(ModItems.mobSoul);
		ItemNBTHelper.setString(mobSoul, "Name", "Any");

	//Components Key[C=Corner, S=Side, M=Middle ]
		addOre(ModItems.draconicCore, "CSC", "SMS", "CSC", 'C', "ingotGold", 'S', ModItems.draconiumIngot, 'M', "gemDiamond");
		add(ModItems.wyvernCore, "CSC", "SMS", "CSC", 'C', ModItems.draconiumIngot, 'S', ModItems.draconicCore, 'M', Items.nether_star);
		add(ModItems.awakenedCore, "CSC", "SCS", "CSC", 'C', ModItems.draconicIngot, 'S', ModItems.wyvernCore);
		add(ModItems.wyvernEnergyCore, "CSC", "SMS", "CSC", 'C', ModItems.draconiumIngot, 'S', Blocks.redstone_block, 'M', ModItems.draconicCore);
		add(ModItems.draconicEnergyCore, "CSC", "SMS", "CSC", 'C', ModItems.draconicIngot, 'S', ModItems.wyvernEnergyCore, 'M', ModItems.wyvernCore);
		//addOre(ModItems.draconiumBlend, " D ", "DID", " D ", 'I', "ingotIron", 'D', ModItems.draconiumDust);

		addShaplessOre(getStack(ModItems.draconicIngot, 9, 0), Item.getItemFromBlock(ModBlocks.draconicBlock));
		addShaplessOre(getStack(ModItems.draconiumIngot, 9, 0), Item.getItemFromBlock(ModBlocks.draconiumBlock));
		if (ModItems.isEnabled(ModItems.draconiumIngot) && ModItems.isEnabled(ModItems.draconiumBlend)) GameRegistry.addSmelting(ModItems.draconiumBlend, getStack(ModItems.draconiumIngot, 2, 0), 1.0f);
		if (ModItems.isEnabled(ModItems.draconiumIngot) && ModItems.isEnabled(ModItems.draconiumDust)) GameRegistry.addSmelting(ModItems.draconiumDust, getStack(ModItems.draconiumIngot, 1, 0), 1.0f);


	//Wyvern tools
		add(ModItems.wyvernFluxCapacitor, "CSC", "SMS", "CSC", 'C', ModItems.draconiumIngot, 'S', ModItems.wyvernEnergyCore, 'M', ModItems.wyvernCore);
		//tool
		add(ModItems.wyvernPickaxe, " W ", "ITI", " E ", 'W', ModItems.wyvernCore, 'I', ModItems.draconiumIngot, 'T', Items.diamond_pickaxe, 'E', ModItems.wyvernEnergyCore);
		add(ModItems.wyvernShovel, " W ", "ITI", " E ", 'W', ModItems.wyvernCore, 'I', ModItems.draconiumIngot, 'T', Items.diamond_shovel, 'E', ModItems.wyvernEnergyCore);
		add(ModItems.wyvernSword, " W ", "ITI", " E ", 'W', ModItems.wyvernCore, 'I', ModItems.draconiumIngot, 'T', Items.diamond_sword, 'E', ModItems.wyvernEnergyCore);
		add(ModItems.wyvernBow, " W ", "ITI", " E ", 'W', ModItems.wyvernCore, 'I', ModItems.draconiumIngot, 'T', Items.bow, 'E', ModItems.wyvernEnergyCore);
		//armor
		add(ModItems.wyvernHelm, "IWI", "IAI", "IEI", 'W', ModItems.wyvernCore, 'I', ModItems.draconiumIngot, 'A', Items.diamond_helmet, 'E', ModItems.wyvernEnergyCore);
		add(ModItems.wyvernChest, "IWI", "IAI", "IEI", 'W', ModItems.wyvernCore, 'I', ModItems.draconiumIngot, 'A', Items.diamond_chestplate, 'E', ModItems.wyvernEnergyCore);
		add(ModItems.wyvernLeggs, "IWI", "IAI", "IEI", 'W', ModItems.wyvernCore, 'I', ModItems.draconiumIngot, 'A', Items.diamond_leggings, 'E', ModItems.wyvernEnergyCore);
		add(ModItems.wyvernBoots, "IWI", "IAI", "IEI", 'W', ModItems.wyvernCore, 'I', ModItems.draconiumIngot, 'A', Items.diamond_boots, 'E', ModItems.wyvernEnergyCore);


	//Draconic Tools
		addEnergy(ModItems.draconicFluxCapacitor, "CMC", "SPS", "CSC", 'C', ModItems.draconicIngot, 'S', ModItems.draconicEnergyCore, 'M', ModItems.awakenedCore, 'P', ModItems.wyvernFluxCapacitor);
		//tools
		addEnergy(ModItems.draconicPickaxe, " C ", "ITI", " E ", 'C', ModItems.awakenedCore, 'I', ModItems.draconicIngot, 'T', ModItems.wyvernPickaxe, 'E', ModItems.draconicEnergyCore);
		addEnergy(ModItems.draconicShovel, " C ", "ITI", " E ", 'C', ModItems.awakenedCore, 'I', ModItems.draconicIngot, 'T', ModItems.wyvernShovel, 'E', ModItems.draconicEnergyCore);
		add(ModItems.draconicAxe, " C ", "ITI", " E ", 'C', ModItems.awakenedCore, 'I', ModItems.draconicIngot, 'T', Items.diamond_axe, 'E', ModItems.draconicEnergyCore);
		add(ModItems.draconicHoe, " C ", "ITI", " E ", 'C', ModItems.awakenedCore, 'I', ModItems.draconicIngot, 'T', Items.diamond_hoe, 'E', ModItems.draconicEnergyCore);
		addEnergy(ModItems.draconicSword, " C ", "ITI", " E ", 'C', ModItems.awakenedCore, 'I', ModItems.draconicIngot, 'T', ModItems.wyvernSword, 'E', ModItems.draconicEnergyCore);
		add(ModItems.draconicBow, " C ", "ITI", " E ", 'C', ModItems.awakenedCore, 'I', ModItems.draconicIngot, 'T', ModItems.wyvernBow, 'E', ModItems.draconicEnergyCore);
		addEnergy(ModItems.draconicDestructionStaff, "IAI", "PIS", "IWI", 'I', ModItems.draconicIngot, 'A', ModItems.awakenedCore, 'P', ModItems.draconicPickaxe, 'S', ModItems.draconicShovel, 'W', ModItems.draconicSword);
		//armor
		addEnergy(ModItems.draconicHelm, "IWI", "IAI", "IEI", 'W', ModItems.awakenedCore, 'I', ModItems.draconicIngot, 'A', ModItems.wyvernHelm, 'E', ModItems.draconicEnergyCore);
		addEnergy(ModItems.draconicChest, "IWI", "IAI", "IEI", 'W', ModItems.awakenedCore, 'I', ModItems.draconicIngot, 'A', ModItems.wyvernChest, 'E', ModItems.draconicEnergyCore);
		addEnergy(ModItems.draconicLeggs, "IWI", "IAI", "IEI", 'W', ModItems.awakenedCore, 'I', ModItems.draconicIngot, 'A', ModItems.wyvernLeggs, 'E', ModItems.draconicEnergyCore);
		addEnergy(ModItems.draconicBoots, "IWI", "IAI", "IEI", 'W', ModItems.awakenedCore, 'I', ModItems.draconicIngot, 'A', ModItems.wyvernBoots, 'E', ModItems.draconicEnergyCore);



	//Blocks
		//storage
		add(ModBlocks.draconiumBlock, "III", "III", "III", 'I', ModItems.draconiumIngot);
		add(ModBlocks.draconicBlock, "III", "III", "III", 'I', ModItems.draconicIngot);

		//aesthetic
		add(ModBlocks.particleGenerator, "RBR", "BCB", "RBR", 'R', Blocks.redstone_block, 'B', Items.blaze_rod, 'C', ModItems.draconicCore);
		add(ModBlocks.infusedObsidian, "BOB", "ODO", "BOB", 'B', Items.blaze_powder, 'O', Blocks.obsidian, 'D', ModItems.draconiumDust);

		//machines
		addOre(ModBlocks.potentiometer, "ITI", "QCQ", "IRI", 'I', "ingotIron", 'T', Blocks.redstone_torch, 'Q', "gemQuartz", 'C', Items.comparator, 'R', Blocks.redstone_block);
		addOre(ModBlocks.rainSensor, "   ", "RBR", "SPS", 'R', "dustRedstone", 'B', Items.bucket, 'S', Blocks.stone_slab, 'P', Blocks.heavy_weighted_pressure_plate);
		addOre(ModBlocks.teleporterStand, " P ", " S ", "HBH", 'P', Blocks.stone_pressure_plate, 'S', "stone", 'H', new ItemStack(Blocks.stone_slab, 1, 0), 'B', Items.blaze_powder);
		addOre(ModBlocks.dislocatorReceptacle, "ICI", " O ", "ISI", 'I', "ingotIron", 'C', ModItems.draconicCore, 'O', ModBlocks.infusedObsidian, 'S', ModBlocks.teleporterStand);


		//machines adv
		add(ModBlocks.resurrectionStone, "CSC", "SMS", "CSC", 'C', mobSoul, 'S', ModItems.wyvernCore, 'M', ModBlocks.draconiumBlock);
		add(ModBlocks.energyStorageCore, "CCC", "SMS", "CCC", 'C', ModItems.draconiumIngot, 'S', ModItems.wyvernEnergyCore, 'M', ModItems.wyvernCore);
		add(ModBlocks.weatherController, "RIR", "TPT", "IEI", 'R', Items.blaze_rod, 'T', Blocks.tnt, 'P', ModItems.draconicCore, 'I', ModItems.draconiumIngot, 'E', Blocks.enchanting_table);
		add(ModBlocks.playerDetectorAdvanced, "ISI", "EDE", "ICI", 'I', ModItems.draconiumIngot, 'E', Items.ender_eye, 'S', new ItemStack(Items.skull, 1, 1), 'C', Items.compass, 'D', ModBlocks.playerDetector);
		add(ModBlocks.energyInfuser, "IPI", "CEC", "ICI", 'I', ModItems.draconiumIngot, 'P', ModBlocks.particleGenerator, 'C', ModItems.draconicCore, 'E', Blocks.enchanting_table);
		add(ModBlocks.draconiumChest, "IFI", "SCS", "IWI", 'I', ModItems.draconiumIngot, 'C', ModItems.draconicCore, 'S', getChest(), 'W', Blocks.crafting_table, 'F', Blocks.furnace);
		addOre(getStack(ModBlocks.energyPylon, 2, 0), "IEI", "MCM", "IDI", 'I', ModItems.draconiumIngot, 'E', Items.ender_eye, 'C', ModItems.draconicCore, 'D', "gemDiamond", 'M', "gemEmerald");
		addOre(getStack(ModBlocks.grinder, 1, 3), "IXI", "DCD", "IFI", 'I', "ingotIron", 'X', ModItems.draconiumIngot, 'D', Items.diamond_sword, 'C', ModItems.draconicCore, 'F', Blocks.furnace);
		addOre(ModBlocks.playerDetector, "ITI", "CEC", "IDI", 'I', "ingotIron", 'E', Items.ender_eye, 'T', Blocks.redstone_torch, 'C', Items.comparator, 'D', ModItems.draconicCore);
		addOre(getStack(ModBlocks.generator, 1, 3), "NIN", "IFI", "NCN", 'N', Items.netherbrick, 'I', "ingotIron", 'F', Blocks.furnace, 'C', ModItems.draconicCore);
		addOre(ModBlocks.dissEnchanter, "PIP", "ETE", "CBC", 'P', Items.ender_eye, 'I', Items.enchanted_book, 'E', "gemEmerald", 'T', Blocks.enchanting_table, 'C', ModItems.draconicCore, 'B', Items.book);
		addOre(getStack(ModBlocks.energyCrystal, 4, 0), "IDI", "DCD", "IDI", 'I' , ModItems.draconiumIngot, 'D', "gemDiamond", 'C', ModItems.draconicCore);
		addOre(getStack(ModBlocks.energyCrystal, 4, 1), "CRC", "RWR", "CRC", 'R' , getStack(ModBlocks.energyCrystal, 1, 0), 'W', ModItems.wyvernCore, 'C', ModItems.draconicCore);
		addShaplessOre(getStack(ModBlocks.energyCrystal, 1, 0), getStack(ModBlocks.energyCrystal, 1, 2), getStack(ModBlocks.energyCrystal, 1, 2));
		addShaplessOre(getStack(ModBlocks.energyCrystal, 2, 2), getStack(ModBlocks.energyCrystal, 1, 0));
		addShaplessOre(getStack(ModBlocks.energyCrystal, 1, 1), getStack(ModBlocks.energyCrystal, 1, 3), getStack(ModBlocks.energyCrystal, 1, 3));
		addShaplessOre(getStack(ModBlocks.energyCrystal, 2, 3), getStack(ModBlocks.energyCrystal, 1, 1));
		addOre(getStack(ModBlocks.energyCrystal, 1, 4), "PGP", "ECE", "PGP", 'P', Items.ender_pearl, 'G', ModBlocks.particleGenerator, 'E', Items.ender_eye, 'C', getStack(ModBlocks.energyCrystal, 1, 0));
		addOre(getStack(ModBlocks.energyCrystal, 1, 5), "PGP", "ECE", "PGP", 'P', Items.ender_pearl, 'G', ModBlocks.particleGenerator, 'E', Items.ender_eye, 'C', getStack(ModBlocks.energyCrystal, 1, 1));


	//Tools
		add(ModItems.teleporterMKII, "IEI", "ETE", "IWI", 'I', ModItems.draconiumIngot, 'E', Items.ender_pearl, 'T', ModItems.teleporterMKI, 'W', ModItems.wyvernCore);
		add(ModItems.teleporterMKI, "CSC", "SMS", "CSC", 'C', Items.blaze_powder, 'S', ModItems.draconiumDust, 'M', Items.ender_eye);
		addOre(getStack(ModItems.safetyMatch, 1, 1000), " O ", " S ", "   ", 'O', "dyeOrange", 'S', "stickWood");
		add(ModItems.safetyMatch, "MMM", "MMM", "MMM", 'M', getStack(ModItems.safetyMatch, 1, 1000));
		addOre(ModItems.wrench, " ID", " RI", "C  ", 'I' , ModItems.draconiumIngot, 'D', "gemDiamond", 'R', Items.blaze_rod, 'C', ModItems.draconicCore);

	//Other
		addOre(ModItems.infoTablet, "SSS", "SDS", "SSS", 'S', "stone", 'D', ModItems.draconiumDust);
		addShaplessOre(getStack(ModItems.enderArrow, 1, 0), Items.arrow, Items.ender_pearl);
		addShaplessOre(new ItemStack(Blocks.dirt), Item.getItemFromBlock(Blocks.sand), Items.rotten_flesh, "treeSapling", "treeSapling", "treeSapling");
		addShaplessOre(new ItemStack(Blocks.dirt), Item.getItemFromBlock(Blocks.sand), Items.rotten_flesh, "treeLeaves", "treeLeaves", "treeLeaves");


	//Disable able
		//if(ConfigHandler.disableSunDial == 0)
		add(ModBlocks.sunDial, "IAI", "CDC", "IEI", 'I', ModItems.draconiumIngot, 'A', ModItems.awakenedCore, 'C', ModItems.draconicCore, 'E', Blocks.enchanting_table, 'D', Blocks.dragon_egg);
		//if(ConfigHandler.disableXrayBlock == 0)
		addOre(getStack(ModBlocks.xRayBlock, 4, 0), "SGS", "GDG", "SGS", 'S', Items.nether_star, 'G', "blockGlassColorless", 'D', "gemDiamond");

	}

	private static ItemStack getChest(){
		if (Loader.isModLoaded("IronChest")){
			LogHelper.info("Adding Iron Chests Integration");
			return new ItemStack(GameRegistry.findBlock("IronChest", "BlockIronChest"), 1, 6);
		}else

		LogHelper.info("Iron Chests was not detected! using fallback chest recipe");
		return new ItemStack(Blocks.chest);
	}

	private static void addOre(Block result, Object... recipe){ addOre(new ItemStack(result), recipe); }
	private static void addOre(Item result, Object... recipe){ addOre(new ItemStack(result), recipe); }
	private static void addOre(ItemStack result, Object... recipe){
		if (result == null) return;
		for (Object o : recipe) {
			if (o == null) return;
			String s = o instanceof Item ? ((Item) o).getUnlocalizedName() : o instanceof Block ? ((Block) o).getUnlocalizedName() : null;
			if (s != null && ConfigHandler.disabledNamesList.contains(s)) return;
		}

		GameRegistry.addRecipe(new ShapedOreRecipe(result, recipe));
	}

	private static void addShaplessOre(ItemStack result, Object... recipe){
		if (result == null) return;
		for (Object o : recipe) {
			if (o == null) return;
			String s = o instanceof Item ? ((Item) o).getUnlocalizedName() : o instanceof Block ? ((Block) o).getUnlocalizedName() : null;
			if (s != null && ConfigHandler.disabledNamesList.contains(s)) return;
		}

		GameRegistry.addRecipe(new ShapelessOreRecipe(result, recipe));
	}

	private static void add(Block result, Object... recipe){ add(new ItemStack(result), recipe); }
	private static void add(Item result, Object... recipe){ add(new ItemStack(result), recipe); }
	private static void add(ItemStack result, Object... recipe){
		if (result == null) return;
		for (Object o : recipe) {
			if (o == null) return;
			String s = o instanceof Item ? ((Item) o).getUnlocalizedName() : o instanceof Block ? ((Block) o).getUnlocalizedName() : null;
			if (s != null && ConfigHandler.disabledNamesList.contains(s)) return;
		}

		GameRegistry.addRecipe(result, recipe);
	}

	private static void addEnergy(Block result, Object... recipe){ addEnergy(new ItemStack(result), recipe); }
	private static void addEnergy(Item result, Object... recipe){ addEnergy(new ItemStack(result), recipe); }
	private static void addEnergy(ItemStack result, Object... recipe){
		if (result == null) return;
		for (Object o : recipe) {
			if (o == null) return;
			String s = o instanceof Item ? ((Item) o).getUnlocalizedName() : o instanceof Block ? ((Block) o).getUnlocalizedName() : null;
			if (s != null && ConfigHandler.disabledNamesList.contains(s)) return;
		}

		GameRegistry.addRecipe(new ShapedOreEnergyRecipe(result, recipe));
	}

	private static ItemStack getStack(Block block, int count, int meta)
	{
		return ModBlocks.isEnabled(block) ? new ItemStack(block, count, meta) : null;
	}

	private static ItemStack getStack(Item item, int count, int meta)
	{
		return ModItems.isEnabled(item) ? new ItemStack(item, count, meta) : null;
	}
}