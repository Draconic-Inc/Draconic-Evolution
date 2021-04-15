package com.brandon3055.draconicevolution.blocks.tileentity;

import codechicken.lib.raytracer.IndexedCuboid6;
import codechicken.lib.vec.*;
import com.brandon3055.brandonscore.blocks.TileBCore;
import com.brandon3055.brandonscore.lib.Vec3D;
import com.brandon3055.brandonscore.lib.datamanager.ManagedBool;
import com.brandon3055.brandonscore.lib.datamanager.ManagedByte;
import com.brandon3055.brandonscore.network.BCoreNetwork;
import com.brandon3055.brandonscore.utils.FeatureUtils;
import com.brandon3055.draconicevolution.init.DEContent;
import com.brandon3055.draconicevolution.blocks.PlacedItem;
import com.brandon3055.draconicevolution.integration.ModHelper;
import com.brandon3055.draconicevolution.handlers.DESounds;
import com.brandon3055.draconicevolution.utils.LogHelper;
import com.google.common.collect.ImmutableList;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static com.brandon3055.brandonscore.lib.datamanager.DataFlags.SAVE_NBT_SYNC_TILE;
import static com.brandon3055.brandonscore.lib.datamanager.DataFlags.TRIGGER_UPDATE;

/**
 * Created by brandon3055 on 25/07/2016.
 */
//TODO talk/yell at covers
public class TilePlacedItem extends TileBCore /*implements ICuboidProvider*/ {

    public final ManagedByte displayCount = register(new ManagedByte("display_count", (byte)1, SAVE_NBT_SYNC_TILE, TRIGGER_UPDATE));
    public final ManagedBool toolDisplay = register(new ManagedBool("tool_display", SAVE_NBT_SYNC_TILE, TRIGGER_UPDATE));
    public final ManagedBool altRenderMode = register(new ManagedBool("alt_render_mode", SAVE_NBT_SYNC_TILE, TRIGGER_UPDATE));
    public final ManagedByte[] rotation = new ManagedByte[4];
    public Direction facing = Direction.NORTH;
    private boolean[] isBlock = new boolean[]{false, false, false, false};
    public PlacedItemInventory inventory = new PlacedItemInventory(this);

    public TilePlacedItem() {
        super(DEContent.tile_placed_item);
        for (int i = 0; i < rotation.length; i++) {
            rotation[i] = register(new ManagedByte("rotation" + i, SAVE_NBT_SYNC_TILE, TRIGGER_UPDATE));
        }
    }

    //region Bounds / Interaction

    public void handleClick(int hit, PlayerEntity player) {
        if (!player.getMainHandItem().isEmpty() && ModHelper.isWrench(player.getMainHandItem())) {
            altRenderMode.invert();
            LogHelper.dev(altRenderMode);
            super.tick();
            return;
        }

        if (player.isShiftKeyDown()) {
            if (hit == -1) {
                return;
            }

            int index = hit - 1;

            if (index >= 0 && index < rotation.length) {
                rotation[index].inc();
                BCoreNetwork.sendSound(level, worldPosition, SoundEvents.ITEM_FRAME_ROTATE_ITEM, SoundCategory.PLAYERS, 1.0F, 0.9F + level.random.nextFloat() * 0.2F, false);
                super.tick();
            }

            return;
        }

        if (hit == 0) {
            for (int i = 0; i < inventory.getContainerSize(); i++) {
                FeatureUtils.dropItemNoDellay(inventory.getItem(i), level, Vector3.fromEntity(player));
            }
            inventory.stacks.clear();
            level.removeBlock(worldPosition, false);
        }
        else {
            if (!inventory.getItem(hit - 1).isEmpty()) {
                FeatureUtils.dropItemNoDellay(inventory.getItem(hit - 1), level, Vector3.fromEntity(player));
                inventory.setItem(hit - 1, ItemStack.EMPTY);
            }
        }
    }

    public void breakBlock() {
        for (int i = 0; i < inventory.getContainerSize(); i++) {
            FeatureUtils.dropItemNoDellay(inventory.getItem(i), level, Vector3.fromTileCenter(this));
        }
        inventory.stacks.clear();
    }

    private IndexedCuboid6 blockBounds = new IndexedCuboid6(0, new Cuboid6(0, 0, 0, 1, 1, 1));
    private List<IndexedCuboid6> indexedCuboids = new LinkedList<>();

