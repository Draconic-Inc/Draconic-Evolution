package com.brandon3055.draconicevolution.init;

import codechicken.lib.gui.SimpleItemGroup;
import com.brandon3055.brandonscore.BrandonsCore;
import com.brandon3055.brandonscore.blocks.BlockBCore;
import com.brandon3055.brandonscore.blocks.ItemBlockBCore;
import com.brandon3055.brandonscore.client.utils.CyclingItemGroup;
import com.brandon3055.brandonscore.inventory.ContainerBCBase;
import com.brandon3055.draconicevolution.DraconicEvolution;
import com.brandon3055.draconicevolution.api.TechLevel;
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
import com.brandon3055.draconicevolution.blocks.tileentity.flowgate.TileFluidGate;
import com.brandon3055.draconicevolution.blocks.tileentity.flowgate.TileFluxGate;
import com.brandon3055.draconicevolution.inventory.*;
import com.brandon3055.draconicevolution.items.EnderEnergyManipulator;
import com.brandon3055.draconicevolution.items.InfoTablet;
import com.brandon3055.draconicevolution.items.ItemCore;
import com.brandon3055.draconicevolution.items.MobSoul;
import com.brandon3055.draconicevolution.items.tools.*;
import com.brandon3055.draconicevolution.modules_temp.impl.TestModuleHost;
import net.minecraft.block.Block;
import net.minecraft.block.Block.Properties;
import net.minecraft.block.material.Material;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.DyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.extensions.IForgeContainerType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ObjectHolder;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

import static com.brandon3055.draconicevolution.api.TechLevel.*;
import static com.brandon3055.draconicevolution.api.TechLevel.DRACONIC;
import static com.brandon3055.draconicevolution.blocks.energynet.EnergyCrystal.CrystalType.*;
import static com.brandon3055.draconicevolution.blocks.energynet.EnergyCrystal.CrystalType.WIRELESS;
import static com.brandon3055.draconicevolution.inventory.GuiLayoutFactories.*;
import static net.minecraft.block.material.Material.GLASS;
import static net.minecraft.block.material.Material.IRON;
import static net.minecraft.block.material.MaterialColor.GRAY;
import static net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus.MOD;

/**
 * Created by brandon3055 on 18/3/2016.
 * This class contains a reference to all blocks and items in Draconic Evolution
 */
@Mod.EventBusSubscriber(modid = DraconicEvolution.MODID, bus = MOD)
@ObjectHolder(DraconicEvolution.MODID)
public class DEContent {

    //#################################################################
    // Tile Entities
    //#################################################################

    //@formatter:off
    @ObjectHolder("generator")                  public static TileEntityType<TileGenerator>             tile_generator;
    @ObjectHolder("grinder")                    public static TileEntityType<TileGrinder>               tile_grinder;
    @ObjectHolder("disenchanter")               public static TileEntityType<TileDissEnchanter>         tile_disenchanter;
    @ObjectHolder("energy_infuser")             public static TileEntityType<TileEnergyInfuser>         tile_energy_infuser;
    @ObjectHolder("dislocator_pedestal")        public static TileEntityType<TileDislocatorPedestal>    tile_dislocator_pedestal;
    @ObjectHolder("dislocator_receptacle")      public static TileEntityType<TileDislocatorReceptacle>  tile_dislocator_receptacle;
    @ObjectHolder("creative_op_capacitor")      public static TileEntityType<TileCreativeOPCapacitor>   tile_creative_op_capacitor;
    @ObjectHolder("entity_detector")            public static TileEntityType<TileEntityDetector>        tile_entity_detector;
    @ObjectHolder("entity_detector_advanced")   public static TileEntityType<TileEntityDetector>        tile_entity_detector_advanced;
    @ObjectHolder("stabilized_spawner")         public static TileEntityType<TileStabilizedSpawner>     tile_stabilized_spawner;
    @ObjectHolder("potentiometer")              public static TileEntityType<TilePotentiometer>         tile_potentiometer;
    @ObjectHolder("celestial_manipulator")      public static TileEntityType<TileCelestialManipulator>  tile_celestial_manipulator;
    @ObjectHolder("draconium_chest")            public static TileEntityType<TileDraconiumChest>        tile_draconium_chest;
    @ObjectHolder("particle_generator")         public static TileEntityType<TileParticleGenerator>     tile_particle_generator;
    @ObjectHolder("placed_item")                public static TileEntityType<TilePlacedItem>            tile_placed_item;
    @ObjectHolder("portal")                     public static TileEntityType<TilePortal>                tile_portal;
    @ObjectHolder("portal_client")              public static TileEntityType<TilePortalClient>          tile_portal_client;//TODO?
    @ObjectHolder("chaos_crystal")              public static TileEntityType<TileChaosCrystal>          tile_chaos_crystal;
    @ObjectHolder("crafting_injector")          public static TileEntityType<TileCraftingInjector>      tile_crafting_injector;
    @ObjectHolder("crafting_core")              public static TileEntityType<TileCraftingCore>          tile_crafting_core;
    @ObjectHolder("storage_core")               public static TileEntityType<TileEnergyCore>            tile_storage_core;
    @ObjectHolder("core_stabilizer")            public static TileEntityType<TileEnergyCoreStabilizer>  tile_core_stabilizer;
    @ObjectHolder("energy_pylon")               public static TileEntityType<TileEnergyPylon>           tile_energy_pylon;
    @ObjectHolder("core_structure")             public static TileEntityType<TileCoreStructure>         tile_core_structure;
    @ObjectHolder("reactor_core")               public static TileEntityType<TileReactorCore>           tile_reactor_core;
    @ObjectHolder("reactor_stabilizer")         public static TileEntityType<TileReactorStabilizer>     tile_reactor_stabilizer;
    @ObjectHolder("reactor_injector")           public static TileEntityType<TileReactorInjector>       tile_reactor_injector;
    @ObjectHolder("flux_gate")                  public static TileEntityType<TileFluxGate>              tile_flux_gate;
    @ObjectHolder("fluid_gate")                 public static TileEntityType<TileFluidGate>             tile_fluid_gate;
    @ObjectHolder("crystal_io")                 public static TileEntityType<TileCrystalDirectIO>       tile_crystal_io;
    @ObjectHolder("crystal_relay")              public static TileEntityType<TileCrystalRelay>          tile_crystal_relay;
    @ObjectHolder("crystal_wireless")           public static TileEntityType<TileCrystalWirelessIO>     tile_crystal_wireless;
    //@formatter:on

