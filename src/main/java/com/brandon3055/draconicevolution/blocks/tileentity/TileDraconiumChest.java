package com.brandon3055.draconicevolution.blocks.tileentity;

import codechicken.lib.data.MCDataInput;
import codechicken.lib.inventory.InventoryUtils;
import com.brandon3055.brandonscore.api.power.OPStorage;
import com.brandon3055.brandonscore.blocks.TileBCore;
import com.brandon3055.brandonscore.capability.CapabilityOP;
import com.brandon3055.brandonscore.inventory.ItemHandlerIOControl;
import com.brandon3055.brandonscore.inventory.ItemHandlerSlotWrapper;
import com.brandon3055.brandonscore.inventory.TileItemStackHandler;
import com.brandon3055.brandonscore.lib.datamanager.*;
import com.brandon3055.brandonscore.utils.DataUtils;
import com.brandon3055.brandonscore.utils.EnergyUtils;
import com.brandon3055.draconicevolution.DEOldConfig;
import com.brandon3055.draconicevolution.init.DEContent;
import com.brandon3055.draconicevolution.blocks.DraconiumChest;
import com.brandon3055.draconicevolution.inventory.ContainerDraconiumChest;
import com.brandon3055.draconicevolution.items.ItemCore;
import com.brandon3055.draconicevolution.init.OreDoublingRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.NonNullList;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.items.CapabilityItemHandler;

import javax.annotation.Nonnull;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.brandon3055.brandonscore.lib.datamanager.DataFlags.*;

/**
 * Created by brandon3055 on 28/09/2016.
 */
public class TileDraconiumChest extends TileBCore implements ITickableTileEntity {

    private NonNullList<ItemStack> craftingStacks = NonNullList.withSize(10, ItemStack.EMPTY);
    public ManagedEnum<AutoSmeltMode> autoSmeltMode = register(new ManagedEnum<>("auto_smelt_mode", AutoSmeltMode.OFF, SAVE_BOTH_SYNC_CONTAINER));
    public ManagedEnum<Direction> facing = register(new ManagedEnum<>("facing", Direction.NORTH, SAVE_NBT_SYNC_TILE));
    public ManagedDouble burnRate = register(new ManagedDouble("burn_rate", SAVE_BOTH_SYNC_CONTAINER));
    public ManagedDouble smeltProgress = register(new ManagedDouble("smelt_progress", SAVE_BOTH_SYNC_CONTAINER));
    public ManagedBool isSmelting = register(new ManagedBool("is_smelting", SAVE_BOTH));
    public ManagedBool furnaceOutputBlocked = register(new ManagedBool("furnace_output_blocked", SAVE_BOTH));
    public ManagedInt smeltEnergyPerTick = register(new ManagedInt("smelt_energy_per_tick", 256, SAVE_BOTH_SYNC_CONTAINER));
    public ManagedInt colour = register(new ManagedInt("colour", 0x640096, SAVE_BOTH_SYNC_TILE));
    public ManagedShort numPlayersUsing = register(new ManagedShort("num_players_using", SYNC_TILE));
    /**
     * The number of ticks it takes to complete 1 smelting operation.
     */
    public ManagedByte smeltTime = register(new ManagedByte("smelt_time", (byte) 100, SAVE_BOTH_SYNC_CONTAINER));

    public float prevLidAngle;
    public float lidAngle;
    private int ticksSinceSync;

    public static int LAST_CHEST_SLOT = 259;
    public static int FIRST_FURNACE_SLOT = 260;
    public static int LAST_FURNACE_SLOT = 264;
    public static int CAPACITOR_SLOT = 265;
    public static int CORE_SLOT = 266;

    private boolean autoFeedRun = false;
    private boolean autoFeedScheduled = false;

    public TileItemStackHandler itemHandler = new TileItemStackHandler(267);
    public OPStorage opStorage = new OPStorage(1000000, 32000, 0);

    public SlotRegion[] slotRegions = new SlotRegion[7]; {
        slotRegions[0] = new SlotRegion(0, 0xFF0000, false);
        slotRegions[1] = new SlotRegion(1, 0x00FF00, false);
        slotRegions[2] = new SlotRegion(2, 0x0000FF, false);
        slotRegions[3] = new SlotRegion(3, 0xFFFF00, false);
        slotRegions[4] = new SlotRegion(4, 0x00FFFF, false);
        slotRegions[5] = new SlotRegion(5, 0xFF00FF, false);
        slotRegions[6] = new SlotRegion(6, 0x000000, true);
    }

