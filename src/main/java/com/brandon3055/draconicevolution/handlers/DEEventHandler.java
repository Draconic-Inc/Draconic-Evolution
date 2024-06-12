package com.brandon3055.draconicevolution.handlers;

import codechicken.lib.raytracer.RayTracer;
import com.brandon3055.draconicevolution.DEOldConfig;
import com.brandon3055.draconicevolution.api.energy.ICrystalBinder;
import com.brandon3055.draconicevolution.entity.GuardianCrystalEntity;
import com.brandon3055.draconicevolution.network.CrystalUpdateBatcher;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.item.ItemTossEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.WeakHashMap;

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
        ItemEntity item = event.getEntity();
        Player player = event.getPlayer();
        if (DEOldConfig.forceDroppedItemOwner && player != null && (item.getOwner() == null)) {
            item.setThrower(player.getUUID());
        }
    }

    //region Crystal Binder

    @SubscribeEvent
    public void rightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        if (event.isCanceled()) {
            return;
        }

        Player player = event.getEntity();
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

        if (BinderHandler.onBinderUse(event.getEntity(), event.getHand(), event.getLevel(), event.getPos(), stack, event.getFace())) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void rightClickItem(PlayerInteractEvent.RightClickItem event) {
        if (event.getLevel().isClientSide || event.isCanceled() || !event.getEntity().isShiftKeyDown() || !(event.getItemStack().getItem() instanceof ICrystalBinder)) {
            return;
        }

        BlockHitResult traceResult = RayTracer.retrace(event.getEntity());

        if (traceResult.getType() == HitResult.Type.BLOCK) {
            return;
        }

        if (BinderHandler.clearBinder(event.getEntity(), event.getItemStack())) {
            event.setCanceled(true);
        }
    }

    //endregion

    @SubscribeEvent
    public void entityInteract(PlayerInteractEvent.EntityInteract event) {
        if (event.getTarget() instanceof GuardianCrystalEntity) {
            event.setCanceled(true);
        }
    }
}
