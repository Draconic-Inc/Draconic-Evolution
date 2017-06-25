package com.brandon3055.draconicevolution.api.itemconfig;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by brandon3055 on 8/06/2016.
 * A simple map wrapper for handling IItemConfigField's
 */
public class ItemConfigFieldRegistry {
    protected Map<Integer, IItemConfigField> fieldRegistry = new HashMap<Integer, IItemConfigField>();
    protected Map<String, Integer> nameToIndexMap = new HashMap<String, Integer>();
    private int index = 0;

    /**
     * Adds a field to the registry and reads its current value from the given item stack.
     * Will also write the default value to the stack if the stack dose not contain a tag for this field.
     */
    public ItemConfigFieldRegistry register(ItemStack stack, IItemConfigField field) {
        fieldRegistry.put(index, field);
        nameToIndexMap.put(field.getName(), index);

        NBTTagCompound fieldStorage = ToolConfigHelper.getFieldStorage(stack);
        if (!fieldStorage.hasKey(field.getName())) {
            field.writeToNBT(fieldStorage);
        }
        else {
            field.readFromNBT(fieldStorage);
        }

        index++;
        return this;
    }

    public IItemConfigField getField(int index) {
        return fieldRegistry.get(index);
    }

    public IItemConfigField getField(String name) {
        for (IItemConfigField field : fieldRegistry.values()) {
            if (field.getName().equals(name)) {
                return field;
            }
        }
        return null;
    }

    public String getNameFromIndex(int index) {
        IItemConfigField field = getField(index);
        return field == null ? "" : field.getName();
    }

    public int getIndexFromName(String name) {
        Integer index = nameToIndexMap.get(name);
        return index == null ? -1 : index;
    }

    public Collection<IItemConfigField> getFields() {
        return fieldRegistry.values();
    }

    public void clear() {
        index = 0;
        fieldRegistry.clear();
        nameToIndexMap.clear();
    }

    public int size() {
        return fieldRegistry.size();
    }
}
