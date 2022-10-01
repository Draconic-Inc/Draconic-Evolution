package com.brandon3055.draconicevolution.client.model;

import codechicken.lib.vec.Matrix4;
import com.brandon3055.draconicevolution.DEConfig;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;

import java.util.function.Supplier;

/**
 * Created by brandon3055 on 30/6/20
 */
public class VBOModelRender/* extends ModelPart*/ { //May just have to make this its own independent class... If that's even possible???

//    private final VBORenderType vboRenderer;
//    private Supplier<ShaderRenderType> shaderTypeGetter;
//    private Supplier<Boolean> enabledCallback = () -> this.visible;
//
//    public VBOModelRender(Model model, VBORenderType vboRenderer) {
//        super(model);
//        this.vboRenderer = vboRenderer;
//    }
//
//    public VBOModelRender(Model model, VBORenderType vboRenderer, Supplier<Boolean> enabledCallback) {
//        super(model);
//        this.vboRenderer = vboRenderer;
//        this.enabledCallback = enabledCallback;
//    }
//
//    public VBOModelRender setShader(Supplier<ShaderRenderType> shaderTypeGetter) {
//        this.shaderTypeGetter = shaderTypeGetter;
//        return this;
//    }

//    @Override
//    public void render(PoseStack mStack, VertexConsumer buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
//    }

    public void render(PoseStack mStack, MultiBufferSource getter, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
//        if (enabledCallback.get() && vboRenderer != null) {
//            mStack.pushPose();
//            this.translateAndRotate(mStack);
//            if (shaderTypeGetter != null && DEConfig.toolShaders) {
//                getter.getBuffer(vboRenderer.withMatrix(new Matrix4(mStack)).withLightMap(packedLight).withState(shaderTypeGetter.get()));
//            } else {
//                getter.getBuffer(vboRenderer.withMatrix(new Matrix4(mStack)).withLightMap(packedLight));
//            }
//            for (ModelPart child : this.children) {
//                if (child instanceof VBOModelRender) {
//                    ((VBOModelRender) child).render(mStack, getter, packedLight, packedOverlay, red, green, blue, alpha);
//                }
//            }
//            mStack.popPose();
//        }
    }
}
