package com.brandon3055.draconicevolution.blocks.tileentity;

import codechicken.lib.data.MCDataInput;
import com.brandon3055.brandonscore.blocks.TileBCore;
import com.brandon3055.brandonscore.inventory.TileItemStackHandler;
import com.brandon3055.brandonscore.lib.IActivatableTile;
import com.brandon3055.brandonscore.lib.Vec3D;
import com.brandon3055.brandonscore.lib.datamanager.ManagedBool;
import com.brandon3055.brandonscore.lib.datamanager.ManagedShort;
import com.brandon3055.brandonscore.utils.FacingUtils;
import com.brandon3055.brandonscore.utils.Utils;
import com.brandon3055.draconicevolution.api.DraconicAPI;
import com.brandon3055.draconicevolution.api.crafting.IFusionInventory;
import com.brandon3055.draconicevolution.api.crafting.IFusionRecipe;
import com.brandon3055.draconicevolution.api.crafting.IngredientStack;
import com.brandon3055.draconicevolution.init.DEContent;
import com.brandon3055.draconicevolution.api.fusioncrafting.ICraftingInjector;
import com.brandon3055.draconicevolution.client.render.effect.EffectTrackerFusionCrafting;
import com.brandon3055.draconicevolution.client.sound.FusionRotationSound;
import com.brandon3055.draconicevolution.handlers.DEEventHandler;
import com.brandon3055.draconicevolution.inventory.ContainerFusionCraftingCore;
import com.brandon3055.draconicevolution.utils.ResourceHelperDE;
import com.brandon3055.draconicevolution.handlers.DESounds;
import com.brandon3055.draconicevolution.client.DETextures;
import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.NetworkHooks;
import net.minecraftforge.items.CapabilityItemHandler;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import static com.brandon3055.brandonscore.lib.datamanager.DataFlags.SAVE_NBT_SYNC_TILE;
import static com.brandon3055.brandonscore.lib.datamanager.DataFlags.TRIGGER_UPDATE;

/**
 * Created by brandon3055 on 11/06/2016.
 */
public class TileCraftingCore extends TileBCore implements IFusionInventory, ITickableTileEntity, INamedContainerProvider, IActivatableTile {

    public List<ICraftingInjector> pedestals = new ArrayList<ICraftingInjector>();
    public final ManagedBool isCrafting = register(new ManagedBool("is_crafting", SAVE_NBT_SYNC_TILE, TRIGGER_UPDATE));
    /**
     * 0 = Not crafting<br>
     * 1 -> 1000 = Charge percentage<br>
     * 1000 -> 2000 = Crafting progress
     */
    public final ManagedShort craftingStage = register(new ManagedShort("crafting_stage", SAVE_NBT_SYNC_TILE));
    public IFusionRecipe activeRecipe = null;
    private int craftingSpeedBoost = 0;

    public TileItemStackHandler itemHandler = new TileItemStackHandler(2);

    @OnlyIn(Dist.CLIENT)
    public LinkedList<EffectTrackerFusionCrafting> effects;

    public TileCraftingCore() {
        super(DEContent.tile_crafting_core);
        capManager.setManaged("inventory", CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, itemHandler).saveBoth();
    }

    @Nullable
    @Override
    public Container createMenu(int currentWindowIndex, PlayerInventory playerInventory, PlayerEntity player) {
        return new ContainerFusionCraftingCore(DEContent.container_fusion_crafting_core, currentWindowIndex, player.inventory, this/*, GuiLayoutFactories.*/);
    }

    @Override
    public boolean onBlockActivated(BlockState state, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
        updateInjectors();
        if (player instanceof ServerPlayerEntity) {
            NetworkHooks.openGui((ServerPlayerEntity) player, this, pos);
        }
        return true;
    }

    //region Logic

