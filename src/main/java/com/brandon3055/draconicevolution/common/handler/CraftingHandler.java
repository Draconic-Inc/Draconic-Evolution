package com.brandon3055.draconicevolution.common.handler;

import com.brandon3055.draconicevolution.common.ModBlocks;
import com.brandon3055.draconicevolution.common.utills.ItemNBTHelper;
import com.brandon3055.draconicevolution.common.utills.LogHelper;
import com.brandon3055.draconicevolution.common.ModItems;
import com.brandon3055.draconicevolution.common.items.armor.DraconicArmor;
import com.brandon3055.draconicevolution.common.items.armor.WyvernArmor;
import com.brandon3055.draconicevolution.common.items.tools.*;
import com.brandon3055.draconicevolution.common.items.weapons.DraconicBow;
import com.brandon3055.draconicevolution.common.items.weapons.DraconicSword;
import com.brandon3055.draconicevolution.common.items.weapons.WyvernBow;
import com.brandon3055.draconicevolution.common.items.weapons.WyvernSword;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraftforge.oredict.ShapedOreRecipe;

public class CraftingHandler {
	public static void init()
	{
		registerShapedRecipes();
		registerSmeltingRecipes();
		registerShapelessRecipes();
		registerRemoteRecipes();
	}

	private static void registerRemoteRecipes()
	{
		DraconicPickaxe.registerRecipe();
		DraconicAxe.registerRecipe();
		DraconicDistructionStaff.registerRecipe();
		DraconicShovel.registerRecipe();
		DraconicHoe.registerRecipe();
		DraconicSword.registerRecipe();
		DraconicBow.registerRecipe();
		WyvernPickaxe.registerRecipe();
		WyvernShovel.registerRecipe();
		WyvernSword.registerRecipe();
		WyvernBow.registerRecipe();
		TeleporterMKI.registerRecipe();
		TeleporterMKII.registerRecipe();
		DraconicArmor.registerRecipe();
		WyvernArmor.registerRecipe();
	}

