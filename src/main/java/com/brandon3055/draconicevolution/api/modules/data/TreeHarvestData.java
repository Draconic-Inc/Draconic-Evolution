package com.brandon3055.draconicevolution.api.modules.data;

import com.brandon3055.draconicevolution.api.modules.lib.ModuleContext;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;

/**
 * Created by brandon3055 on 3/5/20.
 */
public class TreeHarvestData implements ModuleData<TreeHarvestData> {
    private final int range;

    public TreeHarvestData(int range) {
        this.range = range;
    }

    public int getRange() {
        return range;
    }

    @Override
    public TreeHarvestData combine(TreeHarvestData other) {
        return new TreeHarvestData(range + other.range);
    }

    @Override
    public void addInformation(Map<Component, Component> map, ModuleContext context) {
        if (getRange() > 0) {
            map.put(new TranslatableComponent("module.draconicevolution.tree_harvest_range.name"), new TranslatableComponent("module.draconicevolution.tree_harvest_range.value", range));
        }
    }

    @Override
    public void addHostHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {


    }
}