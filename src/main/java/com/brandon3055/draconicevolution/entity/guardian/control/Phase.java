package com.brandon3055.draconicevolution.entity.guardian.control;

import com.brandon3055.draconicevolution.entity.GuardianCrystalEntity;
import com.brandon3055.draconicevolution.entity.guardian.DraconicGuardianEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EnderCrystalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;

import javax.annotation.Nullable;

public abstract class Phase implements IPhase {
   protected final DraconicGuardianEntity guardian;

   public Phase(DraconicGuardianEntity guardisn) {
      this.guardian = guardisn;
   }

   @Override
   public boolean getIsStationary() {
      return false;
   }

   @Override
   public void clientTick() {
   }

   @Override
   public void serverTick() {
   }

   @Override
   public void onCrystalDestroyed(GuardianCrystalEntity crystal, BlockPos pos, DamageSource dmgSrc, @Nullable PlayerEntity plyr) {
   }

   @Override
   public void initPhase() {
   }

   @Override
   public void removeAreaEffect() {
   }

   @Override
   public float getMaxRiseOrFall() {
      return 0.6F;
   }

   @Override
   @Nullable
   public Vector3d getTargetLocation() {
      return null;
   }

   @Override
   public float onAttacked(DamageSource source, float damage) {
      return damage;
   }

   @Override
   public float getYawFactor() {
      float f = MathHelper.sqrt(Entity.horizontalMag(this.guardian.getMotion())) + 1.0F;
      float f1 = Math.min(f, 40.0F);
      return 0.7F / f1 / f;
   }
}
