package com.brandon3055.draconicevolution.entity.guardian.control;

import com.brandon3055.draconicevolution.entity.guardian.DraconicGuardianEntity;
import net.minecraft.util.math.vector.Vector3d;

import javax.annotation.Nullable;

@Deprecated //Old vanilla phase
public class HoverPhase extends Phase {
   private Vector3d targetLocation;

   public HoverPhase(DraconicGuardianEntity guardisn) {
      super(guardisn);
   }

   int i = 0;
   public void serverTick() {
      if (this.targetLocation == null) {
         this.targetLocation = this.guardian.position();
      }
//
//      if (i++ > 10*20) {
//         targetLocation = targetLocation.add((Math.random() - 0.5) * 500, 0, (Math.random() - 0.5) * 500);
//         System.out.println("Update Target: " + targetLocation);
//         i = 0;
//      }

   }

   public boolean getIsStationary() {
      return true;
   }

   public void initPhase() {
      this.targetLocation = null;
   }

   public float getMaxRiseOrFall() {
      return 1.0F;
   }

   @Nullable
   public Vector3d getTargetLocation() {
      return this.targetLocation;
   }

   public PhaseType<HoverPhase> getType() {
      return PhaseType.HOVER;
   }
}
