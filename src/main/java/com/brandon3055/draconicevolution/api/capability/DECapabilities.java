package com.brandon3055.draconicevolution.api.capability;

import com.brandon3055.draconicevolution.DraconicEvolution;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.capabilities.BlockCapability;
import net.neoforged.neoforge.capabilities.EntityCapability;
import net.neoforged.neoforge.capabilities.ItemCapability;
import org.jetbrains.annotations.Nullable;

/**
 * Created by brandon3055 on 2/5/20.
 */
public class DECapabilities {

    public static class Module {
        public static final BlockCapability<ModuleProvider, @Nullable Direction> BLOCK = BlockCapability.createSided(new ResourceLocation(DraconicEvolution.MODID, "module"), ModuleProvider.class);
        public static final EntityCapability<ModuleProvider, @Nullable Direction> ENTITY = EntityCapability.createSided(new ResourceLocation(DraconicEvolution.MODID, "module"), ModuleProvider.class);
        public static final ItemCapability<ModuleProvider, Void> ITEM = ItemCapability.createVoid(new ResourceLocation(DraconicEvolution.MODID, "module"), ModuleProvider.class);

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
        public static final BlockCapability<ModuleHost, @Nullable Direction> BLOCK = BlockCapability.createSided(new ResourceLocation(DraconicEvolution.MODID, "module_host"), ModuleHost.class);
        public static final EntityCapability<ModuleHost, @Nullable Direction> ENTITY = EntityCapability.createSided(new ResourceLocation(DraconicEvolution.MODID, "module_host"), ModuleHost.class);
        public static final ItemCapability<ModuleHost, Void> ITEM = ItemCapability.createVoid(new ResourceLocation(DraconicEvolution.MODID, "module_host"), ModuleHost.class);

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
        public static final BlockCapability<PropertyProvider, @Nullable Direction> BLOCK = BlockCapability.createSided(new ResourceLocation(DraconicEvolution.MODID, "properties"), PropertyProvider.class);
        public static final EntityCapability<PropertyProvider, @Nullable Direction> ENTITY = EntityCapability.createSided(new ResourceLocation(DraconicEvolution.MODID, "properties"), PropertyProvider.class);
        public static final ItemCapability<PropertyProvider, Void> ITEM = ItemCapability.createVoid(new ResourceLocation(DraconicEvolution.MODID, "properties"), PropertyProvider.class);

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


//    public static Capability<ModuleProvider<?>> MODULE_CAPABILITY = CapabilityManager.get(new CapabilityToken<>() {});

//    public static Capability<ModuleHost> MODULE_HOST_CAPABILITY = CapabilityManager.get(new CapabilityToken<>() {});

//    public static Capability<PropertyProvider> PROPERTY_PROVIDER_CAPABILITY = CapabilityManager.get(new CapabilityToken<>() {});

    //This belongs to Brandon's Core but i have it here for convenience as its needed in parts of the DE API.
    //And if you have the DE API then you should also have the BC API.
//    public static Capability<IOPStorage> OP_STORAGE = CapabilityManager.get(new CapabilityToken<>() {});
//
//    /**
//     * This and {@link #readFromShareTag(ItemStack, CompoundTag)} must be implemented if your modular / configurable item's {@link net.minecraft.world.item.Item#getShareTag(ItemStack)} method.<br>
//     * This is required because these capabilities are required by the client side GUI's and by default capabilities are not sent to the client.<br>
//     * Example:
//     * <pre>
//     * Nullable
//     * Override
//     * public CompoundNBT getShareTag(ItemStack stack) {
//     *     return DECapabilities.writeToShareTag(stack, stack.getTag());
//     * }
//     * <pre/>
//     */
//    public static CompoundTag writeToShareTag(ItemStack stack, CompoundTag nbt) {
//        CompoundTag capTags = new CompoundTag();
//
//        ModuleHost host = null;
//        LazyOptional<ModuleHost> optional = stack.getCapability(MODULE_HOST_CAPABILITY);
//        if (optional.isPresent()) {
//            host = optional.orElseThrow(RuntimeException::new);
//            capTags.put("modules", host.serializeNBT());
//        }
//        if (!(host instanceof PropertyProvider)) {
//            stack.getCapability(PROPERTY_PROVIDER_CAPABILITY).ifPresent(provider -> capTags.put("properties", provider.serializeNBT()));
//        }
//
//        LazyOptional<IOPStorage> energy = stack.getCapability(OP_STORAGE);
//        if (energy.isPresent()) {
//            IOPStorage storage = energy.orElseThrow(RuntimeException::new);
//            if (storage instanceof INBTSerializable<?>) {
//                capTags.put("energy", ((INBTSerializable<?>) storage).serializeNBT());
//            }
//        }
//
//        if (capTags.isEmpty()) {
//            return nbt;
//        }
//
//        if (nbt == null) {
//            nbt = new CompoundTag();
//        } else {
//            nbt = nbt.copy(); //Because we dont actually want to add this data to the server side ItemStack
//        }
//
//        nbt.put("share_caps", capTags);
//        return nbt;
//    }
//
//    /**
//     * This and {@link #writeToShareTag(ItemStack, CompoundTag)} must be implemented if your modular / configurable item's {@link net.minecraft.world.item.Item#readShareTag(ItemStack, CompoundTag)} method. <br>
//     * This is required because these capabilities are required by the client side GUI's and by default capabilities are not sent to the client.<br>
//     * Example:
//     * <pre>
//     * Override
//     * public void readShareTag(ItemStack stack, @Nullable CompoundNBT nbt) {
//     *     stack.setTag(nbt);
//     *     DECapabilities.readFromShareTag(stack, nbt);
//     * }
//     * <pre/>
//     */
//    public static void readFromShareTag(ItemStack stack, CompoundTag nbt) {
//        if (nbt != null && nbt.contains("share_caps")) {
//            CompoundTag capTags = nbt.getCompound("share_caps");
//
//            if (capTags.contains("modules")) {
//                stack.getCapability(MODULE_HOST_CAPABILITY).ifPresent(host -> host.deserializeNBT(capTags.getCompound("modules")));
//            }
//            if (capTags.contains("properties")) {
//                stack.getCapability(PROPERTY_PROVIDER_CAPABILITY).ifPresent(provider -> provider.deserializeNBT(capTags.getCompound("properties")));
//            }
//            if (capTags.contains("energy")) {
//                stack.getCapability(OP_STORAGE).ifPresent(provider -> {
//                    if (provider instanceof INBTSerializable<?>) {
//                        ((INBTSerializable<Tag>) provider).deserializeNBT(capTags.getCompound("energy"));
//                    }
//                });
//            }
//
//            nbt.remove("share_caps");
//        }
//    }
}
