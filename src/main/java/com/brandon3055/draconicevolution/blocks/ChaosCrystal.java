package com.brandon3055.draconicevolution.blocks;

import com.brandon3055.brandonscore.blocks.BlockBCore;
import com.brandon3055.brandonscore.registry.Feature;
import com.brandon3055.brandonscore.registry.IRenderOverride;
import com.brandon3055.draconicevolution.DEFeatures;
import com.brandon3055.draconicevolution.blocks.tileentity.TileChaosCrystal;
import com.brandon3055.draconicevolution.client.render.tile.RenderTileChaosCrystal;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;

/**
 * Created by brandon3055 on 24/9/2015.
 */
public class ChaosCrystal extends BlockBCore implements ITileEntityProvider, IRenderOverride {

    public ChaosCrystal() {
        this.setHardness(100.0F);
        this.setResistance(4000.0F);
        this.setBlockUnbreakable();
    }

    @Override
    public boolean uberIsBlockFullCube() {
        return false;
    }

    @Override
    public float getBlockHardness(IBlockState blockState, World world, BlockPos pos) {
        TileChaosCrystal tile = world.getTileEntity(pos) instanceof TileChaosCrystal ? (TileChaosCrystal) world.getTileEntity(pos) : null;
        if (tile != null) return tile.guardianDefeated.get() ? 100F : -1F;
        return super.getBlockHardness(blockState, world, pos);
    }

    @Override
    public void getSubBlocks(CreativeTabs tab, NonNullList<ItemStack> list) {}

    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new TileChaosCrystal();
    }

    @Nullable
    @Override
    public Item getItemDropped(IBlockState state, Random rand, int fortune) {
        return DEFeatures.chaosShard;
    }

    @Override
    public int quantityDropped(Random random) {
        return 5;
    }

    @Override
    public boolean canEntityDestroy(IBlockState state, IBlockAccess world, BlockPos pos, Entity entity) {
        return false;
    }

    @Override
    public void onBlockDestroyedByExplosion(World worldIn, BlockPos pos, Explosion explosionIn) {
    }

    @Override
    public void breakBlock(World world, BlockPos pos, IBlockState state) {
        TileEntity tile = world.getTileEntity(pos);
        if (!world.isRemote && tile instanceof TileChaosCrystal) {
            ((TileChaosCrystal) tile).detonate();
        }
        super.breakBlock(world, pos, state);
    }

    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
        if (placer instanceof EntityPlayer && ((EntityPlayer) placer).capabilities.isCreativeMode) {
            TileEntity tile = world.getTileEntity(pos);
            if (!world.isRemote && tile instanceof TileChaosCrystal) {
                ((TileChaosCrystal) tile).setLockPos();
                ((TileChaosCrystal) tile).guardianDefeated.set(true);
            }
        }
        else {
            placer.attackEntityFrom(punishment, Float.MAX_VALUE);
        }
    }

    private static String[] naughtyList = new String[]{"item.blockMover", "tile.CardboardBox", "item.WandCasting"};
    private static DamageSource punishment = new DamageSource("chrystalMoved").setDamageAllowedInCreativeMode().setDamageBypassesArmor().setDamageIsAbsolute();

    @Override
    public void onBlockAdded(World world, BlockPos pos, IBlockState state) {
        List<EntityPlayer> players = world.getEntitiesWithinAABB(EntityPlayer.class, new AxisAlignedBB(pos, pos.add(1, 1, 1)).grow(15, 15, 15));

        for (EntityPlayer player : players) {
            if (player.capabilities.isCreativeMode) {
                return;
            }
            if (!player.getHeldItemMainhand().isEmpty()) {
                for (String s : naughtyList) {
                    if (player.getHeldItemMainhand().getUnlocalizedName().equals(s)) {
                        player.attackEntityFrom(punishment, Float.MAX_VALUE);
                    }
                }
            }
            if (!player.getHeldItemOffhand().isEmpty()) {
                for (String s : naughtyList) {
                    if (player.getHeldItemOffhand().getUnlocalizedName().equals(s)) {
                        player.attackEntityFrom(punishment, Float.MAX_VALUE);
                    }
                }
            }
        }
    }

    //region Rendering


    @Override
    public EnumBlockRenderType getRenderType(IBlockState state) {
        return EnumBlockRenderType.INVISIBLE;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void registerRenderer(Feature feature) {
        ClientRegistry.bindTileEntitySpecialRenderer(TileChaosCrystal.class, new RenderTileChaosCrystal());
    }

    @Override
    public boolean registerNormal(Feature feature) {
        return false;
    }

    //endregion
}
