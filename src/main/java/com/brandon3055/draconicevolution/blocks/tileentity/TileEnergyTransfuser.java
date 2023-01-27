package com.brandon3055.draconicevolution.blocks.tileentity;

import codechicken.lib.raytracer.RayTracer;
import codechicken.lib.raytracer.SubHitBlockHitResult;
import com.brandon3055.brandonscore.api.power.IOPStorage;
import com.brandon3055.brandonscore.api.power.IOPStorageModifiable;
import com.brandon3055.brandonscore.blocks.TileBCore;
import com.brandon3055.brandonscore.capability.CapabilityOP;
import com.brandon3055.brandonscore.inventory.ContainerBCTile;
import com.brandon3055.brandonscore.inventory.TileItemStackHandler;
import com.brandon3055.brandonscore.lib.IInteractTile;
import com.brandon3055.brandonscore.lib.IRSSwitchable;
import com.brandon3055.brandonscore.lib.datamanager.DataFlags;
import com.brandon3055.brandonscore.lib.datamanager.ManagedBool;
import com.brandon3055.brandonscore.lib.datamanager.ManagedEnum;
import com.brandon3055.brandonscore.utils.EnergyUtils;
import com.brandon3055.draconicevolution.init.DEContent;
import com.brandon3055.draconicevolution.inventory.ContainerDETile;
import com.brandon3055.draconicevolution.inventory.GuiLayoutFactories;
import com.brandon3055.draconicevolution.utils.ItemCapMerger;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.network.NetworkHooks;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Created by brandon3055 on 12/12/2020.
 */
public class TileEnergyTransfuser extends TileBCore implements IInteractTile, MenuProvider, IRSSwitchable {

    public TileItemStackHandler itemNorth = new TileItemStackHandler(1).setSlotLimit(1).setStackValidator(EnergyUtils::isEnergyItem);
    public TileItemStackHandler itemEast = new TileItemStackHandler(1).setSlotLimit(1).setStackValidator(EnergyUtils::isEnergyItem);
    public TileItemStackHandler itemSouth = new TileItemStackHandler(1).setSlotLimit(1).setStackValidator(EnergyUtils::isEnergyItem);
    public TileItemStackHandler itemWest = new TileItemStackHandler(1).setSlotLimit(1).setStackValidator(EnergyUtils::isEnergyItem);
    private IItemHandlerModifiable[] indexedItemHandlers = {itemNorth, itemEast, itemSouth, itemWest};
    public IItemHandlerModifiable itemsCombined = (IItemHandlerModifiable) ItemCapMerger.merge(itemNorth, itemEast, itemSouth, itemWest);
    public IOPStorage opStorage = new OPIOAdapter();
    @SuppressWarnings("unchecked")
    public ManagedEnum<ItemIOMode>[] ioModes = new ManagedEnum[4]; //North, East, South, West
    public ManagedBool balancedMode = register(new ManagedBool("balance_mode", DataFlags.SAVE_NBT_SYNC_CONTAINER, DataFlags.CLIENT_CONTROL));

    public TileEnergyTransfuser(BlockPos pos, BlockState state) {
        super(DEContent.tile_energy_transfuser, pos, state);
        capManager.setInternalManaged("item_north", CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, itemNorth).syncTile().saveBoth();
        capManager.setInternalManaged("item_east", CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, itemEast).syncTile().saveBoth();
        capManager.setInternalManaged("item_south", CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, itemSouth).syncTile().saveBoth();
        capManager.setInternalManaged("item_west", CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, itemWest).syncTile().saveBoth();
        capManager.set(CapabilityOP.OP, opStorage, Direction.UP, Direction.DOWN, null);

        capManager.set(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, new ItemIOAdapter(0, itemNorth), Direction.NORTH);
        capManager.set(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, new ItemIOAdapter(1, itemEast), Direction.EAST);
        capManager.set(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, new ItemIOAdapter(2, itemSouth), Direction.SOUTH);
        capManager.set(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, new ItemIOAdapter(3, itemWest), Direction.WEST);
        capManager.set(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, new ItemIOAdapter(-1, itemsCombined), Direction.UP, Direction.DOWN, null);

        for (int i = 0; i < 4; i++) {
            ioModes[i] = register(new ManagedEnum<>("item_mode_" + i, ItemIOMode.CHARGE, DataFlags.SAVE_BOTH_SYNC_TILE, DataFlags.CLIENT_CONTROL));
        }
    }

