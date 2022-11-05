package com.brandon3055.draconicevolution.client;

import codechicken.lib.data.MCDataInput;
import codechicken.lib.render.buffer.TransformingVertexConsumer;
import codechicken.lib.render.shader.ShaderProgram;
import codechicken.lib.render.shader.ShaderProgramBuilder;
import com.brandon3055.brandonscore.api.TimeKeeper;
import com.brandon3055.draconicevolution.DEConfig;
import com.brandon3055.draconicevolution.DraconicEvolution;
import com.brandon3055.draconicevolution.client.render.entity.DraconicGuardianRenderer;
import com.google.common.collect.Maps;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.LerpingBossEvent;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.BossEvent;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.common.MinecraftForge;

import java.util.Map;
import java.util.UUID;

/**
 * Created by brandon3055 on 24/7/21
 */
public class CustomBossInfoHandler extends GuiComponent {
    private static final ResourceLocation GUI_BARS_LOCATION = new ResourceLocation("textures/gui/bars.png");
    private static final Map<UUID, BossShieldInfo> events = Maps.newLinkedHashMap();

    private static final ResourceLocation ENDER_CRYSTAL_TEXTURES = new ResourceLocation(DraconicEvolution.MODID, "textures/entity/guardian_crystal.png");
    private static final RenderType RENDER_TYPE = RenderType.entityCutoutNoCull(ENDER_CRYSTAL_TEXTURES);
    private static final float SIN_45 = (float) Math.sin((Math.PI / 4D));

    private static final ModelPart glass;
    private static final ModelPart cube;

