package com.brandon3055.draconicevolution.client.model;

import codechicken.lib.render.CCModel;
import codechicken.lib.render.CCRenderState;
import codechicken.lib.render.buffer.VBORenderType;
import codechicken.lib.render.model.OBJParser;
import codechicken.lib.vec.Matrix4;
import codechicken.lib.vec.Translation;
import com.brandon3055.brandonscore.api.TechLevel;
import com.brandon3055.brandonscore.client.model.ExtendedModelPart;
import com.brandon3055.brandonscore.client.render.EquippedItemModel;
import com.brandon3055.brandonscore.client.shader.BCShaders;
import com.brandon3055.brandonscore.handlers.contributor.ContributorHandler;
import com.brandon3055.brandonscore.handlers.contributor.ContributorProperties;
import com.brandon3055.draconicevolution.DraconicEvolution;
import com.brandon3055.draconicevolution.api.capability.DECapabilities;
import com.brandon3055.draconicevolution.api.capability.ModuleHost;
import com.brandon3055.draconicevolution.api.modules.ModuleTypes;
import com.brandon3055.draconicevolution.api.modules.entities.ShieldControlEntity;
import com.brandon3055.draconicevolution.client.DEShaders;
import com.brandon3055.draconicevolution.client.render.item.ToolRenderBase;
import com.brandon3055.brandonscore.client.shader.BCShader;
import com.brandon3055.draconicevolution.client.shader.ShieldShader;
import com.brandon3055.draconicevolution.client.shader.ToolShader;
import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.util.LazyOptional;

import java.util.*;

import static com.brandon3055.draconicevolution.DraconicEvolution.MODID;

/**
 * Created by brandon3055 on 13/11/2022
 */
public class ModularChestpieceModel<T extends LivingEntity> extends HumanoidModel<T> implements EquippedItemModel {

    private final TechLevel techLevel;
    private int shieldColour;
    private float shieldState;