    @Override
    public void tick() {
        super.tick();
        //LogHelper.info("- " + isCrafting);
//        if (craftingStage.get() > 0) craftingStage.set((short) 0);

        if (world.isRemote) {
            updateEffects();
            return;
        }

        //Update Crafting
        if (isCrafting.get()) {
            if (DEEventHandler.serverTicks % 10 == 0) {
                world.notifyNeighborsOfStateChange(pos, getBlockState().getBlock());
            }

            for (ICraftingInjector pedestal : pedestals) {
                if (((TileEntity) pedestal).isRemoved()) {
                    invalidateCrafting();
                    return;
                }
            }

            if (activeRecipe == null || !activeRecipe.matches(this, world) || !activeRecipe.canCraft(this, world)/* || !activeRecipe.canCraft(this, world, pos).equals("true")*/) {
                invalidateCrafting();
                return;
            }

            long totalCharge = 0;

            for (ICraftingInjector pedestal : pedestals) {
                if (pedestal.getStackInPedestal().isEmpty()) {
                    continue;
                }
                totalCharge += pedestal.getInjectorCharge();
            }

//            long averageCharge = totalCharge / activeRecipe.getIngredients().size();
            double percentage = totalCharge / (double) activeRecipe.getEnergyCost();

            if (percentage <= 1D && craftingStage.get() < 1000) {
                craftingStage.set((short) (percentage * 1000D));
                if (craftingStage.get() == 0 && percentage > 0) {
                    craftingStage.set((short) 1);
                }
            } else if (craftingStage.get() < 2000) {
                craftingStage.add((short) (2 + craftingSpeedBoost));
            } else if (craftingStage.get() >= 2000) {
//                activeRecipe.craft(this, world, pos);
                doCraft();

                for (ICraftingInjector pedestal : pedestals) {
                    pedestal.onCraft();
                }

                isCrafting.set(false);
                updateBlock();
            }
        } else if (craftingStage.get() > 0) {
            craftingStage.zero();
        }
    }

    private void doCraft() {
        //This shouldn't be needed but cant hurt.
        if (!activeRecipe.matches(this, world)) {
            return;
        }

        List<ICraftingInjector> pedestals = new ArrayList<>();
        pedestals.addAll(getInjectors());

        //Use Ingredients
        for (IFusionRecipe.IFusionIngredient ingred : activeRecipe.fusionIngredients()) {
            for (ICraftingInjector pedestal : pedestals) {
                if (!pedestal.getStackInPedestal().isEmpty() && ingred.get().test(pedestal.getStackInPedestal()) && pedestal.getPedestalTier() >= activeRecipe.getRecipeTier().index) {
                    if (!ingred.consume()) break;

                    ItemStack stack = pedestal.getStackInPedestal();
                    if (stack.getItem().hasContainerItem(stack)) {
                        stack = stack.getItem().getContainerItem(stack);
                    } else {
                        stack.shrink(1);
                    }

                    pedestal.setStackInPedestal(stack);
                    pedestals.remove(pedestal);
                    break;
                }
            }
        }

        int catCount = 1;
        if (activeRecipe.getCatalyst() instanceof IngredientStack) {
            catCount = ((IngredientStack) activeRecipe.getCatalyst()).getCount();
        }

        ItemStack catalyst = getStackInCore(0);
        ItemStack result = activeRecipe.getCraftingResult(this);

        catalyst.shrink(catCount);
        setStackInCore(0, catalyst);
        setStackInCore(1, result.copy());
    }

    public void attemptStartCrafting() {
        if (world.isRemote) {
            return;
        }
        updateInjectors();
        activeRecipe = world.getRecipeManager().getRecipe(DraconicAPI.FUSION_RECIPE_TYPE, this, world).orElse(null);

        if (activeRecipe != null && activeRecipe.canCraft(this, world)) {
            int minTier = 3;
            for (ICraftingInjector pedestal : pedestals) {
                if (!pedestal.getStackInPedestal().isEmpty() && pedestal.getPedestalTier() < minTier) {
                    minTier = pedestal.getPedestalTier();
                }
                craftingSpeedBoost = minTier == 0 ? 0 : minTier == 1 ? 1 : minTier == 2 ? 3 : minTier == 3 ? 5 : 0;
            }
            isCrafting.set(true);
        } else {
            activeRecipe = null;
        }
    }

    private void invalidateCrafting() {
        if (world.isRemote) {
            return;
        }
        isCrafting.set(false);
        activeRecipe = null;
        craftingStage.zero();
        pedestals.clear();
        world.notifyNeighborsOfStateChange(pos, getBlockState().getBlock());
    }

