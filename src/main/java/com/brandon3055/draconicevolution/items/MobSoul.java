package com.brandon3055.draconicevolution.items;

import com.brandon3055.brandonscore.api.TimeKeeper;
import com.brandon3055.brandonscore.utils.InventoryUtils;
import com.brandon3055.draconicevolution.DraconicEvolution;
import com.brandon3055.draconicevolution.init.DEContent;
import com.brandon3055.draconicevolution.init.ItemData;
import com.brandon3055.draconicevolution.utils.LogHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.event.EventHooks;

import javax.annotation.Nullable;
import java.util.*;

/**
 * Created by brandon3055 on 25/09/2016.
 */
public class MobSoul extends Item {

    private static final Map<ResourceLocation, Entity> renderEntityMap = new HashMap<>();
    public static List<ResourceLocation> randomDisplayList = null;

    public MobSoul(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        Direction facing = context.getClickedFace();
        BlockPos pos = context.getClickedPos();
        Player player = context.getPlayer();

        ItemStack stack = player.getItemInHand(context.getHand());
        if (player.isShiftKeyDown()) {

            Entity entity = createEntity(level, stack);
            double sX = pos.getX() + facing.getStepX() + 0.5;
            double sY = pos.getY() + facing.getStepY() + 0.5;
            double sZ = pos.getZ() + facing.getStepZ() + 0.5;
            if (entity == null) {
                LogHelper.error("Mob Soul bound entity = null");
                return super.useOn(context);
            }
            entity.moveTo(sX, sY, sZ, player.getYRot(), 0F);

            if (!level.isClientSide) {
                if (!stack.has(ItemData.SOUL_DATA) && entity instanceof Mob mob && level instanceof ServerLevel serverLevel) {
                    EventHooks.finalizeMobSpawn(mob, serverLevel, serverLevel.getCurrentDifficultyAt(new BlockPos(0, 0, 0)), MobSpawnType.SPAWN_EGG, null);
                }
                level.addFreshEntity(entity);
                if (!player.getAbilities().instabuild) {
                    InventoryUtils.consumeHeldItem(player, stack, context.getHand());
                }
            }
        }
        return super.useOn(context);
    }

    @Override
    public Component getName(ItemStack stack) {
        EntityType<?> type = BuiltInRegistries.ENTITY_TYPE.get(getEntity(stack));
        return Component.translatable(type.getDescriptionId()).append(" ").append(super.getName(stack));
    }

    public ResourceLocation getEntity(ItemStack stack) {
        return stack.getOrDefault(ItemData.SOUL_ID, ResourceLocation.withDefaultNamespace("pig"));
    }

    public void setEntity(ResourceLocation entityName, ItemStack stack) {
        stack.set(ItemData.SOUL_ID, entityName);
    }

    @Nullable
    public CompoundTag getEntityData(ItemStack stack) {
        CustomData data = stack.get(ItemData.SOUL_DATA);
        if (data == null || data.isEmpty()) {
            return null;
        }
        return data.copyTag();
    }

    public void setEntityData(CompoundTag compound, ItemStack stack) {
        compound.remove("UUID");
        compound.remove("Motion");
        stack.set(ItemData.SOUL_DATA, CustomData.of(compound));
    }

    public Entity createEntity(Level level, ItemStack stack) {
        try {
            CompoundTag entityData = getEntityData(stack);
            EntityType<?> type = BuiltInRegistries.ENTITY_TYPE.get(getEntity(stack));
            Entity entity;

            entity = type.create(level);
            if (entity == null) {
                return EntityType.PIG.create(level);
            }
            if (entityData != null) {
                entity.load(entityData);
            } else {
                if (entity instanceof Mob mob && level instanceof ServerLevel serverLevel) {
                    EventHooks.finalizeMobSpawn(mob, serverLevel, serverLevel.getCurrentDifficultyAt(new BlockPos(0, 0, 0)), MobSpawnType.SPAWN_EGG, null);
                }
            }
            return entity;
        } catch (Throwable e) {
            return EntityType.PIG.create(level);
        }
    }

    public ItemStack getSoulFromEntity(Entity entity, boolean saveEntityData) {
        ItemStack soul = new ItemStack(DEContent.MOB_SOUL.get());

        setEntity(BuiltInRegistries.ENTITY_TYPE.getKey(entity.getType()), soul);

        if (saveEntityData) {
            CompoundTag compound = new CompoundTag();
            entity.saveWithoutId(compound);
            setEntityData(compound, soul);
        }

        return soul;
    }

    public Entity getRenderEntity(ItemStack stack) {
        return getRenderEntity(getEntity(stack));
    }

    @OnlyIn (Dist.CLIENT)
    public Entity getRenderEntity(ResourceLocation name) {
        if (name == null || name.equals(ResourceLocation.fromNamespaceAndPath(DraconicEvolution.MODID, "random_display_entity"))) {
            if (randomDisplayList == null) {
                randomDisplayList = new ArrayList<>();
                SpawnEggItem.BY_ID.keySet().forEach(type -> randomDisplayList.add(BuiltInRegistries.ENTITY_TYPE.getKey(type)));
            }

            if (!randomDisplayList.isEmpty()) {
                name = randomDisplayList.get((TimeKeeper.getClientTick() / 20) % randomDisplayList.size());
            }
        }

        if (!renderEntityMap.containsKey(name)) {
            Level level = Minecraft.getInstance().level;
            Entity entity;
            try {
                EntityType<?> type = BuiltInRegistries.ENTITY_TYPE.get(name);
                entity = type.create(level);
                if (entity == null) {
                    entity = EntityType.PIG.create(level);
                }
            } catch (Throwable e) {
                entity = EntityType.PIG.create(level);
            }
            renderEntityMap.put(name, entity);
        }

        return renderEntityMap.get(name);
    }
}
