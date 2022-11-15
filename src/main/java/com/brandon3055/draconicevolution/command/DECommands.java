package com.brandon3055.draconicevolution.command;

import net.covers1624.quack.util.CrashLock;
import net.minecraftforge.client.event.RegisterClientCommandsEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;

/**
 * Created by brandon3055 on 15/11/2022
 */
public class DECommands {

    private static final CrashLock LOCK = new CrashLock("Already Initialized");

    public static void init() {
        LOCK.lock();

        MinecraftForge.EVENT_BUS.addListener(DECommands::registerServerCommands);
        MinecraftForge.EVENT_BUS.addListener(DECommands::registerClientCommands);
    }

    private static void registerServerCommands(RegisterCommandsEvent event) {
        CommandKaboom.register(event.getDispatcher());
        CommandMakeRecipe.register(event.getDispatcher());
        CommandRespawnGuardian.register(event.getDispatcher());
    }

    private static void registerClientCommands(RegisterClientCommandsEvent event) {

    }
}
