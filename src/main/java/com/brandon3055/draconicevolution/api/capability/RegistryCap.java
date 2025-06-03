package com.brandon3055.draconicevolution.api.capability;

import net.minecraft.core.HolderLookup;

import java.util.function.Function;

/**
 * Created by brandon3055 on 31/05/2025
 */
public class RegistryCap<T> {
    private final Function<HolderLookup.Provider, T> getter;

    public RegistryCap(Function<HolderLookup.Provider, T> getter) {
        this.getter = getter;
    }

    public T get(HolderLookup.Provider provider) {
        return getter.apply(provider);
    }
}
