package com.brandon3055.draconicevolution.common.items.tools.baseclasses;

import com.brandon3055.draconicevolution.common.lib.References;
import com.brandon3055.draconicevolution.common.utills.ItemConfigField;
import com.brandon3055.draconicevolution.common.utills.ItemNBTHelper;
import com.brandon3055.draconicevolution.common.utills.LogHelper;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.server.S18PacketEntityTeleport;
import net.minecraft.network.play.server.S23PacketBlockChange;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Brandon on 2/01/2015.
 */
public abstract class MiningTool extends ToolBase {//todo add custom information to sub classes, Update documentation and add doc for hud


	public MiningTool(ToolMaterial material) {
		super(0, material, null);
	}


	public Map<Block, Integer> getObliterationList(ItemStack stack){
		Map<Block, Integer> blockMap = new HashMap<Block, Integer>();

		NBTTagCompound compound = ItemNBTHelper.getCompound(stack);

		if (compound.hasNoTags()) return blockMap;
		for (int i = 0; i < 9; i++)
		{
			NBTTagCompound tag = new NBTTagCompound();
			if (compound.hasKey("Item" + i)) tag = compound.getCompoundTag("Item" + i);

			if (tag.hasNoTags()) continue;

			ItemStack stack1 = ItemStack.loadItemStackFromNBT(tag);

			if (stack1 != null && stack1.getItem() instanceof ItemBlock) blockMap.put(Block.getBlockFromItem(stack1.getItem()), stack1.getItemDamage());
		}

		return blockMap;
	}

	@Override
	public boolean onBlockStartBreak(ItemStack stack, int x, int y, int z, EntityPlayer player) {
		int radius = ItemNBTHelper.getInteger(stack, References.DIG_AOE, 0);
		int depth = ItemNBTHelper.getInteger(stack, References.DIG_DEPTH, 1) - 1;

		return getEnergyStored(stack) >= energyPerOperation && (radius > 0 || depth > 0) ? breakAOEBlocks(stack, x, y, z, radius, depth, player) : super.onBlockStartBreak(stack, x, y, z, player);
	}

	@Override
	public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
		ToolHandler.updateGhostBlocks(player, world);
		LogHelper.info(getDisplayData(stack));
		if (player.isSneaking())
		{
			List<ItemConfigField> fields = getFields(stack, player.inventory.currentItem);
			for (ItemConfigField field : fields)
			{
				if (!world.isRemote && field.name.equals(References.DIG_AOE))
				{
					int aoe = (Integer) field.value;
					aoe++;
					if (aoe > (Integer)field.max) aoe = (Integer) field.min;
					field.value = aoe;
					field.sendChanges();
				}
			}
		}

