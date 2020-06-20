package com.brandon3055.draconicevolution.items;

import com.brandon3055.brandonscore.blocks.BlockBCore;
import com.brandon3055.brandonscore.items.ItemBCore;
import com.brandon3055.brandonscore.utils.InventoryUtils;
import com.brandon3055.draconicevolution.init.DEContent;
import com.brandon3055.draconicevolution.blocks.tileentity.TileStabilizedSpawner.SpawnerTier;
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
    public ActionResultType onItemUse(ItemUseContext context) {
        World world = context.getWorld();
        Hand hand = context.getHand();
        BlockPos pos = context.getPos();

        TileEntity tile = world.getTileEntity(pos);

        if (tile instanceof MobSpawnerTileEntity) {
            if (!world.isRemote) {
                ResourceLocation name = ((MobSpawnerTileEntity) tile).getSpawnerBaseLogic().getEntityId();
                ItemStack soul = new ItemStack(DEContent.mob_soul);
                DEContent.mob_soul.setEntity(name, soul);
                SpawnerTier tier = SpawnerTier.getTierFromCore(this);

                ItemStack spawner = new ItemStack(DEContent.stabilized_spawner);
                CompoundNBT managedData = new CompoundNBT();
                spawner.getOrCreateChildTag(BlockBCore.BC_TILE_DATA_TAG).put(BlockBCore.BC_MANAGED_DATA_FLAG, managedData);
                managedData.put("mob_soul", soul.serializeNBT());
                managedData.putByte("spawner_tier", (byte) tier.ordinal());

                world.removeBlock(pos, false);
                world.addEntity(new ItemEntity(world, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, spawner));
                InventoryUtils.consumeHeldItem(context.getPlayer(), context.getPlayer().getHeldItem(hand), hand);
            }
            return ActionResultType.SUCCESS;
        }
        return super.onItemUse(context);
    }

    @Override
    public boolean hasEffect(ItemStack stack) {
        return stack.getItem() == DEContent.core_chaotic;
    }
}
