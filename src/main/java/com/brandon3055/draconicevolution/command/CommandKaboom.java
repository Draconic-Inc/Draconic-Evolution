package com.brandon3055.draconicevolution.command;

import com.brandon3055.brandonscore.api.TimeKeeper;
import com.brandon3055.brandonscore.handlers.ProcessHandler;
import com.brandon3055.draconicevolution.blocks.reactor.ProcessExplosion;
import com.brandon3055.draconicevolution.client.handler.ClientEventHandler;
import com.brandon3055.draconicevolution.network.DraconicNetwork;
import com.brandon3055.draconicevolution.utils.LogHelper;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.BlockPosArgument;
import net.minecraft.network.play.server.SUpdateLightPacket;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.server.ServerWorld;

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
                                                .then(Commands.literal("no_effect")
                                                        .executes(ctx -> calculate(ctx.getSource(), IntegerArgumentType.getInteger(ctx, "radius"), BlockPosArgument.getBlockPos(ctx, "position"), true, false))
                                                )
                                        )
                                        .then(Commands.literal("no_effect")
                                                .executes(ctx -> calculate(ctx.getSource(), IntegerArgumentType.getInteger(ctx, "radius"), BlockPosArgument.getBlockPos(ctx, "position"), false, false))
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
//                        .then(Commands.literal("relight").executes(ctx -> relight(ctx.getSource())))
        );
    }

    private static int calculate(CommandSource source, int radius, Vec3i pos, boolean prime, boolean effect) {
        if (explosionProcess != null && !explosionProcess.isDead()) {
            source.sendErrorMessage(new StringTextComponent("Explosion already in progress"));
            return 1;
        }

        LogHelper.dev("calculate Rad: " + radius + ", Pos: " + pos + ", Prime: " + prime);
        explosionProcess = new ProcessExplosion(new BlockPos(pos), radius, source.getWorld(), prime ? -1 : 0);
        explosionProcess.enableEffect = effect;
        explosionProcess.progressMon = progress -> {
            if (TimeKeeper.getServerTick() % 20 == 0) {
                source.sendFeedback(new StringTextComponent("Calculating: " + Math.round(progress * 100D) + "%"), true);
            }
        };
        ProcessHandler.addProcess(explosionProcess);
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
        if (explosionProcess != null && explosionProcess.isCalculationComplete()) {
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
