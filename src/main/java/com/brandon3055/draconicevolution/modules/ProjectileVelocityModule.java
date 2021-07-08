package com.brandon3055.draconicevolution.modules;

import com.brandon3055.brandonscore.api.TechLevel;
import com.brandon3055.draconicevolution.api.modules.Module;
import com.brandon3055.draconicevolution.api.modules.ModuleType;
import com.brandon3055.draconicevolution.api.modules.data.ProjectileData;
import com.brandon3055.draconicevolution.api.modules.lib.InstallResult;
import com.brandon3055.draconicevolution.api.modules.lib.ModuleImpl;
import com.google.common.collect.Iterables;
import com.google.common.collect.Streams;
import net.minecraft.item.Item;
import net.minecraft.util.text.TranslationTextComponent;

import java.util.Collection;
import java.util.Collections;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.brandon3055.draconicevolution.api.modules.lib.InstallResult.InstallResultType.NO;
import static com.brandon3055.draconicevolution.api.modules.lib.InstallResult.InstallResultType.ONLY_WHEN_OVERRIDEN;

/**
 * Created by brandon3055 on 8/7/21
 */
public class ProjectileVelocityModule extends ModuleImpl<ProjectileData> {

    public ProjectileVelocityModule(ModuleType<ProjectileData> moduleType, TechLevel techLevel, Function<Module<ProjectileData>, ProjectileData> dataGenerator, Item moduleItem) {
        super(moduleType, techLevel, dataGenerator, moduleItem);
    }

    public ProjectileVelocityModule(ModuleType<ProjectileData> moduleType, TechLevel techLevel, Function<Module<ProjectileData>, ProjectileData> dataGenerator, int width, int height, Item moduleItem) {
        super(moduleType, techLevel, dataGenerator, width, height, moduleItem);
    }

    public ProjectileVelocityModule(ModuleType<ProjectileData> moduleType, TechLevel techLevel, Function<Module<ProjectileData>, ProjectileData> dataGenerator) {
        super(moduleType, techLevel, dataGenerator);
    }

    public ProjectileVelocityModule(ModuleType<ProjectileData> moduleType, TechLevel techLevel, Function<Module<ProjectileData>, ProjectileData> dataGenerator, int width, int height) {
        super(moduleType, techLevel, dataGenerator, width, height);
    }

    @Override
    public int maxInstallable() {
        return -1;//super.maxInstallable();
    }

    @Override
    public InstallResult doInstallationCheck(Stream<Module<?>> moduleStream) {
        Collection<Module<?>> view = Collections.unmodifiableList(moduleStream.collect(Collectors.toList()));
        Optional<InstallResult> opt = view.stream()//
                .map(other -> this.areModulesCompatible(other).getBlockingResult(other.areModulesCompatible(this)))//
                .filter(e -> e.resultType == NO || e.resultType == ONLY_WHEN_OVERRIDEN)//
                .findFirst();
        if (opt.isPresent()) {
            return opt.get();
        }

        Iterable<Module<?>> newModules = Iterables.concat(view, Collections.singleton(this));
        opt = Streams.stream(newModules).parallel()//
                .map(module -> {
                    int max = module.maxInstallable();
                    //This is a nasty hack.... Dont do this!
                    if (module instanceof ProjectileVelocityModule) {
                        max = this.maxInstall;
                    }
                    if (max == -1) {
                        return null;
                    }
                    int installed = (int) Streams.stream(newModules)//
                            .filter(e -> e instanceof ProjectileVelocityModule && e.getModuleTechLevel().index <= module.getModuleTechLevel().index)//
                            .count();
                    if (installed > max) {
                        return new InstallResult(InstallResult.InstallResultType.NO, module, null, new TranslationTextComponent("too_complex"));//TODO Localize
                    }
                    return null;
                })//
                .filter(Objects::nonNull)//
                .findFirst();
        return opt.orElseGet(() -> new InstallResult(InstallResult.InstallResultType.YES, this, null, null));
    }
}
