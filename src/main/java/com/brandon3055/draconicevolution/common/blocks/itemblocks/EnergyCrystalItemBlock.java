package com.brandon3055.draconicevolution.common.blocks.itemblocks;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import com.brandon3055.draconicevolution.common.tileentities.energynet.TileEnergyTransceiver;

/**
 * Created by Brandon on 10/02/2015.
 */
public class EnergyCrystalItemBlock extends ItemBlock {

    public EnergyCrystalItemBlock(Block block) {
        super(block);
        this.setHasSubtypes(true);
    }

    @Override
    public int getMetadata(int par1) {
        return par1;
    }

    @Override
    public String getUnlocalizedName(ItemStack stack) {
        return super.getUnlocalizedName(stack) + stack.getItemDamage();
    }

    @Override
    public boolean placeBlockAt(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side,
            float hitX, float hitY, float hitZ, int metadata) {
        boolean b = super.placeBlockAt(stack, player, world, x, y, z, side, hitX, hitY, hitZ, metadata);
        TileEnergyTransceiver transceiver = world.getTileEntity(x, y, z) instanceof TileEnergyTransceiver
                ? (TileEnergyTransceiver) world.getTileEntity(x, y, z)
                : null;
        if (transceiver != null) transceiver.facing = side;
        return b;
    }
}
