package com.brandon3055.draconicevolution.client.render.tile;

import codechicken.lib.math.MathHelper;
import com.brandon3055.draconicevolution.DraconicEvolution;
import com.brandon3055.draconicevolution.blocks.DraconiumChest;
import com.brandon3055.draconicevolution.blocks.tileentity.chest.TileDraconiumChest;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3f;

/**
 * Created by brandon3055 on 4/06/2017.
 */
public class DraconiumChestTileRenderer extends TileEntityRenderer<TileDraconiumChest> {
    private static final RenderType renderType = RenderType.entityCutout(new ResourceLocation(DraconicEvolution.MODID, "textures/block/draconium_chest.png"));
    private final ModelRenderer lid;
    private final ModelRenderer bottom;
    private final ModelRenderer lock;

    public DraconiumChestTileRenderer(TileEntityRendererDispatcher rendererDispatcherIn) {
        super(rendererDispatcherIn);
        this.bottom = new ModelRenderer(64, 64, 0, 19);
        this.bottom.addBox(1.0F, 0.0F, 1.0F, 14.0F, 10.0F, 14.0F, 0.0F);
        this.lid = new ModelRenderer(64, 64, 0, 0);
        this.lid.addBox(1.0F, 0.0F, 0.0F, 14.0F, 5.0F, 14.0F, 0.0F);
        this.lid.y = 9.0F;
        this.lid.z = 1.0F;
        this.lock = new ModelRenderer(64, 64, 0, 0);
        this.lock.addBox(7.0F, -1.0F, 15.0F, 2.0F, 4.0F, 1.0F, 0.0F);
        this.lock.y = 8.0F;
    }

    @Override
    public void render(TileDraconiumChest tile, float partialTicks, MatrixStack mStack, IRenderTypeBuffer getter, int packedLight, int packedOverlay) {
        BlockState blockstate = tile.getBlockState();
        float rotation = blockstate.getValue(DraconiumChest.FACING).toYRot();
        float lidAngle = MathHelper.interpolate(tile.prevLidAngle, tile.lidAngle, partialTicks);
        lidAngle = 1.0F - lidAngle;
        lidAngle = 1.0F - lidAngle * lidAngle * lidAngle;
        lidAngle *= 3.141593F * -0.5F;
        renderChest(mStack, getter, rotation, lidAngle, packedLight, packedOverlay, tile.colour.get());
    }

    public void renderChest(MatrixStack mStack, IRenderTypeBuffer getter, float rotation, float lidAngle, int packedLight, int packedOverlay, int colour) {
        mStack.pushPose();
        mStack.translate(0.5D, 0.5D, 0.5D);
        mStack.mulPose(Vector3f.YP.rotationDegrees(-rotation));
        mStack.translate(-0.5D, -0.5D, -0.5D);
        IVertexBuilder buffer = getter.getBuffer(renderType);
        this.render(mStack, buffer, this.lid, this.lock, this.bottom, lidAngle, packedLight, packedOverlay, colour);
        mStack.popPose();
    }

    private void render(MatrixStack mStack, IVertexBuilder buffer, ModelRenderer lidRenderer, ModelRenderer lockRenderer, ModelRenderer bottomRenderer, float lidAngle, int packedLight, int packedOverlay, int colour) {
        float red = (float) ((colour >> 16) & 0xFF) / 255f;
        float green = (float) ((colour >> 8) & 0xFF) / 255f;
        float blue = (float) (colour & 0xFF) / 255f;
        lidRenderer.xRot = lidAngle;
        lockRenderer.xRot = lidRenderer.xRot;
        lidRenderer.render(mStack, buffer, packedLight, packedOverlay, red, green, blue, 1.0F);
        lockRenderer.render(mStack, buffer, packedLight, packedOverlay, 1.0F, 1.0F, 1.0F, 1.0F);
        bottomRenderer.render(mStack, buffer, packedLight, packedOverlay, red, green, blue, 1.0F);
    }
}