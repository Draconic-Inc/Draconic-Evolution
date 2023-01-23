package com.brandon3055.draconicevolution.api.render;

import com.brandon3055.brandonscore.client.shader.BCShaders;
import com.brandon3055.draconicevolution.client.ModuleSpriteUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;

/**
 * Created by brandon3055 on 22/01/2023
 */
public class RenderTypes {

    public static final RenderType MODULE_TYPE = RenderType.create("module_type", DefaultVertexFormat.POSITION_COLOR_TEX, VertexFormat.Mode.QUADS, 256, RenderType.CompositeState.builder()
            .setShaderState(new RenderStateShard.ShaderStateShard(() -> BCShaders.posColourTexAlpha0))
            .setTextureState(new RenderStateShard.TextureStateShard(ModuleSpriteUploader.LOCATION_MODULE_TEXTURE, false, false))
            .setTransparencyState(RenderStateShard.TRANSLUCENT_TRANSPARENCY)
            .createCompositeState(false)
    );
}
