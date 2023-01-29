package com.brandon3055.draconicevolution.common.blocks;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import com.brandon3055.draconicevolution.DraconicEvolution;
import com.brandon3055.draconicevolution.common.ModBlocks;
import com.brandon3055.draconicevolution.common.lib.References;
import com.brandon3055.draconicevolution.common.lib.Strings;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class RainSensor extends BlockDE {

    IIcon icon_inactive;
    IIcon icon_active;
    IIcon icon_side;

    public RainSensor() {
        super(Material.circuits);
        this.setCreativeTab(DraconicEvolution.tabBlocksItems);
        this.setBlockBounds(0, 0, 0, 1, 0.125F, 1);
        this.setHardness(0.3f);
        this.setResistance(0.5f);
        this.setTickRandomly(true);
        this.setBlockName(Strings.rainSensorName);
        ModBlocks.register(this);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister iconRegister) {
        icon_inactive = iconRegister.registerIcon(References.RESOURCESPREFIX + "rain_sensor_inactive");
        icon_active = iconRegister.registerIcon(References.RESOURCESPREFIX + "rain_sensor_active");
        icon_side = iconRegister.registerIcon(References.RESOURCESPREFIX + "rain_sensor_side");
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int face, int meta) {
        IIcon iconstate = (meta == 0 ? icon_inactive : icon_active);
        return face == 1 ? iconstate : icon_side;
    }

    @Override
    public boolean canProvidePower() {
        return true;
    }

    @Override
    public int tickRate(World world) {
        return 100;
    }

    @Override
    public boolean isOpaqueCube() {
        return false;
    }

    @Override
    public void updateTick(World world, int x, int y, int z, Random random) {
        if (world.canBlockSeeTheSky(x, y, z))
            world.setBlockMetadataWithNotify(x, y, z, world.getWorldInfo().isRaining() ? 1 : 0, 2);
        else world.setBlockMetadataWithNotify(x, y, z, 0, 2);
        world.scheduleBlockUpdate(x, y, z, this, 100);
        world.notifyBlocksOfNeighborChange(x, y, z, this);
    }

    @Override
    public boolean renderAsNormalBlock() {
        return false;
    }

    @Override
    public int isProvidingWeakPower(IBlockAccess world, int x, int y, int z, int meta) {
        return world.getBlockMetadata(x, y, z) == 1 ? 15 : 0;
    }

    @Override
    public void onNeighborBlockChange(World world, int x, int y, int z, Block p_149695_5_) {
        world.scheduleBlockUpdate(x, y, z, this, 100);
    }

    @Override
    public int onBlockPlaced(World world, int x, int y, int z, int p_149660_5_, float p_149660_6_, float p_149660_7_,
            float p_149660_8_, int p_149660_9_) {
        world.scheduleBlockUpdate(x, y, z, this, 10);
        return super.onBlockPlaced(world, x, y, z, p_149660_5_, p_149660_6_, p_149660_7_, p_149660_8_, p_149660_9_);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void randomDisplayTick(World world, int x, int y, int z, Random random) {
        int l = world.getBlockMetadata(x, y, z);
        int rand = random.nextInt(3);

        if (l == 1) {
            if (rand == 0) world.spawnParticle("reddust", x + 1 - 0.155, y + 0.12, z + 1 - 0.155, 0.5, 0.4, 1);
            if (rand == 1) world.spawnParticle("reddust", x + 1 - 0.155, y + 0.12, z + 1 - 0.155, 1, 0.2, 0.2);
            if (rand == 1) world.spawnParticle("reddust", x + 1 - 0.155, y + 0.12, z + 0.155, 0.5, 0.4, 1);
            if (rand == 2) world.spawnParticle("reddust", x + 1 - 0.155, y + 0.12, z + 0.155, 1, 0.2, 0.2);
            if (rand == 2) world.spawnParticle("reddust", x + 0.155, y + 0.12, z + 1 - 0.155, 0.5, 0.4, 1);
            if (rand == 3) world.spawnParticle("reddust", x + 0.155, y + 0.12, z + 1 - 0.155, 1, 0.2, 0.2);
            if (rand == 3) world.spawnParticle("reddust", x + 0.155, y + 0.12, z + 0.155, 0.5, 0.4, 1);
            if (rand == 0) world.spawnParticle("reddust", x + 0.155, y + 0.12, z + 0.155, 1, 0.2, 0.2);
        }
    }
}
