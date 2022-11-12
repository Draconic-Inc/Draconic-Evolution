package com.brandon3055.draconicevolution.client;

import codechicken.lib.model.ModelRegistryHelper;
import codechicken.lib.texture.SpriteRegistryHelper;
import codechicken.lib.util.ResourceUtils;
import com.brandon3055.brandonscore.api.TechLevel;
import com.brandon3055.brandonscore.api.hud.AbstractHudElement;
import com.brandon3055.draconicevolution.CommonProxy;
import com.brandon3055.draconicevolution.DEConfig;
import com.brandon3055.draconicevolution.api.energy.IENetEffectTile;
import com.brandon3055.draconicevolution.blocks.energynet.EnergyCrystal;
import com.brandon3055.draconicevolution.blocks.energynet.rendering.ENetFXHandler;
import com.brandon3055.draconicevolution.blocks.reactor.ReactorEffectHandler;
import com.brandon3055.draconicevolution.blocks.reactor.tileentity.TileReactorCore;
import com.brandon3055.draconicevolution.blocks.tileentity.TileFusionCraftingCore;
import com.brandon3055.draconicevolution.blocks.tileentity.TileGenerator;
import com.brandon3055.draconicevolution.client.gui.*;
import com.brandon3055.draconicevolution.client.gui.modular.GuiModularItem;
import com.brandon3055.draconicevolution.client.gui.modular.itemconfig.GuiConfigurableItem;
import com.brandon3055.draconicevolution.client.handler.ClientEventHandler;
import com.brandon3055.draconicevolution.client.handler.StaffRenderEventHandler;
import com.brandon3055.draconicevolution.client.keybinding.KeyBindings;
import com.brandon3055.draconicevolution.client.keybinding.KeyInputHandler;
import com.brandon3055.draconicevolution.client.model.ModularArmorLayer;
import com.brandon3055.draconicevolution.client.render.entity.DraconicGuardianRenderer;
import com.brandon3055.draconicevolution.client.render.entity.GuardianCrystalRenderer;
import com.brandon3055.draconicevolution.client.render.entity.GuardianProjectileRenderer;
import com.brandon3055.draconicevolution.client.render.entity.GuardianWitherRenderer;
import com.brandon3055.draconicevolution.client.render.entity.projectile.DraconicArrowRenderer;
import com.brandon3055.draconicevolution.client.render.hud.ShieldHudElement;
import com.brandon3055.draconicevolution.client.render.item.*;
import com.brandon3055.draconicevolution.client.render.tile.*;
import com.brandon3055.draconicevolution.client.render.tile.fxhandlers.FusionTileFXHandler;
import com.brandon3055.draconicevolution.client.render.tile.fxhandlers.ITileFXHandler;
import com.brandon3055.draconicevolution.client.sound.GeneratorSoundHandler;
import com.brandon3055.draconicevolution.init.DEContent;
import com.brandon3055.draconicevolution.items.equipment.IModularArmor;
import com.brandon3055.draconicevolution.lib.ISidedTileHandler;
import net.covers1624.quack.util.SneakyUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.layers.ElytraLayer;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.ParticleFactoryRegisterEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLDedicatedServerSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.util.thread.EffectiveSide;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;

public class ClientProxy extends CommonProxy {

    public static SpriteRegistryHelper spriteHelper = new SpriteRegistryHelper();
    public static ModelRegistryHelper modelHelper = new ModelRegistryHelper();
    public static ModuleSpriteUploader moduleSpriteUploader;
    public static ShieldHudElement hudElement = null;
//    public static LayerContributorPerkRenderer layerWings;


    @Override
    public void construct() {
        super.construct();
        IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();

        modBus.addListener((ColorHandlerEvent.Block event) -> moduleSpriteUploader = new ModuleSpriteUploader());
        modBus.addListener(DEGuiSprites::initialize);
        modBus.addListener(DEMiscSprites::initialize);
        modBus.addGenericListener(AbstractHudElement.class, this::registerHudElements);
        modBus.addListener(this::registerEntityRenderers);
        modBus.addListener(this::onAddRenderLayers);

        StaffRenderEventHandler.init();
        CustomBossInfoHandler.init();
        DEShaders.init();
    }

    @Override
    public void commonSetup(FMLCommonSetupEvent event) {
        super.commonSetup(event);
    }

    @Override
    public void clientSetup(FMLClientSetupEvent event) {
        super.clientSetup(event);

        registerGuiFactories();
        registerItemRenderers();
        setupRenderLayers();

        MinecraftForge.EVENT_BUS.register(new KeyInputHandler());
        MinecraftForge.EVENT_BUS.register(new ClientEventHandler());
        KeyBindings.init();
    }

