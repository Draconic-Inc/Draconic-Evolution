package com.brandon3055.draconicevolution.client.model.toolold;

import codechicken.lib.render.ModelRegistryHelper;
import codechicken.lib.render.TextureUtils;
import codechicken.lib.render.TransformUtils;
import com.brandon3055.brandonscore.lib.PairKV;
import com.brandon3055.draconicevolution.utils.LogHelper;
import com.google.common.collect.ImmutableSet;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.IResourceManagerReloadListener;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static com.brandon3055.draconicevolution.DEFeatures.*;

/**
 * Created by brandon3055 on 5/06/2016.
 */
public class ToolModelLoader implements TextureUtils.IIconRegister, IResourceManagerReloadListener {

    public static Map<IDualModel, PairKV<ResourceLocation, ResourceLocation>> itemMap = new HashMap<IDualModel, PairKV<ResourceLocation, ResourceLocation>>();
    public static Map<String, PairKV<ResourceLocation, ResourceLocation>> bowMap = new HashMap<String, PairKV<ResourceLocation, ResourceLocation>>();
    public static Map<IDualModel, IBakedModel> modelCache = new HashMap<IDualModel, IBakedModel>();
    public static Map<String, IBakedModel> bowModelCache = new HashMap<String, IBakedModel>();

    public static void buildItemMap() {
        itemMap.clear();
        itemMap.put(wyvernAxe, new PairKV<ResourceLocation, ResourceLocation>(new ResourceLocation("draconicevolution", "items/tools/wyvernAxe"), new ResourceLocation("draconicevolution", "models/item/tools/wyvernAxe.obj")));
        //itemMap.put(wyvernBow, new PairKV<ResourceLocation, ResourceLocation>(new ResourceLocation("draconicevolution", "items/tools/wyvernBow00"), new ResourceLocation("draconicevolution", "models/item/tools/wyvernBow00.obj")));
        itemMap.put(wyvernPick, new PairKV<ResourceLocation, ResourceLocation>(new ResourceLocation("draconicevolution", "items/tools/wyvernPickaxe"), new ResourceLocation("draconicevolution", "models/item/tools/wyvernPickaxe.obj")));
        itemMap.put(wyvernShovel, new PairKV<ResourceLocation, ResourceLocation>(new ResourceLocation("draconicevolution", "items/tools/wyvernShovel"), new ResourceLocation("draconicevolution", "models/item/tools/wyvernShovel.obj")));
        itemMap.put(wyvernSword, new PairKV<ResourceLocation, ResourceLocation>(new ResourceLocation("draconicevolution", "items/tools/wyvernSword"), new ResourceLocation("draconicevolution", "models/item/tools/wyvernSword.obj")));
        itemMap.put(draconicAxe, new PairKV<ResourceLocation, ResourceLocation>(new ResourceLocation("draconicevolution", "items/tools/draconicAxe"), new ResourceLocation("draconicevolution", "models/item/tools/draconicAxe.obj")));
        //itemMap.put(draconicBow, new PairKV<ResourceLocation, ResourceLocation>(new ResourceLocation("draconicevolution", "items/tools/draconicBow00"), new ResourceLocation("draconicevolution", "models/item/tools/draconicBow00.obj")));
        itemMap.put(draconicHoe, new PairKV<ResourceLocation, ResourceLocation>(new ResourceLocation("draconicevolution", "items/tools/draconicHoe"), new ResourceLocation("draconicevolution", "models/item/tools/draconicHoe.obj")));
        itemMap.put(draconicPick, new PairKV<ResourceLocation, ResourceLocation>(new ResourceLocation("draconicevolution", "items/tools/draconicPickaxe"), new ResourceLocation("draconicevolution", "models/item/tools/draconicPickaxe.obj")));
        itemMap.put(draconicShovel, new PairKV<ResourceLocation, ResourceLocation>(new ResourceLocation("draconicevolution", "items/tools/draconicShovel"), new ResourceLocation("draconicevolution", "models/item/tools/draconicShovel.obj")));
        itemMap.put(draconicStaffOfPower, new PairKV<ResourceLocation, ResourceLocation>(new ResourceLocation("draconicevolution", "items/tools/draconicStaffOfPower"), new ResourceLocation("draconicevolution", "models/item/tools/draconicStaffOfPower.obj")));
        itemMap.put(draconicSword, new PairKV<ResourceLocation, ResourceLocation>(new ResourceLocation("draconicevolution", "items/tools/draconicSword"), new ResourceLocation("draconicevolution", "models/item/tools/draconicSword.obj")));

        bowMap.clear();
        bowMap.put("wyvernBow00", new PairKV<ResourceLocation, ResourceLocation>(new ResourceLocation("draconicevolution", "items/tools/wyvernBow00"), new ResourceLocation("draconicevolution", "models/item/tools/wyvernBow00.obj")));
        bowMap.put("wyvernBow01", new PairKV<ResourceLocation, ResourceLocation>(new ResourceLocation("draconicevolution", "items/tools/wyvernBow01"), new ResourceLocation("draconicevolution", "models/item/tools/wyvernBow01.obj")));
        bowMap.put("wyvernBow02", new PairKV<ResourceLocation, ResourceLocation>(new ResourceLocation("draconicevolution", "items/tools/wyvernBow02"), new ResourceLocation("draconicevolution", "models/item/tools/wyvernBow02.obj")));
        bowMap.put("wyvernBow03", new PairKV<ResourceLocation, ResourceLocation>(new ResourceLocation("draconicevolution", "items/tools/wyvernBow03"), new ResourceLocation("draconicevolution", "models/item/tools/wyvernBow03.obj")));
        bowMap.put("draconicBow00", new PairKV<ResourceLocation, ResourceLocation>(new ResourceLocation("draconicevolution", "items/tools/draconicBow00"), new ResourceLocation("draconicevolution", "models/item/tools/draconicBow00.obj")));
        bowMap.put("draconicBow01", new PairKV<ResourceLocation, ResourceLocation>(new ResourceLocation("draconicevolution", "items/tools/draconicBow01"), new ResourceLocation("draconicevolution", "models/item/tools/draconicBow01.obj")));
        bowMap.put("draconicBow02", new PairKV<ResourceLocation, ResourceLocation>(new ResourceLocation("draconicevolution", "items/tools/draconicBow02"), new ResourceLocation("draconicevolution", "models/item/tools/draconicBow02.obj")));
        bowMap.put("draconicBow03", new PairKV<ResourceLocation, ResourceLocation>(new ResourceLocation("draconicevolution", "items/tools/draconicBow03"), new ResourceLocation("draconicevolution", "models/item/tools/draconicBow03.obj")));

    }

