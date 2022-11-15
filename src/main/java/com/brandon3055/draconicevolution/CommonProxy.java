package com.brandon3055.draconicevolution;

import com.brandon3055.draconicevolution.api.energy.IENetEffectTile;
import com.brandon3055.draconicevolution.blocks.energynet.rendering.ENetFXHandler;
import com.brandon3055.draconicevolution.blocks.reactor.ReactorEffectHandler;
import com.brandon3055.draconicevolution.blocks.reactor.tileentity.TileReactorCore;
import com.brandon3055.draconicevolution.blocks.tileentity.TileFusionCraftingCore;
import com.brandon3055.draconicevolution.blocks.tileentity.TileGenerator;
import com.brandon3055.draconicevolution.client.render.tile.fxhandlers.ITileFXHandler;
import com.brandon3055.draconicevolution.handlers.DEEventHandler;
import com.brandon3055.draconicevolution.init.DETags;
import com.brandon3055.draconicevolution.init.ModCapabilities;
import com.brandon3055.draconicevolution.integration.computers.ComputerCraftCompatEventHandler;
import com.brandon3055.draconicevolution.integration.equipment.EquipmentManager;
import com.brandon3055.draconicevolution.items.tools.Dislocator;
import com.brandon3055.draconicevolution.lib.ISidedTileHandler;
import com.brandon3055.draconicevolution.network.DraconicNetwork;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.OptionalMod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLDedicatedServerSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Deprecated
public class CommonProxy {

    public ENetFXHandler createENetFXHandler(IENetEffectTile tile) {
        return tile.createServerFXHandler();
    }

    public ReactorEffectHandler createReactorFXHandler(TileReactorCore tile) {
        return null;
    }

    public ISidedTileHandler createGeneratorSoundHandler(TileGenerator tile) {
        return null;
    }

    public ITileFXHandler createFusionFXHandler(TileFusionCraftingCore tile) {
        return null;
    }
}
