package com.brandon3055.draconicevolution.common;

import com.brandon3055.draconicevolution.DraconicEvolution;
import com.brandon3055.draconicevolution.common.blocks.*;
import com.brandon3055.draconicevolution.common.blocks.machine.*;
import com.brandon3055.draconicevolution.common.blocks.multiblock.EnergyPylon;
import com.brandon3055.draconicevolution.common.blocks.multiblock.EnergyStorageCore;
import com.brandon3055.draconicevolution.common.blocks.multiblock.InvisibleMultiblock;
import com.brandon3055.draconicevolution.common.handler.ConfigHandler;
import com.brandon3055.draconicevolution.common.lib.References;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

@GameRegistry.ObjectHolder(References.MODID)
public class ModBlocks {
	public static BlockDE xRayBlock;
	public static BlockDE weatherController;
	public static BlockDE sunDial;
	public static BlockDE draconiumOre;
	public static BlockDE testBlock;
	public static BlockDE grinder;
	public static BlockDE potentiometer;
	public static BlockDE rainSensor;
	public static BlockDE particleGenerator;
	public static BlockDE playerDetector;
	public static BlockDE playerDetectorAdvanced;
	public static BlockDE energyInfuser;
	public static BlockDE customSpawner;
	public static BlockDE longRangeDislocator;
	public static BlockDE generator;
	public static BlockDE energyStorageCore;
	public static BlockDE draconiumBlock;
	public static BlockDE invisibleMultiblock;
	public static BlockDE energyPylon;
	public static BlockDE placedItem;
	public static BlockDE cKeyStone;
	public static BlockDE containerTemplate;
	public static BlockDE dissEnchanter;
	public static BlockDE teleporterStand;
	public static BlockDE draconiumChest;
	public static BlockDE draconicBlock;
	public static Block safetyFlame;

	public static ItemStack resurrectionStone;

	public static void init()
	{
		if(ConfigHandler.disableXrayBlock < 2) xRayBlock = new XRayBlock();
		weatherController = new WeatherController();
		if(ConfigHandler.disableSunDial < 2) sunDial = new SunDial();
		draconiumOre = new DraconiumOre();
		grinder = new Grinder();
		potentiometer = new Potentiometer();
		rainSensor = new RainSensor();
		particleGenerator = new ParticleGenerator();
		playerDetector = new PlayerDetector();
		playerDetectorAdvanced = new PlayerDetectorAdvanced();
		energyInfuser = new EnergyInfuser();
		customSpawner = new CustomSpawner();
		generator = new Generator();
		energyStorageCore = new EnergyStorageCore();
		draconiumBlock = new DraconiumBlock();
		invisibleMultiblock = new InvisibleMultiblock();
		energyPylon = new EnergyPylon();
		placedItem = new PlacedItem();
		safetyFlame = new SafetyFlame();
		cKeyStone = new CKeyStone();
		dissEnchanter = new DissEnchanter();
		teleporterStand = new TeleporterStand();
		draconiumChest = new DraconiumChest();
		draconicBlock = new DraconicBlock();

		if(ConfigHandler.disable_LRD < 2) longRangeDislocator = new LongRangeDislocator();
		
		if(DraconicEvolution.debug) {
			testBlock = new TestBlock();
			containerTemplate = new BlockContainerTemplate();
		}

		resurrectionStone = new ItemStack(ModBlocks.draconiumBlock, 1, 1);
	}

	public static void register(BlockDE block)
	{
		String name = block.getUnwrappedUnlocalizedName(block.getUnlocalizedName());
		GameRegistry.registerBlock(block, name.substring(name.indexOf(":") + 1));
		//GameRegistry.registerBlock(block, block.getUnwrappedUnlocalizedName(block.getUnlocalizedName()));
	}

	public static void register(BlockDE block, Class<? extends ItemBlock> item)
	{
		String name = block.getUnwrappedUnlocalizedName(block.getUnlocalizedName());
		GameRegistry.registerBlock(block, item, name.substring(name.indexOf(":") + 1));
	}

	public static void registerOther(Block block)
	{
		String name = block.getUnlocalizedName().substring(block.getUnlocalizedName().indexOf(".") + 1);
		GameRegistry.registerBlock(block, name.substring(name.indexOf(":") + 1));
	}
}
