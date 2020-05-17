package com.brandon3055.draconicevolution.api.config;

import codechicken.lib.data.MCDataInput;
import codechicken.lib.data.MCDataOutput;
import com.brandon3055.draconicevolution.api.capability.PropertyProvider;
import com.brandon3055.draconicevolution.client.gui.modular.itemconfig.PropertyData;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.common.util.INBTSerializable;

import java.util.function.Function;

/**
 * Created by brandon3055 on 2/5/20.
 * This is the base for the "Property" object. These properties can be supplied by a {@link PropertyProvider}
 * But they can also exist independently in the case of property presets.
 * Though technically i wont be storing the actual property object just its name and serialized data.
 * When i want to apply the preset i just retrieve the property from the provider and load inject the nbt.
 */
public abstract class ConfigProperty implements INBTSerializable<CompoundNBT> {
    private String name;
    private ITextComponent displayName;
    private boolean showOnHud = true;

    public ConfigProperty(String name) {
        this.name = name;
    }

    public ConfigProperty(String name, ITextComponent displayName) {
        this.name = name;
        this.displayName = displayName;
    }

    /**
     * @return the display name for this config property. e.g. Mining AOE
     */
    public ITextComponent getDisplayName() {
        return displayName == null ? new TranslationTextComponent("item_property.draconicevolution." + name + ".name") : displayName;
    }

    public ITextComponent getToolTip() {
        return new TranslationTextComponent("item_property.draconicevolution." + name + ".info");
    }

    /**
     * For properties that use a formatter this MUST be the same as getFormatter().format(getValue());
     *
     * @return the formatted value of this property.
     */
    public abstract String getDisplayValue();

    /**
     * This name will be used to identify this within its parent {@link PropertyProvider} A property provider can never have more than one
     * property with the same name.
     *
     * @return the name of this property. e.g. "mining_aoe"
     */
    public String getName() {
        return name;
    }

    /**
     * Called on the client and the server when this value is changed by the user.
     *
     * @param stack the {@link ItemStack} this property belongs to.
     */
    public abstract void onValueChanged(ItemStack stack);

    public abstract Type getType();

    public boolean showOnHud() {
        return showOnHud;
    }

    public void setShowOnHud(boolean showOnHud) {
        this.showOnHud = showOnHud;
    }

    @Override
    public CompoundNBT serializeNBT() {
        CompoundNBT nbt = new CompoundNBT();
        nbt.putBoolean("hud", showOnHud);
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt) {
        showOnHud = nbt.getBoolean("hud");
    }

    public void serializeMCData(MCDataOutput output) {
        output.writeBoolean(showOnHud);
    }

    public void deSerializeMCData(MCDataInput input) {
        showOnHud = input.readBoolean();
    }

    public abstract void loadData(PropertyData data);

    public enum Type {
        BOOLEAN,
        INTEGER,
        DECIMAL,
        ENUM;

        public static Type getSafe(int index) {
            return index >= 0 && index < values().length ? values()[index] : BOOLEAN;
        }
    }

    public enum BooleanFormatter {
        TRUE_FALSE(e -> I18n.format("gui.draconicevolution.boolean_property." + (e ? "true" : "false"))),
        ENABLED_DISABLED(e -> I18n.format("gui.draconicevolution.boolean_property." + (e ? "enabled" : "disabled"))),
        ACTIVE_INACTIVE(e -> I18n.format("gui.draconicevolution.boolean_property." + (e ? "active" : "inactive"))),
        YES_NO(e -> I18n.format("gui.draconicevolution.boolean_property." + (e ? "yes" : "no")));

        private Function<Boolean, String> formatter;

        BooleanFormatter(Function<Boolean, String> formatter) {
            this.formatter = formatter;
        }

        public String format(boolean value) {
            return formatter.apply(value);
        }

        public static BooleanFormatter getSafe(int index) {
            return index >= 0 && index < values().length ? values()[index] : TRUE_FALSE;
        }
    }

    public enum IntegerFormatter {
        RAW(String::valueOf);
        //Will add formatters as needed

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
    }

    public enum DecimalFormatter {
        RAW_0(e -> String.valueOf(e.intValue())),
        RAW_1(e -> String.format("%.1f", e)),
        RAW_2(e -> String.format("%.2f", e)),
        RAW_3(e -> String.format("%.3f", e)),
        RAW_4(e -> String.format("%.4f", e));
        //Will add formatters as needed

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
    }
}