    public ModularChestpieceModel(TechLevel techLevel, boolean isOnArmor) {
        super(createMesh(new CubeDeformation(1), 0).getRoot().bake(64, 64));
        this.techLevel = techLevel;
        Map<String, CCModel> model = new OBJParser(new ResourceLocation(DraconicEvolution.MODID, "models/item/equipment/chestpeice.obj")).ignoreMtl().parse();
        CCModel baseModel = model.get("base_model").backfacedCopy();
        CCModel materialModel = model.get("chevrons").backfacedCopy();
        CCModel gemModel = model.get("power_crystals").backfacedCopy();
        CCModel coreGemModel = model.get("crystal_core").backfacedCopy();

        CCModel shieldHeadModel = model.get("shield_head").backfacedCopy();
        CCModel shieldBodyModel = model.get("shield_body").backfacedCopy();
        CCModel shieldRightArmModel = model.get("shield_right_arm").backfacedCopy();
        CCModel shieldLeftArmModel = model.get("shield_left_arm").backfacedCopy();
        CCModel shieldRightLegModel = model.get("shield_right_leg").backfacedCopy();
        CCModel shieldLeftLegModel = model.get("shield_left_leg").backfacedCopy();

        if (isOnArmor) {
            materialModel.apply(new Translation(0, 0, -0.0625));
            gemModel.apply(new Translation(0, 0, -0.0625 / 2));
            coreGemModel.apply(new Translation(0, 0, -0.0625 / 2));
        }

        String levelName = techLevel.name().toLowerCase(Locale.ROOT);
        RenderType baseType = RenderType.create(MODID + ":base", DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.TRIANGLES, 256, true, false, RenderType.CompositeState.builder()
                .setShaderState(new RenderStateShard.ShaderStateShard(DEShaders.TOOL_BASE_SHADER::getShaderInstance))
                .setTextureState(new RenderStateShard.TextureStateShard(new ResourceLocation(MODID, "textures/item/equipment/" + levelName + "_chestpeice.png"), false, false))
                .setLightmapState(RenderStateShard.LIGHTMAP)
                .setOverlayState(RenderStateShard.OVERLAY)
                .createCompositeState(true)
        );

        RenderType chaoticType = RenderType.create(MODID + ":tool_chaos", DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.TRIANGLES, 256, RenderType.CompositeState.builder()
                .setShaderState(new RenderStateShard.ShaderStateShard(BCShaders.CHAOS_ENTITY_SHADER::getShaderInstance))
                .setTextureState(new RenderStateShard.TextureStateShard(new ResourceLocation(MODID, "textures/item/equipment/chaos_shader.png"), true, false))
                .setLightmapState(RenderStateShard.LIGHTMAP)
                .setOverlayState(RenderStateShard.OVERLAY)
                .createCompositeState(false)
        );

        RenderType gemType = RenderType.create(MODID + ":tool_gem", DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.TRIANGLES, 256, RenderType.CompositeState.builder()
                .setShaderState(new RenderStateShard.ShaderStateShard(DEShaders.TOOL_GEM_SHADER::getShaderInstance))
                .setTextureState(new RenderStateShard.TextureStateShard(new ResourceLocation(MODID, "textures/item/equipment/shader_fallback_" + levelName + ".png"), false, false))
                .setLightmapState(RenderStateShard.LIGHTMAP)
                .setOverlayState(RenderStateShard.OVERLAY)
                .createCompositeState(false)
        );

        RenderType coreGemType = RenderType.create(MODID + ":core_gem", DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.TRIANGLES, 256, RenderType.CompositeState.builder()
                .setShaderState(new RenderStateShard.ShaderStateShard(DEShaders.CHESTPIECE_GEM_SHADER::getShaderInstance))
                .setTextureState(new RenderStateShard.TextureStateShard(new ResourceLocation(MODID, "textures/item/equipment/shader_fallback_" + levelName + ".png"), false, false))
                .setLightmapState(RenderStateShard.LIGHTMAP)
                .setOverlayState(RenderStateShard.OVERLAY)
                .createCompositeState(false)
        );

        RenderType shieldType = RenderType.create(MODID + ":armor_shield", DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.TRIANGLES, 256, RenderType.CompositeState.builder()
                .setShaderState(new RenderStateShard.ShaderStateShard(DEShaders.CHESTPIECE_SHIELD_SHADER::getShaderInstance))
                .setTransparencyState(RenderStateShard.TRANSLUCENT_TRANSPARENCY)
                .setOutputState(RenderStateShard.ITEM_ENTITY_TARGET)
                .setWriteMaskState(RenderStateShard.COLOR_DEPTH_WRITE)
                .setLightmapState(RenderStateShard.LIGHTMAP)
                .createCompositeState(false)
        );

        ExtendedModelPart body = new ExtendedModelPart();
        if (!isOnArmor) {
            body.addChild(new ChestpieceModelPart(baseModel, baseType, DEShaders.TOOL_BASE_SHADER));
        }
        if (techLevel == TechLevel.CHAOTIC) {
            body.addChild(new ChestpieceModelPart(materialModel, chaoticType, BCShaders.CHAOS_ENTITY_SHADER));
        } else {
            body.addChild(new ChestpieceModelPart(materialModel, baseType, DEShaders.TOOL_BASE_SHADER));
        }
        body.addChild(new ChestpieceModelPart(gemModel, gemType, DEShaders.TOOL_GEM_SHADER));
        body.addChild(new CoreGemModelPart(coreGemModel, coreGemType, DEShaders.CHESTPIECE_GEM_SHADER));
        this.body = body;

        head = new ShieldModelPart(shieldHeadModel, shieldType, DEShaders.CHESTPIECE_SHIELD_SHADER);
        body.addChild(new ShieldModelPart(shieldBodyModel, shieldType, DEShaders.CHESTPIECE_SHIELD_SHADER));
        leftArm = new ShieldModelPart(shieldLeftArmModel, shieldType, DEShaders.CHESTPIECE_SHIELD_SHADER);
        rightArm = new ShieldModelPart(shieldRightArmModel, shieldType, DEShaders.CHESTPIECE_SHIELD_SHADER);
        leftLeg = new ShieldModelPart(shieldLeftLegModel, shieldType, DEShaders.CHESTPIECE_SHIELD_SHADER);
        rightLeg = new ShieldModelPart(shieldRightLegModel, shieldType, DEShaders.CHESTPIECE_SHIELD_SHADER);
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer consumer, int packedLight, int packedOverlay, float r, float g, float b, float a) {}

    @Override
    protected Iterable<ModelPart> headParts() {
        return ImmutableList.of(head);
    }

    @Override
    protected Iterable<ModelPart> bodyParts() {
        return ImmutableList.of(body, leftArm, rightArm, leftLeg, rightLeg);
    }

