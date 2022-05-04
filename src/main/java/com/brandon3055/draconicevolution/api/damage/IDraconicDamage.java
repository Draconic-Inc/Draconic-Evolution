package com.brandon3055.draconicevolution.api.damage;

import com.brandon3055.brandonscore.api.TechLevel;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;

import javax.annotation.Nullable;

/**
 * Created by brandon3055 on 18/7/21
 * Can be implemented on a {@link net.minecraft.util.DamageSource} or any Melee weapon.
 */
public interface IDraconicDamage {

    /**
     * Chaotic damage source can break though the shields on chaos guardian crystals.
     * As of writing this i am also considering adding a mechanic that would give draconic weapons increased effectiveness
     * against draconic shields. The idea being you would deal significantly more damage if your weapon is a higher level than the shield you are attacking.
     * And maybe slightly more if your weapon is the same level as your target.
     *
     * @param stack The item stack (This will be null when implemented on a {@link DamageSource})
     * @return The draconic tech level for this damage source.
     */
    TechLevel getTechLevel(ItemStack stack);

    /**
     * This is the recommended way to check the draconic tech level of a damage source.
     *
     * @param source The damage source being tested.
     * @return the draconic damage level op null if this is not an {@link IDraconicDamage} source.
     */
    @Nullable
    static TechLevel getDamageLevel(DamageSource source) {
        if (source instanceof IDraconicDamage) {
            return ((IDraconicDamage) source).getTechLevel(null);
        } else if (source.getEntity() instanceof LivingEntity) {
            ItemStack stack = ((LivingEntity) source.getEntity()).getMainHandItem();
            if (stack.getItem() instanceof IDraconicDamage) {
                return ((IDraconicDamage) stack.getItem()).getTechLevel(stack);
            }
        }
        return null;
    }
}
