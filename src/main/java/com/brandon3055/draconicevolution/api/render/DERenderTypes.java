package com.brandon3055.draconicevolution.api.render;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;

import java.util.OptionalDouble;

import static net.minecraft.client.renderer.RenderStateShard.*;

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

    //Broken?
//    public static final RenderType TRANS_COLOUR_TYPE = RenderType.create("de_trans_colour", DefaultVertexFormat.POSITION_COLOR, VertexFormat.Mode.QUADS, 256,
//            RenderType.CompositeState.builder()
//                    .setShaderState(new RenderStateShard.ShaderStateShard(GameRenderer::getPositionColorShader))
//                    .setTransparencyState(RenderStateShard.TRANSLUCENT_TRANSPARENCY)
//                    .setCullState(RenderStateShard.NO_CULL)
//                    .createCompositeState(false)
//    );

    public static final RenderStateShard.DepthTestStateShard DISABLE_DEPTH = new RenderStateShard.DepthTestStateShard("none", 519) {
        @Override
        public void setupRenderState() {
            RenderSystem.disableDepthTest();
        }
    };

    public static final RenderType BOX_NO_DEPTH = RenderType.create("de:box_no_depth", DefaultVertexFormat.POSITION_COLOR, VertexFormat.Mode.QUADS, 256, false, true, RenderType.CompositeState.builder()
            .setShaderState(POSITION_COLOR_SHADER)
            .setTransparencyState(TRANSLUCENT_TRANSPARENCY)
            .setWriteMaskState(COLOR_WRITE)
            .setDepthTestState(DISABLE_DEPTH)
            .createCompositeState(false)
    );

    public static final RenderType OUTLINE_TYPE = RenderType.create("de:outline", DefaultVertexFormat.POSITION_COLOR_NORMAL, VertexFormat.Mode.LINES, 256, RenderType.CompositeState.builder()
            .setShaderState(RenderStateShard.RENDERTYPE_LINES_SHADER)
            .setTransparencyState(RenderStateShard.TRANSLUCENT_TRANSPARENCY)
            .setCullState(RenderStateShard.NO_CULL)
            .setWriteMaskState(RenderStateShard.COLOR_WRITE)
            .setDepthTestState(RenderStateShard.NO_DEPTH_TEST)
            .setLineState(new RenderStateShard.LineStateShard(OptionalDouble.of(4.0)))
            .createCompositeState(false)
    );

}
