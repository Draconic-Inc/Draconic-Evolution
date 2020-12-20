package com.brandon3055.draconicevolution.entity.guardian.control;

import com.brandon3055.draconicevolution.entity.guardian.DraconicGuardianEntity;
import net.minecraft.entity.EntityPredicate;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;

@Deprecated //Old vanilla phase
public class ScanningSittingPhase extends SittingPhase {
   private static final EntityPredicate field_221115_b = (new EntityPredicate()).setDistance(150.0D);
   private final EntityPredicate field_221116_c;
   private int scanningTime;

   public ScanningSittingPhase(DraconicGuardianEntity guardisn) {
      super(guardisn);
      this.field_221116_c = (new EntityPredicate()).setDistance(20.0D).setCustomPredicate((p_221114_1_) -> {
         return Math.abs(p_221114_1_.getPosY() - guardisn.getPosY()) <= 10.0D;
      });
   }

   public void serverTick() {
      ++this.scanningTime;
      LivingEntity livingentity = this.guardian.world.getClosestPlayer(this.field_221116_c, this.guardian, this.guardian.getPosX(), this.guardian.getPosY(), this.guardian.getPosZ());
      if (livingentity != null) {
         if (this.scanningTime > 25) {
            this.guardian.getPhaseManager().setPhase(PhaseType.SITTING_ATTACKING);
         } else {
            Vector3d vector3d = (new Vector3d(livingentity.getPosX() - this.guardian.getPosX(), 0.0D, livingentity.getPosZ() - this.guardian.getPosZ())).normalize();
            Vector3d vector3d1 = (new Vector3d((double)MathHelper.sin(this.guardian.rotationYaw * ((float)Math.PI / 180F)), 0.0D, (double)(-MathHelper.cos(this.guardian.rotationYaw * ((float)Math.PI / 180F))))).normalize();
            float f = (float)vector3d1.dotProduct(vector3d);
            float f1 = (float)(Math.acos((double)f) * (double)(180F / (float)Math.PI)) + 0.5F;
            if (f1 < 0.0F || f1 > 10.0F) {
               double d0 = livingentity.getPosX() - this.guardian.dragonPartHead.getPosX();
               double d1 = livingentity.getPosZ() - this.guardian.dragonPartHead.getPosZ();
               double d2 = MathHelper.clamp(MathHelper.wrapDegrees(180.0D - MathHelper.atan2(d0, d1) * (double)(180F / (float)Math.PI) - (double)this.guardian.rotationYaw), -100.0D, 100.0D);
               this.guardian.field_226525_bB_ *= 0.8F;
               float f2 = MathHelper.sqrt(d0 * d0 + d1 * d1) + 1.0F;
               float f3 = f2;
               if (f2 > 40.0F) {
                  f2 = 40.0F;
               }

               this.guardian.field_226525_bB_ = (float)((double)this.guardian.field_226525_bB_ + d2 * (double)(0.7F / f2 / f3));
               this.guardian.rotationYaw += this.guardian.field_226525_bB_;
            }
         }
      } else if (this.scanningTime >= 100) {
         livingentity = this.guardian.world.getClosestPlayer(field_221115_b, this.guardian, this.guardian.getPosX(), this.guardian.getPosY(), this.guardian.getPosZ());
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
