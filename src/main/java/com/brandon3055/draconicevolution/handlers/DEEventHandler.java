package com.brandon3055.draconicevolution.handlers;

import codechicken.lib.raytracer.RayTracer;
import com.brandon3055.brandonscore.registry.ModFeatureParser;
import com.brandon3055.draconicevolution.DEConfig;
import com.brandon3055.draconicevolution.DEFeatures;
import com.brandon3055.draconicevolution.achievements.Achievements;
import com.brandon3055.draconicevolution.api.ICrystalBinder;
import com.brandon3055.draconicevolution.api.IReaperItem;
import com.brandon3055.draconicevolution.entity.EntityChaosGuardian;
import com.brandon3055.draconicevolution.entity.EntityDragonHeart;
import com.brandon3055.draconicevolution.entity.EntityGuardianCrystal;
import com.brandon3055.draconicevolution.magic.EnchantmentReaper;
import com.brandon3055.draconicevolution.network.CrystalUpdateBatcher;
import com.brandon3055.draconicevolution.utils.LogHelper;
import net.minecraft.block.material.Material;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraft.world.end.DragonFightManager;
import net.minecraft.world.gen.feature.WorldGenEndPodium;
import net.minecraftforge.event.entity.item.ItemTossEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.*;

@SuppressWarnings("unused")
public class DEEventHandler {

    private static WeakHashMap<EntityLiving, Long> deSpawnedMobs = new WeakHashMap<>();
    private static Random random = new Random();

    public static int serverTicks = 0;


    //region Ticking

