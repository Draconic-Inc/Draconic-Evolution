package com.brandon3055.draconicevolution.client;

import com.brandon3055.draconicevolution.helpers.ResourceHelperDE;
import net.minecraft.util.ResourceLocation;

/**
 * Created by brandon3055 on 23/4/2016.
 * A list of all of DE's particles
 */
public class DEParticles {
    public static final ResourceLocation VANILLA_SHEET = new ResourceLocation("textures/particle/particles.png");
    public static final ResourceLocation DE_SHEET = ResourceHelperDE.getResource("textures/particle/particles.png");
    public static final ResourceLocation CUSTOM_SHEET = ResourceHelperDE.getResource("textures/particle/particle_generator.png");

    public static int ENERGY_PARTICLE;
    public static int ENERGY_CORE_FX;
    public static int LINE_INDICATOR;
    public static int INFUSER;
    public static int GUARDIAN_PROJECTILE;
    public static int CHAOS_IMPLOSION;
    public static int PORTAL;
    public static int DRAGON_HEART;
    public static int AXE_SELECTION;
    public static int SOUL_EXTRACTION;
    public static int ARROW_SHOCKWAVE;
    public static int CUSTOM;
    public static int FLAME;

    public static void registerClient() {
//        ENERGY_PARTICLE = BCEffectHandler.registerFX(DE_SHEET, new ParticleEnergy.Factory());
//        ENERGY_CORE_FX = BCEffectHandler.registerFX(DE_SHEET, new ParticleEnergyCoreFX.Factory());
//        LINE_INDICATOR = BCEffectHandler.registerFX(DE_SHEET, new ParticleLineIndicator.Factory());
//        INFUSER = BCEffectHandler.registerFX(DE_SHEET, new ParticleInfuser.Factory());
//        GUARDIAN_PROJECTILE = BCEffectHandler.registerFX(DE_SHEET, new ParticleGuardianProjectile.Factory());
//        CHAOS_IMPLOSION = BCEffectHandler.registerFX(DE_SHEET, new ParticleChaosImplosion.Factory());
//        PORTAL = BCEffectHandler.registerFX(DE_SHEET, new ParticlePortal.Factory());
//        DRAGON_HEART = BCEffectHandler.registerFX(DE_SHEET, new ParticleDragonHeart.Factory());
//        AXE_SELECTION = BCEffectHandler.registerFX(new ResourceLocation("textures/items/diamond_axe.png"), new ParticleAxeSelection.Factory());
//        SOUL_EXTRACTION = BCEffectHandler.registerFX(DE_SHEET, new ParticleSoulExtraction.Factory());
//        ARROW_SHOCKWAVE = BCEffectHandler.registerFX(DE_SHEET, new ParticleArrowShockwave.Factory());
//        CUSTOM = BCEffectHandler.registerFX(CUSTOM_SHEET, new ParticleCustom.Factory());
//        FLAME = BCEffectHandler.registerFX(VANILLA_SHEET, new ParticleFlame.Factory());
    }

    public static void registerServer() {
//        ENERGY_PARTICLE = BCEffectHandler.registerFXServer();
//        ENERGY_CORE_FX = BCEffectHandler.registerFXServer();
//        LINE_INDICATOR = BCEffectHandler.registerFXServer();
//        INFUSER = BCEffectHandler.registerFXServer();
//        GUARDIAN_PROJECTILE = BCEffectHandler.registerFXServer();
//        CHAOS_IMPLOSION = BCEffectHandler.registerFXServer();
//        PORTAL = BCEffectHandler.registerFXServer();
//        DRAGON_HEART = BCEffectHandler.registerFXServer();
//        AXE_SELECTION = BCEffectHandler.registerFXServer();
//        SOUL_EXTRACTION = BCEffectHandler.registerFXServer();
//        ARROW_SHOCKWAVE = BCEffectHandler.registerFXServer();
//        CUSTOM = BCEffectHandler.registerFXServer();
//        FLAME = BCEffectHandler.registerFXServer();
    }
}
