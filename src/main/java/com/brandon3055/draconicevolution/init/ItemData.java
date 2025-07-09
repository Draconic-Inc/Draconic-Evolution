package com.brandon3055.draconicevolution.init;

import com.brandon3055.brandonscore.api.BCStreamCodec;
import com.brandon3055.brandonscore.api.TechLevel;
import com.brandon3055.brandonscore.inventory.InventoryDynamic;
import com.brandon3055.brandonscore.utils.TargetPos;
import com.brandon3055.draconicevolution.api.capability.ModuleHost;
import com.brandon3055.draconicevolution.api.config.BooleanProperty;
import com.brandon3055.draconicevolution.api.config.ConfigProperty;
import com.brandon3055.draconicevolution.api.config.DecimalProperty;
import com.brandon3055.draconicevolution.api.config.IntegerProperty;
import com.brandon3055.draconicevolution.api.modules.lib.ModularOPStorage;
import com.brandon3055.draconicevolution.api.modules.lib.ModuleEntity;
import com.brandon3055.draconicevolution.items.equipment.IModularItem.DestroySpeedData;
import com.brandon3055.draconicevolution.items.equipment.IModularTieredItem.AttributeData;
import com.brandon3055.draconicevolution.items.tools.DislocatorAdvanced.DislocatorTarget;
import com.brandon3055.draconicevolution.lib.DumData;
import com.mojang.serialization.Codec;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.UUIDUtil;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static com.brandon3055.draconicevolution.DraconicEvolution.MODID;

/**
 * Created by brandon3055 on 25/03/2025
 */
public class ItemData {

    public static final DeferredRegister<DataComponentType<?>> DATA = DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE, MODID);

    public static void init(IEventBus modBus) {
        DATA.register(modBus);
    }

    //@formatter:off
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<TechLevel>>                  TECH_LEVEL                  = DATA.register("tech_level",                   () -> DataComponentType.<TechLevel>builder().persistent(TechLevel.CODEC).networkSynchronized(TechLevel.STREAM_CODEC).build());

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<UUID>>                       DISLOCATOR_PLAYER_ID        = DATA.register("dislocator_player_id",         () -> DataComponentType.<UUID>builder().persistent(UUIDUtil.CODEC).networkSynchronized(UUIDUtil.STREAM_CODEC).build());
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<UUID>>                       DISLOCATOR_STACK_ID         = DATA.register("dislocator_stack_id",          () -> DataComponentType.<UUID>builder().persistent(UUIDUtil.CODEC).networkSynchronized(UUIDUtil.STREAM_CODEC).build());
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<UUID>>                       DISLOCATOR_LINK_ID          = DATA.register("dislocator_link_id",           () -> DataComponentType.<UUID>builder().persistent(UUIDUtil.CODEC).networkSynchronized(UUIDUtil.STREAM_CODEC).build());
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<String>>                     DISLOCATOR_PLAYER_NAME      = DATA.register("dislocator_player_name",       () -> DataComponentType.<String>builder().persistent(Codec.STRING).networkSynchronized(ByteBufCodecs.STRING_UTF8).build());

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<TargetPos>>                  DISLOCATOR_TARGET           = DATA.register("dislocator_target",            () -> DataComponentType.<TargetPos>builder().persistent(TargetPos.CODEC).networkSynchronized(TargetPos.STREAM_CODEC).build());
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>>                    DISLOCATOR_FUEL             = DATA.register("dislocator_fuel",              () -> DataComponentType.<Integer>builder().persistent(Codec.INT).networkSynchronized(ByteBufCodecs.INT).build());
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>>                    DISLOCATOR_SELECTED         = DATA.register("dislocator_selected",          () -> DataComponentType.<Integer>builder().persistent(Codec.INT).networkSynchronized(ByteBufCodecs.INT).build());
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Boolean>>                    DISLOCATOR_BLINK            = DATA.register("dislocator_blink",             () -> DataComponentType.<Boolean>builder().persistent(Codec.BOOL).networkSynchronized(ByteBufCodecs.BOOL).build());
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<List<DislocatorTarget>>>     DISLOCATOR_TARGETS          = DATA.register("dislocator_targets",           () -> DataComponentType.<List<DislocatorTarget>>builder().persistent(DislocatorTarget.CODEC.listOf()).networkSynchronized(DislocatorTarget.STREAM_CODEC.apply(ByteBufCodecs.list())).build());

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Boolean>>                    MAGNET_ACTIVE               = DATA.register("magnet_active",                () -> DataComponentType.<Boolean>builder().persistent(Codec.BOOL).networkSynchronized(ByteBufCodecs.BOOL).build());

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<ResourceLocation>>           SOUL_ID                     = DATA.register("soul_id",                      () -> DataComponentType.<ResourceLocation>builder().persistent(ResourceLocation.CODEC).networkSynchronized(ResourceLocation.STREAM_CODEC).build());
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<CustomData>>                 SOUL_DATA                   = DATA.register("soul_data",                    () -> DataComponentType.<CustomData>builder().persistent(CustomData.CODEC).networkSynchronized(CustomData.STREAM_CODEC).build());

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<GlobalPos>>                  BINDER_POS                  = DATA.register("binder_pos",                   () -> DataComponentType.<GlobalPos>builder().persistent(GlobalPos.CODEC).networkSynchronized(GlobalPos.STREAM_CODEC).build());

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<DumData<ModuleHost>>>        HOST_CAP_HOLDER             = DATA.register("module_host_cap_instance",     () -> DataComponentType.<DumData<ModuleHost>>builder().persistent(Codec.unit(DumData::new)).build());
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<DumData<ModularOPStorage>>>  ENERGY_CAP_HOLDER           = DATA.register("energy_cap_instance",          () -> DataComponentType.<DumData<ModularOPStorage>>builder().persistent(Codec.unit(DumData::new)).build());

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<DestroySpeedData>>           DESTROY_SPEED_DATA          = DATA.register("destroy_speed_data",           () -> DataComponentType.<DestroySpeedData>builder().persistent(DestroySpeedData.CODEC).build());
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<AttributeData>>              ATTRIBUTE_DATA              = DATA.register("attribute_data",               () -> DataComponentType.<AttributeData>builder().persistent(AttributeData.CODEC).build());

