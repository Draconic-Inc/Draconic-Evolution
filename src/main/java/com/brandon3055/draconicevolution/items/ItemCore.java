package com.brandon3055.draconicevolution.items;

import com.brandon3055.brandonscore.items.ItemBCore;
import com.brandon3055.brandonscore.utils.InventoryUtils;
import com.brandon3055.draconicevolution.DEFeatures;
import com.brandon3055.draconicevolution.blocks.tileentity.TileStabilizedSpawner.SpawnerTier;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * Created by brandon3055 on 2/06/2017.
 */
public class ItemCore extends ItemBCore {

    @Override
    public EnumActionResult onItemUse(EntityPlayer playerIn, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        TileEntity tile = world.getTileEntity(pos);

        if (tile instanceof TileEntityMobSpawner) {
            if (!world.isRemote) {
                ResourceLocation name = ((TileEntityMobSpawner) tile).getSpawnerBaseLogic().getEntityId();
                ItemStack soul = new ItemStack(DEFeatures.mobSoul);
                DEFeatures.mobSoul.setEntity(name, soul);
                SpawnerTier tier = SpawnerTier.getTierFromCore(this);

                ItemStack spawner = new ItemStack(DEFeatures.stabilizedSpawner);
                NBTTagCompound managedData = new NBTTagCompound();
                spawner.getOrCreateSubCompound("BCTileData").setTag("BCManagedData", managedData);
                managedData.setTag("mobSoul", soul.serializeNBT());
                managedData.setByte("spawnerTier", (byte) tier.ordinal());

                world.setBlockToAir(pos);
                world.spawnEntity(new EntityItem(world, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, spawner));
                InventoryUtils.consumeHeldItem(playerIn, playerIn.getHeldItem(hand), hand);
            }
            return EnumActionResult.SUCCESS;
        }

        return super.onItemUse(playerIn, world, pos, hand, facing, hitX, hitY, hitZ);
    }

    @Override
    public boolean hasEffect(ItemStack stack) {
        return stack.getItem() == DEFeatures.chaoticCore;
    }
}