    public TileDraconiumChest() {
        super(DEContent.tile_draconium_chest);
        capManager.setManaged("energy", CapabilityOP.OP, opStorage).saveBoth().syncContainer();

        capManager.setInternalManaged("inventory", CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, itemHandler).saveBoth();
        itemHandler.setStackValidator(this::isItemValidForSlot);
        itemHandler.setContentsChangeListener(this::onSlotContentsChange);

        for (Direction dir : Direction.values()) {
            capManager.set(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, new ItemHandlerIOControl(itemHandler).setExtractCheck((slot, stack) -> canExtractItem(slot, stack, dir)).setInsertCheck((slot, stack) -> canInsertItem(slot, stack, dir)), dir);
        }
    }

    @Override
    public void tick() {
        super.tick();
        autoFeedRun = false;
        if (!world.isRemote) {
            updateEnergy();
            updateSmelting();

            if (autoFeedScheduled && attemptAutoFeed()) {
                validateSmelting();
            }
            autoFeedScheduled = false;
        }
        updateModel();
    }

    //region Furnace

    /**
     * Updates the current smelting operation. But this does not initiate smelting.
     * Its much more efficient listen for changes to the smelting slots via setInventorySlotContents then to just keep checking every tick.
     * And as everything that ever modifies the inventory in any way should go through that method it 'should' never break.
     */
    public void updateSmelting() {
        //If the furnace output is blocked we wait for something in the inventory to change then try again.
        if (furnaceOutputBlocked.get()) {
//            LogHelper.dev("furnaceOutputBlocked");
            burnRate.zero();
            return;
        }
        //If not smelting then decrement smelt progress if greater than 0 and return.
        else if (!isSmelting.get()) {
            if (smeltProgress.get() >= 1) {
                smeltProgress.dec();
            }
            burnRate.zero();
//            LogHelper.dev("!isSmelting");
            return;
        }
        //Else if smelting increment the smelt timer and use energy.
        else if (smeltProgress.get() < smeltTime.get()) {
            burnRate.set(getSmeltingSpeed());
            int energyUsage = Math.max((int) (burnRate.get() * smeltEnergyPerTick.get()), 8);
            smeltProgress.add(burnRate.get());
            opStorage.modifyEnergyStored(-energyUsage);
//            LogHelper.dev("smeltProgress.value < smeltTime, Speed: " + burnRate.value);
            return;
        }

        if (!smeltItems()) {
            furnaceOutputBlocked.set(true);
//            LogHelper.dev("!smeltItems()");
            return;
        }
        smeltProgress.zero();
        isSmelting.set(canSmelt());
    }

    int itemsSmelted = 0;

    /**
     * @return false if there is nowhere to put the output otherwise will always return true.
     */
    private boolean smeltItems() {
        checkIOCache();
        //We want to try to smelt 5 items total even if they are not spread across all 5 slots.
        for (int smeltPass = 0; smeltPass < 5; smeltPass++) {
            //Iterate over the 5 furnace input slots.
            for (int i = FIRST_FURNACE_SLOT; i <= LAST_FURNACE_SLOT; i++) {
                ItemStack stack = itemHandler.getStackInSlot(i);
                ItemStack result = getSmeltResult(stack);

                if (!result.isEmpty() && (!autoSmeltMode.get().keep1Item || stack.getCount() > 1)) {
                    ItemHandlerSlotWrapper range = new ItemHandlerSlotWrapper(itemHandler, furnaceOutputs);
                    //Given the size of the inventory this is probably a bit inefficient but i dont think there is a better option.
                    //If an item produces a result stack size greater than 1 i cant exactly just insert half the stack then smelt the other half of the stack later
                    //I guess i could cache the remainder until there is room but.. meh.
                    if (!InventoryUtils.insertItem(range, result.copy(), true).isEmpty()) {
                        return false;
                    }

                    InventoryUtils.insertItem(range, result.copy(), false);

                    stack.shrink(1);
                    if (stack.getCount() <= 0) {
                        itemHandler.setStackInSlot(i, ItemStack.EMPTY);
                    }

                    itemsSmelted++;
                    if (itemsSmelted == 5) {
                        itemsSmelted = 0;
                        attemptAutoFeed();
                        return true;
                    }
                }
            }
        }

        attemptAutoFeed();
        return true;
    }

