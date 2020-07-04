package com.brandon3055.draconicevolution.client.model.tool;

import codechicken.lib.math.MathHelper;
import codechicken.lib.render.buffer.VBORenderType;
import codechicken.lib.render.shader.ShaderProgram;
import codechicken.lib.render.shader.ShaderRenderType;
import codechicken.lib.vec.Matrix4;
import codechicken.lib.vec.Rotation;
import codechicken.lib.vec.Vector3;
import com.brandon3055.draconicevolution.DEConfig;
import com.brandon3055.draconicevolution.utils.LogHelper;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.client.renderer.model.ModelRenderer;

import java.util.function.Supplier;

/**
 * Created by brandon3055 on 30/6/20
 */
public class VBOModelRender extends ModelRenderer {

    private final VBORenderType vboRenderer;
    private Supplier<ShaderRenderType> shaderTypeGetter;

    public VBOModelRender(Model model, VBORenderType vboRenderer) {
        super(model);
        this.vboRenderer = vboRenderer;
    }

    public VBOModelRender setShader(Supplier<ShaderRenderType> shaderTypeGetter) {
        this.shaderTypeGetter = shaderTypeGetter;
        return this;
    }

    @Override
    public void render(MatrixStack mStack, IVertexBuilder buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
    }

    public void render(MatrixStack mStack, IRenderTypeBuffer getter, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        if (this.showModel && vboRenderer != null) {
            mStack.push();
            this.translateRotate(mStack);
            if (shaderTypeGetter != null && DEConfig.toolShaders) {
                getter.getBuffer(vboRenderer.withMatrix(new Matrix4(mStack)).withLightMap(packedLight).withState(shaderTypeGetter.get()));
            } else {
                getter.getBuffer(vboRenderer.withMatrix(new Matrix4(mStack)).withLightMap(packedLight));
            }
            for (ModelRenderer child : this.childModels) {
                if (child instanceof VBOModelRender) {
                    ((VBOModelRender) child).render(mStack, getter, packedLight, packedOverlay, red, green, blue, alpha);
                }
            }
            mStack.pop();
        }
    }
}
