package com.brandon3055.draconicevolution.entity.guardian;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntitySize;
import net.minecraft.entity.Pose;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.util.DamageSource;

public class DraconicGuardianPartEntity extends Entity {
   public final DraconicGuardianEntity dragon;
   public final String name;
   private final EntitySize size;

   public DraconicGuardianPartEntity(DraconicGuardianEntity dragon, String name, float width, float height) {
      super(dragon.getType(), dragon.world);
      this.size = EntitySize.flexible(width, height);
      this.recalculateSize();
      this.dragon = dragon;
      this.name = name;
   }

   protected void registerData() {
   }

   protected void readAdditional(CompoundNBT compound) {

   }

   protected void writeAdditional(CompoundNBT compound) {

   }

   public boolean canBeCollidedWith() {
      return true;
   }

   public boolean attackEntityFrom(DamageSource source, float amount) {
      return !this.isInvulnerableTo(source) && this.dragon.attackEntityPartFrom(this, source, amount);
   }

   public boolean isEntityEqual(Entity entityIn) {
      return this == entityIn || this.dragon == entityIn;
   }

   public IPacket<?> createSpawnPacket() {
      throw new UnsupportedOperationException();
   }

   public EntitySize getSize(Pose poseIn) {
      return this.size;
   }
}
