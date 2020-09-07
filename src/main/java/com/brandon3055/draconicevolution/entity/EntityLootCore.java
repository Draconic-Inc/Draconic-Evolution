package com.brandon3055.draconicevolution.entity;

import com.brandon3055.brandonscore.inventory.InventoryDynamic;
import com.brandon3055.draconicevolution.DraconicEvolution;
import com.brandon3055.draconicevolution.network.PacketLootSync;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.event.ForgeEventFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by brandon3055 on 26/08/2016.
 */
public class EntityLootCore extends Entity {
    private InventoryDynamic inventory = new InventoryDynamic();
    public double rotX = 0;
    public double rotY = 0;
    public int timeOffset = 0;
    public int pickupDellay = 0;
    public Map<ItemStack, Integer> displayMap = new HashMap<ItemStack, Integer>();
    public boolean isLooking = false;
    public float lookAnimation = 0;
    private int despawnTimer = 0;
    private int lifespan = 6000;
    private boolean canDespawn = true;

    public EntityLootCore(World world) {
        super(world);
        this.setSize(0.30F, 0.30F);
        rotX = world.rand.nextDouble();
        rotY = world.rand.nextDouble();
        timeOffset = world.rand.nextInt(1000);
    }

    public EntityLootCore(World world, InventoryDynamic inventory) {
        super(world);
        this.inventory = inventory;
        updateStored();
    }

    @Override
    public boolean canBeCollidedWith() {
        return true;
    }

    @Override
    protected void entityInit() {
    }

    @Override
    public void onUpdate() {
        if (world.isRemote) {
            if (isLooking && lookAnimation < 1F) {
                lookAnimation += 0.05F;
            } else if (!isLooking && lookAnimation > 0F) {
                lookAnimation -= 0.05F;
            }
        } else if (canDespawn && despawnTimer++ > lifespan) {
            setDead();
        }

        super.onUpdate();
    }

    @Override
    public void onEntityUpdate() {
        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;

        if (!this.hasNoGravity()) {
            this.motionY -= 0.03999999910593033D;
        }

        this.noClip = this.pushOutOfBlocks(this.posX, (this.getEntityBoundingBox().minY + this.getEntityBoundingBox().maxY) / 2.0D, this.posZ);
        this.move(MoverType.SELF, this.motionX, this.motionY, this.motionZ);

        float f = 0.98F;

        if (this.onGround) {
            f = this.world.getBlockState(new BlockPos(MathHelper.floor(this.posX), MathHelper.floor(this.getEntityBoundingBox().minY) - 1, MathHelper.floor(this.posZ))).getBlock().slipperiness * 0.98F;
        }

        this.motionX *= (double) f;
        this.motionY *= 0.9800000190734863D;
        this.motionZ *= (double) f;

        super.onEntityUpdate();
    }

    @Override
    public void onCollideWithPlayer(EntityPlayer player) {
        if (world.isRemote) {
            return;
        }

        despawnTimer = 0;

        if (pickupDellay > 0) {
            pickupDellay--;
            return;
        }

        if (inventory.xp > 0) {
            player.addExperience(inventory.xp);
            inventory.xp = 0;
        }

        boolean inserted = false;

        for (int i = inventory.getSizeInventory() - 1; i >= 0; i--) {
            ItemStack stack = inventory.getStackInSlot(i);
            if (!stack.isEmpty()) {
                ItemStack copy = stack.copy();

                EntityItem item = new EntityItem(world, posX, posY, posZ, copy);
                item.pickupDelay = 0;
                item.onCollideWithPlayer(player);
                copy = item.getItem();

                if (item.isDead || !ItemStack.areItemStacksEqual(stack, copy)) {
                    if (item.isDead || copy.isEmpty()) {
                        inventory.setInventorySlotContents(i, ItemStack.EMPTY);
                        inserted = true;
                    } else {
                        inventory.setInventorySlotContents(i, copy);
                        inserted = copy.getCount() < stack.getCount();
                    }
                }
            }
        }

        if (inserted) {
            this.world.playSound(null, player.posX, player.posY, player.posZ, SoundEvents.ENTITY_ITEM_PICKUP, SoundCategory.PLAYERS, 0.2F, ((this.rand.nextFloat() - this.rand.nextFloat()) * 0.7F + 1.0F) * 2.0F);
            updateStored();
        }

        pickupDellay = 10;

        if (inventory.getSizeInventory() == 1 && inventory.getStackInSlot(0).isEmpty()) {
            setDead();
        }
    }

    private void updateStored() {
        if (world.isRemote) {
            return;
        }

        displayMap = new HashMap<>();

        for (int i = 0; i < inventory.getSizeInventory(); i++) {
            ItemStack insert = inventory.getStackInSlot(i);
            if (insert.isEmpty()) {
                continue;
            }

            boolean added = false;
            for (ItemStack stack : displayMap.keySet()) {
                if (insert.isItemEqual(stack) && ItemStack.areItemStackTagsEqual(insert, stack)) {
                    added = true;
                    displayMap.put(stack, displayMap.get(stack) + insert.getCount());
                    break;
                }
            }

            if (!added) {
                displayMap.put(insert, insert.getCount());
            }
        }

        for (EntityPlayerMP playerMP : trackingPlayers) {
            DraconicEvolution.network.sendTo(new PacketLootSync(getEntityId(), displayMap), playerMP);
        }
    }

    private List<EntityPlayerMP> trackingPlayers = new ArrayList<>();

    @Override
    public void addTrackingPlayer(EntityPlayerMP player) {
        trackingPlayers.add(player);
        DraconicEvolution.network.sendTo(new PacketLootSync(getEntityId(), displayMap), player);
        super.addTrackingPlayer(player);
    }

    @Override
    public void removeTrackingPlayer(EntityPlayerMP player) {
        trackingPlayers.remove(player);
        super.removeTrackingPlayer(player);
    }

    @Override
    protected void readEntityFromNBT(NBTTagCompound compound) {
        inventory.readFromNBT(compound);
        updateStored();
        despawnTimer = compound.getInteger("DespawnTimer");
        lifespan = compound.getInteger("Lifespan");
        canDespawn = compound.getBoolean("CanDespawn");
    }

    @Override
    protected void writeEntityToNBT(NBTTagCompound compound) {
        inventory.writeToNBT(compound);
        compound.setInteger("DespawnTimer", despawnTimer);
        compound.setInteger("Lifespan", lifespan);
        compound.setBoolean("CanDespawn", canDespawn);
    }
}
