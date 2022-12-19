package com.brandon3055.draconicevolution.blocks.machines;

import com.brandon3055.brandonscore.blocks.BlockBCore;
import com.brandon3055.brandonscore.blocks.EntityBlockBCore;
import com.brandon3055.draconicevolution.blocks.tileentity.TileStabilizedSpawner;
import com.brandon3055.draconicevolution.init.DEContent;
import com.brandon3055.draconicevolution.items.MobSoul;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;

/**
 * Created by brandon3055 on 25/09/2016.
 */
public class StabilizedSpawner extends EntityBlockBCore {

    public StabilizedSpawner(Properties properties) {
        super(properties);
        setMobResistant();
        setBlockEntity(() -> DEContent.tile_stabilized_spawner, true);
    }

    @Override
    public void onRemove(BlockState state, Level worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
        super.onRemove(state, worldIn, pos, newState, isMoving);
    }

    public void setStackData(ItemStack stack, String entityString, TileStabilizedSpawner.SpawnerTier tier) {
        setStackDataTier(stack, tier);
        setStackDataEntity(stack, entityString);
    }

    public void setStackDataTier(ItemStack stack, TileStabilizedSpawner.SpawnerTier tier) {
        CompoundTag managedData = stack.getOrCreateTagElement(BlockBCore.BC_TILE_DATA_TAG).getCompound(BlockBCore.BC_MANAGED_DATA_FLAG);
        managedData.putByte("spawner_tier", (byte) tier.ordinal());
        stack.getOrCreateTagElement(BlockBCore.BC_TILE_DATA_TAG).put(BlockBCore.BC_MANAGED_DATA_FLAG, managedData);
    }

    //TODO
    public void setStackDataEntity(ItemStack stack, String entityString) {
        if (entityString != null) {
            ItemStack soul = new ItemStack(DEContent.mob_soul);
            DEContent.mob_soul.setEntity(MobSoul.getCachedRegName(entityString), soul);
            CompoundTag managedData = stack.getOrCreateTagElement(BlockBCore.BC_TILE_DATA_TAG).getCompound(BlockBCore.BC_MANAGED_DATA_FLAG);
            stack.getOrCreateTagElement(BlockBCore.BC_TILE_DATA_TAG).getCompound(BlockBCore.BC_MANAGED_DATA_FLAG);
            managedData.put("mob_soul", soul.serializeNBT());
            stack.getOrCreateTagElement(BlockBCore.BC_TILE_DATA_TAG).put(BlockBCore.BC_MANAGED_DATA_FLAG, managedData);
        }
    }

    @Override
    public boolean canConnectRedstone(BlockState state, BlockGetter world, BlockPos pos, @Nullable Direction side) {
        return true;
    }
}
