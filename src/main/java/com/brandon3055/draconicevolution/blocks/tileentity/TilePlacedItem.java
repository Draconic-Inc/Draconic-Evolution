package com.brandon3055.draconicevolution.blocks.tileentity;

import codechicken.lib.raytracer.ICuboidProvider;
import codechicken.lib.raytracer.IndexedCuboid6;
import codechicken.lib.vec.*;
import com.brandon3055.brandonscore.blocks.TileInventoryBase;
import com.brandon3055.brandonscore.lib.Vec3D;
import com.brandon3055.brandonscore.network.PacketSyncableObject;
import com.brandon3055.brandonscore.network.wrappers.SyncableBool;
import com.brandon3055.brandonscore.network.wrappers.SyncableByte;
import com.brandon3055.brandonscore.utils.FeatureUtils;
import com.brandon3055.draconicevolution.DEFeatures;
import com.brandon3055.draconicevolution.blocks.PlacedItem;
import com.brandon3055.draconicevolution.lib.DESoundHandler;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundCategory;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by brandon3055 on 25/07/2016.
 */
public class TilePlacedItem extends TileInventoryBase implements ICuboidProvider{

    private LinkedList<ItemStack> stacks = new LinkedList<ItemStack>();
    public final SyncableByte displayCount = new SyncableByte((byte) 1, true, false, true);
    public final SyncableBool toolDisplay = new SyncableBool(false, true, false, true);
    public final SyncableByte[] rotation = new SyncableByte[4];
    public EnumFacing facing = EnumFacing.NORTH;
    private boolean[] isBlock = new boolean[] {false, false, false, false};

    public TilePlacedItem() {
        registerSyncableObject(displayCount, true);
        registerSyncableObject(toolDisplay, true);

        for (int i = 0; i < rotation.length; i++){
            rotation[i] = new SyncableByte((byte)0, true, false, true);
            registerSyncableObject(rotation[i], true);
        }
    }

    //region Bounds / Interaction

    public void handleClick(int hit, EntityPlayer player) {
        if (player.isSneaking()) {
            if (hit == -1) {
                return;
            }

            int index = hit - 1;

            if (index >= 0 && index < rotation.length) {
                rotation[index].value++;
                DESoundHandler.playSoundFromServer(worldObj, Vec3D.getCenter(pos), SoundEvents.ENTITY_ITEMFRAME_ROTATE_ITEM, SoundCategory.PLAYERS, 1.0F, 0.9F + worldObj.rand.nextFloat() * 0.2F, false, 24);
                detectAndSendChanges();
            }

            return;
        }

        if (hit == 0) {
            for (int i = 0; i < getSizeInventory(); i++) {
                FeatureUtils.dropItemNoDellay(getStackInSlot(i), worldObj, Vector3.fromEntity(player));
            }
            stacks.clear();
            worldObj.setBlockToAir(pos);
        }
        else {
            if (getStackInSlot(hit - 1) != null) {
                FeatureUtils.dropItemNoDellay(getStackInSlot(hit - 1), worldObj, Vector3.fromEntity(player));
                setInventorySlotContents(hit - 1, null);

            }
        }
    }

    public void breakBlock() {
        for (int i = 0; i < getSizeInventory(); i++) {
            FeatureUtils.dropItemNoDellay(getStackInSlot(i), worldObj, Vector3.fromTileCenter(this));
        }
        stacks.clear();
    }

    private IndexedCuboid6 blockBounds = new IndexedCuboid6(0, new Cuboid6(0, 0, 0, 1, 1, 1));
    private List<IndexedCuboid6> indexedCuboids = new LinkedList<IndexedCuboid6>();

