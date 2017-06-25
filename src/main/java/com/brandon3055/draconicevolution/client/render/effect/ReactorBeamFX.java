package com.brandon3055.draconicevolution.client.render.effect;

import codechicken.lib.colour.Colour;
import codechicken.lib.colour.ColourARGB;
import codechicken.lib.math.MathHelper;
import codechicken.lib.render.shader.ShaderProgram;
import com.brandon3055.brandonscore.client.particle.BCParticle;
import com.brandon3055.brandonscore.client.particle.IGLFXHandler;
import com.brandon3055.brandonscore.lib.Vec3D;
import com.brandon3055.brandonscore.utils.Utils;
import com.brandon3055.draconicevolution.DEConfig;
import com.brandon3055.draconicevolution.blocks.reactor.tileentity.TileReactorCore;
import com.brandon3055.draconicevolution.client.handler.ClientEventHandler;
import com.brandon3055.draconicevolution.client.render.shaders.DEShaders;
import com.brandon3055.draconicevolution.helpers.ResourceHelperDE;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import org.lwjgl.opengl.GL11;

/**
 * Created by brandon3055 on 12/02/2017.
 */
public class ReactorBeamFX extends BCParticle {

    protected int ticksTillDeath = 0;
    protected float fxState;
    private float powerState;
    private TileReactorCore tile;
    private boolean isInjectorEffect;
    private final EnumFacing facing;
    private double dist;
    private static Colour fieldBeamColour = new ColourARGB(0x00B0FF);
    private static Colour extractBeamColour = new ColourARGB(0xff6600);
    private static Colour energyBeamColour = new ColourARGB(0xff0000);
    private int boltSeed = -1;

    private static ShaderProgram beam_E;
    private static ShaderProgram beam_O;
    private static ShaderProgram beam_I;

    public ReactorBeamFX(World worldIn, Vec3D pos, EnumFacing facing, TileReactorCore tile, boolean isInjectorEffect) {
        super(worldIn, pos);
        this.facing = facing;
        this.tile = tile;
        this.isInjectorEffect = isInjectorEffect;
        this.dist = Utils.getDistanceAtoB(pos, Vec3D.getCenter(tile.getPos()));
        this.rand.setSeed(worldIn.rand.nextLong());
    }