    private boolean canSmelt() {
        for (int i = FIRST_FURNACE_SLOT; i <= LAST_FURNACE_SLOT; i++) {
            ItemStack stack = itemHandler.getStackInSlot(i);
            if (!getSmeltResult(stack).isEmpty() && (!autoSmeltMode.get().keep1Item || stack.getCount() > 1)) {
                return true;
            }
        }
        return false;
    }

    public ItemStack getSmeltResult(ItemStack stack) {
        if (stack.isEmpty()) {
            return ItemStack.EMPTY;
        } else if (!itemHandler.getStackInSlot(CORE_SLOT).isEmpty()) {
            return OreDoublingRegistry.getDoubledSmeltingResult(stack, world);
        }
        return OreDoublingRegistry.getSmeltingResult(stack, world);
    }

    /**
     * @return A smelting speed scaled based on energy capacity if capacity is less then 10%
     */
    private double getSmeltingSpeed() {
        double eCapacity = (double) opStorage.getEnergyStored() / (double) opStorage.getMaxEnergyStored();
        return eCapacity > 0.1D ? 1D : eCapacity * 10D;
    }

    private void updateEnergy() {
        if (opStorage.getEnergyStored() < opStorage.getMaxEnergyStored()) {
            EnergyUtils.transferEnergy(itemHandler.getStackInSlot(CAPACITOR_SLOT), opStorage);
        }
    }

    public void validateSmelting() {
        ItemStack stack = itemHandler.getStackInSlot(CORE_SLOT);
        if (stack.getItem() instanceof ItemCore) {
            if (stack.getItem() == DEContent.core_wyvern) {
                smeltEnergyPerTick.set(1024);
                smeltTime.set((byte) 50);
            } else if (stack.getItem() == DEContent.core_awakened) {
                smeltEnergyPerTick.set(4069);
                smeltTime.set((byte) 25);
            } else if (stack.getItem() == DEContent.core_chaotic) {
                smeltEnergyPerTick.set(16384);
                smeltTime.set((byte) 2);
            } else {
                smeltEnergyPerTick.set(256);
                smeltTime.set((byte) 100);
            }
        } else {
            smeltEnergyPerTick.set(256);
            smeltTime.set((byte) 100);
        }
        isSmelting.set(canSmelt());
        furnaceOutputBlocked.set(false);
    }

    //endregion

    //region Furnace Auto Feed

    public void scheduleAutoFeed() {
        autoFeedScheduled = true;
    }

    public boolean attemptAutoFeed() {
        if (autoSmeltMode.get() == AutoSmeltMode.OFF || autoFeedRun) {
            return false;
        }
        checkIOCache();

        autoFeedRun = true;
        boolean stacksInserted = false;

        for (int i = 0; i < LAST_CHEST_SLOT; i++) {
            if (!furnaceInputs.contains(i)) {
                continue;
            }
            ItemStack stack = itemHandler.getStackInSlot(i);
            if (!stack.isEmpty() && !getSmeltResult(stack).isEmpty()) {
                int fullStacks = 0;
                for (int f = FIRST_FURNACE_SLOT; f <= LAST_FURNACE_SLOT; f++) {
                    ItemStack stackInFernace = itemHandler.getStackInSlot(f);

                    switch (autoSmeltMode.get()) {
                        case FILL:
                        case LOCK:
                            if (ItemStack.areItemsEqual(stackInFernace, stack) && ItemStack.areItemStackTagsEqual(stackInFernace, stack) && stackInFernace.getCount() < stackInFernace.getMaxStackSize()) {
                                int count = Math.min(stack.getCount(), stackInFernace.getMaxStackSize() - stackInFernace.getCount());
                                stackInFernace.grow(count);
                                stack.shrink(count);
                                stacksInserted = true;
                            }
                            break;
                        case ALL:
                            if (stackInFernace.isEmpty()) {
                                itemHandler.setStackInSlot(f, stack.copy());
                                stack = ItemStack.EMPTY;
                                stacksInserted = true;
                            } else if (ItemStack.areItemsEqual(stackInFernace, stack) && ItemStack.areItemStackTagsEqual(stackInFernace, stack) && stackInFernace.getCount() < stackInFernace.getMaxStackSize()) {
                                int count = Math.min(stack.getCount(), stackInFernace.getMaxStackSize() - stackInFernace.getCount());
                                stackInFernace.grow(count);
                                stack.shrink(count);
                                stacksInserted = true;
                            }
                            break;
                    }

                    stackInFernace = itemHandler.getStackInSlot(f);
                    if (!stackInFernace.isEmpty() && stackInFernace.getCount() == stackInFernace.getMaxStackSize()) {
                        fullStacks++;
                    }

                    if (stack.isEmpty() || stack.getCount() == 0) {
                        itemHandler.setStackInSlot(i, ItemStack.EMPTY);
                        if (fullStacks == 5) {
                            return stacksInserted;
                        }
                        break;
                    } else if (fullStacks == 5) {
                        return stacksInserted;
                    }
                }
            }
        }

        return stacksInserted;
    }

