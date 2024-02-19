package com.brandon3055.draconicevolution.api.modules.items;

import com.brandon3055.brandonscore.utils.Utils;
import com.brandon3055.draconicevolution.api.modules.Module;
import com.brandon3055.draconicevolution.api.modules.data.EnergyData;
import com.brandon3055.draconicevolution.api.modules.lib.ModuleImpl;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
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
    public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
        super.appendHoverText(stack, worldIn, tooltip, flagIn);
        if (stack.hasTag() && stack.getOrCreateTag().contains("stored_energy")) {
            tooltip.add(Component.literal(I18n.get("module.draconicevolution.energy.stored_energy")
                            + ": "
                            + Utils.formatNumber(stack.getOrCreateTag().getLong("stored_energy"))
                            + " "
                            + I18n.get("op.brandonscore." + (Screen.hasShiftDown() ? "operational_potential" : "op")))
                    .withStyle(ChatFormatting.GRAY));
        }
    }
}
