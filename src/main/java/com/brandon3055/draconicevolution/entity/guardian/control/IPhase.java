package com.brandon3055.draconicevolution.entity.guardian.control;

import com.brandon3055.draconicevolution.entity.GuardianCrystalEntity;
import net.minecraft.entity.item.EnderCrystalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;

import javax.annotation.Nullable;

public interface IPhase {
   boolean getIsStationary();

   void clientTick();

   void serverTick();

   void onCrystalDestroyed(GuardianCrystalEntity crystal, BlockPos pos, DamageSource dmgSrc, @Nullable PlayerEntity plyr);

   void initPhase();

   void removeAreaEffect();

   float getMaxRiseOrFall();

   float getYawFactor();

   PhaseType<? extends IPhase> getType();

   @Nullable
   Vector3d getTargetLocation();

   float onAttacked(DamageSource source, float damage);

   default double getGuardianSpeed() {
      return 1.5;
   }

   default boolean highVerticalAgility() {
      return false;
   }
}
