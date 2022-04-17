package com.brandon3055.draconicevolution.client;

import codechicken.lib.model.ModelRegistryHelper;
import codechicken.lib.texture.SpriteRegistryHelper;
import codechicken.lib.util.ResourceUtils;
import com.brandon3055.brandonscore.api.hud.AbstractHudElement;
import com.brandon3055.draconicevolution.CommonProxy;
import com.brandon3055.draconicevolution.DEConfig;
import com.brandon3055.draconicevolution.api.energy.IENetEffectTile;
import com.brandon3055.draconicevolution.blocks.energynet.rendering.ENetFXHandler;
import com.brandon3055.draconicevolution.blocks.reactor.ReactorEffectHandler;
import com.brandon3055.draconicevolution.blocks.reactor.tileentity.TileReactorCore;
import com.brandon3055.draconicevolution.blocks.tileentity.TileFusionCraftingCore;
import com.brandon3055.draconicevolution.blocks.tileentity.TileGenerator;
import com.brandon3055.draconicevolution.client.gui.*;
import com.brandon3055.draconicevolution.client.gui.GuiDraconiumChest;
import com.brandon3055.draconicevolution.client.gui.modular.GuiModularItem;
import com.brandon3055.draconicevolution.client.gui.modular.itemconfig.GuiConfigurableItem;
import com.brandon3055.draconicevolution.client.handler.ClientEventHandler;
import com.brandon3055.draconicevolution.client.handler.StaffRenderEventHandler;
import com.brandon3055.draconicevolution.client.keybinding.KeyBindings;
import com.brandon3055.draconicevolution.client.keybinding.KeyInputHandler;
import com.brandon3055.draconicevolution.client.model.ModularArmorModel;
import com.brandon3055.draconicevolution.client.model.VBOArmorLayer;
import com.brandon3055.draconicevolution.client.render.effect.ExplosionFX;
import com.brandon3055.draconicevolution.client.render.effect.ReactorBeamFX;
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
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.entity.LivingRenderer;
import net.minecraft.client.renderer.entity.PlayerRenderer;
import net.minecraft.client.renderer.entity.layers.BipedArmorLayer;
import net.minecraft.client.renderer.entity.layers.ElytraLayer;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.renderer.model.ModelResourceLocation;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.client.event.ParticleFactoryRegisterEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.thread.EffectiveSide;
import net.minecraftforge.fml.event.lifecycle.*;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import static com.brandon3055.brandonscore.api.TechLevel.*;
import static com.brandon3055.draconicevolution.blocks.energynet.EnergyCrystal.CrystalType.*;

public class ClientProxy extends CommonProxy {

    public static SpriteRegistryHelper spriteHelper = new SpriteRegistryHelper();
    public static ModelRegistryHelper modelHelper = new ModelRegistryHelper();
    public static ModuleSpriteUploader moduleSpriteUploader;
    public static ShieldHudElement hudElement = null;
//    public static LayerContributorPerkRenderer layerWings;