    private void calculateBounds() {

        IBlockState state = worldObj.getBlockState(getPos());
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

    private void recalculateCuboids() {
        IBlockState state = worldObj.getBlockState(getPos());
        if (state.getBlock() != DEFeatures.placedItem) {
            return;
        }
        indexedCuboids.clear();

        double scale = toolDisplay.value ? 0.2 : 0.32;

        Transformation rotation = rotations[state.getValue(PlacedItem.FACING).getIndex()].at(Vector3.center);

        double offset = 0.225;
        double blockH = 0.36;
        double itemH = 0.03;

        if (displayCount.value == 1) {
            indexedCuboids.add(new IndexedCuboid6(1, new Cuboid6(scale, scale, 0, 1 - scale, 1 - scale, isBlock[0] ? blockH : itemH).apply(rotation)));
        } else if (displayCount.value == 2) {
            indexedCuboids.add(new IndexedCuboid6(1, new Cuboid6(scale, scale, 0, 1 - scale, 1 - scale, isBlock[0] ? blockH : itemH).apply(new Translation(-offset, 0, 0).with(rotation))));
            indexedCuboids.add(new IndexedCuboid6(2, new Cuboid6(scale, scale, 0, 1 - scale, 1 - scale, isBlock[1] ? blockH : itemH).apply(new Translation(offset, 0, 0).with(rotation))));
        } else if (displayCount.value == 3) {
            indexedCuboids.add(new IndexedCuboid6(1, new Cuboid6(scale, scale, 0, 1 - scale, 1 - scale, isBlock[0] ? blockH : itemH).apply(new Translation(0, offset, 0).with(rotation))));
            indexedCuboids.add(new IndexedCuboid6(2, new Cuboid6(scale, scale, 0, 1 - scale, 1 - scale, isBlock[1] ? blockH : itemH).apply(new Translation(-offset, -offset, 0).with(rotation))));
            indexedCuboids.add(new IndexedCuboid6(3, new Cuboid6(scale, scale, 0, 1 - scale, 1 - scale, isBlock[2] ? blockH : itemH).apply(new Translation(offset, -offset, 0).with(rotation))));
        } else if (displayCount.value == 4) {
            indexedCuboids.add(new IndexedCuboid6(1, new Cuboid6(scale, scale, 0, 1 - scale, 1 - scale, isBlock[0] ? blockH : itemH).apply(new Translation(-offset, offset, 0).with(rotation))));
            indexedCuboids.add(new IndexedCuboid6(2, new Cuboid6(scale, scale, 0, 1 - scale, 1 - scale, isBlock[1] ? blockH : itemH).apply(new Translation(offset, offset, 0).with(rotation))));
            indexedCuboids.add(new IndexedCuboid6(3, new Cuboid6(scale, scale, 0, 1 - scale, 1 - scale, isBlock[2] ? blockH : itemH).apply(new Translation(-offset, -offset, 0).with(rotation))));
            indexedCuboids.add(new IndexedCuboid6(4, new Cuboid6(scale, scale, 0, 1 - scale, 1 - scale, isBlock[3] ? blockH : itemH).apply(new Translation(offset, -offset, 0).with(rotation))));
        }
    }

    private static final Transformation[] rotations = new Transformation[]{Rotation.sideRotations[3], Rotation.sideRotations[2], Rotation.sideRotations[0], Rotation.sideRotations[1], Rotation.quarterRotations[3], Rotation.quarterRotations[1],};

    public List<IndexedCuboid6> getCachedRenderCuboids() {
        if (indexedCuboids.isEmpty()) {
            recalculateCuboids();
        }
        return indexedCuboids;
    }

    @Override
    public List<IndexedCuboid6> getIndexedCuboids() {
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
    public void setInventorySlotContents(int index, ItemStack stack) {
        setSlot(index, stack);

        int count = 0;
        for (int i = 0; i < getSizeInventory(); i++) {
            if (getStackInSlot(i) != null) {
                isBlock[i] = getStackInSlot(i).getItem() instanceof ItemBlock;

                count++;
            }
        }

        if (count == 0) {
            worldObj.setBlockToAir(getPos());
        } else {
            displayCount.value = (byte) count;
            ItemStack stack0 = getStackInSlot(0);
            toolDisplay.value = count == 1 && stack0 != null && stack0.getItem().isItemTool(stack0);
            detectAndSendChanges();
            updateBlock();
        }
    }

    @Override
    public int getSizeInventory() {
        return 4;
    }

    @Override
    public ItemStack getStackInSlot(int index) {
        return index >= 0 && index < stacks.size() ? stacks.get(index) : null;
    }

    private void setSlot(int index, ItemStack stack) {
        if (index < 0 || index >= 4){
            return;
        }

        if (stack == null) {
            if (index < stacks.size()){
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

    @Override
    public void receiveSyncPacketFromServer(PacketSyncableObject packet) {
        super.receiveSyncPacketFromServer(packet);
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
    }

    @Override
    public void readExtraNBT(NBTTagCompound compound) {
        super.readExtraNBT(compound);
        facing = EnumFacing.getFront(compound.getByte("Facing"));
        for (int i = 0; i < isBlock.length; i++) {
            isBlock[i] = compound.getBoolean("IsBlock" + i);
        }
    }

    @Override
    protected void writeInventoryToNBT(NBTTagCompound compound) {
        NBTTagList itemList = new NBTTagList();

        for (ItemStack stack : stacks) {
            if (stack == null) {
                continue;
            }
            itemList.appendTag(stack.writeToNBT(new NBTTagCompound()));
        }

        compound.setTag("InventoryStacks", itemList);
    }

    @Override
    protected void readInventoryFromNBT(NBTTagCompound compound) {
        stacks.clear();
        NBTTagList itemList = compound.getTagList("InventoryStacks", 10);

        for (int i = 0; i < itemList.tagCount(); i++) {
            stacks.add(ItemStack.loadItemStackFromNBT(itemList.getCompoundTagAt(i)));
        }
    }

    //endregion
}
