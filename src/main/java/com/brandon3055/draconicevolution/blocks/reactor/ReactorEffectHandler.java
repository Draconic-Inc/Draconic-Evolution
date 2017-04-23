package com.brandon3055.draconicevolution.blocks.reactor;

import com.brandon3055.brandonscore.client.particle.BCEffectHandler;
import com.brandon3055.brandonscore.lib.Vec3D;
import com.brandon3055.draconicevolution.blocks.reactor.tileentity.TileReactorComponent;
import com.brandon3055.draconicevolution.blocks.reactor.tileentity.TileReactorCore;
import com.brandon3055.draconicevolution.blocks.reactor.tileentity.TileReactorEnergyInjector;
import com.brandon3055.draconicevolution.client.render.effect.ReactorBeamFX;
import com.brandon3055.draconicevolution.client.sound.ReactorSound;
import net.minecraft.client.Minecraft;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Created by brandon3055 on 11/02/2017.
 */
public class ReactorEffectHandler {

    private TileReactorCore reactor;
    @SideOnly(Side.CLIENT)
    private ReactorBeamFX[] effects;

    @SideOnly(Side.CLIENT)
    private ReactorSound reactorSound;

    public ReactorEffectHandler(TileReactorCore reactor) {
        this.reactor = reactor;
    }

    @SideOnly(Side.CLIENT)
    public void updateEffects() {
        if (effects == null) {
            effects = new ReactorBeamFX[6];
        }

//        reactorSound.donePlaying = true;
//        reactorSound = null;;
        if ((reactorSound == null || reactorSound.isDonePlaying() || !Minecraft.getMinecraft().getSoundHandler().isSoundPlaying(reactorSound)) && reactor.reactorState.value.isShieldActive() && reactor.shieldCharge.value > 0) {
            reactorSound = new ReactorSound(reactor);
            Minecraft.getMinecraft().getSoundHandler().playSound(reactorSound);
        }
        else if (reactorSound != null && (!reactor.reactorState.value.isShieldActive() || reactor.shieldCharge.value <= 0)) {
            reactorSound.donePlaying = true;
        }

        if (reactor.reactorState.value == TileReactorCore.ReactorState.INVALID || reactor.shieldAnimationState <= 0) {
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
                effects[index].updateFX((float) reactor.shieldAnimationState, 1);//todo Power Stat
                continue;
            }

            ReactorBeamFX beamFX = new ReactorBeamFX(reactor.getWorld(), Vec3D.getCenter(component.getPos()), component.facing.value, reactor, component instanceof TileReactorEnergyInjector);
            effects[index] = beamFX;
            BCEffectHandler.spawnGLParticle(ReactorBeamFX.FX_HANDLER, beamFX);
        }
    }

}
