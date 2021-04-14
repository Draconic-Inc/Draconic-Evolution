package com.brandon3055.draconicevolution.items.equipment.damage;

import com.brandon3055.draconicevolution.api.modules.lib.IDamageModifier;
import com.brandon3055.draconicevolution.api.modules.lib.ModuleContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.Map;

/**
 * Created by brandon3055 on 11/4/21
 */
public class LightningDmgMod implements IDamageModifier {
    @Override
    public void addInformation(Map<ITextComponent, ITextComponent> map, @Nullable ModuleContext context, boolean stack) {

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
    public void doDamageAndEffects(World world, Vector3d pos, @Nullable LivingEntity entityHit, LivingEntity source, float baseDamage, float secondaryCharge, boolean isProjectile) {

    }
}
