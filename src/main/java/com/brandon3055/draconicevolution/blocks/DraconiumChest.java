package com.brandon3055.draconicevolution.blocks;

import com.brandon3055.brandonscore.blocks.BlockBCore;
import com.brandon3055.brandonscore.lib.IActivatableTile;
import com.brandon3055.brandonscore.utils.ItemNBTHelper;
import com.brandon3055.draconicevolution.DraconicEvolution;
import com.brandon3055.draconicevolution.init.DEContent;
import com.brandon3055.draconicevolution.blocks.tileentity.TileDraconiumChest;
import net.minecraft.block.*;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Locale;

/**
 * Created by brandon3055 on 25/09/2016.
 */
public class DraconiumChest extends BlockBCore {

    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final BooleanProperty ACTIVE = BooleanProperty.create("active");

    protected static final VoxelShape SHAPE = Block.box(1.0, 0.0D, 1.0, 15.0, 14.0, 15.0);

    public DraconiumChest(Properties properties) {
        super(properties);
        this.registerDefaultState(stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(ACTIVE, false));
    }

    @Override
    public boolean isBlockFullCube() {
        return false;
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(FACING, ACTIVE);
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new TileDraconiumChest();
    }

    public static boolean isStackValid(ItemStack stack) {
        if (stack.getItem() == Item.byBlock(DEContent.draconium_chest)) {
            return false;
        }
        else if (!stack.isEmpty()) {
            String name = stack.getDescriptionId().toLowerCase(Locale.ENGLISH);
            if (name.contains("pouch") || name.contains("bag") || name.contains("strongbox") || name.contains("shulker_box")) {
                return false;
            }
        }
        return true;
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
    public BlockState getStateForPlacement(BlockItemUseContext context) {
        return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader world, BlockPos pos, ISelectionContext context) {
        return SHAPE;
    }

    @Override
    public BlockRenderType getRenderShape(BlockState state) {
        return BlockRenderType.INVISIBLE;
    }

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

    @Override
    public boolean addDestroyEffects(BlockState state, World world, BlockPos pos, ParticleManager manager) {
        return true;
    }

    @Override
    public boolean addLandingEffects(BlockState state1, ServerWorld worldserver, BlockPos pos, BlockState state2, LivingEntity entity, int numberOfParticles) {
        return true;
    }

    @Override
    public boolean addHitEffects(BlockState state, World worldObj, RayTraceResult target, ParticleManager manager) {
        return true;
    }

    @Override
    public boolean addRunningEffects(BlockState state, World world, BlockPos pos, Entity entity) {
        return true;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable IBlockReader worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        super.appendHoverText(stack, worldIn, tooltip, flagIn);
        tooltip.add(new StringTextComponent("//WIP"));
    }
}
