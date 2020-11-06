package com.brandon3055.draconicevolution.client.render.effect;

import codechicken.lib.colour.Colour;
import codechicken.lib.colour.ColourARGB;
import codechicken.lib.math.MathHelper;
import codechicken.lib.render.CCRenderState;
import codechicken.lib.render.shader.ShaderProgram;
import com.brandon3055.brandonscore.lib.Vec3D;
import com.brandon3055.brandonscore.utils.Utils;
import com.brandon3055.draconicevolution.blocks.reactor.tileentity.TileReactorCore;
import com.brandon3055.draconicevolution.client.DETextures;
import com.brandon3055.draconicevolution.client.handler.ClientEventHandler;
import com.brandon3055.draconicevolution.utils.ResourceHelperDE;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.particle.IParticleRenderType;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.lwjgl.opengl.GL11;

/**
 * Created by brandon3055 on 12/02/2017.
 */
public class ReactorBeamFX extends Particle {

    protected int ticksTillDeath = 0;
    protected float fxState;
    private float powerState;
    private TileReactorCore tile;
    private boolean isInjectorEffect;
    private Direction facing;
    private float dist;
    private static Colour fieldBeamColour = new ColourARGB(0x00B0FF);
    private static Colour extractBeamColour = new ColourARGB(0xff6600);
    private static Colour energyBeamColour = new ColourARGB(0xff0000);
    private int boltSeed = -1;

    public static ShaderProgram beam_E;
    public static ShaderProgram beam_O;
    public static ShaderProgram beam_I;

    public ReactorBeamFX(World worldIn, Vec3D pos, Direction facing, TileReactorCore tile, boolean isInjectorEffect) {
        super(worldIn, pos.x, pos.y, pos.z);
        this.tile = tile;
        this.facing = facing;
        this.isInjectorEffect = isInjectorEffect;
        this.dist = (float) Utils.getDistanceAtoB(pos, Vec3D.getCenter(tile.getPos()));
        this.rand.setSeed(worldIn.rand.nextLong());
    }

    public void updateFX(float fxState, float powerState) {
        this.fxState = fxState;
        if (powerState != this.powerState) {
            fieldBeamColour.r = (byte) (this.powerState * 0xFF);
            fieldBeamColour.g = (byte) ((1F - this.powerState) * 0xB0);
            fieldBeamColour.b = (byte) ((1F - this.powerState) * 0xFF);
        }
        this.powerState = powerState;
        ticksTillDeath = 4;
    }

    @Override
    public void tick() {
        if (ticksTillDeath-- <= 0) {
            setExpired();
        }
    }

