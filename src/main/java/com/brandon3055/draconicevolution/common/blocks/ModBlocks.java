package com.brandon3055.draconicevolution.common.blocks;

import com.brandon3055.draconicevolution.common.blocks.machine.*;
import com.brandon3055.draconicevolution.common.blocks.multiblock.EnergyPylon;
import com.brandon3055.draconicevolution.common.blocks.multiblock.EnergyStorageCore;
import com.brandon3055.draconicevolution.common.blocks.multiblock.InvisibleMultiblock;
import com.brandon3055.draconicevolution.common.core.utills.Utills;
import com.brandon3055.draconicevolution.common.lib.References;
import cpw.mods.fml.common.registry.GameRegistry;
import com.brandon3055.draconicevolution.DraconicEvolution;
import com.brandon3055.draconicevolution.common.core.handler.ConfigHandler;
import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;

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
	public static BlockDE draconium;
	public static BlockDE invisibleMultiblock;
	public static BlockDE energyPylon;

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
		draconium = new Draconium();
		invisibleMultiblock = new InvisibleMultiblock();
		energyPylon = new EnergyPylon();
		if(ConfigHandler.disable_LRD < 2) longRangeDislocator = new LongRangeDislocator();
		
		if(DraconicEvolution.debug) testBlock = new TestBlock();

		if (ConfigHandler.updateFix)
		{
			Block changer1 = new Utills.BlockChanger("tile.particleGenerator", particleGenerator);
			Block changer2 = new Utills.BlockChanger("tile.customSpawner", customSpawner);
			Block changer3 = new Utills.BlockChanger("tile.generator", generator);
			Block changer4 = new Utills.BlockChanger("tile.playerDetectorAdvanced", playerDetectorAdvanced);
			Block changer5 = new Utills.BlockChanger("tile.energyInfuser", energyInfuser);
			Block changer6 = new Utills.BlockChanger("tile.draconiumOre", draconiumOre);
			Block changer7 = new Utills.BlockChanger("tile.weatherController", weatherController);
			Block changer8 = new Utills.BlockChanger("tile.longRangeDislocator", longRangeDislocator);
			Block changer9 = new Utills.BlockChanger("tile.grinder", grinder);
		}
	}

	public static void register(BlockDE block)
	{
		String name = block.getUnwrappedUnlocalizedName(block.getUnlocalizedName());
		GameRegistry.registerBlock(block, name.substring(name.indexOf(":") + 1));
		//GameRegistry.registerBlock(block, block.getUnwrappedUnlocalizedName(block.getUnlocalizedName()));
	}

	public static void registerWithItem(BlockDE block, Class<? extends ItemBlock> item)
	{
		String name = block.getUnwrappedUnlocalizedName(block.getUnlocalizedName());
		GameRegistry.registerBlock(block, item, name.substring(name.indexOf(":") + 1));
	}
}
