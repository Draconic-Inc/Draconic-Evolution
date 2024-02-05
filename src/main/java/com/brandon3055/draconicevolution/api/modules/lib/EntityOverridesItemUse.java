package com.brandon3055.draconicevolution.api.modules.lib;

import net.minecraft.client.model.PlayerModel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderHandEvent;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;

/**
 * Created by brandon3055 on 27/01/2023
 */
public interface EntityOverridesItemUse {

    /**
     * Called when the player interact event is fired while the player is holding a host containing this module.
     * For a specific event simply instanceof check for RightClickBlock, RightClickItem, RightClickEmpty, etc. Any event that extends PlayerInteractEvent.
     * <p>
     * Note: be sure to check if the event has already been canceled and react accordingly.
     */
    default void onPlayerInteractEvent(PlayerInteractEvent event) {}

    /**
     * Can be used to receive item use start, tick, stop and finish events from the host.
     */
    default void onEntityUseItem(LivingEntityUseItemEvent event) {}

    /**
     * @param stack The host stack.
     * @return true to enable overriding of the default using pose
     */
    default boolean overrideUsingPose(ItemStack stack) {
        return true;
    }

    /**
     * Allows you to customise the using pose.
     * The event is provides as a convenient way to retrieve the PoseStack and other relevant fields.
     * Note: The event is already canceled at this point and it needs to stay that way.
     */
    @OnlyIn(Dist.CLIENT)
    default void modifyFirstPersonUsingPose(RenderHandEvent event, boolean leftHand) {}

    /**
     * Counterpart to modifyFirstPersonUsingPose that lets you modify the third person player model.
     */
    @OnlyIn(Dist.CLIENT)
    default void modifyPlayerModelPose(Player player, PlayerModel<?> model, boolean leftHand) {}
}
