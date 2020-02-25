package com.brandon3055.draconicevolution.integration;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by brandon3055 on 27/05/19.
 */
public class BaublesHelper {

//    @CapabilityInject(IBaublesItemHandler.class)
//    public static Capability<IBaublesItemHandler> CAPABILITY_BAUBLES = null;

//    @Optional.Method(modid = "baubles")
    public static List<ItemStack> getBaubles(PlayerEntity entity) {
//        if (CAPABILITY_BAUBLES == null) {
            return new ArrayList<>();
//        }

//        IBaublesItemHandler handler = BaublesApi.getBaublesHandler(entity);
//
//        if (handler == null) {
//            return new ArrayList<>();
//        }
//
//        return IntStream.range(0, handler.getSlots()).mapToObj(handler::getStackInSlot).filter(stack -> !stack.isEmpty()).collect(Collectors.toList());
    }

}
