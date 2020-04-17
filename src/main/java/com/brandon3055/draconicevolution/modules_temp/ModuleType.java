package com.brandon3055.draconicevolution.modules_temp;

import com.brandon3055.draconicevolution.modules_temp.InstallResult.InstallResultType;
import net.minecraft.util.text.TranslationTextComponent;

import java.util.Collection;

/**
 * Created by brandon3055 and covers1624 on 4/16/20.
 */
public interface ModuleType<T extends IModuleProperties<T>> {

//    T createProperties();

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
    default InstallResult areModulesCompatible(IModule<T> thisModule, IModule<?> otherModule) {
        return new InstallResult(InstallResultType.YES, thisModule, null, null);
    }

//    /**
//     * When a new modules is about to be added this is called for every existing module as well as the module about to be installed.
//     * This allows you to prevent the installation if for some reason the new list of modules is invalid.
//     *
//     * By default this is where module count limits are enforced.
//     *
//     * @param modulesAfterInstall The new module list if the installation is allowed to proceed.
//     * Otherwise return fail with an ITextTranslation specifying a reason that can be displayed to the player.
//     */
//    InstallResult isInstallationValid(IModule<T> thisModule, Collection<IModule<?>> modulesAfterInstall) {
//        int max = thisModule.maxInstallable();
//        int installed = 0;
//        for (IModule<?> module : modulesAfterInstall) {
//            //Only count modules of the same or lower tech levels.
//            //This way modules of a higher tech level can override caps set my lower level modules.
//            if (module.getModuleTechLevel().index <= thisModule.getModuleTechLevel().index) {
//                installed++;
//            }
//        }
//        if (installed > max) {
//            return new InstallResult(InstallResultType.NO, thisModule, null, new TranslationTextComponent("todo.module_cap_exceeded.txt"));
//        }
//        return new InstallResult(InstallResultType.YES, thisModule, null, null);
//    }
}
