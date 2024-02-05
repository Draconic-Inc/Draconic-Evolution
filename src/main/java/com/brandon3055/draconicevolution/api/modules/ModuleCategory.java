package com.brandon3055.draconicevolution.api.modules;

/**
 * Created by brandon3055 on 16/6/20
 * These work similar ro enchantment types except a module can be in more than one category and a module host
 * can accept more than one category.
 * The logic behind this is if the host and the module both share at least one category or the module is in the "ALL" category
 * then the module can be installed in the host. A module host can also bypass the category system and accept or reject specific module types.
 */
@SuppressWarnings("InstantiationOfUtilityClass")
public class ModuleCategory {

    //@formatter:off
    /**
     * Special case category. A module with this category flag can be installed in any module grid.
     */
    public static final ModuleCategory ALL              = new ModuleCategory();
    /**
     * Applies to module hosts that have energy storage and can accept energy modifying modules.
     */
    public static final ModuleCategory ENERGY           = new ModuleCategory();
    public static final ModuleCategory ARMOR            = new ModuleCategory();
    public static final ModuleCategory ARMOR_FEET       = new ModuleCategory();
    public static final ModuleCategory ARMOR_LEGS       = new ModuleCategory();
    public static final ModuleCategory ARMOR_CHEST      = new ModuleCategory();
    public static final ModuleCategory ARMOR_HEAD       = new ModuleCategory();
    /**
     * This if for DE's chestpeice which is technically a chest plate but its also its own separate thing.
     */
    public static final ModuleCategory CHESTPIECE       = new ModuleCategory();
    /**
     * Anything designed to deal damage.
     */
//    public static final ModuleCategory WEAPON           = new ModuleCategory();
    public static final ModuleCategory MELEE_WEAPON     = new ModuleCategory();
    public static final ModuleCategory RANGED_WEAPON    = new ModuleCategory();
    public static final ModuleCategory MINING_TOOL      = new ModuleCategory();
    public static final ModuleCategory TOOL_AXE         = new ModuleCategory();
    public static final ModuleCategory TOOL_SHOVEL      = new ModuleCategory();
    public static final ModuleCategory TOOL_HOE         = new ModuleCategory();
    //@formatter:on


    public ModuleCategory() {}
}
