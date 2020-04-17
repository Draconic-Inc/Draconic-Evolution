//package com.brandon3055.draconicevolution.api.modules_old;
//
//import com.brandon3055.draconicevolution.api.TechLevel;
//import com.brandon3055.draconicevolution.items.modules.ModuleItemTest;
//
//import java.util.List;
//
///**
// * Created by brandon3055 on 8/4/20.
// *
// * An {@link IModule} IS and Item. They are one and the same. So IModule must be implemented by your module item class.
// * @see ModuleItemTest
// */
//public interface IModule<T extends IModule<T>> {
//
//    ModuleType<T> getModuleType();
//
//    /**
//     * @return the minimum device/item tech level required in order to install this module.
//     */
//    TechLevel getTechLevel();
//
//    /**
//     * @return width of module in grid
//     */
//    default int getWidth() {
//        return getModuleType().getWidth();
//    }
//
//    /**
//     * @return height of module in grid
//     */
//    default int getHeight() {
//        return getModuleType().getHeight();
//    }
//
//    /**
//     * If you are overriding this in your {@link IModule} then you must use {@link #getConflictingModules(AbstractModuleGrid, List)}
//     * to exclude other modules of the same type.
//     *
//     * @return max installable modules of this type.
//     */
//    default int getMaxModuleCount() {
//        return getModuleType().getMaxModuleCount();
//    }
//
//    /**
//     * @param grid the module grid.
//     * @param conflicts a list of installed modules that conflict with this one.
//     */
//    default void getConflictingModules(AbstractModuleGrid grid, List<IModule> conflicts) {
//        getModuleType().getConflictingModules(grid, conflicts);
//    }
//}
