package com.brandon3055.draconicevolution.api.modules.lib;

import com.brandon3055.brandonscore.api.TechLevel;
import com.brandon3055.draconicevolution.api.capability.ModuleHost;
import com.brandon3055.draconicevolution.api.capability.PropertyProvider;
import com.brandon3055.draconicevolution.api.config.ConfigProperty;
import com.brandon3055.draconicevolution.api.modules.ModuleCategory;
import com.brandon3055.draconicevolution.api.modules.ModuleRegistry;
import com.brandon3055.draconicevolution.api.modules.ModuleType;
import com.brandon3055.draconicevolution.api.modules.ModuleTypes;
import com.brandon3055.draconicevolution.api.modules.data.EnergyData;
import com.brandon3055.draconicevolution.api.modules.data.EnergyShareData;
import com.brandon3055.draconicevolution.api.modules.data.ModuleData;
import net.covers1624.quack.util.SneakyUtils;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by brandon3055 and covers1624 on 4/16/20.
 */
public class ModuleHostImpl implements ModuleHost, PropertyProvider {
    private static final Logger LOGGER = LogManager.getLogger(ModuleHostImpl.class);

    private final int gridWidth;
    private final int gridHeight;
    private UUID providerID = null;
    private final String providerName;
    private final boolean deleteInvalidModules;
    private final TechLevel techLevel;
    private final List<ModuleEntity<?>> moduleEntities = new ArrayList<>();
    private final Set<ModuleType<?>> additionalTypeList = new HashSet<>();
    private final Set<ModuleType<?>> typeBlackList = new HashSet<>();
    private final Set<ModuleCategory> categories = new HashSet<>();
    private final List<ConfigProperty> providedProperties = new ArrayList<>();
    private final Map<String, ConfigProperty> propertyMap = new LinkedHashMap<>();
    private final Map<ModuleType<?>, Consumer<?>> propertyValidators = new HashMap<>();
    private final Map<ModuleType<?>, ModuleData<?>> moduleDataCache = new HashMap<>();
    private Consumer<List<ConfigProperty>> propertyBuilder;
    private BiFunction<ModuleEntity<?>, List<Component>, Boolean> removeCheck = null;

    public ModuleHostImpl(TechLevel techLevel, int gridWidth, int gridHeight, String providerName, boolean deleteInvalidModules, ModuleCategory... categories) {
        this.techLevel = techLevel;
        this.gridWidth = gridWidth;
        this.gridHeight = gridHeight;
        this.providerName = providerName;
        this.deleteInvalidModules = deleteInvalidModules;
        this.categories.addAll(Arrays.asList(categories));
    }

    public void setRemoveCheck(BiFunction<ModuleEntity<?>, List<Component>, Boolean> removeCheck) {
        this.removeCheck = removeCheck;
    }

    //region ModuleHost

    @Override
    public Stream<com.brandon3055.draconicevolution.api.modules.Module<?>> getModules() {
        return getModuleEntities().stream().map(ModuleEntity::getModule);
    }

    @Override
    public List<ModuleEntity<?>> getModuleEntities() {
        synchronized (moduleEntities) {
            return Collections.unmodifiableList(moduleEntities);
        }
    }

    @Override
    public void addModule(ModuleEntity<?> entity, ModuleContext context) {
        synchronized (moduleEntities) {
            moduleEntities.add(entity);
        }
        entity.setHost(this);
        clearCaches();
        entity.onInstalled(context);
        gatherProperties();
    }

    @Override
    public void removeModule(ModuleEntity<?> entity, ModuleContext context) {
        synchronized (moduleEntities) {
            moduleEntities.remove(entity);
        }
        clearCaches();
        entity.onRemoved(context);
        gatherProperties();
    }

    public void transferModules(ModuleHostImpl source) {
        if (getGridWidth() >= source.getGridWidth() && getGridHeight() >= source.getGridHeight()) {
            synchronized (moduleEntities) {
                moduleEntities.addAll(source.getModuleEntities());
                source.moduleEntities.clear();
                moduleEntities.forEach(moduleEntity -> moduleEntity.setHost(this));
            }
            clearCaches();
            gatherProperties();
        } else {
            LOGGER.warn("Cant transfer modules to smaller grid");
        }
    }

    @Override
    public Collection<ModuleCategory> getModuleCategories() {
        return categories;
    }

    public ModuleHostImpl addCategories(ModuleCategory... categories) {
        this.categories.addAll(Arrays.asList(categories));
        return this;
    }

    @Override
    public Collection<ModuleType<?>> getAdditionalTypes() {
        return additionalTypeList;
    }

    @Override
    public Collection<ModuleType<?>> getTypeBlackList() {
        return typeBlackList;
    }

    /**
     * Allows you to specifically allow certain module types bypassing the category system.
     */
    public ModuleHostImpl addAdditionalType(ModuleType<?> type) {
        additionalTypeList.add(type);
        return this;
    }

