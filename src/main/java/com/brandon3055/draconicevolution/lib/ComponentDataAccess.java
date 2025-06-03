package com.brandon3055.draconicevolution.lib;

import com.brandon3055.draconicevolution.api.capability.DataAccess;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;

/**
 * Created by brandon3055 on 31/05/2025
 */
public class ComponentDataAccess implements DataAccess {

    private final ItemStack stack;
    private final HolderLookup.Provider provider;
    private final DataComponentType<CustomData> dataComponent;

    public ComponentDataAccess(ItemStack stack, HolderLookup.Provider provider, DataComponentType<CustomData> dataComponent) {
        this.stack = stack;
        this.provider = provider;
        this.dataComponent = dataComponent;
    }

    @Override
    public HolderLookup.Provider getProvider() {
        return provider;
    }

    @Override
    public void setData(CompoundTag nbt) {
        stack.set(dataComponent, CustomData.of(nbt));
    }

    @Override
    public CompoundTag getData() {
        return stack.getOrDefault(dataComponent, CustomData.EMPTY).copyTag();
    }
}
