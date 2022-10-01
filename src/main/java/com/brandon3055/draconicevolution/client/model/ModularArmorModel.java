package com.brandon3055.draconicevolution.client.model;

import codechicken.lib.render.CCModel;
import codechicken.lib.render.model.OBJParser;
import codechicken.lib.render.shader.ShaderObject;
import codechicken.lib.render.shader.ShaderProgram;
import codechicken.lib.render.shader.ShaderProgramBuilder;
import codechicken.lib.render.shader.UniformType;
import codechicken.lib.vec.Translation;
import com.brandon3055.brandonscore.api.TechLevel;
import com.brandon3055.brandonscore.utils.ModelUtils;
import com.brandon3055.draconicevolution.DraconicEvolution;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

import java.util.Collections;
import java.util.Map;

/**
 * Created by brandon3055 on 29/6/20
 */
public class ModularArmorModel extends VBOBipedModel<LivingEntity> {

    public static ShaderProgram shieldShader = ShaderProgramBuilder.builder()
            .addShader("vert", shader -> shader
                    .type(ShaderObject.StandardShaderType.VERTEX)
                    .source(new ResourceLocation(DraconicEvolution.MODID, "shaders/armor_shield.vert"))
            )
            .addShader("frag", shader -> shader
                    .type(ShaderObject.StandardShaderType.FRAGMENT)
                    .source(new ResourceLocation(DraconicEvolution.MODID, "shaders/armor_shield.frag"))
                    .uniform("time", UniformType.FLOAT)
                    .uniform("activation", UniformType.FLOAT)
                    .uniform("baseColour", UniformType.VEC4)
            )
//            .whenUsed(cache -> cache.glUniform1f("time", (BCClientEventHandler.elapsedTicks + Minecraft.getInstance().getFrameTime()) / 20))
            .build();

    private CCModel baseModel;
    private CCModel materialModel;
    private CCModel gemModel;
    private CCModel centralGemModel;

//    private VBORenderType baseVBOType;
//    private VBORenderType materialVBOType;
//    private VBORenderType materialChaosVBOType;
//    private VBORenderType gemVBOType;
//    private VBORenderType centralGemVBOType;

    public RenderType modelType;
    public RenderType chaosType;
    public RenderType shieldType;
    public RenderType shaderParentType;

    private float shieldState = 0;
    private int shieldColour = 0x00FF00;
    private boolean onArmor;

