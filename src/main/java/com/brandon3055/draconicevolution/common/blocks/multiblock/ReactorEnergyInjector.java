package com.brandon3055.draconicevolution.common.blocks.multiblock;

import com.brandon3055.brandonscore.common.utills.Utills;
import com.brandon3055.draconicevolution.DraconicEvolution;
import com.brandon3055.draconicevolution.common.ModBlocks;
import com.brandon3055.draconicevolution.common.blocks.BlockDE;
import com.brandon3055.draconicevolution.common.lib.References;
import com.brandon3055.draconicevolution.common.tileentities.multiblocktiles.reactor.TileReactorCore;
import com.brandon3055.draconicevolution.common.tileentities.multiblocktiles.reactor.TileReactorEnergyInjector;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ChatComponentText;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

/**
 * Created by Brandon on 23/7/2015.
 */
public class ReactorEnergyInjector extends BlockDE {
    public ReactorEnergyInjector() {
        this.setCreativeTab(DraconicEvolution.tabBlocksItems);
        this.setBlockName("reactorEnergyInjector");

        ModBlocks.register(this);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void registerBlockIcons(IIconRegister iconRegister) {
        blockIcon = iconRegister.registerIcon(References.RESOURCESPREFIX + "transparency");
    }

    @Override
    public void setBlockBoundsBasedOnState(IBlockAccess access, int x, int y, int z) {
        TileReactorEnergyInjector tile = access.getTileEntity(x, y, z) instanceof TileReactorEnergyInjector
                ? (TileReactorEnergyInjector) access.getTileEntity(x, y, z)
                : null;
        if (tile != null) {
            switch (tile.facingDirection) {
                case 0:
                    this.setBlockBounds(0F, 0.885F, 0F, 1F, 1F, 1F);
                    break;
                case 1:
                    this.setBlockBounds(0F, 0F, 0F, 1F, 0.125F, 1F);
                    break;
                case 2:
                    this.setBlockBounds(0F, 0F, 0.885F, 1F, 1F, 1F);
                    break;
                case 3:
                    this.setBlockBounds(0F, 0F, 0F, 1F, 1F, 0.125F);
                    break;
                case 4:
                    this.setBlockBounds(0.885F, 0F, 0F, 1F, 1F, 1F);
                    break;
                case 5:
                    this.setBlockBounds(0F, 0F, 0F, 0.125F, 1F, 1F);
                    break;
            }
        }
        super.setBlockBoundsBasedOnState(access, x, y, z);
    }

    @Override
    public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z) {
        TileReactorEnergyInjector tile = world.getTileEntity(x, y, z) instanceof TileReactorEnergyInjector
                ? (TileReactorEnergyInjector) world.getTileEntity(x, y, z)
                : null;
        if (tile != null) {
            switch (tile.facingDirection) {
                case 0:
                    this.setBlockBounds(0F, 0.885F, 0F, 1F, 1F, 1F);
                    break;
                case 1:
                    this.setBlockBounds(0F, 0F, 0F, 1F, 0.125F, 1F);
                    break;
                case 2:
                    this.setBlockBounds(0F, 0F, 0.885F, 1F, 1F, 1F);
                    break;
                case 3:
                    this.setBlockBounds(0F, 0F, 0F, 1F, 1F, 0.125F);
                    break;
                case 4:
                    this.setBlockBounds(0.885F, 0F, 0F, 1F, 1F, 1F);
                    break;
                case 5:
                    this.setBlockBounds(0F, 0F, 0F, 0.125F, 1F, 1F);
                    break;
            }
        }
        return super.getCollisionBoundingBoxFromPool(world, x, y, z);
    }

    @Override
    public int getRenderType() {
        return -1;
    }

    @Override
    public boolean renderAsNormalBlock() {
        return false;
    }

    @Override
    public boolean isOpaqueCube() {
        return false;
    }

    @Override
    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase entity, ItemStack stack) {
        int d = Utills.determineOrientation(x, y, z, entity);
        TileReactorEnergyInjector tile = (TileReactorEnergyInjector) world.getTileEntity(x, y, z);
        tile.facingDirection = ForgeDirection.getOrientation(d).getOpposite().ordinal();
        tile.onPlaced();
    }

    @Override
    public boolean onBlockActivated(
            World world,
            int x,
            int y,
            int z,
            EntityPlayer player,
            int p_149727_6_,
            float p_149727_7_,
            float p_149727_8_,
            float p_149727_9_) {
        IReactorPart tile = world.getTileEntity(x, y, z) instanceof IReactorPart
                ? (IReactorPart) world.getTileEntity(x, y, z)
                : null;
        if (tile != null && player.isSneaking()) {
            tile.changeRedstoneMode();
            if (!world.isRemote) player.addChatComponentMessage(new ChatComponentText(tile.getRedstoneModeString()));
            return true;
        }
        return false;
    }

    public boolean hasComparatorInputOverride() {
        return true;
    }

    @Override
    public int getComparatorInputOverride(World world, int x, int y, int z, int p_149736_5_) {
        IReactorPart tile = world.getTileEntity(x, y, z) instanceof IReactorPart
                ? (IReactorPart) world.getTileEntity(x, y, z)
                : null;
        if (tile == null) return 0;
        TileReactorCore core = tile.getMaster().getTileEntity(world) instanceof TileReactorCore
                ? (TileReactorCore) tile.getMaster().getTileEntity(world)
                : null;
        if (core != null) return core.getComparatorOutput(tile.getRedstoneMode());
        return 0;
    }

    @Override
    public boolean hasTileEntity(int metadata) {
        return true;
    }

    @Override
    public TileEntity createTileEntity(World world, int metadata) {
        return new TileReactorEnergyInjector();
    }
}
