package com.brandon3055.draconicevolution.api.render;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;

/**
 * Created by brandon3055 on 22/01/2023
 */
public class DERenderTypes {

//    public static final RenderType MODULE_TYPE = RenderType.create("module_type", DefaultVertexFormat.POSITION_COLOR_TEX, VertexFormat.Mode.QUADS, 256, RenderType.CompositeState.builder()
//            .setShaderState(new RenderStateShard.ShaderStateShard(() -> BCShaders.posColourTexAlpha0))
//            .setTextureState(new RenderStateShard.TextureStateShard(ModuleTextures.LOCATION_MODULE_TEXTURE, false, false))
//            .setTransparencyState(RenderStateShard.TRANSLUCENT_TRANSPARENCY)
//            .createCompositeState(false)
//    );

    public static final RenderType TRANS_COLOUR_TYPE = RenderType.create("de_trans_colour", DefaultVertexFormat.POSITION_COLOR, VertexFormat.Mode.QUADS, 256,
            RenderType.CompositeState.builder()
                    .setShaderState(new RenderStateShard.ShaderStateShard(GameRenderer::getPositionColorShader))
                    .setTransparencyState(RenderStateShard.TRANSLUCENT_TRANSPARENCY)
                    .setCullState(RenderStateShard.NO_CULL)
                    .createCompositeState(false)
    );

    public static RenderType FAN_TYPE = RenderType.create("tri_fan_type", DefaultVertexFormat.POSITION_COLOR, VertexFormat.Mode.TRIANGLE_FAN, 256, RenderType.CompositeState.builder()
                    .setShaderState(new RenderStateShard.ShaderStateShard(GameRenderer::getPositionColorShader))
                    .setTransparencyState(RenderStateShard.TRANSLUCENT_TRANSPARENCY)
//            .setAlphaState(RenderStateShard.NO_ALPHA)
//            .setTexturingState(new RenderStateShard.TexturingStateShard("lighting", RenderSystem::disableLighting, SneakyUtils.none()))
                    .createCompositeState(false)
    );
}
