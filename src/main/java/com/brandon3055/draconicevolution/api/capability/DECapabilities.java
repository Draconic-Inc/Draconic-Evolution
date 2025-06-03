package com.brandon3055.draconicevolution.api.capability;

import com.brandon3055.draconicevolution.DraconicEvolution;
import net.covers1624.quack.util.SneakyUtils;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.capabilities.BlockCapability;
import net.neoforged.neoforge.capabilities.EntityCapability;
import net.neoforged.neoforge.capabilities.ItemCapability;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Created by brandon3055 on 2/5/20.
 */
public class DECapabilities {

    @Nullable
    public static ModuleHost getHost(ItemStack stack, HolderLookup.Provider provider) {
        RegistryCap<ModuleHost> cap = stack.getCapability(DECapabilities.Host.ITEM);
        return cap == null || provider == null ? null : cap.get(provider);
    }

    @Nullable
    public static PropertyProvider getProps(ItemStack stack, HolderLookup.Provider provider) {
        RegistryCap<PropertyProvider> cap = stack.getCapability(DECapabilities.Properties.ITEM);
        return cap == null || provider == null ? null : cap.get(provider);
    }

    public static class Module {
        public static final BlockCapability<ModuleProvider, @Nullable Direction> BLOCK = BlockCapability.createSided(ResourceLocation.fromNamespaceAndPath(DraconicEvolution.MODID, "module"), ModuleProvider.class);
        public static final EntityCapability<ModuleProvider, @Nullable Direction> ENTITY = EntityCapability.createSided(ResourceLocation.fromNamespaceAndPath(DraconicEvolution.MODID, "module"), ModuleProvider.class);
        public static final ItemCapability<ModuleProvider, Void> ITEM = ItemCapability.createVoid(ResourceLocation.fromNamespaceAndPath(DraconicEvolution.MODID, "module"), ModuleProvider.class);

        public Module() {}

        @Nullable
        public static ModuleProvider<?> fromBlockEntity(BlockEntity blockEntity, @Nullable Direction direction) {
            return BLOCK.getCapability(blockEntity.getLevel(), blockEntity.getBlockPos(), blockEntity.getBlockState(), blockEntity, direction);
        }

        @Nullable
        public static ModuleProvider<?> fromBlockEntity(BlockEntity blockEntity) {
            return fromBlockEntity(blockEntity, null);
        }
    }

    public static class Host {
        public static final BlockCapability<ModuleHost, @Nullable Direction> BLOCK = BlockCapability.createSided(ResourceLocation.fromNamespaceAndPath(DraconicEvolution.MODID, "module_host"), ModuleHost.class);
        public static final EntityCapability<ModuleHost, @Nullable Direction> ENTITY = EntityCapability.createSided(ResourceLocation.fromNamespaceAndPath(DraconicEvolution.MODID, "module_host"), ModuleHost.class);
        @NotNull
        public static final ItemCapability<RegistryCap<ModuleHost>, Void> ITEM = SneakyUtils.unsafeCast(ItemCapability.createVoid(ResourceLocation.fromNamespaceAndPath(DraconicEvolution.MODID, "module_host"), RegistryCap.class));

        public Host() {}

        @Nullable
        public static ModuleHost fromBlockEntity(BlockEntity blockEntity, @Nullable Direction direction) {
            return BLOCK.getCapability(blockEntity.getLevel(), blockEntity.getBlockPos(), blockEntity.getBlockState(), blockEntity, direction);
        }

        @Nullable
        public static ModuleHost fromBlockEntity(BlockEntity blockEntity) {
            return fromBlockEntity(blockEntity, null);
        }
    }

    public static class Properties {
        public static final BlockCapability<PropertyProvider, @Nullable Direction> BLOCK = BlockCapability.createSided(ResourceLocation.fromNamespaceAndPath(DraconicEvolution.MODID, "properties"), PropertyProvider.class);
        public static final EntityCapability<PropertyProvider, @Nullable Direction> ENTITY = EntityCapability.createSided(ResourceLocation.fromNamespaceAndPath(DraconicEvolution.MODID, "properties"), PropertyProvider.class);
        @NotNull
        public static final ItemCapability<RegistryCap<PropertyProvider>, Void> ITEM = SneakyUtils.unsafeCast(ItemCapability.createVoid(ResourceLocation.fromNamespaceAndPath(DraconicEvolution.MODID, "properties"), RegistryCap.class));

        public Properties() {}

        @Nullable
        public static PropertyProvider fromBlockEntity(BlockEntity blockEntity, @Nullable Direction direction) {
            return BLOCK.getCapability(blockEntity.getLevel(), blockEntity.getBlockPos(), blockEntity.getBlockState(), blockEntity, direction);
        }

        @Nullable
        public static PropertyProvider fromBlockEntity(BlockEntity blockEntity) {
            return fromBlockEntity(blockEntity, null);
        }
    }
}
