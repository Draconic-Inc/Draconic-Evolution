package com.brandon3055.draconicevolution.client.shader;

import codechicken.lib.render.shader.CCUniform;
import com.brandon3055.brandonscore.client.shader.BCShader;
import com.brandon3055.draconicevolution.DraconicEvolution;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.resources.ResourceLocation;

import java.util.Objects;

/**
 * Created by brandon3055 on 13/11/2022
 */
public class ShieldShader extends BCShader<ShieldShader> {

    private CCUniform activationUniform;
    private CCUniform baseColourUniform;

    public ShieldShader(String path, VertexFormat format) {
        super(new ResourceLocation(DraconicEvolution.MODID, path), format);
    }

    public ShieldShader(ResourceLocation location, VertexFormat format) {
        super(location, format);
    }

    public CCUniform getActivationUniform() {
        return Objects.requireNonNull(activationUniform, missingUniformMessage("Activation"));
    }

    public CCUniform getBaseColourUniform() {
        return Objects.requireNonNull(baseColourUniform, missingUniformMessage("BaseColor"));
    }

    @Override
    protected void onShaderLoaded() {
        super.onShaderLoaded();
        activationUniform = shaderInstance.getUniform("Activation");
        baseColourUniform = shaderInstance.getUniform("BaseColor");
    }
}
