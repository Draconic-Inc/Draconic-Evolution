package com.brandon3055.draconicevolution.client.gui.modular.itemconfig;

import codechicken.lib.data.MCDataInput;
import codechicken.lib.data.MCDataOutput;
import codechicken.lib.math.MathHelper;
import com.brandon3055.brandonscore.utils.Utils;
import com.brandon3055.draconicevolution.api.capability.PropertyProvider;
import com.brandon3055.draconicevolution.api.config.*;
import com.brandon3055.draconicevolution.api.config.ConfigProperty.BooleanFormatter;
import com.brandon3055.draconicevolution.api.config.ConfigProperty.DecimalFormatter;
import com.brandon3055.draconicevolution.api.config.ConfigProperty.IntegerFormatter;
import com.brandon3055.draconicevolution.api.config.ConfigProperty.Type;
import com.brandon3055.draconicevolution.inventory.ContainerConfigurableItem;
import com.brandon3055.draconicevolution.network.DraconicNetwork;
import com.google.gson.JsonParseException;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;

import javax.annotation.Nullable;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by brandon3055 on 9/5/20.
 */
public class PropertyData {
    public final Type type;
    public final UUID providerID;
    public final String providerName;
    private String propName;
    protected UUID propUniqueName;
    public Component toolTip;
    public Component displayName;
    public Runnable changeListener;
    //Logic
    public boolean isGlobal = false;
    private boolean isPropertyAvailable = false;
    public boolean isProviderAvailable = false;
    //Value
    public int integerValue = 0;
    public double decimalValue = 0;
    public String displayValue = "value";
    public boolean booleanValue = false;
    public double minValue = 0;
    public double maxValue = 1;
    //Formatters
    public BooleanFormatter booleanFormatter = BooleanFormatter.TRUE_FALSE;
    public IntegerFormatter integerFormatter = IntegerFormatter.RAW;
    public DecimalFormatter decimalFormatter = DecimalFormatter.RAW_1;
    //Enum Stuff
    public int enumValueIndex = 0;
    public List<Integer> enumValueOptions;
    public Map<Integer, String> enumDisplayValues;

    public PropertyData(PropertyProvider provider, ConfigProperty property, boolean pullValue) {
        this(provider.getProviderID(), provider.getProviderName(), property.getType());
        this.displayName = property.getDisplayName();
        this.toolTip = property.getToolTip();
        if (property.getUniqueName() != null) {
            propUniqueName = property.getUniqueName();
        } else {
            propName = property.getName();
        }
        isProviderAvailable = true;
        pullData(property, pullValue);
    }

    public PropertyData(UUID providerID, String providerName, Type type) {
        this.providerID = providerID;
        this.providerName = providerName;
        this.type = type;
    }

    public void setChangeListener(Runnable changeListener) {
        this.changeListener = changeListener;
    }

    public void pullData(ConfigProperty property, boolean pullValue) {
        isPropertyAvailable = property != null;
        if (isPropertyAvailable) {
            displayName = property.getDisplayName();
            toolTip = property.getToolTip();
            if (toolTip instanceof TranslatableComponent && ((TranslatableComponent) toolTip).getKey().equals(toolTip.getString())) {
                toolTip = null;
            }

            displayValue = property.getDisplayValue();

            switch (property.getType()) {
                case BOOLEAN -> {
                    BooleanProperty prop = (BooleanProperty) property;
                    booleanFormatter = prop.getFormatter();
                    if (pullValue) {
                        booleanValue = prop.getValue();
                    }
                }
                case INTEGER -> {
                    IntegerProperty prop = (IntegerProperty) property;
                    integerFormatter = prop.getFormatter();
                    minValue = prop.getMin();
                    maxValue = prop.getMax();
                    if (pullValue) {
                        integerValue = prop.getValue();
                    }
                }
                case DECIMAL -> {
                    DecimalProperty prop = (DecimalProperty) property;
                    decimalFormatter = prop.getFormatter();
                    minValue = prop.getMin();
                    maxValue = prop.getMax();
                    if (pullValue) {
                        decimalValue = prop.getValue();
                    }
                }
                case ENUM -> {
                    EnumProperty<?> prop = (EnumProperty<?>) property;
                    enumValueOptions = prop.getAllowedValues().stream().map(Enum::ordinal).collect(Collectors.toList());
                    enumDisplayValues = prop.generateValueDisplayMap();
                    if (pullValue) {
                        enumValueIndex = prop.getValue().ordinal();
                    }
                }
            }
        }
        updateDisplayValue();
    }

