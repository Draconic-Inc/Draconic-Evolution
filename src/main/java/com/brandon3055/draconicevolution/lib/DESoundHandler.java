package com.brandon3055.draconicevolution.lib;

import com.brandon3055.draconicevolution.DraconicEvolution;
import com.brandon3055.draconicevolution.helpers.ResourceHelperDE;
import com.brandon3055.draconicevolution.network.PacketPlaySound;
import net.minecraft.init.Bootstrap;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

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

    static {
        if (!Bootstrap.isRegistered()) {
            throw new RuntimeException("Accessed Sounds before Bootstrap!");
        } else {
            energyBolt = getRegisteredSoundEvent("draconicevolution:energyBolt");
            fusionComplete = getRegisteredSoundEvent("draconicevolution:fusionComplete");
            fusionRotation = getRegisteredSoundEvent("draconicevolution:fusionRotation");

            charge = getRegisteredSoundEvent("draconicevolution:charge");
            discharge = getRegisteredSoundEvent("draconicevolution:discharge");
            boom = getRegisteredSoundEvent("draconicevolution:boom");
            beam = getRegisteredSoundEvent("draconicevolution:beam");
            portal = getRegisteredSoundEvent("draconicevolution:portal");
            shieldUp = getRegisteredSoundEvent("draconicevolution:shieldUp");
            fusionExplosion = getRegisteredSoundEvent("draconicevolution:fusionExplosion");
            chaosChamberAmbient = getRegisteredSoundEvent("draconicevolution:chaosChamberAmbient");
            coreSound = getRegisteredSoundEvent("draconicevolution:coreSound");
            shieldStrike = getRegisteredSoundEvent("draconicevolution:shieldStrike");
        }
    }


    private static SoundEvent getRegisteredSoundEvent(String id) {
        SoundEvent soundevent = new SoundEvent(new ResourceLocation(id));

        if (soundevent == null) {
            throw new IllegalStateException("Invalid Sound requested: " + id);
        } else {
            SOUND_EVENTS.put(id, soundevent);
            return soundevent;
        }
    }

    public static SoundEvent getSound(String id) {
        if (SOUND_EVENTS.containsKey(id)){
            return SOUND_EVENTS.get(id);
        }
        else if (SoundEvent.REGISTRY.containsKey(ResourceHelperDE.getResourceRAW(id))) {
            return SoundEvent.REGISTRY.getObject(ResourceHelperDE.getResourceRAW(id));
        }
        else {
            return null;
        }
    }

    public static void playSoundFromServer(World world, double x, double y, double z, SoundEvent soundIn, SoundCategory category, float volume, float pitch, boolean distanceDelay, double range) {
        Object o = ReflectionHelper.getPrivateValue(SoundEvent.class, soundIn, "field_187506_b", "soundName");

        if (o instanceof ResourceLocation){
            o = o.toString();
        }

        if (o instanceof String) {
            String soundId = (String) o;
            String categoryName = category.getName();
            DraconicEvolution.network.sendToAllAround(new PacketPlaySound(x, y, z, soundId, categoryName, volume, pitch, distanceDelay), new NetworkRegistry.TargetPoint(world.provider.getDimension(), x, y, z, range));
        }
    }
}
