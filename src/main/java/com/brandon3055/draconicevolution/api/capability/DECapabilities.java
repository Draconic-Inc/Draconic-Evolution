package com.brandon3055.draconicevolution.api.capability;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;

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

}
