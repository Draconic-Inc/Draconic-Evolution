package com.brandon3055.draconicevolution.api.capability;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.util.LazyOptional;

import java.util.Objects;

/**
 * Created by brandon3055 on 2/5/20.
 */
public class DECapabilities {

    @CapabilityInject(ModuleProvider.class)
    public static Capability<ModuleProvider<?>> MODULE_CAPABILITY = null;

    @CapabilityInject(ModuleHost.class)
    public static Capability<ModuleHost> MODULE_HOST_CAPABILITY = null;

    @CapabilityInject(PropertyProvider.class)
    public static Capability<PropertyProvider> PROPERTY_PROVIDER_CAPABILITY = null;

    /**
     * This and {@link #readFromShareTag(ItemStack, CompoundNBT)} must be implemented if your modular / configurable item's {@link net.minecraft.item.Item#getShareTag(ItemStack)} method.<br>
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
    public static CompoundNBT writeToShareTag(ItemStack stack, CompoundNBT nbt) {
        CompoundNBT capTags = new CompoundNBT();

        ModuleHost host = null;
        LazyOptional<ModuleHost> optional = stack.getCapability(MODULE_HOST_CAPABILITY);
        if (optional.isPresent()) {
            host = optional.orElseThrow(RuntimeException::new);
            capTags.put("modules", Objects.requireNonNull(MODULE_HOST_CAPABILITY.writeNBT(host, null)));
        }
        if (!(host instanceof PropertyProvider)) {
            stack.getCapability(PROPERTY_PROVIDER_CAPABILITY).ifPresent(provider -> capTags.put("properties", Objects.requireNonNull(PROPERTY_PROVIDER_CAPABILITY.writeNBT(provider, null))));
        }

        if (capTags.isEmpty()) {
            return nbt;
        }

        if (nbt == null) {
            nbt = new CompoundNBT();
        } else {
            nbt = nbt.copy(); //Because we dont actually want to add this data to the server side ItemStack
        }

        nbt.put("share_caps", capTags);
        return nbt;
    }

    /**
     * This and {@link #writeToShareTag(ItemStack, CompoundNBT)} must be implemented if your modular / configurable item's {@link net.minecraft.item.Item#readShareTag(ItemStack, CompoundNBT)} method. <br>
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
    public static void readFromShareTag(ItemStack stack, CompoundNBT nbt) {
        if (nbt != null && nbt.contains("share_caps")) {
            CompoundNBT capTags = nbt.getCompound("share_caps");

            if (capTags.contains("modules")) {
                stack.getCapability(MODULE_HOST_CAPABILITY).ifPresent(host -> MODULE_HOST_CAPABILITY.readNBT(host, null, capTags.get("modules")));
            }
            if (capTags.contains("properties")) {
                stack.getCapability(PROPERTY_PROVIDER_CAPABILITY).ifPresent(provider -> PROPERTY_PROVIDER_CAPABILITY.readNBT(provider, null, capTags.get("properties")));
            }

            nbt.remove("share_caps");
        }
    }
}