    public static void registerModels(){
        for (IDualModel item : itemMap.keySet()) {
//            IBakedModel model = getModel(item);
//            if (model == null){
//                LogHelper.bigError("Model for tool is null! "+item);
//                continue;
//            }

            ModelRegistryHelper.register(item.getModelLocation(), new OverrideBakedModel());
        }
        for (String name : bowMap.keySet()) {
            ModelRegistryHelper.register(new ModelResourceLocation("draconicevolution:"+name, "inventory"), new OverrideBakedModel());
        }
    }

    public Collection<ResourceLocation> getTextures() {
        ImmutableSet.Builder<ResourceLocation> builder = ImmutableSet.builder();

        for (PairKV<ResourceLocation, ResourceLocation> pairKV : itemMap.values()) {
            builder.add(pairKV.getKey());
            builder.add(new ResourceLocation(pairKV.getKey().toString().replace("tools", "tools/obj")));
        }

        for (PairKV<ResourceLocation, ResourceLocation> pairKV : bowMap.values()) {
            builder.add(pairKV.getKey());
            builder.add(new ResourceLocation(pairKV.getKey().toString().replace("tools", "tools/obj")));
        }

        return builder.build();
    }

    @Override
    public void registerIcons(TextureMap textureMap) {
        for (ResourceLocation resourceLocation : getTextures()) {
            textureMap.registerSprite(resourceLocation);
        }
    }

    public static IBakedModel getModel(IDualModel item){
        if (!itemMap.containsKey(item)){
            throw new IllegalArgumentException("Attempted to load model for an invalid item... "+item);
        }

        if (!modelCache.containsKey(item)){
            PairKV<ResourceLocation, ResourceLocation> itemPair = itemMap.get(item);

            try {
                modelCache.put(item, ToolModelBakery.bake(TransformUtils.DEFAULT_TOOL, itemPair.getKey(), itemPair.getValue()));
            }
            catch (Exception e) {
                LogHelper.error("Something went wrong when loading model for item: "+item);
                e.printStackTrace();
                return null;
            }
        }

        return modelCache.get(item);
    }

    public static IBakedModel getBowModel(ItemStack stack) {
        String model = stack.getItem().getRegistryName().getResourcePath() + "0" + stack.getItemDamage();
        //LogHelper.info(model + bowMap.containsKey(model));

        if (!bowMap.containsKey(model)) {
            throw new IllegalArgumentException("Attempted to load model for an invalid item or item damage "+stack);
        }

        if (!bowModelCache.containsKey(model)) {
            PairKV<ResourceLocation, ResourceLocation> itemPair = bowMap.get(model);

            try {
                bowModelCache.put(model, ToolModelBakery.bake(TransformUtils.DEFAULT_TOOL, itemPair.getKey(), itemPair.getValue()));
            }
            catch (Exception e) {
                LogHelper.error("Something went wrong when loading model for item: "+model);
                e.printStackTrace();
                return null;
            }
        }
        //return getModel((IDualModel)stack.getItem());
        return bowModelCache.get(model);
    }

    @Override
    public void onResourceManagerReload(IResourceManager resourceManager) {
        modelCache.clear();
    }
}
