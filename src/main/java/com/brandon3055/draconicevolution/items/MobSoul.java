package com.brandon3055.draconicevolution.items;

import com.brandon3055.brandonscore.items.ItemBCore;
import com.brandon3055.brandonscore.utils.InventoryUtils;
import com.brandon3055.brandonscore.utils.ItemNBTHelper;
import com.brandon3055.draconicevolution.client.handler.ClientEventHandler;
import com.brandon3055.draconicevolution.init.DEContent;
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
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.*;

/**
 * Created by brandon3055 on 25/09/2016.
 */
public class MobSoul extends ItemBCore {

    private static Map<String, Entity> renderEntityMap = new HashMap<>();
    private static Map<String, String> entityNameCache = new HashMap<>();
    public static List<String> randomDisplayList = null;
    private static Map<String, ResourceLocation> rlCache = new WeakHashMap<>();

    public MobSoul(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level world = context.getLevel();
        Direction facing = context.getClickedFace();
        BlockPos pos = context.getClickedPos();
        Player player = context.getPlayer();

        ItemStack stack = player.getItemInHand(context.getHand());
        if (player.isShiftKeyDown()) {

            Entity entity = createEntity(world, stack);
            double sX = pos.getX() + facing.getStepX() + 0.5;
            double sY = pos.getY() + facing.getStepY() + 0.5;
            double sZ = pos.getZ() + facing.getStepZ() + 0.5;
            if (entity == null) {
                LogHelper.error("Mob Soul bound entity = null");
                return super.useOn(context);
            }
            entity.moveTo(sX, sY, sZ, player.getYRot(), 0F);

            if (!world.isClientSide) {
                CompoundTag compound = ItemNBTHelper.getCompound(stack);
                if (!compound.contains("EntityData") && entity instanceof Mob) {
                    ((Mob) entity).finalizeSpawn((ServerLevel) world, world.getCurrentDifficultyAt(new BlockPos(0, 0, 0)), MobSpawnType.SPAWN_EGG, null, null);
                }
                world.addFreshEntity(entity);
                if (!player.getAbilities().instabuild) {
                    InventoryUtils.consumeHeldItem(player, stack, context.getHand());
                }
            }
        }
        return super.useOn(context);
    }

    @Override
    public Component getName(ItemStack stack) {
        String eName = getEntityString(stack);
        EntityType<?> type = BuiltInRegistries.ENTITY_TYPE.get(getCachedRegName(eName));
        return Component.translatable(type.getDescriptionId()).append(" ").append(super.getName(stack));
    }

    public String getEntityString(ItemStack stack) {
        return ItemNBTHelper.getString(stack, "EntityName", "pig");
    }

    public void setEntity(ResourceLocation entityName, ItemStack stack) {
        ItemNBTHelper.setString(stack, "EntityName", String.valueOf(entityName));
    }

    @Nullable
    public CompoundTag getEntityData(ItemStack stack) {
        CompoundTag compound = ItemNBTHelper.getCompound(stack);
        if (compound.contains("EntityData")) {
            return compound.getCompound("EntityData");
        }
        return null;
    }

    public void setEntityData(CompoundTag compound, ItemStack stack) {
        compound.remove("UUID");
        compound.remove("Motion");
        ItemNBTHelper.getCompound(stack).put("EntityData", compound);
    }

    public Entity createEntity(Level world, ItemStack stack) {
        try {
            String eName = getEntityString(stack);
            CompoundTag entityData = getEntityData(stack);
            EntityType<?> type = BuiltInRegistries.ENTITY_TYPE.get(getCachedRegName(eName));
            Entity entity;

            entity = type.create(world);
            if (entity == null) {
                return EntityType.PIG.create(world);
            }
            if (entityData != null) {
                entity.load(entityData);
            } else {
                if (entity instanceof Mob) {
                    ((Mob) entity).finalizeSpawn((ServerLevel) world, world.getCurrentDifficultyAt(new BlockPos(0, 0, 0)), MobSpawnType.SPAWN_EGG, null, null);
                }
            }
            return entity;
        } catch (Throwable e) {
            return EntityType.PIG.create(world);
        }
    }

    public ItemStack getSoulFromEntity(Entity entity, boolean saveEntityData) {
        ItemStack soul = new ItemStack(DEContent.MOB_SOUL.get());

        String registryName = BuiltInRegistries.ENTITY_TYPE.getKey(entity.getType()).toString();
        ItemNBTHelper.setString(soul, "EntityName", registryName);

        if (saveEntityData) {
            CompoundTag compound = new CompoundTag();
            entity.saveWithoutId(compound);
            setEntityData(compound, soul);
        }

        return soul;
    }

    public Entity getRenderEntity(ItemStack stack) {
        return getRenderEntity(getEntityString(stack));
    }

    @OnlyIn (Dist.CLIENT)
    public Entity getRenderEntity(String name) {
        if (name.equals("[Random-Display]")) {
            if (randomDisplayList == null) {
                randomDisplayList = new ArrayList<>();
                SpawnEggItem.BY_ID.keySet().forEach(type -> randomDisplayList.add(BuiltInRegistries.ENTITY_TYPE.getKey(type).toString()));
            }

            if (randomDisplayList.size() > 0) {
                name = randomDisplayList.get((ClientEventHandler.elapsedTicks / 20) % randomDisplayList.size());
            }
        }

        if (!renderEntityMap.containsKey(name)) {
            Level level = Minecraft.getInstance().level;
            Entity entity;
            try {
                EntityType<?> type = BuiltInRegistries.ENTITY_TYPE.get(getCachedRegName(name));
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

    public static ResourceLocation getCachedRegName(String name) {
        return rlCache.computeIfAbsent(name, ResourceLocation::new);
    }
}
