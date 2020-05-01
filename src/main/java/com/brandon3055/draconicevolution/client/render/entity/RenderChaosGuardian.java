//package com.brandon3055.draconicevolution.client.render.entity;
//
//
//import com.brandon3055.draconicevolution.client.model.ModelChaosGuardian;
//import com.brandon3055.draconicevolution.entity.EntityChaosGuardian;
//import com.brandon3055.draconicevolution.utils.ResourceHelperDE;
//import com.brandon3055.draconicevolution.client.DETextures;
//import net.minecraft.client.renderer.*;
//import net.minecraft.client.renderer.entity.RenderLiving;
//import net.minecraft.client.renderer.entity.RenderManager;
//import net.minecraft.client.renderer.entity.layers.LayerRenderer;
//import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
//import net.minecraft.util.ResourceLocation;
//import net.minecraft.util.math.BlockPos;
//import net.minecraft.util.math.MathHelper;
//import net.minecraftforge.fml.relauncher.Side;
//import net.minecraftforge.fml.relauncher.SideOnly;
//import org.lwjgl.opengl.GL11;
//
///**
// * Created by brandon3055 on 06/07/2016.
// */
//public class RenderChaosGuardian extends RenderLiving<EntityChaosGuardian> {
//    protected ModelChaosGuardian modelDragon;
//
//    public RenderChaosGuardian(RenderManager manager) {
//        super(manager, new ModelChaosGuardian(0.0F), 0.5F);
//        this.modelDragon = (ModelChaosGuardian) this.mainModel;
////        this.setRenderPassModel(this.mainModel);
//        this.addLayer(new LayerChaosGuardianEyes(this));
//        this.addLayer(new LayerCHaosGuardianDeath());
//    }
//
//    @Override
//    protected void applyRotations(EntityChaosGuardian entityLiving, float p_77043_2_, float p_77043_3_, float partialTicks) {
//        float f3 = (float) entityLiving.getMovementOffsets(7, partialTicks)[0];
//        float f4 = (float) (entityLiving.getMovementOffsets(5, partialTicks)[1] - entityLiving.getMovementOffsets(10, partialTicks)[1]);
//        GL11.glRotatef(-f3, 0.0F, 1.0F, 0.0F);
//        GL11.glRotatef(f4 * 10.0F, 1.0F, 0.0F, 0.0F);
//        GL11.glTranslatef(0.0F, 0.0F, 1.0F);
//
//        if (entityLiving.deathTime > 0) {
//            float f5 = ((float) entityLiving.deathTime + partialTicks - 1.0F) / 20.0F * 1.6F;
//            f5 = MathHelper.sqrt(f5);
//
//            if (f5 > 1.0F) {
//                f5 = 1.0F;
//            }
//
//            GL11.glRotatef(f5 * this.getDeathMaxRotation(entityLiving), 0.0F, 0.0F, 1.0F);
//        }
//    }
//
//    @Override
//    protected void renderModel(EntityChaosGuardian chaosGuardian, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor) {
//        if (chaosGuardian.deathTicks > 0) {
//            float f6 = (float) chaosGuardian.deathTicks / 200.0F;
//            GL11.glDepthFunc(GL11.GL_LEQUAL);
//            GL11.glEnable(GL11.GL_ALPHA_TEST);
//            GL11.glAlphaFunc(GL11.GL_GREATER, f6);
//            this.bindTexture(ResourceHelperDE.getResourceRAW("textures/entity/enderdragon/dragon_exploding.png"));
//            this.mainModel.render(chaosGuardian, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scaleFactor);
//            GL11.glAlphaFunc(GL11.GL_GREATER, 0.1F);
//            GL11.glDepthFunc(GL11.GL_EQUAL);
//        }
//
//        this.bindEntityTexture(chaosGuardian);
//        this.mainModel.render(chaosGuardian, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scaleFactor);
//
//        if (chaosGuardian.hurtTime > 0) {
//            GL11.glDepthFunc(GL11.GL_EQUAL);
//            GL11.glDisable(GL11.GL_TEXTURE_2D);
//            GL11.glEnable(GL11.GL_BLEND);
//            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
//            GL11.glColor4f(1.0F, 0.0F, 0.0F, 0.5F);
//            this.mainModel.render(chaosGuardian, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scaleFactor);
//            GL11.glEnable(GL11.GL_TEXTURE_2D);
//            GL11.glDisable(GL11.GL_BLEND);
//            GL11.glDepthFunc(GL11.GL_LEQUAL);
//        }
//    }
//
//    @Override
//    public void doRender(EntityChaosGuardian chaosGuardian, double x, double y, double z, float entityYaw, float partialTicks) {
//        super.doRender(chaosGuardian, x, y, z, entityYaw, partialTicks);
//        BlockPos pos = chaosGuardian.getCrystalPos();
//        if (pos != null) {
//            float innerRotation = chaosGuardian.healingChaosCrystal != null ? (float) chaosGuardian.healingChaosCrystal.innerRotation + partialTicks : 0;
//            float bob = MathHelper.sin(innerRotation * 0.2F) / 2.0F + 0.5F;
//            bob = (bob * bob + bob) * 0.2F;
//            float renderX = (float) (pos.getX() + 0.5 - chaosGuardian.posX - (chaosGuardian.prevPosX - chaosGuardian.posX) * (double) (1.0F - partialTicks));
//            float renderY = (float) ((double) bob + pos.getY() - 1.0D - chaosGuardian.posY - (chaosGuardian.prevPosY - chaosGuardian.posY) * (double) (1.0F - partialTicks));
//            float renderZ = (float) (pos.getZ() + 0.5 - chaosGuardian.posZ - (chaosGuardian.prevPosZ - chaosGuardian.posZ) * (double) (1.0F - partialTicks));
//            float f7 = MathHelper.sqrt(renderX * renderX + renderZ * renderZ);
//            float f8 = MathHelper.sqrt(renderX * renderX + renderY * renderY + renderZ * renderZ);
//            GL11.glPushMatrix();
//            GL11.glTranslatef((float) x, (float) y + 2.0F, (float) z);
//            GL11.glRotatef((float) (-Math.atan2((double) renderZ, (double) renderX)) * 180.0F / (float) Math.PI - 90.0F, 0.0F, 1.0F, 0.0F);
//            GL11.glRotatef((float) (-Math.atan2((double) f7, (double) renderY)) * 180.0F / (float) Math.PI - 90.0F, 1.0F, 0.0F, 0.0F);
//            Tessellator tessellator = Tessellator.getInstance();
//            BufferBuilder buffer = tessellator.getBuffer();
//            RenderHelper.disableStandardItemLighting();
//            GL11.glDisable(GL11.GL_CULL_FACE);
//            this.bindTexture(ResourceHelperDE.getResourceRAW("textures/entity/endercrystal/endercrystal_beam.png"));
//            GL11.glShadeModel(GL11.GL_SMOOTH);
//            float f9 = 0.0F - ((float) chaosGuardian.ticksExisted + partialTicks) * 0.01F;
//            float f10 = MathHelper.sqrt(renderX * renderX + renderY * renderY + renderZ * renderZ) / 32.0F - ((float) chaosGuardian.ticksExisted + partialTicks) * 0.01F;
//
//            buffer.begin(GL11.GL_TRIANGLE_STRIP, DefaultVertexFormats.POSITION_TEX_COLOR);
//
//            byte b0 = 8;
//            for (int i = 0; i <= b0; ++i) {
//                float f11 = MathHelper.sin((float) (i % b0) * (float) Math.PI * 2.0F / (float) b0) * 0.75F;
//                float f12 = MathHelper.cos((float) (i % b0) * (float) Math.PI * 2.0F / (float) b0) * 0.75F;
//                float f13 = (float) (i % b0) * 1.0F / (float) b0;
//                buffer.pos((double) (f11 * 0.2F), (double) (f12 * 0.2F), 0.0D).tex((double) f13, (double) f10).color(0F, 0F, 0F, 1F).endVertex();
//                buffer.pos((double) f11, (double) f12, (double) f8).tex((double) f13, (double) f9).color(1F, 0F, 0F, 1F).endVertex();
//            }
//
//            tessellator.draw();
//            GL11.glEnable(GL11.GL_CULL_FACE);
//            GL11.glShadeModel(GL11.GL_FLAT);
//            RenderHelper.enableStandardItemLighting();
//            GL11.glPopMatrix();
//        }
//
//    }
//
//    @Override
//    protected ResourceLocation getEntityTexture(EntityChaosGuardian entity) {
//        return ResourceHelperDE.getResource(DETextures.CHAOS_GUARDIAN);
//    }
////
////    protected void renderEquippedItems(EntityDragon dragon, float p_77029_2_) {
////        super.renderEquippedItems(dragon, p_77029_2_);
////        Tessellator tessellator = Tessellator.instance;
////
////        if (dragon.deathTicks > 0) {
////            RenderHelper.disableStandardItemLighting();
////            float f1 = ((float) dragon.deathTicks + p_77029_2_) / 200.0F;
////            float f2 = 0.0F;
////
////            if (f1 > 0.8F) {
////                f2 = (f1 - 0.8F) / 0.2F;
////            }
////
////            if (dragon instanceof EntityChaosGuardian) f2 = 0;
////
////            Random random = new Random(1L);
////            GL11.glDisable(GL11.GL_TEXTURE_2D);
////            GL11.glShadeModel(GL11.GL_SMOOTH);
////            GL11.glEnable(GL11.GL_BLEND);
////            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
////            GL11.glDisable(GL11.GL_ALPHA_TEST);
////            GL11.glEnable(GL11.GL_CULL_FACE);
////            GL11.glDepthMask(false);
////            GL11.glPushMatrix();
////            GL11.glTranslatef(0.0F, -1.0F, -2.0F);
////
////            for (int i = 0; (float) i < (f1 + f1 * f1) / 2.0F * 60.0F; ++i) {
////                GL11.glRotatef(random.nextFloat() * 360.0F, 1.0F, 0.0F, 0.0F);
////                GL11.glRotatef(random.nextFloat() * 360.0F, 0.0F, 1.0F, 0.0F);
////                GL11.glRotatef(random.nextFloat() * 360.0F, 0.0F, 0.0F, 1.0F);
////                GL11.glRotatef(random.nextFloat() * 360.0F, 1.0F, 0.0F, 0.0F);
////                GL11.glRotatef(random.nextFloat() * 360.0F, 0.0F, 1.0F, 0.0F);
////                GL11.glRotatef(random.nextFloat() * 360.0F + f1 * 90.0F, 0.0F, 0.0F, 1.0F);
////                tessellator.startDrawing(6);
////                float f3 = random.nextFloat() * 20.0F + 5.0F + f2 * 10.0F;
////                float f4 = random.nextFloat() * 2.0F + 1.0F + f2 * 2.0F;
////                tessellator.setColorRGBA_I(16777215, (int) (255.0F * (1.0F - f2)));
////                tessellator.addVertex(0.0D, 0.0D, 0.0D);
////                tessellator.setColorRGBA_I(16711935, 0);
////                tessellator.addVertex(-0.866D * (double) f4, (double) f3, (double) (-0.5F * f4));
////                tessellator.addVertex(0.866D * (double) f4, (double) f3, (double) (-0.5F * f4));
////                tessellator.addVertex(0.0D, (double) f3, (double) (1.0F * f4));
////                tessellator.addVertex(-0.866D * (double) f4, (double) f3, (double) (-0.5F * f4));
////                tessellator.draw();
////            }
////
////            GL11.glPopMatrix();
////            GL11.glDepthMask(true);
////            GL11.glDisable(GL11.GL_CULL_FACE);
////            GL11.glDisable(GL11.GL_BLEND);
////            GL11.glShadeModel(GL11.GL_FLAT);
////            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
////            GL11.glEnable(GL11.GL_TEXTURE_2D);
////            GL11.glEnable(GL11.GL_ALPHA_TEST);
////            RenderHelper.enableStandardItemLighting();
////        }
////    }
////
////    protected int shouldRenderPass(EntityDragon p_77032_1_, int p_77032_2_, float p_77032_3_) {
////        if (p_77032_2_ == 1) {
////            GL11.glDepthFunc(GL11.GL_LEQUAL);
////        }
////
////        if (p_77032_2_ != 0) {
////            return -1;
////        } else {
////            this.bindTexture(enderDragonEyesTextures);
////            GL11.glEnable(GL11.GL_BLEND);
////            GL11.glDisable(GL11.GL_ALPHA_TEST);
////            GL11.glBlendFunc(GL11.GL_ONE, GL11.GL_ONE);
////            GL11.glDisable(GL11.GL_LIGHTING);
////            GL11.glDepthFunc(GL11.GL_EQUAL);
////            char c0 = 61680;
////            int j = c0 % 65536;
////            int k = c0 / 65536;
////            OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, (float) j / 1.0F, (float) k / 1.0F);
////            GL11.glEnable(GL11.GL_LIGHTING);
////            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
////            return 1;
////        }
////    }
//
//    public static class LayerChaosGuardianEyes implements LayerRenderer<EntityChaosGuardian> {
//        private static final ResourceLocation TEXTURE = new ResourceLocation("textures/entity/enderdragon/dragon_eyes.png");
//        private final RenderChaosGuardian dragonRenderer;
//
//        public LayerChaosGuardianEyes(RenderChaosGuardian dragonRendererIn) {
//            this.dragonRenderer = dragonRendererIn;
//        }
//
//        public void doRenderLayer(EntityChaosGuardian entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
//            this.dragonRenderer.bindTexture(TEXTURE);
//            RenderSystem.enableBlend();
//            RenderSystem.disableAlpha();
//            RenderSystem.blendFunc(RenderSystem.SourceFactor.ONE, RenderSystem.DestFactor.ONE);
//            RenderSystem.disableLighting();
//            RenderSystem.depthFunc(514);
//            int i = 61680;
//            int j = i % 65536;
//            int k = i / 65536;
//            OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, (float) j, (float) k);
//            RenderSystem.enableLighting();
//            RenderSystem.color(1.0F, 1.0F, 1.0F, 1.0F);
//            this.dragonRenderer.getMainModel().render(entitylivingbaseIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
//            this.dragonRenderer.setLightmap(entitylivingbaseIn);
//            RenderSystem.disableBlend();
//            RenderSystem.enableAlpha();
//            RenderSystem.depthFunc(515);
//        }
//
//        public boolean shouldCombineTextures() {
//            return false;
//        }
//    }
//}
//
