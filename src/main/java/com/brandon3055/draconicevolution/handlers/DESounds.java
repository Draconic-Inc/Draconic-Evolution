package com.brandon3055.draconicevolution.handlers;

import com.brandon3055.draconicevolution.DraconicEvolution;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import static com.brandon3055.draconicevolution.DraconicEvolution.MODID;

/**
 * Created by brandon3055 on 25/3/2016.
 * This stores all sound events for Draconic Evolution
 */
public class DESounds {
    private static final DeferredRegister<SoundEvent> SOUNDS = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, MODID);

    public static void init() {
        IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();
        SOUNDS.register(eventBus);
    }

//    @ObjectHolder("shield_up")
//    public static SoundEvent shieldUp;
//    @ObjectHolder("bow_second_charge")
//    public static SoundEvent bowSecondCharge;
//    @ObjectHolder("bow_charge_shot")
//    public static SoundEvent bowChargeShot;

    public static final RegistryObject<SoundEvent> ENERGY_BOLT              = SOUNDS.register("energy_bolt",                () -> SoundEvent.createFixedRangeEvent(new ResourceLocation(DraconicEvolution.MODID, "energy_bolt"), 16F));
    public static final RegistryObject<SoundEvent> FUSION_COMPLETE          = SOUNDS.register("fusion_complete",            () -> SoundEvent.createFixedRangeEvent(new ResourceLocation(DraconicEvolution.MODID, "fusion_complete"), 16F));
    public static final RegistryObject<SoundEvent> FUSION_ROTATION          = SOUNDS.register("fusion_rotation",            () -> SoundEvent.createFixedRangeEvent(new ResourceLocation(DraconicEvolution.MODID, "fusion_rotation"), 16F));
    public static final RegistryObject<SoundEvent> CHARGE                   = SOUNDS.register("charge",                     () -> SoundEvent.createFixedRangeEvent(new ResourceLocation(DraconicEvolution.MODID, "charge"), 16F));
    public static final RegistryObject<SoundEvent> DISCHARGE                = SOUNDS.register("discharge",                  () -> SoundEvent.createFixedRangeEvent(new ResourceLocation(DraconicEvolution.MODID, "discharge"), 16F));
    public static final RegistryObject<SoundEvent> BOOM                     = SOUNDS.register("boom",                       () -> SoundEvent.createFixedRangeEvent(new ResourceLocation(DraconicEvolution.MODID, "boom"), 16F));
    public static final RegistryObject<SoundEvent> BEAM                     = SOUNDS.register("beam",                       () -> SoundEvent.createFixedRangeEvent(new ResourceLocation(DraconicEvolution.MODID, "beam"), 16F));
    public static final RegistryObject<SoundEvent> PORTAL                   = SOUNDS.register("portal",                     () -> SoundEvent.createFixedRangeEvent(new ResourceLocation(DraconicEvolution.MODID, "portal"), 16F));
    public static final RegistryObject<SoundEvent> FUSION_EXPLOSION         = SOUNDS.register("fusion_explosion",           () -> SoundEvent.createFixedRangeEvent(new ResourceLocation(DraconicEvolution.MODID, "fusion_explosion"), 16F));
    public static final RegistryObject<SoundEvent> CHAOS_CHAMBER_AMBIENT    = SOUNDS.register("chaos_chamber_ambient",      () -> SoundEvent.createFixedRangeEvent(new ResourceLocation(DraconicEvolution.MODID, "chaos_chamber_ambient"), 16F));
    public static final RegistryObject<SoundEvent> CORE_SOUND               = SOUNDS.register("core_sound",                 () -> SoundEvent.createFixedRangeEvent(new ResourceLocation(DraconicEvolution.MODID, "core_sound"), 16F));
    public static final RegistryObject<SoundEvent> SHIELD_STRIKE            = SOUNDS.register("shield_strike",              () -> SoundEvent.createFixedRangeEvent(new ResourceLocation(DraconicEvolution.MODID, "shield_strike"), 16F));
    public static final RegistryObject<SoundEvent> ELECTRIC_BUZZ            = SOUNDS.register("electric_buzz",              () -> SoundEvent.createFixedRangeEvent(new ResourceLocation(DraconicEvolution.MODID, "electric_buzz"), 16F));
    public static final RegistryObject<SoundEvent> SUN_DIAL_EFFECT          = SOUNDS.register("sun_dial_effect",            () -> SoundEvent.createFixedRangeEvent(new ResourceLocation(DraconicEvolution.MODID, "sun_dial_effect"), 16F));
    public static final RegistryObject<SoundEvent> GENERATOR1               = SOUNDS.register("generator1",                 () -> SoundEvent.createFixedRangeEvent(new ResourceLocation(DraconicEvolution.MODID, "generator1"), 16F));
    public static final RegistryObject<SoundEvent> GENERATOR2               = SOUNDS.register("generator2",                 () -> SoundEvent.createFixedRangeEvent(new ResourceLocation(DraconicEvolution.MODID, "generator2"), 16F));
    public static final RegistryObject<SoundEvent> GENERATOR3               = SOUNDS.register("generator3",                 () -> SoundEvent.createFixedRangeEvent(new ResourceLocation(DraconicEvolution.MODID, "generator3"), 16F));
    public static final RegistryObject<SoundEvent> BLINK                    = SOUNDS.register("blink",                      () -> SoundEvent.createFixedRangeEvent(new ResourceLocation(DraconicEvolution.MODID, "blink"), 16F));
    public static final RegistryObject<SoundEvent> STAFF_CHARGE_ELECTRIC    = SOUNDS.register("staff_charge_electric",      () -> SoundEvent.createFixedRangeEvent(new ResourceLocation(DraconicEvolution.MODID, "staff_charge_electric"), 16F));
    public static final RegistryObject<SoundEvent> STAFF_CHARGE_FIRE        = SOUNDS.register("staff_charge_fire",          () -> SoundEvent.createFixedRangeEvent(new ResourceLocation(DraconicEvolution.MODID, "staff_charge_fire"), 16F));
    public static final RegistryObject<SoundEvent> STAFF_HIT_DEFAULT        = SOUNDS.register("staff_hit_default",          () -> SoundEvent.createFixedRangeEvent(new ResourceLocation(DraconicEvolution.MODID, "staff_hit_default"), 16F));
    public static final RegistryObject<SoundEvent> STAFF_HIT_ELECTRIC       = SOUNDS.register("staff_hit_electric",         () -> SoundEvent.createFixedRangeEvent(new ResourceLocation(DraconicEvolution.MODID, "staff_hit_electric"), 16F));
    public static final RegistryObject<SoundEvent> CRYSTAL_UNSTABLE         = SOUNDS.register("crystal_unstable",           () -> SoundEvent.createFixedRangeEvent(new ResourceLocation(DraconicEvolution.MODID, "crystal_unstable"), 16F));
    public static final RegistryObject<SoundEvent> CRYSTAL_BEAM             = SOUNDS.register("crystal_beam",               () -> SoundEvent.createFixedRangeEvent(new ResourceLocation(DraconicEvolution.MODID, "crystal_beam"), 16F));
    public static final RegistryObject<SoundEvent> CRYSTAL_DESTABILIZE      = SOUNDS.register("crystal_destabilize",        () -> SoundEvent.createFixedRangeEvent(new ResourceLocation(DraconicEvolution.MODID, "crystal_destabilize"), 16F));
    public static final RegistryObject<SoundEvent> CRYSTAL_RESTORE          = SOUNDS.register("crystal_restore",            () -> SoundEvent.createFixedRangeEvent(new ResourceLocation(DraconicEvolution.MODID, "crystal_restore"), 16F));
    public static final RegistryObject<SoundEvent> GUARDIAN_THONK           = SOUNDS.register("guardian_thonk",             () -> SoundEvent.createFixedRangeEvent(new ResourceLocation(DraconicEvolution.MODID, "guardian_thonk"), 16F));
}
