package com.brandon3055.draconicevolution.common.tileentities;

import java.util.List;
import java.util.UUID;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.IEntityMultiPart;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.*;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.common.util.FakePlayerFactory;
import net.minecraftforge.common.util.ForgeDirection;

import cofh.api.energy.IEnergyReceiver;

import com.brandon3055.draconicevolution.common.handler.BalanceConfigHandler;
import com.brandon3055.draconicevolution.common.lib.References;
import com.brandon3055.draconicevolution.common.utills.EnergyStorage;
import com.mojang.authlib.GameProfile;

import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class TileGrinder extends TileObjectSync implements ISidedInventory, IEnergyReceiver {

    // ########### variables #############//
    public int meta = -1;
    List<EntityLiving> killList;
    AxisAlignedBB killBox;
    int tick = 0;
    public double centreX;
    public double centreY = -1;
    public double centreZ;
    private ItemStack[] items;
    public int burnTime = 1;
    public int burnTimeRemaining = 0;
    public boolean disabled = false;
    private boolean disabledCach = false;
    public boolean hasPower = false;
    public boolean hasPowerCach = false;
    private boolean readyNext = false;
    public EnergyStorage internalGenBuffer = new EnergyStorage(
            BalanceConfigHandler.grinderInternalEnergyBufferSize,
            BalanceConfigHandler.grinderMaxReceive,
            0);
    public EnergyStorage externalInputBuffer = new EnergyStorage(
            BalanceConfigHandler.grinderExternalEnergyBufferSize,
            BalanceConfigHandler.grinderMaxReceive,
            0);
    public int energyPerKill = BalanceConfigHandler.grinderEnergyPerKill;
    private ItemStack diamondSword;
    public static FakePlayer fakePlayer;

    public void updateVariables() {
        if (meta == -1) meta = worldObj.getBlockMetadata(xCoord, yCoord, zCoord);
        if (centreY == -1) {
            switch (meta) {
                case 0:
                    centreX = xCoord + 0.5;
                    centreY = yCoord + 0.5;
                    centreZ = zCoord + 0.5 - 5;
                    break;
                case 1:
                    centreX = xCoord + 0.5 + 5;
                    centreY = yCoord + 0.5;
                    centreZ = zCoord + 0.5;
                    break;
                case 2:
                    centreX = xCoord + 0.5;
                    centreY = yCoord + 0.5;
                    centreZ = zCoord + 0.5 + 5;
                    break;
                case 3:
                    centreX = xCoord + 0.5 - 5;
                    centreY = yCoord + 0.5;
                    centreZ = zCoord + 0.5;
                    break;
            }
        }
    }
    // ##################################//

    public TileGrinder() {
        items = new ItemStack[1];
        diamondSword = new ItemStack(Items.diamond_sword);
        if (BalanceConfigHandler.grinderShouldUseLooting) {
            diamondSword.addEnchantment(Enchantment.looting, 3);
        }
    }

    @Override
    public void updateEntity() {
        updateVariables();

        if (worldObj.isRemote) return;

        hasPower = getActiveBuffer().getEnergyStored() >= energyPerKill;

        int burnSpeed = 2;
        int EPBT = 10;

        if (burnTimeRemaining > 0 && internalGenBuffer.getEnergyStored() < internalGenBuffer.getMaxEnergyStored()) {
            burnTimeRemaining -= burnSpeed;
            internalGenBuffer.setEnergyStored(
                    internalGenBuffer.getEnergyStored() + Math.min(
                            burnSpeed * EPBT,
                            internalGenBuffer.getMaxEnergyStored() - internalGenBuffer.getEnergyStored()));
        } else if (burnTimeRemaining <= 0) tryRefuel();

        if (readyNext && !disabled && getActiveBuffer().getEnergyStored() >= energyPerKill) {
            if (killNextEntity()) {
                getActiveBuffer().modifyEnergyStored(-energyPerKill);
            }
        }

        if (tick % 100 == 0) {
            // checkSignal();
            readyNext = true;
        }
        detectAndSendChanges(tick % 500 == 0);
        tick++;
    }

    public EnergyStorage getActiveBuffer() {
        return isExternallyPowered() ? externalInputBuffer : internalGenBuffer;
    }

    public boolean isExternallyPowered() {
        return externalInputBuffer.getEnergyStored() > energyPerKill;
    }

    public void tryRefuel() {
        if (burnTimeRemaining > 0 || internalGenBuffer.getEnergyStored() >= internalGenBuffer.getMaxEnergyStored())
            return;
        if (items[0] != null && items[0].stackSize > 0) {
            int itemBurnTime = getItemBurnTime(items[0]);

            if (itemBurnTime > 0) {
                --items[0].stackSize;
                if (this.items[0].stackSize == 0) {
                    this.items[0] = items[0].getItem().getContainerItem(items[0]);
                }
                burnTime = itemBurnTime;
                burnTimeRemaining = itemBurnTime;
            }
        }
    }

    @SuppressWarnings("unchecked")
    public boolean killNextEntity() {
        if (worldObj.isRemote) return false;
        // fakePlayer = null;
        if (fakePlayer == null) {
            fakePlayer = FakePlayerFactory.get(
                    (WorldServer) worldObj,
                    new GameProfile(UUID.fromString("5b5689b9-e43d-4282-a42a-dc916f3616b7"), "[Draconic-Evolution]"));
        }
        if (BalanceConfigHandler.grinderShouldUseLooting && (fakePlayer.getHeldItem() == null
                || !ItemStack.areItemStacksEqual(fakePlayer.getHeldItem(), diamondSword))) {
            fakePlayer.setCurrentItemOrArmor(0, diamondSword);
        }
        killBox = AxisAlignedBB.getBoundingBox(
                centreX - 4.5,
                centreY - 4.5,
                centreZ - 4.5,
                centreX + 4.5,
                centreY + 4.5,
                centreZ + 4.5);

        killList = worldObj.getEntitiesWithinAABB(EntityLiving.class, killBox);
        List<EntityXPOrb> xp = worldObj.getEntitiesWithinAABB(EntityXPOrb.class, killBox.expand(4, 4, 4));
        for (EntityXPOrb orb : xp) if (orb.xpOrbAge < 5400) orb.xpOrbAge = 5400;

        if (killList.size() > 0) {
            EntityLiving mob = killList.get(worldObj.rand.nextInt(killList.size()));
            if (mob.isEntityAlive()) {
                if (mob instanceof IEntityMultiPart) {
                    ((IEntityMultiPart) mob)
                            .attackEntityFromPart(null, DamageSource.causePlayerDamage(fakePlayer), 50000F);
                } else {
                    mob.attackEntityFrom(DamageSource.causePlayerDamage(fakePlayer), 50000F);
                }
                readyNext = true;
                return true;
            }
            readyNext = true;
            return false;
        }
        readyNext = false;
        return false;
    }

    public static int getItemBurnTime(ItemStack stack) {
        if (stack == null) {
            return 0;
        } else {
            Item item = stack.getItem();

            if (item instanceof ItemBlock && Block.getBlockFromItem(item) != Blocks.air) {
                Block block = Block.getBlockFromItem(item);

                if (block == Blocks.wooden_slab) {
                    return 150;
                }

                if (block.getMaterial() == Material.wood) {
                    return 300;
                }

                if (block == Blocks.coal_block) {
                    return 16000;
                }
            }

            if (item instanceof ItemTool && ((ItemTool) item).getToolMaterialName().equals("WOOD")) return 200;
            if (item instanceof ItemSword && ((ItemSword) item).getToolMaterialName().equals("WOOD")) return 200;
            if (item instanceof ItemHoe && ((ItemHoe) item).getToolMaterialName().equals("WOOD")) return 200;
            if (item == Items.stick) return 100;
            if (item == Items.coal) return 1600;
            if (item == Items.lava_bucket) return 20000;
            if (item == Item.getItemFromBlock(Blocks.sapling)) return 100;
            if (item == Items.blaze_rod) return 2400;
            return GameRegistry.getFuelValue(stack);
        }
    }

    @Override
    public int getSizeInventory() {
        return items.length;
    }

    @Override
    public ItemStack getStackInSlot(int i) {
        return items[i];
    }

    @Override
    public ItemStack decrStackSize(int i, int count) {
        ItemStack itemstack = getStackInSlot(i);

        if (itemstack != null) {
            if (itemstack.stackSize <= count) {
                setInventorySlotContents(i, null);
            } else {
                itemstack = itemstack.splitStack(count);
                if (itemstack.stackSize == 0) {
                    setInventorySlotContents(i, null);
                }
            }
        }
        return itemstack;
    }

    @Override
    public ItemStack getStackInSlotOnClosing(int i) {
        ItemStack item = getStackInSlot(i);
        if (item != null) setInventorySlotContents(i, null);
        return item;
    }

    @Override
    public void setInventorySlotContents(int i, ItemStack itemstack) {
        items[i] = itemstack;
        if (itemstack != null && itemstack.stackSize > getInventoryStackLimit()) {
            itemstack.stackSize = getInventoryStackLimit();
        }
    }

    @Override
    public String getInventoryName() {
        return "";
    }

    @Override
    public boolean hasCustomInventoryName() {
        return false;
    }

    @Override
    public int getInventoryStackLimit() {
        return 64;
    }

    @Override
    public boolean isUseableByPlayer(EntityPlayer player) {
        if (worldObj == null) {
            return true;
        }
        if (worldObj.getTileEntity(xCoord, yCoord, zCoord) != this) {
            return false;
        }
        return player.getDistanceSq(xCoord + 0.5, yCoord + 0.5, zCoord + 0.4) < 64;
    }

    @Override
    public void openInventory() {}

    @Override
    public void closeInventory() {}

    @Override
    public boolean isItemValidForSlot(int i, ItemStack stack) {
        return getItemBurnTime(stack) > 0;
    }

    @Override
    public int[] getAccessibleSlotsFromSide(int var1) {
        return new int[1];
    }

    @Override
    public boolean canInsertItem(int var1, ItemStack var2, int var3) {
        return true;
    }

    @Override
    public boolean canExtractItem(int var1, ItemStack var2, int var3) {
        return false;
    }

    @Override
    public void writeToNBT(NBTTagCompound compound) {
        NBTTagCompound[] tag = new NBTTagCompound[items.length];

        for (int i = 0; i < items.length; i++) {
            tag[i] = new NBTTagCompound();

            if (items[i] != null) {
                tag[i] = items[i].writeToNBT(tag[i]);
            }

            compound.setTag("Item" + i, tag[i]);
        }

        compound.setBoolean("Disabled", disabled);
        compound.setInteger("BurnTime", burnTime);
        compound.setInteger("BurnTimeRemaining", burnTimeRemaining);
        externalInputBuffer.writeToNBT(compound, "ExternalBuffer");
        internalGenBuffer.writeToNBT(compound, "InternalBuffer");

        super.writeToNBT(compound);
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        NBTTagCompound[] tag = new NBTTagCompound[items.length];

        for (int i = 0; i < items.length; i++) {
            tag[i] = compound.getCompoundTag("Item" + i);
            items[i] = ItemStack.loadItemStackFromNBT(tag[i]);
        }

        disabled = compound.getBoolean("Disabled");
        burnTime = compound.getInteger("BurnTime");
        burnTimeRemaining = compound.getInteger("BurnTimeRemaining");
        externalInputBuffer.readFromNBT(compound, "ExternalBuffer");
        internalGenBuffer.readFromNBT(compound, "InternalBuffer");

        super.readFromNBT(compound);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void receiveObjectFromServer(int index, Object object) {
        if (index == 0 && disabled != (Boolean) object) {
            disabled = (Boolean) object;
            worldObj.markBlockRangeForRenderUpdate(xCoord, yCoord, zCoord, xCoord, yCoord, zCoord);
        } else if (hasPower != (Boolean) object) {
            hasPower = (Boolean) object;
            worldObj.markBlockRangeForRenderUpdate(xCoord, yCoord, zCoord, xCoord, yCoord, zCoord);
        }
    }

    private void detectAndSendChanges(boolean sendAnyway) {
        if (disabledCach != disabled || sendAnyway) {
            disabledCach = (Boolean) sendObjectToClient(References.BOOLEAN_ID, 0, disabled);
        }
        if (hasPowerCach != hasPower || sendAnyway) {
            hasPowerCach = (Boolean) sendObjectToClient(References.BOOLEAN_ID, 1, hasPower);
        }
    }

    @Override
    public Packet getDescriptionPacket() {
        NBTTagCompound tagCompound = new NBTTagCompound();
        writeToNBT(tagCompound);
        return new S35PacketUpdateTileEntity(this.xCoord, this.yCoord, this.zCoord, 1, tagCompound);
    }

    @Override
    public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt) {
        readFromNBT(pkt.func_148857_g());
    }

    /* IEnergyHandler */
    @Override
    public boolean canConnectEnergy(ForgeDirection from) {
        return true;
    }

    @Override
    public int receiveEnergy(ForgeDirection from, int maxReceive, boolean simulate) {
        return externalInputBuffer.receiveEnergy(maxReceive, simulate);
    }

    @Override
    public int getEnergyStored(ForgeDirection from) {
        return externalInputBuffer.getEnergyStored();
    }

    @Override
    public int getMaxEnergyStored(ForgeDirection from) {
        return externalInputBuffer.getMaxEnergyStored();
    }

    public EnergyStorage getInternalBuffer() {
        return internalGenBuffer;
    }
}
