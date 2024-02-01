package com.brandon3055.draconicevolution.client.render.tile;

import codechicken.lib.math.MathHelper;
import com.brandon3055.draconicevolution.DraconicEvolution;
import com.brandon3055.draconicevolution.blocks.DraconiumChest;
import com.brandon3055.draconicevolution.blocks.tileentity.chest.TileDraconiumChest;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Created by brandon3055 on 4/06/2017.
 */
public class DraconiumChestTileRenderer implements BlockEntityRenderer<TileDraconiumChest> {
    private static final RenderType renderType = RenderType.entityCutout(new ResourceLocation(DraconicEvolution.MODID, "textures/block/draconium_chest.png"));
    private final ModelPart lid;
    private final ModelPart bottom;
    private final ModelPart lock;

    public DraconiumChestTileRenderer(BlockEntityRendererProvider.Context context) {
        ModelPart chestRoot = createChestLayer().bakeRoot();
        bottom = chestRoot.getChild("bottom");
        lid = chestRoot.getChild("lid");
        lock = chestRoot.getChild("lock");

//        this.bottom = new ModelPart(64, 64, 0, 19);
//        this.bottom.addBox(1.0F, 0.0F, 1.0F, 14.0F, 10.0F, 14.0F, 0.0F);
//        this.lid = new ModelPart(64, 64, 0, 0);
//        this.lid.addBox(1.0F, 0.0F, 0.0F, 14.0F, 5.0F, 14.0F, 0.0F);
//        this.lid.y = 9.0F;
//        this.lid.z = 1.0F;
//        this.lock = new ModelPart(64, 64, 0, 0);
//        this.lock.addBox(7.0F, -1.0F, 15.0F, 2.0F, 4.0F, 1.0F, 0.0F);
//        this.lock.y = 8.0F;
    }

    public static LayerDefinition createChestLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();
        root.addOrReplaceChild("bottom", CubeListBuilder.create().texOffs(0, 19).addBox(1.0F, 0.0F, 1.0F, 14.0F, 10.0F, 14.0F), PartPose.ZERO);
        root.addOrReplaceChild("lid", CubeListBuilder.create().texOffs(0, 0).addBox(1.0F, 0.0F, 0.0F, 14.0F, 5.0F, 14.0F), PartPose.offset(0.0F, 9.0F, 1.0F));
        root.addOrReplaceChild("lock", CubeListBuilder.create().texOffs(0, 0).addBox(7.0F, -1.0F, 15.0F, 2.0F, 4.0F, 1.0F), PartPose.offset(0.0F, 8.0F, 0.0F));
        return LayerDefinition.create(mesh, 64, 64);
    }

    @Override
    public void render(TileDraconiumChest tile, float partialTicks, PoseStack mStack, MultiBufferSource getter, int packedLight, int packedOverlay) {
        BlockState blockstate = tile.getBlockState();
        float rotation = blockstate.getValue(DraconiumChest.FACING).toYRot();
        float lidAngle = MathHelper.interpolate(tile.prevLidAngle, tile.lidAngle, partialTicks);
        lidAngle = 1.0F - lidAngle;
        lidAngle = 1.0F - lidAngle * lidAngle * lidAngle;
        lidAngle *= 3.141593F * -0.5F;
        renderChest(mStack, getter, rotation, lidAngle, packedLight, packedOverlay, tile.colour.get());
    }

    public void renderChest(PoseStack mStack, MultiBufferSource getter, float rotation, float lidAngle, int packedLight, int packedOverlay, int colour) {
        mStack.pushPose();
        mStack.translate(0.5D, 0.5D, 0.5D);
        mStack.mulPose(Axis.YP.rotationDegrees(-rotation));
        mStack.translate(-0.5D, -0.5D, -0.5D);
        VertexConsumer buffer = getter.getBuffer(renderType);
        this.render(mStack, buffer, this.lid, this.lock, this.bottom, lidAngle, packedLight, packedOverlay, colour);
        mStack.popPose();
    }

    private void render(PoseStack mStack, VertexConsumer buffer, ModelPart lidRenderer, ModelPart lockRenderer, ModelPart bottomRenderer, float lidAngle, int packedLight, int packedOverlay, int colour) {
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