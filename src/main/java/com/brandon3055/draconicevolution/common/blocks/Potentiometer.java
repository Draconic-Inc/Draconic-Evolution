package com.brandon3055.draconicevolution.common.blocks;

import static net.minecraftforge.common.util.ForgeDirection.*;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import com.brandon3055.draconicevolution.DraconicEvolution;
import com.brandon3055.draconicevolution.common.ModBlocks;
import com.brandon3055.draconicevolution.common.lib.References;
import com.brandon3055.draconicevolution.common.lib.Strings;
import com.brandon3055.draconicevolution.common.tileentities.TilePotentiometer;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class Potentiometer extends BlockDE {

    IIcon icons[] = new IIcon[16];

    public Potentiometer() {
        super(Material.circuits);
        this.setCreativeTab(DraconicEvolution.tabBlocksItems);
        this.setHardness(0.3f);
        this.setResistance(0.1f);
        this.setLightLevel(0.3F);
        this.setBlockName(Strings.potentiometerName);
        ModBlocks.register(this);
    }

    @Override
    public boolean renderAsNormalBlock() {
        return false;
    }

    @Override
    public AxisAlignedBB getCollisionBoundingBoxFromPool(World p_149668_1_, int p_149668_2_, int p_149668_3_,
            int p_149668_4_) {
        return null;
    }

    @Override
    public boolean hasTileEntity(int metadata) {
        return true;
    }

    @Override
    public TileEntity createTileEntity(World world, int metadata) {
        return new TilePotentiometer();
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(final IIconRegister iconRegister) {
        for (int i = 0; i < 16; i++) {
            icons[i] = iconRegister.registerIcon(References.RESOURCESPREFIX + "potentiometer/potentiometer_" + i);
        }
    }

    @Override
    public IIcon getIcon(IBlockAccess world, int x, int y, int z, int meta) {
        TileEntity tile = world.getTileEntity(x, y, z);
        if (tile != null && tile instanceof TilePotentiometer) return icons[((TilePotentiometer) tile).power];
        else return null;
    }

    @Override
    public IIcon getIcon(int side, int meta) {
        return icons[0];
    }

    @Override
    public boolean canPlaceBlockAt(World world, int x, int y, int z) {
        return (world.isSideSolid(x, y + 1, z, UP)) || (world.isSideSolid(x, y - 1, z, DOWN))
                || (world.isSideSolid(x - 1, y, z, EAST))
                || (world.isSideSolid(x + 1, y, z, WEST))
                || (world.isSideSolid(x, y, z - 1, SOUTH))
                || (world.isSideSolid(x, y, z + 1, NORTH));
    }

    @Override
    public void setBlockBoundsBasedOnState(IBlockAccess world, int x, int y, int z) {
        int l = world.getBlockMetadata(x, y, z);
        this.setBounds(l);
    }

    private void setBounds(int meta) {
        int j = meta;
        float f = 0.206F;
        float f1 = 0.796F;
        float f2 = 0.296F;
        float f3 = 0.125F;

        if (j == 6) {
            this.setBlockBounds(0.5F - f2, 0.0F, 0.5F - f2, 0.5F + f2, f3, 0.5F + f2);
        }
        if (j == 1) {
            this.setBlockBounds(0.0F, f, 0.5F - f2, f3, f1, 0.5F + f2);
        } else if (j == 2) {
            this.setBlockBounds(1.0F - f3, f, 0.5F - f2, 1.0F, f1, 0.5F + f2);
        } else if (j == 3) {
            this.setBlockBounds(0.5F - f2, f, 0.0F, 0.5F + f2, f1, f3);
        } else if (j == 4) {
            this.setBlockBounds(0.5F - f2, f, 1.0F - f3, 0.5F + f2, f1, 1.0F);
        }
        if (j == 5) {
            this.setBlockBounds(0.5F - f2, 1.0F - f3, 0.5F - f2, 0.5F + f2, 1.0F, 0.5F + f2);
        }
    }

    @Override
    public void setBlockBoundsForItemRender() {
        float f = 0.206F;
        float f1 = 0.796F;
        float f2 = 0.296F;
        float f3 = 0.125F;
        this.setBlockBounds(0.5F - f2, f, 0.3F, 0.5F + f2, f1, f3 + 0.3F);
    }

    @Override
    public int onBlockPlaced(World world, int x, int y, int z, int side, float p_149660_6_, float p_149660_7_,
            float p_149660_8_, int p_149660_9_) {
        int j1 = world.getBlockMetadata(x, y, z);
        int k1 = j1 & 8;
        j1 &= 7;

        ForgeDirection dir = ForgeDirection.getOrientation(side);

        if (dir == NORTH && world.isSideSolid(x, y, z + 1, NORTH)) {
            j1 = 4;
        } else if (dir == SOUTH && world.isSideSolid(x, y, z - 1, SOUTH)) {
            j1 = 3;
        } else if (dir == WEST && world.isSideSolid(x + 1, y, z, WEST)) {
            j1 = 2;
        } else if (dir == EAST && world.isSideSolid(x - 1, y, z, EAST)) {
            j1 = 1;
        } else if (dir == UP && world.isSideSolid(x, y - 1, z, UP)) {
            j1 = 6;
        } else if (dir == DOWN && world.isSideSolid(x, y + 1, z, DOWN)) {
            j1 = 5;
        } else {
            j1 = this.fundSolidSide(world, x, y, z);
        }

        return j1 + k1;
    }

    private int fundSolidSide(World world, int x, int y, int z) {
        if (world.isSideSolid(x, y - 1, z, UP)) return 6;
        if (world.isSideSolid(x - 1, y, z, EAST)) return 1;
        if (world.isSideSolid(x + 1, y, z, WEST)) return 2;
        if (world.isSideSolid(x, y, z - 1, SOUTH)) return 3;
        if (world.isSideSolid(x, y, z + 1, NORTH)) return 4;
        if (world.isSideSolid(x, y + 1, z, UP)) return 5;
        return 1;
    }

    @Override
    public void onNeighborBlockChange(World world, int x, int y, int z, Block block) {
        if (this.isLocationStillValid(world, x, y, z)) {
            int l = world.getBlockMetadata(x, y, z) & 7;
            boolean flag = false;

            if (!world.isSideSolid(x, y - 1, z, UP) && l == 6) {
                flag = true;
            }

            if (!world.isSideSolid(x - 1, y, z, EAST) && l == 1) {
                flag = true;
            }

            if (!world.isSideSolid(x + 1, y, z, WEST) && l == 2) {
                flag = true;
            }

            if (!world.isSideSolid(x, y, z - 1, SOUTH) && l == 3) {
                flag = true;
            }

            if (!world.isSideSolid(x, y, z + 1, NORTH) && l == 4) {
                flag = true;
            }

            if (!world.isSideSolid(x, y + 1, z, DOWN) && l == 5) {
                flag = true;
            }

            if (flag) {
                this.dropBlockAsItem(world, x, y, z, world.getBlockMetadata(x, y, z), 0);
                world.setBlockToAir(x, y, z);
            }
        }
    }

    private boolean isLocationStillValid(World world, int x, int y, int z) {
        if (!this.canPlaceBlockAt(world, x, y, z)) {
            this.dropBlockAsItem(world, x, y, z, world.getBlockMetadata(x, y, z), 0);
            world.setBlockToAir(x, y, z);
            return false;
        } else {
            return true;
        }
    }

    @Override
    public boolean canProvidePower() {
        return true;
    }

    @Override
    public boolean isOpaqueCube() {
        return false;
    }

    public void breakBlock(World world, int x, int y, int z, Block block, int meta) {
        if (meta > 0) {
            int i1 = meta;
            this.updateBlocks(world, x, y, z, i1);
        }

        super.breakBlock(world, x, y, z, block, meta);
    }

    private void updateBlocks(World world, int x, int y, int z, int meta) {
        world.notifyBlocksOfNeighborChange(x, y, z, this);

        if (meta == 1) {
            world.notifyBlocksOfNeighborChange(x - 1, y, z, this);
        } else if (meta == 2) {
            world.notifyBlocksOfNeighborChange(x + 1, y, z, this);
        } else if (meta == 3) {
            world.notifyBlocksOfNeighborChange(x, y, z - 1, this);
        } else if (meta == 4) {
            world.notifyBlocksOfNeighborChange(x, y, z + 1, this);
        } else if (meta == 5) {
            world.notifyBlocksOfNeighborChange(x, y + 1, z, this);
        } else if (meta == 6) {
            world.notifyBlocksOfNeighborChange(x, y - 1, z, this);
        } else {
            world.notifyBlocksOfNeighborChange(x, y - 1, z, this);
        }
    }

    // #################################LOGIC##################################//
    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float p_149727_7_,
            float p_149727_8_, float p_149727_9_) {
        TilePotentiometer tile = (TilePotentiometer) world.getTileEntity(x, y, z);

        if (tile != null && tile instanceof TilePotentiometer) {
            if (!player.isSneaking()) ((TilePotentiometer) tile).increasePower();
            else((TilePotentiometer) tile).decreasePower();

            if (world.isRemote)
                player.addChatMessage(new ChatComponentText(String.valueOf(((TilePotentiometer) tile).power)));
            world.markBlockRangeForRenderUpdate(x, y, z, x, y, z);
        } else System.out.println("Invalid tile");
        return true;
    }

    @Override
    public int isProvidingWeakPower(IBlockAccess world, int x, int y, int z, int side) {
        TileEntity tile = world.getTileEntity(x, y, z);
        if (tile != null && tile instanceof TilePotentiometer) return ((TilePotentiometer) tile).power;
        else return 0;
    }

    @Override
    public int isProvidingStrongPower(IBlockAccess world, int x, int y, int z, int side) {
        TileEntity tile = world.getTileEntity(x, y, z);
        if (tile != null && tile instanceof TilePotentiometer) return ((TilePotentiometer) tile).power;
        else return 0;
    }
}
