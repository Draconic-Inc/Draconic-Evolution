package com.brandon3055.draconicevolution.init;

import com.brandon3055.brandonscore.api.TechLevel;
import com.brandon3055.brandonscore.utils.TargetPos;
import com.brandon3055.draconicevolution.api.capability.ModuleHost;
import com.brandon3055.draconicevolution.api.modules.lib.ModularOPStorage;
import com.brandon3055.draconicevolution.items.tools.DislocatorAdvanced;
import com.brandon3055.draconicevolution.items.tools.DislocatorAdvanced.DislocatorTarget;
import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.UUIDUtil;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.component.CustomData;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;

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
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<TechLevel>>              TECH_LEVEL                  = DATA.register("tech_level",                   () -> DataComponentType.<TechLevel>builder().persistent(TechLevel.CODEC).networkSynchronized(TechLevel.STREAM_CODEC).build());

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<UUID>>                   DISLOCATOR_PLAYER_ID        = DATA.register("dislocator_player_id",         () -> DataComponentType.<UUID>builder().persistent(UUIDUtil.CODEC).networkSynchronized(UUIDUtil.STREAM_CODEC).build());
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<UUID>>                   DISLOCATOR_STACK_ID         = DATA.register("dislocator_stack_id",          () -> DataComponentType.<UUID>builder().persistent(UUIDUtil.CODEC).networkSynchronized(UUIDUtil.STREAM_CODEC).build());
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<UUID>>                   DISLOCATOR_LINK_ID          = DATA.register("dislocator_link_id",           () -> DataComponentType.<UUID>builder().persistent(UUIDUtil.CODEC).networkSynchronized(UUIDUtil.STREAM_CODEC).build());
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<String>>                 DISLOCATOR_PLAYER_NAME      = DATA.register("dislocator_player_name",       () -> DataComponentType.<String>builder().persistent(Codec.STRING).networkSynchronized(ByteBufCodecs.STRING_UTF8).build());

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<TargetPos>>              DISLOCATOR_TARGET           = DATA.register("dislocator_target",            () -> DataComponentType.<TargetPos>builder().persistent(TargetPos.CODEC).networkSynchronized(TargetPos.STREAM_CODEC).build());
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>>                DISLOCATOR_FUEL             = DATA.register("dislocator_fuel",              () -> DataComponentType.<Integer>builder().persistent(Codec.INT).networkSynchronized(ByteBufCodecs.INT).build());
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>>                DISLOCATOR_SELECTED         = DATA.register("dislocator_selected",          () -> DataComponentType.<Integer>builder().persistent(Codec.INT).networkSynchronized(ByteBufCodecs.INT).build());
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Boolean>>                DISLOCATOR_BLINK            = DATA.register("dislocator_blink",             () -> DataComponentType.<Boolean>builder().persistent(Codec.BOOL).networkSynchronized(ByteBufCodecs.BOOL).build());
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<List<DislocatorTarget>>> DISLOCATOR_TARGETS       = DATA.register("dislocator_targets",              () -> DataComponentType.<List<DislocatorTarget>>builder().persistent(DislocatorTarget.CODEC.listOf()).networkSynchronized(DislocatorTarget.STREAM_CODEC.apply(ByteBufCodecs.list())).build());

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Boolean>>                MAGNET_ACTIVE               = DATA.register("magnet_active",                () -> DataComponentType.<Boolean>builder().persistent(Codec.BOOL).networkSynchronized(ByteBufCodecs.BOOL).build());

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<ResourceLocation>>       SOUL_ID                     = DATA.register("soul_id",                      () -> DataComponentType.<ResourceLocation>builder().persistent(ResourceLocation.CODEC).networkSynchronized(ResourceLocation.STREAM_CODEC).build());
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<CustomData>>             SOUL_DATA                   = DATA.register("soul_data",                    () -> DataComponentType.<CustomData>builder().persistent(CustomData.CODEC).networkSynchronized(CustomData.STREAM_CODEC).build());

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<GlobalPos>>              BINDER_POS                  = DATA.register("binder_pos",                   () -> DataComponentType.<GlobalPos>builder().persistent(GlobalPos.CODEC).networkSynchronized(GlobalPos.STREAM_CODEC).build());

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<CustomData>>             MODULE_ENTITY_TAG           = DATA.register("module_entity_tag",            () -> DataComponentType.<CustomData>builder().persistent(CustomData.CODEC).networkSynchronized(CustomData.STREAM_CODEC).build());

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<UUID>>                   LINK_MODULE_LINK_ID         = DATA.register("link_module_link_id",          () -> DataComponentType.<UUID>builder().persistent(UUIDUtil.CODEC).networkSynchronized(UUIDUtil.STREAM_CODEC).build());
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<GlobalPos>>              LINK_MODULE_LINK_POS        = DATA.register("link_module_link_pos",         () -> DataComponentType.<GlobalPos>builder().persistent(GlobalPos.CODEC).networkSynchronized(GlobalPos.STREAM_CODEC).build());

//    public static final DeferredHolder<DataComponentType<?>, DataComponentType<ModuleHost>>             MODULAR_ITEM_HOST           = DATA.register("module_item_host",             () -> DataComponentType.<ModuleHost>builder().persistent()serializable(CapabilityData::createHostFor).build());
//    public static final DeferredHolder<DataComponentType<?>, DataComponentType<ModularOPStorage>>       MODULAR_ITEM_ENERGY         = DATA.register("module_item_energy",           () -> DataComponentType.<ModularOPStorage>builder().persistent()serializable(CapabilityData::createEnergyFor).build());

    //@formatter:on
}
