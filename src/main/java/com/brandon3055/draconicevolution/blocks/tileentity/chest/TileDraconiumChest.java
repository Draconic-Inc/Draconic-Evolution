package com.brandon3055.draconicevolution.blocks.tileentity.chest;

import codechicken.lib.inventory.InventoryUtils;
import codechicken.lib.math.MathHelper;
import com.brandon3055.brandonscore.api.power.OPStorage;
import com.brandon3055.brandonscore.blocks.TileBCore;
import com.brandon3055.brandonscore.capability.CapabilityOP;
import com.brandon3055.brandonscore.inventory.TileItemStackHandler;
import com.brandon3055.brandonscore.lib.IInteractTile;
import com.brandon3055.brandonscore.lib.IRSSwitchable;
import com.brandon3055.brandonscore.lib.datamanager.ManagedInt;
import com.brandon3055.brandonscore.lib.datamanager.ManagedShort;
import com.brandon3055.brandonscore.utils.EnergyUtils;
import com.brandon3055.draconicevolution.DraconicEvolution;
import com.brandon3055.draconicevolution.blocks.DraconiumChest;
import com.brandon3055.draconicevolution.init.DEContent;
import com.brandon3055.draconicevolution.inventory.ContainerDraconiumChest;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraftforge.fml.network.NetworkHooks;
import net.minecraftforge.items.CapabilityItemHandler;

import static com.brandon3055.brandonscore.lib.datamanager.DataFlags.*;
import static net.minecraft.util.SoundEvents.CHEST_CLOSE;
import static net.minecraft.util.SoundEvents.CHEST_OPEN;

/**
 * Created by brandon3055 on 28/09/2016.
 */
public class TileDraconiumChest extends TileBCore implements ITickableTileEntity, IRSSwitchable, INamedContainerProvider, IInteractTile {

    public final ManagedInt colour = register(new ManagedInt("colour", 0x640096, SAVE_BOTH_SYNC_TILE, CLIENT_CONTROL));
    public final ManagedShort numPlayersUsing = register(new ManagedShort("num_players_using", SYNC_TILE));

    public float prevLidAngle;
    public float lidAngle;

    public TileItemStackHandler mainInventory = new TileItemStackHandler(260);
    public TileItemStackHandler craftingItems = new TileItemStackHandler(9);
    public TileItemStackHandler furnaceItems = new TileItemStackHandler(5);
    public TileItemStackHandler capacitorInv = new TileItemStackHandler(1);
    public OPStorage opStorage = new OPStorage(1000000, 128000, 0);

    public SmeltingLogic smeltingLogic = new SmeltingLogic(this, furnaceItems, mainInventory, opStorage);

    @Deprecated //TODO remove in a few versions
    public TileItemStackHandler old_item_handler = new TileItemStackHandler(267);

    public TileDraconiumChest() {
        super(DEContent.tile_draconium_chest);
        capManager.setManaged("energy", CapabilityOP.OP, opStorage).saveBoth().syncContainer();
        capManager.setManaged("main_inv", CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, mainInventory).saveBoth();
        capManager.setInternalManaged("crafting_inv", CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, craftingItems).saveBoth();
        capManager.setInternalManaged("furnace_inv", CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, furnaceItems).saveBoth();
        capManager.setInternalManaged("energy_inv", CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, capacitorInv).saveBoth();

        furnaceItems.setStackValidator(stack -> smeltingLogic.isSmeltable(stack));
        furnaceItems.setContentsChangeListener(e -> smeltingLogic.inputInventoryChanged());
        mainInventory.setContentsChangeListener(e -> smeltingLogic.outputInventoryChanged().scheduleAutoFeed());
        mainInventory.setStackValidator(DraconiumChest::isStackValid);
        capacitorInv.setStackValidator(EnergyUtils::isEnergyItem);

        smeltingLogic.setFeedSourceInv(mainInventory);
        installIOTracker(opStorage);

        capManager.setInternalManaged("inventory", CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, old_item_handler).saveBoth();

        /*
         * Ok so the plan for IO control
         * Will have some sort of "region manager" to assign each slot a region
         * Then will have "RegionItemHandler"s one for each face and one for the furnace.
         * The region item handlers will wrap the main inventory and talk to the region manager to find out which slots they each have access to.
         * */
    }

