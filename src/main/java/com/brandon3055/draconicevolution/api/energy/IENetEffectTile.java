package com.brandon3055.draconicevolution.api.energy;

import com.brandon3055.draconicevolution.blocks.energynet.rendering.ENetFXHandler;
import com.brandon3055.draconicevolution.client.render.effect.CrystalFXBase;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import java.util.LinkedList;

/**
 * Created by brandon3055 on 3/18/2018.
 */
public interface IENetEffectTile extends ICrystalLink {

    ENetFXHandler createServerFXHandler();

    @OnlyIn (Dist.CLIENT)
    ENetFXHandler createClientFXHandler();

    @OnlyIn(Dist.CLIENT)
    CrystalFXBase createStaticFX();

    LinkedList<Byte> getFlowRates();

    int getTier();

    int getIDHash();

    default boolean hasStaticFX() { return true; }
}
