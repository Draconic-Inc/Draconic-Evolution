package com.brandon3055.draconicevolution;

import com.brandon3055.draconicevolution.api.energy.IENetEffectTile;
import com.brandon3055.draconicevolution.blocks.energynet.rendering.ENetFXHandler;
import com.brandon3055.draconicevolution.blocks.reactor.ReactorEffectHandler;
import com.brandon3055.draconicevolution.blocks.reactor.tileentity.TileReactorCore;
import com.brandon3055.draconicevolution.blocks.tileentity.TileFusionCraftingCore;
import com.brandon3055.draconicevolution.blocks.tileentity.TileGenerator;
import com.brandon3055.draconicevolution.client.render.tile.fxhandlers.ITileFXHandler;
import com.brandon3055.draconicevolution.lib.ISidedTileHandler;

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
