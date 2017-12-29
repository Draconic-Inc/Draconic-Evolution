package com.brandon3055.draconicevolution.client.sound;

import codechicken.lib.math.MathHelper;
import com.brandon3055.draconicevolution.blocks.reactor.tileentity.TileReactorCore;
import com.brandon3055.draconicevolution.lib.DESoundHandler;
import net.minecraft.client.audio.ITickableSound;
import net.minecraft.client.audio.PositionedSound;
import net.minecraft.util.SoundCategory;

/**
 * Created by brandon3055 on 4/10/2015.
 */
public class ReactorSound extends PositionedSound implements ITickableSound {
    public boolean donePlaying = false;
    private TileReactorCore tile;
    private float targetPitch;
    private float targetVolume;

    public ReactorSound(TileReactorCore tile) {
        super(DESoundHandler.coreSound, SoundCategory.BLOCKS);
        this.tile = tile;
        this.xPosF = (float) tile.getPos().getX() + 0.5F;
        this.yPosF = (float) tile.getPos().getY() + 0.5F;
        this.zPosF = (float) tile.getPos().getZ() + 0.5F;
        this.repeat = true;
        if (tile.reactorState.value == TileReactorCore.ReactorState.BEYOND_HOPE) {
            this.volume = 10F;
        }
        else {
            this.volume = 1.5F;
        }
        this.targetPitch = 1F;
    }

    @Override
    public boolean isDonePlaying() {
        return donePlaying;
    }

    @Override
    public void update() {
        if (tile.roller != null) {
            xPosF = (float) tile.roller.pos.x;
            yPosF = (float) tile.roller.pos.y;
            zPosF = (float) tile.roller.pos.z;
        }
        else {
            xPosF = (float) tile.getPos().getX() + 0.5F;
            yPosF = (float) tile.getPos().getY() + 0.5F;
            zPosF = (float) tile.getPos().getZ() + 0.5F;
        }

        pitch = (float) MathHelper.approachExp(pitch, targetPitch, 0.05);
        if (tile.reactorState.value == TileReactorCore.ReactorState.WARMING_UP || tile.reactorState.value == TileReactorCore.ReactorState.STOPPING || tile.reactorState.value == TileReactorCore.ReactorState.COOLING) {
            targetPitch = 0.5F + (tile.shieldAnimationState / 2F);
        }
        else if (tile.reactorState.value == TileReactorCore.ReactorState.RUNNING) {
            targetPitch = 1F + (float) Math.max(0, Math.min(0.5, 1 - ((tile.shieldCharge.value / tile.maxShieldCharge.value) * 10)));
        }
        else if (tile.reactorState.value == TileReactorCore.ReactorState.BEYOND_HOPE) {
            if (volume == 1.5F) {
                donePlaying = true;
            }
            if (tile.getWorld().rand.nextInt(10) == 0) {
                targetPitch = 1F + (tile.getWorld().rand.nextFloat() / 2F);
            }
        }


        if (tile.isInvalid() || !tile.getWorld().getChunkFromBlockCoords(tile.getPos()).isLoaded()) {// || player == null || tile.getDistanceFrom(player.posX, player.posY, player.posZ) > 512){
            donePlaying = true;
            repeat = false;
        }
    }
}
