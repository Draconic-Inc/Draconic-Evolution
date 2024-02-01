//package com.brandon3055.draconicevolution.lib;
//
//import com.brandon3055.brandonscore.api.TechLevel;
//import com.brandon3055.draconicevolution.api.damage.IDraconicDamage;
//import net.minecraft.network.chat.Component;
//import net.minecraft.network.chat.TranslatableComponent;
//import net.minecraft.world.damagesource.EntityDamageSource;
//import net.minecraft.world.entity.Entity;
//import net.minecraft.world.entity.LivingEntity;
//import net.minecraft.world.item.ItemStack;
//
//import javax.annotation.Nullable;
//
///**
// * Created by brandon3055 on 20/7/21
// */
//public class ProjectileAntiImmunityDamage extends EntityDamageSource implements IDraconicDamage {
//    private final Entity owner;
//    private final TechLevel techLevel;
//
//    public ProjectileAntiImmunityDamage(String message, Entity entity, @Nullable Entity owner, TechLevel techLevel) {
//        super(message, entity);
//        this.owner = owner;
//        this.techLevel = techLevel;
//    }
//
//    @Nullable
//    public Entity getDirectEntity() {
//        return null;
//    }
//
//    @Nullable
//    public Entity getEntity() {
//        return this.owner;
//    }
//
//    @Override
//    public Component getLocalizedDeathMessage(LivingEntity p_151519_1_) {
//        Component itextcomponent = this.owner == null ? this.entity.getDisplayName() : this.owner.getDisplayName();
//        ItemStack itemstack = this.owner instanceof LivingEntity ? ((LivingEntity)this.owner).getMainHandItem() : ItemStack.EMPTY;
//        String s = "death.attack." + this.msgId;
//        String s1 = s + ".item";
//        return !itemstack.isEmpty() && itemstack.hasCustomHoverName() ? Component.translatable(s1, p_151519_1_.getDisplayName(), itextcomponent, itemstack.getDisplayName()) : Component.translatable(s, p_151519_1_.getDisplayName(), itextcomponent);
//    }
//
//    @Override
//    public TechLevel getTechLevel(ItemStack stack) {
//        return techLevel;
//    }
//}