    //endregion

    //region Interaction

    public void setAutoSmeltMode(AutoSmeltMode mode) {
        if (world.isRemote) {
            sendPacketToServer(output -> output.writeByte(mode.ordinal()), 0);
        }
    }

    public void setColour(int colour) {
        if (world.isRemote) {
            sendPacketToServer(output -> output.writeInt(colour), 2);
        }
    }

    public void setRegionState(int region) {
        CompoundNBT compound = new CompoundNBT();
        if (region >= 0 && region < slotRegions.length) {
            compound.putInt("regionID", region);
            slotRegions[region].toNBT(compound);
            sendPacketToServer(output -> output.writeCompoundNBT(compound), 1);
        }
        ioCacheValid = false;
    }

    @Override
    public void receivePacketFromClient(MCDataInput data, ServerPlayerEntity client, int id) {
        if (id == 0) {
            int index = data.readByte();
            autoSmeltMode.set(AutoSmeltMode.values()[index]);
            scheduleAutoFeed();
            validateSmelting();
        } else if (id == 1) {
            CompoundNBT compound = data.readCompoundNBT();
            int region = compound.getByte("regionID");
            if (region >= 0 && region < slotRegions.length) {
                slotRegions[region].fromNBT(compound);
                for (SlotRegion r : slotRegions) {
                    r.validate();
                }
                ioCacheValid = false;
                markDirty();

                scheduleAutoFeed();
                validateSmelting();
            }
        } else if (id == 2) {
            colour.set(data.readInt());
            markDirty();
        }
    }

    //endregion

    //region Inventory

    protected void writeCraftingToNBT(CompoundNBT compound) {
        ListNBT nbttaglist = new ListNBT();

        for (int i = 0; i < craftingStacks.size(); ++i) {
            ItemStack itemstack = craftingStacks.get(i);

            if (!itemstack.isEmpty()) {
                CompoundNBT nbttagcompound = new CompoundNBT();
                nbttagcompound.putByte("Slot", (byte) i);
                itemstack.write(nbttagcompound);
                nbttaglist.add(nbttagcompound);
            }
        }

        if (!nbttaglist.isEmpty()) {
            compound.put("CraftingItems", nbttaglist);
        }
    }

    protected void readCraftingFromNBT(CompoundNBT compound) {
        ListNBT nbttaglist = compound.getList("CraftingItems", 10);

        for (int i = 0; i < nbttaglist.size(); ++i) {
            CompoundNBT nbttagcompound = nbttaglist.getCompound(i);
            int j = nbttagcompound.getByte("Slot") & 255;
            if (j >= 0 && j < craftingStacks.size()) {
                craftingStacks.set(j, ItemStack.read(nbttagcompound));
            }
        }
    }

