package com.brandon3055.draconicevolution.blocks.tileentity;

import codechicken.lib.data.MCDataInput;
import codechicken.lib.inventory.InventoryUtils;
import codechicken.lib.math.MathHelper;
import com.brandon3055.brandonscore.BrandonsCore;
import com.brandon3055.brandonscore.api.power.OPStorage;
import com.brandon3055.brandonscore.blocks.TileBCore;
import com.brandon3055.brandonscore.capability.CapabilityOP;
import com.brandon3055.brandonscore.inventory.ContainerBCTile;
import com.brandon3055.brandonscore.inventory.TileItemStackHandler;
import com.brandon3055.brandonscore.lib.IInteractTile;
import com.brandon3055.brandonscore.lib.IRSSwitchable;
import com.brandon3055.brandonscore.lib.datamanager.ManagedBool;
import com.brandon3055.brandonscore.lib.datamanager.ManagedByte;
import com.brandon3055.brandonscore.lib.datamanager.ManagedInt;
import com.brandon3055.brandonscore.lib.entityfilter.EntityFilter;
import com.brandon3055.brandonscore.utils.EnergyUtils;
import com.brandon3055.draconicevolution.DEOldConfig;
import com.brandon3055.draconicevolution.init.DEContent;
import com.brandon3055.draconicevolution.blocks.machines.Grinder;
import com.brandon3055.draconicevolution.inventory.GuiLayoutFactories;
import com.brandon3055.draconicevolution.utils.LogHelper;
import com.mojang.authlib.GameProfile;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ExperienceOrbEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.common.util.FakePlayerFactory;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fml.network.NetworkHooks;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nullable;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import static com.brandon3055.brandonscore.lib.datamanager.DataFlags.*;
import static com.brandon3055.brandonscore.lib.entityfilter.FilterType.*;

public class TileGrinder extends TileBCore implements ITickableTileEntity, IRSSwitchable, INamedContainerProvider, IInteractTile {

    private static FakePlayer cachedFakePlayer;
    public final ManagedBool active = register(new ManagedBool("active", SAVE_NBT_SYNC_TILE, TRIGGER_UPDATE));
    public final ManagedByte aoe = register(new ManagedByte("aoe", (byte) getMaxAOE(), SAVE_BOTH_SYNC_TILE, CLIENT_CONTROL));
    public final ManagedBool showAOE = register(new ManagedBool("show_aoe", SAVE_NBT_SYNC_TILE, CLIENT_CONTROL));
    public final ManagedBool collectItems = register(new ManagedBool("collect_items", true, SAVE_NBT_SYNC_CONTAINER, CLIENT_CONTROL));
    public final ManagedBool collectXP = register(new ManagedBool("collect_xp", true, SAVE_NBT_SYNC_CONTAINER, CLIENT_CONTROL));
    public final ManagedInt storedXP = register(new ManagedInt("stored_xp", SAVE_BOTH_SYNC_CONTAINER));
    public TileItemStackHandler itemHandler = new TileItemStackHandler(2);
    public EntityFilter entityFilter;
    public OPStorage opStorage = new OPStorage(1000000, 128000, 0);

    //Client side rendering fields
    public Entity targetA = null;
    public float animA = 0.8F;
    public Entity targetB = null;
    public float animB = 0.8F;
    private boolean swordFlipFlop = false;
    public float fanRotation = 0;
    public float fanSpeed = 0;
    public float aoeDisplay = 0;


    //Grinding logic fields
    public AxisAlignedBB killZone;
    private int coolDown = 0;
    private LivingEntity nextTarget = null;
    private int killRate = 5; //Number of ticks between kills

