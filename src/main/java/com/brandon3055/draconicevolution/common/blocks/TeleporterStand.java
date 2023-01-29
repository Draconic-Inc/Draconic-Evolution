package com.brandon3055.draconicevolution.common.blocks;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

import com.brandon3055.brandonscore.common.utills.Teleporter;
import com.brandon3055.draconicevolution.DraconicEvolution;
import com.brandon3055.draconicevolution.common.ModBlocks;
import com.brandon3055.draconicevolution.common.items.tools.TeleporterMKI;
import com.brandon3055.draconicevolution.common.lib.References;
import com.brandon3055.draconicevolution.common.lib.Strings;
import com.brandon3055.draconicevolution.common.tileentities.TileTeleporterStand;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/**
 * Created by Brandon on 27/06/2014.
 */
public class TeleporterStand extends BlockCustomDrop {

    public TeleporterStand() {
        super(Material.rock);
        this.setBlockName(Strings.teleporterStandName);
        this.setCreativeTab(DraconicEvolution.tabBlocksItems);
        this.setStepSound(soundTypeStone);
        this.setHardness(1.5f);
        this.setResistance(10.0f);
        this.setBlockBounds(0.35f, 0f, 0.35f, 0.65f, 0.8f, 0.65f);
        ModBlocks.register(this);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void registerBlockIcons(IIconRegister iconRegister) {
        // blockIcon = iconRegister.registerIcon(References.RESOURCESPREFIX + "machine_side");
    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        return new TileTeleporterStand();
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float prx,
            float pry, float prz) {
        TileTeleporterStand tile = world.getTileEntity(x, y, z) instanceof TileTeleporterStand
                ? (TileTeleporterStand) world.getTileEntity(x, y, z)
                : null;
        if (tile == null) return false;
        if (tile.getStackInSlot(0) == null && player.getHeldItem() != null
                && (player.getHeldItem().getItem() instanceof TeleporterMKI)) {
            ItemStack stack = player.getHeldItem();
            tile.setInventorySlotContents(0, stack.copy());
            player.inventory.setInventorySlotContents(player.inventory.currentItem, null);
            world.markBlockForUpdate(x, y, z);
            return true;
        }

        if (tile.getStackInSlot(0) != null && player.isSneaking()) {
            EntityItem item = new EntityItem(world, x + 0.5, y + 0.9, z + 0.5, tile.getStackInSlot(0).copy());
            item.motionX = 0;
            item.motionY = 0;
            item.motionZ = 0;
            item.delayBeforeCanPickup = 0;
            tile.setInventorySlotContents(0, null);
            if (!world.isRemote) world.spawnEntityInWorld(item);
            return true;
        }

        if (tile.getStackInSlot(0) != null && !player.isSneaking()
                && tile.getStackInSlot(0).getItem() instanceof TeleporterMKI) {
            Teleporter.TeleportLocation l = ((TeleporterMKI) tile.getStackInSlot(0).getItem())
                    .getLocation(tile.getStackInSlot(0));
            if (l != null) l.sendEntityToCoords(player);
            return true;
        }

        return true;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public int getRenderType() {
        return References.idTeleporterStand;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int par1, int par2) {
        return Block.getBlockFromName("stone").getIcon(par1, 1);
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
    protected boolean dropInventory() {
        return true;
    }

    @Override
    protected boolean hasCustomDropps() {
        return false;
    }

    @Override
    protected void getCustomTileEntityDrops(TileEntity te, List<ItemStack> droppes) {}

    @Override
    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase entity, ItemStack p_149689_6_) {
        super.onBlockPlacedBy(world, x, y, z, entity, p_149689_6_);
        TileTeleporterStand tile = world.getTileEntity(x, y, z) instanceof TileTeleporterStand
                ? (TileTeleporterStand) world.getTileEntity(x, y, z)
                : null;
        if (tile == null) return;
        tile.rotation = (int) entity.rotationYawHead;
    }

    @Override
    public ItemStack getPickBlock(MovingObjectPosition target, World world, int x, int y, int z, EntityPlayer player) {
        return super.getPickBlock(target, world, x, y, z, player);
    }
}
