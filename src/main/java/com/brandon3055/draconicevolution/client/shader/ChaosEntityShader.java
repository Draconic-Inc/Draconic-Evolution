package com.brandon3055.draconicevolution.client.shader;

import codechicken.lib.render.shader.CCUniform;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.resources.ResourceLocation;

import static java.util.Objects.requireNonNull;

/**
 * Created by covers1624 on 1/10/22.
 */
public final class ChaosEntityShader extends DEShader<ChaosEntityShader> {

    private CCUniform yawUniform;
    private CCUniform pitchUniform;
    private CCUniform alphaUniform;
    private CCUniform simpleLightUniform;
    private CCUniform disableLightUniform;
    private CCUniform disableOverlayUniform;

    public ChaosEntityShader(String path, VertexFormat format) {
        super(path, format);
    }

    public ChaosEntityShader(ResourceLocation location, VertexFormat format) {
        super(location, format);
    }

    // @formatter:off
    public final CCUniform getYawUniform() { return requireNonNull(yawUniform, missingUniformMessage("Yaw")); }
    public final boolean hasYawUniform() { return yawUniform != null; }
    public final CCUniform getPitchUniform() { return requireNonNull(pitchUniform, missingUniformMessage("Pitch")); }
    public final boolean hasPitchUniform() { return pitchUniform != null; }
    public final CCUniform getAlphaUniform() { return requireNonNull(alphaUniform, missingUniformMessage("Alpha")); }
    public final boolean hasAlphaUniform() { return alphaUniform != null; }
    public final CCUniform getSimpleLightUniform() { return requireNonNull(simpleLightUniform, missingUniformMessage("SimpleLight")); }
    public final boolean hasSimpleLightUniform() { return simpleLightUniform != null; }
    public final CCUniform getDisableLightUniform() { return requireNonNull(disableLightUniform, missingUniformMessage("DisableLight")); }
    public final boolean hasDisableLightUniform() { return disableLightUniform != null; }
    public final CCUniform getDisableOverlayUniform() { return requireNonNull(disableOverlayUniform, missingUniformMessage("DisableOverlay")); }
    public final boolean hasDisableOverlayUniform() { return disableOverlayUniform != null; }
    // @formatter:on

    @Override
    protected void onShaderLoaded() {
        super.onShaderLoaded();
        yawUniform = shaderInstance.getUniform("Yaw");
        pitchUniform = shaderInstance.getUniform("Pitch");
        alphaUniform = shaderInstance.getUniform("Alpha");
        simpleLightUniform = shaderInstance.getUniform("SimpleLight");
        disableLightUniform = shaderInstance.getUniform("DisableLight");
        disableOverlayUniform = shaderInstance.getUniform("DisableOverlay");
    }
}
