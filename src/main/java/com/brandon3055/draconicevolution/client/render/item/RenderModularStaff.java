package com.brandon3055.draconicevolution.client.render.item;

import codechicken.lib.math.MathHelper;
import codechicken.lib.render.CCModel;
import codechicken.lib.render.CCRenderState;
import codechicken.lib.render.OBJParser;
import codechicken.lib.render.buffer.TransformingVertexBuilder;
import codechicken.lib.render.buffer.VBORenderType;
import codechicken.lib.util.TransformUtils;
import codechicken.lib.vec.Matrix4;
import codechicken.lib.vec.Vector3;
import com.brandon3055.brandonscore.api.TechLevel;
import com.brandon3055.brandonscore.client.utils.GuiHelper;
import com.brandon3055.draconicevolution.DEConfig;
import com.brandon3055.draconicevolution.DraconicEvolution;
import com.brandon3055.draconicevolution.client.render.modelfx.ModelEffect;
import com.brandon3055.draconicevolution.client.render.modelfx.ToolModelEffect;
import com.brandon3055.draconicevolution.items.equipment.ModularStaff;
import com.google.common.collect.ImmutableMap;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.model.PlayerModel;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.IModelTransform;
import net.minecraft.client.renderer.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.model.ItemOverrideList;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.HandSide;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.TransformationMatrix;
import net.minecraftforge.client.model.SimpleModelTransform;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by brandon3055 on 22/5/20.
 */
public class RenderModularStaff extends ToolRenderBase {

    private static SimpleModelTransform TRANSFORMATION;
    static {
        Map<TransformType, TransformationMatrix> map = new HashMap<>();
        map.put(TransformType.GROUND,                   TransformUtils.create(   0F,   2F,    0F, 0F,   0F,  0F,  0.5F));
        map.put(TransformType.FIXED,                    TransformUtils.create(   0F,   0F,    0F, 0F, 180F,  0F,    1F));

        map.put(TransformType.THIRD_PERSON_RIGHT_HAND,  TransformUtils.create(   0F,   1.5F,  -6.5F, 0F,  90F, -15F, 0.85F));
        map.put(TransformType.THIRD_PERSON_LEFT_HAND,   TransformUtils.create(   0F,   1.5F,  -6.5F, 0F, -90F,  15F, 0.85F));

        map.put(TransformType.FIRST_PERSON_RIGHT_HAND,  TransformUtils.create(1.13F, 3.2F, 1.13F, 0F,  90F, -45F, 0.68F));
        map.put(TransformType.FIRST_PERSON_LEFT_HAND,   TransformUtils.create(1.13F, 3.2F, 1.13F, 0F,  -90F, 45F, 0.68F));
        TRANSFORMATION = new SimpleModelTransform(ImmutableMap.copyOf(map));
    }

    @Override
    public IModelTransform getModelTransform() {
        return TRANSFORMATION;
    }

    private CCModel baseGui;
    private CCModel materialGui;
    private CCModel traceGui;
    private CCModel bladeGui;
    private CCModel gemGui;

    private VBORenderType guiBaseVBOType;
    private VBORenderType guiMaterialVBOType;
    private VBORenderType guiMaterialChaosVBOType;
    private VBORenderType guiTraceVBOType;
    private VBORenderType guiBladeVBOType;
    private VBORenderType guiGemVBOType;

    @Nullable
    protected LivingEntity entity;
    @Nullable
    protected ClientWorld world;

    private ToolModelEffect effectRenderer = new ToolModelEffect();

