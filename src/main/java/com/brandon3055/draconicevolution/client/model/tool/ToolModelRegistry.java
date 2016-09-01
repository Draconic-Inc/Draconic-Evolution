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
    public static Map<String, Set3<ResourceLocation, ResourceLocation, ResourceLocation>> itemMap = new HashMap<String, Set3<ResourceLocation, ResourceLocation, ResourceLocation>>();

    static {
        buildItemMap();
    }

    public static void buildItemMap() {
        itemMap.clear();
        itemMap.put("wyvern_axe", new Set3<ResourceLocation, ResourceLocation, ResourceLocation>(new ResourceLocation("draconicevolution", "items/tools/wyvern_axe"), new ResourceLocation("draconicevolution", "items/tools/obj/wyvern_axe"), new ResourceLocation("draconicevolution", "models/item/tools/wyvern_axe.obj")));
        itemMap.put("wyvern_pick", new Set3<ResourceLocation, ResourceLocation, ResourceLocation>(new ResourceLocation("draconicevolution", "items/tools/wyvern_pickaxe"), new ResourceLocation("draconicevolution", "items/tools/obj/wyvern_pickaxe"), new ResourceLocation("draconicevolution", "models/item/tools/wyvern_pickaxe.obj")));
        itemMap.put("wyvern_shovel", new Set3<ResourceLocation, ResourceLocation, ResourceLocation>(new ResourceLocation("draconicevolution", "items/tools/wyvern_shovel"), new ResourceLocation("draconicevolution", "items/tools/obj/wyvern_shovel"), new ResourceLocation("draconicevolution", "models/item/tools/wyvern_shovel.obj")));
        itemMap.put("wyvern_sword", new Set3<ResourceLocation, ResourceLocation, ResourceLocation>(new ResourceLocation("draconicevolution", "items/tools/wyvern_sword"), new ResourceLocation("draconicevolution", "items/tools/obj/wyvern_sword"), new ResourceLocation("draconicevolution", "models/item/tools/wyvern_sword.obj")));
        itemMap.put("draconic_axe", new Set3<ResourceLocation, ResourceLocation, ResourceLocation>(new ResourceLocation("draconicevolution", "items/tools/draconic_axe"), new ResourceLocation("draconicevolution", "items/tools/obj/draconic_axe"), new ResourceLocation("draconicevolution", "models/item/tools/draconic_axe.obj")));
        itemMap.put("draconic_hoe", new Set3<ResourceLocation, ResourceLocation, ResourceLocation>(new ResourceLocation("draconicevolution", "items/tools/draconic_hoe"), new ResourceLocation("draconicevolution", "items/tools/obj/draconic_hoe"), new ResourceLocation("draconicevolution", "models/item/tools/draconic_hoe.obj")));
        itemMap.put("draconic_pick", new Set3<ResourceLocation, ResourceLocation, ResourceLocation>(new ResourceLocation("draconicevolution", "items/tools/draconic_pickaxe"), new ResourceLocation("draconicevolution", "items/tools/obj/draconic_pickaxe"), new ResourceLocation("draconicevolution", "models/item/tools/draconic_pickaxe.obj")));
        itemMap.put("draconic_shovel", new Set3<ResourceLocation, ResourceLocation, ResourceLocation>(new ResourceLocation("draconicevolution", "items/tools/draconic_shovel"), new ResourceLocation("draconicevolution", "items/tools/obj/draconic_shovel"), new ResourceLocation("draconicevolution", "models/item/tools/draconic_shovel.obj")));
        itemMap.put("draconic_staff_of_power", new Set3<ResourceLocation, ResourceLocation, ResourceLocation>(new ResourceLocation("draconicevolution", "items/tools/draconic_staff_of_power"), new ResourceLocation("draconicevolution", "items/tools/obj/draconic_staff_of_power"), new ResourceLocation("draconicevolution", "models/item/tools/draconic_staff_of_power.obj")));
        itemMap.put("draconic_sword", new Set3<ResourceLocation, ResourceLocation, ResourceLocation>(new ResourceLocation("draconicevolution", "items/tools/draconic_sword"), new ResourceLocation("draconicevolution", "items/tools/obj/draconic_sword"), new ResourceLocation("draconicevolution", "models/item/tools/draconic_sword.obj")));

        itemMap.put("wyvern_bow00", new Set3<ResourceLocation, ResourceLocation, ResourceLocation>(new ResourceLocation("draconicevolution", "items/tools/wyvern_bow00"), new ResourceLocation("draconicevolution", "items/tools/obj/wyvern_bow00"), new ResourceLocation("draconicevolution", "models/item/tools/wyvern_bow00.obj")));
        itemMap.put("wyvern_bow01", new Set3<ResourceLocation, ResourceLocation, ResourceLocation>(new ResourceLocation("draconicevolution", "items/tools/wyvern_bow01"), new ResourceLocation("draconicevolution", "items/tools/obj/wyvern_bow01"), new ResourceLocation("draconicevolution", "models/item/tools/wyvern_bow01.obj")));
        itemMap.put("wyvern_bow02", new Set3<ResourceLocation, ResourceLocation, ResourceLocation>(new ResourceLocation("draconicevolution", "items/tools/wyvern_bow02"), new ResourceLocation("draconicevolution", "items/tools/obj/wyvern_bow02"), new ResourceLocation("draconicevolution", "models/item/tools/wyvern_bow02.obj")));
        itemMap.put("wyvern_bow03", new Set3<ResourceLocation, ResourceLocation, ResourceLocation>(new ResourceLocation("draconicevolution", "items/tools/wyvern_bow03"), new ResourceLocation("draconicevolution", "items/tools/obj/wyvern_bow03"), new ResourceLocation("draconicevolution", "models/item/tools/wyvern_bow03.obj")));
        itemMap.put("draconic_bow00", new Set3<ResourceLocation, ResourceLocation, ResourceLocation>(new ResourceLocation("draconicevolution", "items/tools/draconic_bow00"), new ResourceLocation("draconicevolution", "items/tools/obj/draconic_bow00"), new ResourceLocation("draconicevolution", "models/item/tools/draconic_bow00.obj")));
        itemMap.put("draconic_bow01", new Set3<ResourceLocation, ResourceLocation, ResourceLocation>(new ResourceLocation("draconicevolution", "items/tools/draconic_bow01"), new ResourceLocation("draconicevolution", "items/tools/obj/draconic_bow01"), new ResourceLocation("draconicevolution", "models/item/tools/draconic_bow01.obj")));
        itemMap.put("draconic_bow02", new Set3<ResourceLocation, ResourceLocation, ResourceLocation>(new ResourceLocation("draconicevolution", "items/tools/draconic_bow02"), new ResourceLocation("draconicevolution", "items/tools/obj/draconic_bow02"), new ResourceLocation("draconicevolution", "models/item/tools/draconic_bow02.obj")));
        itemMap.put("draconic_bow03", new Set3<ResourceLocation, ResourceLocation, ResourceLocation>(new ResourceLocation("draconicevolution", "items/tools/draconic_bow03"), new ResourceLocation("draconicevolution", "items/tools/obj/draconic_bow03"), new ResourceLocation("draconicevolution", "models/item/tools/draconic_bow03.obj")));
    }
}
