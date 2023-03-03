package com.brandon3055.draconicevolution.items.equipment;

import codechicken.lib.inventory.InventoryUtils;
import codechicken.lib.raytracer.RayTracer;
import com.brandon3055.brandonscore.inventory.BlockToStackHelper;
import com.brandon3055.brandonscore.inventory.InventoryDynamic;
import com.brandon3055.brandonscore.lib.Pair;
import com.brandon3055.brandonscore.utils.EnergyUtils;
import com.brandon3055.draconicevolution.api.capability.DECapabilities;
import com.brandon3055.draconicevolution.api.capability.ModuleHost;
import com.brandon3055.draconicevolution.api.capability.PropertyProvider;
import com.brandon3055.draconicevolution.api.modules.ModuleHelper;
import com.brandon3055.draconicevolution.api.modules.ModuleTypes;
import com.brandon3055.draconicevolution.api.modules.data.AOEData;
import com.brandon3055.draconicevolution.init.EquipCfg;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.network.protocol.game.ClientboundBlockUpdatePacket;
import net.minecraft.network.protocol.game.ServerboundPlayerActionPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.common.ForgeHooks;

import java.util.List;
import java.util.Random;

/**
 * Created by brandon3055 on 16/6/20
 */
public interface IModularMiningTool extends IModularTieredItem {

    Random rand = new Random();

    @Override
    default boolean onBlockStartBreak(ItemStack stack, BlockPos pos, Player player) {
        if (getEnergyStored(stack) < EquipCfg.energyHarvest && !player.getAbilities().instabuild) {
            return false;
        }

        ModuleHost host = stack.getCapability(DECapabilities.MODULE_HOST_CAPABILITY).orElseThrow(IllegalStateException::new);
        int aoe = host.getModuleData(ModuleTypes.AOE, new AOEData(0)).aoe();
        boolean aoeSafe = false;
        if (host instanceof PropertyProvider) {
            if (((PropertyProvider) host).hasInt("mining_aoe")) {
                aoe = ((PropertyProvider) host).getInt("mining_aoe").getValue();
            }
            if (((PropertyProvider) host).hasBool("aoe_safe")) {
                aoeSafe = ((PropertyProvider) host).getBool("aoe_safe").getValue();
            }
        }

        return breakAOEBlocks(host, stack, pos, aoe, 0, player, aoeSafe);
    }

    default boolean breakAOEBlocks(ModuleHost host, ItemStack stack, BlockPos pos, int breakRadius, int breakDepth, Player player, boolean aoeSafe) {
        BlockState blockState = player.level.getBlockState(pos);
        if (!isCorrectToolForDrops(stack, blockState)) {
            return false;
        }

        InventoryDynamic inventoryDynamic = new InventoryDynamic();
        float refStrength = blockStrength(blockState, player, player.level, pos);
        Pair<BlockPos, BlockPos> aoe = getMiningArea(pos, player, breakRadius, breakDepth);
        List<BlockPos> aoeBlocks = BlockPos.betweenClosedStream(aoe.key(), aoe.value()).map(BlockPos::new).toList();

        if (aoeSafe) {
            for (BlockPos block : aoeBlocks) {
                if (!player.level.isEmptyBlock(block) && player.level.getBlockEntity(block) != null) {
                    if (player.level.isClientSide) player.sendMessage(new TranslatableComponent("item_prop.draconicevolution.aoe_safe.blocked"), Util.NIL_UUID);
                    else ((ServerPlayer) player).connection.send(new ClientboundBlockUpdatePacket(((ServerPlayer) player).level, block));
                    return true;
                }
            }
        }

        aoeBlocks.forEach(block -> breakAOEBlock(stack, player.level, block, player, refStrength, inventoryDynamic, rand.nextInt(Math.max(5, (breakRadius * breakDepth) / 5)) == 0));
        List<ItemEntity> items = player.level.getEntitiesOfClass(ItemEntity.class, new AABB(aoe.key(), aoe.value().offset(1, 1, 1)));
        for (ItemEntity item : items) {
            if (!player.level.isClientSide && item.isAlive()) {
                InventoryUtils.insertItem(inventoryDynamic, item.getItem(), false);
                item.discard();
            }
        }

        ModuleHelper.handleItemCollection(player, host, EnergyUtils.getStorage(stack), inventoryDynamic);
        return true;
    }

