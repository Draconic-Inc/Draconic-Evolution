package com.brandon3055.draconicevolution.items;

import com.brandon3055.brandonscore.blocks.BlockBCore;
import com.brandon3055.brandonscore.utils.InventoryUtils;
import com.brandon3055.draconicevolution.blocks.tileentity.TileStabilizedSpawner.SpawnerTier;
import com.brandon3055.draconicevolution.init.DEContent;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.BaseSpawner;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.SpawnData;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.SpawnerBlockEntity;

/**
 * Created by brandon3055 on 2/06/2017.
 */
public class ItemCore extends Item {

    public ItemCore(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level world = context.getLevel();
        InteractionHand hand = context.getHand();
        BlockPos pos = context.getClickedPos();

        BlockEntity tile = world.getBlockEntity(pos);

        if (tile instanceof SpawnerBlockEntity) {
            if (!world.isClientSide) {
                SpawnData data = ((SpawnerBlockEntity) tile).getSpawner().nextSpawnData;
                if (data == null) {
                    return InteractionResult.FAIL;
                }
                String id = data.getEntityToSpawn().getString("id");
                if (id.isEmpty()) {
                    return InteractionResult.FAIL;
                }
                ResourceLocation name = new ResourceLocation(id);
                ItemStack soul = new ItemStack(DEContent.mob_soul);
                DEContent.mob_soul.setEntity(name, soul);
                SpawnerTier tier = SpawnerTier.getTierFromCore(this);

                ItemStack spawner = new ItemStack(DEContent.stabilized_spawner);
                CompoundTag managedData = new CompoundTag();
                spawner.getOrCreateTagElement(BlockBCore.BC_TILE_DATA_TAG).put(BlockBCore.BC_MANAGED_DATA_FLAG, managedData);
                managedData.put("mob_soul", soul.serializeNBT());
                managedData.putByte("spawner_tier", (byte) tier.ordinal());

                world.removeBlock(pos, false);
                world.addFreshEntity(new ItemEntity(world, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, spawner));
                InventoryUtils.consumeHeldItem(context.getPlayer(), context.getPlayer().getItemInHand(hand), hand);
            }
            return InteractionResult.PASS;
        }
        return super.useOn(context);
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return stack.getItem() == DEContent.core_chaotic || super.isFoil(stack);
    }
}
