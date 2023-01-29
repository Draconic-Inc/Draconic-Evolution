package com.brandon3055.draconicevolution.common.blocks;

import java.util.Random;

import net.minecraft.block.BlockFire;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import com.brandon3055.draconicevolution.common.ModBlocks;
import com.brandon3055.draconicevolution.common.lib.References;
import com.brandon3055.draconicevolution.common.lib.Strings;

/**
 * Created by Brandon on 23/08/2014.
 */
public class SafetyFlame extends BlockFire {

    public SafetyFlame() {
        super();
        this.setBlockName(Strings.safetyFlameName);
        this.setBlockTextureName("fire");
        this.setLightLevel(1F);
        ModBlocks.registerOther(this);
    }

    @Override
    public String getUnlocalizedName() {
        return String.format(
                "tile.%s%s",
                References.MODID.toLowerCase() + ":",
                getUnwrappedUnlocalizedName(super.getUnlocalizedName()));
    }

    public String getUnwrappedUnlocalizedName(String unlocalizedName) {
        return unlocalizedName.substring(unlocalizedName.indexOf(".") + 1);
    }

    @Override
    public void updateTick(World p_149674_1_, int p_149674_2_, int p_149674_3_, int p_149674_4_, Random p_149674_5_) {}

    @Override
    public void onNeighborChange(IBlockAccess world, int x, int y, int z, int tileX, int tileY, int tileZ) {
        super.onNeighborChange(world, x, y, z, tileX, tileY, tileZ);
    }

    @Override
    public void onBlockAdded(World world, int x, int y, int z) {
        super.onBlockAdded(world, x, y, z);
    }
}