    public TileGrinder() {
        super(DEContent.tile_grinder);
        enablePlayerAccessTracking(true);

        capManager.setManaged("energy", CapabilityOP.OP, opStorage).saveBoth().syncContainer();
        installIOTracker(opStorage);

        capManager.setInternalManaged("inventory", CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, itemHandler).saveBoth();
        setupPowerSlot(itemHandler, 0, opStorage, false);

        entityFilter = new EntityFilter(true, HOSTILE, TAMED, ADULTS, ENTITY_TYPE, FILTER_GROUP, PLAYER);
        entityFilter.setDirtyHandler(this::setChanged);
        entityFilter.setTypePredicate(e -> e != PLAYER || DEOldConfig.allowGrindingPlayers);
        entityFilter.setupServerPacketHandling(() -> createClientBoundPacket(0), packet -> sendPacketToClients(getAccessingPlayers(), packet));
        entityFilter.setupClientPacketHandling(() -> createServerBoundPacket(0), packetCustom -> BrandonsCore.proxy.sendToServer(packetCustom));
        setClientSidePacketHandler(0, input -> entityFilter.receivePacketFromServer(input));
        setServerSidePacketHandler(0, (input, player) -> entityFilter.receivePacketFromClient(input));
        setSavedDataObject("entity_filter", entityFilter);
        setItemSavedDataObject("entity_filter", entityFilter);

        aoe.setValidator(value -> (byte) MathHelper.clip(value, 1, getMaxAOE()));
        aoe.addValueListener(e -> killZone = null);
    }

    private boolean canExtractItem(int slot, ItemStack stack) {
        return EnergyUtils.isEmptyOrInvalid(stack);
    }

    @Override
    public void onPlayerOpenContainer(PlayerEntity player) {
        super.onPlayerOpenContainer(player);
        if (player instanceof ServerPlayerEntity) {
            entityFilter.syncClient((ServerPlayerEntity) player);
        }
    }

    @Override
    public void tick() {
        super.tick();

        if (level.isClientSide) {
            if (animA < 1) animA += getAnimSpeed();
            else targetA = null;
            if (animB < 1) animB += getAnimSpeed();
            else targetB = null;

            fanRotation += fanSpeed;
            if (active.get() && fanSpeed < 1) {
                fanSpeed = Math.min(fanSpeed + 0.03F, 1F);
            } else if (!active.get() && fanSpeed > 0) {
                fanSpeed = Math.max(fanSpeed - 0.08F, 0F);
            }

            if (showAOE.get()) {
                aoeDisplay = (float) MathHelper.approachExp(aoeDisplay, aoe.get(), 0.1F);
            } else {
                aoeDisplay = MathHelper.approachLinear(aoeDisplay, 0.5F, 0.15F);
            }

            return;
        }

        if (updateActiveState()) {
            if (onInterval(20)) {
                validateKillZone(false);
                handleLootCollection();
            }

            if (coolDown > 0) {
                coolDown--;
                return;
            }
            validateKillZone(false);
            if (attackTarget()) {
                queNextTarget();
            }
        }
    }

    private boolean updateActiveState() {
        int eph = DEOldConfig.grinderEnergyPerHeart;
        boolean isActive = isTileEnabled();

        //Only run if there is a reasonable energy buffer
        if (isActive && opStorage.getOPStored() < eph * 50) {
            isActive = false;
        }

        level.setBlockAndUpdate(worldPosition, level.getBlockState(worldPosition).setValue(Grinder.ACTIVE, isActive));
        return active.set(isActive);
    }

    public void validateKillZone(boolean forceReCalc) {
        if (forceReCalc || killZone == null) {
            BlockState state = level.getBlockState(worldPosition);
            Direction facing = state.getValue(Grinder.FACING);
//            LogHelper.dev("Update Kill Zone: " + facing);
            int aoe = this.aoe.get();
            BlockPos pos1 = worldPosition.offset(-(aoe - 1), -(aoe - 1), -(aoe - 1));
            BlockPos pos2 = worldPosition.offset(aoe, aoe, aoe);
            pos1 = pos1.offset(facing.getStepX() * aoe, 0, facing.getStepZ() * aoe);
            pos2 = pos2.offset(facing.getStepX() * aoe, 0, facing.getStepZ() * aoe);
            killZone = new AxisAlignedBB(pos1, pos2);
        }
    }

