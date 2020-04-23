package com.brandon3055.draconicevolution.api.modules.properties;

import com.brandon3055.draconicevolution.api.TechLevel;
import com.brandon3055.draconicevolution.api.modules.ModuleType;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;

import java.util.List;

/**
 * Created by covers1624 on 4/16/20.
 */
public abstract class ModuleProperties<T extends ModuleProperties<T>> {
    private TechLevel techLevel;
    private int width = -1;
    private int height = -1;

    public ModuleProperties(TechLevel techLevel) {
        this.techLevel = techLevel;
    }

    public ModuleProperties(TechLevel techLevel, int width, int height) {
        this.techLevel = techLevel;
        this.setDimensions(width, height);
    }

    @SuppressWarnings("unchecked")
    public T setDimensions(int width, int height) {
        this.width = width;
        this.height = height;
        return (T) this;
    }

    public int getWidth() {
        if (width == -1) throw new IllegalStateException("Module dimensions have not been set!");
        return width;
    }

    public int getHeight() {
        if (height == -1) throw new IllegalStateException("Module dimensions have not been set!");
        return height;
    }

    public TechLevel getTechLevel() {
        return techLevel;
    }

    public void loadDefaults(ModuleType<T> moduleType) {
        if (width == -1 && moduleType.getDefaultWidth() != -1) {
            width = moduleType.getDefaultWidth();
        }
        if (height == -1 && moduleType.getDefaultHeight() != -1) {
            height = moduleType.getDefaultHeight();
        }
    }

    /**
     * Add information to the module item tooltip.
     * @param toolTip the item tool tip.
     */
    public void addInformation(List<ITextComponent> toolTip) {
        //TODO add info like module type and module grid size
        toolTip.add(new StringTextComponent("//TODO"));
    }

    /**
     * This method is used to combine / add up the total stats of all installed modules of a specific type.
     * This is then displayed in the module configuration GUI.
     * For example if there are multiple energy modules installed this will be used to display the total added energy capacity.
     *
     * @param propertiesList List of module properties for all installed modules of this type.
     * @param combinedStats The list to which the combined stats for this module type should be added.
     */
    public abstract void addCombinedStats(List<T> propertiesList, List<ITextComponent> combinedStats);
}
