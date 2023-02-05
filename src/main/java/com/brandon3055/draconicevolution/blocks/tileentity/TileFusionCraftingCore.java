package com.brandon3055.draconicevolution.blocks.tileentity;

import codechicken.lib.data.MCDataInput;
import com.brandon3055.brandonscore.api.TechLevel;
import com.brandon3055.brandonscore.blocks.TileBCore;
import com.brandon3055.brandonscore.client.particle.IntParticleType;
import com.brandon3055.brandonscore.inventory.ItemHandlerIOControl;
import com.brandon3055.brandonscore.inventory.TileItemStackHandler;
import com.brandon3055.brandonscore.lib.IChangeListener;
import com.brandon3055.brandonscore.lib.IInteractTile;
import com.brandon3055.brandonscore.lib.Vec3D;
import com.brandon3055.brandonscore.lib.datamanager.*;
import com.brandon3055.brandonscore.utils.FacingUtils;
import com.brandon3055.brandonscore.utils.Utils;
import com.brandon3055.draconicevolution.DEConfig;
import com.brandon3055.draconicevolution.DraconicEvolution;
import com.brandon3055.draconicevolution.api.DraconicAPI;
import com.brandon3055.draconicevolution.api.crafting.IFusionInjector;
import com.brandon3055.draconicevolution.api.crafting.IFusionInventory;
import com.brandon3055.draconicevolution.api.crafting.IFusionRecipe;
import com.brandon3055.draconicevolution.api.crafting.IFusionStateMachine;
import com.brandon3055.draconicevolution.client.DEParticles;
import com.brandon3055.draconicevolution.client.render.tile.fxhandlers.ITileFXHandler;
import com.brandon3055.draconicevolution.handlers.DESounds;
import com.brandon3055.draconicevolution.init.DEContent;
import com.brandon3055.draconicevolution.inventory.ContainerFusionCraftingCore;
import com.brandon3055.draconicevolution.inventory.GuiLayoutFactories;
import com.google.common.collect.Streams;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.network.NetworkHooks;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by brandon3055 on 11/06/2016.
 */
public class TileFusionCraftingCore extends TileBCore implements IFusionInventory, IFusionStateMachine, MenuProvider, IInteractTile, IChangeListener {

    private final ManagedEnum<FusionState> fusionState = register(new ManagedEnum<>("fusion_state", FusionState.START, DataFlags.SAVE_NBT_SYNC_TILE));
    private final ManagedResource activeRecipe = register(new ManagedResource("active_recipe", DataFlags.SAVE_NBT_SYNC_TILE));
    private final ManagedBool crafting = register(new ManagedBool("is_crafting", DataFlags.SAVE_NBT_SYNC_TILE));
    private final ManagedInt fusionCounter = register(new ManagedInt("fusion_counter", DataFlags.SAVE_NBT));

    public final ManagedTextComponent userStatus = register(new ManagedTextComponent("user_status", DataFlags.SAVE_NBT_SYNC_CONTAINER));
    public final ManagedFloat craftAnimProgress = register(new ManagedFloat("craft_anim_progress", DataFlags.SYNC_TILE));
    public final ManagedShort craftAnimLength = register(new ManagedShort("craft_anim_length", DataFlags.SYNC_TILE));
    public final ManagedFloat progress = register(new ManagedFloat("progress", -1, DataFlags.SAVE_NBT_SYNC_CONTAINER));

    public TileItemStackHandler itemHandler = new TileItemStackHandler(2);
    public ITileFXHandler fxHandler;

    private List<IFusionInjector> injectorCache = null;
    private List<BlockPos> injectorPositions = new ArrayList<>();
    private IFusionRecipe recipeCache = null;
    private TechLevel minTierCache = null;

