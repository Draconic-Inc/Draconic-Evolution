package com.brandon3055.draconicevolution.client.model;

import codechicken.lib.render.CCModel;
import codechicken.lib.render.CCRenderState;
import codechicken.lib.render.OBJParser;
import codechicken.lib.render.buffer.VBORenderType;
import codechicken.lib.render.shader.*;
import codechicken.lib.util.SneakyUtils;
import com.brandon3055.brandonscore.api.TechLevel;
import com.brandon3055.brandonscore.client.BCClientEventHandler;
import com.brandon3055.draconicevolution.DEConfig;
import com.brandon3055.draconicevolution.DraconicEvolution;
import com.brandon3055.draconicevolution.client.model.tool.VBOModelRender;
import com.brandon3055.draconicevolution.client.render.item.RenderModularChestpeice;
import com.brandon3055.draconicevolution.client.render.item.ToolRenderBase;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderState;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.CrossbowItem;
import net.minecraft.util.HandSide;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.opengl.GL11;

import java.util.Collections;
import java.util.Map;

import static codechicken.lib.render.shader.ShaderObject.StandardShaderType.FRAGMENT;
import static codechicken.lib.render.shader.ShaderObject.StandardShaderType.VERTEX;
import static com.brandon3055.draconicevolution.DraconicEvolution.MODID;
import static net.minecraft.client.renderer.RenderState.*;

/**
 * Created by brandon3055 on 29/6/20
 */
public class ModularArmorModel extends VBOBipedModel<PlayerEntity> {

    public static ShaderProgram shieldShader = ShaderProgramBuilder.builder()
            .addShader("vert", shader -> shader
                    .type(VERTEX)
                    .source(new ResourceLocation(MODID, "shaders/armor_shield.vert"))
            )
            .addShader("frag", shader -> shader
                    .type(FRAGMENT)
                    .source(new ResourceLocation(MODID, "shaders/armor_shield.frag"))
                    .uniform("time", UniformType.FLOAT)
                    .uniform("activation", UniformType.FLOAT)
                    .uniform("tier", UniformType.INT)
            )
            .whenUsed(cache -> cache.glUniform1f("time", (BCClientEventHandler.elapsedTicks + Minecraft.getInstance().getRenderPartialTicks()) / 20))
            .build();

    private CCModel baseModel;
    private CCModel materialModel;
    private CCModel gemModel;
    private CCModel centralGemModel;

    private VBORenderType baseVBOType;
    private VBORenderType materialVBOType;
    private VBORenderType materialChaosVBOType;
    private VBORenderType gemVBOType;
    private VBORenderType centralGemVBOType;

    public RenderType modelType;
    public RenderType chaosType;
    public RenderType shieldType;
    public RenderType shaderParentType;

    private float shieldState = 0;

