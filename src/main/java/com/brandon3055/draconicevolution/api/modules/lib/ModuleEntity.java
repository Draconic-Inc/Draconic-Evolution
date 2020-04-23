package com.brandon3055.draconicevolution.api.modules.lib;

import com.brandon3055.draconicevolution.api.modules.IModule;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;

/**
 * Created by brandon3055 on 18/4/20.
 */
public class ModuleEntity {

    protected final IModule<?> module;
    protected int gridX;
    protected int gridY;

    public ModuleEntity(IModule<?> module) {
        this.module = module;
    }

    public void tick(ModuleContext context) {

    }

    public void writeToNBT(CompoundNBT compound) {
        compound.putByte("x", (byte) gridX);
        compound.putByte("y", (byte) gridY);
    }

    public void readFromNBT(CompoundNBT compound) {
        gridX = compound.getByte("x");
        gridY = compound.getByte("y");
    }

    /**
     * Called when the module is removed from the module grid.
     * This allows you to store data on the module item stack.
     * @param stack The module stack
     */
    public void writeToItemStack(ItemStack stack) {}

    /**
     * Called when a module is inserted into a module grid.
     * This allows you to load any previously saved data.
     *
     * @see #writeToItemStack(ItemStack)
     * @param stack The module stack
     */
    public void readFromItemStack(ItemStack stack) {}

    //region Setters / Getters

    public IModule<?> getModule() {
        return module;
    }

    public void setPos(int gridX, int gridY) {
        setGridX(gridX);
        setGridY(gridY);
    }

    public void setGridX(int gridX) {
        this.gridX = gridX;
    }

    public void setGridY(int gridY) {
        this.gridY = gridY;
    }

    public int getGridX() {
        return gridX;
    }

    public int getGridY() {
        return gridY;
    }

    //end

    //region Helpers

    /**
     * Returns true if the specified grid coordinates are within this modules grid bounds.
     * @param x text x position
     * @param y text y position
     * @return true if the specified position is within this modules grid bounds.
     */
    public boolean contains(int x, int y) {
        return x >= this.gridX && x < this.gridX + module.getProperties().getWidth() && y >= this.gridY && y < this.gridY + module.getProperties().getHeight();
    }

    //end
}
