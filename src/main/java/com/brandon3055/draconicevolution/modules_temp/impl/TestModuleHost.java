package com.brandon3055.draconicevolution.modules_temp.impl;

import com.brandon3055.brandonscore.items.ItemBCore;
import com.brandon3055.draconicevolution.modules_temp.ModuleHostImpl;
import com.brandon3055.draconicevolution.modules_temp.capability.ModuleHostCapabilityProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.common.capabilities.ICapabilityProvider;

import javax.annotation.Nullable;

/**
 * Created by brandon3055 on 17/4/20.
 */
public class TestModuleHost extends ItemBCore {

    public TestModuleHost(Properties properties) {
        super(properties);
    }


    @Nullable
    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundNBT nbt) {
        return new ModuleHostCapabilityProvider(new ModuleHostImpl());
    }

}
