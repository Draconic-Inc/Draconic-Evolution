package com.brandon3055.draconicevolution.command;

import codechicken.lib.raytracer.RayTracer;
import com.brandon3055.brandonscore.BrandonsCore;
import com.brandon3055.brandonscore.lib.ChatHelper;
import com.brandon3055.brandonscore.worldentity.WorldEntity;
import com.brandon3055.brandonscore.worldentity.WorldEntityHandler;
import com.brandon3055.draconicevolution.blocks.tileentity.TileChaosCrystal;
import com.brandon3055.draconicevolution.entity.guardian.GuardianFightManager;
import com.brandon3055.draconicevolution.init.DEContent;
import com.brandon3055.draconicevolution.init.DETags;
import com.brandon3055.draconicevolution.lib.WTFException;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.command.CommandException;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.tags.ITag;
import net.minecraft.tileentity.ChestTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
