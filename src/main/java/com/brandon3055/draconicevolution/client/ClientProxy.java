package com.brandon3055.draconicevolution.client;

import codechicken.lib.model.ModelRegistryHelper;
import codechicken.lib.texture.SpriteRegistryHelper;
import codechicken.lib.util.ResourceUtils;
import com.brandon3055.brandonscore.client.BCSprites;
import com.brandon3055.draconicevolution.CommonProxy;
import com.brandon3055.draconicevolution.DEConfig;
import com.brandon3055.draconicevolution.api.energy.IENetEffectTile;
import com.brandon3055.draconicevolution.blocks.energynet.rendering.ENetFXHandler;
import com.brandon3055.draconicevolution.blocks.reactor.ReactorEffectHandler;
import com.brandon3055.draconicevolution.blocks.reactor.tileentity.TileReactorCore;
import com.brandon3055.draconicevolution.blocks.tileentity.TileGenerator;
import com.brandon3055.draconicevolution.client.gui.*;
import com.brandon3055.draconicevolution.client.gui.modular.GuiModularItem;
import com.brandon3055.draconicevolution.client.gui.modular.itemconfig.GuiConfigurableItem;
import com.brandon3055.draconicevolution.client.handler.ClientEventHandler;
import com.brandon3055.draconicevolution.client.keybinding.KeyBindings;
import com.brandon3055.draconicevolution.client.keybinding.KeyInputHandler;
import com.brandon3055.draconicevolution.client.model.VBOArmorLayer;
import com.brandon3055.draconicevolution.client.render.entity.DraconicGuardianRenderer;
import com.brandon3055.draconicevolution.client.render.entity.GuardianCrystalRenderer;
import com.brandon3055.draconicevolution.client.render.entity.GuardianProjectileRenderer;
import com.brandon3055.draconicevolution.client.render.item.*;
import com.brandon3055.draconicevolution.client.render.tile.*;
import com.brandon3055.draconicevolution.client.sound.GeneratorSoundHandler;
import com.brandon3055.draconicevolution.entity.guardian.DraconicGuardianEntity;
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
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.thread.EffectiveSide;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLDedicatedServerSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import static com.brandon3055.brandonscore.api.TechLevel.*;
import static com.brandon3055.draconicevolution.blocks.energynet.EnergyCrystal.CrystalType.*;

public class ClientProxy extends CommonProxy {

    public static SpriteRegistryHelper spriteHelper = new SpriteRegistryHelper();
    public static ModelRegistryHelper modelHelper = new ModelRegistryHelper();
    public static ModuleSpriteUploader moduleSpriteUploader;
//    public static LayerContributorPerkRenderer layerWings;


    @Override
    public void construct() {
        super.construct();
        FMLJavaModLoadingContext.get().getModEventBus().addListener((ColorHandlerEvent.Block event) -> moduleSpriteUploader = new ModuleSpriteUploader());
        spriteHelper.addIIconRegister(new DETextures());
        FMLJavaModLoadingContext.get().getModEventBus().addListener(DESprites::initialize);
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
        registerTextures();
        setupRenderLayers();

        MinecraftForge.EVENT_BUS.register(new KeyInputHandler());
        MinecraftForge.EVENT_BUS.register(new ClientEventHandler());
        KeyBindings.init();



//        spriteHelper.addIIconRegister(ModuleTextures.LOCATION_MODULE_TEXTURE, new ModuleTextures());

        ResourceUtils.registerReloadListener(new DETextures());
//        ResourceUtils.registerReloadListener(new ModuleTextures());

        //Because i want this to render on bipedal mobs.
        for (EntityRenderer<?> e : Minecraft.getInstance().getRenderManager().renderers.values()) {
            if (e instanceof LivingRenderer && ((LivingRenderer) e).getEntityModel() instanceof BipedModel) {
                boolean foundArmor = false;
                for (Object layer : ((LivingRenderer) e).layerRenderers) {
                    if (layer instanceof BipedArmorLayer) {
                        ((LivingRenderer<?, ?>) e).addLayer(new VBOArmorLayer((LivingRenderer<?, ?>) e, (BipedArmorLayer) layer));
                        foundArmor = true;
                        break;
                    }
                }
                if (!foundArmor){
                    ((LivingRenderer<?, ?>) e).addLayer(new VBOArmorLayer((LivingRenderer<?, ?>) e, null));
                }
            }
        }

        for (PlayerRenderer renderPlayer : Minecraft.getInstance().getRenderManager().getSkinMap().values()) {
            renderPlayer.addLayer(new VBOArmorLayer<>(renderPlayer, null));
            renderPlayer.addLayer(new ElytraLayer(renderPlayer){
                @Override
                public boolean shouldRender(ItemStack stack, LivingEntity entity) {
                    return stack.getItem() instanceof IModularArmor && stack.canElytraFly(entity);
                }
            });
        }


//        ModelResourceLocation modelLocation = new ModelResourceLocation(DEContent.stabilized_spawner.getRegistryName(), "inventory");
//        IBakedModel bakedModel = new RenderItemStabilizedSpawner();
//        modelHelper.register(modelLocation, bakedModel);
//
//        MinecraftForge.EVENT_BUS.addListener((EntityJoinWorldEvent e) -> LogHelper.dev(e.getEntity()));
//        Minecraft.getInstance().getRenderManager().renderers.values().forEach(e -> {
//            if (e instanceof LivingRenderer) {
//                ((LivingRenderer) e).addLayer(new TestRenderLayer((LivingRenderer) e));
//            }
//        });


//        RenderType modelType = RenderType.getEntitySolid(new ResourceLocation(DraconicEvolution.MODID, "textures/models/block/pylon_sphere_texture.png"));
//        CCModel trackerModel;
//        Map<String, CCModel> map = OBJParser.parseModels(new ResourceLocation(DraconicEvolution.MODID, "models/pylon_sphere.obj"), GL11.GL_QUADS, null);
//        trackerModel = CCModel.combine(map.values()).backfacedCopy();
//        trackerModel.apply(new Scale(1, 2, 1));
//        trackerModel.computeNormals();
//
//
//        MinecraftForge.EVENT_BUS.addListener((RenderLivingEvent.Post<LivingEntity, EntityModel<LivingEntity>> e) -> {
//            MatrixStack mStack = e.getMatrixStack();
//            IRenderTypeBuffer getter = e.getBuffers();
//            LivingEntity entity = e.getEntity();
//            if (!entity.getPersistentData().contains("wr:trackers")) return;
//            ListNBT trackers = entity.getPersistentData().getList("wr:trackers", 10);
//
//            for (INBT inbt : trackers) {
//                CompoundNBT nbt = (CompoundNBT) inbt;
//                Vector3 vec = Vector3.fromNBT(nbt.getCompound("vec"));
//                float rot = nbt.getFloat("rot");
//
//                Matrix4 mat = new Matrix4(mStack);
//                CCRenderState ccrs = CCRenderState.instance();
//                ccrs.reset();
//                ccrs.brightness = 240;
//                ccrs.bind(modelType, getter);
//
//                mat.apply(new Rotation((entity.renderYawOffset - rot) * -MathHelper.torad, Vector3.Y_POS));
//                mat.translate(0, entity.getHeight() / 2, 0);
//                mat.translate(vec);
//
//                mat.scale(0.1);
//
//                trackerModel.render(ccrs, mat);
//            }
//        });
    }

