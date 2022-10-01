package com.brandon3055.draconicevolution.client.render.item;

import codechicken.lib.math.MathHelper;
import codechicken.lib.render.CCModel;
import codechicken.lib.render.CCRenderState;
import codechicken.lib.render.model.OBJParser;
import codechicken.lib.util.TransformUtils;
import codechicken.lib.vec.Matrix4;
import codechicken.lib.vec.Vector3;
import com.brandon3055.brandonscore.api.TechLevel;
import com.brandon3055.draconicevolution.DraconicEvolution;
import com.brandon3055.draconicevolution.client.render.modelfx.StaffModelEffect;
import com.brandon3055.draconicevolution.items.equipment.ModularStaff;
import com.google.common.collect.ImmutableMap;
import com.mojang.math.Transformation;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.block.model.ItemTransforms.TransformType;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.model.SimpleModelState;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by brandon3055 on 22/5/20.
 */
public class RenderModularStaff extends ToolRenderBase {

    private static ModelState TRANSFORMATION;
    static {
        Map<TransformType, Transformation> map = new HashMap<>();
        map.put(TransformType.GROUND,                   TransformUtils.create(   0F,   2F,    0F, 0F,   0F,  0F,  0.5F));
        map.put(TransformType.FIXED,                    TransformUtils.create(   0F,   0F,    0F, 0F, 180F,  0F,    1F));

        map.put(TransformType.THIRD_PERSON_RIGHT_HAND,  TransformUtils.create(   0F,   1.5F,  -6.5F, 0F,  90F, -15F, 0.85F));
        map.put(TransformType.THIRD_PERSON_LEFT_HAND,   TransformUtils.create(   0F,   1.5F,  -6.5F, 0F, -90F,  15F, 0.85F));

        map.put(TransformType.FIRST_PERSON_RIGHT_HAND,  TransformUtils.create(1.13F, 3.2F, 1.13F, 0F,  90F, -45F, 0.68F));
        map.put(TransformType.FIRST_PERSON_LEFT_HAND,   TransformUtils.create(1.13F, 3.2F, 1.13F, 0F,  -90F, 45F, 0.68F));
        TRANSFORMATION = new SimpleModelState(ImmutableMap.copyOf(map));
    }

    @Override
    public ModelState getModelTransform() {
        return TRANSFORMATION;
    }

    private CCModel baseGui;
    private CCModel materialGui;
    private CCModel traceGui;
    private CCModel bladeGui;
    private CCModel gemGui;

//    private VBORenderType guiBaseVBOType;
//    private VBORenderType guiMaterialVBOType;
//    private VBORenderType guiMaterialChaosVBOType;
//    private VBORenderType guiTraceVBOType;
//    private VBORenderType guiBladeVBOType;
//    private VBORenderType guiGemVBOType;

    @Nullable
    protected LivingEntity entity;
    @Nullable
    protected ClientLevel world;

    private StaffModelEffect effectRenderer = new StaffModelEffect();

    public RenderModularStaff(TechLevel techLevel) {
        super(techLevel, "staff");
        Map<String, CCModel> model = new OBJParser(new ResourceLocation(DraconicEvolution.MODID, "models/item/equipment/staff.obj")).ignoreMtl().parse();
//        baseModel = CCModel.combine(Arrays.asList(model.get("handle"), model.get("head_connection"), model.get("cage_connection"))).backfacedCopy();
//        materialModel = CCModel.combine(Arrays.asList(model.get("head"), model.get("crystal_cage"))).backfacedCopy();
//        traceModel = model.get("trace");
//        bladeModel = model.get("blade").backfacedCopy();
//        gemModel = CCModel.combine(Arrays.asList(model.get("focus_gem"), model.get("energy_crystal"))).backfacedCopy();

//        initBaseVBO();
//        initMaterialVBO();
//        initTraceVBO();
//        initBladeVBO();
//        initGemVBO();

        model = new OBJParser(new ResourceLocation(DraconicEvolution.MODID, "models/item/equipment/staff_gui.obj")).ignoreMtl().parse();
        baseGui = CCModel.combine(Arrays.asList(model.get("handle"), model.get("head_connection"))).backfacedCopy();
        materialGui = model.get("head").backfacedCopy();
        traceGui = model.get("trace");
        bladeGui = model.get("blade").backfacedCopy();
        gemGui = model.get("focus_gem").backfacedCopy();

//        guiBaseVBOType = new VBORenderType(modelType, DefaultVertexFormat.POSITION_TEX_COLOR_NORMAL, (format, builder) -> {
//            CCRenderState ccrs = CCRenderState.instance();
//            ccrs.reset();
//            ccrs.bind(builder, format);
//            baseGui.render(ccrs);
//        });
//
//        guiMaterialVBOType = new VBORenderType(modelGuiType, DefaultVertexFormat.POSITION_TEX_COLOR_NORMAL, (format, builder) -> {
//            CCRenderState ccrs = CCRenderState.instance();
//            ccrs.reset();
//            ccrs.bind(builder, format);
//            materialGui.render(ccrs);
//        });
//
//        guiMaterialChaosVBOType = new VBORenderType(chaosType, DefaultVertexFormat.POSITION_TEX_COLOR_NORMAL, (format, builder) -> {
//            CCRenderState ccrs = CCRenderState.instance();
//            ccrs.reset();
//            ccrs.bind(builder, format);
//            materialGui.render(ccrs);
//        });
//
//        guiTraceVBOType = new VBORenderType(shaderParentType, DefaultVertexFormat.POSITION_TEX_COLOR_NORMAL, (format, builder) -> {
//            CCRenderState ccrs = CCRenderState.instance();
//            ccrs.reset();
//            ccrs.bind(builder, format);
//            traceGui.render(ccrs);
//        });
//
//        guiBladeVBOType = new VBORenderType(shaderParentType, DefaultVertexFormat.POSITION_TEX_COLOR_NORMAL, (format, builder) -> {
//            CCRenderState ccrs = CCRenderState.instance();
//            ccrs.reset();
//            ccrs.bind(builder, format);
//            bladeGui.render(ccrs);
//        });
//
//        guiGemVBOType = new VBORenderType(shaderParentType, DefaultVertexFormat.POSITION_TEX_COLOR_NORMAL, (format, builder) -> {
//            CCRenderState ccrs = CCRenderState.instance();
//            ccrs.reset();
//            ccrs.bind(builder, format);
//            gemGui.render(ccrs);
//        });
    }