    @Override
    public void renderParticle(IVertexBuilder buffer, ActiveRenderInfo renderInfo, float partialTicks) {
        if (tile.roller != null || !(buffer instanceof BufferBuilder)) {
            return;
        }
        CCRenderState ccrs = CCRenderState.instance();
        ccrs.reset();
        ccrs.bind(buffer, DefaultVertexFormats.POSITION_COLOR_TEX);
        ccrs.brightness = 240;

//        facing = Direction.UP;

        Vec3d viewVec = renderInfo.getProjectedView();
        Vec3D pos1 = new Vec3D(posX - viewVec.x, posY - viewVec.y, posZ - viewVec.z).offset(facing, -0.35D);
        Vec3D pos2;
        float texOffset = (ClientEventHandler.elapsedTicks + partialTicks) / -150F;
        float coreSize = (float) tile.getCoreDiameter() / 2.3F;

//        if (!DEShaders.useShaders() || !DEConfig.useReactorBeamShaders) {
            renderWithoutShaders(ccrs, buffer, pos1, coreSize, texOffset);
//            return;
//        }


        //Testing
//        ResourceHelperDE.bindTexture("textures/test_pattern_checker.png");
//        ResourceHelperDE.bindTexture("textures/test_pattern_h.png");
//        pos2 = pos1.copy().offset(facing, 2.8D);

//        renderBeamTest(ccrs, buffer, pos2, 0.5F, 3F, 5, 0, 0, false, fieldBeamColour);


//        pos1.add(0, 2, 0);
//        RenderSystem.translated(pos1.x, pos1.y, pos1.z);
//        Vec3D point = pos1.copy(); //Bottom Left
//        buffer.pos(point.x, point.y, point.z).color(1F, 1F, 1F, 1F).tex(0F, 0).endVertex();  //BL
//        point.add(-1, 3, 0); //Up
//        buffer.pos(point.x, point.y, point.z).color(1F, 1F, 1F, 1F).tex(0, 1).endVertex();  //TL
//        point.add(3, 0, 0); //Right
//        buffer.pos(point.x, point.y, point.z).color(1F, 1F, 1F, 1F).tex(1, 1).endVertex();  //TR
//        point.add(-1, -3, 0); //Down
//        buffer.pos(point.x, point.y, point.z).color(1F, 1F, 1F, 1F).tex(1F, 0).endVertex();  //BR

//        Vec3D point = pos1.copy(); //Bottom Left
//        buffer.pos(point.x, point.y, point.z).color(1F, 1F, 1F, 1F).tex(0F, 0).endVertex();  //BL
//        point.add(0, 3, 0); //Up
//        buffer.pos(point.x, point.y, point.z).color(1F, 1F, 1F, 1F).tex(0, 1).endVertex();  //TL
//        point.add(3, 0, 0); //Right
//        buffer.pos(point.x, point.y, point.z).color(1F, 1F, 1F, 1F).tex(1, 1).endVertex();  //TR
//        point.add(0, -3, 0); //Down
//        buffer.pos(point.x, point.y, point.z).color(1F, 1F, 1F, 1F).tex(1F, 0).endVertex();  //BR



//        ccrs.startDrawing(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);

        //Normal Quad
//        buffer.pos(0, 0, 0).tex(0F, 0F).endVertex();  //Bottom Left
//        buffer.pos(0, 3, 0).tex(0F, 1F).endVertex();  //Top Left
//        buffer.pos(3, 3, 0).tex(1F, 1F).endVertex();  //Top Right
//        buffer.pos(3, 0, 0).tex(1F, 0F).endVertex();  //Bottom Right

        //Modified Quad
//        buffer.pos(1, 0, 0).tex(0F, 0F).endVertex();  //Bottom Left
//        buffer.pos(0, 3, 0).tex(0F, 1F).endVertex();  //Top Left
//        buffer.pos(3, 3, 0).tex(1F, 1F).endVertex();  //Top Right
//        buffer.pos(2, 0, 0).tex(1F, 0F).endVertex();  //Bottom Right

//        ccrs.draw();

//        RenderSystem.translated(-pos1.x, -pos1.y, -pos1.z);

    }

