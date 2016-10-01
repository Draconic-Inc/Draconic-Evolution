package com.brandon3055.draconicevolution.blocks;

import com.brandon3055.brandonscore.blocks.BlockBCore;
import com.brandon3055.draconicevolution.blocks.tileentity.TileDraconiumChest;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

/**
 * Created by brandon3055 on 25/09/2016.
 */
public class DraconiumChest extends BlockBCore implements ITileEntityProvider {
    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new TileDraconiumChest();
    }
}
