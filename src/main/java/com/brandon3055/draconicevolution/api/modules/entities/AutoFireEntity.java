package com.brandon3055.draconicevolution.api.modules.entities;

import com.brandon3055.draconicevolution.api.config.BooleanProperty;
import com.brandon3055.draconicevolution.api.modules.Module;
import com.brandon3055.draconicevolution.api.modules.data.AutoFireData;
import com.brandon3055.draconicevolution.api.modules.lib.ModuleEntity;

import static com.brandon3055.draconicevolution.api.config.ConfigProperty.BooleanFormatter.ENABLED_DISABLED;

public class AutoFireEntity extends ModuleEntity {

    private BooleanProperty autoFireEnabled = null;

    public AutoFireEntity(Module<AutoFireData> module) {
        super(module);
        if (module.getData() != null) {
            addProperty(autoFireEnabled = new BooleanProperty("auto_fire", true).setFormatter(ENABLED_DISABLED));
        }
        this.savePropertiesToItem = true;
    }

    public boolean getAutoFireEnabled() {
        return autoFireEnabled != null && autoFireEnabled.getValue();
    }
}
