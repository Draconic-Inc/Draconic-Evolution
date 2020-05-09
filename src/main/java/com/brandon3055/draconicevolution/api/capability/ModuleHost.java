package com.brandon3055.draconicevolution.api.capability;

import com.brandon3055.draconicevolution.api.TechLevel;
import com.brandon3055.draconicevolution.api.modules.Module;
import com.brandon3055.draconicevolution.api.modules.lib.InstallResult;
import com.brandon3055.draconicevolution.api.modules.lib.ModuleEntity;
import com.brandon3055.draconicevolution.api.modules.ModuleType;
import com.brandon3055.draconicevolution.api.modules.properties.ModuleProperties;
import com.brandon3055.draconicevolution.api.modules.properties.ModuleData;
import com.brandon3055.draconicevolution.utils.LogHelper;
import com.google.common.collect.Iterables;
import com.google.common.collect.Streams;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.common.util.INBTSerializable;

import javax.annotation.Nullable;
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
     * This method gathers up all modules of this type and returns their combined data.
     *
     * @param moduleType the module type
     * @return a {@link ModuleData} object that is the result of merging the data from all installed modules of this type.
     * Or null if there are no installed modules of this type.
     */
    @Nullable
    default <T extends ModuleData<T>> T getModuleData(ModuleType<T> moduleType) {
        // No that can not be replaced with a method reference. Doing so causes a BootstrapMethodError
        // noinspection unchecked,Convert2MethodRef
        return (T) getModules() //
                .filter(module -> module.getType() == moduleType) //
                .map(Module::getData) //
                .reduce((o1, other) -> o1.combine(other)) //
                .orElse(null);
    }

    /**
     * @return a stream containing all of the module types that are currently installed in this host.
     */
    default Stream<ModuleType<?>> getInstalledTypes() {
        return getModules().map(Module::getType);
    }

    /**
     * This method exists so that a module host can select which information from a given module will be displayed. <br>
     * This is useful for module types like Speed which have different effects depending on what they are installed in.
     */
    default <T extends ModuleData<T>> void getDataInformation(T moduleData, Map<ITextComponent, ITextComponent> map) {
        if (moduleData == null) return;
        moduleData.addInformation(map);
    }

    /**
     * Adds information about the installed modules to the supplied map.<br>
     * Multiple modules of the same type will be combined and their combined stats wil be added.
     * The map is of Property Name to Property Value
     *
     * @param map the map to which information will be added.
     */
    default void addInformation(Map<ITextComponent, ITextComponent> map) {
        getInstalledTypes().map(this::getModuleData).forEach(data -> getDataInformation(data, map));
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
                            .filter(e -> e.getType() == module.getType() && e.getModuleTechLevel().index <= module.getModuleTechLevel().index)//
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
