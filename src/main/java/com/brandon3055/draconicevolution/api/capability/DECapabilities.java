package com.brandon3055.draconicevolution.api.capability;

import com.brandon3055.brandonscore.api.power.IOPStorage;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;

/**
 * Created by brandon3055 on 2/5/20.
 */
public class DECapabilities {

    public static Capability<ModuleProvider<?>> MODULE_CAPABILITY = CapabilityManager.get(new CapabilityToken<>() {});

    public static Capability<ModuleHost> MODULE_HOST_CAPABILITY = CapabilityManager.get(new CapabilityToken<>() {});

    public static Capability<PropertyProvider> PROPERTY_PROVIDER_CAPABILITY = CapabilityManager.get(new CapabilityToken<>() {});

    //This belongs to Brandon's Core but i have it here for convenience as its needed in parts of the DE API.
    //And if you have the DE API then you should also have the BC API.
    public static Capability<IOPStorage> OP_STORAGE = CapabilityManager.get(new CapabilityToken<>() {});

    /**
     * This and {@link #readFromShareTag(ItemStack, CompoundTag)} must be implemented if your modular / configurable item's {@link net.minecraft.world.item.Item#getShareTag(ItemStack)} method.<br>
     * This is required because these capabilities are required by the client side GUI's and by default capabilities are not sent to the client.<br>
     * Example:
     * <pre>
     * Nullable
     * Override
     * public CompoundNBT getShareTag(ItemStack stack) {
     *     return DECapabilities.writeToShareTag(stack, stack.getTag());
     * }
     * <pre/>
     */
    public static CompoundTag writeToShareTag(ItemStack stack, CompoundTag nbt) {
        CompoundTag capTags = new CompoundTag();

        ModuleHost host = null;
        LazyOptional<ModuleHost> optional = stack.getCapability(MODULE_HOST_CAPABILITY);
        if (optional.isPresent()) {
            host = optional.orElseThrow(RuntimeException::new);
            capTags.put("modules", host.serializeNBT());
        }
        if (!(host instanceof PropertyProvider)) {
            stack.getCapability(PROPERTY_PROVIDER_CAPABILITY).ifPresent(provider -> capTags.put("properties", provider.serializeNBT()));
        }

        LazyOptional<IOPStorage> energy = stack.getCapability(OP_STORAGE);
        if (energy.isPresent()) {
            IOPStorage storage = energy.orElseThrow(RuntimeException::new);
            if (storage instanceof INBTSerializable<?>) {
                capTags.put("energy", ((INBTSerializable<?>) storage).serializeNBT());
            }
        }

        if (capTags.isEmpty()) {
            return nbt;
        }

        if (nbt == null) {
            nbt = new CompoundTag();
        } else {
            nbt = nbt.copy(); //Because we dont actually want to add this data to the server side ItemStack
        }

        nbt.put("share_caps", capTags);
        return nbt;
    }

    /**
     * This and {@link #writeToShareTag(ItemStack, CompoundTag)} must be implemented if your modular / configurable item's {@link net.minecraft.world.item.Item#readShareTag(ItemStack, CompoundTag)} method. <br>
     * This is required because these capabilities are required by the client side GUI's and by default capabilities are not sent to the client.<br>
     * Example:
     * <pre>
     * Override
     * public void readShareTag(ItemStack stack, @Nullable CompoundNBT nbt) {
     *     stack.setTag(nbt);
     *     DECapabilities.readFromShareTag(stack, nbt);
     * }
     * <pre/>
     */
    public static void readFromShareTag(ItemStack stack, CompoundTag nbt) {
        if (nbt != null && nbt.contains("share_caps")) {
            CompoundTag capTags = nbt.getCompound("share_caps");

            if (capTags.contains("modules")) {
                stack.getCapability(MODULE_HOST_CAPABILITY).ifPresent(host -> host.deserializeNBT(capTags.getCompound("modules")));
            }
            if (capTags.contains("properties")) {
                stack.getCapability(PROPERTY_PROVIDER_CAPABILITY).ifPresent(provider -> provider.deserializeNBT(capTags.getCompound("properties")));
            }
            if (capTags.contains("energy")) {
                stack.getCapability(OP_STORAGE).ifPresent(provider -> {
                    if (provider instanceof INBTSerializable<?>) {
                        ((INBTSerializable<Tag>) provider).deserializeNBT(capTags.getCompound("energy"));
                    }
                });
            }

            nbt.remove("share_caps");
        }
    }
}
