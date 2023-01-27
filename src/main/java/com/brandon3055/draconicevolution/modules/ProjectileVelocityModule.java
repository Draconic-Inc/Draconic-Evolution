package com.brandon3055.draconicevolution.modules;

import com.brandon3055.brandonscore.api.TechLevel;
import com.brandon3055.draconicevolution.api.modules.Module;
import com.brandon3055.draconicevolution.api.modules.ModuleType;
import com.brandon3055.draconicevolution.api.modules.data.ProjectileData;
import com.brandon3055.draconicevolution.api.modules.lib.InstallResult;
import com.brandon3055.draconicevolution.api.modules.lib.ModuleContext;
import com.brandon3055.draconicevolution.api.modules.lib.ModuleImpl;
import com.google.common.collect.Iterables;
import com.google.common.collect.Streams;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.item.Item;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
                .filter(e -> e.resultType == InstallResult.InstallResultType.NO || e.resultType == InstallResult.InstallResultType.ONLY_WHEN_OVERRIDEN)//
                .findFirst();
        if (opt.isPresent()) {
            return opt.get();
        }

        Iterable<Module<?>> newModules = Iterables.concat(view, Collections.singleton(this));
        opt = Streams.stream(newModules).parallel()//
                .map(module -> {
                    int max;
                    //This is a nasty hack.... Dont do this!
                    if (module instanceof ProjectileVelocityModule) {
                        max = this.maxInstall;
                    } else {
                        return null;
                    }
                    int installed = (int) Streams.stream(newModules)//
                            .filter(e -> e instanceof ProjectileVelocityModule && e.getModuleTechLevel().index <= module.getModuleTechLevel().index)//
                            .count();
                    if (installed > max) {
                        return new InstallResult(InstallResult.InstallResultType.NO, module, null, new TranslatableComponent("modular_item.draconicevolution.error.module_install_limit"));
                    }
                    return null;
                })//
                .filter(Objects::nonNull)//
                .findFirst();
        return opt.orElseGet(() -> new InstallResult(InstallResult.InstallResultType.YES, this, null, (List<Component>)null));
    }

    @Override
    public void addInformation(List<Component> toolTip, ModuleContext context) {
        super.addInformation(toolTip, context);
        toolTip.add(new TranslatableComponent("module.draconicevolution.max_installable")
                .withStyle(ChatFormatting.GRAY)
                .append(": ")
                .append(new TextComponent(String.valueOf(maxInstall))
                        .withStyle(ChatFormatting.DARK_GREEN)));
    }
}
