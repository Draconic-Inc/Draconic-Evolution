package com.brandon3055.draconicevolution.api.modules.entities;

import com.brandon3055.draconicevolution.api.config.BooleanProperty;
import com.brandon3055.draconicevolution.api.config.ConfigProperty;
import com.brandon3055.draconicevolution.api.modules.Module;
import com.brandon3055.draconicevolution.api.modules.data.NoData;
import com.brandon3055.draconicevolution.api.modules.lib.ModuleEntity;

public class AutoFireEntity extends ModuleEntity {

    private BooleanProperty autoFireEnabled;

    public AutoFireEntity(Module<NoData> module) {
        super(module);
        addProperty(autoFireEnabled = new BooleanProperty("auto_fire_mod.enable", true).setFormatter(ConfigProperty.BooleanFormatter.ENABLED_DISABLED));
        this.savePropertiesToItem = true;
    }

    public boolean getAutoFireEnabled() {
        return autoFireEnabled.getValue();
    }
}
