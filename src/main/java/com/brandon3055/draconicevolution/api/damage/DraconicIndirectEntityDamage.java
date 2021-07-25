package com.brandon3055.draconicevolution.api.damage;

import com.brandon3055.brandonscore.api.TechLevel;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.IndirectEntityDamageSource;

import javax.annotation.Nullable;

/**
 * Created by brandon3055 on 18/7/21
 */
public class DraconicIndirectEntityDamage extends IndirectEntityDamageSource implements IDraconicDamage {

    private final TechLevel techLevel;

    public DraconicIndirectEntityDamage(String message, Entity projectile, @Nullable Entity shooter, TechLevel techLevel) {
        super(message, projectile, shooter);
        this.techLevel = techLevel;
    }

    @Override
    public TechLevel getTechLevel(@Nullable ItemStack stack) {
        return techLevel;
    }

    public static DraconicIndirectEntityDamage arrow(AbstractArrowEntity arrowEntity, @Nullable Entity shooter, TechLevel techLevel) {
        return (DraconicIndirectEntityDamage) new DraconicIndirectEntityDamage("arrow", arrowEntity, shooter, techLevel).setProjectile();
    }
}
