package com.brandon3055.draconicevolution.items;

import com.brandon3055.brandonscore.blocks.BlockBCore;
import com.brandon3055.brandonscore.utils.InventoryUtils;
import com.brandon3055.draconicevolution.blocks.tileentity.TileStabilizedSpawner.SpawnerTier;
import com.brandon3055.draconicevolution.init.DEContent;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.MobSpawnerTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * Created by brandon3055 on 2/06/2017.
 */
public class ItemCore extends Item {

    public ItemCore(Properties properties) {
        super(properties);
    }

    @Override
    public ActionResultType useOn(ItemUseContext context) {
        World world = context.getLevel();
        Hand hand = context.getHand();
        BlockPos pos = context.getClickedPos();

        TileEntity tile = world.getBlockEntity(pos);

        if (tile instanceof MobSpawnerTileEntity) {
            if (!world.isClientSide) {
                ResourceLocation name = ((MobSpawnerTileEntity) tile).getSpawner().getEntityId();
                ItemStack soul = new ItemStack(DEContent.mob_soul);
                DEContent.mob_soul.setEntity(name, soul);
                SpawnerTier tier = SpawnerTier.getTierFromCore(this);

                ItemStack spawner = new ItemStack(DEContent.stabilized_spawner);
                CompoundNBT managedData = new CompoundNBT();
                spawner.getOrCreateTagElement(BlockBCore.BC_TILE_DATA_TAG).put(BlockBCore.BC_MANAGED_DATA_FLAG, managedData);
                managedData.put("mob_soul", soul.serializeNBT());
                managedData.putByte("spawner_tier", (byte) tier.ordinal());

                world.removeBlock(pos, false);
                world.addFreshEntity(new ItemEntity(world, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, spawner));
                InventoryUtils.consumeHeldItem(context.getPlayer(), context.getPlayer().getItemInHand(hand), hand);
            }
            return ActionResultType.SUCCESS;
        }
        return super.useOn(context);
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return stack.getItem() == DEContent.core_chaotic;
    }
}
