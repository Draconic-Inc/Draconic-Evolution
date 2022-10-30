package com.brandon3055.draconicevolution.init;

import com.brandon3055.brandonscore.api.TechLevel;
import com.brandon3055.brandonscore.blocks.BlockBCore;
import com.brandon3055.brandonscore.blocks.ItemBlockBCore;
import com.brandon3055.brandonscore.client.utils.CyclingItemGroup;
import com.brandon3055.brandonscore.inventory.ContainerBCTile;
import com.brandon3055.brandonscore.worldentity.WorldEntityType;
import com.brandon3055.draconicevolution.DEConfig;
import com.brandon3055.draconicevolution.DraconicEvolution;
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
import com.brandon3055.draconicevolution.blocks.tileentity.flowgate.TileFlowGate;
import com.brandon3055.draconicevolution.blocks.tileentity.flowgate.TileFluidGate;
import com.brandon3055.draconicevolution.blocks.tileentity.flowgate.TileFluxGate;
import com.brandon3055.draconicevolution.entity.GuardianCrystalEntity;
import com.brandon3055.draconicevolution.entity.guardian.DraconicGuardianEntity;
import com.brandon3055.draconicevolution.entity.guardian.GuardianFightManager;
import com.brandon3055.draconicevolution.entity.guardian.GuardianProjectileEntity;
import com.brandon3055.draconicevolution.entity.guardian.GuardianWither;
import com.brandon3055.draconicevolution.entity.projectile.DraconicArrowEntity;
import com.brandon3055.draconicevolution.inventory.*;
import com.brandon3055.draconicevolution.items.EnderEnergyManipulator;
import com.brandon3055.draconicevolution.items.InfoTablet;
import com.brandon3055.draconicevolution.items.ItemCore;
import com.brandon3055.draconicevolution.items.MobSoul;
import com.brandon3055.draconicevolution.items.equipment.*;
import com.brandon3055.draconicevolution.items.tools.*;
import com.brandon3055.draconicevolution.magic.EnchantmentReaper;
import net.covers1624.quack.util.SneakyUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraftforge.common.TierSortingRegistry;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ObjectHolder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

/**
 * Created by brandon3055 on 18/3/2016.
 * This class contains a reference to all blocks and items in Draconic Evolution
 */
