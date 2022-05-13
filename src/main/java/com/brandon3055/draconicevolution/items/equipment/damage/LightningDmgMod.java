package com.brandon3055.draconicevolution.items.equipment.damage;

import com.brandon3055.draconicevolution.api.modules.lib.IDamageModifier;
import com.brandon3055.draconicevolution.api.modules.lib.ModuleContext;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.Map;

/**
 * Created by brandon3055 on 11/4/21
 */
public class LightningDmgMod implements IDamageModifier {

    @Override
    public EffectType getType() {
        return EffectType.LIGHTNING;
    }

    @Override
    public void addInformation(Map<Component, Component> map, @Nullable ModuleContext context, boolean stack) {

    }

    @Override
    public SoundEvent chargeSound() {
        return null;
    }

    @Override
    public SoundEvent fireSound() {
        return null;
    }

    @Override
    public int effectColour() {
        return 0;
    }

    @Override
    public void doDamageAndEffects(Level world, Vec3 pos, @Nullable HitResult traceResult, LivingEntity source, float baseDamage, float secondaryCharge, boolean isProjectile) {

    }
}
