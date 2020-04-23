package com.brandon3055.draconicevolution.api.modules;

import com.brandon3055.draconicevolution.api.TechLevel;
import com.brandon3055.draconicevolution.api.modules.properties.ModuleProperties;
import com.brandon3055.draconicevolution.api.modules.lib.InstallResult;
import net.minecraft.item.Item;
import net.minecraftforge.registries.IForgeRegistryEntry;

/**
 * Created by brandon3055 and covers1624 on 4/16/20.
 */
public interface IModule<P extends ModuleProperties<P>> extends IForgeRegistryEntry<IModule<?>> {

    ModuleType<P> getModuleType();

    P getProperties();

    /**
     * This is just a convenience method. This should always return the same tech level as defined by the {@link ModuleProperties}
     */
    default TechLevel getModuleTechLevel() {
        return getProperties().getTechLevel();
    }

    Item getItem();

    /**
     * This allows you to prevent this module from being installed along side any other specific module.
     *
     * @param otherModule Other module.
     * @return pass with null value if this module can coexist with the other module.
     * Otherwise return fail with an ITextTranslation specifying a reason that can be displayed to the player.
     */
    default InstallResult areModulesCompatible(IModule<?> otherModule) {
        return getModuleType().areModulesCompatible(this, otherModule);
    }

    /**
     * @return The maximum number of modules of this type that can be installed (-1 = no limit)
     */
    default int maxInstallable() {
        return getModuleType().maxInstallable();
    }
}