    public boolean isItemValidForSlot(int index, @Nonnull ItemStack stack) {
        if (!stack.isEmpty() && DEOldConfig.chestBlacklist.contains(stack.getItem().getRegistryName().toString())) {
            return false;
        }
        if (index >= FIRST_FURNACE_SLOT && index <= LAST_FURNACE_SLOT) {
            return !getSmeltResult(stack).isEmpty();
        } else if (index == CAPACITOR_SLOT) {
            return EnergyUtils.canExtractEnergy(stack);
        } else if (index == CORE_SLOT) {
            return stack.getItem() instanceof ItemCore && stack.getItem() != DEContent.core_draconium;
        }
        return DraconiumChest.isStackValid(stack);
    }

    public ItemStack getStackInCraftingSlot(int i) {
        return craftingStacks.get(i);
    }

    public void setInventoryCraftingSlotContents(int i, @Nonnull ItemStack stack) {
        craftingStacks.set(i, stack);

        if (stack.getCount() > itemHandler.getSlotLimit(i)) {
            stack.setCount(itemHandler.getSlotLimit(i));
        }

        markDirty();
    }

    public void onSlotContentsChange(int index) {
        //Check if one of the smelting slots was modified and if so update the smelting state.
        if ((index >= FIRST_FURNACE_SLOT && index <= LAST_FURNACE_SLOT) || index == CORE_SLOT) {
            validateSmelting();
        } else if (furnaceOutputBlocked.get()) {
            furnaceOutputBlocked.set(false);
        }

        scheduleAutoFeed();
    }

//    @Override
//    protected <T> T getItemHandler(Capability<T> capability, Direction facing) {
//        return CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.cast(itemHandlers[facing.getIndex()]);
//    }


    @Override
    public void writeToItemStack(CompoundNBT compound, boolean willHarvest) {
        super.writeToItemStack(compound, willHarvest);
        writeRegions(compound);
        if (colour.get() != 0x640096) {
            compound.putInt("ChestColour", colour.get());
        }
        writeCraftingToNBT(compound);
    }

    @Override
    public void readFromItemStack(CompoundNBT compound) {
        super.readFromItemStack(compound);
        readRegions(compound);
        readCraftingFromNBT(compound);
    }

    @Override
    public void writeExtraNBT(CompoundNBT compound) {
        super.writeExtraNBT(compound);
        writeRegions(compound);
        writeCraftingToNBT(compound);
    }

    @Override
    public void readExtraNBT(CompoundNBT compound) {
        super.readExtraNBT(compound);
        readRegions(compound);
        readCraftingFromNBT(compound);
    }

    private void writeRegions(CompoundNBT compound) {
        CompoundNBT regionTag = new CompoundNBT();
        for (SlotRegion region : slotRegions) {
            region.toNBT(regionTag);
        }
        compound.put("RegionData", regionTag);
    }

    private void readRegions(CompoundNBT compound) {
        if (compound.contains("RegionData")) {
            CompoundNBT regionTag = compound.getCompound("RegionData");
            for (SlotRegion region : slotRegions) {
                region.fromNBT(regionTag);
            }
        }
    }

    //endregion

    //region Regional I/O

    public boolean ioCacheValid = false;
    private int[][] sidedSlots = new int[6][];
    private boolean[][] canInsert = new boolean[6][260];
    private boolean[][] canExtract = new boolean[6][260];
    /**
     * Slots that the furnace can take from
     */
    private Set<Integer> furnaceInputs = new HashSet<>();
    /**
     * Slots that the furnace can output to
     */
    private int[] furnaceOutputs = new int[0];

