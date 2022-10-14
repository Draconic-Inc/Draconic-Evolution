package com.brandon3055.draconicevolution.command;

import com.brandon3055.brandonscore.worldentity.WorldEntityHandler;
import com.brandon3055.draconicevolution.blocks.tileentity.TileChaosCrystal;
import com.brandon3055.draconicevolution.entity.guardian.GuardianFightManager;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandRuntimeException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;

/**
 * Created by brandon3055 on 23/06/2017.
 */
public class CommandRespawnGuardian {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
                Commands.literal("respawn_draconic_guardian")
                        .requires(cs -> cs.hasPermission(2))
                        .executes(context -> respawn(context, false))
//                        .then(Commands.literal("spawn_here")
//                                .executes(context -> respawn(context, true)))
        );
    }

    private static int respawn(CommandContext<CommandSourceStack> ctx, boolean spawnHere) throws CommandSyntaxException {
        if (spawnHere) {
            return spawnHere(ctx);
        }
        Vec3 pos = ctx.getSource().getPosition();
//        ServerPlayerEntity player = ctx.getSource().getPlayerOrException();
        TileChaosCrystal tile = BlockPos.betweenClosedStream(new BlockPos(pos).offset(-60, -60, -60), new BlockPos(pos).offset(60, 60, 60))
                .filter(e -> ctx.getSource().getLevel().getBlockEntity(e) instanceof TileChaosCrystal)
                .map(e -> (TileChaosCrystal) ctx.getSource().getLevel().getBlockEntity(e))
                .findAny()
                .orElse(null);

        if (tile == null) {
            throw new CommandRuntimeException(new TextComponent("Chaos crystal not detected! Please run this command within 60 blocks of an islands chaos crystal."));
        }

        if (tile.parentPos.notNull()) {
            BlockEntity parent = tile.getLevel().getBlockEntity(tile.parentPos.get());
            if (parent instanceof TileChaosCrystal) {
                tile = (TileChaosCrystal) parent;
            } else {
                throw new CommandRuntimeException(new TextComponent("Found invalid chaos crystal at this location"));
            }
        }

        TileChaosCrystal finalTile = tile;
        GuardianFightManager existingFight = WorldEntityHandler.getWorldEntities()
                .stream()
                .filter(e -> e instanceof GuardianFightManager)
                .map(e -> (GuardianFightManager) e)
                .filter(e -> e.getArenaOrigin().equals(finalTile.getBlockPos()))
                .findAny()
                .orElse(null);

        if (existingFight != null) {
            throw new CommandRuntimeException(new TextComponent("There should already be a guardian in this area"));
        }

        tile.guardianDefeated.set(false);
        WorldEntityHandler.addWorldEntity(tile.getLevel(), new GuardianFightManager(tile.getBlockPos()));
        ctx.getSource().sendSuccess(new TextComponent("Reset Successful. Go to center of island to trigger spawning sequence.").withStyle(ChatFormatting.GREEN), true);
        return 0;
    }

    private static int spawnHere(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {

        return 0;
    }

}
