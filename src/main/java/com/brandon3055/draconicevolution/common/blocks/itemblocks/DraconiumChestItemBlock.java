package com.brandon3055.draconicevolution.common.blocks.itemblocks;

import com.brandon3055.draconicevolution.common.tileentities.TileDraconiumChest;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import java.util.List;

/**
 * Created by Brandon on 31/10/2014.
 */
public class DraconiumChestItemBlock extends ItemBlock {
	public DraconiumChestItemBlock(Block block) {
		super(block);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void addInformation(ItemStack p_77624_1_, EntityPlayer p_77624_2_, List list, boolean p_77624_4_) {
		list.add(StatCollector.translateToLocal("info.draconiumChestInfo1.txt"));
		list.add(StatCollector.translateToLocal("info.draconiumChestInfo2.txt"));
	}

	@Override
	public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ) {
		if (stack.stackSize <= 0) return false;

		Block block = world.getBlock(x, y, z);

		if (block == Blocks.snow && (world.getBlockMetadata(x, y, z) & 7) < 1) side = 1;

		ForgeDirection sideDir = ForgeDirection.getOrientation(side);

		if (!(block != null && block.isReplaceable(world, x, y, z))) {
			x += sideDir.offsetX;
			y += sideDir.offsetY;
			z += sideDir.offsetZ;
		}


		if (!player.canPlayerEdit(x, y, z, side, stack)) return false;

		Block ownBlock = this.field_150939_a;
		if (y == 255 && ownBlock.getMaterial().isSolid()) return false;

		if (!world.canPlaceEntityOnSide(this.field_150939_a, x, y, z, false, side, player, stack)) return false;

		int newMeta = getMetadata(stack.getItemDamage());
		newMeta = ownBlock.onBlockPlaced(world, x, y, z, side, hitX, hitY, hitZ, newMeta);

		if (!placeBlockAt(stack, player, world, x, y, z, side, hitX, hitY, hitZ, newMeta)) return false;

		TileDraconiumChest te = world.getTileEntity(x, y, z) instanceof TileDraconiumChest ? (TileDraconiumChest)world.getTileEntity(x, y, z) : null;

		if (te != null){
			NBTTagCompound itemTag = stack.getTagCompound();
			if (itemTag != null && itemTag.hasKey("TileCompound")) {
				te.readFromItem(itemTag.getCompoundTag("TileCompound"));
			}
		}

		world.playSoundEffect(x + 0.5, y + 0.5, z + 0.5, ownBlock.stepSound.getBreakSound(), (ownBlock.stepSound.getVolume() + 1.0F) / 2.0F, ownBlock.stepSound.getPitch() * 0.8F);
		stack.stackSize--;
		return true;
	}
}
