package com.brandon3055.draconicevolution.entity.guardian.control;

import com.brandon3055.draconicevolution.entity.guardian.DraconicGuardianEntity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PhaseManager {
   private static final Logger LOGGER = LogManager.getLogger();
   private final DraconicGuardianEntity dragon;
   private final IPhase[] phases = new IPhase[PhaseType.getTotalPhases()];
   private IPhase phase;

   public PhaseManager(DraconicGuardianEntity guardisn) {
      this.dragon = guardisn;
      this.setPhase(PhaseType.HOVER);
   }

   public <T extends IPhase> T setPhase(PhaseType<T> phaseIn) {
      if (phase == null || phaseIn != phase.getType()) {
         if (phase != null) {
            phase.removeAreaEffect();
         }

         phase = getPhase(phaseIn);
         if (!dragon.level.isClientSide) {
            dragon.getEntityData().set(DraconicGuardianEntity.PHASE, phaseIn.getId());
         }

         LOGGER.debug("Dragon is now in phase {} on the {}", phaseIn, dragon.level.isClientSide ? "client" : "server");
         phase.initPhase();
         return (T) phase;
      }
      return (T) phase;
   }

   public IPhase getCurrentPhase() {
      return phase;
   }

   public <T extends IPhase> T getPhase(PhaseType<T> phaseIn) {
      int i = phaseIn.getId();
      if (phases[i] == null) {
         phases[i] = phaseIn.createPhase(dragon);
      }

      return (T)phases[i];
   }
}
