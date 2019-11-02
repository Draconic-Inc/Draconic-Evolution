package com.brandon3055.draconicevolution.lib;

import codechicken.lib.reflect.ObfMapping;
import codechicken.lib.reflect.ReflectionManager;
import com.brandon3055.brandonscore.lib.Vec3D;
import com.brandon3055.draconicevolution.DraconicEvolution;
import com.brandon3055.draconicevolution.helpers.ResourceHelperDE;
import com.brandon3055.draconicevolution.network.PacketPlaySound;
import net.minecraft.init.Bootstrap;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.NetworkRegistry;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by brandon3055 on 25/3/2016.
 * This stores all sound events for Brandon's Core
 */
public class DESoundHandler {
    public static final Map<String, SoundEvent> SOUND_EVENTS = new HashMap<String, SoundEvent>();

    public static final SoundEvent energyBolt;
    public static final SoundEvent fusionComplete;
    public static final SoundEvent fusionRotation;
    public static final SoundEvent charge;
    public static final SoundEvent discharge;
    public static final SoundEvent boom;
    public static final SoundEvent beam;
    public static final SoundEvent portal;
    public static final SoundEvent shieldUp;
    public static final SoundEvent fusionExplosion;
    public static final SoundEvent chaosChamberAmbient;
    public static final SoundEvent coreSound;
    public static final SoundEvent shieldStrike;
    public static final SoundEvent electricBuzz;
    public static final SoundEvent sunDialEffect;
    public static final SoundEvent generator1;
    public static final SoundEvent generator2;
    public static final SoundEvent generator3;
    public static ObfMapping soundNameMapping = new ObfMapping("net/minecraft/util/SoundEvent", "field_187506_b");

    static {
        if (!Bootstrap.isRegistered()) {
            throw new RuntimeException("Accessed Sounds before Bootstrap!");
        }
        else {
            energyBolt = getRegisteredSoundEvent("draconicevolution:energy_bolt");
            fusionComplete = getRegisteredSoundEvent("draconicevolution:fusion_complete");
            fusionRotation = getRegisteredSoundEvent("draconicevolution:fusion_rotation");

            charge = getRegisteredSoundEvent("draconicevolution:charge");
            discharge = getRegisteredSoundEvent("draconicevolution:discharge");
            boom = getRegisteredSoundEvent("draconicevolution:boom");
            beam = getRegisteredSoundEvent("draconicevolution:beam");
            portal = getRegisteredSoundEvent("draconicevolution:portal");
            shieldUp = getRegisteredSoundEvent("draconicevolution:shield_up");
            fusionExplosion = getRegisteredSoundEvent("draconicevolution:fusion_explosion");
            chaosChamberAmbient = getRegisteredSoundEvent("draconicevolution:chaos_chamber_ambient");
            coreSound = getRegisteredSoundEvent("draconicevolution:core_sound");
            shieldStrike = getRegisteredSoundEvent("draconicevolution:shield_strike");
            electricBuzz = getRegisteredSoundEvent("draconicevolution:electric_buzz");
            sunDialEffect = getRegisteredSoundEvent("draconicevolution:sun_dial_effect");
            generator1 = getRegisteredSoundEvent("draconicevolution:generator1");
            generator2 = getRegisteredSoundEvent("draconicevolution:generator2");
            generator3 = getRegisteredSoundEvent("draconicevolution:generator3");
        }
    }

    private static SoundEvent getRegisteredSoundEvent(String id) {
        SoundEvent soundevent = new SoundEvent(new ResourceLocation(id));

        if (soundevent == null) {
            throw new IllegalStateException("Invalid Sound requested: " + id);
        }
        else {
            SOUND_EVENTS.put(id, soundevent);
            return soundevent;
        }
    }

    public static SoundEvent getSound(String id) {
        if (SOUND_EVENTS.containsKey(id)) {
            return SOUND_EVENTS.get(id);
        }
        else if (SoundEvent.REGISTRY.containsKey(ResourceHelperDE.getResourceRAW(id))) {
            return SoundEvent.REGISTRY.getObject(ResourceHelperDE.getResourceRAW(id));
        }
        else {
            return null;
        }
    }

    public static void playSoundFromServer(World world, Vec3D pos, SoundEvent soundIn, SoundCategory category, float volume, float pitch, boolean distanceDelay, double range) {
        playSoundFromServer(world, pos.x, pos.y, pos.z, soundIn, category, volume, pitch, distanceDelay, range);
    }

    public static void playSoundFromServer(World world, double x, double y, double z, SoundEvent soundIn, SoundCategory category, float volume, float pitch, boolean distanceDelay, double range) {
        ResourceLocation soundName = ReflectionManager.getField(soundNameMapping, soundIn, ResourceLocation.class);

        if (soundName != null) {
            String soundId = soundName.toString();
            String categoryName = category.getName();
            DraconicEvolution.network.sendToAllAround(new PacketPlaySound(x, y, z, soundId, categoryName, volume, pitch, distanceDelay), new NetworkRegistry.TargetPoint(world.provider.getDimension(), x, y, z, range));
        }
    }
}
