package com.brandon3055.draconicevolution.common.blocks.machine;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import com.brandon3055.draconicevolution.DraconicEvolution;
import com.brandon3055.draconicevolution.common.ModBlocks;
import com.brandon3055.draconicevolution.common.blocks.BlockDE;
import com.brandon3055.draconicevolution.common.lib.References;
import com.brandon3055.draconicevolution.common.lib.Strings;
import com.brandon3055.draconicevolution.common.tileentities.TilePlayerDetector;

public class PlayerDetector extends BlockDE {

    IIcon side_inactive;
    IIcon side_active;
    IIcon top;
    IIcon bottom;

    public PlayerDetector() {
        this.setBlockName(Strings.playerDetectorName);
        this.setCreativeTab(DraconicEvolution.tabBlocksItems);
        this.setStepSound(soundTypeStone);
        ModBlocks.register(this);
    }

    @Override
    public void registerBlockIcons(IIconRegister iconRegister) {
        side_inactive = iconRegister.registerIcon(References.RESOURCESPREFIX + "player_detector_side_inactive");
        side_active = iconRegister.registerIcon(References.RESOURCESPREFIX + "player_detector_side_active");
        top = iconRegister.registerIcon(References.RESOURCESPREFIX + "machine_top_0");
        bottom = iconRegister.registerIcon(References.RESOURCESPREFIX + "machine_side");
    }

    public IIcon getIcon(IBlockAccess world, int x, int y, int z, int side) {
        IIcon side_icon;
        TileEntity tile = world.getTileEntity(x, y, z);
        if (tile != null && tile instanceof TilePlayerDetector && ((TilePlayerDetector) tile).output)
            side_icon = side_active;
        else side_icon = side_inactive;

        if (side == 0) return bottom;
        else if (side == 1) return top;
        else return side_icon;
    }

    @Override
    public IIcon getIcon(int side, int meta) {
        if (side == 0) return bottom;
        else if (side == 1) return top;
        else return side_active;
    }

    @Override
    public boolean isBlockSolid(IBlockAccess p_149747_1_, int p_149747_2_, int p_149747_3_, int p_149747_4_,
            int p_149747_5_) {
        return true;
    }

    @Override
    public boolean isSideSolid(IBlockAccess world, int x, int y, int z, ForgeDirection side) {
        return true;
    }

    @Override
    public boolean hasTileEntity(int meta) {
        return true;
    }

    @Override
    public TileEntity createTileEntity(World world, int metadata) {
        return new TilePlayerDetector();
    }

    @Override
    public boolean canConnectRedstone(IBlockAccess world, int x, int y, int z, int side) {
        if (side == 0 || side == 1) return false;
        else return true;
    }

    @Override
    public boolean canProvidePower() {
        return true;
    }

    @Override
    public int isProvidingWeakPower(IBlockAccess world, int x, int y, int z, int meta) {
        TileEntity te = world.getTileEntity(x, y, z);
        TilePlayerDetector detector = (te != null && te instanceof TilePlayerDetector) ? (TilePlayerDetector) te : null;
        if (detector != null) return detector.output ? 15 : 0;
        else return 0;
    }

    @Override
    public int isProvidingStrongPower(IBlockAccess world, int x, int y, int z, int meta) {
        TileEntity te = world.getTileEntity(x, y, z);
        TilePlayerDetector detector = (te != null && te instanceof TilePlayerDetector) ? (TilePlayerDetector) te : null;
        if (detector != null) return detector.output ? 15 : 0;
        else return 0;
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float p_149727_7_,
            float p_149727_8_, float p_149727_9_) {
        TileEntity te = world.getTileEntity(x, y, z);
        TilePlayerDetector detector = (te != null && te instanceof TilePlayerDetector) ? (TilePlayerDetector) te : null;
        if (detector != null) {
            int range = detector.getRange();

            if (player.isSneaking()) {
                range--;
            } else {
                range++;
            }

            if (range > 10) range = 1;
            if (range < 1) range = 10;
            detector.setRange(range);

            if (world.isRemote) player.addChatMessage(
                    new ChatComponentTranslation("msg.range.txt").appendSibling(new ChatComponentText(" " + range)));
        }
        return true;
    }

    @Override
    public void breakBlock(World world, int x, int y, int z, Block block, int meta) {
        super.breakBlock(world, x, y, z, block, meta);

        world.notifyBlocksOfNeighborChange(x - 1, y, z, world.getBlock(x, y, z));
        world.notifyBlocksOfNeighborChange(x + 1, y, z, world.getBlock(x, y, z));
        world.notifyBlocksOfNeighborChange(x, y - 1, z, world.getBlock(x, y, z));
        world.notifyBlocksOfNeighborChange(x, y + 1, z, world.getBlock(x, y, z));
        world.notifyBlocksOfNeighborChange(x, y, z - 1, world.getBlock(x, y, z));
        world.notifyBlocksOfNeighborChange(x, y, z + 1, world.getBlock(x, y, z));
    }
}
