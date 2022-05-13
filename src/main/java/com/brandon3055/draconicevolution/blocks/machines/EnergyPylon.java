package com.brandon3055.draconicevolution.blocks.machines;

import com.brandon3055.brandonscore.blocks.BlockBCore;
import com.brandon3055.brandonscore.blocks.PropertyString;
import com.brandon3055.draconicevolution.blocks.tileentity.TileEnergyPylon;
import com.brandon3055.draconicevolution.init.DEContent;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;

/**
 * Created by brandon3055 on 30/3/2016.
 */
public class EnergyPylon extends BlockBCore implements EntityBlock {

    public static final BooleanProperty OUTPUT = BooleanProperty.create("output");
    public static final PropertyString FACING = new PropertyString("facing", "up", "down", "null");

    public EnergyPylon(Properties properties) {
        super(properties);
        this.registerDefaultState(stateDefinition.any().setValue(OUTPUT, false).setValue(FACING, "null"));
        setBlockEntity(() -> DEContent.tile_energy_pylon, true);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(OUTPUT, FACING);
    }

    @Override
    public InteractionResult use(BlockState state, Level worldIn, BlockPos pos, Player player, InteractionHand handIn, BlockHitResult hit) {
        BlockEntity tile = worldIn.getBlockEntity(pos);
        if (tile instanceof TileEnergyPylon) {
            if (player.isShiftKeyDown()) {
                ((TileEnergyPylon) tile).selectNextCore();
            } else {
                ((TileEnergyPylon) tile).validateStructure();
            }
            return ((TileEnergyPylon) tile).structureValid.get() ? InteractionResult.SUCCESS : InteractionResult.FAIL;
        }

        return InteractionResult.SUCCESS;
    }

    @Override
    public void neighborChanged(BlockState state, Level world, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving) {
        BlockEntity tile = world.getBlockEntity(pos);

        if (tile instanceof TileEnergyPylon) {
            ((TileEnergyPylon) tile).validateStructure();
        }
    }

    @Override
    public boolean hasAnalogOutputSignal(BlockState state) {
        return true;
    }

    @Override
    public int getAnalogOutputSignal(BlockState blockState, Level world, BlockPos pos) {
        BlockEntity tile = world.getBlockEntity(pos);
        if (tile != null && tile instanceof TileEnergyPylon && ((TileEnergyPylon) tile).getExtendedCapacity() > 0) {
            return (int) ((double) ((TileEnergyPylon) tile).getExtendedStorage() / ((TileEnergyPylon) tile).getExtendedCapacity() * 15D);
        }
        return 0;
    }
}
