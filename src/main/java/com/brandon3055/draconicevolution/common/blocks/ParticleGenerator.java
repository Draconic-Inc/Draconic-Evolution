package com.brandon3055.draconicevolution.common.blocks;

import java.util.Random;

import com.brandon3055.draconicevolution.common.core.utills.LogHelper;
import com.brandon3055.draconicevolution.common.tileentities.multiblocktiles.TileEnergyStorageCore;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import cpw.mods.fml.common.network.internal.FMLNetworkHandler;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import com.brandon3055.draconicevolution.client.interfaces.GuiHandler;
import com.brandon3055.draconicevolution.client.render.CustomParticle;
import com.brandon3055.draconicevolution.DraconicEvolution;
import com.brandon3055.draconicevolution.common.core.handler.ParticleHandler;
import com.brandon3055.draconicevolution.common.lib.References;
import com.brandon3055.draconicevolution.common.lib.Strings;
import com.brandon3055.draconicevolution.common.tileentities.TileParticleGenerator;

public class ParticleGenerator extends Block
{
	public static Block instance;

	public ParticleGenerator() {
		super(Material.rock);
		this.setBlockName(Strings.particleGeneratorName);
		this.setCreativeTab(DraconicEvolution.getCreativeTab(2));
		this.setStepSound(soundTypeStone);
		this.setHardness(1f);
		this.setResistance(200.0f);
		this.setLightOpacity(0);
		GameRegistry.registerBlock(this, this.getUnlocalizedName());
	}

	@Override
	public void registerBlockIcons(IIconRegister iconRegister)
	{
		blockIcon = iconRegister.registerIcon(References.RESOURCESPREFIX + "machine_side");
	}

	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int meta, float p_149727_7_, float p_149727_8_, float p_149727_9_)
	{
		if (meta == 1) return false;
		if (player.isSneaking())
		{
			if (activateEnergyStorageCore(world, x, y, z, player)) return true;
			TileEntity tile = world.getTileEntity(x, y, z);
			TileParticleGenerator gen = (tile != null && tile instanceof TileParticleGenerator) ? (TileParticleGenerator) tile : null;
			if (gen != null)
			{
				gen.toggleInverted();
			}
		} else
			FMLNetworkHandler.openGui(player, DraconicEvolution.instance, GuiHandler.GUIID_PARTICLEGEN, world, x, y, z);
		return true;
	}

	@Override
	public int getRenderType()
	{
		return -1;
	}

	@Override
	public void onNeighborBlockChange(final World world, final int x, final int y, final int z, final Block block)
	{
		TileEntity tile = world.getTileEntity(x, y, z);
		TileParticleGenerator gen = (tile != null && tile instanceof TileParticleGenerator) ? (TileParticleGenerator) tile : null;
		if (gen != null)
		{
			gen.signal = world.isBlockIndirectlyGettingPowered(x, y, z);
			world.markBlockForUpdate(x, y, z);
		}
	}

	@Override
	public boolean canConnectRedstone(IBlockAccess world, int x, int y, int z, int side)
	{
		return true;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void onBlockDestroyedByPlayer(World world, int x, int y, int z, int meta)
	{
		if (world.isRemote)
		{
			Random rand = world.rand;
			float modifier = 0.1F;
			float SCALE = 1;
			double spawnX = x + 0.5D;
			double spawnY = y + 0.5D;
			double spawnZ = z + 0.5D;

			for (int i = 0; i < 100; i++)
			{
				float MX = modifier - ((2f*modifier) * rand.nextFloat());
				float MY = modifier - ((2f*modifier) * rand.nextFloat());
				float MZ = modifier - ((2f*modifier) * rand.nextFloat());

				
				{
					CustomParticle particle = new CustomParticle(world, spawnX, spawnY, spawnZ, MX, MY, MZ, SCALE, false, 1);
					particle.red = rand.nextInt(255);
					particle.green = rand.nextInt(255);
					particle.blue = rand.nextInt(255);
					particle.maxAge = rand.nextInt(10);
					particle.fadeTime = 20;
					particle.fadeLength = 20;
					particle.gravity = 0F;

					ParticleHandler.spawnCustomParticle(particle);
				}
			}
		}
	}

	@Override
	public boolean renderAsNormalBlock()
	{
		return false;
	}

	@Override
	public boolean shouldSideBeRendered(IBlockAccess p_149646_1_, int p_149646_2_, int p_149646_3_, int p_149646_4_, int p_149646_5_)
	{
		return false;
	}

	@Override
	public boolean isOpaqueCube()
	{
		return false;
	}

	@Override
	public boolean hasTileEntity(int meta)
	{
		return true;
	}

	@Override
	public TileEntity createTileEntity(World world, int metadata)
	{
		return new TileParticleGenerator();
	}

	private boolean activateEnergyStorageCore(World world, int x, int y, int z, EntityPlayer player){
		for (int x1 = x - 11; x1 <= x + 11; x1++){
			if (world.getBlock(x1, y, z) == ModBlocks.energyStorageCore) {
				TileEnergyStorageCore tile = (world.getTileEntity(x1, y, z) != null && world.getTileEntity(x1, y, z) instanceof TileEnergyStorageCore) ? (TileEnergyStorageCore) world.getTileEntity(x1, y, z) : null;
				if (tile != null && !tile.isOnline())
				{
					if (!tile.tryActivate() && world.isRemote) {
						player.addChatComponentMessage(new ChatComponentTranslation("msg.energyStorageCoreUTA.txt"));
						return false;
					}
					return true;
				}
			}
		}

		for (int z1 = z - 11; z1 <= z + 11; z1++){
			if (world.getBlock(x, y, z1) == ModBlocks.energyStorageCore) {
				TileEnergyStorageCore tile = (world.getTileEntity(x, y, z1) != null && world.getTileEntity(x, y, z1) instanceof TileEnergyStorageCore) ? (TileEnergyStorageCore) world.getTileEntity(x, y, z1) : null;
				if (tile != null && !tile.isOnline())
				{
					if (!tile.tryActivate() && world.isRemote) {
						player.addChatComponentMessage(new ChatComponentTranslation("msg.energyStorageCoreUTA.txt"));
						return false;
					}
					return true;
				}
			}
		}
		return false;
	}
}