    private void checkIOCache() {
        if (ioCacheValid) {
            return;
        }

        //Clear slot cache from all regions.
        DataUtils.forEach(slotRegions, region -> region.controledSlots.clear());

        //Assign slots to regions
        for (int i = 0; i <= LAST_CHEST_SLOT; i++) {
            for (SlotRegion region : slotRegions) {
                if (region.controlsSlot(i)) {
                    region.controledSlots.add(i);
                    break;
                }
            }
        }

        //Iterate over all the directions and regions to assign which slots allow inserting and or extracting from each face
        for (Direction worldFace : Direction.values()) {
            int faceIndex = worldFace.getIndex();
            Direction facing = getRotatedFacing(worldFace);
            List<Integer> accessibleSlots = new ArrayList<>();
            List<Integer> insertSlots = new ArrayList<>();
            List<Integer> extractSlots = new ArrayList<>();

            for (SlotRegion region : slotRegions) {
                if (!region.isActive()) {
                    continue;
                }

                if (region.hasIO(facing)) {
                    accessibleSlots.addAll(region.controledSlots);
                    if (region.canExtract(facing)) {
                        extractSlots.addAll(region.controledSlots);
                    }
                    if (region.canInsert(facing)) {
                        insertSlots.addAll(region.controledSlots);
                    }
                }
            }

            sidedSlots[faceIndex] = DataUtils.intListToArray(accessibleSlots);
            for (int i = 0; i <= LAST_CHEST_SLOT; i++) {
                canInsert[faceIndex][i] = insertSlots.contains(i);
                canExtract[faceIndex][i] = extractSlots.contains(i);
            }
        }

        //Set which slots accept or reject furnace IO
        List<Integer> furnaceInsertSlots = new ArrayList<>();
        List<Integer> furnaceExtractSlots = new ArrayList<>();

        for (SlotRegion region : slotRegions) {
            if (!region.isActive()) {
                continue;
            }

            if (region.hasIO(null)) {
                if (region.canExtract(null)) {
                    furnaceExtractSlots.addAll(region.controledSlots);
                }
                if (region.canInsert(null)) {
                    furnaceInsertSlots.addAll(region.controledSlots);
                }
            }
        }

        furnaceInputs.clear();
        furnaceInputs.addAll(furnaceInsertSlots);
        furnaceOutputs = DataUtils.intListToArray(furnaceExtractSlots);

        ioCacheValid = true;
    }

    private Direction getRotatedFacing(Direction worldFacing) {
        if (worldFacing == Direction.UP || worldFacing == Direction.DOWN) {
            return worldFacing;
        }

//        Direction f = worldFacing;
        int rotate = facing.get() == Direction.NORTH ? 0 : facing.get() == Direction.EAST ? 1 : facing.get() == Direction.SOUTH ? 2 : 3;
        for (int i = 0; i < rotate; i++) {
            worldFacing = worldFacing.rotateYCCW();
        }
//        LogHelper.dev(f + " -> " + worldFacing);
        return worldFacing;
    }

//    public int[] getSlotsForFace(Direction side) {
//        checkIOCache();
//        return sidedSlots[side.getIndex()];
//    }

    public boolean canInsertItem(int index, ItemStack itemStackIn, Direction direction) {
        checkIOCache();
        return index < 260 && canInsert[direction.getIndex()][index];
    }

    public boolean canExtractItem(int index, ItemStack stack, Direction direction) {
        checkIOCache();
        return index < 260 && canExtract[direction.getIndex()][index];
    }

    //endregion

    //region Model Rendering Stuff

    private void updateModel() {
        int xCoord = this.pos.getX();
        int yCoord = this.pos.getY();
        int zCoord = this.pos.getZ();
        ++ticksSinceSync;

        if (!world.isRemote && numPlayersUsing.get() != 0 && (ticksSinceSync + xCoord + yCoord + zCoord) % 200 == 0) {
            numPlayersUsing.zero();

            for (PlayerEntity entityplayer : this.world.getEntitiesWithinAABB(PlayerEntity.class, new AxisAlignedBB((double) ((float) xCoord - 5.0F), (double) ((float) yCoord - 5.0F), (double) ((float) zCoord - 5.0F), (double) ((float) (xCoord + 1) + 5.0F), (double) ((float) (yCoord + 1) + 5.0F), (double) ((float) (zCoord + 1) + 5.0F)))) {
                if (entityplayer.openContainer instanceof ContainerDraconiumChest) {
                    TileDraconiumChest tile = ((ContainerDraconiumChest) entityplayer.openContainer).tile;
                    if (tile == this) {
                        numPlayersUsing.inc();
                    }
                }
            }
        }

        this.prevLidAngle = this.lidAngle;

        if (this.numPlayersUsing.get() > 0 && this.lidAngle == 0.0F) {
            double d1 = (double) xCoord + 0.5D;
            double d2 = (double) zCoord + 0.5D;
            this.world.playSound((PlayerEntity) null, d1, (double) yCoord + 0.5D, d2, SoundEvents.BLOCK_CHEST_OPEN, SoundCategory.BLOCKS, 0.5F, this.world.rand.nextFloat() * 0.1F + 0.9F);
        }

        if (this.numPlayersUsing.get() == 0 && this.lidAngle > 0.0F || this.numPlayersUsing.get() > 0 && this.lidAngle < 1.0F) {
            float f2 = this.lidAngle;

            if (this.numPlayersUsing.get() > 0) {
                this.lidAngle += 0.1F;
            } else {
                this.lidAngle -= 0.1F;
            }

            if (this.lidAngle > 1.0F) {
                this.lidAngle = 1.0F;
            }

            if (this.lidAngle < 0.5F && f2 >= 0.5F) {
                double d3 = (double) xCoord + 0.5D;
                double d0 = (double) zCoord + 0.5D;
                this.world.playSound((PlayerEntity) null, d3, (double) yCoord + 0.5D, d0, SoundEvents.BLOCK_CHEST_CLOSE, SoundCategory.BLOCKS, 0.5F, this.world.rand.nextFloat() * 0.1F + 0.9F);
            }

            if (this.lidAngle < 0.0F) {
                this.lidAngle = 0.0F;
            }
        }
    }

