package com.brandon3055.draconicevolution.client.sound;

import codechicken.lib.math.MathHelper;
import com.brandon3055.draconicevolution.blocks.tileentity.TileGenerator;
import com.brandon3055.draconicevolution.handlers.DESounds;
import com.brandon3055.draconicevolution.lib.ISidedTileHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.resources.sounds.TickableSoundInstance;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;

/**
 * Created by brandon3055 on 4/10/2015.
 */
public class GeneratorSoundHandler implements ISidedTileHandler {
    private TileGenerator tile;
    private GeneratorSound activeSound = null;
    private boolean currentHigh = false;
    private boolean startHigh = false;

    public GeneratorSoundHandler(TileGenerator tile) {
        this.tile = tile;
    }

    @Override
    public void tick() {
        if (!tile.active.get()) {
            if (activeSound != null) {
                activeSound.fadeDown = true;
                if (activeSound.isStopped()) {
                    activeSound = null;
                }
            }
            return;
        }

        TileGenerator.Mode mode = tile.mode.get();
        if (activeSound == null || activeSound.isStopped()) {
            if (mode.index >= 2) {
                activeSound = new GeneratorSound(tile, DESounds.GENERATOR2.get(), startHigh ? 1.5F : 0.5F);
                Minecraft.getInstance().getSoundManager().play(activeSound);
                currentHigh = true;
            }
            else {
                activeSound = new GeneratorSound(tile, DESounds.GENERATOR1.get(), startHigh ? 1F : 0.5F);
                Minecraft.getInstance().getSoundManager().play(activeSound);
                currentHigh = false;
            }
        }

        if (mode.index >= 2) {
            if (!currentHigh) {
                activeSound.fadeUp = true;
                startHigh = false;
            }
            else {
                activeSound.targetPitch = mode == TileGenerator.Mode.NORMAL ? 0.6F : mode == TileGenerator.Mode.PERFORMANCE ? 1.3F : 1.6F;
            }
        }
        else {
            if (currentHigh) {
                activeSound.fadeDown = true;
                startHigh = true;
            }
            else {
                activeSound.targetPitch = mode == TileGenerator.Mode.ECO_PLUS ? 0.5F : 0.7F;
            }
        }
    }

    public class GeneratorSound extends SimpleSoundInstance implements TickableSoundInstance {
        public boolean donePlaying = false;
        private TileGenerator tile;
        private float targetPitch;
        boolean fadeUp = false;
        boolean fadeDown = false;

        public GeneratorSound(TileGenerator tile, SoundEvent sound, float startPitch) {
            super(sound, SoundSource.BLOCKS, 0.3F, 1F, tile.getLevel().random, tile.getBlockPos());
            this.tile = tile;
            this.looping = true;
            this.pitch = startPitch;
            this.targetPitch = startPitch;
        }

        @Override
        public boolean isStopped() {
            return donePlaying;
        }

        @Override
        public void tick() {
            x = (float) tile.getBlockPos().getX() + 0.5F;
            y = (float) tile.getBlockPos().getY() + 0.5F;
            z = (float) tile.getBlockPos().getZ() + 0.5F;

            if (fadeUp) {
                targetPitch = 1.5F;
            }
            else if (fadeDown) {
                targetPitch = 0.5F;
            }

            if (((fadeUp && pitch >= 1) || (fadeDown && pitch <= 0.55)) || tile.isRemoved()) {
                donePlaying = true;
                looping = false;
            }

            pitch = (float) MathHelper.approachExp(pitch, targetPitch, 0.05);
        }
    }
}