 		return super.onItemRightClick(stack, world, player);
	}

	//todo Hud, Hud Config, shift right click, attack, axe, hoe
	//This method is basses on tinkerers construct
	public boolean breakAOEBlocks(ItemStack stack, int x, int y, int z, int breakRadius, int breakDepth, EntityPlayer player)
	{
		Map<Block, Integer> blockMap = getObliterationList(stack);
		Block block = player.worldObj.getBlock(x,y,z);
		int meta = player.worldObj.getBlockMetadata(x,y,z);
		boolean effective = false;
		if(block != null)
		{
			for (String s : getToolClasses(stack))
			{
				if (block.isToolEffective(s, meta) || func_150893_a(stack, block) > 1F) effective = true;
			}
		}
		if (!effective)	return true;

		float refStrength = ForgeHooks.blockStrength(block, player, player.worldObj, x, y, z);


		MovingObjectPosition mop = ToolHandler.raytraceFromEntity(player.worldObj, player, 4.5d);
		if(mop == null)
		{
			ToolHandler.updateGhostBlocks(player, player.worldObj);
			return true;
		}
		int sideHit = mop.sideHit;

		int xRange = breakRadius;
		int yRange = breakRadius;
		int zRange = breakDepth;
		int yOffset = 0;
		switch (sideHit) {
			case 0:
			case 1:
				yRange = breakDepth;
				zRange = breakRadius;
				break;
			case 2:
			case 3:
				xRange = breakRadius;
				zRange = breakDepth;
				yOffset = breakRadius - 1;
				break;
			case 4:
			case 5:
				xRange = breakDepth;
				zRange = breakRadius;
				yOffset = breakRadius - 1;
				break;
		}

		for (int xPos = x - xRange; xPos <= x + xRange; xPos++)
		{
			for (int yPos = y + yOffset - yRange; yPos <= y + yOffset + yRange; yPos++)
			{
				for (int zPos = z - zRange; zPos <= z + zRange; zPos++)
				{
					breakExtraBlock(stack, player.worldObj, xPos, yPos, zPos, sideHit, player, refStrength, Math.abs(x - xPos) <= 1 && Math.abs(y - yPos) <= 1 && Math.abs(z - zPos) <= 1, blockMap);
				}
			}
		}

		@SuppressWarnings("unchecked")
		List<EntityItem> items = player.worldObj.getEntitiesWithinAABB(EntityItem.class, AxisAlignedBB.getBoundingBox(x - xRange, y + yOffset - yRange, z - zRange, x + xRange + 1, y + yOffset + yRange + 1, z + zRange + 1));
		for (EntityItem item : items){
			if (!player.worldObj.isRemote)
			{
				item.setLocationAndAngles(player.posX, player.posY, player.posZ, 0, 0);
				((EntityPlayerMP)player).playerNetServerHandler.sendPacket(new S18PacketEntityTeleport(item));
				item.delayBeforeCanPickup = 0;
			}
		}

		return true;
	}

	//This method is basses on tinkerers construct
	protected void breakExtraBlock(ItemStack stack, World world, int x, int y, int z, int sidehit, EntityPlayer player, float refStrength, boolean breakSound, Map<Block, Integer> blockMap)
	{
		if (world.isAirBlock(x, y, z))
			return;

		Block block = world.getBlock(x, y, z);
		int meta = world.getBlockMetadata(x, y, z);

		boolean effective = false;
		if(block != null)
		{
			for (String s : getToolClasses(stack))
			{
				if (block.isToolEffective(s, meta) || func_150893_a(stack, block) > 1F) effective = true;
			}
		}
		if (!effective)	return;

		float strength = ForgeHooks.blockStrength(block, player, world, x,y,z);

		if (!player.canHarvestBlock(block) || !ForgeHooks.canHarvestBlock(block, player, meta) || refStrength/strength > 10f)
			return;

		if (player.capabilities.isCreativeMode || (blockMap.containsKey(block) && blockMap.get(block) == meta)) {
			block.onBlockHarvested(world, x, y, z, meta, player);
			if (block.removedByPlayer(world, player, x, y, z, false))
				block.onBlockDestroyedByPlayer(world, x, y, z, meta);

			if (!world.isRemote) {
				((EntityPlayerMP)player).playerNetServerHandler.sendPacket(new S23PacketBlockChange(x, y, z, world));
			}
			return;
		}

		extractEnergy(stack, energyPerOperation, false);

		if (!world.isRemote) {

			block.onBlockHarvested(world, x, y, z, meta, player);

			if(block.removedByPlayer(world, player, x,y,z, true)) // boolean is if block can be harvested, checked above
			{
				block.onBlockDestroyedByPlayer( world, x,y,z, meta);
				block.harvestBlock(world, player, x,y,z, meta);
				player.addExhaustion(-0.025F);
			}

			EntityPlayerMP mpPlayer = (EntityPlayerMP) player;
			mpPlayer.playerNetServerHandler.sendPacket(new S23PacketBlockChange(x, y, z, world));
		}
		else
		{
			if (breakSound) world.playAuxSFX(2001, x, y, z, Block.getIdFromBlock(block) + (meta << 12));
			if(block.removedByPlayer(world, player, x,y,z, true))
			{
				block.onBlockDestroyedByPlayer(world, x,y,z, meta);
			}

			Minecraft.getMinecraft().getNetHandler().addToSendQueue(new C07PacketPlayerDigging(2, x,y,z, Minecraft.getMinecraft().objectMouseOver.sideHit));
		}
	}
}


