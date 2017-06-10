package com.brandon3055.draconicevolution;

import com.brandon3055.brandonscore.blocks.BlockBCore;
import com.brandon3055.brandonscore.blocks.BlockMobSafe;
import com.brandon3055.brandonscore.blocks.ItemBlockBCore;
import com.brandon3055.brandonscore.blocks.ItemBlockBasic;
import com.brandon3055.brandonscore.config.Feature;
import com.brandon3055.brandonscore.items.ItemSimpleSubs;
import com.brandon3055.draconicevolution.blocks.*;
import com.brandon3055.draconicevolution.blocks.energynet.EnergyCrystal;
import com.brandon3055.draconicevolution.blocks.energynet.tileentity.TileCrystalDirectIO;
import com.brandon3055.draconicevolution.blocks.itemblock.ItemDraconiumBlock;
import com.brandon3055.draconicevolution.blocks.machines.*;
import com.brandon3055.draconicevolution.blocks.reactor.ReactorComponent;
import com.brandon3055.draconicevolution.blocks.reactor.ReactorCore;
import com.brandon3055.draconicevolution.blocks.reactor.tileentity.TileReactorCore;
import com.brandon3055.draconicevolution.blocks.tileentity.*;
import com.brandon3055.draconicevolution.items.*;
import com.brandon3055.draconicevolution.items.armor.DraconicArmor;
import com.brandon3055.draconicevolution.items.armor.WyvernArmor;
import com.brandon3055.draconicevolution.items.tools.*;
import net.minecraft.block.material.Material;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.GameRegistry;

/**
 * Created by brandon3055 on 18/3/2016.
 * This class contains a reference to all blocks and items in Draconic Evolution
 */
@GameRegistry.ObjectHolder(DraconicEvolution.MODID)
public class DEFeatures {

	/* ------------------ Blocks ------------------ */

    //region Simple Blocks
    @Feature(registryName = "draconium_ore", variantMap = {"0:type=normal", "1:type=nether", "2:type=end"}, itemBlock = ItemBlockBasic.class)
    public static DraconiumOre draconiumOre = (DraconiumOre) new DraconiumOre().setHardness(10f).setResistance(20.0f);

    @Feature(registryName = "draconium_block", variantMap = {"0:charged=false", "1:charged=true"}, itemBlock = ItemDraconiumBlock.class)
    public static DraconiumBlock draconiumBlock = (DraconiumBlock) new DraconiumBlock().setHardness(10f).setResistance(20.0f);

    @Feature(registryName = "draconic_block")
    public static BlockMobSafe draconicBlock = (BlockMobSafe) ((BlockBCore) new BlockMobSafe(Material.IRON){
        @Override//TODO add a way to override this in BlockBCore
        public float getEnchantPowerBonus(World world, BlockPos pos) {
            return 12f;
        }
    }.setHardness(20F).setResistance(1000F)).setHarvestTool("pickaxe", 4);

    @Feature(registryName = "infused_obsidian")
    public static BlockMobSafe infusedObsidian = (BlockMobSafe) ((BlockBCore) new BlockMobSafe(Material.IRON).setHardness(100F).setResistance(4000F)).setHarvestTool("pickaxe", 4);

    @Feature(registryName = "portal", tileEntity = TilePortal.class)
    public static Portal portal = new Portal();
    //endregion

    //region Machines

    @Feature(registryName = "generator", tileEntity = TileGenerator.class, itemBlock = ItemBlockBCore.class, cTab = 1)
    public static Generator generator = new Generator();

    @Feature(registryName = "grinder", tileEntity = TileGrinder.class, itemBlock = ItemBlockBCore.class, cTab = 1)
    public static Grinder grinder = new Grinder();

    @Feature(registryName = "particle_generator", variantMap = {"0:type=normal", "1:type=inverted", "2:type=stabilizer"}, cTab = 1, itemBlock = ItemBlockBasic.class)
    public static ParticleGenerator particleGenerator = new ParticleGenerator();

    @Feature(registryName = "energy_infuser", tileEntity = TileEnergyInfuser.class, itemBlock = ItemBlockBCore.class, cTab = 1)
    public static EnergyInfuser energyInfuser = new EnergyInfuser();