    @SubscribeEvent
    public static void registerTileEntity(RegistryEvent.Register<TileEntityType<?>> event) {
        Block[] entityDetectors = {entity_detector, entity_detector_advanced};
        Block[] craftInjectors = {crafting_injector_basic, crafting_injector_wyvern, crafting_injector_awakened, crafting_injector_chaotic};
        //@formatter:off
        event.getRegistry().register(TileEntityType.Builder.create(TileGenerator::new,            generator              ).build(null).setRegistryName("generator"));
        event.getRegistry().register(TileEntityType.Builder.create(TileGrinder::new,              grinder                ).build(null).setRegistryName("grinder"));
//        event.getRegistry().register(TileEntityType.Builder.create(TileDissEnchanter::new,        disenchanter           ).build(null).setRegistryName("disenchanter"));
//        event.getRegistry().register(TileEntityType.Builder.create(TileEnergyInfuser::new,        energy_infuser         ).build(null).setRegistryName("energy_infuser"));
//        event.getRegistry().register(TileEntityType.Builder.create(TileDislocatorPedestal::new,   dislocator_pedestal    ).build(null).setRegistryName("dislocator_pedestal"));
//        event.getRegistry().register(TileEntityType.Builder.create(TileDislocatorReceptacle::new, dislocator_receptacle  ).build(null).setRegistryName("dislocator_receptacle"));
        event.getRegistry().register(TileEntityType.Builder.create(TileCreativeOPCapacitor::new,  creative_op_capacitor  ).build(null).setRegistryName("creative_op_capacitor"));
//        event.getRegistry().register(TileEntityType.Builder.create(TileEntityDetector::new,       entityDetectors        ).build(null).setRegistryName("entity_detector"));
        event.getRegistry().register(TileEntityType.Builder.create(TileStabilizedSpawner::new,    stabilized_spawner     ).build(null).setRegistryName("stabilized_spawner"));
//        event.getRegistry().register(TileEntityType.Builder.create(TilePotentiometer::new,        potentiometer          ).build(null).setRegistryName("potentiometer"));
//        event.getRegistry().register(TileEntityType.Builder.create(TileCelestialManipulator::new, celestial_manipulator  ).build(null).setRegistryName("celestial_manipulator"));
//        event.getRegistry().register(TileEntityType.Builder.create(TileDraconiumChest::new,       draconium_chest        ).build(null).setRegistryName("draconium_chest"));
//        event.getRegistry().register(TileEntityType.Builder.create(TileParticleGenerator::new,    particle_generator     ).build(null).setRegistryName("particle_generator"));
//        event.getRegistry().register(TileEntityType.Builder.create(TilePlacedItem::new,           placed_item            ).build(null).setRegistryName("placed_item"));
//        event.getRegistry().register(TileEntityType.Builder.create(TilePortal::new,               portal                 ).build(null).setRegistryName("portal"));
//        event.getRegistry().register(TileEntityType.Builder.create(TilePortalClient::new,         portal                 ).build(null).setRegistryName("portal_client"));
//        event.getRegistry().register(TileEntityType.Builder.create(TileChaosCrystal::new,         chaos_crystal          ).build(null).setRegistryName("chaos_crystal"));
//        event.getRegistry().register(TileEntityType.Builder.create(TileCraftingInjector::new,     craftInjectors         ).build(null).setRegistryName("crafting_injector"));
//        event.getRegistry().register(TileEntityType.Builder.create(TileCraftingCore::new,         crafting_core          ).build(null).setRegistryName("crafting_core"));
        event.getRegistry().register(TileEntityType.Builder.create(TileEnergyCore::new,          energy_core            ).build(null).setRegistryName("storage_core"));
        event.getRegistry().register(TileEntityType.Builder.create(TileEnergyCoreStabilizer::new,       energy_core_stabilizer ).build(null).setRegistryName("core_stabilizer"));
        event.getRegistry().register(TileEntityType.Builder.create(TileEnergyPylon::new,          energy_pylon           ).build(null).setRegistryName("energy_pylon"));
        event.getRegistry().register(TileEntityType.Builder.create(TileCoreStructure::new,        energy_core_structure  ).build(null).setRegistryName("core_structure"));
//        event.getRegistry().register(TileEntityType.Builder.create(TileReactorCore::new,          reactor_core           ).build(null).setRegistryName("reactor_core"));
//        event.getRegistry().register(TileEntityType.Builder.create(TileReactorStabilizer::new,    reactor_stabilizer     ).build(null).setRegistryName("reactor_stabilizer"));
//        event.getRegistry().register(TileEntityType.Builder.create(TileReactorInjector::new,      reactor_injector       ).build(null).setRegistryName("reactor_injector"));

        event.getRegistry().register(TileEntityType.Builder.create(() -> new TileCrystalDirectIO(), crystal_io_basic, crystal_io_wyvern, crystal_io_draconic/*, crystal_io_chaotic*/).build(null).setRegistryName("crystal_io"));
        event.getRegistry().register(TileEntityType.Builder.create(TileCrystalRelay::new, crystal_relay_basic, crystal_relay_wyvern, crystal_relay_draconic/*, crystal_relay_chaotic*/).build(null).setRegistryName("crystal_relay"));
        event.getRegistry().register(TileEntityType.Builder.create(TileCrystalWirelessIO::new, crystal_wireless_basic, crystal_wireless_wyvern, crystal_wireless_draconic/*, crystal_wireless_chaotic*/).build(null).setRegistryName("crystal_wireless"));
        //@formatter:on
    }

    //#################################################################
    // Containers
    //#################################################################

    //@formatter:off
    @ObjectHolder("generator")              public static ContainerType<ContainerBCBase<TileGenerator>>             container_generator;
    @ObjectHolder("grinder")                public static ContainerType<ContainerBCBase<TileGrinder>>               container_grinder;
    @ObjectHolder("energy_core")            public static ContainerType<ContainerBCBase<TileEnergyCore>>            container_energy_core;
    @ObjectHolder("dissenchanter")          public static ContainerType<ContainerDissEnchanter>                     container_dissenchanter;
    @ObjectHolder("draconium_chest")        public static ContainerType<ContainerDraconiumChest>                    container_draconium_chest;
    @ObjectHolder("celestial_manipulator")  public static ContainerType<ContainerDummy<TileCelestialManipulator>>   container_celestial_manipulator;
    @ObjectHolder("energy_crystal")         public static ContainerType<ContainerEnergyCrystal>                     container_energy_crystal;
    @ObjectHolder("energy_infuser")         public static ContainerType<ContainerEnergyInfuser>                     container_energy_infuser;
    @ObjectHolder("fusion_crafting_core")   public static ContainerType<ContainerFusionCraftingCore>                container_fusion_crafting_core;
    @ObjectHolder("reactor")                public static ContainerType<ContainerReactor>                           container_reactor;
    //@formatter:on

    @SubscribeEvent
    public static void registerContainers(RegistryEvent.Register<ContainerType<?>> event) {
        event.getRegistry().register(IForgeContainerType.create((id, playerInv, extraData) -> new ContainerBCBase<>(container_generator, id, playerInv, extraData, GENERATOR_LAYOUT)).setRegistryName("generator"));
        event.getRegistry().register(IForgeContainerType.create((id, playerInv, extraData) -> new ContainerBCBase<>(container_grinder, id, playerInv, extraData, GRINDER_LAYOUT)).setRegistryName("grinder"));

        event.getRegistry().register(IForgeContainerType.create((id, playerInv, extraData) -> new ContainerBCBase<>(container_energy_core, id, playerInv, extraData, ENERGY_CORE_LAYOUT)).setRegistryName("energy_core"));

        event.getRegistry().register(IForgeContainerType.create(ContainerDissEnchanter::new).setRegistryName("dissenchanter"));
        event.getRegistry().register(IForgeContainerType.create((windowId, inv, data) -> new ContainerDummy<TileCelestialManipulator>(container_celestial_manipulator, windowId, inv, data)).setRegistryName("celestial_manipulator"));
        event.getRegistry().register(IForgeContainerType.create(ContainerDraconiumChest::new).setRegistryName("draconium_chest"));
        event.getRegistry().register(IForgeContainerType.create(ContainerEnergyCrystal::new).setRegistryName("energy_crystal"));
        event.getRegistry().register(IForgeContainerType.create(ContainerEnergyInfuser::new).setRegistryName("energy_infuser"));
        event.getRegistry().register(IForgeContainerType.create(ContainerFusionCraftingCore::new).setRegistryName("fusion_crafting_core"));
        event.getRegistry().register(IForgeContainerType.create(ContainerReactor::new).setRegistryName("reactor"));
//        event.getRegistry().register(IForgeContainerType.create(ContainerDummy::new).setRegistryName("container_dummy"));
//        event.getRegistry().register(IForgeContainerType.create(ContainerJunkFilter::new).setRegistryName("container_junk_filter"));
//        event.getRegistry().register(IForgeContainerType.create(ContainerRecipeBuilder::new).setRegistryName("container_recipe_builder"));
    }


    //#################################################################
    // Blocks
    //#################################################################

