package com.brandon3055.draconicevolution.api.modules.lib;

import codechicken.lib.util.SneakyUtils;
import com.brandon3055.brandonscore.api.TechLevel;
import com.brandon3055.brandonscore.api.power.IOPStorageModifiable;
import com.brandon3055.draconicevolution.DraconicEvolution;
import com.brandon3055.draconicevolution.api.capability.PropertyProvider;
import com.brandon3055.draconicevolution.api.config.ConfigProperty;
import com.brandon3055.draconicevolution.api.modules.*;
import com.brandon3055.draconicevolution.api.capability.ModuleHost;
import com.brandon3055.draconicevolution.api.modules.data.EnergyData;
import com.brandon3055.draconicevolution.api.modules.data.ModuleData;
import com.brandon3055.draconicevolution.api.modules.data.EnergyShareData;
import com.google.common.collect.Multimap;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Consumer;
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
    private boolean deleteInvalidModules;
    private TechLevel techLevel;
    private EnergyData energyCache = null;
    private ModuleData energyLinkCache = null;
    private EnergyShareData energyShareCache = null;
    private final List<ModuleEntity> moduleEntities = new ArrayList<>();
    private Set<ModuleType<?>> additionalTypeList = new HashSet<>();
    private Set<ModuleType<?>> typeBlackList = new HashSet<>();
    private Set<ModuleCategory> categories = new HashSet<>();
    private List<ConfigProperty> providedProperties = new ArrayList<>();
    private Map<String, ConfigProperty> propertyMap = new LinkedHashMap<>();
    private Consumer<List<ConfigProperty>> propertyBuilder;
    private Map<ModuleType<?>, Consumer<?>> propertyValidators = new HashMap<>();

    public ModuleHostImpl(TechLevel techLevel, int gridWidth, int gridHeight, String providerName, boolean deleteInvalidModules, ModuleCategory... categories) {
        this.techLevel = techLevel;
        this.gridWidth = gridWidth;
        this.gridHeight = gridHeight;
        this.providerName = providerName;
        this.deleteInvalidModules = deleteInvalidModules;
        this.categories.addAll(Arrays.asList(categories));
    }

    //region ModuleHost

    @Override
    public Stream<Module<?>> getModules() {
        return getModuleEntities().stream().map(ModuleEntity::getModule);
    }

    @Override
    public List<ModuleEntity> getModuleEntities() {
        synchronized (moduleEntities) {
            return Collections.unmodifiableList(moduleEntities);
        }
    }

    @Override
    public void addModule(ModuleEntity entity, ModuleContext context) {
        synchronized (moduleEntities) {
            moduleEntities.add(entity);
        }
        entity.setHost(this);
        clearCaches();
        entity.onInstalled(context);
        gatherProperties();
    }

    @Override
    public void removeModule(ModuleEntity entity, ModuleContext context) {
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
            logger.warn("Cant transfer modules to smaller grid");
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
    public void getAttributeModifiers(EquipmentSlotType slot, ItemStack stack, Multimap<Attribute, AttributeModifier> map) {
        getInstalledTypes().forEach(t -> t.getAttributeModifiers(SneakyUtils.unsafeCast(getModuleData(t)), slot, stack, map));
        getModuleEntities().forEach(e -> e.getAttributeModifiers(slot, stack, map));
    }

    public void handleTick(ModuleContext context) {
        getModuleEntities().forEach(e -> e.tick(context));
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
            providedProperties.addAll(gathered.stream().filter(e -> !installedNames.contains(e.getName())).collect(Collectors.toList()));

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

    //helpers

    private void clearCaches() {
        energyLinkCache = null;
        energyCache = null;
        getModuleEntities().forEach(ModuleEntity::clearCaches);
    }

    public ModuleData getEnergyLink() {
        return energyLinkCache; //TODO
    }

    public EnergyShareData getEnergyShare() {
        if (energyShareCache == null) {
            energyShareCache = getModuleData(ModuleTypes.ENERGY_SHARE, new EnergyShareData(0));
        }
        return energyShareCache; //TODO
    }

    /**
     * @return the energy module data for this host. This data is cached for efficiency.
     */
    public EnergyData getEnergyData() {
        if (energyCache == null) {
            energyCache = getModuleData(ModuleTypes.ENERGY_STORAGE, new EnergyData(0, 0));
        }
        return energyCache;
    }

    @Override
    public CompoundNBT serializeNBT() {
        CompoundNBT nbt = new CompoundNBT();
        //Serialize Modules
        ListNBT modules = new ListNBT();
        synchronized (moduleEntities) {
            for (ModuleEntity entity : moduleEntities) {
                CompoundNBT entityNBT = new CompoundNBT();
                entityNBT.putString("id", entity.module.getRegistryName().toString());
                entity.writeToNBT(entityNBT);
                modules.add(entityNBT);
            }
        }
        nbt.put("modules", modules);

        //Serialize Properties
        nbt.putUUID("provider_id", getProviderID());
        CompoundNBT properties = new CompoundNBT();
        providedProperties.forEach(e -> properties.put(e.getName(), e.serializeNBT()));
        nbt.put("properties", properties);
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt) {
        synchronized (moduleEntities) {
            clearCaches();
            //Deserialize modules first
            moduleEntities.clear();
            ListNBT modules = nbt.getList("modules", 10);
            modules.stream().map(inbt -> (CompoundNBT) inbt).forEach(compound -> {
                ResourceLocation id = new ResourceLocation(compound.getString("id"));
                Module<?> module = ModuleRegistry.getRegistry().getValue(id);
                if (module == null) {
                    logger.warn("Failed to load unregistered module: " + id + " Skipping...");
                } else {
                    ModuleEntity entity = module.createEntity();
                    entity.readFromNBT(compound);
                    if (deleteInvalidModules && !entity.isPosValid(gridWidth, gridHeight)) {
                        logger.warn("Deleting module from invalid grid position: " + entity.toString());
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
            CompoundNBT properties = nbt.getCompound("properties");
            providedProperties.forEach(e -> e.deserializeNBT(properties.getCompound(e.getName())));
        }
    }
}
