package com.brandon3055.draconicevolution.api.modules.entities;

import com.brandon3055.draconicevolution.api.config.BooleanProperty;
import com.brandon3055.draconicevolution.api.config.ConfigProperty;
import com.brandon3055.draconicevolution.api.config.ConfigProperty.DecimalFormatter;
import com.brandon3055.draconicevolution.api.config.DecimalProperty;
import com.brandon3055.draconicevolution.api.modules.Module;
import com.brandon3055.draconicevolution.api.modules.data.FlightData;
import com.brandon3055.draconicevolution.api.modules.lib.ModuleContext;
import com.brandon3055.draconicevolution.api.modules.lib.ModuleEntity;

public class FlightEntity extends ModuleEntity<FlightData> {

    private BooleanProperty elytraEnabled = null;
    private BooleanProperty creativeEnabled = null;
    private DecimalProperty elytraBoost = null;

    public FlightEntity(Module<FlightData> module) {
        super(module);
        if (module.getData().elytra()) {
            addProperty(elytraEnabled = new BooleanProperty("flight_mod.elytra", true).setFormatter(ConfigProperty.BooleanFormatter.ENABLED_DISABLED));
            addProperty(elytraBoost = new DecimalProperty("flight_mod.elytra_boost", module.getData().elytraSpeed()).setFormatter(DecimalFormatter.PERCENT_0).range(0, module.getData().elytraSpeed()));
        }
        if (module.getData().creative()) {
            addProperty(creativeEnabled = new BooleanProperty("flight_mod.creative", true).setFormatter(ConfigProperty.BooleanFormatter.ENABLED_DISABLED));
        }
        this.savePropertiesToItem = true;
    }

    @Override
    public void onInstalled(ModuleContext context) {}

    public boolean getElytraEnabled() {
        return elytraEnabled != null && elytraEnabled.getValue();
    }

    public boolean getCreativeEnabled() {
        return creativeEnabled != null && creativeEnabled.getValue();
    }

    public double getElytraBoost() {
        return elytraBoost != null ? elytraBoost.getValue() : 0;
    }
}