    private synchronized void calculateBounds() {

        BlockState state = getBlockState();//world.getBlockState(getPos());
        Cuboid6 box = new Cuboid6(0.5, 0, 0.5, 0.5, 0, 0.5).apply(Rotation.sideRotations[state.getValue(PlacedItem.FACING).get3DDataValue()].at(Vector3.CENTER));

        int i = 0;
        for (Cuboid6 cuboid : indexedCuboids) {
            i++;
            box.enclose(cuboid);
        }

        if (facing.getAxisDirection() == Direction.AxisDirection.NEGATIVE) {
            box.setSide(facing.get3DDataValue() ^ 1, 0.01);
        }
        else {
            box.setSide(facing.get3DDataValue() ^ 1, 0.99);
        }


        if (i > 1) {
            Direction.Axis axis = facing.getAxis();
            box.expand(new Vector3(axis == Direction.Axis.X ? -0.02 : 0.03, axis == Direction.Axis.Y ? -0.02 : 0.03, axis == Direction.Axis.Z ? -0.02 : 0.03));
        }

        blockBounds = new IndexedCuboid6(0, box);
    }

    private synchronized void recalculateCuboids() {
        BlockState state = level.getBlockState(getBlockPos());
        if (state.getBlock() != DEContent.placed_item) {
            return;
        }
        indexedCuboids = new ArrayList<>();

        double scale = displayCount.get() == 1 && (toolDisplay.get() || altRenderMode.get()) ? 0.2 : 0.32;

        Transformation rotation = rotations[state.getValue(PlacedItem.FACING).get3DDataValue()].at(Vector3.CENTER);

        double offset = 0.225;
        double blockH = 0.36;
        double itemH = 0.03;

        if (displayCount.get() == 1) {
            indexedCuboids.add(new IndexedCuboid6(1, new Cuboid6(scale, scale, 0, 1 - scale, 1 - scale, isBlock[0] ? blockH : itemH).apply(rotation)));
        }
        else if (displayCount.get() == 2) {
            indexedCuboids.add(new IndexedCuboid6(1, new Cuboid6(scale, scale, 0, 1 - scale, 1 - scale, isBlock[0] ? blockH : itemH).apply(new Translation(-offset, 0, 0).with(rotation))));
            indexedCuboids.add(new IndexedCuboid6(2, new Cuboid6(scale, scale, 0, 1 - scale, 1 - scale, isBlock[1] ? blockH : itemH).apply(new Translation(offset, 0, 0).with(rotation))));
        }
        else if (displayCount.get() == 3) {
            indexedCuboids.add(new IndexedCuboid6(1, new Cuboid6(scale, scale, 0, 1 - scale, 1 - scale, isBlock[0] ? blockH : itemH).apply(new Translation(0, offset, 0).with(rotation))));
            indexedCuboids.add(new IndexedCuboid6(2, new Cuboid6(scale, scale, 0, 1 - scale, 1 - scale, isBlock[1] ? blockH : itemH).apply(new Translation(-offset, -offset, 0).with(rotation))));
            indexedCuboids.add(new IndexedCuboid6(3, new Cuboid6(scale, scale, 0, 1 - scale, 1 - scale, isBlock[2] ? blockH : itemH).apply(new Translation(offset, -offset, 0).with(rotation))));
        }
        else if (displayCount.get() == 4) {
            indexedCuboids.add(new IndexedCuboid6(1, new Cuboid6(scale, scale, 0, 1 - scale, 1 - scale, isBlock[0] ? blockH : itemH).apply(new Translation(-offset, offset, 0).with(rotation))));
            indexedCuboids.add(new IndexedCuboid6(2, new Cuboid6(scale, scale, 0, 1 - scale, 1 - scale, isBlock[1] ? blockH : itemH).apply(new Translation(offset, offset, 0).with(rotation))));
            indexedCuboids.add(new IndexedCuboid6(3, new Cuboid6(scale, scale, 0, 1 - scale, 1 - scale, isBlock[2] ? blockH : itemH).apply(new Translation(-offset, -offset, 0).with(rotation))));
            indexedCuboids.add(new IndexedCuboid6(4, new Cuboid6(scale, scale, 0, 1 - scale, 1 - scale, isBlock[3] ? blockH : itemH).apply(new Translation(offset, -offset, 0).with(rotation))));
        }
        indexedCuboids = ImmutableList.copyOf(indexedCuboids);
    }

    private static final Transformation[] rotations = new Transformation[]{Rotation.sideRotations[3], Rotation.sideRotations[2], Rotation.sideRotations[0], Rotation.sideRotations[1], Rotation.quarterRotations[3], Rotation.quarterRotations[1],};

    public synchronized List<IndexedCuboid6> getCachedRenderCuboids() {
        if (indexedCuboids.isEmpty()) {
            recalculateCuboids();
        }
        return indexedCuboids;
    }

/*
    @Override
    public synchronized List<IndexedCuboid6> getIndexedCuboids() {
        recalculateCuboids();
        calculateBounds();
        List<IndexedCuboid6> list = new ArrayList<IndexedCuboid6>();
        list.add(blockBounds);
        list.addAll(indexedCuboids);
        return list;
    }
*/

