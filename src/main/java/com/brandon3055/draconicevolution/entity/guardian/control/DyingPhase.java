package com.brandon3055.draconicevolution.entity.guardian.control;

import com.brandon3055.draconicevolution.entity.guardian.DraconicGuardianEntity;
import com.brandon3055.draconicevolution.entity.guardian.GuardianFightManager;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.math.vector.Vector3d;

import javax.annotation.Nullable;

public class DyingPhase extends Phase {
   private Vector3d targetLocation;
   private int time;

   public DyingPhase(DraconicGuardianEntity guardisn) {
      super(guardisn);
   }

   public void clientTick() {
      if (this.time++ % 10 == 0) {
         float f = (this.guardian.getRandom().nextFloat() - 0.5F) * 8.0F;
         float f1 = (this.guardian.getRandom().nextFloat() - 0.5F) * 4.0F;
         float f2 = (this.guardian.getRandom().nextFloat() - 0.5F) * 8.0F;
         this.guardian.level.addParticle(ParticleTypes.EXPLOSION_EMITTER, this.guardian.getX() + (double)f, this.guardian.getY() + 2.0D + (double)f1, this.guardian.getZ() + (double)f2, 0.0D, 0.0D, 0.0D);
      }
   }

   public void serverTick() {
      ++this.time;
      if (this.targetLocation == null) {
         GuardianFightManager manager = guardian.getFightManager();
         if (manager != null) {
            targetLocation = Vector3d.atBottomCenterOf(manager.getArenaOrigin().above(15));
         } else {
            this.targetLocation = Vector3d.atBottomCenterOf(guardian.blockPosition());
         }
      }

      double d0 = this.targetLocation.distanceToSqr(this.guardian.getX(), this.guardian.getY(), this.guardian.getZ());
      if (!(d0 < 100.0D) && !(d0 > 22500.0D) && !this.guardian.horizontalCollision && !this.guardian.verticalCollision) {
         this.guardian.setHealth(1.0F);
      } else {
         this.guardian.setHealth(0.0F);
      }
   }

   public void initPhase() {
      this.targetLocation = null;
      this.time = 0;
   }

   @Override
   public boolean highVerticalAgility() {
      return true;
   }

   @Override
   public float getMaxRiseOrFall() {
      return 3.0F;
   }

   @Nullable
   public Vector3d getTargetLocation() {
      return this.targetLocation;
   }

   public PhaseType<DyingPhase> getType() {
      return PhaseType.DYING;
   }
}
