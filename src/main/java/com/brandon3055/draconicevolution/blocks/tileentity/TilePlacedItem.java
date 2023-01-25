package com.brandon3055.draconicevolution.blocks.tileentity;

import codechicken.lib.raytracer.SubHitBlockHitResult;
import codechicken.lib.vec.Vector3;
import com.brandon3055.brandonscore.blocks.TileBCore;
import com.brandon3055.brandonscore.inventory.TileItemStackHandler;
import com.brandon3055.brandonscore.lib.IInteractTile;
import com.brandon3055.brandonscore.lib.datamanager.DataFlags;
import com.brandon3055.brandonscore.lib.datamanager.ManagedBool;
import com.brandon3055.brandonscore.lib.datamanager.ManagedByte;
import com.brandon3055.brandonscore.network.BCoreNetwork;
import com.brandon3055.brandonscore.utils.InventoryUtils;
import com.brandon3055.draconicevolution.init.DEContent;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TieredItem;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.items.CapabilityItemHandler;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by brandon3055 on 25/07/2016.
 */
public class TilePlacedItem extends TileBCore implements IInteractTile {
    public static int MAX_STACKS = 4;

    /** The number of separate item stacks on display */
    public final ManagedByte stackCount = register(new ManagedByte("stack_count", (byte) 1, DataFlags.SAVE_NBT_SYNC_TILE));

    /** For large tool type items. Displays the item larger / closer to actual size when held. Only applicable when there is a single stack in the placed item. */
    public final ManagedBool toolMode = register(new ManagedBool("tool_mode", DataFlags.SAVE_NBT_SYNC_TILE));

    /** Stores the rotation of each individual stack in this placed item */
    public final ManagedByte[] rotation = new ManagedByte[MAX_STACKS];

    /** For each of the item stacks stored will be true if the stack contains a block item */
    public final ManagedBool[] isBlock = new ManagedBool[MAX_STACKS];

    //TODO / Something to think about. I could create a new item handler based dynamic inventory for this but i'm not sure if its needed. It should not take much effort to make this work.
    public TileItemStackHandler itemHandler = new TileItemStackHandler(MAX_STACKS);

    public TilePlacedItem(BlockPos pos, BlockState state) {
        super(DEContent.tile_placed_item, pos, state);
        for (int i = 0; i < MAX_STACKS; i++) {
            rotation[i] = register(new ManagedByte("rotation_" + i, DataFlags.SAVE_NBT_SYNC_TILE));
            isBlock[i] = register(new ManagedBool("is_block_" + i, DataFlags.SAVE_NBT_SYNC_TILE));
        }

        capManager.setInternalManaged("inventory", CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, itemHandler).saveBoth().syncTile();
        itemHandler.setContentsChangeListener(e -> updatePlacedItem());
    }

    private void updatePlacedItem() {
        List<ItemStack> stacks = getStacksInOrder();
        stackCount.set(stacks.size());
        for (int i = 0; i < stacks.size(); i++) {
            isBlock[i].set(stacks.get(i).getItem() instanceof BlockItem);
        }
        if (stacks.size() == 1) {
            ItemStack stack = stacks.get(0);
            toolMode.set(stack.getItem() instanceof TieredItem || stack.isDamageableItem());
        } else {
            toolMode.set(false);
        }
        tick();
    }

    /**
     * @return A list of all item stacks in this placed item in their display order ignoring any empty stacks.
     * This iterates in reverse order so that items render in the order they were placed.
     */
    public List<ItemStack> getStacksInOrder() {
        List<ItemStack> stacks = new ArrayList<>();
        for (int i = itemHandler.getSlots() - 1; i >= 0; i--) {
            if (!itemHandler.getStackInSlot(i).isEmpty()) {
                stacks.add(itemHandler.getStackInSlot(i));
            }
        }
        return stacks;
    }

    /**
     * Extracts the item stack at the specified index ignoring any empty slots in the item handler.
     * This iterates in reverse order to match getStacksInOrder
     */
    public ItemStack extractStackAtIndex(int index) {
        int current = 0;
        for (int i = itemHandler.getSlots() - 1; i >= 0; i--) {
            ItemStack stack = itemHandler.getStackInSlot(i);
            if (stack.isEmpty()) {
                continue;
            }
            if (index == current) {
                itemHandler.setStackInSlot(i, ItemStack.EMPTY);
                return stack;
            }
            current++;
        }
        return ItemStack.EMPTY;
    }

