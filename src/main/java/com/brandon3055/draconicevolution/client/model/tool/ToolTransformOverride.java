package com.brandon3055.draconicevolution.client.model.tool;

import codechicken.lib.render.CCModelState;
import com.google.common.collect.ImmutableMap;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraftforge.common.model.TRSRTransformation;

import javax.vecmath.Vector3f;

/**
 * Created by brandon3055 on 1/09/2016.
 */
public class ToolTransformOverride {

    private final String key;
    public static final CCModelState STAFF_STATE;
    public static final CCModelState DR_SWORD_STATE;
    public static final CCModelState WY_SWORD_STATE;
    private static final TRSRTransformation flipX = new TRSRTransformation(null, null, new Vector3f(-1, 1, 1), null);

    static {
        TRSRTransformation thirdPerson = get(0, 3, 1, 0, 0, 0, 0.55f);
        TRSRTransformation firstPerson = get(1.13f, 3.2f, 1.13f, 0, -90, 25, 0.68f);
        ImmutableMap.Builder<ItemCameraTransforms.TransformType, TRSRTransformation> defaultItemBuilder = ImmutableMap.builder();
        defaultItemBuilder.put(ItemCameraTransforms.TransformType.GROUND, get(1, 4, 0, 0, 0, 0, 0.3f));
        defaultItemBuilder.put(ItemCameraTransforms.TransformType.FIXED, get(2, 2, 0, 0, 0, 0, 0.55f));
        defaultItemBuilder.put(ItemCameraTransforms.TransformType.HEAD, get(0, 13, 7, 0, 180, 0, 1));
        defaultItemBuilder.put(ItemCameraTransforms.TransformType.THIRD_PERSON_RIGHT_HAND, thirdPerson);
        defaultItemBuilder.put(ItemCameraTransforms.TransformType.THIRD_PERSON_LEFT_HAND, leftify(thirdPerson));
        defaultItemBuilder.put(ItemCameraTransforms.TransformType.FIRST_PERSON_RIGHT_HAND, firstPerson);
        defaultItemBuilder.put(ItemCameraTransforms.TransformType.FIRST_PERSON_LEFT_HAND, leftify(firstPerson));
        STAFF_STATE = new CCModelState(defaultItemBuilder.build());

        thirdPerson = get(0, 3, 1, 0, 0, 0, 0.55f);
        firstPerson = get(1.13f, 3.2f, 1.13f, 0, -90, 25, 0.68f);
        defaultItemBuilder = ImmutableMap.builder();
        defaultItemBuilder.put(ItemCameraTransforms.TransformType.GROUND, get(-2, 0, 0, 0, 0, 0, 0.4f));
        defaultItemBuilder.put(ItemCameraTransforms.TransformType.FIXED, get(-3, -3, 0, 0, 0, 0, 0.65f));
        defaultItemBuilder.put(ItemCameraTransforms.TransformType.HEAD, get(0, 13, 7, 0, 180, 0, 1));
        defaultItemBuilder.put(ItemCameraTransforms.TransformType.THIRD_PERSON_RIGHT_HAND, thirdPerson);
        defaultItemBuilder.put(ItemCameraTransforms.TransformType.THIRD_PERSON_LEFT_HAND, leftify(thirdPerson));
        defaultItemBuilder.put(ItemCameraTransforms.TransformType.FIRST_PERSON_RIGHT_HAND, firstPerson);
        defaultItemBuilder.put(ItemCameraTransforms.TransformType.FIRST_PERSON_LEFT_HAND, leftify(firstPerson));
        DR_SWORD_STATE = new CCModelState(defaultItemBuilder.build());

        thirdPerson = get(0, 3, 1, 0, 0, 0, 0.55f);
        firstPerson = get(1.13f, 3.2f, 1.13f, 0, -90, 25, 0.68f);
        defaultItemBuilder = ImmutableMap.builder();
        defaultItemBuilder.put(ItemCameraTransforms.TransformType.GROUND, get(-2, 1, 0, 0, 0, 0, 0.4f));
        defaultItemBuilder.put(ItemCameraTransforms.TransformType.FIXED, get(-2, -2, 0, 0, 0, 0, 0.8f));
        defaultItemBuilder.put(ItemCameraTransforms.TransformType.HEAD, get(0, 13, 7, 0, 180, 0, 1));
        defaultItemBuilder.put(ItemCameraTransforms.TransformType.THIRD_PERSON_RIGHT_HAND, thirdPerson);
        defaultItemBuilder.put(ItemCameraTransforms.TransformType.THIRD_PERSON_LEFT_HAND, leftify(thirdPerson));
        defaultItemBuilder.put(ItemCameraTransforms.TransformType.FIRST_PERSON_RIGHT_HAND, firstPerson);
        defaultItemBuilder.put(ItemCameraTransforms.TransformType.FIRST_PERSON_LEFT_HAND, leftify(firstPerson));
        WY_SWORD_STATE = new CCModelState(defaultItemBuilder.build());
    }

    public ToolTransformOverride(String key) {
        this.key = key;
    }

    public CCModelState getOverride(ItemCameraTransforms.TransformType cameraTransformType, CCModelState state) {
        if (key.equals("draconic_staff_of_power")) {
            return cameraTransformType == ItemCameraTransforms.TransformType.FIXED || cameraTransformType == ItemCameraTransforms.TransformType.GROUND ? STAFF_STATE : state;
        }
        else if (key.equals("draconic_sword")) {
            return cameraTransformType == ItemCameraTransforms.TransformType.FIXED || cameraTransformType == ItemCameraTransforms.TransformType.GROUND ? DR_SWORD_STATE : state;
        }
        else if (key.equals("wyvern_sword")) {
            return cameraTransformType == ItemCameraTransforms.TransformType.FIXED || cameraTransformType == ItemCameraTransforms.TransformType.GROUND ? WY_SWORD_STATE : state;
        }

        return state;
    }

    private static TRSRTransformation get(float tx, float ty, float tz, float rx, float ry, float rz, float s) {
        return TRSRTransformation.blockCenterToCorner(new TRSRTransformation(new Vector3f(tx / 16, ty / 16, tz / 16), TRSRTransformation.quatFromXYZDegrees(new Vector3f(rx, ry, rz)), new Vector3f(s, s, s), null));
    }

    private static TRSRTransformation leftify(TRSRTransformation transform) {
        return TRSRTransformation.blockCenterToCorner(flipX.compose(TRSRTransformation.blockCornerToCenter(transform)).compose(flipX));
    }

}
