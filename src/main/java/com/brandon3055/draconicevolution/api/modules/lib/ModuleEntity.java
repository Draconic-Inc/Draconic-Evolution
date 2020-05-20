package com.brandon3055.draconicevolution.api.modules.lib;

import com.brandon3055.draconicevolution.api.config.BooleanProperty;
import com.brandon3055.draconicevolution.api.config.ConfigProperty;
import com.brandon3055.draconicevolution.api.config.IntegerProperty;
import com.brandon3055.draconicevolution.api.modules.Module;
import com.brandon3055.draconicevolution.api.modules.ModuleType;
import com.brandon3055.draconicevolution.api.modules.properties.ModuleData;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;

import java.util.*;

/**
 * Created by brandon3055 on 18/4/20.
 */
public class ModuleEntity {

    protected final Module<?> module;
    protected Map<String, ConfigProperty> propertyMap = new HashMap<>();
    protected boolean savePropertiesToItem = false;
    protected int gridX;
    protected int gridY;

    public ModuleEntity(Module<?> module) {
        this.module = module;
//        addProperty(new IntegerProperty("testModEntProp", 0).range(0, 100));
//        savePropertiesToItem = true;
    }

    public void tick(ModuleContext context) {

    }

    public void onInstalled(ModuleContext context) {

    }

    public void onRemoved(ModuleContext context) {

    }

    /**
     * This can be used to add per module properties.
     * Properties should be added via your {@link ModuleEntity} constructor. These will be saved and loaded along
     * with the rest of the entities data. If you set 'savePropertiesToItem' to true the properties
     * will also be saved and loaded from the ItemStack when this module is removed or installed.<br><br>
     *
     * These properties will be generated for every single instance of this module that is installed.
     * Therefor these are best used with modules that have a max install count of 1.<br><br>
     *
     * If you need to add "global" where you have a single set of properties that apply to all installed modules
     * rather than a set of properties for each module see {@link ModuleType#getTypeProperties(ModuleData, Map)} )}
     * @param property the property to add.
     * @return the property for convenience.
     * @see ModuleType#getTypeProperties(ModuleData, Map)
     */
    public ConfigProperty addProperty(ConfigProperty property) {
        propertyMap.put(property.getName(), property);
        property.generateUnique();
        return property;
    }

    public Collection<ConfigProperty> getEntityProperties() {
        return propertyMap.values();
    }

    public void writeToNBT(CompoundNBT compound) {
        compound.putByte("x", (byte) gridX);
        compound.putByte("y", (byte) gridY);
        if (!propertyMap.isEmpty()) {
            CompoundNBT properties = new CompoundNBT();
            propertyMap.forEach((name, property) -> properties.put(name, property.serializeNBT()));
            compound.put("properties", properties);
        }
    }

    public void readFromNBT(CompoundNBT compound) {
        gridX = compound.getByte("x");
        gridY = compound.getByte("y");
        CompoundNBT properties = compound.getCompound("properties");
        propertyMap.forEach((name, property) -> property.deserializeNBT(properties.getCompound(name)));
    }

    /**
     * Called when the module is removed from the module grid.
     * This allows you to store data on the module item stack.
     *
     * @param stack The module stack
     */
    public void writeToItemStack(ItemStack stack) {
        if (savePropertiesToItem && !propertyMap.isEmpty()) {
            CompoundNBT properties = stack.getOrCreateChildTag("properties");
            propertyMap.forEach((name, property) -> properties.put(name, property.serializeNBT()));
        }
    }

    /**
     * Called when a module is inserted into a module grid.
     * This allows you to load any previously saved data.
     *
     * @param stack The module stack
     * @see #writeToItemStack(ItemStack)
     */
    public void readFromItemStack(ItemStack stack) {
        CompoundNBT properties;
        if (savePropertiesToItem && (properties = stack.getChildTag("properties")) != null) {
            propertyMap.forEach((name, property) -> property.deserializeNBT(properties.getCompound(name)));
        }
    }

    //region Setters / Getters

    public Module<?> getModule() {
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