    @Feature(registryName = "crafting_pedestal", variantMap = {"0:facing=up,tier=basic", "1:facing=up,tier=wyvern", "2:facing=up,tier=draconic", "3:facing=up,tier=chaotic"}, tileEntity = TileCraftingPedestal.class, itemBlock = ItemBlockBasic.class, cTab = 1)
    public static CraftingPedestal craftingPedestal = new CraftingPedestal();

    @Feature(registryName = "dislocator_receptacle", tileEntity = TileDislocatorReceptacle.class, itemBlock = ItemBlockBCore.class, cTab = 1)
    public static DislocatorReceptacle dislocatorReceptacle = new DislocatorReceptacle();

    @Feature(registryName = "dislocator_pedestal", tileEntity = TileDislocatorPedestal.class, cTab = 1)
    public static DislocatorPedestal dislocatorPedestal = new DislocatorPedestal();

    @Feature(registryName = "rain_sensor", tileEntity = TileRainSensor.class, cTab = 1)
    public static RainSensor rainSensor = new RainSensor();

    @Feature(registryName = "diss_enchanter", tileEntity = TileDissEnchanter.class, itemBlock = ItemBlockBCore.class, cTab = 1)
    public static DissEnchanter dissEnchanter = new DissEnchanter();

    @Feature(registryName = "potentiometer", tileEntity = TilePotentiometer.class, cTab = 1)
    public static Potentiometer potentiometer = new Potentiometer();

    @Feature(registryName = "entity_detector", tileEntity = TileEntityDetector.class, variantMap = {"0:advanced=false", "1:advanced=true"}, itemBlock = ItemBlockBasic.class, cTab = 1)
    public static EntityDetector entityDetector = new EntityDetector();

    //endregion

    //region Advanced Machines

    @Feature(registryName = "energy_storage_core", tileEntity = TileEnergyStorageCore.class, cTab = 1)
    public static EnergyStorageCore energyStorageCore = new EnergyStorageCore();

    @Feature(registryName = "energy_pylon", tileEntity = TileEnergyPylon.class, cTab = 1)
    public static EnergyPylon energyPylon = new EnergyPylon();

    @Feature(registryName = "invis_e_core_block", tileEntity = TileInvisECoreBlock.class, cTab = -1)
    public static InvisECoreBlock invisECoreBlock = new InvisECoreBlock();

    @Feature(registryName = "fusion_crafting_core", tileEntity = TileFusionCraftingCore.class, itemBlock = ItemBlockBCore.class, cTab = 1)
    public static FusionCraftingCore fusionCraftingCore = new FusionCraftingCore();

    @Feature(registryName = "celestial_manipulator", tileEntity = TileCelestialManipulator.class, cTab = 1)//, itemBlock = ItemBlockBCore.class, cTab = 1)
    public static CelestialManipulator celestialManipulator = new CelestialManipulator();

    @Feature(registryName = "energy_crystal", tileEntity = TileCrystalDirectIO.class, itemBlock = ItemBlockBCore.class)
    public static EnergyCrystal energyCrystal = new EnergyCrystal();

    @Feature(registryName = "flow_gate", variantMap = {"0:facing=north,type=flux", "8:facing=north,type=fluid"}, itemBlock = ItemBlockBCore.class, cTab = 1)
    public static FlowGate flowGate = new FlowGate();

    @Feature(registryName = "reactor_core", tileEntity = TileReactorCore.class, itemBlock = ItemBlockBCore.class, cTab = 1)
    public static ReactorCore reactorCore = new ReactorCore();

    @Feature(registryName = "reactor_part", cTab = 1)
    public static ReactorPart reactorPart = new ReactorPart();

    @Feature(registryName = "reactor_component", itemBlock = ItemBlockBCore.class, cTab = 1)
    public static ReactorComponent reactorComponent = new ReactorComponent();

    @Feature(registryName = "draconic_spawner", tileEntity = TileStabilizedSpawner.class, itemBlock = ItemBlockBCore.class)
    public static StabilizedSpawner stabilizedSpawner = new StabilizedSpawner();

    @Feature(registryName = "draconium_chest", tileEntity = TileDraconiumChest.class, itemBlock = ItemBlockBCore.class)
    public static DraconiumChest draconiumChest = new DraconiumChest();

    //endregion

    //region Exotic Blocks

    @Feature(registryName = "chaos_crystal", tileEntity = TileChaosCrystal.class, cTab = -1)
    public static ChaosCrystal chaosCrystal = new ChaosCrystal();

