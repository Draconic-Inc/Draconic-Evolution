package com.brandon3055.draconicevolution.blocks;

import com.brandon3055.brandonscore.blocks.BlockBCore;
import com.brandon3055.brandonscore.utils.ItemNBTHelper;
import com.brandon3055.draconicevolution.init.DEContent;
import com.brandon3055.draconicevolution.blocks.tileentity.TileDraconiumChest;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;

import javax.annotation.Nullable;

/**
 * Created by brandon3055 on 25/09/2016.
 */
public class DraconiumChest extends BlockBCore/* implements ITileEntityProvider, IRenderOverride*/ {

    protected static final VoxelShape SHAPE = Block.makeCuboidShape(1.0, 0.0D, 1.0, 15.0, 14.0, 15.0);

    public DraconiumChest(Properties properties) {
        super(properties);
    }

    @Override
    public boolean isBlockFullCube() {
        return false;
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new TileDraconiumChest();
    }

//    @Override
//    public boolean onBlockActivated(World worldIn, BlockPos pos, BlockState state, PlayerEntity playerIn, Hand hand, Direction side, float hitX, float hitY, float hitZ) {
//        if (!worldIn.isRemote) {
//            playerIn.openGui(DraconicEvolution.instance, GuiHandler.GUIID_DRACONIUM_CHEST, worldIn, pos.getX(), pos.getY(), pos.getZ());
//        }
//        return true;//super.onBlockActivated(worldIn, pos, state, playerIn, hand, heldItem, side, hitX, hitY, hitZ);
//    }

    public static boolean isStackValid(ItemStack stack) {
        if (stack.getItem() == Item.getItemFromBlock(DEContent.draconium_chest)) {
            return false;
        }
        else if (!stack.isEmpty()) {
            String name = stack.getTranslationKey().toLowerCase();
            if (name.contains("pouch") || name.contains("bag") || name.contains("strongbox") || name.contains("shulker_box")) {
                return false;
            }
        }
        return true;
    }


    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.INVISIBLE;
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        return SHAPE;
    }

//    @Override
//    public void onBlockPlacedBy(World worldIn, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
//        Direction enumfacing = Direction.getHorizontal(MathHelper.floor((double) (placer.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3).getOpposite();
//        TileEntity tile = worldIn.getTileEntity(pos);
//
//        if (tile instanceof TileDraconiumChest) {
//            ((TileDraconiumChest) tile).facing.set(enumfacing);
//        }
//
//        super.onBlockPlacedBy(worldIn, pos, state, placer, stack);
//    }

//    @Override
//    public boolean rotateBlock(World world, BlockPos pos, Direction axis) {
//        TileEntity tile = world.getTileEntity(pos);
//
//        if (tile instanceof TileDraconiumChest) {
//            ((TileDraconiumChest) tile).facing.set(((TileDraconiumChest) tile).facing.get().rotateY());
//            ((TileDraconiumChest) tile).ioCacheValid = false;
//        }
//
//        return true;
//    }

    //region Rendering

//    @Override
//    @OnlyIn(Dist.CLIENT)
//    public void registerRenderer(Feature feature) {
//        ClientRegistry.bindTileEntitySpecialRenderer(TileDraconiumChest.class, new RenderTileDraconiumChest());
////        ModelRegistryHelper.registerItemRenderer(Item.getItemFromBlock(this), new RenderItemDraconiumChest());
//
//        ModelResourceLocation modelLocation = new ModelResourceLocation(DraconicEvolution.MOD_PREFIX + feature.getName() + "#normal");
//        ModelLoader.registerItemVariants(Item.getItemFromBlock(this), modelLocation);
//        IBakedModel bakedModel = new RenderItemDraconiumChest();
//        ModelRegistryHelper.register(modelLocation, bakedModel);
//        ModelLoader.setCustomMeshDefinition(Item.getItemFromBlock(this), (ItemStack stack) -> modelLocation);
//
//    }
//
//    @Override
//    public boolean registerNormal(Feature feature) {
//        return false;
//    }


    //endregion

    @Override
    public boolean overrideShareTag() {
        return true;
    }

    @Override
    public CompoundNBT getNBTShareTag(ItemStack stack) {
        CompoundNBT compound = new CompoundNBT();
        compound.putInt("ChestColour", ItemNBTHelper.getInteger(stack, "ChestColour", 0x640096));
        return compound;
    }
}
