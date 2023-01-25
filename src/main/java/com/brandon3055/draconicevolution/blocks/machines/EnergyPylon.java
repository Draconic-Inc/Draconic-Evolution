package com.brandon3055.draconicevolution.blocks.machines;

import codechicken.lib.math.MathHelper;
import com.brandon3055.brandonscore.blocks.BlockBCore;
import com.brandon3055.brandonscore.blocks.EntityBlockBCore;
import com.brandon3055.brandonscore.blocks.PropertyString;
import com.brandon3055.draconicevolution.blocks.StructureBlock;
import com.brandon3055.draconicevolution.blocks.tileentity.TileEnergyPylon;
import com.brandon3055.draconicevolution.init.DEContent;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.BlockHitResult;

import java.util.Locale;

/**
 * Created by brandon3055 on 30/3/2016.
 */
public class EnergyPylon extends EntityBlockBCore {

    public static final EnumProperty<Mode> MODE = EnumProperty.create("mode", Mode.class);
    public static final DirectionProperty FACING = DirectionProperty.create("facing");

    public EnergyPylon(Properties properties) {
        super(properties);
        this.registerDefaultState(stateDefinition.any().setValue(MODE, Mode.OUTPUT).setValue(FACING, Direction.UP));
        setBlockEntity(() -> DEContent.tile_energy_pylon, true);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(MODE, FACING);
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (level.getBlockEntity(pos) instanceof TileEnergyPylon tile) {
            if (!level.isClientSide) {
                if (player.isShiftKeyDown()) {
                    tile.selectNextCore();
                } else {
                    tile.validateStructure();
                    if (tile.coreOffset.notNull()) {
                        tile.drawParticleBeam();
                    }
                }
            }
            return tile.structureValid.get() ? InteractionResult.SUCCESS : InteractionResult.FAIL;
        }

        return InteractionResult.SUCCESS;
    }

    @Override
    public void neighborChanged(BlockState state, Level level, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving) {;
        if (level.getBlockEntity(pos) instanceof TileEnergyPylon tile && !StructureBlock.buildingLock) {
            tile.validateStructure();
        }
    }

    @Override
    public boolean hasAnalogOutputSignal(BlockState state) {
        return true;
    }

    @Override
    public int getAnalogOutputSignal(BlockState blockState, Level world, BlockPos pos) {
        if (world.getBlockEntity(pos) instanceof TileEnergyPylon tile && tile.opAdapter.getMaxOPStored() > 0) {
            return (int) MathHelper.clip(((double) tile.opAdapter.getOPStored() / tile.opAdapter.getMaxOPStored() * 15D), 0, 15);
        }
        return 0;
    }

    public enum Mode implements StringRepresentable {
        /** Output energy to the connected device / conduit */
        OUTPUT,
        /** Accept energy from the connected device / conduit */
        INPUT;

        public boolean canReceive() {
            return this == INPUT;
        }

        public boolean canExtract() {
            return this == OUTPUT;
        }

        public Mode reverse() {
            return this == OUTPUT ? INPUT : OUTPUT;
        }

        @Override
        public String getSerializedName() {
            return name().toLowerCase(Locale.ENGLISH);
        }
    }
}
