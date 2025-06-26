package com.brandon3055.draconicevolution.api.modules.lib;

import com.brandon3055.brandonscore.api.TechLevel;
import com.brandon3055.draconicevolution.DraconicEvolution;
import com.brandon3055.draconicevolution.api.DataComponentAccessor;
import com.brandon3055.draconicevolution.api.capability.ModuleHost;
import com.brandon3055.draconicevolution.api.capability.PropertyProvider;
import com.brandon3055.draconicevolution.api.config.BooleanProperty;
import com.brandon3055.draconicevolution.api.config.ConfigProperty;
import com.brandon3055.draconicevolution.api.modules.ModuleCategory;
import com.brandon3055.draconicevolution.api.modules.ModuleType;
import com.brandon3055.draconicevolution.api.modules.data.ModuleData;
import com.brandon3055.draconicevolution.init.ItemData;
import net.covers1624.quack.collection.FastStream;
import net.covers1624.quack.util.SneakyUtils;
import net.minecraft.network.chat.Component;
import net.neoforged.fml.util.thread.EffectiveSide;
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
    private final String providerName;
    private final boolean deleteInvalidModules;
    private final TechLevel techLevel;

    private final Set<ModuleType<?>> additionalTypeList = new HashSet<>();
    private final Set<ModuleType<?>> typeBlackList = new HashSet<>();
    private final Set<ModuleCategory> categories = new HashSet<>();
    private final Map<String, ConfigProperty> propertyMap = new LinkedHashMap<>();
    private final Map<ModuleType<?>, Consumer<?>> propertyValidators = new HashMap<>();
    private final Map<ModuleType<?>, ModuleData<?>> moduleDataCache = new HashMap<>();
    private Consumer<List<ConfigProperty>> propertyBuilder;
    private BiFunction<ModuleEntity<?>, List<Component>, Boolean> removeCheck = null;
    private DataComponentAccessor dataAccess = null;

    private boolean isDirty = true;

    //Serialized
    private final List<ModuleEntity<?>> moduleEntities = new ArrayList<>();
    private final List<ConfigProperty> providedProperties = new ArrayList<>();
    private UUID identity = null;

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
    public UUID getIdentity() {
        if (identity == null) {
            regenIdentity();
        }
        return identity;
    }

    @Override
    public void regenIdentity() {
        identity = UUID.randomUUID();
    }

    @Override
    public String getProviderName() {
        return providerName;
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
            providedProperties.forEach(e -> {
                e.setProvider(this);
                propertyMap.put(e.getName(), e);
            });

            getModuleEntities().forEach(e -> {
                List<ConfigProperty> entityProps = new ArrayList<>();
                e.getEntityProperties(entityProps);
                entityProps.forEach(p -> {
                    if (propertyMap.containsKey(p.getName())) {
                        //TODO, This is a data change that will need to be saved, but theoretically, this will never get called.
                        p.generateUnique(); //This avoids duplicate names due to creative duped items.
                    }
                    propertyMap.put(p.getName(), p);
                });
            });
        }
    }

    //endregion
    public void saveData() {
        if (dataAccess == null) return;

        dataAccess.setter().set(ItemData.MODULE_ENTITIES, FastStream.of(moduleEntities).map(ModuleEntity::copy).toImmutableList(FastStream.infer()));
        dataAccess.setter().set(ItemData.CONFIG_PROPERTIES, FastStream.of(providedProperties).map(ConfigProperty::copy).toImmutableList());
        dataAccess.setter().set(ItemData.PROVIDER_IDENTITY, getIdentity());
    }

    public void loadData() {
        if (dataAccess == null) return;
        moduleEntities.clear();
        providedProperties.clear();

        dataAccess.getter().getOrDefault(ItemData.MODULE_ENTITIES, moduleEntities).forEach(e -> moduleEntities.add(e.copy()));
        dataAccess.getter().getOrDefault(ItemData.CONFIG_PROPERTIES, providedProperties).forEach(e -> providedProperties.add(e.copy()));
        identity = dataAccess.getter().getOrDefault(ItemData.PROVIDER_IDENTITY, getIdentity());

        moduleEntities.forEach(e -> e.setHost(this));
        providedProperties.forEach(e -> e.setProvider(this));
        gatherProperties();
    }

    public void updateDataAccess(DataComponentAccessor newAccess) {
        this.dataAccess = newAccess;
        loadData();
    }

    @Override
    public void markDirty() {
        isDirty = true;
    }

    @Override
    public void close() {
        if (isDirty) {
            isDirty = false;
            saveData();
        }
    }
}
