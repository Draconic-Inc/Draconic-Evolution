package com.brandon3055.draconicevolution.items;

import com.brandon3055.brandonscore.items.ItemBCore;
import com.brandon3055.brandonscore.utils.InventoryUtils;
import com.brandon3055.brandonscore.utils.ItemNBTHelper;
import com.brandon3055.draconicevolution.DEConfig;
import com.brandon3055.draconicevolution.init.DEContent;
import com.brandon3055.draconicevolution.client.handler.ClientEventHandler;
import com.brandon3055.draconicevolution.utils.LogHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.registries.ForgeRegistries;

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
    public ActionResultType useOn(ItemUseContext context) {
        World world = context.getLevel();
        Direction facing = context.getClickedFace();
        BlockPos pos = context.getClickedPos();
        PlayerEntity player = context.getPlayer();

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
            entity.moveTo(sX, sY, sZ, player.yRot, 0F);

            if (!world.isClientSide) {
                CompoundNBT compound = ItemNBTHelper.getCompound(stack);
                if (!compound.contains("EntityData") && entity instanceof MobEntity) {
                    ((MobEntity) entity).finalizeSpawn((ServerWorld)world, world.getCurrentDifficultyAt(new BlockPos(0, 0, 0)), SpawnReason.SPAWN_EGG, null, null);
                }
                world.addFreshEntity(entity);
                if (!player.abilities.instabuild) {
                    InventoryUtils.consumeHeldItem(player, stack, context.getHand());
                }
            }
        }
        return super.useOn(context);
    }

//    @Override
//    public String getItemStackDisplayName(ItemStack stack) {
//        String eString = getEntityString(stack);
//
//        if (eString.equals("[Random-Display]")) {
//            return "Mob " + I18n.translateToLocal(this.getUnlocalizedNameInefficiently(stack) + ".name");
//        }
//
//        String eModifier = "";
//        if (ItemNBTHelper.verifyExistance(stack, "ZombieVillagerType")) {
//            eModifier = "-" + ItemNBTHelper.getString(stack, "ZombieVillagerType", "minecraft:farmer");
//        }
//        else if (ItemNBTHelper.verifyExistance(stack, "ZombieTypeHusk")) {
//            eModifier = "-Husk";
//        }
//
//        String localizedName = entityNameCache.computeIfAbsent(eString + eModifier, s -> {
//            try {
//                Entity entity = EntityList.createEntityByIDFromName(getCachedRegName(eString), null);
//                if (entity == null) {
//                    return I18n.translateToLocal("entity." + EntityList.getTranslationName(getCachedRegName(eString)) + ".name");
//                }
//
//                loadAdditionalEntityInfo(stack, entity);
//                return entity.getName();
//            }
//            catch (Throwable e) {
//                return "Name-Error";
//            }
//        });
//
//
//        return localizedName + " " + I18n.translateToLocal(this.getUnlocalizedNameInefficiently(stack) + ".name");
//    }

