package com.brandon3055.draconicevolution.lib;

import com.brandon3055.brandonscore.api.TechLevel;
import com.brandon3055.draconicevolution.api.damage.IDraconicDamage;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

import javax.annotation.Nullable;

/**
 * Created by brandon3055 on 20/7/21
 */
public class ProjectileAntiImmunityDamage extends EntityDamageSource implements IDraconicDamage {
    private final Entity owner;
    private final TechLevel techLevel;

    public ProjectileAntiImmunityDamage(String message, Entity entity, @Nullable Entity owner, TechLevel techLevel) {
        super(message, entity);
        this.owner = owner;
        this.techLevel = techLevel;
    }

    @Nullable
    public Entity getDirectEntity() {
        return null;
    }

    @Nullable
    public Entity getEntity() {
        return this.owner;
    }

    public ITextComponent getLocalizedDeathMessage(LivingEntity p_151519_1_) {
        ITextComponent itextcomponent = this.owner == null ? this.entity.getDisplayName() : this.owner.getDisplayName();
        ItemStack itemstack = this.owner instanceof LivingEntity ? ((LivingEntity)this.owner).getMainHandItem() : ItemStack.EMPTY;
        String s = "death.attack." + this.msgId;
        String s1 = s + ".item";
        return !itemstack.isEmpty() && itemstack.hasCustomHoverName() ? new TranslationTextComponent(s1, p_151519_1_.getDisplayName(), itextcomponent, itemstack.getDisplayName()) : new TranslationTextComponent(s, p_151519_1_.getDisplayName(), itextcomponent);
    }

    @Override
    public TechLevel getTechLevel(ItemStack stack) {
        return techLevel;
    }
}