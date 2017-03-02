package com.brandon3055.draconicevolution.blocks.machines;

import com.brandon3055.brandonscore.blocks.BlockBCore;
import com.brandon3055.brandonscore.config.Feature;
import com.brandon3055.brandonscore.config.ICustomRender;
import com.brandon3055.draconicevolution.DraconicEvolution;
import com.brandon3055.draconicevolution.GuiHandler;
import com.brandon3055.draconicevolution.blocks.tileentity.TileFusionCraftingCore;
import com.brandon3055.draconicevolution.client.render.tile.RenderTileFusionCraftingCore;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.network.internal.FMLNetworkHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;

/**
 * Created by brandon3055 on 11/06/2016.
 */
public class FusionCraftingCore extends BlockBCore implements ICustomRender, ITileEntityProvider {

    public FusionCraftingCore(){
        super(Material.IRON);
        setIsFullCube(false);
    }

    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new TileFusionCraftingCore();
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, @Nullable ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ) {
        TileEntity tile = world.getTileEntity(pos);

        if (tile instanceof TileFusionCraftingCore){
            ((TileFusionCraftingCore) tile).updatePedestals();
        }

        if (!world.isRemote) {
            FMLNetworkHandler.openGui(player, DraconicEvolution.instance, GuiHandler.GUIID_FUSION_CRAFTING, world, pos.getX(), pos.getY(), pos.getZ());
        }
        return true;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void registerRenderer(Feature feature) {
        ClientRegistry.bindTileEntitySpecialRenderer(TileFusionCraftingCore.class, new RenderTileFusionCraftingCore());
    }

    @Override
    public boolean registerNormal(Feature feature) {
        return true;
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        return new AxisAlignedBB(0.0625, 0.0625, 0.0625, 0.9375, 0.9375, 0.9375);
    }

    public void neighborChanged(IBlockState state, World world, BlockPos pos, Block blockIn)
    {
        if (!world.isRemote)
        {
            if (world.isBlockPowered(pos))
            {
                TileEntity tile = world.getTileEntity(pos);
                if (tile instanceof TileFusionCraftingCore){
                    ((TileFusionCraftingCore) tile).attemptStartCrafting();
                }
            }
        }
    }

    @Override
    public boolean hasComparatorInputOverride(IBlockState state) {
        return true;
    }

    @Override
    public int getComparatorInputOverride(IBlockState blockState, World worldIn, BlockPos pos) {
        TileEntity tile = worldIn.getTileEntity(pos);
        if (tile instanceof TileFusionCraftingCore) {
            return ((TileFusionCraftingCore) tile).getComparatorOutput();
        }
        return super.getComparatorInputOverride(blockState, worldIn, pos);
    }
}
