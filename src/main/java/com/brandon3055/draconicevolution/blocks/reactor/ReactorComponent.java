package com.brandon3055.draconicevolution.blocks.reactor;

import com.brandon3055.brandonscore.blocks.BlockBCore;
import com.brandon3055.brandonscore.config.Feature;
import com.brandon3055.brandonscore.config.ICustomRender;
import com.brandon3055.brandonscore.config.IRegisterMyOwnTiles;
import com.brandon3055.draconicevolution.blocks.reactor.tileentity.TileReactorComponent;
import com.brandon3055.draconicevolution.blocks.reactor.tileentity.TileReactorEnergyInjector;
import com.brandon3055.draconicevolution.blocks.reactor.tileentity.TileReactorStabilizer;
import com.brandon3055.draconicevolution.client.render.tile.RenderTileReactorComponent;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;

/**
 * Created by brandon3055 on 18/01/2017.
 */
public class ReactorComponent extends BlockBCore implements ITileEntityProvider, IRegisterMyOwnTiles, ICustomRender {

    public ReactorComponent() {

    }

    //region Block & Registry

    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return meta == 0 ? new TileReactorStabilizer() : new TileReactorEnergyInjector();
    }

    @Override
    public void registerTiles(String modidPrefix, String blockName) {
        GameRegistry.registerTileEntity(TileReactorStabilizer.class, blockName + "_stabilizer");
        GameRegistry.registerTileEntity(TileReactorEnergyInjector.class, blockName + "_injector");
    }

    //endregion

    //region Rendering

    @SideOnly(Side.CLIENT)
    @Override
    public void registerRenderer(Feature feature) {
        ClientRegistry.bindTileEntitySpecialRenderer(TileReactorStabilizer.class, new RenderTileReactorComponent());
        ClientRegistry.bindTileEntitySpecialRenderer(TileReactorEnergyInjector.class, new RenderTileReactorComponent());
    }

    @Override
    public boolean registerNormal(Feature feature) {
        return false;
    }

    @Override
    public EnumBlockRenderType getRenderType(IBlockState state) {
        return EnumBlockRenderType.INVISIBLE;
    }

    //Endregion

    //region Place & interact

    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
        super.onBlockPlacedBy(world, pos, state, placer, stack);
        TileEntity te = world.getTileEntity(pos);

        if (te instanceof TileReactorComponent) {
            ((TileReactorComponent) te).onPlaced();
        }
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, @Nullable ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ) {
        TileEntity te = world.getTileEntity(pos);

        if (te instanceof TileReactorComponent) {
            ((TileReactorComponent) te).onActivated(playerIn);
        }
        return true;
    }

    @Override
    public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
        TileEntity te = worldIn.getTileEntity(pos);

        if (te instanceof TileReactorComponent) {
            ((TileReactorComponent) te).onBroken();
        }
        super.breakBlock(worldIn, pos, state);
    }

    //endregion



}
