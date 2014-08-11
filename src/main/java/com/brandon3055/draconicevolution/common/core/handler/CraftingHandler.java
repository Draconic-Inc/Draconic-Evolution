package com.brandon3055.draconicevolution.common.core.handler;

import com.brandon3055.draconicevolution.common.blocks.ModBlocks;
import com.brandon3055.draconicevolution.common.items.ModItems;
import com.brandon3055.draconicevolution.common.items.armor.DraconicArmor;
import com.brandon3055.draconicevolution.common.items.armor.WyvernArmor;
import com.brandon3055.draconicevolution.common.items.tools.DraconicAxe;
import com.brandon3055.draconicevolution.common.items.tools.DraconicDistructionStaff;
import com.brandon3055.draconicevolution.common.items.tools.DraconicHoe;
import com.brandon3055.draconicevolution.common.items.tools.DraconicPickaxe;
import com.brandon3055.draconicevolution.common.items.tools.DraconicShovel;
import com.brandon3055.draconicevolution.common.items.tools.TeleporterMKI;
import com.brandon3055.draconicevolution.common.items.tools.TeleporterMKII;
import com.brandon3055.draconicevolution.common.items.tools.WyvernPickaxe;
import com.brandon3055.draconicevolution.common.items.tools.WyvernShovel;
import com.brandon3055.draconicevolution.common.items.weapons.DraconicBow;
import com.brandon3055.draconicevolution.common.items.weapons.DraconicSword;
import com.brandon3055.draconicevolution.common.items.weapons.WyvernBow;
import com.brandon3055.draconicevolution.common.items.weapons.WyvernSword;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.FurnaceRecipes;

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
		CraftingManager.getInstance().addRecipe(new ItemStack(ModItems.draconicCore), "GIG", "ISI", "GIG", 'S', Items.diamond, 'G', Items.gold_ingot, 'I', ModItems.draconiumIngot);
		CraftingManager.getInstance().addRecipe(new ItemStack(ModItems.sunFocus), "RIR", "ISI", "RIR", 'S', Items.nether_star, 'R', Items.blaze_rod, 'I', ModItems.draconiumIngot);
		CraftingManager.getInstance().addRecipe(new ItemStack(ModItems.infusedCompound, 2), "IDI", "DSD", "IDI", 'S', Items.nether_star, 'D', Items.diamond, 'I', ModItems.draconiumIngot);
		CraftingManager.getInstance().addRecipe(new ItemStack(ModItems.dragonHeart), " C ", "CIC", " C ", 'C', ModItems.infusedCompound, 'I', ModItems.draconiumIngot);
		CraftingManager.getInstance().addRecipe(new ItemStack(ModItems.draconiumBlend), " D ", "DID", " D ", 'I', Items.iron_ingot, 'D', ModItems.draconiumDust);
		CraftingManager.getInstance().addRecipe(new ItemStack(ModItems.draconicCompound), " C ", "CHC", " C ", 'C', ModItems.infusedCompound, 'H', ModItems.dragonHeart);		
				
		CraftingManager.getInstance().addRecipe(new ItemStack(ModBlocks.grinder, 1, 3), "IXI", "DCD", "IFI", 'I', Items.iron_ingot, 'X', ModItems.draconiumIngot, 'D', Items.diamond_sword, 'C', ModItems.draconicCore, 'F', Blocks.furnace);
		CraftingManager.getInstance().addRecipe(new ItemStack(ModBlocks.weatherController), "RSR", "TPT", "IEI", 'R', Items.blaze_rod, 'S', ModItems.sunFocus, 'T', Blocks.tnt, 'P', ModItems.draconicCore, 'I', Items.iron_ingot, 'E', Blocks.enchanting_table);
		CraftingManager.getInstance().addRecipe(new ItemStack(ModBlocks.potentiometer), "ITI", "QCQ", "IRI", 'I', Items.iron_ingot, 'T', Blocks.redstone_torch, 'Q', Items.quartz, 'C', Items.comparator, 'R', Blocks.redstone_block);
		CraftingManager.getInstance().addRecipe(new ItemStack(ModBlocks.rainSensor), "   ", "RBR", "SPS", 'R', Items.redstone, 'B', Items.bucket, 'S', Blocks.stone_slab, 'P', Blocks.heavy_weighted_pressure_plate);
		CraftingManager.getInstance().addRecipe(new ItemStack(ModBlocks.particleGenerator), "RBR", "BCB", "RBR", 'R', Blocks.redstone_block, 'B', Items.blaze_rod, 'C', ModItems.draconicCore);
		CraftingManager.getInstance().addRecipe(new ItemStack(ModBlocks.playerDetector), "ITI", "CEC", "IDI", 'I', Items.iron_ingot, 'E', Items.ender_eye, 'T', Blocks.redstone_torch, 'C', Items.comparator, 'D', ModItems.draconicCore);
		CraftingManager.getInstance().addRecipe(new ItemStack(ModBlocks.playerDetectorAdvanced), "ISI", "EDE", "ICI", 'I', ModItems.draconiumIngot, 'E', Items.ender_eye, 'S', new ItemStack(Items.skull, 1, 1), 'C', Items.compass, 'D', ModBlocks.playerDetector);
		CraftingManager.getInstance().addRecipe(new ItemStack(ModBlocks.generator, 1, 3), "NIN", "IFI", "NCN", 'N', Items.netherbrick, 'I', Items.iron_ingot, 'F', Blocks.furnace, 'C', ModItems.draconicCore);
		CraftingManager.getInstance().addRecipe(new ItemStack(ModBlocks.energyInfuser), "IPI", "CEC", "IDI", 'I', ModItems.draconiumIngot, 'P', ModBlocks.particleGenerator, 'C', ModItems.draconicCore, 'D', ModItems.infusedCompound, 'E', Blocks.enchanting_table);
		CraftingManager.getInstance().addRecipe(new ItemStack(ModBlocks.draconium), "III", "III", "III", 'I', ModItems.draconiumIngot);
		CraftingManager.getInstance().addRecipe(new ItemStack(ModBlocks.energyStorageCore), "SRS", "DCD", "SRS", 'S', Items.nether_star, 'R', Blocks.redstone_block, 'C', ModItems.draconicCore, 'D', ModBlocks.draconium);
		CraftingManager.getInstance().addRecipe(new ItemStack(ModBlocks.energyPylon, 2), "IEI", "MCM", "IDI", 'I', ModItems.draconiumIngot, 'E', Items.ender_eye, 'C', ModItems.draconicCore, 'D', Items.diamond, 'M', Items.emerald);

		if(ConfigHandler.disableSunDial == 0)
			CraftingManager.getInstance().addRecipe(new ItemStack(ModBlocks.sunDial), "IFI", "TDT", "GEG", 'I', ModItems.draconiumIngot, 'F', ModItems.sunFocus, 'T', ModItems.draconicCore, 'G', Blocks.glowstone, 'E', Blocks.enchanting_table, 'D', ModItems.dragonHeart);
		if(ConfigHandler.disableXrayBlock == 0)
			CraftingManager.getInstance().addRecipe(new ItemStack(ModBlocks.xRayBlock, 4), "SGS", "GDG", "SGS", 'S', Items.nether_star, 'G', Blocks.glass, 'D', Items.diamond);	
	}

	public static void registerSmeltingRecipes()
	{
		FurnaceRecipes.smelting().func_151396_a(ModItems.draconiumBlend, new ItemStack(ModItems.draconiumIngot, 2), 1.0f);
	}

	public static void registerShapelessRecipes()
	{
		//CraftingManager.getInstance().addShapelessRecipe(new ItemStack(ModItems.tcSword), ModItems.tcSwordBlade, Items.stick);
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