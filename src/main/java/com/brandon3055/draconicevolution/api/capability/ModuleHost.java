package com.brandon3055.draconicevolution.api.capability;

import com.brandon3055.brandonscore.api.TechLevel;
import com.brandon3055.draconicevolution.api.modules.Module;
import com.brandon3055.draconicevolution.api.modules.ModuleCategory;
import com.brandon3055.draconicevolution.api.modules.ModuleType;
import com.brandon3055.draconicevolution.api.modules.data.ModuleData;
import com.brandon3055.draconicevolution.api.modules.lib.InstallResult;
import com.brandon3055.draconicevolution.api.modules.lib.ModuleContext;
import com.brandon3055.draconicevolution.api.modules.lib.ModuleEntity;
import net.covers1624.quack.util.SneakyUtils;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.util.INBTSerializable;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

/**
 * Created by brandon3055 and covers1624 on 4/16/20.
 * Note any item implementing this MUST also implement the share tag read and write functions from {@link DECapabilities} Or something similar.
 *
 * @see DECapabilities#writeToShareTag(ItemStack, CompoundTag)
 * @see DECapabilities#readFromShareTag(ItemStack, CompoundTag)
 */
public interface ModuleHost extends INBTSerializable<CompoundTag> {

    /**
     * @return a list of installed modules.
     */
    Stream<Module<?>> getModules();

    /**
     * @return a list of module entities for all installed modules.
     */
    List<ModuleEntity<?>> getModuleEntities();

    void addModule(ModuleEntity<?> entity, ModuleContext context);

    void removeModule(ModuleEntity<?> entity, ModuleContext context);

    /**
     * This is where the main "does this host support this module" check is done.
     *
     * @param entity the module entity.
     * @return true if this module entity is supported bu this host.
     */
    default boolean isModuleSupported(ModuleEntity<?> entity) {
        Module<?> module = entity.getModule();
        ModuleType<?> type = module.getType();
        if (getTypeBlackList().contains(type)) {
            return false;
        } else if (getAdditionalTypes().contains(type) || module.getCategories().contains(ModuleCategory.ALL)) {
            return true;
        }
        Collection<ModuleCategory> hostCats = getModuleCategories();
        for (ModuleCategory cat : module.getCategories()) {
            if (hostCats.contains(cat)) {
                return true;
            }
        }
        return false;
    }

    Collection<ModuleCategory> getModuleCategories();

    default Collection<ModuleType<?>> getAdditionalTypes() {return Collections.emptyList();}

    default Collection<ModuleType<?>> getTypeBlackList() {return Collections.emptyList();}

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

    default <T extends ModuleData<T>> T getModuleData(ModuleType<T> moduleType, T fallback) {
        T data = getModuleData(moduleType);
        return data == null ? fallback : data;
    }

    default Stream<ModuleEntity<?>> getEntitiesByType(ModuleType<?> moduleType) {
        return getModuleEntities().stream().filter(e -> e.getModule().getType() == moduleType);
    }

    /**
     * @return a stream containing all of the module types that are currently installed in this host.
     */
    default Stream<ModuleType<?>> getInstalledTypes() {
        return getModules().<ModuleType<?>>map(Module::getType).distinct();
    }

    /**
     * This method exists so that a module host can select which information from a given module will be displayed. <br>
     * This is useful for module types like Speed which have different effects depending on what they are installed in.
     */
    default <T extends ModuleData<T>> void getDataInformation(T moduleData, Map<Component, Component> map, ModuleContext context) {
        if (moduleData == null) return;
        moduleData.addInformation(map, context);
    }

    /**
     * Adds information about the installed modules to the supplied map.<br>
     * Multiple modules of the same type will be combined and their combined stats wil be added.
     * The map is of Property Name to Property Value
     *
     * @param map the map to which information will be added.
     */
    default void addInformation(Map<Component, Component> map, ModuleContext context) {
        getInstalledTypes().map(this::getModuleData).forEach(data -> getDataInformation(SneakyUtils.unsafeCast(data), map, context));
    }

    static InstallResult checkAddModule(ModuleHost host, Module<?> newModule) {
        return newModule.doInstallationCheck(host.getModules());
        //Moved this check to the module because i needed more control in cases like the arrow velocity module where specific modules within a module type have a module installation limit.
    }

    /**
     * Called before module is removed from the module grid to verify the module can be removed.
     *
     * @param module The module being removed.
     * @param reason In the event the module can not be removed you can specify a message to be displayed to the user by adding a text component to this list.
     * @return false to prevent the module from being removed.
     */
    boolean checkRemoveModule(ModuleEntity<?> module, List<Component> reason);

//    void getAttributeModifiers(EquipmentSlotType slot, ItemStack stack, Multimap<Attribute, AttributeModifier> map);

//    /**
//     * This will be balled by module entities when they dynamically update their attributes.
//     * This should be used by the ModuleHost to update attributes on the next stack tick.
//     */
//    void markAttributesDirty();

    void handleTick(ModuleContext context);
}
