package com.brandon3055.draconicevolution.client.sound;

import com.brandon3055.brandonscore.lib.Vec3D;
import net.minecraft.client.audio.ITickableSound;
import net.minecraft.client.audio.SimpleSound;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;

/**
 * Created by brandon3055 on 24/06/2016.
 */
public class CelestialModifierSound extends SimpleSound implements ITickableSound {

    private int timer = 0;
    private boolean done = false;

    public CelestialModifierSound(SoundEvent soundEvent, BlockPos pos) {
        super(soundEvent, SoundCategory.BLOCKS, 1, 1, pos);
        looping = true;
    }

    public void updateSound(Vec3D pos, float volume, float pitch) {
        timer = 0;
        x = (float) pos.x;
        y = (float) pos.y;
        z = (float) pos.z;
        this.volume = volume;
        this.pitch = pitch;
    }

    public void kill() {
        done = true;
    }

    @Override
    public boolean isStopped() {
        return done || timer > 20;
    }

    @Override
    public void tick() {
        timer++;
    }
}
