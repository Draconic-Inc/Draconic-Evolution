package com.brandon3055.draconicevolution.common.blocks;

import com.brandon3055.draconicevolution.DraconicEvolution;
import com.brandon3055.draconicevolution.common.ModBlocks;
import com.brandon3055.draconicevolution.common.ModItems;
import com.brandon3055.draconicevolution.common.entity.EntityChaosVortex;
import com.brandon3055.draconicevolution.common.lib.References;
import com.brandon3055.draconicevolution.common.tileentities.TileChaosShard;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import java.util.Random;

/**
 * Created by brandon3055 on 24/9/2015.
 */
public class ChaosCrystal extends BlockDE {

	public ChaosCrystal(){
		super(Material.rock);
		this.setHardness(100.0F);
		this.setResistance(4000.0F);
		this.setBlockUnbreakable();
		this.setBlockName("chaosCrystal");
		this.setCreativeTab(DraconicEvolution.tabBlocksItems);

		ModBlocks.register(this);
	}

	@Override
	public float getBlockHardness(World world, int x, int y, int z) {
		TileChaosShard tile = world.getTileEntity(x, y, z) instanceof TileChaosShard ? (TileChaosShard) world.getTileEntity(x, y, z) : null;
		if (tile != null) return tile.guardianDefeated ? 100F : -1F;
		return super.getBlockHardness(world, x, y, z);
	}

	@Override
	public void registerBlockIcons(IIconRegister iconRegister) {blockIcon = iconRegister.registerIcon(References.RESOURCESPREFIX + "transparency");}

	@Override
	public boolean hasTileEntity(int metadata) {
		return true;
	}

	@Override
	public TileEntity createTileEntity(World world, int metadata) {
		return new TileChaosShard();
	}

	@Override
	public int getRenderType() {
		return -1;
	}

	@Override
	public Item getItemDropped(int p_149650_1_, Random p_149650_2_, int p_149650_3_) {
		return ModItems.chaosShard;
	}

	@Override
	public int quantityDropped(int meta, int fortune, Random random) {
		return 5;
	}

	@Override
	public boolean canEntityDestroy(IBlockAccess world, int x, int y, int z, Entity entity) {
		return false;
	}

	@Override
	public void breakBlock(World world, int x, int y, int z, Block block, int i) {
		super.breakBlock(world, x, y, z, block, i);
		if (!world.isRemote){
			EntityChaosVortex vortex = new EntityChaosVortex(world);
			vortex.setPosition(x+0.5, y+0.5, z+0.5);
			world.spawnEntityInWorld(vortex);
		}
	}
}
