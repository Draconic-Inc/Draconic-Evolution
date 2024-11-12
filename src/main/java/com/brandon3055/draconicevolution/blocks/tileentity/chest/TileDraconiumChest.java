package com.brandon3055.draconicevolution.blocks.tileentity.chest;

import codechicken.lib.math.MathHelper;
import com.brandon3055.brandonscore.api.power.OPStorage;
import com.brandon3055.brandonscore.blocks.TileBCore;
import com.brandon3055.brandonscore.capability.CapabilityOP;
import com.brandon3055.brandonscore.inventory.TileItemStackHandler;
import com.brandon3055.brandonscore.lib.IInteractTile;
import com.brandon3055.brandonscore.lib.IRSSwitchable;
import com.brandon3055.brandonscore.lib.datamanager.DataFlags;
import com.brandon3055.brandonscore.lib.datamanager.ManagedInt;
import com.brandon3055.brandonscore.lib.datamanager.ManagedShort;
import com.brandon3055.brandonscore.utils.EnergyUtils;
import com.brandon3055.draconicevolution.api.modules.lib.ModularOPStorage;
import com.brandon3055.draconicevolution.blocks.DraconiumChest;
import com.brandon3055.draconicevolution.init.DEContent;
import com.brandon3055.draconicevolution.inventory.DraconiumChestMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;

/**
 * Created by brandon3055 on 28/09/2016.
 */
public class TileDraconiumChest extends TileBCore implements IRSSwitchable, MenuProvider, IInteractTile {

    public final ManagedInt colour = register(new ManagedInt("colour", 0x640096, DataFlags.SAVE_BOTH_SYNC_TILE, DataFlags.CLIENT_CONTROL));
    public final ManagedShort numPlayersUsing = register(new ManagedShort("num_players_using", DataFlags.SYNC_TILE));

    public float prevLidAngle;
    public float lidAngle;

    public TileItemStackHandler mainInventory = new TileItemStackHandler(this, 260);
    public TileItemStackHandler craftingItems = new TileItemStackHandler(this, 9);
    public TileItemStackHandler furnaceItems = new TileItemStackHandler(this, 5);
    public TileItemStackHandler capacitorInv = new TileItemStackHandler(this, 1);
    public OPStorage opStorage = new ModularOPStorage(this, 1000000, 128000, 0);

    public SmeltingLogic smeltingLogic = new SmeltingLogic(this, furnaceItems, mainInventory, opStorage);

    public TileDraconiumChest(BlockPos pos, BlockState state) {
        super(DEContent.TILE_DRACONIUM_CHEST.get(), pos, state);
        capManager.setManaged("energy", CapabilityOP.BLOCK, opStorage).saveBoth().syncContainer();
        capManager.setManaged("main_inv", Capabilities.ItemHandler.BLOCK, mainInventory).saveBoth();
        capManager.setInternalManaged("crafting_inv", Capabilities.ItemHandler.BLOCK, craftingItems).saveBoth();
        capManager.setInternalManaged("furnace_inv", Capabilities.ItemHandler.BLOCK, furnaceItems).saveBoth();
        capManager.setInternalManaged("energy_inv", Capabilities.ItemHandler.BLOCK, capacitorInv).saveBoth();

        furnaceItems.setStackValidator(stack -> smeltingLogic.isSmeltable(stack));
        furnaceItems.setContentsChangeListener(e -> smeltingLogic.inputInventoryChanged());
        mainInventory.setContentsChangeListener(e -> smeltingLogic.outputInventoryChanged().scheduleAutoFeed());
        mainInventory.setStackValidator(DraconiumChest::isStackValid);
        capacitorInv.setStackValidator(EnergyUtils::isEnergyItem);

        smeltingLogic.setFeedSourceInv(mainInventory);
        installIOTracker(opStorage);
    }

    public static void register(RegisterCapabilitiesEvent event) {
        capability(event, DEContent.TILE_DRACONIUM_CHEST, CapabilityOP.BLOCK);
        capability(event, DEContent.TILE_DRACONIUM_CHEST, Capabilities.ItemHandler.BLOCK);
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
            level.playSound(null, getBlockPos(), SoundEvents.CHEST_CLOSE, SoundSource.BLOCKS, 0.5F, level.random.nextFloat() * 0.1F + 0.9F);
        } else if (prevLidAngle == 0 && lidAngle > 0) {
            level.playSound(null, getBlockPos(), SoundEvents.CHEST_OPEN, SoundSource.BLOCKS, 0.5F, level.random.nextFloat() * 0.1F + 0.9F);
        }
    }

    @Override
    public InteractionResult onBlockUse(BlockState state, Player player, InteractionHand hand, BlockHitResult hit) {
        if (player instanceof ServerPlayer) {
            player.openMenu(this, worldPosition);
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public AbstractContainerMenu createMenu(int currentWindowIndex, Inventory playerInventory, Player player) {
        return new DraconiumChestMenu(DEContent.MENU_DRACONIUM_CHEST.get(), currentWindowIndex, playerInventory, this);
    }

    @Override
    public void writeExtraTileAndStack(CompoundTag compound) {
        smeltingLogic.saveAdditionalNBT(compound);
        compound.putBoolean("inv_migrated", true);
    }

    @Override
    public void readExtraTileAndStack(CompoundTag compound) {
        smeltingLogic.loadAdditionalNBT(compound);
    }
}