//    @OnlyIn(Dist.CLIENT)
//    @Override
//    public void registerRenderer(Feature feature) {
//        ModelRegistryHelper.registerItemRenderer(this, new RenderItemMobSoul());
//    }
//
//    @Override
//    public boolean registerNormal(Feature feature) {
//        return false;
//    }

    public String getEntityString(ItemStack stack) {
        return ItemNBTHelper.getString(stack, "EntityName", "Pig");
    }

    public void setEntity(ResourceLocation entityName, ItemStack stack) {
        ItemNBTHelper.setString(stack, "EntityName", String.valueOf(entityName));
    }

    @Nullable
    public CompoundNBT getEntityData(ItemStack stack) {
        CompoundNBT compound = ItemNBTHelper.getCompound(stack);
        if (compound.contains("EntityData")) {
            return compound.getCompound("EntityData");
        }
        return null;
    }

    public void setEntityData(CompoundNBT compound, ItemStack stack) {
        compound.remove("UUID");
        compound.remove("Motion");
        ItemNBTHelper.getCompound(stack).put("EntityData", compound);
    }

    public Entity createEntity(World world, ItemStack stack) {
        try {
            String eName = getEntityString(stack);
            CompoundNBT entityData = getEntityData(stack);
            EntityType type = ForgeRegistries.ENTITIES.getValue(getCachedRegName(eName));
            Entity entity;

            if (type == null) {
                entity = EntityType.PIG.create(world);
            }
            else {
                entity = type.create(world);
                if (entity == null) {
                    return EntityType.PIG.create(world);
                }
                if (entityData != null) {
                    entity.load(entityData);
                }
                else {
                    loadAdditionalEntityInfo(stack, entity);
                    if (entity instanceof MobEntity) {
                        ((MobEntity) entity).finalizeSpawn((ServerWorld)world, world.getCurrentDifficultyAt(new BlockPos(0, 0, 0)), SpawnReason.SPAWN_EGG, null, null);
//                        entitytype.spawn(worldIn, itemstack, playerIn, blockpos, SpawnReason.SPAWN_EGG, false, false)
//                        type.spawn(world, stack, null, new BlockPos(0, 0, 0), SpawnReason.SPAWN_EGG, false, false);

//                        if (!ForgeEventFactory.doSpecialSpawn((MobEntity) entity, world, (float) entity.getPosX(), (float) entity.getPosY(), (float) entity.getPosZ())) {
//                            ((LivingEntity) entity).onInitialSpawn(world.getDifficultyForLocation(new BlockPos(entity)), null);
//                        }
                    }
                }
            }
            return entity;
        }
        catch (Throwable e) {
            return EntityType.PIG.create(world);
        }
    }

    public ItemStack getSoulFromEntity(Entity entity, boolean saveEntityData) {
        ItemStack soul = new ItemStack(DEContent.mob_soul);

        String registryName = entity.getType().getRegistryName().toString();
        ItemNBTHelper.setString(soul, "EntityName", registryName);

        if (saveEntityData) {
            CompoundNBT compound = new CompoundNBT();
            entity.saveWithoutId(compound);
            setEntityData(compound, soul);
        }
        else {
            saveAditionalEntityInfo(entity, soul);
        }

        return soul;
    }

    public Entity getRenderEntity(ItemStack stack) {
        return getRenderEntity(getEntityString(stack));
    }

    @OnlyIn(Dist.CLIENT)
    public Entity getRenderEntity(String name) {
        if (name.equals("[Random-Display]")) {
            if (randomDisplayList == null) {
                randomDisplayList = new ArrayList<>();
                SpawnEggItem.BY_ID.keySet().forEach(type -> randomDisplayList.add(type.getRegistryName().toString()));
            }

            if (randomDisplayList.size() > 0) {
                name = randomDisplayList.get((ClientEventHandler.elapsedTicks / 20) % randomDisplayList.size());
            }
        }

        if (!renderEntityMap.containsKey(name)) {
            World world = Minecraft.getInstance().level;
            Entity entity;
            try {
                EntityType type = ForgeRegistries.ENTITIES.getValue(getCachedRegName(name));
                if (type != null) {
                    entity = type.create(world);
                    if (entity == null) {
                        entity = EntityType.PIG.create(world);
                    }
                }
                else {
                    entity = EntityType.PIG.create(world);
                }
            }
            catch (Throwable e) {
                entity = EntityType.PIG.create(world);
            }
            renderEntityMap.put(name, entity);
        }

        return renderEntityMap.get(name);
    }

    private void saveAditionalEntityInfo(Entity entity, ItemStack stack) {
//        if (entity instanceof EntitySkeleton) {TODO Is this not needed now? Mojang? Did you do a good thing?
//            ItemNBTHelper.setInteger(stack, "EntityTypeID", ((EntitySkeleton) entity).getSkeletonType().getId());
//        }
//        else if (entity instanceof EntityZombie) {
//            VillagerRegistry.VillagerProfession prof = ((EntityZombie) entity).getVillagerTypeForge();
//            if (prof != null && prof.getRegistryName() != null) {
//                ItemNBTHelper.setString(stack, "ZombieVillagerType", prof.getRegistryName().toString());
//            }
//            else if (((EntityZombie) entity).getZombieType() == ZombieType.HUSK) {
//                ItemNBTHelper.setBoolean(stack, "ZombieTypeHusk", true);
//            }
//        }
    }

    private void loadAdditionalEntityInfo(ItemStack stack, Entity entity) {
//        if (entity instanceof EntitySkeleton && ItemNBTHelper.verifyExistance(stack, "EntityTypeID")) {
//            ((EntitySkeleton) entity).setSkeletonType(SkeletonType.getByOrdinal(ItemNBTHelper.getInteger(stack, "EntityTypeID", ((EntitySkeleton) entity).getSkeletonType().getId())));
//        }
//        else if (entity instanceof EntityZombie) {
//            if (ItemNBTHelper.verifyExistance(stack, "ZombieVillagerType")) {
//                String name = ItemNBTHelper.getString(stack, "ZombieVillagerType", "minecraft:farmer");
//                VillagerRegistry.VillagerProfession p = ForgeRegistries.VILLAGER_PROFESSIONS.getValue(new ResourceLocation(name));
//                if (p == null) {
//                    p = ForgeRegistries.VILLAGER_PROFESSIONS.getValue(new ResourceLocation("minecraft:farmer"));
//                }
//                ((EntityZombie) entity).setVillagerType(p);
//            }
//            else if (ItemNBTHelper.verifyExistance(stack, "ZombieTypeHusk")) {
//                ((EntityZombie) entity).setZombieType(ZombieType.HUSK);
//            }
//        }
    }

    public static ResourceLocation getCachedRegName(String name) {
        return rlCache.computeIfAbsent(name, ResourceLocation::new);
    }

    public static boolean isValidEntity(LivingEntity entity) {
        ResourceLocation location = entity.getType().getRegistryName();
        String registryName = location == null ? null : location.toString();

        if (!entity.canChangeDimensions() && !DEConfig.allowBossSouls) {
            return false;
        }
        for (int i = 0; i < DEConfig.spawnerList.length; i++) {
            if (DEConfig.spawnerList[i].equals(registryName) && DEConfig.spawnerListWhiteList) {
                return true;
            } else if (DEConfig.spawnerList[i].equals(registryName) && !DEConfig.spawnerListWhiteList) {
                return false;
            }
        }
        return !DEConfig.spawnerListWhiteList;
    }
}
