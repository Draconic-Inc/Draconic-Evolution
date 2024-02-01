package com.brandon3055.draconicevolution.blocks.reactor.tileentity;

import codechicken.lib.data.MCDataInput;
import com.brandon3055.brandonscore.blocks.TileBCore;
import com.brandon3055.brandonscore.lib.Vec3I;
import com.brandon3055.brandonscore.lib.datamanager.*;
import com.brandon3055.brandonscore.utils.MathUtils;
import com.brandon3055.brandonscore.utils.Utils;
import com.brandon3055.draconicevolution.utils.LogHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ChunkHolder;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;

/**
 * Created by brandon3055 on 20/01/2017.
 */
public abstract class TileReactorComponent extends TileBCore {

    private final ManagedVec3I coreOffset       = register(new ManagedVec3I("core_offset", DataFlags.SAVE_NBT_SYNC_TILE));
    public final ManagedEnum<Direction> facing  = register(new ManagedEnum<>("facing", Direction.UP, DataFlags.SAVE_NBT_SYNC_TILE));
    public final ManagedBool isBound            = register(new ManagedBool("is_bound", DataFlags.SAVE_NBT_SYNC_TILE));
    public final ManagedEnum<RSMode> rsMode     = register(new ManagedEnum<>("rs_mode", RSMode.TEMP, DataFlags.SAVE_NBT_SYNC_TILE));
    public final ManagedInt rsPower             = register(new ManagedInt("rs_power", DataFlags.SAVE_NBT_SYNC_TILE, DataFlags.TRIGGER_UPDATE));
    public float animRotation = 0;
    public float animRotationSpeed = 0;
    private TileReactorCore cachedCore = null;
    public boolean coreFalureIminent = false;
    private boolean moveCheckComplete = false;

    public TileReactorComponent(BlockEntityType<?> tileEntityTypeIn, BlockPos pos, BlockState state) {
        super(tileEntityTypeIn, pos, state);
    }

    @Override
    public int getAccessDistanceSq() {
        return 256;
    }

    //region update

    @Override
    public void tick() {
        super.tick();
        moveCheckComplete = false;

        if (level.isClientSide) {
            TileReactorCore core = tryGetCore();
            if (core != null) {
                animRotationSpeed = core.shieldAnimationState * 15F;
                coreFalureIminent = core.reactorState.get() == TileReactorCore.ReactorState.BEYOND_HOPE;
            }
            else {
                coreFalureIminent = false;
                animRotationSpeed = 0;
            }

            animRotation += animRotationSpeed;
            if (coreFalureIminent && level.random.nextInt(10) == 0) {
                animRotation += (level.random.nextDouble() - 0.5) * 360;
                if (level.random.nextBoolean()) {
                    level.addParticle(ParticleTypes.LARGE_SMOKE, worldPosition.getX() + level.random.nextDouble(), worldPosition.getY() + level.random.nextDouble(), worldPosition.getZ() + level.random.nextDouble(), 0, 0, 0);
                }
                else {
                    level.addParticle(ParticleTypes.CLOUD, worldPosition.getX() + level.random.nextDouble(), worldPosition.getY() + level.random.nextDouble(), worldPosition.getZ() + level.random.nextDouble(), 0, 0, 0);
                }
            }
        }
        else {
            TileReactorCore core = getCachedCore();

            if (core != null) {
                int rs = rsMode.get().getRSSignal(core);
                if (rs != rsPower.get()) {
                    rsPower.set(rs);
                    level.updateNeighborsAt(worldPosition, getBlockState().getBlock());
                }
            }
        }
    }

    //endregion

    //region============== Structure ==============

    /**
     * Called by the core itself to validate this component and bind it to the core.
     * This should only be called once the core has determined that this component is pointed at the core.
     * This ignores this components current active isBound state because if the core is calling this method then this component can not possibly be bound to any other core.!
     */
    public void bindToCore(TileReactorCore core) {
        LogHelper.dev("Reactor-Comp: Bind To Core");
        isBound.set(true);
        coreOffset.set(getCoreOffset(core.getBlockPos()));
    }

