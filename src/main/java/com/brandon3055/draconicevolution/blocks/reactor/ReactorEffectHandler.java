package com.brandon3055.draconicevolution.blocks.reactor;

import com.brandon3055.brandonscore.client.particle.BCEffectHandler;
import com.brandon3055.brandonscore.lib.Vec3D;
import com.brandon3055.draconicevolution.blocks.reactor.tileentity.TileReactorComponent;
import com.brandon3055.draconicevolution.blocks.reactor.tileentity.TileReactorCore;
import com.brandon3055.draconicevolution.blocks.reactor.tileentity.TileReactorEnergyInjector;
import com.brandon3055.draconicevolution.client.render.effect.ReactorBeamFX;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Created by brandon3055 on 11/02/2017.
 */
public class ReactorEffectHandler {

    private TileReactorCore reactor;
    @SideOnly(Side.CLIENT)
    private ReactorBeamFX[] effects = new ReactorBeamFX[6];

    public ReactorEffectHandler(TileReactorCore reactor) {
        this.reactor = reactor;
    }

    @SideOnly(Side.CLIENT)
    public void updateEffects() {
        if (false && !reactor.reactorState.value.isReactorActive()) {
            return;
        }

        for (EnumFacing facing : EnumFacing.values()) {
            int index = facing.getIndex();
            TileReactorComponent component = reactor.getComponent(facing);

            if (component == null) {
                if (effects[index] != null) {
                    effects[index].setExpired();
                    effects[index] = null;
                }
                continue;
            }

            if (effects[index] != null && effects[index].isAlive()) {
                effects[index].updateFX((float) reactor.animationState.value, 1);//todo Power Stat
                continue;
            }

            ReactorBeamFX beamFX = new ReactorBeamFX(reactor.getWorld(), Vec3D.getCenter(component.getPos()), component.facing.value, reactor, component instanceof TileReactorEnergyInjector);
            effects[index] = beamFX;
            BCEffectHandler.spawnGLParticle(ReactorBeamFX.FX_HANDLER, beamFX);
        }
    }

}
