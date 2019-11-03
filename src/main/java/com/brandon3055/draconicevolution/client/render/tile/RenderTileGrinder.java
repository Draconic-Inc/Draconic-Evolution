package com.brandon3055.draconicevolution.client.render.tile;

import codechicken.lib.model.ISmartVertexConsumer;
import codechicken.lib.model.Quad;
import codechicken.lib.model.pipeline.BakedPipeline;
import codechicken.lib.model.pipeline.transformers.QuadClamper;
import codechicken.lib.render.CCRenderState;
import codechicken.lib.render.consumer.CCRSConsumer;
import com.brandon3055.draconicevolution.blocks.tileentity.TileGrinder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.util.EnumFacing;

/**
 * Created by brandon3055 on 3/11/19.
 */
public class RenderTileGrinder extends TileEntitySpecialRenderer<TileGrinder> {

    private ThreadLocal<BakedPipeline> pipelines = ThreadLocal.withInitial( () -> BakedPipeline.builder()
            // Clamper is responsible for clamping the vertex to the bounds specified.
            .addElement( "clamper", QuadClamper.FACTORY )
            // Strips faces if they match a mask.
//            .addElement( "face_stripper", QuadFaceStripper.FACTORY )
            // Kicks the edge inner corners in, solves Z fighting
//            .addElement( "corner_kicker", QuadCornerKicker.FACTORY )
            // Re-Interpolates the UV's for the quad.
//            .addElement( "interp", QuadReInterpolator.FACTORY )
            // Tints the quad if we need it to. Disabled by default.
//            .addElement( "tinter", QuadTinter.FACTORY, false )
            // Overrides the quad's alpha if we are forcing transparent facades.
//            .addElement( "transparent", QuadAlphaOverride.FACTORY, false, e -> e.setAlphaOverride( 0x4C / 255F ) )
            .build()//
    );
    private ThreadLocal<Quad> collectors = ThreadLocal.withInitial( Quad::new );

    @Override
    public void renderTileEntityFast(TileGrinder te, double x, double y, double z, float partialTicks, int destroyStage, float partial, BufferBuilder buffer) {
        CCRenderState ccrs = CCRenderState.instance();
        BakedPipeline pipeline = this.pipelines.get();
        Quad collectorQuad = this.collectors.get();



        BlockRendererDispatcher dispatcher = Minecraft.getMinecraft().getBlockRendererDispatcher();
//        dispatcher.getModelForState();



        CCRSConsumer ccrsConsumer = new CCRSConsumer(ccrs);

        pipeline.prepare(ccrsConsumer);




        //End goal create IPipelineConsumer that pipes everything through a vertex ligher flat or smoothAO (which extends flat)
        //Extend quad transformer to do everything for me?

        //Offset buffer builder?

    }


    public static class GrinderVertexConsumer implements ISmartVertexConsumer {

        @Override
        public void put(Quad quad) {

        }

        @Override
        public VertexFormat getVertexFormat() {
            return DefaultVertexFormats.BLOCK;
        }

        @Override
        public void setQuadTint(int tint) {

        }

        @Override
        public void setQuadOrientation(EnumFacing orientation) {

        }

        @Override
        public void setApplyDiffuseLighting(boolean diffuse) {

        }

        @Override
        public void setTexture(TextureAtlasSprite texture) {

        }

        @Override
        public void put(int element, float... data) {

        }
    }
}
