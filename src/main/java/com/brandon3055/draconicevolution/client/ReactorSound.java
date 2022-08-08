package com.brandon3055.draconicevolution.client;

import com.brandon3055.draconicevolution.common.lib.References;
import com.brandon3055.draconicevolution.common.tileentities.multiblocktiles.reactor.TileReactorCore;
import net.minecraft.client.audio.ITickableSound;
import net.minecraft.client.audio.PositionedSound;
import net.minecraft.util.ResourceLocation;

/**
 * Created by brandon3055 on 4/10/2015.
 */
public class ReactorSound extends PositionedSound implements ITickableSound {
    private static ResourceLocation sound = new ResourceLocation(References.RESOURCESPREFIX + "coreSound");
    public boolean donePlaying = false;
    private TileReactorCore tile;

    public ReactorSound(TileReactorCore tile) {
        super(sound);
        this.tile = tile;
        this.xPosF = (float) tile.xCoord + 0.5F;
        this.yPosF = (float) tile.yCoord + 0.5F;
        this.zPosF = (float) tile.zCoord + 0.5F;
        this.repeat = true;
        this.volume = 1.5F;
    }

    @Override
    public boolean isDonePlaying() {
        return donePlaying;
    }

    @Override
    public void update() {

        volume = (tile.renderSpeed - 0.5F) * 2F;
        if (tile.reactionTemperature > 8000) {
            volume += (float) ((tile.reactionTemperature - 8000D) / 1000D);
        }
        if (tile.reactionTemperature > 2000
                && tile.maxFieldCharge > 0
                && tile.fieldCharge < (tile.maxFieldCharge * 0.2D)) {
            volume += 2D - ((tile.fieldCharge / tile.maxFieldCharge) * 10D);
        }
        if (tile.reactionTemperature > 2000
                && tile.reactorFuel + tile.convertedFuel > 0
                && tile.reactorFuel < (double) (tile.reactorFuel + tile.convertedFuel) * 0.2D) {
            volume += 2D - ((tile.reactorFuel / (tile.reactorFuel + tile.convertedFuel)) * 10D);
        }

        field_147663_c = 0.5F + volume / 2F;

        if (tile.isInvalid()
                || !tile.getWorldObj()
                        .getChunkFromBlockCoords(tile.xCoord, tile.zCoord)
                        .isChunkLoaded) { // || player == null || tile.getDistanceFrom(player.posX, player.posY,
            // player.posZ) > 512){
            donePlaying = true;
            repeat = false;
        }
    }
}
