package com.brandon3055.draconicevolution.handlers;

import codechicken.lib.raytracer.RayTracer;
import com.brandon3055.draconicevolution.DEConfig;
import com.brandon3055.draconicevolution.api.energy.ICrystalBinder;
import com.brandon3055.draconicevolution.entity.GuardianCrystalEntity;
import com.brandon3055.draconicevolution.init.DEContent;
import com.brandon3055.draconicevolution.network.CrystalUpdateBatcher;
import net.covers1624.quack.util.CrashLock;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.AddPackFindersEvent;
import net.neoforged.neoforge.event.AnvilUpdateEvent;
import net.neoforged.neoforge.event.TickEvent;
import net.neoforged.neoforge.event.entity.item.ItemTossEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.WeakHashMap;

@SuppressWarnings ("unused")
public class DEEventHandler {
    private static final CrashLock LOCK = new CrashLock("Already Initialized");
    private static WeakHashMap<Mob, Long> deSpawnedMobs = new WeakHashMap<>();
    private static Random random = new Random();
    public static int serverTicks = 0;


    public static void init(IEventBus modBus) {
        LOCK.lock();

        NeoForge.EVENT_BUS.addListener(DEEventHandler::serverTick);
        NeoForge.EVENT_BUS.addListener(DEEventHandler::itemToss);
        NeoForge.EVENT_BUS.addListener(DEEventHandler::rightClickBlock);
        NeoForge.EVENT_BUS.addListener(DEEventHandler::rightClickItem);
        NeoForge.EVENT_BUS.addListener(DEEventHandler::entityInteract);
        NeoForge.EVENT_BUS.addListener(DEEventHandler::onAnvilUpdate);
    }


//Example: Attach module host capability via event.
//    public void attachCaps(TileBCoreInitEvent event) {
//        if (event.getTile() instanceof TileGenerator tile) {
//            SimpleModuleHost host = new SimpleModuleHost(TechLevel.WYVERN, 5, 5, ModuleCfg.removeInvalidModules, ModuleCategory.ENERGY);
//            tile.getCapManager().setManaged(DraconicEvolution.MODID +":host_cap", DECapabilities.Host.ITEM, host).saveBoth().syncContainer();
//        }
//    }

    public static void serverTick(TickEvent.ServerTickEvent event) {
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

    public static void itemToss(ItemTossEvent event) {
        ItemEntity item = event.getEntity();
        Player player = event.getPlayer();
        if (DEConfig.forceDroppedItemOwner && player != null && (item.getOwner() == null)) {
            item.setThrower(player);
        }
    }

    public static void rightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        if (event.isCanceled()) {
            return;
        }

        Player player = event.getEntity();
        ItemStack stack = event.getItemStack();

        //Hacky check to compensate for the completely f***ing stupid interact event handling.
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

        if (stack.isEmpty() || !(stack.getItem() instanceof ICrystalBinder)) {
            return;
        }

        if (BinderHandler.onBinderUse(event.getEntity(), event.getHand(), event.getLevel(), event.getPos(), stack, event.getFace())) {
            event.setCanceled(true);
        }
    }

    public static void rightClickItem(PlayerInteractEvent.RightClickItem event) {
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

    public static void entityInteract(PlayerInteractEvent.EntityInteract event) {
        if (event.getTarget() instanceof GuardianCrystalEntity) {
            event.setCanceled(true);
        }
    }

    public static void onAnvilUpdate(AnvilUpdateEvent event) {
        if (event.getLeft().getItem() == DEContent.DISLOCATOR.get() && event.getRight().getItem() == DEContent.INGOT_DRACONIUM.get() && event.getLeft().getDamageValue() > 0) {
            event.setOutput(event.getLeft().copy());
            event.getOutput().setDamageValue(0);
            event.setCost(1);
            event.setMaterialCost(1);
        }
    }
}
