package com.brandon3055.draconicevolution.api.config;

import com.brandon3055.draconicevolution.api.capability.PropertyProvider;
import com.brandon3055.draconicevolution.client.gui.modular.itemconfig.PropertyData;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.*;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ByIdMap;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.ItemStack;

import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.Supplier;
import java.util.stream.Stream;

/**
 * Created by brandon3055 on 2/5/20.
 * This is the base for the "Property" object. These properties can be supplied by a {@link PropertyProvider}
 * But they can also exist independently in the case of property presets.
 * Though technically i wont be storing the actual property object just its name and serialized data.
 * When i want to apply the preset i just retrieve the property from the provider and load inject the nbt.
 */
public abstract class ConfigProperty {
    protected final String name;
    protected boolean showOnHud = true;
    protected UUID uniqueName = null;

    //Not Serialised
    private Supplier<Component> displayName;
    private Supplier<Component> toolTip;

    protected PropertyProvider provider;

    public static final Codec<ConfigProperty> CODEC = new PropertyCodec();
    public static final StreamCodec<RegistryFriendlyByteBuf, ConfigProperty> STREAM_CODEC = new StreamCodec<>() {
        @Override
        public ConfigProperty decode(RegistryFriendlyByteBuf buf) {
            Type type = Type.STREAM_CODEC.decode(buf);
            return type.getStreamCodec().decode(buf);
        }

        @Override
        public void encode(RegistryFriendlyByteBuf buf, ConfigProperty property) {
            Type.STREAM_CODEC.encode(buf, property.getType());
            property.getType().getStreamCodec().encode(buf, property);
        }
    };

    public ConfigProperty(String name) {
        this.name = name;
    }

    public ConfigProperty(String name, Component displayName) {
        this.name = name;
        this.displayName = () -> displayName;
    }

    public ConfigProperty(String name, boolean showOnHud, Optional<UUID> uniqueName) {
        this.name = name;
        this.showOnHud = showOnHud;
        this.uniqueName = uniqueName.orElse(null);
    }

    public abstract ConfigProperty copy();

    public void setProvider(PropertyProvider provider) {
        this.provider = provider;
    }

    public PropertyProvider getProvider() {
        return provider;
    }

    //    public abstract Codec<? extends ConfigProperty> codec();
//
//    public abstract StreamCodec<RegistryFriendlyByteBuf, ? extends ConfigProperty> streamCodec();

    public void setDisplayName(Supplier<Component> displayName) {
        this.displayName = displayName;
    }

    public void setToolTip(Supplier<Component> toolTip) {
        this.toolTip = toolTip;
    }

    /**
     * @return the display name for this config property. e.g. Mining AOE
     */
    public Component getDisplayName() {
        return displayName == null ? Component.translatable("item_prop.draconicevolution." + name) : displayName.get();
    }

    public Component getToolTip() {
        return toolTip == null ? Component.translatable("item_prop.draconicevolution." + name + ".info") : toolTip.get();
    }

    /**
     * For properties that use a formatter this MUST be the same as getFormatter().format(getValue());
     *
     * @return the formatted value of this property.
     */
    public abstract Component getDisplayValue();

    /**
     * This name will be used to identify this within its parent {@link PropertyProvider} A property provider can never have more than one
     * property with the same name.
     *
     * @return the name of this property. e.g. "mining_aoe"
     */
    public String getName() {
        return uniqueName == null ? name : uniqueName.toString();
    }

    /**
     * This is a work around required to allow {@link com.brandon3055.draconicevolution.api.modules.lib.ModuleEntity}'s to have properties.
     * Because you can have multiple of the same module installed properties in modules require a globally unique name.
     * Do not use this for normal properties. Doing so will make it impossible to retrieve this property fia {@link PropertyProvider#getProperty(String)}
     * as you would need to know this properties UUID. ModuleEntity dont have this issue because they can just hold a reference to the property when they create it.
     * The generated name is saved and loaded with the property data so it will persist.
     */
    public void generateUnique() {
        this.uniqueName = UUID.randomUUID();
    }

    public UUID getUniqueName() {
        return uniqueName;
    }

    protected Optional<UUID> getOptionalUniqueName() {
        return Optional.ofNullable(uniqueName);
    }

    /**
     * Called on the client and the server when this value is changed by the user.
     *
     * @param stack the {@link ItemStack} this property belongs to.
     */
    public abstract void onValueChanged(ItemStack stack);

    public abstract void validateValue();

    public abstract Type getType();

    public boolean showOnHud() {
        return showOnHud;
    }

    public void setShowOnHud(boolean showOnHud) {
        this.showOnHud = showOnHud;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof ConfigProperty that)) return false;
        return showOnHud == that.showOnHud && Objects.equals(name, that.name) && Objects.equals(uniqueName, that.uniqueName);
    }

    public boolean equalsWOValue(Object o) {
        if (!(o instanceof ConfigProperty that)) return false;
        return showOnHud == that.showOnHud && Objects.equals(name, that.name) && Objects.equals(uniqueName, that.uniqueName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, showOnHud, uniqueName);
    }