    @Override
    public void tick() {
        assert level != null;
        super.tick();

        //Server side logic
        if (!level.isClientSide()) {
            numPlayersUsing.set((short) getAccessingPlayers().size());
            boolean enabled = isTileEnabled();

            if (enabled && opStorage.getOPStored() < opStorage.getMaxOPStored() && !capacitorInv.getStackInSlot(0).isEmpty()) {
                EnergyUtils.transferEnergy(capacitorInv.getStackInSlot(0), opStorage);
            }

            smeltingLogic.tick(enabled);
        }

        prevLidAngle = lidAngle;
        lidAngle = (float) MathHelper.approachLinear(lidAngle, numPlayersUsing.get() > 0 ? 1 : 0, 0.1);
        if (prevLidAngle >= 0.5 && lidAngle < 0.5) {
            level.playSound(null, getBlockPos(), CHEST_CLOSE, SoundCategory.BLOCKS, 0.5F, level.random.nextFloat() * 0.1F + 0.9F);
        } else if (prevLidAngle == 0 && lidAngle > 0) {
            level.playSound(null, getBlockPos(), CHEST_OPEN, SoundCategory.BLOCKS, 0.5F, level.random.nextFloat() * 0.1F + 0.9F);
        }
    }

    @Override
    public ActionResultType onBlockUse(BlockState state, PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
        if (player instanceof ServerPlayerEntity) {
            NetworkHooks.openGui((ServerPlayerEntity) player, this, worldPosition);
        }
        return ActionResultType.SUCCESS;
    }

    @Override
    public Container createMenu(int currentWindowIndex, PlayerInventory playerInventory, PlayerEntity player) {
        return new ContainerDraconiumChest(DEContent.container_draconium_chest, currentWindowIndex, playerInventory, this);
    }

    @Override
    public void writeExtraTileAndStack(CompoundNBT compound) {
        smeltingLogic.saveAdditionalNBT(compound);
        compound.putBoolean("inv_migrated", true);
    }

    @Override
    public void readExtraTileAndStack(CompoundNBT compound) {
        smeltingLogic.loadAdditionalNBT(compound);
        DraconicEvolution.LOGGER.info("readExtraTileAndStack");
        if (!compound.contains("inv_migrated")) {
            DraconicEvolution.LOGGER.info("Chest Not Migrated!");
            DraconicEvolution.LOGGER.info(compound);
            for (int i = 0; i < old_item_handler.getSlots(); i++) {
                ItemStack stack = old_item_handler.getStackInSlot(i);
                if (stack.isEmpty()) {
                    continue;
                }
                if (i <= 259) {
                    mainInventory.setStackInSlot(i, stack);
                }else if (i <= 264) {
                    furnaceItems.setStackInSlot(i - 260, stack);
                } else if (i == 265) {
                    capacitorInv.setStackInSlot(0, stack);
                } else if (i == 266) {
                    InventoryUtils.insertItem(mainInventory, stack, false);
                }
                old_item_handler.setStackInSlot(i, ItemStack.EMPTY);
            }

            if (compound.contains("CraftingItems", 9)) {
                DraconicEvolution.LOGGER.info("Migrating Crafting Items");
                ListNBT nbttaglist = compound.getList("CraftingItems", 10);
                DraconicEvolution.LOGGER.info(nbttaglist);
                for (int i = 1; i < nbttaglist.size(); ++i) {
                    CompoundNBT nbttagcompound = nbttaglist.getCompound(i);
                    DraconicEvolution.LOGGER.info(nbttagcompound);
//                    int j = nbttagcompound.getByte("Slot") & 255;
                    if (i < craftingItems.getSlots()) {
                        craftingItems.setStackInSlot(i - 1, ItemStack.of(nbttagcompound));
                    }
                }
            }
        }
    }
}