    private boolean attackTarget() {
        if (nextTarget == null || !nextTarget.isAlive()) {
            return true;
        }

        //TODO add support for powered weapons
        ItemStack weapon = itemHandler.getStackInSlot(1);
        if (weapon.isEmpty() || weapon.getDamageValue() >= weapon.getMaxDamage() - 1) {
            weapon = ItemStack.EMPTY;
        }
        getFakePlayer().setItemInHand(Hand.MAIN_HAND, weapon);

        int eph = DEOldConfig.grinderEnergyPerHeart;
        float health = nextTarget.getHealth();

        //Ensure teh minimum damage dealt is 5 hearts. This is to help prevent endless hurt loops due to mobs with armor.
        if (health < 5) {
            health = 5;
        }

        //Calculate energy cost
        int cost = (int) (health * (float) eph);
        boolean willKill = false;

        //Restrict to the current energy stored if the cost is higher than the energy stored.
        if (cost > opStorage.getOPStored()) {
            cost = opStorage.getEnergyStored();
        } else {
            willKill = true;
        }

        //Dont mess around. If we know the mob should die lets just make it die!
        float damage = willKill ? Float.MAX_VALUE : ((float) cost / (float) eph) * 1.1F;
        DamageSource source = DamageSource.playerAttack(getFakePlayer());

        //Attack the mob and enter cooldown mode for 5 ticks if successful. Else cooldown for 3 ticks.
        if (nextTarget.hurt(source, damage)) {
            if (!weapon.isEmpty()) {
                ItemStack justInCase = weapon.copy();
                justInCase.setDamageValue(justInCase.getMaxDamage() - 1);
                weapon.hurtAndBreak(1, getFakePlayer(), fakePlayer -> itemHandler.setStackInSlot(1, justInCase));
            }

            LogHelper.dev("Grinder: Dealt " + damage + " damage to entity: " + nextTarget);
            nextTarget = null;
            opStorage.modifyEnergyStored(-cost);
            return true;
        }
        LogHelper.dev("Grinder: Failed to deal damage to entity: " + nextTarget.getType().getDescription().getString() + " Waiting 3 ticks...");
        if (!killZone.intersects(nextTarget.getBoundingBox())) {
            nextTarget = null;
        }

        return false;
    }

    private void queNextTarget() {
        List<LivingEntity> entitiesInRange = level.getEntitiesOfClass(LivingEntity.class, killZone, entityFilter.predicate());
        boolean foundInvulnerable = false;

        while (!entitiesInRange.isEmpty()) {
            LivingEntity randEntity = entitiesInRange.remove(level.random.nextInt(entitiesInRange.size()));
            if (isValidEntity(randEntity)) {
//                LogHelper.dev("Grinder: Found next target: " + randEntity);
                if (randEntity.isInvulnerable()) {
//                    LogHelper.dev("Grinder: Target is invulnerable! searching for softer target...");
                    foundInvulnerable = true;
                } else {
                    nextTarget = randEntity;
                    //Throw the sword!
                    sendPacketToChunk(output -> output.writeInt(nextTarget.getId()), 1);
                    level.playSound(null, worldPosition, SoundEvents.TRIDENT_THROW, SoundCategory.BLOCKS, 1, 0.55F + (level.random.nextFloat() * 0.1F));
                    coolDown = killRate;
                    return;
                }
            }
        }

        coolDown = foundInvulnerable ? 5 : 100;
        nextTarget = null;
    }

    private boolean isValidEntity(LivingEntity livingBase) {
        if (!livingBase.isAlive()) return false;
        if (livingBase instanceof PlayerEntity && !DEOldConfig.allowGrindingPlayers) return false;
        if (DEOldConfig.grinderBlacklist.isEmpty()) return true;
        ResourceLocation reg = livingBase.getType().getRegistryName();
        return !(reg != null && DEOldConfig.grinderBlacklist.contains(reg.toString()));
    }

