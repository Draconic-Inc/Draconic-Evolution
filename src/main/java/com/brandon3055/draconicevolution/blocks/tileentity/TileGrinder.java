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
import com.brandon3055.brandonscore.lib.datamanager.DataFlags;
import com.brandon3055.brandonscore.lib.datamanager.ManagedBool;
import com.brandon3055.brandonscore.lib.datamanager.ManagedByte;
import com.brandon3055.brandonscore.lib.datamanager.ManagedInt;
import com.brandon3055.brandonscore.lib.entityfilter.EntityFilter;
import com.brandon3055.brandonscore.lib.entityfilter.FilterType;
import com.brandon3055.brandonscore.utils.EnergyUtils;
import com.brandon3055.draconicevolution.DEConfig;
import com.brandon3055.draconicevolution.DEOldConfig;
import com.brandon3055.draconicevolution.api.modules.lib.ModularOPStorage;
import com.brandon3055.draconicevolution.blocks.machines.Grinder;
import com.brandon3055.draconicevolution.init.DEContent;
import com.brandon3055.draconicevolution.inventory.ContainerDETile;
import com.brandon3055.draconicevolution.inventory.GuiLayoutFactories;
import com.brandon3055.draconicevolution.utils.LogHelper;
import com.mojang.authlib.GameProfile;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.common.util.FakePlayerFactory;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.network.NetworkHooks;

import javax.annotation.Nullable;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

public class TileGrinder extends TileBCore implements IRSSwitchable, MenuProvider, IInteractTile {

    private static FakePlayer cachedFakePlayer;
    public final ManagedBool active = register(new ManagedBool("active", DataFlags.SAVE_NBT_SYNC_TILE, DataFlags.TRIGGER_UPDATE));
    public final ManagedByte aoe = register(new ManagedByte("aoe", (byte) getMaxAOE(), DataFlags.SAVE_BOTH_SYNC_TILE, DataFlags.CLIENT_CONTROL));
    public final ManagedBool showAOE = register(new ManagedBool("show_aoe", DataFlags.SAVE_NBT_SYNC_TILE, DataFlags.CLIENT_CONTROL));
    public final ManagedBool collectItems = register(new ManagedBool("collect_items", true, DataFlags.SAVE_NBT_SYNC_CONTAINER, DataFlags.CLIENT_CONTROL));
    public final ManagedBool collectXP = register(new ManagedBool("collect_xp", true, DataFlags.SAVE_NBT_SYNC_CONTAINER, DataFlags.CLIENT_CONTROL));
    public final ManagedInt storedXP = register(new ManagedInt("stored_xp", DataFlags.SAVE_BOTH_SYNC_CONTAINER));
    public TileItemStackHandler itemHandler = new TileItemStackHandler(2);
    public EntityFilter entityFilter;
    public OPStorage opStorage = new ModularOPStorage(this, 1000000, 128000, 0);

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
    public AABB killZone;
    private int coolDown = 0;
    private LivingEntity nextTarget = null;
    private int killRate = 5; //Number of ticks between kills

    public TileGrinder(BlockPos pos, BlockState state) {
        super(DEContent.tile_grinder, pos, state);
        enablePlayerAccessTracking(true);

        capManager.setManaged("energy", CapabilityOP.OP, opStorage).saveBoth().syncContainer();
        installIOTracker(opStorage);

        capManager.setInternalManaged("inventory", CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, itemHandler).saveBoth();
        setupPowerSlot(itemHandler, 0, opStorage, false);

        entityFilter = new EntityFilter(true, FilterType.HOSTILE, FilterType.TAMED, FilterType.ADULTS, FilterType.ENTITY_TYPE, FilterType.FILTER_GROUP, FilterType.PLAYER);
        entityFilter.setDirtyHandler(this::setChanged);
        entityFilter.setTypePredicate(e -> e != FilterType.PLAYER || DEConfig.allowGrindingPlayers);
        entityFilter.setupServerPacketHandling(() -> createClientBoundPacket(0), packet -> sendPacketToClients(getAccessingPlayers(), packet));
        entityFilter.setupClientPacketHandling(() -> createServerBoundPacket(0));
        setClientSidePacketHandler(0, input -> entityFilter.receivePacketFromServer(input));
        setServerSidePacketHandler(0, (input, player) -> entityFilter.receivePacketFromClient(input));
        setSavedDataObject("entity_filter", entityFilter);
        setItemSavedDataObject("entity_filter", entityFilter);

        aoe.setValidator(value -> (byte) MathHelper.clip(value, 1, getMaxAOE()));
        aoe.addValueListener(e -> killZone = null);

        enableTileDebug();
    }

    private boolean canExtractItem(int slot, ItemStack stack) {
        return EnergyUtils.isEmptyOrInvalid(stack);
    }

