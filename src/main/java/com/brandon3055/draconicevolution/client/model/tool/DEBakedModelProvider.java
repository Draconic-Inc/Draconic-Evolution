package com.brandon3055.draconicevolution.client.model.tool;

import codechicken.lib.model.loader.IBakedModelLoader;
import codechicken.lib.render.TransformUtils;
import codechicken.lib.util.ItemNBTUtils;
import com.brandon3055.brandonscore.lib.Set3;
import com.brandon3055.draconicevolution.DraconicEvolution;
import com.brandon3055.draconicevolution.items.tools.WyvernBow;
import com.google.common.collect.ImmutableList;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;

import java.util.Map;

/**
 * Created by brandon3055 on 28/07/2016.
 */
public class DEBakedModelProvider implements IBakedModelLoader {

    public static final DEBakedModelProvider INSTANCE = new DEBakedModelProvider();

    public static class DEKeyProvider implements IModKeyProvider {

        public static final DEKeyProvider INSTANCE = new DEKeyProvider();

        @Override
        public String getMod() {
            return DraconicEvolution.MODID.toLowerCase();
        }

        @Override
        public String createKey(ItemStack stack) {
            if (ToolModelRegistry.itemMap.containsKey(stack.getItem().getRegistryName().getResourcePath())) {
                return stack.getItem().getRegistryName().getResourcePath();
            }
            else if (stack.getItem() instanceof WyvernBow) {
                return stack.getItem().getRegistryName().getResourcePath() + "0" + ItemNBTUtils.getInteger(stack, "DrawStage");
            }

            return null;
        }

        @Override
        public String createKey(IBlockState state, EnumFacing face) {
            return null;
        }
    }

    @Override
    public IModKeyProvider createKeyProvider() {
        return DEKeyProvider.INSTANCE;
    }

    @Override
    public void addTextures(ImmutableList.Builder<ResourceLocation> builder) {
        for (Map.Entry<String, Set3<ResourceLocation, ResourceLocation, ResourceLocation>> entry : ToolModelRegistry.itemMap.entrySet()) {
            builder.add(entry.getValue().getA());
            builder.add(entry.getValue().getB());
        }
    }

    @Override
    public IBakedModel bakeModel(String key) {
        //LogHelper.info("Try Bake... " + key);
        if (key.contains("Bow")) {
            PerspectiveAwareToolModelBakery bakery = new PerspectiveAwareToolModelBakery(ToolModelRegistry.itemMap.get(key));
            return bakery.bake(TransformUtils.DEFAULT_ITEM);
        }
        else if (ToolModelRegistry.itemMap.containsKey(key)){
            PerspectiveAwareToolModelBakery bakery = new PerspectiveAwareToolModelBakery(ToolModelRegistry.itemMap.get(key));
            return bakery.bake(TransformUtils.DEFAULT_ITEM);
        }


        return null; //Return a perspective aware model
    }
}
