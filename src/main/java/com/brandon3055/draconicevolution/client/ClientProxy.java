package com.brandon3055.draconicevolution.client;

import com.brandon3055.draconicevolution.CommonProxy;
import com.brandon3055.draconicevolution.api.energy.IENetEffectTile;
import com.brandon3055.draconicevolution.blocks.energynet.rendering.ENetFXHandler;
import com.brandon3055.draconicevolution.blocks.reactor.ReactorEffectHandler;
import com.brandon3055.draconicevolution.blocks.reactor.tileentity.TileReactorCore;
import com.brandon3055.draconicevolution.blocks.tileentity.TileFusionCraftingCore;
import com.brandon3055.draconicevolution.blocks.tileentity.TileGenerator;
import com.brandon3055.draconicevolution.client.render.tile.fxhandlers.FusionTileFXHandler;
import com.brandon3055.draconicevolution.client.render.tile.fxhandlers.ITileFXHandler;
import com.brandon3055.draconicevolution.client.sound.GeneratorSoundHandler;
import com.brandon3055.draconicevolution.lib.ISidedTileHandler;
import net.neoforged.fml.util.thread.EffectiveSide;

@Deprecated
public class ClientProxy extends CommonProxy {

    @Override
    public ENetFXHandler createENetFXHandler(IENetEffectTile tile) {
        if (EffectiveSide.get().isServer()) {
            return super.createENetFXHandler(tile);
        }
        return tile.createClientFXHandler();
    }

    @Override
    public ReactorEffectHandler createReactorFXHandler(TileReactorCore tile) {
        if (EffectiveSide.get().isServer()) {
            return super.createReactorFXHandler(tile);
        }
        return new ReactorEffectHandler(tile);
    }

    @Override
    public ISidedTileHandler createGeneratorSoundHandler(TileGenerator tile) {
        return new GeneratorSoundHandler(tile);
    }

    @Override
    public ITileFXHandler createFusionFXHandler(TileFusionCraftingCore tile) {
        return new FusionTileFXHandler(tile);
    }
}
