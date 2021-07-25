package com.brandon3055.draconicevolution.client;

import codechicken.lib.data.MCDataInput;
import codechicken.lib.render.buffer.TransformingVertexBuilder;
import codechicken.lib.render.shader.*;
import com.brandon3055.brandonscore.api.TimeKeeper;
import com.brandon3055.brandonscore.client.BCClientEventHandler;
import com.brandon3055.draconicevolution.DraconicEvolution;
import com.brandon3055.draconicevolution.client.render.entity.DraconicGuardianRenderer;
import com.google.common.collect.Maps;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.ClientBossInfo;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.BossInfo;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.WorldEvent;

import java.util.Map;
import java.util.UUID;

import static codechicken.lib.render.shader.ShaderObject.StandardShaderType.FRAGMENT;
import static codechicken.lib.render.shader.ShaderObject.StandardShaderType.VERTEX;
import static com.brandon3055.draconicevolution.DraconicEvolution.LOGGER;
import static com.brandon3055.draconicevolution.DraconicEvolution.MODID;

/**
 * Created by brandon3055 on 24/7/21
 */
public class CustomBossInfoHandler extends AbstractGui {
    private static final ResourceLocation GUI_BARS_LOCATION = new ResourceLocation("textures/gui/bars.png");
    private static final Map<UUID, BossShieldInfo> events = Maps.newLinkedHashMap();

    private static final ResourceLocation ENDER_CRYSTAL_TEXTURES = new ResourceLocation(DraconicEvolution.MODID, "textures/entity/guardian_crystal.png");
    private static final RenderType RENDER_TYPE = RenderType.entityCutoutNoCull(ENDER_CRYSTAL_TEXTURES);
    private static final float SIN_45 = (float) Math.sin((Math.PI / 4D));
    private static final ModelRenderer cube;
    private static final ModelRenderer glass;
    private static final ModelRenderer base;
    static {
        glass = new ModelRenderer(64, 32, 0, 0);
        glass.addBox(-4.0F, -4.0F, -4.0F, 8.0F, 8.0F, 8.0F);
        cube = new ModelRenderer(64, 32, 32, 0);
        cube.addBox(-4.0F, -4.0F, -4.0F, 8.0F, 8.0F, 8.0F);
        base = new ModelRenderer(64, 32, 0, 16);
        base.addBox(-6.0F, 0.0F, -6.0F, 12.0F, 4.0F, 12.0F);
    }

    public static ShaderProgram shieldShader = ShaderProgramBuilder.builder()
            .addShader("vert", shader -> shader
                    .type(VERTEX)
                    .source(new ResourceLocation(MODID, "shaders/guardian_shield.vert"))
            )
            .addShader("frag", shader -> shader
                    .type(FRAGMENT)
                    .source(new ResourceLocation(MODID, "shaders/guardian_shield.frag"))
                    .uniform("time", UniformType.FLOAT)
                    .uniform("baseColour", UniformType.VEC4)
                    .uniform("activation", UniformType.FLOAT)

            )
            .whenUsed(cache -> cache.glUniform1f("time", (BCClientEventHandler.elapsedTicks + Minecraft.getInstance().getFrameTime()) / 20))
            .build();

    public static void init() {
        MinecraftForge.EVENT_BUS.addListener(CustomBossInfoHandler::onClientDisconnect);
        MinecraftForge.EVENT_BUS.addListener(CustomBossInfoHandler::preDrawBossInfo);
    }

