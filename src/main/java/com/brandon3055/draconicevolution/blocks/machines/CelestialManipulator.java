package com.brandon3055.draconicevolution.blocks.machines;

import com.brandon3055.brandonscore.blocks.BlockBCore;
import com.brandon3055.draconicevolution.blocks.tileentity.TileCelestialManipulator;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

/**
 * Created by brandon3055 on 25/09/2016.
 */
public class CelestialManipulator extends BlockBCore implements ITileEntityProvider {//Replacement For both the weather controller and sun dial
    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new TileCelestialManipulator();
    }
}
