package com.brandon3055.draconicevolution.api.damage;

import com.brandon3055.brandonscore.api.TechLevel;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EntityDamageSource;

import javax.annotation.Nullable;

/**
 * Created by brandon3055 on 18/7/21
 */
public class DraconicDamage extends EntityDamageSource implements IDraconicDamage {

    private final TechLevel techLevel;

    public DraconicDamage(String message, @Nullable Entity entity, TechLevel techLevel) {
        super(message, entity);
        this.techLevel = techLevel;
    }

    @Override
    public TechLevel getTechLevel(@Nullable ItemStack stack) {
        return techLevel;
    }
}
