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
import com.brandon3055.draconicevolution.client.model.VBOArmorLayer;
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
        spriteHelper.addIIconRegister(new DETextures());
        modBus.addListener(DEGuiSprites::initialize);
        modBus.addListener(DEMiscSprites::initialize);

        StaffRenderEventHandler.init();
        CustomBossInfoHandler.init();
        MinecraftForge.EVENT_BUS.addListener(this::registerShaderReloads);
        modBus.addGenericListener(AbstractHudElement.class, this::registerHudElements);
        modBus.addListener(this::registerEntityRenderers);

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
        registerTileRenderers();
        registerEntityRendering();
        setupRenderLayers();

        MinecraftForge.EVENT_BUS.register(new KeyInputHandler());
        MinecraftForge.EVENT_BUS.register(new ClientEventHandler());
        KeyBindings.init();

        ResourceUtils.registerReloadListener(new DETextures());

        event.enqueueWork(() -> {
            Minecraft mc = Minecraft.getInstance();
            EntityModelSet modelSet = mc.getEntityRenderDispatcher().entityModels;

            //Because i want this to render on bipedal mobs.
            for (EntityRenderer<?> e : mc.getEntityRenderDispatcher().renderers.values()) {
                if (e instanceof LivingEntityRenderer && ((LivingEntityRenderer<?, ?>) e).getModel() instanceof HumanoidModel) {
                    boolean foundArmor = false;
                    for (Object layer : ((LivingEntityRenderer<?, ?>) e).layers) {
                        if (layer instanceof HumanoidArmorLayer) {
                            ((LivingEntityRenderer<?, ?>) e).addLayer(new VBOArmorLayer((LivingEntityRenderer<?, ?>) e, (HumanoidArmorLayer<?, ?, ?>) layer));
                            foundArmor = true;
                            break;
                        }
                    }
                    if (!foundArmor) {
                        ((LivingEntityRenderer<?, ?>) e).addLayer(new VBOArmorLayer((LivingEntityRenderer<?, ?>) e, modelSet, false));
                    }
                }
            }

            for (EntityRenderer<? extends Player> renderer : mc.getEntityRenderDispatcher().getSkinMap().values()) {
                if (renderer instanceof PlayerRenderer) {
                    PlayerModel<AbstractClientPlayer> model = ((PlayerRenderer) renderer).getModel();
                    ((PlayerRenderer) renderer).addLayer(new VBOArmorLayer<>((PlayerRenderer) renderer, modelSet, model.slim));
                    ((PlayerRenderer) renderer).addLayer(new ElytraLayer((PlayerRenderer) renderer, modelSet) {
                        @Override
                        public boolean shouldRender(ItemStack stack, LivingEntity entity) {
                            return stack.getItem() instanceof IModularArmor && stack.canElytraFly(entity);
                        }
                    });
                }
            }
        });
    }

    private void registerShaderReloads(ParticleFactoryRegisterEvent event) {
        if (Minecraft.getInstance() == null) return;

//        if (DEConfig.guardianShaders) {
//            ResourceUtils.registerReloadListener(CustomBossInfoHandler.shieldShader);
//            ResourceUtils.registerReloadListener(DraconicGuardianRenderer.shieldShader);
//        }
//
//        if (DEConfig.crystalShaders) {
//            ResourceUtils.registerReloadListener(RenderItemEnergyCrystal.crystalShader);
//            ResourceUtils.registerReloadListener(RenderTileEnergyCrystal.crystalShader);
//        }
//
//        if (DEConfig.toolShaders) {
//            ResourceUtils.registerReloadListener(RenderModularBow.stringShader);
//            ResourceUtils.registerReloadListener(ModularArmorModel.shieldShader);
//            ResourceUtils.registerReloadListener(RenderModularChestpeice.coreShader);
//            ResourceUtils.registerReloadListener(ToolRenderBase.chaosShader);
//            ResourceUtils.registerReloadListener(ToolRenderBase.gemShader);
//            ResourceUtils.registerReloadListener(ToolRenderBase.bladeShader);
//            ResourceUtils.registerReloadListener(ToolRenderBase.traceShader);
//        }
//
//        if (DEConfig.reactorShaders) {
//            ResourceUtils.registerReloadListener(ExplosionFX.blastWaveProgram);
//            ResourceUtils.registerReloadListener(ExplosionFX.coreEffectProgram);
//            ResourceUtils.registerReloadListener(ExplosionFX.leadingWaveProgram);
//            ResourceUtils.registerReloadListener(ReactorBeamFX.beamShaderE);
//            ResourceUtils.registerReloadListener(ReactorBeamFX.beamShaderI);
//            ResourceUtils.registerReloadListener(ReactorBeamFX.beamShaderO);
//            ResourceUtils.registerReloadListener(ClientEventHandler.explosionShader);
//            ResourceUtils.registerReloadListener(RenderTileReactorCore.coreShader);
//            ResourceUtils.registerReloadListener(RenderTileReactorCore.shieldShader);
//        }
//
//        if (DEConfig.otherShaders) {
//            ResourceUtils.registerReloadListener(RenderTileChaosCrystal.chaosShader);
//            ResourceUtils.registerReloadListener(RenderTileChaosCrystal.shieldShader);
//        }

    }

    private void registerGuiFactories() {
        MenuScreens.register(DEContent.container_generator, GuiGenerator::new);
        MenuScreens.register(DEContent.container_grinder, GuiGrinder::new);
        MenuScreens.register(DEContent.container_draconium_chest, GuiDraconiumChest::new);
        MenuScreens.register(DEContent.container_energy_core, GuiEnergyCore::new);
        MenuScreens.register(DEContent.container_modular_item, GuiModularItem::new);
        MenuScreens.register(DEContent.container_configurable_item, GuiConfigurableItem::new);
        MenuScreens.register(DEContent.container_reactor, GuiReactor::new);

//        ScreenManager.registerFactory(DEContent.container_celestial_manipulator, GuiCelestialManipulator::new);
//        ScreenManager.registerFactory(DEContent.container_dissenchanter, ::new);
//        ScreenManager.registerFactory(DEContent.container_energy_crystal, ContainerEnergyCrystal::new);
//        ScreenManager.registerFactory(DEContent.container_energy_infuser, ContainerEnergyInfuser::new);
        MenuScreens.register(DEContent.container_fusion_crafting_core, GuiFusionCraftingCore::new);
//        ScreenManager.registerFactory(DEContent.container_reactor, ContainerReactor::new);
        MenuScreens.register(DEContent.container_flow_gate, GuiFlowGate::new);
        MenuScreens.register(DEContent.container_energy_transfuser, GuiEnergyTransfuser::new);
    }

    private void registerTileRenderers() {
//        BlockEntityRenderers.register(DEContent.tile_grinder, RenderTileGrinder::new);
//        BlockEntityRenderers.register(DEContent.tile_draconium_chest, DraconiumChestTileRenderer::new);
//        BlockEntityRenderers.register(DEContent.tile_storage_core, RenderTileEnergyCore::new);
//        BlockEntityRenderers.register(DEContent.tile_energy_pylon, RenderTileEnergyPylon::new);
//        BlockEntityRenderers.register(DEContent.tile_core_stabilizer, RenderTileECStabilizer::new);
//        BlockEntityRenderers.register(DEContent.tile_stabilized_spawner, RenderTileStabilizedSpawner::new);
//        BlockEntityRenderers.register(DEContent.tile_generator, RenderTileGenerator::new);
//        BlockEntityRenderers.register(DEContent.tile_crystal_io, RenderTileEnergyCrystal::new);
//        BlockEntityRenderers.register(DEContent.tile_crystal_relay, RenderTileEnergyCrystal::new);
//        BlockEntityRenderers.register(DEContent.tile_crystal_wireless, RenderTileEnergyCrystal::new);
//        BlockEntityRenderers.register(DEContent.tile_reactor_core, RenderTileReactorCore::new);
//        BlockEntityRenderers.register(DEContent.tile_reactor_injector, RenderTileReactorComponent::new);
//        BlockEntityRenderers.register(DEContent.tile_reactor_stabilizer, RenderTileReactorComponent::new);
//        BlockEntityRenderers.register(DEContent.tile_crafting_core, RenderTileFusionCraftingCore::new);
//        BlockEntityRenderers.register(DEContent.tile_crafting_injector, RenderTileCraftingInjector::new);
//        BlockEntityRenderers.register(DEContent.tile_potentiometer, RenderTilePotentiometer::new);
//        BlockEntityRenderers.register(DEContent.tile_energy_transfuser, RenderTileEnergyTransfuser::new);
//        BlockEntityRenderers.register(DEContent.tile_chaos_crystal, RenderTileChaosCrystal::new);
//        BlockEntityRenderers.register(DEContent.tile_dislocator_pedestal, RenderTileDislocatorPedestal::new);
//        BlockEntityRenderers.register(DEContent.tile_placed_item, RenderTilePlacedItem::new);
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

        modelHelper.register(new ModelResourceLocation(DEContent.reactor_core.getRegistryName(), "inventory"), new RenderItemReactorComponent(0));
        modelHelper.register(new ModelResourceLocation(DEContent.reactor_stabilizer.getRegistryName(), "inventory"), new RenderItemReactorComponent(1));
        modelHelper.register(new ModelResourceLocation(DEContent.reactor_injector.getRegistryName(), "inventory"), new RenderItemReactorComponent(2));
        modelHelper.register(new ModelResourceLocation(DEContent.reactor_prt_stab_frame.getRegistryName(), "inventory"), new RenderItemReactorPart());
        modelHelper.register(new ModelResourceLocation(DEContent.reactor_prt_in_rotor.getRegistryName(), "inventory"), new RenderItemReactorPart());
        modelHelper.register(new ModelResourceLocation(DEContent.reactor_prt_out_rotor.getRegistryName(), "inventory"), new RenderItemReactorPart());
        modelHelper.register(new ModelResourceLocation(DEContent.reactor_prt_rotor_full.getRegistryName(), "inventory"), new RenderItemReactorPart());
        modelHelper.register(new ModelResourceLocation(DEContent.reactor_prt_focus_ring.getRegistryName(), "inventory"), new RenderItemReactorPart());


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

        //Entities
        event.registerEntityRenderer(DEContent.draconicGuardian, DraconicGuardianRenderer::new);
        event.registerEntityRenderer(DEContent.guardianProjectile, GuardianProjectileRenderer::new);
        event.registerEntityRenderer(DEContent.guardianCrystal, GuardianCrystalRenderer::new);
//        event.registerEntityRenderer(DEContent.persistentItem, manager -> new ItemEntityRenderer(manager, Minecraft.getInstance().getItemRenderer()));
        event.registerEntityRenderer(DEContent.draconicArrow, DraconicArrowRenderer::new);
        event.registerEntityRenderer(DEContent.guardianWither, GuardianWitherRenderer::new);
    }

    public void registerEntityRendering() {


        //Entities
//        RenderingRegistry.registerEntityRenderingHandler(EntityChaosGuardian.class, RenderChaosGuardian::new);
//        RenderingRegistry.registerEntityRenderingHandler(EntityDragonHeart.class, RenderDragonHeart::new);
//        RenderingRegistry.registerEntityRenderingHandler(EntityGuardianProjectile.class, RenderGuardianProjectile::new);
//        RenderingRegistry.registerEntityRenderingHandler(EntityGuardianCrystal.class, RenderGuardianCrystal::new);
//        RenderingRegistry.registerEntityRenderingHandler(EntityChaosImplosion.class, RenderEntityChaosVortex::new);
//
//        if (DEConfig.disableCustomArrowModel) {
//            RenderingRegistry.registerEntityRenderingHandler(EntityCustomArrow.class, manager -> new RenderArrow<EntityCustomArrow>(manager) {
//                @Override
//                protected ResourceLocation getEntityTexture(EntityCustomArrow entity) {
//                    return RenderTippedArrow.RES_ARROW;
//                }
//            });
//        }
//        else {
//            RenderingRegistry.registerEntityRenderingHandler(EntityCustomArrow.class, RenderCustomArrow::new);
//        }
//
//        RenderingRegistry.registerEntityRenderingHandler(EntityLootCore.class, RenderLootCore::new);
//        RenderingRegistry.registerEntityRenderingHandler(EntityEnderEnergyManipulator.class, RenderEntityEnderEnergyManipulator::new);
    }


    public void registerHudElements(RegistryEvent.Register<AbstractHudElement> event) {
        event.getRegistry().register((hudElement = new ShieldHudElement()).setRegistryName("shield_hud"));
    }

