package com.brandon3055.draconicevolution.client.sound;

import com.brandon3055.draconicevolution.blocks.tileentity.TileFusionCraftingCore;
import com.brandon3055.draconicevolution.handlers.DESounds;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.resources.sounds.TickableSoundInstance;
import net.minecraft.sounds.SoundSource;

/**
 * Created by brandon3055 on 24/06/2016.
 */
public class FusionRotationSound extends SimpleSoundInstance implements TickableSoundInstance {
    private TileFusionCraftingCore tile;

    public FusionRotationSound(TileFusionCraftingCore tile) {
        super(DESounds.FUSION_ROTATION.get(), SoundSource.BLOCKS, 1.5F, 1, tile.getLevel().random, tile.getBlockPos());
        this.tile = tile;
        looping = true;
    }

    @Override
    public boolean isStopped() {
        return tile.isRemoved() || !tile.isCrafting();
    }

    @Override
    public void tick() {}

    public void setPitch(float pitch) {
        this.pitch = pitch;
    }
}