    //endregion

    //region inventory

    @Override
    public void updateBlock() {
        super.updateBlock();
        recalculateCuboids();
    }

    //endregion

    //region save

    @Override
    public void writeExtraNBT(CompoundNBT compound) {
        super.writeExtraNBT(compound);
        compound.putByte("Facing", (byte) facing.get3DDataValue());
        for (int i = 0; i < isBlock.length; i++) {
            compound.putBoolean("IsBlock" + i, isBlock[i]);
        }
        inventory.toNBT(compound);
    }

    @Override
    public void readExtraNBT(CompoundNBT compound) {
        super.readExtraNBT(compound);
        facing = Direction.from3DDataValue(compound.getByte("Facing"));
        for (int i = 0; i < isBlock.length; i++) {
            isBlock[i] = compound.getBoolean("IsBlock" + i);
        }
        inventory.fromNBT(compound);
    }

    //endregion


    @OnlyIn(Dist.CLIENT)
    @Override
    public AxisAlignedBB getRenderBoundingBox() {
        return new AxisAlignedBB(worldPosition.offset(-1, -1, -1), worldPosition.offset(2, 2, 2));
    }

    public static class PlacedItemInventory implements IInventory {
        private LinkedList<ItemStack> stacks = new LinkedList<ItemStack>();
        private TilePlacedItem tile;

        public PlacedItemInventory(TilePlacedItem tile) {
            this.tile = tile;
        }

        @Override
        public void setItem(int index, ItemStack stack) {
            setSlot(index, stack);

            int count = 0;
            for (int i = 0; i < getContainerSize(); i++) {
                if (!getItem(i).isEmpty()) {
                    tile.isBlock[i] = getItem(i).getItem() instanceof BlockItem;
                    count++;
                }
            }

            if (count == 0) {
                tile.level.removeBlock(tile.getBlockPos(), false);
            }
            else {
                tile.displayCount.set((byte) count);
                ItemStack stack0 = getItem(0);
                tile.toolDisplay.set(count == 1 && stack0.getItem().isEnchantable(stack0));
                tile.tick();
                tile.updateBlock();
            }
        }

        @Override
        public int getContainerSize() {
            return 4;
        }

        @Override
        public ItemStack getItem(int index) {
            return index >= 0 && index < stacks.size() && stacks.get(index) != null ? stacks.get(index) : ItemStack.EMPTY;
        }

        private void setSlot(int index, ItemStack stack) {
            if (index < 0 || index >= 4) {
                return;
            }

            if (stack.isEmpty()) {
                if (index < stacks.size()) {
                    stacks.remove(index);
                }
            }
            else if (index < stacks.size()) {
                stacks.set(index, stack);
            }
            else {
                stacks.add(stack);
            }

            setChanged();
        }

        protected void toNBT(CompoundNBT compound) {
            ListNBT itemList = new ListNBT();

            for (ItemStack stack : stacks) {
                if (stack.isEmpty()) {
                    continue;
                }
                itemList.add(stack.save(new CompoundNBT()));
            }

            compound.put("InventoryStacks", itemList);
        }

        protected void fromNBT(CompoundNBT compound) {
            stacks.clear();
            ListNBT itemList = compound.getList("InventoryStacks", 10);

            for (int i = 0; i < itemList.size(); i++) {
                stacks.add(ItemStack.of(itemList.getCompound(i)));
            }
        }

        @Override
        public ItemStack removeItemNoUpdate(int index) {
            ItemStack stack = getItem(index);
            setItem(index, ItemStack.EMPTY);
            return stack;
        }

        @Override
        public boolean isEmpty() {
            return stacks.isEmpty();
        }

        @Override
        public ItemStack removeItem(int index, int count) {
            ItemStack stack;

            if (index >= 0 && index < stacks.size() && !stacks.get(index).isEmpty() && count > 0) {
                stack = stacks.get(index).split(count);
            }
            else {
                stack = ItemStack.EMPTY;
            }

            if (!stack.isEmpty()) {
                setChanged();
            }

            return stack;
        }


        @Override
        public int getMaxStackSize() {
            return 64;
        }

        @Override
        public void setChanged() {
            tile.setChanged();
        }

        @Override
        public boolean stillValid(PlayerEntity player) {
            return true;
        }

        @Override
        public void startOpen(PlayerEntity player) {

        }

        @Override
        public void stopOpen(PlayerEntity player) {

        }

        @Override
        public boolean canPlaceItem(int index, ItemStack stack) {
            return true;
        }

        @Override
        public void clearContent() {
            stacks.clear();
            tile.level.removeBlock(tile.getBlockPos(), false);
        }


    }
}