//    public static final DeferredHolder<DataComponentType<?>, DataComponentType<CustomData>>               MODULE_HOST_STORAGE         = DATA.register("module_host_storage",          () -> DataComponentType.<CustomData>builder().persistent(CustomData.CODEC).networkSynchronized(CustomData.STREAM_CODEC).build());
//    public static final DeferredHolder<DataComponentType<?>, DataComponentType<CustomData>>               MODULAR_ENERGY_STORAGE      = DATA.register("modular_energy_storage",       () -> DataComponentType.<CustomData>builder().persistent(CustomData.CODEC).networkSynchronized(CustomData.STREAM_CODEC).build());

    //Module host / property provider
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<List<ModuleEntity<?>>>>      MODULE_ENTITIES             = DATA.register("module_entities",              () -> DataComponentType.<List<ModuleEntity<?>>>builder().persistent(ModuleEntity.CODEC.listOf()).networkSynchronized(ModuleEntity.STREAM_CODEC.apply(ByteBufCodecs.list())).build());
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<List<ConfigProperty>>>       CONFIG_PROPERTIES           = DATA.register("config_properties",            () -> DataComponentType.<List<ConfigProperty>>builder().persistent(ConfigProperty.CODEC.listOf()).networkSynchronized(ConfigProperty.STREAM_CODEC.apply(ByteBufCodecs.list())).build());
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<UUID>>                       PROVIDER_IDENTITY           = DATA.register("provider_identity",            () -> DataComponentType.<UUID>builder().persistent(UUIDUtil.CODEC).networkSynchronized(UUIDUtil.STREAM_CODEC).build());
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Long>>                       MODULAR_ENERGY_CAPABILITY   = DATA.register("modular_energy_capability",    () -> DataComponentType.<Long>builder().persistent(Codec.LONG).networkSynchronized(ByteBufCodecs.VAR_LONG).build());


    //Module item data
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<BooleanProperty>>            BOOL_ITEM_PROP_1            = DATA.register("bool_item_prop_1",             () -> DataComponentType.<BooleanProperty>builder().persistent(BooleanProperty.CODEC).networkSynchronized(BooleanProperty.STREAM_CODEC).build());
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<BooleanProperty>>            BOOL_ITEM_PROP_2            = DATA.register("bool_item_prop_2",             () -> DataComponentType.<BooleanProperty>builder().persistent(BooleanProperty.CODEC).networkSynchronized(BooleanProperty.STREAM_CODEC).build());
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<BooleanProperty>>            BOOL_ITEM_PROP_3            = DATA.register("bool_item_prop_3",             () -> DataComponentType.<BooleanProperty>builder().persistent(BooleanProperty.CODEC).networkSynchronized(BooleanProperty.STREAM_CODEC).build());
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<IntegerProperty>>            INT_ITEM_PROP_1             = DATA.register("int_item_prop_1",              () -> DataComponentType.<IntegerProperty>builder().persistent(IntegerProperty.CODEC).networkSynchronized(IntegerProperty.STREAM_CODEC).build());
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<IntegerProperty>>            INT_ITEM_PROP_2             = DATA.register("int_item_prop_2",              () -> DataComponentType.<IntegerProperty>builder().persistent(IntegerProperty.CODEC).networkSynchronized(IntegerProperty.STREAM_CODEC).build());
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<IntegerProperty>>            INT_ITEM_PROP_3             = DATA.register("int_item_prop_3",              () -> DataComponentType.<IntegerProperty>builder().persistent(IntegerProperty.CODEC).networkSynchronized(IntegerProperty.STREAM_CODEC).build());
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<DecimalProperty>>            DECIMAL_ITEM_PROP_1         = DATA.register("decimal_item_prop_1",          () -> DataComponentType.<DecimalProperty>builder().persistent(DecimalProperty.CODEC).networkSynchronized(DecimalProperty.STREAM_CODEC).build());
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<DecimalProperty>>            DECIMAL_ITEM_PROP_2         = DATA.register("decimal_item_prop_2",          () -> DataComponentType.<DecimalProperty>builder().persistent(DecimalProperty.CODEC).networkSynchronized(DecimalProperty.STREAM_CODEC).build());
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<DecimalProperty>>            DECIMAL_ITEM_PROP_3         = DATA.register("decimal_item_prop_3",          () -> DataComponentType.<DecimalProperty>builder().persistent(DecimalProperty.CODEC).networkSynchronized(DecimalProperty.STREAM_CODEC).build());

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Long>>                       ENERGY_MODULE_ENERGY        = DATA.register("energy_module_energy",         () -> DataComponentType.<Long>builder().persistent(Codec.LONG).networkSynchronized(ByteBufCodecs.VAR_LONG).build());

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<UUID>>                       LINK_MODULE_LINK_ID         = DATA.register("link_module_link_id",          () -> DataComponentType.<UUID>builder().persistent(UUIDUtil.CODEC).networkSynchronized(UUIDUtil.STREAM_CODEC).build());
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<GlobalPos>>                  LINK_MODULE_LINK_POS        = DATA.register("link_module_link_pos",         () -> DataComponentType.<GlobalPos>builder().persistent(GlobalPos.CODEC).networkSynchronized(GlobalPos.STREAM_CODEC).build());

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>>                    SHIELD_MODULE_CAP           = DATA.register("shield_module_cap",            () -> DataComponentType.<Integer>builder().persistent(Codec.INT).networkSynchronized(ByteBufCodecs.INT).build());
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Double>>                     SHIELD_MODULE_POINTS        = DATA.register("shield_module_points",         () -> DataComponentType.<Double>builder().persistent(Codec.DOUBLE).networkSynchronized(ByteBufCodecs.DOUBLE).build());
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>>                    SHIELD_MODULE_COOLDWN       = DATA.register("shield_module_cooldwn",        () -> DataComponentType.<Integer>builder().persistent(Codec.INT).networkSynchronized(ByteBufCodecs.INT).build());

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>>                    UNDYING_MODULE_CHARGE       = DATA.register("undying_module_charge",        () -> DataComponentType.<Integer>builder().persistent(Codec.INT).networkSynchronized(ByteBufCodecs.INT).build());

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Double>>                     AUTO_FEED_MODULE_FOOD       = DATA.register("auto_feed_module_food",        () -> DataComponentType.<Double>builder().persistent(Codec.DOUBLE).networkSynchronized(ByteBufCodecs.DOUBLE).build());

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<InventoryDynamic>>           TREE_MODULE_INVENTORY       = DATA.register("tree_module_inventory",        () -> DataComponentType.<InventoryDynamic>builder().persistent(InventoryDynamic.CODEC).networkSynchronized(InventoryDynamic.STREAM_CODEC).build());

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Map<Integer, ItemStack>>>    FILTER_MODULE_STACKS        = DATA.register("filter_module_stacks",         () -> DataComponentType.<Map<Integer, ItemStack>>builder().persistent(Codec.unboundedMap(Codec.INT, ItemStack.CODEC)).networkSynchronized(ByteBufCodecs.map(HashMap::new, ByteBufCodecs.INT, ItemStack.STREAM_CODEC)).build());
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Map<Integer, TagKey<Item>>>> FILTER_MODULE_TAGS          = DATA.register("filter_module_tags",           () -> DataComponentType.<Map<Integer, TagKey<Item>>>builder().persistent(Codec.unboundedMap(Codec.INT, TagKey.codec(Registries.ITEM))).networkSynchronized(ByteBufCodecs.map(HashMap::new, ByteBufCodecs.INT, BCStreamCodec.tagKeyCodec(Registries.ITEM))).build());

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<CustomData>>                 ENDER_MODULE_FREQUENCY      = DATA.register("ender_module_frequency",       () -> DataComponentType.<CustomData>builder().persistent(CustomData.CODEC).networkSynchronized(CustomData.STREAM_CODEC).build());
    //@formatter:on
}
