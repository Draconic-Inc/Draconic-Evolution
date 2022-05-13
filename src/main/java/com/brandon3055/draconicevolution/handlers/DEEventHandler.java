package com.brandon3055.draconicevolution.handlers;

import codechicken.lib.raytracer.RayTracer;
import com.brandon3055.draconicevolution.DEConfig;
import com.brandon3055.draconicevolution.DEOldConfig;
import com.brandon3055.draconicevolution.achievements.Achievements;
import com.brandon3055.draconicevolution.api.IReaperItem;
import com.brandon3055.draconicevolution.api.energy.ICrystalBinder;
import com.brandon3055.draconicevolution.entity.GuardianCrystalEntity;
import com.brandon3055.draconicevolution.entity.guardian.DraconicGuardianEntity;
import com.brandon3055.draconicevolution.init.DEContent;
import com.brandon3055.draconicevolution.network.CrystalUpdateBatcher;
import com.brandon3055.draconicevolution.utils.LogHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.dimension.end.EndDragonFight;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.EndPodiumFeature;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ClientPlayerChangeGameTypeEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.item.ItemTossEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.*;

@SuppressWarnings("unused")
public class DEEventHandler {

    private static WeakHashMap<Mob, Long> deSpawnedMobs = new WeakHashMap<>();
    private static Random random = new Random();
    public static int serverTicks = 0;


    //region Ticking

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public void changeGameMode(ClientPlayerChangeGameTypeEvent event) {
        Player player = Minecraft.getInstance().player;
        if (DEConfig.creativeWarning && event.getNewGameType() == GameType.CREATIVE && player != null && !Minecraft.getInstance().isLocalServer() && player.getGameProfile().equals(event.getInfo().getProfile())) {
            player.sendMessage(new TextComponent("[Draconic Evolution]: ").withStyle(ChatFormatting.YELLOW).append(new TextComponent("Warning! Using creative inventory on a server will delete all module data on DE tools and armor. This is due a fundamental issue with the creative menu.").withStyle(ChatFormatting.RED)), Util.NIL_UUID);
        }
    }

