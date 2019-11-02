package com.brandon3055.draconicevolution.client.sound;

import codechicken.lib.math.MathHelper;
import com.brandon3055.draconicevolution.blocks.tileentity.TileGenerator;
import com.brandon3055.draconicevolution.lib.DESoundHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.ITickableSound;
import net.minecraft.client.audio.PositionedSound;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;

import static com.brandon3055.draconicevolution.blocks.tileentity.TileGenerator.Mode.*;

/**
 * Created by brandon3055 on 4/10/2015.
 */
public class GeneratorSoundHandler {
    private TileGenerator tile;
    private GeneratorSound activeSound = null;
    private boolean currentHigh = false;
    private boolean startHigh = false;

    public GeneratorSoundHandler(TileGenerator tile) {
        this.tile = tile;
    }

    public void update() {
        if (!tile.active.get()) {
            if (activeSound != null) {
                activeSound.fadeDown = true;
                if (activeSound.isDonePlaying()) {
                    activeSound = null;
                }
            }
            return;
        }

        TileGenerator.Mode mode = tile.mode.get();
        if (activeSound == null || activeSound.isDonePlaying()) {
            if (mode.index >= 2) {
                activeSound = new GeneratorSound(tile, DESoundHandler.generator2, startHigh ? 1.5F : 0.5F);
                Minecraft.getMinecraft().getSoundHandler().playSound(activeSound);
                currentHigh = true;
            }
            else {
                activeSound = new GeneratorSound(tile, DESoundHandler.generator1, startHigh ? 1F : 0.5F);
                Minecraft.getMinecraft().getSoundHandler().playSound(activeSound);
                currentHigh = false;
            }
        }

        if (mode.index >= 2) {
            if (!currentHigh) {
                activeSound.fadeUp = true;
                startHigh = false;
            }
            else {
                activeSound.targetPitch = mode == NORMAL ? 0.6F : mode == PERFORMANCE ? 1.3F : 1.6F;
            }
        }
        else {
            if (currentHigh) {
                activeSound.fadeDown = true;
                startHigh = true;
            }
            else {
                activeSound.targetPitch = mode == ECO_PLUS ? 0.5F : 0.7F;
            }
        }
    }

    public class GeneratorSound extends PositionedSound implements ITickableSound {
        public boolean donePlaying = false;
        private TileGenerator tile;
        private float targetPitch;
        boolean fadeUp = false;
        boolean fadeDown = false;

        public GeneratorSound(TileGenerator tile, SoundEvent sound, float startPitch) {
            super(sound, SoundCategory.BLOCKS);
            this.tile = tile;
            this.xPosF = (float) tile.getPos().getX() + 0.5F;
            this.yPosF = (float) tile.getPos().getY() + 0.5F;
            this.zPosF = (float) tile.getPos().getZ() + 0.5F;
            this.repeat = true;
            this.volume = 0.3F;
            this.pitch = startPitch;
            this.targetPitch = startPitch;
        }

        @Override
        public boolean isDonePlaying() {
            return donePlaying;
        }

        @Override
        public void update() {
            xPosF = (float) tile.getPos().getX() + 0.5F;
            yPosF = (float) tile.getPos().getY() + 0.5F;
            zPosF = (float) tile.getPos().getZ() + 0.5F;

            if (fadeUp) {
                targetPitch = 1.5F;
            }
            else if (fadeDown) {
                targetPitch = 0.5F;
            }

            if (((fadeUp && pitch >= 1) || (fadeDown && pitch <= 0.55)) || tile.isInvalid() || !tile.getWorld().getChunkFromBlockCoords(tile.getPos()).isLoaded()) {
                donePlaying = true;
                repeat = false;
            }

            pitch = (float) MathHelper.approachExp(pitch, targetPitch, 0.05);
        }
    }
}
