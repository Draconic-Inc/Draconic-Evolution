package com.brandon3055.draconicevolution.common.blocks;

import java.util.Random;

import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import com.brandon3055.draconicevolution.DraconicEvolution;
import com.brandon3055.draconicevolution.client.handler.ParticleHandler;
import com.brandon3055.draconicevolution.common.ModBlocks;
import com.brandon3055.draconicevolution.common.ModItems;
import com.brandon3055.draconicevolution.common.lib.Strings;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class XRayBlock extends BlockDE {

    public XRayBlock() {
        this.setBlockName(Strings.xrayBlockName);
        this.setCreativeTab(DraconicEvolution.tabBlocksItems);
        this.setBlockBounds(0.4F, 0.4F, 0.4F, 0.6F, 0.6F, 0.6F);
        this.setHardness(10f);
        this.setHarvestLevel("pickaxe", 4);
        ModBlocks.register(this);
    }

    @Override
    public int getRenderType() {
        return -1;
    }

    @Override
    public boolean isReplaceable(IBlockAccess world, int x, int y, int z) {
        return super.isReplaceable(world, x, y, z);
    }

    @Override
    public boolean renderAsNormalBlock() {
        return false;
    }

    @Override
    public AxisAlignedBB getCollisionBoundingBoxFromPool(final World p_149668_1_, final int p_149668_2_,
            final int p_149668_3_, final int p_149668_4_) {
        return null;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void randomDisplayTick(final World world, final int x, final int y, final int z, final Random rand) {
        final double d0 = x + 0.5F;
        final double d1 = y + 0.48F;
        final double d2 = z + 0.5F;

        float mX1 = (rand.nextFloat() - 0.5F) * 0.005F;
        float mY1 = rand.nextFloat() * 0.01F;
        float mZ1 = (rand.nextFloat() - 0.5F) * 0.005F;
        ParticleHandler.spawnParticle("distortionParticle", d0, d1, d2, mX1, mY1, mZ1, 1);
        for (int i = 0; i < 3; i++) {
            float mX = (rand.nextFloat() - 0.5F) * 0.005F;
            float mY = 0.01F + rand.nextFloat() * 0.005F;
            float mZ = (rand.nextFloat() - 0.5F) * 0.005F;
            float scale = 0.2F + (rand.nextFloat() * 0.2F);
            ParticleHandler.spawnParticle("distortionParticle", d0, d1, d2, mX, mY, mZ, scale);
        }
    }

    @Override
    public int quantityDropped(final Random p_149745_1_) {
        return 0;
    }

    @Override
    public void onBlockClicked(World world, int x, int y, int z, EntityPlayer player) {
        ItemStack tool = player.getCurrentEquippedItem();
        if (tool != null
                && (player.getCurrentEquippedItem().isItemEqual(new ItemStack(ModItems.draconicDestructionStaff))
                        || player.getCurrentEquippedItem().isItemEqual(new ItemStack(ModItems.draconicPickaxe)))) {
            EntityItem item = new EntityItem(
                    world,
                    player.posX,
                    player.posY,
                    player.posZ,
                    new ItemStack(Item.getItemFromBlock(ModBlocks.xRayBlock)));
            world.setBlockToAir(x, y, z);
            if (!world.isRemote) world.spawnEntityInWorld(item);
        }
        super.onBlockClicked(world, x, y, z, player);
    }

    @Override
    protected boolean canSilkHarvest() {
        return false;
    }

    @Override
    public void onEntityCollidedWithBlock(final World world, final int x, final int y, final int z,
            final Entity entity) {
        // if(entity != EntityItem)
        // System.out.println();
        // entity.attackEntityFrom(DamageSource.magic, 10F);
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int p_149727_6_,
            float p_149727_7_, float p_149727_8_, float p_149727_9_) {
        // world.spawnEntityInWorld(new EntityLightningBolt(world, x, y, z));
        return false;
    }
}