    public static void preDrawBossInfo(RenderGameOverlayEvent.BossInfo event) {
        if (event.getType() != RenderGameOverlayEvent.ElementType.BOSSINFO) return;
        ClientBossInfo info = event.getBossInfo();
        if (!events.containsKey(info.getId())) return;
        event.setCanceled(true);
        BossShieldInfo shieldInfo = events.get(info.getId());
        Minecraft mc = Minecraft.getInstance();
        MatrixStack matrixStack = event.getMatrixStack();

        int width = event.getWindow().getGuiScaledWidth();
        int x = event.getX();
        int y = event.getY();
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        mc.getTextureManager().bind(GUI_BARS_LOCATION);
        drawBar(matrixStack, x, y, info);

        float shield = shieldInfo.isImmune() ? 1 : shieldInfo.getShield();

        IRenderTypeBuffer.Impl getter = IRenderTypeBuffer.immediate(Tessellator.getInstance().getBuilder());
        UniformCache uniforms = shieldShader.pushCache();
        if (shieldInfo.isImmune()) {
            uniforms.glUniform4f("baseColour", 0F, 1F, 1F, 2F);
        } else {
            uniforms.glUniform4f("baseColour", 1F, 0F, 0F, 2F);
        }
        uniforms.glUniform1f("activation", shield);
        IVertexBuilder builder = new TransformingVertexBuilder(getter.getBuffer(new ShaderRenderType(DraconicGuardianRenderer.shieldType, shieldShader, uniforms)), matrixStack);
        drawShieldRect(builder, x, y, 0, 0, 182, 182);
        getter.endBatch();

        if (shieldInfo.crystals > 0) {
            ITextComponent countText = new StringTextComponent("x" + shieldInfo.crystals);
            int countWidth = mc.font.width(countText);

            float anim = (TimeKeeper.getClientTick() + event.getPartialTicks()) * 3.0F;
            IVertexBuilder ivertexbuilder = getter.getBuffer(RENDER_TYPE);
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

            uniforms = DraconicGuardianRenderer.shieldShader.pushCache();
            uniforms.glUniform4f("baseColour", 1F, 0F, 0F, 1.5F);
            IVertexBuilder shaderBuilder = getter.getBuffer(new ShaderRenderType(DraconicGuardianRenderer.shieldType, DraconicGuardianRenderer.shieldShader, uniforms));
            matrixStack.pushPose();
            matrixStack.translate(x + 182 - countWidth - 8, y - 6, 0.0D);
            matrixStack.scale(14.0F, 14.0F, 14.0F);
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

            mc.font.drawShadow(matrixStack, new StringTextComponent("x" + shieldInfo.crystals), x + 182 - countWidth, (float) y - 9, 0xffFFFF);
        }

        ITextComponent itextcomponent = info.getName();
        int stringWidth = mc.font.width(itextcomponent);
        int stringX = shieldInfo.crystals > 0 ? x : width / 2 - stringWidth / 2;
        int stringY = y - 9;
        matrixStack.translate(0, 0, 16);
        mc.font.drawShadow(matrixStack, itextcomponent, (float) stringX, (float) stringY, 0xff0000);
    }

    private static void drawBar(MatrixStack matrixStack, int x, int y, BossInfo info) {
        drawRect(matrixStack, x, y, 0, info.getColor().ordinal() * 5 * 2, 182, 5);
        if (info.getOverlay() != BossInfo.Overlay.PROGRESS) {
            drawRect(matrixStack, x, y, 0, 80 + (info.getOverlay().ordinal() - 1) * 5 * 2, 182, 5);
        }

        int i = (int) (info.getPercent() * 183.0F);
        if (i > 0) {
            drawRect(matrixStack, x, y, 0, info.getColor().ordinal() * 5 * 2 + 5, i, 5);
            if (info.getOverlay() != BossInfo.Overlay.PROGRESS) {
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

    public static void drawRect(MatrixStack stack, int x, int y, int u, int v, int width, int height) {
        blit(stack, x, y, 0, (float) u, (float) v, width, height, 256, 256);
    }

    public static void drawShieldRect(IVertexBuilder builder, int x, int y, int u, int v, int width, int height) {
        drawQuad(builder, x, x + width, y, y + height, 0, 0F, 1F, 0F, 1F);
    }

    private static void drawQuad(IVertexBuilder builder, int x, int xMax, int y, int yMax, int z, float u, float uMax, float v, float vMax) {
        builder.vertex((float) x, (float) yMax, (float) z).color(1F, 1F, 1F, 1F).uv(u, vMax).endVertex();
        builder.vertex((float) xMax, (float) yMax, (float) z).color(1F, 1F, 1F, 1F).uv(uMax, vMax).endVertex();
        builder.vertex((float) xMax, (float) y, (float) z).color(1F, 1F, 1F, 1F).uv(uMax, v).endVertex();
        builder.vertex((float) x, (float) y, (float) z).color(1F, 1F, 1F, 1F).uv(u, v).endVertex();
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
            float f = MathHelper.clamp((float) i / 100.0F, 0.0F, 1.0F);
            return MathHelper.lerp(f, this.lastPower, this.targetPower);
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
