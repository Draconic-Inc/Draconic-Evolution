package com.brandon3055.draconicevolution.client.sound;

import com.brandon3055.draconicevolution.blocks.tileentity.TileCraftingCore;
import com.brandon3055.draconicevolution.handlers.DESounds;
import net.minecraft.client.audio.ITickableSound;
import net.minecraft.client.audio.SimpleSound;
import net.minecraft.util.SoundCategory;

/**
 * Created by brandon3055 on 24/06/2016.
 */
public class FusionRotationSound extends SimpleSound implements ITickableSound {
    private TileCraftingCore tile;

    public FusionRotationSound(TileCraftingCore tile) {
        super(DESounds.fusionRotation, SoundCategory.BLOCKS, 1.5F, 1, tile.getBlockPos());
        this.tile = tile;
        looping = true;
    }

    @Override
    public boolean isStopped() {
        return tile.isRemoved() || !tile.craftingInProgress();
    }

    @Override
    public void tick() {
        pitch = 0.1F + (((tile.getCraftingStage() - 1000) / 1000F) * 1.9F);
    }
}
