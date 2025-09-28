package com.brandon3055.draconicevolution.api.modules.items;

import com.brandon3055.brandonscore.utils.Utils;
import com.brandon3055.draconicevolution.api.modules.Module;
import com.brandon3055.draconicevolution.api.modules.data.EnergyData;
import com.brandon3055.draconicevolution.init.ItemData;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;
import java.util.function.Supplier;

public class EnergyModuleItem extends ModuleItem<EnergyData> {

    public EnergyModuleItem(Properties properties, Supplier<Module<?>> moduleSupplier) {
        super(properties, moduleSupplier);
    }

    public EnergyModuleItem(Supplier<Module<?>> moduleSupplier) {
        super(moduleSupplier);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flagIn) {
        super.appendHoverText(stack, context, tooltip, flagIn);
        long energy = stack.getOrDefault(ItemData.ENERGY_MODULE_ENERGY, 0L);

        if (energy > 0) {

            tooltip.add(Component.translatable("module.draconicevolution.energy.stored_energy")
                    .append(": ")
                    .append(Utils.formatNumber(energy))
                    .append(" ")
                    .append(Component.translatable("op.brandonscore." + (Screen.hasShiftDown() ? "operational_potential" : "op")))
                    .withStyle(ChatFormatting.GRAY));
        }
    }
}
