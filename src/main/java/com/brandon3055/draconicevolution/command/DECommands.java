package com.brandon3055.draconicevolution.command;

import net.covers1624.quack.util.CrashLock;
import net.neoforged.neoforge.client.event.RegisterClientCommandsEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

/**
 * Created by brandon3055 on 15/11/2022
 */
public class DECommands {

    private static final CrashLock LOCK = new CrashLock("Already Initialized");

    public static void init() {
        LOCK.lock();

        NeoForge.EVENT_BUS.addListener(DECommands::registerServerCommands);
        NeoForge.EVENT_BUS.addListener(DECommands::registerClientCommands);
    }

    private static void registerServerCommands(RegisterCommandsEvent event) {
        CommandKaboom.register(event.getDispatcher());
//        CommandMakeRecipe.register(event.getDispatcher());
        CommandRespawnGuardian.register(event.getDispatcher());
    }

    private static void registerClientCommands(RegisterClientCommandsEvent event) {

    }
}