    @Override
    public void onPlayerOpenContainer(PlayerEntity player) {
        super.onPlayerOpenContainer(player);
        if (!player.isSpectator()) {
            if (numPlayersUsing.get() < 0) {
                numPlayersUsing.zero();
            }

            numPlayersUsing.inc();
        }
    }

    @Override
    public void onPlayerCloseContainer(PlayerEntity player) {
        super.onPlayerCloseContainer(player);
        if (!player.isSpectator()) {
            numPlayersUsing.dec();
        }
    }

    @Override
    public boolean canRenderBreaking() {
        return true;
    }

    //endregion

    public enum AutoSmeltMode {
        OFF(false),
        FILL(false),
        LOCK(true),
        ALL(false);

        public final boolean keep1Item;

        AutoSmeltMode(boolean keep1Item) {
            this.keep1Item = keep1Item;
        }
    }

    /*
     * ###### Bit Packing ######
     *  N  S  W  E    D  U  F  UU
     * [00 00 00 00] [00 00 00 00]
     *
     * 00 - Disabled
     * 01 - In
     * 10 - Out
     * 11 - In/Out
     *
     * TODO
     *
     * ***Rendering first so i can get rotations worked out
     * ***Side IO implementation
     * ***Furnace IO Implementation
     * ***Model Implementation
     * ***-Gui model implementation
     *
     * ~Prevent NBT being sent to the client
     * ***Ore doubling
     * */
    public class SlotRegion {
        public int regionID;
        public int xPos = 0;
        public int yPos = 0;
        public int xSize = 0;
        public int ySize = 0;
        public boolean enabled = false;
        public boolean invalid = false;
        public int colour;
        public final boolean isDefault;
        private Rectangle rectangle = new Rectangle();
        private byte nsweIO = (byte) 0xFF;
        private byte dufIO = (byte) 0xFF;
        public List<Integer> controledSlots = new ArrayList<>();

        public SlotRegion(int regionID, int colour, boolean isDefault) {
            this.regionID = regionID;
            this.colour = colour;
            this.isDefault = isDefault;
        }

        public void toNBT(CompoundNBT compound) {
            if (!isDefault) {
                compound.putByte("SR_" + regionID + "_xPos", (byte) xPos);
                compound.putByte("SR_" + regionID + "_yPos", (byte) yPos);
                compound.putByte("SR_" + regionID + "_xSize", (byte) xSize);
                compound.putByte("SR_" + regionID + "_ySize", (byte) ySize);
                compound.putBoolean("SR_" + regionID + "_Enabled", enabled);
                compound.putBoolean("SR_" + regionID + "_Invalid", invalid);
            }
            compound.putByte("SR_" + regionID + "_nsweIO", nsweIO);
            compound.putByte("SR_" + regionID + "_dufIO", dufIO);
        }