    public TileFusionCraftingCore(BlockPos pos, BlockState state) {
        super(DEContent.tile_crafting_core, pos, state);
        capManager.setInternalManaged("inventory", CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, itemHandler).saveBoth();
        capManager.set(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, new ItemHandlerIOControl(itemHandler).setInsertCheck((slot, stack) -> slot == 0).setExtractCheck((slot, stack) -> slot == 1));
        itemHandler.setContentsChangeListener(i -> localInventoryChange());
        itemHandler.setStackValidator((slot, stack) -> slot == 0);
        fxHandler = DraconicEvolution.proxy.createFusionFXHandler(this);
        activeRecipe.addValueListener(e -> recipeCache = null);
    }

    public void startCraft() {
        if (isCrafting()) {
            inventoryChanged();
            return;
        }

        updateInjectors();
        IFusionRecipe recipe = level.getRecipeManager().getRecipeFor(DraconicAPI.FUSION_RECIPE_TYPE, this, level).orElse(null);
        setActiveRecipe(recipe);
        if (recipe == null || !recipe.canStartCraft(this, level, null)) {
            return;
        }

        setCounter(0);
        setFusionState(FusionState.START);
        crafting.set(true);
    }

    @Override
    public void receivePacketFromClient(MCDataInput data, ServerPlayer client, int id) {
        startCraft();
    }

    @Override
    public void receivePacketFromServer(MCDataInput data, int id) {
        if (id == 0) {
            injectorPositions.clear();
            int count = data.readShort();
            for (int i = 0; i < count; i++) {
                injectorPositions.add(data.readPos());
            }
            injectorCache = null;
        } else if (id == 1) {// Craft Complete
            level.addParticle(ParticleTypes.EXPLOSION, getBlockPos().getX() + 0.5, getBlockPos().getY() + 0.5, getBlockPos().getZ() + 0.5, 1.0D, 0.0D, 0.0D);
//            level.playLocalSound(getBlockPos().getX() + 0.5, getBlockPos().getY() + 0.5, getBlockPos().getZ() + 0.5, SoundEvents.GENERIC_EXPLODE, SoundCategory.BLOCKS, 4.0F, (1.0F + (this.level.random.nextFloat() - this.level.random.nextFloat()) * 0.2F) * 0.7F, false);
            level.playLocalSound(getBlockPos().getX() + 0.5, getBlockPos().getY() + 0.5, getBlockPos().getZ() + 0.5, DESounds.fusionComplete, SoundSource.BLOCKS, 4.0F, (1.0F + (this.level.random.nextFloat() - this.level.random.nextFloat()) * 0.2F) * 0.7F, false);

            for (int i = 0; i < 100; i++) {
                double velX = (level.random.nextDouble() - 0.5) * 0.1;
                double velY = (level.random.nextDouble() - 0.5) * 0.1;
                double velZ = (level.random.nextDouble() - 0.5) * 0.1;
                level.addParticle(new IntParticleType.IntParticleData(DEParticles.energy_basic, 0, 255, 255, 64), getBlockPos().getX() + 0.5, getBlockPos().getY() + 0.5, getBlockPos().getZ() + 0.5, velX, velY, velZ);
            }
        }
    }

    @Override
    public void tick() {
        super.tick();
        if (level.isClientSide) {
            fxHandler.tick();
        }

        if (crafting.get() && !level.isClientSide) {
            IFusionRecipe recipe = getActiveRecipe();
            if (recipe != null) {
                recipe.tickFusionState(this, this, level);
            } else {
                cancelCraft();
            }
        }
    }

