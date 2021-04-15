package com.brandon3055.draconicevolution.items.equipment;

import com.brandon3055.draconicevolution.DraconicEvolution;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

/**
 * Created by brandon3055 on 10/2/21
 */
public class StaffAttackHandler {

    //Projectile Attack
    public static int getMaxChargeTime(ItemStack stack, PlayerEntity player) {
        return 20;
    }

    public static ActionResult<ItemStack> onRightClick(World world, PlayerEntity player, Hand hand) {
        ItemStack stack = player.getItemInHand(hand);

        DraconicEvolution.LOGGER.info("Click");

        player.startUsingItem(hand);

        return ActionResult.pass(stack);
    }

    public static void onStopUsing(ItemStack stack, World world, LivingEntity entity, int timeLeft) {


    }


}