@Mod.EventBusSubscriber(modid = DraconicEvolution.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
@ObjectHolder(DraconicEvolution.MODID)
public class DEContent {

    //#################################################################
    // Tile Entities
    //#################################################################

	//@formatter:off
    @ObjectHolder("generator")                public static BlockEntityType<TileGenerator>              tile_generator;
    @ObjectHolder("grinder")                  public static BlockEntityType<TileGrinder>                tile_grinder;
    @ObjectHolder("disenchanter")             public static BlockEntityType<TileDisenchanter>           tile_disenchanter;
    @ObjectHolder("energy_transfuser")        public static BlockEntityType<TileEnergyTransfuser>       tile_energy_transfuser;
    @ObjectHolder("dislocator_pedestal")      public static BlockEntityType<TileDislocatorPedestal>     tile_dislocator_pedestal;
    @ObjectHolder("dislocator_receptacle")    public static BlockEntityType<TileDislocatorReceptacle>   tile_dislocator_receptacle;
    @ObjectHolder("creative_op_capacitor")    public static BlockEntityType<TileCreativeOPCapacitor>    tile_creative_op_capacitor;
    @ObjectHolder("entity_detector")          public static BlockEntityType<TileEntityDetector>         tile_entity_detector;
    @ObjectHolder("advanced_entity_detector") public static BlockEntityType<TileEntityDetector>         tile_entity_detector_advanced;
    @ObjectHolder("stabilized_spawner")       public static BlockEntityType<TileStabilizedSpawner>      tile_stabilized_spawner;
    @ObjectHolder("potentiometer")            public static BlockEntityType<TilePotentiometer>          tile_potentiometer;
    @ObjectHolder("celestial_manipulator")    public static BlockEntityType<TileCelestialManipulator>   tile_celestial_manipulator;
    @ObjectHolder("draconium_chest")          public static BlockEntityType<TileDraconiumChest>         tile_draconium_chest;
    @ObjectHolder("particle_generator")       public static BlockEntityType<TileParticleGenerator>      tile_particle_generator;
    @ObjectHolder("placed_item")              public static BlockEntityType<TilePlacedItem>             tile_placed_item;
    @ObjectHolder("portal")                   public static BlockEntityType<TilePortal>                 tile_portal;
    @ObjectHolder("chaos_crystal")            public static BlockEntityType<TileChaosCrystal>           tile_chaos_crystal;
    @ObjectHolder("crafting_injector")        public static BlockEntityType<TileFusionCraftingInjector> tile_crafting_injector;
    @ObjectHolder("crafting_core")            public static BlockEntityType<TileFusionCraftingCore>     tile_crafting_core;
    @ObjectHolder("storage_core")             public static BlockEntityType<TileEnergyCore>             tile_storage_core;
    @ObjectHolder("core_stabilizer")          public static BlockEntityType<TileEnergyCoreStabilizer>   tile_core_stabilizer;
    @ObjectHolder("energy_pylon")             public static BlockEntityType<TileEnergyPylon>            tile_energy_pylon;
    @ObjectHolder("structure_block")          public static BlockEntityType<TileStructureBlock>         tile_structure_block;
    @ObjectHolder("reactor_core")             public static BlockEntityType<TileReactorCore>            tile_reactor_core;
    @ObjectHolder("reactor_stabilizer")       public static BlockEntityType<TileReactorStabilizer>      tile_reactor_stabilizer;
    @ObjectHolder("reactor_injector")         public static BlockEntityType<TileReactorInjector>        tile_reactor_injector;
    @ObjectHolder("flux_gate")                public static BlockEntityType<TileFluxGate>               tile_flux_gate;
    @ObjectHolder("fluid_gate")               public static BlockEntityType<TileFluidGate>              tile_fluid_gate;
    @ObjectHolder("io_crystal")               public static BlockEntityType<TileCrystalDirectIO>        tile_crystal_io;
    @ObjectHolder("relay_crystal")            public static BlockEntityType<TileCrystalRelay>           tile_crystal_relay;
    @ObjectHolder("wireless_crystal")         public static BlockEntityType<TileCrystalWirelessIO>      tile_crystal_wireless;
    //@formatter:on

    @SubscribeEvent
    public static void registerTileEntity(RegistryEvent.Register<BlockEntityType<?>> event) {
        Block[] entityDetectors = {entity_detector, entity_detector_advanced};
        Block[] craftInjectors = {crafting_injector_basic, crafting_injector_wyvern, crafting_injector_awakened, crafting_injector_chaotic};
        //@formatter:off
        event.getRegistry().register(BlockEntityType.Builder.of(TileGenerator::new,              generator               ).build(null).setRegistryName("generator"));
        event.getRegistry().register(BlockEntityType.Builder.of(TileGrinder::new,                grinder                 ).build(null).setRegistryName("grinder"));
        event.getRegistry().register(BlockEntityType.Builder.of(TileDisenchanter::new,           disenchanter            ).build(null).setRegistryName("disenchanter"));
        event.getRegistry().register(BlockEntityType.Builder.of(TileEnergyTransfuser::new,       energy_transfuser       ).build(null).setRegistryName("energy_transfuser"));
        event.getRegistry().register(BlockEntityType.Builder.of(TileDislocatorPedestal::new,     dislocator_pedestal     ).build(null).setRegistryName("dislocator_pedestal"));
        event.getRegistry().register(BlockEntityType.Builder.of(TileDislocatorReceptacle::new,   dislocator_receptacle   ).build(null).setRegistryName("dislocator_receptacle"));
        event.getRegistry().register(BlockEntityType.Builder.of(TileCreativeOPCapacitor::new,    creative_op_capacitor   ).build(null).setRegistryName("creative_op_capacitor"));
//        event.getRegistry().register(BlockEntityType.Builder.of(TileEntityDetector::new,         entityDetectors         ).build(null).setRegistryName("entity_detector"));
        event.getRegistry().register(BlockEntityType.Builder.of(TileStabilizedSpawner::new,      stabilized_spawner      ).build(null).setRegistryName("stabilized_spawner"));
        event.getRegistry().register(BlockEntityType.Builder.of(TilePotentiometer::new,          potentiometer           ).build(null).setRegistryName("potentiometer"));
        event.getRegistry().register(BlockEntityType.Builder.of(TileCelestialManipulator::new,   celestial_manipulator   ).build(null).setRegistryName("celestial_manipulator"));
        event.getRegistry().register(BlockEntityType.Builder.of(TileDraconiumChest::new,         draconium_chest         ).build(null).setRegistryName("draconium_chest"));
//        event.getRegistry().register(BlockEntityType.Builder.of(TileParticleGenerator::new,    particle_generator      ).build(null).setRegistryName("particle_generator"));
        event.getRegistry().register(BlockEntityType.Builder.of(TilePlacedItem::new,             placed_item             ).build(null).setRegistryName("placed_item"));
        event.getRegistry().register(BlockEntityType.Builder.of(TilePortal::new,                 portal                  ).build(null).setRegistryName("portal"));
        event.getRegistry().register(BlockEntityType.Builder.of(TileFusionCraftingInjector::new, craftInjectors          ).build(null).setRegistryName("crafting_injector"));
        event.getRegistry().register(BlockEntityType.Builder.of(TileFusionCraftingCore::new,     crafting_core           ).build(null).setRegistryName("crafting_core"));
        event.getRegistry().register(BlockEntityType.Builder.of(TileEnergyCore::new,             energy_core             ).build(null).setRegistryName("storage_core"));
        event.getRegistry().register(BlockEntityType.Builder.of(TileEnergyCoreStabilizer::new,   energy_core_stabilizer  ).build(null).setRegistryName("core_stabilizer"));
        event.getRegistry().register(BlockEntityType.Builder.of(TileEnergyPylon::new,            energy_pylon            ).build(null).setRegistryName("energy_pylon"));
        event.getRegistry().register(BlockEntityType.Builder.of(TileStructureBlock::new,         structure_block         ).build(null).setRegistryName("structure_block"));
        event.getRegistry().register(BlockEntityType.Builder.of(TileReactorCore::new,            reactor_core            ).build(null).setRegistryName("reactor_core"));
        event.getRegistry().register(BlockEntityType.Builder.of(TileReactorStabilizer::new,      reactor_stabilizer      ).build(null).setRegistryName("reactor_stabilizer"));
        event.getRegistry().register(BlockEntityType.Builder.of(TileReactorInjector::new,        reactor_injector        ).build(null).setRegistryName("reactor_injector"));
        event.getRegistry().register(BlockEntityType.Builder.of(TileFluxGate::new,               flux_gate               ).build(null).setRegistryName("flux_gate"));
        event.getRegistry().register(BlockEntityType.Builder.of(TileFluidGate::new,              fluid_gate              ).build(null).setRegistryName("fluid_gate"));
        event.getRegistry().register(BlockEntityType.Builder.of(TileChaosCrystal::new,           chaos_crystal, chaos_crystal_part).build(null).setRegistryName("chaos_crystal"));

        event.getRegistry().register(BlockEntityType.Builder.of(TileCrystalDirectIO::new, crystal_io_basic, crystal_io_wyvern, crystal_io_draconic/*, crystal_io_chaotic*/).build(null).setRegistryName("io_crystal"));
        event.getRegistry().register(BlockEntityType.Builder.of(TileCrystalRelay::new, crystal_relay_basic, crystal_relay_wyvern, crystal_relay_draconic/*, crystal_relay_chaotic*/).build(null).setRegistryName("relay_crystal"));
        event.getRegistry().register(BlockEntityType.Builder.of(TileCrystalWirelessIO::new, crystal_wireless_basic, crystal_wireless_wyvern, crystal_wireless_draconic/*, crystal_wireless_chaotic*/).build(null).setRegistryName("wireless_crystal"));
        //@formatter:on
    }

    //#################################################################
    // Containers
    //#################################################################

    //@formatter:off
    @ObjectHolder("generator")              public static MenuType<ContainerBCTile<TileGenerator>>             container_generator;
    @ObjectHolder("grinder")                public static MenuType<ContainerBCTile<TileGrinder>>               container_grinder;
    @ObjectHolder("energy_core")            public static MenuType<ContainerBCTile<TileEnergyCore>>            container_energy_core;
    @ObjectHolder("disenchanter")           public static MenuType<ContainerBCTile<TileDisenchanter>>          container_disenchanter;
    @ObjectHolder("draconium_chest")        public static MenuType<ContainerDraconiumChest>                    container_draconium_chest;
    @ObjectHolder("celestial_manipulator")  public static MenuType<ContainerBCTile<TileCelestialManipulator>>  container_celestial_manipulator;
    @ObjectHolder("energy_crystal")         public static MenuType<ContainerEnergyCrystal>                     container_energy_crystal;
    @ObjectHolder("energy_transfuser")      public static MenuType<ContainerBCTile<TileEnergyTransfuser>>      container_energy_transfuser;
    @ObjectHolder("fusion_crafting_core")   public static MenuType<ContainerFusionCraftingCore>                container_fusion_crafting_core;
    @ObjectHolder("reactor")                public static MenuType<ContainerReactor>                           container_reactor;
    @ObjectHolder("modular_item")           public static MenuType<ContainerModularItem>                       container_modular_item;
    @ObjectHolder("configurable_item")      public static MenuType<ContainerConfigurableItem>                  container_configurable_item;
    @ObjectHolder("flow_gate")              public static MenuType<ContainerBCTile<TileFlowGate>>              container_flow_gate;
    //@formatter:on

    @SubscribeEvent
    public static void registerContainers(RegistryEvent.Register<MenuType<?>> event) {
        event.getRegistry().register(IForgeMenuType.create((id, playerInv, extraData) -> new ContainerBCTile<>(container_generator, id, playerInv, extraData, GuiLayoutFactories.GENERATOR_LAYOUT)).setRegistryName("generator"));
        event.getRegistry().register(IForgeMenuType.create((id, playerInv, extraData) -> new ContainerBCTile<>(container_grinder, id, playerInv, extraData, GuiLayoutFactories.GRINDER_LAYOUT)).setRegistryName("grinder"));

        event.getRegistry().register(IForgeMenuType.create(ContainerDraconiumChest::new).setRegistryName("draconium_chest"));

        event.getRegistry().register(IForgeMenuType.create((id, playerInv, extraData) -> new ContainerBCTile<>(container_energy_core, id, playerInv, extraData, GuiLayoutFactories.ENERGY_CORE_LAYOUT)).setRegistryName("energy_core"));

        event.getRegistry().register(IForgeMenuType.create((id, playerInv, extraData) -> new ContainerBCTile<>(container_disenchanter, id, playerInv, extraData, GuiLayoutFactories.DISENCHANTER_LAYOUT)).setRegistryName("disenchanter"));
        event.getRegistry().register(IForgeMenuType.create((windowId, inv, data) -> new ContainerDummy<TileCelestialManipulator>(container_celestial_manipulator, windowId, inv, data)).setRegistryName("celestial_manipulator"));
        event.getRegistry().register(IForgeMenuType.create(ContainerEnergyCrystal::new).setRegistryName("energy_crystal"));
        event.getRegistry().register(IForgeMenuType.create((windowId, inv, data) -> new ContainerFusionCraftingCore(windowId, inv, data, GuiLayoutFactories.FUSION_CRAFTING_CORE)).setRegistryName("fusion_crafting_core"));
        event.getRegistry().register(IForgeMenuType.create(ContainerReactor::new).setRegistryName("reactor"));
        event.getRegistry().register(IForgeMenuType.create((windowId, playerInv, extraData) -> new ContainerModularItem(windowId, playerInv, extraData, GuiLayoutFactories.MODULAR_ITEM_LAYOUT)).setRegistryName("modular_item"));
        event.getRegistry().register(IForgeMenuType.create((windowId, playerInv, extraData) -> new ContainerConfigurableItem(windowId, playerInv, extraData, GuiLayoutFactories.CONFIGURABLE_ITEM_LAYOUT)).setRegistryName("configurable_item"));
//        event.getRegistry().register(IForgeContainerType.create(ContainerDummy::new).setRegistryName("container_dummy"));
//        event.getRegistry().register(IForgeContainerType.create(ContainerJunkFilter::new).setRegistryName("container_junk_filter"));
//        event.getRegistry().register(IForgeContainerType.create(ContainerRecipeBuilder::new).setRegistryName("container_recipe_builder"));
        event.getRegistry().register(IForgeMenuType.create((windowId, inv, data) -> new ContainerBCTile<TileFlowGate>(container_flow_gate, windowId, inv, data, SneakyUtils.unsafeCast(GuiLayoutFactories.PLAYER_ONLY_LAYOUT))).setRegistryName("flow_gate"));
        event.getRegistry().register(IForgeMenuType.create((windowId, inv, data) -> new ContainerBCTile<>(container_energy_transfuser, windowId, inv, data, GuiLayoutFactories.TRANSFUSER_LAYOUT)).setRegistryName("energy_transfuser"));
    }


    //#################################################################
    // Blocks
    //#################################################################

    //@formatter:off
    @ObjectHolder("generator")                  public static Generator                 generator;
    @ObjectHolder("grinder")                    public static Grinder                   grinder;
    @ObjectHolder("disenchanter")               public static Disenchanter              disenchanter;
    @ObjectHolder("energy_transfuser")          public static EnergyTransfuser          energy_transfuser;
    @ObjectHolder("dislocator_pedestal")        public static DislocatorPedestal        dislocator_pedestal;
    @ObjectHolder("dislocator_receptacle")      public static DislocatorReceptacle      dislocator_receptacle;
    @ObjectHolder("creative_op_capacitor")      public static CreativeOPSource          creative_op_capacitor;
    @ObjectHolder("entity_detector")            public static EntityDetector            entity_detector;
    @ObjectHolder("advanced_entity_detector")   public static EntityDetector            entity_detector_advanced;
    @ObjectHolder("stabilized_spawner")         public static StabilizedSpawner         stabilized_spawner;
    @ObjectHolder("potentiometer")              public static Potentiometer             potentiometer;
    @ObjectHolder("celestial_manipulator")      public static CelestialManipulator      celestial_manipulator;
    @ObjectHolder("draconium_chest")            public static DraconiumChest            draconium_chest;
    @ObjectHolder("particle_generator")         public static ParticleGenerator         particle_generator;
    @ObjectHolder("placed_item")                public static PlacedItem                placed_item;
    @ObjectHolder("portal")                     public static Portal                    portal;
    @ObjectHolder("chaos_crystal")              public static ChaosCrystal              chaos_crystal;
    @ObjectHolder("chaos_crystal_part")         public static ChaosCrystal              chaos_crystal_part;
    @ObjectHolder("basic_crafting_injector")    public static CraftingInjector          crafting_injector_basic;
    @ObjectHolder("wyvern_crafting_injector")   public static CraftingInjector          crafting_injector_wyvern;
    @ObjectHolder("awakened_crafting_injector") public static CraftingInjector          crafting_injector_awakened;
    @ObjectHolder("chaotic_crafting_injector")  public static CraftingInjector          crafting_injector_chaotic;
    @ObjectHolder("crafting_core")              public static FusionCraftingCore        crafting_core;
    @ObjectHolder("energy_core")                public static EnergyCore                energy_core;
    @ObjectHolder("energy_core_stabilizer")     public static EnergyCoreStabilizer      energy_core_stabilizer;
    @ObjectHolder("energy_pylon")               public static EnergyPylon               energy_pylon;
    @ObjectHolder("structure_block")            public static StructureBlock            structure_block;
    @ObjectHolder("reactor_core")               public static ReactorCore               reactor_core;
    @ObjectHolder("reactor_stabilizer")         public static ReactorComponent          reactor_stabilizer;
    @ObjectHolder("reactor_injector")           public static ReactorComponent          reactor_injector;
    @ObjectHolder("rain_sensor")                public static RainSensor                rain_sensor;
    @ObjectHolder("dislocation_inhibitor")      public static DislocationInhibitor      dislocation_inhibitor;
    @ObjectHolder("overworld_draconium_ore")    public static DraconiumOre              ore_draconium_overworld;
    @ObjectHolder("nether_draconium_ore")       public static DraconiumOre              ore_draconium_nether;
    @ObjectHolder("end_draconium_ore")          public static DraconiumOre              ore_draconium_end;
    @ObjectHolder("draconium_block")            public static DraconiumBlock            block_draconium;
    @ObjectHolder("awakened_draconium_block")   public static DraconiumBlock            block_draconium_awakened;
    @ObjectHolder("infused_obsidian")           public static BlockBCore                infused_obsidian;
    @ObjectHolder("basic_io_crystal")           public static EnergyCrystal             crystal_io_basic;
    @ObjectHolder("wyvern_io_crystal")          public static EnergyCrystal             crystal_io_wyvern;
    @ObjectHolder("draconic_io_crystal")        public static EnergyCrystal             crystal_io_draconic;
//    @ObjectHolder("chaotic_io_crystal")         public static EnergyCrystal             crystal_io_chaotic;
    @ObjectHolder("basic_relay_crystal")        public static EnergyCrystal             crystal_relay_basic;
    @ObjectHolder("wyvern_relay_crystal")       public static EnergyCrystal             crystal_relay_wyvern;
    @ObjectHolder("draconic_relay_crystal")     public static EnergyCrystal             crystal_relay_draconic;
//    @ObjectHolder("chaotic_relay_crystal")      public static EnergyCrystal             crystal_relay_chaotic;
    @ObjectHolder("basic_wireless_crystal")     public static EnergyCrystal             crystal_wireless_basic;
    @ObjectHolder("wyvern_wireless_crystal")    public static EnergyCrystal             crystal_wireless_wyvern;
    @ObjectHolder("draconic_wireless_crystal")  public static EnergyCrystal             crystal_wireless_draconic;
//    @ObjectHolder("chaotic_wireless_crystal")   public static EnergyCrystal             crystal_wireless_chaotic;
    @ObjectHolder("flux_gate")                  public static FlowGate                  flux_gate;
    @ObjectHolder("fluid_gate")                 public static FlowGate                  fluid_gate;

    //@formatter:on

    @SubscribeEvent
    public static void registerBlocks(RegistryEvent.Register<Block> event) {
        Properties machine = Properties.of(Material.METAL, MaterialColor.COLOR_GRAY).strength(3.0F, 8F).noOcclusion().requiresCorrectToolForDrops();
        Properties hardenedMachine = Properties.of(Material.METAL, MaterialColor.COLOR_GRAY).strength(20.0F, 600F).noOcclusion().requiresCorrectToolForDrops();
        Properties storageBlock = Properties.of(Material.METAL, MaterialColor.COLOR_GRAY).strength(30.0F, 600F).requiresCorrectToolForDrops();
        Properties stoneProp = Properties.of(Material.STONE, MaterialColor.COLOR_GRAY).strength(1.5F, 6F).requiresCorrectToolForDrops();
        Properties ore = Properties.of(Material.STONE, MaterialColor.COLOR_GRAY).strength(6.0F, 16F).requiresCorrectToolForDrops();

        //Machines
        event.getRegistry().register(new Generator(machine).setRegistryName("generator"));
        event.getRegistry().register(new EnergyTransfuser(machine).setRegistryName("energy_transfuser"));
        event.getRegistry().register(new DislocatorPedestal(machine).setRegistryName("dislocator_pedestal"));
        event.getRegistry().register(new DislocatorReceptacle(machine).setRegistryName("dislocator_receptacle"));
        event.getRegistry().register(new CreativeOPSource(machine).setRegistryName("creative_op_capacitor"));
//        event.getRegistry().register(new EntityDetector(machine).setRegistryName("entity_detector"));
//        event.getRegistry().register(new EntityDetector(machine).setRegistryName("advanced_entity_detector"));
        event.getRegistry().register(new StabilizedSpawner(machine).setRegistryName("stabilized_spawner"));
        event.getRegistry().register(new DraconiumChest(hardenedMachine).setRegistryName("draconium_chest"));
        event.getRegistry().register(new ParticleGenerator(machine).setRegistryName("particle_generator"));
        event.getRegistry().register(new DislocationInhibitor(machine).setRegistryName("dislocation_inhibitor"));
        //Stone Type
//        event.getRegistry().register(new RainSensor(stoneProp).setRegistryName("rain_sensor"));
        event.getRegistry().register(new Potentiometer(stoneProp).setRegistryName("potentiometer"));
        //Hardened Machine
        event.getRegistry().register(new Grinder(hardenedMachine).setRegistryName("grinder"));
        event.getRegistry().register(new Disenchanter(hardenedMachine).setRegistryName("disenchanter"));
        event.getRegistry().register(new CelestialManipulator(hardenedMachine).setRegistryName("celestial_manipulator"));
        event.getRegistry().register(new FlowGate(hardenedMachine, true).setRegistryName("flux_gate"));
        event.getRegistry().register(new FlowGate(hardenedMachine, false).setRegistryName("fluid_gate"));
        //Fusion Crafting
        event.getRegistry().register(new FusionCraftingCore(hardenedMachine).setRegistryName("crafting_core"));
        event.getRegistry().register(new CraftingInjector(hardenedMachine, TechLevel.DRACONIUM).setRegistryName("basic_crafting_injector"));
        event.getRegistry().register(new CraftingInjector(hardenedMachine, TechLevel.WYVERN).setRegistryName("wyvern_crafting_injector"));
        event.getRegistry().register(new CraftingInjector(hardenedMachine, TechLevel.DRACONIC).setRegistryName("awakened_crafting_injector"));
        event.getRegistry().register(new CraftingInjector(hardenedMachine, TechLevel.CHAOTIC).setRegistryName("chaotic_crafting_injector"));
        //Energy Core
        event.getRegistry().register(new EnergyCore(hardenedMachine).setRegistryName("energy_core"));
        event.getRegistry().register(new EnergyCoreStabilizer(hardenedMachine).setRegistryName("energy_core_stabilizer"));
        event.getRegistry().register(new EnergyPylon(hardenedMachine).setRegistryName("energy_pylon"));
        event.getRegistry().register(new StructureBlock(Properties.of(Material.METAL, MaterialColor.COLOR_GRAY).strength(5.0F, 12F).noDrops().noOcclusion()).setRegistryName("structure_block"));
        //Reactor
        event.getRegistry().register(new ReactorCore(hardenedMachine).setRegistryName("reactor_core"));
        event.getRegistry().register(new ReactorComponent(Properties.of(Material.METAL, MaterialColor.COLOR_GRAY).strength(5.0F, 6000F).noOcclusion(), false).setRegistryName("reactor_stabilizer"));
        event.getRegistry().register(new ReactorComponent(Properties.of(Material.METAL, MaterialColor.COLOR_GRAY).strength(5.0F, 6000F).noOcclusion(), true).setRegistryName("reactor_injector"));
        //Ore
        event.getRegistry().register(new DraconiumOre(ore).setRegistryName("overworld_draconium_ore"));
        event.getRegistry().register(new DraconiumOre(ore).setRegistryName("nether_draconium_ore"));
        event.getRegistry().register(new DraconiumOre(ore).setRegistryName("end_draconium_ore"));
        //Storage Blocks
        event.getRegistry().register(new DraconiumBlock(storageBlock).setMobResistant().setRegistryName("draconium_block"));
        event.getRegistry().register(new DraconiumBlock(storageBlock).setMobResistant().setRegistryName("awakened_draconium_block"));
        //Special
        event.getRegistry().register(new Portal(Properties.of(Material.GLASS).noOcclusion().noCollission().noDrops().strength(-1F)).setRegistryName("portal"));
        event.getRegistry().register(new ChaosCrystal(Properties.of(Material.GLASS).strength(100, 4000).noOcclusion().noDrops()).setRegistryName("chaos_crystal"));
        event.getRegistry().register(new ChaosCrystal(Properties.of(Material.GLASS).strength(100, 4000).noOcclusion().noDrops()).setRegistryName("chaos_crystal_part"));
        event.getRegistry().register(new BlockBCore(Block.Properties.of(Material.STONE, MaterialColor.COLOR_BLACK).strength(100.0F, 2400.0F)).setMobResistant().setRegistryName("infused_obsidian"));

        event.getRegistry().register(new PlacedItem(Properties.of(Material.GLASS).strength(5F, 12F).noOcclusion().noDrops()).setRegistryName("placed_item"));

        //Energy Crystals
        Properties crystalB = Properties.of(Material.GLASS, DyeColor.BLUE).strength(3.0F, 8F);      //TODO may want to tweak these after testing
        Properties crystalW = Properties.of(Material.GLASS, DyeColor.PURPLE).strength(5.0F, 16F);
        Properties crystalD = Properties.of(Material.GLASS, DyeColor.ORANGE).strength(8.0F, 32F);
        Properties crystalC = Properties.of(Material.GLASS, DyeColor.BLACK).strength(16.0F, 64F);
        event.getRegistry().register(new EnergyCrystal(crystalB, TechLevel.DRACONIUM, EnergyCrystal.CrystalType.CRYSTAL_IO).setRegistryName("basic_io_crystal"));
        event.getRegistry().register(new EnergyCrystal(crystalW, TechLevel.WYVERN, EnergyCrystal.CrystalType.CRYSTAL_IO).setRegistryName("wyvern_io_crystal"));
        event.getRegistry().register(new EnergyCrystal(crystalD, TechLevel.DRACONIC, EnergyCrystal.CrystalType.CRYSTAL_IO).setRegistryName("draconic_io_crystal"));
//        event.getRegistry().register(new EnergyCrystal(crystalC, CHAOTIC,   CRYSTAL_IO).setRegistryName("chaotic_io_crystal"));
        event.getRegistry().register(new EnergyCrystal(crystalB, TechLevel.DRACONIUM, EnergyCrystal.CrystalType.RELAY).setRegistryName("basic_relay_crystal"));
        event.getRegistry().register(new EnergyCrystal(crystalW, TechLevel.WYVERN, EnergyCrystal.CrystalType.RELAY).setRegistryName("wyvern_relay_crystal"));
        event.getRegistry().register(new EnergyCrystal(crystalD, TechLevel.DRACONIC, EnergyCrystal.CrystalType.RELAY).setRegistryName("draconic_relay_crystal"));
//        event.getRegistry().register(new EnergyCrystal(crystalC, CHAOTIC,   RELAY).setRegistryName("chaotic_relay_crystal"));
        event.getRegistry().register(new EnergyCrystal(crystalB, TechLevel.DRACONIUM, EnergyCrystal.CrystalType.WIRELESS).setRegistryName("basic_wireless_crystal"));
        event.getRegistry().register(new EnergyCrystal(crystalW, TechLevel.WYVERN, EnergyCrystal.CrystalType.WIRELESS).setRegistryName("wyvern_wireless_crystal"));
        event.getRegistry().register(new EnergyCrystal(crystalD, TechLevel.DRACONIC, EnergyCrystal.CrystalType.WIRELESS).setRegistryName("draconic_wireless_crystal"));
//        event.getRegistry().register(new EnergyCrystal(crystalC, CHAOTIC,   WIRELESS).setRegistryName("chaotic_wireless_crystal"));
    }


    //#################################################################
    // Items
    //#################################################################

    //@formatter:off
    //Components
    @ObjectHolder("draconium_dust")             public static Item                      dust_draconium;
    @ObjectHolder("awakened_draconium_dust")    public static Item                      dust_draconium_awakened;
    @ObjectHolder("draconium_ingot")            public static Item                      ingot_draconium;
    @ObjectHolder("awakened_draconium_ingot")   public static Item                      ingot_draconium_awakened;
    @ObjectHolder("draconium_nugget")           public static Item                      nugget_draconium;
    @ObjectHolder("awakened_draconium_nugget")  public static Item                      nugget_draconium_awakened;
    @ObjectHolder("draconium_core")             public static ItemCore                  core_draconium;
    @ObjectHolder("wyvern_core")                public static ItemCore                  core_wyvern;
    @ObjectHolder("awakened_core")              public static ItemCore                  core_awakened;
    @ObjectHolder("chaotic_core")               public static ItemCore                  core_chaotic;
    @ObjectHolder("wyvern_energy_core")         public static Item                      energy_core_wyvern;
    @ObjectHolder("draconic_energy_core")       public static Item                      energy_core_draconic;
    @ObjectHolder("chaotic_energy_core")        public static Item                      energy_core_chaotic;
    @ObjectHolder("dragon_heart")               public static Item                      dragon_heart;
    @ObjectHolder("chaos_shard")                public static Item                      chaos_shard;
    @ObjectHolder("large_chaos_frag")           public static Item                      chaos_frag_large;
    @ObjectHolder("medium_chaos_frag")          public static Item                      chaos_frag_medium;
    @ObjectHolder("small_chaos_frag")           public static Item                      chaos_frag_small;
    @ObjectHolder("module_core")                public static Item                      module_core;
    @ObjectHolder("reactor_prt_stab_frame")     public static Item                      reactor_prt_stab_frame;
    @ObjectHolder("reactor_prt_in_rotor")       public static Item                      reactor_prt_in_rotor;
    @ObjectHolder("reactor_prt_out_rotor")      public static Item                      reactor_prt_out_rotor;
    @ObjectHolder("reactor_prt_rotor_full")     public static Item                      reactor_prt_rotor_full;
    @ObjectHolder("reactor_prt_focus_ring")     public static Item                      reactor_prt_focus_ring;
    //Misc Tools
    @ObjectHolder("magnet")                     public static Magnet                    magnet;
    @ObjectHolder("advanced_magnet")            public static Magnet                    magnet_advanced;
    @ObjectHolder("dislocator")                 public static Dislocator                dislocator;
    @ObjectHolder("advanced_dislocator")        public static DislocatorAdvanced        dislocator_advanced;
    @ObjectHolder("p2p_dislocator")             public static BoundDislocator           dislocator_p2p;
    @ObjectHolder("p2p_dislocator_unbound")     public static BoundDislocator           dislocator_p2p_unbound;
    @ObjectHolder("player_dislocator")          public static BoundDislocator           dislocator_player;
    @ObjectHolder("player_dislocator_unbound")  public static BoundDislocator           dislocator_player_unbound;
    @ObjectHolder("crystal_binder")             public static CrystalBinder             crystal_binder;
    @ObjectHolder("info_tablet")                public static InfoTablet                info_tablet;
    @ObjectHolder("ender_energy_manipulator")   public static EnderEnergyManipulator    ender_energy_manipulator;
//    @ObjectHolder("creative_exchanger")         public static CreativeExchanger         creative_exchanger;
    @ObjectHolder("mob_soul")                   public static MobSoul                   mob_soul;
    //Tools
    @ObjectHolder("wyvern_capacitor")           public static DraconiumCapacitor        capacitor_wyvern;
    @ObjectHolder("draconic_capacitor")         public static DraconiumCapacitor        capacitor_draconic;
    @ObjectHolder("chaotic_capacitor")          public static DraconiumCapacitor        capacitor_chaotic;
    @ObjectHolder("creative_capacitor")         public static DraconiumCapacitor        capacitor_creative;
    @ObjectHolder("wyvern_shovel")              public static ModularShovel             shovel_wyvern;
    @ObjectHolder("draconic_shovel")            public static ModularShovel             shovel_draconic;
    @ObjectHolder("chaotic_shovel")             public static ModularShovel             shovel_chaotic;
    @ObjectHolder("wyvern_hoe")                 public static ModularHoe                hoe_wyvern;
    @ObjectHolder("draconic_hoe")               public static ModularHoe                hoe_draconic;
    @ObjectHolder("chaotic_hoe")                public static ModularHoe                hoe_chaotic;
    @ObjectHolder("wyvern_pickaxe")             public static ModularPickaxe            pickaxe_wyvern;
    @ObjectHolder("draconic_pickaxe")           public static ModularPickaxe            pickaxe_draconic;
    @ObjectHolder("chaotic_pickaxe")            public static ModularPickaxe            pickaxe_chaotic;
    @ObjectHolder("wyvern_axe")                 public static ModularAxe                axe_wyvern;
    @ObjectHolder("draconic_axe")               public static ModularAxe                axe_draconic;
    @ObjectHolder("chaotic_axe")                public static ModularAxe                axe_chaotic;
    @ObjectHolder("wyvern_bow")                 public static ModularBow                bow_wyvern;
    @ObjectHolder("draconic_bow")               public static ModularBow                bow_draconic;
    @ObjectHolder("chaotic_bow")                public static ModularBow                bow_chaotic;
    @ObjectHolder("wyvern_sword")               public static ModularSword              sword_wyvern;
    @ObjectHolder("draconic_sword")             public static ModularSword              sword_draconic;
    @ObjectHolder("chaotic_sword")              public static ModularSword              sword_chaotic;
    @ObjectHolder("draconic_staff")             public static ModularStaff              staff_draconic;
    @ObjectHolder("chaotic_staff")              public static ModularStaff              staff_chaotic;
    //Armor
    @ObjectHolder("wyvern_chestpiece")          public static ModularChestpiece         chestpiece_wyvern;
    @ObjectHolder("draconic_chestpiece")        public static ModularChestpiece         chestpiece_draconic;
    @ObjectHolder("chaotic_chestpiece")         public static ModularChestpiece         chestpiece_chaotic;
    //@formatter:on

    public static transient ArrayList<ResourceLocation> ITEM_REGISTRY_ORDER = new ArrayList<>();

    public static DETier WYVERN_TIER = new DETier(TechLevel.WYVERN);
    public static DETier DRACONIC_TIER = new DETier(TechLevel.DRACONIUM);
    public static DETier CHAOTIC_TIER = new DETier(TechLevel.CHAOTIC);

    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> event) {
        Block[] entityDetectors = {entity_detector, entity_detector_advanced};
        Supplier<Object[]> tabBlocks = () -> new Object[]{ore_draconium_end, block_draconium_awakened, infused_obsidian, draconium_chest, stabilized_spawner, grinder, disenchanter};
        CyclingItemGroup blockGroup = new CyclingItemGroup("draconicevolution.blocks", 40, tabBlocks, ITEM_REGISTRY_ORDER).setOffset(20);
        Supplier<Object[]> tabItems = () -> new Object[]{core_wyvern, ingot_draconium_awakened, sword_chaotic, chaos_shard, energy_core_draconic, staff_draconic, crystal_binder, dust_draconium, axe_draconic};
        CyclingItemGroup itemGroup = new CyclingItemGroup("draconicevolution.items", 40, tabItems, ITEM_REGISTRY_ORDER);

        TierSortingRegistry.registerTier(WYVERN_TIER, new ResourceLocation(DraconicEvolution.MODID, "wyvern_tier"), List.of(Tiers.NETHERITE), Collections.emptyList());
        if (DEConfig.useToolTierTags) {
            TierSortingRegistry.registerTier(DRACONIC_TIER, new ResourceLocation(DraconicEvolution.MODID, "draconic_tier"), List.of(WYVERN_TIER), Collections.emptyList());
            TierSortingRegistry.registerTier(CHAOTIC_TIER, new ResourceLocation(DraconicEvolution.MODID, "chaotic_tier"), List.of(DRACONIC_TIER), Collections.emptyList());
        }

        //@formatter:off
        registerItem(event, new ItemBlockBCore(generator,                   new Item.Properties().tab(blockGroup)).setRegistryName(Objects.requireNonNull(generator.getRegistryName())));
        registerItem(event, new ItemBlockBCore(grinder,                     new Item.Properties().tab(blockGroup)).setRegistryName(Objects.requireNonNull(grinder.getRegistryName())));
        registerItem(event, new ItemBlockBCore(disenchanter,                new Item.Properties().tab(blockGroup)).setRegistryName(Objects.requireNonNull(disenchanter.getRegistryName())));
        registerItem(event, new ItemBlockBCore(energy_transfuser,           new Item.Properties().tab(blockGroup)).setRegistryName(Objects.requireNonNull(energy_transfuser.getRegistryName())));
        registerItem(event, new ItemBlockBCore(dislocator_pedestal,         new Item.Properties().tab(blockGroup)).setRegistryName(Objects.requireNonNull(dislocator_pedestal.getRegistryName())));
        registerItem(event, new ItemBlockBCore(dislocator_receptacle,       new Item.Properties().tab(blockGroup)).setRegistryName(Objects.requireNonNull(dislocator_receptacle.getRegistryName())));
        registerItem(event, new ItemBlockBCore(creative_op_capacitor,       new Item.Properties().tab(blockGroup)).setRegistryName(Objects.requireNonNull(creative_op_capacitor.getRegistryName())));
//        registerItem(event, new ItemBlockBCore(entityDetectors[0],          new Item.Properties().tab(blockGroup)).setRegistryName(Objects.requireNonNull(entityDetectors[0].getRegistryName())));
//        registerItem(event, new ItemBlockBCore(entityDetectors[1],          new Item.Properties().tab(blockGroup)).setRegistryName(Objects.requireNonNull(entityDetectors[1].getRegistryName())));
        registerItem(event, new ItemBlockBCore(stabilized_spawner,          new Item.Properties().tab(blockGroup)).setRegistryName(Objects.requireNonNull(stabilized_spawner.getRegistryName())));
        registerItem(event, new ItemBlockBCore(potentiometer,               new Item.Properties().tab(blockGroup)).setRegistryName(Objects.requireNonNull(potentiometer.getRegistryName())));
        registerItem(event, new ItemBlockBCore(celestial_manipulator,       new Item.Properties().tab(blockGroup)).setRegistryName(Objects.requireNonNull(celestial_manipulator.getRegistryName())));
        registerItem(event, new ItemBlockBCore(draconium_chest,             new Item.Properties().tab(blockGroup).rarity(Rarity.RARE)).setRegistryName(Objects.requireNonNull(draconium_chest.getRegistryName())));
        registerItem(event, new ItemBlockBCore(particle_generator,          new Item.Properties().tab(blockGroup)).setRegistryName(Objects.requireNonNull(particle_generator.getRegistryName())));
        registerItem(event, new ItemBlockBCore(dislocation_inhibitor,       new Item.Properties().tab(blockGroup)).setRegistryName(Objects.requireNonNull(dislocation_inhibitor.getRegistryName())));
        registerItem(event, new ItemBlockBCore(placed_item,                 new Item.Properties()).setRegistryName(Objects.requireNonNull(placed_item.getRegistryName())));
        registerItem(event, new ItemBlockBCore(flux_gate,                   new Item.Properties().tab(blockGroup)).setRegistryName(Objects.requireNonNull(flux_gate.getRegistryName())));
        registerItem(event, new ItemBlockBCore(fluid_gate,                  new Item.Properties().tab(blockGroup)).setRegistryName(Objects.requireNonNull(fluid_gate.getRegistryName())));
        registerItem(event, new ItemBlockBCore(infused_obsidian,            new Item.Properties().tab(blockGroup)).setRegistryName(Objects.requireNonNull(infused_obsidian.getRegistryName())));
//        registerItem(event, new ItemBlockBCore(chaos_crystal,               new Item.Properties().tab(blockGroup)).setRegistryName(Objects.requireNonNull(chaos_crystal.getRegistryName())));
        registerItem(event, new ItemBlockBCore(crafting_injector_basic,     new Item.Properties().tab(blockGroup)).setRegistryName(Objects.requireNonNull(crafting_injector_basic.getRegistryName())));
        registerItem(event, new ItemBlockBCore(crafting_injector_wyvern,    new Item.Properties().tab(blockGroup)).setRegistryName(Objects.requireNonNull(crafting_injector_wyvern.getRegistryName())));
        registerItem(event, new ItemBlockBCore(crafting_injector_awakened,  new Item.Properties().tab(blockGroup)).setRegistryName(Objects.requireNonNull(crafting_injector_awakened.getRegistryName())));
        registerItem(event, new ItemBlockBCore(crafting_injector_chaotic,   new Item.Properties().tab(blockGroup)).setRegistryName(Objects.requireNonNull(crafting_injector_chaotic.getRegistryName())));
        registerItem(event, new ItemBlockBCore(crafting_core,               new Item.Properties().tab(blockGroup)).setRegistryName(Objects.requireNonNull(crafting_core.getRegistryName())));
        registerItem(event, new ItemBlockBCore(energy_core,                 new Item.Properties().tab(blockGroup)).setRegistryName(Objects.requireNonNull(energy_core.getRegistryName())));
        registerItem(event, new ItemBlockBCore(energy_core_stabilizer,      new Item.Properties().tab(blockGroup)).setRegistryName(Objects.requireNonNull(energy_core_stabilizer.getRegistryName())));
        registerItem(event, new ItemBlockBCore(energy_pylon,                new Item.Properties().tab(blockGroup)).setRegistryName(Objects.requireNonNull(energy_pylon.getRegistryName())));
        registerItem(event, new ItemBlockBCore(reactor_core,                new Item.Properties().tab(blockGroup)).setRegistryName(Objects.requireNonNull(reactor_core.getRegistryName())));
        registerItem(event, new ItemBlockBCore(reactor_stabilizer,          new Item.Properties().tab(blockGroup)).setRegistryName(Objects.requireNonNull(reactor_stabilizer.getRegistryName())));
        registerItem(event, new ItemBlockBCore(reactor_injector,            new Item.Properties().tab(blockGroup)).setRegistryName(Objects.requireNonNull(reactor_injector.getRegistryName())));
        registerItem(event, new ItemBlockBCore(block_draconium,             new Item.Properties().tab(blockGroup)).setRegistryName(Objects.requireNonNull(block_draconium.getRegistryName())));
        registerItem(event, new ItemBlockBCore(block_draconium_awakened,    new Item.Properties().tab(blockGroup)).setRegistryName(Objects.requireNonNull(block_draconium_awakened.getRegistryName())));
        registerItem(event, new ItemBlockBCore(ore_draconium_end,           new Item.Properties().tab(blockGroup)).setRegistryName(Objects.requireNonNull(ore_draconium_end.getRegistryName())));
        registerItem(event, new ItemBlockBCore(ore_draconium_nether,        new Item.Properties().tab(blockGroup)).setRegistryName(Objects.requireNonNull(ore_draconium_nether.getRegistryName())));
        registerItem(event, new ItemBlockBCore(ore_draconium_overworld,     new Item.Properties().tab(blockGroup)).setRegistryName(Objects.requireNonNull(ore_draconium_overworld.getRegistryName())));
        registerItem(event, new ItemBlockBCore(crystal_io_basic,            new Item.Properties().tab(blockGroup)).setRegistryName(Objects.requireNonNull(crystal_io_basic.getRegistryName())));
        registerItem(event, new ItemBlockBCore(crystal_io_wyvern,           new Item.Properties().tab(blockGroup)).setRegistryName(Objects.requireNonNull(crystal_io_wyvern.getRegistryName())));
        registerItem(event, new ItemBlockBCore(crystal_io_draconic,         new Item.Properties().tab(blockGroup)).setRegistryName(Objects.requireNonNull(crystal_io_draconic.getRegistryName())));
//        registerItem(event, new ItemBlockBCore(crystal_io_chaotic,          new Item.Properties().tab(blockGroup)).setRegistryName(Objects.requireNonNull(crystal_io_chaotic.getRegistryName())));
        registerItem(event, new ItemBlockBCore(crystal_relay_basic,         new Item.Properties().tab(blockGroup)).setRegistryName(Objects.requireNonNull(crystal_relay_basic.getRegistryName())));
        registerItem(event, new ItemBlockBCore(crystal_relay_wyvern,        new Item.Properties().tab(blockGroup)).setRegistryName(Objects.requireNonNull(crystal_relay_wyvern.getRegistryName())));
        registerItem(event, new ItemBlockBCore(crystal_relay_draconic,      new Item.Properties().tab(blockGroup)).setRegistryName(Objects.requireNonNull(crystal_relay_draconic.getRegistryName())));
//        registerItem(event, new ItemBlockBCore(crystal_relay_chaotic,       new Item.Properties().tab(blockGroup)).setRegistryName(Objects.requireNonNull(crystal_relay_chaotic.getRegistryName())));
        registerItem(event, new ItemBlockBCore(crystal_wireless_basic,      new Item.Properties().tab(blockGroup)).setRegistryName(Objects.requireNonNull(crystal_wireless_basic.getRegistryName())));
        registerItem(event, new ItemBlockBCore(crystal_wireless_wyvern,     new Item.Properties().tab(blockGroup)).setRegistryName(Objects.requireNonNull(crystal_wireless_wyvern.getRegistryName())));
        registerItem(event, new ItemBlockBCore(crystal_wireless_draconic,   new Item.Properties().tab(blockGroup)).setRegistryName(Objects.requireNonNull(crystal_wireless_draconic.getRegistryName())));
//        registerItem(event, new ItemBlockBCore(crystal_wireless_chaotic,    new Item.Properties().tab(blockGroup)).setRegistryName(Objects.requireNonNull(crystal_wireless_chaotic.getRegistryName())));
        //Components
        registerItem(event, new Item(new Item.Properties().tab(itemGroup)).setRegistryName("draconium_dust"));
        registerItem(event, new Item(new Item.Properties().tab(itemGroup)).setRegistryName("awakened_draconium_dust"));
        registerItem(event, new Item(new Item.Properties().tab(itemGroup)).setRegistryName("draconium_ingot"));
        registerItem(event, new Item(new Item.Properties().tab(itemGroup)).setRegistryName("awakened_draconium_ingot"));
        registerItem(event, new Item(new Item.Properties().tab(itemGroup)).setRegistryName("draconium_nugget"));
        registerItem(event, new Item(new Item.Properties().tab(itemGroup)).setRegistryName("awakened_draconium_nugget"));
        registerItem(event, new ItemCore(new Item.Properties().tab(itemGroup)).setRegistryName("draconium_core"));
        registerItem(event, new ItemCore(new Item.Properties().tab(itemGroup)).setRegistryName("wyvern_core"));
        registerItem(event, new ItemCore(new Item.Properties().tab(itemGroup)).setRegistryName("awakened_core"));
        registerItem(event, new ItemCore(new Item.Properties().tab(itemGroup)).setRegistryName("chaotic_core"));
        registerItem(event, new Item(new Item.Properties().tab(itemGroup)).setRegistryName("wyvern_energy_core"));
        registerItem(event, new Item(new Item.Properties().tab(itemGroup)).setRegistryName("draconic_energy_core"));
        registerItem(event, new Item(new Item.Properties().tab(itemGroup)).setRegistryName("chaotic_energy_core"));
        registerItem(event, new Item(new Item.Properties().tab(itemGroup)).setRegistryName("dragon_heart"));
        registerItem(event, new Item(new Item.Properties().tab(itemGroup)).setRegistryName("chaos_shard"));
        registerItem(event, new Item(new Item.Properties().tab(itemGroup)).setRegistryName("large_chaos_frag"));
        registerItem(event, new Item(new Item.Properties().tab(itemGroup)).setRegistryName("medium_chaos_frag"));
        registerItem(event, new Item(new Item.Properties().tab(itemGroup)).setRegistryName("small_chaos_frag"));
        registerItem(event, new Item(new Item.Properties().tab(itemGroup)).setRegistryName("module_core"));

        registerItem(event, new Item(new Item.Properties().tab(itemGroup)).setRegistryName("reactor_prt_stab_frame"));
        registerItem(event, new Item(new Item.Properties().tab(itemGroup)).setRegistryName("reactor_prt_in_rotor"));
        registerItem(event, new Item(new Item.Properties().tab(itemGroup)).setRegistryName("reactor_prt_out_rotor"));
        registerItem(event, new Item(new Item.Properties().tab(itemGroup)).setRegistryName("reactor_prt_rotor_full"));
        registerItem(event, new Item(new Item.Properties().tab(itemGroup)).setRegistryName("reactor_prt_focus_ring"));
//        //Items
        registerItem(event, new Magnet(new Item.Properties().stacksTo(1).tab(itemGroup), 8).setRegistryName("magnet"));
        registerItem(event, new Magnet(new Item.Properties().stacksTo(1).tab(itemGroup), 32).setRegistryName("advanced_magnet"));
        registerItem(event, new Dislocator(new Item.Properties().stacksTo(1).durability(31).tab(itemGroup)).setRegistryName("dislocator"));
        registerItem(event, new DislocatorAdvanced(new Item.Properties().stacksTo(1).tab(itemGroup)).setRegistryName("advanced_dislocator"));
        registerItem(event, new BoundDislocator(new Item.Properties().stacksTo(1).tab(itemGroup)).setRegistryName("p2p_dislocator"));
        registerItem(event, new BoundDislocator(new Item.Properties().stacksTo(1).tab(itemGroup)).setRegistryName("p2p_dislocator_unbound"));
        registerItem(event, new BoundDislocator(new Item.Properties().stacksTo(1).tab(itemGroup)).setRegistryName("player_dislocator"));
        registerItem(event, new BoundDislocator(new Item.Properties().stacksTo(1).tab(itemGroup)).setRegistryName("player_dislocator_unbound"));
        registerItem(event, new CrystalBinder(new Item.Properties().stacksTo(1).tab(itemGroup)).setRegistryName("crystal_binder"));
        registerItem(event, new InfoTablet(new Item.Properties().stacksTo(1).tab(itemGroup)).setRegistryName("info_tablet"));
//        registerItem(event, new EnderEnergyManipulator(new Item.Properties().group(itemGroup)).setRegistryName("ender_energy_manipulator"));
//        registerItem(event, new CreativeExchanger(new Item.Properties().group(itemGroup)).setRegistryName("creative_exchanger"));
        registerItem(event, new MobSoul(new Item.Properties().tab(itemGroup)).setRegistryName("mob_soul"));

//        //Tools
        TechProperties wyvernTools = (TechProperties) new TechProperties(TechLevel.WYVERN).tab(itemGroup).rarity(Rarity.UNCOMMON).durability(-1).fireResistant();
        TechProperties draconicTools = (TechProperties) new TechProperties(TechLevel.DRACONIC).tab(itemGroup).rarity(Rarity.RARE).durability(-1).fireResistant();
        TechProperties chaoticTools = (TechProperties) new TechProperties(TechLevel.CHAOTIC).tab(itemGroup).rarity(Rarity.EPIC).durability(-1).fireResistant();
        registerItem(event, new DraconiumCapacitor(wyvernTools).setRegistryName("wyvern_capacitor"));
        registerItem(event, new DraconiumCapacitor(draconicTools).setRegistryName("draconic_capacitor"));
        registerItem(event, new DraconiumCapacitor(chaoticTools).setRegistryName("chaotic_capacitor"));
        registerItem(event, new DraconiumCapacitor(chaoticTools).setRegistryName("creative_capacitor"));
        registerItem(event, new ModularShovel(WYVERN_TIER, wyvernTools).setRegistryName("wyvern_shovel"));
        registerItem(event, new ModularShovel(DRACONIC_TIER, draconicTools).setRegistryName("draconic_shovel"));
        registerItem(event, new ModularShovel(CHAOTIC_TIER, chaoticTools).setRegistryName("chaotic_shovel"));
        registerItem(event, new ModularHoe(WYVERN_TIER, wyvernTools).setRegistryName("wyvern_hoe"));
        registerItem(event, new ModularHoe(DRACONIC_TIER, draconicTools).setRegistryName("draconic_hoe"));
        registerItem(event, new ModularHoe(CHAOTIC_TIER, chaoticTools).setRegistryName("chaotic_hoe"));
        registerItem(event, new ModularPickaxe(WYVERN_TIER, wyvernTools).setRegistryName("wyvern_pickaxe"));
        registerItem(event, new ModularPickaxe(DRACONIC_TIER, draconicTools).setRegistryName("draconic_pickaxe"));
        registerItem(event, new ModularPickaxe(CHAOTIC_TIER, chaoticTools).setRegistryName("chaotic_pickaxe"));
        registerItem(event, new ModularAxe(WYVERN_TIER, wyvernTools).setRegistryName("wyvern_axe"));
        registerItem(event, new ModularAxe(DRACONIC_TIER, draconicTools).setRegistryName("draconic_axe"));
        registerItem(event, new ModularAxe(CHAOTIC_TIER, chaoticTools).setRegistryName("chaotic_axe"));
        registerItem(event, new ModularBow(wyvernTools).setRegistryName("wyvern_bow"));
        registerItem(event, new ModularBow(draconicTools).setRegistryName("draconic_bow"));
        registerItem(event, new ModularBow(chaoticTools).setRegistryName("chaotic_bow"));
        registerItem(event, new ModularSword(WYVERN_TIER, wyvernTools).setRegistryName("wyvern_sword"));
        registerItem(event, new ModularSword(DRACONIC_TIER, draconicTools).setRegistryName("draconic_sword"));
        registerItem(event, new ModularSword(CHAOTIC_TIER, chaoticTools).setRegistryName("chaotic_sword"));
        registerItem(event, new ModularStaff(DRACONIC_TIER, draconicTools).setRegistryName("draconic_staff"));
        registerItem(event, new ModularStaff(CHAOTIC_TIER, chaoticTools).setRegistryName("chaotic_staff"));
        //Armor
        registerItem(event, new ModularChestpiece(wyvernTools).setRegistryName("wyvern_chestpiece"));
        registerItem(event, new ModularChestpiece(draconicTools).setRegistryName("draconic_chestpiece"));
        registerItem(event, new ModularChestpiece(chaoticTools).setRegistryName("chaotic_chestpiece"));
        //@formatter:on
    }

    private static void registerItem(RegistryEvent.Register<Item> event, Item item) {
        event.getRegistry().register(item);
        ITEM_REGISTRY_ORDER.add(item.getRegistryName());
    }


    //#################################################################
    // Entities
    //#################################################################

    @ObjectHolder("draconic_guardian")
    public static EntityType<DraconicGuardianEntity> draconicGuardian;
    @ObjectHolder("guardian_projectile")
    public static EntityType<GuardianProjectileEntity> guardianProjectile;
    @ObjectHolder("guardian_crystal")
    public static EntityType<GuardianCrystalEntity> guardianCrystal;
    @ObjectHolder("draconic_arrow")
    public static EntityType<DraconicArrowEntity> draconicArrow;
    @ObjectHolder("guardian_wither")
    public static EntityType<GuardianWither> guardianWither;

    @SuppressWarnings("unchecked")
    @SubscribeEvent
    public static void registerEntities(RegistryEvent.Register<EntityType<?>> event) {
        event.getRegistry().register(EntityType.Builder.of(DraconicGuardianEntity::new, MobCategory.MONSTER).fireImmune().sized(16.0F, 8.0F).clientTrackingRange(20).build("draconic_guardian").setRegistryName("draconic_guardian"));
        event.getRegistry().register(EntityType.Builder.of(GuardianWither::new, MobCategory.MONSTER).fireImmune().immuneTo(Blocks.WITHER_ROSE).sized(0.9F, 3.5F).clientTrackingRange(10).build("guardian_wither").setRegistryName("guardian_wither"));

        event.getRegistry().register(EntityType.Builder.of(GuardianProjectileEntity::new, MobCategory.MISC).fireImmune().sized(2F, 2F).clientTrackingRange(20)/*.updateInterval(10)*/.build("guardian_projectile").setRegistryName("guardian_projectile"));
        event.getRegistry().register(EntityType.Builder.of(GuardianCrystalEntity::new, MobCategory.MISC).fireImmune().sized(2F, 2F).clientTrackingRange(20).updateInterval(100).build("guardian_crystal").setRegistryName("guardian_crystal"));
        event.getRegistry().register(EntityType.Builder.<DraconicArrowEntity>of(DraconicArrowEntity::new, MobCategory.MISC).sized(0.5F, 0.5F).clientTrackingRange(4).updateInterval(20).build("draconic_arrow").setRegistryName("draconic_arrow"));
    }

    @ObjectHolder("reaper_enchantment")
    public static EnchantmentReaper reaperEnchant;

    @SubscribeEvent
    public static void registerEnchantments(RegistryEvent.Register<Enchantment> event) {
        event.getRegistry().register(new EnchantmentReaper().setRegistryName("reaper_enchantment"));
    }

        @SubscribeEvent
    public static void registerAttributes(EntityAttributeCreationEvent event) {
        event.put(draconicGuardian, DraconicGuardianEntity.registerAttributes().build());
        event.put(guardianWither, GuardianWither.createAttributes().build());
    }

    @SubscribeEvent
    public static void registerRecipeType(RegistryEvent.Register<RecipeSerializer<?>> event) {
        event.getRegistry().register(new FusionRecipe.Serializer().setRegistryName("fusion_crafting"));
    }

    @ObjectHolder("guardian_manager")
    public static WorldEntityType<GuardianFightManager> guardianManagerType;

    @SubscribeEvent
    public static void registerWorldEntityType(RegistryEvent.Register<WorldEntityType<?>> event) {
        event.getRegistry().register(new WorldEntityType<>(GuardianFightManager::new).setRegistryName("guardian_manager"));
    }
}