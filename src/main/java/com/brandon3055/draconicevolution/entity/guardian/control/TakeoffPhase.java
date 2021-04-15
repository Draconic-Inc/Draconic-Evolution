package com.brandon3055.draconicevolution.entity.guardian.control;

import com.brandon3055.draconicevolution.entity.guardian.DraconicGuardianEntity;
import net.minecraft.pathfinding.Path;
import net.minecraft.pathfinding.PathPoint;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3i;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.feature.EndPodiumFeature;

import javax.annotation.Nullable;

@Deprecated //Old vanilla phase
public class TakeoffPhase extends Phase {
   private boolean firstTick;
   private Path currentPath;
   private Vector3d targetLocation;

   public TakeoffPhase(DraconicGuardianEntity guardisn) {
      super(guardisn);
   }

   public void serverTick() {
      if (!this.firstTick && this.currentPath != null) {
         BlockPos blockpos = this.guardian.level.getHeightmapPos(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, EndPodiumFeature.END_PODIUM_LOCATION);
         if (!blockpos.closerThan(this.guardian.position(), 10.0D)) {
            this.guardian.getPhaseManager().setPhase(PhaseType.START);
         }
      } else {
         this.firstTick = false;
         this.findNewTarget();
      }

   }

   public void initPhase() {
      this.firstTick = true;
      this.currentPath = null;
      this.targetLocation = null;
   }

   private void findNewTarget() {
      int i = this.guardian.initPathPoints(false);
      Vector3d vector3d = this.guardian.getHeadLookVec(1.0F);
      int j = this.guardian.getNearestPpIdx(-vector3d.x * 40.0D, 105.0D, -vector3d.z * 40.0D);
      if (this.guardian.getFightManager() != null && this.guardian.getFightManager().getNumAliveCrystals() > 0) {
         j = j % 12;
         if (j < 0) {
            j += 12;
         }
      } else {
         j = j - 12;
         j = j & 7;
         j = j + 12;
      }

      this.currentPath = this.guardian.findPath(i, j, (PathPoint)null);
      this.navigateToNextPathNode();
   }

   private void navigateToNextPathNode() {
      if (this.currentPath != null) {
         this.currentPath.advance();
         if (!this.currentPath.isDone()) {
            Vector3i vector3i = this.currentPath.getNextNodePos();
            this.currentPath.advance();

            double d0;
            do {
               d0 = (double)((float)vector3i.getY() + this.guardian.getRandom().nextFloat() * 20.0F);
            } while(d0 < (double)vector3i.getY());

            this.targetLocation = new Vector3d((double)vector3i.getX(), d0, (double)vector3i.getZ());
         }
      }

   }

   @Nullable
   public Vector3d getTargetLocation() {
      return this.targetLocation;
   }

   public PhaseType<TakeoffPhase> getType() {
      return PhaseType.TAKEOFF;
   }
}
