package com.brandon3055.draconicevolution.client.render.tile;

import codechicken.lib.render.CCModel;
import codechicken.lib.render.CCRenderState;
import codechicken.lib.render.OBJParser;
import codechicken.lib.render.RenderUtils;
import codechicken.lib.vec.Matrix4;
import codechicken.lib.vec.Rotation;
import codechicken.lib.vec.Vector3;
import com.brandon3055.brandonscore.client.render.TESRBase;
import com.brandon3055.draconicevolution.blocks.tileentity.TileChaosCrystal;
import com.brandon3055.draconicevolution.client.handler.ClientEventHandler;
import com.brandon3055.draconicevolution.helpers.ResourceHelperDE;
import com.brandon3055.draconicevolution.utils.DETextures;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.opengl.GL11;

import java.util.Map;

/**
 * Created by brandon3055 on 24/9/2015.
 */
public class RenderTileChaosCrystal extends TESRBase<TileChaosCrystal> {
    private CCModel model;

    public RenderTileChaosCrystal() {
        Map<String, CCModel> map = OBJParser.parseModels(ResourceHelperDE.getResource("models/chaos_crystal.obj"));
        model = CCModel.combine(map.values());
    }

//    public static void initShader() {
//        if (program != null) {
//            program.cleanup();
//        }
//
//        shaderOperation = CCShaderPipeline.registerOperation();
//        program = new ShaderProgram();
//        program.attachFrag("/assets/draconicevolution/shaders/reactor.frag");
//        program.attachVert("/assets/draconicevolution/shaders/reactor.vert");
//        program.attachShaderOperation(new IShaderOperation() {
//            @Override
//            public boolean load(ShaderProgram program) {
//                return true;
//            }
//
//            //Do rendering stuff and things!
//            //Also set shader variables using
//            //
//            // int varID = program.getUniformLoc("var");
//            // ARBShaderObjects.glUniform1fARB(varID, value);
//            //
//            @Override
//            public void operate(ShaderProgram program) {
////                initShader();
//                //Reset - Clears operations
//                //Set Oipelone Loads
//                //
//                Minecraft mc = Minecraft.getMinecraft();
//                int width = 2;//mc.displayWidth;
//                int height = 2;//mc.displayHeight;
//
////                int x = program.getUniformLoc("yaw");//ARBShaderObjects.glGetUniformLocationARB(shader, "yaw");
////                ARBShaderObjects.glUniform1fARB(x, (float) ((mc.player.rotationYaw * 2 * Math.PI) / 360.0));
////
////                int z = program.getUniformLoc("pitch");//ARBShaderObjects.glGetUniformLocationARB(shader, "pitch");
////                ARBShaderObjects.glUniform1fARB(z, -(float) ((mc.player.rotationPitch * 2 * Math.PI) / 360.0));
//
//                int time = program.getUniformLoc("time");
//                ARBShaderObjects.glUniform1fARB(time, ClientEventHandler.elapsedTicks / 50F);
////
////                int alpha = program.getUniformLoc("alpha");
////                ARBShaderObjects.glUniform1fARB(alpha, 0.1F);
////
////                int widthID = program.getUniformLoc("displayW");
////                ARBShaderObjects.glUniform1fARB(widthID, width);
////
////                int heightID = program.getUniformLoc("displayH");
////                ARBShaderObjects.glUniform1fARB(heightID, height);
//
//
////                ResourceHelperDE.bindTexture("textures/java_2016-11-04_13-04-37.png");
////                ResourceHelperDE.bindTexture("textures/models/reactor_core.png");
//                //Do Rendering
//
////                GlStateManager.matrixMode(GL11.GL_TEXTURE);
////                GlStateManager.pushMatrix();
////                GlStateManager.loadIdentity();
////                GlStateManager.enableBlend();
////                GlStateManager.alphaFunc(GL11.GL_GREATER, 0.0F);
//
////                mc.getFramebuffer().bindFramebuffer(true);
////                mc.getFramebuffer().bindFramebufferTexture();
////                framebuffer.bindFramebuffer(true);
////                framebuffer.bindFramebufferTexture();
////                framebuffer.
//
////                new Framebuffer()
//
////                Tessellator tess = Tessellator.getInstance();
////                BufferBuilder buffer = tess.getBuffer();
//
//                int min = 0;
////                width -= 400;
////                height -= 400;
//
////                buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
////                buffer.pos(width, height, 0).tex(1, 0).endVertex();
////                buffer.pos(width, min,    0).tex(1, 1).endVertex();
////                buffer.pos(min  , min,    0).tex(0, 1).endVertex();
////                buffer.pos(min  , height, 0).tex(0, 0).endVertex();
//
//
////                buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION);
////                buffer.pos(width, height, 0).endVertex();
////                buffer.pos(width, min, 0).endVertex();
////                buffer.pos(min, min, 0).endVertex();
////                buffer.pos(min, height, 0).endVertex();
//
////                buffer.pos(0, height, width).endVertex();
////                buffer.pos(0, min,    width).endVertex();
////                buffer.pos(0, min,    min  ).endVertex();
////                buffer.pos(0, height, min  ).endVertex();
//
////                tess.draw();
//
//
////                GlStateManager.alphaFunc(GL11.GL_GREATER, 0.1F);
////                GlStateManager.popMatrix();
////                GlStateManager.matrixMode(GL11.GL_MODELVIEW);
////                mc.getFramebuffer().createFramebuffer(width, height);
////                mc.getFramebuffer().bindFramebuffer(true);
////                framebuffer.framebufferRender(width, height);
//
//            }
//
//            @Override
//            public int operationID() {
//                return shaderOperation;
//            }
//        });
//    }


