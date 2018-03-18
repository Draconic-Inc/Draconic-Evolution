package com.brandon3055.draconicevolution.api;

import com.brandon3055.draconicevolution.blocks.energynet.rendering.ENetFXHandler;
import com.brandon3055.draconicevolution.client.render.effect.CrystalGLFXBase;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.LinkedList;

/**
 * Created by brandon3055 on 3/18/2018.
 */
public interface IENetEffectTile extends ICrystalLink {

    ENetFXHandler createServerFXHandler();

    @SideOnly(Side.CLIENT)
    ENetFXHandler createClientFXHandler();

    @SideOnly(Side.CLIENT)
    CrystalGLFXBase createStaticFX();

    LinkedList<Byte> getFlowRates();

    int getTier();

    int getIDHash();

    default boolean hasStaticFX() { return true; }
}