//    @Override
//    public void preInit(FMLPreInitializationEvent event) {
//        super.preInit(event);
//
//        OBJLoader.INSTANCE.addDomain(DraconicEvolution.MODID);
//        TextureUtils.addIconRegister(new DETextures());
//        ResourceUtils.registerReloadListener(new DETextures());
//
//        DEImageHandler.init(event);
//
//        TextureUtils.addIconRegister(new ArmorModelHelper());
//        TextureUtils.addIconRegister(new DETextureCache());
//
//        registerRendering();
//    }
//
//    @Override
//    public void init(FMLInitializationEvent event) {
//
//        super.init(event);

    //        CCRenderEventHandler.init();
//    }
//
//    @Override
//    public void postInit(FMLPostInitializationEvent event) {
//        super.postInit(event);
//
//        for (RenderPlayer renderPlayer : Minecraft.getInstance().getRenderManager().getSkinMap().values()) {
//            renderPlayer.addLayer(layerWings = new LayerContributorPerkRenderer(renderPlayer));
//        }
//    }
//
//    @Override
//    public void initializeNetwork() {
//        super.initializeNetwork();
//        PacketCustom.assignHandler("DEPCChannel", new ClientPacketHandler());
//    }
//

    //
//    @Override
//    public void registerParticles() {
//        DEParticles.registerClient();
//    }
//
//    public boolean isOp(String paramString) {
//        return Minecraft.getInstance().world.getWorldInfo().getGameType().isCreative();
//    }
//
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

    //    @Override
//    public ISound playISound(ISound sound) {
//        FMLClientHandler.instance().getClient().getSoundHandler().playSound(sound);
//        return sound;
//    }
}
