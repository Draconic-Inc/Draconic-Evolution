package com.brandon3055.draconicevolution.api.modules;

import com.brandon3055.brandonscore.api.TechLevel;
import com.brandon3055.draconicevolution.api.modules.data.ModuleData;
import com.brandon3055.draconicevolution.api.modules.data.ModuleProperties;
import com.brandon3055.draconicevolution.api.modules.lib.EntityOverridesItemUse;
import com.brandon3055.draconicevolution.api.modules.lib.InstallResult;
import com.brandon3055.draconicevolution.api.modules.lib.ModuleContext;
import com.brandon3055.draconicevolution.api.modules.lib.ModuleEntity;
import com.google.common.collect.Iterables;
import com.google.common.collect.Streams;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.IForgeRegistryEntry;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by brandon3055 and covers1624 on 4/16/20.
 */
public interface Module<T extends ModuleData<T>> extends IForgeRegistryEntry<Module<?>> {

    ModuleType<T> getType();

    ModuleProperties<T> getProperties();

    /**
     * @return a convenience method for getting the module data.
     */
    default T getData() {
        return getProperties().getData();
    }

    /**
     * This is just a convenience method. This should always return the same tech level as defined by the {@link ModuleProperties}
     */
    default TechLevel getModuleTechLevel() {
        return getProperties().getTechLevel();
    }

    Item getItem();

    default Collection<ModuleCategory> getCategories() {
        return getType().getCategories();
    }

    /**
     * Ideally the module entity should be always be created by the {@link ModuleType} because all modules of a specific type should use the same entity.
     * However if for some reason you wish need to modify the module entity for your module this method can be used to do that.<br><br>
     * <b>
     * Note the module entity you return MUST extend the default module entity for this module's type.
     * If you do not do this any code that needs to retrieve, cast and interact with this entity WILL break.</b>
     * In other words <br>
     * Module#getType().createEntity(this).getClass().isAssignableFrom(Module#createEntity().getClass())<br>
     * Must return true.
     * <br><br>
     *
     * @return a new {@link ModuleEntity} instance for this module.
     * @see ModuleType#createEntity(Module)
     */
    default ModuleEntity<?> createEntity() {
        return getType().createEntity(this);
    }

    /**
     * This allows you to prevent this module from being installed along side any other specific module.
     *
     * @param otherModule Other module.
     * @return pass with null value if this module can coexist with the other module.
     * Otherwise return fail with an ITextTranslation specifying a reason that can be displayed to the player.
     */
    default InstallResult areModulesCompatible(Module<?> otherModule) {
        if (this.createEntity() instanceof EntityOverridesItemUse && otherModule.createEntity() instanceof EntityOverridesItemUse) {
            return new InstallResult(InstallResult.InstallResultType.NO, this, otherModule, List.of(new TranslatableComponent("modular_item.draconicevolution.error.only_one_use_override_module").withStyle(ChatFormatting.RED),
                    new TranslatableComponent("modular_item.draconicevolution.error.not_compatible_with").withStyle(ChatFormatting.GRAY).append(": ").append(new TranslatableComponent(otherModule.getItem().getDescriptionId()))));
        }
        return getType().areModulesCompatible(this, otherModule);
    }

    /**
     * @return The maximum number of modules of this type that can be installed (-1 = no limit)
     */
    default int maxInstallable() {
        return getType().maxInstallable();
    }

    default void addInformation(List<Component> toolTip, ModuleContext context) {
        getProperties().addStats(toolTip, this, context);

        if (maxInstallable() != -1) {
            toolTip.add(new TranslatableComponent("module.draconicevolution.max_installable")
                    .withStyle(ChatFormatting.GRAY)
                    .append(": ")
                    .append(new TextComponent(String.valueOf(maxInstallable()))
                            .withStyle(ChatFormatting.DARK_GREEN)));
        }
    }

    default InstallResult doInstallationCheck(Stream<Module<?>> moduleStream) {
        Collection<Module<?>> view = moduleStream.toList();
        Optional<InstallResult> opt = view.stream()
                .map(other -> this.areModulesCompatible(other).getBlockingResult(other.areModulesCompatible(this)))
                .filter(e -> e.resultType == InstallResult.InstallResultType.NO || e.resultType == InstallResult.InstallResultType.ONLY_WHEN_OVERRIDEN)
                .findFirst();
        if (opt.isPresent()) {
            return opt.get();
        }

        Iterable<Module<?>> newModules = Iterables.concat(view, Collections.singleton(this));
        opt = Streams.stream(newModules).parallel()
                .map(module -> {
                    int max = module.maxInstallable();
                    if (max == -1) {
                        return null;
                    }
                    int installed = (int) Streams.stream(newModules)
                            .filter(e -> e.getType() == module.getType() && e.getModuleTechLevel().index <= module.getModuleTechLevel().index)
                            .count();
                    if (installed > max) {
                        return new InstallResult(InstallResult.InstallResultType.NO, module, null, new TranslatableComponent("modular_item.draconicevolution.error.module_install_limit").withStyle(ChatFormatting.RED));
                    }
                    return null;
                })
                .filter(Objects::nonNull)
                .findFirst();
        return opt.orElseGet(() -> new InstallResult(InstallResult.InstallResultType.YES, this, null, (List<Component>)null));
    }
}
