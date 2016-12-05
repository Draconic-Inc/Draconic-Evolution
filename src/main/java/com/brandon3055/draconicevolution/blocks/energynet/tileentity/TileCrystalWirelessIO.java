package com.brandon3055.draconicevolution.blocks.energynet.tileentity;

import com.brandon3055.draconicevolution.blocks.energynet.EnergyCrystal;
import com.brandon3055.draconicevolution.client.render.effect.CrystalFXRing;
import com.brandon3055.draconicevolution.client.render.effect.CrystalGLFXBase;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Created by brandon3055 on 19/11/2016.
 */
public class TileCrystalWirelessIO extends TileCrystalBase {

    public TileCrystalWirelessIO() {
        super();
    }

    //region Rendering

    @Override
    public EnergyCrystal.CrystalType getType() {
        return EnergyCrystal.CrystalType.WIRELESS;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public CrystalGLFXBase createStaticFX() {
        return new CrystalFXRing(worldObj, this);
    }

    //endregion
}