    public ModularArmorModel(float size, TechLevel techLevel, boolean onArmor) {
        super(ModelUtils.getEntityModel(ModelLayers.PLAYER));

        this.onArmor = onArmor;
        float yOffsetIn = 0;


        Map<String, CCModel> model = new OBJParser(new ResourceLocation(DraconicEvolution.MODID, "models/item/equipment/chestpeice.obj")).ignoreMtl().parse();
        baseModel = CCModel.combine(Collections.singletonList(model.get("base_model"))).backfacedCopy();
        materialModel = model.get("chevrons").backfacedCopy();
        gemModel = model.get("power_crystals").backfacedCopy();
        centralGemModel = model.get("crystal_core").backfacedCopy();

        if (onArmor) {
            materialModel.apply(new Translation(0, 0, -0.0625));
            gemModel.apply(new Translation(0, 0, -0.0625 / 2));
            centralGemModel.apply(new Translation(0, 0, -0.0625 / 2));
        }

//        String levelName = techLevel.name().toLowerCase(Locale.ENGLISH);
//        modelType = RenderType.create("modelType", DefaultVertexFormat.BLOCK, GL11.GL_TRIANGLES, 256, true, false, RenderType.CompositeState.builder()
//                .setTextureState(new RenderStateShard.TextureStateShard(new ResourceLocation(DraconicEvolution.MODID, "textures/item/equipment/" + levelName + "_chestpeice.png"), false, false))
//                .setDiffuseLightingState(RenderStateShard.DIFFUSE_LIGHTING)
//                .setLightmapState(RenderStateShard.LIGHTMAP)
//                .createCompositeState(true));
//        chaosType = RenderType.create("chaosShaderType", DefaultVertexFormat.BLOCK, GL11.GL_TRIANGLES, 256, RenderType.CompositeState.builder()
//                .setTextureState(new RenderStateShard.TextureStateShard(new ResourceLocation(DraconicEvolution.MODID, "textures/item/equipment/chaos_shader.png"), true, false))
//                .setLightmapState(RenderStateShard.LIGHTMAP)
//                .setOverlayState(RenderStateShard.OVERLAY)
//                .createCompositeState(false));
//        shaderParentType = RenderType.create("shaderGemType", DefaultVertexFormat.BLOCK, GL11.GL_TRIANGLES, 256, RenderType.CompositeState.builder()
//                .setDiffuseLightingState(RenderStateShard.DIFFUSE_LIGHTING)
//                .setTextureState(new RenderStateShard.TextureStateShard(new ResourceLocation(DraconicEvolution.MODID, "textures/item/equipment/shader_fallback_" + levelName + ".png"), false, false))
//                .setLightmapState(RenderStateShard.LIGHTMAP)
//                .setOverlayState(RenderStateShard.OVERLAY)
//                .createCompositeState(false));
//        shieldType = RenderType.create("shieldType", DefaultVertexFormat.POSITION_COLOR_LIGHTMAP, GL11.GL_TRIANGLES, 256, RenderType.CompositeState.builder()
//                .setDiffuseLightingState(RenderStateShard.DIFFUSE_LIGHTING)
//                .setTransparencyState(RenderStateShard.TRANSLUCENT_TRANSPARENCY)
//                .setTexturingState(new RenderStateShard.TexturingStateShard("lighting", RenderSystem::disableLighting, SneakyUtils.none()))
//                .setLightmapState(RenderStateShard.LIGHTMAP)
//                .setCullState(RenderStateShard.NO_CULL)
//                .createCompositeState(false));
//
//        baseVBOType = new VBORenderType(modelType, DefaultVertexFormat.POSITION_TEX_COLOR_NORMAL, (format, builder) -> {
//            if (!onArmor) {
//                CCRenderState ccrs = CCRenderState.instance();
//                ccrs.reset();
//                ccrs.bind(builder, format);
//                baseModel.render(ccrs);
//            }
//        });
//
//        materialVBOType = new VBORenderType(modelType, DefaultVertexFormat.POSITION_TEX_COLOR_NORMAL, (format, builder) -> {
//            CCRenderState ccrs = CCRenderState.instance();
//            ccrs.reset();
//            ccrs.bind(builder, format);
//            materialModel.render(ccrs);
//        });
//        materialChaosVBOType = new VBORenderType(chaosType, DefaultVertexFormat.POSITION_TEX_COLOR_NORMAL, (format, builder) -> {
//            CCRenderState ccrs = CCRenderState.instance();
//            ccrs.reset();
//            ccrs.bind(builder, format);
//            materialModel.render(ccrs);
//        });
//        gemVBOType = new VBORenderType(shaderParentType, DefaultVertexFormat.POSITION_TEX_COLOR_NORMAL, (format, builder) -> {
//            CCRenderState ccrs = CCRenderState.instance();
//            ccrs.reset();
//            ccrs.bind(builder, format);
//            gemModel.render(ccrs);
//        });
//        centralGemVBOType = new VBORenderType(shaderParentType, DefaultVertexFormat.POSITION_TEX_COLOR_NORMAL, (format, builder) -> {
//            CCRenderState ccrs = CCRenderState.instance();
//            ccrs.reset();
//            ccrs.bind(builder, format);
//            centralGemModel.render(ccrs);
//        });
//        CCModel shieldHeadModel = model.get("shield_head").backfacedCopy();
//        VBORenderType shieldHeadVBO = new VBORenderType(shieldType, DefaultVertexFormat.POSITION_TEX_COLOR_NORMAL, (format, builder) -> {
//            CCRenderState ccrs = CCRenderState.instance();
//            ccrs.reset();
//            ccrs.bind(builder, format);
//            ccrs.baseColour = 0xFFFFFF40;
//            shieldHeadModel.render(ccrs);
//        });
//        CCModel shieldBodyModel = model.get("shield_body").backfacedCopy();
//        VBORenderType shieldBodyVBO = new VBORenderType(shieldType, DefaultVertexFormat.POSITION_TEX_COLOR_NORMAL, (format, builder) -> {
//            CCRenderState ccrs = CCRenderState.instance();
//            ccrs.reset();
//            ccrs.bind(builder, format);
//            ccrs.baseColour = 0xFFFFFF40;
//            shieldBodyModel.render(ccrs);
//        });
//        CCModel shieldRightArmModel = model.get("shield_right_arm").backfacedCopy();
//        VBORenderType shieldRightArmVBO = new VBORenderType(shieldType, DefaultVertexFormat.POSITION_TEX_COLOR_NORMAL, (format, builder) -> {
//            CCRenderState ccrs = CCRenderState.instance();
//            ccrs.reset();
//            ccrs.bind(builder, format);
//            ccrs.baseColour = 0xFFFFFF40;
//            shieldRightArmModel.render(ccrs);
//        });
//        CCModel shieldLeftArmModel = model.get("shield_left_arm").backfacedCopy();
//        VBORenderType shieldLeftArmVBO = new VBORenderType(shieldType, DefaultVertexFormat.POSITION_TEX_COLOR_NORMAL, (format, builder) -> {
//            CCRenderState ccrs = CCRenderState.instance();
//            ccrs.reset();
//            ccrs.bind(builder, format);
//            ccrs.baseColour = 0xFFFFFF40;
//            shieldLeftArmModel.render(ccrs);
//        });
//        CCModel shieldRightLegModel = model.get("shield_right_leg").backfacedCopy();
//        VBORenderType shieldRightLegVBO = new VBORenderType(shieldType, DefaultVertexFormat.POSITION_TEX_COLOR_NORMAL, (format, builder) -> {
//            CCRenderState ccrs = CCRenderState.instance();
//            ccrs.reset();
//            ccrs.bind(builder, format);
//            ccrs.baseColour = 0xFFFFFF40;
//            shieldRightLegModel.render(ccrs);
//        });
//        CCModel shieldLeftLegModel = model.get("shield_left_leg").backfacedCopy();
//        VBORenderType shieldLeftLegVBO = new VBORenderType(shieldType, DefaultVertexFormat.POSITION_TEX_COLOR_NORMAL, (format, builder) -> {
//            CCRenderState ccrs = CCRenderState.instance();
//            ccrs.reset();
//            ccrs.bind(builder, format);
//            ccrs.baseColour = 0xFFFFFF40;
//            shieldLeftLegModel.render(ccrs);
//        });
//
//        bipedBody = new VBOModelRender(this, baseVBOType);
//        bipedBody.setPos(0.0F, 0.0F + yOffsetIn, 0.0F);
//        VBOModelRender matRender = new VBOModelRender(this, techLevel == TechLevel.CHAOTIC ? materialChaosVBOType : materialVBOType);
//        if (techLevel == TechLevel.CHAOTIC) {
//            matRender.setShader(() -> RenderModularChestpeice.getShaderType(chaosType, RenderModularChestpeice.chaosShader));
//        }
//        bipedBody.addChild(matRender);
//        bipedBody.addChild(new VBOModelRender(this, gemVBOType).setShader(() -> RenderModularChestpeice.getShaderType(shaderParentType, techLevel, RenderModularChestpeice.gemShader)));
//        bipedBody.addChild(new VBOModelRender(this, centralGemVBOType).setShader(() -> RenderModularChestpeice.getShaderType(shaderParentType, techLevel, shieldColour, RenderModularChestpeice.coreShader)));
//        bipedBody.addChild(new VBOModelRender(this, shieldBodyVBO, () -> shieldState > 0).setShader(() -> getShaderType(shieldType, techLevel, shieldState, shieldColour, shieldShader)));
//
//        this.bipedHead = new VBOModelRender(this, shieldHeadVBO, () -> shieldState > 0).setShader(() -> getShaderType(shieldType, techLevel, shieldState, shieldColour, shieldShader));
//        this.bipedHead.setPos(0.0F, 0.0F + yOffsetIn, 0.0F);
//
//        this.bipedRightArm = new VBOModelRender(this, shieldRightArmVBO, () -> shieldState > 0).setShader(() -> getShaderType(shieldType, techLevel, shieldState, shieldColour, shieldShader));
//        this.bipedRightArm.setPos(-5.0F, 2.0F + yOffsetIn, 0.0F);
//
//        this.bipedLeftArm = new VBOModelRender(this, shieldLeftArmVBO, () -> shieldState > 0).setShader(() -> getShaderType(shieldType, techLevel, shieldState, shieldColour, shieldShader));
//        this.bipedLeftArm.mirror = true;
//        this.bipedLeftArm.setPos(5.0F, 2.0F + yOffsetIn, 0.0F);
//
//        this.bipedRightLeg = new VBOModelRender(this, shieldRightLegVBO, () -> shieldState > 0).setShader(() -> getShaderType(shieldType, techLevel, shieldState, shieldColour, shieldShader));
//        this.bipedRightLeg.setPos(-1.9F, 12.0F + yOffsetIn, 0.0F);
//
//        this.bipedLeftLeg = new VBOModelRender(this, shieldLeftLegVBO, () -> shieldState > 0).setShader(() -> getShaderType(shieldType, techLevel, shieldState, shieldColour, shieldShader));
//        this.bipedLeftLeg.mirror = true;
//        this.bipedLeftLeg.setPos(1.9F, 12.0F + yOffsetIn, 0.0F);
    }

