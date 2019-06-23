package com.brandon3055.draconicevolution.api.itemconfig;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by brandon3055 on 8/06/2016.
 * A simple map wrapper for handling IItemConfigField's
 */
public class ItemConfigFieldRegistry {
    private Map<String, IItemConfigField> fields = new LinkedHashMap<>();

    /**
     * Adds a field to the registry and reads its current value from the given item stack.
     * Will also write the default value to the stack if the stack does not contain a tag for this field.
     */
    public ItemConfigFieldRegistry register(ItemStack stack, IItemConfigField field) {
        fields.put(field.getName(), field);

        NBTTagCompound fieldStorage = ToolConfigHelper.getFieldStorage(stack);
        if (!fieldStorage.hasKey(field.getName())) {
            field.writeToNBT(fieldStorage);
        }
        else {
            field.readFromNBT(fieldStorage);
        }
        return this;
    }

    public IItemConfigField getField(String name) {
        return fields.get(name);
    }

    public Collection<IItemConfigField> getFields() {
        return fields.values();
    }

    public void clear() {
        fields.clear();
    }

    public int size() {
        return fields.size();
    }
}