    /**
     * Allows you to specifically deny certain module types bypassing the category system.
     */
    public ModuleHostImpl blackListType(ModuleType<?> type) {
        typeBlackList.add(type);
        return this;
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

    @Override
    public void handleTick(ModuleContext context) {
        getModuleEntities().forEach(e -> e.tick(context));
    }

    @Nullable
    @Override
    public <T extends ModuleData<T>> T getModuleData(ModuleType<T> moduleType) {
        //noinspection unchecked
        return (T) moduleDataCache.computeIfAbsent(moduleType, ModuleHost.super::getModuleData);
    }

    @Override
    public boolean checkRemoveModule(ModuleEntity<?> module, List<Component> reason) {
        return removeCheck == null || removeCheck.apply(module, reason);
    }

    private void clearCaches() {
        moduleDataCache.clear();
        getModuleEntities().forEach(ModuleEntity::clearCaches);
    }

    //endregion

    //region PropertyProvider

    public void addPropertyBuilder(Consumer<List<ConfigProperty>> propertyBuilder) {
        Consumer<List<ConfigProperty>> builder = this.propertyBuilder;
        if (builder == null) {
            this.propertyBuilder = propertyBuilder;
        } else {
            this.propertyBuilder = builder.andThen(propertyBuilder);
        }
        gatherProperties();
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
        synchronized (moduleEntities) {
            //TODO there are issues with this. It does not update properties when modules change
            List<ConfigProperty> gathered = new ArrayList<>();
            if (propertyBuilder != null) {
                propertyBuilder.accept(gathered);
            }

            Set<ModuleType<?>> installedTypes = getInstalledTypes().collect(Collectors.toSet());
            propertyValidators.entrySet().removeIf(e -> !installedTypes.contains(e.getKey()));

            installedTypes.forEach(type -> {
                Map<ConfigProperty, Consumer<?>> map = new HashMap<>();
                type.getTypeProperties(SneakyUtils.unsafeCast(getModuleData(type)), SneakyUtils.unsafeCast(map));
                gathered.addAll(map.keySet());
                if (propertyValidators.containsKey(type)) {
                    propertyValidators.get(type).accept(SneakyUtils.unsafeCast(getModuleData(type)));
                } else {
                    map.forEach((property, consumer) -> {
                        if (consumer != null) {
                            propertyValidators.put(type, consumer);
                        }
                    });
                }
            });

            //Gather is not just called on load but also when a property is added or removed so we need to avoid overwriting existing loaded properties.
            Set<String> gatheredNames = gathered.stream().map(ConfigProperty::getName).collect(Collectors.toSet());
            //Remove properties that no longer exist
            providedProperties.removeIf(e -> !gatheredNames.contains(e.getName()));

            Set<String> installedNames = providedProperties.stream().map(ConfigProperty::getName).collect(Collectors.toSet());
            //Add new properties
            providedProperties.addAll(gathered.stream().filter(e -> !installedNames.contains(e.getName())).toList());

            //Repopulate the property map.
            propertyMap.clear();
            providedProperties.forEach(e -> propertyMap.put(e.getName(), e));

            getModuleEntities().forEach(e -> e.getEntityProperties().forEach(p -> {
                if (propertyMap.containsKey(p.getName())) {
                    p.generateUnique(); //This avoids duplicate names due to creative duped items.
                }
                propertyMap.put(p.getName(), p);
            }));
        }
    }

    //endregion

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag nbt = new CompoundTag();
        //Serialize Modules
        ListTag modules = new ListTag();
        synchronized (moduleEntities) {
            for (ModuleEntity<?> entity : moduleEntities) {
                CompoundTag entityNBT = new CompoundTag();
                entityNBT.putString("id", entity.module.getRegistryName().toString());
                entity.writeToNBT(entityNBT);
                modules.add(entityNBT);
            }
        }
        nbt.put("modules", modules);

        //Serialize Properties
        nbt.putUUID("provider_id", getProviderID());
        CompoundTag properties = new CompoundTag();
        providedProperties.forEach(e -> properties.put(e.getName(), e.serializeNBT()));
        nbt.put("properties", properties);
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        synchronized (moduleEntities) {
            clearCaches();
            //Deserialize modules first
            moduleEntities.clear();
            ListTag modules = nbt.getList("modules", 10);
            modules.stream().map(inbt -> (CompoundTag) inbt).forEach(compound -> {
                ResourceLocation id = new ResourceLocation(compound.getString("id"));
                com.brandon3055.draconicevolution.api.modules.Module<?> module = ModuleRegistry.getRegistry().getValue(id);
                if (module == null) {
                    LOGGER.warn("Failed to load unregistered module: " + id + " Skipping...");
                } else {
                    ModuleEntity<?> entity = module.createEntity();
                    entity.readFromNBT(compound);
                    if (deleteInvalidModules && !entity.isPosValid(gridWidth, gridHeight)) {
                        LOGGER.warn("Deleting module from invalid grid position: " + entity.toString());
                    } else {
                        moduleEntities.add(entity);
                        entity.setHost(this);
                    }
                }
            });

            //So that we can gather properties which may depend on installed modules.
            gatherProperties();
            if (nbt.hasUUID("provider_id")) {
                providerID = nbt.getUUID("provider_id");
            }
            CompoundTag properties = nbt.getCompound("properties");
            providedProperties.forEach(e -> e.deserializeNBT(properties.getCompound(e.getName())));
        }
    }
}
