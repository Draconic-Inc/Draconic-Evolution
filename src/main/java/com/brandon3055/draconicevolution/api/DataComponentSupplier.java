package com.brandon3055.draconicevolution.api;

import net.minecraft.core.component.DataComponentType;

import javax.annotation.Nullable;

/**
 * Created by brandon3055 on 13/06/2025
 */
public interface DataComponentSupplier {

    @Nullable
    <T> T get(DataComponentType<? extends T> type);

}
