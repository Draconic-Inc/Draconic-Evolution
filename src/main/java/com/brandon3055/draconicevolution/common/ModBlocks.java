package com.brandon3055.draconicevolution.common;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

import com.brandon3055.draconicevolution.DraconicEvolution;
import com.brandon3055.draconicevolution.common.blocks.*;
import com.brandon3055.draconicevolution.common.blocks.machine.*;
import com.brandon3055.draconicevolution.common.blocks.multiblock.*;
import com.brandon3055.draconicevolution.common.handler.ConfigHandler;
import com.brandon3055.draconicevolution.common.lib.References;
import cpw.mods.fml.common.registry.GameRegistry;

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
    public static BlockDE earthBlock;
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
    public static BlockDE energyCrystal;
    public static BlockDE infusedObsidian;
    public static BlockDE dislocatorReceptacle;
    public static BlockDE portal;
    public static BlockDE reactorCore;
    public static BlockDE flowGate;
    public static BlockDE reactorStabilizer;
    public static BlockDE reactorEnergyInjector;
    public static BlockDE chaosCrystal;
    public static BlockDE upgradeModifier;
    public static Block safetyFlame;
    public static Block chaosShardAtmos = new ChaosShardAtmos()
            .setBlockName(References.RESOURCESPREFIX + "chaosShardAtmos")
            .setBlockTextureName(References.RESOURCESPREFIX + "transparency");

    public static ItemStack resurrectionStone;

    public static void init() {
        xRayBlock = new XRayBlock();
        weatherController = new WeatherController();
        sunDial = new SunDial();
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
        earthBlock = new Earth();
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
        energyCrystal = new EnergyCrystal();
        infusedObsidian = new InfusedObsidian();
        dislocatorReceptacle = new DislocatorReceptacle();
        portal = new Portal();
        reactorCore = new ReactorCore();
        flowGate = new FlowGate();
        reactorStabilizer = new ReactorStabilizer();
        reactorEnergyInjector = new ReactorEnergyInjector();
        chaosCrystal = new ChaosCrystal();
        upgradeModifier = new UpgradeModifier();

        longRangeDislocator = new LongRangeDislocator();

        if (isEnabled(chaosShardAtmos)) GameRegistry.registerBlock(chaosShardAtmos, "chaosShardAtmos");

        if (DraconicEvolution.debug) {
            testBlock = new TestBlock();
            containerTemplate = new BlockContainerTemplate();
        }

        resurrectionStone = new ItemStack(ModBlocks.draconiumBlock, 1, 1);
    }

    public static void register(BlockDE block) {
        String name = block.getUnwrappedUnlocalizedName(block.getUnlocalizedName());
        if (isEnabled(block)) GameRegistry.registerBlock(block, name.substring(name.indexOf(":") + 1));
    }

    public static void register(BlockDE block, Class<? extends ItemBlock> item) {
        String name = block.getUnwrappedUnlocalizedName(block.getUnlocalizedName());
        if (isEnabled(block)) GameRegistry.registerBlock(block, item, name.substring(name.indexOf(":") + 1));
    }

    public static void registerOther(Block block) {
        String name = block.getUnlocalizedName().substring(block.getUnlocalizedName().indexOf(".") + 1);
        if (isEnabled(block)) GameRegistry.registerBlock(block, name.substring(name.indexOf(":") + 1));
    }

    public static boolean isEnabled(Block block) {
        return !ConfigHandler.disabledNamesList.contains(block.getUnlocalizedName());
    }
}
