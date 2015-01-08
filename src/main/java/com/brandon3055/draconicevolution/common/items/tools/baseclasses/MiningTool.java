package com.brandon3055.draconicevolution.common.items.tools.baseclasses;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by Brandon on 2/01/2015.
 */
public class MiningTool extends ToolBase {


	public MiningTool(ToolMaterial material) {
		super(0, material, null);

	}





	public Set<Block> getObliterationList(ItemStack stack){
		return new HashSet<Block>();
	}







//	@Override
//	public boolean onBlockStartBreak(ItemStack stack, int x, int y, int z, EntityPlayer player) {
//
//		return super.onBlockStartBreak(stack, x, y, z, player);//breakAOEBlocks(stack, x, y, z, player);
//	}
//
//	/* Harvest */
//
//	/**Can Harvest Block*/
//	public boolean func_150897_b(Block block)
//	{
//		boolean flag = false;
//		Set<String> s = getToolClasses(null);
//		if (s.contains("shovel") && block == Blocks.snow_layer || block == Blocks.snow) flag = true;
//		if (s.contains("pickaxe")) flag = true;
//
//		return flag;
//	}
//
//	/**Get Strength vs block*/
//	public float func_150893_a(ItemStack stack, Block block)
//	{
//		boolean flag = false;
//		Set<String> s = getToolClasses(stack);
//		Material m = block.getMaterial();
//
//		if (s.contains("shovel") && m == Material.clay || m == Material.craftedSnow || m == Material.sand || m == Material.grass || m == Material.snow || m == Material.ground) flag = true;
//		if (s.contains("pickaxe") && m == Material.) flag = true;
//
//		LogHelper.info(flag);
//
//		return flag ? this.efficiencyOnProperMaterial : super.func_150893_a(stack, block);
//	}
//
//
//	public boolean breakAOEBlocks(ItemStack stack, int x, int y, int z, EntityPlayer player) {
//		int breakRadius = 2;
//		int breakDepth = 5;
//		// only effective materials matter. We don't want to aoe when beraking dirt with a hammer.
//		Block block = player.worldObj.getBlock(x,y,z);
//		int meta = player.worldObj.getBlockMetadata(x,y,z);
////		if(block == null || (block.isToolEffective(toolClasses[0], meta) && block.isToolEffective(toolClasses[1], meta) && block.isToolEffective(toolClasses[2], meta)))//!isEffective(block, meta))
////			return super.onBlockStartBreak(stack, x,y,z, player);
//
//		MovingObjectPosition mop = ToolHandler.raytraceFromEntity(player.worldObj, player, 4.5d);
//		if(mop == null)
//			return super.onBlockStartBreak(stack, x,y,z, player);
//		int sideHit = mop.sideHit;
//		//int sideHit = Minecraft.getMinecraft().objectMouseOver.sideHit;
//
//		// we successfully destroyed a block. time to do AOE!
//		int xRange = breakRadius;
//		int yRange = breakRadius;
//		int zRange = breakDepth;
//		switch (sideHit) {
//			case 0:
//			case 1:
//				yRange = breakDepth;
//				zRange = breakRadius;
//				break;
//			case 2:
//			case 3:
//				xRange = breakRadius;
//				zRange = breakDepth;
//				break;
//			case 4:
//			case 5:
//				xRange = breakDepth;
//				zRange = breakRadius;
//				break;
//		}
//
//		for (int xPos = x - xRange; xPos <= x + xRange; xPos++)
//			for (int yPos = y - yRange; yPos <= y + yRange; yPos++)
//				for (int zPos = z - zRange; zPos <= z + zRange; zPos++) {
//					// don't break the originally already broken block, duh
//					if (xPos == x && yPos == y && zPos == z)
//						continue;
//
//					if(!super.onBlockStartBreak(stack, xPos, yPos, zPos, player))
//						breakExtraBlock(player.worldObj, xPos, yPos, zPos, sideHit, player, x,y,z);
//				}
//
//
//		return super.onBlockStartBreak(stack, x, y, z, player);
//	}
//
//	protected void breakExtraBlock(World world, int x, int y, int z, int sidehit, EntityPlayer player, int refX, int refY, int refZ) {
//		// prevent calling that stuff for air blocks, could lead to unexpected behaviour since it fires events
//		if (world.isAirBlock(x, y, z))
//			return;
//
//		// check if the block can be broken, since extra block breaks shouldn't instantly break stuff like obsidian
//		// or precious ores you can't harvest while mining stone
//		Block block = world.getBlock(x, y, z);
//		int meta = world.getBlockMetadata(x, y, z);
//
////		if(block == null || (block.isToolEffective(toolClasses[0], meta) && block.isToolEffective(toolClasses[1], meta) && block.isToolEffective(toolClasses[2], meta)))//!isEffective(block, meta))
////			return;
//			// only effective materials
////		if (!isEffective(block, meta))
////			return;
//
//		Block refBlock = world.getBlock(refX, refY, refZ);
//		float refStrength = ForgeHooks.blockStrength(refBlock, player, world, refX, refY, refZ);
//		float strength = ForgeHooks.blockStrength(block, player, world, x,y,z);
//
//		// only harvestable blocks that aren't impossibly slow to harvest
//		if (!ForgeHooks.canHarvestBlock(block, player, meta) || refStrength/strength > 10f)
//			return;
//
//		if (player.capabilities.isCreativeMode) {
//			block.onBlockHarvested(world, x, y, z, meta, player);
//			if (block.removedByPlayer(world, player, x, y, z, false))
//				block.onBlockDestroyedByPlayer(world, x, y, z, meta);
//
//			// send update to client
//			if (!world.isRemote) {
//				((EntityPlayerMP)player).playerNetServerHandler.sendPacket(new S23PacketBlockChange(x, y, z, world));
//			}
//			return;
//		}
//
//		// callback to the tool the player uses. Called on both sides. This damages the tool n stuff.
//		player.getCurrentEquippedItem().func_150999_a(world, block, x, y, z, player);
//
//		// server sided handling
//		if (!world.isRemote) {
//			// serverside we reproduce ItemInWorldManager.tryHarvestBlock
//
//			// ItemInWorldManager.removeBlock
//			block.onBlockHarvested(world, x,y,z, meta, player);
//
//			if(block.removedByPlayer(world, player, x,y,z, true)) // boolean is if block can be harvested, checked above
//			{
//				block.onBlockDestroyedByPlayer( world, x,y,z, meta);
//				block.harvestBlock(world, player, x,y,z, meta);
//			}
//
//			// always send block update to client
//			EntityPlayerMP mpPlayer = (EntityPlayerMP) player;
//			mpPlayer.playerNetServerHandler.sendPacket(new S23PacketBlockChange(x, y, z, world));
//		}
//		// client sided handling
//		else {
//			PlayerControllerMP pcmp = Minecraft.getMinecraft().playerController;
//			// clientside we do a "this clock has been clicked on long enough to be broken" call. This should not send any new packets
//			// the code above, executed on the server, sends a block-updates that give us the correct state of the block we destroy.
//
//			// following code can be found in PlayerControllerMP.onPlayerDestroyBlock
//			world.playAuxSFX(2001, x, y, z, Block.getIdFromBlock(block) + (meta << 12));
//			if(block.removedByPlayer(world, player, x,y,z))
//			{
//				block.onBlockDestroyedByPlayer(world, x,y,z, meta);
//			}
//			pcmp.onPlayerDestroyBlock(x, y, z, sidehit);
//
//			// send an update to the server, so we get an update back
////			if(PHConstruct.extraBlockUpdates)
////				Minecraft.getMinecraft().getNetHandler().addToSendQueue(new C07PacketPlayerDigging(2, x,y,z, Minecraft.getMinecraft().objectMouseOver.sideHit));
//		}
//	}
}


