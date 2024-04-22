package com.brandon3055.draconicevolution.init;

import com.brandon3055.brandonscore.api.TechLevel;
import com.brandon3055.brandonscore.blocks.BlockBCore;
import com.brandon3055.brandonscore.blocks.ItemBlockBCore;
import com.brandon3055.brandonscore.worldentity.WorldEntityHandler;
import com.brandon3055.brandonscore.worldentity.WorldEntityType;
import com.brandon3055.draconicevolution.api.DraconicAPI;
import com.brandon3055.draconicevolution.api.crafting.FusionRecipe;
import com.brandon3055.draconicevolution.blocks.*;
import com.brandon3055.draconicevolution.blocks.energynet.EnergyCrystal;
import com.brandon3055.draconicevolution.blocks.energynet.tileentity.TileCrystalDirectIO;
import com.brandon3055.draconicevolution.blocks.energynet.tileentity.TileCrystalRelay;
import com.brandon3055.draconicevolution.blocks.energynet.tileentity.TileCrystalWirelessIO;
import com.brandon3055.draconicevolution.blocks.machines.*;
import com.brandon3055.draconicevolution.blocks.reactor.ReactorComponent;
import com.brandon3055.draconicevolution.blocks.reactor.ReactorCore;
import com.brandon3055.draconicevolution.blocks.reactor.tileentity.TileReactorCore;
import com.brandon3055.draconicevolution.blocks.reactor.tileentity.TileReactorInjector;
import com.brandon3055.draconicevolution.blocks.reactor.tileentity.TileReactorStabilizer;
import com.brandon3055.draconicevolution.blocks.tileentity.*;
import com.brandon3055.draconicevolution.blocks.tileentity.chest.TileDraconiumChest;
import com.brandon3055.draconicevolution.blocks.tileentity.flowgate.TileFluidGate;
import com.brandon3055.draconicevolution.blocks.tileentity.flowgate.TileFluxGate;
import com.brandon3055.draconicevolution.entity.GuardianCrystalEntity;
import com.brandon3055.draconicevolution.entity.guardian.DraconicGuardianEntity;
import com.brandon3055.draconicevolution.entity.guardian.GuardianFightManager;
import com.brandon3055.draconicevolution.entity.guardian.GuardianProjectileEntity;
import com.brandon3055.draconicevolution.entity.guardian.GuardianWither;
import com.brandon3055.draconicevolution.entity.projectile.DraconicArrowEntity;
import com.brandon3055.draconicevolution.inventory.*;
import com.brandon3055.draconicevolution.items.InfoTablet;
import com.brandon3055.draconicevolution.items.ItemCore;
import com.brandon3055.draconicevolution.items.MobSoul;
import com.brandon3055.draconicevolution.items.equipment.*;
import com.brandon3055.draconicevolution.items.tools.*;
import com.brandon3055.draconicevolution.magic.EnchantmentReaper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.material.MapColor;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import static com.brandon3055.draconicevolution.DraconicEvolution.MODID;

/**
 * Created by brandon3055 on 18/3/2016.
 * This class contains a reference to all blocks and items in Draconic Evolution
 */