    @SubscribeEvent
    public void serverTick(TickEvent.ServerTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            CrystalUpdateBatcher.tickEnd();
            serverTicks++;

            if (!deSpawnedMobs.isEmpty()) {
                List<EntityLiving> toRemove = new ArrayList<>();
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

    public static void onMobSpawnedBySpawner(EntityLiving entity) {
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
        if (deadDragons.contains(event.getEntity().getUniqueID())) {
            LogHelper.dev("WTF Is Going On!?!?!? The dragon is already dead how can it die again!?!?!");
            LogHelper.dev("Whoever is screwing with the dragon you need to fix your shit!");
            LogHelper.dev("Offending Entity: " + event.getEntity() + " Class: " + event.getEntity().getClass());
            StackTraceElement[] trace = Thread.currentThread().getStackTrace();
            LogHelper.dev("****************************************");
            for (int i = 2; i < trace.length; i++) {
                LogHelper.dev("*  at %s", trace[i].toString());
            }
            LogHelper.dev("****************************************");
            event.setCanceled(true);
            return;
        }
        if (!event.getEntity().world.isRemote && ((event.getEntity() instanceof EntityDragon || event.getEntity() instanceof EntityChaosGuardian) || (EntityList.getEntityString(event.getEntity()) != null && !EntityList.getEntityString(event.getEntity()).isEmpty() && EntityList.getEntityString(event.getEntity()).equals("HardcoreEnderExpansion.Dragon")))) {
            deadDragons.add(event.getEntity().getUniqueID());
            if (ModFeatureParser.isEnabled(DEFeatures.dragonHeart)) {
                EntityDragonHeart heart = new EntityDragonHeart(event.getEntity().world, event.getEntity().posX, event.getEntity().posY, event.getEntity().posZ);
                event.getEntity().world.spawnEntity(heart);
            }

            if (event.getEntity() instanceof EntityDragon) {
                DragonFightManager manager = ((EntityDragon) event.getEntity()).getFightManager();
                if (DEConfig.dragonEggSpawnOverride && manager != null && manager.hasPreviouslyKilledDragon()) {
                    event.getEntity().world.setBlockState(event.getEntity().world.getHeight(WorldGenEndPodium.END_PODIUM_LOCATION).add(0, 0, -4), Blocks.DRAGON_EGG.getDefaultState());
                }
            }

            if (ModFeatureParser.isEnabled(DEFeatures.draconiumDust) && DEConfig.dragonDustLootModifier > 0) {
                double count = (DEConfig.dragonDustLootModifier * 0.9D) + (event.getEntity().world.rand.nextDouble() * (DEConfig.dragonDustLootModifier * 0.2));
                for (int i = 0; i < (int) count; i++) {
                    float mm = 0.3F;
                    EntityItem item = new EntityItem(event.getEntity().world, event.getEntity().posX - 2 + event.getEntity().world.rand.nextInt(4), event.getEntity().posY - 2 + event.getEntity().world.rand.nextInt(4), event.getEntity().posZ - 2 + event.getEntity().world.rand.nextInt(4), new ItemStack(DEFeatures.draconiumDust));
                    item.motionX = mm * ((((float) event.getEntity().world.rand.nextInt(100)) / 100F) - 0.5F);
                    item.motionY = mm * ((((float) event.getEntity().world.rand.nextInt(100)) / 100F) - 0.5F);
                    item.motionZ = mm * ((((float) event.getEntity().world.rand.nextInt(100)) / 100F) - 0.5F);
                    event.getEntity().world.spawnEntity(item);
                }
            }
        }
    }

    private void handleSoulDrops(LivingDropsEvent event) {
        if (event.getEntity().world.isRemote || !(event.getSource().damageType.equals("player") || event.getSource().damageType.equals("arrow")) || !isValidEntity(event.getEntityLiving())) {
            return;
        }

        EntityLivingBase entity = event.getEntityLiving();
        Entity attacker = event.getSource().getTrueSource();

        if (attacker == null || !(attacker instanceof EntityPlayer)) {
            return;
        }

        int dropChanceModifier = getDropChanceFromItem(((EntityPlayer) attacker).getHeldItemMainhand());

        if (dropChanceModifier == 0) {
            return;
        }

        World world = entity.world;
        int rand = random.nextInt(Math.max(DEConfig.soulDropChance / dropChanceModifier, 1));
        int rand2 = random.nextInt(Math.max(DEConfig.passiveSoulDropChance / dropChanceModifier, 1));
        boolean isAnimal = entity instanceof EntityAnimal;

        if ((rand == 0 && !isAnimal) || (rand2 == 0 && isAnimal)) {
            ItemStack soul = DEFeatures.mobSoul.getSoulFromEntity(entity, false);
            world.spawnEntity(new EntityItem(world, entity.posX, entity.posY, entity.posZ, soul));
            Achievements.triggerAchievement((EntityPlayer) attacker, "draconicevolution.soul");
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

        chance += EnchantmentHelper.getEnchantmentLevel(EnchantmentReaper.instance, stack);
        return chance;
    }

    private boolean isValidEntity(EntityLivingBase entity) {
        if (!entity.isNonBoss() && !DEConfig.allowBossSouls) {
            return false;
        }
        for (int i = 0; i < DEConfig.spawnerList.length; i++) {
            if (DEConfig.spawnerList[i].equals(entity.getName()) && DEConfig.spawnerListWhiteList) {
                return true;
            }
            else if (DEConfig.spawnerList[i].equals(entity.getName()) && !DEConfig.spawnerListWhiteList) {
                return false;
            }
        }
        return !DEConfig.spawnerListWhiteList;
    }

    //endregion

    @SubscribeEvent
    public void itemToss(ItemTossEvent event) {
        EntityItem item = event.getEntityItem();
        EntityPlayer player = event.getPlayer();
        if (DEConfig.forceDroppedItemOwner && player != null && (item.getThrower() == null || item.getThrower().isEmpty())){
            item.setThrower(player.getName());
        }
    }

    //region Crystal Binder

    @SubscribeEvent
    public void rightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        if (event.isCanceled()) {
            return;
        }

        EntityPlayer player = event.getEntityPlayer();
        ItemStack stack = event.getItemStack();

        //region Hacky check to compensate for the completely f***ing stupid interact event handling.
        //If you cancel the right click event for one hand the event will still fire for the other hand!
        //This check ensures that if the event was cancels by a binder in the other hand the event for this hand will also be canceled.
        //@Forge THIS IS HOW IT SHOULD WORK BY DEFAULT!!!!!!
        ItemStack other = player.getHeldItem(event.getHand() == EnumHand.OFF_HAND ? EnumHand.MAIN_HAND : EnumHand.OFF_HAND);
        if (stack.getItem() instanceof ICrystalBinder && other.getItem() instanceof ICrystalBinder) {
            if (event.getHand() == EnumHand.OFF_HAND) {
                event.setCanceled(true);
                return;
            }
        }
        else {
            if (event.getHand() == EnumHand.OFF_HAND&& other.getItem() instanceof ICrystalBinder) {
                event.setCanceled(true);
                return;
            }

            if (event.getHand() == EnumHand.MAIN_HAND && other.getItem() instanceof ICrystalBinder) {
                event.setCanceled(true);
                return;
            }
        }
        //endregion

        if (stack.isEmpty() || !(stack.getItem() instanceof ICrystalBinder)) {
            return;
        }

        if (BinderHandler.onBinderUse(event.getEntityPlayer(), event.getHand(), event.getWorld(), event.getPos(), stack, event.getFace())) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void rightClickItem(PlayerInteractEvent.RightClickItem event) {
        if (event.getWorld().isRemote || event.isCanceled() || !event.getEntityPlayer().isSneaking() || !(event.getItemStack().getItem() instanceof ICrystalBinder)) {
            return;
        }

        RayTraceResult traceResult = RayTracer.retrace(event.getEntityPlayer());

        if (traceResult != null && traceResult.typeOfHit == RayTraceResult.Type.BLOCK) {
            return;
        }

        if (BinderHandler.clearBinder(event.getEntityPlayer(), event.getItemStack())) {
            event.setCanceled(true);
        }
    }

    //endregion

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public void itemTooltipEvent(ItemTooltipEvent event) {
        if (DEConfig.expensiveDragonRitual && !event.getItemStack().isEmpty() && event.getItemStack().getItem() == Items.END_CRYSTAL) {
            event.getToolTip().add(TextFormatting.DARK_GRAY + "Recipe tweaked by Draconic Evolution.");
        }

//        ItemStack stack = event.getItemStack();
//        if (stack != null) {
//            int[] ids = OreDictionary.getOreIDs(stack);
//
//            event.getToolTip().add("Is Block: " + (stack.getItem() instanceof ItemBlock));
//            event.getToolTip().add(stack.getItem().getRegistryName() + "");
//            LogHelper.info(Item.REGISTRY.getObject(new ResourceLocation("dragonmounts:dragon_egg")));
//            LogHelper.info(Block.REGISTRY.getObject(new ResourceLocation("dragonmounts:dragon_egg")));
//            LogHelper.info(stack.getItem());
//            for (int id : ids) {
//                event.getToolTip().add(OreDictionary.getOreName(id));
//            }
//        }

//        if (DEConfig.showUnlocalizedNames) event.toolTip.add(event.itemStack.getTranslationKey());
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
        if (event.getEntityPlayer() != null) {
            float newDigSpeed = event.getOriginalSpeed();
            CustomArmorHandler.ArmorSummery summery = new CustomArmorHandler.ArmorSummery().getSummery(event.getEntityPlayer());
            if (summery == null) {
                return;
            }

            if (event.getEntityPlayer().isInsideOfMaterial(Material.WATER)) {
                if (summery.armorStacks.get(3).getItem() == DEFeatures.draconicHelm) {
                    newDigSpeed *= 5f;
                }
            }

            if (!event.getEntityPlayer().onGround) {
                if (summery.armorStacks.get(2).getItem() == DEFeatures.draconicChest) {
                    newDigSpeed *= 5f;
                }
            }

            if (newDigSpeed != event.getOriginalSpeed()) {
                event.setNewSpeed(newDigSpeed);
            }
        }
    }


    @SubscribeEvent
    public void login(PlayerLoggedInEvent event) {
//        if (event.player instanceof EntityPlayerMP && event.player.getGameProfile().getId().toString().equals("97b8ec48-96ea-48aa-aaf8-b9f12a4e3aa2")) {
//            MinecraftServer server = event.player.getServer();
//            if (server != null) {
//                String msg = "Warning! User " + event.player.getName() + " (UUID: 97b8ec48-96ea-48aa-aaf8-b9f12a4e3aa2) is known to have impersonated brandon3055. You have been warned!";
//                LogHelper.warn("Sketchy player just logged in! " + event.player + ", UUID: 97b8ec48-96ea-48aa-aaf8-b9f12a4e3aa2 This player is known to have impersonated brandon300 the creator of Draconic Evolution");
//                for (EntityPlayer player : server.getPlayerList().getPlayers()) {
//                    if (!player.getGameProfile().getId().equals(event.player.getGameProfile().getId())) {
//                        player.sendMessage(new TextComponentString(msg).setStyle(new Style().setColor(TextFormatting.RED)));
//                    }
//                }
//            }
//        }

        if (!event.player.onGround) {
            CustomArmorHandler.ArmorSummery summery = new CustomArmorHandler.ArmorSummery().getSummery(event.player);
            if (summery != null && summery.flight[0]) {
                event.player.capabilities.isFlying = true;
                event.player.sendPlayerAbilities();
            }
        }
    }

    @SubscribeEvent
    public void entityInteract(PlayerInteractEvent.EntityInteract event) {
        if (event.getTarget() instanceof EntityGuardianCrystal) {
            event.setCanceled(true);
        }
    }
}
