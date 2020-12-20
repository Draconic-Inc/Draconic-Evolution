package com.brandon3055.draconicevolution.entity.guardian.control;

import com.brandon3055.draconicevolution.entity.guardian.DraconicGuardianEntity;
import net.minecraft.util.SoundEvents;

@Deprecated //Old vanilla phase
public class AttackingSittingPhase extends SittingPhase {
   private int attackingTicks;

   public AttackingSittingPhase(DraconicGuardianEntity guardisn) {
      super(guardisn);
   }

   public void clientTick() {
      this.guardian.world.playSound(this.guardian.getPosX(), this.guardian.getPosY(), this.guardian.getPosZ(), SoundEvents.ENTITY_ENDER_DRAGON_GROWL, this.guardian.getSoundCategory(), 2.5F, 0.8F + this.guardian.getRNG().nextFloat() * 0.3F, false);
   }

   public void serverTick() {
      if (this.attackingTicks++ >= 40) {
         this.guardian.getPhaseManager().setPhase(PhaseType.SITTING_FLAMING);
      }

   }

   public void initPhase() {
      this.attackingTicks = 0;
   }

   public PhaseType<AttackingSittingPhase> getType() {
      return PhaseType.SITTING_ATTACKING;
   }
}
