package com.brandon3055.draconicevolution.init;

import codechicken.lib.gui.modular.sprite.GuiTextures;
import com.brandon3055.brandonscore.BrandonsCore;
import com.brandon3055.brandonscore.api.hud.AbstractHudElement;
import com.brandon3055.brandonscore.client.hud.HudManager;
import com.brandon3055.brandonscore.handlers.contributor.ContributorHandler;
import com.brandon3055.draconicevolution.client.*;
import com.brandon3055.draconicevolution.client.gui.*;
import com.brandon3055.draconicevolution.client.gui.modular.ModularItemGui;
import com.brandon3055.draconicevolution.client.gui.modular.itemconfig.ConfigurableItemGui;
import com.brandon3055.draconicevolution.client.handler.ClientEventHandler;
import com.brandon3055.draconicevolution.client.handler.ModularItemRenderOverrideHandler;
import com.brandon3055.draconicevolution.client.handler.OverlayRenderHandler;
import com.brandon3055.draconicevolution.client.keybinding.KeyBindings;
import com.brandon3055.draconicevolution.client.keybinding.KeyInputHandler;
import com.brandon3055.draconicevolution.client.render.entity.DraconicGuardianRenderer;
import com.brandon3055.draconicevolution.client.render.entity.GuardianCrystalRenderer;
import com.brandon3055.draconicevolution.client.render.entity.GuardianProjectileRenderer;
import com.brandon3055.draconicevolution.client.render.entity.GuardianWitherRenderer;
import com.brandon3055.draconicevolution.client.render.entity.projectile.DraconicArrowRenderer;
import com.brandon3055.draconicevolution.client.render.hud.ShieldHudElement;
import com.brandon3055.draconicevolution.client.render.tile.*;
import com.brandon3055.draconicevolution.items.equipment.IModularArmor;
import net.covers1624.quack.util.CrashLock;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.layers.ElytraLayer;
import net.minecraft.client.resources.PlayerSkin;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterClientReloadListenersEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.jetbrains.annotations.NotNull;

import static com.brandon3055.draconicevolution.DraconicEvolution.MODID;

/**
 * Created by brandon3055 on 15/11/2022
 */
public class DEClient {
    private static final CrashLock LOCK = new CrashLock("Already Initialized.");

    public static final DeferredRegister<AbstractHudElement> HUDS = DeferredRegister.create(HudManager.HUD_TYPE, MODID);
    public static final DeferredHolder<AbstractHudElement, ShieldHudElement> SHIELD_HUD = HUDS.register("shield_hud", ShieldHudElement::new);

    public static void init(IEventBus modBus) {
        LOCK.lock();
        modBus.addListener(DEClient::clientSetupEvent);
//        modBus.addListener(ClientInit::onModelRegistryEvent);
        modBus.addListener(DEClient::registerRenderers);
        modBus.addListener(DEClient::onAddRenderLayers);
        modBus.addListener(DEClient::onResourceReload);

//        modBus.addListener((RegisterColorHandlersEvent.Block event) -> moduleSpriteUploader = new ModuleSpriteUploader());

        HUDS.register(modBus);

        ModularItemRenderOverrideHandler.init();
        OverlayRenderHandler.init();
        DEShaders.init(modBus);
        ClientEventHandler.init(modBus);
        AtlasTextureHelper.init(modBus);
        KeyBindings.init(modBus);
        DEGuiTextures.init(modBus);
    }

    private static final CrashLock LOCK2 = new CrashLock("Already Initialized.");
    private static void clientSetupEvent(FMLClientSetupEvent event) {
        LOCK2.lock();
        registerGuiFactories();
        registerItemRenderers();
        setupRenderLayers();
        CustomBossInfoHandler.init();

        NeoForge.EVENT_BUS.register(new KeyInputHandler());
    }

    public static void onResourceReload(RegisterClientReloadListenersEvent event) {
        event.registerReloadListener(ModuleTextures.getAtlasHolder());
    }

