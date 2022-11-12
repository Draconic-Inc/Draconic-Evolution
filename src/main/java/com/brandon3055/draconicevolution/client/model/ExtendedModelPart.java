package com.brandon3055.draconicevolution.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by brandon3055 on 13/11/2022
 */
public class ExtendedModelPart extends ModelPart {
    private List<ExtendedModelPart> children = new ArrayList<>();

    public ExtendedModelPart() {
        super(Collections.emptyList(), Collections.emptyMap());
    }

    public ExtendedModelPart addChild(ExtendedModelPart part) {
        children.add(part);
        return this;
    }

    @Override
    public final void render(PoseStack poseStack, VertexConsumer consumer, int packedLight, int packedOverlay, float r, float g, float b, float a) {}

    public void render(PoseStack poseStack, MultiBufferSource getter, int packedLight, int packedOverlay, float r, float g, float b, float a) {
        if (this.visible) {
            if (!this.children.isEmpty()) {
                poseStack.pushPose();
                this.translateAndRotate(poseStack);

                for (ExtendedModelPart child : this.children) {
                    child.render(poseStack, getter, packedLight, packedOverlay, r, g, b, a);
                }

                poseStack.popPose();
            }
        }
    }
}
