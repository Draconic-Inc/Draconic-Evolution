package com.brandon3055.draconicevolution.handlers;

import com.brandon3055.brandonscore.api.power.IOPStorage;
import com.brandon3055.brandonscore.utils.EnergyUtils;
import com.brandon3055.draconicevolution.DEConfig;
import com.brandon3055.draconicevolution.api.IReaperItem;
import com.brandon3055.draconicevolution.api.capability.DECapabilities;
import com.brandon3055.draconicevolution.api.capability.ModuleHost;
import com.brandon3055.draconicevolution.api.modules.ModuleTypes;
import com.brandon3055.draconicevolution.api.modules.entities.EnderCollectionEntity;
import com.brandon3055.draconicevolution.api.modules.entities.JunkFilterEntity;
import com.brandon3055.draconicevolution.api.modules.lib.ModuleEntity;
import com.brandon3055.draconicevolution.entity.guardian.DraconicGuardianEntity;
import com.brandon3055.draconicevolution.init.DEContent;
import net.covers1624.quack.util.CrashLock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.dimension.end.EndDragonFight;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.EndPodiumFeature;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.living.LivingDropsEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;
import java.util.function.Predicate;

/**
 * Created by brandon3055 on 25/01/2023
 */
public class LootEventHandler {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final CrashLock LOCK = new CrashLock("Already Initialized");
    private static final Random RANDOM = new Random();

    public static void init() {
        LOCK.lock();
        NeoForge.EVENT_BUS.addListener(EventPriority.HIGH, LootEventHandler::addDrops);
        NeoForge.EVENT_BUS.addListener(EventPriority.LOWEST, LootEventHandler::processDrops);
    }

    public static void addDrops(LivingDropsEvent event) {
        LivingEntity entity = event.getEntity();
        Level level = entity.level();
        if (level.isClientSide() || event.isCanceled()) {
            return;
        }

        handleDragonDrops(entity, event);

        Entity attacker = event.getSource().getEntity();
        if (!(attacker instanceof Player player)) {
            return;
        }

        handleSoulDrops(entity, player, level, event);
    }

    public static void processDrops(LivingDropsEvent event) {
        Entity attacker = event.getSource().getEntity();
        if (!(attacker instanceof Player player) || player.level().isClientSide()) {
            return;
        }
        handleLootCollection(player, event);
    }


    private static final List<UUID> deadDragons = new LinkedList<>();

    private static void handleDragonDrops(LivingEntity entity, LivingDropsEvent event) {
        if (deadDragons.contains(entity.getUUID())) {
            event.setCanceled(true);
            return;
        }

        if (entity instanceof EnderDragon dragon) {
            deadDragons.add(entity.getUUID());
            EndDragonFight manager = dragon.getDragonFight();
            if (manager == null) return;

            ItemEntity item = EntityType.ITEM.create(entity.level());
            if (item != null) {
                item.setItem(new ItemStack(DEContent.DRAGON_HEART.get()));
                BlockPos podiumPos = entity.level().getHeightmapPos(Heightmap.Types.WORLD_SURFACE, EndPodiumFeature.getLocation(manager.origin)).offset(0, 3, 0);
                item.moveTo(podiumPos.getX() + 0.5, podiumPos.getY(), podiumPos.getZ() + 0.5, 0, 0);
                item.setDeltaMovement(0, 0, 0);
                item.setUnlimitedLifetime();
                item.setNoGravity(true);
                entity.level().addFreshEntity(item);
            }

            if (DEConfig.dragonEggSpawnOverride && manager.hasPreviouslyKilledDragon()) {
                entity.level().setBlockAndUpdate(entity.level().getHeightmapPos(Heightmap.Types.WORLD_SURFACE, EndPodiumFeature.getLocation(manager.origin)).offset(0, 0, -4), Blocks.DRAGON_EGG.defaultBlockState());
            }
        }

        if (entity instanceof EnderDragon || entity instanceof DraconicGuardianEntity) {
            if (DEConfig.dragonDustLootModifier > 0) {
                double count = (DEConfig.dragonDustLootModifier * 0.9D) + (entity.level().random.nextDouble() * (DEConfig.dragonDustLootModifier * 0.2));
                for (int i = 0; i < (int) count; i++) {
                    float mm = 0.3F;
                    ItemEntity dust = new ItemEntity(entity.level(), entity.getX() - 2 + entity.level().random.nextInt(4), entity.getY() - 2 + entity.level().random.nextInt(4), entity.getZ() - 2 + entity.level().random.nextInt(4), new ItemStack(DEContent.DUST_DRACONIUM.get()));
                    dust.setDeltaMovement(
                            mm * ((((float) entity.level().random.nextInt(100)) / 100F) - 0.5F),
                            mm * ((((float) entity.level().random.nextInt(100)) / 100F) - 0.5F),
                            mm * ((((float) entity.level().random.nextInt(100)) / 100F) - 0.5F)
                    );
                    entity.level().addFreshEntity(dust);
                }
            }
        }
    }

