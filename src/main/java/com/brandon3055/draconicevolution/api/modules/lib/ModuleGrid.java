package com.brandon3055.draconicevolution.api.modules.lib;

import com.brandon3055.draconicevolution.api.modules.Module;
import com.brandon3055.draconicevolution.api.capability.ModuleHost;
import com.brandon3055.draconicevolution.api.modules.lib.InstallResult.InstallResultType;
import com.brandon3055.draconicevolution.inventory.ContainerModuleHost;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.StringTextComponent;

import java.util.Iterator;
import java.util.Objects;

/**
 * Created by brandon3055 on 8/4/20.
 * This is the main class responsible for managing the module "grid" as well as saving and loading the grid via NBT
 */
public class ModuleGrid {

    private int xPos = 0;
    private int yPos = 0;
    public ContainerModuleHost<?> container;
    private PlayerInventory player;
    private int cellSize = 16;
    private Runnable onGridChange;

    public ModuleGrid(ContainerModuleHost<?> container, PlayerInventory player) {
        this.container = container;
        this.player = player;
    }

    public void setOnGridChange(Runnable onGridChange) {
        this.onGridChange = onGridChange;
    }

    private void onGridChange() {
        if (onGridChange != null) {
            onGridChange.run();;
        }
    }

    public void setPosition(int guiXPos, int guiYPos) {
        this.xPos = guiXPos;
        this.yPos = guiYPos;
    }

    public ModuleHost getModuleHost() {
        return container.getModuleHost();
    }

    public int getWidth() {
        return getModuleHost().getGridWidth();
    }

    public int getHeight() {
        return getModuleHost().getGridHeight();
    }

    public int getCellSize() {
        return cellSize;
    }

    public void setCellSize(int cellSize) {
        this.cellSize = cellSize;
    }

    public InstallResult cellClicked(GridPos pos, int button, ClickType clickType) {
        ItemStack stack = player.getItemStack();
        Module<?> module = ModuleItem.getModule(stack);

        //Sanity Checks
        if ((!stack.isEmpty() && module == null) || !pos.isValidCell()) {
            return null; //Player tried to insert an item that is not a valid module
        }

        if (clickType == ClickType.PICKUP) { ///Really this could be pick up or drop off
            if (!stack.isEmpty()) { //Try to insert module
                ModuleEntity entity = new ModuleEntity(module);
                entity.setPos(pos.gridX, pos.gridY);
                InstallResult result = checkInstall(entity);
                if (result.resultType == InstallResultType.YES) {
                    getModuleHost().getModuleEntities().add(entity);
                    entity.readFromItemStack(stack);
                    entity.onInstalled(container.getModuleContext());
                    stack.shrink(1);
                    onGridChange();
                    return null;
                }
                return result;
            }
            else if (pos.hasEntity()) { //Try to extract module
                ModuleEntity entity = pos.getEntity();
                stack = new ItemStack(entity.getModule().getItem());
                entity.writeToItemStack(stack);
                getModuleHost().getModuleEntities().remove(entity);
                entity.onRemoved(container.getModuleContext());
                player.setItemStack(stack);
                onGridChange();
            }
        }
        else if (clickType == ClickType.QUICK_MOVE) {
            if (pos.hasEntity()) { //Try to transfer module
                ModuleEntity entity = pos.getEntity();
                stack = new ItemStack(entity.getModule().getItem());
                entity.writeToItemStack(stack);
                if (player.addItemStackToInventory(stack)) {
                    getModuleHost().getModuleEntities().remove(entity);
                    onGridChange();
                    entity.onRemoved(container.getModuleContext());
                }
            }
        }
        else if (clickType == ClickType.PICKUP_ALL && module != null) {
            Iterator<ModuleEntity> i = getModuleHost().getModuleEntities().iterator();
            while (i.hasNext()) {
                ModuleEntity entity = i.next();
                if (entity.module == module) {
                    ItemStack modStack = new ItemStack(module.getItem());
                    entity.writeToItemStack(modStack);
                    if (Container.areItemsAndTagsEqual(stack, modStack) && stack.getCount() < stack.getMaxStackSize()) {
                        stack.grow(1);
                        i.remove();
                        entity.onRemoved(container.getModuleContext());
                    }
                }
            }
            onGridChange();
        }
        else if (clickType == ClickType.CLONE) {
            if (player.player.abilities.isCreativeMode && player.getItemStack().isEmpty() && pos.hasEntity()) {
                ModuleEntity entity = pos.getEntity();
                ItemStack modStack = new ItemStack(entity.module.getItem());
                entity.writeToItemStack(modStack);
                player.setItemStack(modStack);
            }
        }
        return null;
    }

