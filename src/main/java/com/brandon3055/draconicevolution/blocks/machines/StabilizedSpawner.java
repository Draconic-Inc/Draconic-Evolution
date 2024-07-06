package com.brandon3055.draconicevolution.blocks.machines;

import com.brandon3055.brandonscore.api.TechLevel;
import com.brandon3055.brandonscore.blocks.BlockBCore;
import com.brandon3055.brandonscore.blocks.EntityBlockBCore;
import com.brandon3055.draconicevolution.blocks.tileentity.TileStabilizedSpawner;
import com.brandon3055.draconicevolution.init.DEContent;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Created by brandon3055 on 25/09/2016.
 */
public class StabilizedSpawner extends EntityBlockBCore {

    public StabilizedSpawner(Properties properties) {
        super(properties);
        setMobResistant();
        setExplosionResistant();
        setBlockEntity(DEContent.TILE_STABILIZED_SPAWNER::get, true);
    }

    @Override
    public void onRemove(BlockState state, Level worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
        super.onRemove(state, worldIn, pos, newState, isMoving);
    }

    @Override
    public boolean canConnectRedstone(BlockState state, BlockGetter world, BlockPos pos, @Nullable Direction side) {
        return true;
    }

    @Override
    public void appendHoverText(ItemStack stack, @org.jetbrains.annotations.Nullable BlockGetter worldIn, List<Component> tooltip, TooltipFlag flagIn) {
        super.appendHoverText(stack, worldIn, tooltip, flagIn);
        if (!stack.hasTag()) return;
        CompoundTag tag = stack.getOrCreateTagElement(BlockBCore.BC_TILE_DATA_TAG).getCompound(BlockBCore.BC_MANAGED_DATA_FLAG);

        CompoundTag tier = tag.getCompound("spawner_tier");
        if (tier.contains("value")) {
            int index = tier.getByte("value");
            if (index >= 0 && index < TileStabilizedSpawner.SpawnerTier.values().length) {
                TechLevel techLevel = TileStabilizedSpawner.SpawnerTier.values()[index].getTechLevel();
                tooltip.add(techLevel.getDisplayName().copy().withStyle(techLevel.getTextColour()));
            }
        }
        if (tag.contains("mob_soul")) {
            ItemStack soul = ItemStack.of(tag.getCompound("mob_soul"));
            if (!soul.isEmpty()) {
                tooltip.add(soul.getDisplayName().copy().withStyle(ChatFormatting.YELLOW));
            }
        }
    }
}