    /**
     * Clears the pedestal list and then re acquires all valid pedestals.
     */
    public void updateInjectors() {
        if (isCrafting.get()) {
            return;
        }

        pedestals.clear();
        int range = 16;

        List<BlockPos> positions = new ArrayList<BlockPos>();
        //X
        positions.addAll(Lists.newArrayList(BlockPos.getAllInBox(pos.add(-range, -1, -1), pos.add(range, 1, 1)).map(BlockPos::new).collect(Collectors.toList())));
        //Y
        positions.addAll(Lists.newArrayList(BlockPos.getAllInBox(pos.add(-1, -range, -1), pos.add(1, range, 1)).map(BlockPos::new).collect(Collectors.toList())));
        //Z
        positions.addAll(Lists.newArrayList(BlockPos.getAllInBox(pos.add(-1, -1, -range), pos.add(1, 1, range)).map(BlockPos::new).collect(Collectors.toList())));

        for (BlockPos checkPos : positions) {
            TileEntity tile = world.getTileEntity(checkPos);

            if (tile instanceof ICraftingInjector) {
                ICraftingInjector pedestal = (ICraftingInjector) tile;
                Vec3D dirVec = new Vec3D(tile.getPos()).subtract(pos);
                double dist = Utils.getDistanceAtoB(new Vec3D(tile.getPos()), new Vec3D(pos));

                if (dist >= 2 && Direction.getFacingFromVector((int) dirVec.x, (int) dirVec.y, (int) dirVec.z) == pedestal.getDirection().getOpposite() && pedestal.setCraftingInventory(this)) {
                    BlockPos pPos = tile.getPos();
                    Direction facing = pedestal.getDirection();
                    List<BlockPos> checkList = Lists.newArrayList(BlockPos.getAllInBoxMutable(pPos.offset(facing), pPos.offset(facing, FacingUtils.distanceInDirection(pPos, pos, facing) - 1)));

                    boolean obstructed = false;
                    for (BlockPos bp : checkList) {
                        if (!world.isAirBlock(bp) && (world.getBlockState(bp).isSolid() || world.getTileEntity(bp) instanceof ICraftingInjector)) {
                            obstructed = true;
                            break;
                        }
                    }

                    if (!obstructed) {
                        pedestals.add(pedestal);
                    } else {
                        pedestal.setCraftingInventory(null);
                    }
                }
            }
        }
    }

    @Override
    public boolean craftingInProgress() {
        return isCrafting.get();
    }

    @Override
    public void receivePacketFromClient(MCDataInput data, ServerPlayerEntity client, int id) {
        attemptStartCrafting();
    }

    @Override
    public long getIngredientEnergyCost() {
        if (activeRecipe == null) {
            return 0;
        } else {
            return activeRecipe.fusionIngredients().isEmpty() ? 0 : activeRecipe.getEnergyCost() / activeRecipe.fusionIngredients().size();
        }
    }

    @Override
    public int getCraftingStage() {
        return craftingStage.get();
    }

    //endregion

    //    //region Inventory
//
//    @Override
//    public void setInventorySlotContents(int index, ItemStack stack) {
//        super.setInventorySlotContents(index, stack);
//        updateBlock();
//    }
//
//    @Override
//    public ItemStack decrStackSize(int index, int count) {
//        ItemStack ret = super.decrStackSize(index, count);
//        updateBlock();
//        return ret;
//    }
//
    @Override
    public ItemStack getStackInCore(int slot) {
        return itemHandler.getStackInSlot(slot);
    }

    @Override
    public void setStackInCore(int slot, ItemStack stack) {
        itemHandler.setStackInSlot(slot, stack);
    }

    @Override
    public List<ICraftingInjector> getInjectors() {
        return pedestals;
    }
//
//    @Override
//    public int[] getSlotsForFace(Direction side) {
//        return new int[]{0, 1};
//    }
//
//    @Override
//    public boolean canInsertItem(int index, ItemStack itemStackIn, Direction direction) {
//        return index == 0;
//    }
//
//    @Override
//    public boolean canExtractItem(int index, ItemStack stack, Direction direction) {
//        return index == 1;
//    }
//
//    //endregion

