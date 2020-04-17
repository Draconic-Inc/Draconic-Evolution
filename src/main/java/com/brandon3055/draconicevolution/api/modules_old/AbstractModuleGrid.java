//package com.brandon3055.draconicevolution.api.modules_old;
//
//import com.brandon3055.brandonscore.utils.LogHelperBC;
//import com.brandon3055.draconicevolution.init.ModuleRegistry;
//import net.minecraft.nbt.CompoundNBT;
//import net.minecraft.util.ResourceLocation;
//
//import javax.annotation.Nullable;
//import java.util.ArrayList;
//import java.util.List;
//
///**
// * Created by brandon3055 on 8/4/20.
// * This is the main class responsible for managing the module "grid" as well as saving and loading the grid via NBT
// */
//public class AbstractModuleGrid<T extends IModularThing> {
//
//    public T modularThing;
//    protected List<GridModule> gridModules = new ArrayList<>();
//
//    public AbstractModuleGrid(T modularThing) {
//        this.modularThing = modularThing;
//    }
//
//    public int getWidth() {
//        return modularThing.getGridWidth();
//    }
//
//    public int getHeight() {
//        return modularThing.getGridHeight();
//    }
//
//    /**
//     * Returns the {@link GridModule} occupying the specified grid position (if there is one)
//     * @param x grid x coord
//     * @param y grid y coord
//     * @return the module occupying this position if there is one.
//     */
//    @Nullable
//    public GridModule getModule(int x, int y) {
//        return gridModules.parallelStream().filter(module -> module.contains(x, y)).findFirst().orElse(null);
//    }
//
//    public void clear() {
//        gridModules.clear();
//    }
//
//    /**
//     * This represents a module that is installed in the grid.
//     */
//    public static class GridModule {
//        public int x;
//        public int y;
//        public IModule module;
//
//        public GridModule(int x, int y, IModule module) {
//            this.x = x;
//            this.y = y;
//            this.module = module;
//        }
//
//        /**
//         * Returns true if the specified grid coordinates are within this modules grid bounds.
//         * @param x text x position
//         * @param y text y position
//         * @return true if the specified position is within this modules grid bounds.
//         */
//        public boolean contains(int x, int y) {
//            return x >= this.x && x < this.x + module.getWidth() && y >= this.y && y < this.y + module.getHeight();
//        }
//
//        public ResourceLocation getModuleName() {
//            return ModuleRegistry.INSTANCE.getModuleName(module);
//        }
//
//        public void writeToNBT(CompoundNBT compound) {
//            compound.putByte("x", (byte) x);
//            compound.putByte("y", (byte) y);
//            compound.putString("name", getModuleName().toString());
//        }
//
//        @Nullable
//        public static GridModule readFromNBT(CompoundNBT compound) {
//            int x = compound.getByte("x");
//            int y = compound.getByte("y");
//            ResourceLocation name = new ResourceLocation(compound.getString("name"));
//            IModule module = ModuleRegistry.INSTANCE.getModuleByName(name);
//            if (module != null) {
//                return new GridModule(x, y, module);
//            }
//            LogHelperBC.warn("Skipping module with missing registry entry: " + name);
//            return null;
//        }
//    }
//
//
//
//    //TODO Since "modules" are Items and Items are immutable single instance objects i am going to need a way to account for multiple identical
//    // being installed at the same time. Because if i list all installed modules and there are say 5 draconic energy modules then all 5 modules
//    // will be the same instance.
//
//
//
//
////    public Collection<? extends IModule> getModulesByType(ModuleType<?> type) {
////        return new ArrayList<>();
////    }
////
////    public int getModuleCount(IModule module) {
////        return 0;
////    }
//}
