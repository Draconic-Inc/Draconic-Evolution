package com.brandon3055.draconicevolution.entity.guardian;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntitySize;
import net.minecraft.entity.Pose;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.util.DamageSource;
import net.minecraftforge.entity.PartEntity;

public class DraconicGuardianPartEntity extends PartEntity<DraconicGuardianEntity> {
   public final DraconicGuardianEntity dragon;
   public final String name;
   private final EntitySize size;

   public DraconicGuardianPartEntity(DraconicGuardianEntity dragon, String name, float width, float height) {
      super(dragon);
      this.size = EntitySize.scalable(width, height);
      this.refreshDimensions();
      this.dragon = dragon;
      this.name = name;
   }

   protected void defineSynchedData() {
   }

   protected void readAdditionalSaveData(CompoundNBT compound) {

   }

   protected void addAdditionalSaveData(CompoundNBT compound) {

   }

   public boolean isPickable() {
      return true;
   }

   public boolean hurt(DamageSource source, float amount) {
      return !this.isInvulnerableTo(source) && this.dragon.attackEntityPartFrom(this, source, amount);
   }

   public boolean is(Entity entityIn) {
      return this == entityIn || this.dragon == entityIn;
   }

   public IPacket<?> getAddEntityPacket() {
      throw new UnsupportedOperationException();
   }

   public EntitySize getDimensions(Pose poseIn) {
      return this.size;
   }
}
