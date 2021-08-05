package com.brandon3055.draconicevolution.entity.guardian.control;

import com.brandon3055.draconicevolution.entity.guardian.DraconicGuardianEntity;
import net.minecraft.util.math.vector.Vector3d;

import javax.annotation.Nullable;

public class ApproachPositionPhase extends Phase {
   private Vector3d targetLocation;
   private PhaseType<?> nextPhase;
   private int startDistance = 0;

   public ApproachPositionPhase(DraconicGuardianEntity guardisn) {
      super(guardisn);
   }

   public PhaseType<ApproachPositionPhase> getType() {
      return PhaseType.APPROACH_POSITION;
   }

   public ApproachPositionPhase setTargetLocation(Vector3d targetLocation) {
      this.targetLocation = targetLocation;
      return this;
   }

   public ApproachPositionPhase setNextPhase(PhaseType<?> nextPhase) {
      this.nextPhase = nextPhase;
      return this;
   }

   public void initPhase() {
      targetLocation = null;
      nextPhase = null;
      startDistance = -1;
   }

   public void serverTick() {
      if (targetLocation == null || nextPhase == null) {
         debug("Cancel Approach: (Invalid)");
         guardian.getPhaseManager().setPhase(PhaseType.START);
         return;
      } else if (startDistance == -1) {
         startDistance = (int) Math.max(Math.sqrt(guardian.distanceToSqr(targetLocation)), 32);
      }

      double distanceSqr = targetLocation.distanceToSqr(this.guardian.getX(), this.guardian.getY(), this.guardian.getZ());
      if (distanceSqr > (startDistance + 64) * (startDistance + 64)){
         debug("Cancel Approach: (Moving away from target)");
         guardian.getPhaseManager().setPhase(PhaseType.START);
         return;
      }

      if (distanceSqr < 5*5) {
         debug("Approach Complete");
         guardian.getPhaseManager().setPhase(nextPhase);
      }
   }

   @Nullable
   public Vector3d getTargetLocation() {
      return this.targetLocation;
   }

   @Override
   public double getGuardianSpeed() {
      if (targetLocation == null) {
         return 1;
      }
      double td = guardian.distanceToSqr(targetLocation);
      double distMod = (td > 32 * 32) ? 1 : Math.sqrt(td) / 32;
      return 0.2 + (2 * distMod);
   }

   @Override
   public boolean highVerticalAgility() {
      return true;
   }
}
