package com.brandon3055.draconicevolution.api.modules.lib;

import com.brandon3055.draconicevolution.api.capability.ModuleHost;
import com.brandon3055.draconicevolution.api.modules.Module;
import com.brandon3055.draconicevolution.api.modules.lib.InstallResult.InstallResultType;
import com.google.common.collect.ImmutableList;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Created by brandon3055 on 8/4/20.
 * This is the main class responsible for managing the module "grid" as well as saving and loading the grid via NBT
 */
public class ModuleGrid {

    protected int xPos = 0;
    protected int yPos = 0;
    public ModuleHostContainer container;
    protected Inventory player;
    protected int cellSize = 16;
    protected Runnable onGridChange;

    public ModuleGrid(ModuleHostContainer container, Inventory player) {
        this.container = container;
        this.player = player;
    }

    public void setOnGridChange(Runnable onGridChange) {
        this.onGridChange = onGridChange;
    }

    protected void onGridChange() {
        if (onGridChange != null) {
            onGridChange.run();;
        }
        container.onGridChange();
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

    public InstallResult cellClicked(GridPos pos, double x, double y, int button, ClickType clickType) {
        ItemStack stack = player.player.containerMenu.getCarried();
        Module<?> module = ModuleItem.getModule(stack);
        boolean holdingStack = !stack.isEmpty();
        ModuleContext context = container.getModuleContext();

        ModuleEntity<?> posEntity = pos.getEntity();
        if (posEntity != null && posEntity.moduleClicked(player.player, x, y, button, clickType)) {
            return null;
        }

        //Sanity Checks
        if ((holdingStack && module == null) || !pos.isValidCell()) {
            return null; //Player tried to insert an item that is not a valid module
        }

        ModuleHost host = getModuleHost();
        //Really this could be pick up or drop off
        if (clickType == ClickType.PICKUP) {
            if (holdingStack) { //Try to insert module
                ModuleEntity<?> entity = module.createEntity();
                entity.setPos(pos.gridX, pos.gridY);
                InstallResult result = checkInstall(entity);
                if (result.resultType == InstallResultType.YES) {
                    entity.readFromItemStack(stack, context);
                    host.addModule(entity, context);
                    stack.shrink(1);
                    onGridChange();
                    return null;
                }
                return result;
            }
            else if (pos.hasEntity()) { //Try to extract module
                ModuleEntity<?> entity = pos.getEntity();
                ItemStack extracted = new ItemStack(entity.getModule().getItem());
                entity.writeToItemStack(extracted, context);
                List<Component> error = new ArrayList<>();
                if (!host.checkRemoveModule(entity, error)) {
                    return new InstallResult(InstallResultType.NO, null, null, error);
                }
                host.removeModule(entity, context);
                player.player.containerMenu.setCarried(extracted);
                onGridChange();
            }
        }
        else if (clickType == ClickType.QUICK_MOVE) {
            if (pos.hasEntity()) { //Try to transfer module
                ModuleEntity<?> entity = pos.getEntity();
                ItemStack extracted = new ItemStack(entity.getModule().getItem());
                entity.writeToItemStack(extracted, context);
                List<Component> error = new ArrayList<>();
                if (!host.checkRemoveModule(entity, error)) {
                    return new InstallResult(InstallResultType.NO, null, null, error);
                }
                if (player.add(extracted)) {
                    host.removeModule(entity, context);
                    onGridChange();
                }
            }
        }
        else if (clickType == ClickType.PICKUP_ALL && module != null) {
            for (ModuleEntity<?> entity : ImmutableList.copyOf(host.getModuleEntities())) {
                if (entity.module == module) {
                    ItemStack modStack = new ItemStack(module.getItem());
                    entity.writeToItemStack(modStack, context);
                    List<Component> error = new ArrayList<>();
                    if (!host.checkRemoveModule(entity, error)) {
                        return new InstallResult(InstallResultType.NO, null, null, error);
                    }
                    if (ItemStack.isSameItemSameTags(stack, modStack) && stack.getCount() < stack.getMaxStackSize()) {
                        stack.grow(1);
                        host.removeModule(entity, context);
                    }
                }
                onGridChange();
            }
        }
        else if (clickType == ClickType.CLONE) {
            if (player.player.getAbilities().instabuild && player.player.inventoryMenu.getCarried().isEmpty() && pos.hasEntity()) {
                ModuleEntity<?> entity = pos.getEntity();
                ItemStack modStack = new ItemStack(entity.module.getItem());
                entity.writeToItemStack(modStack, context);
                player.player.containerMenu.setCarried(modStack);
            }
        }
        return null;
    }

    /**
     * This will attempt to install the module entity in the first available grid cell.
     */
    public boolean attemptInstall(ModuleEntity<?> entity) {
        for (int y = 0; y < getHeight(); y++) {
            for (int x = 0; x < getWidth(); x++) {
                entity.setPos(x, y);
                if (checkInstall(entity).resultType == InstallResultType.YES) {
                    getModuleHost().addModule(entity, container.getModuleContext());
                    onGridChange();
                    return true;
                }
            }
        }
        return false;
    }

    public InstallResult checkInstall(ModuleEntity<?> entity) {
        ModuleHost host = getModuleHost();
        if (host.getHostTechLevel().index < entity.module.getModuleTechLevel().index) {
            return new InstallResult(InstallResultType.NO, entity.module, null, new TranslatableComponent("modular_item.draconicevolution.cant_install.level_high"));
        }
        if (!host.isModuleSupported(entity)) {
            return new InstallResult(InstallResultType.NO, entity.module, null, new TranslatableComponent("modular_item.draconicevolution.cant_install.not_supported"));
        }
        if (host.getModuleEntities().stream().anyMatch(entity::intersects)) {
            return new InstallResult(InstallResultType.NO, entity.module, null, new TranslatableComponent("modular_item.draconicevolution.cant_install.wont_fit"));
        }
        if (entity.getMaxGridX() > host.getGridWidth() || entity.getMaxGridY() > getModuleHost().getGridHeight()) {
            return new InstallResult(InstallResultType.NO, entity.module, null, new TranslatableComponent("modular_item.draconicevolution.cant_install.wont_fit"));
        }
        InstallResult result = ModuleHost.checkAddModule(host, entity.module);
        if (result.resultType == InstallResultType.YES || result.resultType == InstallResultType.OVERRIDE) {
            return new InstallResult(InstallResultType.YES, entity.module, null, (List<Component>) null);
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
        private final ModuleEntity<?> entity;

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
        public ModuleEntity<?> getEntity() {
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
}