    @Override
    public void construct() {
        super.construct();
        FMLJavaModLoadingContext.get().getModEventBus().addListener((ColorHandlerEvent.Block event) -> moduleSpriteUploader = new ModuleSpriteUploader());
        spriteHelper.addIIconRegister(new DETextures());
        FMLJavaModLoadingContext.get().getModEventBus().addListener(DESprites::initialize);

        StaffRenderEventHandler.init();
        CustomBossInfoHandler.init();
        FMLJavaModLoadingContext.get().getModEventBus().addGenericListener(AbstractHudElement.class, this::registerHudElements);
        MinecraftForge.EVENT_BUS.addListener(this::registerShaderReloads);
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
            //Because i want this to render on bipedal mobs.
            for (EntityRenderer<?> e : Minecraft.getInstance().getEntityRenderDispatcher().renderers.values()) {
                if (e instanceof LivingRenderer && ((LivingRenderer<?, ?>) e).getModel() instanceof BipedModel) {
                    boolean foundArmor = false;
                    for (Object layer : ((LivingRenderer<?, ?>) e).layers) {
                        if (layer instanceof BipedArmorLayer) {
                            ((LivingRenderer<?, ?>) e).addLayer(new VBOArmorLayer((LivingRenderer<?, ?>) e, (BipedArmorLayer<?, ?, ?>) layer));
                            foundArmor = true;
                            break;
                        }
                    }
                    if (!foundArmor){
                        ((LivingRenderer<?, ?>) e).addLayer(new VBOArmorLayer((LivingRenderer<?, ?>) e, null));
                    }
                }
            }

            for (PlayerRenderer renderPlayer : Minecraft.getInstance().getEntityRenderDispatcher().getSkinMap().values()) {
                renderPlayer.addLayer(new VBOArmorLayer<>(renderPlayer, null));
                renderPlayer.addLayer(new ElytraLayer(renderPlayer){
                    @Override
                    public boolean shouldRender(ItemStack stack, LivingEntity entity) {
                        return stack.getItem() instanceof IModularArmor && stack.canElytraFly(entity);
                    }
                });
            }
        });
    }

    private void registerShaderReloads(ParticleFactoryRegisterEvent event) {
        if (Minecraft.getInstance() == null) return;

        if (DEConfig.guardianShaders) {
            ResourceUtils.registerReloadListener(CustomBossInfoHandler.shieldShader);
            ResourceUtils.registerReloadListener(DraconicGuardianRenderer.shieldShader);
        }

        if (DEConfig.crystalShaders) {
            ResourceUtils.registerReloadListener(RenderItemEnergyCrystal.crystalShader);
            ResourceUtils.registerReloadListener(RenderTileEnergyCrystal.crystalShader);
        }

        if (DEConfig.toolShaders) {
            ResourceUtils.registerReloadListener(RenderModularBow.stringShader);
            ResourceUtils.registerReloadListener(ModularArmorModel.shieldShader);
            ResourceUtils.registerReloadListener(RenderModularChestpeice.coreShader);
            ResourceUtils.registerReloadListener(ToolRenderBase.chaosShader);
            ResourceUtils.registerReloadListener(ToolRenderBase.gemShader);
            ResourceUtils.registerReloadListener(ToolRenderBase.bladeShader);
            ResourceUtils.registerReloadListener(ToolRenderBase.traceShader);
        }

        if (DEConfig.reactorShaders) {
            ResourceUtils.registerReloadListener(ExplosionFX.blastWaveProgram);
            ResourceUtils.registerReloadListener(ExplosionFX.coreEffectProgram);
            ResourceUtils.registerReloadListener(ExplosionFX.leadingWaveProgram);
            ResourceUtils.registerReloadListener(ReactorBeamFX.beamShaderE);
            ResourceUtils.registerReloadListener(ReactorBeamFX.beamShaderI);
            ResourceUtils.registerReloadListener(ReactorBeamFX.beamShaderO);
            ResourceUtils.registerReloadListener(ClientEventHandler.explosionShader);
            ResourceUtils.registerReloadListener(RenderTileReactorCore.coreShader);
            ResourceUtils.registerReloadListener(RenderTileReactorCore.shieldShader);
        }

        if (DEConfig.otherShaders) {
            ResourceUtils.registerReloadListener(RenderTileChaosCrystal.chaosShader);
            ResourceUtils.registerReloadListener(RenderTileChaosCrystal.shieldShader);
        }

    }

    private void registerGuiFactories() {
        ScreenManager.register(DEContent.container_generator, GuiGenerator::new);
        ScreenManager.register(DEContent.container_grinder, GuiGrinder::new);
        ScreenManager.register(DEContent.container_draconium_chest, GuiDraconiumChest::new);
        ScreenManager.register(DEContent.container_energy_core, GuiEnergyCore::new);
        ScreenManager.register(DEContent.container_modular_item, GuiModularItem::new);
        ScreenManager.register(DEContent.container_configurable_item, GuiConfigurableItem::new);
        ScreenManager.register(DEContent.container_reactor, GuiReactor::new);

//        ScreenManager.registerFactory(DEContent.container_celestial_manipulator, GuiCelestialManipulator::new);
//        ScreenManager.registerFactory(DEContent.container_dissenchanter, ::new);
//        ScreenManager.registerFactory(DEContent.container_energy_crystal, ContainerEnergyCrystal::new);
//        ScreenManager.registerFactory(DEContent.container_energy_infuser, ContainerEnergyInfuser::new);
        ScreenManager.register(DEContent.container_fusion_crafting_core, GuiFusionCraftingCore::new);
//        ScreenManager.registerFactory(DEContent.container_reactor, ContainerReactor::new);
        ScreenManager.register(DEContent.container_flow_gate, GuiFlowGate::new);
        ScreenManager.register(DEContent.container_energy_transfuser, GuiEnergyTransfuser::new);
    }

    private void registerTileRenderers() {
        ClientRegistry.bindTileEntityRenderer(DEContent.tile_grinder, RenderTileGrinder::new);
        ClientRegistry.bindTileEntityRenderer(DEContent.tile_draconium_chest, RenderTileDraconiumChest::new);
        ClientRegistry.bindTileEntityRenderer(DEContent.tile_storage_core, RenderTileEnergyCore::new);
        ClientRegistry.bindTileEntityRenderer(DEContent.tile_energy_pylon, RenderTileEnergyPylon::new);
        ClientRegistry.bindTileEntityRenderer(DEContent.tile_core_stabilizer, RenderTileECStabilizer::new);
        ClientRegistry.bindTileEntityRenderer(DEContent.tile_stabilized_spawner, RenderTileStabilizedSpawner::new);
        ClientRegistry.bindTileEntityRenderer(DEContent.tile_generator, RenderTileGenerator::new);
        ClientRegistry.bindTileEntityRenderer(DEContent.tile_crystal_io, RenderTileEnergyCrystal::new);
        ClientRegistry.bindTileEntityRenderer(DEContent.tile_crystal_relay, RenderTileEnergyCrystal::new);
        ClientRegistry.bindTileEntityRenderer(DEContent.tile_crystal_wireless, RenderTileEnergyCrystal::new);
        ClientRegistry.bindTileEntityRenderer(DEContent.tile_reactor_core, RenderTileReactorCore::new);
        ClientRegistry.bindTileEntityRenderer(DEContent.tile_reactor_injector, RenderTileReactorComponent::new);
        ClientRegistry.bindTileEntityRenderer(DEContent.tile_reactor_stabilizer, RenderTileReactorComponent::new);
        ClientRegistry.bindTileEntityRenderer(DEContent.tile_crafting_core, RenderTileFusionCraftingCore::new);
        ClientRegistry.bindTileEntityRenderer(DEContent.tile_crafting_injector, RenderTileCraftingInjector::new);
        ClientRegistry.bindTileEntityRenderer(DEContent.tile_potentiometer, RenderTilePotentiometer::new);
        ClientRegistry.bindTileEntityRenderer(DEContent.tile_energy_transfuser, RenderTileEnergyTransfuser::new);
        ClientRegistry.bindTileEntityRenderer(DEContent.tile_chaos_crystal, RenderTileChaosCrystal::new);
        ClientRegistry.bindTileEntityRenderer(DEContent.tile_dislocator_pedestal, RenderTileDislocatorPedestal::new);
        ClientRegistry.bindTileEntityRenderer(DEContent.tile_placed_item, RenderTilePlacedItem::new);
    }

    @SuppressWarnings("ConstantConditions")
    private void registerItemRenderers() {
        modelHelper.register(new ModelResourceLocation(DEContent.chaos_shard.getRegistryName(), "inventory"), new RenderItemChaosShard(DEContent.chaos_shard));
        modelHelper.register(new ModelResourceLocation(DEContent.chaos_frag_large.getRegistryName(), "inventory"), new RenderItemChaosShard(DEContent.chaos_frag_large));
        modelHelper.register(new ModelResourceLocation(DEContent.chaos_frag_medium.getRegistryName(), "inventory"), new RenderItemChaosShard(DEContent.chaos_frag_medium));
        modelHelper.register(new ModelResourceLocation(DEContent.chaos_frag_small.getRegistryName(), "inventory"), new RenderItemChaosShard(DEContent.chaos_frag_small));
        modelHelper.register(new ModelResourceLocation(DEContent.mob_soul.getRegistryName(), "inventory"), new RenderItemMobSoul());
        modelHelper.register(new ModelResourceLocation(DEContent.crystal_io_basic.getRegistryName(), "inventory"), new RenderItemEnergyCrystal(CRYSTAL_IO, DRACONIUM));
        modelHelper.register(new ModelResourceLocation(DEContent.crystal_io_wyvern.getRegistryName(), "inventory"), new RenderItemEnergyCrystal(CRYSTAL_IO, WYVERN));
        modelHelper.register(new ModelResourceLocation(DEContent.crystal_io_draconic.getRegistryName(), "inventory"), new RenderItemEnergyCrystal(CRYSTAL_IO, DRACONIC));
//        modelHelper.register(new ModelResourceLocation(DEContent.crystal_io_chaotic.getRegistryName(), "inventory"), new RenderItemEnergyCrystal(CRYSTAL_IO, CHAOTIC));
        modelHelper.register(new ModelResourceLocation(DEContent.crystal_relay_basic.getRegistryName(), "inventory"), new RenderItemEnergyCrystal(RELAY, DRACONIUM));
        modelHelper.register(new ModelResourceLocation(DEContent.crystal_relay_wyvern.getRegistryName(), "inventory"), new RenderItemEnergyCrystal(RELAY, WYVERN));
        modelHelper.register(new ModelResourceLocation(DEContent.crystal_relay_draconic.getRegistryName(), "inventory"), new RenderItemEnergyCrystal(RELAY, DRACONIC));
//        modelHelper.register(new ModelResourceLocation(DEContent.crystal_relay_chaotic.getRegistryName(), "inventory"), new RenderItemEnergyCrystal(RELAY, CHAOTIC));
        modelHelper.register(new ModelResourceLocation(DEContent.crystal_wireless_basic.getRegistryName(), "inventory"), new RenderItemEnergyCrystal(WIRELESS, DRACONIUM));
        modelHelper.register(new ModelResourceLocation(DEContent.crystal_wireless_wyvern.getRegistryName(), "inventory"), new RenderItemEnergyCrystal(WIRELESS, WYVERN));
        modelHelper.register(new ModelResourceLocation(DEContent.crystal_wireless_draconic.getRegistryName(), "inventory"), new RenderItemEnergyCrystal(WIRELESS, DRACONIC));
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
            modelHelper.register(new ModelResourceLocation(DEContent.pickaxe_wyvern.getRegistryName(), "inventory"), new RenderModularPickaxe(WYVERN));
            modelHelper.register(new ModelResourceLocation(DEContent.pickaxe_draconic.getRegistryName(), "inventory"), new RenderModularPickaxe(DRACONIC));
            modelHelper.register(new ModelResourceLocation(DEContent.pickaxe_chaotic.getRegistryName(), "inventory"), new RenderModularPickaxe(CHAOTIC));

            modelHelper.register(new ModelResourceLocation(DEContent.axe_wyvern.getRegistryName(), "inventory"), new RenderModularAxe(WYVERN));
            modelHelper.register(new ModelResourceLocation(DEContent.axe_draconic.getRegistryName(), "inventory"), new RenderModularAxe(DRACONIC));
            modelHelper.register(new ModelResourceLocation(DEContent.axe_chaotic.getRegistryName(), "inventory"), new RenderModularAxe(CHAOTIC));

            modelHelper.register(new ModelResourceLocation(DEContent.shovel_wyvern.getRegistryName(), "inventory"), new RenderModularShovel(WYVERN));
            modelHelper.register(new ModelResourceLocation(DEContent.shovel_draconic.getRegistryName(), "inventory"), new RenderModularShovel(DRACONIC));
            modelHelper.register(new ModelResourceLocation(DEContent.shovel_chaotic.getRegistryName(), "inventory"), new RenderModularShovel(CHAOTIC));

            modelHelper.register(new ModelResourceLocation(DEContent.sword_wyvern.getRegistryName(), "inventory"), new RenderModularSword(WYVERN));
            modelHelper.register(new ModelResourceLocation(DEContent.sword_draconic.getRegistryName(), "inventory"), new RenderModularSword(DRACONIC));
            modelHelper.register(new ModelResourceLocation(DEContent.sword_chaotic.getRegistryName(), "inventory"), new RenderModularSword(CHAOTIC));

            modelHelper.register(new ModelResourceLocation(DEContent.bow_wyvern.getRegistryName(), "inventory"), new RenderModularBow(WYVERN));
            modelHelper.register(new ModelResourceLocation(DEContent.bow_draconic.getRegistryName(), "inventory"), new RenderModularBow(DRACONIC));
            modelHelper.register(new ModelResourceLocation(DEContent.bow_chaotic.getRegistryName(), "inventory"), new RenderModularBow(CHAOTIC));

            modelHelper.register(new ModelResourceLocation(DEContent.staff_draconic.getRegistryName(), "inventory"), new RenderModularStaff(DRACONIC));
            modelHelper.register(new ModelResourceLocation(DEContent.staff_chaotic.getRegistryName(), "inventory"), new RenderModularStaff(CHAOTIC));

            modelHelper.register(new ModelResourceLocation(DEContent.hoe_wyvern.getRegistryName(), "inventory"), new RenderModularHoe(WYVERN));
            modelHelper.register(new ModelResourceLocation(DEContent.hoe_draconic.getRegistryName(), "inventory"), new RenderModularHoe(DRACONIC));
            modelHelper.register(new ModelResourceLocation(DEContent.hoe_chaotic.getRegistryName(), "inventory"), new RenderModularHoe(CHAOTIC));

            modelHelper.register(new ModelResourceLocation(DEContent.chestpiece_wyvern.getRegistryName(), "inventory"), new RenderModularChestpeice(WYVERN));
            modelHelper.register(new ModelResourceLocation(DEContent.chestpiece_draconic.getRegistryName(), "inventory"), new RenderModularChestpeice(DRACONIC));
            modelHelper.register(new ModelResourceLocation(DEContent.chestpiece_chaotic.getRegistryName(), "inventory"), new RenderModularChestpeice(CHAOTIC));
        }
    }

    private void setupRenderLayers() {
        RenderTypeLookup.setRenderLayer(DEContent.grinder, RenderType.cutoutMipped());
        RenderTypeLookup.setRenderLayer(DEContent.generator, RenderType.cutoutMipped());
        RenderTypeLookup.setRenderLayer(DEContent.energy_transfuser, RenderType.cutoutMipped());
        RenderTypeLookup.setRenderLayer(DEContent.portal, RenderType.cutout());
//        RenderTypeLookup.setRenderLayer(DEContent.chaos_crystal, RenderType.getCutout());
//        RenderTypeLookup.setRenderLayer(DEContent.chaos_crystal_part, RenderType.getCutout());
    }

    @Override
    public void serverSetup(FMLDedicatedServerSetupEvent event) {
        super.serverSetup(event);
    }

    public void registerEntityRendering() {

        RenderingRegistry.registerEntityRenderingHandler(DEContent.draconicGuardian, DraconicGuardianRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(DEContent.guardianProjectile, GuardianProjectileRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(DEContent.guardianCrystal, GuardianCrystalRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(DEContent.persistentItem, manager -> new ItemRenderer(manager, Minecraft.getInstance().getItemRenderer()));
        RenderingRegistry.registerEntityRenderingHandler(DEContent.draconicArrow, DraconicArrowRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(DEContent.guardianWither, GuardianWitherRenderer::new);

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
