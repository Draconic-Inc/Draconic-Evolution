package com.brandon3055.draconicevolution.client;

import com.brandon3055.brandonscore.client.particle.IntParticleType;
import com.brandon3055.draconicevolution.DraconicEvolution;
import com.brandon3055.draconicevolution.client.render.particle.*;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ParticleFactoryRegisterEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ObjectHolder;

/**
 * Created by brandon3055 on 23/4/2016.
 * A list of all of DE's particles
 */
@Mod.EventBusSubscriber(modid = DraconicEvolution.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
@ObjectHolder(DraconicEvolution.MODID)
public class DEParticles {

    @ObjectHolder("flame")
    public static IntParticleType flame;
    @ObjectHolder("line_indicator")
    public static IntParticleType line_indicator;
    @ObjectHolder("energy")
    public static IntParticleType energy;
    @ObjectHolder("energy_basic")
    public static IntParticleType energy_basic;
    @ObjectHolder("energy_core")
    public static IntParticleType energy_core;
    @ObjectHolder("guardian_projectile")
    public static SimpleParticleType guardian_projectile;
    @ObjectHolder("blink")
    public static SimpleParticleType blink;
    @ObjectHolder("guardian_cloud")
    public static SimpleParticleType guardian_cloud;
    @ObjectHolder("guardian_beam")
    public static SimpleParticleType guardian_beam;

    @SubscribeEvent
    public static void registerParticles(RegistryEvent.Register<ParticleType<?>> event) {
        event.getRegistry().register(new IntParticleType(false).setRegistryName("flame"));
        event.getRegistry().register(new IntParticleType(false).setRegistryName("line_indicator"));
        event.getRegistry().register(new IntParticleType(false).setRegistryName("energy"));
        event.getRegistry().register(new IntParticleType(false).setRegistryName("energy_basic"));
        event.getRegistry().register(new IntParticleType(false).setRegistryName("energy_core"));
        event.getRegistry().register(new SimpleParticleType(false).setRegistryName("guardian_projectile"));
        event.getRegistry().register(new SimpleParticleType(false).setRegistryName("blink"));
        event.getRegistry().register(new SimpleParticleType(false).setRegistryName("guardian_cloud"));
        event.getRegistry().register(new SimpleParticleType(false).setRegistryName("guardian_beam"));
    }

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public static void registerFactories(ParticleFactoryRegisterEvent event) {
        ParticleEngine manager = Minecraft.getInstance().particleEngine;
        manager.register(flame, CustomFlameParticle.Factory::new);
        manager.register(line_indicator, ParticleLineIndicator.Factory::new);
        manager.register(energy, ParticleEnergy.Factory::new);
        manager.register(energy_basic, ParticleEnergyBasic.Factory::new);
        manager.register(energy_core, ParticleEnergyCoreFX.Factory::new);
        manager.register(guardian_projectile, GuardianProjectileParticle.Factory::new);
        manager.register(blink, BlinkParticle.Factory::new);
        manager.register(guardian_cloud, GuardianCloudParticle.Factory::new);
        manager.register(guardian_beam, GuardianBeamParticle.Factory::new);
    }


    @OnlyIn(Dist.CLIENT)
    public static Particle addParticleDirect(Level world, Particle particle) {
        if (world instanceof ClientLevel) {
            Minecraft mc = Minecraft.getInstance();
            Camera activerenderinfo = mc.gameRenderer.getMainCamera();
            if (mc != null && activerenderinfo.isInitialized() && mc.particleEngine != null) {
                mc.particleEngine.add(particle);
                return particle;
            }
        }
        return null;
    }






//    public static int ENERGY_PARTICLE;
//    public static int ENERGY_CORE_FX;
//    public static int LINE_INDICATOR;
//    public static int INFUSER;
//    public static int GUARDIAN_PROJECTILE;
//    public static int CHAOS_IMPLOSION;
//    public static int PORTAL;
//    public static int DRAGON_HEART;
//    public static int AXE_SELECTION;
//    public static int SOUL_EXTRACTION;
//    public static int ARROW_SHOCKWAVE;
//    public static int CUSTOM;
//    public static int FLAME;
//
//    public static void registerClient() {
////        ENERGY_PARTICLE = BCEffectHandler.registerFX(DE_SHEET, new ParticleEnergy.Factory());
////        ENERGY_CORE_FX = BCEffectHandler.registerFX(DE_SHEET, new ParticleEnergyCoreFX.Factory());
////        LINE_INDICATOR = BCEffectHandler.registerFX(DE_SHEET, new ParticleLineIndicator.Factory());
////        INFUSER = BCEffectHandler.registerFX(DE_SHEET, new ParticleInfuser.Factory());
////        GUARDIAN_PROJECTILE = BCEffectHandler.registerFX(DE_SHEET, new ParticleGuardianProjectile.Factory());
////        CHAOS_IMPLOSION = BCEffectHandler.registerFX(DE_SHEET, new ParticleChaosImplosion.Factory());
////        PORTAL = BCEffectHandler.registerFX(DE_SHEET, new ParticlePortal.Factory());
////        DRAGON_HEART = BCEffectHandler.registerFX(DE_SHEET, new ParticleDragonHeart.Factory());
////        AXE_SELECTION = BCEffectHandler.registerFX(new ResourceLocation("textures/items/diamond_axe.png"), new ParticleAxeSelection.Factory());
////        SOUL_EXTRACTION = BCEffectHandler.registerFX(DE_SHEET, new ParticleSoulExtraction.Factory());
////        ARROW_SHOCKWAVE = BCEffectHandler.registerFX(DE_SHEET, new ParticleArrowShockwave.Factory());
////        CUSTOM = BCEffectHandler.registerFX(CUSTOM_SHEET, new ParticleCustom.Factory());
////        FLAME = BCEffectHandler.registerFX(VANILLA_SHEET, new ParticleFlame.Factory());
//    }
//
//    public static void registerServer() {
////        ENERGY_PARTICLE = BCEffectHandler.registerFXServer();
////        ENERGY_CORE_FX = BCEffectHandler.registerFXServer();
////        LINE_INDICATOR = BCEffectHandler.registerFXServer();
////        INFUSER = BCEffectHandler.registerFXServer();
////        GUARDIAN_PROJECTILE = BCEffectHandler.registerFXServer();
////        CHAOS_IMPLOSION = BCEffectHandler.registerFXServer();
////        PORTAL = BCEffectHandler.registerFXServer();
////        DRAGON_HEART = BCEffectHandler.registerFXServer();
////        AXE_SELECTION = BCEffectHandler.registerFXServer();
////        SOUL_EXTRACTION = BCEffectHandler.registerFXServer();
////        ARROW_SHOCKWAVE = BCEffectHandler.registerFXServer();
////        CUSTOM = BCEffectHandler.registerFXServer();
////        FLAME = BCEffectHandler.registerFXServer();
//    }
}
