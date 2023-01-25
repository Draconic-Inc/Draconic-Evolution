package com.brandon3055.draconicevolution.handlers;

import codechicken.lib.raytracer.RayTracer;
import com.brandon3055.brandonscore.api.TechLevel;
import com.brandon3055.brandonscore.api.event.TileBCoreInitEvent;
import com.brandon3055.brandonscore.capability.CapabilityProviderSerializable;
import com.brandon3055.brandonscore.lib.DelayedTask;
import com.brandon3055.draconicevolution.DEConfig;
import com.brandon3055.draconicevolution.DEOldConfig;
import com.brandon3055.draconicevolution.DraconicEvolution;
import com.brandon3055.draconicevolution.achievements.Achievements;
import com.brandon3055.draconicevolution.api.IReaperItem;
import com.brandon3055.draconicevolution.api.capability.DECapabilities;
import com.brandon3055.draconicevolution.api.energy.ICrystalBinder;
import com.brandon3055.draconicevolution.api.modules.ModuleCategory;
import com.brandon3055.draconicevolution.api.modules.lib.SimpleModuleHost;
import com.brandon3055.draconicevolution.blocks.tileentity.TileGenerator;
import com.brandon3055.draconicevolution.entity.GuardianCrystalEntity;
import com.brandon3055.draconicevolution.entity.guardian.DraconicGuardianEntity;
import com.brandon3055.draconicevolution.init.DEContent;
import com.brandon3055.draconicevolution.init.ModuleCfg;
import com.brandon3055.draconicevolution.network.CrystalUpdateBatcher;
import com.brandon3055.draconicevolution.utils.LogHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
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
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.dimension.end.EndDragonFight;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.EndPodiumFeature;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ClientPlayerChangeGameTypeEvent;
import net.minecraftforge.event.AttachCapabilitiesEvent;
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

//    @SubscribeEvent //Example: Attach module host capability via event.
//    public void attachCaps(TileBCoreInitEvent event) {
//        if (event.getTile() instanceof TileGenerator tile) {
//            SimpleModuleHost host = new SimpleModuleHost(TechLevel.WYVERN, 5, 5, ModuleCfg.removeInvalidModules, ModuleCategory.ENERGY);
//            tile.getCapManager().setManaged(DraconicEvolution.MODID +":host_cap", DECapabilities.MODULE_HOST_CAPABILITY, host).saveBoth().syncContainer();
//        }
//    }

    //region Ticking

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