    private void registerGuiFactories() {
        MenuScreens.register(DEContent.container_generator, GuiGenerator::new);
        MenuScreens.register(DEContent.container_grinder, GuiGrinder::new);
        MenuScreens.register(DEContent.container_draconium_chest, GuiDraconiumChest::new);
        MenuScreens.register(DEContent.container_energy_core, GuiEnergyCore::new);
        MenuScreens.register(DEContent.container_modular_item, GuiModularItem::new);
        MenuScreens.register(DEContent.container_configurable_item, GuiConfigurableItem::new);
        MenuScreens.register(DEContent.container_reactor, GuiReactor::new);

        MenuScreens.register(DEContent.container_celestial_manipulator, GuiCelestialManipulator::new);
        MenuScreens.register(DEContent.container_disenchanter, GuiDisenchanter::new);
//        MenuScreens.register(DEContent.container_energy_crystal, ContainerEnergyCrystal::new);
        MenuScreens.register(DEContent.container_fusion_crafting_core, GuiFusionCraftingCore::new);
//        MenuScreens.register(DEContent.container_reactor, ContainerReactor::new);
        MenuScreens.register(DEContent.container_flow_gate, GuiFlowGate::new);
        MenuScreens.register(DEContent.container_entity_detector, GuiEntityDetector::new);
        MenuScreens.register(DEContent.container_energy_transfuser, GuiEnergyTransfuser::new);
    }

