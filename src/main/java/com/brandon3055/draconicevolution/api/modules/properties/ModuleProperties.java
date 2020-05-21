package com.brandon3055.draconicevolution.api.modules.properties;

import com.brandon3055.brandonscore.api.TechLevel;
import com.brandon3055.draconicevolution.api.modules.Module;
import com.brandon3055.draconicevolution.api.modules.ModuleType;
import net.minecraft.util.text.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static net.minecraft.util.text.TextFormatting.*;

/**
 * Created by brandon3055 on 3/5/20.
 */
public class ModuleProperties<T extends ModuleData<T>> {
    private final TechLevel techLevel;
    private int width;
    private int height;
    private T data;

    public ModuleProperties(TechLevel techLevel, T data) {
        this(techLevel, -1, -1, data);
    }

    public ModuleProperties(TechLevel techLevel, int width, int height, T data) {
        this.techLevel = techLevel;
        this.width = width;
        this.height = height;
        this.data = data;
    }

    public T getData() {
        return data;
    }

    public int getWidth() {
        if (width == -1) throw new IllegalStateException("Module dimensions have not been set! " + getData());
        return width;
    }

    public int getHeight() {
        if (height == -1) throw new IllegalStateException("Module dimensions have not been set! " + getData());
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
     *
     * @param toolTip the item tool tip.
     */
    public void addStats(List<ITextComponent> toolTip, Module<?> module) {
        toolTip.add(new TranslationTextComponent("module.draconicevolution.module_type") //
                .applyTextStyle(GRAY) //
                .appendSibling(new StringTextComponent(": ") //
                        .appendSibling(techLevel.getDisplayName().applyTextStyle(techLevel.getTextColour())) //
                        .appendSibling(new StringTextComponent(": ")) //
                        .appendSibling(new TranslationTextComponent("module.draconicevolution." + module.getType().getName() + ".name").applyTextStyle(DARK_GREEN))));
        toolTip.add(new TranslationTextComponent("module.draconicevolution.grid_size") //
                .applyTextStyle(GRAY) //
                .appendSibling(new StringTextComponent(": ") //
                        .appendSibling(new StringTextComponent(getWidth() + "x" + getHeight()) //
                                .applyTextStyle(DARK_GREEN))));

        Map<ITextComponent, ITextComponent> map = new HashMap<>();
        getData().addInformation(map);
        map.forEach((name, value) -> toolTip.add(name.applyTextStyle(GRAY).appendSibling(new StringTextComponent(": ")).appendSibling(value.applyTextStyle(DARK_GREEN))));
    }
}
