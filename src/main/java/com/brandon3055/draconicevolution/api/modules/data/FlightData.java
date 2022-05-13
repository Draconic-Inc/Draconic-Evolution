package com.brandon3055.draconicevolution.api.modules.data;

import com.brandon3055.draconicevolution.DEConfig;
import com.brandon3055.draconicevolution.api.modules.lib.ModuleContext;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;

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
    public void addInformation(Map<Component, Component> map, ModuleContext context, boolean stack) {
        map.put(new TranslatableComponent("module.draconicevolution.flight.name"), new TranslatableComponent("module.draconicevolution.flight." + elytra + "." + creative));
        if (elytra && elytraSpeed > 0) {
            map.put(new TranslatableComponent("module.draconicevolution.flight.boost.name"), new TextComponent((int)(elytraSpeed * 100) + "%"));
        }
        if (elytra && !DEConfig.enableElytraFlight) {
            map.put(new TextComponent("Elytra Flight").withStyle(ChatFormatting.RED), new TextComponent("Disabled by server").withStyle(ChatFormatting.RED));
        }
        if (creative && !DEConfig.enableCreativeFlight) {
            map.put(new TextComponent("Creative Flight").withStyle(ChatFormatting.RED), new TextComponent("Disabled by server").withStyle(ChatFormatting.RED));
        }
    }
}