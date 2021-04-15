package com.brandon3055.draconicevolution.entity.guardian.control;

import com.brandon3055.draconicevolution.entity.guardian.DraconicGuardianEntity;
import net.minecraft.entity.EntityPredicate;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;

@Deprecated //Old vanilla phase
public class ScanningSittingPhase extends SittingPhase {
   private static final EntityPredicate CHARGE_TARGETING = (new EntityPredicate()).range(150.0D);
   private final EntityPredicate scanTargeting;
   private int scanningTime;

   public ScanningSittingPhase(DraconicGuardianEntity guardisn) {
      super(guardisn);
      this.scanTargeting = (new EntityPredicate()).range(20.0D).selector((p_221114_1_) -> {
         return Math.abs(p_221114_1_.getY() - guardisn.getY()) <= 10.0D;
      });
   }

   public void serverTick() {
      ++this.scanningTime;
      LivingEntity livingentity = this.guardian.level.getNearestPlayer(this.scanTargeting, this.guardian, this.guardian.getX(), this.guardian.getY(), this.guardian.getZ());
      if (livingentity != null) {
         if (this.scanningTime > 25) {
            this.guardian.getPhaseManager().setPhase(PhaseType.SITTING_ATTACKING);
         } else {
            Vector3d vector3d = (new Vector3d(livingentity.getX() - this.guardian.getX(), 0.0D, livingentity.getZ() - this.guardian.getZ())).normalize();
            Vector3d vector3d1 = (new Vector3d((double)MathHelper.sin(this.guardian.yRot * ((float)Math.PI / 180F)), 0.0D, (double)(-MathHelper.cos(this.guardian.yRot * ((float)Math.PI / 180F))))).normalize();
            float f = (float)vector3d1.dot(vector3d);
            float f1 = (float)(Math.acos((double)f) * (double)(180F / (float)Math.PI)) + 0.5F;
            if (f1 < 0.0F || f1 > 10.0F) {
               double d0 = livingentity.getX() - this.guardian.dragonPartHead.getX();
               double d1 = livingentity.getZ() - this.guardian.dragonPartHead.getZ();
               double d2 = MathHelper.clamp(MathHelper.wrapDegrees(180.0D - MathHelper.atan2(d0, d1) * (double)(180F / (float)Math.PI) - (double)this.guardian.yRot), -100.0D, 100.0D);
               this.guardian.yRotA *= 0.8F;
               float f2 = MathHelper.sqrt(d0 * d0 + d1 * d1) + 1.0F;
               float f3 = f2;
               if (f2 > 40.0F) {
                  f2 = 40.0F;
               }

               this.guardian.yRotA = (float)((double)this.guardian.yRotA + d2 * (double)(0.7F / f2 / f3));
               this.guardian.yRot += this.guardian.yRotA;
            }
         }
      } else if (this.scanningTime >= 100) {
         livingentity = this.guardian.level.getNearestPlayer(CHARGE_TARGETING, this.guardian, this.guardian.getX(), this.guardian.getY(), this.guardian.getZ());
         this.guardian.getPhaseManager().setPhase(PhaseType.TAKEOFF);
         if (livingentity != null) {
            this.guardian.getPhaseManager().setPhase(PhaseType.CHARGE_PLAYER);
//            this.guardian.getPhaseManager().getPhase(PhaseType.CHARGING_PLAYER).setTarget(new Vector3d(livingentity.getPosX(), livingentity.getPosY(), livingentity.getPosZ()));
         }
      }

   }

   public void initPhase() {
      this.scanningTime = 0;
   }

   public PhaseType<ScanningSittingPhase> getType() {
      return PhaseType.SITTING_SCANNING;
   }
}