    public void renderBeamTest(CCRenderState ccrs, IVertexBuilder buffer, Vec3D pos, float widthStart, float widthEnd, float length, float beamAnimation, float beamRotation, boolean fadeReverse, Colour colour) {
        ccrs.startDrawing(GL11.GL_TRIANGLE_STRIP, DefaultVertexFormats.POSITION_COLOR_TEX);
        float sides = 99;
        float r = (colour.r & 0xFF) / 255F, g = (colour.g & 0xFF) / 255F, b = (colour.b & 0xFF) / 255F;
        for (int i = 0; i < sides + 1; i++) {
            double angle = (i / sides) * (Math.PI * 2);// + beamRotation;
            float sin = (float) MathHelper.sin(angle);
            float cos = (float) MathHelper.cos(angle);
            float texX = i / sides;
            Vec3D point = pos.copy().radialOffset(facing.getAxis(), sin, cos, widthStart);
            buffer.pos(point.x, point.y, point.z).color(r, g, b, fadeReverse ? 0F : fxState).tex(texX, beamAnimation).endVertex();
//            buffer.pos(point.x, point.y, point.z).color(r, g, b, fadeReverse ? 0F : fxState).tex(texX, beamAnimation).endVertex();
            point.offset(facing, length);
            point.radialOffset(facing.getAxis(), sin, cos, widthEnd - widthStart);
            buffer.pos(point.x, point.y, point.z).color(r, g, b, fadeReverse ? fxState : 0F).tex(texX, 0.1F + beamAnimation).endVertex();
//            buffer.pos(point.x, point.y, point.z).color(r, g, b, fadeReverse ? fxState : 0F).tex(texX, 0.1F + beamAnimation).endVertex();
        }
        ccrs.draw();
    }


//    @Override
//    public void renderParticle(BufferBuilder buffer, ActiveRenderInfo entityIn, float partialTicks, float rotationX, float rotationZ, float rotationYZ, float rotationXY, float rotationXZ) {
//
//        float animation = (ClientEventHandler.elapsedTicks + partialTicks) * 0.02F;
//        texOffset = 0;
//
//        if (isInjectorEffect) {
//            if (beam_E == null) {
//                beam_E = new ShaderProgram();
//                beam_E.attachShader(DEShaders.reactorBeamE);
//            }
//
//            beam_E.useShader(cache -> {
//                cache.glUniform1F("time", animation);
//                cache.glUniform1F("fade", 1);
//                cache.glUniform1F("power", fxState);
//                cache.glUniform1F("startup", fxState);
//            });
//            pos2 = pos1.copy().offset(facing, 0.6D);
//            renderShaderBeam(buffer, pos1, 0.1F, 0.1F, 0.6D, 0, 0, true, energyBeamColour);
//            beam_E.useShader(cache -> cache.glUniform1F("fade", 0));
//            renderShaderBeam(buffer, pos2, 0.1F, coreSize / 1.5, dist - (coreSize * 1.3), 0, 0, false, energyBeamColour);
//            beam_E.releaseShader();
//        }
//        else {
//            pos2 = pos1.copy().offset(facing, 0.8D);
//
//            //Draw Inner
//            if (beam_O == null) {
//                beam_O = new ShaderProgram();
//                beam_O.attachShader(DEShaders.reactorBeamO);
//            }
//
//            beam_O.useShader(cache -> {
//                cache.glUniform1F("time", animation);
//                cache.glUniform1F("fade", 1);
//                cache.glUniform1F("power", (float) tile.animExtractState.get());
//                cache.glUniform1F("startup", (float) tile.animExtractState.get());
//            });
//            renderShaderBeam(buffer, pos1, 0.263F, 0.263F, 0.8D, texOffset, 0, true, extractBeamColour);
//            beam_O.useShader(cache -> cache.glUniform1F("fade", 0));
//            renderShaderBeam(buffer, pos2, 0.263F, coreSize / 2, dist - (coreSize * 1.3), texOffset, 0, false, extractBeamColour);
//
//            //Draw Outer
//            if (beam_I == null) {
//                beam_I = new ShaderProgram();
//                beam_I.attachShader(DEShaders.reactorBeamI);
//            }
//
//            beam_I.useShader(cache -> {
//                cache.glUniform1F("time", animation);
//                cache.glUniform1F("fade", 1);
//                cache.glUniform1F("power", fxState);
//                cache.glUniform1F("startup", fxState);
//            });
//            renderShaderBeam(buffer, pos1, 0.355D, 0.355D, 0.8D, texOffset, 0, true, fieldBeamColour);
//            beam_I.useShader(cache -> cache.glUniform1F("fade", 0));
//            renderShaderBeam(buffer, pos2, 0.355D, coreSize, dist - coreSize, texOffset, 0, false, fieldBeamColour);
//            beam_I.releaseShader();
//        }
//    }