    private void registerTextures() {
//        BCSprites.registerThemed(MODID, "<location>");
    }

    private void registerGuiFactories() {
        ScreenManager.registerFactory(DEContent.container_generator, GuiGenerator::new);
        ScreenManager.registerFactory(DEContent.container_grinder, GuiGrinder::new);
        ScreenManager.registerFactory(DEContent.container_energy_core, GuiEnergyCore::new);
        ScreenManager.registerFactory(DEContent.container_modular_item, GuiModularItem::new);
        ScreenManager.registerFactory(DEContent.container_configurable_item, GuiConfigurableItem::new);
        ScreenManager.registerFactory(DEContent.container_reactor, GuiReactor::new);

//        ScreenManager.registerFactory(DEContent.container_celestial_manipulator, GuiCelestialManipulator::new);
//        ScreenManager.registerFactory(DEContent.container_dissenchanter, ::new);
//        ScreenManager.registerFactory(DEContent.container_draconium_chest, ContainerDraconiumChest::new);
//        ScreenManager.registerFactory(DEContent.container_energy_crystal, ContainerEnergyCrystal::new);
//        ScreenManager.registerFactory(DEContent.container_energy_infuser, ContainerEnergyInfuser::new);
        ScreenManager.registerFactory(DEContent.container_fusion_crafting_core, GuiFusionCraftingCore::new);
//        ScreenManager.registerFactory(DEContent.container_reactor, ContainerReactor::new);
        ScreenManager.registerFactory(DEContent.container_flow_gate, GuiFlowGate::new);
        ScreenManager.registerFactory(DEContent.container_energy_transfuser, GuiEnergyTransfuser::new);
    }

    private void registerTileRenderers() {
        ClientRegistry.bindTileEntityRenderer(DEContent.tile_grinder, RenderTileGrinder::new);
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

        modelHelper.register(new ModelResourceLocation(DEContent.reactor_core.getRegistryName(), "inventory"), new RenderItemReactorComponent(0));
        modelHelper.register(new ModelResourceLocation(DEContent.reactor_stabilizer.getRegistryName(), "inventory"), new RenderItemReactorComponent(1));
        modelHelper.register(new ModelResourceLocation(DEContent.reactor_injector.getRegistryName(), "inventory"), new RenderItemReactorComponent(2));


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
        RenderTypeLookup.setRenderLayer(DEContent.grinder, RenderType.getCutoutMipped());
        RenderTypeLookup.setRenderLayer(DEContent.generator, RenderType.getCutoutMipped());
        RenderTypeLookup.setRenderLayer(DEContent.energy_transfuser, RenderType.getCutoutMipped());
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

    //    @Override
//    public ISound playISound(ISound sound) {
//        FMLClientHandler.instance().getClient().getSoundHandler().playSound(sound);
//        return sound;
//    }
}
