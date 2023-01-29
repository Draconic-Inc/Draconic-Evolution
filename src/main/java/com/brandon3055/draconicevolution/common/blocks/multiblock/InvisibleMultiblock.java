package com.brandon3055.draconicevolution.common.blocks.multiblock;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

import com.brandon3055.brandonscore.common.utills.InfoHelper;
import com.brandon3055.brandonscore.common.utills.Utills;
import com.brandon3055.draconicevolution.common.ModBlocks;
import com.brandon3055.draconicevolution.common.blocks.BlockDE;
import com.brandon3055.draconicevolution.common.handler.BalanceConfigHandler;
import com.brandon3055.draconicevolution.common.lib.References;
import com.brandon3055.draconicevolution.common.lib.Strings;
import com.brandon3055.draconicevolution.common.tileentities.multiblocktiles.TileEnergyPylon;
import com.brandon3055.draconicevolution.common.tileentities.multiblocktiles.TileEnergyStorageCore;
import com.brandon3055.draconicevolution.common.tileentities.multiblocktiles.TileInvisibleMultiblock;
import com.brandon3055.draconicevolution.common.utills.IHudDisplayBlock;
import com.brandon3055.draconicevolution.common.utills.LogHelper;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/**
 * Created by Brandon on 25/07/2014.
 */
public class InvisibleMultiblock extends BlockDE implements IHudDisplayBlock {

    public InvisibleMultiblock() {
        super(Material.iron);
        this.setHardness(10F);
        this.setResistance(2000F);
        // this.setCreativeTab(DraconicEvolution.getCreativeTab(2));
        this.setBlockName(Strings.invisibleMultiblockName);
        ModBlocks.register(this);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister iconRegister) {
        blockIcon = iconRegister.registerIcon(References.RESOURCESPREFIX + "draconium_block_0");
    }

    @Override
    @SideOnly(Side.CLIENT)
    public int getRenderType() {
        return -1;
    }

    @Override
    public boolean isOpaqueCube() {
        return false;
    }

    @Override
    public boolean renderAsNormalBlock() {
        return false;
    }

    @Override
    public boolean hasTileEntity(int metadata) {
        return metadata == 0 || metadata == 1;
    }

    @Override
    public TileEntity createTileEntity(World world, int metadata) {
        if (metadata == 0 || metadata == 1) return new TileInvisibleMultiblock();
        else return null;
    }

    @Override
    public Item getItemDropped(int meta, Random p_149650_2_, int var2) {
        if (meta == 0) {
            return Item.getItemFromBlock(BalanceConfigHandler.energyStorageStructureOuterBlock);
        }
        if (meta == 1) {
            return Item.getItemFromBlock(BalanceConfigHandler.energyStorageStructureBlock);
        }
        return null;
    }