    static float blockStrength(BlockState state, Player player, Level world, BlockPos pos) {
        float hardness = state.getDestroySpeed(world, pos);
        if (hardness < 0.0F) {
            return 0.0F;
        }

        if (!ForgeHooks.isCorrectToolForDrops(state, player)) {
            return player.getDigSpeed(state, pos) / hardness / 100F;
        } else {
            return player.getDigSpeed(state, pos) / hardness / 30F;
        }
    }

    default Pair<BlockPos, BlockPos> getMiningArea(BlockPos pos, Player player, int breakRadius, int breakDepth) {
        BlockHitResult traceResult = RayTracer.retrace(player);
        if (traceResult.getType() == HitResult.Type.MISS) {
            return new Pair<>(pos, pos);
        }

        int sideHit = traceResult.getDirection().get3DDataValue();

        int xMax = breakRadius;
        int xMin = breakRadius;
        int yMax = breakRadius;
        int yMin = breakRadius;
        int zMax = breakRadius;
        int zMin = breakRadius;
        int yOffset = 0;

        switch (sideHit) {
            case 0 -> {
                yMax = breakDepth;
                yMin = 0;
                zMax = breakRadius;
            }
            case 1 -> {
                yMin = breakDepth;
                yMax = 0;
                zMax = breakRadius;
            }
            case 2 -> {
                xMax = breakRadius;
                zMin = 0;
                zMax = breakDepth;
                yOffset = breakRadius - 1;
            }
            case 3 -> {
                xMax = breakRadius;
                zMax = 0;
                zMin = breakDepth;
                yOffset = breakRadius - 1;
            }
            case 4 -> {
                xMax = breakDepth;
                xMin = 0;
                zMax = breakRadius;
                yOffset = breakRadius - 1;
            }
            case 5 -> {
                xMin = breakDepth;
                xMax = 0;
                zMax = breakRadius;
                yOffset = breakRadius - 1;
            }
        }

        if (breakRadius == 0) {
            yOffset = 0;
        }

        return new Pair<>(pos.offset(-xMin, yOffset - yMin, -zMin), pos.offset(xMax, yOffset + yMax, zMax));
    }

    default void breakAOEBlock(ItemStack stack, Level world, BlockPos pos, Player player, float refStrength, InventoryDynamic inventory, boolean breakFX) {
        if (world.isEmptyBlock(pos)) {
            return;
        }

        BlockState state = world.getBlockState(pos);
        FluidState fluidState = world.getFluidState(pos);
        Block block = state.getBlock();

        if (!isCorrectToolForDrops(stack, state)) {
            return;
        }

        float strength = blockStrength(state, player, world, pos);

        if (!ForgeHooks.isCorrectToolForDrops(state, player) || refStrength / strength > 10f) {
            return;
        }

        if (player instanceof ServerPlayer serverPlayer) {
            int xp = ForgeHooks.onBlockBreakEvent(world, serverPlayer.gameMode.getGameModeForPlayer(), (ServerPlayer) player, pos);
            if (xp == -1) {
                ServerPlayer mpPlayer = (ServerPlayer) player;
                mpPlayer.connection.send(new ClientboundBlockUpdatePacket(world, pos));
                return;
            }

            if (player.getAbilities().instabuild) {
                if (block.onDestroyedByPlayer(state, world, pos, player, false, fluidState)) {
                    block.destroy(world, pos, state);
                }
            } else {
                stack.mineBlock(world, state, pos, player);
                BlockToStackHelper.breakAndCollectWithPlayer(world, pos, inventory, player, xp);
                extractEnergy(player, stack, EquipCfg.energyHarvest);
            }
        } else {
            if (block.onDestroyedByPlayer(state, world, pos, player, true, fluidState)) {
                block.destroy(world, pos, state);
            }

            stack.mineBlock(world, state, pos, player);

            if (Minecraft.getInstance().hitResult instanceof BlockHitResult) {
                Minecraft.getInstance().getConnection().send(new ServerboundPlayerActionPacket(ServerboundPlayerActionPacket.Action.STOP_DESTROY_BLOCK, pos, ((BlockHitResult) Minecraft.getInstance().hitResult).getDirection()));
            }
        }
    }
}
