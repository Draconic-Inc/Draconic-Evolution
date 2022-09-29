package com.brandon3055.draconicevolution.entity;

import com.brandon3055.brandonscore.inventory.InventoryDynamic;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
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

    public EntityLootCore(EntityType<?> entityTypeIn, Level worldIn) {
        super(entityTypeIn, worldIn);
    }

    @Override
    protected void defineSynchedData() {

    }

    @Override
    public Packet<?> getAddEntityPacket() {
        return null;
    }

    //    public EntityLootCore(World world) {
//        super(world);
////        this.setSize(0.30F, 0.30F);
//        rotX = world.rand.nextDouble();
//        rotY = world.rand.nextDouble();
//        timeOffset = world.rand.nextInt(1000);
//    }
//
//    public EntityLootCore(World world, InventoryDynamic inventory) {
//        super(world);
//        this.inventory = inventory;
//        updateStored();
//    }

    @Override
    public boolean isPickable() {
        return true;
    }

//    @Override
//    protected void entityInit() {
//    }

    @Override
    public void tick() {
        if (level.isClientSide) {
            if (isLooking && lookAnimation < 1F) {
                lookAnimation += 0.05F;
            }
            else if (!isLooking && lookAnimation > 0F) {
                lookAnimation -= 0.05F;
            }
        }
        else if (canDespawn && despawnTimer++ > lifespan) {
            discard();
        }

        super.tick();
    }

//    @Override
//    public void onEntityUpdate() {
//        this.prevPosX = this.posX;
//        this.prevPosY = this.posY;
//        this.prevPosZ = this.posZ;
//
//        if (!this.hasNoGravity()) {
//            this.motionY -= 0.03999999910593033D;
//        }
//
//        this.noClip = this.pushOutOfBlocks(this.posX, (this.getEntityBoundingBox().minY + this.getEntityBoundingBox().maxY) / 2.0D, this.posZ);
//        this.move(MoverType.SELF, this.motionX, this.motionY, this.motionZ);
//
//        float f = 0.98F;
//
//        if (this.onGround) {
//            f = this.world.getBlockState(new BlockPos(MathHelper.floor(this.posX), MathHelper.floor(this.getEntityBoundingBox().minY) - 1, MathHelper.floor(this.posZ))).getBlock().slipperiness * 0.98F;
//        }
//
//        this.motionX *= (double) f;
//        this.motionY *= 0.9800000190734863D;
//        this.motionZ *= (double) f;
//
//        super.onEntityUpdate();
//    }

    @Override
    public void playerTouch(Player player) {
        if (level.isClientSide) {
            return;
        }

        despawnTimer = 0;

        if (pickupDellay > 0) {
            pickupDellay--;
            return;
        }

        if (inventory.xp > 0) {
            player.giveExperiencePoints(inventory.xp);
            inventory.xp = 0;
        }

        boolean inserted = false;


        for (int i = inventory.getContainerSize() - 1; i >= 0; i--) {
            ItemStack stack = inventory.getItem(i);
            if (!stack.isEmpty()) {
                int start = stack.getCount();

                ItemEntity item = new ItemEntity(level, 0, 0, 0, stack);
                item.setPos(getX(), getY(), getZ());
                int result = ForgeEventFactory.onItemPickup(item, player);

                if (result == 1 || stack.getCount() <= 0 || player.getInventory().add(stack)) {
                    if (!item.isAlive()) {
                        stack.setCount(0);
                    }

                    if (stack.getCount() == 0) {
                        inventory.setItem(i, ItemStack.EMPTY);
                    }
                    else {
                        inventory.setItem(i, stack);
                    }

                    if (stack.getCount() < start) {
                        inserted = true;
                    }
                }
            }
        }

        if (inserted) {
            this.level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ITEM_PICKUP, SoundSource.PLAYERS, 0.2F, ((this.random.nextFloat() - this.random.nextFloat()) * 0.7F + 1.0F) * 2.0F);
            updateStored();
        }

        pickupDellay = 10;

        if (inventory.getContainerSize() == 1 && inventory.getItem(0).isEmpty()) {
            discard();
        }
    }

    private void updateStored() {
        if (level.isClientSide) {
            return;
        }

        displayMap = new HashMap<>();

        for (int i = 0; i < inventory.getContainerSize(); i++) {
            ItemStack insert = inventory.getItem(i);
            if (insert.isEmpty()) {
                continue;
            }

            boolean added = false;
            for (ItemStack stack : displayMap.keySet()) {
                if (insert.sameItem(stack) && ItemStack.tagMatches(insert, stack)) {
                    added = true;
                    displayMap.put(stack, displayMap.get(stack) + insert.getCount());
                    break;
                }
            }

            if (!added) {
                displayMap.put(insert, insert.getCount());
            }
        }

        for (ServerPlayer playerMP : trackingPlayers) {
            //TODO Packets
//            DraconicEvolution.network.sendTo(new PacketLootSync(getEntityId(), displayMap), playerMP);
        }
    }

    private List<ServerPlayer> trackingPlayers = new ArrayList<>();

    @Override
    public void startSeenByPlayer(ServerPlayer player) {
        trackingPlayers.add(player);
//        DraconicEvolution.network.sendTo(new PacketLootSync(getEntityId(), displayMap), player);
        super.startSeenByPlayer(player);
    }

    @Override
    public void stopSeenByPlayer(ServerPlayer player) {
        trackingPlayers.remove(player);
        super.stopSeenByPlayer(player);
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag compound) {
        inventory.readFromNBT(compound);
        updateStored();
        despawnTimer = compound.getInt("DespawnTimer");
        lifespan = compound.getInt("Lifespan");
        canDespawn = compound.getBoolean("CanDespawn");
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compound) {
        inventory.writeToNBT(compound);
        compound.putInt("DespawnTimer", despawnTimer);
        compound.putInt("Lifespan", lifespan);
        compound.putBoolean("CanDespawn", canDespawn);
    }

    //    @Override
//    protected void readEntityFromNBT(CompoundNBT compound) {
//        inventory.readFromNBT(compound);
//        updateStored();
//        despawnTimer = compound.getInt("DespawnTimer");
//        lifespan = compound.getInt("Lifespan");
//        canDespawn = compound.getBoolean("CanDespawn");
//    }
//
//    @Override
//    protected void writeEntityToNBT(CompoundNBT compound) {
//        inventory.writeToNBT(compound);
//        compound.putInt("DespawnTimer", despawnTimer);
//        compound.putInt("Lifespan", lifespan);
//        compound.putBoolean("CanDespawn", canDespawn);
//    }
}
