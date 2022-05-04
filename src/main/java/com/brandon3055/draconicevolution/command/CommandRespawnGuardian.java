package com.brandon3055.draconicevolution.command;

import com.brandon3055.brandonscore.worldentity.WorldEntityHandler;
import com.brandon3055.draconicevolution.blocks.tileentity.TileChaosCrystal;
import com.brandon3055.draconicevolution.entity.guardian.GuardianFightManager;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.command.CommandException;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;

/**
 * Created by brandon3055 on 23/06/2017.
 */
public class CommandRespawnGuardian {
    public static void register(CommandDispatcher<CommandSource> dispatcher) {
        dispatcher.register(
                Commands.literal("respawn_draconic_guardian")
                        .requires(cs -> cs.hasPermission(2))
                        .executes(context -> respawn(context, false))
//                        .then(Commands.literal("spawn_here")
//                                .executes(context -> respawn(context, true)))
        );
    }

    private static int respawn(CommandContext<CommandSource> ctx, boolean spawnHere) throws CommandSyntaxException {
        if (spawnHere) {
            return spawnHere(ctx);
        }
        Vector3d pos = ctx.getSource().getPosition();
//        ServerPlayerEntity player = ctx.getSource().getPlayerOrException();
        TileChaosCrystal tile = BlockPos.betweenClosedStream(new BlockPos(pos).offset(-60, -60, -60), new BlockPos(pos).offset(60, 60, 60))
                .filter(e -> ctx.getSource().getLevel().getBlockEntity(e) instanceof TileChaosCrystal)
                .map(e -> (TileChaosCrystal) ctx.getSource().getLevel().getBlockEntity(e))
                .findAny()
                .orElse(null);

        if (tile == null) {
            throw new CommandException(new StringTextComponent("Chaos crystal not detected! Please run this command within 60 blocks of an islands chaos crystal."));
        }

        if (tile.parentPos.get().getY() != -1) {
            TileEntity parent = tile.getLevel().getBlockEntity(tile.parentPos.get());
            if (parent instanceof TileChaosCrystal) {
                tile = (TileChaosCrystal) parent;
            } else {
                throw new CommandException(new StringTextComponent("Found invalid chaos crystal at this location"));
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
            throw new CommandException(new StringTextComponent("There should already be a guardian in this area"));
        }

        tile.guardianDefeated.set(false);
        WorldEntityHandler.addWorldEntity(tile.getLevel(), new GuardianFightManager(tile.getBlockPos()));
        ctx.getSource().sendSuccess(new StringTextComponent("Reset Successful. Go to center of island to trigger spawning sequence.").withStyle(TextFormatting.GREEN), true);
        return 0;
    }

    private static int spawnHere(CommandContext<CommandSource> ctx) throws CommandSyntaxException {

        return 0;
    }

}