	public static void registerShapedRecipes()
	{
		ItemStack mobSoul = new ItemStack(ModItems.mobSoul);
		ItemNBTHelper.setString(mobSoul, "Name", "Any");

		ItemStack chest;
		boolean isIronChestsInstalled = Loader.isModLoaded("IronChest");
		if (isIronChestsInstalled){
			chest = new ItemStack(GameRegistry.findBlock("IronChest", "BlockIronChest"), 1, 6);
			LogHelper.info("Added Iron Chests Integration");
		}else {
			chest = new ItemStack(Blocks.chest);
			LogHelper.info("Iron Chests was not detected! using fallback chest recipe");
		}


		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModItems.draconicCore), "GIG", "ISI", "GIG", 'S', "gemDiamond", 'G', "ingotGold", 'I', ModItems.draconiumIngot));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModItems.infusedCompound, 2), "IDI", "DSD", "IDI", 'S', Items.nether_star, 'D', "gemDiamond", 'I', ModItems.draconiumIngot));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModItems.draconiumBlend), " D ", "DID", " D ", 'I', "ingotIron", 'D', ModItems.draconiumDust));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModItems.safetyMatch, 1, 1000), " O ", " S ", "   ", 'O', "dyeOrange", 'S', "stickWood"));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModItems.infoTablet), "SSS", "SDS", "SSS", 'S', "stone", 'D', ModItems.draconiumDust));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModBlocks.grinder, 1, 3), "IXI", "DCD", "IFI", 'I', "ingotIron", 'X', ModItems.draconiumIngot, 'D', Items.diamond_sword, 'C', ModItems.draconicCore, 'F', Blocks.furnace));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModBlocks.weatherController), "RSR", "TPT", "IEI", 'R', Items.blaze_rod, 'S', ModItems.sunFocus, 'T', Blocks.tnt, 'P', ModItems.draconicCore, 'I', "ingotIron", 'E', Blocks.enchanting_table));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModBlocks.potentiometer), "ITI", "QCQ", "IRI", 'I', "ingotIron", 'T', Blocks.redstone_torch, 'Q', "gemQuartz", 'C', Items.comparator, 'R', Blocks.redstone_block));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModBlocks.rainSensor), "   ", "RBR", "SPS", 'R', "dustRedstone", 'B', Items.bucket, 'S', Blocks.stone_slab, 'P', Blocks.heavy_weighted_pressure_plate));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModBlocks.playerDetector), "ITI", "CEC", "IDI", 'I', "ingotIron", 'E', Items.ender_eye, 'T', Blocks.redstone_torch, 'C', Items.comparator, 'D', ModItems.draconicCore));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModBlocks.generator, 1, 3), "NIN", "IFI", "NCN", 'N', Items.netherbrick, 'I', "ingotIron", 'F', Blocks.furnace, 'C', ModItems.draconicCore));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModBlocks.energyPylon, 2), "IEI", "MCM", "IDI", 'I', ModItems.draconiumIngot, 'E', Items.ender_eye, 'C', ModItems.draconicCore, 'D', "gemDiamond", 'M', "gemEmerald"));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModBlocks.dissEnchanter), "PIP", "ETE", "CBC", 'P', Items.ender_eye, 'I', Items.enchanted_book, 'E', "gemEmerald", 'T', Blocks.enchanting_table, 'C', ModItems.draconicCore, 'B', Items.book));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModBlocks.teleporterStand), " P ", " S ", "HBH", 'P', Blocks.stone_pressure_plate, 'S', "stone", 'H', new ItemStack(Blocks.stone_slab, 1, 0), 'B', Items.blaze_powder));


		CraftingManager.getInstance().addRecipe(new ItemStack(ModItems.sunFocus), "RIR", "ISI", "RIR", 'S', Items.nether_star, 'R', Items.blaze_rod, 'I', ModItems.draconiumIngot);
		CraftingManager.getInstance().addRecipe(new ItemStack(ModItems.draconicCompound, 2), " C ", "CHC", " C ", 'C', ModItems.infusedCompound, 'H', ModItems.dragonHeart);
		CraftingManager.getInstance().addRecipe(new ItemStack(ModItems.safetyMatch, 0), "MMM", "MMM", "MMM", 'M', new ItemStack(ModItems.safetyMatch, 1, 1000));

		CraftingManager.getInstance().addRecipe(new ItemStack(ModBlocks.particleGenerator), "RBR", "BCB", "RBR", 'R', Blocks.redstone_block, 'B', Items.blaze_rod, 'C', ModItems.draconicCore);
		CraftingManager.getInstance().addRecipe(new ItemStack(ModBlocks.playerDetectorAdvanced), "ISI", "EDE", "ICI", 'I', ModItems.draconiumIngot, 'E', Items.ender_eye, 'S', new ItemStack(Items.skull, 1, 1), 'C', Items.compass, 'D', ModBlocks.playerDetector);
		CraftingManager.getInstance().addRecipe(new ItemStack(ModBlocks.energyInfuser), "IPI", "CEC", "ICI", 'I', ModItems.draconiumIngot, 'P', ModBlocks.particleGenerator, 'C', ModItems.draconicCore, 'E', Blocks.enchanting_table);
		CraftingManager.getInstance().addRecipe(new ItemStack(ModBlocks.draconium), "III", "III", "III", 'I', ModItems.draconiumIngot);
		CraftingManager.getInstance().addRecipe(new ItemStack(ModBlocks.energyStorageCore), "SRS", "DCD", "SRS", 'S', Items.nether_star, 'R', Blocks.redstone_block, 'C', ModItems.draconicCore, 'D', new ItemStack(ModBlocks.draconium, 1, 0));
		CraftingManager.getInstance().addRecipe(new ItemStack(ModBlocks.draconium, 1, 1), "CSC", "SDS", "CSC", 'C', mobSoul, 'S', ModItems.sunFocus, 'D', new ItemStack(ModBlocks.draconium, 1, 0));
		CraftingManager.getInstance().addRecipe(new ItemStack(ModBlocks.draconiumChest), "IFI", "SCS", "IWI", 'I', ModItems.draconiumIngot, 'C', ModItems.draconicCore, 'S', chest, 'W', Blocks.crafting_table, 'F', Blocks.furnace);


		if(ConfigHandler.disableSunDial == 0)
			CraftingManager.getInstance().addRecipe(new ItemStack(ModBlocks.sunDial), "IFI", "TDT", "GEG", 'I', ModItems.draconiumIngot, 'F', ModItems.sunFocus, 'T', ModItems.draconicCore, 'G', Blocks.glowstone, 'E', Blocks.enchanting_table, 'D', ModItems.dragonHeart);
		if(ConfigHandler.disableXrayBlock == 0)
			GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModBlocks.xRayBlock, 4), "SGS", "GDG", "SGS", 'S', Items.nether_star, 'G', "blockGlassColorless", 'D', "gemDiamond"));
			//CraftingManager.getInstance().addRecipe(new ItemStack(ModBlocks.xRayBlock, 4), "SGS", "GDG", "SGS", 'S', Items.nether_star, 'G', "blockGlassColorless", 'D', "gemDiamond");
	}

	public static void registerSmeltingRecipes()
	{
		FurnaceRecipes.smelting().func_151396_a(ModItems.draconiumBlend, new ItemStack(ModItems.draconiumIngot, 2), 1.0f);
	}

	public static void registerShapelessRecipes()
	{
		CraftingManager.getInstance().addShapelessRecipe(new ItemStack(ModItems.enderArrow), Items.arrow, Items.ender_pearl);
	}

}
/*
//turns dirt into enchanted diamond sword
ItemStack manipulation = new ItemStack(Items.diamond_sword);
manipulation.addEnchantment(Enchantment.efficiency, 2);
CraftingManager.getInstance().addShapelessRecipe(manipulation, Blocks.dirt);

ItemStack manipulation2 = new ItemStack(Items.diamond_sword);
manipulation2.damageItem(10, null);
CraftingManager.getInstance().addShapelessRecipe(manipulation2, Blocks.end_portal_frame); */