    private final ItemOverrides overrideList = new ItemOverrides() {
        @Override
        public BakedModel resolve(BakedModel originalModel, ItemStack stack, @Nullable ClientLevel world, @Nullable LivingEntity entity, int light) {
            RenderModularStaff.this.entity = entity;
            RenderModularStaff.this.world = world == null ? entity == null ? null : (ClientLevel) entity.level : null;
            return originalModel;
        }
    };

    @Override
    public ItemOverrides getOverrides() {
        return overrideList;
    }

    //TODO want to combine the swing and equip animation somehow so the 'draw back / return' after a stab corresponds to the equip cooldown
    @Override
    public void renderTool(CCRenderState ccrs, ItemStack stack, TransformType transform, Matrix4 mat, MultiBufferSource buffers, boolean gui) {
//        float flair = 0F;
//        if (entity != null && entity.getMainHandItem() == stack) {
//            flair = MathHelper.interpolate(entity.oAttackAnim, entity.attackAnim, Minecraft.getInstance().getFrameTime());
//            flair = MathHelper.clip(flair * 5F, 0F, 1F);
//        }
//
//        handleArmPose(stack, transform, mat);
//        if (gui) {
//            transform(mat, 0.19, 0.19, 0.5, 1.1);
//
//            getter.getBuffer(guiBaseVBOType.withMatrix(mat).withLightMap(packedLight));
//            if (techLevel == TechLevel.CHAOTIC && DEConfig.toolShaders) {
//                getter.getBuffer(guiMaterialChaosVBOType.withMatrix(mat).withLightMap(packedLight).withState(getShaderType(chaosType, chaosShader)));
//            } else {
//                getter.getBuffer(guiMaterialVBOType.withMatrix(mat).withLightMap(packedLight));
//            }
//
//            if (DEConfig.toolShaders) {
//                getter.getBuffer(guiTraceVBOType.withMatrix(mat).withLightMap(packedLight).withState(getShaderType(shaderParentType, techLevel, traceShader, flair)));
//                getter.getBuffer(guiBladeVBOType.withMatrix(mat).withLightMap(packedLight).withState(getShaderType(shaderParentType, techLevel, bladeShader, flair)));
//                getter.getBuffer(guiGemVBOType.withMatrix(mat).withLightMap(packedLight).withState(getShaderType(shaderParentType, techLevel, gemShader, flair)));
//            } else {
//                getter.getBuffer(guiTraceVBOType.withMatrix(mat).withLightMap(packedLight));
//                getter.getBuffer(guiBladeVBOType.withMatrix(mat).withLightMap(packedLight));
//                getter.getBuffer(guiGemVBOType.withMatrix(mat).withLightMap(packedLight));
//            }
//            return;
//        }
//
//        if (transform == TransformType.FIXED || transform == TransformType.GROUND || transform == TransformType.NONE) {
//            transform(mat, 0.6, 0.6, 0.5, 0.75);
//        } else {
//            transform(mat, 0.27, 0.27, 0.5, 1.125);
//            if (transform == TransformType.FIRST_PERSON_LEFT_HAND || transform == TransformType.THIRD_PERSON_LEFT_HAND) {
//                mat.rotate(45 * -MathHelper.torad, Vector3.Z_NEG);
//            } else {
//                mat.rotate(45 * MathHelper.torad, Vector3.Z_NEG);
//            }
//        }
//
//        getter.getBuffer(baseVBOType.withMatrix(mat).withLightMap(packedLight));
//        if (techLevel == TechLevel.CHAOTIC && DEConfig.toolShaders) {
//            getter.getBuffer(materialChaosVBOType.withMatrix(mat).withLightMap(packedLight).withState(getShaderType(chaosType, chaosShader)));
//        } else {
//            getter.getBuffer(materialVBOType.withMatrix(mat).withLightMap(packedLight));
//        }
//
//        if (DEConfig.toolShaders) {
//            getter.getBuffer(traceVBOType.withMatrix(mat).withLightMap(packedLight).withState(getShaderType(shaderParentType, techLevel, traceShader, flair)));
//            getter.getBuffer(bladeVBOType.withMatrix(mat).withLightMap(packedLight).withState(getShaderType(shaderParentType, techLevel, bladeShader, flair)));
//            getter.getBuffer(gemVBOType.withMatrix(mat).withLightMap(packedLight).withState(getShaderType(shaderParentType, techLevel, gemShader, flair)));
//        } else {
//            getter.getBuffer(traceVBOType.withMatrix(mat).withLightMap(packedLight));
//            getter.getBuffer(bladeVBOType.withMatrix(mat).withLightMap(packedLight));
//            getter.getBuffer(gemVBOType.withMatrix(mat).withLightMap(packedLight));
//        }
//
//        ToolRenderBase.endBatch(getter);
//
//        Minecraft mc = Minecraft.getInstance();
//        mat.rotate(torad(90), Vector3.X_NEG);
//        mat.translate(-0.5, 0.1, -0.5);
//        effectRenderer.renderEffect(mat, getter, mc.getFrameTime(), techLevel);
    }


