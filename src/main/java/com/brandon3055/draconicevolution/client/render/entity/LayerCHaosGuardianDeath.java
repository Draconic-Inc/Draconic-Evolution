//package com.brandon3055.draconicevolution.client.render.entity;
//
//import com.brandon3055.draconicevolution.entity.EntityChaosGuardian;
//import net.minecraft.client.renderer.RenderSystem;
//import net.minecraft.client.renderer.RenderHelper;
//import net.minecraft.client.renderer.Tessellator;
//import net.minecraft.client.renderer.BufferBuilder;
//import net.minecraft.client.renderer.entity.layers.LayerRenderer;
//import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
//import net.minecraftforge.fml.relauncher.Side;
//import net.minecraftforge.fml.relauncher.SideOnly;
//
//import java.util.Random;
//
//@OnlyIn(Dist.CLIENT)
//public class LayerCHaosGuardianDeath implements LayerRenderer<EntityChaosGuardian> {
//    public void doRenderLayer(EntityChaosGuardian entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
//        if (entitylivingbaseIn.deathTicks > 0) {
//            Tessellator tessellator = Tessellator.getInstance();
//            BufferBuilder vertexbuffer = tessellator.getBuffer();
//            RenderHelper.disableStandardItemLighting();
//            float f = ((float) entitylivingbaseIn.deathTicks + partialTicks) / 200.0F;
//            float f1 = 0.0F;
//
//            if (f > 0.8F) {
//                f1 = (f - 0.8F) / 0.2F;
//            }
//
//            Random random = new Random(432L);
//            RenderSystem.disableTexture2D();
//            RenderSystem.shadeModel(7425);
//            RenderSystem.enableBlend();
//            RenderSystem.blendFunc(RenderSystem.SourceFactor.SRC_ALPHA, RenderSystem.DestFactor.ONE);
//            RenderSystem.disableAlpha();
//            RenderSystem.enableCull();
//            RenderSystem.depthMask(false);
//            RenderSystem.pushMatrix();
//            RenderSystem.translate(0.0F, -1.0F, -2.0F);
//
//            for (int i = 0; (float) i < (f + f * f) / 2.0F * 60.0F; ++i) {
//                RenderSystem.rotate(random.nextFloat() * 360.0F, 1.0F, 0.0F, 0.0F);
//                RenderSystem.rotate(random.nextFloat() * 360.0F, 0.0F, 1.0F, 0.0F);
//                RenderSystem.rotate(random.nextFloat() * 360.0F, 0.0F, 0.0F, 1.0F);
//                RenderSystem.rotate(random.nextFloat() * 360.0F, 1.0F, 0.0F, 0.0F);
//                RenderSystem.rotate(random.nextFloat() * 360.0F, 0.0F, 1.0F, 0.0F);
//                RenderSystem.rotate(random.nextFloat() * 360.0F + f * 90.0F, 0.0F, 0.0F, 1.0F);
//                float f2 = random.nextFloat() * 20.0F + 5.0F + f1 * 10.0F;
//                float f3 = random.nextFloat() * 2.0F + 1.0F + f1 * 2.0F;
//                vertexbuffer.begin(6, DefaultVertexFormats.POSITION_COLOR);
//                vertexbuffer.pos(0.0D, 0.0D, 0.0D).color(255, 255, 255, (int) (255.0F * (1.0F - f1))).endVertex();
//                vertexbuffer.pos(-0.866D * (double) f3, (double) f2, (double) (-0.5F * f3)).color(255, 0, 255, 0).endVertex();
//                vertexbuffer.pos(0.866D * (double) f3, (double) f2, (double) (-0.5F * f3)).color(255, 0, 255, 0).endVertex();
//                vertexbuffer.pos(0.0D, (double) f2, (double) (1.0F * f3)).color(255, 0, 255, 0).endVertex();
//                vertexbuffer.pos(-0.866D * (double) f3, (double) f2, (double) (-0.5F * f3)).color(255, 0, 255, 0).endVertex();
//                tessellator.draw();
//            }
//
//            RenderSystem.popMatrix();
//            RenderSystem.depthMask(true);
//            RenderSystem.disableCull();
//            RenderSystem.disableBlend();
//            RenderSystem.shadeModel(7424);
//            RenderSystem.color(1.0F, 1.0F, 1.0F, 1.0F);
//            RenderSystem.enableTexture2D();
//            RenderSystem.enableAlpha();
//            RenderHelper.enableStandardItemLighting();
//        }
//    }
//
//    public boolean shouldCombineTextures() {
//        return false;
//    }
//}