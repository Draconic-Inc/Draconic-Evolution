package com.brandon3055.draconicevolution.modules_temp.capability;

import com.brandon3055.draconicevolution.modules_temp.IModule;
import com.brandon3055.draconicevolution.modules_temp.InstallResult;
import com.google.common.collect.Iterables;
import com.google.common.collect.Streams;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.common.util.INBTSerializable;
import org.apache.commons.lang3.tuple.Pair;

import java.util.*;
import java.util.stream.Stream;

import static com.brandon3055.draconicevolution.modules_temp.InstallResult.InstallResultType.NO;
import static com.brandon3055.draconicevolution.modules_temp.InstallResult.InstallResultType.ONLY_WHEN_OVERRIDEN;

/**
 * Created by brandon3055 and covers1624 on 4/16/20.
 */
public interface IModuleHost extends INBTSerializable<CompoundNBT> {

    List<IModule<?>> getModules();

    default InstallResult checkAddModule(IModule<?> newModule) {
        Collection<IModule<?>> view = Collections.unmodifiableList(getModules());
        Optional<InstallResult> opt = view.stream()//
                .map(other -> newModule.areModulesCompatible(other).getBlockingResult(other.areModulesCompatible(newModule)))//
                .filter(e -> e.resultType == NO || e.resultType == ONLY_WHEN_OVERRIDEN)//
                .findFirst();
        if (opt.isPresent()) {
            return opt.get();
        }

        Iterable<IModule<?>> newModules = Iterables.concat(view, Collections.singleton(newModule));
        opt = Streams.stream(newModules).parallel()//
                .map(module -> {
                    int max = module.maxInstallable();
                    if (max == -1) {
                        return null;
                    }
                    int installed = (int) Streams.stream(newModules)//
                            .filter(e -> e.getModuleTechLevel().index <= module.getModuleTechLevel().index)//
                            .count();
                    if (installed > max) {
                        return new InstallResult(InstallResult.InstallResultType.NO, module, null, new TranslationTextComponent("too_complex"));
                    }
                    return null;
                })//
                .filter(Objects::nonNull)//
                .findFirst();
        return opt.orElseGet(() -> new InstallResult(InstallResult.InstallResultType.YES, newModule, null, null));
    }
}