    public void renderWithoutShaders(CCRenderState ccrs, IVertexBuilder buffer, Vec3D pos1, float coreSize, float texOffset) {
        Vec3D pos2;
        if (isInjectorEffect) {
            ResourceHelperDE.bindTexture(DETextures.REACTOR_ENERGY_BEAM);
//            ResourceHelperDE.bindTexture("textures/test_pattern_v.png");
            pos2 = pos1.copy().offset(facing, 0.6D);
            renderBeamTest(ccrs, buffer, pos1, 0.1F, 0.1F, 0.6F, texOffset, texOffset * 15, true, energyBeamColour);
            renderBeamTest(ccrs, buffer, pos2, 0.1F, coreSize / 1.5F, dist - (coreSize * 1.3F), texOffset + 0.1F, texOffset * 15F, false, energyBeamColour);
        } else {
            pos2 = pos1.copy().offset(facing, 0.8D);
            //Draw Outer
            renderBeamTest(ccrs, buffer, pos1, 0.355F, 0.355F, 0.8F, texOffset, 0, true, fieldBeamColour);
            renderBeamTest(ccrs, buffer, pos2, 0.355F, coreSize, dist - coreSize, texOffset + 0.1F, 0, false, fieldBeamColour);

            //Draw Inner
            renderBeamTest(ccrs, buffer, pos1, 0.263F, 0.263F, 0.8F, -texOffset, 0, true, extractBeamColour);
            renderBeamTest(ccrs, buffer, pos2, 0.263F, coreSize / 2, dist - (coreSize * 1.3F), -texOffset + 0.1F, 0, false, extractBeamColour);
        }
    }

    public void renderBeam(CCRenderState ccrs, IVertexBuilder buffer, Vec3D pos, float widthStart, float widthEnd, float length, float beamAnimation, float beamRotation, boolean fadeReverse, Colour colour) {
        ccrs.startDrawing(GL11.GL_TRIANGLE_STRIP, DefaultVertexFormats.POSITION_TEX_COLOR);
        float sides = 16;
        float r = (colour.r & 0xFF) / 255F, g = (colour.g & 0xFF) / 255F, b = (colour.b & 0xFF) / 255F;
        for (int i = 0; i < sides; i++) {
            double angle = ((i % sides) * Math.PI * 2.13325F / sides) + beamRotation;
            float sin = (float) MathHelper.sin(angle);
            float cos = (float) MathHelper.cos(angle);
            float texX = (i % sides) * 1.0F / sides;
            Vec3D point = pos.copy().radialOffset(facing.getAxis(), sin, cos, widthStart);
            buffer.pos(point.x, point.y, point.z).tex(texX, beamAnimation).color(r, g, b, fadeReverse ? 0F : fxState).endVertex();
            point.offset(facing, length);
            point.radialOffset(facing.getAxis(), sin, cos, widthEnd - widthStart);
            buffer.pos(point.x, point.y, point.z).tex(texX, 0.1F + beamAnimation).color(r, g, b, fadeReverse ? fxState : 0F).endVertex();
        }
        ccrs.draw();
    }
//
//    public void renderShaderBeam(BufferBuilder buffer, Vec3D pos, double widthStart, double widthEnd, double length, double beamAnimation, double beamRotation, boolean fadeReverse, Colour colour) {
//        buffer.begin(GL11.GL_TRIANGLE_STRIP, DefaultVertexFormats.POSITION_TEX_COLOR);
//        double sides = 16;
//        for (int i = 0; i < sides; i++) {
//            double sin = MathHelper.sin(((i % sides) * Math.PI * 2.13325D / sides) + beamRotation);//2.13325D
//            double cos = MathHelper.cos(((i % sides) * Math.PI * 2.13325D / sides) + beamRotation);//2.124999D
//            double s = sides - 0.99999999;
//            double texX = (i % s) * (1.0D / s);
//
//            Vec3D point = pos.copy().radialOffset(facing.getAxis(), sin, cos, widthStart);
//            buffer.pos(point.x, point.y, point.z).tex(texX, (fadeReverse ? 0.1 : 1) + beamAnimation).color(1F, 1F, 1F, 1F).endVertex();
//            point.offset(facing, length);
//            point.radialOffset(facing.getAxis(), sin, cos, widthEnd - widthStart);
//            buffer.pos(point.x, point.y, point.z).tex(texX, beamAnimation).color(1F, 1F, 1F, 1F).endVertex();
//        }
//        Tessellator.getInstance().draw();
//    }

//    @Override
//    public IGLFXHandler getFXHandler() {
//        return FX_HANDLER;
//    }
//
//    public static final IGLFXHandler FX_HANDLER = new IGLFXHandler() {
//        @Override
//        public void preDraw(int layer, BufferBuilder vertexbuffer, Entity entityIn, float partialTicks, float rotationX, float rotationZ, float rotationYZ, float rotationXY, float rotationXZ) {
//            ResourceHelperDE.bindTexture("textures/particle/reactor_beam.png");
//            RenderSystem.disableCull();
//            RenderSystem.depthMask(false);
//            RenderSystem.alphaFunc(GL11.GL_GREATER, 0F);
//
////            if (!DEShaders.useShaders() || !DEOldConfig.useReactorBeamShaders) {
//                RenderSystem.texParameter(3553, 10242, 10497);
//                RenderSystem.texParameter(3553, 10243, 10497);
//                RenderSystem.enableBlend();
//                RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE);
////            }
//        }
//
//        @Override
//        public void postDraw(int layer, BufferBuilder vertexbuffer, Tessellator tessellator) {
//            RenderSystem.alphaFunc(GL11.GL_GREATER, 0.1F);
//
//            RenderSystem.enableCull();
////            if (!DEShaders.useShaders() || !DEOldConfig.useReactorBeamShaders) {
//                RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
////            }
//        }
//    };

