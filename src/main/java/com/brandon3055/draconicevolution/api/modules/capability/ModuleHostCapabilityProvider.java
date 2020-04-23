package com.brandon3055.draconicevolution.api.modules.capability;

import com.brandon3055.brandonscore.utils.FacingUtils;
import com.brandon3055.draconicevolution.init.ModuleCapability;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.UUID;

/**
 * Created by brandon3055 on 17/4/20.
 */
public class ModuleHostCapabilityProvider implements ICapabilitySerializable<CompoundNBT> {

    private IModuleHost moduleHost;
    private UUID hostID;

    public ModuleHostCapabilityProvider(IModuleHost moduleHost) {
        this.moduleHost = moduleHost;
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        if (cap == ModuleCapability.MODULE_HOST_CAPABILITY){
            return LazyOptional.of(() -> moduleHost).cast();
        }
        return LazyOptional.empty();
    }

    @Override
    public CompoundNBT serializeNBT() {
        CompoundNBT nbt = moduleHost.serializeNBT();
        if (hostID == null) {
            nbt.putUniqueId("host_id", UUID.randomUUID());
        }
        else {
            nbt.putUniqueId("host_id", hostID);
        }
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt) {
        moduleHost.deserializeNBT(nbt);
        if (nbt.hasUniqueId("host_id")){
            hostID = nbt.getUniqueId("host_id");
        }
    }
}