    //region Effects

    @OnlyIn(Dist.CLIENT)
    public void initializeEffects() {
        pedestals.clear();
        int range = 16;

        List<BlockPos> positions = new ArrayList<BlockPos>();

        //X
        positions.addAll(Lists.newArrayList(BlockPos.getAllInBox(pos.add(-range, -1, -1), pos.add(range, 1, 1)).map(BlockPos::new).collect(Collectors.toList())));
        //Y
        positions.addAll(Lists.newArrayList(BlockPos.getAllInBox(pos.add(-1, -range, -1), pos.add(1, range, 1)).map(BlockPos::new).collect(Collectors.toList())));
        //Z
        positions.addAll(Lists.newArrayList(BlockPos.getAllInBox(pos.add(-1, -1, -range), pos.add(1, 1, range)).map(BlockPos::new).collect(Collectors.toList())));

        for (BlockPos checkPos : positions) {
            TileEntity tile = world.getTileEntity(checkPos);

            if (tile instanceof ICraftingInjector) {
                ICraftingInjector pedestal = (ICraftingInjector) tile;
                Vec3D dirVec = new Vec3D(tile.getPos()).subtract(pos);
                double dist = Utils.getDistanceAtoB(new Vec3D(tile.getPos()), new Vec3D(pos));

                if (dist >= 2 && Direction.getFacingFromVector((int) dirVec.x, (int) dirVec.y, (int) dirVec.z) == pedestal.getDirection().getOpposite() && pedestal.setCraftingInventory(this)) {
                    BlockPos pPos = tile.getPos();
                    Direction facing = pedestal.getDirection();
                    List<BlockPos> checkList = Lists.newArrayList(BlockPos.getAllInBoxMutable(pPos.offset(facing), pPos.offset(facing, FacingUtils.distanceInDirection(pPos, pos, facing) - 1)));

                    boolean obstructed = false;
                    for (BlockPos bp : checkList) {
                        if (!world.isAirBlock(bp) && (world.getBlockState(bp).isSolid() || world.getTileEntity(bp) instanceof ICraftingInjector)) {
                            obstructed = true;
                            break;
                        }
                    }

                    if (!obstructed) {
                        pedestals.add(pedestal);
                    } else {
                        pedestal.setCraftingInventory(null);
                    }
                }
            }
        }

//        activeRecipe = RecipeManager.FUSION_REGISTRY.findRecipe(this, world, pos);
        activeRecipe = world.getRecipeManager().getRecipe(DraconicAPI.FUSION_RECIPE_TYPE, this, world).orElse(null);

        if (activeRecipe == null) {
            effects = null;
            return;
        }

        effects = new LinkedList<>();

        for (ICraftingInjector pedestal : pedestals) {
            if (pedestal.getStackInPedestal().isEmpty()) {
                continue;
            }

            pedestal.setCraftingInventory(this);
            Vec3D spawn = new Vec3D(((TileEntity) pedestal).getPos());
            spawn.add(0.5 + pedestal.getDirection().getXOffset() * 0.45, 0.5 + pedestal.getDirection().getYOffset() * 0.45, 0.5 + pedestal.getDirection().getZOffset() * 0.45);
            effects.add(new EffectTrackerFusionCrafting(world, spawn, new Vec3D(pos), this, activeRecipe.getIngredients().size()));
//            BCEffectHandler.effectRenderer.addEffect(ResourceHelperDE.getResource("textures/blocks/fusion_crafting/fusion_particle.png"), new ParticleFusionCrafting(world, spawn, new Vec3D(pos), this));
        }
    }

    private double effectRotation = 0;
    private boolean allLocked = false;
    private boolean halfCycle = false;

