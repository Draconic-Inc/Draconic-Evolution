package com.brandon3055.draconicevolution.api.modules;

import com.brandon3055.brandonscore.api.power.IOPStorage;
import com.brandon3055.brandonscore.inventory.InventoryDynamic;
import com.brandon3055.brandonscore.utils.EnergyUtils;
import com.brandon3055.draconicevolution.api.capability.ModuleHost;
import com.brandon3055.draconicevolution.api.modules.entities.EnderCollectionEntity;
import com.brandon3055.draconicevolution.api.modules.entities.JunkFilterEntity;
import com.brandon3055.draconicevolution.api.modules.lib.ModuleEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Predicate;

/**
 * Created by brandon3055 on 31/01/2023
 */
public class ModuleHelper {

    /**
     * Handles the process of giving the supplied item stacks to the player accounting for junk filters and ender collection.
     */
    public static void handleItemCollection(Player player, ModuleHost host, IOPStorage storage, InventoryDynamic inventory) {
        if (player.level.isClientSide) return;

        Predicate<ItemStack> junkTest = null;
        for (ModuleEntity<?> entity : host.getEntitiesByType(ModuleTypes.JUNK_FILTER).toList()) {
            junkTest = junkTest == null ? ((JunkFilterEntity) entity).createFilterTest() : junkTest.or(((JunkFilterEntity) entity).createFilterTest());
        }
        if (junkTest != null) {
            inventory.removeIf(junkTest);
        }

        ModuleEntity<?> optionalCollector = host.getEntitiesByType(ModuleTypes.ENDER_COLLECTION).findAny().orElse(null);
        if (optionalCollector instanceof EnderCollectionEntity collector) {
            List<ItemStack> remainder = collector.insertStacks(player, inventory.getStacks(), storage);
            inventory.setStacks(new LinkedList<>(remainder));
        }

        for (int i = 0; i < inventory.getContainerSize(); i++) {
            ItemStack sis = inventory.getItem(i);
            if (sis != null) {
                ItemEntity item = new ItemEntity(player.level, player.getX(), player.getY(), player.getZ(), sis);
                item.setPickUpDelay(0);
                player.level.addFreshEntity(item);
            }
        }
        player.giveExperiencePoints(inventory.xp);
        inventory.clearContent();
    }

}