    @Feature(registryName = "chaos_shard_atmos", cTab = -1)
    public static ChaosShardAtmos chaosShardAtmos = new ChaosShardAtmos();

    @Feature(registryName = "creative_rf_source", tileEntity = TileCreativeRFCapacitor.class, cTab = 1)
    public static CreativeRFSource creativeRFSource = new CreativeRFSource();

    @Feature(registryName = "placed_item", tileEntity = TilePlacedItem.class, cTab = -1)
    public static PlacedItem placedItem = new PlacedItem();

    //endregion

	/* ------------------ Items ------------------ */

    //region Crafting Components / Base items
    @Feature(registryName = "draconium_dust", stateOverride = "simple_components#type=draconiumDust")
    public static Item draconiumDust = new Item();

    @Feature(registryName = "draconium_ingot", stateOverride = "simple_components#type=draconiumIngot")
    public static Item draconiumIngot = new Item();

    @Feature(registryName = "draconic_ingot", stateOverride = "simple_components#type=draconicIngot")
    public static Item draconicIngot = new Item();

    @Feature(registryName = "draconic_core", stateOverride = "simple_components#type=draconicCore")
    public static ItemCore draconicCore = new ItemCore();

    @Feature(registryName = "wyvern_core", stateOverride = "simple_components#type=wyvernCore")
    public static ItemCore wyvernCore = new ItemCore();

    @Feature(registryName = "awakened_core", stateOverride = "simple_components#type=awakenedCore")
    public static ItemCore awakenedCore = new ItemCore();

    @Feature(registryName = "chaotic_core", stateOverride = "simple_components#type=chaoticCore")
    public static ItemCore chaoticCore = new ItemCore();

    @Feature(registryName = "wyvern_energy_core", stateOverride = "simple_components#type=wyvernECore")
    public static Item wyvernEnergyCore = new Item();

    @Feature(registryName = "draconic_energy_core", stateOverride = "simple_components#type=draconicECore")
    public static Item draconicEnergyCore = new Item();

    @Feature(registryName = "dragon_heart", stateOverride = "simple_components#type=dragonHeart")
    public static ItemPersistent dragonHeart = new ItemPersistent();

    @Feature(registryName = "debugger", stateOverride = "simple_components#type=draconicIngot")
    public static Item debugger = new Debugger();

    @Feature(registryName = "nugget", variantMap = {"0:type=draconium", "1:type=awakened"})
    public static ItemSimpleSubs nugget = new ItemSimpleSubs(new String[]{"0:draconium", "1:awakened"});

    @Feature(registryName = "chaos_shard", variantMap = {"0:type=shard", "1:type=fragLarge", "2:type=fragMedium", "3:type=fragSmall"})
    public static ItemSimpleSubs chaosShard = new ItemSimpleSubs(new String[]{"0:shard", "1:fragLarge", "2:fragMedium", "3:fragSmall"});


    //endregion

    //region Tools

    @Feature(registryName = "draconium_capacitor", variantMap = {"0:type=wyvern", "1:type=draconic", "2:type=creative"}, cTab = 1)
    public static DraconiumCapacitor draconiumCapacitor = new DraconiumCapacitor();
    public static ItemStack wyvernCapacitor = new ItemStack(draconiumCapacitor, 1, 0);
    public static ItemStack draconicCapacitor = new ItemStack(draconiumCapacitor, 1, 1);
    public static ItemStack creativeCapacitor = new ItemStack(draconiumCapacitor, 1, 2);

    @Feature(registryName = "wyvern_axe", cTab = 1)
    public static WyvernAxe wyvernAxe = new WyvernAxe();

    @Feature(registryName = "wyvern_bow", cTab = 1)
    public static WyvernBow wyvernBow = new WyvernBow();

    @Feature(registryName = "wyvern_pick", cTab = 1)
    public static WyvernPick wyvernPick = new WyvernPick();

    @Feature(registryName = "wyvern_shovel", cTab = 1)
    public static WyvernShovel wyvernShovel = new WyvernShovel();

    @Feature(registryName = "wyvern_sword", cTab = 1)
    public static WyvernSword wyvernSword = new WyvernSword();


    @Feature(registryName = "draconic_axe", cTab = 1)
    public static DraconicAxe draconicAxe = new DraconicAxe();

