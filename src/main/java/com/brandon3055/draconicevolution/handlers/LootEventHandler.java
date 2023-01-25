package com.brandon3055.draconicevolution.handlers;

import com.brandon3055.draconicevolution.DEConfig;
import com.brandon3055.draconicevolution.achievements.Achievements;
import com.brandon3055.draconicevolution.api.IReaperItem;
import com.brandon3055.draconicevolution.entity.guardian.DraconicGuardianEntity;
import com.brandon3055.draconicevolution.init.DEContent;
import net.covers1624.quack.util.CrashLock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.dimension.end.EndDragonFight;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.EndPodiumFeature;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

/**
 * Created by brandon3055 on 25/01/2023
 */
public class LootEventHandler {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final CrashLock LOCK = new CrashLock("Already Initialized");
    private static final Random RANDOM = new Random();

    public static void init() {
        LOCK.lock();
        MinecraftForge.EVENT_BUS.addListener(LootEventHandler::onDropEvent);
    }

    public static void onDropEvent(LivingDropsEvent event) {
        LivingEntity entity = event.getEntityLiving();
        Level level = entity.level;
        if (level.isClientSide()) {
            return;
        }

        handleDragonDrops(entity, event);

        Entity attacker = event.getSource().getEntity();
        if (!(attacker instanceof Player player)) {
            return;
        }

        handleSoulDrops(entity, player, level, event);

    }

    private static final List<UUID> deadDragons = new LinkedList<>();

    private static void handleDragonDrops(LivingEntity entity, LivingDropsEvent event) {
        if (deadDragons.contains(entity.getUUID())) {
            event.setCanceled(true);
            return;
        }

        if (entity instanceof EnderDragon || entity instanceof DraconicGuardianEntity) {
            deadDragons.add(entity.getUUID());

            ItemEntity item = EntityType.ITEM.create(entity.level);
            if (item != null) {
                item.setItem(new ItemStack(DEContent.dragon_heart));
                BlockPos podiumPos = entity.level.getHeightmapPos(Heightmap.Types.WORLD_SURFACE, EndPodiumFeature.END_PODIUM_LOCATION).offset(0, 3, 0);
                item.moveTo(podiumPos.getX() + 0.5, podiumPos.getY(), podiumPos.getZ() + 0.5, 0, 0);
                item.setDeltaMovement(0, 0, 0);
                item.age = -32767;
                item.setNoGravity(true);
                entity.level.addFreshEntity(item);
            }

            if (entity instanceof EnderDragon) {
                EndDragonFight manager = ((EnderDragon) entity).getDragonFight();
                if (DEConfig.dragonEggSpawnOverride && manager != null && manager.hasPreviouslyKilledDragon()) {
                    entity.level.setBlockAndUpdate(entity.level.getHeightmapPos(Heightmap.Types.WORLD_SURFACE, EndPodiumFeature.END_PODIUM_LOCATION).offset(0, 0, -4), Blocks.DRAGON_EGG.defaultBlockState());
                }
            }

            if (DEConfig.dragonDustLootModifier > 0) {
                double count = (DEConfig.dragonDustLootModifier * 0.9D) + (entity.level.random.nextDouble() * (DEConfig.dragonDustLootModifier * 0.2));
                for (int i = 0; i < (int) count; i++) {
                    float mm = 0.3F;
                    ItemEntity dust = new ItemEntity(entity.level, entity.getX() - 2 + entity.level.random.nextInt(4), entity.getY() - 2 + entity.level.random.nextInt(4), entity.getZ() - 2 + entity.level.random.nextInt(4), new ItemStack(DEContent.dust_draconium));
                    dust.setDeltaMovement(
                            mm * ((((float) entity.level.random.nextInt(100)) / 100F) - 0.5F),
                            mm * ((((float) entity.level.random.nextInt(100)) / 100F) - 0.5F),
                            mm * ((((float) entity.level.random.nextInt(100)) / 100F) - 0.5F)
                    );
                    entity.level.addFreshEntity(dust);
                }
            }
        }
    }

    private static void handleSoulDrops(LivingEntity entity, Player player, Level level, LivingDropsEvent event) {
        if (!(event.getSource().msgId.equals("player") || event.getSource().msgId.equals("arrow")) || !canEntityDropSoul(entity)) {
            return;
        }

        if (entity instanceof Player) {
            return;
        }

        int dropChanceModifier = getSoulDropChance(player.getMainHandItem());
        if (dropChanceModifier == 0) {
            return;
        }

        int rand = RANDOM.nextInt(Math.max(DEConfig.soulDropChance / dropChanceModifier, 1));
        int rand2 = RANDOM.nextInt(Math.max(DEConfig.passiveSoulDropChance / dropChanceModifier, 1));
        boolean isAnimal = entity instanceof Animal;

        if ((rand == 0 && !isAnimal) || (rand2 == 0 && isAnimal)) {
            ItemStack soul = DEContent.mob_soul.getSoulFromEntity(entity, false);
            level.addFreshEntity(new ItemEntity(level, entity.getX(), entity.getY(), entity.getZ(), soul));
            Achievements.triggerAchievement(player, "draconicevolution.soul");
        }
    }

    private static int getSoulDropChance(ItemStack stack) {
        int chance = 0;
        if (stack.isEmpty()) {
            return 0;
        }

        if (stack.getItem() instanceof IReaperItem) {
            chance = ((IReaperItem) stack.getItem()).getReaperLevel(stack);
        }

        chance += EnchantmentHelper.getItemEnchantmentLevel(DEContent.reaperEnchant, stack);
        return chance;
    }

    private static boolean canEntityDropSoul(LivingEntity entity) {
        if (!entity.canChangeDimensions() && !DEConfig.allowBossSouls) {
            return false;
        }
        //noinspection DataFlowIssue
        String regName = entity.getType().getRegistryName().toString();
        if (DEConfig.spawnerList.contains(regName) && DEConfig.spawnerListWhiteList) {
            return true;
        } else if (DEConfig.spawnerList.contains(regName) && !DEConfig.spawnerListWhiteList) {
            return false;
        }
        return !DEConfig.spawnerListWhiteList;
    }
}