    @Override
    public void render(TileChaosCrystal te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        CCRenderState ccrs = CCRenderState.instance();
        ResourceHelperDE.bindTexture(DETextures.CHAOS_CRYSTAL);
        ccrs.startDrawing(GL11.GL_TRIANGLES, DefaultVertexFormats.POSITION_TEX);
        Matrix4 mat = RenderUtils.getMatrix(new Vector3(x + 0.5, y + 0.5, z + 0.5), new Rotation((ClientEventHandler.elapsedTicks + partialTicks) / 40F, 0, 1, 0), -1);
        model.render(ccrs, mat);
        ccrs.draw();

        if (!te.guardianDefeated.value) {
            GlStateManager.pushMatrix();
            GlStateManager.translate(0, -4.5, 0);
            Tessellator tessellator = Tessellator.getInstance();
            BufferBuilder buffer = tessellator.getBuffer();
            ResourceHelperDE.bindTexture(ResourceHelperDE.getResourceRAW("textures/entity/beacon_beam.png"));
            GlStateManager.glTexParameterf(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, 10497.0F);
            GlStateManager.glTexParameterf(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, 10497.0F);
            GlStateManager.disableLighting();
            GlStateManager.disableCull();
            OpenGlHelper.glBlendFunc(770, 1, 1, 0);
            float f2 = (float) ClientEventHandler.elapsedTicks + partialTicks;
            float f3 = -f2 * 0.2F - (float) MathHelper.floor(-f2 * 0.1F);
            GlStateManager.enableBlend();
            OpenGlHelper.glBlendFunc(770, 771, 1, 0);
            GlStateManager.depthMask(false);

            double size = 0.7F;
            double d30 = 0.2D - size;
            double d4 = 0.2D - size;
            double d6 = 0.8D + size;
            double d8 = 0.2D - size;
            double d10 = 0.2D - size;
            double d12 = 0.8D + size;
            double d14 = 0.8D + size;
            double d16 = 0.8D + size;
            double d18 = 10.0D; //Height
            double d20 = 0.0D;
            double d22 = 1.0D;
            double d24 = (double) (-1.0F + f3);
            double d26 = d18 + d24;
            buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);
            buffer.pos(x + d30, y + d18, z + d4).tex(d22, d26).color(200, 0, 0, 62).endVertex();
            buffer.pos(x + d30, y, z + d4).tex(d22, d24).color(200, 0, 0, 62).endVertex();
            buffer.pos(x + d6, y, z + d8).tex(d20, d24).color(200, 0, 0, 62).endVertex();
            buffer.pos(x + d6, y + d18, z + d8).tex(d20, d26).color(200, 0, 0, 62).endVertex();
            buffer.pos(x + d14, y + d18, z + d16).tex(d22, d26).color(200, 0, 0, 62).endVertex();
            buffer.pos(x + d14, y, z + d16).tex(d22, d24).color(200, 0, 0, 62).endVertex();
            buffer.pos(x + d10, y, z + d12).tex(d20, d24).color(200, 0, 0, 62).endVertex();
            buffer.pos(x + d10, y + d18, z + d12).tex(d20, d26).color(200, 0, 0, 62).endVertex();
            buffer.pos(x + d6, y + d18, z + d8).tex(d22, d26).color(200, 0, 0, 62).endVertex();
            buffer.pos(x + d6, y, z + d8).tex(d22, d24).color(200, 0, 0, 62).endVertex();
            buffer.pos(x + d14, y, z + d16).tex(d20, d24).color(200, 0, 0, 62).endVertex();
            buffer.pos(x + d14, y + d18, z + d16).tex(d20, d26).color(200, 0, 0, 62).endVertex();
            buffer.pos(x + d10, y + d18, z + d12).tex(d22, d26).color(200, 0, 0, 62).endVertex();
            buffer.pos(x + d10, y, z + d12).tex(d22, d24).color(200, 0, 0, 62).endVertex();
            buffer.pos(x + d30, y, z + d4).tex(d20, d24).color(200, 0, 0, 62).endVertex();
            buffer.pos(x + d30, y + d18, z + d4).tex(d20, d26).color(200, 0, 0, 62).endVertex();
            tessellator.draw();
            GlStateManager.enableLighting();
            GlStateManager.depthMask(true);
            GlStateManager.popMatrix();
        }
    }