    private void handleLootCollection() {
        List<ExperienceOrbEntity> xp = level.getEntitiesOfClass(ExperienceOrbEntity.class, killZone.inflate(4, 4, 4));
        for (ExperienceOrbEntity orb : xp) {
            if (!orb.isAlive()) continue;
            if (collectXP.get() && storedXP.get() + orb.value <= getXPStorageCapacity()) {
                storedXP.add(orb.value);
                orb.remove();
            } else if (orb.age < 5400) {
                orb.age = 5700;
            }
        }

        if (collectItems.get()) {
            List<ItemEntity> items = level.getEntitiesOfClass(ItemEntity.class, killZone.inflate(1, 1, 1));
            for (Direction dir : Direction.values()) {
                TileEntity target = level.getBlockEntity(worldPosition.relative(dir));
                if (target != null) {
                    LazyOptional<IItemHandler> opCap = target.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, dir.getOpposite());
                    opCap.ifPresent(iItemHandler -> {
                        Iterator<ItemEntity> i = items.iterator();
                        while (i.hasNext()) {
                            ItemEntity next = i.next();
                            if (next.isAlive()) {
                                ItemStack stack = next.getItem();
                                stack = InventoryUtils.insertItem(iItemHandler, stack, false);
                                if (stack.isEmpty()) {
                                    next.setItem(ItemStack.EMPTY);
                                    next.remove();
                                    i.remove();
                                } else {
                                    next.setItem(stack);
                                }
                            }
                        }
                    });
                }
            }
        }
    }

    @Override
    public void receivePacketFromClient(MCDataInput data, ServerPlayerEntity client, int id) {
        super.receivePacketFromClient(data, client, id);
        if (id == 1) {
            int levels = 0;
            switch (data.readByte()) {
                case 0:
                    client.giveExperiencePoints(storedXP.get());
                    storedXP.set(0);
                    return;
                case 1:
                    levels = 1;
                    break;
                case 2:
                    levels = 5;
                    break;
                case 3:
                    levels = 10;
                    break;
            }
            for (int i = 0; i < levels; i++) {
                int xp = Math.min(client.getXpNeededForNextLevel(), storedXP.get());
                storedXP.subtract(xp);
                client.giveExperiencePoints(xp);
            }
        }
        coolDown = killRate;
    }

    @Override
    public void receivePacketFromServer(MCDataInput data, int id) {
        super.receivePacketFromServer(data, id);
        if (id == 1) {
            Entity target = level.getEntity(data.readInt());
            if (target != null) {
                if (swordFlipFlop) {
                    targetA = target;
                    animA = 0;
                } else {
                    targetB = target;
                    animB = 0;
                }
                swordFlipFlop = !swordFlipFlop;
            }
        }
    }

    public int getMaxAOE() {
        return 4;
    }

    public int getXPStorageCapacity() {
        return 8192;
    }

    public float getAnimSpeed() {
        return 0.425F / killRate;
    }

    public FakePlayer getFakePlayer() {
        if (cachedFakePlayer == null) {
            cachedFakePlayer = FakePlayerFactory.get((ServerWorld) level, new GameProfile(UUID.fromString("5b5689b9-e43d-4282-a42a-dc916f3616b7"), "Draconic Evolution Grinder"));
        }
        return cachedFakePlayer;
    }

    @Override
    public void onNeighborChange(BlockPos neighbor) {
        super.onNeighborChange(neighbor);

        if (coolDown > killRate) {
            updateActiveState();
            validateKillZone(true);
            coolDown = killRate;
        }
    }

    @Nullable
    @Override
    public Container createMenu(int currentWindowIndex, PlayerInventory playerInventory, PlayerEntity player) {
        return new ContainerBCTile<>(DEContent.container_grinder, currentWindowIndex, playerInventory, this, GuiLayoutFactories.GRINDER_LAYOUT);
    }

    @Override
    public boolean onBlockActivated(BlockState state, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
        if (player instanceof ServerPlayerEntity) {
            NetworkHooks.openGui((ServerPlayerEntity) player, this, worldPosition);
        }
        return true;
    }

    @Override
    public AxisAlignedBB getRenderBoundingBox() {
        if (showAOE.get()) {
            return INFINITE_EXTENT_AABB;
        }
        return super.getRenderBoundingBox();
    }
}
