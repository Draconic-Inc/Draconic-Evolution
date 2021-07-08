package com.brandon3055.draconicevolution.api.modules.data;

import com.brandon3055.brandonscore.api.TechLevel;
import com.brandon3055.draconicevolution.api.modules.Module;
import com.brandon3055.draconicevolution.api.modules.ModuleType;
import net.minecraft.util.text.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

import static net.minecraft.util.text.TextFormatting.*;

/**
 * Created by brandon3055 on 3/5/20.
 */
public class ModuleProperties<T extends ModuleData<T>> {
    private final TechLevel techLevel;
    private int width;
    private int height;
    private T data;
    private Function<Module<T>, T> dataGenerator;

    public ModuleProperties(TechLevel techLevel, Function<Module<T>, T> dataGenerator) {
        this(techLevel, -1, -1, dataGenerator);
    }

    public ModuleProperties(TechLevel techLevel, int width, int height, Function<Module<T>, T> dataGenerator) {
        this.techLevel = techLevel;
        this.width = width;
        this.height = height;
        this.dataGenerator = dataGenerator;
    }

    public void reloadData(Module<T> module) {
        data = dataGenerator.apply(module);
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
        toolTip.add(new TranslationTextComponent("module.draconicevolution.module_type")
                .withStyle(GRAY)
                .append(": ")
                .append(techLevel.getDisplayName()
                        .withStyle(techLevel.getTextColour()))
                .append(" ")
                .append(module.getType().getDisplayName()
                        .withStyle(techLevel.getTextColour())));
        toolTip.add(new TranslationTextComponent("module.draconicevolution.grid_size")
                .withStyle(GRAY)
                .append(": ")
                .append(new StringTextComponent(getWidth() + "x" + getHeight())
                        .withStyle(DARK_GREEN)));

        Map<ITextComponent, ITextComponent> map = new HashMap<>();
        getData().addInformation(map, null, true);
        map.forEach((name, value) -> {
            if (value == null) {
                toolTip.add(name.plainCopy().withStyle(GRAY));
            } else {
                toolTip.add(name.plainCopy().withStyle(GRAY).append(": ").append(value.plainCopy().withStyle(DARK_GREEN).getString().replace("\n", " ")));
            }
        });
    }
}
