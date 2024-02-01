package com.brandon3055.draconicevolution.entity.guardian.control;

import com.brandon3055.draconicevolution.entity.guardian.DraconicGuardianEntity;

public class ShockwavePhase extends ChargeUpPhase {

    public ShockwavePhase(DraconicGuardianEntity guardian) {
        super(guardian, 10 * 20);
    }

    @Override
    public void serverTick() {
        super.serverTick();


    }

    @Override
    public PhaseType<ShockwavePhase> getType() {
        return PhaseType.SHOCKWAVE;
    }
}