    @Override
    public int damageDropped(int metadata) {
        if (metadata == 0) {
            return BalanceConfigHandler.energyStorageStructureOuterBlockMetadata;
        }
        if (metadata == 1) {
            return BalanceConfigHandler.energyStorageStructureBlockMetadata;
        }
        return super.damageDropped(metadata);
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int p_149727_6_,
            float p_149727_7_, float p_149727_8_, float p_149727_9_) {
        int meta = world.getBlockMetadata(x, y, z);
        if (meta == 0 || meta == 1) {
            TileInvisibleMultiblock thisTile = (world.getTileEntity(x, y, z) != null
                    && world.getTileEntity(x, y, z) instanceof TileInvisibleMultiblock)
                            ? (TileInvisibleMultiblock) world.getTileEntity(x, y, z)
                            : null;
            if (thisTile == null) {
                LogHelper.error("Missing Tile Entity (TileInvisibleMultiblock)");
                return false;
            }
            TileEnergyStorageCore master = thisTile.getMaster();
            if (master == null) {
                onNeighborBlockChange(world, x, y, z, this);
                return false;
            }
            if (!world.isRemote) {
                world.markBlockForUpdate(master.xCoord, master.yCoord, master.zCoord);
                player.addChatComponentMessage(new ChatComponentText("Tier:" + (master.getTier() + 1)));
                String BN = String.valueOf(master.getEnergyStored());
                player.addChatComponentMessage(
                        new ChatComponentText(
                                StatCollector.translateToLocal("info.de.charge.txt") + ": "
                                        + Utills.formatNumber(master.getEnergyStored())
                                        + " / "
                                        + Utills.formatNumber(master.getMaxEnergyStored())
                                        + " ["
                                        + BN
                                        + " RF]"));
            }
            return true;
        } else if (meta == 2) {
            TileEnergyPylon pylon = (world.getTileEntity(x, y + 1, z) != null
                    && world.getTileEntity(x, y + 1, z) instanceof TileEnergyPylon)
                            ? (TileEnergyPylon) world.getTileEntity(x, y + 1, z)
                            : (world.getTileEntity(x, y - 1, z) != null
                                    && world.getTileEntity(x, y - 1, z) instanceof TileEnergyPylon)
                                            ? (TileEnergyPylon) world.getTileEntity(x, y - 1, z)
                                            : null;
            if (pylon == null) return false;
            pylon.reciveEnergy = !pylon.reciveEnergy;
            world.markBlockForUpdate(pylon.xCoord, pylon.yCoord, pylon.zCoord);
            pylon.onActivated();
            return true;
        }
        return false;
    }

    @Override
    public void onNeighborBlockChange(World world, int x, int y, int z, Block p_149695_5_) {
        int meta = world.getBlockMetadata(x, y, z);
        if (meta == 0 || meta == 1) {
            TileInvisibleMultiblock thisTile = (world.getTileEntity(x, y, z) != null
                    && world.getTileEntity(x, y, z) instanceof TileInvisibleMultiblock)
                            ? (TileInvisibleMultiblock) world.getTileEntity(x, y, z)
                            : null;
            if (thisTile == null) {
                LogHelper.error("Missing Tile Entity (TileInvisibleMultiblock)");
                revert(world, x, y, z);
                return;
            }
            TileEnergyStorageCore master = thisTile.getMaster();
            if (master == null) {
                LogHelper.error("Master = null reverting!");
                revert(world, x, y, z);
                return;
            }
            if (master.isOnline()) master.isStructureStillValid(thisTile.getMaster().getTier() == 1);
            if (!master.isOnline()) revert(world, x, y, z);
        } else if (meta == 2) {
            if (world.getBlock(x, y + 1, z) != ModBlocks.energyPylon
                    && world.getBlock(x, y - 1, z) != ModBlocks.energyPylon)
                world.setBlock(x, y, z, Blocks.glass);
        }
    }

    private void revert(World world, int x, int y, int z) {
        int meta = world.getBlockMetadata(x, y, z);
        if (meta == 0) {
            world.setBlock(
                    x,
                    y,
                    z,
                    BalanceConfigHandler.energyStorageStructureOuterBlock,
                    BalanceConfigHandler.energyStorageStructureOuterBlockMetadata,
                    3);
        } else if (meta == 1) {
            world.setBlock(
                    x,
                    y,
                    z,
                    BalanceConfigHandler.energyStorageStructureBlock,
                    BalanceConfigHandler.energyStorageStructureBlockMetadata,
                    3);
        }
    }

    @Override
    public void breakBlock(World world, int x, int y, int z, Block p_149749_5_, int meta) {
        TileEntity tile = world.getTileEntity(x, y, z);
        TileInvisibleMultiblock thisTile = (tile != null && tile instanceof TileInvisibleMultiblock)
                ? (TileInvisibleMultiblock) tile
                : null;
        if (thisTile != null && thisTile.getMaster() != null && thisTile.getMaster().isOnline()) {
            world.setBlockMetadataWithNotify(x, y, z, 0, 2);

            thisTile.getMaster().isStructureStillValid(thisTile.getMaster().getTier() == 1);
        }
        super.breakBlock(world, x, y, z, p_149749_5_, meta);
    }

