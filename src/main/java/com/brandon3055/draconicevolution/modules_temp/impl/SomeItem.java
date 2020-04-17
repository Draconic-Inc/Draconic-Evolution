package com.brandon3055.draconicevolution.modules_temp.impl;

import codechicken.lib.data.MCByteStream;
import codechicken.lib.data.MCDataOutput;
import com.brandon3055.draconicevolution.modules_temp.EnergyModuleProperties;
import com.brandon3055.draconicevolution.modules_temp.IModule;
import com.brandon3055.draconicevolution.modules_temp.capability.IModuleProvider;
import com.brandon3055.draconicevolution.modules_temp.capability.ModuleCapability;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.ByteArrayOutputStream;

/**
 * Created by covers1624 on 4/16/20.
 */
public class SomeItem extends Item implements IModuleProvider<EnergyModuleProperties> {

    public SomeItem(Properties properties) {
        super(properties);
    }

    public static IModule<?> getModuleFromItem(ItemStack stack) {
        LazyOptional<IModuleProvider<?>> cap = stack.getCapability(ModuleCapability.MODULE_CAPABILITY);
        return cap.map(IModuleProvider::getModule).orElse(null);
    }

    public static void save() {
//        IModule<?> module = null;
//
//        MCDataOutput out = new MCByteStream(new ByteArrayOutputStream());
//        out.writeRegistryId(module);
//
//        module.getRegistryName();

    }

    @Nullable
    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundNBT nbt) {
        return new ICapabilityProvider() {

            @Override
            public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
                return LazyOptional.of(() -> SomeItem.this).cast();
            }
        };
    }

    @Override
    public IModule<EnergyModuleProperties> getModule() {
        return ModModuleRegistration.draconicEnergyModule;
    }
}
