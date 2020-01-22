package com.brandon3055.draconicevolution.blocks;

import com.brandon3055.brandonscore.blocks.BlockBCore;
import com.brandon3055.brandonscore.registry.Feature;
import com.brandon3055.brandonscore.registry.IRegistryOverride;
import com.brandon3055.brandonscore.registry.IRenderOverride;
import com.brandon3055.draconicevolution.DraconicEvolution;
import com.brandon3055.draconicevolution.GuiHandler;
import com.brandon3055.draconicevolution.blocks.tileentity.TileEnergyCoreStabilizer;
import com.brandon3055.draconicevolution.blocks.tileentity.TileEnergyStorageCore;
import com.brandon3055.draconicevolution.blocks.tileentity.TileParticleGenerator;
import com.brandon3055.draconicevolution.client.render.tile.RenderTileECStabilizer;
import com.brandon3055.draconicevolution.lib.PropertyStringTemp;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Created by brandon3055 on 30/3/2016.
 */
public class ParticleGenerator extends BlockBCore implements ITileEntityProvider, IRegistryOverride, IRenderOverride {
    public static final PropertyStringTemp TYPE = new PropertyStringTemp("type", "normal", "inverted", "stabilizer", "stabilizer2");

    public ParticleGenerator() {
        super(Material.IRON);
        this.setDefaultState(blockState.getBaseState().withProperty(TYPE, "normal"));
        this.addName(0, "particle_generator");
        this.addName(2, "energy_core_stabilizer");
    }

    //region BlockState
    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, TYPE);
    }

    @Override
    public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
        return state;
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return this.getDefaultState().withProperty(TYPE, TYPE.fromMeta(meta));
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return TYPE.toMeta(state.getValue(TYPE));
    }

    //endregion

    //region Standard Block Methods

    @Override
    public void getSubBlocks(CreativeTabs tab, NonNullList<ItemStack> list) {
        list.add(new ItemStack(this, 1, 0));
        list.add(new ItemStack(this, 1, 2));
    }

    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    @Override
    public boolean isNormalCube(IBlockState state, IBlockAccess world, BlockPos pos) {
        return false;
    }

    @Override
    public boolean isFullCube(IBlockState state) {
        return false;
    }

    @Override
    public int damageDropped(IBlockState state) {
        return Math.min(getMetaFromState(state), 2);
    }

    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return (meta == 0 || meta == 1) ? new TileParticleGenerator() : meta == 2 || meta == 3 ? new TileEnergyCoreStabilizer() : null;
    }

    @Override
    public boolean canConnectRedstone(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing side) {
        return state.getValue(TYPE).equals("normal") || state.getValue(TYPE).equals("inverted");
    }

    //endregion

    //region Render


    @Override
    public int getLightValue(IBlockState state) {
        return (state.getValue(TYPE).equals("stabilizer") || state.getValue(TYPE).equals("stabilizer2")) ? 10 : 0;
    }

    @Override
    public EnumBlockRenderType getRenderType(IBlockState state) {
        return state.getValue(TYPE).equals("stabilizer2") ? EnumBlockRenderType.INVISIBLE : super.getRenderType(state);
    }

    @Override
    public AxisAlignedBB getSelectedBoundingBox(IBlockState state, World world, BlockPos pos) {
        TileEntity tile = world.getTileEntity(pos);

        if (tile instanceof TileEnergyCoreStabilizer) {
            if (((TileEnergyCoreStabilizer) tile).isValidMultiBlock.value) {
                AxisAlignedBB bb = new AxisAlignedBB(tile.getPos());

                if (((TileEnergyCoreStabilizer) tile).multiBlockAxis.getPlane() == EnumFacing.Plane.HORIZONTAL) {
                    if (((TileEnergyCoreStabilizer) tile).multiBlockAxis == EnumFacing.Axis.X) {
                        bb = bb.grow(0, 1, 1);
                    }
                    else {
                        bb = bb.grow(1, 1, 0);
                    }
                }
                else {
                    bb = bb.grow(1, 0, 1);
                }
                return bb;
            }
        }

        return super.getSelectedBoundingBox(state, world, pos);
    }

    //endregion

    //region Place/Break stuff

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
        if (state.getValue(TYPE).equals("normal") || state.getValue(TYPE).equals("inverted")) {
            if (player.isSneaking()) {
                world.setBlockState(pos, state.withProperty(TYPE, state.getValue(TYPE).equals("normal") ? "inverted" : "normal"));
            } else if (world.isRemote) {
                player.openGui(DraconicEvolution.instance, GuiHandler.GUIID_PARTICLEGEN, world, pos.getX(), pos.getY(), pos.getZ());
            }
        }
        else {
            TileEntity tile = world.getTileEntity(pos);

            if (tile instanceof TileEnergyCoreStabilizer) {
                ((TileEnergyCoreStabilizer) tile).onTileClicked(world, pos, state, player);
            }

        }
        return true;
    }

    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
        world.setBlockState(pos, state.withProperty(TYPE, TYPE.fromMeta(stack.getItemDamage())));

        TileEntity tile = world.getTileEntity(pos);

        if (tile instanceof TileEnergyCoreStabilizer) {
            ((TileEnergyCoreStabilizer) tile).onPlaced();
        }
        else {
            super.onBlockPlacedBy(world, pos, state, placer, stack);
        }
    }

    @Override
    public void breakBlock(World world, BlockPos pos, IBlockState state) {
        TileEntity tile = world.getTileEntity(pos);

        if (tile instanceof TileEnergyCoreStabilizer) {
            if (((TileEnergyCoreStabilizer) tile).isValidMultiBlock.value) {
                ((TileEnergyCoreStabilizer) tile).deFormStructure();
            }
            TileEnergyStorageCore core = ((TileEnergyCoreStabilizer) tile).getCore();

            if (core != null) {
                world.removeTileEntity(pos);
                ((TileEnergyCoreStabilizer) tile).validateStructure();
            }

        }

        super.breakBlock(world, pos, state);
    }

    //endregion

    //region Registry


    @Override
    public void handleCustomRegistration(Feature feature) {
        GameRegistry.registerTileEntity(TileParticleGenerator.class, feature.getRegistryName() + ".particle");
        GameRegistry.registerTileEntity(TileEnergyCoreStabilizer.class, feature.getRegistryName() + ".stabilize");
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void registerRenderer(Feature feature) {
        ClientRegistry.bindTileEntitySpecialRenderer(TileEnergyCoreStabilizer.class, new RenderTileECStabilizer());
        //ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(this), 10, new ModelResourceLocation(getRegistryName(), "type=stabilizer2"));
    }

    @Override
    public boolean registerNormal(Feature feature) {
        return true;
    }

    //endregion
}
