//package com.brandon3055.draconicevolution.client.render.entity;
//
//import com.brandon3055.draconicevolution.client.model.ModelContributorWings;
//import com.brandon3055.draconicevolution.handlers.ContributorHandler;
//import com.brandon3055.draconicevolution.handlers.ContributorHandler.Contributor;
//import com.brandon3055.draconicevolution.helpers.ResourceHelperDE;
//import com.mojang.blaze3d.platform.GlStateManager;
//import net.minecraft.client.Minecraft;
//import net.minecraft.client.entity.AbstractClientPlayer;
//import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
//import net.minecraft.client.model.ModelBase;
//import net.minecraft.client.renderer.GlStateManager;
//import net.minecraft.client.renderer.Tessellator;
//import net.minecraft.client.renderer.BufferBuilder;
//import net.minecraft.client.renderer.entity.PlayerRenderer;
//import net.minecraft.client.renderer.entity.RenderLivingBase;
//import net.minecraft.client.renderer.entity.RenderPlayer;
//import net.minecraft.client.renderer.entity.layers.LayerRenderer;
//import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
//import net.minecraft.entity.LivingEntity;
//import net.minecraft.entity.player.EnumPlayerModelParts;
//import net.minecraft.util.ResourceLocation;
//import net.minecraftforge.api.distmarker.Dist;
//import net.minecraftforge.api.distmarker.OnlyIn;
//import net.minecraftforge.fml.relauncher.Side;
//import net.minecraftforge.fml.relauncher.SideOnly;
//import org.lwjgl.opengl.GL11;
//
//@OnlyIn(Dist.CLIENT)
//public class LayerContributorPerkRenderer implements LayerRenderer<AbstractClientPlayerEntity> {
//    private final PlayerRenderer renderPlayer;
//    private ModelContributorWings modelWings = new ModelContributorWings();
//    protected static final ResourceLocation ENCHANTED_ITEM_GLINT_RES = new ResourceLocation("textures/misc/enchanted_item_glint.png");
//
//    public LayerContributorPerkRenderer(RenderPlayer renderPlayerIn) {
//        this.renderPlayer = renderPlayerIn;
//    }
//
//    public void doRenderLayer(AbstractClientPlayer player, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
//        if (!ContributorHandler.isPlayerContributor(player)) {
//            return;
//        }
//
//        Contributor contributor = ContributorHandler.contributors.get(player.getName());
//
//        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
//
//        if (contributor.hasWings && contributor.contributorWingsEnabled) {
//            GlStateManager.disableBlend();
//            ResourceHelperDE.bindTexture("textures/models/contributor_wings.png");
//
//            GlStateManager.pushMatrix();
//            GlStateManager.translate(0.0F, 0.0F, 0.125F);
//            this.modelWings.setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale, player);
//            this.modelWings.render(player, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
//            renderEnchantedGlint(this.renderPlayer, player, this.modelWings, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch, scale);
//            GlStateManager.popMatrix();
//        }
//
//        if (contributor.isPatreonSupporter && contributor.patreonBadgeEnabled) {
//            renderBadge(player);
//        }
//
//        if (contributor.isLolnetContributor && contributor.lolnetBadgeEnabled) {
//            renderLolnetBadge(player, contributor.isPatreonSupporter && contributor.patreonBadgeEnabled);
//        }
//
//        GlStateManager.color(1F, 1F, 1F, 1.0F);
//    }
//
//    public static void renderEnchantedGlint(RenderLivingBase<?> livingBase, LivingEntity entity, ModelBase model, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
//        float f = (float) entity.ticksExisted + partialTicks;
//        livingBase.bindTexture(ENCHANTED_ITEM_GLINT_RES);
//        GlStateManager.enableBlend();
//        GlStateManager.depthFunc(514);
//        GlStateManager.depthMask(false);
//        GlStateManager.color4f(0.5F, 0.5F, 0.5F, 1.0F);
//
//        for (int i = 0; i < 2; ++i) {
//            GlStateManager.disableLighting();
//            GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_COLOR, GlStateManager.DestFactor.ONE);
//            float f11 = 0.76F;
//            GlStateManager.color4f(0.9F * f11, 0.8F * f11, 0.1F * f11, 1.0F);
//            GlStateManager.matrixMode(5890);
//            GlStateManager.loadIdentity();
//            GlStateManager.scalef(0.23333334F, 0.23333334F, 0.23333334F);
//            GlStateManager.rotatef(30.0F - (float) i * 60.0F, 0.0F, 0.0F, 1.0F);
//            GlStateManager.translatef(0F, f * (0.003F + (float) i * 0.003F) * -20.0F, 0F);
//            GlStateManager.matrixMode(5888);
//            model.render(entity, 0, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
//        }
//
//        GlStateManager.matrixMode(5890);
//        GlStateManager.loadIdentity();
//        GlStateManager.matrixMode(5888);
//        GlStateManager.enableLighting();
//        GlStateManager.depthMask(true);
//        GlStateManager.depthFunc(515);
//        GlStateManager.disableBlend();
//    }
//
//    public boolean shouldCombineTextures() {
//        return false;
//    }
//
//
//    public void renderBadge(AbstractClientPlayer player) {
//        ResourceHelperDE.bindTexture("textures/special/patreon_badge.png");
//        Tessellator tess = Tessellator.getInstance();
//        GlStateManager.pushMatrix();
//
//        if (player.isSneaking()) {
//            GlStateManager.rotate(29.0F, 1.0F, 0.0F, 0F);
//            GlStateManager.translate(0, 0.15, -0.1);
//        }
//
//        double x = 0.01;
//        double y = 0.04;
//        double z = -0.13 - (player.isWearing(EnumPlayerModelParts.JACKET) ? 0.02 : 0);
//        double xSize = 0.22;
//        double ySize = 0.22;
//
//        GlStateManager.color(1F, 1F, 1F, 1F);
//        GlStateManager.enableBlend();
//        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
//        GlStateManager.alphaFunc(516, 0.003921569F);
//
//        BufferBuilder buffer = tess.getBuffer();
//        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
//        buffer.pos(x, y, z).tex(0, 0).endVertex();
//        buffer.pos(x, y + ySize, z).tex(0, 1).endVertex();
//        buffer.pos(x + xSize, y + ySize, z).tex(1, 1).endVertex();
//        buffer.pos(x + xSize, y, z).tex(1, 0).endVertex();
//        tess.draw();
//
//        GlStateManager.alphaFunc(516, 0.1F);
//
//        GlStateManager.depthFunc(GL11.GL_EQUAL);
//        GlStateManager.disableLighting();
//        renderPlayer.bindTexture(ENCHANTED_ITEM_GLINT_RES);
//        GlStateManager.enableBlend();
//        GlStateManager.blendFunc(GL11.GL_SRC_COLOR, GL11.GL_ONE);
//        float f11 = 0.76F;
//        GlStateManager.color(0.9F * f11, 0.8F * f11, 0.1F * f11, 1.0F);
//        GlStateManager.matrixMode(GL11.GL_TEXTURE);
//        GlStateManager.pushMatrix();
//
//        float f12 = 0.125F;
//        GlStateManager.scale(f12, f12, f12);
//        float f13 = (float) (Minecraft.getSystemTime() % 3000L) / 3000.0F * 8.0F;
//        GlStateManager.translate(f13, 0.0F, 0.0F);
//        GL11.glRotatef(-50.0F, 0.0F, 0.0F, 1.0F);
//
//        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
//        buffer.pos(x, y, z).tex(0, 0).endVertex();
//        buffer.pos(x, y + ySize, z).tex(0, 1).endVertex();
//        buffer.pos(x + xSize, y + ySize, z).tex(1, 1).endVertex();
//        buffer.pos(x + xSize, y, z).tex(1, 0).endVertex();
//        tess.draw();
//
//        GL11.glPopMatrix();
//        GL11.glPushMatrix();
//        GL11.glScalef(f12, f12, f12);
//        f13 = (float) (Minecraft.getSystemTime() % 4873L) / 4873.0F * 8.0F;
//        GL11.glTranslatef(-f13, 0.0F, 0.0F);
//        GL11.glRotatef(10.0F, 0.0F, 0.0F, 1.0F);
//
//        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
//        buffer.pos(x, y, z).tex(0, 0).endVertex();
//        buffer.pos(x, y + ySize, z).tex(0, 1).endVertex();
//        buffer.pos(x + xSize, y + ySize, z).tex(1, 1).endVertex();
//        buffer.pos(x + xSize, y, z).tex(1, 0).endVertex();
//        tess.draw();
//
//        GlStateManager.popMatrix();
//        GlStateManager.matrixMode(GL11.GL_MODELVIEW);
//        GlStateManager.disableBlend();
//        GlStateManager.enableLighting();
//        GlStateManager.depthFunc(GL11.GL_LEQUAL);
//
//        GlStateManager.popMatrix();
//    }
//
//    public void renderLolnetBadge(AbstractClientPlayer player, boolean offset) {
//        ResourceHelperDE.bindTexture("textures/special/lolnet_badge.png");
//        Tessellator tess = Tessellator.getInstance();
//        GlStateManager.pushMatrix();
//
//        if (player.isSneaking()) {
//            GlStateManager.rotate(29.0F, 1.0F, 0.0F, 0F);
//            GlStateManager.translate(0, 0.15, -0.1);
//        }
//
//        double x = 0.01;
//        double y = 0.04 + (offset ? 0.25 : 0);
//        double z = -0.13 - (player.isWearing(EnumPlayerModelParts.JACKET) ? 0.02 : 0);
//        double xSize = 0.22;
//        double ySize = 0.22;
//
//        GlStateManager.color(1F, 1F, 1F, 1F);
//        GlStateManager.enableBlend();
//        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
//        GlStateManager.alphaFunc(516, 0.003921569F);
//
//        BufferBuilder buffer = tess.getBuffer();
//        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
//        buffer.pos(x, y, z).tex(0, 0).endVertex();
//        buffer.pos(x, y + ySize, z).tex(0, 1).endVertex();
//        buffer.pos(x + xSize, y + ySize, z).tex(1, 1).endVertex();
//        buffer.pos(x + xSize, y, z).tex(1, 0).endVertex();
//        tess.draw();
//
//        GlStateManager.depthFunc(GL11.GL_EQUAL);
//        GlStateManager.disableLighting();
//        renderPlayer.bindTexture(ENCHANTED_ITEM_GLINT_RES);
//        GlStateManager.enableBlend();
//        GlStateManager.blendFunc(GL11.GL_SRC_COLOR, GL11.GL_ONE);
//        float f11 = 0.76F;
//        GlStateManager.color(0.9F * f11, 0.8F * f11, 0.1F * f11, 1.0F);
//        GlStateManager.matrixMode(GL11.GL_TEXTURE);
//        GlStateManager.pushMatrix();
//
//        float f12 = 0.125F;
//        GlStateManager.scale(f12, f12, f12);
//        float f13 = (float) ((Minecraft.getSystemTime() + 4352) % 3000L) / 3000.0F * 8.0F;
//        GlStateManager.translate(f13, 0.0F, 0.0F);
//        GL11.glRotatef(-50.0F, 0.0F, 0.0F, 1.0F);
//
//        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
//        buffer.pos(x, y, z).tex(0, 0).endVertex();
//        buffer.pos(x, y + ySize, z).tex(0, 1).endVertex();
//        buffer.pos(x + xSize, y + ySize, z).tex(1, 1).endVertex();
//        buffer.pos(x + xSize, y, z).tex(1, 0).endVertex();
//        tess.draw();
//
//        GL11.glPopMatrix();
//        GL11.glPushMatrix();
//        GL11.glScalef(f12, f12, f12);
//        f13 = (float) (Minecraft.getSystemTime() % 4873L) / 4873.0F * 8.0F;
//        GL11.glTranslatef(-f13, 0.0F, 0.0F);
//        GL11.glRotatef(10.0F, 0.0F, 0.0F, 1.0F);
//
//        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
//        buffer.pos(x, y, z).tex(0, 0).endVertex();
//        buffer.pos(x, y + ySize, z).tex(0, 1).endVertex();
//        buffer.pos(x + xSize, y + ySize, z).tex(1, 1).endVertex();
//        buffer.pos(x + xSize, y, z).tex(1, 0).endVertex();
//        tess.draw();
//
//        GlStateManager.popMatrix();
//        GlStateManager.matrixMode(GL11.GL_MODELVIEW);
//        GlStateManager.disableBlend();
//        GlStateManager.enableLighting();
//        GlStateManager.depthFunc(GL11.GL_LEQUAL);
//
//        GlStateManager.popMatrix();
//    }
//}