    @Override
    public void onPlayerOpenContainer(Player player) {
        super.onPlayerOpenContainer(player);
        if (player instanceof ServerPlayer) {
            entityFilter.syncClient((ServerPlayer) player);
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
                debug("Cool down: " + coolDown);
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
        int eph = DEConfig.grinderEnergyPerHeart;
        boolean isActive = isTileEnabled();

        //Only run if there is a reasonable energy buffer
        if (isActive && opStorage.getOPStored() < eph * 50L) {
            isActive = false;
        }

        level.setBlockAndUpdate(worldPosition, level.getBlockState(worldPosition).setValue(Grinder.ACTIVE, isActive));
        return active.set(isActive);
    }

    public void validateKillZone(boolean forceReCalc) {
        if (forceReCalc || killZone == null) {
            BlockState state = level.getBlockState(worldPosition);
            Direction facing = state.getValue(Grinder.FACING);
            int aoe = this.aoe.get();
            BlockPos pos1 = worldPosition.offset(-(aoe - 1), -(aoe - 1), -(aoe - 1));
            BlockPos pos2 = worldPosition.offset(aoe, aoe, aoe);
            pos1 = pos1.offset(facing.getStepX() * aoe, 0, facing.getStepZ() * aoe);
            pos2 = pos2.offset(facing.getStepX() * aoe, 0, facing.getStepZ() * aoe);
            killZone = new AABB(pos1, pos2);
            debug("Kill zone updated: " + killZone);
        }
    }

    private boolean attackTarget() {
        if (nextTarget == null || !nextTarget.isAlive()) {
            debug("Next target is null or dead: " + nextTarget);
            return true;
        }

        ItemStack weapon = itemHandler.getStackInSlot(1);
        if (weapon.isEmpty() || weapon.getDamageValue() >= weapon.getMaxDamage() - 1) {
            weapon = ItemStack.EMPTY;
        }
        getFakePlayer().setItemInHand(InteractionHand.MAIN_HAND, weapon);

        int eph = DEConfig.grinderEnergyPerHeart;
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

            debug("Dealt " + damage + " damage to entity: " + nextTarget);
            nextTarget = null;
            opStorage.modifyEnergyStored(-cost);
            return true;
        }
        debug("Failed to deal damage to entity: " + nextTarget.getType().getDescription().getString() + " Waiting 3 ticks...");
        if (!killZone.intersects(nextTarget.getBoundingBox())) {
            nextTarget = null;
        }

        return false;
    }

    private void queNextTarget() {
        List<LivingEntity> entitiesInRange = level.getEntitiesOfClass(LivingEntity.class, killZone, entityFilter.predicate());
        debug("Searching for next target, " + entitiesInRange.size() + " targets in range");
        boolean foundInvulnerable = false;

        while (!entitiesInRange.isEmpty()) {
            LivingEntity randEntity = entitiesInRange.remove(level.random.nextInt(entitiesInRange.size()));
            debug("Checking Target: " + randEntity);
            if (isValidEntity(randEntity)) {
                debug("Found valid target: " + randEntity);
                if (randEntity.isInvulnerable()) {
                    debug("Target is invulnerable! searching for softer target...");
                    foundInvulnerable = true;
                } else {
                    nextTarget = randEntity;
                    //Throw the sword!
                    sendPacketToChunk(output -> output.writeInt(nextTarget.getId()), 1);
                    level.playSound(null, worldPosition, SoundEvents.TRIDENT_THROW, SoundSource.BLOCKS, 1, 0.55F + (level.random.nextFloat() * 0.1F));
                    coolDown = killRate;
                    return;
                }
            }
        }

        coolDown = foundInvulnerable ? 5 : 100;
        debug("No attachable target found. Will check again in " + coolDown + " Ticks");
        nextTarget = null;
    }

    private boolean isValidEntity(LivingEntity livingBase) {
        if (!livingBase.isAlive()) {
            debug("Target Invalid: " + livingBase + ", [Already Dead]");
            return false;
        }
        if (livingBase instanceof Player && !DEConfig.allowGrindingPlayers) {
            debug("Target Invalid: " + livingBase + ", [Is Player]");
            return false;
        }
        if (DEConfig.grinderBlackList.isEmpty()) return true;
        ResourceLocation reg = livingBase.getType().getRegistryName();
        return !(reg != null && DEConfig.grinderBlackList.contains(reg.toString()));
    }

    private void handleLootCollection() {
        List<ExperienceOrb> xp = level.getEntitiesOfClass(ExperienceOrb.class, killZone.inflate(4, 4, 4));
        debug("Detected: " + xp.size() + " XP entities");
        for (ExperienceOrb orb : xp) {
            if (!orb.isAlive()) continue;
            if (collectXP.get() && storedXP.get() + orb.value <= getXPStorageCapacity()) {
                storedXP.add(orb.value);
                orb.discard();
            } else if (orb.age < 5400) {
                orb.age = 5700;
            }
        }

        if (collectItems.get()) {
            List<ItemEntity> items = level.getEntitiesOfClass(ItemEntity.class, killZone.inflate(1, 1, 1));
            debug("Detected: " + items.size() + " Item entities");
            for (Direction dir : Direction.values()) {
                BlockEntity target = level.getBlockEntity(worldPosition.relative(dir));
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
                                    next.discard();
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
    public void receivePacketFromClient(MCDataInput data, ServerPlayer client, int id) {
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
            cachedFakePlayer = FakePlayerFactory.get((ServerLevel) level, new GameProfile(UUID.fromString("5b5689b9-e43d-4282-a42a-dc916f3616b7"), "Draconic Evolution Grinder"));
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
    public AbstractContainerMenu createMenu(int currentWindowIndex, Inventory playerInventory, Player player) {
        return new ContainerDETile<>(DEContent.container_grinder, currentWindowIndex, playerInventory, this, GuiLayoutFactories.GRINDER_LAYOUT);
    }

    @Override
    public boolean onBlockActivated(BlockState state, Player player, InteractionHand handIn, BlockHitResult hit) {
        if (player instanceof ServerPlayer) {
            NetworkHooks.openGui((ServerPlayer) player, this, worldPosition);
        }
        return true;
    }

    @Override
    public AABB getRenderBoundingBox() {
        if (showAOE.get()) {
            return INFINITE_EXTENT_AABB;
        }
        return super.getRenderBoundingBox();
    }
}
