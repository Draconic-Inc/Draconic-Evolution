package com.brandon3055.draconicevolution.client.render.entity;

import com.brandon3055.draconicevolution.client.model.ModelContributorWings;
import com.brandon3055.draconicevolution.handlers.ContributorHandler;
import com.brandon3055.draconicevolution.handlers.ContributorHandler.Contributor;
import com.brandon3055.draconicevolution.helpers.ResourceHelperDE;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public class LayerContributorWings implements LayerRenderer<AbstractClientPlayer> {
    private final RenderPlayer renderPlayer;
    private ModelContributorWings modelWings = new ModelContributorWings();
    protected static final ResourceLocation ENCHANTED_ITEM_GLINT_RES = new ResourceLocation("textures/misc/enchanted_item_glint.png");

    public LayerContributorWings(RenderPlayer renderPlayerIn) {
        this.renderPlayer = renderPlayerIn;
    }

    public void doRenderLayer(AbstractClientPlayer player, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        if (!ContributorHandler.isPlayerContributor(player)) {
            return;
        }

        Contributor contributor = ContributorHandler.contributors.get(player.getName());

        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

        if (contributor.contributionLevel >= 1 && contributor.contributorWingsEnabled) {
            GlStateManager.disableBlend();
            ResourceHelperDE.bindTexture("textures/models/contributor_wings.png");

            GlStateManager.pushMatrix();
            GlStateManager.translate(0.0F, 0.0F, 0.125F);
            this.modelWings.setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale, player);
            this.modelWings.render(player, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
            renderEnchantedGlint(this.renderPlayer, player, this.modelWings, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch, scale);
            GlStateManager.popMatrix();
        }

        if (contributor.contribution != null && contributor.contribution.toLowerCase().contains("patreon") && contributor.patreonBadgeEnabled) {
            renderBadge(player);
        }
    }

    public static void renderEnchantedGlint(RenderLivingBase<?> livingBase, EntityLivingBase entity, ModelBase model, float p_188364_3_, float p_188364_4_, float p_188364_5_, float p_188364_6_, float p_188364_7_, float p_188364_8_, float p_188364_9_) {
        float f = (float) entity.ticksExisted + p_188364_5_;
        livingBase.bindTexture(ENCHANTED_ITEM_GLINT_RES);
        GlStateManager.enableBlend();
        GlStateManager.depthFunc(514);
        GlStateManager.depthMask(false);
        GlStateManager.color(0.5F, 0.5F, 0.5F, 1.0F);

        for (int i = 0; i < 2; ++i) {
            GlStateManager.disableLighting();
            GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_COLOR, GlStateManager.DestFactor.ONE);
            float f11 = 0.76F;
            GlStateManager.color(0.9F * f11, 0.8F * f11, 0.1F * f11, 1.0F);
            GlStateManager.matrixMode(5890);
            GlStateManager.loadIdentity();
            GlStateManager.scale(0.23333334F, 0.23333334F, 0.23333334F);
            GlStateManager.rotate(30.0F - (float) i * 60.0F, 0.0F, 0.0F, 1.0F);
            GlStateManager.translate(0F, f * (0.003F + (float) i * 0.003F) * -20.0F, 0F);
            GlStateManager.matrixMode(5888);
            model.render(entity, 0, p_188364_4_, p_188364_6_, p_188364_7_, p_188364_8_, p_188364_9_);
        }

        GlStateManager.matrixMode(5890);
        GlStateManager.loadIdentity();
        GlStateManager.matrixMode(5888);
        GlStateManager.enableLighting();
        GlStateManager.depthMask(true);
        GlStateManager.depthFunc(515);
        GlStateManager.disableBlend();
    }

    public boolean shouldCombineTextures() {
        return false;
    }


    public void renderBadge(AbstractClientPlayer entitylivingbaseIn) {
        if (entitylivingbaseIn.isElytraFlying()) {
            return;
        }

        ResourceHelperDE.bindTexture("textures/special/patreon_badge.png");
        Tessellator tess = Tessellator.getInstance();
        GlStateManager.pushMatrix();

        if (entitylivingbaseIn.isSneaking()) {
            GlStateManager.rotate(29.0F, 1.0F, 0.0F, 0F);
            GlStateManager.translate(0, 0.15, -0.1);
        }

        double x = 0.01;
        double y = 0.04;
        double z = -0.13;
        double xSize = 0.22;
        double ySize = 0.22;

        VertexBuffer buffer = tess.getBuffer();
        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
        buffer.pos(x, y, z).tex(0, 0).endVertex();
        buffer.pos(x, y + ySize, z).tex(0, 1).endVertex();
        buffer.pos(x + xSize, y + ySize, z).tex(1, 1).endVertex();
        buffer.pos(x + xSize, y, z).tex(1, 0).endVertex();
        tess.draw();

        GlStateManager.depthFunc(GL11.GL_EQUAL);
        GlStateManager.disableLighting();
        renderPlayer.bindTexture(ENCHANTED_ITEM_GLINT_RES);
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GL11.GL_SRC_COLOR, GL11.GL_ONE);
        float f11 = 0.76F;
        GlStateManager.color(0.9F * f11, 0.8F * f11, 0.1F * f11, 1.0F);
        GlStateManager.matrixMode(GL11.GL_TEXTURE);
        GlStateManager.pushMatrix();

        float f12 = 0.125F;
        GlStateManager.scale(f12, f12, f12);
        float f13 = (float) (Minecraft.getSystemTime() % 3000L) / 3000.0F * 8.0F;
        GlStateManager.translate(f13, 0.0F, 0.0F);
        GL11.glRotatef(-50.0F, 0.0F, 0.0F, 1.0F);

        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
        buffer.pos(x, y, z).tex(0, 0).endVertex();
        buffer.pos(x, y + ySize, z).tex(0, 1).endVertex();
        buffer.pos(x + xSize, y + ySize, z).tex(1, 1).endVertex();
        buffer.pos(x + xSize, y, z).tex(1, 0).endVertex();
        tess.draw();

        GL11.glPopMatrix();
        GL11.glPushMatrix();
        GL11.glScalef(f12, f12, f12);
        f13 = (float) (Minecraft.getSystemTime() % 4873L) / 4873.0F * 8.0F;
        GL11.glTranslatef(-f13, 0.0F, 0.0F);
        GL11.glRotatef(10.0F, 0.0F, 0.0F, 1.0F);

        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
        buffer.pos(x, y, z).tex(0, 0).endVertex();
        buffer.pos(x, y + ySize, z).tex(0, 1).endVertex();
        buffer.pos(x + xSize, y + ySize, z).tex(1, 1).endVertex();
        buffer.pos(x + xSize, y, z).tex(1, 0).endVertex();
        tess.draw();

        GlStateManager.popMatrix();
        GlStateManager.matrixMode(GL11.GL_MODELVIEW);
        GlStateManager.disableBlend();
        GlStateManager.enableLighting();
        GlStateManager.depthFunc(GL11.GL_LEQUAL);

        GlStateManager.popMatrix();
    }
}