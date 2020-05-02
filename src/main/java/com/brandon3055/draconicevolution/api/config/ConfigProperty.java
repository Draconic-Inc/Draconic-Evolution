package com.brandon3055.draconicevolution.api.config;

import codechicken.lib.data.MCDataInput;
import codechicken.lib.data.MCDataOutput;
import com.brandon3055.draconicevolution.api.capability.PropertyProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.common.util.INBTSerializable;

/**
 * Created by brandon3055 on 2/5/20.
 * This is the base for the "Property" object. These properties can be supplied by a {@link PropertyProvider}
 * But they can also exist independently in the case of property presets.
 * Though technically i wont be storing the actual property object just its name and serialized data.
 * When i want to apply the preset i just retrieve the property from the provider and load inject the nbt.
 */
public interface ConfigProperty extends INBTSerializable<CompoundNBT> {

    /**
     * @return the display name for this config property. e.g. Mining AOE
     */
    ITextComponent getDisplayName();

    /**
     * This may will be used in conjunction with {@link #getDisplayName()} <br>
     * The format will be {@link #getDisplayName().getFormattedText()} + ": " + {@link #getDisplayValue()}
     * @return the formatted value of this property.
     */
    String getDisplayValue();

    /**
     * @return the name of this property. e.g. "mining_aoe" There should be no other property with the same name in the parent {@link PropertyProvider}
     */
    String getName();

    /**
     * @param stack the {@link ItemStack} this property belongs to.
     */
    void onValueChanged(ItemStack stack);

    boolean showOnHud();

    void serializeMCData(MCDataOutput output);

    void deSerializeMCData(MCDataInput input);
}
