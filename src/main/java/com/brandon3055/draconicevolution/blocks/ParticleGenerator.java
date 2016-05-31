package com.brandon3055.draconicevolution.blocks;

import com.brandon3055.brandonscore.blocks.BlockBCore;
import com.brandon3055.brandonscore.blocks.properties.PropertyString;
import com.brandon3055.brandonscore.config.Feature;
import com.brandon3055.brandonscore.config.ICustomRender;
import com.brandon3055.brandonscore.config.IRegisterMyOwnTiles;
import com.brandon3055.draconicevolution.blocks.tileentity.TileEnergyCoreStabilizer;
import com.brandon3055.draconicevolution.blocks.tileentity.TileEnergyStorageCore;
import com.brandon3055.draconicevolution.blocks.tileentity.TileParticleGenerator;
import com.brandon3055.draconicevolution.client.render.tile.RenderTileECStabilizer;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

/**
 * Created by brandon3055 on 30/3/2016.
 */
public class ParticleGenerator extends BlockBCore implements ITileEntityProvider, IRegisterMyOwnTiles, ICustomRender {
    public static final PropertyString TYPE = new PropertyString("type", "normal", "inverted", "stabilizer", "stabilizer2");

    public ParticleGenerator() {
        super(Material.iron);
        this.setDefaultState(blockState.getBaseState().withProperty(TYPE, "normal"));
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
    public void getSubBlocks(Item item, CreativeTabs tab, List<ItemStack> list) {
        list.add(new ItemStack(item, 1, 0));
        list.add(new ItemStack(item, 1, 2));
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
            if (((TileEnergyCoreStabilizer)tile).isValidMultiBlock.value) {
                AxisAlignedBB bb = new AxisAlignedBB(tile.getPos());

                if (((TileEnergyCoreStabilizer)tile).multiBlockAxis.getPlane() == EnumFacing.Plane.HORIZONTAL) {
                    if (((TileEnergyCoreStabilizer)tile).multiBlockAxis == EnumFacing.Axis.X) {
                        bb = bb.expand(0, 1, 1);
                    } else {
                        bb = bb.expand(1, 1, 0);
                    }
                } else {
                    bb = bb.expand(1, 0, 1);
                }
                return bb;
            }
        }

        return super.getSelectedBoundingBox(state, world, pos);
    }

    //endregion

    //region Place/Break stuff

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ) {
        if (state.getValue(TYPE).equals("normal") || state.getValue(TYPE).equals("inverted")) {
            world.setBlockState(pos, state.withProperty(TYPE, state.getValue(TYPE).equals("normal") ? "inverted" : "normal"));
        } else {
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
            ((TileEnergyCoreStabilizer)tile).onPlaced();
        }
    }

    @Override
    public void breakBlock(World world, BlockPos pos, IBlockState state) {
        TileEntity tile = world.getTileEntity(pos);

        if (tile instanceof TileEnergyCoreStabilizer) {
            if (((TileEnergyCoreStabilizer)tile).isValidMultiBlock.value) {
                ((TileEnergyCoreStabilizer)tile).deFormStructure();
            }
            TileEnergyStorageCore core = ((TileEnergyCoreStabilizer)tile).getCore();

            if (core != null) {
                world.removeTileEntity(pos);
                ((TileEnergyCoreStabilizer)tile).validateStructure();
            }

        }

        super.breakBlock(world, pos, state);
    }

    @Override
    public IBlockState onBlockPlaced(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
        return super.onBlockPlaced(world, pos, facing, hitX, hitY, hitZ, meta, placer);
    }

    //endregion

    //region Registry

    @Override
    public void registerTiles(String modidPrefix, String blockName) {
        GameRegistry.registerTileEntity(TileParticleGenerator.class, modidPrefix + blockName + ".particle");
        GameRegistry.registerTileEntity(TileEnergyCoreStabilizer.class, modidPrefix + blockName + ".stabilize");
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