    //@formatter:off
    @ObjectHolder("generator")                  public static Generator                 generator;
    @ObjectHolder("grinder")                    public static Grinder                   grinder;
    @ObjectHolder("disenchanter")               public static DissEnchanter             disenchanter;
    @ObjectHolder("energy_infuser")             public static EnergyInfuser             energy_infuser;
    @ObjectHolder("dislocator_pedestal")        public static DislocatorPedestal        dislocator_pedestal;
    @ObjectHolder("dislocator_receptacle")      public static DislocatorReceptacle      dislocator_receptacle;
    @ObjectHolder("creative_op_capacitor")      public static CreativeOPSource          creative_op_capacitor;
    @ObjectHolder("entity_detector")            public static EntityDetector            entity_detector;
    @ObjectHolder("entity_detector_advanced")   public static EntityDetector            entity_detector_advanced;
    @ObjectHolder("stabilized_spawner")         public static StabilizedSpawner         stabilized_spawner;
    @ObjectHolder("potentiometer")              public static Potentiometer             potentiometer;
    @ObjectHolder("celestial_manipulator")      public static CelestialManipulator      celestial_manipulator;
    @ObjectHolder("draconium_chest")            public static DraconiumChest            draconium_chest;
    @ObjectHolder("particle_generator")         public static ParticleGenerator         particle_generator;
    @ObjectHolder("placed_item")                public static PlacedItem                placed_item;
    @ObjectHolder("portal")                     public static Portal                    portal;
    @ObjectHolder("chaos_crystal")              public static ChaosCrystal              chaos_crystal;
    @ObjectHolder("crafting_injector_basic")    public static CraftingInjector          crafting_injector_basic;
    @ObjectHolder("crafting_injector_wyvern")   public static CraftingInjector          crafting_injector_wyvern;
    @ObjectHolder("crafting_injector_awakened") public static CraftingInjector          crafting_injector_awakened;
    @ObjectHolder("crafting_injector_chaotic")  public static CraftingInjector          crafting_injector_chaotic;
    @ObjectHolder("crafting_core")              public static FusionCraftingCore        crafting_core;
    @ObjectHolder("energy_core")                public static EnergyCore                energy_core;
    @ObjectHolder("energy_core_stabilizer")     public static EnergyCoreStabilizer      energy_core_stabilizer;
    @ObjectHolder("energy_pylon")               public static EnergyPylon               energy_pylon;
    @ObjectHolder("energy_core_structure")      public static EnergyCoreStructureBlock  energy_core_structure;
    @ObjectHolder("reactor_core")               public static ReactorCore               reactor_core;
    @ObjectHolder("reactor_stabilizer")         public static ReactorComponent          reactor_stabilizer;
    @ObjectHolder("reactor_injector")           public static ReactorComponent          reactor_injector;
    @ObjectHolder("rain_sensor")                public static RainSensor                rain_sensor;
    @ObjectHolder("dislocation_inhibitor")      public static DislocationInhibitor      dislocation_inhibitor;
    @ObjectHolder("ore_draconium_overworld")    public static DraconiumOre              ore_draconium_overworld;
    @ObjectHolder("ore_draconium_nether")       public static DraconiumOre              ore_draconium_nether;
    @ObjectHolder("ore_draconium_end")          public static DraconiumOre              ore_draconium_end;
    @ObjectHolder("block_draconium")            public static DraconiumBlock            block_draconium;
    @ObjectHolder("block_draconium_awakened")   public static DraconiumBlock            block_draconium_awakened;
    @ObjectHolder("infused_obsidian")           public static BlockBCore                infused_obsidian;
    @ObjectHolder("crystal_io_basic")           public static EnergyCrystal             crystal_io_basic;
    @ObjectHolder("crystal_io_wyvern")          public static EnergyCrystal             crystal_io_wyvern;
    @ObjectHolder("crystal_io_draconic")        public static EnergyCrystal             crystal_io_draconic;
//    @ObjectHolder("crystal_io_chaotic")         public static EnergyCrystal             crystal_io_chaotic;
    @ObjectHolder("crystal_relay_basic")        public static EnergyCrystal             crystal_relay_basic;
    @ObjectHolder("crystal_relay_wyvern")       public static EnergyCrystal             crystal_relay_wyvern;
    @ObjectHolder("crystal_relay_draconic")     public static EnergyCrystal             crystal_relay_draconic;
//    @ObjectHolder("crystal_relay_chaotic")      public static EnergyCrystal             crystal_relay_chaotic;
    @ObjectHolder("crystal_wireless_basic")     public static EnergyCrystal             crystal_wireless_basic;
    @ObjectHolder("crystal_wireless_wyvern")    public static EnergyCrystal             crystal_wireless_wyvern;
    @ObjectHolder("crystal_wireless_draconic")  public static EnergyCrystal             crystal_wireless_draconic;
//    @ObjectHolder("crystal_wireless_chaotic")   public static EnergyCrystal             crystal_wireless_chaotic;
    //@formatter:on

    @SubscribeEvent
    public static void registerBlocks(RegistryEvent.Register<Block> event) {
        Properties machine = Properties.create(IRON, GRAY).hardnessAndResistance(3.0F, 8F);
        Properties hardenedMachine = Properties.create(IRON, GRAY).hardnessAndResistance(5.0F, 12F);
        Properties storageBlock = Properties.create(IRON, GRAY).hardnessAndResistance(30.0F, 600F);
        Properties stoneProp = Properties.create(Material.ROCK, GRAY).hardnessAndResistance(1.5F, 6F);
        Properties ore = Properties.create(Material.ROCK, GRAY).hardnessAndResistance(6.0F, 16F);

        //Machines
        event.getRegistry().register(new Generator(machine).setRegistryName("generator"));
//        event.getRegistry().register(new EnergyInfuser(machine).setRegistryName("energy_infuser"));
//        event.getRegistry().register(new DislocatorPedestal(machine).setRegistryName("dislocator_pedestal"));
//        event.getRegistry().register(new DislocatorReceptacle(machine).setRegistryName("dislocator_receptacle"));
        event.getRegistry().register(new CreativeOPSource(machine).setRegistryName("creative_op_capacitor"));
//        event.getRegistry().register(new EntityDetector(machine).setRegistryName("entity_detector"));
//        event.getRegistry().register(new EntityDetector(machine).setRegistryName("entity_detector_advanced"));
        event.getRegistry().register(new StabilizedSpawner(machine).setRegistryName("stabilized_spawner"));
//        event.getRegistry().register(new DraconiumChest(machine).setRegistryName("draconium_chest"));
//        event.getRegistry().register(new ParticleGenerator(machine).setRegistryName("particle_generator"));
//        event.getRegistry().register(new DislocationInhibitor(machine).setRegistryName("dislocation_inhibitor"));
//        //Stone Type
//        event.getRegistry().register(new RainSensor(stoneProp).setRegistryName("rain_sensor"));
//        event.getRegistry().register(new Potentiometer(stoneProp).setRegistryName("potentiometer"));
//        //Hardened Machines
        event.getRegistry().register(new Grinder(hardenedMachine).setRegistryName("grinder"));
//        event.getRegistry().register(new DissEnchanter(hardenedMachine).setRegistryName("disenchanter"));
//        event.getRegistry().register(new CelestialManipulator(hardenedMachine).setRegistryName("celestial_manipulator"));
//        event.getRegistry().register(new PlacedItem(hardenedMachine).setRegistryName("placed_item"));
//        //Fusion Crafting
//        event.getRegistry().register(new FusionCraftingCore(hardenedMachine).setRegistryName("crafting_core"));
//        event.getRegistry().register(new CraftingInjector(hardenedMachine).setRegistryName("crafting_injector_basic"));
//        event.getRegistry().register(new CraftingInjector(hardenedMachine).setRegistryName("crafting_injector_wyvern"));
//        event.getRegistry().register(new CraftingInjector(hardenedMachine).setRegistryName("crafting_injector_awakened"));
//        event.getRegistry().register(new CraftingInjector(hardenedMachine).setRegistryName("crafting_injector_chaotic"));
//        //Energy Core
        event.getRegistry().register(new EnergyCore(hardenedMachine).setRegistryName("energy_core"));
        event.getRegistry().register(new EnergyCoreStabilizer(hardenedMachine).setRegistryName("energy_core_stabilizer"));
        event.getRegistry().register(new EnergyPylon(hardenedMachine).setRegistryName("energy_pylon"));
        event.getRegistry().register(new EnergyCoreStructureBlock(Properties.create(IRON, GRAY).hardnessAndResistance(5.0F, 12F).noDrops()).setRegistryName("energy_core_structure"));
//        //Reactor
//        event.getRegistry().register(new ReactorCore(hardenedMachine).setRegistryName("reactor_core"));
//        event.getRegistry().register(new ReactorComponent(hardenedMachine, false).setRegistryName("reactor_stabilizer"));
//        event.getRegistry().register(new ReactorComponent(hardenedMachine, true).setRegistryName("reactor_injector"));
//        //Ore
        event.getRegistry().register(new DraconiumOre(ore).setRegistryName("ore_draconium_overworld"));
        event.getRegistry().register(new DraconiumOre(ore).setRegistryName("ore_draconium_nether"));
        event.getRegistry().register(new DraconiumOre(ore).setRegistryName("ore_draconium_end"));
//        //Storage Blocks
        event.getRegistry().register(new DraconiumBlock(storageBlock).setRegistryName("block_draconium"));
        event.getRegistry().register(new DraconiumBlock(storageBlock).setRegistryName("block_draconium_awakened"));
//        //Special
//        event.getRegistry().register(new Portal(Properties.create(Material.GLASS).hardnessAndResistance(-1F)).setRegistryName("portal"));
//        event.getRegistry().register(new ChaosCrystal(Properties.create(Material.GLASS).hardnessAndResistance(100, 4000)).setRegistryName("chaos_crystal"));
//        event.getRegistry().register(new BlockBCore(Block.Properties.create(Material.ROCK, MaterialColor.BLACK).hardnessAndResistance(100.0F, 2400.0F)).setRegistryName("infused_obsidian"));

        //Energy Crystals
        Properties crystalB = Properties.create(GLASS, DyeColor.BLUE).hardnessAndResistance(3.0F, 8F);      //TODO may want to tweak these after testing
        Properties crystalW = Properties.create(GLASS, DyeColor.PURPLE).hardnessAndResistance(5.0F, 16F);
        Properties crystalD = Properties.create(GLASS, DyeColor.ORANGE).hardnessAndResistance(8.0F, 32F);
        Properties crystalC = Properties.create(GLASS, DyeColor.BLACK).hardnessAndResistance(16.0F, 64F);
        event.getRegistry().register(new EnergyCrystal(crystalB, DRACONIUM, CRYSTAL_IO).setRegistryName("crystal_io_basic"));
        event.getRegistry().register(new EnergyCrystal(crystalW, WYVERN,    CRYSTAL_IO).setRegistryName("crystal_io_wyvern"));
        event.getRegistry().register(new EnergyCrystal(crystalD, DRACONIC,  CRYSTAL_IO).setRegistryName("crystal_io_draconic"));
//        event.getRegistry().register(new EnergyCrystal(crystalC, CHAOTIC,   CRYSTAL_IO).setRegistryName("crystal_io_chaotic"));
        event.getRegistry().register(new EnergyCrystal(crystalB, DRACONIUM, RELAY).setRegistryName("crystal_relay_basic"));
        event.getRegistry().register(new EnergyCrystal(crystalW, WYVERN,    RELAY).setRegistryName("crystal_relay_wyvern"));
        event.getRegistry().register(new EnergyCrystal(crystalD, DRACONIC,  RELAY).setRegistryName("crystal_relay_draconic"));
//        event.getRegistry().register(new EnergyCrystal(crystalC, CHAOTIC,   RELAY).setRegistryName("crystal_relay_chaotic"));
        event.getRegistry().register(new EnergyCrystal(crystalB, DRACONIUM, WIRELESS).setRegistryName("crystal_wireless_basic"));
        event.getRegistry().register(new EnergyCrystal(crystalW, WYVERN,    WIRELESS).setRegistryName("crystal_wireless_wyvern"));
        event.getRegistry().register(new EnergyCrystal(crystalD, DRACONIC,  WIRELESS).setRegistryName("crystal_wireless_draconic"));
//        event.getRegistry().register(new EnergyCrystal(crystalC, CHAOTIC,   WIRELESS).setRegistryName("crystal_wireless_chaotic"));
    }























