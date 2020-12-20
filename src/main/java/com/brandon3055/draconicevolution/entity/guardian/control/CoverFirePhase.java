package com.brandon3055.draconicevolution.entity.guardian.control;

import com.brandon3055.draconicevolution.DraconicEvolution;
import com.brandon3055.draconicevolution.entity.guardian.DraconicGuardianEntity;
import com.brandon3055.draconicevolution.entity.GuardianProjectileEntity;
import net.minecraft.pathfinding.Path;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3i;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;

/**
 * For when players are getting just a little to close
 */
public class CoverFirePhase extends Phase {
   private static final Logger LOGGER = DraconicEvolution.LOGGER;
   private Path currentPath;
   private Vector3d targetLocation;
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
      Vector3d vector3d2 = guardian.getLook(1.0F);
      double headX = guardian.dragonPartHead.getPosX() - vector3d2.x * 1.0D;
      double headY = guardian.dragonPartHead.getPosYHeight(0.5D) + 0.5D;
      double headZ = guardian.dragonPartHead.getPosZ() - vector3d2.z * 1.0D;
      Vector3d targetPos = guardian.getPositionVec();
      targetPos = targetPos.add((guardian.getRNG().nextDouble() - 0.5) * 50, (guardian.getRNG().nextDouble() - 0.5) * 50, (guardian.getRNG().nextDouble() - 0.5) * 50);
//      targetPos = targetPos.add(0, 20, 0);
      double targetRelX = targetPos.x - headX;
      double targetRelY = targetPos.y - headY;
      double targetRelZ = targetPos.z - headZ;
      if (!guardian.isSilent()) {
         guardian.world.playEvent(null, 1017, guardian.getPosition(), 0);
      }
      GuardianProjectileEntity projectile = new GuardianProjectileEntity(this.guardian.world, this.guardian, targetRelX, targetRelY, targetRelZ, targetPos, 25, 20);
      projectile.setLocationAndAngles(headX, headY, headZ, 0.0F, 0.0F);
      guardian.world.addEntity(projectile);

      double distanceFromTarget = targetLocation == null ? 0.0D : targetLocation.squareDistanceTo(guardian.getPosX(), guardian.getPosY(), guardian.getPosZ());
      if (currentPath != null && currentPath.isFinished() && distanceFromTarget < 100) {
         guardian.getPhaseManager().setPhase(PhaseType.START).immediateAttack();
         return;
      }

      if (distanceFromTarget < 100){
         updatePathing();
      }
   }

   @Override
   public void initPhase() {
      currentPath = null;
      targetLocation = null;
      tick = 0;
   }

   private void updatePathing() {
      if (this.currentPath == null || this.currentPath.isFinished()) {
         int nearestIndex = this.guardian.initPathPoints(false);
         int endIndex = nearestIndex;
         if (this.guardian.getRNG().nextInt(8) == 0) {
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
            this.currentPath.incrementPathIndex();
         }
      }

      this.navigateToNextPathNode();
   }

   private void navigateToNextPathNode() {
      if (currentPath != null && !currentPath.isFinished()) {
         Vector3i nextPos = currentPath.func_242948_g();
         currentPath.incrementPathIndex();
         double x = nextPos.getX();
         double z = nextPos.getZ();
         double y = (float) nextPos.getY() + guardian.getRNG().nextFloat() * 20.0F;
         targetLocation = new Vector3d(x, y, z);
      }
   }

   @Override
   public double getGuardianSpeed() {
      return 3;
   }

   @Nullable
   public Vector3d getTargetLocation() {
      return this.targetLocation;
   }
}
