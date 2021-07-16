package com.brandon3055.draconicevolution.blocks.tileentity;

import codechicken.lib.data.MCDataInput;
import com.brandon3055.brandonscore.api.TechLevel;
import com.brandon3055.brandonscore.blocks.TileBCore;
import com.brandon3055.brandonscore.client.particle.IntParticleType;
import com.brandon3055.brandonscore.inventory.ItemHandlerIOControl;
import com.brandon3055.brandonscore.inventory.TileItemStackHandler;
import com.brandon3055.brandonscore.lib.IActivatableTile;
import com.brandon3055.brandonscore.lib.IChangeListener;
import com.brandon3055.brandonscore.lib.Vec3D;
import com.brandon3055.brandonscore.lib.datamanager.*;
import com.brandon3055.brandonscore.utils.FacingUtils;
import com.brandon3055.brandonscore.utils.Utils;
import com.brandon3055.draconicevolution.DEConfig;
import com.brandon3055.draconicevolution.DraconicEvolution;
import com.brandon3055.draconicevolution.api.DraconicAPI;
import com.brandon3055.draconicevolution.api.crafting.*;
import com.brandon3055.draconicevolution.client.DEParticles;
import com.brandon3055.draconicevolution.client.render.tile.fxhandlers.ITileFXHandler;
import com.brandon3055.draconicevolution.handlers.DESounds;
import com.brandon3055.draconicevolution.init.DEContent;
import com.brandon3055.draconicevolution.inventory.ContainerFusionCraftingCore;
import com.brandon3055.draconicevolution.inventory.GuiLayoutFactories;
import com.google.common.collect.Lists;
import com.google.common.collect.Streams;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.Explosion;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.NetworkHooks;
import net.minecraftforge.items.CapabilityItemHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.stream.Collectors;

import static com.brandon3055.brandonscore.lib.datamanager.DataFlags.*;

/**
 * Created by brandon3055 on 11/06/2016.
 */
public class TileFusionCraftingCore extends TileBCore implements IFusionInventory, IFusionStateMachine, ITickableTileEntity, INamedContainerProvider, IActivatableTile, IChangeListener {

    private final ManagedEnum<FusionState> fusionState = register(new ManagedEnum<>("fusion_state", FusionState.START, SAVE_NBT_SYNC_TILE));
    private final ManagedResource activeRecipe = register(new ManagedResource("active_recipe", SAVE_NBT_SYNC_TILE));
    private final ManagedBool crafting = register(new ManagedBool("is_crafting", SAVE_NBT_SYNC_TILE));
    private final ManagedInt fusionCounter = register(new ManagedInt("fusion_counter", SAVE_NBT));

    public final ManagedTextComponent userStatus = register(new ManagedTextComponent("user_status", SAVE_NBT_SYNC_CONTAINER));
    public final ManagedFloat craftAnimProgress = register(new ManagedFloat("craft_anim_progress", SYNC_TILE));
    public final ManagedShort craftAnimLength = register(new ManagedShort("craft_anim_length", SYNC_TILE));
    public final ManagedFloat progress = register(new ManagedFloat("progress", -1, SAVE_NBT_SYNC_CONTAINER));

    public TileItemStackHandler itemHandler = new TileItemStackHandler(2);
    public ITileFXHandler fxHandler;

    private List<IFusionInjector> injectorCache = null;
    private List<BlockPos> injectorPositions = new ArrayList<>();
    private IFusionRecipe recipeCache = null;
    private TechLevel minTierCache = null;