//    @Override
//    public CompoundTag serializeNBT(HolderLookup.Provider provider) {
//        CompoundTag nbt = new CompoundTag();
//        nbt.putBoolean("hud", showOnHud);
//        if (uniqueName != null) {
//            nbt.putUUID("uni_name", uniqueName);
//        }
//        return nbt;
//    }
//
//    @Override
//    public void deserializeNBT(HolderLookup.Provider provider, CompoundTag nbt) {
//        showOnHud = nbt.getBoolean("hud");
//        if (nbt.hasUUID("uni_name")) {
//            uniqueName = nbt.getUUID("uni_name");
//        }
//    }

//    @Deprecated // This didnt end up getting used for sync but i will leave it in just in case i need it later
//    public void serializeMCData(MCDataOutput output) {
//        output.writeBoolean(showOnHud);
//    }
//
//    @Deprecated // This didnt end up getting used for sync but i will leave it in just in case i need it later
//    public void deSerializeMCData(MCDataInput input) {
//        showOnHud = input.readBoolean();
//    }

    public abstract void loadData(PropertyData data, ItemStack stack);

    public enum Type implements StringRepresentable {
        BOOLEAN(BooleanProperty.CODEC, BooleanProperty.STREAM_CODEC),
        INTEGER(IntegerProperty.CODEC, IntegerProperty.STREAM_CODEC),
        DECIMAL(DecimalProperty.CODEC, DecimalProperty.STREAM_CODEC);
        //        ENUM;

        public static final StringRepresentable.EnumCodec<Type> CODEC = StringRepresentable.fromEnum(Type::values);
        public static final IntFunction<Type> BY_ID = ByIdMap.continuous(Type::ordinal, values(), ByIdMap.OutOfBoundsStrategy.WRAP);
        public static final StreamCodec<ByteBuf, Type> STREAM_CODEC = ByteBufCodecs.idMapper(BY_ID, Type::ordinal);

        private final Codec<? extends ConfigProperty> codec;
        private final StreamCodec<RegistryFriendlyByteBuf, ? extends ConfigProperty> streamCodec;

        Type(Codec<? extends ConfigProperty> codec, StreamCodec<RegistryFriendlyByteBuf, ? extends ConfigProperty> streamCodec) {
            this.codec = codec;
            this.streamCodec = streamCodec;
        }

        public Codec<ConfigProperty> getCodec() {
            return (Codec<ConfigProperty>) codec;
        }

        public StreamCodec<RegistryFriendlyByteBuf, ConfigProperty> getStreamCodec() {
            return (StreamCodec<RegistryFriendlyByteBuf, ConfigProperty>) streamCodec;
        }

        public static Type getSafe(int index) {
            return index >= 0 && index < values().length ? values()[index] : BOOLEAN;
        }

        @Override
        public String getSerializedName() {
            return this.name();
        }
    }

    public enum BooleanFormatter implements StringRepresentable {
        TRUE_FALSE(e -> Component.translatable("gui.draconicevolution.boolean_property." + (e ? "true" : "false"))),
        ENABLED_DISABLED(e -> Component.translatable("gui.draconicevolution.boolean_property." + (e ? "enabled" : "disabled"))),
        ACTIVE_INACTIVE(e -> Component.translatable("gui.draconicevolution.boolean_property." + (e ? "active" : "inactive"))),
        YES_NO(e -> Component.translatable("gui.draconicevolution.boolean_property." + (e ? "yes" : "no")));

        public static final Codec<BooleanFormatter> CODEC = StringRepresentable.fromValues(BooleanFormatter::values);
        public static final IntFunction<BooleanFormatter> BY_ID = ByIdMap.continuous(BooleanFormatter::ordinal, values(), ByIdMap.OutOfBoundsStrategy.WRAP);
        public static final StreamCodec<ByteBuf, BooleanFormatter> STREAM_CODEC = ByteBufCodecs.idMapper(BY_ID, BooleanFormatter::ordinal);

        private Function<Boolean, Component> formatter;

        BooleanFormatter(Function<Boolean, Component> formatter) {
            this.formatter = formatter;
        }

        public Component format(boolean value) {
            return formatter.apply(value);
        }

        public static BooleanFormatter getSafe(int index) {
            return index >= 0 && index < values().length ? values()[index] : TRUE_FALSE;
        }

        @Override
        public String getSerializedName() {
            return this.name().toLowerCase(Locale.ROOT);
        }
    }

    public enum IntegerFormatter implements StringRepresentable {
        RAW(String::valueOf),
        AOE(e -> String.format("%sx%s", 1 + (e * 2), 1 + (e * 2))); //Input is radius
        //Will add formatters as needed

        public static final Codec<IntegerFormatter> CODEC = StringRepresentable.fromValues(IntegerFormatter::values);
        public static final IntFunction<IntegerFormatter> BY_ID = ByIdMap.continuous(IntegerFormatter::ordinal, values(), ByIdMap.OutOfBoundsStrategy.WRAP);
        public static final StreamCodec<ByteBuf, IntegerFormatter> STREAM_CODEC = ByteBufCodecs.idMapper(BY_ID, IntegerFormatter::ordinal);

        private Function<Integer, String> formatter;

        IntegerFormatter(Function<Integer, String> formatter) {
            this.formatter = formatter;
        }

        public String format(int value) {
            return formatter.apply(value);
        }

        public static IntegerFormatter getSafe(int index) {
            return index >= 0 && index < values().length ? values()[index] : RAW;
        }

        @Override
        public String getSerializedName() {
            return this.name().toLowerCase(Locale.ROOT);
        }
    }

    public enum DecimalFormatter implements StringRepresentable {
        RAW_0(e -> String.valueOf(e.intValue())),
        RAW_1(e -> String.format("%.1f", e)),
        RAW_2(e -> String.format("%.2f", e)),
        RAW_3(e -> String.format("%.3f", e)),
        RAW_4(e -> String.format("%.4f", e)),
        PERCENT_0(e -> String.format("%.0f%%", e * 100D)),
        PERCENT_1(e -> String.format("%.1f%%", e * 100D)),
        PERCENT_2(e -> String.format("%.2f%%", e * 100D)),
        PLUS_PERCENT_0(e -> String.format("+%.0f%%", e * 100D)),
        PLUS_PERCENT_1(e -> String.format("+%.1f%%", e * 100D)),
        PLUS_PERCENT_2(e -> String.format("+%.2f%%", e * 100D)),
        AOE_0(e -> String.format("%.0fx%.0f", 1 + (e * 2), 1 + (e * 2))),
        AOE_1(e -> String.format("%.1fx%.1f", 1 + (e * 2), 1 + (e * 2))),
        AOE_2(e -> String.format("%.2fx%.2f", 1 + (e * 2), 1 + (e * 2)));
        //Will add formatters as needed

        public static final Codec<DecimalFormatter> CODEC = StringRepresentable.fromValues(DecimalFormatter::values);
        public static final IntFunction<DecimalFormatter> BY_ID = ByIdMap.continuous(DecimalFormatter::ordinal, values(), ByIdMap.OutOfBoundsStrategy.WRAP);
        public static final StreamCodec<ByteBuf, DecimalFormatter> STREAM_CODEC = ByteBufCodecs.idMapper(BY_ID, DecimalFormatter::ordinal);

        private Function<Double, String> formatter;

        DecimalFormatter(Function<Double, String> formatter) {
            this.formatter = formatter;
        }

        public String format(double value) {
            return formatter.apply(value);
        }

        public static DecimalFormatter getSafe(int index) {
            return index >= 0 && index < values().length ? values()[index] : RAW_1;
        }

        @Override
        public String getSerializedName() {
            return this.name().toLowerCase(Locale.ROOT);
        }
    }

    public static class PropertyCodec implements Codec<ConfigProperty> {

        @Override
        public <T> DataResult<Pair<ConfigProperty, T>> decode(DynamicOps<T> ops, T input) {
            return ops.getList(input)
                    .setLifecycle(Lifecycle.stable())
                    .flatMap(stream -> {
                        DecoderState<T> decoder = new DecoderState<>(ops);
                        stream.accept(decoder::accept);
                        return decoder.build();
                    });
        }

        @Override
        public <T> DataResult<T> encode(ConfigProperty input, DynamicOps<T> ops, T prefix) {
            final ListBuilder<T> builder = ops.listBuilder();
            builder.add(Type.CODEC.encodeStart(ops, input.getType()));
            builder.add(input.getType().getCodec().encodeStart(ops, input));
            return builder.build(prefix);
        }

        private static class DecoderState<T> {
            private final DynamicOps<T> ops;
            private final Stream.Builder<T> failed = Stream.builder();
            private int count;
            private Type type = null;
            private ConfigProperty property = null;

            private DecoderState(DynamicOps<T> ops) {
                this.ops = ops;
            }

            public void accept(T value) {
                count++;
                if (type == null) {
                    DataResult<Pair<Type, T>> elementResult = Type.CODEC.decode(ops, value);
                    elementResult.error().ifPresent(error -> failed.add(value));
                    elementResult.resultOrPartial().ifPresent(e -> type = e.getFirst());
                } else {
                    DataResult<Pair<ConfigProperty, T>> elementResult = type.getCodec().decode(ops, value);
                    elementResult.error().ifPresent(error -> failed.add(value));
                    elementResult.resultOrPartial().ifPresent(e -> property = e.getFirst());
                }
            }

            public DataResult<Pair<ConfigProperty, T>> build() {
                if (count != 2) {
                    return DataResult.error(() -> "ConfigProperty is invalid. Read: " + count + " inputs, expected is 2");
                }
                T errors = ops.createList(failed.build());
                return DataResult.success(Pair.of(property, errors), Lifecycle.stable());
            }
        }
    }

}