    @Override
    public void tick() {
        super.tick();
        if (level.isClientSide || !isTileEnabled()) {
            return;
        }

        for (int i = 0; i < 4; i++) {
            ItemIOMode sourcemode = ioModes[i].get();
            if (sourcemode.discharge) {
                ItemStack stack = itemsCombined.getStackInSlot(i);
                IOPStorage sourceStorage = EnergyUtils.getStorage(stack);
                if (sourceStorage == null) continue;

                boolean canExtract = sourceStorage.canExtract();
                //I want to be able to discharge DE tools, armor, etc but i dont want them to be usable as buffer items.
                boolean extractOverride = !canExtract && sourcemode == ItemIOMode.DISCHARGE && sourceStorage instanceof IOPStorageModifiable;
                if (canExtract || extractOverride) {
                    long maxExtract;
                    if (extractOverride) {
                        maxExtract = Math.min(((IOPStorageModifiable) sourceStorage).maxExtract(), sourceStorage.getOPStored());
                    } else {
                        maxExtract = sourceStorage.extractOP(sourceStorage.getOPStored(), true);
                    }
                    if (maxExtract == 0) continue;

                    long totalSent = 0;
                    long sent = sendEnergyTo(maxExtract, Direction.DOWN);
                    maxExtract -= sent;
                    totalSent += sent;
                    sent = sendEnergyTo(maxExtract, Direction.UP);
                    maxExtract -= sent;
                    totalSent += sent;

                    if (maxExtract > 0) {
                        for (int j = 0; j < 4; j++) {
                            if (j == i || maxExtract == 0) continue;
                            ItemIOMode targetMode = ioModes[j].get();
                            //We dont want to bother sending power between buffers.
                            if (targetMode.charge && (targetMode == ItemIOMode.CHARGE || sourcemode == ItemIOMode.DISCHARGE)) {
                                sent = EnergyUtils.insertEnergy(itemsCombined.getStackInSlot(j), maxExtract, false);
                                maxExtract -= sent;
                                totalSent += sent;
                            }
                        }
                    }

                    if (extractOverride) {
                        ((IOPStorageModifiable) sourceStorage).modifyEnergyStored(-totalSent);
                    } else {
                        sourceStorage.extractOP(totalSent, false);
                    }
                }
            }
        }
    }

    @Override
    public boolean onBlockActivated(BlockState state, Player player, InteractionHand handIn, BlockHitResult trace) {
        if (level.isClientSide) {
            return true;
        }

        HitResult hit = RayTracer.retrace(player);
        int slot = hit instanceof SubHitBlockHitResult ? ((SubHitBlockHitResult) hit).subHit : -1;
        if (slot > -1 && slot < 4) {
            ItemStack stack = itemsCombined.getStackInSlot(slot);
            ItemStack heldStack = player.getItemInHand(handIn);
            if (!stack.isEmpty() && heldStack.isEmpty()) {
                player.setItemInHand(handIn, stack);
                itemsCombined.setStackInSlot(slot, ItemStack.EMPTY);
                return true;
            } else if (stack.isEmpty() && !heldStack.isEmpty()) {
                if (itemsCombined.isItemValid(slot, heldStack)) {
                    if (heldStack.getCount() > 1) {
                        ItemStack copy = heldStack.copy();
                        copy.setCount(1);
                        itemsCombined.setStackInSlot(slot, copy);
                        heldStack.shrink(1);
                    } else {
                        itemsCombined.setStackInSlot(slot, heldStack);
                        player.setItemInHand(handIn, ItemStack.EMPTY);
                    }
                    return true;
                }
            }
        }

        if (player instanceof ServerPlayer) {
            NetworkHooks.openGui((ServerPlayer) player, this, worldPosition);
        }
        return true;
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inv, Player player) {
        return new ContainerDETile<>(DEContent.container_energy_transfuser, id, player.getInventory(), this, GuiLayoutFactories.TRANSFUSER_LAYOUT);
    }

    public enum ItemIOMode {
        /**
         * - Charge from External
         * - Charge from Buffer
         * - Can be extracted when full
         */
        CHARGE(0, "mode_charge", true, false, 0xFF8500),
        /**
         * - Discharge to External
         * - Discharge to Buffer
         * - Can be extracted when empty
         */
        DISCHARGE(1, "mode_discharge", false, true, 0x0050FF),
        /**
         * - Charge from External
         * - Charge from Discharge Slots
         * - Discharge to External
         * - Discharge to Charge Slots
         * - Can not be extracted
         */
        BUFFER(2, "mode_buffer", true, true, 0xFF00FF),
        DISABLED(3, "mode_disabled", false, false, 0x202020); //Also disables slots

        private int index;
        private String name;
        public final boolean discharge;
        public final boolean charge;
        private int colour;