    private void handleArmPose(ItemStack stack, TransformType transform, Matrix4 mat) {
        if (isThirdPerson(transform)) {
            if (transform == TransformType.THIRD_PERSON_RIGHT_HAND) {
                mat.rotate(torad(-15), new Vector3(-0.5, 0.5, 0));
            } else {
                mat.rotate(torad(15), new Vector3(-0.5, 0.5, 0));
            }
            if (entity != null){
                double anim = entity.attackAnim;
                mat.translate(0.125 * anim, 0.125 * anim, 0);
                mat.rotate(torad(-20) * anim, Vector3.Z_POS);
            }
        }
    }

    private boolean isThirdPerson(TransformType transform) {
        return transform == TransformType.THIRD_PERSON_RIGHT_HAND || transform == TransformType.THIRD_PERSON_LEFT_HAND;
    }

    public static void doMixinStuff(LivingEntity entity, PlayerModel<?> model) {
        ItemStack mainHand = entity.getMainHandItem();
        ItemStack offHand = entity.getOffhandItem();
        boolean rightHanded = entity.getMainArm() == HumanoidArm.RIGHT;
        boolean hasMain = mainHand.getItem() instanceof ModularStaff;
        boolean hasOff = offHand.getItem() instanceof ModularStaff;

        if (/*hasOff || */hasMain) {
//            if (hasOff) {
//                setStaffPose(entity, model.bipedRightArm, model.bipedLeftArm, model.bipedBody, model.bipedHead, rightHanded, !hasMain);
//            }
            if (hasMain) {
                setStaffPose(entity, model.rightArm, model.leftArm, model.body, model.head, !rightHanded, !hasOff);
            }
            model.leftPants.copyFrom(model.leftLeg);
            model.rightPants.copyFrom(model.rightLeg);
            model.leftSleeve.copyFrom(model.leftArm);
            model.rightSleeve.copyFrom(model.rightArm);
            model.jacket.copyFrom(model.body);
        }
    }

    public static void setStaffPose(LivingEntity entity, ModelPart rightArm, ModelPart leftArm, ModelPart body, ModelPart head, boolean lefthand, boolean bothHands) {
        if (lefthand) {
            //Yaw
            leftArm.yRot = head.yRot + torad(15F);
            //Pitch
            leftArm.xRot = head.xRot - torad(60F) - torad(20F * entity.attackAnim);
            if (bothHands) {
                rightArm.xRot = head.xRot - torad(60F) - torad(20F * entity.attackAnim);
                rightArm.yRot = head.yRot - torad(45F);
            }
        } else {
            //Yaw
            rightArm.yRot = head.yRot - torad(15F);
            //Pitch
            rightArm.xRot = head.xRot - torad(60F) - torad(20F * entity.attackAnim);
            if (bothHands) {
                leftArm.xRot = head.xRot - torad(60F) - torad(20F * entity.attackAnim);
                leftArm.yRot = head.yRot + torad(45F);
            }
        }
    }

    public static float torad(double degrees) {
        return (float) (degrees * MathHelper.torad);
    }
}
