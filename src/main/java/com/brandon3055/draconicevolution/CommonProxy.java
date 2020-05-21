package com.brandon3055.draconicevolution;

import codechicken.lib.math.MathHelper;
import codechicken.lib.vec.Vector3;
import com.brandon3055.draconicevolution.api.energy.IENetEffectTile;
import com.brandon3055.draconicevolution.blocks.energynet.rendering.ENetFXHandler;
import com.brandon3055.draconicevolution.blocks.reactor.ReactorEffectHandler;
import com.brandon3055.draconicevolution.blocks.reactor.tileentity.TileReactorCore;
import com.brandon3055.draconicevolution.handlers.DEEventHandler;
import com.brandon3055.draconicevolution.init.ModCapabilities;
import com.brandon3055.draconicevolution.network.DraconicNetwork;
import com.brandon3055.draconicevolution.utils.LogHelper;
import net.minecraft.client.audio.ISound;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.FloatNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.ProjectileImpactEvent;
import net.minecraftforge.fml.common.thread.EffectiveSide;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLDedicatedServerSetupEvent;

public class CommonProxy {

    public void construct() {
        DraconicNetwork.init();
    }

    public void commonSetup(FMLCommonSetupEvent event) {
        ModCapabilities.register();
        MinecraftForge.EVENT_BUS.register(new DEEventHandler());


        MinecraftForge.EVENT_BUS.addListener((ProjectileImpactEvent.Throwable e) -> {
            if (!(e.getRayTraceResult() instanceof EntityRayTraceResult) || EffectiveSide.get().isClient()) return;
            LivingEntity entity = (LivingEntity) ((EntityRayTraceResult) e.getRayTraceResult()).getEntity();
            Entity throwable = e.getThrowable();

            Vector3 evec = Vector3.fromEntityCenter(entity);
//            Vector3 posVec = new Vector3(throwable.posX, throwable.posY + throwable.getHeight() / 2, throwable.posZ);
            Vector3 posVec = new Vector3(throwable.posX, throwable.posY + (throwable.getHeight() / 2D), throwable.posZ).subtract(new Vector3(throwable.getMotion()).multiply(4));
//            Vector3 lastPosVec = new Vector3(throwable.lastTickPosX, throwable.lastTickPosY + (throwable.getHeight() / 2D), throwable.lastTickPosZ);
            Vector3 lastPosVec = new Vector3(throwable.posX, throwable.posY + (throwable.getHeight() / 2D), throwable.posZ);
            Vector3 diffVec = lastPosVec.copy().subtract(posVec);
            double distanceBetweenTicks = diffVec.mag();
            double width = entity.getWidth() + 0.0;
            double height = entity.getHeight() + 0.0;

            Vector3 interpVec = null;
            for (double d = 0; d <= distanceBetweenTicks; d += 0.05) {
                interpVec = diffVec.copy().normalize().multiply(d).add(lastPosVec);
                double xDiff = Math.abs(evec.x - interpVec.x);
                double yDiff = Math.abs(evec.y - interpVec.y);
                double zDiff = Math.abs(evec.z - interpVec.z);
                if (yDiff <= height / 2 && xDiff <= width / 2 && zDiff <= width / 2) {
                    break;
                }
                interpVec = null;
            }

            if (interpVec == null) {
                return; //In your actual entity code this would be considered a miss.
            }

            interpVec.set(interpVec.x - entity.posX, (interpVec.y + throwable.getHeight() / 2 - (entity.posY - entity.getYOffset() + entity.getHeight() / 2)), interpVec.z - entity.posZ);
//            interpVec.subtract(throwable.posX, 0, throwable.posZ);

//            Vector3 entPos = Vector3.fromEntity(hit);
//            Vector3 throwPos = Vector3.fromEntity(e.getThrowable());
//            Vector3 hitVec = throwPos.subtract(entPos);
//            hitVec.rotate(MathHelper.torad * (hit.renderYawOffset + 180), Vector3.Y_POS);

//            entity.getPersistentData().remove("wr:trackers");
            CompoundNBT trackerData = new CompoundNBT();
            trackerData.put("vec", interpVec.writeToNBT(new CompoundNBT()));
            trackerData.putFloat("rot", entity.renderYawOffset);

            ListNBT trackerList = entity.getPersistentData().getList("wr:trackers", 10);
            trackerList.add(trackerData);
            entity.getPersistentData().put("wr:trackers", trackerList);


            DraconicNetwork.sendTrackerData(entity.getEntityId(), trackerList);

//            LogHelper.dev(hitVec + " " + (hit.renderYawOffset % 360) * MathHelper.torad);
//            LogHelper.dev((hit.renderYawOffset));


            ///summon minecraft:pig ~ ~ ~  {NoAI:1}
        });
    }

    public void clientSetup(FMLClientSetupEvent event) {

    }

    public void serverSetup(FMLDedicatedServerSetupEvent event) {
    }

    public void registerToolRenderer(Item tool) {

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
