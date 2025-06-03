package com.brandon3055.draconicevolution.api.capability;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;

/**
 * Created by brandon3055 on 28/05/2025
 */
public interface DataAccess {

    void setData(CompoundTag nbt);

    CompoundTag getData();

    HolderLookup.Provider getProvider();
}