    public boolean[] getBlockArray() {
        boolean[] bools = new boolean[MAX_STACKS];
        for (int i = 0; i < MAX_STACKS; i++) {
            bools[i] = isBlock[i].get();
        }
        return bools;
    }

    @Override
    public InteractionResult onBlockUse(BlockState state, Player player, InteractionHand hand, BlockHitResult hit) {
        if (player.level.isClientSide()) return InteractionResult.SUCCESS;
        List<ItemStack> stacks = getStacksInOrder();
        if (!(hit instanceof SubHitBlockHitResult)){
            return InteractionResult.PASS;
        }

        int index = ((SubHitBlockHitResult) hit).subHit - 1;

        ItemStack held = player.getItemInHand(hand);
        if (!held.isEmpty() && held.getItem() == DEContent.crystal_binder && getStacksInOrder().size() == 1) {
            toolMode.invert();
            tick();
            return InteractionResult.SUCCESS;
        }

        if (player.isShiftKeyDown()) {
            if (index >= 0 && index < rotation.length) {
                rotation[index].inc();
                BCoreNetwork.sendSound(level, worldPosition, SoundEvents.ITEM_FRAME_ROTATE_ITEM, SoundSource.PLAYERS, 1.0F, 0.9F + level.random.nextFloat() * 0.2F, false);
                tick();
            }
            return InteractionResult.SUCCESS;
        }

        if (index == -1) {
            onBroken(player, Vector3.fromEntityCenter(player), true);
            level.removeBlock(getBlockPos(), false);
            return InteractionResult.SUCCESS;
        }

        if (index < stacks.size()) {
            ItemStack stack = extractStackAtIndex(index);
            InventoryUtils.givePlayerStack(player, stack);
            if (stacks.size() == 1) {
                level.removeBlock(getBlockPos(), false);
            }
        }

        return InteractionResult.SUCCESS;
    }

    @Override
    public void onBlockAttack(BlockState state, Player player) {
        if (!player.isShiftKeyDown()) return;
        List<ItemStack> stacks = getStacksInOrder();
        if (stacks.size() == 1 && !(stacks.get(0).getItem() instanceof BlockItem)) {
            toolMode.invert();
            BCoreNetwork.sendSound(level, worldPosition, SoundEvents.ITEM_FRAME_ROTATE_ITEM, SoundSource.PLAYERS, 1.0F, 0.9F + level.random.nextFloat() * 0.2F, false);
            tick();
        } else {
            HitResult hit = player.pick(4, 0, false);
            if (hit instanceof SubHitBlockHitResult && hit.getType() == HitResult.Type.BLOCK && ((SubHitBlockHitResult) hit).subHit > 0 && ((SubHitBlockHitResult) hit).subHit - 1 < rotation.length) {
                rotation[((SubHitBlockHitResult) hit).subHit - 1].dec();
                BCoreNetwork.sendSound(level, worldPosition, SoundEvents.ITEM_FRAME_ROTATE_ITEM, SoundSource.PLAYERS, 1.0F, 0.9F + level.random.nextFloat() * 0.2F, false);
                tick();
            }
        }
    }

    public void onBroken(Player player, Vector3 dropPos, boolean noPickupDelay) {
        for (int i = 0; i < itemHandler.getSlots(); i++) {
            if (!player.getAbilities().instabuild) {
                popResource(level, dropPos, itemHandler.getStackInSlot(i), noPickupDelay);
            }
            itemHandler.setStackInSlot(i, ItemStack.EMPTY);
        }
    }

    public static void popResource(Level world, Vector3 pos, ItemStack stack, boolean noPickupDelay) {
        if (!world.isClientSide && !stack.isEmpty() && world.getGameRules().getBoolean(GameRules.RULE_DOBLOCKDROPS) && !world.restoringBlockSnapshots) {
            double d0 = (double) (world.random.nextFloat() * 0.5F) + 0.25D;
            double d1 = (double) (world.random.nextFloat() * 0.5F) + 0.25D;
            double d2 = (double) (world.random.nextFloat() * 0.5F) + 0.25D;
            ItemEntity itementity = new ItemEntity(world, pos.x + d0, pos.y + d1, pos.z + d2, stack);
            if (noPickupDelay) {
                itementity.setNoPickUpDelay();
            } else {
                itementity.setDefaultPickUpDelay();
            }
            world.addFreshEntity(itementity);
        }
    }

    @Override
    public boolean saveToItem() {
        return false;
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public AABB getRenderBoundingBox() {
        return new AABB(worldPosition.offset(-1, -1, -1), worldPosition.offset(2, 2, 2));
    }
}
