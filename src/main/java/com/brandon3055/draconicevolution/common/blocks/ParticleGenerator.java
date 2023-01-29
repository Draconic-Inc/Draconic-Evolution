package com.brandon3055.draconicevolution.common.blocks;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import com.brandon3055.draconicevolution.DraconicEvolution;
import com.brandon3055.draconicevolution.client.gui.GuiHandler;
import com.brandon3055.draconicevolution.client.handler.ParticleHandler;
import com.brandon3055.draconicevolution.client.render.particle.ParticleCustom;
import com.brandon3055.draconicevolution.common.ModBlocks;
import com.brandon3055.draconicevolution.common.lib.References;
import com.brandon3055.draconicevolution.common.lib.Strings;
import com.brandon3055.draconicevolution.common.tileentities.TileParticleGenerator;
import com.brandon3055.draconicevolution.common.tileentities.multiblocktiles.TileEnergyStorageCore;

import cpw.mods.fml.common.network.internal.FMLNetworkHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ParticleGenerator extends BlockDE {

    public static Block instance;

    public ParticleGenerator() {
        this.setBlockName(Strings.particleGeneratorName);
        this.setCreativeTab(DraconicEvolution.tabBlocksItems);
        this.setStepSound(soundTypeStone);
        this.setLightOpacity(0);
        ModBlocks.register(this);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void registerBlockIcons(IIconRegister iconRegister) {
        blockIcon = iconRegister.registerIcon(References.RESOURCESPREFIX + "machine_side");
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float p_149727_7_,
            float p_149727_8_, float p_149727_9_) {
        if (world.getBlockMetadata(x, y, z) == 1) return false;

        if (player.getHeldItem() != null && player.getHeldItem().getItem() == Items.paper) {
            TileEntity tile = world.getTileEntity(x, y, z);
            TileParticleGenerator gen = (tile != null && tile instanceof TileParticleGenerator)
                    ? (TileParticleGenerator) tile
                    : null;
            ItemStack stack = player.getHeldItem();
            if (gen != null && stack.hasTagCompound() && stack.getTagCompound().hasKey("particles_enabled")) {
                gen.setBlockNBT(stack.getTagCompound());
                return true;
            }
        }

        if (player.isSneaking()) {
            if (activateEnergyStorageCore(world, x, y, z, player)) return true;
            TileEntity tile = world.getTileEntity(x, y, z);
            TileParticleGenerator gen = (tile != null && tile instanceof TileParticleGenerator)
                    ? (TileParticleGenerator) tile
                    : null;
            if (gen != null) {
                gen.toggleInverted();
            }
        } else
            FMLNetworkHandler.openGui(player, DraconicEvolution.instance, GuiHandler.GUIID_PARTICLEGEN, world, x, y, z);
        return true;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public int getRenderType() {
        return -1;
    }

    @Override
    public void onNeighborBlockChange(final World world, final int x, final int y, final int z, final Block block) {
        TileEntity tile = world.getTileEntity(x, y, z);
        TileParticleGenerator gen = (tile != null && tile instanceof TileParticleGenerator)
                ? (TileParticleGenerator) tile
                : null;
        if (gen != null) {
            gen.signal = world.isBlockIndirectlyGettingPowered(x, y, z);
            world.markBlockForUpdate(x, y, z);
        }
    }

    @Override
    public boolean canConnectRedstone(IBlockAccess world, int x, int y, int z, int side) {
        return true;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void onBlockDestroyedByPlayer(World world, int x, int y, int z, int meta) {
        if (world.isRemote) {
            Random rand = world.rand;
            float modifier = 0.1F;
            float SCALE = 1;
            double spawnX = x + 0.5D;
            double spawnY = y + 0.5D;
            double spawnZ = z + 0.5D;

            for (int i = 0; i < 100; i++) {
                float MX = modifier - ((2f * modifier) * rand.nextFloat());
                float MY = modifier - ((2f * modifier) * rand.nextFloat());
                float MZ = modifier - ((2f * modifier) * rand.nextFloat());

                {
                    ParticleCustom particle = new ParticleCustom(
                            world,
                            spawnX,
                            spawnY,
                            spawnZ,
                            MX,
                            MY,
                            MZ,
                            SCALE,
                            false,
                            1);
                    particle.red = rand.nextInt(255);
                    particle.green = rand.nextInt(255);
                    particle.blue = rand.nextInt(255);
                    particle.maxAge = rand.nextInt(10);
                    particle.fadeTime = 20;
                    particle.fadeLength = 20;
                    particle.gravity = 0F;

                    ParticleHandler.spawnCustomParticle(particle);
                }
            }
        }
    }

    @Override
    public boolean renderAsNormalBlock() {
        return false;
    }

    @Override
    public boolean shouldSideBeRendered(IBlockAccess p_149646_1_, int p_149646_2_, int p_149646_3_, int p_149646_4_,
            int p_149646_5_) {
        return false;
    }

    @Override
    public boolean isOpaqueCube() {
        return false;
    }

    @Override
    public boolean hasTileEntity(int meta) {
        return true;
    }

    @Override
    public TileEntity createTileEntity(World world, int metadata) {
        return new TileParticleGenerator();
    }

    private boolean activateEnergyStorageCore(World world, int x, int y, int z, EntityPlayer player) {
        for (int x1 = x - 11; x1 <= x + 11; x1++) {
            if (world.getBlock(x1, y, z) == ModBlocks.energyStorageCore) {
                TileEnergyStorageCore tile = (world.getTileEntity(x1, y, z) != null
                        && world.getTileEntity(x1, y, z) instanceof TileEnergyStorageCore)
                                ? (TileEnergyStorageCore) world.getTileEntity(x1, y, z)
                                : null;
                if (tile != null && !tile.isOnline()) {
                    if (player.capabilities.isCreativeMode) {
                        if (!tile.creativeActivate()) {
                            if (world.isRemote) player.addChatComponentMessage(
                                    new ChatComponentTranslation("msg.energyStorageCoreUTA.txt"));
                            return false;
                        }
                    } else {
                        if (!tile.tryActivate()) {
                            if (world.isRemote) player.addChatComponentMessage(
                                    new ChatComponentTranslation("msg.energyStorageCoreUTA.txt"));
                            return false;
                        }
                    }
                    return true;
                }
            }
        }

        for (int z1 = z - 11; z1 <= z + 11; z1++) {
            if (world.getBlock(x, y, z1) == ModBlocks.energyStorageCore) {
                TileEnergyStorageCore tile = (world.getTileEntity(x, y, z1) != null
                        && world.getTileEntity(x, y, z1) instanceof TileEnergyStorageCore)
                                ? (TileEnergyStorageCore) world.getTileEntity(x, y, z1)
                                : null;
                if (tile != null && !tile.isOnline()) {
                    if (player.capabilities.isCreativeMode) {
                        if (!tile.creativeActivate()) {
                            if (world.isRemote) player.addChatComponentMessage(
                                    new ChatComponentTranslation("msg.energyStorageCoreUTA.txt"));
                            return false;
                        }
                    } else {
                        if (!tile.tryActivate()) {
                            if (world.isRemote) player.addChatComponentMessage(
                                    new ChatComponentTranslation("msg.energyStorageCoreUTA.txt"));
                            return false;
                        }
                    }
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public void breakBlock(World world, int x, int y, int z, Block p_149749_5_, int meta) {
        if (meta == 1) {
            TileEntity tile = world.getTileEntity(x, y, z);
            TileParticleGenerator gen = (tile != null && tile instanceof TileParticleGenerator)
                    ? (TileParticleGenerator) tile
                    : null;
            if (gen != null && gen.getMaster() != null) {
                world.setBlockMetadataWithNotify(x, y, z, 0, 2);
                // LogHelper.info("deActivate");
                gen.getMaster().isStructureStillValid(true);
            }
        }
        super.breakBlock(world, x, y, z, p_149749_5_, meta);
    }
}
