package com.brandon3055.draconicevolution.api.modules.data;

import com.brandon3055.draconicevolution.DEConfig;
import com.brandon3055.draconicevolution.api.modules.lib.ModuleContext;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;

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
        map.put(Component.translatable("module.draconicevolution.flight.name"), Component.translatable("module.draconicevolution.flight." + elytra + "." + creative));
        if (elytra && elytraSpeed > 0) {
            map.put(Component.translatable("module.draconicevolution.flight.boost.name"), Component.literal((int) (elytraSpeed * 100) + "%"));
        }
        if (elytra && !DEConfig.enableElytraFlight) {
            map.put(Component.literal("Elytra Flight").withStyle(ChatFormatting.RED), Component.literal("Disabled by server").withStyle(ChatFormatting.RED));
        }
        if (creative && !DEConfig.enableCreativeFlight) {
            map.put(Component.literal("Creative Flight").withStyle(ChatFormatting.RED), Component.literal("Disabled by server").withStyle(ChatFormatting.RED));
        }
    }
}