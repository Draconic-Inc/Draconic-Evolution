package com.brandon3055.draconicevolution.common.blocks.machine;

import java.util.List;
import java.util.Random;

import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

import com.brandon3055.draconicevolution.DraconicEvolution;
import com.brandon3055.draconicevolution.client.gui.GuiHandler;
import com.brandon3055.draconicevolution.common.ModBlocks;
import com.brandon3055.draconicevolution.common.blocks.BlockCustomDrop;
import com.brandon3055.draconicevolution.common.lib.References;
import com.brandon3055.draconicevolution.common.lib.Strings;
import com.brandon3055.draconicevolution.common.tileentities.TileDissEnchanter;

import cpw.mods.fml.common.network.internal.FMLNetworkHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/**
 * Created by Brandon on 27/06/2014.
 */
public class DissEnchanter extends BlockCustomDrop {

    IIcon top;
    IIcon bottom;

    public DissEnchanter() {
        super(Material.iron);
        this.setBlockName(Strings.dissEnchanterName);
        this.setCreativeTab(DraconicEvolution.tabBlocksItems);
        this.setStepSound(soundTypeStone);
        this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.75F, 1.0F);
        ModBlocks.register(this);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void registerBlockIcons(IIconRegister iconRegister) {
        blockIcon = iconRegister.registerIcon(References.RESOURCESPREFIX + "dissEnchanter_side");
        top = iconRegister.registerIcon(References.RESOURCESPREFIX + "dissEnchanter_top");
        bottom = iconRegister.registerIcon(References.RESOURCESPREFIX + "dissEnchanter_bottom");
    }

    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int side, int meta) {
        return side == 0 ? bottom : (side == 1 ? top : blockIcon);
    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        return new TileDissEnchanter();
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float prx,
            float pry, float prz) {
        if (!world.isRemote) {
            FMLNetworkHandler
                    .openGui(player, DraconicEvolution.instance, GuiHandler.GUIID_DISSENCHANTER, world, x, y, z);
        }
        return true;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public int getRenderType() {
        return super.getRenderType();
    }

    @Override
    public boolean isOpaqueCube() {
        return false;
    }

    @Override
    public boolean renderAsNormalBlock() {
        return false;
    }

    @Override
    protected boolean dropInventory() {
        return true;
    }

    @Override
    protected boolean hasCustomDropps() {
        return false;
    }

    @Override
    protected void getCustomTileEntityDrops(TileEntity te, List<ItemStack> droppes) {}

    @Override
    @SideOnly(Side.CLIENT)
    public void randomDisplayTick(World world, int x, int y, int z, Random rand) {
        super.randomDisplayTick(world, x, y, z, rand);

        for (int x1 = x - 2; x1 <= x + 2; ++x1) {
            for (int z1 = z - 2; z1 <= z + 2; ++z1) {
                if (x1 > x - 2 && x1 < x + 2 && z1 == z - 1) {
                    z1 = z + 2;
                }

                if (rand.nextInt(16) == 0) {
                    for (int y1 = y; y1 <= y + 1; ++y1) {
                        if (world.getBlock(x1, y1, z1) == Blocks.bookshelf) {
                            if (!world.isAirBlock((x1 - x) / 2 + x, y1, (z1 - z) / 2 + z)) {
                                break;
                            }

                            // world.spawnParticle("enchantmenttable", x + 0.5D, y + 2.0D, z + 0.5D, l - x +
                            // rand.nextFloat() - 0.5D, j1 - y - rand.nextFloat() - 1.0F, i1 - z + rand.nextFloat() -
                            // 0.5D);
                            world.spawnParticle(
                                    "enchantmenttable",
                                    x1 + 0.4 + (rand.nextFloat() * 0.2),
                                    y1 + 0.8,
                                    z1 + 0.4 + (rand.nextFloat() * 0.2),
                                    x - x1 + rand.nextFloat() - 0.5,
                                    y - y1 + rand.nextFloat(),
                                    z - z1 + rand.nextFloat() - 0.5);
                        }
                    }
                }
            }
        }
    }
}