    private static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        //Block Entities
        event.registerBlockEntityRenderer(DEContent.TILE_GRINDER.get(), RenderTileGrinder::new);
        event.registerBlockEntityRenderer(DEContent.TILE_DRACONIUM_CHEST.get(), DraconiumChestTileRenderer::new);
        event.registerBlockEntityRenderer(DEContent.TILE_STORAGE_CORE.get(), RenderTileEnergyCore::new);
        event.registerBlockEntityRenderer(DEContent.TILE_ENERGY_PYLON.get(), RenderTileEnergyPylon::new);
        event.registerBlockEntityRenderer(DEContent.TILE_CORE_STABILIZER.get(), RenderEnergyCoreStabilizer::new);
        event.registerBlockEntityRenderer(DEContent.TILE_STABILIZED_SPAWNER.get(), RenderTileStabilizedSpawner::new);
        event.registerBlockEntityRenderer(DEContent.TILE_GENERATOR.get(), RenderTileGenerator::new);
        event.registerBlockEntityRenderer(DEContent.TILE_IO_CRYSTAL.get(), RenderTileEnergyCrystal::new);
        event.registerBlockEntityRenderer(DEContent.TILE_RELAY_CRYSTAL.get(), RenderTileEnergyCrystal::new);
        event.registerBlockEntityRenderer(DEContent.TILE_WIRELESS_CRYSTAL.get(), RenderTileEnergyCrystal::new);
        event.registerBlockEntityRenderer(DEContent.TILE_REACTOR_CORE.get(), RenderTileReactorCore::new);
        event.registerBlockEntityRenderer(DEContent.TILE_REACTOR_INJECTOR.get(), RenderTileReactorComponent::new);
        event.registerBlockEntityRenderer(DEContent.TILE_REACTOR_STABILIZER.get(), RenderTileReactorComponent::new);
        event.registerBlockEntityRenderer(DEContent.TILE_CRAFTING_CORE.get(), RenderTileFusionCraftingCore::new);
        event.registerBlockEntityRenderer(DEContent.TILE_CRAFTING_INJECTOR.get(), RenderTileCraftingInjector::new);
        event.registerBlockEntityRenderer(DEContent.TILE_POTENTIOMETER.get(), RenderTilePotentiometer::new);
        event.registerBlockEntityRenderer(DEContent.TILE_ENERGY_TRANSFUSER.get(), RenderTileEnergyTransfuser::new);
        event.registerBlockEntityRenderer(DEContent.TILE_CHAOS_CRYSTAL.get(), RenderTileChaosCrystal::new);
        event.registerBlockEntityRenderer(DEContent.TILE_DISLOCATOR_PEDESTAL.get(), RenderTileDislocatorPedestal::new);
        event.registerBlockEntityRenderer(DEContent.TILE_PLACED_ITEM.get(), RenderTilePlacedItem::new);
        event.registerBlockEntityRenderer(DEContent.TILE_DISENCHANTER.get(), RenderTileDisenchanter::new);
        event.registerBlockEntityRenderer(DEContent.TILE_CELESTIAL_MANIPULATOR.get(), RenderTileCelestialManipulator::new);
        event.registerBlockEntityRenderer(DEContent.TILE_ENTITY_DETECTOR.get(), RenderTileEntityDetector::new);

