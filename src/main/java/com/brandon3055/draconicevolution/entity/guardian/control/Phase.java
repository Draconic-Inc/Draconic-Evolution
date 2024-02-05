package com.brandon3055.draconicevolution.entity.guardian.control;

import codechicken.lib.data.MCDataOutput;
import com.brandon3055.draconicevolution.DraconicEvolution;
import com.brandon3055.draconicevolution.entity.GuardianCrystalEntity;
import com.brandon3055.draconicevolution.entity.guardian.DraconicGuardianEntity;
import com.brandon3055.draconicevolution.network.DraconicNetwork;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.function.Consumer;

public abstract class Phase implements IPhase {
   protected final DraconicGuardianEntity guardian;
   protected RandomSource random = RandomSource.create();

   public Phase(DraconicGuardianEntity guardian) {
      this.guardian = guardian;
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
   public void globalServerTick() {
   }

   @Override
   public void onCrystalAttacked(GuardianCrystalEntity crystal, BlockPos pos, DamageSource dmgSrc, @Nullable Player plyr, float damage, boolean destroyed) {
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
   public Vec3 getTargetLocation() {
      return null;
   }

   @Override
   public float onAttacked(DamageSource source, float damage, float shield, boolean effective) {
      return damage;
   }

   @Override
   public float getYawFactor() {
      float f = (float) this.guardian.getDeltaMovement().horizontalDistance() + 1.0F;
      float f1 = Math.min(f, 40.0F);
      return 0.7F / f1 / f;
   }

   protected void debug(String message) {
      DraconicEvolution.LOGGER.debug(message);
   }

   public boolean isEnded() {
      return guardian.getPhaseManager().getCurrentPhase() != this || !guardian.isAlive();
   }

   @Override
   public void sendPacket(Consumer<MCDataOutput> callBack, int data) {
      DraconicNetwork.sendGuardianPhasePacket(guardian, this, data, callBack);
   }

   public boolean isValidTarget(LivingEntity entity) {
      return entity.isAlive() && entity.level().dimensionType() == guardian.level().dimensionType() && entity.distanceToSqr(guardian) < 300*300;
   }
}
