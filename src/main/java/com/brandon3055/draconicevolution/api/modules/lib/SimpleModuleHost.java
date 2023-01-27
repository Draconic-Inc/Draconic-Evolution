package com.brandon3055.draconicevolution.api.modules.lib;

import com.brandon3055.brandonscore.api.TechLevel;
import com.brandon3055.draconicevolution.api.capability.ModuleHost;
import com.brandon3055.draconicevolution.api.modules.ModuleCategory;
import com.brandon3055.draconicevolution.api.modules.ModuleRegistry;
import com.brandon3055.draconicevolution.api.modules.ModuleType;
import com.brandon3055.draconicevolution.api.modules.data.ModuleData;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.BiFunction;
import java.util.stream.Stream;

/**
 * Created by brandon3055 on 16/11/2022
 */
public class SimpleModuleHost implements ModuleHost {
    private static final Logger LOGGER = LogManager.getLogger(SimpleModuleHost.class);

    private final int gridWidth;
    private final int gridHeight;
    private final boolean deleteInvalidModules;
    private final TechLevel techLevel;
    private final List<ModuleEntity<?>> moduleEntities = new ArrayList<>();
    private final Set<ModuleType<?>> additionalTypeList = new HashSet<>();
    private final Set<ModuleType<?>> typeBlackList = new HashSet<>();
    private final Set<ModuleCategory> categories = new HashSet<>();
    private final Map<ModuleType<?>, ModuleData<?>> moduleDataCache = new HashMap<>();
    private BiFunction<ModuleEntity<?>, List<Component>, Boolean> removeCheck = null;

    public SimpleModuleHost(TechLevel techLevel, int gridWidth, int gridHeight, boolean deleteInvalidModules, ModuleCategory... categories) {
        this.techLevel = techLevel;
        this.gridWidth = gridWidth;
        this.gridHeight = gridHeight;
        this.deleteInvalidModules = deleteInvalidModules;
        this.categories.addAll(Arrays.asList(categories));
    }

    /**
     * Allows you to add a listener that will be fired whenever a module is about to be removed.
     * The listener is a BiFunction that provides the entity being removed and a list of chat components.
     * The return is a boolean. A return value of true will allow the module to be removed.
     * False will prevent removal.
     * In the event removal is blocked you can add chat components to the supplied list.
     * These will be displayed to the user the same way module installation errors are shown.
     *
     * @param removeListener listener callback.
     */
    public void setEntityRemoveListener(BiFunction<ModuleEntity<?>, List<Component>, Boolean> removeListener) {
        this.removeCheck = removeListener;
    }

    @Override
    public Stream<com.brandon3055.draconicevolution.api.modules.Module<?>> getModules() {
        return getModuleEntities().stream().map(ModuleEntity::getModule);
    }

    @Override
    public List<ModuleEntity<?>> getModuleEntities() {
        return Collections.unmodifiableList(moduleEntities);
    }

    @Override
    public void addModule(ModuleEntity<?> entity, ModuleContext context) {
        moduleEntities.add(entity);
        entity.setHost(this);
        clearCaches();
        entity.onInstalled(context);
    }

    @Override
    public void removeModule(ModuleEntity<?> entity, ModuleContext context) {
        moduleEntities.remove(entity);
        clearCaches();
        entity.onRemoved(context);
    }

    @Override
    public Collection<ModuleCategory> getModuleCategories() {
        return categories;
    }

    public SimpleModuleHost addCategories(ModuleCategory... categories) {
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
    public SimpleModuleHost addAdditionalType(ModuleType<?> type) {
        additionalTypeList.add(type);
        return this;
    }

    /**
     * Allows you to specifically deny certain module types bypassing the category system.
     */
    public SimpleModuleHost blackListType(ModuleType<?> type) {
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

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag nbt = new CompoundTag();
        ListTag modules = new ListTag();
        for (ModuleEntity<?> entity : moduleEntities) {
            CompoundTag entityNBT = new CompoundTag();
            entityNBT.putString("id", entity.module.getRegistryName().toString());
            entity.writeToNBT(entityNBT);
            modules.add(entityNBT);
        }
        nbt.put("modules", modules);
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        clearCaches();
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
    }
}