    public ModularArmorModel(float size, TechLevel techLevel) {
        super(size);
        float yOffsetIn = 0;

        Map<String, CCModel> model = OBJParser.parseModels(new ResourceLocation(DraconicEvolution.MODID, "models/item/equipment/chestpeice.obj"), GL11.GL_TRIANGLES, null);
        baseModel = CCModel.combine(Collections.singletonList(model.get("base_model"))).backfacedCopy();
        materialModel = model.get("chevrons").backfacedCopy();
        gemModel = model.get("power_crystals").backfacedCopy();
        centralGemModel = model.get("crystal_core").backfacedCopy();


        String levelName = techLevel.name().toLowerCase();
        modelType = RenderType.makeType("modelType", DefaultVertexFormats.BLOCK, GL11.GL_TRIANGLES, 256, true, false, RenderType.State.getBuilder()
                .texture(new RenderState.TextureState(new ResourceLocation(DraconicEvolution.MODID, "textures/models/item/equipment/" + levelName + "_chestpeice.png"), false, false))
                .diffuseLighting(DIFFUSE_LIGHTING_ENABLED)
                .lightmap(LIGHTMAP_ENABLED)
                .build(true));
        chaosType = RenderType.makeType("chaosShaderType", DefaultVertexFormats.BLOCK, GL11.GL_TRIANGLES, 256, RenderType.State.getBuilder()
                .texture(new RenderState.TextureState(new ResourceLocation(DraconicEvolution.MODID, "textures/models/item/equipment/chaos_shader.png"), true, false))
                .lightmap(LIGHTMAP_ENABLED)
                .overlay(OVERLAY_ENABLED)
                .build(false));
        shaderParentType = RenderType.makeType("shaderGemType", DefaultVertexFormats.BLOCK, GL11.GL_TRIANGLES, 256, RenderType.State.getBuilder()
                .diffuseLighting(DIFFUSE_LIGHTING_ENABLED)
                .texture(new RenderState.TextureState(new ResourceLocation(DraconicEvolution.MODID, "textures/models/item/equipment/shader_fallback_" + levelName + ".png"), false, false))
                .lightmap(LIGHTMAP_ENABLED)
                .overlay(OVERLAY_ENABLED)
                .build(false));
        shieldType = RenderType.makeType("shieldType", DefaultVertexFormats.POSITION_COLOR_LIGHTMAP, GL11.GL_TRIANGLES, 256, RenderType.State.getBuilder()
                .diffuseLighting(DIFFUSE_LIGHTING_ENABLED)
                .transparency(TRANSLUCENT_TRANSPARENCY)
                .texturing(new RenderState.TexturingState("lighting", RenderSystem::disableLighting, SneakyUtils.none()))
                .lightmap(LIGHTMAP_ENABLED)
                .cull(CULL_DISABLED)
                .build(false));

        baseVBOType = new VBORenderType(modelType, DefaultVertexFormats.POSITION_TEX_COLOR_NORMAL, (format, builder) -> {
            CCRenderState ccrs = CCRenderState.instance();
            ccrs.reset();
            ccrs.bind(builder, format);
            baseModel.render(ccrs);
        });
        materialVBOType = new VBORenderType(modelType, DefaultVertexFormats.POSITION_TEX_COLOR_NORMAL, (format, builder) -> {
            CCRenderState ccrs = CCRenderState.instance();
            ccrs.reset();
            ccrs.bind(builder, format);
            materialModel.render(ccrs);
        });
        materialChaosVBOType = new VBORenderType(chaosType, DefaultVertexFormats.POSITION_TEX_COLOR_NORMAL, (format, builder) -> {
            CCRenderState ccrs = CCRenderState.instance();
            ccrs.reset();
            ccrs.bind(builder, format);
            materialModel.render(ccrs);
        });
        gemVBOType = new VBORenderType(shaderParentType, DefaultVertexFormats.POSITION_TEX_COLOR_NORMAL, (format, builder) -> {
            CCRenderState ccrs = CCRenderState.instance();
            ccrs.reset();
            ccrs.bind(builder, format);
            gemModel.render(ccrs);
        });
        centralGemVBOType = new VBORenderType(shaderParentType, DefaultVertexFormats.POSITION_TEX_COLOR_NORMAL, (format, builder) -> {
            CCRenderState ccrs = CCRenderState.instance();
            ccrs.reset();
            ccrs.bind(builder, format);
            centralGemModel.render(ccrs);
        });
        CCModel shieldHeadModel = model.get("shield_head").backfacedCopy();
        VBORenderType shieldHeadVBO = new VBORenderType(shieldType, DefaultVertexFormats.POSITION_TEX_COLOR_NORMAL, (format, builder) -> {
            CCRenderState ccrs = CCRenderState.instance();
            ccrs.reset();
            ccrs.bind(builder, format);
            ccrs.baseColour = 0xFFFFFF40;
            shieldHeadModel.render(ccrs);
        });
        CCModel shieldBodyModel = model.get("shield_body").backfacedCopy();
        VBORenderType shieldBodyVBO = new VBORenderType(shieldType, DefaultVertexFormats.POSITION_TEX_COLOR_NORMAL, (format, builder) -> {
            CCRenderState ccrs = CCRenderState.instance();
            ccrs.reset();
            ccrs.bind(builder, format);
            ccrs.baseColour = 0xFFFFFF40;
            shieldBodyModel.render(ccrs);
        });
        CCModel shieldRightArmModel = model.get("shield_right_arm").backfacedCopy();
        VBORenderType shieldRightArmVBO = new VBORenderType(shieldType, DefaultVertexFormats.POSITION_TEX_COLOR_NORMAL, (format, builder) -> {
            CCRenderState ccrs = CCRenderState.instance();
            ccrs.reset();
            ccrs.bind(builder, format);
            ccrs.baseColour = 0xFFFFFF40;
            shieldRightArmModel.render(ccrs);
        });
        CCModel shieldLeftArmModel = model.get("shield_left_arm").backfacedCopy();
        VBORenderType shieldLeftArmVBO = new VBORenderType(shieldType, DefaultVertexFormats.POSITION_TEX_COLOR_NORMAL, (format, builder) -> {
            CCRenderState ccrs = CCRenderState.instance();
            ccrs.reset();
            ccrs.bind(builder, format);
            ccrs.baseColour = 0xFFFFFF40;
            shieldLeftArmModel.render(ccrs);
        });
        CCModel shieldRightLegModel = model.get("shield_right_leg").backfacedCopy();
        VBORenderType shieldRightLegVBO = new VBORenderType(shieldType, DefaultVertexFormats.POSITION_TEX_COLOR_NORMAL, (format, builder) -> {
            CCRenderState ccrs = CCRenderState.instance();
            ccrs.reset();
            ccrs.bind(builder, format);
            ccrs.baseColour = 0xFFFFFF40;
            shieldRightLegModel.render(ccrs);
        });
        CCModel shieldLeftLegModel = model.get("shield_left_leg").backfacedCopy();
        VBORenderType shieldLeftLegVBO = new VBORenderType(shieldType, DefaultVertexFormats.POSITION_TEX_COLOR_NORMAL, (format, builder) -> {
            CCRenderState ccrs = CCRenderState.instance();
            ccrs.reset();
            ccrs.bind(builder, format);
            ccrs.baseColour = 0xFFFFFF40;
            shieldLeftLegModel.render(ccrs);
        });

        bipedBody = new VBOModelRender(this, baseVBOType);
        bipedBody.setRotationPoint(0.0F, 0.0F + yOffsetIn, 0.0F);
        VBOModelRender matRender = new VBOModelRender(this, techLevel == TechLevel.CHAOTIC ? materialChaosVBOType : materialVBOType);
        if (techLevel == TechLevel.CHAOTIC) {
            matRender.setShader(() -> RenderModularChestpeice.getShaderType(chaosType, RenderModularChestpeice.chaosShader));
        }
        bipedBody.addChild(matRender);
        bipedBody.addChild(new VBOModelRender(this, gemVBOType).setShader(() -> RenderModularChestpeice.getShaderType(shaderParentType, techLevel, RenderModularChestpeice.gemShader)));
        bipedBody.addChild(new VBOModelRender(this, centralGemVBOType).setShader(() -> RenderModularChestpeice.getShaderType(shaderParentType, techLevel, RenderModularChestpeice.coreShader)));
        bipedBody.addChild(new VBOModelRender(this, shieldBodyVBO).setShader(() -> getShaderType(shieldType, techLevel, shieldState, shieldShader)));

        this.bipedHead = new VBOModelRender(this, shieldHeadVBO).setShader(() -> getShaderType(shieldType, techLevel, shieldState, shieldShader));
        this.bipedHead.setRotationPoint(0.0F, 0.0F + yOffsetIn, 0.0F);

        this.bipedRightArm = new VBOModelRender(this, shieldRightArmVBO).setShader(() -> getShaderType(shieldType, techLevel, shieldState, shieldShader));
        this.bipedRightArm.setRotationPoint(-5.0F, 2.0F + yOffsetIn, 0.0F);

        this.bipedLeftArm = new VBOModelRender(this, shieldLeftArmVBO).setShader(() -> getShaderType(shieldType, techLevel, shieldState, shieldShader));
        this.bipedLeftArm.mirror = true;
        this.bipedLeftArm.setRotationPoint(5.0F, 2.0F + yOffsetIn, 0.0F);

        this.bipedRightLeg = new VBOModelRender(this, shieldRightLegVBO).setShader(() -> getShaderType(shieldType, techLevel, shieldState, shieldShader));
        this.bipedRightLeg.setRotationPoint(-1.9F, 12.0F + yOffsetIn, 0.0F);

        this.bipedLeftLeg = new VBOModelRender(this, shieldLeftLegVBO).setShader(() -> getShaderType(shieldType, techLevel, shieldState, shieldShader));
        this.bipedLeftLeg.mirror = true;
        this.bipedLeftLeg.setRotationPoint(1.9F, 12.0F + yOffsetIn, 0.0F);
    }

