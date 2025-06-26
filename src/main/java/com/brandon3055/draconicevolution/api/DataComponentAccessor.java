package com.brandon3055.draconicevolution.api;

import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

/**
 * Created by brandon3055 on 13/06/2025
 */
public record DataComponentAccessor(Setter setter, Getter getter) {

    public static DataComponentAccessor itemStack(ItemStack stack) {
        return new DataComponentAccessor(Setter.forStack(stack), Getter.forStack(stack));
    }

    public static DataComponentAccessor blockEntity(DataComponentMap.Builder builder, DataComponentMap map) {
        return new DataComponentAccessor(Setter.forBlockEntity(builder), Getter.forBlockEntity(map));
    }

    @FunctionalInterface
    public interface Setter {

        <T> void set(DataComponentType<? super T> type, @Nullable T value);

        default <T> void set(Supplier<? extends DataComponentType<? super T>> type, @Nullable T value) {
            set(type.get(), value);
        }

        static Setter forStack(ItemStack stack) {
            return stack::set;
        }

        static Setter forBlockEntity(DataComponentMap.Builder builder) {
            return builder::set;
        }
    }

    @FunctionalInterface
    public interface Getter {

        @Nullable
        <T> T get(DataComponentType<? extends T> type);

        @Nullable
        default <T> T get(Supplier<? extends DataComponentType<? extends T>> type) {
            return get(type.get());
        }

        default <T> T getOrDefault(DataComponentType<? extends T> type, T defaultValue) {
            T t = this.get(type);
            return t != null ? t : defaultValue;
        }

        default <T> T getOrDefault(Supplier<? extends DataComponentType<? extends T>> type, T defaultValue) {
            return getOrDefault(type.get(), defaultValue);
        }

        static Getter forStack(ItemStack stack) {
            return stack::get;
        }

        static Getter forBlockEntity(DataComponentMap components) {
            return components::get;
        }
    }

}