    private static void handleSoulDrops(LivingEntity entity, Player player, Level level, LivingDropsEvent event) {
        //TODO Test This
        if (!(event.getSource().getMsgId().equals("player") || event.getSource().getMsgId().equals("arrow")) || !canEntityDropSoul(entity)) {
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
            ItemStack soul = DEContent.MOB_SOUL.get().getSoulFromEntity(entity, false);
            event.getDrops().add(new ItemEntity(level, entity.getX(), entity.getY(), entity.getZ(), soul));
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

        chance += stack.getEnchantmentLevel(DEContent.ENCHANTMENT_REAPER.get());
        return chance;
    }

    private static boolean canEntityDropSoul(LivingEntity entity) {
        if (!entity.canChangeDimensions() && !DEConfig.allowBossSouls) {
            return false;
        }
        //noinspection DataFlowIssue
        String regName = BuiltInRegistries.ENTITY_TYPE.getKey(entity.getType()).toString();
        if (DEConfig.spawnerList.contains(regName) && DEConfig.spawnerListWhiteList) {
            return true;
        } else if (DEConfig.spawnerList.contains(regName) && !DEConfig.spawnerListWhiteList) {
            return false;
        }
        return !DEConfig.spawnerListWhiteList;
    }

    private static void handleLootCollection(Player player, LivingDropsEvent event) {
        ItemStack hostStack = player.getMainHandItem();
        if (hostStack.isEmpty() || event.getDrops().isEmpty()) return;

        ModuleHost host = hostStack.getCapability(DECapabilities.Host.ITEM);

        if (host != null) {
            Predicate<ItemStack> junkTest = null;
            for (ModuleEntity<?> entity : host.getEntitiesByType(ModuleTypes.JUNK_FILTER).toList()) {
                junkTest = junkTest == null ? ((JunkFilterEntity) entity).createFilterTest() : junkTest.or(((JunkFilterEntity) entity).createFilterTest());
            }
            if (junkTest != null) {
                Predicate<ItemStack> finalJunkTest = junkTest;
                event.getDrops().removeIf(e -> finalJunkTest.test(e.getItem()));
            }

            if (event.getDrops().isEmpty()) return;

            IOPStorage storage = EnergyUtils.getStorage(hostStack);
            ModuleEntity<?> optionalCollector = host.getEntitiesByType(ModuleTypes.ENDER_COLLECTION).findAny().orElse(null);
            if (optionalCollector instanceof EnderCollectionEntity collector) {
                List<ItemEntity> remove = new ArrayList<>();
                for (ItemEntity drop : event.getDrops()) {
                    ItemStack stack = drop.getItem();
                    int remainder = collector.insertStack(player, stack, storage);
                    if (remainder == 0) {
                        drop.setItem(ItemStack.EMPTY);
                        remove.add(drop);
                    } else {
                        stack.setCount(remainder);
                        drop.setItem(stack);
                    }
                }
                event.getDrops().removeAll(remove);
            }
        }
    }
}