    //#################################################################
    // Items
    //#################################################################

    //@formatter:off
    //Components
    @ObjectHolder("dust_draconium")             public static Item                      dust_draconium;
    @ObjectHolder("dust_draconium_awakened")    public static Item                      dust_draconium_awakened;
    @ObjectHolder("ingot_draconium")            public static Item                      ingot_draconium;
    @ObjectHolder("ingot_draconium_awakened")   public static Item                      ingot_draconium_awakened;
    @ObjectHolder("nugget_draconium")           public static Item                      nugget_draconium;
    @ObjectHolder("nugget_draconium_awakened")  public static Item                      nugget_draconium_awakened;
    @ObjectHolder("core_draconium")             public static ItemCore                  core_draconium;
    @ObjectHolder("core_wyvern")                public static ItemCore                  core_wyvern;
    @ObjectHolder("core_awakened")              public static ItemCore                  core_awakened;
    @ObjectHolder("core_chaotic")               public static ItemCore                  core_chaotic;
    @ObjectHolder("energy_core_wyvern")         public static Item                      energy_core_wyvern;
    @ObjectHolder("energy_core_draconic")       public static Item                      energy_core_draconic;
    @ObjectHolder("dragon_heart")               public static Item                      dragon_heart;
    @ObjectHolder("chaos_shard")                public static Item                      chaos_shard;
    @ObjectHolder("chaos_frag_large")           public static Item                      chaos_frag_large;
    @ObjectHolder("chaos_frag_medium")          public static Item                      chaos_frag_medium;
    @ObjectHolder("chaos_frag_small")           public static Item                      chaos_frag_small;
    //Misc Tools
    @ObjectHolder("magnet")                     public static Magnet                    magnet;
    @ObjectHolder("magnet_advanced")            public static Magnet                    magnet_advanced;
    @ObjectHolder("dislocator")                 public static Dislocator                dislocator;
    @ObjectHolder("dislocator_advanced")        public static DislocatorAdvanced        dislocator_advanced;
    @ObjectHolder("dislocator_p2p")             public static DislocatorBound           dislocator_p2p;
    @ObjectHolder("dislocator_player")          public static DislocatorBound           dislocator_player;
    @ObjectHolder("crystal_binder")             public static CrystalBinder             crystal_binder;
    @ObjectHolder("info_tablet")                public static InfoTablet                info_tablet;
    @ObjectHolder("ender_energy_manipulator")   public static EnderEnergyManipulator    ender_energy_manipulator;
    @ObjectHolder("creative_exchanger")         public static CreativeExchanger         creative_exchanger;
    @ObjectHolder("mob_soul")                   public static MobSoul                   mob_soul;
    //Tools
    @ObjectHolder("capacitor_wyvern")           public static DraconiumCapacitor        capacitor_wyvern;
    @ObjectHolder("capacitor_draconic")         public static DraconiumCapacitor        capacitor_draconic;
    @ObjectHolder("capacitor_creative")         public static DraconiumCapacitor        capacitor_creative;
    @ObjectHolder("shovel_wyvern")              public static WyvernShovel              shovel_wyvern;
    @ObjectHolder("shovel_draconic")            public static Item                      shovel_draconic;
    @ObjectHolder("shovel_chaotic")             public static Item                      shovel_chaotic;
    @ObjectHolder("pickaxe_wyvern")             public static Item                      pickaxe_wyvern;
    @ObjectHolder("pickaxe_draconic")           public static Item                      pickaxe_draconic;
    @ObjectHolder("pickaxe_chaotic")            public static Item                      pickaxe_chaotic;
    @ObjectHolder("axe_wyvern")                 public static Item                      axe_wyvern;
    @ObjectHolder("axe_draconic")               public static Item                      axe_draconic;
    @ObjectHolder("axe_chaotic")                public static Item                      axe_chaotic;
    @ObjectHolder("bow_wyvern")                 public static Item                      bow_wyvern;
    @ObjectHolder("bow_draconic")               public static Item                      bow_draconic;
    @ObjectHolder("bow_chaotic")                public static Item                      bow_chaotic;
    @ObjectHolder("sword_wyvern")               public static Item                      sword_wyvern;
    @ObjectHolder("sword_draconic")             public static Item                      sword_draconic;
    @ObjectHolder("sword_chaotic")              public static Item                      sword_chaotic;
    @ObjectHolder("staff_draconic")             public static Item                      staff_draconic;
    @ObjectHolder("staff_chaotic")              public static Item                      staff_chaotic;
    //Armor
    @ObjectHolder("armor_wyvern")               public static Item                      armor_wyvern;
    @ObjectHolder("armor_draconic")             public static Item                      armor_draconic;
    @ObjectHolder("armor_chaotic")              public static Item                      armor_chaotic;


    @ObjectHolder("test_module_host")           public static Item                      test_module_host;
    //@formatter:on