    public String getPropertyName() {
        return propUniqueName == null ? propName : propUniqueName.toString();
    }

    public void pullData(ContainerConfigurableItem container, boolean pullValue) {
        PropertyProvider provider = container.findProvider(providerID);
        isProviderAvailable = provider != null;
        if (isProviderAvailable) {
            pullData(provider.getProperty(getPropertyName()), pullValue);
        }
    }

    public void updateDisplayValue() {
        switch (type) {
            case BOOLEAN -> {
                if (booleanFormatter != null) {
                    displayValue = booleanFormatter.format(booleanValue);
                }
            }
            case INTEGER -> {
                if (integerFormatter != null) {
                    displayValue = integerFormatter.format(integerValue);
                }
            }
            case DECIMAL -> {
                if (decimalFormatter != null) {
                    displayValue = decimalFormatter.format(decimalValue);
                }
            }
            case ENUM -> {
                if (enumDisplayValues != null) {
                    displayValue = enumDisplayValues.getOrDefault(enumValueIndex, "[Error]");
                }
            }
        }
    }

    public void updateBooleanValue(boolean newValue) {
        if (type == Type.BOOLEAN) {
            booleanValue = newValue;
            updateDisplayValue();
            onValueChanged();
        }
    }

    public void toggleBooleanValue() {
        updateBooleanValue(!booleanValue);
    }

    public void updateEnumValue(int newIndex) {
        if (type == Type.ENUM && enumValueOptions.contains(newIndex)) {
            enumValueIndex = newIndex;
            updateDisplayValue();
            onValueChanged();
        }
    }

    public void updateNumberValue(double value, boolean isFinalValue) {
        if (type == Type.INTEGER) {
            integerValue = (int) Math.round(value);
        } else if (type == Type.DECIMAL) {
            decimalValue = value;
        } else {
            return;
        }
        updateDisplayValue();
        if (isFinalValue) {
            onValueChanged();
        }
    }

    public void increment(int dir) {
        if (type == Type.INTEGER) {
            int inc = (int) (Math.max(1, getInc(maxValue - minValue)) * dir * (Screen.hasShiftDown() ? 2 : 1));
            integerValue = (int) MathHelper.clip(integerValue + inc, minValue, maxValue);
        } else if (type == Type.DECIMAL) {
            double inc = getInc(maxValue - minValue) * dir * (Screen.hasShiftDown() ? 2 : 1);
            decimalValue = MathHelper.clip(decimalValue + inc, minValue, maxValue);
        } else if (type == Type.ENUM && enumValueOptions != null && enumValueOptions.contains(enumValueIndex)) {
            int index = enumValueOptions.indexOf(enumValueIndex);
            int newIndex = Math.floorMod(index + dir, enumValueOptions.size());
            enumValueIndex = enumValueOptions.get(newIndex);
        } else {
            return;
        }
        updateDisplayValue();
        onValueChanged();
    }

    private double getInc(double range) {
        if (range <= 1) return 0.1;
        else if (range <= 5) return 0.5;
        else if (range <= 10) return 1;
        else if (range <= 20) return 2;
        else if (range <= 50) return 5;
        return 10;
    }

    public void onValueChanged() {
        if (changeListener != null) {
            changeListener.run();
        }
    }

    public void toggleGlobal() {
        isGlobal = !isGlobal;
    }

    public String getEnumDisplayName(int enumIndex) {
        return enumDisplayValues != null && enumDisplayValues.containsKey(enumIndex) ? enumDisplayValues.get(enumIndex) : ChatFormatting.RED + "[Error]";
    }