    public TileFusionCraftingCore() {
        super(DEContent.tile_crafting_core);
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
    public void receivePacketFromClient(MCDataInput data, ServerPlayerEntity client, int id) {
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
            level.playLocalSound(getBlockPos().getX() + 0.5, getBlockPos().getY() + 0.5, getBlockPos().getZ() + 0.5, DESounds.fusionComplete, SoundCategory.BLOCKS, 4.0F, (1.0F + (this.level.random.nextFloat() - this.level.random.nextFloat()) * 0.2F) * 0.7F, false);

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

    public void updateInjectors() {
        minTierCache = null;
        if (isCrafting() || level.isClientSide) {
            return;
        }

        injectorCache = null;
        List<BlockPos> oldPositions = new ArrayList<>(injectorPositions);
        injectorPositions.clear();
        int range = DEConfig.fusionInjectorRange;
        int radius = 1;
        Streams.concat(
                BlockPos.betweenClosedStream(worldPosition.offset(-range, -radius, -radius), worldPosition.offset(range, radius, radius)), //X
                BlockPos.betweenClosedStream(worldPosition.offset(-radius, -range, -radius), worldPosition.offset(radius, range, radius)), //Y
                BlockPos.betweenClosedStream(worldPosition.offset(-radius, -radius, -range), worldPosition.offset(radius, radius, range))  //Z
        )
                .map(level::getBlockEntity)
                .filter(e -> e instanceof TileFusionCraftingInjector)
                .forEach(tile -> {
                    Vec3D dirVec = new Vec3D(tile.getBlockPos()).subtract(worldPosition);
                    double dist = Utils.getDistanceAtoB(new Vec3D(tile.getBlockPos()), new Vec3D(worldPosition));
                    if (dist > DEConfig.fusionInjectorMinDist && Direction.getNearest((int) dirVec.x, (int) dirVec.y, (int) dirVec.z) == ((TileFusionCraftingInjector) tile).getRotation().getOpposite() && ((TileFusionCraftingInjector) tile).setCore(this)) {
                        BlockPos pos = tile.getBlockPos();
                        Direction facing = ((TileFusionCraftingInjector) tile).getRotation();
                        List<BlockPos> checkList = Lists.newArrayList(BlockPos.betweenClosed(pos.relative(facing), pos.relative(facing, FacingUtils.distanceInDirection(pos, worldPosition, facing) - 1)));
                        boolean obstructed = false;
                        for (BlockPos bp : checkList) {
                            if (!level.isEmptyBlock(bp) && (level.getBlockState(bp).canOcclude() || level.getBlockEntity(bp) instanceof TileFusionCraftingInjector)) {
                                obstructed = true;
                                ((TileFusionCraftingInjector) tile).setCore(null);
                                break;
                            }
                        }
                        if (!obstructed) {
                            injectorPositions.add(tile.getBlockPos());
                        }
                    }
                });
        dirtyBlock();
        if (!oldPositions.equals(injectorPositions)) {
            sendPacketToChunk(e -> {
                e.writeShort(injectorPositions.size());
                injectorPositions.forEach(e::writePos);
            }, 0);
        }
    }

    public void inventoryChanged() {
        updateInjectors();
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
                setFusionStatus(-1, new TranslationTextComponent("fusion_status.draconicevolution.no_recipe"));
            }
            setActiveRecipe(recipe);
        }
    }

    private void localInventoryChange() {
        updateBlock();
        inventoryChanged();
    }

    @Override
    public boolean onBlockActivated(BlockState state, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
        if (player instanceof ServerPlayerEntity) {
            updateInjectors(); //TODO just have the injectors poke the core when placed so i dont need this
            NetworkHooks.openGui((ServerPlayerEntity) player, this, worldPosition);
        }
        return true;
    }

