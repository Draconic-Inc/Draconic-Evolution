package com.brandon3055.draconicevolution.items.equipment.damage;

import codechicken.lib.data.MCDataInput;
import codechicken.lib.vec.Vector3;
import com.brandon3055.draconicevolution.api.modules.lib.IDamageModifier;
import com.brandon3055.draconicevolution.api.modules.lib.ModuleContext;
import com.brandon3055.draconicevolution.client.render.effect.StaffBeamEffect;
import com.brandon3055.draconicevolution.handlers.DESounds;
import com.brandon3055.draconicevolution.network.DraconicNetwork;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.Map;

/**
 * Created by brandon3055 on 11/4/21
 * This is a "special case" damage modifier that does not actually belong to any module.
 * It controls the default damage type of the staff.
 */
public class DefaultStaffDmgMod implements IDamageModifier {
    @Override
    public EffectType getType() {
        return EffectType.STAFF_BEAM;
    }

    @Override
    public void addInformation(Map<Component, Component> map, @Nullable ModuleContext context, boolean stack) {

    }

    @Override
    public SoundEvent chargeSound() {
        return DESounds.STAFF_CHARGE_FIRE.get();
    }

    @Override
    public SoundEvent fireSound() {
        return SoundEvents.BLAZE_SHOOT;
    }

    @Override
    public int effectColour() {
        return 0;
    }

    @Override
    public void doDamageAndEffects(Level world, Vec3 pos, @Nullable HitResult traceResult, LivingEntity source, float baseDamage, float secondaryCharge, boolean isProjectile) {
        if (source == null) return;
        if (traceResult instanceof EntityHitResult) {
            pos = pos.add(0, ((EntityHitResult) traceResult).getEntity().getBbHeight()/2, 0);
        } else if (traceResult != null) {
            pos = traceResult.getLocation();
        }
        Vec3 finalPos = pos;
        DraconicNetwork.sendStaffEffect(source, 0, e -> e.writeVector(new Vector3(finalPos)));
    }

    @OnlyIn(Dist.CLIENT)
    public static void handleEffect(LivingEntity source, MCDataInput data) {
        Vector3 pos = data.readVector();
        ClientLevel world = (ClientLevel) source.level();
        Minecraft mc = Minecraft.getInstance();
        mc.getSoundManager().play(new SimpleSoundInstance(DESounds.STAFF_HIT_DEFAULT.get(), SoundSource.PLAYERS, 10, 1, world.random, pos.pos()));
        mc.particleEngine.add(new StaffBeamEffect(world, source, pos));
    }
}
