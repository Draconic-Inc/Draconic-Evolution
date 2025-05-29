package com.brandon3055.draconicevolution.api.modules.items;

import com.brandon3055.brandonscore.utils.Utils;
import com.brandon3055.draconicevolution.api.modules.Module;
import com.brandon3055.draconicevolution.api.modules.data.EnergyData;
import com.brandon3055.draconicevolution.api.modules.lib.ModuleImpl;
import com.brandon3055.draconicevolution.init.ItemData;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.CustomData;
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
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flagIn) {
        super.appendHoverText(stack, context, tooltip, flagIn);
        CompoundTag tag = stack.getOrDefault(ItemData.MODULE_ENTITY_TAG, CustomData.EMPTY).copyTag();

        if (tag.contains("stored_energy")) {
            tooltip.add(Component.literal(I18n.get("module.draconicevolution.energy.stored_energy")
                            + ": "
                            + Utils.formatNumber(tag.getLong("stored_energy"))
                            + " "
                            + I18n.get("op.brandonscore." + (Screen.hasShiftDown() ? "operational_potential" : "op")))
                    .withStyle(ChatFormatting.GRAY));
        }
    }
}
