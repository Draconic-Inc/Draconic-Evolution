package com.brandon3055.draconicevolution.api.modules.lib;

import com.brandon3055.brandonscore.utils.Utils;
import com.brandon3055.draconicevolution.api.modules.Module;
import com.brandon3055.draconicevolution.api.modules.data.EnergyData;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Supplier;

public class EnergyModuleItem extends ModuleItem<EnergyData> {
    public EnergyModuleItem(Properties properties, Supplier<Module<EnergyData>> moduleSupplier) {
        super(properties, moduleSupplier);
    }

    public EnergyModuleItem(Properties properties) {
        super(properties);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        super.appendHoverText(stack, worldIn, tooltip, flagIn);
        if (stack.hasTag() && stack.getOrCreateTag().contains("stored_energy")) {
            tooltip.add(new StringTextComponent(I18n.get("module.draconicevolution.energy.stored_energy")
                    + ": "
                    + Utils.formatNumber(stack.getOrCreateTag().getLong("stored_energy"))
                    + " "
                    + I18n.get("op.brandonscore." + (Screen.hasShiftDown() ? "operational_potential" : "op")))
                    .withStyle(TextFormatting.GRAY));
        }
    }
}