public class DEContent {

    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, MODID);
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);

    public static final DeferredRegister<BlockEntityType<?>> TILES_ENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, MODID);
    public static final DeferredRegister<MenuType<?>> MENU_TYPES = DeferredRegister.create(ForgeRegistries.MENU_TYPES, MODID);

    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, MODID);
    public static final DeferredRegister<WorldEntityType<?>> WORLD_ENTITY_TYPES = DeferredRegister.create(WorldEntityHandler.ENTITY_TYPE, MODID);

    public static final DeferredRegister<RecipeType<?>> RECIPE_TYPES = DeferredRegister.create(ForgeRegistries.RECIPE_TYPES, MODID);
    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIAL = DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, MODID);
    public static final DeferredRegister<Enchantment> ENCHANTMENTS = DeferredRegister.create(ForgeRegistries.ENCHANTMENTS, MODID);


    public static void init() {
        IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();
        BLOCKS.register(eventBus);
        ITEMS.register(eventBus);
        TILES_ENTITIES.register(eventBus);
        MENU_TYPES.register(eventBus);
        ENTITY_TYPES.register(eventBus);
        WORLD_ENTITY_TYPES.register(eventBus);
        RECIPE_TYPES.register(eventBus);
        RECIPE_SERIAL.register(eventBus);
        ENCHANTMENTS.register(eventBus);
        eventBus.addListener(DEContent::registerAttributes);
    }

    //#################################################################
    // Blocks
    //#################################################################

    public static final Properties MACHINE = Properties.of().mapColor(MapColor.COLOR_GRAY).sound(SoundType.METAL).strength(3.0F, 8F).noOcclusion().requiresCorrectToolForDrops();
    public static final Properties HARDENED_MACHINE = Properties.of().mapColor(MapColor.COLOR_GRAY).sound(SoundType.METAL).strength(20.0F, 600F).noOcclusion().requiresCorrectToolForDrops();
    public static final Properties STORAGE_BLOCK = Properties.of().mapColor(MapColor.COLOR_GRAY).sound(SoundType.METAL).strength(30.0F, 600F).requiresCorrectToolForDrops();
    public static final Properties STONE_PROP = Properties.of().mapColor(MapColor.COLOR_GRAY).strength(1.5F, 6F).requiresCorrectToolForDrops();
    public static final Properties ORE = Properties.of().mapColor(MapColor.COLOR_GRAY).strength(6.0F, 16F).requiresCorrectToolForDrops();
    //@formatter:off
    //Machines
    public static final RegistryObject<Generator> GENERATOR                             = BLOCKS.register("generator",                          () -> new Generator(MACHINE));
    public static final RegistryObject<EnergyTransfuser> ENERGY_TRANSFUSER              = BLOCKS.register("energy_transfuser",                  () -> new EnergyTransfuser(MACHINE));
    public static final RegistryObject<DislocatorPedestal> DISLOCATOR_PEDESTAL          = BLOCKS.register("dislocator_pedestal",                () -> new DislocatorPedestal(MACHINE));
    public static final RegistryObject<DislocatorReceptacle> DISLOCATOR_RECEPTACLE      = BLOCKS.register("dislocator_receptacle",              () -> new DislocatorReceptacle(MACHINE));
    public static final RegistryObject<CreativeOPSource> CREATIVE_OP_CAPACITOR          = BLOCKS.register("creative_op_capacitor",              () -> new CreativeOPSource(MACHINE));
    public static final RegistryObject<EntityDetector> ENTITY_DETECTOR                  = BLOCKS.register("entity_detector",                    () -> new EntityDetector(MACHINE, false));
    public static final RegistryObject<EntityDetector> ENTITY_DETECTOR_ADVANCED         = BLOCKS.register("entity_detector_advanced",           () -> new EntityDetector(MACHINE, true));
    public static final RegistryObject<StabilizedSpawner> STABILIZED_SPAWNER            = BLOCKS.register("stabilized_spawner",                 () -> new StabilizedSpawner(MACHINE));
    public static final RegistryObject<DraconiumChest> DRACONIUM_CHEST                  = BLOCKS.register("draconium_chest",                    () -> new DraconiumChest(HARDENED_MACHINE));
    public static final RegistryObject<ParticleGenerator> PARTICLE_GENERATOR            = BLOCKS.register("particle_generator",                 () -> new ParticleGenerator(MACHINE));
    public static final RegistryObject<DislocationInhibitor> DISLOCATION_INHIBITOR      = BLOCKS.register("dislocation_inhibitor",              () -> new DislocationInhibitor(MACHINE));
    //Stone Type
    public static final RegistryObject<RainSensor> RAIN_SENSOR                          = BLOCKS.register("rain_sensor",                        () -> new RainSensor(STONE_PROP));
    public static final RegistryObject<Potentiometer> POTENTIOMETER                     = BLOCKS.register("potentiometer",                      () -> new Potentiometer(STONE_PROP));
    //Hardened Machine
    public static final RegistryObject<Grinder> GRINDER                                 = BLOCKS.register("grinder",                            () -> new Grinder(HARDENED_MACHINE));
    public static final RegistryObject<Disenchanter> DISENCHANTER                       = BLOCKS.register("disenchanter",                       () -> new Disenchanter(HARDENED_MACHINE));
    public static final RegistryObject<CelestialManipulator> CELESTIAL_MANIPULATOR      = BLOCKS.register("celestial_manipulator",              () -> new CelestialManipulator(HARDENED_MACHINE));
    public static final RegistryObject<FlowGate> FLUX_GATE                              = BLOCKS.register("flux_gate",                          () -> new FlowGate(HARDENED_MACHINE, true));
    public static final RegistryObject<FlowGate> FLUID_GATE                             = BLOCKS.register("fluid_gate",                         () -> new FlowGate(HARDENED_MACHINE, false));
    //Fusion Crafting
    public static final RegistryObject<FusionCraftingCore> CRAFTING_CORE                = BLOCKS.register("crafting_core",                      () -> new FusionCraftingCore(HARDENED_MACHINE));
    public static final RegistryObject<CraftingInjector> BASIC_CRAFTING_INJECTOR        = BLOCKS.register("basic_crafting_injector",            () -> new CraftingInjector(HARDENED_MACHINE, TechLevel.DRACONIUM));
    public static final RegistryObject<CraftingInjector> WYVERN_CRAFTING_INJECTOR       = BLOCKS.register("wyvern_crafting_injector",           () -> new CraftingInjector(HARDENED_MACHINE, TechLevel.WYVERN));
    public static final RegistryObject<CraftingInjector> AWAKENED_CRAFTING_INJECTOR     = BLOCKS.register("awakened_crafting_injector",         () -> new CraftingInjector(HARDENED_MACHINE, TechLevel.DRACONIC));
    public static final RegistryObject<CraftingInjector> CHAOTIC_CRAFTING_INJECTOR      = BLOCKS.register("chaotic_crafting_injector",          () -> new CraftingInjector(HARDENED_MACHINE, TechLevel.CHAOTIC));
    //Energy Core
    public static final RegistryObject<EnergyCore> ENERGY_CORE                          = BLOCKS.register("energy_core",                        () -> new EnergyCore(HARDENED_MACHINE));
    public static final RegistryObject<EnergyCoreStabilizer> ENERGY_CORE_STABILIZER     = BLOCKS.register("energy_core_stabilizer",             () -> new EnergyCoreStabilizer(HARDENED_MACHINE));
    public static final RegistryObject<EnergyPylon> ENERGY_PYLON                        = BLOCKS.register("energy_pylon",                       () -> new EnergyPylon(HARDENED_MACHINE));
    public static final RegistryObject<StructureBlock> STRUCTURE_BLOCK                  = BLOCKS.register("structure_block",                    () -> new StructureBlock(Properties.of().mapColor(MapColor.COLOR_GRAY).strength(5.0F, 12F).noOcclusion()));
    //Reactor
    public static final RegistryObject<ReactorCore> REACTOR_CORE                        = BLOCKS.register("reactor_core",                       () -> new ReactorCore(HARDENED_MACHINE));
    public static final RegistryObject<ReactorComponent> REACTOR_STABILIZER             = BLOCKS.register("reactor_stabilizer",                 () -> new ReactorComponent(Properties.of().mapColor(MapColor.COLOR_GRAY).strength(5.0F, 6000F).noOcclusion(), false));
    public static final RegistryObject<ReactorComponent> REACTOR_INJECTOR               = BLOCKS.register("reactor_injector",                   () -> new ReactorComponent(Properties.of().mapColor(MapColor.COLOR_GRAY).strength(5.0F, 6000F).noOcclusion(), true));
    //Ore
    public static final RegistryObject<DraconiumOre> OVERWORLD_DRACONIUM_ORE            = BLOCKS.register("overworld_draconium_ore",            () -> new DraconiumOre(ORE));
    public static final RegistryObject<DraconiumOre> DEEPSLATE_DRACONIUM_ORE            = BLOCKS.register("deepslate_draconium_ore",            () -> new DraconiumOre(ORE));
    public static final RegistryObject<DraconiumOre> NETHER_DRACONIUM_ORE               = BLOCKS.register("nether_draconium_ore",               () -> new DraconiumOre(ORE));
    public static final RegistryObject<DraconiumOre> END_DRACONIUM_ORE                  = BLOCKS.register("end_draconium_ore",                  () -> new DraconiumOre(ORE));
    //Storage Blocks
    public static final RegistryObject<DraconiumBlock> DRACONIUM_BLOCK                  = BLOCKS.register("draconium_block",                    () -> (DraconiumBlock) new DraconiumBlock(STORAGE_BLOCK).setMobResistant().setExplosionResistant());
    public static final RegistryObject<DraconiumBlock> AWAKENED_DRACONIUM_BLOCK         = BLOCKS.register("awakened_draconium_block",           () -> (DraconiumBlock) new DraconiumBlock(STORAGE_BLOCK).setMobResistant().setExplosionResistant());
    //Special
    public static final RegistryObject<Portal> PORTAL                                   = BLOCKS.register("portal",                             () -> new Portal(Properties.of().noOcclusion().noCollission().strength(-1F)));
    public static final RegistryObject<ChaosCrystal> CHAOS_CRYSTAL                      = BLOCKS.register("chaos_crystal",                      () -> new ChaosCrystal(Properties.of().strength(100, 4000).noOcclusion()));
    public static final RegistryObject<ChaosCrystal> CHAOS_CRYSTAL_PART                 = BLOCKS.register("chaos_crystal_part",                 () -> new ChaosCrystal(Properties.of().strength(100, 4000).noOcclusion()));
    public static final RegistryObject<BlockBCore> INFUSED_OBSIDIAN                     = BLOCKS.register("infused_obsidian",                   () -> new BlockBCore(Properties.of().mapColor(MapColor.COLOR_BLACK).strength(100.0F, 2400.0F)).setMobResistant().setExplosionResistant());

    public static final RegistryObject<PlacedItem> PLACED_ITEM                          = BLOCKS.register("placed_item",                        () -> new PlacedItem(Properties.of().strength(5F, 12F).noOcclusion()));

    //Energy Crystals
    public static final Properties CRYSTAL_B = Properties.of().mapColor(DyeColor.BLUE).strength(3.0F, 8F);
    public static final Properties CRYSTAL_W = Properties.of().mapColor(DyeColor.PURPLE).strength(5.0F, 16F);
    public static final Properties CRYSTAL_D = Properties.of().mapColor(DyeColor.ORANGE).strength(8.0F, 32F);
    public static final RegistryObject<EnergyCrystal> BASIC_IO_CRYSTAL                  = BLOCKS.register("basic_io_crystal",                   () -> new EnergyCrystal(CRYSTAL_B, TechLevel.DRACONIUM, EnergyCrystal.CrystalType.CRYSTAL_IO));
    public static final RegistryObject<EnergyCrystal> WYVERN_IO_CRYSTAL                 = BLOCKS.register("wyvern_io_crystal",                  () -> new EnergyCrystal(CRYSTAL_W, TechLevel.WYVERN, EnergyCrystal.CrystalType.CRYSTAL_IO));
    public static final RegistryObject<EnergyCrystal> DRACONIC_IO_CRYSTAL               = BLOCKS.register("draconic_io_crystal",                () -> new EnergyCrystal(CRYSTAL_D, TechLevel.DRACONIC, EnergyCrystal.CrystalType.CRYSTAL_IO));
    public static final RegistryObject<EnergyCrystal> BASIC_RELAY_CRYSTAL               = BLOCKS.register("basic_relay_crystal",                () -> new EnergyCrystal(CRYSTAL_B, TechLevel.DRACONIUM, EnergyCrystal.CrystalType.RELAY));
    public static final RegistryObject<EnergyCrystal> WYVERN_RELAY_CRYSTAL              = BLOCKS.register("wyvern_relay_crystal",               () -> new EnergyCrystal(CRYSTAL_W, TechLevel.WYVERN, EnergyCrystal.CrystalType.RELAY));
    public static final RegistryObject<EnergyCrystal> DRACONIC_RELAY_CRYSTAL            = BLOCKS.register("draconic_relay_crystal",             () -> new EnergyCrystal(CRYSTAL_D, TechLevel.DRACONIC, EnergyCrystal.CrystalType.RELAY));
    public static final RegistryObject<EnergyCrystal> BASIC_WIRELESS_CRYSTAL            = BLOCKS.register("basic_wireless_crystal",             () -> new EnergyCrystal(CRYSTAL_B, TechLevel.DRACONIUM, EnergyCrystal.CrystalType.WIRELESS));
    public static final RegistryObject<EnergyCrystal> WYVERN_WIRELESS_CRYSTAL           = BLOCKS.register("wyvern_wireless_crystal",            () -> new EnergyCrystal(CRYSTAL_W, TechLevel.WYVERN, EnergyCrystal.CrystalType.WIRELESS));
    public static final RegistryObject<EnergyCrystal> DRACONIC_WIRELESS_CRYSTAL         = BLOCKS.register("draconic_wireless_crystal",          () -> new EnergyCrystal(CRYSTAL_D, TechLevel.DRACONIC, EnergyCrystal.CrystalType.WIRELESS));

    //@formatter:on

    //#################################################################
    // Items
    //#################################################################

    //@formatter:off
    //Components
    public static final RegistryObject<Item> DUST_DRACONIUM                         = ITEMS.register("draconium_dust",                      () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> DUST_DRACONIUM_AWAKENED                = ITEMS.register("awakened_draconium_dust",             () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> INGOT_DRACONIUM                        = ITEMS.register("draconium_ingot",                     () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> INGOT_DRACONIUM_AWAKENED               = ITEMS.register("awakened_draconium_ingot",            () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> NUGGET_DRACONIUM                       = ITEMS.register("draconium_nugget",                    () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> NUGGET_DRACONIUM_AWAKENED              = ITEMS.register("awakened_draconium_nugget",           () -> new Item(new Item.Properties()));
    public static final RegistryObject<ItemCore> CORE_DRACONIUM                     = ITEMS.register("draconium_core",                      () -> new ItemCore(new Item.Properties()));
    public static final RegistryObject<ItemCore> CORE_WYVERN                        = ITEMS.register("wyvern_core",                         () -> new ItemCore(new Item.Properties()));
    public static final RegistryObject<ItemCore> CORE_AWAKENED                      = ITEMS.register("awakened_core",                       () -> new ItemCore(new Item.Properties()));
    public static final RegistryObject<ItemCore> CORE_CHAOTIC                       = ITEMS.register("chaotic_core",                        () -> new ItemCore(new Item.Properties()));
    public static final RegistryObject<Item> ENERGY_CORE_WYVERN                     = ITEMS.register("wyvern_energy_core",                  () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> ENERGY_CORE_DRACONIC                   = ITEMS.register("draconic_energy_core",                () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> ENERGY_CORE_CHAOTIC                    = ITEMS.register("chaotic_energy_core",                 () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> DRAGON_HEART                           = ITEMS.register("dragon_heart",                        () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> CHAOS_SHARD                            = ITEMS.register("chaos_shard",                         () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> CHAOS_FRAG_LARGE                       = ITEMS.register("large_chaos_frag",                    () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> CHAOS_FRAG_MEDIUM                      = ITEMS.register("medium_chaos_frag",                   () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> CHAOS_FRAG_SMALL                       = ITEMS.register("small_chaos_frag",                    () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> MODULE_CORE                            = ITEMS.register("module_core",                         () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> REACTOR_PRT_STAB_FRAME                 = ITEMS.register("reactor_prt_stab_frame",              () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> REACTOR_PRT_IN_ROTOR                   = ITEMS.register("reactor_prt_in_rotor",                () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> REACTOR_PRT_OUT_ROTOR                  = ITEMS.register("reactor_prt_out_rotor",               () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> REACTOR_PRT_ROTOR_FULL                 = ITEMS.register("reactor_prt_rotor_full",              () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> REACTOR_PRT_FOCUS_RING                 = ITEMS.register("reactor_prt_focus_ring",              () -> new Item(new Item.Properties()));
    //Misc Tools
    public static final RegistryObject<Magnet> MAGNET                               = ITEMS.register("magnet",                              () -> new Magnet(new Item.Properties().stacksTo(1), 8));
    public static final RegistryObject<Magnet> MAGNET_ADVANCED                      = ITEMS.register("advanced_magnet",                     () -> new Magnet(new Item.Properties().stacksTo(1), 32));
    public static final RegistryObject<Dislocator> DISLOCATOR                       = ITEMS.register("dislocator",                          () -> new Dislocator(new Item.Properties().stacksTo(1).durability(31)));
    public static final RegistryObject<DislocatorAdvanced> DISLOCATOR_ADVANCED      = ITEMS.register("advanced_dislocator",                 () -> new DislocatorAdvanced(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<BoundDislocator> DISLOCATOR_P2P              = ITEMS.register("p2p_dislocator",                      () -> new BoundDislocator(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<BoundDislocator> DISLOCATOR_P2P_UNBOUND      = ITEMS.register("p2p_dislocator_unbound",              () -> new BoundDislocator(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<BoundDislocator> DISLOCATOR_PLAYER           = ITEMS.register("player_dislocator",                   () -> new BoundDislocator(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<BoundDislocator> DISLOCATOR_PLAYER_UNBOUND   = ITEMS.register("player_dislocator_unbound",           () -> new BoundDislocator(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<CrystalBinder> CRYSTAL_BINDER                = ITEMS.register("crystal_binder",                      () -> new CrystalBinder(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<InfoTablet> INFO_TABLET                      = ITEMS.register("info_tablet",                         () -> new InfoTablet(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<MobSoul> MOB_SOUL                            = ITEMS.register("mob_soul",                            () -> new MobSoul(new Item.Properties()));
    //Tools
    public static final TechProperties WYVERN_TOOLS = (TechProperties) new TechProperties(TechLevel.WYVERN).rarity(Rarity.UNCOMMON).durability(-1).fireResistant();
    public static final TechProperties DRACONIC_TOOLS = (TechProperties) new TechProperties(TechLevel.DRACONIC).rarity(Rarity.RARE).durability(-1).fireResistant();
    public static final TechProperties CHAOTIC_TOOLS = (TechProperties) new TechProperties(TechLevel.CHAOTIC).rarity(Rarity.EPIC).durability(-1).fireResistant();
    public static DETier WYVERN_TIER = new DETier(TechLevel.WYVERN);
    public static DETier DRACONIC_TIER = new DETier(TechLevel.DRACONIUM);
    public static DETier CHAOTIC_TIER = new DETier(TechLevel.CHAOTIC);
    public static final RegistryObject<DraconiumCapacitor> CAPACITOR_WYVERN             = ITEMS.register("wyvern_capacitor",                    () -> new DraconiumCapacitor(WYVERN_TOOLS));
    public static final RegistryObject<DraconiumCapacitor> CAPACITOR_DRACONIC           = ITEMS.register("draconic_capacitor",                  () -> new DraconiumCapacitor(DRACONIC_TOOLS));
    public static final RegistryObject<DraconiumCapacitor> CAPACITOR_CHAOTIC            = ITEMS.register("chaotic_capacitor",                   () -> new DraconiumCapacitor(CHAOTIC_TOOLS));
    public static final RegistryObject<DraconiumCapacitor> CAPACITOR_CREATIVE           = ITEMS.register("creative_capacitor",                  () -> new DraconiumCapacitor(CHAOTIC_TOOLS));
    public static final RegistryObject<ModularShovel> SHOVEL_WYVERN                     = ITEMS.register("wyvern_shovel",                       () -> new ModularShovel(WYVERN_TIER, WYVERN_TOOLS));
    public static final RegistryObject<ModularShovel> SHOVEL_DRACONIC                   = ITEMS.register("draconic_shovel",                     () -> new ModularShovel(DRACONIC_TIER, DRACONIC_TOOLS));
    public static final RegistryObject<ModularShovel> SHOVEL_CHAOTIC                    = ITEMS.register("chaotic_shovel",                      () -> new ModularShovel(CHAOTIC_TIER, CHAOTIC_TOOLS));
    public static final RegistryObject<ModularHoe> HOE_WYVERN                           = ITEMS.register("wyvern_hoe",                          () -> new ModularHoe(WYVERN_TIER, WYVERN_TOOLS));
    public static final RegistryObject<ModularHoe> HOE_DRACONIC                         = ITEMS.register("draconic_hoe",                        () -> new ModularHoe(DRACONIC_TIER, DRACONIC_TOOLS));
    public static final RegistryObject<ModularHoe> HOE_CHAOTIC                          = ITEMS.register("chaotic_hoe",                         () -> new ModularHoe(CHAOTIC_TIER, CHAOTIC_TOOLS));
    public static final RegistryObject<ModularPickaxe> PICKAXE_WYVERN                   = ITEMS.register("wyvern_pickaxe",                      () -> new ModularPickaxe(WYVERN_TIER, WYVERN_TOOLS));
    public static final RegistryObject<ModularPickaxe> PICKAXE_DRACONIC                 = ITEMS.register("draconic_pickaxe",                    () -> new ModularPickaxe(DRACONIC_TIER, DRACONIC_TOOLS));
    public static final RegistryObject<ModularPickaxe> PICKAXE_CHAOTIC                  = ITEMS.register("chaotic_pickaxe",                     () -> new ModularPickaxe(CHAOTIC_TIER, CHAOTIC_TOOLS));
    public static final RegistryObject<ModularAxe> AXE_WYVERN                           = ITEMS.register("wyvern_axe",                          () -> new ModularAxe(WYVERN_TIER, WYVERN_TOOLS));
    public static final RegistryObject<ModularAxe> AXE_DRACONIC                         = ITEMS.register("draconic_axe",                        () -> new ModularAxe(DRACONIC_TIER, DRACONIC_TOOLS));
    public static final RegistryObject<ModularAxe> AXE_CHAOTIC                          = ITEMS.register("chaotic_axe",                         () -> new ModularAxe(CHAOTIC_TIER, CHAOTIC_TOOLS));
    public static final RegistryObject<ModularBow> BOW_WYVERN                           = ITEMS.register("wyvern_bow",                          () -> new ModularBow(WYVERN_TOOLS));
    public static final RegistryObject<ModularBow> BOW_DRACONIC                         = ITEMS.register("draconic_bow",                        () -> new ModularBow(DRACONIC_TOOLS));
    public static final RegistryObject<ModularBow> BOW_CHAOTIC                          = ITEMS.register("chaotic_bow",                         () -> new ModularBow(CHAOTIC_TOOLS));
    public static final RegistryObject<ModularSword> SWORD_WYVERN                       = ITEMS.register("wyvern_sword",                        () -> new ModularSword(WYVERN_TIER, WYVERN_TOOLS));
    public static final RegistryObject<ModularSword> SWORD_DRACONIC                     = ITEMS.register("draconic_sword",                      () -> new ModularSword(DRACONIC_TIER, DRACONIC_TOOLS));
    public static final RegistryObject<ModularSword> SWORD_CHAOTIC                      = ITEMS.register("chaotic_sword",                       () -> new ModularSword(CHAOTIC_TIER, CHAOTIC_TOOLS));
    public static final RegistryObject<ModularStaff> STAFF_DRACONIC                     = ITEMS.register("draconic_staff",                      () -> new ModularStaff(DRACONIC_TIER, DRACONIC_TOOLS));
    public static final RegistryObject<ModularStaff> STAFF_CHAOTIC                      = ITEMS.register("chaotic_staff",                       () -> new ModularStaff(CHAOTIC_TIER, CHAOTIC_TOOLS));
    //Armor
    public static final RegistryObject<ModularChestpiece> CHESTPIECE_WYVERN             = ITEMS.register("wyvern_chestpiece",                   () -> new ModularChestpiece(WYVERN_TOOLS));
    public static final RegistryObject<ModularChestpiece> CHESTPIECE_DRACONIC           = ITEMS.register("draconic_chestpiece",                 () -> new ModularChestpiece(DRACONIC_TOOLS));
    public static final RegistryObject<ModularChestpiece> CHESTPIECE_CHAOTIC            = ITEMS.register("chaotic_chestpiece",                  () -> new ModularChestpiece(CHAOTIC_TOOLS));

    //Blocks
    public static final RegistryObject<ItemBlockBCore> ITEM_GENERATOR                   = ITEMS.register("generator",                           () ->  new ItemBlockBCore(GENERATOR.get(),                     new Item.Properties()));  //Generator
    public static final RegistryObject<ItemBlockBCore> ITEM_GRINDER                     = ITEMS.register("grinder",                             () ->  new ItemBlockBCore(GRINDER.get(),                       new Item.Properties()));  //Grinder
    public static final RegistryObject<ItemBlockBCore> ITEM_DISENCHANTER                = ITEMS.register("disenchanter",                        () ->  new ItemBlockBCore(DISENCHANTER.get(),                  new Item.Properties()));  //Disenchanter
    public static final RegistryObject<ItemBlockBCore> ITEM_ENERGY_TRANSFUSER           = ITEMS.register("energy_transfuser",                   () ->  new ItemBlockBCore(ENERGY_TRANSFUSER.get(),             new Item.Properties()));  //EnergyTransfuser
    public static final RegistryObject<ItemBlockBCore> ITEM_DISLOCATOR_PEDESTAL         = ITEMS.register("dislocator_pedestal",                 () ->  new ItemBlockBCore(DISLOCATOR_PEDESTAL.get(),           new Item.Properties()));  //DislocatorPedestal
    public static final RegistryObject<ItemBlockBCore> ITEM_DISLOCATOR_RECEPTACLE       = ITEMS.register("dislocator_receptacle",               () ->  new ItemBlockBCore(DISLOCATOR_RECEPTACLE.get(),         new Item.Properties()));  //DislocatorReceptacle
    public static final RegistryObject<ItemBlockBCore> ITEM_CREATIVE_OP_CAPACITOR       = ITEMS.register("creative_op_capacitor",               () ->  new ItemBlockBCore(CREATIVE_OP_CAPACITOR.get(),         new Item.Properties()));  //CreativeOPSource
    public static final RegistryObject<ItemBlockBCore> ITEM_ENTITY_DETECTOR             = ITEMS.register("entity_detector",                     () ->  new ItemBlockBCore(ENTITY_DETECTOR.get(),               new Item.Properties()));  //EntityDetector
    public static final RegistryObject<ItemBlockBCore> ITEM_ENTITY_DETECTOR_ADVANCED    = ITEMS.register("entity_detector_advanced",            () ->  new ItemBlockBCore(ENTITY_DETECTOR_ADVANCED.get(),      new Item.Properties()));  //EntityDetector
    public static final RegistryObject<ItemBlockBCore> ITEM_STABILIZED_SPAWNER          = ITEMS.register("stabilized_spawner",                  () ->  new ItemBlockBCore(STABILIZED_SPAWNER.get(),            new Item.Properties()));  //StabilizedSpawner
    public static final RegistryObject<ItemBlockBCore> ITEM_POTENTIOMETER               = ITEMS.register("potentiometer",                       () ->  new ItemBlockBCore(POTENTIOMETER.get(),                 new Item.Properties()));  //Potentiometer
    public static final RegistryObject<ItemBlockBCore> ITEM_CELESTIAL_MANIPULATOR       = ITEMS.register("celestial_manipulator",               () ->  new ItemBlockBCore(CELESTIAL_MANIPULATOR.get(),         new Item.Properties()));  //CelestialManipulator
    public static final RegistryObject<ItemBlockBCore> ITEM_DRACONIUM_CHEST             = ITEMS.register("draconium_chest",                     () ->  new ItemBlockBCore(DRACONIUM_CHEST.get(),               new Item.Properties().rarity(Rarity.RARE)));  //DraconiumChest
    public static final RegistryObject<ItemBlockBCore> ITEM_PARTICLE_GENERATOR          = ITEMS.register("particle_generator",                  () ->  new ItemBlockBCore(PARTICLE_GENERATOR.get(),            new Item.Properties()));  //ParticleGenerator
    public static final RegistryObject<ItemBlockBCore> ITEM_PLACED_ITEM                 = ITEMS.register("placed_item",                         () ->  new ItemBlockBCore(PLACED_ITEM.get(),                   new Item.Properties()));  //PlacedItem
    public static final RegistryObject<ItemBlockBCore> ITEM_PORTAL                      = ITEMS.register("portal",                              () ->  new ItemBlockBCore(PORTAL.get(),                        new Item.Properties()));  //Portal
    public static final RegistryObject<ItemBlockBCore> ITEM_CHAOS_CRYSTAL               = ITEMS.register("chaos_crystal",                       () ->  new ItemBlockBCore(CHAOS_CRYSTAL.get(),                 new Item.Properties()));  //ChaosCrystal
    public static final RegistryObject<ItemBlockBCore> ITEM_CHAOS_CRYSTAL_PART          = ITEMS.register("chaos_crystal_part",                  () ->  new ItemBlockBCore(CHAOS_CRYSTAL_PART.get(),            new Item.Properties()));  //ChaosCrystal
    public static final RegistryObject<ItemBlockBCore> ITEM_BASIC_CRAFTING_INJECTOR     = ITEMS.register("basic_crafting_injector",             () ->  new ItemBlockBCore(BASIC_CRAFTING_INJECTOR.get(),       new Item.Properties()));  //CraftingInjector
    public static final RegistryObject<ItemBlockBCore> ITEM_WYVERN_CRAFTING_INJECTOR    = ITEMS.register("wyvern_crafting_injector",            () ->  new ItemBlockBCore(WYVERN_CRAFTING_INJECTOR.get(),      new Item.Properties()));  //CraftingInjector
    public static final RegistryObject<ItemBlockBCore> ITEM_AWAKENED_CRAFTING_INJECTOR  = ITEMS.register("awakened_crafting_injector",          () ->  new ItemBlockBCore(AWAKENED_CRAFTING_INJECTOR.get(),    new Item.Properties()));  //CraftingInjector
    public static final RegistryObject<ItemBlockBCore> ITEM_CHAOTIC_CRAFTING_INJECTOR   = ITEMS.register("chaotic_crafting_injector",           () ->  new ItemBlockBCore(CHAOTIC_CRAFTING_INJECTOR.get(),     new Item.Properties()));  //CraftingInjector
    public static final RegistryObject<ItemBlockBCore> ITEM_CRAFTING_CORE               = ITEMS.register("crafting_core",                       () ->  new ItemBlockBCore(CRAFTING_CORE.get(),                 new Item.Properties()));  //FusionCraftingCore
    public static final RegistryObject<ItemBlockBCore> ITEM_ENERGY_CORE                 = ITEMS.register("energy_core",                         () ->  new ItemBlockBCore(ENERGY_CORE.get(),                   new Item.Properties()));  //EnergyCore
    public static final RegistryObject<ItemBlockBCore> ITEM_ENERGY_CORE_STABILIZER      = ITEMS.register("energy_core_stabilizer",              () ->  new ItemBlockBCore(ENERGY_CORE_STABILIZER.get(),        new Item.Properties()));  //EnergyCoreStabilizer
    public static final RegistryObject<ItemBlockBCore> ITEM_ENERGY_PYLON                = ITEMS.register("energy_pylon",                        () ->  new ItemBlockBCore(ENERGY_PYLON.get(),                  new Item.Properties()));  //EnergyPylon
    public static final RegistryObject<ItemBlockBCore> ITEM_STRUCTURE_BLOCK             = ITEMS.register("structure_block",                     () ->  new ItemBlockBCore(STRUCTURE_BLOCK.get(),               new Item.Properties()));  //StructureBlock
    public static final RegistryObject<ItemBlockBCore> ITEM_REACTOR_CORE                = ITEMS.register("reactor_core",                        () ->  new ItemBlockBCore(REACTOR_CORE.get(),                  new Item.Properties()));  //ReactorCore
    public static final RegistryObject<ItemBlockBCore> ITEM_REACTOR_STABILIZER          = ITEMS.register("reactor_stabilizer",                  () ->  new ItemBlockBCore(REACTOR_STABILIZER.get(),            new Item.Properties()));  //ReactorComponent
    public static final RegistryObject<ItemBlockBCore> ITEM_REACTOR_INJECTOR            = ITEMS.register("reactor_injector",                    () ->  new ItemBlockBCore(REACTOR_INJECTOR.get(),              new Item.Properties()));  //ReactorComponent
    public static final RegistryObject<ItemBlockBCore> ITEM_RAIN_SENSOR                 = ITEMS.register("rain_sensor",                         () ->  new ItemBlockBCore(RAIN_SENSOR.get(),                   new Item.Properties()));  //RainSensor
    public static final RegistryObject<ItemBlockBCore> ITEM_DISLOCATION_INHIBITOR       = ITEMS.register("dislocation_inhibitor",               () ->  new ItemBlockBCore(DISLOCATION_INHIBITOR.get(),         new Item.Properties()));  //DislocationInhibitor
    public static final RegistryObject<ItemBlockBCore> ITEM_OVERWORLD_DRACONIUM_ORE     = ITEMS.register("overworld_draconium_ore",             () ->  new ItemBlockBCore(OVERWORLD_DRACONIUM_ORE.get(),       new Item.Properties()));  //DraconiumOre
    public static final RegistryObject<ItemBlockBCore> ITEM_DEEPSLATE_DRACONIUM_ORE     = ITEMS.register("deepslate_draconium_ore",             () ->  new ItemBlockBCore(DEEPSLATE_DRACONIUM_ORE.get(),       new Item.Properties()));  //DraconiumOre
    public static final RegistryObject<ItemBlockBCore> ITEM_NETHER_DRACONIUM_ORE        = ITEMS.register("nether_draconium_ore",                () ->  new ItemBlockBCore(NETHER_DRACONIUM_ORE.get(),          new Item.Properties()));  //DraconiumOre
    public static final RegistryObject<ItemBlockBCore> ITEM_END_DRACONIUM_ORE           = ITEMS.register("end_draconium_ore",                   () ->  new ItemBlockBCore(END_DRACONIUM_ORE.get(),             new Item.Properties()));  //DraconiumOre
    public static final RegistryObject<ItemBlockBCore> ITEM_DRACONIUM_BLOCK             = ITEMS.register("draconium_block",                     () ->  new ItemBlockBCore(DRACONIUM_BLOCK.get(),               new Item.Properties()));  //DraconiumBlock
    public static final RegistryObject<ItemBlockBCore> ITEM_AWAKENED_DRACONIUM_BLOCK    = ITEMS.register("awakened_draconium_block",            () ->  new ItemBlockBCore(AWAKENED_DRACONIUM_BLOCK.get(),      new Item.Properties()));  //DraconiumBlock
    public static final RegistryObject<ItemBlockBCore> ITEM_INFUSED_OBSIDIAN            = ITEMS.register("infused_obsidian",                    () ->  new ItemBlockBCore(INFUSED_OBSIDIAN.get(),              new Item.Properties()));  //BlockBCore
    public static final RegistryObject<ItemBlockBCore> ITEM_BASIC_IO_CRYSTAL            = ITEMS.register("basic_io_crystal",                    () ->  new ItemBlockBCore(BASIC_IO_CRYSTAL.get(),              new Item.Properties()));  //EnergyCrystal
    public static final RegistryObject<ItemBlockBCore> ITEM_WYVERN_IO_CRYSTAL           = ITEMS.register("wyvern_io_crystal",                   () ->  new ItemBlockBCore(WYVERN_IO_CRYSTAL.get(),             new Item.Properties()));  //EnergyCrystal
    public static final RegistryObject<ItemBlockBCore> ITEM_DRACONIC_IO_CRYSTAL         = ITEMS.register("draconic_io_crystal",                 () ->  new ItemBlockBCore(DRACONIC_IO_CRYSTAL.get(),           new Item.Properties()));  //EnergyCrystal
    public static final RegistryObject<ItemBlockBCore> ITEM_BASIC_RELAY_CRYSTAL         = ITEMS.register("basic_relay_crystal",                 () ->  new ItemBlockBCore(BASIC_RELAY_CRYSTAL.get(),           new Item.Properties()));  //EnergyCrystal
    public static final RegistryObject<ItemBlockBCore> ITEM_WYVERN_RELAY_CRYSTAL        = ITEMS.register("wyvern_relay_crystal",                () ->  new ItemBlockBCore(WYVERN_RELAY_CRYSTAL.get(),          new Item.Properties()));  //EnergyCrystal
    public static final RegistryObject<ItemBlockBCore> ITEM_DRACONIC_RELAY_CRYSTAL      = ITEMS.register("draconic_relay_crystal",              () ->  new ItemBlockBCore(DRACONIC_RELAY_CRYSTAL.get(),        new Item.Properties()));  //EnergyCrystal
    public static final RegistryObject<ItemBlockBCore> ITEM_BASIC_WIRELESS_CRYSTAL      = ITEMS.register("basic_wireless_crystal",              () ->  new ItemBlockBCore(BASIC_WIRELESS_CRYSTAL.get(),        new Item.Properties()));  //EnergyCrystal
    public static final RegistryObject<ItemBlockBCore> ITEM_WYVERN_WIRELESS_CRYSTAL     = ITEMS.register("wyvern_wireless_crystal",             () ->  new ItemBlockBCore(WYVERN_WIRELESS_CRYSTAL.get(),       new Item.Properties()));  //EnergyCrystal
    public static final RegistryObject<ItemBlockBCore> ITEM_DRACONIC_WIRELESS_CRYSTAL   = ITEMS.register("draconic_wireless_crystal",           () ->  new ItemBlockBCore(DRACONIC_WIRELESS_CRYSTAL.get(),     new Item.Properties()));  //EnergyCrystal
    public static final RegistryObject<ItemBlockBCore> ITEM_FLUX_GATE                   = ITEMS.register("flux_gate",                           () ->  new ItemBlockBCore(FLUX_GATE.get(),                     new Item.Properties()));  //FlowGate
    public static final RegistryObject<ItemBlockBCore> ITEM_FLUID_GATE                  = ITEMS.register("fluid_gate",                          () ->  new ItemBlockBCore(FLUID_GATE.get(),                    new Item.Properties()));  //FlowGate

    //@formatter:on

//        TierSortingRegistry.registerTier(WYVERN_TIER, new ResourceLocation(MODID, "wyvern_tier"), List.of(Tiers.NETHERITE), Collections.emptyList());
//        if (DEConfig.useToolTierTags) {
//        TierSortingRegistry.registerTier(DRACONIC_TIER, new ResourceLocation(MODID, "draconic_tier"), List.of(WYVERN_TIER), Collections.emptyList());
//        TierSortingRegistry.registerTier(CHAOTIC_TIER, new ResourceLocation(MODID, "chaotic_tier"), List.of(DRACONIC_TIER), Collections.emptyList());
//    }


    //#################################################################
    // Tile Entities
    //#################################################################

    //@formatter:off
    public static final RegistryObject<BlockEntityType<TileGenerator>>              TILE_GENERATOR                  = TILES_ENTITIES.register("generator",             () -> BlockEntityType.Builder.of(TileGenerator::new,                    GENERATOR.get()                     ).build(null));
    public static final RegistryObject<BlockEntityType<TileGrinder>>                TILE_GRINDER                    = TILES_ENTITIES.register("grinder",               () -> BlockEntityType.Builder.of(TileGrinder::new,                      GRINDER.get()                       ).build(null));
    public static final RegistryObject<BlockEntityType<TileDisenchanter>>           TILE_DISENCHANTER               = TILES_ENTITIES.register("disenchanter",          () -> BlockEntityType.Builder.of(TileDisenchanter::new,                 DISENCHANTER.get()                  ).build(null));
    public static final RegistryObject<BlockEntityType<TileEnergyTransfuser>>       TILE_ENERGY_TRANSFUSER          = TILES_ENTITIES.register("energy_transfuser",     () -> BlockEntityType.Builder.of(TileEnergyTransfuser::new,             ENERGY_TRANSFUSER.get()             ).build(null));
    public static final RegistryObject<BlockEntityType<TileDislocatorPedestal>>     TILE_DISLOCATOR_PEDESTAL        = TILES_ENTITIES.register("dislocator_pedestal",   () -> BlockEntityType.Builder.of(TileDislocatorPedestal::new,           DISLOCATOR_PEDESTAL.get()           ).build(null));
    public static final RegistryObject<BlockEntityType<TileDislocatorReceptacle>>   TILE_DISLOCATOR_RECEPTACLE      = TILES_ENTITIES.register("dislocator_receptacle", () -> BlockEntityType.Builder.of(TileDislocatorReceptacle::new,         DISLOCATOR_RECEPTACLE.get()         ).build(null));
    public static final RegistryObject<BlockEntityType<TileCreativeOPCapacitor>>    TILE_CREATIVE_OP_CAPACITOR      = TILES_ENTITIES.register("creative_op_capacitor", () -> BlockEntityType.Builder.of(TileCreativeOPCapacitor::new,          CREATIVE_OP_CAPACITOR.get()         ).build(null));
    public static final RegistryObject<BlockEntityType<TileEntityDetector>>         TILE_ENTITY_DETECTOR            = TILES_ENTITIES.register("entity_detector",       () -> BlockEntityType.Builder.of(TileEntityDetector::new,               ENTITY_DETECTOR.get(), ENTITY_DETECTOR_ADVANCED.get()).build(null));
    public static final RegistryObject<BlockEntityType<TileStabilizedSpawner>>      TILE_STABILIZED_SPAWNER         = TILES_ENTITIES.register("stabilized_spawner",    () -> BlockEntityType.Builder.of(TileStabilizedSpawner::new,            STABILIZED_SPAWNER.get()            ).build(null));
    public static final RegistryObject<BlockEntityType<TileRainSensor>>             TILE_RAIN_SENSOR                = TILES_ENTITIES.register("rain_sensor",           () -> BlockEntityType.Builder.of(TileRainSensor::new,                   RAIN_SENSOR.get()                   ).build(null));
    public static final RegistryObject<BlockEntityType<TilePotentiometer>>          TILE_POTENTIOMETER              = TILES_ENTITIES.register("potentiometer",         () -> BlockEntityType.Builder.of(TilePotentiometer::new,                POTENTIOMETER.get()                 ).build(null));
    public static final RegistryObject<BlockEntityType<TileCelestialManipulator>>   TILE_CELESTIAL_MANIPULATOR      = TILES_ENTITIES.register("celestial_manipulator", () -> BlockEntityType.Builder.of(TileCelestialManipulator::new,         CELESTIAL_MANIPULATOR.get()         ).build(null));
    public static final RegistryObject<BlockEntityType<TileDraconiumChest>>         TILE_DRACONIUM_CHEST            = TILES_ENTITIES.register("draconium_chest",       () -> BlockEntityType.Builder.of(TileDraconiumChest::new,               DRACONIUM_CHEST.get()               ).build(null));
    public static final RegistryObject<BlockEntityType<TilePlacedItem>>             TILE_PLACED_ITEM                = TILES_ENTITIES.register("placed_item",           () -> BlockEntityType.Builder.of(TilePlacedItem::new,                   PLACED_ITEM.get()                   ).build(null));
    public static final RegistryObject<BlockEntityType<TilePortal>>                 TILE_PORTAL                     = TILES_ENTITIES.register("portal",                () -> BlockEntityType.Builder.of(TilePortal::new,                       PORTAL.get()                        ).build(null));
    public static final RegistryObject<BlockEntityType<TileFusionCraftingInjector>> TILE_CRAFTING_INJECTOR          = TILES_ENTITIES.register("crafting_injector",     () -> BlockEntityType.Builder.of(TileFusionCraftingInjector::new,       BASIC_CRAFTING_INJECTOR.get(), WYVERN_CRAFTING_INJECTOR.get(), AWAKENED_CRAFTING_INJECTOR.get(), CHAOTIC_CRAFTING_INJECTOR.get()).build(null));
    public static final RegistryObject<BlockEntityType<TileFusionCraftingCore>>     TILE_CRAFTING_CORE              = TILES_ENTITIES.register("crafting_core",         () -> BlockEntityType.Builder.of(TileFusionCraftingCore::new,           CRAFTING_CORE.get()                 ).build(null));
    public static final RegistryObject<BlockEntityType<TileEnergyCore>>             TILE_STORAGE_CORE               = TILES_ENTITIES.register("storage_core",          () -> BlockEntityType.Builder.of(TileEnergyCore::new,                   ENERGY_CORE.get()                   ).build(null));
    public static final RegistryObject<BlockEntityType<TileEnergyCoreStabilizer>>   TILE_CORE_STABILIZER            = TILES_ENTITIES.register("core_stabilizer",       () -> BlockEntityType.Builder.of(TileEnergyCoreStabilizer::new,         ENERGY_CORE_STABILIZER.get()        ).build(null));
    public static final RegistryObject<BlockEntityType<TileEnergyPylon>>            TILE_ENERGY_PYLON               = TILES_ENTITIES.register("energy_pylon",          () -> BlockEntityType.Builder.of(TileEnergyPylon::new,                  ENERGY_PYLON.get()                  ).build(null));
    public static final RegistryObject<BlockEntityType<TileStructureBlock>>         TILE_STRUCTURE_BLOCK            = TILES_ENTITIES.register("structure_block",       () -> BlockEntityType.Builder.of(TileStructureBlock::new,               STRUCTURE_BLOCK.get()               ).build(null));
    public static final RegistryObject<BlockEntityType<TileReactorCore>>            TILE_REACTOR_CORE               = TILES_ENTITIES.register("reactor_core",          () -> BlockEntityType.Builder.of(TileReactorCore::new,                  REACTOR_CORE.get()                  ).build(null));
    public static final RegistryObject<BlockEntityType<TileReactorStabilizer>>      TILE_REACTOR_STABILIZER         = TILES_ENTITIES.register("reactor_stabilizer",    () -> BlockEntityType.Builder.of(TileReactorStabilizer::new,            REACTOR_STABILIZER.get()            ).build(null));
    public static final RegistryObject<BlockEntityType<TileReactorInjector>>        TILE_REACTOR_INJECTOR           = TILES_ENTITIES.register("reactor_injector",      () -> BlockEntityType.Builder.of(TileReactorInjector::new,              REACTOR_INJECTOR.get()              ).build(null));
    public static final RegistryObject<BlockEntityType<TileFluxGate>>               TILE_FLUX_GATE                  = TILES_ENTITIES.register("flux_gate",             () -> BlockEntityType.Builder.of(TileFluxGate::new,                     FLUX_GATE.get()                     ).build(null));
    public static final RegistryObject<BlockEntityType<TileFluidGate>>              TILE_FLUID_GATE                 = TILES_ENTITIES.register("fluid_gate",            () -> BlockEntityType.Builder.of(TileFluidGate::new,                    FLUID_GATE.get()                    ).build(null));
    public static final RegistryObject<BlockEntityType<TileChaosCrystal>>           TILE_CHAOS_CRYSTAL              = TILES_ENTITIES.register("chaos_crystal",         () -> BlockEntityType.Builder.of(TileChaosCrystal::new,                 CHAOS_CRYSTAL.get(), CHAOS_CRYSTAL_PART.get()).build(null));

    public static final RegistryObject<BlockEntityType<TileCrystalDirectIO>>        TILE_IO_CRYSTAL                 = TILES_ENTITIES.register("io_crystal",            () -> BlockEntityType.Builder.of(TileCrystalDirectIO::new, BASIC_IO_CRYSTAL.get(), WYVERN_IO_CRYSTAL.get(), DRACONIC_IO_CRYSTAL.get()).build(null));
    public static final RegistryObject<BlockEntityType<TileCrystalRelay>>           TILE_RELAY_CRYSTAL              = TILES_ENTITIES.register("relay_crystal",         () -> BlockEntityType.Builder.of(TileCrystalRelay::new, BASIC_RELAY_CRYSTAL.get(), WYVERN_RELAY_CRYSTAL.get(), DRACONIC_RELAY_CRYSTAL.get()).build(null));
    public static final RegistryObject<BlockEntityType<TileCrystalWirelessIO>>      TILE_WIRELESS_CRYSTAL           = TILES_ENTITIES.register("wireless_crystal",      () -> BlockEntityType.Builder.of(TileCrystalWirelessIO::new, BASIC_WIRELESS_CRYSTAL.get(), WYVERN_WIRELESS_CRYSTAL.get(), DRACONIC_WIRELESS_CRYSTAL.get()).build(null));
    //@formatter:on


    //#################################################################
    // MenuTypes
    //#################################################################

    //@formatter:off
    public static final RegistryObject<MenuType<GeneratorMenu>> MENU_GENERATOR                              = MENU_TYPES.register("generator",                      () -> IForgeMenuType.create(GeneratorMenu::new));
    public static final RegistryObject<MenuType<GrinderMenu>> MENU_GRINDER                                  = MENU_TYPES.register("grinder",                        () -> IForgeMenuType.create(GrinderMenu::new));
    public static final RegistryObject<MenuType<ConfigurableItemMenu>> MENU_CONFIGURABLE_ITEM               = MENU_TYPES.register("configurable_item",              () -> IForgeMenuType.create(ConfigurableItemMenu::new));
    public static final RegistryObject<MenuType<CelestialManipulatorMenu>> MENU_CELESTIAL_MANIPULATOR       = MENU_TYPES.register("celestial_manipulator",          () -> IForgeMenuType.create(CelestialManipulatorMenu::new));
    public static final RegistryObject<MenuType<DisenchanterMenu>> MENU_DISENCHANTER                        = MENU_TYPES.register("disenchanter",                   () -> IForgeMenuType.create(DisenchanterMenu::new));
    public static final RegistryObject<MenuType<TransfuserMenu>> MENU_ENERGY_TRANSFUSER                     = MENU_TYPES.register("energy_transfuser",              () -> IForgeMenuType.create(TransfuserMenu::new));
    public static final RegistryObject<MenuType<EnergyCoreMenu>> MENU_ENERGY_CORE                           = MENU_TYPES.register("energy_core",                    () -> IForgeMenuType.create(EnergyCoreMenu::new));
    public static final RegistryObject<MenuType<FlowGateMenu>> MENU_FLOW_GATE                               = MENU_TYPES.register("flow_gate",                      () -> IForgeMenuType.create(FlowGateMenu::new));
    public static final RegistryObject<MenuType<EntityDetectorMenu>> MENU_ENTITY_DETECTOR                   = MENU_TYPES.register("entity_detector",                () -> IForgeMenuType.create(EntityDetectorMenu::new));

    public static final RegistryObject<MenuType<FusionCraftingCoreMenu>> MENU_FUSION_CRAFTING_CORE     = MENU_TYPES.register("fusion_crafting_core",           () -> IForgeMenuType.create(FusionCraftingCoreMenu::new));
    public static final RegistryObject<MenuType<ModularItemMenu>> MENU_MODULAR_ITEM                         = MENU_TYPES.register("modular_item",                   () -> IForgeMenuType.create(ModularItemMenu::new));
    public static final RegistryObject<MenuType<DraconiumChestMenu>> MENU_DRACONIUM_CHEST              = MENU_TYPES.register("draconium_chest",                () -> IForgeMenuType.create(DraconiumChestMenu::new));
    public static final RegistryObject<MenuType<ContainerEnergyCrystal>> MENU_ENERGY_CRYSTAL                = MENU_TYPES.register("energy_crystal",                 () -> IForgeMenuType.create(ContainerEnergyCrystal::new));
    public static final RegistryObject<MenuType<ContainerReactor>> MENU_REACTOR                             = MENU_TYPES.register("reactor",                        () -> IForgeMenuType.create(ContainerReactor::new));
    //@formatter:on


    //#################################################################
    // Entities
    //#################################################################

    public static final RegistryObject<EntityType<DraconicGuardianEntity>>      ENTITY_DRACONIC_GUARDIAN    = ENTITY_TYPES.register("draconic_guardian",            () -> EntityType.Builder.of(DraconicGuardianEntity::new, MobCategory.MONSTER).fireImmune().sized(16.0F, 8.0F).clientTrackingRange(20).build("draconic_guardian"));
    public static final RegistryObject<EntityType<GuardianWither>>              ENTITY_GUARDIAN_WITHER      = ENTITY_TYPES.register("guardian_wither",              () -> EntityType.Builder.of(GuardianWither::new, MobCategory.MONSTER).fireImmune().immuneTo(Blocks.WITHER_ROSE).sized(0.9F, 3.5F).clientTrackingRange(10).build("guardian_wither"));

    public static final RegistryObject<EntityType<GuardianProjectileEntity>>    ENTITY_GUARDIAN_PROJECTILE  = ENTITY_TYPES.register("guardian_projectile",          () -> EntityType.Builder.<GuardianProjectileEntity>of(GuardianProjectileEntity::new, MobCategory.MISC).fireImmune().sized(2F, 2F).clientTrackingRange(20)/*.updateInterval(10)*/.build("guardian_projectile"));
    public static final RegistryObject<EntityType<GuardianCrystalEntity>>       ENTITY_GUARDIAN_CRYSTAL     = ENTITY_TYPES.register("guardian_crystal",             () -> EntityType.Builder.<GuardianCrystalEntity>of(GuardianCrystalEntity::new, MobCategory.MISC).fireImmune().sized(2F, 2F).clientTrackingRange(20).updateInterval(100).build("guardian_crystal"));
    public static final RegistryObject<EntityType<DraconicArrowEntity>>         ENTITY_DRACONIC_ARROW       = ENTITY_TYPES.register("draconic_arrow",               () -> EntityType.Builder.<DraconicArrowEntity>of(DraconicArrowEntity::new, MobCategory.MISC).sized(0.5F, 0.5F).clientTrackingRange(4).updateInterval(20).build("draconic_arrow"));

    public static void registerAttributes(EntityAttributeCreationEvent event) {
        event.put(ENTITY_DRACONIC_GUARDIAN.get(), DraconicGuardianEntity.registerAttributes().build());
        event.put(ENTITY_GUARDIAN_WITHER.get(), GuardianWither.createAttributes().build());
    }

    //#################################################################
    // Enchantments
    //#################################################################

    public static final RegistryObject<Enchantment> ENCHANTMENT_REAPER              = ENCHANTMENTS.register("reaper_enchantment", EnchantmentReaper::new);


    //#################################################################
    // Recipe Types
    //#################################################################

    static {
        DraconicAPI.FUSION_RECIPE_SERIALIZER = RECIPE_SERIAL.register("fusion_crafting", FusionRecipe.Serializer::new);
        DraconicAPI.FUSION_RECIPE_TYPE = RECIPE_TYPES.register("fusion_crafting", () -> RecipeType.simple(new ResourceLocation(MODID, "fusion_crafting")));
    }


    //#################################################################
    // World Entities
    //#################################################################

    public static final RegistryObject<WorldEntityType<?>> WE_GUARDIAN_MANAGER      = WORLD_ENTITY_TYPES.register("guardian_manager",       () -> new WorldEntityType<>(GuardianFightManager::new));
}