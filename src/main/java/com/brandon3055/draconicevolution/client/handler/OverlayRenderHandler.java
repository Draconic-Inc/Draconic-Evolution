package com.brandon3055.draconicevolution.client.handler;

import com.brandon3055.draconicevolution.api.energy.ICrystalBinder;
import com.brandon3055.draconicevolution.handlers.BinderHandler;
import net.covers1624.quack.util.CrashLock;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.client.event.RenderLevelLastEvent;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.common.MinecraftForge;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Created by brandon3055 on 07/02/2023
 */
public class OverlayRenderHandler {
    public static final Logger LOGGER = LogManager.getLogger();
    private static final CrashLock LOCK = new CrashLock("Already Initialized");

    public static void init() {
        LOCK.lock();
        MinecraftForge.EVENT_BUS.addListener(OverlayRenderHandler::renderLevelStage);
    }

    public static void renderLevelStage(RenderLevelLastEvent event) {
        if (event.isCanceled()) {
            return;
        }
        ClientEventHandler.MODELVIEW.set(event.getPoseStack().last().pose());
        ClientEventHandler.PROJECTION.set(event.getProjectionMatrix());

        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null) return;
        Level world = player.getCommandSenderWorld();
        ItemStack stack = player.getMainHandItem();
        ItemStack offStack = player.getOffhandItem();
        Minecraft mc = Minecraft.getInstance();
        float partialTicks = event.getPartialTick();

        try {
            if (!stack.isEmpty() && stack.getItem() instanceof ICrystalBinder) {
                BinderHandler.renderWorldOverlay(player, event.getPoseStack(), world, stack, mc, partialTicks);
            } else if (!stack.isEmpty() && offStack.getItem() instanceof ICrystalBinder) {
                BinderHandler.renderWorldOverlay(player, event.getPoseStack(), world, offStack, mc, partialTicks);
            }
        } catch (Throwable e) {
            LOGGER.error("An error occurred while rendering crystal binder overlay", e);
        }
    }

}
