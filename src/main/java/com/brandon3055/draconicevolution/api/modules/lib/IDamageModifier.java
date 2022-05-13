package com.brandon3055.draconicevolution.api.modules.lib;

import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.Map;

/**
 * Created by brandon3055 on 8/4/21
 */
public interface IDamageModifier {

    EffectType getType();

    void addInformation(Map<Component, Component> map, @Nullable ModuleContext context, boolean stack);

    /**
     * @return A loopable sound that is played during projectile charging and when charged.
     */
    SoundEvent chargeSound();

    default float chargeSoundPitch(float charge) {
        return 0.5F + (charge * 0.5F);
    }

    default float chargeSoundVolume(float charge) {
        return 0.1F + (charge * 1.9F);
    }

    /**
     * @return the sound that is played when a projectile is fired.
     */
    SoundEvent fireSound();

    /**
     * @return the primary colour of this effect.
     */
    int effectColour();

    /**
     * Use this method to do the actual damage as well as play any sounds or create any visual effects.
     * For effects keep in mind this is called server side only so packets go Brrr!
     * @param world The world.
     * @param pos The position of the damage source or the entity being damaged.
     * @param traceResult The trace result for the block or entity that was hit.
     * @param source The entity responsible for causing the damage.
     * @param baseDamage The damage that would normally be applied without this effect.
     * @param secondaryCharge The charge of the secondary effect (range 0 -> 1).
     * @param isProjectile Will be true if damage is caused by a projectile weapon.
     */
    void doDamageAndEffects(Level world, Vec3 pos, @Nullable HitResult traceResult, @Nullable LivingEntity source, float baseDamage, float secondaryCharge, boolean isProjectile);

    /**
     * This controls how the projectile renders during the draw / charge stage and while in flight.
     * Can also have other effects but that's up to the implementor.
     * For example LIGHTNING on the staff causes projectiles to travel at extreme speed for almost instantaneous travel to target.
     * */
    enum EffectType {
        FIRE,
        LIGHTNING,
        /** Special type for the staff only */
        STAFF_BEAM,
        GENERIC
    }
}
