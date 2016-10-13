package com.brandon3055.draconicevolution.client.sound;

import com.brandon3055.draconicevolution.blocks.tileentity.TileFusionCraftingCore;
import com.brandon3055.draconicevolution.lib.DESoundHandler;
import net.minecraft.client.audio.ITickableSound;
import net.minecraft.client.audio.PositionedSound;
import net.minecraft.util.SoundCategory;

/**
 * Created by brandon3055 on 24/06/2016.
 */
public class FusionRotationSound extends PositionedSound implements ITickableSound {
    private TileFusionCraftingCore tile;

    public FusionRotationSound(TileFusionCraftingCore tile) {
        super(DESoundHandler.fusionRotation, SoundCategory.BLOCKS);
        this.tile = tile;
        xPosF = tile.getPos().getX() + 0.5F;
        yPosF = tile.getPos().getY() + 0.5F;
        zPosF = tile.getPos().getZ() + 0.5F;
        repeat = true;
        volume = 1.5F;
    }

    @Override
    public boolean isDonePlaying() {
        return tile.isInvalid() || !tile.craftingInProgress();
    }

    @Override
    public void update() {
        pitch = 0.1F + (((tile.getCraftingStage() - 1000) / 1000F) * 1.9F);
    }
}
