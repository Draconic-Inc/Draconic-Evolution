package com.brandon3055.draconicevolution.blocks.tileentity;

import com.brandon3055.brandonscore.blocks.TileBCore;
import com.brandon3055.brandonscore.lib.IInteractTile;
import com.brandon3055.brandonscore.lib.datamanager.DataFlags;
import com.brandon3055.brandonscore.lib.datamanager.ManagedBool;
import com.brandon3055.brandonscore.lib.datamanager.ManagedPos;
import com.brandon3055.brandonscore.lib.datamanager.ManagedResource;
import com.brandon3055.draconicevolution.blocks.StructureBlock;
import com.brandon3055.draconicevolution.init.DEContent;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.client.event.RenderHighlightEvent;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 * Created by brandon3055 on 16/08/2022
 */
public class TileStructureBlock extends TileBCore implements IInteractTile {

    public final ManagedPos controllerOffset = register(new ManagedPos("controller_offset", (BlockPos) null, DataFlags.SAVE_NBT_SYNC_TILE, DataFlags.SYNC_ON_SET));
    public final ManagedResource blockName = register(new ManagedResource("block_name", DataFlags.SAVE_NBT_SYNC_TILE, DataFlags.SYNC_ON_SET));
    public final ManagedBool reverting = register(new ManagedBool("reverting", DataFlags.SAVE_NBT));

    public TileStructureBlock(BlockPos pos, BlockState state) {
        super(DEContent.TILE_STRUCTURE_BLOCK.get(), pos, state);
        enableTileDebug();
    }

    public void setController(MultiBlockController controller) {
        controllerOffset.set(worldPosition.subtract(((BlockEntity) controller).getBlockPos()));
    }

    @Override
    public void tick() {
        super.tick();
    }

    @Nullable
    private BlockPos getControllerPos() {
        return controllerOffset.get() == null ? null : worldPosition.subtract(Objects.requireNonNull(controllerOffset.get()));
    }

    @Nullable
    public MultiBlockController getController() {
        BlockPos pos = getControllerPos();
        if (pos != null) {
            BlockEntity tile = level.getBlockEntity(pos);
            if (tile instanceof MultiBlockController) {
                return (MultiBlockController) tile;
            }
        }

        debug("Structure Block: Reverting because controller not found");
        revert();
        return null;
    }

    public Block getOriginalBlock() {
        ResourceLocation name = blockName.get();
        if (name != null) {
            return BuiltInRegistries.BLOCK.get(name);
        }
        return Blocks.AIR;
    }

    public void revert() {
        if (level.isClientSide) {
            return;
        }

        StructureBlock.buildingLock = true;
        level.scheduleTick(worldPosition, DEContent.STRUCTURE_BLOCK.get(), 1);
        reverting.set(true);
        StructureBlock.buildingLock = false;
    }

    public void doRevert() {
        if (reverting.get()) {
            debug("Tile Structure Block: doRevert");
//            if (debugEnabled()) LogHelper.bigInfo("Trace");
            Block block = getOriginalBlock();
            if (block != null && block != Blocks.AIR) {
                level.setBlockAndUpdate(worldPosition, block.defaultBlockState());
                return;
            }

            level.removeBlock(worldPosition, false);
        }
    }

    @Override
    public InteractionResult onBlockUse(BlockState state, Player player, InteractionHand hand, BlockHitResult hit) {
        MultiBlockController controller = getController();
        if (controller != null) {
            return controller.handleRemoteClick(player, hand, hit);
        }
        return InteractionResult.PASS;
    }

    public VoxelShape getShape(CollisionContext context) {
        MultiBlockController controller = getController();
        return controller == null ? Shapes.empty() : controller.getShapeForPart(getBlockPos(), context);
    }

    @OnlyIn(Dist.CLIENT)
    public boolean renderSelectionBox(RenderHighlightEvent.Block event) {
        MultiBlockController controller = getController();
        return controller == null || controller.renderSelectionBox(event);
    }
}