        public void fromNBT(CompoundNBT compound) {
            if (!isDefault) {
                xPos = compound.getByte("SR_" + regionID + "_xPos");
                yPos = compound.getByte("SR_" + regionID + "_yPos");
                xSize = compound.getByte("SR_" + regionID + "_xSize");
                ySize = compound.getByte("SR_" + regionID + "_ySize");
                enabled = compound.getBoolean("SR_" + regionID + "_Enabled");
                invalid = compound.getBoolean("SR_" + regionID + "_Invalid");
            }
            nsweIO = compound.getByte("SR_" + regionID + "_nsweIO");
            dufIO = compound.getByte("SR_" + regionID + "_dufIO");

            if (xPos < 0 || xPos > 25) xPos = 0;
            if (yPos < 0 || yPos > 9) yPos = 0;
            if (xSize < 1 || xSize > 26 - xPos) xSize = 0;
            if (ySize < 1 || ySize > 10 - yPos) ySize = 0;

            validate();
        }

        /**
         * @return a rectangle which defines the group of slots in the inventory that this region controls.
         */
        public Rectangle getRectangle() {
            if (!isDefault) {
                rectangle.setBounds(xPos, yPos, xSize, ySize);
            }
            return rectangle;
        }

        /**
         * @return true if the region configuration is valid.
         */
        public boolean isValid() {
            return isDefault || (!invalid && xSize > 0 && ySize > 0);
        }

        /**
         * Validates the region.
         * If the size is invalid or this region overlaps another active region validation will not pass.
         *
         * @return true if the region is valid.
         */
        public boolean validate() {
            invalid = false;
            if (isDefault) {
                invalid = false;
            } else if (xSize <= 0 || ySize <= 0) {
                invalid = true;
            } else {
                for (TileDraconiumChest.SlotRegion region : slotRegions) {
                    if (region != this && region.enabled && region.getRectangle().intersects(getRectangle())) {
                        invalid = true;
                        break;
                    }
                }
            }
            return !invalid;
        }

        /**
         * @param facing north = front, south = back, west = right, east = left
         * @return 0 = disabled, 1 = in, 2 = out, 3 = in/out
         */
        public int getFaceIO(Direction facing) {
            if (facing == null) {
                return getFurnaceIO();
            }
            switch (facing) {
                case DOWN:
                    return (dufIO >> 6) & 3;
                case UP:
                    return (dufIO >> 4) & 3;
                case NORTH:
                    return (nsweIO >> 6) & 3;
                case SOUTH:
                    return (nsweIO >> 4) & 3;
                case WEST:
                    return (nsweIO >> 2) & 3;
                case EAST:
                    return nsweIO & 3;
            }
            return 0;
        }

        public int getFurnaceIO() {
            return (dufIO >> 2) & 3;
        }

        public void setFaceIO(Direction facing, int io) {
            if (facing == null) {
                setFurnaceIO(io);
                return;
            }
            switch (facing) {
                case DOWN:
                    dufIO = (byte) ((dufIO & 0x3F) | io << 6);
                    return;
                case UP:
                    dufIO = (byte) ((dufIO & 0xCF) | io << 4);
                    return;
                case NORTH:
                    nsweIO = (byte) ((nsweIO & 0x3F) | io << 6);
                    return;
                case SOUTH:
                    nsweIO = (byte) ((nsweIO & 0xCF) | io << 4);
                    return;
                case WEST:
                    nsweIO = (byte) ((nsweIO & 0xF3) | io << 2);
                    return;
                case EAST:
                    nsweIO = (byte) ((nsweIO & 0xFC) | io);
            }
        }

        public void setFurnaceIO(int io) {
            dufIO = (byte) ((dufIO & 0xF3) | io << 2);
        }

        /**
         * @return true if region is enabled and contains this slot, Will also return true if this is the default region.
         */
        public boolean controlsSlot(int index) {
            if (isDefault) {
                return true;
            }

            int x = index % 26;
            int y = index / 26;

            return enabled && validate() && getRectangle().contains(x, y);
        }

        public boolean isActive() {
            return isDefault || (enabled && isValid());
        }

        /**
         * @return true if it is possible to insert OR extract from this face.
         */
        public boolean hasIO(Direction facing) {
            return getFaceIO(facing) > 0;
        }

        public boolean canInsert(Direction facing) {
            return (getFaceIO(facing) & 1) == 1;
        }

        public boolean canExtract(Direction facing) {
            return (getFaceIO(facing) & 2) == 2;
        }
    }
}


