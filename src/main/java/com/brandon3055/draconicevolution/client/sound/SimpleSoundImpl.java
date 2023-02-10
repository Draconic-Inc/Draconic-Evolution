package com.brandon3055.draconicevolution.client.sound;

import codechicken.lib.vec.Vector3;
import com.brandon3055.draconicevolution.entity.guardian.control.LaserBeamPhase;
import com.brandon3055.draconicevolution.handlers.DESounds;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.resources.sounds.TickableSoundInstance;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;

import java.util.function.Supplier;

/**
 * Created by brandon3055 on 4/10/2015.
 */
public class SimpleSoundImpl extends SimpleSoundInstance implements TickableSoundInstance {

    private Supplier<Float> pitchSupplier;
    private Supplier<Float> volumeSupplier;
    private Supplier<Boolean> stoppedSupplier;
    private Supplier<Vector3> posSupplier;
    private boolean stop = false;

    public static SimpleSoundImpl create(SoundEvent sound, SoundSource source) {
        return new SimpleSoundImpl(sound, source);
    }

    private SimpleSoundImpl(SoundEvent sound, SoundSource source) {
        super(sound, source, 1F, 1F, 0, 0, 0);
    }

    public SimpleSoundImpl setPitch(float pitch) {
        this.pitch = pitch;
        return this;
    }

    public SimpleSoundImpl setPitchSupplier(Supplier<Float> pitchSupplier) {
        this.pitchSupplier = pitchSupplier;
        return this;
    }

    public SimpleSoundImpl setVolume(float volume) {
        this.volume = volume;
        return this;
    }

    public SimpleSoundImpl setVolumeSupplier(Supplier<Float> volumeSupplier) {
        this.volumeSupplier = volumeSupplier;
        return this;
    }

    public SimpleSoundImpl setPos(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
        return this;
    }

    public SimpleSoundImpl setPos(Vector3 pos) {
        return setPos(pos.x, pos.y, pos.z);
    }

    public SimpleSoundImpl setPos(BlockPos pos) {
        return setPos(Vector3.fromBlockPosCenter(pos));
    }

    public SimpleSoundImpl setPos(Entity pos) {
        return setPos(Vector3.fromEntityCenter(pos));
    }

    public SimpleSoundImpl setPosSupplier(Supplier<Vector3> posSupplier) {
        this.posSupplier = posSupplier;
        return this;
    }

    public SimpleSoundImpl setStoppedSupplier(Supplier<Boolean> stoppedSupplier) {
        this.stoppedSupplier = stoppedSupplier;
        return this;
    }

    public SimpleSoundImpl loop() {
        this.looping = true;
        return this;
    }

    public SimpleSoundImpl play(Minecraft minecraft) {
        minecraft.getSoundManager().play(this);
        return this;
    }

    public void stop() {
        this.stop = true;
    }

    @Override
    public boolean isStopped() {
        return stop || (stoppedSupplier != null && stoppedSupplier.get());
    }

    @Override
    public void tick() {
        if (pitchSupplier != null) setPitch(pitchSupplier.get());
        if (volumeSupplier != null) setVolume(volumeSupplier.get());
        if (posSupplier != null) setPos(posSupplier.get());
    }
}
