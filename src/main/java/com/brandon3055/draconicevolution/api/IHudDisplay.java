package com.brandon3055.draconicevolution.api;

import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Created by brandon3055 on 4/07/2016.
 */
public interface IHudDisplay {
    /**
     * Warning this is client side! Remember to keep that in mind when implementing and don't forget the @SideOnly
     *
     * @param stack Will be the stack the player is holding if this interface is implemented by an item.
     * @param world The players world.
     * @param pos The position of the block if this interface is implemented by a block.
     * @param displayList The list to which display data should be added.
     */
    @SideOnly(Side.CLIENT)
    void addDisplayData(@Nullable ItemStack stack, World world, @Nullable BlockPos pos, List<String> displayList);
}
