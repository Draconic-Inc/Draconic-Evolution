//package com.brandon3055.draconicevolution.api.config;
//
//import codechicken.lib.data.MCDataInput;
//import codechicken.lib.data.MCDataOutput;
//import com.brandon3055.draconicevolution.client.gui.modular.itemconfig.PropertyData;
//import com.mojang.serialization.Codec;
//import com.mojang.serialization.codecs.RecordCodecBuilder;
//import net.minecraft.core.HolderLookup;
//import net.minecraft.core.UUIDUtil;
//import net.minecraft.nbt.CompoundTag;
//import net.minecraft.network.RegistryFriendlyByteBuf;
//import net.minecraft.network.chat.Component;
//import net.minecraft.network.codec.ByteBufCodecs;
//import net.minecraft.network.codec.StreamCodec;
//import net.minecraft.util.ExtraCodecs;
//import net.minecraft.world.item.ItemStack;
//
//import java.util.Arrays;
//import java.util.List;
//import java.util.Map;
//import java.util.UUID;
//import java.util.function.BiConsumer;
//import java.util.function.Consumer;
//import java.util.function.Function;
//import java.util.stream.Collectors;
//
///**
// * Created by brandon3055 on 2/5/20.
// */
//public class EnumProperty<T extends Enum<T>> extends ConfigProperty {
//    private T value;
//
//    //Not Serialised
//    private Function<T, String> displayFormatter = String::valueOf;
//    private BiConsumer<ItemStack, EnumProperty<T>> changeListener = null;
//    private List<T> allowedValues;
//
////    private static final Codec<EnumProperty<?>> CODEC = RecordCodecBuilder.create(builder -> builder.group(
////            Codec.STRING.fieldOf("name").forGetter(e -> e.name),
////            Codec.BOOL.fieldOf("show_on_hud").forGetter(e -> e.showOnHud),
////            UUIDUtil.CODEC.fieldOf("unique_name").forGetter(e -> e.getOrCreateUniqueName()),
////            Codec.BYTE.fieldOf("value").forGetter(e -> e.value.ordinal())
////    ).apply(builder, EnumProperty::new));
////
////    private static final StreamCodec<RegistryFriendlyByteBuf, EnumProperty> STREAM_CODEC = StreamCodec.composite(
////            ByteBufCodecs.STRING_UTF8, e -> e.name,
////            ByteBufCodecs.BOOL, e -> e.showOnHud,
////            UUIDUtil.STREAM_CODEC, e -> e.getOrCreateUniqueName(),
////            ByteBufCodecs.DOUBLE, e -> e.value,
////            EnumProperty::new
////    );
//
//    public EnumProperty(String name, T defaultValue) {
//        super(name);
//        this.value = defaultValue;
//        this.allowedValues = Arrays.asList(value.getDeclaringClass().getEnumConstants());
//    }
//
//    public EnumProperty(String name, Component displayName, T defaultValue) {
//        super(name, displayName);
//        this.value = defaultValue;
//        this.allowedValues = Arrays.asList(value.getDeclaringClass().getEnumConstants());
//    }
//
////    public EnumProperty(String name, boolean showOnHud, UUID uniqueName, T value) {
////        super(name, showOnHud, uniqueName);
////        this.value = value;
////    }
//
//    public EnumProperty<T> setAllowedValues(List<T> allowedValues) {
//        this.allowedValues = allowedValues;
//        validateValue();
//        return this;
//    }
//
//    public EnumProperty<T> setAllowedValues(T... allowedValues) {
//        this.allowedValues = Arrays.asList(allowedValues);
//        validateValue();
//        return this;
//    }
//
//    public List<T> getAllowedValues() {
//        return allowedValues;
//    }
//
//    public Map<Integer, String> generateValueDisplayMap() {
//        return allowedValues.stream().collect(Collectors.toMap(Enum::ordinal, e -> displayFormatter.apply(e)));
//    }
//
//    public T getValue() {
//        return value;
//    }
//
//    public void setValue(T value) {
//        this.value = value;
//    }
//
//    @Override
//    public String getDisplayValue() {
//        return displayFormatter.apply(getValue());
//    }
//
//    @Override
//    public void onValueChanged(ItemStack stack) {
//        if (changeListener != null) {
//            changeListener.accept(stack, this);
//        }
//    }
//
//    @Override
//    public void validateValue() {
//        if (!allowedValues.contains(value) && !allowedValues.isEmpty()) {
//            value = allowedValues.get(0);
//        }
//    }
//
//    @Override
//    public Type getType() {
//        return Type.ENUM;
//    }
//
//    public void setChangeListener(Runnable changeListener) {
//        this.changeListener = (stack, integerProperty) -> changeListener.run();
//    }
//
//    public void setChangeListener(Consumer<ItemStack> changeListener) {
//        this.changeListener = (stack, integerProperty) -> changeListener.accept(stack);
//    }
//
//    public void setChangeListener(BiConsumer<ItemStack, EnumProperty<T>> changeListener) {
//        this.changeListener = changeListener;
//    }
//
//    public void setValueFormatter(Function<T, String> displayFormatter) {
//        this.displayFormatter = displayFormatter;
//    }
//
//    public Function<T, String> getDisplayFormatter() {
//        return displayFormatter;
//    }
//
//    @Override
//    public CompoundTag serializeNBT(HolderLookup.Provider provider) {
//        CompoundTag nbt = super.serializeNBT(provider);
//        nbt.putByte("value", (byte) value.ordinal());
//        return nbt;
//    }
//
//    @Override
//    public void deserializeNBT(HolderLookup.Provider provider, CompoundTag nbt) {
//        try {
//            if (nbt.contains("value")) {
//                T newValue = value.getDeclaringClass().getEnumConstants()[nbt.getByte("value")];
//                if (allowedValues.contains(newValue)) {
//                    value = newValue;
//                }
//            }
//        }
//        catch (Throwable e) {
//            e.printStackTrace();
//        }
//        super.deserializeNBT(provider, nbt);
//    }
//
//    @Override
//    public void serializeMCData(MCDataOutput output) {
//        super.serializeMCData(output);
//        output.writeEnum(value);
//    }
//
//    @Override
//    public void deSerializeMCData(MCDataInput input) {
//        super.deSerializeMCData(input);
//        T newValue = input.readEnum(value.getDeclaringClass());
//        if (allowedValues.contains(newValue)) {
//            this.value = newValue;
//        }
//    }
//
//    @Override
//    public void loadData(PropertyData data, ItemStack stack) {
//        try {
//            T newValue = value.getDeclaringClass().getEnumConstants()[data.enumValueIndex];
//            if (getAllowedValues().contains(newValue)) {
//                value = newValue;
//                onValueChanged(stack);
//            }
//        }
//        catch (Throwable ignored) {}
//    }
//}
