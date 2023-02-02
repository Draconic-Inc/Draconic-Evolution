package com.brandon3055.draconicevolution.api.modules.entities.logic;

import com.brandon3055.brandonscore.api.power.IOPStorage;
import com.brandon3055.brandonscore.inventory.BlockToStackHelper;
import com.brandon3055.brandonscore.inventory.InventoryDynamic;
import com.brandon3055.draconicevolution.init.EquipCfg;
import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.game.ClientboundBlockUpdatePacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraftforge.common.ForgeHooks;

/**
 * Created by brandon3055 on 31/01/2023
 */
public interface IHarvestHandler {

    /**
     * Start harvest operation
     *
     * @param origin Starting position of the harvest operation.
     * @param level  The level
     * @return true if the operation was able to start successfully.
     */
    boolean start(BlockPos origin, Level level, ServerPlayer player);

    /**
     * Update the harvest operation.
     */
    void tick(Level level, ServerPlayer player, ItemStack stack, IOPStorage storage, InventoryDynamic stackCollector);

    /**
     * @return true if the operation is complete.
     */
    boolean isDone();

    /**
     * Interrupt the current harvest operation and clear any data stores.
     */
    void stop(Level level, ServerPlayer player);

    default void doHarvest(ItemStack stack, Player player, Level level, BlockPos pos, IOPStorage storage, InventoryDynamic stackCollector) {
        if (level.isClientSide || (storage.getOPStored() < EquipCfg.energyHarvest && !player.getAbilities().instabuild)) return;

        BlockState state = level.getBlockState(pos);
        FluidState fluidState = level.getFluidState(pos);
        Block block = state.getBlock();

        int xp = ForgeHooks.onBlockBreakEvent(level, ((ServerPlayer) player).gameMode.getGameModeForPlayer(), (ServerPlayer) player, pos);
        if (xp == -1) {
            ServerPlayer mpPlayer = (ServerPlayer) player;
            mpPlayer.connection.send(new ClientboundBlockUpdatePacket(level, pos));
            return;
        }

        if (player.getAbilities().instabuild) {
            if (block.onDestroyedByPlayer(state, level, pos, player, false, fluidState)) {
                block.destroy(level, pos, state);
            }

            ((ServerPlayer) player).connection.send(new ClientboundBlockUpdatePacket(level, pos));
            return;
        }

        stack.mineBlock(level, state, pos, player);
        BlockToStackHelper.breakAndCollectWithPlayer(level, pos, stackCollector, player, xp);
        storage.modifyEnergyStored(-EquipCfg.energyHarvest);
    }
}