    public static transient ArrayList<ResourceLocation> ITEM_REGISTRY_ORDER = new ArrayList<>();

    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> event) {
        Block[] entityDetectors = {entity_detector, entity_detector_advanced};
        Block[] craftInjectors = {crafting_injector_basic, crafting_injector_wyvern, crafting_injector_awakened, crafting_injector_chaotic};
        Supplier<Object[]> tabBlocks = () -> new Object[]{ore_draconium_end, block_draconium_awakened, infused_obsidian, draconium_chest, stabilized_spawner, grinder, disenchanter};
        CyclingItemGroup blockGroup = new CyclingItemGroup("draconic.blocks", 40, tabBlocks, ITEM_REGISTRY_ORDER).setOffset(20);
        Supplier<Object[]> tabItems = () -> new Object[]{core_wyvern, ingot_draconium_awakened, sword_chaotic, chaos_shard, energy_core_draconic, staff_draconic, crystal_binder, dust_draconium, axe_draconic};
        CyclingItemGroup itemGroup = new CyclingItemGroup("draconic.items", 40, tabItems, ITEM_REGISTRY_ORDER);

        //@formatter:off
        registerItem(event, new ItemBlockBCore(generator,                   new Item.Properties().group(blockGroup)).setRegistryName(Objects.requireNonNull(generator.getRegistryName())));
        registerItem(event, new ItemBlockBCore(grinder,                     new Item.Properties().group(blockGroup)).setRegistryName(Objects.requireNonNull(grinder.getRegistryName())));
//        registerItem(event, new ItemBlockBCore(disenchanter,                new Item.Properties().group(blockGroup)).setRegistryName(Objects.requireNonNull(disenchanter.getRegistryName())));
//        registerItem(event, new ItemBlockBCore(energy_infuser,              new Item.Properties().group(blockGroup)).setRegistryName(Objects.requireNonNull(energy_infuser.getRegistryName())));
//        registerItem(event, new ItemBlockBCore(dislocator_pedestal,         new Item.Properties().group(blockGroup)).setRegistryName(Objects.requireNonNull(dislocator_pedestal.getRegistryName())));
//        registerItem(event, new ItemBlockBCore(dislocator_receptacle,       new Item.Properties().group(blockGroup)).setRegistryName(Objects.requireNonNull(dislocator_receptacle.getRegistryName())));
        registerItem(event, new ItemBlockBCore(creative_op_capacitor,       new Item.Properties().group(blockGroup)).setRegistryName(Objects.requireNonNull(creative_op_capacitor.getRegistryName())));
//        registerItem(event, new ItemBlockBCore(entityDetectors[0],          new Item.Properties().group(blockGroup)).setRegistryName(Objects.requireNonNull(entityDetectors[0].getRegistryName())));
//        registerItem(event, new ItemBlockBCore(entityDetectors[1],          new Item.Properties().group(blockGroup)).setRegistryName(Objects.requireNonNull(entityDetectors[1].getRegistryName())));
        registerItem(event, new ItemBlockBCore(stabilized_spawner,          new Item.Properties().group(blockGroup)).setRegistryName(Objects.requireNonNull(stabilized_spawner.getRegistryName())));
//        registerItem(event, new ItemBlockBCore(potentiometer,               new Item.Properties().group(blockGroup)).setRegistryName(Objects.requireNonNull(potentiometer.getRegistryName())));
//        registerItem(event, new ItemBlockBCore(celestial_manipulator,       new Item.Properties().group(blockGroup)).setRegistryName(Objects.requireNonNull(celestial_manipulator.getRegistryName())));
//        registerItem(event, new ItemBlockBCore(draconium_chest,             new Item.Properties().group(blockGroup)).setRegistryName(Objects.requireNonNull(draconium_chest.getRegistryName())));
//        registerItem(event, new ItemBlockBCore(particle_generator,          new Item.Properties().group(blockGroup)).setRegistryName(Objects.requireNonNull(particle_generator.getRegistryName())));
//        registerItem(event, new ItemBlockBCore(placed_item,                 new Item.Properties().group(blockGroup)).setRegistryName(Objects.requireNonNull(placed_item.getRegistryName())));
//        registerItem(event, new ItemBlockBCore(portal,                      new Item.Properties().group(blockGroup)).setRegistryName(Objects.requireNonNull(portal.getRegistryName())));
//        registerItem(event, new ItemBlockBCore(chaos_crystal,               new Item.Properties().group(blockGroup)).setRegistryName(Objects.requireNonNull(chaos_crystal.getRegistryName())));
//        registerItem(event, new ItemBlockBCore(craftInjectors[0],           new Item.Properties().group(blockGroup)).setRegistryName(Objects.requireNonNull(craftInjectors[0].getRegistryName())));
//        registerItem(event, new ItemBlockBCore(craftInjectors[1],           new Item.Properties().group(blockGroup)).setRegistryName(Objects.requireNonNull(craftInjectors[1].getRegistryName())));
//        registerItem(event, new ItemBlockBCore(craftInjectors[2],           new Item.Properties().group(blockGroup)).setRegistryName(Objects.requireNonNull(craftInjectors[2].getRegistryName())));
//        registerItem(event, new ItemBlockBCore(craftInjectors[3],           new Item.Properties().group(blockGroup)).setRegistryName(Objects.requireNonNull(craftInjectors[3].getRegistryName())));
//        registerItem(event, new ItemBlockBCore(crafting_core,               new Item.Properties().group(blockGroup)).setRegistryName(Objects.requireNonNull(crafting_core.getRegistryName())));
        registerItem(event, new ItemBlockBCore(energy_core,                 new Item.Properties().group(blockGroup)).setRegistryName(Objects.requireNonNull(energy_core.getRegistryName())));
        registerItem(event, new ItemBlockBCore(energy_core_stabilizer,      new Item.Properties().group(blockGroup)).setRegistryName(Objects.requireNonNull(energy_core_stabilizer.getRegistryName())));
        registerItem(event, new ItemBlockBCore(energy_pylon,                new Item.Properties().group(blockGroup)).setRegistryName(Objects.requireNonNull(energy_pylon.getRegistryName())));
        registerItem(event, new ItemBlockBCore(energy_core_structure,       new Item.Properties().group(blockGroup)).setRegistryName(Objects.requireNonNull(energy_core_structure.getRegistryName())));
//        registerItem(event, new ItemBlockBCore(reactor_core,                new Item.Properties().group(blockGroup)).setRegistryName(Objects.requireNonNull(reactor_core.getRegistryName())));
//        registerItem(event, new ItemBlockBCore(reactor_stabilizer,          new Item.Properties().group(blockGroup)).setRegistryName(Objects.requireNonNull(reactor_stabilizer.getRegistryName())));
//        registerItem(event, new ItemBlockBCore(reactor_injector,            new Item.Properties().group(blockGroup)).setRegistryName(Objects.requireNonNull(reactor_injector.getRegistryName())));
        registerItem(event, new ItemBlockBCore(block_draconium,             new Item.Properties().group(blockGroup)).setRegistryName(Objects.requireNonNull(block_draconium.getRegistryName())));
        registerItem(event, new ItemBlockBCore(block_draconium_awakened,    new Item.Properties().group(blockGroup)).setRegistryName(Objects.requireNonNull(block_draconium_awakened.getRegistryName())));
        registerItem(event, new ItemBlockBCore(ore_draconium_end,           new Item.Properties().group(blockGroup)).setRegistryName(Objects.requireNonNull(ore_draconium_end.getRegistryName())));
        registerItem(event, new ItemBlockBCore(ore_draconium_nether,        new Item.Properties().group(blockGroup)).setRegistryName(Objects.requireNonNull(ore_draconium_nether.getRegistryName())));
        registerItem(event, new ItemBlockBCore(ore_draconium_overworld,     new Item.Properties().group(blockGroup)).setRegistryName(Objects.requireNonNull(ore_draconium_overworld.getRegistryName())));
        registerItem(event, new ItemBlockBCore(crystal_io_basic,            new Item.Properties().group(blockGroup)).setRegistryName(Objects.requireNonNull(crystal_io_basic.getRegistryName())));
        registerItem(event, new ItemBlockBCore(crystal_io_wyvern,           new Item.Properties().group(blockGroup)).setRegistryName(Objects.requireNonNull(crystal_io_wyvern.getRegistryName())));
        registerItem(event, new ItemBlockBCore(crystal_io_draconic,         new Item.Properties().group(blockGroup)).setRegistryName(Objects.requireNonNull(crystal_io_draconic.getRegistryName())));
//        registerItem(event, new ItemBlockBCore(crystal_io_chaotic,          new Item.Properties().group(blockGroup)).setRegistryName(Objects.requireNonNull(crystal_io_chaotic.getRegistryName())));
        registerItem(event, new ItemBlockBCore(crystal_relay_basic,         new Item.Properties().group(blockGroup)).setRegistryName(Objects.requireNonNull(crystal_relay_basic.getRegistryName())));
        registerItem(event, new ItemBlockBCore(crystal_relay_wyvern,        new Item.Properties().group(blockGroup)).setRegistryName(Objects.requireNonNull(crystal_relay_wyvern.getRegistryName())));
        registerItem(event, new ItemBlockBCore(crystal_relay_draconic,      new Item.Properties().group(blockGroup)).setRegistryName(Objects.requireNonNull(crystal_relay_draconic.getRegistryName())));
//        registerItem(event, new ItemBlockBCore(crystal_relay_chaotic,       new Item.Properties().group(blockGroup)).setRegistryName(Objects.requireNonNull(crystal_relay_chaotic.getRegistryName())));
        registerItem(event, new ItemBlockBCore(crystal_wireless_basic,      new Item.Properties().group(blockGroup)).setRegistryName(Objects.requireNonNull(crystal_wireless_basic.getRegistryName())));
        registerItem(event, new ItemBlockBCore(crystal_wireless_wyvern,     new Item.Properties().group(blockGroup)).setRegistryName(Objects.requireNonNull(crystal_wireless_wyvern.getRegistryName())));
        registerItem(event, new ItemBlockBCore(crystal_wireless_draconic,   new Item.Properties().group(blockGroup)).setRegistryName(Objects.requireNonNull(crystal_wireless_draconic.getRegistryName())));
//        registerItem(event, new ItemBlockBCore(crystal_wireless_chaotic,    new Item.Properties().group(blockGroup)).setRegistryName(Objects.requireNonNull(crystal_wireless_chaotic.getRegistryName())));
        //Components
        registerItem(event, new Item(new Item.Properties().group(itemGroup)).setRegistryName("dust_draconium"));
        registerItem(event, new Item(new Item.Properties().group(itemGroup)).setRegistryName("dust_draconium_awakened"));
        registerItem(event, new Item(new Item.Properties().group(itemGroup)).setRegistryName("ingot_draconium"));
        registerItem(event, new Item(new Item.Properties().group(itemGroup)).setRegistryName("ingot_draconium_awakened"));
        registerItem(event, new Item(new Item.Properties().group(itemGroup)).setRegistryName("nugget_draconium"));
        registerItem(event, new Item(new Item.Properties().group(itemGroup)).setRegistryName("nugget_draconium_awakened"));
        registerItem(event, new ItemCore(new Item.Properties().group(itemGroup)).setRegistryName("core_draconium"));
        registerItem(event, new ItemCore(new Item.Properties().group(itemGroup)).setRegistryName("core_wyvern"));
        registerItem(event, new ItemCore(new Item.Properties().group(itemGroup)).setRegistryName("core_awakened"));
        registerItem(event, new ItemCore(new Item.Properties().group(itemGroup)).setRegistryName("core_chaotic"));
        registerItem(event, new Item(new Item.Properties().group(itemGroup)).setRegistryName("energy_core_wyvern"));
        registerItem(event, new Item(new Item.Properties().group(itemGroup)).setRegistryName("energy_core_draconic"));
        registerItem(event, new Item(new Item.Properties().group(itemGroup)).setRegistryName("dragon_heart"));
        registerItem(event, new Item(new Item.Properties().group(itemGroup)).setRegistryName("chaos_shard"));
        registerItem(event, new Item(new Item.Properties().group(itemGroup)).setRegistryName("chaos_frag_large"));
        registerItem(event, new Item(new Item.Properties().group(itemGroup)).setRegistryName("chaos_frag_medium"));
        registerItem(event, new Item(new Item.Properties().group(itemGroup)).setRegistryName("chaos_frag_small"));
//        //Items
        registerItem(event, new Magnet(new Item.Properties().maxStackSize(1).group(itemGroup), 8).setRegistryName("magnet"));
        registerItem(event, new Magnet(new Item.Properties().maxStackSize(1).group(itemGroup), 32).setRegistryName("magnet_advanced"));
        registerItem(event, new Dislocator(new Item.Properties().maxStackSize(1).maxDamage(31).group(itemGroup)).setRegistryName("dislocator"));
        registerItem(event, new DislocatorAdvanced(new Item.Properties().maxStackSize(1).group(itemGroup)).setRegistryName("dislocator_advanced"));
        registerItem(event, new DislocatorBound(new Item.Properties().maxStackSize(1).group(itemGroup)).setRegistryName("dislocator_p2p"));
        registerItem(event, new DislocatorBound(new Item.Properties().maxStackSize(1).group(itemGroup)).setRegistryName("dislocator_player"));
        registerItem(event, new CrystalBinder(new Item.Properties().maxStackSize(1).group(itemGroup)).setRegistryName("crystal_binder"));
        registerItem(event, new InfoTablet(new Item.Properties().maxStackSize(1).group(itemGroup)).setRegistryName("info_tablet"));
//        registerItem(event, new EnderEnergyManipulator(new Item.Properties().group(itemGroup)).setRegistryName("ender_energy_manipulator"));
//        registerItem(event, new CreativeExchanger(new Item.Properties().group(itemGroup)).setRegistryName("creative_exchanger"));
        registerItem(event, new MobSoul(new Item.Properties().group(itemGroup)).setRegistryName("mob_soul"));

        registerItem(event, new TestModuleHost(new Item.Properties().group(itemGroup)).setRegistryName("test_module_host"));
//        //Tools
//        registerItem(event, new DraconiumCapacitor(new Item.Properties().group(itemGroup)).setRegistryName("capacitor_wyvern"));
//        registerItem(event, new DraconiumCapacitor(new Item.Properties().group(itemGroup)).setRegistryName("capacitor_draconic"));
//        registerItem(event, new DraconiumCapacitor(new Item.Properties().group(itemGroup)).setRegistryName("capacitor_creative"));
//        registerItem(event, new WyvernShovel(new Item.Properties().group(itemGroup)).setRegistryName("shovel_wyvern"));
//        registerItem(event, new DraconicShovel(new Item.Properties().group(itemGroup)).setRegistryName("shovel_draconic"));
//        registerItem(event, new DraconicShovel(new Item.Properties().group(itemGroup)).setRegistryName("shovel_chaotic"));
//        registerItem(event, new WyvernPick(new Item.Properties().group(itemGroup)).setRegistryName("pickaxe_wyvern"));
//        registerItem(event, new DraconicPick(new Item.Properties().group(itemGroup)).setRegistryName("pickaxe_draconic"));
//        registerItem(event, new DraconicPick(new Item.Properties().group(itemGroup)).setRegistryName("pickaxe_chaotic"));
//        registerItem(event, new WyvernAxe(new Item.Properties().group(itemGroup)).setRegistryName("axe_wyvern"));
//        registerItem(event, new DraconicAxe(new Item.Properties().group(itemGroup)).setRegistryName("axe_draconic"));
//        registerItem(event, new DraconicAxe(new Item.Properties().group(itemGroup)).setRegistryName("axe_chaotic"));
//        registerItem(event, new WyvernBow(new Item.Properties().group(itemGroup)).setRegistryName("bow_wyvern"));
//        registerItem(event, new DraconicBow(new Item.Properties().group(itemGroup)).setRegistryName("bow_draconic"));
//        registerItem(event, new DraconicBow(new Item.Properties().group(itemGroup)).setRegistryName("bow_chaotic"));
//        registerItem(event, new WyvernSword(new Item.Properties().group(itemGroup)).setRegistryName("sword_wyvern"));
//        registerItem(event, new DraconicSword(new Item.Properties().group(itemGroup)).setRegistryName("sword_draconic"));
//        registerItem(event, new DraconicSword(new Item.Properties().group(itemGroup)).setRegistryName("sword_chaotic"));
//        registerItem(event, new DraconicStaffOfPower(new Item.Properties().group(itemGroup)).setRegistryName("staff_draconic"));
//        registerItem(event, new DraconicStaffOfPower(new Item.Properties().group(itemGroup)).setRegistryName("staff_chaotic"));
//        //Armor
//        registerItem(event, new WyvernArmor(new Item.Properties().group(itemGroup)).setRegistryName("armor_wyvern"));
//        registerItem(event, new DraconicArmor(new Item.Properties().group(itemGroup)).setRegistryName("armor_draconic"));
//        registerItem(event, new DraconicArmor(new Item.Properties().group(itemGroup)).setRegistryName("armor_chaotic"));
        //@formatter:on
    }

    private static void registerItem(RegistryEvent.Register<Item> event, Item item) {
        event.getRegistry().register(item);
        ITEM_REGISTRY_ORDER.add(item.getRegistryName());
    }


