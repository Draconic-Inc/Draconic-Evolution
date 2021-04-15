package com.brandon3055.draconicevolution.entity.guardian.control;

import com.brandon3055.draconicevolution.entity.guardian.DraconicGuardianEntity;
import net.minecraft.entity.EntityPredicate;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.pathfinding.Path;
import net.minecraft.pathfinding.PathPoint;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3i;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.feature.EndPodiumFeature;

import javax.annotation.Nullable;

@Deprecated //Old vanilla phase
public class LandingApproachPhase extends Phase {
   private static final EntityPredicate NEAR_EGG_TARGETING = (new EntityPredicate()).range(128.0D);
   private Path currentPath;
   private Vector3d targetLocation;

   public LandingApproachPhase(DraconicGuardianEntity guardisn) {
      super(guardisn);
   }

   public PhaseType<LandingApproachPhase> getType() {
      return PhaseType.LANDING_APPROACH;
   }

   public void initPhase() {
      this.currentPath = null;
      this.targetLocation = null;
   }

   public void serverTick() {
      double d0 = this.targetLocation == null ? 0.0D : this.targetLocation.distanceToSqr(this.guardian.getX(), this.guardian.getY(), this.guardian.getZ());
      if (d0 < 100.0D || d0 > 22500.0D || this.guardian.horizontalCollision || this.guardian.verticalCollision) {
         this.findNewTarget();
      }

   }

   @Nullable
   public Vector3d getTargetLocation() {
      return this.targetLocation;
   }

   private void findNewTarget() {
      if (this.currentPath == null || this.currentPath.isDone()) {
         int i = this.guardian.initPathPoints(false);
         BlockPos blockpos = this.guardian.level.getHeightmapPos(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, EndPodiumFeature.END_PODIUM_LOCATION);
         PlayerEntity playerentity = this.guardian.level.getNearestPlayer(NEAR_EGG_TARGETING, (double)blockpos.getX(), (double)blockpos.getY(), (double)blockpos.getZ());
         int j;
         if (playerentity != null) {
            Vector3d vector3d = (new Vector3d(playerentity.getX(), 0.0D, playerentity.getZ())).normalize();
            j = this.guardian.getNearestPpIdx(-vector3d.x * 40.0D, 105.0D, -vector3d.z * 40.0D);
         } else {
            j = this.guardian.getNearestPpIdx(40.0D, (double)blockpos.getY(), 0.0D);
         }

         PathPoint pathpoint = new PathPoint(blockpos.getX(), blockpos.getY(), blockpos.getZ());
         this.currentPath = this.guardian.findPath(i, j, pathpoint);
         if (this.currentPath != null) {
            this.currentPath.advance();
         }
      }

      this.navigateToNextPathNode();
      if (this.currentPath != null && this.currentPath.isDone()) {
         this.guardian.getPhaseManager().setPhase(PhaseType.LANDING);
      }

   }

   private void navigateToNextPathNode() {
      if (this.currentPath != null && !this.currentPath.isDone()) {
         Vector3i vector3i = this.currentPath.getNextNodePos();
         this.currentPath.advance();
         double d0 = (double)vector3i.getX();
         double d1 = (double)vector3i.getZ();

         double d2;
         do {
            d2 = (double)((float)vector3i.getY() + this.guardian.getRandom().nextFloat() * 20.0F);
         } while(d2 < (double)vector3i.getY());

         this.targetLocation = new Vector3d(d0, d2, d1);
      }

   }
}
