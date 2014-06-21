package com.brandon3055.draconicevolution.common.blocks;

import java.util.Random;

import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import com.brandon3055.draconicevolution.DraconicEvolution;
import com.brandon3055.draconicevolution.common.core.handler.ParticleHandler;
import com.brandon3055.draconicevolution.common.core.network.ExamplePacket;

public class TestBlock extends TolkienBlock {

	protected TestBlock() {
		super(Material.rock);
		this.setBlockName("testBlock");
		this.setCreativeTab(DraconicEvolution.getCreativeTab(2));
		this.setHardness(5f);
		this.setResistance(200.0f);
		//this.setBlockBounds(0.4f, 0f, 0.4f, 0.6f, 1f, 0.6f);
		ModBlocks.register(this);
	}

	@Override
	public AxisAlignedBB getCollisionBoundingBoxFromPool(World p_149668_1_, int p_149668_2_, int p_149668_3_, int p_149668_4_)
	{
		return null;
	}
	
	@Override
	public boolean isOpaqueCube()
	{
		return false;
	}
	
	@Override
	public int getRenderType()
	{
		return -1;
	}

	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float px, float py, float pz)
	{	
		if (!world.isRemote && !player.isSneaking())
		{
			System.out.println("Sending from server");
			DraconicEvolution.channelHandler.sendToAll(new ExamplePacket());
		}

		if (world.isRemote && player.isSneaking())
		{
			System.out.println("Sending from client");
			DraconicEvolution.channelHandler.sendToServer(new ExamplePacket());
		}

		return true;
	}

	@Override
	public Item getItemDropped(int p_149650_1_, Random p_149650_2_, int p_149650_3_)
	{
		// TODO Auto-generated method stub
		return Item.getItemFromBlock(Blocks.cobblestone);
	}

	
	@Override
	public void randomDisplayTick(World world, int x, int y, int z, Random rand)
	{
		float mX1 = (rand.nextFloat() - 0.5F) * 0.005F;
		float mY1 = rand.nextFloat() * 0.01F;
		float mZ1 = (rand.nextFloat() - 0.5F) * 0.005F;
		ParticleHandler.spawnParticle("testParticle", x + 0.5, y + 1.5, z + 0.5, mX1, mY1, mZ1, 1);
		for (int i = 0; i < 3; i++)
		{
			float mX = (rand.nextFloat() - 0.5F) * 0.005F;
			float mY = 0.01F + rand.nextFloat() * 0.005F;
			float mZ = (rand.nextFloat() - 0.5F) * 0.005F;	
			float scale = 0.2F + (rand.nextFloat() * 0.2F);
			ParticleHandler.spawnParticle("testParticle", x + 0.5, y + 1.5, z + 0.5, mX, mY, mZ, scale);
		}
	}
}
