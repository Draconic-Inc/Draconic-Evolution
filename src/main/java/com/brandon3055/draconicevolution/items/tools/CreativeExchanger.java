package com.brandon3055.draconicevolution.items.tools;

import codechicken.lib.raytracer.RayTracer;
import com.brandon3055.brandonscore.items.ItemBCore;
import com.brandon3055.brandonscore.utils.ItemNBTHelper;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

import java.util.List;

/**
 * Created by brandon3055 on 19/07/2016.
 */
public class CreativeExchanger extends ItemBCore {

    @Override
    public boolean hasEffect(ItemStack stack) {
        return true;
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(ItemStack itemStack, World world, EntityPlayer player, EnumHand hand) {
        RayTraceResult traceResult = RayTracer.retrace(player);

        if (traceResult != null && traceResult.typeOfHit == RayTraceResult.Type.BLOCK) {
            return super.onItemRightClick(itemStack, world, player, hand);
        }

        if (world.isRemote){
            return super.onItemRightClick(itemStack, world, player, hand);
        }
        int range = ItemNBTHelper.getByte(itemStack, "Size", (byte)0);
        if (player.isSneaking()){
            range++;
            if (range > 10) {
                range = 0;
            }
            player.addChatComponentMessage(new TextComponentString("Range: " + (range * 2 + 1) +" x " + (range * 2 + 1)).setStyle(new Style().setColor(TextFormatting.AQUA)));
            ItemNBTHelper.setInteger(itemStack, "Size", range);
            return super.onItemRightClick(itemStack, world, player, hand);
        }

        return super.onItemRightClick(itemStack, world, player, hand);
    }

    @Override
    public EnumActionResult onItemUseFirst(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, EnumHand hand) {
        if (world.isRemote) {
            return EnumActionResult.PASS;
        }

        IBlockState prevState = world.getBlockState(pos);

        if (player.isSneaking()){
            String name = Block.REGISTRY.getNameForObject(prevState.getBlock()).toString();
            int data = prevState.getBlock().getMetaFromState(prevState);

            ItemNBTHelper.setString(stack, "BlockName", name);
            ItemNBTHelper.setByte(stack, "BlockData", (byte) data);

            player.addChatComponentMessage(new TextComponentString("Selected: " + new TextComponentTranslation(prevState.getBlock().getUnlocalizedName() + ".name").getFormattedText()).setStyle(new Style().setColor(TextFormatting.GREEN)));

            return EnumActionResult.SUCCESS;
        }
        else {
            Block block = Block.REGISTRY.getObject(new ResourceLocation(ItemNBTHelper.getString(stack, "BlockName", "")));
            if (block == Blocks.AIR){
                player.addChatComponentMessage(new TextComponentString("[ERROR-404] Set Block not Found").setStyle(new Style().setColor(TextFormatting.DARK_RED)));
                return EnumActionResult.SUCCESS;
            }

            IBlockState newState = block.getStateFromMeta(ItemNBTHelper.getByte(stack, "BlockData", (byte)0));
            int size = ItemNBTHelper.getByte(stack, "Size", (byte) 0);

            int xRange = 0;
            int yRange = 0;
            int zRange = 0;

            switch (side.getAxis()){
                case X:
                    zRange = size;
                    yRange = size;
                    break;
                case Y:
                    xRange = size;
                    zRange = size;
                    break;
                case Z:
                    xRange = size;
                    yRange = size;
                    break;
            }

            Iterable<BlockPos> blocks = BlockPos.getAllInBox(pos.add(-xRange, -yRange, -zRange), pos.add(xRange, yRange, zRange));

            for (BlockPos setPos : blocks) {
                if (world.isAirBlock(setPos)){
                    continue;
                }

                world.setBlockState(setPos, newState);
            }

            world.playEvent(2001, pos, Block.getStateId(newState));
        }


        return EnumActionResult.SUCCESS;
    }

    @Override
    public void addInformation(ItemStack stack, EntityPlayer playerIn, List<String> tooltip, boolean advanced) {
        tooltip.add("This item may not be completely stable.");
        tooltip.add("Please don't try anything stupid!");
        tooltip.add("Added for BTM may or may not stay");
    }
}
