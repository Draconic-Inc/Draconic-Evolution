package com.brandon3055.draconicevolution.blocks.tileentity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.client.event.RenderHighlightEvent;

/**
 * The primary purpose of this class is to allow a TileStructureBlock to check the status of its controller
 *
 * Created by brandon3055 on 16/08/2022
 */
public interface MultiBlockController {

    /**
     * @return true if the multi-block structure is valid.
     */
    boolean isStructureValid();

    /**
     * Tells the controller to check if the structure is still valid and take appropriate action if the structure is no longer valid.
     * @return true if the structure is still valid.
     */
    boolean validateStructure();

    /**
     * Called when a structure block associated with this controller is right-clicked by a player.
     * */
    default InteractionResult handleRemoteClick(Player player, InteractionHand hand, BlockHitResult hit) {
        return InteractionResult.PASS;
    }

    default VoxelShape getShapeForPart(BlockPos pos, CollisionContext context) {
        return Shapes.block();
    }

    @OnlyIn (Dist.CLIENT)
    default boolean renderSelectionBox(RenderHighlightEvent.Block event) {
        return true;
    }
}
