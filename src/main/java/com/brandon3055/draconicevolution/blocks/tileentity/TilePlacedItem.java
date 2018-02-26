package com.brandon3055.draconicevolution.blocks.tileentity;

import codechicken.lib.raytracer.ICuboidProvider;
import codechicken.lib.raytracer.IndexedCuboid6;
import codechicken.lib.vec.*;
import com.brandon3055.brandonscore.blocks.TileBCBase;
import com.brandon3055.brandonscore.lib.Vec3D;
import com.brandon3055.brandonscore.lib.datamanager.ManagedBool;
import com.brandon3055.brandonscore.lib.datamanager.ManagedByte;
import com.brandon3055.brandonscore.utils.FeatureUtils;
import com.brandon3055.draconicevolution.DEFeatures;
import com.brandon3055.draconicevolution.blocks.PlacedItem;
import com.brandon3055.draconicevolution.integration.ModHelper;
import com.brandon3055.draconicevolution.lib.DESoundHandler;
import com.brandon3055.draconicevolution.utils.LogHelper;
import com.google.common.collect.ImmutableList;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by brandon3055 on 25/07/2016.
 */
public class TilePlacedItem extends TileBCBase implements ICuboidProvider {

    public final ManagedByte displayCount = register("displayCount", new ManagedByte(1)).saveToTile().syncViaTile().trigerUpdate().finish();
    public final ManagedBool toolDisplay = register("toolDisplay", new ManagedBool(false)).saveToTile().syncViaTile().trigerUpdate().finish();
    public final ManagedBool altRenderMode = register("altRenderMode", new ManagedBool(false)).saveToTile().syncViaTile().trigerUpdate().finish();
    public final ManagedByte[] rotation = new ManagedByte[4];
    public EnumFacing facing = EnumFacing.NORTH;
    private boolean[] isBlock = new boolean[]{false, false, false, false};
    public PlacedItemInventory inventory = new PlacedItemInventory(this);

    public TilePlacedItem() {
        for (int i = 0; i < rotation.length; i++) {
            rotation[i] = register("rotation" + i, new ManagedByte(0)).saveToTile().syncViaTile().trigerUpdate().finish();
        }
    }

    //region Bounds / Interaction

    public void handleClick(int hit, EntityPlayer player) {
        if (!player.getHeldItemMainhand().isEmpty() && ModHelper.isWrench(player.getHeldItemMainhand())) {
            altRenderMode.value = !altRenderMode.value;
            LogHelper.dev(altRenderMode);
            super.update();
            return;
        }

        if (player.isSneaking()) {
            if (hit == -1) {
                return;
            }

            int index = hit - 1;

            if (index >= 0 && index < rotation.length) {
                rotation[index].value++;
                DESoundHandler.playSoundFromServer(world, Vec3D.getCenter(pos), SoundEvents.ENTITY_ITEMFRAME_ROTATE_ITEM, SoundCategory.PLAYERS, 1.0F, 0.9F + world.rand.nextFloat() * 0.2F, false, 24);
                super.update();
            }

            return;
        }

        if (hit == 0) {
            for (int i = 0; i < inventory.getSizeInventory(); i++) {
                FeatureUtils.dropItemNoDellay(inventory.getStackInSlot(i), world, Vector3.fromEntity(player));
            }
            inventory.stacks.clear();
            world.setBlockToAir(pos);
        }
        else {
            if (!inventory.getStackInSlot(hit - 1).isEmpty()) {
                FeatureUtils.dropItemNoDellay(inventory.getStackInSlot(hit - 1), world, Vector3.fromEntity(player));
                inventory.setInventorySlotContents(hit - 1, ItemStack.EMPTY);
            }
        }
    }

    public void breakBlock() {
        for (int i = 0; i < inventory.getSizeInventory(); i++) {
            FeatureUtils.dropItemNoDellay(inventory.getStackInSlot(i), world, Vector3.fromTileCenter(this));
        }
        inventory.stacks.clear();
    }

