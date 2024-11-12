package com.brandon3055.draconicevolution;

import com.brandon3055.draconicevolution.api.DraconicAPI;

import com.brandon3055.draconicevolution.client.ClientProxy;
import com.brandon3055.draconicevolution.client.DEParticles;
import com.brandon3055.draconicevolution.command.DECommands;
import com.brandon3055.draconicevolution.handlers.*;
import com.brandon3055.draconicevolution.init.*;
import com.brandon3055.draconicevolution.integration.computers.ComputerCraftCompatEventHandler;
import com.brandon3055.draconicevolution.integration.equipment.EquipmentManager;
import com.brandon3055.draconicevolution.items.tools.Dislocator;
import com.brandon3055.draconicevolution.network.DraconicNetwork;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.DistExecutor;
import net.neoforged.fml.OptionalMod;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


@Mod (DraconicEvolution.MODID)
public class DraconicEvolution {
    public static final Logger LOGGER = LogManager.getLogger();
    public static final String MODID = "draconicevolution";
    public static final String MODNAME = "Draconic Evolution";

    public static CommonProxy proxy;

    public DraconicEvolution(IEventBus modBus) {
        proxy = DistExecutor.unsafeRunForDist(() -> ClientProxy::new, () -> CommonProxy::new);

        DEConfig.load();
        DETags.init();
        DEContent.init(modBus);
        DEModules.init(modBus);
        DESounds.init(modBus);
        DEParticles.init(modBus);
        DECreativeTabs.init(modBus);
        EquipmentManager.initialize(modBus);
        DECommands.init();
        CapabilityData.init(modBus);
        LootEventHandler.init();
        ModuleEventHandler.init();
        ModularArmorEventHandler.init();
        DraconicNetwork.init(modBus);
        DEEventHandler.init(modBus);

        OptionalMod.of("computercraft").ifPresent(e -> modBus.register(new ComputerCraftCompatEventHandler()));

        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> DEClient.init(modBus));
        DraconicAPI.addModuleProvider(MODID);
    }
}