//    //    @ModFeature(name = "generator", tileEntity = TileGenerator.class, itemBlock = ItemBlockBCore.class, cTab = 1)
////    public static Generator generator = null;//new Generator();
////        @ModFeature(name = "grinder", tileEntity = TileGrinder.class, itemBlock = ItemBlockBCore.class, cTab = 1)
////    public static Grinder grinder = null;//new Grinder();
//    //    @ModFeature(name = "particle_generator", variantMap = {"0:type=normal", "1:type=inverted", "2:type=stabilizer", "3:type=stabilizer"}, cTab = 1, itemBlock = ItemBlockBCore.class)
//    public static ParticleGenerator particleGenerator = null;//new ParticleGenerator();
//    //    @ModFeature(name = "energy_infuser", tileEntity = TileEnergyInfuser.class, itemBlock = ItemBlockBCore.class, cTab = 1)
//    public static EnergyInfuser energyInfuser = null;//new EnergyInfuser();
//
//    /* ------------------ Blocks ------------------ */
//
//    //region Simple Blocks
////TODO Split into multiple ore blocks
////    @ModFeature(name = "draconium_ore", variantMap = {"0:type=normal", "1:type=nether", "2:type=end"}, itemBlock = ItemBlockBCore.class)
//    public static DraconiumOre draconiumOre = null;//(DraconiumOre) new DraconiumOre().setHardness(10f).setResistance(20.0f);
//
//    //    @ModFeature(name = "draconium_block", variantMap = {"0:charged=false", "1:charged=true"}, itemBlock = ItemDraconiumBlock.class)
//    public static DraconiumBlock draconiumBlock = null;//(DraconiumBlock) new DraconiumBlock().setHardness(10f).setResistance(20.0f);
//
//    //    @ModFeature(name = "draconic_block")
//    public static BlockBCore draconicBlock = null;//new BlockBCore(Material.IRON) {
////        @Override//TODO add a way to override this in BlockBCore
////        public float getEnchantPowerBonus(World world, BlockPos pos) {
////            return 12f;
////        }
////    }.setHardness(20F).setResistance(1000F)).setHarvestTool("pickaxe", 4);
//
//    //    @ModFeature(name = "infused_obsidian")
//    public static BlockBCore infusedObsidian = null;//((BlockBCore) new BlockBCore(Material.IRON).setHardness(100F).setResistance(4000F)).setHarvestTool("pickaxe", 4);
//
//    //    @ModFeature(name = "portal", tileEntity = TilePortal.class)
//    public static Portal portal = null;//new Portal();
//
//    //    @ModFeature(name = "item_dislocation_inhibitor")
//    public static ItemDislocationInhibitor itemDislocationInhibitor = null;//new ItemDislocationInhibitor();
//
//    //endregion
//
//    //region Machines
//
//
//    //    @ModFeature(name = "dislocator_receptacle", tileEntity = TileDislocatorReceptacle.class, itemBlock = ItemBlockBCore.class, cTab = 1)
//    public static DislocatorReceptacle dislocatorReceptacle = null;//new DislocatorReceptacle();
//    //    @ModFeature(name = "dislocator_pedestal", tileEntity = TileDislocatorPedestal.class, cTab = 1)
//    public static DislocatorPedestal dislocatorPedestal = null;//new DislocatorPedestal();
//    //    @ModFeature(name = "rain_sensor", tileEntity = TileRainSensor.class, cTab = 1)
//    public static RainSensor rainSensor = null;//new RainSensor();
//    //    @ModFeature(name = "diss_enchanter", tileEntity = TileDissEnchanter.class, itemBlock = ItemBlockBCore.class, cTab = 1)
//    public static DissEnchanter dissEnchanter = null;//new DissEnchanter();
//    //    @ModFeature(name = "potentiometer", tileEntity = TilePotentiometer.class, cTab = 1)
//    public static Potentiometer potentiometer = null;//new Potentiometer();
//    //    @ModFeature(name = "entity_detector", tileEntity = TileEntityDetector.class, variantMap = {"0:advanced=false", "1:advanced=true"}, itemBlock = ItemBlockBCore.class, cTab = 1)
//    public static EntityDetector entityDetector = null;//new EntityDetector();
//
//    //endregion
//
//    //region Advanced Machines
//
//    //    @ModFeature(name = "energy_storage_core", tileEntity = TileEnergyStorageCore.class, cTab = 1)
//    public static EnergyStorageCore energyStorageCore = null;//new EnergyStorageCore();
//    //    @ModFeature(name = "energy_pylon", tileEntity = TileEnergyPylon.class, cTab = 1)
//    public static EnergyPylon energyPylon = null;//new EnergyPylon();
//    //    @ModFeature(name = "invis_e_core_block", tileEntity = TileInvisECoreBlock.class, cTab = -1)
//    public static InvisECoreBlock invisECoreBlock = null;//new InvisECoreBlock();
//    //    @ModFeature(name = "fusion_crafting_core", tileEntity = TileFusionCraftingCore.class, itemBlock = ItemBlockBCore.class, cTab = 1)
//    public static FusionCraftingCore fusionCraftingCore = null;//new FusionCraftingCore();
//    //    @ModFeature(name = "crafting_injector", variantMap = {"0:facing=up,tier=basic", "1:facing=up,tier=wyvern", "2:facing=up,tier=draconic", "3:facing=up,tier=chaotic"}, tileEntity = TileCraftingInjector.class, itemBlock = ItemBlockBCore.class, cTab = 1)
//    public static CraftingInjector craftingInjector = null;//new CraftingInjector();
//    //    @ModFeature(name = "celestial_manipulator", tileEntity = TileCelestialManipulator.class, cTab = 1)//, itemBlock = ItemBlockBCore.class, cTab = 1)
//    public static CelestialManipulator celestialManipulator = null;//new CelestialManipulator();
//    //    @ModFeature(name = "energy_crystal", itemBlock = ItemBlockBCore.class)
//    public static EnergyCrystal energyCrystal = null;//new EnergyCrystal();
//    //    @ModFeature(name = "flow_gate", variantMap = {"0:facing=north,type=flux", "8:facing=north,type=fluid"}, itemBlock = ItemBlockBCore.class, cTab = 1)
//    public static FlowGate flowGate = null;//new FlowGate();
//    //    @ModFeature(name = "reactor_core", tileEntity = TileReactorCore.class, itemBlock = ItemBlockBCore.class, cTab = 1)
//    public static ReactorCore reactorCore = null;//new ReactorCore();
//    //    @ModFeature(name = "reactor_part", cTab = 1)
//    public static ReactorPart reactorPart = null;//new ReactorPart();
//    //    @ModFeature(name = "reactor_component", itemBlock = ItemBlockBCore.class, cTab = 1)
//    public static ReactorComponent reactorComponent = null;//new ReactorComponent();
//    //    @ModFeature(name = "draconic_spawner", tileEntity = TileStabilizedSpawner.class, itemBlock = ItemBlockBCore.class)
//    public static StabilizedSpawner stabilizedSpawner = null;//new StabilizedSpawner();
//    //    @ModFeature(name = "draconium_chest", tileEntity = TileDraconiumChest.class, itemBlock = ItemBlockBCore.class)
//    public static DraconiumChest draconiumChest = null;//new DraconiumChest();
//    //endregion
//
//    //region Exotic Blocks
//
//    //    @ModFeature(name = "chaos_crystal", tileEntity = TileChaosCrystal.class, cTab = -1)
//    public static ChaosCrystal chaosCrystal = null;//new ChaosCrystal();
//    //    @ModFeature(name = "chaos_shard_atmos", cTab = -1)
//    public static ChaosShardAtmos chaosShardAtmos = null;//new ChaosShardAtmos();
//    //    @ModFeature(name = "creative_rf_source", tileEntity = TileCreativeRFCapacitor.class, cTab = 1)
//    public static CreativeRFSource creativeRFSource = null;//new CreativeRFSource();
//    //    @ModFeature(name = "placed_item", tileEntity = TilePlacedItem.class, cTab = -1)
//    public static PlacedItem placedItem = null;//new PlacedItem();
//    //endregion
//
//    /* ------------------ Items ------------------ */
//
//    //region Crafting Components / Base items
//    @ModFeature(name = "draconium_dust", stateOverride = "simple_components#type=draconiumDust")
//    public static Item draconiumDust = null;//new Item();
//
//    //    @ModFeature(name = "draconium_ingot", stateOverride = "simple_components#type=draconiumIngot")
//    public static Item draconiumIngot = null;//new Item();
//
//    //    @ModFeature(name = "draconic_ingot", stateOverride = "simple_components#type=draconicIngot")
//    public static Item draconicIngot = null;//new Item();
//
//    //    @ModFeature(name = "draconic_core", stateOverride = "simple_components#type=draconicCore")
//    public static ItemCore draconicCore = null;//new ItemCore();
//
//    //    @ModFeature(name = "wyvern_core", stateOverride = "simple_components#type=wyvernCore")
//    public static ItemCore wyvernCore = null;//new ItemCore();
//
//    //    @ModFeature(name = "awakened_core", stateOverride = "simple_components#type=awakenedCore")
//    public static ItemCore awakenedCore = null;//new ItemCore();
//
//    //    @ModFeature(name = "chaotic_core", stateOverride = "simple_components#type=chaoticCore")
//    public static ItemCore chaoticCore = null;//new ItemCore();
//
//    //    @ModFeature(name = "wyvern_energy_core", stateOverride = "simple_components#type=wyvernECore")
//    public static Item wyvernEnergyCore = null;//new Item();
//
//    //    @ModFeature(name = "draconic_energy_core", stateOverride = "simple_components#type=draconicECore")
//    public static Item draconicEnergyCore = null;//new Item();
//
//    //    @ModFeature(name = "dragon_heart", stateOverride = "simple_components#type=dragonHeart")
//    public static ItemPersistent dragonHeart = null;//new ItemPersistent();
//
//    //    @ModFeature(name = "debugger", stateOverride = "simple_components#type=draconicIngot")
//    public static Item debugger = null;//new Debugger();
//
//    //    @ModFeature(name = "nugget", variantMap = {"0:type=draconium", "1:type=awakened"})
//    public static ItemSimpleSubs nugget = null;//new ItemSimpleSubs(new String[]{"0:draconium", "1:awakened"});
//
//    //    @ModFeature(name = "chaos_shard", variantMap = {"0:type=shard", "1:type=fragLarge", "2:type=fragMedium", "3:type=fragSmall"})
//    public static ItemSimpleSubs chaosShard = null;//new ItemSimpleSubs(new String[]{"0:shard", "1:fragLarge", "2:fragMedium", "3:fragSmall"});
//
//
//    //endregion
//
//    //region Tools
//
//    //    @ModFeature(name = "draconium_capacitor", variantMap = {"0:type=wyvern", "1:type=draconic", "2:type=creative"}, cTab = 1)
//    public static DraconiumCapacitor draconiumCapacitor = null;//new DraconiumCapacitor();
//    public static ItemStack wyvernCapacitor = null;//new ItemStack(draconiumCapacitor, 1, 0);
//    public static ItemStack draconicCapacitor = null;//new ItemStack(draconiumCapacitor, 1, 1);
//    public static ItemStack creativeCapacitor = null;//new ItemStack(draconiumCapacitor, 1, 2);
//
//    //    @ModFeature(name = "wyvern_axe", cTab = 1)
//    public static WyvernAxe wyvernAxe = null;//new WyvernAxe();
//
//    //    @ModFeature(name = "wyvern_bow", cTab = 1)
//    public static WyvernBow wyvernBow = null;//new WyvernBow();
//
//    //    @ModFeature(name = "wyvern_pick", cTab = 1)
//    public static WyvernPick wyvernPick = null;//new WyvernPick();
//
//    //    @ModFeature(name = "wyvern_shovel", cTab = 1)
//    public static WyvernShovel wyvernShovel = null;//new WyvernShovel();
//
//    //    @ModFeature(name = "wyvern_sword", cTab = 1)
//    public static WyvernSword wyvernSword = null;//new WyvernSword();
//
//    //    @ModFeature(name = "draconic_axe", cTab = 1)
//    public static DraconicAxe draconicAxe = null;//new DraconicAxe();
//
//    //    @ModFeature(name = "draconic_bow", cTab = 1)
//    public static DraconicBow draconicBow = null;//new DraconicBow();
//
//    //    @ModFeature(name = "draconic_hoe", cTab = 1)
//    public static DraconicHoe draconicHoe = null;//new DraconicHoe();
//
//    //    @ModFeature(name = "draconic_pick", cTab = 1)
//    public static DraconicPick draconicPick = null;//new DraconicPick();
//
//    //    @ModFeature(name = "draconic_shovel", cTab = 1)
//    public static DraconicShovel draconicShovel = null;//new DraconicShovel();
//
//    //    @ModFeature(name = "draconic_staff_of_power", cTab = 1)
//    public static DraconicStaffOfPower draconicStaffOfPower = null;//new DraconicStaffOfPower();
//
//    //    @ModFeature(name = "draconic_sword", cTab = 1)
//    public static DraconicSword draconicSword = null;//new DraconicSword();
//
//    //    @ModFeature(name = "tool_upgrade", cTab = 1)
//    public static ToolUpgrade toolUpgrade = null;//new ToolUpgrade();
//
//    //    @ModFeature(name = "dislocator", cTab = 1)
//    public static Dislocator dislocator = null;//new Dislocator();
//
//    //    @ModFeature(name = "dislocator_advanced", cTab = 1)
//    public static DislocatorAdvanced dislocatorAdvanced = null;//new DislocatorAdvanced();
//
//    //    @ModFeature(name = "dislocator_bound", cTab = 1)
//    public static DislocatorBound dislocatorBound = null;//new DislocatorBound();
//
//    //    @ModFeature(name = "magnet", cTab = 1, stateOverride = "misc", variantMap = {"0:type=magnet_basic", "1:type=magnet_advanced"})
//    public static Magnet magnet = null;//new Magnet();
//
//    //    @ModFeature(name = "crystal_binder", stateOverride = "misc#type=crystal_binder")
//    public static CrystalBinder crystalBinder = null;//new CrystalBinder();
//
//    //endregion
//
//    //region Armor
//    @ModFeature(name = "wyvern_helm", cTab = 1, stateOverride = "armor#type=wyvernHelm")
//    public static WyvernArmor wyvernHelm = null;//new WyvernArmor(0, EquipmentSlotType.HEAD);
//
//    //    @ModFeature(name = "wyvern_chest", cTab = 1, stateOverride = "armor#type=wyvernChest")
//    public static WyvernArmor wyvernChest = null;//new WyvernArmor(1, EquipmentSlotType.CHEST);
//
//    //    @ModFeature(name = "wyvern_legs", cTab = 1, stateOverride = "armor#type=wyvernLegs")
//    public static WyvernArmor wyvernLegs = null;//new WyvernArmor(2, EquipmentSlotType.LEGS);
//
//    //    @ModFeature(name = "wyvern_boots", cTab = 1, stateOverride = "armor#type=wyvernBoots")
//    public static WyvernArmor wyvernBoots = null;//new WyvernArmor(3, EquipmentSlotType.FEET);
//
//    //    @ModFeature(name = "draconic_helm", cTab = 1, stateOverride = "armor#type=draconicHelm")
//    public static DraconicArmor draconicHelm = null;//new DraconicArmor(0, EquipmentSlotType.HEAD);
//
//    //    @ModFeature(name = "draconic_chest", cTab = 1, stateOverride = "armor#type=draconicChest")
//    public static DraconicArmor draconicChest = null;//new DraconicArmor(1, EquipmentSlotType.CHEST);
//
//    //    @ModFeature(name = "draconic_legs", cTab = 1, stateOverride = "armor#type=draconicLegs")
//    public static DraconicArmor draconicLegs = null;//new DraconicArmor(2, EquipmentSlotType.LEGS);
//
//    //    @ModFeature(name = "draconic_boots", cTab = 1, stateOverride = "armor#type=draconicBoots")
//    public static DraconicArmor draconicBoots = null;//new DraconicArmor(3, EquipmentSlotType.FEET);
//
//    //endregion
//
//    //region Misc
//
//    //    @ModFeature(name = "info_tablet", stateOverride = "simple_components#type=info_tablet", cTab = 1)
//    public static InfoTablet infoTablet = null;//new InfoTablet();
//
//    //    @ModFeature(name = "mob_soul")
//    public static MobSoul mobSoul = null;//new MobSoul();
//
//
//    //endregion
//
//    //region Exotic Items
//
//    //    @ModFeature(name = "creative_exchanger", stateOverride = "misc#type=creative_exchanger", cTab = 1)
//    public static CreativeExchanger creativeExchanger = null;//new CreativeExchanger();
//
//    //    @ModFeature(name = "ender_energy_manipulator", cTab = 1)
//    public static EnderEnergyManipulator enderEnergyManipulator = null;//new EnderEnergyManipulator();
//
//    //endregion
}