    @SubscribeEvent
    public void serverTick(TickEvent.ServerTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            CrystalUpdateBatcher.tickEnd();
            serverTicks++;

            if (!deSpawnedMobs.isEmpty()) {
                List<LivingEntity> toRemove = new ArrayList<>();
                long time = System.currentTimeMillis();

                deSpawnedMobs.forEach((entity, aLong) -> {
                    if (time - aLong > 30000) {
                        entity.persistenceRequired = false;
                        toRemove.add(entity);
                    }
                });

                toRemove.forEach(entity -> deSpawnedMobs.remove(entity));
            }
        }
    }

    public static void onMobSpawnedBySpawner(Mob entity) {
        deSpawnedMobs.put(entity, System.currentTimeMillis());
    }

    //endregion

    //region Mob Drops

    @SubscribeEvent
    public void onDropEvent(LivingDropsEvent event) {
        handleDragonDrops(event);
        handleSoulDrops(event);
    }

    List<UUID> deadDragons = new LinkedList<>();

    private void handleDragonDrops(LivingDropsEvent event) {
        Entity entity = event.getEntity();
        if (deadDragons.contains(entity.getUUID())) {
            LogHelper.dev("WTF Is Going On!?!?!? The dragon is already dead how can it die again!?!?!");
            LogHelper.dev("Whoever is screwing with the dragon you need to fix your shit!");
            LogHelper.dev("Offending Entity: " + entity + " Class: " + entity.getClass());
            StackTraceElement[] trace = Thread.currentThread().getStackTrace();
            LogHelper.dev("****************************************");
            for (int i = 2; i < trace.length; i++) {
                LogHelper.dev("*  at %s", trace[i].toString());
            }
            LogHelper.dev("****************************************");
            event.setCanceled(true);
            return;
        }
        if (!entity.level.isClientSide && (entity instanceof EnderDragon || entity instanceof DraconicGuardianEntity)) {
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

    private void handleSoulDrops(LivingDropsEvent event) {
        if (event.getEntity().level.isClientSide || !(event.getSource().msgId.equals("player") || event.getSource().msgId.equals("arrow")) || !isValidEntity(event.getEntityLiving())) {
            return;
        }

        LivingEntity entity = event.getEntityLiving();
        Entity attacker = event.getSource().getEntity();

        if (attacker == null || !(attacker instanceof Player) || entity instanceof Player) {
            return;
        }

        int dropChanceModifier = getDropChanceFromItem(((Player) attacker).getMainHandItem());

        if (dropChanceModifier == 0) {
            return;
        }

        Level world = entity.level;
        int rand = random.nextInt(Math.max(DEConfig.soulDropChance / dropChanceModifier, 1));
        int rand2 = random.nextInt(Math.max(DEConfig.passiveSoulDropChance / dropChanceModifier, 1));
        boolean isAnimal = entity instanceof Animal;

        if ((rand == 0 && !isAnimal) || (rand2 == 0 && isAnimal)) {
            ItemStack soul = DEContent.mob_soul.getSoulFromEntity(entity, false);
            world.addFreshEntity(new ItemEntity(world, entity.getX(), entity.getY(), entity.getZ(), soul));
            Achievements.triggerAchievement((Player) attacker, "draconicevolution.soul");
        }
    }

    private int getDropChanceFromItem(ItemStack stack) {
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

    private boolean isValidEntity(LivingEntity entity) {
        if (!entity.canChangeDimensions() && !DEConfig.allowBossSouls) {
            return false;
        }
        for (int i = 0; i < DEConfig.spawnerList.length; i++) {
            if (DEConfig.spawnerList[i].equals(entity.getName()) && DEConfig.spawnerListWhiteList) {
                return true;
            } else if (DEConfig.spawnerList[i].equals(entity.getName()) && !DEConfig.spawnerListWhiteList) {
                return false;
            }
        }
        return !DEConfig.spawnerListWhiteList;
    }

    //endregion

    @SubscribeEvent
    public void itemToss(ItemTossEvent event) {
        ItemEntity item = event.getEntityItem();
        Player player = event.getPlayer();
        if (DEOldConfig.forceDroppedItemOwner && player != null && (item.getThrower() == null)) {
            item.setThrower(player.getUUID());
        }
    }

    //region Crystal Binder

    @SubscribeEvent
    public void rightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        if (event.isCanceled()) {
            return;
        }

        Player player = event.getPlayer();
        ItemStack stack = event.getItemStack();

        //region Hacky check to compensate for the completely f***ing stupid interact event handling.
        //If you cancel the right click event for one hand the event will still fire for the other hand!
        //This check ensures that if the event was cancels by a binder in the other hand the event for this hand will also be canceled.
        //@Forge THIS IS HOW IT SHOULD WORK BY DEFAULT!!!!!!
        ItemStack other = player.getItemInHand(event.getHand() == InteractionHand.OFF_HAND ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND);
        if (stack.getItem() instanceof ICrystalBinder && other.getItem() instanceof ICrystalBinder) {
            if (event.getHand() == InteractionHand.OFF_HAND) {
                event.setCanceled(true);
                return;
            }
        } else {
            if (event.getHand() == InteractionHand.OFF_HAND && other.getItem() instanceof ICrystalBinder) {
                event.setCanceled(true);
                return;
            }

            if (event.getHand() == InteractionHand.MAIN_HAND && other.getItem() instanceof ICrystalBinder) {
                event.setCanceled(true);
                return;
            }
        }
        //endregion

        if (stack.isEmpty() || !(stack.getItem() instanceof ICrystalBinder)) {
            return;
        }

        if (BinderHandler.onBinderUse(event.getPlayer(), event.getHand(), event.getWorld(), event.getPos(), stack, event.getFace())) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void rightClickItem(PlayerInteractEvent.RightClickItem event) {
        if (event.getWorld().isClientSide || event.isCanceled() || !event.getPlayer().isShiftKeyDown() || !(event.getItemStack().getItem() instanceof ICrystalBinder)) {
            return;
        }

        BlockHitResult traceResult = RayTracer.retrace(event.getPlayer());

        if (traceResult.getType() == HitResult.Type.BLOCK) {
            return;
        }

        if (BinderHandler.clearBinder(event.getPlayer(), event.getItemStack())) {
            event.setCanceled(true);
        }
    }

    //endregion

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public void itemTooltipEvent(ItemTooltipEvent event) {
//        if (DEOldConfig.expensiveDragonRitual && !event.getItemStack().isEmpty() && event.getItemStack().getItem() == Items.END_CRYSTAL) {
//            event.getToolTip().add(new StringTextComponent(TextFormatting.DARK_GRAY + "Recipe tweaked by Draconic Evolution."));
//        }

//        ItemStack stack = event.getItemStack();
//        if (stack != null) {
//            int[] ids = OreDictionary.getOreIDs(stack);
//
//            event.getToolTip().add("Is Block: " + (stack.getItem() instanceof BlockItem));
//            event.getToolTip().add(stack.getItem().getRegistryName() + "");
//            LogHelper.info(Item.REGISTRY.getObject(new ResourceLocation("dragonmounts:dragon_egg")));
//            LogHelper.info(Block.REGISTRY.getObject(new ResourceLocation("dragonmounts:dragon_egg")));
//            LogHelper.info(stack.getItem());
//            for (int id : ids) {
//                event.getToolTip().add(OreDictionary.getOreName(id));
//            }
//        }

//        if (DEConfig.showUnlocalizedNames) event.toolTip.add(event.itemStack.getUnlocalizedName());
//        if (DraconicEvolution.debug && event.itemStack.hasTagCompound()) {
//            String s = event.itemStack.getTagCompound().toString();
//            int escape = 0;
//            while (s.contains(",")) {
//                event.toolTip.add(s.substring(0, s.indexOf(",") + 1));
//                s = s.substring(s.indexOf(",") + 1, s.length());
//
//                if (escape++ >= 100) break;
//            }
//            event.toolTip.add(s);
//        }
    }


    @SubscribeEvent(priority = EventPriority.LOW)
    public void getBreakSpeed(PlayerEvent.BreakSpeed event) {
        if (event.getPlayer() != null) {
            float newDigSpeed = event.getOriginalSpeed();
//            ModularArmorEventHandler.ArmorSummery summery = new ModularArmorEventHandler.ArmorSummery().getSummery(event.getPlayer());
//            if (summery == null) {
//                return;
//            }

//            if (event.getPlayer().world.isMaterialInBB(event.getPlayer().getBoundingBox(), Material.WATER)) {
//                if (summery.armorStacks.get(3).getItem() == DEContent.draconicHelm) {
//                    newDigSpeed *= 5f;
//                }
//            }
//
//            if (!event.getEntityPlayer().onGround) {
//                if (summery.armorStacks.get(2).getItem() == DEContent.draconicChest) {
//                    newDigSpeed *= 5f;
//                }
//            }

            if (newDigSpeed != event.getOriginalSpeed()) {
                event.setNewSpeed(newDigSpeed);
            }
        }
    }


    @SubscribeEvent
    public void login(PlayerEvent.PlayerLoggedInEvent event) {
//        if (event.player instanceof ServerPlayerEntity && event.player.getGameProfile().getId().toString().equals("97b8ec48-96ea-48aa-aaf8-b9f12a4e3aa2")) {
//            MinecraftServer server = event.player.getServer();
//            if (server != null) {
//                String msg = "Warning! User " + event.player.getName() + " (UUID: 97b8ec48-96ea-48aa-aaf8-b9f12a4e3aa2) is known to have impersonated brandon3055. You have been warned!";
//                LogHelper.warn("Sketchy player just logged in! " + event.player + ", UUID: 97b8ec48-96ea-48aa-aaf8-b9f12a4e3aa2 This player is known to have impersonated brandon300 the creator of Draconic Evolution");
//                for (PlayerEntity player : server.getPlayerList().getPlayers()) {
//                    if (!player.getGameProfile().getId().equals(event.player.getGameProfile().getId())) {
//                        player.sendMessage(new StringTextComponent(msg).setStyle(new Style().setColor(TextFormatting.RED)));
//                    }
//                }
//            }
//        }

        if (!event.getPlayer().isOnGround()) {
//            ModularArmorEventHandler.ArmorSummery summery = new ModularArmorEventHandler.ArmorSummery().getSummery(event.getPlayer());
//            if (summery != null && summery.flight[0]) {
//                event.getPlayer().abilities.isFlying = true;
//                event.getPlayer().sendPlayerAbilities();
//            }
        }
    }

    @SubscribeEvent
    public void entityInteract(PlayerInteractEvent.EntityInteract event) {
        if (event.getTarget() instanceof GuardianCrystalEntity) {
            event.setCanceled(true);
        }
    }
}
