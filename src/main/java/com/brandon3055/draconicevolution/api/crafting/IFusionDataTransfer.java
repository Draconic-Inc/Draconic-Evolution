package com.brandon3055.draconicevolution.api.crafting;

import com.brandon3055.brandonscore.api.power.IOPStorage;
import com.brandon3055.brandonscore.capability.CapabilityOP;
import com.brandon3055.draconicevolution.api.capability.DECapabilities;
import com.brandon3055.draconicevolution.api.capability.ModuleHost;
import com.brandon3055.draconicevolution.api.modules.lib.ModuleHostImpl;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.ItemEnchantments;

/**
 * Created by brandon3055 on 06/10/2021
 */
public interface IFusionDataTransfer {

    default void transferIngredientData(ItemStack result, IFusionInventory fusionInventory) {
        ItemStack cat = fusionInventory.getCatalystStack();
        if (cat.isEnchanted()) {
            ItemEnchantments enchantments = cat.getOrDefault(DataComponents.ENCHANTMENTS, ItemEnchantments.EMPTY);
            result.set(DataComponents.ENCHANTMENTS, enchantments);
        }

        ModuleHost catHost = cat.getCapability(DECapabilities.Host.ITEM);
        if (catHost != null) {
            ModuleHost resultHost = result.getCapability(DECapabilities.Host.ITEM);
            if (resultHost != null) {
                if (resultHost instanceof ModuleHostImpl && catHost instanceof ModuleHostImpl) {
                    ((ModuleHostImpl) resultHost).transferModules((ModuleHostImpl) catHost);
                }
            }
        }

        IOPStorage catStorage = cat.getCapability(CapabilityOP.ITEM);
        if (catStorage != null) {
            IOPStorage resStorage = result.getCapability(CapabilityOP.ITEM);
            if (resStorage != null) {
                resStorage.modifyEnergyStored(Math.min(resStorage.getMaxOPStored(), catStorage.getOPStored()));
            }
        }
    }
}