    @Nullable
    @Override
    public Container createMenu(int currentWindowIndex, PlayerInventory playerInventory, PlayerEntity player) {
        return new ContainerFusionCraftingCore(currentWindowIndex, player.inventory, this, GuiLayoutFactories.FUSION_CRAFTING_CORE);
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
        setFusionStatus(-1, new TranslationTextComponent("fusion_status.draconicevolution.canceled"));
        level.playSound(null, getBlockPos(), DESounds.fusionComplete, SoundCategory.BLOCKS, 4.0F, (1.0F + (this.level.random.nextFloat() - this.level.random.nextFloat()) * 0.2F) * 0.7F);
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
    public void setFusionStatus(double progress, ITextComponent stateText) {
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
                IRecipe<?> recipe = level.getRecipeManager().byKey(activeRecipe.get()).orElse(null);
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
//        updateInjectors();
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
    public void writeExtraNBT(CompoundNBT compound) {
        super.writeExtraNBT(compound);
        compound.putLongArray("injector_positions", injectorPositions.stream().mapToLong(BlockPos::asLong).toArray());
    }

    @Override
    public void readExtraNBT(CompoundNBT compound) {
        super.readExtraNBT(compound);
        injectorPositions = Arrays.stream(compound.getLongArray("injector_positions")).mapToObj(BlockPos::of).collect(Collectors.toList());
        injectorCache = null;
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public AxisAlignedBB getRenderBoundingBox() {
        return new AxisAlignedBB(worldPosition.offset(-16, -16, -16), worldPosition.offset(17, 17, 17));
    }

    //
//    public final ManagedBool isCrafting = register(new ManagedBool("is_crafting", SAVE_NBT_SYNC_TILE, TRIGGER_UPDATE));
//    /**
//     * 0 = Not crafting<br>
//     * 1 -> 1000 = Charge percentage<br>
//     * 1000 -> 2000 = Crafting progress
//     */
//    public final ManagedShort craftingStage = register(new ManagedShort("crafting_stage", SAVE_NBT_SYNC_TILE));
//    public IFusionRecipe activeRecipe = null;
//    private int craftingSpeedBoost = 0;
//    @OnlyIn(Dist.CLIENT)
//    public LinkedList<EffectTrackerFusionCrafting> effects;


//    //region Logic
//
//    @Override
//    public void tick() {
//        super.tick();
//        //LogHelper.info("- " + isCrafting);
////        if (craftingStage.get() > 0) craftingStage.set((short) 0);
//
//        if (level.isClientSide) {
//            updateEffects();
//            return;
//        }
//
//        //Update Crafting
//        if (isCrafting.get()) {
//            if (DEEventHandler.serverTicks % 10 == 0) {
//                level.updateNeighborsAt(worldPosition, getBlockState().getBlock());
//            }
//
//            for (ICraftingInjector pedestal : pedestals) {
//                if (((TileEntity) pedestal).isRemoved()) {
//                    invalidateCrafting();
//                    return;
//                }
//            }
//
//            if (activeRecipe == null || !activeRecipe.matches(this, level) || !activeRecipe.canCraft(this, level)/* || !activeRecipe.canCraft(this, world, pos).equals("true")*/) {
//                invalidateCrafting();
//                return;
//            }
//
//            long totalCharge = 0;
//
//            for (ICraftingInjector pedestal : pedestals) {
//                if (pedestal.getStackInPedestal().isEmpty()) {
//                    continue;
//                }
//                totalCharge += pedestal.getInjectorCharge();
//            }
//
////            long averageCharge = totalCharge / activeRecipe.getIngredients().size();
//            long craftCost = activeRecipe.getEnergyCost();
//            double percentage = totalCharge / (double) activeRecipe.getEnergyCost();
//
//                                        //>10 is just to account for incomplete injector power allocation.
//            if (craftCost - totalCharge > 10 && craftingStage.get() < 1000) {
//                craftingStage.set((short) (percentage * 1000D));
//                if (craftingStage.get() == 0 && percentage > 0) {
//                    craftingStage.set((short) 1);
//                }
//            } else if (craftingStage.get() < 2000) {
//                craftingStage.add((short) (2 + craftingSpeedBoost));
//            } else if (craftingStage.get() >= 2000) {
////                activeRecipe.craft(this, world, pos);
//                doCraft();
//
//                for (ICraftingInjector pedestal : pedestals) {
//                    pedestal.onCraft();
//                }
//
//                isCrafting.set(false);
//                updateBlock();
//            }
//        } else if (craftingStage.get() > 0) {
//            craftingStage.zero();
//        }
//    }
//
//    private void doCraft() {
//        //This shouldn't be needed but cant hurt.
//        if (!activeRecipe.matches(this, level)) {
//            return;
//        }
//
//        List<ICraftingInjector> pedestals = new ArrayList<>();
//        pedestals.addAll(getInjectors());
//
//        //Use Ingredients
//        for (IFusionRecipe.IFusionIngredient ingred : activeRecipe.fusionIngredients()) {
//            for (ICraftingInjector pedestal : pedestals) {
//                if (!pedestal.getStackInPedestal().isEmpty() && ingred.get().test(pedestal.getStackInPedestal()) && pedestal.getPedestalTier() >= activeRecipe.getRecipeTier().index) {
//                    if (!ingred.consume()) break;
//
//                    ItemStack stack = pedestal.getStackInPedestal();
//                    if (stack.getItem().hasContainerItem(stack)) {
//                        stack = stack.getItem().getContainerItem(stack);
//                    } else {
//                        stack.shrink(1);
//                    }
//
//                    pedestal.setStackInPedestal(stack);
//                    pedestals.remove(pedestal);
//                    break;
//                }
//            }
//        }
//
//        int catCount = 1;
//        if (activeRecipe.getCatalyst() instanceof IngredientStack) {
//            catCount = ((IngredientStack) activeRecipe.getCatalyst()).getCount();
//        }
//
//        ItemStack catalyst = getStackInCore(0);
//        ItemStack result = activeRecipe.assemble(this);
//
//        catalyst.shrink(catCount);
//        setStackInCore(0, catalyst);
//        setStackInCore(1, result.copy());
//    }
//
//    public void attemptStartCrafting() {
//        if (level.isClientSide) {
//            return;
//        }
//        updateInjectors();
//        activeRecipe = level.getRecipeManager().getRecipeFor(DraconicAPI.FUSION_RECIPE_TYPE, this, level).orElse(null);
//
//        if (activeRecipe != null && activeRecipe.canCraft(this, level)) {
//            int minTier = 3;
//            for (ICraftingInjector pedestal : pedestals) {
//                if (!pedestal.getStackInPedestal().isEmpty() && pedestal.getPedestalTier() < minTier) {
//                    minTier = pedestal.getPedestalTier();
//                }
//                craftingSpeedBoost = minTier == 0 ? 0 : minTier == 1 ? 1 : minTier == 2 ? 3 : minTier == 3 ? 5 : 0;
//            }
//            isCrafting.set(true);
//        } else {
//            activeRecipe = null;
//        }
//    }
//
//    private void invalidateCrafting() {
//        if (level.isClientSide) {
//            return;
//        }
//        isCrafting.set(false);
//        activeRecipe = null;
//        craftingStage.zero();
//        pedestals.clear();
//        level.updateNeighborsAt(worldPosition, getBlockState().getBlock());
//    }
//
//    /**
//     * Clears the pedestal list and then re acquires all valid pedestals.
//     */
//    public void updateInjectors() {
//        if (isCrafting.get()) {
//            return;
//        }
//
//        pedestals.clear();
//        int range = 16;
//
//        List<BlockPos> positions = new ArrayList<BlockPos>();
//        //X
//        positions.addAll(Lists.newArrayList(BlockPos.betweenClosedStream(worldPosition.offset(-range, -1, -1), worldPosition.offset(range, 1, 1)).map(BlockPos::new).collect(Collectors.toList())));
//        //Y
//        positions.addAll(Lists.newArrayList(BlockPos.betweenClosedStream(worldPosition.offset(-1, -range, -1), worldPosition.offset(1, range, 1)).map(BlockPos::new).collect(Collectors.toList())));
//        //Z
//        positions.addAll(Lists.newArrayList(BlockPos.betweenClosedStream(worldPosition.offset(-1, -1, -range), worldPosition.offset(1, 1, range)).map(BlockPos::new).collect(Collectors.toList())));
//
//        for (BlockPos checkPos : positions) {
//            TileEntity tile = level.getBlockEntity(checkPos);
//
//            if (tile instanceof ICraftingInjector) {
//                ICraftingInjector pedestal = (ICraftingInjector) tile;
//                Vec3D dirVec = new Vec3D(tile.getBlockPos()).subtract(worldPosition);
//                double dist = Utils.getDistanceAtoB(new Vec3D(tile.getBlockPos()), new Vec3D(worldPosition));
//
//                if (dist >= 2 && Direction.getNearest((int) dirVec.x, (int) dirVec.y, (int) dirVec.z) == pedestal.getDirection().getOpposite() && pedestal.setCraftingInventory(this)) {
//                    BlockPos pPos = tile.getBlockPos();
//                    Direction facing = pedestal.getDirection();
//                    List<BlockPos> checkList = Lists.newArrayList(BlockPos.betweenClosed(pPos.relative(facing), pPos.relative(facing, FacingUtils.distanceInDirection(pPos, worldPosition, facing) - 1)));
//
//                    boolean obstructed = false;
//                    for (BlockPos bp : checkList) {
//                        if (!level.isEmptyBlock(bp) && (level.getBlockState(bp).canOcclude() || level.getBlockEntity(bp) instanceof ICraftingInjector)) {
//                            obstructed = true;
//                            break;
//                        }
//                    }
//
//                    if (!obstructed) {
//                        pedestals.add(pedestal);
//                    } else {
//                        pedestal.setCraftingInventory(null);
//                    }
//                }
//            }
//        }
//    }
//
//    @Override
//    public boolean craftingInProgress() {
//        return isCrafting.get();
//    }
//
//    @Override
//    public void receivePacketFromClient(MCDataInput data, ServerPlayerEntity client, int id) {
//        attemptStartCrafting();
//    }
//
//    @Override
//    public long getIngredientEnergyCost() {
//        if (activeRecipe == null) {
//            return 0;
//        } else {
//            return activeRecipe.fusionIngredients().isEmpty() ? 0 : activeRecipe.getEnergyCost() / activeRecipe.fusionIngredients().size();
//        }
//    }
//
//    @Override
//    public int getCraftingStage() {
//        return craftingStage.get();
//    }
//
//    @Override
//    public BlockPos getCorePos() {
//        return getBlockPos();
//    }
//
//    //endregion
//
//    //    //region Inventory
////
////    @Override
////    public void setInventorySlotContents(int index, ItemStack stack) {
////        super.setInventorySlotContents(index, stack);
////        updateBlock();
////    }
////
////    @Override
////    public ItemStack decrStackSize(int index, int count) {
////        ItemStack ret = super.decrStackSize(index, count);
////        updateBlock();
////        return ret;
////    }
////
//    @Override
//    public ItemStack getStackInCore(int slot) {
//        return itemHandler.getStackInSlot(slot);
//    }
//
//    @Override
//    public void setStackInCore(int slot, ItemStack stack) {
//        itemHandler.setStackInSlot(slot, stack);
//    }
//
//    @Override
//    public List<ICraftingInjector> getInjectors() {
//        return pedestals;
//    }
////
////    @Override
////    public int[] getSlotsForFace(Direction side) {
////        return new int[]{0, 1};
////    }
////
////    @Override
////    public boolean canInsertItem(int index, ItemStack itemStackIn, Direction direction) {
////        return index == 0;
////    }
////
////    @Override
////    public boolean canExtractItem(int index, ItemStack stack, Direction direction) {
////        return index == 1;
////    }
////
////    //endregion
//
//    //region Effects
//
//    @OnlyIn(Dist.CLIENT)
//    public void initializeEffects() {
//        pedestals.clear();
//        int range = 16;
//
//        List<BlockPos> positions = new ArrayList<BlockPos>();
//
//        //X
//        positions.addAll(Lists.newArrayList(BlockPos.betweenClosedStream(worldPosition.offset(-range, -1, -1), worldPosition.offset(range, 1, 1)).map(BlockPos::new).collect(Collectors.toList())));
//        //Y
//        positions.addAll(Lists.newArrayList(BlockPos.betweenClosedStream(worldPosition.offset(-1, -range, -1), worldPosition.offset(1, range, 1)).map(BlockPos::new).collect(Collectors.toList())));
//        //Z
//        positions.addAll(Lists.newArrayList(BlockPos.betweenClosedStream(worldPosition.offset(-1, -1, -range), worldPosition.offset(1, 1, range)).map(BlockPos::new).collect(Collectors.toList())));
//
//        for (BlockPos checkPos : positions) {
//            TileEntity tile = level.getBlockEntity(checkPos);
//
//            if (tile instanceof ICraftingInjector) {
//                ICraftingInjector pedestal = (ICraftingInjector) tile;
//                Vec3D dirVec = new Vec3D(tile.getBlockPos()).subtract(worldPosition);
//                double dist = Utils.getDistanceAtoB(new Vec3D(tile.getBlockPos()), new Vec3D(worldPosition));
//
//                if (dist >= 2 && Direction.getNearest((int) dirVec.x, (int) dirVec.y, (int) dirVec.z) == pedestal.getDirection().getOpposite() && pedestal.setCraftingInventory(this)) {
//                    BlockPos pPos = tile.getBlockPos();
//                    Direction facing = pedestal.getDirection();
//                    List<BlockPos> checkList = Lists.newArrayList(BlockPos.betweenClosed(pPos.relative(facing), pPos.relative(facing, FacingUtils.distanceInDirection(pPos, worldPosition, facing) - 1)));
//
//                    boolean obstructed = false;
//                    for (BlockPos bp : checkList) {
//                        if (!level.isEmptyBlock(bp) && (level.getBlockState(bp).canOcclude() || level.getBlockEntity(bp) instanceof ICraftingInjector)) {
//                            obstructed = true;
//                            break;
//                        }
//                    }
//
//                    if (!obstructed) {
//                        pedestals.add(pedestal);
//                    } else {
//                        pedestal.setCraftingInventory(null);
//                    }
//                }
//            }
//        }
//
////        activeRecipe = RecipeManager.FUSION_REGISTRY.findRecipe(this, world, pos);
//        activeRecipe = level.getRecipeManager().getRecipeFor(DraconicAPI.FUSION_RECIPE_TYPE, this, level).orElse(null);
//
//        if (activeRecipe == null) {
//            effects = null;
//            return;
//        }
//
//        effects = new LinkedList<>();
//
//        for (ICraftingInjector pedestal : pedestals) {
//            if (pedestal.getStackInPedestal().isEmpty()) {
//                continue;
//            }
//
//            pedestal.setCraftingInventory(this);
//            Vec3D spawn = new Vec3D(((TileEntity) pedestal).getBlockPos());
//            spawn.add(0.5 + pedestal.getDirection().getStepX() * 0.45, 0.5 + pedestal.getDirection().getStepY() * 0.45, 0.5 + pedestal.getDirection().getStepZ() * 0.45);
//            effects.add(new EffectTrackerFusionCrafting(level, spawn, new Vec3D(worldPosition), this, activeRecipe.getIngredients().size()));
////            BCEffectHandler.effectRenderer.addEffect(ResourceHelperDE.getResource("textures/blocks/fusion_crafting/fusion_particle.png"), new ParticleFusionCrafting(world, spawn, new Vec3D(pos), this));
//        }
//    }
//
//    private double effectRotation = 0;
//    private boolean allLocked = false;
//    private boolean halfCycle = false;
//
//    @OnlyIn(Dist.CLIENT)
//    public void updateEffects() {
//        if (effects == null) {
//            if (isCrafting.get()) {
//                initializeEffects();
//                effectRotation = 0;
//                allLocked = false;
//            }
//            return;
//        }
//
////        craftingStage.value = 1500;
//
//
//        //region Calculate Distance
//        double distFromCore = 1.2;
//        if (getCraftingStage() > 1600) {
//            distFromCore *= 1.0D - (getCraftingStage() - 1600) / 400D;
//        }
//
//        if (allLocked) {
//            effectRotation -= Math.min(((getCraftingStage() - 1100D) / 900D) * 0.8D, 0.5D);
//            if (effectRotation > 0) {
//                effectRotation = 0;
//            }
//
//        }
//
//        int index = 0;
//        int count = effects.size();
//        boolean flag = true;
//        boolean isMoving = getCraftingStage() > 1000;
//        for (EffectTrackerFusionCrafting effect : effects) {
//            effect.onUpdate(isMoving);
//            if (!effect.positionLocked) {
//                flag = false;
//            }
//
//            if (isMoving) {
//                effect.scale = 0.7F + ((float) (distFromCore / 1.2D) * 0.3F);
//                effect.green = effect.blue = (float) (distFromCore - 0.2);
//                effect.red = 1F - (float) (distFromCore - 0.2);
//            }
//
//            double indexPos = (double) index / (double) count;
//            double offset = indexPos * (Math.PI * 2);
//            double offsetX = Math.sin(effectRotation + offset) * distFromCore;
//            double offsetZ = Math.cos(effectRotation + offset) * distFromCore;
//
//            double mix = effectRotation / 5F;
//            double xAdditive = offsetX * Math.sin(-mix);
//            double zAdditive = offsetZ * Math.cos(-mix);
//
//            double offsetY = (xAdditive + zAdditive) * 0.2 * (distFromCore / 1.2);
//
//            effect.circlePosition.set(worldPosition.getX() + 0.5 + offsetX, worldPosition.getY() + 0.5 + offsetY, worldPosition.getZ() + 0.5 + offsetZ);
//            index++;
//        }
//
//        SoundHandler soundManager = Minecraft.getInstance().getSoundManager();
//        if (!allLocked && flag) {
//            soundManager.play(new FusionRotationSound(this));
//        }
//
//        allLocked = flag;
//
//        if (!isCrafting.get()) {
//            for (int i = 0; i < 100; i++) {
//                //TODO Particles
////                BCEffectHandler.spawnFXDirect(DEParticles.DE_SHEET, new EffectTrackerFusionCrafting.SubParticle(world, new Vec3D(pos).add(0.5, 0.5, 0.5)));
//            }
//
//            level.playLocalSound(worldPosition.getX() + 0.5, worldPosition.getY() + 0.5, worldPosition.getZ() + 0.5, DESounds.fusionComplete, SoundCategory.BLOCKS, 2F, 1F, false);
//            effects = null;
//        }
//    }
//
//    @OnlyIn(Dist.CLIENT)
//    public void renderEffects(float partialTicks) {
//        //     craftingStage.value = 1500;
//        if (effects != null) {
//            RenderSystem.pushMatrix();
//
//            ResourceHelperDE.bindTexture(DETextures.FUSION_PARTICLE);
//            Tessellator tessellator = Tessellator.getInstance();
//
//            //Pre-Render
//            RenderSystem.enableBlend();
//            RenderSystem.disableLighting();
//            RenderSystem.depthMask(true);
//            RenderSystem.alphaFunc(GL11.GL_GREATER, 0F);
//
//            for (EffectTrackerFusionCrafting effect : effects) {
//                effect.renderEffect(tessellator, partialTicks);
//            }
//
//            //Post-Render
//            RenderSystem.disableBlend();
//            RenderSystem.enableLighting();
//            RenderSystem.depthMask(true);
//            RenderSystem.alphaFunc(GL11.GL_GREATER, 0.1F);
//
//            RenderSystem.popMatrix();
//        }
//    }
//
//    @Override
//    public AxisAlignedBB getRenderBoundingBox() {
//        return new AxisAlignedBB(worldPosition.offset(-16, -16, -16), worldPosition.offset(17, 17, 17));
//    }
//
//    //endregion
//
//    public int getComparatorOutput() {
//        updateInjectors();
//        if (!getStackInCore(1).isEmpty()) {
//            return 15;
//        } else if (craftingStage.get() > 0) {
//            return (int) Math.max(1, ((craftingStage.get() / 2000D) * 15D));
//        } else {
//            IFusionRecipe recipe = level.getRecipeManager().getRecipeFor(DraconicAPI.FUSION_RECIPE_TYPE, this, level).orElse(null);
//            if (recipe != null && recipe.canCraft(this, level)) {
//                return 1;
//            }
//
//            return 0;
//        }
//
//    }
}