    /**
     * Finds the core if it iss location is not already stored and pokes it. Core then validates or revalidates the structure.
     */
    public void pokeCore() {
        LogHelper.dev("Reactor-Comp: Try Poke Core");
        if (isBound.get()) {
            TileReactorCore core = checkAndGetCore();
            if (core != null) {
                core.pokeCore(this, facing.get().getOpposite());
                return;
            }
        }

        LogHelper.dev("Reactor-Comp: Try Poke Core | Find");
        for (int i = 1; i < TileReactorCore.COMPONENT_MAX_DISTANCE; i++) {
            BlockPos searchPos = worldPosition.relative(facing.get(), i);
            if (!level.isEmptyBlock(searchPos)) {
                BlockEntity tile = level.getBlockEntity(searchPos);
                LogHelper.dev("Reactor-Comp: Check: " + tile);
                LogHelper.dev("Reactor-Comp: Try Poke Core | Found: " + tile);

                if (tile instanceof TileReactorCore && i > 1) {
                    //I want this to poke the core regardless of weather or not the core structure is already valid in case this is an energy injector. The core will decide what to do.
                    ((TileReactorCore) tile).pokeCore(this, facing.get().getOpposite());
                }
                return;
            }
        }
    }

    public void invalidateComponent() {
        isBound.set(false);
    }

    //endregion ===================================

    //region Player Interaction

    public void onPlaced() {
        if (level.isClientSide) {
            return;
        }
        pokeCore();
    }

    public void onBroken() {
        if (level.isClientSide) {
            return;
        }

        TileReactorCore core = checkAndGetCore();
        if (core != null) {
            core.componentBroken(this, facing.get().getOpposite());
        }
    }

    public void onActivated(Player player) {
        if (level.isClientSide) {
            return;
        }

        pokeCore();
        TileReactorCore core = checkAndGetCore();
        if (core != null) {
            core.onComponentClicked(player, this);
        }
    }

    public void setRSMode(Player player, RSMode rsMode) {
        if (level.isClientSide) {
            TileReactorCore core = tryGetCore();
            if (core != null) {
                core.sendPacketToServer(output -> output.writeString(rsMode.name()).writePos(getBlockPos()), 99);
            }
        }
        else {
            this.rsMode.set(rsMode);
        }
    }

    @Override
    public void receivePacketFromClient(MCDataInput data, ServerPlayer client, int id) {
        if (id == 0) {
            setRSMode(client, RSMode.valueOf(data.readString()));
        }
    }

    //endregion

    //region Getters & Setters

    protected BlockPos getCorePos() {
        return worldPosition.subtract(coreOffset.get().getPos());
    }

    protected Vec3I getCoreOffset(BlockPos corePos) {
        return new Vec3I(worldPosition.subtract(corePos));
    }

    /**
     * @return The core this component is bound to or null if not bound or core is nolonger at bound position. Invalidates the block if the core could not be found.
     */
    protected TileReactorCore checkAndGetCore() {
        if (!isBound.get()) {
            LogHelper.dev("Reactor-Comp: Not Bound");
            return null;
        }

        BlockEntity tile = level.getBlockEntity(getCorePos());
        if (tile instanceof TileReactorCore) {
            return (TileReactorCore) tile;
        }

        if (level.isAreaLoaded(getCorePos(), 16)) {
            invalidateComponent();
        }

        LogHelper.dev("Reactor-Comp: Core Connection Lost");
        return null;
    }

    public TileReactorCore tryGetCore() {
        if (!isBound.get()) {
            return null;
        }

        BlockEntity tile = level.getBlockEntity(getCorePos());
        if (tile instanceof TileReactorCore) {
            return (TileReactorCore) tile;
        }
        return null;
    }

