package com.brandon3055.draconicevolution.api.modules;

import com.brandon3055.draconicevolution.api.config.ConfigProperty;
import com.brandon3055.draconicevolution.api.modules.data.ModuleData;
import com.brandon3055.draconicevolution.api.modules.lib.InstallResult;
import com.brandon3055.draconicevolution.api.modules.lib.InstallResult.InstallResultType;
import com.brandon3055.draconicevolution.api.modules.lib.ModuleEntity;
import com.google.common.collect.Multimap;
import net.covers1624.quack.util.SneakyUtils;
import net.minecraft.network.chat.BaseComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Created by brandon3055 and covers1624 on 4/16/20.
 */
public interface ModuleType<T extends ModuleData<T>> {

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
        return new InstallResult(InstallResultType.YES, thisModule, null, (List<Component>)null);
    }

    /**
     * This is a convenience method that automatically casts the modules data to the correct type for this {@link ModuleType}
     * @param module A module matching this module type.
     */
    default T getData(Module<?> module) {
        return SneakyUtils.unsafeCast(module.getData());
    }

    int getDefaultWidth();

    int getDefaultHeight();

    String getName();

    default BaseComponent getDisplayName() {
        return new TranslatableComponent("module_type.draconicevolution." + getName() + ".name");
    }

    /**
     * A module entity is to a module what a tile entity is to a block with a few differences.
     * When a module is installed in a host it is always stored as a module entity.
     * The default module entity does nothing but store its module as well as information about its
     * position within the module grid. However a module entity can be extended to add additional functionality.
     * For example the tick method can be used to do pretty much what ever you want. Module entities can also
     * provide {@link com.brandon3055.draconicevolution.api.config.ConfigProperty}'s allowing modules to be configured via the item config gui.
     * They can also supply tool tip information when for when they are in a module grid as well as render an overlay on the
     * module in the grid. useful for things like cool down animations or charge bars etc.
     * Finally module entities can save and load data from the module item when it is installed / removed from a grid.
     *
     * @param module The module this entity is being created for.
     * @return a new {@link ModuleEntity} instance for this module.
     */
    ModuleEntity createEntity(Module<T> module);

    /**
     * These can be thought of as "global" properties for a specific type and their data is stored in the module host capability.
     * These properties are held within the module host. The consumer you supply with each property will be called with the up to date module data
     * whenever the module grid changes. Use this to ensure the property still complies with the new module data.
     *.<br><br>
     *
     * An example use case for this is the jump module. Say for example the user has 5 jump modules installed giving them a jump boost of +500%
     * The JUMP_BOOST {@link ModuleType} can add a property that allows the user to adjust their jump boost between +0% and +500%<br><br>
     *
     * If you need to add per module properties then see {@link ModuleEntity#addProperty(ConfigProperty)}<br><br>
     *
     * Please note the names for these properties must not conflict with the names of other module properties or the names of any properties
     * that could exist on anything these modules could be installed in. Recommended naming convention is [partial modid].[module].[name]<br>
     * E.g. de.module.flightSpeed<br>
     * This is not an issue with per module properties as those use a UUID for their name as its not possible to use standard names for those
     * without the possibility of conflicts.
     *
     * @see ModuleEntity#addProperty(ConfigProperty)
     * @param moduleData The combined data from all installed modules of this type.
     * @param propertyMap The map to which you add your property and optional property validator.
     */
    default void getTypeProperties(@Nullable T moduleData, Map<ConfigProperty, Consumer<T>> propertyMap) {
//        propertyMap.put(new IntegerProperty("de.module.testTypeProp", 0).range(0, 100), null);
    }

    /**
     * This method allows modules to apply attribute modifiers to the items they are installed in.
     *
     * @param moduleData The combined data from all installed modules of this type.
     * @param slot the equipment slot that the item containing this module is in.
     * @param stack The ItemStack containing this module/modules
     * @param map The map to which the modifiers must be added.
     */
    default void getAttributeModifiers(@Nullable T moduleData, EquipmentSlot slot, ItemStack stack, Multimap<Attribute, AttributeModifier> map) {}

    Collection<ModuleCategory> getCategories();

//    /**
//     * If you are using {@link #getAttributeModifiers(ModuleData, EquipmentSlotType, ItemStack, Multimap)} to add custom attributes you MUST also
//     * implement this method and add all of your attribute id's to the provided list. This list is used to refresh or remove attributes added by modules.
//     * @param list the list to which you must add your attribute id's
//     */
//    default void getAttributeIDs(List<UUID> list) {}
}
