package com.brandon3055.draconicevolution.client.sound;

import codechicken.lib.math.MathHelper;
import com.brandon3055.draconicevolution.blocks.reactor.tileentity.TileReactorCore;
import com.brandon3055.draconicevolution.entity.guardian.control.LaserBeamPhase;
import com.brandon3055.draconicevolution.handlers.DESounds;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.ITickableSound;
import net.minecraft.client.audio.SimpleSound;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;

import static com.brandon3055.draconicevolution.blocks.reactor.tileentity.TileReactorCore.ReactorState.BEYOND_HOPE;

/**
 * Created by brandon3055 on 4/10/2015.
 */
public class GuardianLaserSound extends SimpleSound implements ITickableSound {

    private final LaserBeamPhase phase;

    public GuardianLaserSound(BlockPos pos, LaserBeamPhase phase) {
        super(DESounds.beam, SoundCategory.HOSTILE, 100F, 0.5F, pos);
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
