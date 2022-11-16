package com.brandon3055.draconicevolution.api.modules.data;

import com.brandon3055.draconicevolution.api.modules.lib.ModuleContext;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
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
public class EnergyData implements ModuleData<EnergyData> {
    public static final EnergyData EMPTY = new EnergyData(0, 0);

    private final long capacity;
    private final long transfer;

    public EnergyData(long capacity, long transfer) {
        this.capacity = capacity;
        this.transfer = transfer;
    }

    public long getCapacity() {
        return capacity;
    }

    public long getTransfer() {
        return transfer;
    }

    @Override
    public EnergyData combine(EnergyData other) {
        return new EnergyData(capacity + other.capacity, transfer + other.transfer);
    }

    @Override
    public void addInformation(Map<Component, Component> map, ModuleContext context, boolean stack) {
        long capacity = getCapacity();
        long transfer = getTransfer();
        map.put(new TranslatableComponent("module.draconicevolution.energy.capacity"), new TranslatableComponent("module.draconicevolution.energy.capacity.value", ModuleData.formatNumber(capacity)));
        map.put(new TranslatableComponent("module.draconicevolution.energy.transfer"), new TranslatableComponent("module.draconicevolution.energy.transfer.value", ModuleData.formatNumber(transfer)));
    }
}