    public boolean updateInjectors() {
        minTierCache = null;
        if (isCrafting() || level.isClientSide) {
            return true;
        }

        if (injectorPositions.isEmpty()) {
            setFusionStatus(-1, null);
        }

        injectorCache = null;
        List<BlockPos> oldPositions = new ArrayList<>(injectorPositions);
        injectorPositions.clear();
        int range = DEConfig.fusionInjectorRange;
        int radius = 1;
        List<TileFusionCraftingInjector> searchTiles = Streams.concat(
                        BlockPos.betweenClosedStream(worldPosition.offset(-range, -radius, -radius), worldPosition.offset(range, radius, radius)), //X
                        BlockPos.betweenClosedStream(worldPosition.offset(-radius, -range, -radius), worldPosition.offset(radius, range, radius)), //Y
                        BlockPos.betweenClosedStream(worldPosition.offset(-radius, -radius, -range), worldPosition.offset(radius, radius, range))  //Z
                )
                .map(level::getBlockEntity)
                .filter(e -> e instanceof TileFusionCraftingInjector)
                .map(e -> (TileFusionCraftingInjector) e)
                .collect(Collectors.toList());

        for (TileFusionCraftingInjector tile : searchTiles) {
            Vec3D dirVec = new Vec3D(tile.getBlockPos()).subtract(worldPosition);
            double dist = Utils.getCardinalDistance(tile.getBlockPos(), worldPosition);

            if (dist <= DEConfig.fusionInjectorMinDist) {
                setFusionStatus(-1, new TranslatableComponent("fusion_status.draconicevolution.injector_close").withStyle(ChatFormatting.RED));
                injectorPositions.clear();
                return false;
            }

            if (Direction.getNearest((int) dirVec.x, (int) dirVec.y, (int) dirVec.z) == tile.getRotation().getOpposite()) {
                BlockPos pos = tile.getBlockPos();
                Direction facing = tile.getRotation();
                boolean obstructed = false;
                for (BlockPos bp : BlockPos.betweenClosed(pos.relative(facing), pos.relative(facing, FacingUtils.distanceInDirection(pos, worldPosition, facing) - 1))) {
                    if (!level.isEmptyBlock(bp) && (level.getBlockState(bp).canOcclude() || level.getBlockEntity(bp) instanceof TileFusionCraftingInjector)) {
                        obstructed = true;
                        tile.setCore(null);
                        break;
                    }
                }
                if (!obstructed && tile.setCore(this)) {
                    injectorPositions.add(tile.getBlockPos());
                }
            }
        }

        dirtyBlock();
        if (!oldPositions.equals(injectorPositions)) {
            sendPacketToChunk(e -> {
                e.writeShort(injectorPositions.size());
                injectorPositions.forEach(e::writePos);
            }, 0);
        }
        return true;
    }

    public void inventoryChanged() {
        setChanged();
        if (!updateInjectors()) {
            return;
        }
        if (isCrafting()) {
            IFusionRecipe recipe = getActiveRecipe();
            if (recipe == null || !recipe.matches(this, level)) {
                cancelCraft();
            }
        } else if (!level.isClientSide) {
            IFusionRecipe recipe = level.getRecipeManager().getRecipeFor(DraconicAPI.FUSION_RECIPE_TYPE, this, level).orElse(null);
            if (recipe != null) {
                recipe.canStartCraft(this, level, e -> setFusionStatus(-1, e));
            } else {
                setFusionStatus(-1, new TranslatableComponent("fusion_status.draconicevolution.no_recipe"));
            }
            setActiveRecipe(recipe);
        }
    }

    private void localInventoryChange() {
        updateBlock();
        inventoryChanged();
    }

    @Override
    public boolean onBlockActivated(BlockState state, Player player, InteractionHand handIn, BlockHitResult hit) {
        if (player instanceof ServerPlayer) {
            updateInjectors(); //TODO just have the injectors poke the core when placed so i dont need this
            NetworkHooks.openGui((ServerPlayer) player, this, worldPosition);
        }
        return true;
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int currentWindowIndex, Inventory playerInventory, Player player) {
        return new ContainerFusionCraftingCore(currentWindowIndex, player.getInventory(), this, GuiLayoutFactories.FUSION_CRAFTING_CORE);
    }

    @Nonnull
    @Override
    public ItemStack getCatalystStack() {
        return itemHandler.getStackInSlot(0);
    }

    @Nonnull
    @Override
    public ItemStack getOutputStack() {
        return itemHandler.getStackInSlot(1);
    }

    @Override
    public void setCatalystStack(@Nonnull ItemStack stack) {
        itemHandler.setStackInSlot(0, stack);
    }

    @Override
    public void setOutputStack(@Nonnull ItemStack stack) {
        itemHandler.setStackInSlot(1, stack);
    }