    @OnlyIn(Dist.CLIENT)
    public void updateEffects() {
        if (effects == null) {
            if (isCrafting.get()) {
                initializeEffects();
                effectRotation = 0;
                allLocked = false;
            }
            return;
        }

//        craftingStage.value = 1500;


        //region Calculate Distance
        double distFromCore = 1.2;
        if (getCraftingStage() > 1600) {
            distFromCore *= 1.0D - (getCraftingStage() - 1600) / 400D;
        }

        if (allLocked) {
            effectRotation -= Math.min(((getCraftingStage() - 1100D) / 900D) * 0.8D, 0.5D);
            if (effectRotation > 0) {
                effectRotation = 0;
            }

        }

        int index = 0;
        int count = effects.size();
        boolean flag = true;
        boolean isMoving = getCraftingStage() > 1000;
        for (EffectTrackerFusionCrafting effect : effects) {
            effect.onUpdate(isMoving);
            if (!effect.positionLocked) {
                flag = false;
            }

            if (isMoving) {
                effect.scale = 0.7F + ((float) (distFromCore / 1.2D) * 0.3F);
                effect.green = effect.blue = (float) (distFromCore - 0.2);
                effect.red = 1F - (float) (distFromCore - 0.2);
            }

            double indexPos = (double) index / (double) count;
            double offset = indexPos * (Math.PI * 2);
            double offsetX = Math.sin(effectRotation + offset) * distFromCore;
            double offsetZ = Math.cos(effectRotation + offset) * distFromCore;

            double mix = effectRotation / 5F;
            double xAdditive = offsetX * Math.sin(-mix);
            double zAdditive = offsetZ * Math.cos(-mix);

            double offsetY = (xAdditive + zAdditive) * 0.2 * (distFromCore / 1.2);

            effect.circlePosition.set(pos.getX() + 0.5 + offsetX, pos.getY() + 0.5 + offsetY, pos.getZ() + 0.5 + offsetZ);
            index++;
        }

        SoundHandler soundManager = Minecraft.getInstance().getSoundHandler();
        if (!allLocked && flag) {
            soundManager.play(new FusionRotationSound(this));
        }

        allLocked = flag;

        if (!isCrafting.get()) {
            for (int i = 0; i < 100; i++) {
                //TODO Particles
//                BCEffectHandler.spawnFXDirect(DEParticles.DE_SHEET, new EffectTrackerFusionCrafting.SubParticle(world, new Vec3D(pos).add(0.5, 0.5, 0.5)));
            }

            world.playSound(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, DESounds.fusionComplete, SoundCategory.BLOCKS, 2F, 1F, false);
            effects = null;
        }
    }

    @OnlyIn(Dist.CLIENT)
    public void renderEffects(float partialTicks) {
        //     craftingStage.value = 1500;
        if (effects != null) {
            RenderSystem.pushMatrix();

            ResourceHelperDE.bindTexture(DETextures.FUSION_PARTICLE);
            Tessellator tessellator = Tessellator.getInstance();

            //Pre-Render
            RenderSystem.enableBlend();
            RenderSystem.disableLighting();
            RenderSystem.depthMask(true);
            RenderSystem.alphaFunc(GL11.GL_GREATER, 0F);

            for (EffectTrackerFusionCrafting effect : effects) {
                effect.renderEffect(tessellator, partialTicks);
            }

            //Post-Render
            RenderSystem.disableBlend();
            RenderSystem.enableLighting();
            RenderSystem.depthMask(true);
            RenderSystem.alphaFunc(GL11.GL_GREATER, 0.1F);

            RenderSystem.popMatrix();
        }
    }

    @Override
    public AxisAlignedBB getRenderBoundingBox() {
        return new AxisAlignedBB(pos.add(-16, -16, -16), pos.add(17, 17, 17));
    }
//
//    @Override
//    public boolean shouldRenderInPass(int pass) {
//        return true;
//    }

    //endregion

    public int getComparatorOutput() {
        updateInjectors();
        if (!getStackInCore(1).isEmpty()) {
            return 15;
        } else if (craftingStage.get() > 0) {
            return (int) Math.max(1, ((craftingStage.get() / 2000D) * 15D));
        } else {
            IFusionRecipe recipe = world.getRecipeManager().getRecipe(DraconicAPI.FUSION_RECIPE_TYPE, this, world).orElse(null);
            if (recipe != null && recipe.canCraft(this, world)) {
                return 1;
            }

            return 0;
        }

    }

//    @Override
//    protected <T> T getItemHandler(Capability<T> capability, Direction facing) {
//        return CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.cast(itemHandlers[facing.getIndex()]);
//    }
}

