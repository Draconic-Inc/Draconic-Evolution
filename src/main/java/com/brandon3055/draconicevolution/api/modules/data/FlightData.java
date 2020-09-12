package com.brandon3055.draconicevolution.api.modules.data;

import com.brandon3055.draconicevolution.DEConfig;
import com.brandon3055.draconicevolution.api.modules.lib.ModuleContext;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;

import java.util.Map;

/**
 * Created by brandon3055 on 3/5/20.
 */
public class FlightData implements ModuleData<FlightData> {
    private final boolean elytra;
    private final boolean creative;
    private final double elytraSpeed;

    public FlightData(boolean elytra, boolean creative, double elytraSpeed) {
        this.elytra = elytra;
        this.creative = creative;
        this.elytraSpeed = elytraSpeed;
    }

    public boolean elytra() {
        return elytra;
    }

    public boolean creative() {
        return creative;
    }

    public double getElytraSpeed() {
        return elytraSpeed;
    }

    @Override
    public FlightData combine(FlightData other) {
        return other;
    }

    @Override
    public void addInformation(Map<ITextComponent, ITextComponent> map, ModuleContext context, boolean stack) {
        map.put(new TranslationTextComponent("module.draconicevolution.flight.name"), new TranslationTextComponent("module.draconicevolution.flight." + elytra + "." + creative));
        if (elytra && elytraSpeed > 0) {
            map.put(new TranslationTextComponent("module.draconicevolution.flight.boost.name"), new StringTextComponent((int)(elytraSpeed * 100) + "%"));
        }
        if (elytra && !DEConfig.enableElytraFlight) {
            map.put(new StringTextComponent("Elytra Flight").applyTextStyle(TextFormatting.RED), new StringTextComponent("Disabled by server").applyTextStyle(TextFormatting.RED));
        }
        if (creative && !DEConfig.enableCreativeFlight) {
            map.put(new StringTextComponent("Creative Flight").applyTextStyle(TextFormatting.RED), new StringTextComponent("Disabled by server").applyTextStyle(TextFormatting.RED));
        }
    }
}