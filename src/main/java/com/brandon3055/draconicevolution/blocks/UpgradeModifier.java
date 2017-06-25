package com.brandon3055.draconicevolution.blocks;

///**
// * Created by brandon3055 on 9/06/2016.
// */
//public class UpgradeModifier extends BlockBCore implements ITileEntityProvider, ICustomRender {
//
//    public UpgradeModifier(){
//        super(Material.IRON);
//        setIsFullCube(false);
//    }
//
//    //region Block
//
//    @Override
//    public TileEntity createNewTileEntity(World worldIn, int meta) {
//        return new TileUpgradeModifier();
//    }
//
//    @Override
//    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ) {
//        if (!world.isRemote) {
//            FMLNetworkHandler.openGui(player, DraconicEvolution.instance, GuiHandler.GUIID_UPGRADE_MODIFIER, world, pos.getX(), pos.getY(), pos.getZ());
//        }
//        return true;
//    }
//
//    //endregion
//
//    //region Rendering
//
//    @Override
//    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
//        return new AxisAlignedBB(0, 0, 0, 1, 0.375, 1);
//    }
//
//    @SideOnly(Side.CLIENT)
//    @Override
//    public void registerRenderer(Feature feature) {
//        ClientRegistry.bindTileEntitySpecialRenderer(TileUpgradeModifier.class, new RenderTileUpgradeModifier());
//    }
//
//    @Override
//    public boolean registerNormal(Feature feature) {
//        return true;
//    }
//
//    //endregion
//}
