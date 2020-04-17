package com.brandon3055.draconicevolution;

import com.brandon3055.draconicevolution.api.IENetEffectTile;
import com.brandon3055.draconicevolution.blocks.energynet.rendering.ENetFXHandler;
import com.brandon3055.draconicevolution.blocks.reactor.ReactorEffectHandler;
import com.brandon3055.draconicevolution.blocks.reactor.tileentity.TileReactorCore;
import com.brandon3055.draconicevolution.client.DEParticles;
import com.brandon3055.draconicevolution.handlers.DEEventHandler;
import com.brandon3055.draconicevolution.network.DraconicNetwork;
import net.minecraft.client.audio.ISound;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLDedicatedServerSetupEvent;

public class CommonProxy {


    public void commonSetup(FMLCommonSetupEvent event) {
        DraconicNetwork.init();
        MinecraftForge.EVENT_BUS.register(new DEEventHandler());
    }

    public void clientSetup(FMLClientSetupEvent event) {

    }

    public void serverSetup(FMLDedicatedServerSetupEvent event) {

    }



//    public void preInit(FMLPreInitializationEvent event) {
//        RecipeManager.initialize();
//        registerEventListeners(event.getSide());
//        ContributorHandler.init();
//        initializeNetwork();
//        EnchantmentReaper.init();
//        AE2Compat.init();
//
//        registerEntities();
//
//        LogHelper.info("Finished PreInitialization");
//    }
//
//    public void init(FMLInitializationEvent event) {
//        CCOCIntegration.init();
//        ModHelper.init();
//        DragonChunkLoader.init();
//
//        LogHelper.info("Finished Initialization");
//    }
//
//    public void postInit(FMLPostInitializationEvent event) {
//        OreDoublingRegistry.init();
//        Achievements.registerAchievementPane();
//        RecipeManager.loadJsonRecipeModifications();
//
//        LogHelper.info("Finished PostInitialization");
//    }
//
//    public void initializeNetwork() {
//        DraconicEvolution.network = NetworkRegistry.INSTANCE.newSimpleChannel(DraconicEvolution.networkChannelName);
//        DraconicEvolution.network.registerMessage(PacketSimpleBoolean.Handler.class, PacketSimpleBoolean.class, 0, Side.SERVER);
//        DraconicEvolution.network.registerMessage(PacketConfigureTool.Handler.class, PacketConfigureTool.class, 1, Side.SERVER);
//        DraconicEvolution.network.registerMessage(PacketPlaySound.Handler.class, PacketPlaySound.class, 2, Side.CLIENT);
//        DraconicEvolution.network.registerMessage(PacketShieldHit.Handler.class, PacketShieldHit.class, 3, Side.CLIENT);
//        DraconicEvolution.network.registerMessage(PacketDislocator.Handler.class, PacketDislocator.class, 4, Side.SERVER);
//        DraconicEvolution.network.registerMessage(PacketPlaceItem.Handler.class, PacketPlaceItem.class, 5, Side.SERVER);
//        DraconicEvolution.network.registerMessage(PacketLootSync.Handler.class, PacketLootSync.class, 6, Side.CLIENT);
//        DraconicEvolution.network.registerMessage(PacketToolProfile.Handler.class, PacketToolProfile.class, 7, Side.SERVER);
//        DraconicEvolution.network.registerMessage(PacketContributor.Handler.class, PacketContributor.class, 8, Side.CLIENT);
//        DraconicEvolution.network.registerMessage(PacketContributor.Handler.class, PacketContributor.class, 9, Side.SERVER);
//        DraconicEvolution.network.registerMessage(CrystalUpdateBatcher.Handler.class, CrystalUpdateBatcher.class, 10, Side.CLIENT);
//        DraconicEvolution.network.registerMessage(PacketExplosionFX.Handler.class, PacketExplosionFX.class, 11, Side.CLIENT);
//        DraconicEvolution.network.registerMessage(PacketDislocatorUpdateRequest.Handler.class, PacketDislocatorUpdateRequest.class, 12, Side.CLIENT);
//        DraconicEvolution.network.registerMessage(PacketDislocatorUpdateRequest.Handler.class, PacketDislocatorUpdateRequest.class, 13, Side.SERVER);
//        DraconicEvolution.network.registerMessage(PacketParticleGenerator.Handler.class, PacketParticleGenerator.class, 14, Side.SERVER);
//
//        PacketCustom.assignHandler("DEPCChannel", new ServerPacketHandler());
//    }
//
//    public void registerEventListeners(Side s) {
//
//        MinecraftForge.EVENT_BUS.register(new Achievements());
//        MinecraftForge.EVENT_BUS.register(new CustomArmorHandler());
//    }
//
//    //@Callback
//    public void registerEntities() {//TODO 1.14 fix naming and localizations
////		EntityRegistry.registerModEntity(EntityCustomDragon.class, "EnderDragon", 0, DraconicEvolution.instance, 256, 3, true);
//        EntityRegistry.registerModEntity(new ResourceLocation(DraconicEvolution.MODID, "PersistentItem"), EntityPersistentItem.class, "draconicevolution:PersistentItem", 1, DraconicEvolution.instance, 64, 5, true);
////		EntityRegistry.registerModEntity(EntityDraconicArrow.class, "Arrow", 2, DraconicEvolution.instance, 32, 5, true);
////		EntityRegistry.registerModEntity(EntityEnderArrow.class, "Ender Arrow", 3, DraconicEvolution.instance, 32, 1, true);
//        EntityRegistry.registerModEntity(new ResourceLocation(DraconicEvolution.MODID, "DragonHeartItem"), EntityDragonHeart.class, "draconicevolution:DragonHeartItem", 5, DraconicEvolution.instance, 128, 5, true);
//        EntityRegistry.registerModEntity(new ResourceLocation(DraconicEvolution.MODID, "ChaosGuardian"), EntityChaosGuardian.class, "draconicevolution:ChaosGuardian", 6, DraconicEvolution.instance, 512, 1, true);
//        EntityRegistry.registerModEntity(new ResourceLocation(DraconicEvolution.MODID, "GuardianProjectile"), EntityGuardianProjectile.class, "draconicevolution:GuardianProjectile", 7, DraconicEvolution.instance, 256, 1, true);
//        EntityRegistry.registerModEntity(new ResourceLocation(DraconicEvolution.MODID, "GuardianCrystal"), EntityGuardianCrystal.class, "draconicevolution:GuardianCrystal", 8, DraconicEvolution.instance, 256, 5, false);
////		EntityRegistry.registerModEntity(EntityChaosBolt.class, "ChaosBolt", 9, DraconicEvolution.instance, 32, 5, true);
//        EntityRegistry.registerModEntity(new ResourceLocation(DraconicEvolution.MODID, "EntityChaosEnergyVortex"), EntityChaosImplosion.class, "draconicevolution:EntityChaosEnergyVortex", 10, DraconicEvolution.instance, 512, 5, true);
//        EntityRegistry.registerModEntity(new ResourceLocation(DraconicEvolution.MODID, "CustomArrow"), EntityCustomArrow.class, "draconicevolution:CustomArrow", 11, DraconicEvolution.instance, 128, 1, true);
//        EntityRegistry.registerModEntity(new ResourceLocation(DraconicEvolution.MODID, "LootCore"), EntityLootCore.class, "draconicevolution:LootCore", 12, DraconicEvolution.instance, 64, 5, true);
//        EntityRegistry.registerModEntity(new ResourceLocation(DraconicEvolution.MODID, "EnderEnergyManipulator"), EntityEnderEnergyManipulator.class, "draconicevolution:EnderEnergyManipulator", 13, DraconicEvolution.instance, 128, 5, true);
//    }

//    public void registerParticles() {
//        DEParticles.registerServer();
//    }

    public ENetFXHandler createENetFXHandler(IENetEffectTile tile) {
        return tile.createServerFXHandler();
    }

    public ReactorEffectHandler createReactorFXHandler(TileReactorCore tile) {
        return null;
    }

    public ISound playISound(ISound sound) {
        return null;
    }

}