    @SuppressWarnings("ConstantConditions")
    private void registerItemRenderers() {
        modelHelper.register(new ModelResourceLocation(DEContent.chaos_shard.getRegistryName(), "inventory"), new RenderItemChaosShard(DEContent.chaos_shard));
        modelHelper.register(new ModelResourceLocation(DEContent.chaos_frag_large.getRegistryName(), "inventory"), new RenderItemChaosShard(DEContent.chaos_frag_large));
        modelHelper.register(new ModelResourceLocation(DEContent.chaos_frag_medium.getRegistryName(), "inventory"), new RenderItemChaosShard(DEContent.chaos_frag_medium));
        modelHelper.register(new ModelResourceLocation(DEContent.chaos_frag_small.getRegistryName(), "inventory"), new RenderItemChaosShard(DEContent.chaos_frag_small));
        modelHelper.register(new ModelResourceLocation(DEContent.mob_soul.getRegistryName(), "inventory"), new RenderItemMobSoul());
        modelHelper.register(new ModelResourceLocation(DEContent.crystal_io_basic.getRegistryName(), "inventory"), new RenderItemEnergyCrystal(EnergyCrystal.CrystalType.CRYSTAL_IO, TechLevel.DRACONIUM));
        modelHelper.register(new ModelResourceLocation(DEContent.crystal_io_wyvern.getRegistryName(), "inventory"), new RenderItemEnergyCrystal(EnergyCrystal.CrystalType.CRYSTAL_IO, TechLevel.WYVERN));
        modelHelper.register(new ModelResourceLocation(DEContent.crystal_io_draconic.getRegistryName(), "inventory"), new RenderItemEnergyCrystal(EnergyCrystal.CrystalType.CRYSTAL_IO, TechLevel.DRACONIC));
//        modelHelper.register(new ModelResourceLocation(DEContent.crystal_io_chaotic.getRegistryName(), "inventory"), new RenderItemEnergyCrystal(CRYSTAL_IO, CHAOTIC));
        modelHelper.register(new ModelResourceLocation(DEContent.crystal_relay_basic.getRegistryName(), "inventory"), new RenderItemEnergyCrystal(EnergyCrystal.CrystalType.RELAY, TechLevel.DRACONIUM));
        modelHelper.register(new ModelResourceLocation(DEContent.crystal_relay_wyvern.getRegistryName(), "inventory"), new RenderItemEnergyCrystal(EnergyCrystal.CrystalType.RELAY, TechLevel.WYVERN));
        modelHelper.register(new ModelResourceLocation(DEContent.crystal_relay_draconic.getRegistryName(), "inventory"), new RenderItemEnergyCrystal(EnergyCrystal.CrystalType.RELAY, TechLevel.DRACONIC));
//        modelHelper.register(new ModelResourceLocation(DEContent.crystal_relay_chaotic.getRegistryName(), "inventory"), new RenderItemEnergyCrystal(RELAY, CHAOTIC));
        modelHelper.register(new ModelResourceLocation(DEContent.crystal_wireless_basic.getRegistryName(), "inventory"), new RenderItemEnergyCrystal(EnergyCrystal.CrystalType.WIRELESS, TechLevel.DRACONIUM));
        modelHelper.register(new ModelResourceLocation(DEContent.crystal_wireless_wyvern.getRegistryName(), "inventory"), new RenderItemEnergyCrystal(EnergyCrystal.CrystalType.WIRELESS, TechLevel.WYVERN));
        modelHelper.register(new ModelResourceLocation(DEContent.crystal_wireless_draconic.getRegistryName(), "inventory"), new RenderItemEnergyCrystal(EnergyCrystal.CrystalType.WIRELESS, TechLevel.DRACONIC));
//        modelHelper.register(new ModelResourceLocation(DEContent.crystal_wireless_chaotic.getRegistryName(), "inventory"), new RenderItemEnergyCrystal(WIRELESS, CHAOTIC));

        modelHelper.register(new ModelResourceLocation(DEContent.draconium_chest.getRegistryName(), "inventory"), new RenderItemDraconiumChest());

        modelHelper.register(new ModelResourceLocation(DEContent.reactor_core.getRegistryName(), "inventory"), new RenderItemReactorComponent());
        modelHelper.register(new ModelResourceLocation(DEContent.reactor_stabilizer.getRegistryName(), "inventory"), new RenderItemReactorComponent());
        modelHelper.register(new ModelResourceLocation(DEContent.reactor_injector.getRegistryName(), "inventory"), new RenderItemReactorComponent());
        modelHelper.register(new ModelResourceLocation(DEContent.reactor_prt_stab_frame.getRegistryName(), "inventory"), new RenderItemReactorComponent());
        modelHelper.register(new ModelResourceLocation(DEContent.reactor_prt_in_rotor.getRegistryName(), "inventory"), new RenderItemReactorComponent());
        modelHelper.register(new ModelResourceLocation(DEContent.reactor_prt_out_rotor.getRegistryName(), "inventory"), new RenderItemReactorComponent());
        modelHelper.register(new ModelResourceLocation(DEContent.reactor_prt_rotor_full.getRegistryName(), "inventory"), new RenderItemReactorComponent());
        modelHelper.register(new ModelResourceLocation(DEContent.reactor_prt_focus_ring.getRegistryName(), "inventory"), new RenderItemReactorComponent());


        if (DEConfig.fancyToolModels) {
            modelHelper.register(new ModelResourceLocation(DEContent.pickaxe_wyvern.getRegistryName(), "inventory"), new RenderModularPickaxe(TechLevel.WYVERN));
            modelHelper.register(new ModelResourceLocation(DEContent.pickaxe_draconic.getRegistryName(), "inventory"), new RenderModularPickaxe(TechLevel.DRACONIC));
            modelHelper.register(new ModelResourceLocation(DEContent.pickaxe_chaotic.getRegistryName(), "inventory"), new RenderModularPickaxe(TechLevel.CHAOTIC));

            modelHelper.register(new ModelResourceLocation(DEContent.axe_wyvern.getRegistryName(), "inventory"), new RenderModularAxe(TechLevel.WYVERN));
            modelHelper.register(new ModelResourceLocation(DEContent.axe_draconic.getRegistryName(), "inventory"), new RenderModularAxe(TechLevel.DRACONIC));
            modelHelper.register(new ModelResourceLocation(DEContent.axe_chaotic.getRegistryName(), "inventory"), new RenderModularAxe(TechLevel.CHAOTIC));

            modelHelper.register(new ModelResourceLocation(DEContent.shovel_wyvern.getRegistryName(), "inventory"), new RenderModularShovel(TechLevel.WYVERN));
            modelHelper.register(new ModelResourceLocation(DEContent.shovel_draconic.getRegistryName(), "inventory"), new RenderModularShovel(TechLevel.DRACONIC));
            modelHelper.register(new ModelResourceLocation(DEContent.shovel_chaotic.getRegistryName(), "inventory"), new RenderModularShovel(TechLevel.CHAOTIC));

            modelHelper.register(new ModelResourceLocation(DEContent.sword_wyvern.getRegistryName(), "inventory"), new RenderModularSword(TechLevel.WYVERN));
            modelHelper.register(new ModelResourceLocation(DEContent.sword_draconic.getRegistryName(), "inventory"), new RenderModularSword(TechLevel.DRACONIC));
            modelHelper.register(new ModelResourceLocation(DEContent.sword_chaotic.getRegistryName(), "inventory"), new RenderModularSword(TechLevel.CHAOTIC));

            modelHelper.register(new ModelResourceLocation(DEContent.bow_wyvern.getRegistryName(), "inventory"), new RenderModularBow(TechLevel.WYVERN));
            modelHelper.register(new ModelResourceLocation(DEContent.bow_draconic.getRegistryName(), "inventory"), new RenderModularBow(TechLevel.DRACONIC));
            modelHelper.register(new ModelResourceLocation(DEContent.bow_chaotic.getRegistryName(), "inventory"), new RenderModularBow(TechLevel.CHAOTIC));

            modelHelper.register(new ModelResourceLocation(DEContent.staff_draconic.getRegistryName(), "inventory"), new RenderModularStaff(TechLevel.DRACONIC));
            modelHelper.register(new ModelResourceLocation(DEContent.staff_chaotic.getRegistryName(), "inventory"), new RenderModularStaff(TechLevel.CHAOTIC));

            modelHelper.register(new ModelResourceLocation(DEContent.hoe_wyvern.getRegistryName(), "inventory"), new RenderModularHoe(TechLevel.WYVERN));
            modelHelper.register(new ModelResourceLocation(DEContent.hoe_draconic.getRegistryName(), "inventory"), new RenderModularHoe(TechLevel.DRACONIC));
            modelHelper.register(new ModelResourceLocation(DEContent.hoe_chaotic.getRegistryName(), "inventory"), new RenderModularHoe(TechLevel.CHAOTIC));

            modelHelper.register(new ModelResourceLocation(DEContent.chestpiece_wyvern.getRegistryName(), "inventory"), new RenderModularChestpiece(TechLevel.WYVERN));
            modelHelper.register(new ModelResourceLocation(DEContent.chestpiece_draconic.getRegistryName(), "inventory"), new RenderModularChestpiece(TechLevel.DRACONIC));
            modelHelper.register(new ModelResourceLocation(DEContent.chestpiece_chaotic.getRegistryName(), "inventory"), new RenderModularChestpiece(TechLevel.CHAOTIC));
        }
    }

