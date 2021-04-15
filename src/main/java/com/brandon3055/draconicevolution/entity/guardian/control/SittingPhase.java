package com.brandon3055.draconicevolution.entity.guardian.control;

import com.brandon3055.draconicevolution.entity.guardian.DraconicGuardianEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.util.DamageSource;

@Deprecated //Old vanilla phase
public abstract class SittingPhase extends Phase {
   public SittingPhase(DraconicGuardianEntity guardisn) {
      super(guardisn);
   }

   public boolean getIsStationary() {
      return true;
   }

   public float onAttacked(DamageSource source, float damage) {
      if (source.getDirectEntity() instanceof AbstractArrowEntity) {
         source.getDirectEntity().setSecondsOnFire(1);
         return 0.0F;
      } else {
         return super.onAttacked(source, damage);
      }
   }
}
