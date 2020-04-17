//package com.brandon3055.draconicevolution.api.modules_old;
//
//
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.List;
//
///**
// * Created by brandon3055 on 8/4/20.
// * This is the underlying type for a module. For example if you wanted to implement another energy capacity module
// * you would simply register a new module that uses the "energy storage" module type.
// */
//public class ModuleType<T extends IModule> {
//
//    //General Modules
//    public static ModuleType<ILongModule> ENERGY_STORAGE = new ModuleType<>();
//    public static ModuleType<IModule> ENERGY_LINK = new ModuleType<>().setSize(3, 3);
//    public static ModuleType<IIntModule> AREA_OF_EFFECT = new ModuleType<IIntModule>().setSize(2, 2);
//    public static ModuleType<IIntModule> SPEED = new ModuleType<IIntModule>().setSize(2, 2); //Maybe a little to generic?
//
//    //Armor Modules
//    public static ModuleType<IModule> SHIELD_CONTROLLER = new ModuleType<>().setSize(3, 3).setMaxModuleCount(1);
//    public static ModuleType<IIntModule> SHIELD_CAPACITY = new ModuleType<>();
//    public static ModuleType<IIntModule> SHIELD_RECHARGE = new ModuleType<>();
//    public static ModuleType<IModule> LAST_STAND = new ModuleType<>().setSize(2, 2).setMaxModuleCount(1);
//    public static ModuleType<IModule> CREATIVE_FLIGHT = new ModuleType<>().setSize(3, 3).setMaxModuleCount(1);
//    public static ModuleType<IModule> ELYTRA_FLIGHT = new ModuleType<>().setSize(2, 2).setMaxModuleCount(1);
//
//    //Tool Modules
//
//
//    private int maxModuleCount = -1;
//    private int width = 1;
//    private int height = 1;
//    private List<ModuleType<?>> conflictingTypes = new ArrayList<>();
//
//    public ModuleType() {}
//
//    public ModuleType<T> addConflictingTypes(ModuleType<?>... conflictingTypes) {
//        this.conflictingTypes.addAll(Arrays.asList(conflictingTypes));
//        return this;
//    }
//
//    /**
//     * @param maxModuleCount the maximum number of modules of this type that can be installed simultaneously.
//     */
//    public ModuleType<T> setMaxModuleCount(int maxModuleCount) {
//        this.maxModuleCount = maxModuleCount;
//        return this;
//    }
//
//    /**
//     * @return the maximum number of modules of this type that can be installed simultaneously.
//     */
//    protected int getMaxModuleCount() {
//        return maxModuleCount;
//    }
//
//    /**
//     * @param width the default width of this module type when added to the module grid.
//     */
//    public ModuleType<T> setWidth(int width) {
//        this.width = width;
//        return this;
//    }
//
//    /**
//     * @param height the default height of this module type when added to the module grid.
//     */
//    public ModuleType<T> setHeight(int height) {
//        this.height = height;
//        return this;
//    }
//
//    /**
//     * @param width  the default width of this module type when added to the module grid.
//     * @param height the default height of this module type when added to the module grid.
//     */
//    public ModuleType<T> setSize(int width, int height) {
//        this.width = width;
//        this.height = height;
//        return this;
//    }
//
//    /**
//     * @return the default width of this module type when added to the module grid.
//     */
//    protected int getWidth() {
//        return width;
//    }
//
//    /**
//     * @return the default height of this module type when added to the module grid.
//     */
//    protected int getHeight() {
//        return height;
//    }
//
//    public void getConflictingModules(AbstractModuleGrid grid, List<IModule> conflicts) {
////        conflictingTypes.forEach(type -> conflicts.addAll(grid.getModulesByType(type)));
//    }
//
//    //Module Size
//}
