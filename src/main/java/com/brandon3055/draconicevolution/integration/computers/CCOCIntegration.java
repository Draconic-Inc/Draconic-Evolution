package com.brandon3055.draconicevolution.integration.computers;

import com.brandon3055.draconicevolution.integration.computers.cc.DEPeripheralProvider;
import com.brandon3055.draconicevolution.integration.computers.oc2.OC2DeviceProvider;
import dan200.computercraft.api.ForgeComputerCraftAPI;
import net.minecraftforge.fml.ModList;

/**
 * Created by brandon3055 on 21/9/2015.
 */
public class CCOCIntegration {

    public static String ENERGY_PYLON = "draconic_rf_storage";
    public static String FLUX_GATE = "flux_gate";
    public static String FLUID_GATE = "fluid_gate";
    public static String FLOW_GATE = "flow_gate";
    public static String DRACONIC_REACTOR = "draconic_reactor";

    public static void init() {
        if (ModList.get().isLoaded("computercraft")) {
            ForgeComputerCraftAPI.registerPeripheralProvider(new DEPeripheralProvider());
        }
        if (ModList.get().isLoaded("oc2r")) {
            OC2DeviceProvider.init();
        }
    }
}
