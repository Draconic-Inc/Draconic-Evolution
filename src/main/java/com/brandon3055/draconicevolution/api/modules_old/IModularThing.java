//package com.brandon3055.draconicevolution.api.modules_old;
//
//import com.brandon3055.draconicevolution.api.TechLevel;
//import com.brandon3055.draconicevolution.modules.TileModuleGrid;
//
///**
// * Created by brandon3055 on 8/4/20.
// * Must be implemented by items that have a modular grid.
// * For tiles see {@link IModularTile}
// */
//public interface IModularThing {
//
//    /**
//     * @return the width of the module grid.
//     */
//    int getGridWidth();
//
//    /**
//     * @return the height of the module grid.
//     */
//    int getGridHeight();
//
//    /**
//     * @return the max module tech level accepted by this grid.
//     */
//    TechLevel getTechLevel();
//
////    /**
////     * @param stack the item stack if this is {@link IHasModuleGrid} is an item.
////     * @return the module grid for this item or tile.
////     */
////    //TODO I'm not sure if this is how i want to handle this. It leaves a lot of questions when it comes to ItemStack instance safety.
////    ModuleGrid getGrid(ItemStack stack);
//    //TODO i think i will just do new ModuleGrid(IHasModuleGrid); then grid.load(nbt) or something along those lines
//
//    /**
//     * M<ust be implemented by tiles that have a modular grid.
//     */
//    interface IModularTile extends IModularThing {
//
//        TileModuleGrid getModuleGrid();
//    }
//}