    /**
     * This will attempt to install the module entity in the first available grid cell.
     */
    public boolean attemptInstall(ModuleEntity entity) {
        for (int y = 0; y < getWidth(); y++) {
            for (int x = 0; x < getWidth(); x++) {
                entity.setPos(x, y);
                if (checkInstall(entity).resultType == InstallResultType.YES) {
                    getModuleHost().getModuleEntities().add(entity);
                    onGridChange();
                    return true;
                }
            }
        }
        return false;
    }

    public InstallResult checkInstall(ModuleEntity entity) {
        if (getModuleHost().getModuleEntities().stream().anyMatch(entity::intersects)) {
            return new InstallResult(InstallResultType.NO, entity.module, null, new StringTextComponent("//Module does not fit in this space"));
        }
        if (entity.getMaxGridX() > getModuleHost().getGridWidth() || entity.getMaxGridY() > getModuleHost().getGridHeight()) {
            return new InstallResult(InstallResultType.NO, entity.module, null, new StringTextComponent("//Module out of bounds"));
        }
        InstallResult result = getModuleHost().checkAddModule(entity.module);
        if (result.resultType == InstallResultType.YES || result.resultType == InstallResultType.OVERRIDE) {
            return new InstallResult(InstallResultType.YES, entity.module, null, null);
        }
        return result;
    }

    /**
     * This method returns a cell reference for the specified grid position.
     * It should be noted that this is a throwaway object that will not be updated when the grid changes.
     * So once you are done with it throw it away and if you need it again in the future just request a new one.
     */
    public GridPos getCell(int gridX, int gridY) {
        if (gridX < 0 || gridX >= getModuleHost().getGridWidth() || gridY < 0 || gridY >= getModuleHost().getGridHeight()){
            return new GridPos(this); //This situation should ideally be avoided before we get this far but just in case.
        }
        return new GridPos(gridX, gridY, this);
    }

    /**
     * This is throwaway / short term object used to make interacting with cells a little cleaner.
     */
    public static class GridPos {
        private final int gridX;
        private final int gridY;
        private final ModuleGrid grid;
        private final ModuleEntity entity;

        GridPos(ModuleGrid grid) {
            this.grid = grid;
            this.gridY = -1;
            this.gridX = -1;
            this.entity = null;
        }

        public GridPos(int gridX, int gridY, ModuleGrid grid) {
            this.gridX = gridX;
            this.gridY = gridY;
            this.grid = grid;
            this.entity = grid.getModuleHost().getModuleEntities().stream().filter(module -> module.contains(gridX, gridY)).findFirst().orElse(null);
        }

        /**
         * @return true if this grid cell is occupied by a module entity.
         */
        public boolean hasEntity() {
            return entity != null;
        }

        /**
         * @return The entity occupying this cell.
         */
        public ModuleEntity getEntity() {
            return entity;
        }

        public int getGridX() {
            return gridX;
        }

        public int getGridY() {
            return gridY;
        }

        /**
         * @return true of this is the 'actual' position of the entity (top left) and not just a cell that falls within the entity's bounds.
         */
        public boolean isActualEntityPos() {
            return hasEntity() && entity.checkPos(gridX, gridY);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            GridPos gridPos = (GridPos) o;
            return gridX == gridPos.gridX &&
                    gridY == gridPos.gridY;
        }

        @Override
        public int hashCode() {
            return Objects.hash(gridX, gridY);
        }

        public boolean isValidCell() {
            return gridX != -1 && gridY != -1;
        }
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
