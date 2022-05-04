package com.brandon3055.draconicevolution.blocks.reactor;

import codechicken.lib.util.RotationUtils;
import com.brandon3055.brandonscore.blocks.BlockBCore;
import com.brandon3055.draconicevolution.blocks.reactor.tileentity.TileReactorComponent;
import com.brandon3055.draconicevolution.blocks.reactor.tileentity.TileReactorInjector;
import com.brandon3055.draconicevolution.blocks.reactor.tileentity.TileReactorStabilizer;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Created by brandon3055 on 18/01/2017.
 */
public class ReactorComponent extends BlockBCore {

    private static final VoxelShape SHAPE_INJ_DOWN  = VoxelShapes.box(0F, 0.885F, 0F, 1F, 1F, 1F);
    private static final VoxelShape SHAPE_INJ_UP    = VoxelShapes.box(0F, 0F, 0F, 1F, 0.125F, 1F);
    private static final VoxelShape SHAPE_INJ_NORTH = VoxelShapes.box(0F, 0F, 0.885F, 1F, 1F, 1F);
    private static final VoxelShape SHAPE_INJ_SOUTH = VoxelShapes.box(0F, 0F, 0F, 1F, 1F, 0.125F);
    private static final VoxelShape SHAPE_INJ_WEST  = VoxelShapes.box(0.885F, 0F, 0F, 1F, 1F, 1F);
    private static final VoxelShape SHAPE_INJ_EAST  = VoxelShapes.box(0F, 0F, 0F, 0.125F, 1F, 1F);
    private final boolean injector;

    public ReactorComponent(Properties properties, boolean injector) {
        super(properties);
        this.injector = injector;
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
        return injector ? new TileReactorInjector() : new TileReactorStabilizer();
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader world, BlockPos pos, ISelectionContext context) {
        TileEntity tile = world.getBlockEntity(pos);

        if (tile instanceof TileReactorInjector) {
            switch (((TileReactorInjector) tile).facing.get()) {
                case DOWN:
                    return SHAPE_INJ_DOWN;
                case UP:
                    return SHAPE_INJ_UP;
                case NORTH:
                    return SHAPE_INJ_NORTH;
                case SOUTH:
                    return SHAPE_INJ_SOUTH;
                case WEST:
                    return SHAPE_INJ_WEST;
                case EAST:
                    return SHAPE_INJ_EAST;
            }
        }
        return super.getShape(state, world, pos, context);
    }

    @Override
    public BlockRenderType getRenderShape(BlockState state) {
        return BlockRenderType.INVISIBLE;
    }

    @Override
    public void setPlacedBy(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
        super.setPlacedBy(world, pos, state, placer, stack);
        TileEntity te = world.getBlockEntity(pos);
        Direction facing = RotationUtils.getPlacedRotation(pos, placer).getOpposite();

        if (te instanceof TileReactorComponent) {
            ((TileReactorComponent) te).facing.set(facing);
            ((TileReactorComponent) te).onPlaced();
        }
    }

    @Override
    public ActionResultType use(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
        TileEntity te = world.getBlockEntity(pos);

        if (te instanceof TileReactorComponent) {
            ((TileReactorComponent) te).onActivated(player);
        }
        return ActionResultType.SUCCESS;
    }

    @Override
    public void onRemove(BlockState state, World worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
        TileEntity te = worldIn.getBlockEntity(pos);

        if (te instanceof TileReactorComponent) {
            ((TileReactorComponent) te).onBroken();
        }
        super.onRemove(state, worldIn, pos, newState, isMoving);
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void appendHoverText(ItemStack stack, @Nullable IBlockReader worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
//        tooltip.add(new TranslationTextComponent("info.de.shiftReversePlaceLogic.txt"));
        super.appendHoverText(stack, worldIn, tooltip, flagIn);
    }

    @Override
    public boolean hasAnalogOutputSignal(BlockState state) {
        return true;
    }

    @Override
    public int getAnalogOutputSignal(BlockState blockState, World worldIn, BlockPos pos) {
        TileEntity tileEntity = worldIn.getBlockEntity(pos);

        if (tileEntity instanceof TileReactorComponent) {
            return ((TileReactorComponent) tileEntity).rsPower.get();
        }

        return 0;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public boolean addDestroyEffects(BlockState state, World world, BlockPos pos, ParticleManager manager) {
        return true;
    }

    @Override
    public boolean addLandingEffects(BlockState state1, ServerWorld worldserver, BlockPos pos, BlockState state2, LivingEntity entity, int numberOfParticles) {
        return true;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public boolean addHitEffects(BlockState state, World worldObj, RayTraceResult target, ParticleManager manager) {
        return true;
    }

    @Override
    public boolean addRunningEffects(BlockState state, World world, BlockPos pos, Entity entity) {
        return true;
    }
}