    static {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();
        partdefinition.addOrReplaceChild("glass", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -4.0F, -4.0F, 8.0F, 8.0F, 8.0F), PartPose.ZERO);
        partdefinition.addOrReplaceChild("cube", CubeListBuilder.create().texOffs(32, 0).addBox(-4.0F, -4.0F, -4.0F, 8.0F, 8.0F, 8.0F), PartPose.ZERO);
        ModelPart modelRoot = LayerDefinition.create(meshdefinition, 64, 32).bakeRoot();
        glass = modelRoot.getChild("glass");
        cube = modelRoot.getChild("cube");
    }

    public static void init() {
        MinecraftForge.EVENT_BUS.addListener(CustomBossInfoHandler::onClientDisconnect);
        MinecraftForge.EVENT_BUS.addListener(CustomBossInfoHandler::preDrawBossInfo);
    }

    public static void preDrawBossInfo(RenderGameOverlayEvent.BossInfo event) {
        if (event.getType() != RenderGameOverlayEvent.ElementType.BOSSINFO) return;
        LerpingBossEvent info = event.getBossEvent();
        if (!events.containsKey(info.getId())) return;
        event.setCanceled(true);
        BossShieldInfo shieldInfo = events.get(info.getId());
        Minecraft mc = Minecraft.getInstance();
        PoseStack matrixStack = event.getMatrixStack();

        int width = event.getWindow().getGuiScaledWidth();
        int x = event.getX();
        int y = event.getY();

        RenderSystem.enableDepthTest();
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, GUI_BARS_LOCATION);
        drawBar(matrixStack, x, y, info);

        float shield = shieldInfo.isImmune() ? 1 : shieldInfo.getShield();
        MultiBufferSource.BufferSource getter = Minecraft.getInstance().renderBuffers().bufferSource();//IRenderTypeBuffer.immediate(Tessellator.getInstance().getBuilder());

        if (DEConfig.guardianShaders) {
            if (shieldInfo.isImmune()) {
                DEShaders.shieldColour.glUniform4f(0F, 1F, 1F, 2F);
            } else {
                DEShaders.shieldColour.glUniform4f(1F, 0F, 0F, 2F);
            }
            DEShaders.shieldBarMode.glUniform1i(1);
            DEShaders.shieldActivation.glUniform1f(shield);
            VertexConsumer builder = new TransformingVertexConsumer(getter.getBuffer(DraconicGuardianRenderer.SHIELD_TYPE), matrixStack);
            drawShieldRect(builder, x, y, 182, 6);
            getter.endBatch();
        }

        if (shieldInfo.crystals > 0) {
            Component countText = new TextComponent("x" + shieldInfo.crystals);
            int countWidth = mc.font.width(countText);

            float anim = (TimeKeeper.getClientTick() + event.getPartialTicks()) * 3.0F;
            VertexConsumer ivertexbuilder = getter.getBuffer(RENDER_TYPE);
            matrixStack.pushPose();
            matrixStack.translate(x + 182 - countWidth - 8, y - 6, 0.0D);
            matrixStack.scale(14.0F, 14.0F, 14.0F);
            int i = OverlayTexture.NO_OVERLAY;
            matrixStack.mulPose(Vector3f.YP.rotationDegrees(anim));
            matrixStack.mulPose(new Quaternion(new Vector3f(SIN_45, 0.0F, SIN_45), 60.0F, true));
            glass.render(matrixStack, ivertexbuilder, 240, i);
            matrixStack.scale(0.875F, 0.875F, 0.875F);
            matrixStack.mulPose(new Quaternion(new Vector3f(SIN_45, 0.0F, SIN_45), 60.0F, true));
            matrixStack.mulPose(Vector3f.YP.rotationDegrees(anim));
            glass.render(matrixStack, ivertexbuilder, 240, i);
            matrixStack.scale(0.875F, 0.875F, 0.875F);
            matrixStack.mulPose(new Quaternion(new Vector3f(SIN_45, 0.0F, SIN_45), 60.0F, true));
            matrixStack.mulPose(Vector3f.YP.rotationDegrees(anim));
            cube.render(matrixStack, ivertexbuilder, 240, i);
            matrixStack.popPose();
            getter.endBatch();

            if (DEConfig.guardianShaders) {
                DEShaders.shieldColour.glUniform4f(1F, 0F, 0F, 1.5F);
                DEShaders.shieldActivation.glUniform1f(1F);
                VertexConsumer shaderBuilder = getter.getBuffer(DraconicGuardianRenderer.SHIELD_TYPE);
                matrixStack.pushPose();
                matrixStack.translate(x + 182 - countWidth - 8, y - 6, 0.0D);
                matrixStack.scale(14.0F, 14.0F, 14.0F);
                matrixStack.mulPose(Vector3f.YP.rotationDegrees(anim));
                matrixStack.mulPose(new Quaternion(new Vector3f(SIN_45, 0.0F, SIN_45), 60.0F, true));
                glass.render(matrixStack, shaderBuilder, 240, i);
                matrixStack.scale(0.875F, 0.875F, 0.875F);
                matrixStack.mulPose(new Quaternion(new Vector3f(SIN_45, 0.0F, SIN_45), 60.0F, true));
                matrixStack.mulPose(Vector3f.YP.rotationDegrees(anim));
                glass.render(matrixStack, shaderBuilder, 240, i);
                matrixStack.scale(0.875F, 0.875F, 0.875F);
                matrixStack.mulPose(new Quaternion(new Vector3f(SIN_45, 0.0F, SIN_45), 60.0F, true));
                matrixStack.mulPose(Vector3f.YP.rotationDegrees(anim));
                cube.render(matrixStack, shaderBuilder, 240, i);
                matrixStack.popPose();
                getter.endBatch();
            }

            mc.font.drawShadow(matrixStack, new TextComponent("x" + shieldInfo.crystals), x + 182 - countWidth, (float) y - 9, 0xffFFFF);
        }

        Component itextcomponent = info.getName();
        int stringWidth = mc.font.width(itextcomponent);
        int stringX = shieldInfo.crystals > 0 ? x : width / 2 - stringWidth / 2;
        int stringY = y - 9;
        matrixStack.translate(0, 0, 16);
        mc.font.drawShadow(matrixStack, itextcomponent, (float) stringX, (float) stringY, 0xff0000);
    }

    private static void drawBar(PoseStack matrixStack, int x, int y, BossEvent info) {
        drawRect(matrixStack, x, y, 0, info.getColor().ordinal() * 5 * 2, 182, 5);
        if (info.getOverlay() != BossEvent.BossBarOverlay.PROGRESS) {
            drawRect(matrixStack, x, y, 0, 80 + (info.getOverlay().ordinal() - 1) * 5 * 2, 182, 5);
        }

        int i = (int) (info.getProgress() * 183.0F);
        if (i > 0) {
            drawRect(matrixStack, x, y, 0, info.getColor().ordinal() * 5 * 2 + 5, i, 5);
            if (info.getOverlay() != BossEvent.BossBarOverlay.PROGRESS) {
                drawRect(matrixStack, x, y, 0, 80 + (info.getOverlay().ordinal() - 1) * 5 * 2 + 5, i, 5);
            }
        }
    }

    private static void onClientDisconnect(ClientPlayerNetworkEvent.LoggedOutEvent event) {
        events.clear();
    }

    public static void handlePacket(MCDataInput input) {
        UUID id = input.readUUID();
        int op = input.readByte();
//        LOGGER.info("Packet, OP: " + op + ". ID: " + id + ", Exists: " + events.containsKey(id));
        switch (op) {
            case 0: //Add
                events.put(id, new BossShieldInfo(input.readFloat(), input.readByte(), input.readBoolean()));
                break;
            case 1: //Remove
                events.remove(id);
                break;
            case 2: //Update Shield
                events.get(id).setShield(input.readFloat());
                break;
            case 3: //Update Crystals
                events.get(id).setCrystals(input.readByte());
                break;
            case 4: //Update Immune
                events.get(id).setImmune(input.readBoolean());
                break;
        }
    }

    public static void drawRect(PoseStack stack, int x, int y, int u, int v, int width, int height) {
        blit(stack, x, y, 0, (float) u, (float) v, width, height, 256, 256);
    }

    public static void drawShieldRect(VertexConsumer builder, int x, int y, int width, int height) {
        drawQuad(builder, x, x + width, y, y + height, 0, 0F, 1F, 0F, 1F);
    }

    private static void drawQuad(VertexConsumer builder, int x, int xMax, int y, int yMax, int z, float u, float uMax, float v, float vMax) {
        builder.vertex((float) x, (float) yMax, (float) z).uv(u, vMax).endVertex();
        builder.vertex((float) xMax, (float) yMax, (float) z).uv(uMax, vMax).endVertex();
        builder.vertex((float) xMax, (float) y, (float) z).uv(uMax, v).endVertex();
        builder.vertex((float) x, (float) y, (float) z).uv(u, v).endVertex();
    }

    public static class BossShieldInfo {
        protected float lastPower;
        protected float targetPower;
        protected long setTime;
        protected int crystals;
        protected boolean immune;

        public BossShieldInfo(float power, int crystals, boolean immune) {
            this.lastPower = power;
            this.targetPower = power;
            this.crystals = crystals;
            this.immune = immune;
            this.setTime = Util.getMillis();
        }

        public BossShieldInfo setShield(float power) {
            this.lastPower = this.getShield();
            this.targetPower = power;
            this.setTime = Util.getMillis();
            return this;
        }

        public float getShield() {
            long i = Util.getMillis() - this.setTime;
            float f = Mth.clamp((float) i / 100.0F, 0.0F, 1.0F);
            return Mth.lerp(f, this.lastPower, this.targetPower);
        }

        public void setCrystals(int crystals) {
            this.crystals = crystals;
        }

        public int getCrystals() {
            return crystals;
        }

        public void setImmune(boolean immune) {
            this.immune = immune;
        }

        public boolean isImmune() {
            return immune;
        }
    }
}
