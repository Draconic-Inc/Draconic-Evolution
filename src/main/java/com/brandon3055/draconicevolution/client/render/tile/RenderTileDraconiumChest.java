package com.brandon3055.draconicevolution.client.render.tile;

import com.brandon3055.draconicevolution.DraconicEvolution;
import com.brandon3055.draconicevolution.blocks.DraconiumChest;
import com.brandon3055.draconicevolution.blocks.tileentity.TileDraconiumChest;
import com.brandon3055.draconicevolution.client.DETextures;
import com.brandon3055.draconicevolution.client.model.ModelDraconiumChest;
import com.brandon3055.draconicevolution.init.DEContent;
import com.brandon3055.draconicevolution.utils.LogHelper;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;

import java.util.Objects;

public class RenderTileDraconiumChest extends TileEntityRenderer<TileDraconiumChest> {

    public static final ResourceLocation DRACONIUM_CHEST = new ResourceLocation(DraconicEvolution.MODID, DETextures.DRACONIUM_CHEST);

    private final ModelDraconiumChest chestModel = new ModelDraconiumChest(RenderType::entitySolid);
    private float r, g, b;
    private float dr, dg, db = 0.005f;

    public RenderTileDraconiumChest(TileEntityRendererDispatcher rendererDispatcherIn) {
        super(rendererDispatcherIn);
    }

    @Override
    public void render(TileDraconiumChest tile, float partialTicks, MatrixStack matrixStack, IRenderTypeBuffer getter, int packedLight, int packedOverlay) {
        boolean rgbGamingMode = false;
        if (rgbGamingMode) {
            r = 1;
            g = 0;
            b = 0.5f;
            db = 0.001f;
        } else {
            r = (float) (50 + ((tile.colour.get() >> 16) & 0xFF)) / 255f;
            g = (float) (50 + ((tile.colour.get() >> 8) & 0xFF)) / 255f;
            b = (float) (50 + (tile.colour.get() & 0xFF)) / 255f;
        }

        BlockState state = Objects.requireNonNull(tile.getLevel()).getBlockState(tile.getBlockPos());
        if (state.getBlock() != DEContent.draconium_chest) return;

        Direction facing = state.getValue(DraconiumChest.FACING);
        int facingRotationAngle = 0;
        switch (facing.ordinal()) {
            case 2 : facingRotationAngle = 0;
                break;
            case 3 : facingRotationAngle = 180;
                break;
            case 4 : facingRotationAngle = 90;
                break;
            case 5 : facingRotationAngle = -90;
                break;
        }

        float lidAngle = tile.prevLidAngle + (tile.lidAngle - tile.prevLidAngle) * partialTicks;
        lidAngle = 1.0F - lidAngle;
        lidAngle = 1.0F - lidAngle * lidAngle * lidAngle;
        lidAngle = lidAngle * ((float) Math.PI /2f);

        chestModel.setLidAngle(lidAngle);
        chestModel.setFacingDirection(facingRotationAngle);
        chestModel.renderToBuffer(matrixStack, getter.getBuffer(chestModel.renderType(DRACONIUM_CHEST)), packedLight, packedOverlay, r, g, b, 1F);
        if (rgbGamingMode) {
            if (r >= 0.992) dr = -0.005f; else if (r <= 0.002) dr = 0.005f;
            if (g >= 0.992) dg = -0.003f; else if (g <= 0.002) dg = 0.003f;
            if (b >= 0.992) db = -0.001f; else if (b <= 0.002) db = 0.001f;

            r += dr;
            g += dg;
            b += db;
        }
    }
}
