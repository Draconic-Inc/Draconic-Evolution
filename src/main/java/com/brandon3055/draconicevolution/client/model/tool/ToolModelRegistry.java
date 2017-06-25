package com.brandon3055.draconicevolution.client.model.tool;

import com.brandon3055.brandonscore.lib.Set3;
import net.minecraft.util.ResourceLocation;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by brandon3055 on 28/07/2016.
 */
public class ToolModelRegistry {

    //Set contains 2D texture location, OBJ texture location, OBJ location
    public static Map<String, Set3<ResourceLocation, ResourceLocation, ResourceLocation>> itemMap = new HashMap<>();

    static {
        buildItemMap();
    }

    public static void buildItemMap() {
        itemMap.clear();

        itemMap.put("wyvern_bow00", new Set3<>(new ResourceLocation("draconicevolution", "items/tools/wyvern_bow00"), new ResourceLocation("draconicevolution", "items/tools/obj/wyvern_bow00"), new ResourceLocation("draconicevolution", "models/item/tools/wyvern_bow00.obj")));
        itemMap.put("wyvern_bow01", new Set3<>(new ResourceLocation("draconicevolution", "items/tools/wyvern_bow01"), new ResourceLocation("draconicevolution", "items/tools/obj/wyvern_bow01"), new ResourceLocation("draconicevolution", "models/item/tools/wyvern_bow01.obj")));
        itemMap.put("wyvern_bow02", new Set3<>(new ResourceLocation("draconicevolution", "items/tools/wyvern_bow02"), new ResourceLocation("draconicevolution", "items/tools/obj/wyvern_bow02"), new ResourceLocation("draconicevolution", "models/item/tools/wyvern_bow02.obj")));
        itemMap.put("wyvern_bow03", new Set3<>(new ResourceLocation("draconicevolution", "items/tools/wyvern_bow03"), new ResourceLocation("draconicevolution", "items/tools/obj/wyvern_bow03"), new ResourceLocation("draconicevolution", "models/item/tools/wyvern_bow03.obj")));
        itemMap.put("draconic_bow00", new Set3<>(new ResourceLocation("draconicevolution", "items/tools/draconic_bow00"), new ResourceLocation("draconicevolution", "items/tools/obj/draconic_bow00"), new ResourceLocation("draconicevolution", "models/item/tools/draconic_bow00.obj")));
        itemMap.put("draconic_bow01", new Set3<>(new ResourceLocation("draconicevolution", "items/tools/draconic_bow01"), new ResourceLocation("draconicevolution", "items/tools/obj/draconic_bow01"), new ResourceLocation("draconicevolution", "models/item/tools/draconic_bow01.obj")));
        itemMap.put("draconic_bow02", new Set3<>(new ResourceLocation("draconicevolution", "items/tools/draconic_bow02"), new ResourceLocation("draconicevolution", "items/tools/obj/draconic_bow02"), new ResourceLocation("draconicevolution", "models/item/tools/draconic_bow02.obj")));
        itemMap.put("draconic_bow03", new Set3<>(new ResourceLocation("draconicevolution", "items/tools/draconic_bow03"), new ResourceLocation("draconicevolution", "items/tools/obj/draconic_bow03"), new ResourceLocation("draconicevolution", "models/item/tools/draconic_bow03.obj")));
    }
}