    @Override
    public boolean isRawGLParticle() {
        return true;
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
    public void onUpdate() {
        if (ticksTillDeath-- <= 0) {
            setExpired();
        }

//        if (rand.nextInt(10) == 0) {
//            boltSeed = rand.nextInt();
//        }
//        else {
//            boltSeed = -1;
//        }

    }

    @Override
    public void renderParticle(VertexBuffer buffer, Entity entity, float partialTicks, float rotationX, float rotationZ, float rotationYZ, float rotationXY, float rotationXZ) {
        Vec3D pos1 = new Vec3D(posX - interpPosX, posY - interpPosY, posZ - interpPosZ).offset(facing, -0.35D);
        Vec3D pos2;
        double texOffset = (ClientEventHandler.elapsedTicks + partialTicks) / -150D;
        double coreSize = tile.getCoreDiameter() / 2.3;

        if (!DEShaders.useShaders() || !DEConfig.useReactorBeamShaders) {
            renderWithoutShaders(buffer, pos1, coreSize, texOffset);
            return;
        }

        float animation = (ClientEventHandler.elapsedTicks + partialTicks) * 0.02F;

        texOffset = 0;

        if (isInjectorEffect) {
            if (beam_E == null) {
                beam_E = new ShaderProgram();
                beam_E.attachShader(DEShaders.reactorBeamE);
            }

            beam_E.useShader(cache -> {
                cache.glUniform1F("time", animation);
                cache.glUniform1F("power", fxState);
                cache.glUniform1F("fade", 1);
                cache.glUniform1F("startup", fxState);
            });
            pos2 = pos1.copy().offset(facing, 0.6D);
            renderShaderBeam(buffer, pos1, 0.1F, 0.1F, 0.6D, 0, 0, true, energyBeamColour);
            beam_E.releaseShader();
            renderShaderBeam(buffer, pos2, 0.1F, coreSize / 1.5, dist - (coreSize * 1.3), 0, 0, false, energyBeamColour);
            beam_E.releaseShader();
        }
        else {
            pos2 = pos1.copy().offset(facing, 0.8D);

            //Draw Inner
            if (beam_O == null) {
                beam_O = new ShaderProgram();
                beam_O.attachShader(DEShaders.reactorBeamE);
            }

            beam_O.useShader(cache -> {
                cache.glUniform1F("time", animation);
                cache.glUniform1F("power", (float) tile.animExtractState.value);
                cache.glUniform1F("fade", 1);
                cache.glUniform1F("startup", (float) tile.animExtractState.value);
            });
            renderShaderBeam(buffer, pos1, 0.263F, 0.263F, 0.8D, texOffset, 0, true, extractBeamColour);
            beam_O.useShader(cache -> {
                cache.glUniform1F("time", animation);
                cache.glUniform1F("power", (float) tile.animExtractState.value);
                cache.glUniform1F("fade", 0);
                cache.glUniform1F("startup", (float) tile.animExtractState.value);
            });
            renderShaderBeam(buffer, pos2, 0.263F, coreSize / 2, dist - (coreSize * 1.3), texOffset, 0, false, extractBeamColour);

            //Draw Outer
            if (beam_I == null) {
                beam_I = new ShaderProgram();
                beam_I.attachShader(DEShaders.reactorBeamE);
            }

            beam_I.useShader(cache -> {
                cache.glUniform1F("time", animation);
                cache.glUniform1F("power", fxState);
                cache.glUniform1F("fade", 1);
                cache.glUniform1F("startup", fxState);
            });
            renderShaderBeam(buffer, pos1, 0.355D, 0.355D, 0.8D, texOffset, 0, true, fieldBeamColour);
            beam_I.useShader(cache -> {
                cache.glUniform1F("time", animation);
                cache.glUniform1F("power", fxState);
                cache.glUniform1F("fade", 0);
                cache.glUniform1F("startup", fxState);
            });
            renderShaderBeam(buffer, pos2, 0.355D, coreSize, dist - coreSize, texOffset, 0, false, fieldBeamColour);
            beam_I.releaseShader();
        }
    }

    public void renderWithoutShaders(VertexBuffer buffer, Vec3D pos1, double coreSize, double texOffset) {
        Vec3D pos2;
        if (isInjectorEffect) {
            ResourceHelperDE.bindTexture("textures/particle/reactor_energy_beam.png");
            pos2 = pos1.copy().offset(facing, 0.6D);
            renderBeam(buffer, pos1, 0.1D, 0.1D, 0.6D, texOffset, texOffset * 15, true, energyBeamColour);
            renderBeam(buffer, pos2, 0.1D, coreSize / 1.5, dist - (coreSize * 1.3), texOffset + 0.1, texOffset * 15, false, energyBeamColour);
        }
        else {
            pos2 = pos1.copy().offset(facing, 0.8D);
            //Draw Outer
            renderBeam(buffer, pos1, 0.355D, 0.355D, 0.8D, texOffset, 0, true, fieldBeamColour);
            renderBeam(buffer, pos2, 0.355D, coreSize, dist - coreSize, texOffset + 0.1, 0, false, fieldBeamColour);

            //Draw Inner
            renderBeam(buffer, pos1, 0.263F, 0.263F, 0.8D, -texOffset, 0, true, extractBeamColour);
            renderBeam(buffer, pos2, 0.263F, coreSize / 2, dist - (coreSize * 1.3), -texOffset + 0.1, 0, false, extractBeamColour);
        }
    }

    public void renderBeam(VertexBuffer buffer, Vec3D pos, double widthStart, double widthEnd, double length, double beamAnimation, double beamRotation, boolean fadeReverse, Colour colour) {
        buffer.begin(GL11.GL_TRIANGLE_STRIP, DefaultVertexFormats.POSITION_TEX_COLOR);
        double sides = 16;
        float r = (colour.r & 0xFF) / 255F, g = (colour.g & 0xFF) / 255F, b = (colour.b & 0xFF) / 255F;
        for (int i = 0; i < sides; i++) {
            double sin = MathHelper.sin(((i % sides) * Math.PI * 2.13325D / sides) + beamRotation);
            double cos = MathHelper.cos(((i % sides) * Math.PI * 2.13325D / sides) + beamRotation);
            double texX = (i % sides) * 1.0D / sides;
            Vec3D point = pos.copy().radialOffset(facing.getAxis(), sin, cos, widthStart);
            buffer.pos(point.x, point.y, point.z).tex(texX, beamAnimation).color(r, g, b, fadeReverse ? 0F : fxState).endVertex();
            point.offset(facing, length);
            point.radialOffset(facing.getAxis(), sin, cos, widthEnd - widthStart);
            buffer.pos(point.x, point.y, point.z).tex(texX, 0.1 + beamAnimation).color(r, g, b, fadeReverse ? fxState : 0F).endVertex();
        }
        Tessellator.getInstance().draw();
    }

    public void renderShaderBeam(VertexBuffer buffer, Vec3D pos, double widthStart, double widthEnd, double length, double beamAnimation, double beamRotation, boolean fadeReverse, Colour colour) {
        buffer.begin(GL11.GL_TRIANGLE_STRIP, DefaultVertexFormats.POSITION_TEX_COLOR);
        double sides = 16;
        for (int i = 0; i < sides; i++) {
            double sin = MathHelper.sin(((i % sides) * Math.PI * 2.13325D / sides) + beamRotation);//2.13325D
            double cos = MathHelper.cos(((i % sides) * Math.PI * 2.13325D / sides) + beamRotation);//2.124999D
            double s = sides - 0.99999999;
            double texX = (i % s) * (1.0D / s);

            Vec3D point = pos.copy().radialOffset(facing.getAxis(), sin, cos, widthStart);
            buffer.pos(point.x, point.y, point.z).tex(texX, (fadeReverse ? 0.1 : 1) + beamAnimation).color(1F, 1F, 1F, 1F).endVertex();
            point.offset(facing, length);
            point.radialOffset(facing.getAxis(), sin, cos, widthEnd - widthStart);
            buffer.pos(point.x, point.y, point.z).tex(texX, beamAnimation).color(1F, 1F, 1F, 1F).endVertex();
        }
        Tessellator.getInstance().draw();
    }

    @Override
    public IGLFXHandler getFXHandler() {
        return FX_HANDLER;
    }

    public static final IGLFXHandler FX_HANDLER = new IGLFXHandler() {
        @Override
        public void preDraw(int layer, VertexBuffer vertexbuffer, Entity entityIn, float partialTicks, float rotationX, float rotationZ, float rotationYZ, float rotationXY, float rotationXZ) {
            ResourceHelperDE.bindTexture("textures/particle/reactor_beam.png");
            GlStateManager.disableCull();
            GlStateManager.depthMask(false);
            GlStateManager.alphaFunc(GL11.GL_GREATER, 0F);

            if (!DEShaders.useShaders()) {
                GlStateManager.glTexParameterf(3553, 10242, 10497.0F);
                GlStateManager.glTexParameterf(3553, 10243, 10497.0F);
                GlStateManager.enableBlend();
                GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE);
            }
        }

        @Override
        public void postDraw(int layer, VertexBuffer vertexbuffer, Tessellator tessellator) {
            GlStateManager.alphaFunc(GL11.GL_GREATER, 0.1F);

            GlStateManager.enableCull();
            if (!DEShaders.useShaders()) {
                GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
            }
        }
    };
}