    @Override
    public AxisAlignedBB getSelectedBoundingBoxFromPool(World world, int x, int y, int z) {
        int meta = world.getBlockMetadata(x, y, z);
        if (meta == 0 || meta == 1) {
            TileInvisibleMultiblock thisTile = (world.getTileEntity(x, y, z) != null
                    && world.getTileEntity(x, y, z) instanceof TileInvisibleMultiblock)
                            ? (TileInvisibleMultiblock) world.getTileEntity(x, y, z)
                            : null;
            if (thisTile != null && thisTile.getMaster() != null) {
                return AxisAlignedBB.getBoundingBox(
                        thisTile.getMaster().xCoord,
                        thisTile.getMaster().yCoord,
                        thisTile.getMaster().zCoord,
                        thisTile.getMaster().xCoord + 0.5,
                        thisTile.getMaster().yCoord + 0.5,
                        thisTile.getMaster().zCoord + 0.5);
            }
            return super.getSelectedBoundingBoxFromPool(world, x, y, z);
        } else if (meta == 2) {
            return AxisAlignedBB.getBoundingBox(x + 0.49, y + 0.49, z + 0.49, x + 0.51, y + 0.51, z + 0.51);
        }
        return super.getSelectedBoundingBoxFromPool(world, x, y, z);
    }

    @Override
    public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z) {

        int meta = world.getBlockMetadata(x, y, z);
        if (meta == 2) {
            return AxisAlignedBB.getBoundingBox(x, y, z, x, y, z);
        }
        return super.getCollisionBoundingBoxFromPool(world, x, y, z);
    }

    @Override
    public ItemStack getPickBlock(MovingObjectPosition target, World world, int x, int y, int z, EntityPlayer player) {
        int metadata = world.getBlockMetadata(x, y, z);
        if (metadata == 0) {
            return new ItemStack(
                    BalanceConfigHandler.energyStorageStructureOuterBlock,
                    1,
                    BalanceConfigHandler.energyStorageStructureOuterBlockMetadata);
        }
        if (metadata == 1) {
            return new ItemStack(
                    BalanceConfigHandler.energyStorageStructureBlock,
                    1,
                    BalanceConfigHandler.energyStorageStructureBlockMetadata);
        }
        return new ItemStack(Blocks.glass);
    }

    @Override
    public List<String> getDisplayData(World world, int x, int y, int z) {
        List<String> list = new ArrayList<String>();

        int meta = world.getBlockMetadata(x, y, z);
        if (meta == 0 || meta == 1) {
            TileInvisibleMultiblock thisTile = (world.getTileEntity(x, y, z) != null
                    && world.getTileEntity(x, y, z) instanceof TileInvisibleMultiblock)
                            ? (TileInvisibleMultiblock) world.getTileEntity(x, y, z)
                            : null;
            if (thisTile == null) {
                LogHelper.error("Missing Tile Entity (TileInvisibleMultiblock getDisplayData)");
                return list;
            }

            TileEnergyStorageCore master = thisTile.getMaster();

            if (master == null) {
                return list;
            }

            list.add(InfoHelper.HITC() + ModBlocks.energyStorageCore.getLocalizedName());
            list.add("Tier: " + InfoHelper.ITC() + (master.getTier() + 1));
            String BN = String.valueOf(master.getEnergyStored());
            list.add(
                    StatCollector.translateToLocal("info.de.charge.txt") + ": "
                            + InfoHelper.ITC()
                            + Utills.formatNumber(master.getEnergyStored())
                            + " / "
                            + Utills.formatNumber(master.getMaxEnergyStored())
                            + " ["
                            + Utills.addCommas(master.getEnergyStored())
                            + " RF]");

            return list;
        }

        return list;
    }

    @Override
    public String getUnlocalizedName() {
        return super.getUnlocalizedName();
    }
}
