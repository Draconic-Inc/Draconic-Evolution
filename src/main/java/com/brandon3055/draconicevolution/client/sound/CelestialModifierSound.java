package com.brandon3055.draconicevolution.client.sound;

import com.brandon3055.brandonscore.lib.Vec3D;
import net.minecraft.client.audio.ITickableSound;
import net.minecraft.client.audio.PositionedSound;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;

/**
 * Created by brandon3055 on 24/06/2016.
 */
public class CelestialModifierSound extends PositionedSound implements ITickableSound {

    private int timer = 0;
    private boolean done = false;

    public CelestialModifierSound(SoundEvent soundEvent) {
        super(soundEvent, SoundCategory.BLOCKS);
        repeat = true;
    }

    public void updateSound(Vec3D pos, float volume, float pitch) {
        timer = 0;
        xPosF = (float) pos.x;
        yPosF = (float) pos.y;
        zPosF = (float) pos.z;
        this.volume = volume;
        this.pitch = pitch;
    }

    public void kill() {
        done = true;
    }

    @Override
    public boolean isDonePlaying() {
        return done || timer > 20;
    }

    @Override
    public void update() {
        timer++;
    }
}
