package com.brandon3055.draconicevolution.api.modules;

import com.brandon3055.brandonscore.api.power.IOPStorage;
import com.brandon3055.brandonscore.inventory.InventoryDynamic;
import com.brandon3055.draconicevolution.api.capability.DECapabilities;
import com.brandon3055.draconicevolution.api.capability.ModuleHost;
import com.brandon3055.draconicevolution.api.modules.data.ModuleData;
import com.brandon3055.draconicevolution.api.modules.entities.EnderCollectionEntity;
import com.brandon3055.draconicevolution.api.modules.entities.JunkFilterEntity;
import com.brandon3055.draconicevolution.api.modules.lib.ModuleEntity;
import com.brandon3055.draconicevolution.integration.equipment.EquipmentManager;
import com.brandon3055.draconicevolution.items.equipment.IModularItem;
import com.brandon3055.draconicevolution.lib.WTFException;
import net.covers1624.quack.collection.FastStream;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.*;
import java.util.function.Predicate;

/**
 * Created by brandon3055 on 31/01/2023
 */
public class ModuleHelper {

    /**
     * Handles the process of giving the supplied item stacks to the player accounting for junk filters and ender collection.
     */
    public static void handleItemCollection(Player player, ModuleHost host, IOPStorage storage, InventoryDynamic inventory) {
        if (player.level().isClientSide) return;

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
                ItemEntity item = new ItemEntity(player.level(), player.getX(), player.getY(), player.getZ(), sis);
                item.setPickUpDelay(0);
                player.level().addFreshEntity(item);
            }
        }
        player.giveExperiencePoints(inventory.xp);
        inventory.clearContent();
    }

    public static List<ItemStack> getEquippedHostItems(LivingEntity entity) {
        List<ItemStack> stacks = new ArrayList<>(EquipmentManager.findItems(e -> e.getItem() instanceof IModularItem item && item.isEquipped(e, null, true), entity));
        for (EquipmentSlot slot : EquipmentSlot.values()) {
            ItemStack stack = entity.getItemBySlot(slot);
            if (!stack.isEmpty() && stack.getItem() instanceof IModularItem item && item.isEquipped(stack, slot, false)) {
                stacks.add(stack);
            }
        }
        return stacks;
    }

    public static FastStream<ModuleHost> getEquippedHosts(LivingEntity entity) {
        return FastStream.of(getEquippedHostItems(entity))
                .map(e -> e.getCapability(DECapabilities.Host.ITEM))
                .filter(Objects::nonNull);
    }

    public static <T extends ModuleData<T>> T getCombinedEquippedData(LivingEntity entity, ModuleType<T> moduleType, T fallback) {
        return getEquippedHosts(entity)
                .map(e -> e.getModuleData(moduleType))
                .filter(Objects::nonNull)
                .fold(fallback, ModuleData::combine);
    }

    public static <T extends ModuleData<T>> List<T> getEquippedModules(LivingEntity entity, ModuleType<T> moduleType) {
        return getEquippedHosts(entity)
                .map(e -> e.getModuleData(moduleType))
                .filter(Objects::nonNull)
                .toList();
    }

}
