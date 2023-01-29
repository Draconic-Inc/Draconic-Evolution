package com.brandon3055.draconicevolution.common.blocks.multiblock;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentText;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import com.brandon3055.brandonscore.common.utills.Utills;
import com.brandon3055.draconicevolution.DraconicEvolution;
import com.brandon3055.draconicevolution.common.ModBlocks;
import com.brandon3055.draconicevolution.common.blocks.BlockDE;
import com.brandon3055.draconicevolution.common.lib.References;
import com.brandon3055.draconicevolution.common.tileentities.multiblocktiles.reactor.TileReactorCore;
import com.brandon3055.draconicevolution.common.tileentities.multiblocktiles.reactor.TileReactorStabilizer;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/**
 * Created by Brandon on 5/7/2015.
 */
public class ReactorStabilizer extends BlockDE {

    public ReactorStabilizer() {
        this.setCreativeTab(DraconicEvolution.tabBlocksItems);
        this.setBlockName("reactorStabilizer");
        ModBlocks.register(this);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void registerBlockIcons(IIconRegister iconRegister) {
        blockIcon = iconRegister.registerIcon(References.RESOURCESPREFIX + "transparency");
    }

    @Override
    public int getRenderType() {
        return -1;
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int p_149727_6_,
            float p_149727_7_, float p_149727_8_, float p_149727_9_) {
        if (!player.isSneaking()) {
            TileReactorStabilizer tile = world.getTileEntity(x, y, z) instanceof TileReactorStabilizer
                    ? (TileReactorStabilizer) world.getTileEntity(x, y, z)
                    : null;
            TileEntity core = null;
            if (tile != null) core = tile.getMaster().getTileEntity(world);
            if (core instanceof TileReactorCore) {
                ((TileReactorCore) core).onStructureRightClicked(player);
                return true;
            }
        } else if (!world.isRemote) {
            IReactorPart tile = world.getTileEntity(x, y, z) instanceof IReactorPart
                    ? (IReactorPart) world.getTileEntity(x, y, z)
                    : null;
            if (tile != null) {
                tile.changeRedstoneMode();
                if (!world.isRemote)
                    player.addChatComponentMessage(new ChatComponentText(tile.getRedstoneModeString()));
                return true;
            }
        }
        return false;
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
    public boolean hasTileEntity(int metadata) {
        return true;
    }

    @Override
    public TileEntity createTileEntity(World world, int metadata) {
        return new TileReactorStabilizer();
    }

    @Override
    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase entity, ItemStack stack) {
        int d = Utills.determineOrientation(x, y, z, entity);
        TileReactorStabilizer tile = world.getTileEntity(x, y, z) instanceof TileReactorStabilizer
                ? (TileReactorStabilizer) world.getTileEntity(x, y, z)
                : null;
        if (tile != null) {
            if (entity.isSneaking()) tile.facingDirection = ForgeDirection.getOrientation(d).getOpposite().ordinal();
            else tile.facingDirection = ForgeDirection.getOrientation(d).ordinal();
            tile.onPlaced();
        }
    }

    @Override
    public void breakBlock(World world, int x, int y, int z, Block p_149749_5_, int p_149749_6_) {
        TileReactorStabilizer tile = world.getTileEntity(x, y, z) instanceof TileReactorStabilizer
                ? (TileReactorStabilizer) world.getTileEntity(x, y, z)
                : null;
        TileEntity core = null;
        if (tile != null) core = tile.getMaster().getTileEntity(world);
        super.breakBlock(world, x, y, z, p_149749_5_, p_149749_6_);
        if (core instanceof TileReactorCore) ((TileReactorCore) core).validateStructure();
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
}