    private IndexedCuboid6 blockBounds = new IndexedCuboid6(0, new Cuboid6(0, 0, 0, 1, 1, 1));
    private List<IndexedCuboid6> indexedCuboids = new LinkedList<>();

    private synchronized void calculateBounds() {

        IBlockState state = getState(DEFeatures.placedItem);//world.getBlockState(getPos());
        Cuboid6 box = new Cuboid6(0.5, 0, 0.5, 0.5, 0, 0.5).apply(Rotation.sideRotations[state.getValue(PlacedItem.FACING).getIndex()].at(Vector3.center));

        int i = 0;
        for (Cuboid6 cuboid : indexedCuboids) {
            i++;
            box.enclose(cuboid);
        }

        if (facing.getAxisDirection() == EnumFacing.AxisDirection.NEGATIVE) {
            box.setSide(facing.getIndex() ^ 1, 0.01);
        }
        else {
            box.setSide(facing.getIndex() ^ 1, 0.99);
        }


        if (i > 1) {
            EnumFacing.Axis axis = facing.getAxis();
            box.expand(new Vector3(axis == EnumFacing.Axis.X ? -0.02 : 0.03, axis == EnumFacing.Axis.Y ? -0.02 : 0.03, axis == EnumFacing.Axis.Z ? -0.02 : 0.03));
        }

        blockBounds = new IndexedCuboid6(0, box);
    }