    @Override
    public List<IFusionInjector> getInjectors() {
        if (injectorCache == null) {
            injectorCache = injectorPositions.stream()
                    .map(level::getBlockEntity)
                    .filter(e -> e instanceof IFusionInjector)
                    .map(e -> (IFusionInjector) e)
                    .collect(Collectors.toList());
        }
        return injectorCache;
    }

    @Override
    public TechLevel getMinimumTier() {
        if (minTierCache == null) {
            minTierCache = getInjectors().stream()
                    .filter(e -> !e.getInjectorStack().isEmpty())
                    .sorted(Comparator.comparing(e -> e.getInjectorTier().index))
                    .map(IFusionInjector::getInjectorTier)
                    .findFirst()
                    .orElse(TechLevel.DRACONIUM);
        }

        return minTierCache;
    }

    @Override
    public void onNeighborChange(BlockPos neighbor) {
        super.onNeighborChange(neighbor);
        updateInjectors();
    }

    @Override
    public FusionState getFusionState() {
        return fusionState.get();
    }

    @Override
    public void setFusionState(FusionState state) {
        fusionState.set(state);
        setChanged();
    }

    @Override
    public void completeCraft() {
        crafting.set(false);
        inventoryChanged();
        sendPacketToChunk(e -> {}, 1);
    }

    @Override
    public void cancelCraft() {
        crafting.set(false);
        getInjectors().forEach(e -> e.setEnergyRequirement(0, 0));
        setFusionStatus(-1, new TranslatableComponent("fusion_status.draconicevolution.canceled"));
        level.playSound(null, getBlockPos(), DESounds.fusionComplete, SoundSource.BLOCKS, 4.0F, (1.0F + (this.level.random.nextFloat() - this.level.random.nextFloat()) * 0.2F) * 0.7F);
        inventoryChanged();
    }

    @Override
    public int getCounter() {
        return fusionCounter.get();
    }

    @Override
    public void setCounter(int count) {
        fusionCounter.set(count);
    }

    @Override
    public void setFusionStatus(double progress, Component stateText) {
        this.progress.set((float) progress);
        userStatus.set(stateText);
    }

    @Override
    public void setCraftAnimation(float progress, int length) {
        craftAnimProgress.set(progress);
        craftAnimLength.set((short) length);
    }

    public boolean isCrafting() {
        return crafting.get();
    }

    @Nullable
    public IFusionRecipe getActiveRecipe() {
        if (recipeCache == null) {
            if (activeRecipe.get() != null) {
                Recipe<?> recipe = level.getRecipeManager().byKey(activeRecipe.get()).orElse(null);
                if (recipe instanceof IFusionRecipe) {
                    recipeCache = (IFusionRecipe) recipe;
                }
            }
        }
        return recipeCache;
    }

    public void setActiveRecipe(@Nullable IFusionRecipe recipe) {
        recipeCache = recipe;
        activeRecipe.set(recipe == null ? null : recipe.getId());
    }

    public int getComparatorOutput() {
        if (!getOutputStack().isEmpty()) {
            return 15;
        } else if (crafting.get()) {
            return 1 + getFusionState().ordinal();
        } else {
            IFusionRecipe recipe = level.getRecipeManager().getRecipeFor(DraconicAPI.FUSION_RECIPE_TYPE, this, level).orElse(null);
            if (recipe != null && recipe.canStartCraft(this, level, null)) {
                return 1;
            }
            return 0;
        }
    }

    @Override
    public void writeExtraNBT(CompoundTag compound) {
        super.writeExtraNBT(compound);
        compound.putLongArray("injector_positions", injectorPositions.stream().mapToLong(BlockPos::asLong).toArray());
    }

    @Override
    public void readExtraNBT(CompoundTag compound) {
        super.readExtraNBT(compound);
        injectorPositions = Arrays.stream(compound.getLongArray("injector_positions")).mapToObj(BlockPos::of).collect(Collectors.toList());
        injectorCache = null;
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public AABB getRenderBoundingBox() {
        return new AABB(worldPosition.offset(-16, -16, -16), worldPosition.offset(17, 17, 17));
    }
}

