package com.brandon3055.draconicevolution.api.modules;

import codechicken.lib.util.SneakyUtils;
import com.brandon3055.draconicevolution.api.modules.lib.InstallResult;
import com.brandon3055.draconicevolution.api.modules.lib.InstallResult.InstallResultType;
import com.brandon3055.draconicevolution.api.modules.properties.ModuleProperties;

/**
 * Created by brandon3055 and covers1624 on 4/16/20.
 */
public interface ModuleType<T extends ModuleProperties<T>> {

    /**
     * @return The maximum number of modules of this type that can be installed (-1 = no limit)
     */
    default int maxInstallable() {
        return -1;
    }

    /**
     * This allows you to prevent this module from being installed along side any other specific module.
     *
     * @param otherModule Other module.
     * Otherwise return fail with an ITextTranslation specifying a reason that can be displayed to the player.
     */
    default InstallResult areModulesCompatible(Module<T> thisModule, Module<?> otherModule) {
        return new InstallResult(InstallResultType.YES, thisModule, null, null);
    }

    /**
     * This is a convenience method that automatically casts the modules properties to the correct type for this {@link ModuleType}
     * @param module A module matching this module type.
     */
    default T getProperties(Module<?> module) {
        return SneakyUtils.unsafeCast(module.getProperties());
    }

    int getDefaultWidth();

    int getDefaultHeight();

    String getName();
}