    public RenderModularStaff(TechLevel techLevel) {
        super(techLevel, "staff");
        Map<String, CCModel> model = OBJParser.parseModels(new ResourceLocation(DraconicEvolution.MODID, "models/item/equipment/staff.obj"), GL11.GL_TRIANGLES, null);
        baseModel = CCModel.combine(Arrays.asList(model.get("handle"), model.get("head_connection"), model.get("cage_connection"))).backfacedCopy();
        materialModel = CCModel.combine(Arrays.asList(model.get("head"), model.get("crystal_cage"))).backfacedCopy();
        traceModel = model.get("trace");
        bladeModel = model.get("blade").backfacedCopy();
        gemModel = CCModel.combine(Arrays.asList(model.get("focus_gem"), model.get("energy_crystal"))).backfacedCopy();

        initBaseVBO();
        initMaterialVBO();
        initTraceVBO();
        initBladeVBO();
        initGemVBO();

        model = OBJParser.parseModels(new ResourceLocation(DraconicEvolution.MODID, "models/item/equipment/staff_gui.obj"), GL11.GL_TRIANGLES, null);
        baseGui = CCModel.combine(Arrays.asList(model.get("handle"), model.get("head_connection"))).backfacedCopy();
        materialGui = model.get("head").backfacedCopy();
        traceGui = model.get("trace");
        bladeGui = model.get("blade").backfacedCopy();
        gemGui = model.get("focus_gem").backfacedCopy();

        guiBaseVBOType = new VBORenderType(modelType, DefaultVertexFormats.POSITION_TEX_COLOR_NORMAL, (format, builder) -> {
            CCRenderState ccrs = CCRenderState.instance();
            ccrs.reset();
            ccrs.bind(builder, format);
            baseGui.render(ccrs);
        });

        guiMaterialVBOType = new VBORenderType(modelGuiType, DefaultVertexFormats.POSITION_TEX_COLOR_NORMAL, (format, builder) -> {
            CCRenderState ccrs = CCRenderState.instance();
            ccrs.reset();
            ccrs.bind(builder, format);
            materialGui.render(ccrs);
        });

        guiMaterialChaosVBOType = new VBORenderType(chaosType, DefaultVertexFormats.POSITION_TEX_COLOR_NORMAL, (format, builder) -> {
            CCRenderState ccrs = CCRenderState.instance();
            ccrs.reset();
            ccrs.bind(builder, format);
            materialGui.render(ccrs);
        });

        guiTraceVBOType = new VBORenderType(shaderParentType, DefaultVertexFormats.POSITION_TEX_COLOR_NORMAL, (format, builder) -> {
            CCRenderState ccrs = CCRenderState.instance();
            ccrs.reset();
            ccrs.bind(builder, format);
            traceGui.render(ccrs);
        });

        guiBladeVBOType = new VBORenderType(shaderParentType, DefaultVertexFormats.POSITION_TEX_COLOR_NORMAL, (format, builder) -> {
            CCRenderState ccrs = CCRenderState.instance();
            ccrs.reset();
            ccrs.bind(builder, format);
            bladeGui.render(ccrs);
        });

        guiGemVBOType = new VBORenderType(shaderParentType, DefaultVertexFormats.POSITION_TEX_COLOR_NORMAL, (format, builder) -> {
            CCRenderState ccrs = CCRenderState.instance();
            ccrs.reset();
            ccrs.bind(builder, format);
            gemGui.render(ccrs);
        });
    }

    private final ItemOverrideList overrideList = new ItemOverrideList() {
        @Override
        public IBakedModel resolve(IBakedModel originalModel, ItemStack stack, @Nullable ClientWorld world, @Nullable LivingEntity entity) {
            RenderModularStaff.this.entity = entity;
            RenderModularStaff.this.world = world == null ? entity == null ? null : (ClientWorld) entity.level : null;
            return originalModel;
        }
    };

    @Override
    public ItemOverrideList getOverrides() {
        return overrideList;
    }

