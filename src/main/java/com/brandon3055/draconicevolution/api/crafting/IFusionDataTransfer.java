package com.brandon3055.draconicevolution.api.crafting;

import com.brandon3055.brandonscore.api.power.IOPStorage;
import com.brandon3055.brandonscore.api.power.IOPStorageModifiable;
import com.brandon3055.draconicevolution.api.capability.DECapabilities;
import com.brandon3055.draconicevolution.api.capability.ModuleHost;
import com.brandon3055.draconicevolution.api.modules.lib.ModuleHostImpl;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraftforge.common.util.LazyOptional;

/**
 * Created by brandon3055 on 06/10/2021
 */
public interface IFusionDataTransfer {

    default void transferIngredientData(ItemStack result, IFusionInventory fusionInventory) {
        ItemStack cat = fusionInventory.getCatalystStack();
        if (cat.isEnchanted()) {
            EnchantmentHelper.getEnchantments(cat).forEach((enchant, level) -> {
                if (result.canApplyAtEnchantingTable(enchant)) {
                    result.enchant(enchant, level);
                }
            });
        }

        LazyOptional<ModuleHost> optCatHost = cat.getCapability(DECapabilities.MODULE_HOST_CAPABILITY);
        optCatHost.ifPresent(catHost -> {
            LazyOptional<ModuleHost> optResultHost = result.getCapability(DECapabilities.MODULE_HOST_CAPABILITY);
            optResultHost.ifPresent(host -> {
                if (host instanceof ModuleHostImpl && catHost instanceof ModuleHostImpl)
                    ((ModuleHostImpl) host).transferModules((ModuleHostImpl) catHost);
            });
        });

        LazyOptional<IOPStorage> optCatStorage = cat.getCapability(DECapabilities.OP_STORAGE);
        optCatStorage.ifPresent(catStorage -> {
            LazyOptional<IOPStorage> optResStorage = result.getCapability(DECapabilities.OP_STORAGE);
            optResStorage.ifPresent(resStorage -> {
                if (resStorage instanceof IOPStorageModifiable) {
                    ((IOPStorageModifiable) resStorage).modifyEnergyStored(Math.min(resStorage.getMaxOPStored(), catStorage.getOPStored()));
                }
            });
        });
    }
}
