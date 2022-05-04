package com.brandon3055.draconicevolution.items.equipment.damage;

import codechicken.lib.data.MCDataInput;
import codechicken.lib.vec.Vector3;
import com.brandon3055.draconicevolution.api.modules.lib.IDamageModifier;
import com.brandon3055.draconicevolution.api.modules.lib.ModuleContext;
import com.brandon3055.draconicevolution.client.render.effect.StaffBeamEffect;
import com.brandon3055.draconicevolution.handlers.DESounds;
import com.brandon3055.draconicevolution.network.DraconicNetwork;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SimpleSound;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

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
    public void addInformation(Map<ITextComponent, ITextComponent> map, @Nullable ModuleContext context, boolean stack) {

    }

    @Override
    public SoundEvent chargeSound() {
        return DESounds.staffChargeFire;
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
    public void doDamageAndEffects(World world, Vector3d pos, @Nullable RayTraceResult traceResult, LivingEntity source, float baseDamage, float secondaryCharge, boolean isProjectile) {
        if (source == null) return;
        if (traceResult instanceof EntityRayTraceResult) {
            pos = pos.add(0, ((EntityRayTraceResult) traceResult).getEntity().getBbHeight()/2, 0);
        } else if (traceResult != null) {
            pos = traceResult.getLocation();
        }
        Vector3d finalPos = pos;
        DraconicNetwork.sendStaffEffect(source, 0, e -> e.writeVector(new Vector3(finalPos)));
    }

    @OnlyIn(Dist.CLIENT)
    public static void handleEffect(LivingEntity source, MCDataInput data) {
        Vector3 pos = data.readVector();
        ClientWorld world = (ClientWorld) source.level;
        Minecraft mc = Minecraft.getInstance();
        mc.getSoundManager().play(new SimpleSound(DESounds.staffHitDefault, SoundCategory.PLAYERS, 10, 1, pos.pos()));
        mc.particleEngine.add(new StaffBeamEffect(world, source, pos));
    }
}
