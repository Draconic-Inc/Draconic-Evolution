package com.brandon3055.draconicevolution.entity.guardian.control;

import com.brandon3055.draconicevolution.DraconicEvolution;
import com.brandon3055.draconicevolution.entity.guardian.DraconicGuardianEntity;
import com.brandon3055.draconicevolution.entity.guardian.GuardianFightManager;
import com.brandon3055.draconicevolution.entity.guardian.GuardianProjectileEntity;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.phys.Vec3;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;

/**
 * For when players are getting just a little to close
 */
public class CoverFirePhase extends Phase {
   private static final Logger LOGGER = DraconicEvolution.LOGGER;
   private Path currentPath;
   private Vec3 targetLocation;
   private boolean clockwise;
   private int tick;

   public CoverFirePhase(DraconicGuardianEntity guardisn) {
      super(guardisn);
   }

   public PhaseType<CoverFirePhase> getType() {
      return PhaseType.COVER_FIRE;
   }

   @Override
   public void serverTick() {
      Vec3 vector3d2 = guardian.getViewVector(1.0F);
      double headX = guardian.dragonPartHead.getX() - vector3d2.x * 1.0D;
      double headY = guardian.dragonPartHead.getY(0.5D) + 0.5D;
      double headZ = guardian.dragonPartHead.getZ() - vector3d2.z * 1.0D;
      Vec3 targetPos = guardian.position();
      targetPos = targetPos.add((guardian.getRandom().nextDouble() - 0.5) * 50, (guardian.getRandom().nextDouble() - 0.5) * 50, (guardian.getRandom().nextDouble() - 0.5) * 50);
//      targetPos = targetPos.add(0, 20, 0);
      double targetRelX = targetPos.x - headX;
      double targetRelY = targetPos.y - headY;
      double targetRelZ = targetPos.z - headZ;
      if (!guardian.isSilent()) {
         guardian.level().levelEvent(null, 1017, guardian.blockPosition(), 0);
      }
      GuardianProjectileEntity projectile = new GuardianProjectileEntity(this.guardian.level(), this.guardian, targetRelX, targetRelY, targetRelZ, targetPos, 25, GuardianFightManager.COVER_FIRE_POWER);
      projectile.moveTo(headX, headY, headZ, 0.0F, 0.0F);
      guardian.level().addFreshEntity(projectile);

      double distanceFromTarget = targetLocation == null ? 0.0D : targetLocation.distanceToSqr(guardian.getX(), guardian.getY(), guardian.getZ());
      if (currentPath != null && currentPath.isDone() && distanceFromTarget < 100) {
         guardian.getPhaseManager().setPhase(PhaseType.START).immediateAttack(null);
         return;
      }

      if (distanceFromTarget < 100){
         updatePathing();
      }

      if (tick++ > 60) {
         guardian.getPhaseManager().setPhase(PhaseType.START).immediateAttack(null);
      }
   }

   @Override
   public void initPhase() {
      currentPath = null;
      targetLocation = null;
      tick = 0;
   }

   private void updatePathing() {
      if (this.currentPath == null || this.currentPath.isDone()) {
         int nearestIndex = this.guardian.initPathPoints(false);
         int endIndex = nearestIndex;
         if (this.guardian.getRandom().nextInt(8) == 0) {
            this.clockwise = !this.clockwise;
         }

         if (this.clockwise) {
            endIndex += 12;
         } else {
            endIndex -= 12;
         }

         endIndex = Math.floorMod(endIndex, 24);

         this.currentPath = this.guardian.findPath(nearestIndex, endIndex, null);
         if (this.currentPath != null) {
            this.currentPath.advance();
         }
      }

      this.navigateToNextPathNode();
   }

   private void navigateToNextPathNode() {
      if (currentPath != null && !currentPath.isDone()) {
         Vec3i nextPos = currentPath.getNextNodePos();
         currentPath.advance();
         double x = nextPos.getX();
         double z = nextPos.getZ();
         double y = (float) nextPos.getY() + guardian.getRandom().nextFloat() * 20.0F;
         targetLocation = new Vec3(x, y, z);
      }
   }

   @Override
   public double getGuardianSpeed() {
      return 3;
   }

   @Nullable
   public Vec3 getTargetLocation() {
      return this.targetLocation;
   }
}
