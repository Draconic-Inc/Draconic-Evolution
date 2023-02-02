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
    private final int speed;

    public TreeHarvestData(int range, int speed) {
        this.range = range;
        this.speed = speed;
    }

    public int getRange() {
        return range;
    }

    public int getSpeed() {
        return speed;
    }

    @Override
    public TreeHarvestData combine(TreeHarvestData other) {
        return new TreeHarvestData(range + other.range, speed + other.speed);
    }

    @Override
    public void addInformation(Map<Component, Component> map, ModuleContext context) {
        if (getRange() > 0) {
            map.put(new TranslatableComponent("module.draconicevolution.tree_harvest_range.name"), new TranslatableComponent("module.draconicevolution.tree_harvest_range.value", range));
        }
        if (getSpeed() > 0) {
            map.put(new TranslatableComponent("module.draconicevolution.tree_harvest_speed.name"), new TranslatableComponent("module.draconicevolution.tree_harvest_speed.value", speed));
        }
    }

    @Override
    public void addHostHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {


    }
}
