package com.brandon3055.draconicevolution.client.render.item;

import codechicken.lib.math.MathHelper;
import codechicken.lib.model.PerspectiveModelState;
import codechicken.lib.render.CCModel;
import codechicken.lib.render.CCRenderState;
import codechicken.lib.render.model.OBJParser;
import codechicken.lib.util.TransformUtils;
import codechicken.lib.vec.Matrix4;
import codechicken.lib.vec.Vector3;
import com.brandon3055.brandonscore.api.TechLevel;
import com.brandon3055.brandonscore.client.render.RenderUtils;
import com.brandon3055.draconicevolution.DraconicEvolution;
import com.brandon3055.draconicevolution.client.render.modelfx.StaffModelEffect;
import com.brandon3055.draconicevolution.items.equipment.ModularStaff;
import com.google.common.collect.ImmutableMap;
import com.mojang.math.Transformation;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by brandon3055 on 22/5/20.
 */
public class RenderModularStaff extends ToolRenderBase {

    private static final StaffModelEffect effectRenderer = new StaffModelEffect();
    private static final PerspectiveModelState TRANSFORMATION;

    static {
        // @formatter:off
        Map<ItemDisplayContext, Transformation> map = new HashMap<>();
        map.put(ItemDisplayContext.GROUND,                   TransformUtils.create(   0F,   2F,    0F, 0F,   0F,  0F,  0.5F));
        map.put(ItemDisplayContext.FIXED,                    TransformUtils.create(   0F,   0F,    0F, 0F, 180F,  0F,    1F));

        map.put(ItemDisplayContext.THIRD_PERSON_RIGHT_HAND,  TransformUtils.create(   0F,   1.5F,  -6.5F, 0F,  90F, -15F, 0.85F));
        map.put(ItemDisplayContext.THIRD_PERSON_LEFT_HAND,   TransformUtils.create(   0F,   1.5F,  -6.5F, 0F, -90F,  15F, 0.85F));

        map.put(ItemDisplayContext.FIRST_PERSON_RIGHT_HAND,  TransformUtils.create(1.13F, 3.2F, 1.13F, 0F,  90F, -45F, 0.68F));
        map.put(ItemDisplayContext.FIRST_PERSON_LEFT_HAND,   TransformUtils.create(1.13F, 3.2F, 1.13F, 0F,  -90F, 45F, 0.68F));
        TRANSFORMATION = new PerspectiveModelState(ImmutableMap.copyOf(map));
        // @formatter:on
    }

    private final ToolPart baseGuiPart;
    private final ToolPart materialGuiPart;
    private final ToolPart traceGuiPart;
    private final ToolPart bladeGuiPart;
    private final ToolPart gemGuiPart;

    private final ToolPart basePart;
    private final ToolPart materialPart;
    private final ToolPart tracePart;
    private final ToolPart bladePart;
    private final ToolPart gemPart;

    @Nullable
    protected LivingEntity entity;
    @Nullable
    protected ClientLevel world;

    public RenderModularStaff(TechLevel techLevel) {
        super(techLevel, "staff");

        Map<String, CCModel> guiModel = new OBJParser(new ResourceLocation(DraconicEvolution.MODID, "models/item/equipment/staff_gui.obj")).ignoreMtl().parse();
        baseGuiPart = basePart(CCModel.combine(Arrays.asList(guiModel.get("handle"), guiModel.get("head_connection"))).backfacedCopy());
        materialGuiPart = materialPart(guiModel.get("head").backfacedCopy());
        traceGuiPart = tracePart(guiModel.get("trace"));
        bladeGuiPart = bladePart(guiModel.get("blade").backfacedCopy());
        gemGuiPart = gemPart(guiModel.get("focus_gem").backfacedCopy());

        Map<String, CCModel> model = new OBJParser(new ResourceLocation(DraconicEvolution.MODID, "models/item/equipment/staff.obj")).ignoreMtl().parse();
        basePart = basePart(CCModel.combine(Arrays.asList(model.get("handle"), model.get("head_connection"), model.get("cage_connection"))).backfacedCopy());
        materialPart = materialPart(CCModel.combine(Arrays.asList(model.get("head"), model.get("crystal_cage"))).backfacedCopy());
        tracePart = tracePart(model.get("trace"));
        bladePart = bladePart(model.get("blade").backfacedCopy());
        gemPart = gemPart(CCModel.combine(Arrays.asList(model.get("focus_gem"), model.get("energy_crystal"))).backfacedCopy());
    }

