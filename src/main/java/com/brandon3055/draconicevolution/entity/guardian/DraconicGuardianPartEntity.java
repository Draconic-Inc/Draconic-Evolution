package com.brandon3055.draconicevolution.entity.guardian;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.Pose;
import net.neoforged.neoforge.entity.PartEntity;

public class DraconicGuardianPartEntity extends PartEntity<DraconicGuardianEntity> {
   public final DraconicGuardianEntity dragon;
   public final String name;
   private final EntityDimensions size;

   public DraconicGuardianPartEntity(DraconicGuardianEntity dragon, String name, float width, float height) {
      super(dragon);
      this.size = EntityDimensions.scalable(width, height);
      this.refreshDimensions();
      this.dragon = dragon;
      this.name = name;
   }

   @Override
   protected void defineSynchedData() {
   }

   @Override
   protected void readAdditionalSaveData(CompoundTag compound) {

   }

   @Override
   protected void addAdditionalSaveData(CompoundTag compound) {

   }

   @Override
   public boolean isPickable() {
      return true;
   }

   @Override
   public boolean hurt(DamageSource source, float amount) {
      return !this.isInvulnerableTo(source) && this.dragon.attackEntityPartFrom(this, source, amount);
   }

   @Override
   public boolean is(Entity entityIn) {
      return this == entityIn || this.dragon == entityIn;
   }

   @Override
   public Packet<ClientGamePacketListener> getAddEntityPacket() {
      throw new UnsupportedOperationException();
   }

   @Override
   public EntityDimensions getDimensions(Pose poseIn) {
      return this.size;
   }

   @Override
   public boolean shouldBeSaved() {
      return false;
   }
}