    //TODO want to combine the swing and equip animation somehow so the 'draw back / return' after a stab corresponds to the equip cooldown
    @Override
    public void renderTool(CCRenderState ccrs, ItemStack stack, TransformType transform, Matrix4 mat, MatrixStack mStack, IRenderTypeBuffer getter, boolean gui, int packedLight) {
        float flair = 0F;
        if (entity != null && entity.getMainHandItem() == stack) {
            flair = MathHelper.interpolate(entity.oAttackAnim, entity.attackAnim, Minecraft.getInstance().getFrameTime());
            flair = MathHelper.clip(flair * 5F, 0F, 1F);
        }

        handleArmPose(stack, transform, mat);
        if (gui) {
            transform(mat, 0.19, 0.19, 0.5, 1.1);

            getter.getBuffer(guiBaseVBOType.withMatrix(mat).withLightMap(packedLight));
            if (techLevel == TechLevel.CHAOTIC && DEConfig.toolShaders) {
                getter.getBuffer(guiMaterialChaosVBOType.withMatrix(mat).withLightMap(packedLight).withState(getShaderType(chaosType, chaosShader)));
            } else {
                getter.getBuffer(guiMaterialVBOType.withMatrix(mat).withLightMap(packedLight));
            }

            if (DEConfig.toolShaders) {
                getter.getBuffer(guiTraceVBOType.withMatrix(mat).withLightMap(packedLight).withState(getShaderType(shaderParentType, techLevel, traceShader, flair)));
                getter.getBuffer(guiBladeVBOType.withMatrix(mat).withLightMap(packedLight).withState(getShaderType(shaderParentType, techLevel, bladeShader, flair)));
                getter.getBuffer(guiGemVBOType.withMatrix(mat).withLightMap(packedLight).withState(getShaderType(shaderParentType, techLevel, gemShader, flair)));
            } else {
                getter.getBuffer(guiTraceVBOType.withMatrix(mat).withLightMap(packedLight));
                getter.getBuffer(guiBladeVBOType.withMatrix(mat).withLightMap(packedLight));
                getter.getBuffer(guiGemVBOType.withMatrix(mat).withLightMap(packedLight));
            }
            return;
        }

        if (transform == TransformType.FIXED || transform == TransformType.GROUND || transform == TransformType.NONE) {
//            transform(mat, 0.6, 0.6, 0.5, 1.125);
            transform(mat, 0.6, 0.6, 0.5, 0.75);
        } else {
            transform(mat, 0.27, 0.27, 0.5, 1.125);
            if (transform == TransformType.FIRST_PERSON_LEFT_HAND || transform == TransformType.THIRD_PERSON_LEFT_HAND) {
                mat.rotate(45 * -MathHelper.torad, Vector3.Z_NEG);
            } else {
                mat.rotate(45 * MathHelper.torad, Vector3.Z_NEG);
            }
        }

        getter.getBuffer(baseVBOType.withMatrix(mat).withLightMap(packedLight));
        if (techLevel == TechLevel.CHAOTIC && DEConfig.toolShaders) {
            getter.getBuffer(materialChaosVBOType.withMatrix(mat).withLightMap(packedLight).withState(getShaderType(chaosType, chaosShader)));
        } else {
            getter.getBuffer(materialVBOType.withMatrix(mat).withLightMap(packedLight));
        }

        if (DEConfig.toolShaders) {
            getter.getBuffer(traceVBOType.withMatrix(mat).withLightMap(packedLight).withState(getShaderType(shaderParentType, techLevel, traceShader, flair)));
            getter.getBuffer(bladeVBOType.withMatrix(mat).withLightMap(packedLight).withState(getShaderType(shaderParentType, techLevel, bladeShader, flair)));
            getter.getBuffer(gemVBOType.withMatrix(mat).withLightMap(packedLight).withState(getShaderType(shaderParentType, techLevel, gemShader, flair)));
        } else {
            getter.getBuffer(traceVBOType.withMatrix(mat).withLightMap(packedLight));
            getter.getBuffer(bladeVBOType.withMatrix(mat).withLightMap(packedLight));
            getter.getBuffer(gemVBOType.withMatrix(mat).withLightMap(packedLight));
        }
        ((IRenderTypeBuffer.Impl) getter).endBatch();

        Minecraft mc = Minecraft.getInstance();
        mat.rotate(torad(90), Vector3.X_NEG);
        mat.translate(-0.5, 0.1, -0.5);
        effectRenderer.renderEffect(mat, getter, mc.getFrameTime(), techLevel);
//        new ModelEffect.DebugEffect().renderEffect(mat, getter, mc.getRenderPartialTicks());

//        testParticleRender(ccrs, stack, transform, mat, mStack, getter, gui);
//        if (true) return;
//
//
//        Minecraft mc = Minecraft.getInstance();
//        float partialTicks = mc.getRenderPartialTicks();
//        ActiveRenderInfo renderInfo = mc.getRenderManager().info;
//
//
//        RenderSystem.pushMatrix();
//        RenderSystem.disableCull();
//        mat.rotate(90 * MathHelper.torad, new Vector3(0, 1, 0));
//        mat.glApply();
//        IVertexBuilder builder = getter.getBuffer(GuiHelper.TRANS_TYPE);
//
//
//        Vector3d vector3d = renderInfo.getProjectedView();
//        float f = (float) (net.minecraft.util.math.MathHelper.lerp((double) partialTicks, 0, 0) - vector3d.getX());
//        float f1 = (float) (net.minecraft.util.math.MathHelper.lerp((double) partialTicks, 0, 0) - vector3d.y);
//        float f2 = (float) (net.minecraft.util.math.MathHelper.lerp((double) partialTicks, 0, 0) - vector3d.getZ());
//        Quaternion quaternion;
////        if (this.particleAngle == 0.0F) {
//        quaternion = renderInfo.getRotation();
////        } else {
////            quaternion = new Quaternion(renderInfo.getRotation());
////            float f3 = net.minecraft.util.math.MathHelper.lerp(partialTicks, this.prevParticleAngle, this.particleAngle);
////            quaternion.multiply(Vector3f.ZP.rotation(f3));
////        }
//
//        Vector3f vector3f1 = new Vector3f(-1.0F, -1.0F, 0.0F);
//        vector3f1.transform(quaternion);
//        Vector3f[] avector3f = new Vector3f[]{new Vector3f(-1.0F, -1.0F, 0.0F), new Vector3f(-1.0F, 1.0F, 0.0F), new Vector3f(1.0F, 1.0F, 0.0F), new Vector3f(1.0F, -1.0F, 0.0F)};
//        float f4 = 1;//this.getScale(partialTicks);
//
//        for (int i = 0; i < 4; ++i) {
//            Vector3f vector3f = avector3f[i];
//            vector3f.transform(quaternion);
//            vector3f.mul(f4);
//            vector3f.add(f, f1, f2);
//        }
//
//        float f7 = 0;//this.getMinU();
//        float f8 = 1;//this.getMaxU();
//        float f5 = 0;//this.getMinV();
//        float f6 = 1;// this.getMaxV();
//        int j = 240;//this.getBrightnessForRender(partialTicks);
//        builder.pos((double) avector3f[0].getX(), (double) avector3f[0].getY(), (double) avector3f[0].getZ())/*.tex(f8, f6)*/.color(1F, 1F, 1F, 1F)/*.lightmap(j)*/.endVertex();
//        builder.pos((double) avector3f[1].getX(), (double) avector3f[1].getY(), (double) avector3f[1].getZ())/*.tex(f8, f5)*/.color(1F, 1F, 1F, 1F)/*.lightmap(j)*/.endVertex();
//        builder.pos((double) avector3f[2].getX(), (double) avector3f[2].getY(), (double) avector3f[2].getZ())/*.tex(f7, f5)*/.color(1F, 1F, 1F, 1F)/*.lightmap(j)*/.endVertex();
//        builder.pos((double) avector3f[3].getX(), (double) avector3f[3].getY(), (double) avector3f[3].getZ())/*.tex(f7, f6)*/.color(1F, 1F, 1F, 1F)/*.lightmap(j)*/.endVertex();
//
//
////        new TransformationMatrix(mat.toMatrix4f()).getRotationLeft();
//
//
////        IVertexBuilder builder = getter.getBuffer(BCSprites.GUI_TEX_TYPE);
////        TextureAtlasSprite sprite = BCSprites.get("dark/gear").getSprite();
////        float x = 0;
////        float y = 0;
////        float z = 0;
////        float width = 1;
////        float height = 1;
////
//////        builder.pos(x,          y + height, z).color(1F, 1F, 1F, 1F).tex(sprite.getMinU(), sprite.getMaxV()).endVertex();
//////        builder.pos(x + width,  y + height, z).color(1F, 1F, 1F, 1F).tex(sprite.getMaxU(), sprite.getMaxV()).endVertex();
//////        builder.pos(x + width,  y,          z).color(1F, 1F, 1F, 1F).tex(sprite.getMaxU(), sprite.getMinV()).endVertex();
//////        builder.pos(x,          y,          z).color(1F, 1F, 1F, 1F).tex(sprite.getMinU(), sprite.getMinV()).endVertex();
////        builder.pos(x,          y + height, z).color(1F, 1F, 1F, 1F).endVertex();
////        builder.pos(x + width,  y + height, z).color(1F, 1F, 1F, 1F).endVertex();
////        builder.pos(x + width,  y,          z).color(1F, 1F, 1F, 1F).endVertex();
////        builder.pos(x,          y,          z).color(1F, 1F, 1F, 1F).endVertex();
//
//        ((IRenderTypeBuffer.Impl) getter).finish();
//
//        RenderSystem.popMatrix();
    }

