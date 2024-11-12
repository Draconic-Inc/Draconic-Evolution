package com.brandon3055.draconicevolution.integration.equipment;

import com.brandon3055.brandonscore.BrandonsCore;
import com.brandon3055.brandonscore.lib.IEquipmentManager;
import com.brandon3055.draconicevolution.lib.WTFException;
import com.google.common.collect.ImmutableList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

/**
 * Created by brandon3055 on 6/1/21
 * <p>
 * This class is meant to provide a 'safe' interface for interacting with equipment mods such as Curio
 * Safe as in the DE will still run if no such mod is installed.
 * This currently only supports Curio but may also add support for baubles at some point if that's still a thing in 1.16.
 * //TODO overhaul and move to BC?
 */
public abstract class EquipmentManager implements IEquipmentManager {
    private static boolean curiosLoaded;
    private static EquipmentManager instance = null;

    public static void initialize(IEventBus modBus) {
        curiosLoaded = ModList.get().isLoaded("curios");

        if (curiosLoaded) {
//            modBus.addListener(CuriosIntegration::sendIMC);
            instance = new CuriosIntegration();
        }

        BrandonsCore.equipmentManager = instance;
    }

    public static boolean equipModLoaded() {
        return instance != null;
    }

    public static String equipModID() {
        if (curiosLoaded) return "curios";
        return "";
    }

    public static void registerCapability(RegisterCapabilitiesEvent event, Item item) {
        if (instance != null) {
            if (!(item instanceof IDEEquipment)) {
                throw new IllegalStateException("\"Equipment items\" must implement IDEEquipment");
            }
            instance.registerCap(event, item);
        }
    }

    public static Optional<IItemHandlerModifiable> getEquipmentInventory(LivingEntity entity) {
        if (instance != null) {
            return instance.getInventory(entity);
        }
        return Optional.empty();
    }

    public static ItemStack findItem(Item item, LivingEntity entity) {
        if (instance != null) {
            return instance.findMatchingItem(item, entity);
        }
        return ItemStack.EMPTY;
    }

    public static List<ItemStack> findItems(Item item, LivingEntity entity) {
        return findItems(stack -> stack.getItem() == item, entity);
    }

    public static ItemStack findItem(Predicate<ItemStack> predicate, LivingEntity entity) {
        if (instance != null) {
            return instance.findMatchingItem(predicate, entity);
        }
        return ItemStack.EMPTY;
    }

    public static List<ItemStack> findItems(Predicate<ItemStack> predicate, LivingEntity entity) {
        if (instance != null) {
            Optional<IItemHandlerModifiable> optionalHandler = instance.getInventory(entity);
            if (optionalHandler.isPresent()) {
                IItemHandlerModifiable handler = optionalHandler.orElseThrow(() -> new WTFException("This should not happen"));
                List<ItemStack> list = new ArrayList<>();
                for (int i = 0; i < handler.getSlots(); i++) {
                    ItemStack stack = handler.getStackInSlot(i);
                    if (predicate.test(stack)) {
                        list.add(stack);
                    }
                }
                return ImmutableList.copyOf(list);
            }
        }
        return Collections.emptyList();
    }

    public static List<ItemStack> getAllItems(LivingEntity entity) {
        return findItems(stack -> !stack.isEmpty(), entity);
    }


    public static List<ResourceLocation> getIcons(LivingEntity entity) {
        if (instance == null) return Collections.emptyList();
        return instance.getSlotIcons(entity);
    }
}
