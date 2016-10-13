package com.brandon3055.draconicevolution.blocks;

import com.brandon3055.brandonscore.blocks.BlockBCore;
import com.brandon3055.draconicevolution.blocks.tileentity.TileCreativeRFCapacitor;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

/**
 * Created by brandon3055 on 19/07/2016.
 */
public class CreativeRFSource extends BlockBCore implements ITileEntityProvider {

    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new TileCreativeRFCapacitor();
    }
}