    @Override
    public IParticleRenderType getRenderType() {
        return FX_HANDLER;
    }

    private static final IParticleRenderType FX_HANDLER = new FXHandler(DETextures.REACTOR_BEAM);

    public static class FXHandler implements IParticleRenderType {
        private String texture;

        public FXHandler(String texture) {
            this.texture = texture;
        }

        @Override
        public void beginRender(BufferBuilder builder, TextureManager p_217600_2_) {
            ResourceHelperDE.bindTexture(texture);
            RenderSystem.disableCull();
            RenderSystem.depthMask(false);
            RenderSystem.alphaFunc(GL11.GL_GREATER, 0F);
            RenderSystem.shadeModel(GL11.GL_SMOOTH);
//            RenderSystem.disableTexture();

//            if (!DEShaders.useShaders() || !DEOldConfig.useReactorBeamShaders) {
//            RenderSystem.texParameter(3553, 10242, 10497);
//            RenderSystem.texParameter(3553, 10243, 10497);
            RenderSystem.enableBlend();
            RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE);
//            }


//            ResourceHelperDE.bindTexture(texture);
//            RenderSystem.color4f(1.0F, 1F, 1.0F, 1.0F);
//
//            RenderSystem.disableCull();
//            RenderSystem.depthMask(false);
//            RenderSystem.alphaFunc(516, 0.003921569F);
//            RenderSystem.enableBlend();
//            RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE);
//            RenderSystem.glMultiTexCoord2f(0x84c2, 240.0F, 240.0F); //Lightmap
//
//            if (ClientEventHandler.playerHoldingWrench) {
//                RenderSystem.color4f(0, 0, 1, 1);
//            }
//
//            builder.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
        }

        @Override
        public void finishRender(Tessellator tessellator) {
            RenderSystem.alphaFunc(GL11.GL_GREATER, 0.1F);
            RenderSystem.enableTexture();
            RenderSystem.shadeModel(GL11.GL_FLAT);

            RenderSystem.enableCull();
//            if (!DEShaders.useShaders() || !DEOldConfig.useReactorBeamShaders) {
            RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
//            }
        }
    }
}
