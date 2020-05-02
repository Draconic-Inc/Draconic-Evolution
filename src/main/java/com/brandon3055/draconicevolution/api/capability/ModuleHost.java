package com.brandon3055.draconicevolution.api.capability;

import com.brandon3055.draconicevolution.api.TechLevel;
import com.brandon3055.draconicevolution.api.modules.Module;
import com.brandon3055.draconicevolution.api.modules.ModuleTypes;
import com.brandon3055.draconicevolution.api.modules.lib.InstallResult;
import com.brandon3055.draconicevolution.api.modules.lib.ModuleEntity;
import com.brandon3055.draconicevolution.api.modules.ModuleType;
import com.brandon3055.draconicevolution.api.modules.properties.EnergyModuleProperties;
import com.brandon3055.draconicevolution.api.modules.properties.ModuleProperties;
import com.brandon3055.draconicevolution.api.modules.properties.ModuleProperties.SubProperty;
import com.google.common.collect.Iterables;
import com.google.common.collect.Streams;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.common.util.INBTSerializable;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.brandon3055.draconicevolution.api.modules.lib.InstallResult.InstallResultType.NO;
import static com.brandon3055.draconicevolution.api.modules.lib.InstallResult.InstallResultType.ONLY_WHEN_OVERRIDEN;

/**
 * Created by brandon3055 and covers1624 on 4/16/20.
 */
public interface ModuleHost extends INBTSerializable<CompoundNBT> {

    /**
     * @return a list of installed modules.
     */
    Stream<Module<?>> getModules();

    /**
     * @return a list of module entities for all installed modules.
     */
    List<ModuleEntity> getModuleEntities();

    /**
     * @return a list of module types supported by this host.
     */
    List<ModuleType<?>> getSupportedTypes();

    /**
     * Only modules with this tech level or lower will be accepted by this host.
     */
    TechLevel getHostTechLevel();

    /**
     * @return the width of this {@link ModuleHost}'s module grid.
     */
    int getGridWidth();

    /**
     * @return the height of this {@link ModuleHost}'s module grid.
     */
    int getGridHeight();

    /**
     * For modules that have different stats based on what they are installed in.
     * For example speed modules have different sub properties for machines and players.
     *
     * This is currently only used for displaying the stats for modules installed in a grid.
     */
    default boolean isSubPropertySupported(ModuleProperties<?> properties, SubProperty<?> subProperty) {
        EnergyModuleProperties props = getModuleProperties(ModuleTypes.ENERGY_STORAGE);

        return true;
    }

    default <T extends ModuleProperties<T>> T getModuleProperties(ModuleType<T> moduleType) {
        return null;
    }

    default InstallResult checkAddModule(Module<?> newModule) {
        Collection<Module<?>> view = Collections.unmodifiableList(getModules().collect(Collectors.toList()));
        Optional<InstallResult> opt = view.stream()//
                .map(other -> newModule.areModulesCompatible(other).getBlockingResult(other.areModulesCompatible(newModule)))//
                .filter(e -> e.resultType == NO || e.resultType == ONLY_WHEN_OVERRIDEN)//
                .findFirst();
        if (opt.isPresent()) {
            return opt.get();
        }

        Iterable<Module<?>> newModules = Iterables.concat(view, Collections.singleton(newModule));
        opt = Streams.stream(newModules).parallel()//
                .map(module -> {
                    int max = module.maxInstallable();
                    if (max == -1) {
                        return null;
                    }
                    int installed = (int) Streams.stream(newModules)//
                            .filter(e -> e.getModuleType() == module.getModuleType() && e.getModuleTechLevel().index <= module.getModuleTechLevel().index)//
                            .count();
                    if (installed > max) {
                        return new InstallResult(InstallResult.InstallResultType.NO, module, null, new TranslationTextComponent("too_complex"));//TODO Localize
                    }
                    return null;
                })//
                .filter(Objects::nonNull)//
                .findFirst();
        return opt.orElseGet(() -> new InstallResult(InstallResult.InstallResultType.YES, newModule, null, null));
    }
}
