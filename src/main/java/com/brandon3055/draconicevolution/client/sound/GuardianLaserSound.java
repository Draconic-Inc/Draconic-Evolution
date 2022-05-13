package com.brandon3055.draconicevolution.client.sound;

import com.brandon3055.draconicevolution.entity.guardian.control.LaserBeamPhase;
import com.brandon3055.draconicevolution.handlers.DESounds;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.resources.sounds.TickableSoundInstance;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundSource;

/**
 * Created by brandon3055 on 4/10/2015.
 */
public class GuardianLaserSound extends SimpleSoundInstance implements TickableSoundInstance {

    private final LaserBeamPhase phase;

    public GuardianLaserSound(BlockPos pos, LaserBeamPhase phase) {
        super(DESounds.beam, SoundSource.HOSTILE, 100F, 0.5F, pos);
        this.phase = phase;
        this.looping = true;
    }

    @Override
    public boolean isStopped() {
        return phase.isEnded();
    }

    @Override
    public void tick() {
        pitch = phase.getSoundPitch();
    }
}
