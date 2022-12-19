package com.brandon3055.draconicevolution.blocks.reactor;

import codechicken.lib.util.RotationUtils;
import com.brandon3055.brandonscore.blocks.BlockBCore;
import com.brandon3055.brandonscore.blocks.EntityBlockBCore;
import com.brandon3055.draconicevolution.blocks.reactor.tileentity.TileReactorComponent;
import com.brandon3055.draconicevolution.blocks.reactor.tileentity.TileReactorInjector;
import com.brandon3055.draconicevolution.init.DEContent;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Created by brandon3055 on 18/01/2017.
 */
public class ReactorComponent extends EntityBlockBCore {

    private static final VoxelShape SHAPE_INJ_DOWN  = Shapes.box(0F, 0.885F, 0F, 1F, 1F, 1F);
    private static final VoxelShape SHAPE_INJ_UP    = Shapes.box(0F, 0F, 0F, 1F, 0.125F, 1F);
    private static final VoxelShape SHAPE_INJ_NORTH = Shapes.box(0F, 0F, 0.885F, 1F, 1F, 1F);
    private static final VoxelShape SHAPE_INJ_SOUTH = Shapes.box(0F, 0F, 0F, 1F, 1F, 0.125F);
    private static final VoxelShape SHAPE_INJ_WEST  = Shapes.box(0.885F, 0F, 0F, 1F, 1F, 1F);
    private static final VoxelShape SHAPE_INJ_EAST  = Shapes.box(0F, 0F, 0F, 0.125F, 1F, 1F);
    private final boolean injector;

    public ReactorComponent(Properties properties, boolean injector) {
        super(properties);
        this.injector = injector;
        setBlockEntity(() -> injector ? DEContent.tile_reactor_injector : DEContent.tile_reactor_stabilizer, true);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        BlockEntity tile = world.getBlockEntity(pos);

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
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.INVISIBLE;
    }

    @Override
    public void setPlacedBy(Level world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
        super.setPlacedBy(world, pos, state, placer, stack);
        BlockEntity te = world.getBlockEntity(pos);
        Direction facing = RotationUtils.getPlacedRotation(pos, placer).getOpposite();

        if (te instanceof TileReactorComponent) {
            ((TileReactorComponent) te).facing.set(facing);
            ((TileReactorComponent) te).onPlaced();
        }
    }

    @Override
    public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand handIn, BlockHitResult hit) {
        BlockEntity te = world.getBlockEntity(pos);

        if (te instanceof TileReactorComponent) {
            ((TileReactorComponent) te).onActivated(player);
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public void onRemove(BlockState state, Level worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
        BlockEntity te = worldIn.getBlockEntity(pos);

        if (te instanceof TileReactorComponent) {
            ((TileReactorComponent) te).onBroken();
        }
        super.onRemove(state, worldIn, pos, newState, isMoving);
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void appendHoverText(ItemStack stack, @Nullable BlockGetter worldIn, List<Component> tooltip, TooltipFlag flagIn) {
        super.appendHoverText(stack, worldIn, tooltip, flagIn);
    }

    @Override
    public boolean hasAnalogOutputSignal(BlockState state) {
        return true;
    }

    @Override
    public int getAnalogOutputSignal(BlockState blockState, Level worldIn, BlockPos pos) {
        BlockEntity tileEntity = worldIn.getBlockEntity(pos);

        if (tileEntity instanceof TileReactorComponent) {
            return ((TileReactorComponent) tileEntity).rsPower.get();
        }

        return 0;
    }

    @Override
    protected void spawnDestroyParticles(Level level, Player player, BlockPos pos, BlockState state) {
        level.levelEvent(player, 2001, pos, getId(state));
    }

    @Override
    public boolean addLandingEffects(BlockState state1, ServerLevel worldserver, BlockPos pos, BlockState state2, LivingEntity entity, int numberOfParticles) {
        return true;
    }

    @Override
    public boolean addRunningEffects(BlockState state, Level world, BlockPos pos, Entity entity) {
        return true;
    }
}
