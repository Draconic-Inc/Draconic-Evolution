package com.brandon3055.draconicevolution.api.modules.lib;

import com.brandon3055.draconicevolution.api.modules.IModule;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;

import java.awt.*;

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
     *
     * @param stack The module stack
     */
    public void writeToItemStack(ItemStack stack) {}

    /**
     * Called when a module is inserted into a module grid.
     * This allows you to load any previously saved data.
     *
     * @param stack The module stack
     * @see #writeToItemStack(ItemStack)
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

    public int getMaxGridX() {
        return gridX + module.getProperties().getWidth();
    }

    public int getMaxGridY() {
        return gridY + module.getProperties().getHeight();
    }

    public int getWidth() {
        return module.getProperties().getWidth();
    }

    public int getHeight() {
        return module.getProperties().getHeight();
    }

    //end

    //region Helpers

    public boolean checkPos(int gridX, int gridY) {
        return this.gridX == gridX && this.gridY == gridY;
    }

    /**
     * Returns true if the specified grid coordinates are within this modules grid bounds.
     *
     * @param gridX text x position
     * @param gridY text y position
     * @return true if the specified position is within this modules grid bounds.
     */
    public boolean contains(int gridX, int gridY) {
        return gridX >= this.gridX && gridX < this.gridX + module.getProperties().getWidth() && gridY >= this.gridY && gridY < this.gridY + module.getProperties().getHeight();
    }

    /**
     * Returns true if any part of this module overlaps with any part of the other module.
     */
    public boolean intersects(ModuleEntity other) {
        int tw = getWidth();
        int th = getHeight();
        int rw = other.getWidth();
        int rh = other.getHeight();
        if (rw <= 0 || rh <= 0 || tw <= 0 || th <= 0) {
            return false;
        }
        int tx = gridX;
        int ty = gridY;
        int rx = other.gridX;
        int ry = other.gridY;
        rw += rx;
        rh += ry;
        tw += tx;
        th += ty;
        //      overflow || intersect
        return ((rw < rx || rw > tx) &&
                (rh < ry || rh > ty) &&
                (tw < tx || tw > rx) &&
                (th < ty || th > ry));
    }
}
