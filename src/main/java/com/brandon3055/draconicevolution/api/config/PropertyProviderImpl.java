package com.brandon3055.draconicevolution.api.config;

import com.brandon3055.draconicevolution.api.capability.PropertyProvider;
import net.minecraft.nbt.CompoundTag;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by brandon3055 on 2/5/20.
 */
public class PropertyProviderImpl implements PropertyProvider {

    private UUID providerID = null;
    private String providerName;
    private Map<String, ConfigProperty> propertyMap = new HashMap<>();

    /**
     * @see #getProviderName()
     */
    public PropertyProviderImpl(String providerName) {
        this.providerName = providerName;
    }

    @Override
    public UUID getProviderID() {
        if (providerID == null) {
            regenProviderID();
        }
        return providerID;
    }

    @Override
    public String getProviderName() {
        return providerName;
    }

    @Override
    public void regenProviderID() {
        providerID = UUID.randomUUID();
    }

    @Override
    public Collection<ConfigProperty> getProperties() {
        return propertyMap.values();
    }

    @Nullable
    @Override
    public ConfigProperty getProperty(String propertyID) {
        return propertyMap.get(propertyID);
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag nbt = new CompoundTag();
        nbt.putUUID("provider_id", getProviderID());
        CompoundTag properties = new CompoundTag();
        propertyMap.forEach((name, property) -> properties.put(name, property.serializeNBT()));
        nbt.put("properties", properties);
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        if (nbt.hasUUID("provider_id")){
            providerID = nbt.getUUID("provider_id");
        }
        CompoundTag properties = nbt.getCompound("properties");
        propertyMap.forEach((name, property) -> property.deserializeNBT(properties.getCompound(name)));
    }
}
