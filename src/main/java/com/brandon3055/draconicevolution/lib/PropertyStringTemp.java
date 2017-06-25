package com.brandon3055.draconicevolution.lib;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import net.minecraft.block.properties.PropertyHelper;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

//TODO replace with PropString in CCL when CCL3 is ready
public class PropertyStringTemp extends PropertyHelper<String> {

    private final List<String> valuesSet;

    public PropertyStringTemp(String name, Collection<String> values) {
        super(name, String.class);
        valuesSet = new LinkedList<>(values);
    }

    public PropertyStringTemp(String name, String... values) {
        super(name, String.class);
        valuesSet = new LinkedList<>();
        Collections.addAll(valuesSet, values);
    }

    public List<String> values() {
        return ImmutableList.copyOf(valuesSet);
    }

    @Nonnull
    @Override
    public Collection<String> getAllowedValues() {
        return ImmutableList.copyOf(valuesSet);
    }

    @SuppressWarnings ("Guava")
    @Nonnull
    @Override
    public Optional<String> parseValue(@Nonnull String value) {
        if (valuesSet.contains(value)) {
            return Optional.of(value);
        }
        return Optional.absent();
    }

    @Nonnull
    @Override
    public String getName(@Nonnull String value) {
        return value;
    }

    public int toMeta(String value) {
        return valuesSet.indexOf(value);
    }

    public String fromMeta(int meta) {
        return valuesSet.get(meta);
    }
}