    private synchronized void recalculateCuboids() {
        IBlockState state = world.getBlockState(getPos());
        if (state.getBlock() != DEFeatures.placedItem) {
            return;
        }
        indexedCuboids = new ArrayList<>();

        double scale = displayCount.value == 1 && (toolDisplay.value || altRenderMode.value) ? 0.2 : 0.32;

        Transformation rotation = rotations[state.getValue(PlacedItem.FACING).getIndex()].at(Vector3.center);

        double offset = 0.225;
        double blockH = 0.36;
        double itemH = 0.03;

        if (displayCount.value == 1) {
            indexedCuboids.add(new IndexedCuboid6(1, new Cuboid6(scale, scale, 0, 1 - scale, 1 - scale, isBlock[0] ? blockH : itemH).apply(rotation)));
        }
        else if (displayCount.value == 2) {
            indexedCuboids.add(new IndexedCuboid6(1, new Cuboid6(scale, scale, 0, 1 - scale, 1 - scale, isBlock[0] ? blockH : itemH).apply(new Translation(-offset, 0, 0).with(rotation))));
            indexedCuboids.add(new IndexedCuboid6(2, new Cuboid6(scale, scale, 0, 1 - scale, 1 - scale, isBlock[1] ? blockH : itemH).apply(new Translation(offset, 0, 0).with(rotation))));
        }
        else if (displayCount.value == 3) {
            indexedCuboids.add(new IndexedCuboid6(1, new Cuboid6(scale, scale, 0, 1 - scale, 1 - scale, isBlock[0] ? blockH : itemH).apply(new Translation(0, offset, 0).with(rotation))));
            indexedCuboids.add(new IndexedCuboid6(2, new Cuboid6(scale, scale, 0, 1 - scale, 1 - scale, isBlock[1] ? blockH : itemH).apply(new Translation(-offset, -offset, 0).with(rotation))));
            indexedCuboids.add(new IndexedCuboid6(3, new Cuboid6(scale, scale, 0, 1 - scale, 1 - scale, isBlock[2] ? blockH : itemH).apply(new Translation(offset, -offset, 0).with(rotation))));
        }
        else if (displayCount.value == 4) {
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

    @Override
    public synchronized List<IndexedCuboid6> getIndexedCuboids() {
        recalculateCuboids();
        calculateBounds();
        List<IndexedCuboid6> list = new ArrayList<IndexedCuboid6>();
        list.add(blockBounds);
        list.addAll(indexedCuboids);
        return list;
    }

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
    public void writeExtraNBT(NBTTagCompound compound) {
        super.writeExtraNBT(compound);
        compound.setByte("Facing", (byte) facing.getIndex());
        for (int i = 0; i < isBlock.length; i++) {
            compound.setBoolean("IsBlock" + i, isBlock[i]);
        }
        inventory.toNBT(compound);
    }

    @Override
    public void readExtraNBT(NBTTagCompound compound) {
        super.readExtraNBT(compound);
        facing = EnumFacing.getFront(compound.getByte("Facing"));
        for (int i = 0; i < isBlock.length; i++) {
            isBlock[i] = compound.getBoolean("IsBlock" + i);
        }
        inventory.fromNBT(compound);
    }

    //endregion

    public static class PlacedItemInventory implements IInventory {
        private LinkedList<ItemStack> stacks = new LinkedList<ItemStack>();
        private TilePlacedItem tile;

        public PlacedItemInventory(TilePlacedItem tile) {
            this.tile = tile;
        }

        @Override
        public void setInventorySlotContents(int index, ItemStack stack) {
            setSlot(index, stack);

            int count = 0;
            for (int i = 0; i < getSizeInventory(); i++) {
                if (!getStackInSlot(i).isEmpty()) {
                    tile.isBlock[i] = getStackInSlot(i).getItem() instanceof ItemBlock;
                    count++;
                }
            }

            if (count == 0) {
                tile.world.setBlockToAir(tile.getPos());
            }
            else {
                tile.displayCount.value = (byte) count;
                ItemStack stack0 = getStackInSlot(0);
                tile.toolDisplay.value = count == 1 && stack0.getItem().isEnchantable(stack0);
                tile.update();
                tile.updateBlock();
            }
        }

        @Override
        public int getSizeInventory() {
            return 4;
        }

        @Override
        public ItemStack getStackInSlot(int index) {
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

            markDirty();
        }

        protected void toNBT(NBTTagCompound compound) {
            NBTTagList itemList = new NBTTagList();

            for (ItemStack stack : stacks) {
                if (stack.isEmpty()) {
                    continue;
                }
                itemList.appendTag(stack.writeToNBT(new NBTTagCompound()));
            }

            compound.setTag("InventoryStacks", itemList);
        }

        protected void fromNBT(NBTTagCompound compound) {
            stacks.clear();
            NBTTagList itemList = compound.getTagList("InventoryStacks", 10);

            for (int i = 0; i < itemList.tagCount(); i++) {
                stacks.add(new ItemStack(itemList.getCompoundTagAt(i)));
            }
        }

        @Override
        public ItemStack removeStackFromSlot(int index) {
            ItemStack stack = getStackInSlot(index);
            setInventorySlotContents(index, ItemStack.EMPTY);
            return stack;
        }

        @Override
        public boolean isEmpty() {
            return stacks.isEmpty();
        }

        @Override
        public ItemStack decrStackSize(int index, int count) {
            ItemStack stack;

            if (index >= 0 && index < stacks.size() && !stacks.get(index).isEmpty() && count > 0) {
                stack = stacks.get(index).splitStack(count);
            }
            else {
                stack = ItemStack.EMPTY;
            }

            if (!stack.isEmpty()) {
                markDirty();
            }

            return stack;
        }


        @Override
        public int getInventoryStackLimit() {
            return 64;
        }

        @Override
        public void markDirty() {
            tile.markDirty();
        }

        @Override
        public boolean isUsableByPlayer(EntityPlayer player) {
            return true;
        }

        @Override
        public void openInventory(EntityPlayer player) {

        }

        @Override
        public void closeInventory(EntityPlayer player) {

        }

        @Override
        public boolean isItemValidForSlot(int index, ItemStack stack) {
            return true;
        }

        @Override
        public int getField(int id) {
            return 0;
        }

        @Override
        public void setField(int id, int value) {

        }

        @Override
        public int getFieldCount() {
            return 0;
        }

        @Override
        public void clear() {
            stacks.clear();
            tile.world.setBlockToAir(tile.getPos());
        }

        @Override
        public String getName() {
            return "";
        }

        @Override
        public boolean hasCustomName() {
            return false;
        }

        @Override
        public ITextComponent getDisplayName() {
            return new TextComponentString("");
        }
    }
}