    public void sendToServer() {
        DraconicNetwork.sendPropertyData(this);
    }

    public ConfigProperty getPropIfApplicable(PropertyProvider provider) {
        if (provider.getProviderName().equals(providerName)) {
            if (isGlobal || provider.getProviderID().equals(providerID)) {
                return provider.getProperty(getPropertyName());
            }
        }
        return null;
    }

    public boolean doesDataMatch(ConfigProperty prop) {
        return switch (type) {
            case BOOLEAN -> prop instanceof BooleanProperty && ((BooleanProperty) prop).getValue() == booleanValue;
            case INTEGER -> prop instanceof IntegerProperty && ((IntegerProperty) prop).getValue() == integerValue;
            case DECIMAL -> prop instanceof DecimalProperty && ((DecimalProperty) prop).getValue() == decimalValue;
            case ENUM -> prop instanceof EnumProperty && ((EnumProperty<?>) prop).getValue().ordinal() == enumValueIndex;
        };
    }

    public boolean isPropertyAvailable() {
        return isPropertyAvailable && isProviderAvailable;
    }

    public PropertyData copy() {
        PropertyData copy = new PropertyData(providerID, providerName, type);
        copy.propName = propName;
        copy.propUniqueName = propUniqueName;
        copy.toolTip = toolTip;
        copy.displayName = displayName;
        copy.changeListener = changeListener;

        copy.isGlobal = isGlobal;
        copy.isPropertyAvailable = isPropertyAvailable;
        copy.isProviderAvailable = isProviderAvailable;
        copy.integerValue = integerValue;
        copy.decimalValue = decimalValue;
        copy.displayValue = displayValue;
        copy.booleanValue = booleanValue;
        copy.minValue = minValue;
        copy.maxValue = maxValue;

        copy.booleanFormatter = booleanFormatter;
        copy.integerFormatter = integerFormatter;
        copy.decimalFormatter = decimalFormatter;

        copy.enumValueIndex = enumValueIndex;
        if (enumValueOptions != null && enumDisplayValues != null) {
            copy.enumValueOptions = new ArrayList<>(enumValueOptions);
            copy.enumDisplayValues = new HashMap<>(enumDisplayValues);
        }
        return copy;
    }

    public CompoundTag serialize() {
        CompoundTag nbt = new CompoundTag();
        nbt.putByte("type", (byte) type.ordinal());
        nbt.putUUID("prov_id", providerID);
        nbt.putString("prov_name", providerName);
        if (propUniqueName != null) {
            nbt.putUUID("prop_name", propUniqueName);
        } else {
            nbt.putString("prop_name", propName);
        }

        nbt.putString("tooltip", Component.Serializer.toJson(toolTip));
        nbt.putString("display_name", Component.Serializer.toJson(displayName));
        nbt.putString("display_value", displayValue);
        nbt.putBoolean("global", isGlobal);
        switch (type) {
            case BOOLEAN -> {
                nbt.putBoolean("value", booleanValue);
                nbt.putByte("formatter", (byte) booleanFormatter.ordinal());
            }
            case INTEGER -> {
                nbt.putInt("value", integerValue);
                nbt.putByte("formatter", (byte) integerFormatter.ordinal());
                nbt.putInt("min", (int) minValue);
                nbt.putInt("max", (int) maxValue);
            }
            case DECIMAL -> {
                nbt.putDouble("value", decimalValue);
                nbt.putByte("formatter", (byte) decimalFormatter.ordinal());
                nbt.putDouble("min", minValue);
                nbt.putDouble("max", maxValue);
            }
            case ENUM -> {
                nbt.putInt("value", enumValueIndex);
                if (enumValueOptions != null) {
                    nbt.put("names", enumValueOptions.stream().map(IntTag::valueOf).collect(Collectors.toCollection(ListTag::new)));
                }
                if (enumDisplayValues != null) {
                    CompoundTag nameValues = new CompoundTag();
                    enumDisplayValues.forEach((key, value) -> nameValues.putString(String.valueOf(key), value));
                    nbt.put("name_values", nameValues);
                }
            }
        }
        return nbt;
    }

