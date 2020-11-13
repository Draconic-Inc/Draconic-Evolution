package com.brandon3055.draconicevolution.command;

import com.brandon3055.brandonscore.BrandonsCore;
import com.brandon3055.brandonscore.api.TimeKeeper;
import com.brandon3055.brandonscore.handlers.BCEventHandler;
import com.brandon3055.brandonscore.handlers.HandHelper;
import com.brandon3055.brandonscore.handlers.ProcessHandler;
import com.brandon3055.brandonscore.inventory.ContainerPlayerAccess;
import com.brandon3055.brandonscore.lib.ChatHelper;
import com.brandon3055.brandonscore.lib.Pair;
import com.brandon3055.brandonscore.network.BCoreNetwork;
import com.brandon3055.brandonscore.utils.DataUtils;
import com.brandon3055.brandonscore.utils.InventoryUtils;
import com.brandon3055.brandonscore.utils.LogHelperBC;
import com.brandon3055.draconicevolution.blocks.reactor.ProcessExplosion;
import com.brandon3055.draconicevolution.client.handler.ClientEventHandler;
import com.brandon3055.draconicevolution.network.DraconicNetwork;
import com.brandon3055.draconicevolution.utils.LogHelper;
import com.google.common.base.Charsets;
import com.google.common.collect.Lists;
import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.sun.istack.internal.Nullable;
import net.minecraft.block.Blocks;
import net.minecraft.command.CommandException;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.ISuggestionProvider;
import net.minecraft.command.arguments.BlockPosArgument;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.network.play.server.SChunkDataPacket;
import net.minecraft.network.play.server.SUpdateLightPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.PlayerProfileCache;
import net.minecraft.util.math.*;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.util.text.event.HoverEvent;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.server.ServerChunkProvider;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.Tags;
import net.minecraftforge.eventbus.EventBus;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventListener;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import org.apache.commons.io.IOUtils;

import java.io.*;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import static net.minecraft.util.text.event.HoverEvent.Action.SHOW_TEXT;

/**
 * Created by brandon3055 on 23/06/2017.
 */
public class CommandKaboom {

    private static ProcessExplosion explosionProcess = null;

    public static void register(CommandDispatcher<CommandSource> dispatcher) {
        dispatcher.register(
                Commands.literal("de_kaboom")
                        .requires(cs -> cs.hasPermissionLevel(3))
                        .then(Commands.argument("radius", IntegerArgumentType.integer(10, 50000))
//                                .executes(ctx -> calculate(ctx.getSource(), IntegerArgumentType.getInteger(ctx, "radius"), new BlockPos(ctx.getSource().getPos()), false))
                                .then(Commands.argument("position", BlockPosArgument.blockPos())
                                        .executes(ctx -> calculate(ctx.getSource(), IntegerArgumentType.getInteger(ctx, "radius"), BlockPosArgument.getBlockPos(ctx, "position"), false, true))
                                        .then(Commands.literal("effect_only")
                                                .executes(ctx -> effect(ctx.getSource(), IntegerArgumentType.getInteger(ctx, "radius"), BlockPosArgument.getBlockPos(ctx, "position"), false))
                                                .then(Commands.literal("flash")
                                                        .executes(ctx -> effect(ctx.getSource(), IntegerArgumentType.getInteger(ctx, "radius"), BlockPosArgument.getBlockPos(ctx, "position"), true))
                                                ))
                                        .then(Commands.literal("prime")
                                                .executes(ctx -> calculate(ctx.getSource(), IntegerArgumentType.getInteger(ctx, "radius"), BlockPosArgument.getBlockPos(ctx, "position"), true, true))
                                        )
                                        .then(Commands.literal("no_effect")
                                                .executes(ctx -> calculate(ctx.getSource(), IntegerArgumentType.getInteger(ctx, "radius"), BlockPosArgument.getBlockPos(ctx, "position"), true, false))
                                        )
                                )
//                                .then(Commands.literal("effect_only")
//                                        .executes(ctx -> effect(ctx.getSource(), IntegerArgumentType.getInteger(ctx, "radius"), new BlockPos(ctx.getSource().getPos()), false))
//                                        .then(Commands.literal("flash")
//                                                .executes(ctx -> effect(ctx.getSource(), IntegerArgumentType.getInteger(ctx, "radius"), new BlockPos(ctx.getSource().getPos()), true))
//                                        ))
//                                .then(Commands.literal("prime")
//                                        .executes(ctx -> calculate(ctx.getSource(), IntegerArgumentType.getInteger(ctx, "radius"), new BlockPos(ctx.getSource().getPos()), true))
//                                )
                        )
                        .then(Commands.literal("detonate").executes(ctx -> detonate()))
                        .then(Commands.literal("abort").executes(ctx -> abort()))
                        .then(Commands.literal("relight").executes(ctx -> relight(ctx.getSource())))
        );
    }

    private static int calculate(CommandSource source, int radius, Vec3i pos, boolean prime, boolean effect) {
        if (explosionProcess != null && !explosionProcess.isDead()) {
            source.sendErrorMessage(new StringTextComponent("Explosion already in progress"));
            return 1;
        }

        LogHelper.dev("calculate Rad: " + radius + ", Pos: " + pos + ", Prime: " + prime);
        ProcessExplosion explosion = new ProcessExplosion(new BlockPos(pos), radius, source.getWorld(), prime ? -1 : 0);
        explosion.enableEffect = effect;
        explosion.progressMon = progress -> {
            if (TimeKeeper.getServerTick() % 20 == 0) {
                source.sendFeedback(new StringTextComponent("Calculating: " + Math.round(progress * 100D) + "%"), true);
            }
        };
        ProcessHandler.addProcess(explosion);
        return 0;
    }

    private static int effect(CommandSource source, int radius, Vec3i pos, boolean flash) {
        if (flash) {
            ClientEventHandler.triggerExplosionEffect(new BlockPos(pos));
        } else {
            DraconicNetwork.sendExplosionEffect(source.getWorld().getDimension().getType(), new BlockPos(pos), radius, false);
        }
        return 0;
    }

    private static int detonate() {
        if (explosionProcess != null && !explosionProcess.isCalculationComplete()) {
            explosionProcess.detonate();
            explosionProcess = null;
            return 0;
        }
        return 1;
    }

    private static int abort() {
        if (explosionProcess != null) {
            explosionProcess.isDead = true;
            explosionProcess = null;
            return 0;
        }
        return 1;
    }

    private static int relight(CommandSource source) {
        BlockPos pos = new BlockPos(source.getPos());
        ServerWorld world = source.getWorld();
        while (world.isAirBlock(pos)) pos = pos.down();

//        world.setBlockState(pos, Blocks.GLASS.getDefaultState());

        BlockPos.getAllInBox(pos.add(-10, -10, -10), pos.add(10, 10, 10)).forEach(e -> {
            world.getLightManager().checkBlock(e);
            Chunk chunk = world.getChunkAt(e);
            SUpdateLightPacket packet = new SUpdateLightPacket(chunk.getPos(), world.getLightManager());
            world.getChunkProvider().chunkManager.getTrackingPlayers(chunk.getPos(), false).forEach(f -> f.connection.sendPacket(packet));
//            world.getLightManager().func_215567_a(e, true);
        });

        return 0;
    }
}
