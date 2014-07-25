package com.brandon3055.draconicevolution.common.blocks.multiblock;

import com.brandon3055.draconicevolution.DraconicEvolution;
import com.brandon3055.draconicevolution.common.blocks.DraconicEvolutionBlock;
import com.brandon3055.draconicevolution.common.blocks.ModBlocks;
import com.brandon3055.draconicevolution.common.core.utills.LogHelper;
import com.brandon3055.draconicevolution.common.lib.References;
import com.brandon3055.draconicevolution.common.lib.Strings;
import com.brandon3055.draconicevolution.common.tileentities.TileGenerator;
import com.brandon3055.draconicevolution.common.tileentities.multiblocktiles.TileEnergyStorageCore;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentText;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

/**
 * Created by Brandon on 25/07/2014.
 */
public class EnergyStorageCore extends DraconicEvolutionBlock {

	public EnergyStorageCore() {
		super(Material.iron);
		this.setHardness(10F);
		this.setResistance(100F);
		this.setCreativeTab(DraconicEvolution.getCreativeTab(2));
		this.setBlockName(Strings.energyStorageCoreName);
		ModBlocks.register(this);
	}


	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister iconRegister) {
		blockIcon = iconRegister.registerIcon(References.RESOURCESPREFIX + "energy_storage_core");
	}

	@Override
	public boolean hasTileEntity(int metadata) {
		return true;
	}

	@Override
	public TileEntity createTileEntity(World world, int metadata) {
		return new TileEnergyStorageCore();
	}

	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int p_149727_6_, float p_149727_7_, float p_149727_8_, float p_149727_9_) {
		TileEnergyStorageCore tile = (world.getTileEntity(x, y, z) != null && world.getTileEntity(x, y, z) instanceof TileEnergyStorageCore) ? (TileEnergyStorageCore) world.getTileEntity(x, y, z) : null;
		if (tile == null)
		{
			LogHelper.error("Missing Tile Entity (EnergyStorageCore)");
			return false;
		}
		if (!world.isRemote)
			tile.tryActivate();
		//if (world.isRemote)
			//player.addChatComponentMessage(new ChatComponentText(""+tile.getEnergyStored(ForgeDirection.UP)));
		return true;
	}
}