//    public static void t() {
//        program.bindShader();
//        program.runShader();
//    }

//    public void testing(TileChaosCrystal te, double x, double y, double z, float partialTicks, int destroyStage) {
////        ResourceHelperDE.bindTexture(DETextures.CHAOS_GUARDIAN);
////
////        ResourceHelperDE.bindTexture("textures/models/reactor_core.png");
////        Tessellator tess = Tessellator.getInstance();
////        BufferBuilder buffer = tess.getBuffer();
////
//////        GlStateManager.enableBlend();
////        program.bindShader();
////
////        int width = 20;
////        int height = 20;
////        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
////        buffer.pos(width, height, 0).tex(1, 0).endVertex();
////        buffer.pos(width, 0, 0).tex(1, 1).endVertex();
////        buffer.pos(0, 0, 0).tex(0, 1).endVertex();
////        buffer.pos(0, height, 0).tex(0, 0).endVertex();
////        tess.draw();
////
//////        program.bindShader();
//////        program.runShader();
//////        program.runShader();
////        ShaderProgram.unbindShader();
////        GlStateManager.disableBlend();
//////        float ex  = (float)this.rendererDispatcher.entityX;
//////        float f1 = (float)this.rendererDispatcher.entityY;
//////        float ez = (float)this.rendererDispatcher.entityZ;
//////        float f3 = 0.75F;
////        RANDOM.setSeed(31100L);
////
////        Tessellator tess = Tessellator.getInstance();
////        BufferBuilder buffer = tess.getBuffer();
////
////        GlStateManager.disableLighting();
////        GlStateManager.enableBlend();
//////        GlStateManager.blendFunc(GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ONE);
////
////        for (int i = 0; i < 16; i++) {
////            GlStateManager.pushMatrix();
//////            float f4 = (float)(16 - i);
//////            float f5 = 0.0625F;
//////            float f6 = 1.0F / (f4 + 1.0F);
//////
//////            float f7 = (float)(-(y + 0.75D));
//////            float f8 = f7 + (float)ActiveRenderInfo.getPosition().yCoord;
//////            float f9 = f7 + f4 + (float)ActiveRenderInfo.getPosition().yCoord;
//////            float f10 = f8 / f9;
//////            f10 = (float)(y + 0.75D) + f10;
////
////
////            float t1 = ClientEventHandler.elapsedTicks * RANDOM.nextFloat();
////            float t2 = ClientEventHandler.elapsedTicks * 2F * RANDOM.nextFloat();
////            float t3 = ClientEventHandler.elapsedTicks * 8F * RANDOM.nextFloat();
////
////
////            GlStateManager.translate(1, 0.1, 0);
////            GlStateManager.texGen(GlStateManager.TexGen.S, GL11.GL_OBJECT_LINEAR);
////            GlStateManager.texGen(GlStateManager.TexGen.T, GL11.GL_OBJECT_LINEAR);
////            GlStateManager.texGen(GlStateManager.TexGen.R, GL11.GL_OBJECT_LINEAR);
////            GlStateManager.texGen(GlStateManager.TexGen.Q, GL11.GL_EYE_LINEAR);
////            GlStateManager.texGen(GlStateManager.TexGen.S, GL11.GL_OBJECT_PLANE, this.getBuffer(1.0F, 0.0F, 0.0F, 0.0F));
////            GlStateManager.texGen(GlStateManager.TexGen.T, GL11.GL_OBJECT_PLANE, this.getBuffer(0.0F, 0.0F, 1.0F, 0.0F));
////            GlStateManager.texGen(GlStateManager.TexGen.R, GL11.GL_OBJECT_PLANE, this.getBuffer(0.0F, 0.0F, 0.0F, 1.0F));
////            GlStateManager.texGen(GlStateManager.TexGen.Q, GL11.GL_EYE_PLANE, this.getBuffer(0.0F, 1.0F, 0.0F, 0.0F));
////            GlStateManager.enableTexGenCoord(GlStateManager.TexGen.S);
////            GlStateManager.enableTexGenCoord(GlStateManager.TexGen.T);
////            GlStateManager.enableTexGenCoord(GlStateManager.TexGen.R);
////            GlStateManager.enableTexGenCoord(GlStateManager.TexGen.Q);
////
////            GlStateManager.popMatrix();
////            GlStateManager.matrixMode(GL11.GL_TEXTURE);
////            GlStateManager.pushMatrix();
////            GlStateManager.loadIdentity();
////
//////            GlStateManager.translate(0.0F, (float)(Minecraft.getSystemTime() % 700000L) / 700000.0F, 0.0F);
////            float scale = i * 0.01F;
////            GlStateManager.scale(scale, scale, scale);
////
////            float f11 = (RANDOM.nextFloat() * 0.5F + 0.1F);// * i / 16F;
////            float f12 = (RANDOM.nextFloat() * 0.5F + 0.4F);// * i / 16F;
////            float f13 = (RANDOM.nextFloat() * 0.5F + 0.5F);// * i / 16F;
////
////            buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
////            buffer.pos(1, 1, 0).color(f11, f12, f13, 1.0F).endVertex();
////            buffer.pos(1, 0, 0).color(f11, f12, f13, 1.0F).endVertex();
////            buffer.pos(0, 0, 0).color(f11, f12, f13, 1.0F).endVertex();
////            buffer.pos(0, 1, 0).color(f11, f12, f13, 1.0F).endVertex();
////            tess.draw();
////
////
////            GlStateManager.popMatrix();
////            GlStateManager.matrixMode(GL11.GL_MODELVIEW);
////        }
////
////
////        GlStateManager.disableBlend();
////        GlStateManager.disableTexGenCoord(GlStateManager.TexGen.S);
////        GlStateManager.disableTexGenCoord(GlStateManager.TexGen.T);
////        GlStateManager.disableTexGenCoord(GlStateManager.TexGen.R);
////        GlStateManager.disableTexGenCoord(GlStateManager.TexGen.Q);
////        GlStateManager.enableLighting();
//    }
}