        ItemIOMode(int index, String name, boolean charge, boolean discharge, int colour) {
            this.index = index;
            this.name = name;
            this.discharge = discharge;
            this.charge = charge;
            this.colour = colour;
        }

        public ItemIOMode nextMode(boolean previous) {
            return values()[Math.floorMod(index + (previous ? -1 : 1), values().length)];
        }

        public String getName() {
            return name;
        }

        public String getSpriteName() {
            return "transfuser/" + name;
        }

        public boolean canExtract(IOPStorage storage) {
            switch (this) {
                case CHARGE:
                    return storage.getOPStored() >= storage.getMaxOPStored();
                case DISCHARGE:
                    return storage.getOPStored() == 0 || (!storage.canExtract() && !(storage instanceof IOPStorageModifiable));
                default:
                    return false;
            }
        }

        public int getColour() {
            return colour;
        }
    }

    public class OPIOAdapter implements IOPStorage {

        @Override
        public long receiveOP(long maxReceive, boolean simulate) {
            if (!isTileEnabled()) return 0;
            long totalAccepted = 0;
            for (int i = 0; i < 4; i++) {
                ItemIOMode mode = ioModes[i].get();
                if (mode.charge) {
                    ItemStack stack = itemsCombined.getStackInSlot(i);
                    long accepted;
                    if (balancedMode.get()) {
                        accepted = EnergyUtils.insertEnergy(stack, maxReceive / 4, simulate);
                    } else {
                        accepted = EnergyUtils.insertEnergy(stack, maxReceive, simulate);
                        maxReceive -= accepted;
                    }
                    totalAccepted += accepted;
                }
            }
            return totalAccepted;
        }

        @Override
        public long extractOP(long maxExtract, boolean simulate) {
            if (!isTileEnabled()) return 0;
            long totalExtracted = 0;
            for (int i = 0; i < 4; i++) {
                ItemIOMode mode = ioModes[i].get();
                if (mode.discharge) {
                    ItemStack stack = itemsCombined.getStackInSlot(i);
                    long extracted = EnergyUtils.extractEnergy(stack, maxExtract, simulate);
                    totalExtracted += extracted;
                    maxExtract -= extracted;
                }
            }
            return totalExtracted;
        }

        @Override
        public long getOPStored() {
            long total = 0;
            for (int i = 0; i < 4; i++) {
                total += EnergyUtils.getEnergyStored(itemsCombined.getStackInSlot(i));
            }
            return total;
        }

        @Override
        public long getMaxOPStored() {
            long total = 0;
            for (int i = 0; i < 4; i++) {
                total += EnergyUtils.getMaxEnergyStored(itemsCombined.getStackInSlot(i));
            }
            return total;
        }

        @Override
        public boolean canExtract() {
            if (!isTileEnabled()) return false;
            for (ManagedEnum<ItemIOMode> mode : ioModes) {
                if (mode.get().discharge) return true;
            }
            return false;
        }

        @Override
        public boolean canReceive() {
            if (!isTileEnabled()) return false;
            for (ManagedEnum<ItemIOMode> mode : ioModes) {
                if (mode.get().charge) return true;
            }
            return false;
        }

        @Override
        public long modifyEnergyStored(long amount) {
            return 0; //Invalid operation for this device
        }
    }

    public class ItemIOAdapter implements IItemHandler {
        private int index;
        private IItemHandler handler;

        public ItemIOAdapter(int index, IItemHandler handler) {
            this.index = index;
            this.handler = handler;
        }

        @Override
        public int getSlots() {
            return handler.getSlots();
        }

        @Nonnull
        @Override
        public ItemStack getStackInSlot(int slot) {
            return handler.getStackInSlot(slot);
        }

        @Nonnull
        @Override
        public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
            ItemIOMode mode = getMode(slot);
            if (mode == ItemIOMode.DISABLED) {
                return stack;
            }
            return handler.insertItem(slot, stack, simulate);
        }

        @Nonnull
        @Override
        public ItemStack extractItem(int slot, int amount, boolean simulate) {
            ItemIOMode mode = getMode(slot);
            IOPStorage storage = EnergyUtils.getStorage(handler.getStackInSlot(slot));
            if (storage != null && mode.canExtract(storage)) {
                return handler.extractItem(slot, amount, simulate);
            }
            return ItemStack.EMPTY;
        }

        @Override
        public int getSlotLimit(int slot) {
            return handler.getSlotLimit(slot);
        }

        @Override
        public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
            return handler.isItemValid(slot, stack);
        }

        private ItemIOMode getMode(int slot) {
            int i = index != -1 ? index : slot;
            return ioModes[i].get();
        }
    }

}