        //Entities
        event.registerEntityRenderer(DEContent.ENTITY_DRACONIC_GUARDIAN.get(), DraconicGuardianRenderer::new);
        event.registerEntityRenderer(DEContent.ENTITY_GUARDIAN_PROJECTILE.get(), GuardianProjectileRenderer::new);
        event.registerEntityRenderer(DEContent.ENTITY_GUARDIAN_CRYSTAL.get(), GuardianCrystalRenderer::new);
        event.registerEntityRenderer(DEContent.ENTITY_DRACONIC_ARROW.get(), DraconicArrowRenderer::new);
        event.registerEntityRenderer(DEContent.ENTITY_GUARDIAN_WITHER.get(), GuardianWitherRenderer::new);
    }

    private static void registerGuiFactories() {
        MenuScreens.register(DEContent.MENU_GENERATOR.get(), GeneratorGui.Screen::new);
        MenuScreens.register(DEContent.MENU_GRINDER.get(), GrinderGui.Screen::new);

        MenuScreens.register(DEContent.MENU_CONFIGURABLE_ITEM.get(), ConfigurableItemGui.Screen::new);
        MenuScreens.register(DEContent.MENU_MODULAR_ITEM.get(), ModularItemGui.Screen::new);

        MenuScreens.register(DEContent.MENU_DRACONIUM_CHEST.get(), DraconiumChestGui.Screen::new);
        MenuScreens.register(DEContent.MENU_ENERGY_CORE.get(), EnergyCoreGui.Screen::new);
        MenuScreens.register(DEContent.MENU_REACTOR.get(), ReactorGui.Screen::new);

        MenuScreens.register(DEContent.MENU_CELESTIAL_MANIPULATOR.get(), CelestialManipulatorGui.Screen::new);
        MenuScreens.register(DEContent.MENU_DISENCHANTER.get(), DisenchanterGui.Screen::new);
        MenuScreens.register(DEContent.MENU_FUSION_CRAFTING_CORE.get(), FusionCraftingCoreGui.Screen::new);
        MenuScreens.register(DEContent.MENU_FLOW_GATE.get(), FlowGateGui.Screen::new);
        MenuScreens.register(DEContent.MENU_ENTITY_DETECTOR.get(), EntityDetectorGui.Screen::new);
        MenuScreens.register(DEContent.MENU_ENERGY_TRANSFUSER.get(), EnergyTransfuserGui.Screen::new);
    }

    @SuppressWarnings ("ConstantConditions")
    private static void registerItemRenderers() {
//        MODEL_HELPER.register(new ModelResourceLocation(BuiltInRegistries.ITEM.getKey(DEContent.CHAOS_SHARD.get()), "inventory"), new RenderItemChaosShard(DEContent.CHAOS_SHARD.get()));
//        MODEL_HELPER.register(new ModelResourceLocation(BuiltInRegistries.ITEM.getKey(DEContent.CHAOS_FRAG_LARGE.get()), "inventory"), new RenderItemChaosShard(DEContent.CHAOS_FRAG_LARGE.get()));
//        MODEL_HELPER.register(new ModelResourceLocation(BuiltInRegistries.ITEM.getKey(DEContent.CHAOS_FRAG_MEDIUM.get()), "inventory"), new RenderItemChaosShard(DEContent.CHAOS_FRAG_MEDIUM.get()));
//        MODEL_HELPER.register(new ModelResourceLocation(BuiltInRegistries.ITEM.getKey(DEContent.CHAOS_FRAG_SMALL.get()), "inventory"), new RenderItemChaosShard(DEContent.CHAOS_FRAG_SMALL.get()));
//        MODEL_HELPER.register(new ModelResourceLocation(BuiltInRegistries.ITEM.getKey(DEContent.MOB_SOUL.get()), "inventory"), new RenderItemMobSoul());
//        MODEL_HELPER.register(new ModelResourceLocation(BuiltInRegistries.ITEM.getKey(DEContent.ITEM_BASIC_IO_CRYSTAL.get()), "inventory"), new RenderItemEnergyCrystal(EnergyCrystal.CrystalType.CRYSTAL_IO, TechLevel.DRACONIUM));
//        MODEL_HELPER.register(new ModelResourceLocation(BuiltInRegistries.ITEM.getKey(DEContent.ITEM_WYVERN_IO_CRYSTAL.get()), "inventory"), new RenderItemEnergyCrystal(EnergyCrystal.CrystalType.CRYSTAL_IO, TechLevel.WYVERN));
//        MODEL_HELPER.register(new ModelResourceLocation(BuiltInRegistries.ITEM.getKey(DEContent.ITEM_DRACONIC_IO_CRYSTAL.get()), "inventory"), new RenderItemEnergyCrystal(EnergyCrystal.CrystalType.CRYSTAL_IO, TechLevel.DRACONIC));
//        MODEL_HELPER.register(new ModelResourceLocation(BuiltInRegistries.ITEM.getKey(DEContent.ITEM_BASIC_RELAY_CRYSTAL.get()), "inventory"), new RenderItemEnergyCrystal(EnergyCrystal.CrystalType.RELAY, TechLevel.DRACONIUM));
//        MODEL_HELPER.register(new ModelResourceLocation(BuiltInRegistries.ITEM.getKey(DEContent.ITEM_WYVERN_RELAY_CRYSTAL.get()), "inventory"), new RenderItemEnergyCrystal(EnergyCrystal.CrystalType.RELAY, TechLevel.WYVERN));
//        MODEL_HELPER.register(new ModelResourceLocation(BuiltInRegistries.ITEM.getKey(DEContent.ITEM_DRACONIC_RELAY_CRYSTAL.get()), "inventory"), new RenderItemEnergyCrystal(EnergyCrystal.CrystalType.RELAY, TechLevel.DRACONIC));
//        MODEL_HELPER.register(new ModelResourceLocation(BuiltInRegistries.ITEM.getKey(DEContent.ITEM_BASIC_WIRELESS_CRYSTAL.get()), "inventory"), new RenderItemEnergyCrystal(EnergyCrystal.CrystalType.WIRELESS, TechLevel.DRACONIUM));
//        MODEL_HELPER.register(new ModelResourceLocation(BuiltInRegistries.ITEM.getKey(DEContent.ITEM_WYVERN_WIRELESS_CRYSTAL.get()), "inventory"), new RenderItemEnergyCrystal(EnergyCrystal.CrystalType.WIRELESS, TechLevel.WYVERN));
//        MODEL_HELPER.register(new ModelResourceLocation(BuiltInRegistries.ITEM.getKey(DEContent.ITEM_DRACONIC_WIRELESS_CRYSTAL.get()), "inventory"), new RenderItemEnergyCrystal(EnergyCrystal.CrystalType.WIRELESS, TechLevel.DRACONIC));
//
//        MODEL_HELPER.register(new ModelResourceLocation(BuiltInRegistries.ITEM.getKey(DEContent.ITEM_DRACONIUM_CHEST.get()), "inventory"), new RenderItemDraconiumChest());
//
//        MODEL_HELPER.register(new ModelResourceLocation(BuiltInRegistries.ITEM.getKey(DEContent.ITEM_REACTOR_CORE.get()), "inventory"), new RenderItemReactorComponent());
//        MODEL_HELPER.register(new ModelResourceLocation(BuiltInRegistries.ITEM.getKey(DEContent.ITEM_REACTOR_STABILIZER.get()), "inventory"), new RenderItemReactorComponent());
//        MODEL_HELPER.register(new ModelResourceLocation(BuiltInRegistries.ITEM.getKey(DEContent.ITEM_REACTOR_INJECTOR.get()), "inventory"), new RenderItemReactorComponent());
//        MODEL_HELPER.register(new ModelResourceLocation(BuiltInRegistries.ITEM.getKey(DEContent.REACTOR_PRT_STAB_FRAME.get()), "inventory"), new RenderItemReactorComponent());
//        MODEL_HELPER.register(new ModelResourceLocation(BuiltInRegistries.ITEM.getKey(DEContent.REACTOR_PRT_IN_ROTOR.get()), "inventory"), new RenderItemReactorComponent());
//        MODEL_HELPER.register(new ModelResourceLocation(BuiltInRegistries.ITEM.getKey(DEContent.REACTOR_PRT_OUT_ROTOR.get()), "inventory"), new RenderItemReactorComponent());
//        MODEL_HELPER.register(new ModelResourceLocation(BuiltInRegistries.ITEM.getKey(DEContent.REACTOR_PRT_ROTOR_FULL.get()), "inventory"), new RenderItemReactorComponent());
//        MODEL_HELPER.register(new ModelResourceLocation(BuiltInRegistries.ITEM.getKey(DEContent.REACTOR_PRT_FOCUS_RING.get()), "inventory"), new RenderItemReactorComponent());
//
//        if (DEConfig.fancyToolModels) {
//            MODEL_HELPER.register(new ModelResourceLocation(BuiltInRegistries.ITEM.getKey(DEContent.PICKAXE_WYVERN.get()), "inventory"), new RenderModularPickaxe(TechLevel.WYVERN));
//            MODEL_HELPER.register(new ModelResourceLocation(BuiltInRegistries.ITEM.getKey(DEContent.PICKAXE_DRACONIC.get()), "inventory"), new RenderModularPickaxe(TechLevel.DRACONIC));
//            MODEL_HELPER.register(new ModelResourceLocation(BuiltInRegistries.ITEM.getKey(DEContent.PICKAXE_CHAOTIC.get()), "inventory"), new RenderModularPickaxe(TechLevel.CHAOTIC));
//
//            MODEL_HELPER.register(new ModelResourceLocation(BuiltInRegistries.ITEM.getKey(DEContent.AXE_WYVERN.get()), "inventory"), new RenderModularAxe(TechLevel.WYVERN));
//            MODEL_HELPER.register(new ModelResourceLocation(BuiltInRegistries.ITEM.getKey(DEContent.AXE_DRACONIC.get()), "inventory"), new RenderModularAxe(TechLevel.DRACONIC));
//            MODEL_HELPER.register(new ModelResourceLocation(BuiltInRegistries.ITEM.getKey(DEContent.AXE_CHAOTIC.get()), "inventory"), new RenderModularAxe(TechLevel.CHAOTIC));
//
//            MODEL_HELPER.register(new ModelResourceLocation(BuiltInRegistries.ITEM.getKey(DEContent.SHOVEL_WYVERN.get()), "inventory"), new RenderModularShovel(TechLevel.WYVERN));
//            MODEL_HELPER.register(new ModelResourceLocation(BuiltInRegistries.ITEM.getKey(DEContent.SHOVEL_DRACONIC.get()), "inventory"), new RenderModularShovel(TechLevel.DRACONIC));
//            MODEL_HELPER.register(new ModelResourceLocation(BuiltInRegistries.ITEM.getKey(DEContent.SHOVEL_CHAOTIC.get()), "inventory"), new RenderModularShovel(TechLevel.CHAOTIC));
//
//            MODEL_HELPER.register(new ModelResourceLocation(BuiltInRegistries.ITEM.getKey(DEContent.SWORD_WYVERN.get()), "inventory"), new RenderModularSword(TechLevel.WYVERN));
//            MODEL_HELPER.register(new ModelResourceLocation(BuiltInRegistries.ITEM.getKey(DEContent.SWORD_DRACONIC.get()), "inventory"), new RenderModularSword(TechLevel.DRACONIC));
//            MODEL_HELPER.register(new ModelResourceLocation(BuiltInRegistries.ITEM.getKey(DEContent.SWORD_CHAOTIC.get()), "inventory"), new RenderModularSword(TechLevel.CHAOTIC));
//
//            MODEL_HELPER.register(new ModelResourceLocation(BuiltInRegistries.ITEM.getKey(DEContent.BOW_WYVERN.get()), "inventory"), new RenderModularBow(TechLevel.WYVERN));
//            MODEL_HELPER.register(new ModelResourceLocation(BuiltInRegistries.ITEM.getKey(DEContent.BOW_DRACONIC.get()), "inventory"), new RenderModularBow(TechLevel.DRACONIC));
//            MODEL_HELPER.register(new ModelResourceLocation(BuiltInRegistries.ITEM.getKey(DEContent.BOW_CHAOTIC.get()), "inventory"), new RenderModularBow(TechLevel.CHAOTIC));
//
//            MODEL_HELPER.register(new ModelResourceLocation(BuiltInRegistries.ITEM.getKey(DEContent.STAFF_DRACONIC.get()), "inventory"), new RenderModularStaff(TechLevel.DRACONIC));
//            MODEL_HELPER.register(new ModelResourceLocation(BuiltInRegistries.ITEM.getKey(DEContent.STAFF_CHAOTIC.get()), "inventory"), new RenderModularStaff(TechLevel.CHAOTIC));
//
//            MODEL_HELPER.register(new ModelResourceLocation(BuiltInRegistries.ITEM.getKey(DEContent.HOE_WYVERN.get()), "inventory"), new RenderModularHoe(TechLevel.WYVERN));
//            MODEL_HELPER.register(new ModelResourceLocation(BuiltInRegistries.ITEM.getKey(DEContent.HOE_DRACONIC.get()), "inventory"), new RenderModularHoe(TechLevel.DRACONIC));
//            MODEL_HELPER.register(new ModelResourceLocation(BuiltInRegistries.ITEM.getKey(DEContent.HOE_CHAOTIC.get()), "inventory"), new RenderModularHoe(TechLevel.CHAOTIC));
//
//            MODEL_HELPER.register(new ModelResourceLocation(BuiltInRegistries.ITEM.getKey(DEContent.CHESTPIECE_WYVERN.get()), "inventory"), new RenderModularChestpiece(TechLevel.WYVERN));
//            MODEL_HELPER.register(new ModelResourceLocation(BuiltInRegistries.ITEM.getKey(DEContent.CHESTPIECE_DRACONIC.get()), "inventory"), new RenderModularChestpiece(TechLevel.DRACONIC));
//            MODEL_HELPER.register(new ModelResourceLocation(BuiltInRegistries.ITEM.getKey(DEContent.CHESTPIECE_CHAOTIC.get()), "inventory"), new RenderModularChestpiece(TechLevel.CHAOTIC));
//        }
    }

    private static void setupRenderLayers() {
        ItemBlockRenderTypes.setRenderLayer(DEContent.GRINDER.get(), RenderType.cutoutMipped());
        ItemBlockRenderTypes.setRenderLayer(DEContent.GENERATOR.get(), RenderType.cutoutMipped());
        ItemBlockRenderTypes.setRenderLayer(DEContent.ENERGY_TRANSFUSER.get(), RenderType.cutoutMipped());
        ItemBlockRenderTypes.setRenderLayer(DEContent.PORTAL.get(), RenderType.cutout());
        ItemBlockRenderTypes.setRenderLayer(DEContent.OVERWORLD_DRACONIUM_ORE.get(), renderType -> renderType == RenderType.solid() || renderType == RenderType.cutoutMipped());
        ItemBlockRenderTypes.setRenderLayer(DEContent.END_DRACONIUM_ORE.get(), renderType -> renderType == RenderType.solid() || renderType == RenderType.cutoutMipped());
        ItemBlockRenderTypes.setRenderLayer(DEContent.NETHER_DRACONIUM_ORE.get(), renderType -> renderType == RenderType.solid() || renderType == RenderType.cutoutMipped());
        ItemBlockRenderTypes.setRenderLayer(DEContent.DEEPSLATE_DRACONIUM_ORE.get(), renderType -> renderType == RenderType.solid() || renderType == RenderType.cutoutMipped());
    }

    public static boolean deElytraVisible(ItemStack stack, LivingEntity entity) {
        if (ContributorHandler.shouldCancelElytra(entity)) return false;
        if (stack.getItem() instanceof IModularArmor item) {
            return item.canElytraFlyBC(stack, entity);
        }
        if (BrandonsCore.equipmentManager != null) {
            ItemStack curio = BrandonsCore.equipmentManager.findMatchingItem(e -> e.getItem() instanceof IModularArmor, entity);
            return curio.getItem() instanceof IModularArmor item && item.canElytraFlyBC(curio, entity);
        }
        return false;
    }

    @SuppressWarnings ({"rawtypes", "unchecked"})
    private static void onAddRenderLayers(EntityRenderersEvent.AddLayers event) {
        for (PlayerSkin.Model skin : event.getSkins()) {
            LivingEntityRenderer renderer = event.getSkin(skin);
            assert renderer != null;
            renderer.addLayer(new ElytraLayer(renderer, event.getEntityModels()) {
                @Override
                public boolean shouldRender(@NotNull ItemStack stack, @NotNull LivingEntity entity) {
                    return deElytraVisible(stack, entity);
                }
            });
        }
    }
}
