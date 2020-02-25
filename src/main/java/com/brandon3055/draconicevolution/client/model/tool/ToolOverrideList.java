package com.brandon3055.draconicevolution.client.model.tool;

import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.model.ItemOverrideList;
import net.minecraft.item.Item;
import net.minecraftforge.common.model.IModelState;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

/**
 * Created by covers1624 on 29/06/2017.
 */
public class ToolOverrideList extends ItemOverrideList {

    private static Map<Item, BiFunction<ItemCameraTransforms.TransformType, IModelState, IModelState>> toolOverrides = new HashMap<>();

    public static void putOverride(Item item, BiFunction<ItemCameraTransforms.TransformType, IModelState, IModelState> override) {
        toolOverrides.put(item, override);
    }

    public ToolOverrideList() {
//        super(new ArrayList<>());
    }


    //@Override
//    public IBakedModel handleItemState(IBakedModel originalModel, ItemStack stack, @Nullable World world, @Nullable LivingEntity entity) {
//        ItemStack renderStack;
//        if (stack.getItem() instanceof WyvernBow || stack.getItem() instanceof DraconicBow) {
//            renderStack = stack.copy();
//            if (entity instanceof PlayerEntity && entity.getActiveItemStack() == stack && entity.getItemInUseMaxCount() > 0) {
//                BowProperties bowProperties = new BowProperties(renderStack, (PlayerEntity) entity);
//                byte pull = (byte) Math.min(MathHelper.floor((double) entity.getItemInUseMaxCount() / bowProperties.getDrawTicks() * 3D), 3);
//                ItemNBTUtils.putByte(renderStack, "render:bow_pull", pull);
//            }
//        } else {
//            renderStack = stack;
//        }
//        return new ToolBakedModel(ModelProperties.DEFAULT_ITEM, () -> ToolModelBakery.get2DModel(renderStack), () -> ToolModelBakery.get3DModel(renderStack), toolOverrides.get(stack.getItem()));
//    }
}
