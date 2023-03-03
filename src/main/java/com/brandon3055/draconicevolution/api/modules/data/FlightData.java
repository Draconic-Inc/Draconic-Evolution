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
public record FlightData(boolean elytra, boolean creative, double elytraSpeed) implements ModuleData<FlightData> {

    @Override
    public FlightData combine(FlightData other) {
        return other;
    }

    @Override
    public void addInformation(Map<Component, Component> map, ModuleContext context, boolean stack) {
        map.put(new TranslatableComponent("module.draconicevolution.flight.name"), new TranslatableComponent("module.draconicevolution.flight." + elytra + "." + creative));
        if (elytra && elytraSpeed > 0) {
            map.put(new TranslatableComponent("module.draconicevolution.flight.boost.name"), new TextComponent((int) (elytraSpeed * 100) + "%"));
        }
        if (elytra && !DEConfig.enableElytraFlight) {
            map.put(new TextComponent("Elytra Flight").withStyle(ChatFormatting.RED), new TextComponent("Disabled by server").withStyle(ChatFormatting.RED));
        }
        if (creative && !DEConfig.enableCreativeFlight) {
            map.put(new TextComponent("Creative Flight").withStyle(ChatFormatting.RED), new TextComponent("Disabled by server").withStyle(ChatFormatting.RED));
        }
    }
}