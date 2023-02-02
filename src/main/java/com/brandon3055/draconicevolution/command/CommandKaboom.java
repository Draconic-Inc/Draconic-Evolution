package com.brandon3055.draconicevolution.command;

import com.brandon3055.brandonscore.api.TimeKeeper;
import com.brandon3055.brandonscore.handlers.ProcessHandler;
import com.brandon3055.brandonscore.worldentity.WorldEntity;
import com.brandon3055.brandonscore.worldentity.WorldEntityHandler;
import com.brandon3055.draconicevolution.blocks.reactor.ProcessExplosion;
import com.brandon3055.draconicevolution.client.handler.ClientEventHandler;
import com.brandon3055.draconicevolution.network.DraconicNetwork;
import com.brandon3055.draconicevolution.utils.LogHelper;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.chunk.LevelChunk;

/**
 * Created by brandon3055 on 23/06/2017.
 */
public class CommandKaboom {

    private static ProcessExplosion explosionProcess = null;

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
                Commands.literal("de_kaboom")
                        .requires(cs -> cs.hasPermission(2))
                        .then(Commands.argument("radius", IntegerArgumentType.integer(10, 50000))
//                                .executes(ctx -> calculate(ctx.getSource(), IntegerArgumentType.getInteger(ctx, "radius"), new BlockPos(ctx.getSource().getPos()), false))
                                        .then(Commands.argument("position", BlockPosArgument.blockPos())
                                                .executes(ctx -> calculate(ctx.getSource(), IntegerArgumentType.getInteger(ctx, "radius"), BlockPosArgument.getSpawnablePos(ctx, "position"), false, true))
                                                .then(Commands.literal("effect_only")
                                                        .executes(ctx -> effect(ctx.getSource(), IntegerArgumentType.getInteger(ctx, "radius"), BlockPosArgument.getSpawnablePos(ctx, "position"), false))
                                                        .then(Commands.literal("flash")
                                                                .executes(ctx -> effect(ctx.getSource(), IntegerArgumentType.getInteger(ctx, "radius"), BlockPosArgument.getSpawnablePos(ctx, "position"), true))
                                                        ))
                                                .then(Commands.literal("prime")
                                                        .executes(ctx -> calculate(ctx.getSource(), IntegerArgumentType.getInteger(ctx, "radius"), BlockPosArgument.getSpawnablePos(ctx, "position"), true, true))
                                                        .then(Commands.literal("no_effect")
                                                                .executes(ctx -> calculate(ctx.getSource(), IntegerArgumentType.getInteger(ctx, "radius"), BlockPosArgument.getSpawnablePos(ctx, "position"), true, false))
                                                        )
                                                )
                                                .then(Commands.literal("no_effect")
                                                        .executes(ctx -> calculate(ctx.getSource(), IntegerArgumentType.getInteger(ctx, "radius"), BlockPosArgument.getSpawnablePos(ctx, "position"), false, false))
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
                        .then(Commands.literal("abort").executes(ctx -> abort(ctx)))
//                        .then(Commands.literal("relight").executes(ctx -> relight(ctx.getSource())))
        );
    }

    private static int calculate(CommandSourceStack source, int radius, Vec3i pos, boolean prime, boolean effect) {
        if (explosionProcess != null && !explosionProcess.isDead()) {
            source.sendFailure(new TextComponent("Explosion already in progress"));
            return 1;
        }

        LogHelper.dev("calculate Rad: " + radius + ", Pos: " + pos + ", Prime: " + prime);
        explosionProcess = new ProcessExplosion(new BlockPos(pos), radius, source.getLevel(), prime ? -1 : 0);
        explosionProcess.enableEffect = effect;
        explosionProcess.progressMon = progress -> {
            if (TimeKeeper.getServerTick() % 20 == 0) {
                source.sendSuccess(new TextComponent("Calculating: " + Math.round(progress * 100D) + "%"), true);
            }
        };
        ProcessHandler.addProcess(explosionProcess);
        return 0;
    }

    private static int effect(CommandSourceStack source, int radius, Vec3i pos, boolean flash) {
        if (flash) {
            ClientEventHandler.triggerExplosionEffect(new BlockPos(pos), false);
        } else {
            DraconicNetwork.sendExplosionEffect(source.getLevel().dimension(), new BlockPos(pos), radius * 4, true);
        }
        return 0;
    }

    private static int detonate() {
        WorldEntityHandler.getWorldEntities().forEach(WorldEntity::removeEntity);

        if (explosionProcess != null && explosionProcess.isCalculationComplete()) {
            explosionProcess.detonate();
            explosionProcess = null;
            return 0;
        }
        return 1;
    }

    private static int abort(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        ServerPlayer player = ctx.getSource().getPlayerOrException();

//        try {
////            DraconicGuardianEntity guardian = DEContent.draconicGuardian.create(player.world);
////            guardian.getPhaseManager().setPhase(PhaseType.HOLDING_PATTERN);
////            guardian.setPosition(0, 128, 0);
////            player.world.addEntity(guardian);
//            ChaosWorldGenHandler.generateObelisk((ServerWorld)player.world, player.getPosition().add(0, 0, 40), player.world.rand);
//        } catch (Throwable e) {
//            e.printStackTrace();
//        }


//        ShortPos shortPos = new ShortPos(player.getPosition());
//        ExplosionHelper helper = new ExplosionHelper((ServerWorld) player.world, player.getPosition(), shortPos);
//        LinkedList<HashSet<Integer>> list = new LinkedList<>();
//        HashSet<Integer> set= new HashSet<>();
//
//        int xzRange = 100;
//        int yRange = 70;
//        BlockPos.Mutable pos = new BlockPos.Mutable();
//        for (int x = -xzRange; x < xzRange; x++) {
//            for (int z = -xzRange; z < xzRange; z++) {
//                for (int y = 0; y < yRange; y++) {
//                    pos.setPos(player.getPosX() + x, y, player.getPosZ() + z);
//                    BlockState state = player.world.getBlockState(pos);
//                    if (state.getBlock() instanceof DraconiumOre || state.getBlock() == Blocks.BEDROCK || state.getBlock() == Blocks.DIAMOND_ORE || state.getBlock() == Blocks.ANCIENT_DEBRIS) continue;
////                    player.world.setBlockState(pos, Blocks.AIR.getDefaultState());
//                    set.add(shortPos.getIntPos(pos));
//                }
//            }
//        }
//        list.add(set);
//        helper.setBlocksForRemoval(list);
//        helper.finish();


//        WorldEntityHandler.addWorldEntity(player.world, new TestWorldEntity());


        if (explosionProcess != null) {
            explosionProcess.isDead = true;
            explosionProcess = null;
            return 0;
        }
        return 1;
    }

    private static int relight(CommandSourceStack source) {
        BlockPos pos = new BlockPos(source.getPosition());
        ServerLevel world = source.getLevel();
        while (world.isEmptyBlock(pos)) pos = pos.below();

//        world.setBlockState(pos, Blocks.GLASS.getDefaultState());

        BlockPos.betweenClosedStream(pos.offset(-10, -10, -10), pos.offset(10, 10, 10)).forEach(e -> {
            world.getLightEngine().checkBlock(e);
            LevelChunk chunk = world.getChunkAt(e);
//            SUpdateLightPacket packet = new SUpdateLightPacket(chunk.getPos(), world.getLightManager());
//            world.getChunkProvider().chunkManager.getTrackingPlayers(chunk.getPos(), false).forEach(f -> f.connection.sendPacket(packet));
//            world.getLightManager().updateSectionStatus(e, true);
        });

        return 0;
    }
}
