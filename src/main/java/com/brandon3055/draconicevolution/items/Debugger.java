package com.brandon3055.draconicevolution.items;

import com.brandon3055.brandonscore.items.ItemBCore;
import com.brandon3055.brandonscore.utills.SimplexNoise;
import com.brandon3055.draconicevolution.utills.LogHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * Created by brandon3055 on 4/4/2016.
 */
public class Debugger extends ItemBCore {

    //todo remove from tab and NEI

    @Override
    public boolean hasEffect(ItemStack stack) {
        return true;
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(ItemStack itemStack, World world, EntityPlayer player, EnumHand hand) {
        if (world.isRemote) return super.onItemRightClick(itemStack, world, player, hand);


        LogHelper.info("Clic");
        int posX = (int)player.posX;
        int posY = (int)player.posY;
        int posZ = (int)player.posZ;
        int size = 500;


        for (int x = posX; x < posX + size; x++){
            double perc = (double)(x - posX) / (double)(size);
            LogHelper.info(perc * 100D+"-Percent");
            for (int z = posZ; z < posZ + size; z++){
                for (int y = posY; y < 255; y++){
                    BlockPos pos = new BlockPos(x, y, z);

                    double noise = 0;

                    for (int octave = 1; octave < 5; octave++) {
                        double d = octave * 0.007D;
                        noise += SimplexNoise.noise(x * d, y * d, z * d);
                    }



                    //noise = Math.abs(noise);

                    //LogHelper.info(noise);
                    double d = 0.05D;
                    if (noise > SimplexNoise.noise(y * d, x * d, z * d)) {
                        world.setBlockState(pos, Blocks.stone.getDefaultState());
                    }
                    else {
                       // world.setBlockToAir(pos);
                    }
                }
            }
        }



//        for (int x = posX; x < posX + size; x++){
//            for (int y = posY; y < posY + size; y++){
//                for (int z = posZ; z < posZ + size; z++){
//                    BlockPos pos = new BlockPos(x, y, z);
//
//                    double dist = Utills.getDistanceAtoB(x - posX, y - posY, z - posZ, size / 2, size / 2, size / 2);
//
//                    dist /= size;
//
//                   // dist = 1 - dist;
//
//                   //LogHelper.info(dist);
//
//                    double noise = 0;
//
//                    for (int octave = 1; octave < 2; octave++) {
//                        double d = octave * 0.5D;
//                        noise += SimplexNoise.noise(x * d, y * d, z * d) - (dist * 2);
//                    }
//
//
//
//                    //noise = Math.abs(noise);
//
//                    //LogHelper.info(noise);
//
//                    if (noise > 0) {
//                       world.setBlockState(pos, Blocks.stone.getDefaultState());
//                    }
//                    else {
//                       world.setBlockToAir(pos);
//                    }
//                }
//            }
//        }
//
//        LogHelper.info("Done!");
//
//
//
//
//
//        for (int x = posX; x < posX + size; x++){
//            for (int y = posY; y < posY + size; y++){
//                for (int z = posZ; z < posZ + size; z++){
//                    BlockPos pos = new BlockPos(x, y, z);
//
//                    double noise = 0;
//
//                    for (int octave = 1; octave < 5; octave++) {
//                        double d = octave * 0.005D;
//                        noise += SimplexNoise.noise(x * d, y * d, z * d);
//                    }
//
//
//
//                    //noise = Math.abs(noise);
//
//                    //LogHelper.info(noise);
//
//                    if (noise > 0) {
//                        world.setBlockState(pos, Blocks.stone.getDefaultState());
//                    }
//                    else {
//                        world.setBlockToAir(pos);
//                    }
//                }
//            }
//        }


        return super.onItemRightClick(itemStack, world, player, hand);
    }
}


//int drain = Integer.MAX_VALUE;
//long ticks = Long.MAX_VALUE / drain;
//long seconds = ticks / 20;
//long minutes = seconds / 60;
//long hours = minutes / 60;
//long days = hours / 24;
//double years = days / 365D;
//int centuries = (int)years / 100;
//LogHelper.info(centuries+" "+years+" "+Integer.MAX_VALUE);