package com.brandon3055.draconicevolution.api.modules.lib;

import com.brandon3055.draconicevolution.api.TechLevel;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

/**
 * Created by brandon3055 and covers1624 on 4/16/20.
 */
public class ModuleHostImpl implements ModuleHost, INBTSerializable<CompoundNBT> {

    private static Logger logger = LogManager.getLogger("draconic-modules");

    private int gridWidth;
    private int gridHeight;
    private TechLevel techLevel;
    private List<ModuleType<?>> supportedTypes = new ArrayList<>();
    private List<ModuleEntity> moduleEntities = new ArrayList<>();

    public ModuleHostImpl(TechLevel techLevel, int gridWidth, int gridHeight) {
        this.techLevel = techLevel;
        this.gridWidth = gridWidth;
        this.gridHeight = gridHeight;
    }

    public ModuleHostImpl(TechLevel techLevel, int gridWidth, int gridHeight, ModuleType<?>... supportedTypes) {
        this.techLevel = techLevel;
        this.gridWidth = gridWidth;
        this.gridHeight = gridHeight;
        this.supportedTypes.addAll(Arrays.asList(supportedTypes));
    }

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

    @Override
    public CompoundNBT serializeNBT() {
        CompoundNBT nbt = new CompoundNBT();
        ListNBT modules = new ListNBT();
        for (ModuleEntity entity : moduleEntities) {
            CompoundNBT entityNBT = new CompoundNBT();
            entityNBT.putString("id", entity.module.getRegistryName().toString());
            entity.writeToNBT(entityNBT);
            modules.add(entityNBT);
        }
        nbt.put("modules", modules);
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt) {
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
    }
}
