package com.brandon3055.draconicevolution.items;

import com.brandon3055.brandonscore.items.ItemBCore;
import com.brandon3055.brandonscore.network.wrappers.SyncableEnum;
import com.brandon3055.brandonscore.network.wrappers.SyncableStack;
import com.brandon3055.draconicevolution.DEFeatures;
import com.brandon3055.draconicevolution.blocks.tileentity.TileStabilizedSpawner;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import static com.brandon3055.draconicevolution.blocks.tileentity.TileStabilizedSpawner.SpawnerTier.*;

/**
 * Created by brandon3055 on 2/06/2017.
 */
public class ItemCore extends ItemBCore {

    @Override
    public EnumActionResult onItemUse(ItemStack stack, EntityPlayer playerIn, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        TileEntity tile = world.getTileEntity(pos);

        if (tile instanceof TileEntityMobSpawner) {
            if (!world.isRemote) {
                String name = ((TileEntityMobSpawner) tile).getSpawnerBaseLogic().getEntityNameToSpawn();
                ItemStack soul = new ItemStack(DEFeatures.mobSoul);
 //               DEFeatures.mobSoul.setEntityString(name, soul);

                ItemStack spawner = new ItemStack(DEFeatures.stabilizedSpawner);
                NBTTagCompound compound = spawner.getSubCompound("DETileData", true);

                TileStabilizedSpawner.SpawnerTier tier = this == DEFeatures.draconicCore ? BASIC : this == DEFeatures.wyvernCore ? WYVERN : this == DEFeatures.awakenedCore ? DRACONIC : CHAOTIC;

                new SyncableEnum<>(tier, false, false).setIndex(0).toNBT(compound);
                new SyncableStack(soul, false, false).setIndex(1).toNBT(compound);

                world.setBlockToAir(pos);
                world.spawnEntityInWorld(new EntityItem(world, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, spawner));
            }
            return EnumActionResult.SUCCESS;
        }

        return super.onItemUse(stack, playerIn, world, pos, hand, facing, hitX, hitY, hitZ);
    }

    @Override
    public boolean hasEffect(ItemStack stack) {
        return stack.getItem() == DEFeatures.chaoticCore;
    }
}
