package com.brandon3055.draconicevolution.blocks.reactor;

import com.brandon3055.brandonscore.blocks.BlockBCore;
import com.brandon3055.draconicevolution.blocks.reactor.tileentity.TileReactorCore;
import com.brandon3055.draconicevolution.init.DEContent;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

/**
 * Created by brandon3055 on 6/11/2016.
 */
public class ReactorCore extends BlockBCore implements EntityBlock {

    private static final VoxelShape NO_AABB = Shapes.box(0.5, 0.5, 0.5, 0.5, 0.5, 0.5);
    private static final VoxelShape AABB = Shapes.box(0.25, 0.25, 0.25, 0.75, 0.75, 0.75);

    public ReactorCore(Properties properties) {
        super(properties);
        setBlockEntity(() -> DEContent.tile_reactor_core, true);
    }

    @Override
    public float getExplosionResistance(BlockState state, BlockGetter world, BlockPos pos, Explosion explosion) {
        BlockEntity tile = world.getBlockEntity(pos);
        if (tile instanceof TileReactorCore) {
            return ((TileReactorCore) tile).reactorState.get().isShieldActive() ? 6000000.0F : super.getExplosionResistance(state, world, pos, explosion);
        }

        return super.getExplosionResistance(state, world, pos, explosion);
    }

    @Override
    public float getDestroyProgress(BlockState state, Player player, BlockGetter world, BlockPos pos) {
        BlockEntity tile = world.getBlockEntity(pos);
        if (tile instanceof TileReactorCore) {
            return ((TileReactorCore) tile).reactorState.get().isShieldActive() ? -1F : super.getDestroyProgress(state, player, world, pos);
        }
        return super.getDestroyProgress(state, player, world, pos);
    }

    //region Rendering

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.INVISIBLE;
    }

    @Override
    public void onBlockExploded(BlockState state, Level world, BlockPos pos, Explosion explosion) {
        BlockEntity tile = world.getBlockEntity(pos);
        if (tile instanceof TileReactorCore && ((TileReactorCore) tile).reactorState.get().isShieldActive()) {
            return;
        }
        super.onBlockExploded(state, world, pos, explosion);
    }

    @Override
    public VoxelShape getOcclusionShape(BlockState state, BlockGetter worldIn, BlockPos pos) {
        return Shapes.block();
    }

    @Override
    public VoxelShape getInteractionShape(BlockState state, BlockGetter worldIn, BlockPos pos) {
        return Shapes.block();
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
        return Shapes.block();
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
        return AABB;
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
