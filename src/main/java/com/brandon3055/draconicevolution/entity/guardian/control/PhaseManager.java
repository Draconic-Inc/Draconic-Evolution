package com.brandon3055.draconicevolution.entity.guardian.control;

import com.brandon3055.draconicevolution.entity.guardian.DraconicGuardianEntity;
import com.brandon3055.draconicevolution.entity.guardian.GuardianFightManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Reminder. There are a fixed number of {@link PhaseType}'s but those types will create a new instance of their associated phase for each guardian entity.
 * Meaning all dater in a phase is "per-guardian" and not "global" so you dont need to worry about the possibility of simultaneous fights conflicting with each other.
 */
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
                GuardianFightManager manager = dragon.getFightManager();
                if (manager != null) {
                    manager.guardianUpdate(dragon);
                }
//            dragon.level.getServer().getPlayerList().broadcastMessage(new StringTextComponent("Start Phase: " + phaseIn), ChatType.CHAT, Util.NIL_UUID);
            }

//         LOGGER.info("Dragon is now in phase {} on the {}", phaseIn, dragon.level.isClientSide ? "client" : "server");
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

        return (T) phases[i];
    }

    public void globalServerTick() {
        for (IPhase phase : phases) {
            if (phase != null) {
                phase.globalServerTick();
            }
        }
    }
}