    private void setupRenderLayers() {
        ItemBlockRenderTypes.setRenderLayer(DEContent.grinder, RenderType.cutoutMipped());
        ItemBlockRenderTypes.setRenderLayer(DEContent.generator, RenderType.cutoutMipped());
        ItemBlockRenderTypes.setRenderLayer(DEContent.energy_transfuser, RenderType.cutoutMipped());
        ItemBlockRenderTypes.setRenderLayer(DEContent.portal, RenderType.cutout());
        ItemBlockRenderTypes.setRenderLayer(DEContent.ore_draconium_overworld, renderType -> renderType == RenderType.solid() || renderType == RenderType.cutoutMipped());
        ItemBlockRenderTypes.setRenderLayer(DEContent.ore_draconium_end, renderType -> renderType == RenderType.solid() || renderType == RenderType.cutoutMipped());
        ItemBlockRenderTypes.setRenderLayer(DEContent.ore_draconium_nether, renderType -> renderType == RenderType.solid() || renderType == RenderType.cutoutMipped());
        ItemBlockRenderTypes.setRenderLayer(DEContent.ore_draconium_deepslate, renderType -> renderType == RenderType.solid() || renderType == RenderType.cutoutMipped());
//        RenderTypeLookup.setRenderLayer(DEContent.chaos_crystal, RenderType.getCutout());
//        RenderTypeLookup.setRenderLayer(DEContent.chaos_crystal_part, RenderType.getCutout());
    }

    @Override
    public void serverSetup(FMLDedicatedServerSetupEvent event) {
        super.serverSetup(event);
    }