    @Override
    public void render(PoseStack mStack, MultiBufferSource getter, LivingEntity player, ItemStack stack, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) {
//        shieldState = 0;
//        shieldColour = 0xFFFFFFFF;
//        LazyOptional<ModuleHost> optionalHost = stack.getCapability(DECapabilities.MODULE_HOST_CAPABILITY);
//        if (!stack.isEmpty() && optionalHost.isPresent()) {
//            ModuleHost host = optionalHost.orElseThrow(IllegalStateException::new);
//            ShieldControlEntity shieldControl = host.getEntitiesByType(ModuleTypes.SHIELD_CONTROLLER).map(e -> (ShieldControlEntity) e).findAny().orElse(null);
//            if (shieldControl != null) {
//                shieldState = shieldControl.getShieldState();
//                shieldColour = shieldControl.getShieldColour();
//            }
//        }
//
//
//        if (this.young) {
//            mStack.pushPose();
//            if (this.scaleHead) {
//                float f = 1.5F / this.babyHeadScale;
//                mStack.scale(f, f, f);
//            }
//            mStack.translate(0.0D, this.yHeadOffset / 16.0F, this.zHeadOffset / 16.0F);
//            bipedHead.render(mStack, getter, packedLightIn, packedOverlayIn, red, green, blue, alpha);
//            mStack.popPose();
//            mStack.pushPose();
//            float f1 = 1.0F / this.babyBodyScale;
//            mStack.scale(f1, f1, f1);
//            mStack.translate(0.0D, this.bodyYOffset / 16.0F, 0.0D);
//            bipedBody.render(mStack, getter, packedLightIn, packedOverlayIn, red, green, blue, alpha);
//            bipedLeftArm.render(mStack, getter, packedLightIn, packedOverlayIn, red, green, blue, alpha);
//            bipedRightArm.render(mStack, getter, packedLightIn, packedOverlayIn, red, green, blue, alpha);
//            bipedLeftLeg.render(mStack, getter, packedLightIn, packedOverlayIn, red, green, blue, alpha);
//            bipedRightLeg.render(mStack, getter, packedLightIn, packedOverlayIn, red, green, blue, alpha);
//            mStack.popPose();
//        } else {
//            bipedBody.render(mStack, getter, packedLightIn, packedOverlayIn, red, green, blue, alpha);
//            bipedHead.render(mStack, getter, packedLightIn, packedOverlayIn, red, green, blue, alpha);
//            bipedLeftArm.render(mStack, getter, packedLightIn, packedOverlayIn, red, green, blue, alpha);
//            bipedRightArm.render(mStack, getter, packedLightIn, packedOverlayIn, red, green, blue, alpha);
//            bipedLeftLeg.render(mStack, getter, packedLightIn, packedOverlayIn, red, green, blue, alpha);
//            bipedRightLeg.render(mStack, getter, packedLightIn, packedOverlayIn, red, green, blue, alpha);
//        }
    }

//    public static ShaderRenderType getShaderType(RenderType parent, TechLevel techLevel, float activation, int colour, ShaderProgram shader) {
//        UniformCache uniforms = shader.pushCache();
//        uniforms.glUniform1f("activation", activation);
//        uniforms.glUniform4f("baseColour", ((colour >> 16) & 0xFF) / 255F, ((colour >> 8) & 0xFF) / 255F, (colour & 0xFF) / 255F, ((colour >> 24) & 0xFF) / 63F);
//        return new ShaderRenderType(parent, shader, uniforms);
//    }
}
