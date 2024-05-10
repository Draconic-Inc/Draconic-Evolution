package com.brandon3055.draconicevolution.blocks.energynet;

import codechicken.lib.vec.Cuboid6;
import codechicken.lib.vec.Rotation;
import codechicken.lib.vec.Vector3;
import com.brandon3055.brandonscore.api.TechLevel;
import com.brandon3055.brandonscore.api.hud.IHudBlock;
import com.brandon3055.brandonscore.blocks.EntityBlockBCore;
import com.brandon3055.brandonscore.blocks.TileBCore;
import com.brandon3055.draconicevolution.blocks.energynet.tileentity.TileCrystalBase;
import com.brandon3055.draconicevolution.blocks.energynet.tileentity.TileCrystalDirectIO;
import com.brandon3055.draconicevolution.blocks.energynet.tileentity.TileCrystalRelay;
import com.brandon3055.draconicevolution.blocks.energynet.tileentity.TileCrystalWirelessIO;
import com.brandon3055.draconicevolution.init.DEContent;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Locale;
import java.util.function.Supplier;

/**
 * Created by brandon3055 on 19/11/2016.
 */
public class EnergyCrystal extends EntityBlockBCore implements IHudBlock {
    private final TechLevel techLevel;
    private final CrystalType crystalType;
    private static VoxelShape CRYSTAL_SHAPE = Shapes.create(new AABB(0.375, 0.125, 0.375, 0.625, 0.875, 0.625));
    private static VoxelShape[] IO_CRYSTAL_SHAPES = new VoxelShape[6];

    static {
        for (Direction dir : Direction.values()) {
            Cuboid6 c = new Cuboid6(0.35, 0, 0.35, 0.65, 0.425, 0.65);
            c.apply(Rotation.sideRotations[dir.get3DDataValue()].at(Vector3.CENTER));
            IO_CRYSTAL_SHAPES[dir.get3DDataValue()] = Shapes.create(c.aabb());
        }
    }

    public EnergyCrystal(Properties properties, TechLevel techLevel, CrystalType crystalType) {
        super(properties);
        this.techLevel = techLevel;
        this.crystalType = crystalType;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return crystalType.createTile(techLevel, pos, state);
    }

    @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> entityType) {
        if (entityType == crystalType.type.get()) {
            return (e, e2, e3, tile) -> ((TileBCore) tile).tick();
        }
        return null;
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        if (crystalType == CrystalType.CRYSTAL_IO) {
            BlockEntity tile = world.getBlockEntity(pos);
            Direction facing = tile instanceof TileCrystalDirectIO ? ((TileCrystalDirectIO) tile).facing.get() : Direction.DOWN;
            return IO_CRYSTAL_SHAPES[facing.get3DDataValue()];
        }
        return CRYSTAL_SHAPE; //Crystal
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.INVISIBLE;
    }

    @Override
    public void generateHudText(Level world, BlockPos pos, Player player, List<Component> displayList) {
        BlockEntity te = world.getBlockEntity(pos);
        if (!(te instanceof TileCrystalBase)) return;

        displayList.add(Component.translatable(asItem().getDescriptionId()).withStyle(ChatFormatting.ITALIC, ChatFormatting.GOLD));
        TileCrystalBase tile = (TileCrystalBase) te;
        tile.addDisplayData(displayList);
    }

    public enum CrystalType implements StringRepresentable {
        RELAY(0, DEContent.TILE_RELAY_CRYSTAL::get) {
            @Override
            public BlockEntity createTile(TechLevel techLevel, BlockPos pos, BlockState state) {
                return new TileCrystalRelay(techLevel, pos, state);
            }
        },
        CRYSTAL_IO(1, DEContent.TILE_IO_CRYSTAL::get) {
            @Override
            public BlockEntity createTile(TechLevel techLevel, BlockPos pos, BlockState state) {
                return new TileCrystalDirectIO(techLevel, pos, state);
            }
        },
        WIRELESS(2, DEContent.TILE_WIRELESS_CRYSTAL::get) {
            @Override
            public BlockEntity createTile(TechLevel techLevel, BlockPos pos, BlockState state) {
                return new TileCrystalWirelessIO(techLevel, pos, state);
            }
        };

        private final int index;
        private Supplier<BlockEntityType<?>> type;

        CrystalType(int index, Supplier<BlockEntityType<?>> type) {
            this.index = index;
            this.type = type;
        }

        public int getIndex() {
            return index;
        }

        private static CrystalType fromIndex(int i) {
            return i == 0 ? RELAY : i == 1 ? CRYSTAL_IO : WIRELESS;
        }

        @Override
        public String getSerializedName() {
            return name().toLowerCase(Locale.ENGLISH);
        }

        public abstract BlockEntity createTile(TechLevel techLevel, BlockPos pos, BlockState state);
    }
}