    public void registerEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
        //Block Entities
        event.registerBlockEntityRenderer(DEContent.tile_grinder, RenderTileGrinder::new);
        event.registerBlockEntityRenderer(DEContent.tile_draconium_chest, DraconiumChestTileRenderer::new);
        event.registerBlockEntityRenderer(DEContent.tile_storage_core, RenderTileEnergyCore::new);
        event.registerBlockEntityRenderer(DEContent.tile_energy_pylon, RenderTileEnergyPylon::new);
        event.registerBlockEntityRenderer(DEContent.tile_core_stabilizer, RenderEnergyCoreStabilizer::new);
        event.registerBlockEntityRenderer(DEContent.tile_stabilized_spawner, RenderTileStabilizedSpawner::new);
        event.registerBlockEntityRenderer(DEContent.tile_generator, RenderTileGenerator::new);
        event.registerBlockEntityRenderer(DEContent.tile_crystal_io, RenderTileEnergyCrystal::new);
        event.registerBlockEntityRenderer(DEContent.tile_crystal_relay, RenderTileEnergyCrystal::new);
        event.registerBlockEntityRenderer(DEContent.tile_crystal_wireless, RenderTileEnergyCrystal::new);
        event.registerBlockEntityRenderer(DEContent.tile_reactor_core, RenderTileReactorCore::new);
        event.registerBlockEntityRenderer(DEContent.tile_reactor_injector, RenderTileReactorComponent::new);
        event.registerBlockEntityRenderer(DEContent.tile_reactor_stabilizer, RenderTileReactorComponent::new);
        event.registerBlockEntityRenderer(DEContent.tile_crafting_core, RenderTileFusionCraftingCore::new);
        event.registerBlockEntityRenderer(DEContent.tile_crafting_injector, RenderTileCraftingInjector::new);
        event.registerBlockEntityRenderer(DEContent.tile_potentiometer, RenderTilePotentiometer::new);
        event.registerBlockEntityRenderer(DEContent.tile_energy_transfuser, RenderTileEnergyTransfuser::new);
        event.registerBlockEntityRenderer(DEContent.tile_chaos_crystal, RenderTileChaosCrystal::new);
        event.registerBlockEntityRenderer(DEContent.tile_dislocator_pedestal, RenderTileDislocatorPedestal::new);
        event.registerBlockEntityRenderer(DEContent.tile_placed_item, RenderTilePlacedItem::new);
        event.registerBlockEntityRenderer(DEContent.tile_disenchanter, RenderTileDisenchanter::new);
        event.registerBlockEntityRenderer(DEContent.tile_celestial_manipulator, RenderTileCelestialManipulator::new);
        event.registerBlockEntityRenderer(DEContent.tile_entity_detector, RenderTileEntityDetector::new);

        //Entities
        event.registerEntityRenderer(DEContent.draconicGuardian, DraconicGuardianRenderer::new);
        event.registerEntityRenderer(DEContent.guardianProjectile, GuardianProjectileRenderer::new);
        event.registerEntityRenderer(DEContent.guardianCrystal, GuardianCrystalRenderer::new);
//        event.registerEntityRenderer(DEContent.persistentItem, manager -> new ItemEntityRenderer(manager, Minecraft.getInstance().getItemRenderer()));
        event.registerEntityRenderer(DEContent.draconicArrow, DraconicArrowRenderer::new);
        event.registerEntityRenderer(DEContent.guardianWither, GuardianWitherRenderer::new);
    }

    public void registerHudElements(RegistryEvent.Register<AbstractHudElement> event) {
        event.getRegistry().register((hudElement = new ShieldHudElement()).setRegistryName("shield_hud"));
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private void onAddRenderLayers(EntityRenderersEvent.AddLayers event) {
        for (String skin : event.getSkins()) {
            LivingEntityRenderer renderer = event.getSkin(skin);
            assert renderer != null;
            renderer.addLayer(new ModularArmorLayer(renderer, event.getEntityModels(), skin.equals("slim")));
            renderer.addLayer(new ElytraLayer(renderer, event.getEntityModels()) {
                @Override
                public boolean shouldRender(@NotNull ItemStack stack, @NotNull LivingEntity entity) {
                    return stack.getItem() instanceof IModularArmor && stack.canElytraFly(entity);
                }
            });
        }

//        for (EntityType<?> type : ForgeRegistries.ENTITIES.getValues()) {
//            EntityRenderer r = event.getRenderer(SneakyUtils.unsafeCast(type));
//            if (r instanceof LivingEntityRenderer<?, ?> renderer && renderer.getModel() instanceof HumanoidModel) {
//                renderer.addLayer(new ModularArmorLayer(renderer, event.getEntityModels(), false));
//            }
//        }
    }

    @Override
    public ENetFXHandler createENetFXHandler(IENetEffectTile tile) {
        if (EffectiveSide.get().isServer()) {
            return super.createENetFXHandler(tile);
        }
        return tile.createClientFXHandler();
    }

    @Override
    public ReactorEffectHandler createReactorFXHandler(TileReactorCore tile) {
        if (EffectiveSide.get().isServer()) {
            return super.createReactorFXHandler(tile);
        }
        return new ReactorEffectHandler(tile);
    }

    @Override
    public ISidedTileHandler createGeneratorSoundHandler(TileGenerator tile) {
        return new GeneratorSoundHandler(tile);
    }

    @Override
    public ITileFXHandler createFusionFXHandler(TileFusionCraftingCore tile) {
        return new FusionTileFXHandler(tile);
    }
}
