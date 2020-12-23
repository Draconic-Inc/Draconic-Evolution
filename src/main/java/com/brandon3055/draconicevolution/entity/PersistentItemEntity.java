package com.brandon3055.draconicevolution.entity;

import com.brandon3055.draconicevolution.init.DEContent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.IPacket;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

/**
 * Created by brandon3055 on 23/12/20
 */
public class PersistentItemEntity extends ItemEntity {

    public PersistentItemEntity(EntityType<? extends ItemEntity> type, World world) {
        super(type, world);
    }

    public PersistentItemEntity(World world, double x, double y, double z) {
        super(DEContent.persistentItem, world);
        this.setPosition(x, y, z);
    }

    public PersistentItemEntity(World world, double x, double y, double z, ItemStack stack) {
        super(DEContent.persistentItem, world);
        this.setPosition(x, y, z);
        this.setItem(stack);
    }

    public PersistentItemEntity(World world, Entity location, ItemStack itemstack) {
        super(DEContent.persistentItem, world);
        this.setItem(itemstack);
        this.setPosition(location.getPosX(), location.getPosY(), location.getPosZ());
        this.setMotion(location.getMotion());
        if (location instanceof ItemEntity){
            this.setPickupDelay(((ItemEntity)location).pickupDelay);
        }else {
            this.setDefaultPickupDelay();
        }
    }

    @Override
    public void tick() {
        super.tick();
        if (age >= 0) age = -6000;
    }

    @Override
    public boolean isInvulnerableTo(DamageSource source) {
        return source != DamageSource.OUT_OF_WORLD;
    }

    @Override
    public IPacket<?> createSpawnPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }
}
