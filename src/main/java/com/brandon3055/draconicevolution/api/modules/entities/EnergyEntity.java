package com.brandon3055.draconicevolution.api.modules.entities;

import com.brandon3055.brandonscore.api.power.IOPStorage;
import com.brandon3055.brandonscore.api.power.IOPStorageModifiable;
import com.brandon3055.draconicevolution.api.modules.Module;
import com.brandon3055.draconicevolution.api.modules.ModuleTypes;
import com.brandon3055.draconicevolution.api.modules.lib.ModuleContext;
import com.brandon3055.draconicevolution.api.modules.lib.ModuleEntity;
import net.minecraft.world.item.ItemStack;

public class EnergyEntity extends ModuleEntity {

    private long energy = 0;

    public EnergyEntity(Module<?> module) {
        super(module);
    }

    @Override
    public void onRemoved(ModuleContext context) {
        super.onRemoved(context);
        IOPStorage storage = context.getOpStorage();
        if (energy > 0 && storage instanceof IOPStorageModifiable) {
            ((IOPStorageModifiable) storage).modifyEnergyStored(-energy);
        } else if (energy > 0 && storage != null) {
            storage.extractOP(energy, false);
        }
    }

    @Override
    public void onInstalled(ModuleContext context) {
        super.onInstalled(context);
        IOPStorage storage = context.getOpStorage();
        if (energy > 0 && storage instanceof IOPStorageModifiable) {
            ((IOPStorageModifiable) storage).modifyEnergyStored(energy);
        } else if (energy > 0 && storage != null) {
            storage.receiveOP(energy, false);
        }
    }

    @Override
    public void writeToItemStack(ItemStack stack, ModuleContext context) {
        super.writeToItemStack(stack, context);
        IOPStorage storage = context.getOpStorage();
        if (storage != null) {
            long moduleCap = ModuleTypes.ENERGY_STORAGE.getData(module).getCapacity();
            long newCapacity = storage.getMaxOPStored() - moduleCap;
            if (newCapacity < storage.getEnergyStored()) {
                energy = Math.min(storage.getEnergyStored() - newCapacity, moduleCap);
                stack.getOrCreateTag().putLong("stored_energy", energy);
            }
        }
    }

    @Override
    public void readFromItemStack(ItemStack stack, ModuleContext context) {
        super.readFromItemStack(stack, context);
        if (stack.hasTag()) {
            energy = stack.getOrCreateTag().getLong("stored_energy");
        }
    }
}
