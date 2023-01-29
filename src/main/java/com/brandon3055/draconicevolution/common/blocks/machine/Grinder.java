package com.brandon3055.draconicevolution.common.blocks.machine;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import com.brandon3055.draconicevolution.DraconicEvolution;
import com.brandon3055.draconicevolution.client.gui.GuiHandler;
import com.brandon3055.draconicevolution.common.ModBlocks;
import com.brandon3055.draconicevolution.common.blocks.BlockCustomDrop;
import com.brandon3055.draconicevolution.common.lib.References;
import com.brandon3055.draconicevolution.common.lib.Strings;
import com.brandon3055.draconicevolution.common.tileentities.TileGrinder;

import cpw.mods.fml.common.network.internal.FMLNetworkHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class Grinder extends BlockCustomDrop {

    public IIcon icon_front;
    public IIcon icon_side;
    public IIcon icon_back;
    public IIcon icon_back_inactive;
    public IIcon icon_front_inactive;
    public IIcon icon_top[] = new IIcon[4];

    public Grinder() {
        super(Material.iron);
        this.setBlockName(Strings.grinderName);
        this.setCreativeTab(DraconicEvolution.tabBlocksItems);
        this.setStepSound(soundTypeStone);
        this.setResistance(2000.0f);
        ModBlocks.register(this);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(final IIconRegister iconRegister) {
        icon_front = iconRegister.registerIcon(References.RESOURCESPREFIX + "animated/grinder_front_active");
        icon_front_inactive = iconRegister.registerIcon(References.RESOURCESPREFIX + "grinder_front");
        icon_side = iconRegister.registerIcon(References.RESOURCESPREFIX + "machine_side");
        icon_back = iconRegister.registerIcon(References.RESOURCESPREFIX + "animated/machine_fan");
        icon_back_inactive = iconRegister.registerIcon(References.RESOURCESPREFIX + "machine_fan");
        for (int i = 0; i < 4; i++) {
            icon_top[i] = iconRegister.registerIcon(References.RESOURCESPREFIX + "machine_top_" + i);
        }
    }

    @Override
    public void onBlockExploded(World world, int x, int y, int z, Explosion explosion) {}

    @SideOnly(Side.CLIENT)
    @Override
    public void getSubBlocks(final Item item, final CreativeTabs tab, final List par3list) {
        par3list.add(new ItemStack(item, 1, 3));
    }

    @Override
    public int damageDropped(int p_149692_1_) {
        return 3;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(IBlockAccess world, int x, int y, int z, int side) {
        TileGrinder tile = (TileGrinder) world.getTileEntity(x, y, z);
        int meta = world.getBlockMetadata(x, y, z);

        IIcon back;
        IIcon front;

        if (!tile.disabled && tile.hasPower) {
            back = icon_back;
            front = icon_front;
        } else {
            back = icon_back_inactive;
            front = icon_front_inactive;
        }

        switch (side) {
            case 0:
                return icon_side;
            case 1:
                return icon_top[meta];
            case 2:
                if (meta == 0) return front;
                else if (meta == 2) return back;
                else return icon_side;
            case 3:
                if (meta == 2) return front;
                else if (meta == 0) return back;
                else return icon_side;
            case 4:
                if (meta == 3) return front;
                else if (meta == 1) return back;
                else return icon_side;
            case 5:
                if (meta == 1) return front;
                else if (meta == 3) return back;
                else return icon_side;
        }
        return null;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int side, int meta) {
        switch (side) {
            case 0:
                return icon_side;
            case 1:
                return icon_top[meta];
            case 2:
                if (meta == 0) return icon_front;
                else if (meta == 2) return icon_back;
                else return icon_side;
            case 3:
                if (meta == 2) return icon_front;
                else if (meta == 0) return icon_back;
                else return icon_side;
            case 4:
                if (meta == 3) return icon_front;
                else if (meta == 1) return icon_back;
                else return icon_side;
            case 5:
                if (meta == 1) return icon_front;
                else if (meta == 3) return icon_back;
                else return icon_side;
        }
        return null;
    }

    @Override
    public TileEntity createNewTileEntity(World var1, int var2) {
        return new TileGrinder();
    }

    @Override
    public boolean onBlockActivated(World world, int X, int Y, int Z, EntityPlayer player, int side, float prx,
            float pry, float prz) {
        TileEntity tile = world.getTileEntity(X, Y, Z);
        if (!(tile instanceof TileGrinder)) {
            return false;
        }
        if (!world.isRemote && !player.isSneaking()) {
            FMLNetworkHandler.openGui(player, DraconicEvolution.instance, GuiHandler.GUIID_GRINDER, world, X, Y, Z);
        } else if (player.isSneaking()) {
            for (double x = ((TileGrinder) tile).centreX - 4; x <= ((TileGrinder) tile).centreX + 4; x++) {
                for (double y = ((TileGrinder) tile).centreY - 4; y <= ((TileGrinder) tile).centreY + 4; y++) {
                    for (double z = ((TileGrinder) tile).centreZ - 4; z <= ((TileGrinder) tile).centreZ + 4; z++) {
                        world.spawnParticle("flame", x, y, z, 0D, 0D, 0D);
                    }
                }
            }
        }
        return true;
    }

    @Override
    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase placer, ItemStack stack) {
        int l = MathHelper.floor_double((double) (placer.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3;
        if (l == 0) {
            world.setBlockMetadataWithNotify(x, y, z, 0, 2);
        }

        if (l == 1) {
            world.setBlockMetadataWithNotify(x, y, z, 1, 2);
        }

        if (l == 2) {
            world.setBlockMetadataWithNotify(x, y, z, 2, 2);
        }

        if (l == 3) {
            world.setBlockMetadataWithNotify(x, y, z, 3, 2);
        }
    }

    @Override
    public void onNeighborBlockChange(World world, int x, int y, int z, Block block) {
        TileEntity tile = world.getTileEntity(x, y, z);
        if (tile instanceof TileGrinder) {
            ((TileGrinder) tile).disabled = world.isBlockIndirectlyGettingPowered(x, y, z);
            world.markBlockForUpdate(x, y, z);
        }
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
}
