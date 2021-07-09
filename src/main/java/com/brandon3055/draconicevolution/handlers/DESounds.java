package com.brandon3055.draconicevolution.handlers;

import com.brandon3055.draconicevolution.DraconicEvolution;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ObjectHolder;

import static net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus.MOD;

/**
 * Created by brandon3055 on 25/3/2016.
 * This stores all sound events for Brandon's Core
 */
@ObjectHolder(DraconicEvolution.MODID)
@Mod.EventBusSubscriber(modid = DraconicEvolution.MODID, bus = MOD)
public class DESounds {
    @ObjectHolder("energy_bolt")
    public static SoundEvent energyBolt;
    @ObjectHolder("fusion_complete")
    public static SoundEvent fusionComplete;
    @ObjectHolder("fusion_rotation")
    public static SoundEvent fusionRotation;
    @ObjectHolder("charge")
    public static SoundEvent charge;
    @ObjectHolder("discharge")
    public static SoundEvent discharge;
    @ObjectHolder("boom")
    public static SoundEvent boom;
    @ObjectHolder("beam")
    public static SoundEvent beam;
    @ObjectHolder("portal")
    public static SoundEvent portal;
    @ObjectHolder("shield_up")
    public static SoundEvent shieldUp;
    @ObjectHolder("fusion_explosion")
    public static SoundEvent fusionExplosion;
    @ObjectHolder("chaos_chamber_ambient")
    public static SoundEvent chaosChamberAmbient;
    @ObjectHolder("core_sound")
    public static SoundEvent coreSound;
    @ObjectHolder("shield_strike")
    public static SoundEvent shieldStrike;
    @ObjectHolder("electric_buzz")
    public static SoundEvent electricBuzz;
    @ObjectHolder("sun_dial_effect")
    public static SoundEvent sunDialEffect;
    @ObjectHolder("generator1")
    public static SoundEvent generator1;
    @ObjectHolder("generator2")
    public static SoundEvent generator2;
    @ObjectHolder("generator3")
    public static SoundEvent generator3;
    @ObjectHolder("blink")
    public static SoundEvent blink;
    @ObjectHolder("staff_charge_electric")
    public static SoundEvent staffChargeElectric;
    @ObjectHolder("staff_charge_fire")
    public static SoundEvent staffChargeFire;
    @ObjectHolder("staff_hit_default")
    public static SoundEvent staffHitDefault;
    @ObjectHolder("staff_hit_electric")
    public static SoundEvent staffHitElectric;
    @ObjectHolder("bow_second_charge")
    public static SoundEvent bowSecondCharge;
    @ObjectHolder("bow_charge_shot")
    public static SoundEvent bowChargeShot;

    @SubscribeEvent
    public static void registerSounds(RegistryEvent.Register<SoundEvent> event) {
        event.getRegistry().register(new SoundEvent(new ResourceLocation(DraconicEvolution.MODID, "energy_bolt")).setRegistryName("energy_bolt"));
        event.getRegistry().register(new SoundEvent(new ResourceLocation(DraconicEvolution.MODID, "fusion_complete")).setRegistryName("fusion_complete"));
        event.getRegistry().register(new SoundEvent(new ResourceLocation(DraconicEvolution.MODID, "fusion_rotation")).setRegistryName("fusion_rotation"));
        event.getRegistry().register(new SoundEvent(new ResourceLocation(DraconicEvolution.MODID, "charge")).setRegistryName("charge"));
        event.getRegistry().register(new SoundEvent(new ResourceLocation(DraconicEvolution.MODID, "discharge")).setRegistryName("discharge"));
        event.getRegistry().register(new SoundEvent(new ResourceLocation(DraconicEvolution.MODID, "boom")).setRegistryName("boom"));
        event.getRegistry().register(new SoundEvent(new ResourceLocation(DraconicEvolution.MODID, "beam")).setRegistryName("beam"));
        event.getRegistry().register(new SoundEvent(new ResourceLocation(DraconicEvolution.MODID, "portal")).setRegistryName("portal"));
        event.getRegistry().register(new SoundEvent(new ResourceLocation(DraconicEvolution.MODID, "shield_up")).setRegistryName("shield_up"));
        event.getRegistry().register(new SoundEvent(new ResourceLocation(DraconicEvolution.MODID, "fusion_explosion")).setRegistryName("fusion_explosion"));
        event.getRegistry().register(new SoundEvent(new ResourceLocation(DraconicEvolution.MODID, "chaos_chamber_ambient")).setRegistryName("chaos_chamber_ambient"));
        event.getRegistry().register(new SoundEvent(new ResourceLocation(DraconicEvolution.MODID, "core_sound")).setRegistryName("core_sound"));
        event.getRegistry().register(new SoundEvent(new ResourceLocation(DraconicEvolution.MODID, "shield_strike")).setRegistryName("shield_strike"));
        event.getRegistry().register(new SoundEvent(new ResourceLocation(DraconicEvolution.MODID, "electric_buzz")).setRegistryName("electric_buzz"));
        event.getRegistry().register(new SoundEvent(new ResourceLocation(DraconicEvolution.MODID, "sun_dial_effect")).setRegistryName("sun_dial_effect"));
        event.getRegistry().register(new SoundEvent(new ResourceLocation(DraconicEvolution.MODID, "generator1")).setRegistryName("generator1"));
        event.getRegistry().register(new SoundEvent(new ResourceLocation(DraconicEvolution.MODID, "generator2")).setRegistryName("generator2"));
        event.getRegistry().register(new SoundEvent(new ResourceLocation(DraconicEvolution.MODID, "generator3")).setRegistryName("generator3"));
        event.getRegistry().register(new SoundEvent(new ResourceLocation(DraconicEvolution.MODID, "blink")).setRegistryName("blink"));
        event.getRegistry().register(new SoundEvent(new ResourceLocation(DraconicEvolution.MODID, "staff_charge_electric")).setRegistryName("staff_charge_electric"));
        event.getRegistry().register(new SoundEvent(new ResourceLocation(DraconicEvolution.MODID, "staff_charge_fire")).setRegistryName("staff_charge_fire"));
        event.getRegistry().register(new SoundEvent(new ResourceLocation(DraconicEvolution.MODID, "staff_hit_default")).setRegistryName("staff_hit_default"));
        event.getRegistry().register(new SoundEvent(new ResourceLocation(DraconicEvolution.MODID, "staff_hit_electric")).setRegistryName("staff_hit_electric"));
        event.getRegistry().register(new SoundEvent(new ResourceLocation(DraconicEvolution.MODID, "bow_second_charge")).setRegistryName("bow_second_charge"));
        event.getRegistry().register(new SoundEvent(new ResourceLocation(DraconicEvolution.MODID, "bow_charge_shot")).setRegistryName("bow_charge_shot"));
    }
}
