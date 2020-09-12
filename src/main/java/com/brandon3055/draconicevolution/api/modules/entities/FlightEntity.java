package com.brandon3055.draconicevolution.api.modules.entities;

import com.brandon3055.draconicevolution.api.config.BooleanProperty;
import com.brandon3055.draconicevolution.api.config.ConfigProperty;
import com.brandon3055.draconicevolution.api.config.DecimalProperty;
import com.brandon3055.draconicevolution.api.modules.Module;
import com.brandon3055.draconicevolution.api.modules.ModuleTypes;
import com.brandon3055.draconicevolution.api.modules.data.FlightData;
import com.brandon3055.draconicevolution.api.modules.data.SpeedData;
import com.brandon3055.draconicevolution.api.modules.lib.ModuleContext;
import com.brandon3055.draconicevolution.api.modules.lib.ModuleEntity;

import static com.brandon3055.draconicevolution.api.config.ConfigProperty.BooleanFormatter.ENABLED_DISABLED;
import static com.brandon3055.draconicevolution.api.config.ConfigProperty.DecimalFormatter.PLUS_PERCENT_0;

public class FlightEntity extends ModuleEntity {

    private BooleanProperty elytraEnabled = null;
    private BooleanProperty creativeEnabled = null;
//    private DecimalProperty speed = null;
//    private DecimalProperty boostSpeed = null;

    public FlightEntity(Module<FlightData> module) {
        super(module);
        if (module.getData().elytra()) {
            addProperty(elytraEnabled = new BooleanProperty("flight_mod.elytra", true).setFormatter(ENABLED_DISABLED));
        }
        if (module.getData().creative()) {
            addProperty(creativeEnabled = new BooleanProperty("flight_mod.creative", true).setFormatter(ENABLED_DISABLED));
//            addProperty(speed = new DecimalProperty("flight_mod.speed", 0).setFormatter(PLUS_PERCENT_0).range(0, 1));
//            addProperty(boostSpeed = new DecimalProperty("flight_mod.boost_speed", 1).setFormatter(PLUS_PERCENT_0).range(0, 1));
        }
        this.savePropertiesToItem = true;
    }

    @Override
    public void onInstalled(ModuleContext context) {
//        SpeedData speedData = host.getModuleData(ModuleTypes.SPEED, new SpeedData(0));
//        speed.range(0, 1 + speedData.getSpeedMultiplier());
//        boostSpeed.range(0, 1 + speedData.getSpeedMultiplier());
    }

    public boolean getElytraEnabled() {
        return elytraEnabled != null && elytraEnabled.getValue();
    }

    public boolean getCreativeEnabled() {
        return creativeEnabled != null && creativeEnabled.getValue();
    }

//    public double getSpeed(boolean boost) {
//        if (boost) {
//            return speed == null ? 0 : speed.getValue();
//        } else {
//            return boostSpeed == null ? 0 : boostSpeed.getValue();
//        }
//    }
}
