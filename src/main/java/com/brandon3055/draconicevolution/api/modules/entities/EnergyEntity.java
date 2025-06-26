package com.brandon3055.draconicevolution.api.modules.entities;

import com.brandon3055.brandonscore.api.power.IOPStorage;
import com.brandon3055.draconicevolution.api.modules.Module;
import com.brandon3055.draconicevolution.api.modules.ModuleTypes;
import com.brandon3055.draconicevolution.api.modules.data.EnergyData;
import com.brandon3055.draconicevolution.api.modules.lib.ModuleContext;
import com.brandon3055.draconicevolution.api.modules.lib.ModuleEntity;
import com.brandon3055.draconicevolution.init.DEModules;
import com.brandon3055.draconicevolution.init.ItemData;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;

public class EnergyEntity extends ModuleEntity<EnergyData> {

    private long energy = 0;

    public static final Codec<EnergyEntity> CODEC = RecordCodecBuilder.create(builder -> builder.group(
            DEModules.codec().fieldOf("module").forGetter(EnergyEntity::getModule),
            Codec.INT.fieldOf("gridx").forGetter(ModuleEntity::getGridX),
            Codec.INT.fieldOf("gridy").forGetter(ModuleEntity::getGridY),
            Codec.LONG.fieldOf("energy").forGetter(e -> e.energy)
    ).apply(builder, EnergyEntity::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, EnergyEntity> STREAM_CODEC = StreamCodec.composite(
            DEModules.streamCodec(), ModuleEntity::getModule,
            ByteBufCodecs.INT, ModuleEntity::getGridX,
            ByteBufCodecs.INT, ModuleEntity::getGridY,
            ByteBufCodecs.VAR_LONG, e -> e.energy,
            EnergyEntity::new
    );

    public EnergyEntity(Module<EnergyData> module) {
        super(module);
    }

    EnergyEntity(Module<?> module, int gridX, int gridY, long energy) {
        super((Module<EnergyData>) module, gridX, gridY);
        this.energy = energy;
    }

    @Override
    public ModuleEntity<?> copy() {
        return new EnergyEntity(module, getGridX(), getGridY(), energy);
    }

    @Override
    public Module<EnergyData> getModule() {
        return super.getModule();
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
    public void saveEntityToStack(ItemStack stack, ModuleContext context) {
        IOPStorage storage = context.getOpStorage();
        if (storage != null) {
            long moduleCap = ModuleTypes.ENERGY_STORAGE.getData(module).capacity();
            long newCapacity = storage.getMaxOPStored() - moduleCap;
            if (newCapacity < storage.getOPStored()) {
                energy = Math.min(storage.getOPStored() - newCapacity, moduleCap);
                stack.set(ItemData.ENERGY_MODULE_ENERGY, energy);
                markDirty();
            } else {
                energy = 0;
            }
        }
    }

    @Override
    public void loadEntityFromStack(ItemStack stack, ModuleContext context) {
        energy = stack.getOrDefault(ItemData.ENERGY_MODULE_ENERGY, 0L);
    }
}
