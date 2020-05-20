package com.brandon3055.draconicevolution.client.model.tool;

import codechicken.lib.model.ItemQuadBakery;
import codechicken.lib.model.bakedmodels.ModelProperties;
import codechicken.lib.model.bakedmodels.PerspectiveAwareBakedModel;
import codechicken.lib.model.bakery.key.IItemStackKeyGenerator;
import codechicken.lib.util.ResourceUtils;
import codechicken.lib.util.TransformUtils;
import com.brandon3055.draconicevolution.utils.LogHelper;
import com.google.common.base.Preconditions;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static codechicken.lib.model.bakery.ModelBakery.defaultItemKeyGenerator;

/**
 * Created by brandon3055 on 21/06/2017.
 */
public class ToolModelBakery {

    //Invalidating such a small cache after 30 min is pointless.
    private static Map<String, IBakedModel> modelCache = new HashMap<>();
    private static Map<Item, IItemStackKeyGenerator> itemKeyGeneratorMap = new HashMap<>();

    private static ModelProperties.PerspectiveProperties props2D = new ModelProperties.PerspectiveProperties(TransformUtils.DEFAULT_TOOL, ModelProperties.DEFAULT_ITEM);

    public static IItemStackKeyGenerator getKeyGenerator(Item item) {
        if (itemKeyGeneratorMap.containsKey(item)) {
            return itemKeyGeneratorMap.get(item);
        }
        return defaultItemKeyGenerator;
    }

    public static void registerItemKeyGenerator(Item item, IItemStackKeyGenerator generator) {
        if (itemKeyGeneratorMap.containsKey(item)) {
            throw new IllegalArgumentException("Unable to register IItemStackKeyGenerator as one is already registered for item: " + item.getRegistryName());
        }
        itemKeyGeneratorMap.put(item, generator);
    }

    public static IBakedModel get2DModel(ItemStack stack) {
        Preconditions.checkArgument(stack.getItem() instanceof IToolModelProvider, "Item is not a DE IToolModelProvider! Stack:" + stack);
        IToolModelProvider provider = (IToolModelProvider) stack.getItem();
        String cacheKey = getKeyGenerator(stack.getItem()).generateKey(stack) + "|2d";
        return modelCache.computeIfAbsent(cacheKey, k -> {
            List<BakedQuad> quads = ItemQuadBakery.bakeItem(provider.getModels(stack).key());
            return new PerspectiveAwareBakedModel(quads, props2D);
        });
    }

    public static IBakedModel get3DModel(ItemStack stack) {
        Preconditions.checkArgument(stack.getItem() instanceof IToolModelProvider, "Item is not a DE IToolModelProvider! Stack:" + stack);
        IToolModelProvider provider = (IToolModelProvider) stack.getItem();
        String cacheKey = getKeyGenerator(stack.getItem()).generateKey(stack) + "|3d";
        return modelCache.computeIfAbsent(cacheKey, k -> {
            IBakedModel model;
            try {
                model = null; //OBJLoader.INSTANCE.loadModel(provider.getModels(stack).getValue()).bake(TRSRTransformation.identity(), DefaultVertexFormats.ITEM, TextureUtils::getTexture);
            } catch (Exception e) {
                LogHelper.errorError("Exception thrown whilst baking 3d OBJ model!", e);
                model = get2DModel(stack);
            }
            return model;
        });
    }

    public static void initialize() {
        ResourceUtils.registerReloadListener((resourceManager, resourcePredicate) -> modelCache.clear());
    }
}
