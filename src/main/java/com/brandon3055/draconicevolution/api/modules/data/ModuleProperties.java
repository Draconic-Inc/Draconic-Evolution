package com.brandon3055.draconicevolution.api.modules.data;

import com.brandon3055.brandonscore.api.TechLevel;
import com.brandon3055.draconicevolution.api.modules.Module;
import com.brandon3055.draconicevolution.api.modules.ModuleType;
import com.brandon3055.draconicevolution.api.modules.lib.ModuleContext;
import com.brandon3055.draconicevolution.api.modules.lib.StackModuleContext;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

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
    public void addStats(List<Component> toolTip, Module<?> module, ModuleContext context) {
        toolTip.add(new TranslatableComponent("module.draconicevolution.module_type")
                .withStyle(ChatFormatting.GRAY)
                .append(": ")
                .append(module.getType().getDisplayName()
                        .withStyle(techLevel.getTextColour())));
        toolTip.add(new TranslatableComponent("module.draconicevolution.grid_size")
                .withStyle(ChatFormatting.GRAY)
                .append(": ")
                .append(new TextComponent(getWidth() + "x" + getHeight())
                        .withStyle(ChatFormatting.DARK_GREEN)));

        Map<Component, Component> map = new HashMap<>();
        getData().addInformation(map, context);
        map.forEach((name, value) -> {
            if (value == null) {
                toolTip.add(name.plainCopy().withStyle(ChatFormatting.GRAY));
            } else {
                toolTip.add(name.plainCopy().withStyle(ChatFormatting.GRAY).append(": ").append(ChatFormatting.DARK_GREEN + value.plainCopy().getString().replace("\n", " ")));
            }
        });
    }
}
