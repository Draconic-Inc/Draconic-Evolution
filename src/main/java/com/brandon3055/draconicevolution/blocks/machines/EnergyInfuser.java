package com.brandon3055.draconicevolution.blocks.machines;

import com.brandon3055.brandonscore.blocks.BlockBCore;
import com.brandon3055.draconicevolution.blocks.tileentity.TileEnergyInfuser;
import net.minecraft.block.BlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockReader;

import javax.annotation.Nullable;

/**
 * Created by brandon3055 on 30/05/2016.
 */
public class EnergyInfuser extends BlockBCore/* implements ITileEntityProvider, IRenderOverride*/ {

    public EnergyInfuser(Properties properties) {
        super(properties);
    }

    @Override
    public boolean isBlockFullCube() {
        return false;
    }

    //region Block


    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new TileEnergyInfuser();
    }

//    @Override
//    public boolean onBlockActivated(World world, BlockPos pos, BlockState state, PlayerEntity player, Hand hand, Direction side, float hitX, float hitY, float hitZ) {
//        if (!world.isRemote) {
//            FMLNetworkHandler.openGui(player, DraconicEvolution.instance, GuiHandler.GUIID_ENERGY_INFUSER, world, pos.getX(), pos.getY(), pos.getZ());
//        }
//        return true;
//    }

    //endregion

    //region Rendering

//    @Override
//    public AxisAlignedBB getBoundingBox(BlockState state, IBlockAccess source, BlockPos pos) {
//        return new AxisAlignedBB(0, 0, 0, 1, 0.375, 1); Old shape will be irrelevant when i remodel
//    }
//
//    @OnlyIn(Dist.CLIENT)
//    @Override
//    public void registerRenderer(Feature feature) {
//        ClientRegistry.bindTileEntitySpecialRenderer(TileEnergyInfuser.class, new RenderTileEnergyInfuser());
//    }
//
//    @Override
//    public boolean registerNormal(Feature feature) {
//        return true;
//    }

    //endregion
}