    @Nullable
    public static PropertyData deserialize(CompoundTag nbt) {
        if (!nbt.hasUUID("prov_id") || !nbt.contains("prov_name") || (!nbt.contains("prop_name") && !nbt.hasUUID("prop_name")) || !nbt.contains("type")) {
            return null;
        }

        PropertyData data = new PropertyData(
                nbt.getUUID("prov_id"),
                nbt.getString("prov_name"),
                Type.getSafe(nbt.getByte("type")));

        if (nbt.hasUUID("prop_name")) {
            data.propUniqueName = nbt.getUUID("prop_name");
        } else {
            data.propName = nbt.getString("prop_name");
        }

        try {
            data.toolTip = Component.Serializer.fromJsonLenient(nbt.getString("tooltip"));
        }catch (JsonParseException ignored) {
            data.toolTip = new TextComponent(nbt.getString("tooltip"));
        }
        try {
            data.displayName = Component.Serializer.fromJsonLenient(nbt.getString("display_name"));
        }catch (JsonParseException ignored) {
            data.displayName = new TextComponent(nbt.getString("display_name"));
        }

        data.displayValue = nbt.getString("display_value");
        data.isGlobal = nbt.getBoolean("global");

        switch (data.type) {
            case BOOLEAN -> {
                data.booleanValue = nbt.getBoolean("value");
                data.booleanFormatter = BooleanFormatter.getSafe(nbt.getByte("formatter"));
            }
            case INTEGER -> {
                data.integerValue = nbt.getInt("value");
                data.integerFormatter = IntegerFormatter.getSafe(nbt.getByte("formatter"));
                data.minValue = nbt.getInt("min");
                data.maxValue = nbt.getInt("max");
            }
            case DECIMAL -> {
                data.decimalValue = nbt.getDouble("value");
                data.decimalFormatter = DecimalFormatter.getSafe(nbt.getByte("formatter"));
                data.minValue = nbt.getDouble("min");
                data.maxValue = nbt.getDouble("max");
            }
            case ENUM -> {
                data.enumValueIndex = nbt.getInt("value");
                if (nbt.contains("names")) {
                    data.enumValueOptions = nbt.getList("names", 3).stream().map(inbt -> ((IntTag) inbt).getAsInt()).collect(Collectors.toList());
                }
                if (nbt.contains("name_values")) {
                    CompoundTag nameValues = nbt.getCompound("name_values");
                    data.enumDisplayValues = nameValues.getAllKeys().stream().collect(Collectors.toMap(Utils::parseInt, nameValues::getString));
                }
            }
        }

        return data;
    }

    public void write(MCDataOutput output) {
        output.writeEnum(type);
        output.writeBoolean(isGlobal);
        if (!isGlobal) output.writeUUID(providerID);
        output.writeString(providerName);
        output.writeBoolean(propUniqueName != null);
        if (propUniqueName != null) {
            output.writeUUID(propUniqueName);
        }else {
            output.writeString(propName);
        }

        switch (type) {
            case BOOLEAN -> output.writeBoolean(booleanValue);
            case INTEGER -> output.writeVarInt(integerValue);
            case DECIMAL -> output.writeDouble(decimalValue);
            case ENUM -> output.writeVarInt(enumValueIndex);
        }
    }

    public static PropertyData read(MCDataInput input) {
        Type type = input.readEnum(Type.class);
        boolean isGlobal = input.readBoolean();
        UUID provID = isGlobal ? null : input.readUUID();
        String provName = input.readString();
        PropertyData data = new PropertyData(provID, provName, type);
        if (input.readBoolean()) {
            data.propUniqueName = input.readUUID();
        }else {
            data.propName = input.readString();
        }

        data.isGlobal = isGlobal;
        switch (type) {
            case BOOLEAN -> data.booleanValue = input.readBoolean();
            case INTEGER -> data.integerValue = input.readVarInt();
            case DECIMAL -> data.decimalValue = input.readDouble();
            case ENUM -> data.enumValueIndex = input.readVarInt();
        }
        return data;
    }
}