    @Override
    public void render(MatrixStack mStack, IRenderTypeBuffer getter, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) {
//        DEConfig.toolShaders = false;
        if (shieldState < 1) shieldState += 0.01;

        if (this.isChild) {
            mStack.push();
            if (this.field_228221_a_) {
                float f = 1.5F / this.field_228224_g_;
                mStack.scale(f, f, f);
            }
            mStack.translate(0.0D, this.field_228222_b_ / 16.0F, this.field_228223_f_ / 16.0F);
            bipedHead.render(mStack, getter, packedLightIn, packedOverlayIn, red, green, blue, alpha);
            mStack.pop();
            mStack.push();
            float f1 = 1.0F / this.field_228225_h_;
            mStack.scale(f1, f1, f1);
            mStack.translate(0.0D, this.field_228226_i_ / 16.0F, 0.0D);
            bipedBody.render(mStack, getter, packedLightIn, packedOverlayIn, red, green, blue, alpha);
            bipedLeftArm.render(mStack, getter, packedLightIn, packedOverlayIn, red, green, blue, alpha);
            bipedRightArm.render(mStack, getter, packedLightIn, packedOverlayIn, red, green, blue, alpha);
            bipedLeftLeg.render(mStack, getter, packedLightIn, packedOverlayIn, red, green, blue, alpha);
            bipedRightLeg.render(mStack, getter, packedLightIn, packedOverlayIn, red, green, blue, alpha);
            mStack.pop();
        } else {
            bipedBody.render(mStack, getter, packedLightIn, packedOverlayIn, red, green, blue, alpha);
            bipedHead.render(mStack, getter, packedLightIn, packedOverlayIn, red, green, blue, alpha);
            bipedLeftArm.render(mStack, getter, packedLightIn, packedOverlayIn, red, green, blue, alpha);
            bipedRightArm.render(mStack, getter, packedLightIn, packedOverlayIn, red, green, blue, alpha);
            bipedLeftLeg.render(mStack, getter, packedLightIn, packedOverlayIn, red, green, blue, alpha);
            bipedRightLeg.render(mStack, getter, packedLightIn, packedOverlayIn, red, green, blue, alpha);
        }
    }

    public static ShaderRenderType getShaderType(RenderType parent, TechLevel techLevel, float activation, ShaderProgram shader) {
        UniformCache uniforms = shader.pushCache();
        uniforms.glUniform1i("tier", techLevel.index);
        uniforms.glUniform1f("activation", activation);
        return new ShaderRenderType(parent, shader, uniforms);
    }
}
