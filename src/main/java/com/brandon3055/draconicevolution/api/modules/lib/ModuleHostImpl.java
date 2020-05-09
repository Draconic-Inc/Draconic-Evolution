package com.brandon3055.draconicevolution.api.modules.lib;

import com.brandon3055.draconicevolution.api.TechLevel;
import com.brandon3055.draconicevolution.api.capability.PropertyProvider;
import com.brandon3055.draconicevolution.api.config.ConfigProperty;
import com.brandon3055.draconicevolution.api.modules.Module;
import com.brandon3055.draconicevolution.api.modules.ModuleRegistry;
import com.brandon3055.draconicevolution.api.modules.ModuleType;
import com.brandon3055.draconicevolution.api.capability.ModuleHost;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.INBTSerializable;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by brandon3055 and covers1624 on 4/16/20.
 */
public class ModuleHostImpl implements ModuleHost, PropertyProvider {

    private static Logger logger = LogManager.getLogger("draconic-modules");

    private int gridWidth;
    private int gridHeight;
    private UUID providerID = null;
    private String providerName;
    private TechLevel techLevel;
    private List<ModuleEntity> moduleEntities = new ArrayList<>();
    private List<ModuleType<?>> supportedTypes = new ArrayList<>();
    private Map<String, ConfigProperty> propertyMap = new HashMap<>();
    private Consumer<List<ConfigProperty>> propertyBuilder;

    public ModuleHostImpl(TechLevel techLevel, int gridWidth, int gridHeight, String providerName, ModuleType<?>... supportedTypes) {
        this.techLevel = techLevel;
        this.gridWidth = gridWidth;
        this.gridHeight = gridHeight;
        this.providerName = providerName;
        this.supportedTypes.addAll(Arrays.asList(supportedTypes));
    }

    //region ModuleHost

    @Override
    public Stream<Module<?>> getModules() {
        return getModuleEntities().stream().map(ModuleEntity::getModule);
    }

    @Override
    public List<ModuleEntity> getModuleEntities() {
        return moduleEntities;
    }

    @Override
    public List<ModuleType<?>> getSupportedTypes() {
        return Collections.unmodifiableList(supportedTypes);
    }

    @Override
    public TechLevel getHostTechLevel() {
        return techLevel;
    }

    @Override
    public int getGridWidth() {
        return gridWidth;
    }

    @Override
    public int getGridHeight() {
        return gridHeight;
    }

    //end

    //region PropertyProvider

    public void setPropertyBuilder(Consumer<List<ConfigProperty>> propertyBuilder) {
        this.propertyBuilder = propertyBuilder;
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

    private void gatherProperties() {
        List<ConfigProperty> gathered = new ArrayList<>();
        //TODO Gather properties from modules

        if (propertyBuilder != null) {
            propertyBuilder.accept(gathered);
        }

        Map<String, ConfigProperty> gatheredMap = gathered.stream().collect(Collectors.toMap(ConfigProperty::getName, property -> property));
        propertyMap.entrySet().removeIf(entry -> !gatheredMap.containsKey(entry.getKey())); //Clear out old properties that no longer exist
        gatheredMap.forEach((name, property) -> propertyMap.putIfAbsent(name, property));
    }

    //end

    @Override
    public CompoundNBT serializeNBT() {
        CompoundNBT nbt = new CompoundNBT();
        //Serialize Modules
        ListNBT modules = new ListNBT();
        for (ModuleEntity entity : moduleEntities) {
            CompoundNBT entityNBT = new CompoundNBT();
            entityNBT.putString("id", entity.module.getRegistryName().toString());
            entity.writeToNBT(entityNBT);
            modules.add(entityNBT);
        }
        nbt.put("modules", modules);

        //Serialize Properties
        nbt.putUniqueId("provider_id", getProviderID());
        CompoundNBT properties = new CompoundNBT();
        propertyMap.forEach((name, property) -> properties.put(name, property.serializeNBT()));
        nbt.put("properties", properties);
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt) {
        //Deserialize modules first
        moduleEntities.clear();
        ListNBT modules = nbt.getList("modules", 10);
        modules.stream().map(inbt -> (CompoundNBT) inbt).forEach(compound -> {
            ResourceLocation id = new ResourceLocation(compound.getString("id"));
            Module<?> module = ModuleRegistry.getRegistry().getValue(id);
            if (module == null) {
                logger.warn("Failed to load unregistered module: " + id + " Skipping...");
            }
            else {
                ModuleEntity entity = new ModuleEntity(module);
                entity.readFromNBT(compound);
                moduleEntities.add(entity);
            }
        });

        //So that we can gather properties which may depend on installed modules.
        gatherProperties();
        if (nbt.hasUniqueId("provider_id")){
            providerID = nbt.getUniqueId("provider_id");
        }
        CompoundNBT properties = nbt.getCompound("properties");
        propertyMap.forEach((name, property) -> property.deserializeNBT(properties.getCompound(name)));
    }
}