    private final ItemOverrides overrideList = new ItemOverrides() {
        @Override
        public BakedModel resolve(BakedModel originalModel, ItemStack stack, @Nullable ClientLevel world, @Nullable LivingEntity entity, int light) {
            RenderModularStaff.this.entity = entity;
            RenderModularStaff.this.world = world == null ? entity == null ? null : (ClientLevel) entity.level() : null;
            return originalModel;
        }
    };

    @Override
    public ItemOverrides getOverrides() {
        return overrideList;
    }

    @Override
    public @Nullable PerspectiveModelState getModelState() {
        return TRANSFORMATION;
    }

    //TODO want to combine the swing and equip animation somehow so the 'draw back / return' after a stab corresponds to the equip cooldown
    @Override
    public void renderTool(CCRenderState ccrs, ItemStack stack, ItemDisplayContext transform, Matrix4 mat, MultiBufferSource buffers, boolean gui) {
        float flair = 0F;
        if (entity != null && entity.getMainHandItem() == stack) {
            flair = MathHelper.interpolate(entity.oAttackAnim, entity.attackAnim, Minecraft.getInstance().getFrameTime());
            flair = MathHelper.clip(flair * 5F, 0F, 1F);
        }

        handleArmPose(stack, transform, mat);
        if (gui) {
            transform(mat, 0.19, 0.19, 0.5, 1.1);

            baseGuiPart.render(transform, buffers, mat);
            materialGuiPart.render(transform, buffers, mat);
            traceGuiPart.render(transform, buffers, mat);
            bladeGuiPart.render(transform, buffers, mat);
            gemGuiPart.render(transform, buffers, mat);
            return;
        }

        if (transform == ItemDisplayContext.FIXED || transform == ItemDisplayContext.GROUND || transform == ItemDisplayContext.NONE) {
            transform(mat, 0.6, 0.6, 0.5, 0.75);
        } else {
            transform(mat, 0.27, 0.27, 0.5, 1.125);
            if (transform == ItemDisplayContext.FIRST_PERSON_LEFT_HAND || transform == ItemDisplayContext.THIRD_PERSON_LEFT_HAND) {
                mat.rotate(45 * -MathHelper.torad, Vector3.Z_NEG);
            } else {
                mat.rotate(45 * MathHelper.torad, Vector3.Z_NEG);
            }
        }

        // TODO flair
        basePart.render(transform, buffers, mat);
        materialPart.render(transform, buffers, mat);
        tracePart.render(transform, buffers, mat, flair);
        bladePart.render(transform, buffers, mat, flair);
        gemPart.render(transform, buffers, mat, flair);

        RenderUtils.endBatch(buffers);

        Minecraft mc = Minecraft.getInstance();
        mat.rotate(torad(90), Vector3.X_NEG);
        mat.translate(-0.5, 0.1, -0.5);
        effectRenderer.renderEffect(mat, buffers, mc.getFrameTime(), techLevel);
    }

    private void handleArmPose(ItemStack stack, ItemDisplayContext transform, Matrix4 mat) {
        if (isThirdPerson(transform)) {
            if (transform == ItemDisplayContext.THIRD_PERSON_RIGHT_HAND) {
                mat.rotate(torad(-15), new Vector3(-0.5, 0.5, 0));
            } else {
                mat.rotate(torad(15), new Vector3(-0.5, 0.5, 0));
            }
            if (entity != null) {
                double anim = entity.attackAnim;
                mat.translate(0.125 * anim, 0.125 * anim, 0);
                mat.rotate(torad(-20) * anim, Vector3.Z_POS);
            }
        }
    }

    private boolean isThirdPerson(ItemDisplayContext transform) {
        return transform == ItemDisplayContext.THIRD_PERSON_RIGHT_HAND || transform == ItemDisplayContext.THIRD_PERSON_LEFT_HAND;
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

    //@formatter:off //This is not cursed at all! idk what your talking about!
    public static class STAFF_DRACONIC extends RenderModularStaff { public STAFF_DRACONIC() {super(TechLevel.DRACONIC);}}
    public static class STAFF_CHAOTIC extends RenderModularStaff { public STAFF_CHAOTIC() {super(TechLevel.CHAOTIC);}}
    //@formatter::on
}
