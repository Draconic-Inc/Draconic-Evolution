package com.brandon3055.draconicevolution.client.model.tool;

import codechicken.lib.math.MathHelper;
import codechicken.lib.model.BakedModelProperties;
import codechicken.lib.util.ItemNBTUtils;
import com.brandon3055.draconicevolution.handlers.BowHandler.BowProperties;
import com.brandon3055.draconicevolution.items.tools.DraconicBow;
import com.brandon3055.draconicevolution.items.tools.WyvernBow;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.model.IModelState;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

/**
 * Created by covers1624 on 29/06/2017.
 */
public class ToolOverrideList extends ItemOverrideList {

    private static Map<Item, BiFunction<TransformType, IModelState, IModelState>> toolOverrides = new HashMap<>();

    public static void putOverride(Item item, BiFunction<TransformType, IModelState, IModelState> override) {
        toolOverrides.put(item, override);
    }

    public ToolOverrideList() {
        super(new ArrayList<>());
    }

    @Override
    public IBakedModel handleItemState(IBakedModel originalModel, ItemStack stack, @Nullable World world, @Nullable EntityLivingBase entity) {
        ItemStack renderStack;
        if (stack.getItem() instanceof WyvernBow || stack.getItem() instanceof DraconicBow) {
            renderStack = stack.copy();
            if (entity instanceof EntityPlayer && entity.getActiveItemStack() == stack && entity.getItemInUseMaxCount() > 0) {
                BowProperties bowProperties = new BowProperties(renderStack, (EntityPlayer) entity);
                byte pull = (byte) Math.min(MathHelper.floor((double) entity.getItemInUseMaxCount() / bowProperties.getDrawTicks() * 3D), 3);
                ItemNBTUtils.setByte(renderStack, "render:bow_pull", pull);
            }
        } else {
            renderStack = stack;
        }
        return new ToolBakedModel(BakedModelProperties.DEFAULT_ITEM, () -> ToolModelBakery.get2DModel(renderStack), () -> ToolModelBakery.get3DModel(renderStack), toolOverrides.get(stack.getItem()));
    }
}