    public TileReactorCore getCachedCore() {
        if (isBound.get()) {
            BlockPos corePos = getCorePos();
            LevelChunk coreChunk = level.getChunkAt(corePos);

            if (!level.isLoaded(corePos)) {
                cachedCore = null;
                return null;
            }

            BlockEntity tileAtPos = coreChunk.getBlockEntity(corePos, LevelChunk.EntityCreationType.CHECK);
            if (tileAtPos == null || cachedCore == null || tileAtPos != cachedCore || tileAtPos.isRemoved()) {
                BlockEntity tile = level.getBlockEntity(corePos);

                if (tile instanceof TileReactorCore) {
                    cachedCore = (TileReactorCore) tile;
                }
                else {
                    cachedCore = null;
                    isBound.set(false);
                }
            }
        }
        return cachedCore;
    }

    //endregion

//    @Override
//    public boolean canConnectEnergy(Direction from) {
//        return from == facing.get().getOpposite();
//    }

    public enum RSMode {
        TEMP {
            @Override
            public int getRSSignal(TileReactorCore tile) {
                return (int) ((tile.temperature.get() / TileReactorCore.MAX_TEMPERATURE) * 15D);
            }
        },
        TEMP_INV {
            @Override
            public int getRSSignal(TileReactorCore tile) {
                return 15 - TEMP.getRSSignal(tile);
            }
        },
        FIELD {
            @Override
            public int getRSSignal(TileReactorCore tile) {
                double value = tile.shieldCharge.get() / tile.maxShieldCharge.get();
                value -= 0.05;
                value *= 1.2;
                return (int) (value * 15);
            }
        },
        FIELD_INV {
            @Override
            public int getRSSignal(TileReactorCore tile) {
                return 15 - FIELD.getRSSignal(tile);
            }
        },
        SAT {
            @Override
            public int getRSSignal(TileReactorCore tile) {
                return (int) (((double) tile.saturation.get() / (double) tile.maxSaturation.get()) * 15D);
            }
        },
        SAT_INV {
            @Override
            public int getRSSignal(TileReactorCore tile) {
                return 15 - SAT.getRSSignal(tile);
            }
        },
        FUEL {
            @Override
            public int getRSSignal(TileReactorCore tile) {
                double value = tile.convertedFuel.get() / (tile.convertedFuel.get() + tile.reactableFuel.get());
                value += 0.1;
                value = MathUtils.map(value, 0.1, 1, 0, 1);
                return (int) (value * 15);
            }
        },
        FUEL_INV {
            @Override
            public int getRSSignal(TileReactorCore tile) {
                return 15 - FUEL.getRSSignal(tile);
            }
        };

        public abstract int getRSSignal(TileReactorCore tile);
    }

    //Frame movement

//    @Override
//    public Iterable<BlockPos> getBlocksForFrameMove() {
//        TileReactorCore core = getCachedCore();
//        if (core != null && !core.moveBlocksProvided) {
//            HashSet<BlockPos> blocks = new HashSet<>();
//            for (Direction facing : Direction.values()) {
//                TileReactorComponent comp = core.getComponent(facing);
//                if (comp != null) {
//                    blocks.add(comp.getPos());
//                }
//            }
//
//            blocks.add(core.getPos());
//            core.moveBlocksProvided = true;
//            return blocks;
//        }
//        return Collections.emptyList();
//    }

//    @Override
//    public EnumActionResult canMove() {
//        TileReactorCore core = getCachedCore();
//        if (core != null) {
//            if (core.isFrameMoving) {
//                return EnumActionResult.SUCCESS;
//            }
//            if (!moveCheckComplete) {
//                core.frameMoveContactPoints++;
//            }
//            HashSet<BlockPos> blocks = new HashSet<>();
//            for (Direction facing : Direction.values()) {
//                TileReactorComponent comp = core.getComponent(facing);
//                if (comp != null) {
//                    blocks.add(comp.getPos());
//                }
//            }
//
//            moveCheckComplete = true;
//            if (core.frameMoveContactPoints == blocks.size()) {
//                core.frameMoveContactPoints = 0;
//                core.isFrameMoving = true;
//                return EnumActionResult.SUCCESS;
//            }
//        }
//
//        return EnumActionResult.FAIL;
//    }
}