    @Override
    public void render(LivingEntity entity, PoseStack poseStack, MultiBufferSource buffers, ItemStack stack, int packedLight, int packedOverlay, float partialTicks) {
        shieldColour = 0xFFFFFFFF;
        shieldState = 0;
        LazyOptional<ModuleHost> optionalHost = stack.getCapability(DECapabilities.MODULE_HOST_CAPABILITY);
        if (!stack.isEmpty() && optionalHost.isPresent()) {
            ModuleHost host = optionalHost.orElseThrow(IllegalStateException::new);
            ShieldControlEntity shieldControl = host.getEntitiesByType(ModuleTypes.SHIELD_CONTROLLER).map(e -> (ShieldControlEntity) e).findAny().orElse(null);
            if (shieldControl != null) {
                shieldState = shieldControl.getShieldState();
                shieldColour = shieldControl.getShieldColour() | 0xFF000000;
                if (entity instanceof Player player) {
                    ContributorProperties props = ContributorHandler.getProps(player);
                    if (props.hasShieldRGB() && props.getConfig().overrideShield()) {
                        shieldColour = props.getConfig().getShieldColour(partialTicks);
                    }
                }
            }
        }

        if (this.young) {
            poseStack.pushPose();
            if (this.scaleHead) {
                float f = 1.5F / this.babyHeadScale;
                poseStack.scale(f, f, f);
            }

            poseStack.translate(0.0D, this.babyYHeadOffset / 16.0F, this.babyZHeadOffset / 16.0F);
            this.headParts().forEach(part -> ((ExtendedModelPart) part).render(poseStack, buffers, packedLight, packedOverlay));
            poseStack.popPose();
            poseStack.pushPose();
            float f1 = 1.0F / this.babyBodyScale;
            poseStack.scale(f1, f1, f1);
            poseStack.translate(0.0D, this.bodyYOffset / 16.0F, 0.0D);
            this.bodyParts().forEach(part -> ((ExtendedModelPart) part).render(poseStack, buffers, packedLight, packedOverlay));
            poseStack.popPose();
        } else {
            this.headParts().forEach(part -> ((ExtendedModelPart) part).render(poseStack, buffers, packedLight, packedOverlay));
            this.bodyParts().forEach(part -> ((ExtendedModelPart) part).render(poseStack, buffers, packedLight, packedOverlay));
        }
    }

    public class ChestpieceModelPart extends ExtendedModelPart {
        protected final VBORenderType renderType;
        protected final BCShader<?> shader;

        public ChestpieceModelPart(CCModel model, RenderType baseType, BCShader<?> shader) {
            this.shader = shader;
            renderType = new VBORenderType(baseType, (format, builder) -> {
                CCRenderState ccrs = CCRenderState.instance();
                ccrs.reset();
                ccrs.bind(builder, format);
                model.render(ccrs);
            });
        }

        @Override
        public void render(PoseStack poseStack, MultiBufferSource buffers, int packedLight, int packedOverlay, float r, float g, float b, float a) {
            if (this.visible) {
                poseStack.pushPose();
                this.translateAndRotate(poseStack);
                Matrix4 mat = new Matrix4(poseStack);
                buffers.getBuffer(renderType.withCallback(() -> {
                    ToolRenderBase.glUniformBaseColor(shader, techLevel, 1F);
                    shader.getModelMatUniform().glUniformMatrix4f(mat);
                }));
                poseStack.popPose();
            }
        }
    }

    public class CoreGemModelPart extends ChestpieceModelPart {
        private final ToolShader shader;

        public CoreGemModelPart(CCModel model, RenderType baseType, ToolShader shader) {
            super(model, baseType, shader);
            this.shader = shader;
        }

        @Override
        public void render(PoseStack poseStack, MultiBufferSource buffers, int packedLight, int packedOverlay, float r, float g, float b, float a) {
            if (this.visible) {
                poseStack.pushPose();
                this.translateAndRotate(poseStack);
                Matrix4 mat = new Matrix4(poseStack);
                int color = shieldColour;
                buffers.getBuffer(renderType.withCallback(() -> {
                    shader.getBaseColorUniform().glUniform4f(((color >> 16) & 0xFF) / 255F, ((color >> 8) & 0xFF) / 255F, (color & 0xFF) / 255F, ((color >> 24) & 0xFF) / 255F);
                    shader.getModelMatUniform().glUniformMatrix4f(mat);
                }));
                poseStack.popPose();
            }
        }
    }

    public class ShieldModelPart extends ChestpieceModelPart {
        private final ShieldShader shader;

        public ShieldModelPart(CCModel model, RenderType baseType, ShieldShader shader) {
            super(model, baseType, shader);
            this.shader = shader;
        }

        @Override
        public void render(PoseStack poseStack, MultiBufferSource buffers, int packedLight, int packedOverlay, float r, float g, float b, float a) {
            if (shieldState > 0) {
                poseStack.pushPose();
                this.translateAndRotate(poseStack);
                Matrix4 mat = new Matrix4(poseStack);
                int color = shieldColour;
                float state = shieldState;
                buffers.getBuffer(renderType.withCallback(() -> {
                    shader.getBaseColourUniform().glUniform4f(((color >> 16) & 0xFF) / 255F, ((color >> 8) & 0xFF) / 255F, (color & 0xFF) / 255F, ((color >> 24) & 0xFF) / 255F);
                    shader.getActivationUniform().glUniform1f(state);
                    shader.getModelMatUniform().glUniformMatrix4f(mat);
                }));
                poseStack.popPose();
            }
        }
    }
}
