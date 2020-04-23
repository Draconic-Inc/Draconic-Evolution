package com.brandon3055.draconicevolution.api.modules.lib;

import com.brandon3055.draconicevolution.api.modules.capability.IModuleHost;

/**
 * Created by brandon3055 on 8/4/20.
 * This is the main class responsible for managing the module "grid" as well as saving and loading the grid via NBT
 */
public class ModuleGrid {

    private int xPos = 0;
    private int yPos = 0;
    private IModuleHost moduleHost;
    private int cellSize = 16;

    public ModuleGrid(IModuleHost moduleHost) {
        this.moduleHost = moduleHost;
    }

    public void setPosition(int guiXPos, int guiYPos) {
        this.xPos = guiXPos;
        this.yPos = guiYPos;
    }

    public int getWidth() {
        return moduleHost.getGridWidth();
    }

    public int getHeight() {
        return moduleHost.getGridHeight();
    }

    public int getCellSize() {
        return cellSize;
    }

    public void setCellSize(int cellSize) {
        this.cellSize = cellSize;
    }

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




}
