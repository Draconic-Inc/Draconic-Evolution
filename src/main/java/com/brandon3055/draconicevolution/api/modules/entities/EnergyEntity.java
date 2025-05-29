package com.brandon3055.draconicevolution.api.modules.entities;

import com.brandon3055.brandonscore.api.power.IOPStorage;
import com.brandon3055.draconicevolution.api.modules.Module;
import com.brandon3055.draconicevolution.api.modules.ModuleTypes;
import com.brandon3055.draconicevolution.api.modules.data.EnergyData;
import com.brandon3055.draconicevolution.api.modules.lib.ModuleContext;
import com.brandon3055.draconicevolution.api.modules.lib.ModuleEntity;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;

public class EnergyEntity extends ModuleEntity<EnergyData> {

    private long energy = 0;

    public EnergyEntity(Module<EnergyData> module) {
        super(module);
    }

    @Override
    public void onRemoved(ModuleContext context) {
        super.onRemoved(context);
        IOPStorage storage = context.getOpStorage();
        if (energy > 0 && storage != null) {
            storage.modifyEnergyStored(-energy);
        }
    }

    @Override
    public void onInstalled(ModuleContext context) {
        super.onInstalled(context);
        IOPStorage storage = context.getOpStorage();
        if (energy > 0 && storage != null) {
            storage.modifyEnergyStored(energy);
        }
    }

    @Override
    protected void writeToItemStack(ItemStack stack, CompoundTag tag, ModuleContext context, HolderLookup.Provider provider) {
        super.writeToItemStack(stack, tag, context, provider);
        IOPStorage storage = context.getOpStorage();
        if (storage != null) {
            long moduleCap = ModuleTypes.ENERGY_STORAGE.getData(module).capacity();
            long newCapacity = storage.getMaxOPStored() - moduleCap;
            if (newCapacity < storage.getOPStored()) {
                energy = Math.min(storage.getOPStored() - newCapacity, moduleCap);
                tag.putLong("stored_energy", energy);
            } else {
                energy = 0;
            }
        }
    }

    @Override
    public void readFromItemStack(ItemStack stack, CompoundTag tag, ModuleContext context, HolderLookup.Provider provider) {
        super.readFromItemStack(stack, tag, context, provider);
        energy = tag.getLong("stored_energy");
    }
}
