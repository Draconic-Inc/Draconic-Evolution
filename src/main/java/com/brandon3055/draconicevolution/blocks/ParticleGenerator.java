package com.brandon3055.draconicevolution.blocks;

import com.brandon3055.brandonscore.blocks.BlockBCore;
import com.brandon3055.brandonscore.blocks.TileBCBase;
import com.brandon3055.brandonscore.blocks.properties.PropertyString;
import com.brandon3055.brandonscore.config.IRegisterMyOwnTiles;
import com.brandon3055.draconicevolution.blocks.tileentity.TileEnergyCoreStabilizer;
import com.brandon3055.draconicevolution.blocks.tileentity.TileEnergyStorageCore;
import com.brandon3055.draconicevolution.blocks.tileentity.TileParticleGenerator;
import com.brandon3055.draconicevolution.utills.LogHelper;
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
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.GameRegistry;

import java.util.List;

/**
 * Created by brandon3055 on 30/3/2016.
 */
public class ParticleGenerator extends BlockBCore implements ITileEntityProvider, IRegisterMyOwnTiles {
	public static final PropertyString TYPE = new PropertyString("type", "normal", "inverted", "stabilizer");
	//public static final PropertyEnum<EnumType> TYPE = PropertyEnum.create("type", EnumType.class);

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
	//	TileGrinder tileGrinder = worldIn.tileEntity(pos) instanceof TileGrinder ? (TileGrinder) worldIn.tileEntity(pos) : null;
		return state;//state.withProperty(ACTIVE, tileGrinder != null && tileGrinder.active.value);todo Implement active state
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
	public int damageDropped(IBlockState state) {
		return getMetaFromState(state);
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return (meta == 0 || meta == 1) ? new TileParticleGenerator() : meta == 2 ? new TileEnergyCoreStabilizer() : null;
	}

	//endregion

	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ) {
        if (state.getValue(TYPE).equals("normal") || state.getValue(TYPE).equals("inverted")){
			world.setBlockState(pos, state.withProperty(TYPE, state.getValue(TYPE).equals("normal") ? "inverted" : "normal"));
		}
        else if (state.getValue(TYPE).equals("stabilizer")){
            TileEntity tile = world.getTileEntity(pos);

            if (tile instanceof TileEnergyCoreStabilizer){
                ((TileEnergyCoreStabilizer)tile).onTileClicked(world, pos, state, player);
            }

        }
		return true;
	}

	@Override
	public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
		world.setBlockState(pos, state.withProperty(TYPE, TYPE.fromMeta(stack.getItemDamage())));
	}

	@Override
	public void registerTiles(String modidPrefix, String blockName) {
		GameRegistry.registerTileEntity(TileParticleGenerator.class, modidPrefix + blockName + ".particle");
		GameRegistry.registerTileEntity(TileEnergyCoreStabilizer.class, modidPrefix + blockName + ".stabilize");
	}

    @Override
    public void breakBlock(World world, BlockPos pos, IBlockState state) {
        if (state.getValue(TYPE).equals("stabilizer")){
            TileEnergyCoreStabilizer tile = TileBCBase.getCastTileAt(world, pos, TileEnergyCoreStabilizer.class);

            if (tile != null){
                TileEnergyStorageCore core = tile.getCore();

                if (core != null){
                    LogHelper.info("Validate");
                    world.removeTileEntity(pos);
                    core.validateStructure();
                }
            }

        }
        super.breakBlock(world, pos, state);
    }
}