    @Feature(registryName = "draconic_bow", cTab = 1)
    public static DraconicBow draconicBow = new DraconicBow();

    @Feature(registryName = "draconic_hoe", cTab = 1)
    public static DraconicHoe draconicHoe = new DraconicHoe();

    @Feature(registryName = "draconic_pick", cTab = 1)
    public static DraconicPick draconicPick = new DraconicPick();

    @Feature(registryName = "draconic_shovel", cTab = 1)
    public static DraconicShovel draconicShovel = new DraconicShovel();

    @Feature(registryName = "draconic_staff_of_power", cTab = 1)
    public static DraconicStaffOfPower draconicStaffOfPower = new DraconicStaffOfPower();

    @Feature(registryName = "draconic_sword", cTab = 1)
    public static DraconicSword draconicSword = new DraconicSword();

    @Feature(registryName = "tool_upgrade", cTab = 1)
    public static ToolUpgrade toolUpgrade = new ToolUpgrade();

    @Feature(registryName = "dislocator", cTab = 1)
    public static Dislocator dislocator = new Dislocator();

    @Feature(registryName = "dislocator_advanced", cTab = 1)
    public static DislocatorAdvanced dislocatorAdvanced = new DislocatorAdvanced();

    @Feature(registryName = "magnet", cTab = 1, stateOverride = "misc#", variantMap = {"0:type=magnet_basic", "1:type=magnet_advanced"})
    public static Magnet magnet = new Magnet();

    @Feature(registryName = "crystal_binder", stateOverride = "misc#type=crystal_binder")
    public static CrystalBinder crystalBinder = new CrystalBinder();

    //endregion

    //region Armor
    @Feature(registryName = "wyvern_helm", cTab = 1, stateOverride = "armor#type=wyvernHelm")
    public static WyvernArmor wyvernHelm = new WyvernArmor(0, EntityEquipmentSlot.HEAD);

    @Feature(registryName = "wyvern_chest", cTab = 1, stateOverride = "armor#type=wyvernChest")
    public static WyvernArmor wyvernChest = new WyvernArmor(1, EntityEquipmentSlot.CHEST);

    @Feature(registryName = "wyvern_legs", cTab = 1, stateOverride = "armor#type=wyvernLegs")
    public static WyvernArmor wyvernLegs = new WyvernArmor(2, EntityEquipmentSlot.LEGS);

    @Feature(registryName = "wyvern_boots", cTab = 1, stateOverride = "armor#type=wyvernBoots")
    public static WyvernArmor wyvernBoots = new WyvernArmor(3, EntityEquipmentSlot.FEET);

    @Feature(registryName = "draconic_helm", cTab = 1, stateOverride = "armor#type=draconicHelm")
    public static DraconicArmor draconicHelm = new DraconicArmor(0, EntityEquipmentSlot.HEAD);

    @Feature(registryName = "draconic_chest", cTab = 1, stateOverride = "armor#type=draconicChest")
    public static DraconicArmor draconicChest = new DraconicArmor(1, EntityEquipmentSlot.CHEST);

    @Feature(registryName = "draconic_legs", cTab = 1, stateOverride = "armor#type=draconicLegs")
    public static DraconicArmor draconicLegs = new DraconicArmor(2, EntityEquipmentSlot.LEGS);

    @Feature(registryName = "draconic_boots", cTab = 1, stateOverride = "armor#type=draconicBoots")
    public static DraconicArmor draconicBoots = new DraconicArmor(3, EntityEquipmentSlot.FEET);

    //endregion

    //region Misc

    @Feature(registryName = "info_tablet", stateOverride = "simple_components#type=info_tablet", cTab = 1)
    public static InfoTablet infoTablet = new InfoTablet();

    @Feature(registryName = "mob_soul")
    public static MobSoul mobSoul = new MobSoul();


    //endregion

    //region Exotic Items

    @Feature(registryName = "creative_exchanger", stateOverride = "simple_components#type=chaoticCore", cTab = 1)
    public static CreativeExchanger creativeExchanger = new CreativeExchanger();

    @Feature(registryName = "ender_energy_manipulator", cTab = 1)
    public static EnderEnergyManipulator enderEnergyManipulator = new EnderEnergyManipulator();

    //endregion





    //In Progress... To be sorted into categories when done*

    //These are not ready for release and so are commented out for release builds


//

//

//
//
//
//

}