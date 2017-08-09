package com.brandon3055.draconicevolution.blocks;

import com.brandon3055.brandonscore.blocks.BlockBCore;
import com.brandon3055.brandonscore.config.Feature;
import com.brandon3055.brandonscore.config.ICustomRender;
import com.brandon3055.draconicevolution.blocks.tileentity.TileDislocatorPedestal;
import com.brandon3055.draconicevolution.client.render.tile.RenderTileDislocatorPedestal;
import com.brandon3055.draconicevolution.utils.LogHelper;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;

/**
 * Created by brandon3055 on 25/09/2016.
 */
public class DislocatorPedestal extends BlockBCore implements ITileEntityProvider, ICustomRender {

    public DislocatorPedestal() {}

    @Override
    public boolean uberIsBlockFullCube() {
        return false;
    }

    @Override
    public IBlockState onBlockPlaced(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
        TileEntity tile = worldIn.getTileEntity(pos);
        if (tile instanceof TileDislocatorPedestal) {
            ((TileDislocatorPedestal) tile).rotation.value = (int)(-(placer.rotationYawHead + 180 + 11.25) / 22.5F);
            ((TileDislocatorPedestal) tile).detectAndSendChanges(true);
        }

        return super.onBlockPlaced(worldIn, pos, facing, hitX, hitY, hitZ, meta, placer);
    }

    @Override
    public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack)
    {
        TileEntity tile = worldIn.getTileEntity(pos);
        if (tile instanceof TileDislocatorPedestal) {
            float f = (float) MathHelper.floor_float((MathHelper.wrapDegrees(placer.rotationYaw - 180.0F) + 11.25F) / 22.5F);
            LogHelper.dev(f);
            ((TileDislocatorPedestal) tile).rotation.value = (int) f;
            ((TileDislocatorPedestal) tile).detectAndSendChanges(true);
        }
        super.onBlockPlacedBy(worldIn, pos, state, placer, stack);
    }

    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new TileDislocatorPedestal();
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, @Nullable ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ) {
        TileEntity tileEntity = worldIn.getTileEntity(pos);

        if (tileEntity instanceof TileDislocatorPedestal) {
            return ((TileDislocatorPedestal) tileEntity).onBlockActivated(playerIn);
        }

        return super.onBlockActivated(worldIn, pos, state, playerIn, hand, heldItem, side, hitX, hitY, hitZ);
    }

    //region Render

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        return new AxisAlignedBB(0.35f, 0f, 0.35f, 0.65f, 0.8f, 0.65f);
    }

    @Override
    public EnumBlockRenderType getRenderType(IBlockState state) {
        return EnumBlockRenderType.INVISIBLE;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void registerRenderer(Feature feature) {
        ClientRegistry.bindTileEntitySpecialRenderer(TileDislocatorPedestal.class, new RenderTileDislocatorPedestal());
    }

    @Override
    public boolean registerNormal(Feature feature) {
        return true;
    }

    //endregion
}