    private void testParticleRender(CCRenderState ccrs, ItemStack stack, TransformType transform, Matrix4 mat, MatrixStack mStack, IRenderTypeBuffer getter, boolean gui) {
        Minecraft mc = Minecraft.getInstance();
        float partialTicks = mc.getFrameTime();
        ActiveRenderInfo renderInfo = mc.getEntityRenderDispatcher().camera;


        RenderSystem.pushMatrix();
        RenderSystem.disableCull();
//        mat.translate(0, 0, -1);

//        mat.rotate(new Rotation(new Quat(new TransformationMatrix(mat.toMatrix4f()).getRotationLeft())));

        mStack.translate(0, 1, 1);


//        mStack.rotate(new TransformationMatrix(mStack.getLast().getMatrix()).getRotationLeft());


//        mat.apply(new Rotation(new Quat(new TransformationMatrix(mat.toMatrix4f()).getRotationLeft())).inverse());
//        mat.glApply();

//        Quaternion quaternion = new TransformationMatrix(mat.toMatrix4f()).getRotationLeft();


//        mat.apply(new Rotation(new Quat(undoQuat)).inverse());

//        mat.apply(new Vector3(1, 0, 0).apply(mat));

//        mat.apply(new Rotation(new Quat(renderInfo.getRotation())));

        Vector3 vec = new Vector3(0, 0, -1);
        mat.applyN(vec);


//        Matrix4f matrix4f = mat.toMatrix4f().copy();
//        matrix4f.invert();
//        matrix4f(vec);

        IVertexBuilder builder = new TransformingVertexBuilder(getter.getBuffer(GuiHelper.TRANS_TYPE), mat);


        float x = 0;
        float y = 0;
        float z = 0;
        float width = 1;
        float height = 1;
        builder.vertex(x, y + height, z).color(1F, 1F, 1F, 1F).endVertex();
        builder.vertex(x + width, y + height, z).color(1F, 1F, 1F, 1F).endVertex();
        builder.vertex(x + width, y, z).color(1F, 1F, 1F, 1F).endVertex();
        builder.vertex(x, y, z).color(1F, 1F, 1F, 1F).endVertex();


//        Vector3 vector3f1 = new Vector3(-1.0F, -1.0F, 0.0F);
//        vector3f1.rotate(new Quat(quaternion));
//        Vector3[] avector3f = new Vector3[]{new Vector3(-1.0F, -1.0F, 0.0F), new Vector3(-1.0F, 1.0F, 0.0F), new Vector3(1.0F, 1.0F, 0.0F), new Vector3(1.0F, -1.0F, 0.0F)};
//        float f4 = 1;//this.getScale(partialTicks);
//
//        for (int i = 0; i < 4; ++i) {
//            Vector3 vector3f = avector3f[i];
////            vector3f.rotate(new Quat(quaternion));
//            vector3f.multiply(f4);
//            vector3f.apply(mat);
////            vector3f.add(f, f1, f2);
//        }
//
//        float f7 = 0;//this.getMinU();
//        float f8 = 1;//this.getMaxU();
//        float f5 = 0;//this.getMinV();
//        float f6 = 1;// this.getMaxV();
//        int j = 240;//this.getBrightnessForRender(partialTicks);
//        builder.pos((double) avector3f[0].x, (double) avector3f[0].y, (double) avector3f[0].z)/*.tex(f8, f6)*/.color(1F, 1F, 1F, 1F)/*.lightmap(j)*/.endVertex();
//        builder.pos((double) avector3f[1].x, (double) avector3f[1].y, (double) avector3f[1].z)/*.tex(f8, f5)*/.color(1F, 1F, 1F, 1F)/*.lightmap(j)*/.endVertex();
//        builder.pos((double) avector3f[2].x, (double) avector3f[2].y, (double) avector3f[2].z)/*.tex(f7, f5)*/.color(1F, 1F, 1F, 1F)/*.lightmap(j)*/.endVertex();
//        builder.pos((double) avector3f[3].x, (double) avector3f[3].y, (double) avector3f[3].z)/*.tex(f7, f6)*/.color(1F, 1F, 1F, 1F)/*.lightmap(j)*/.endVertex();
//


        if (getter instanceof IRenderTypeBuffer.Impl) {
            ((IRenderTypeBuffer.Impl) getter).endBatch();
        }
        RenderSystem.popMatrix();
    }

//    @Override
//    public IModelTransform getModelTransform() {
//        return TRANSFORMATION;
//    }

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

    //TODO. This is temporary to allow hotswap and debugging. Will move into mixin method when done.
    public static void doMixinStuff(LivingEntity entity, PlayerModel<?> model) {
        ItemStack mainHand = entity.getMainHandItem();
        ItemStack offHand = entity.getOffhandItem();
        boolean rightHanded = entity.getMainArm() == HandSide.RIGHT;
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

    public static void setStaffPose(LivingEntity entity, ModelRenderer rightArm, ModelRenderer leftArm, ModelRenderer body, ModelRenderer head, boolean lefthand, boolean bothHands) {
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