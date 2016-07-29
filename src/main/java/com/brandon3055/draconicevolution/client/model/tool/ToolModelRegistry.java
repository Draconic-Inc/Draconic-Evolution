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
        itemMap.put("wyvernAxe", new Set3<ResourceLocation, ResourceLocation, ResourceLocation>(new ResourceLocation("draconicevolution", "items/tools/wyvernAxe"), new ResourceLocation("draconicevolution", "items/tools/obj/wyvernAxe"), new ResourceLocation("draconicevolution", "models/item/tools/wyvernAxe.obj")));
        itemMap.put("wyvernPick", new Set3<ResourceLocation, ResourceLocation, ResourceLocation>(new ResourceLocation("draconicevolution", "items/tools/wyvernPickaxe"), new ResourceLocation("draconicevolution", "items/tools/obj/wyvernPickaxe"), new ResourceLocation("draconicevolution", "models/item/tools/wyvernPickaxe.obj")));
        itemMap.put("wyvernShovel", new Set3<ResourceLocation, ResourceLocation, ResourceLocation>(new ResourceLocation("draconicevolution", "items/tools/wyvernShovel"), new ResourceLocation("draconicevolution", "items/tools/obj/wyvernShovel"), new ResourceLocation("draconicevolution", "models/item/tools/wyvernShovel.obj")));
        itemMap.put("wyvernSword", new Set3<ResourceLocation, ResourceLocation, ResourceLocation>(new ResourceLocation("draconicevolution", "items/tools/wyvernSword"), new ResourceLocation("draconicevolution", "items/tools/obj/wyvernSword"), new ResourceLocation("draconicevolution", "models/item/tools/wyvernSword.obj")));
        itemMap.put("draconicAxe", new Set3<ResourceLocation, ResourceLocation, ResourceLocation>(new ResourceLocation("draconicevolution", "items/tools/draconicAxe"), new ResourceLocation("draconicevolution", "items/tools/obj/draconicAxe"), new ResourceLocation("draconicevolution", "models/item/tools/draconicAxe.obj")));
        itemMap.put("draconicHoe", new Set3<ResourceLocation, ResourceLocation, ResourceLocation>(new ResourceLocation("draconicevolution", "items/tools/draconicHoe"), new ResourceLocation("draconicevolution", "items/tools/obj/draconicHoe"), new ResourceLocation("draconicevolution", "models/item/tools/draconicHoe.obj")));
        itemMap.put("draconicPick", new Set3<ResourceLocation, ResourceLocation, ResourceLocation>(new ResourceLocation("draconicevolution", "items/tools/draconicPickaxe"), new ResourceLocation("draconicevolution", "items/tools/obj/draconicPickaxe"), new ResourceLocation("draconicevolution", "models/item/tools/draconicPickaxe.obj")));
        itemMap.put("draconicShovel", new Set3<ResourceLocation, ResourceLocation, ResourceLocation>(new ResourceLocation("draconicevolution", "items/tools/draconicShovel"), new ResourceLocation("draconicevolution", "items/tools/obj/draconicShovel"), new ResourceLocation("draconicevolution", "models/item/tools/draconicShovel.obj")));
        itemMap.put("draconicStaffOfPower", new Set3<ResourceLocation, ResourceLocation, ResourceLocation>(new ResourceLocation("draconicevolution", "items/tools/draconicStaffOfPower"), new ResourceLocation("draconicevolution", "items/tools/obj/draconicStaffOfPower"), new ResourceLocation("draconicevolution", "models/item/tools/draconicStaffOfPower.obj")));
        itemMap.put("draconicSword", new Set3<ResourceLocation, ResourceLocation, ResourceLocation>(new ResourceLocation("draconicevolution", "items/tools/draconicSword"), new ResourceLocation("draconicevolution", "items/tools/obj/draconicSword"), new ResourceLocation("draconicevolution", "models/item/tools/draconicSword.obj")));

        itemMap.put("wyvernBow00", new Set3<ResourceLocation, ResourceLocation, ResourceLocation>(new ResourceLocation("draconicevolution", "items/tools/wyvernBow00"), new ResourceLocation("draconicevolution", "items/tools/obj/wyvernBow00"), new ResourceLocation("draconicevolution", "models/item/tools/wyvernBow00.obj")));
        itemMap.put("wyvernBow01", new Set3<ResourceLocation, ResourceLocation, ResourceLocation>(new ResourceLocation("draconicevolution", "items/tools/wyvernBow01"), new ResourceLocation("draconicevolution", "items/tools/obj/wyvernBow01"), new ResourceLocation("draconicevolution", "models/item/tools/wyvernBow01.obj")));
        itemMap.put("wyvernBow02", new Set3<ResourceLocation, ResourceLocation, ResourceLocation>(new ResourceLocation("draconicevolution", "items/tools/wyvernBow02"), new ResourceLocation("draconicevolution", "items/tools/obj/wyvernBow02"), new ResourceLocation("draconicevolution", "models/item/tools/wyvernBow02.obj")));
        itemMap.put("wyvernBow03", new Set3<ResourceLocation, ResourceLocation, ResourceLocation>(new ResourceLocation("draconicevolution", "items/tools/wyvernBow03"), new ResourceLocation("draconicevolution", "items/tools/obj/wyvernBow03"), new ResourceLocation("draconicevolution", "models/item/tools/wyvernBow03.obj")));
        itemMap.put("draconicBow00", new Set3<ResourceLocation, ResourceLocation, ResourceLocation>(new ResourceLocation("draconicevolution", "items/tools/draconicBow00"), new ResourceLocation("draconicevolution", "items/tools/obj/draconicBow00"), new ResourceLocation("draconicevolution", "models/item/tools/draconicBow00.obj")));
        itemMap.put("draconicBow01", new Set3<ResourceLocation, ResourceLocation, ResourceLocation>(new ResourceLocation("draconicevolution", "items/tools/draconicBow01"), new ResourceLocation("draconicevolution", "items/tools/obj/draconicBow01"), new ResourceLocation("draconicevolution", "models/item/tools/draconicBow01.obj")));
        itemMap.put("draconicBow02", new Set3<ResourceLocation, ResourceLocation, ResourceLocation>(new ResourceLocation("draconicevolution", "items/tools/draconicBow02"), new ResourceLocation("draconicevolution", "items/tools/obj/draconicBow02"), new ResourceLocation("draconicevolution", "models/item/tools/draconicBow02.obj")));
        itemMap.put("draconicBow03", new Set3<ResourceLocation, ResourceLocation, ResourceLocation>(new ResourceLocation("draconicevolution", "items/tools/draconicBow03"), new ResourceLocation("draconicevolution", "items/tools/obj/draconicBow03"), new ResourceLocation("draconicevolution", "models/item/tools/draconicBow03.obj")));
    }
}
