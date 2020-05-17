package com.brandon3055.draconicevolution.items.tools;

import codechicken.lib.inventory.InventoryUtils;
import codechicken.lib.raytracer.RayTracer;
import com.brandon3055.brandonscore.inventory.BlockToStackHelper;
import com.brandon3055.brandonscore.inventory.InventoryDynamic;
import com.brandon3055.brandonscore.lib.PairKV;
import com.brandon3055.draconicevolution.DEOldConfig;
import com.brandon3055.draconicevolution.api.itemconfig_dep.*;
import com.brandon3055.draconicevolution.api.itemupgrade_dep.UpgradeHelper;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.fluid.IFluidState;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.play.client.CPlayerDiggingPacket;
import net.minecraft.network.play.server.SChangeBlockPacket;
import net.minecraft.util.Direction;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.brandon3055.draconicevolution.api.itemconfig_dep.IItemConfigField.EnumControlType.SLIDER;
import static com.brandon3055.draconicevolution.items.ToolUpgrade.DIG_AOE;
import static com.brandon3055.draconicevolution.items.ToolUpgrade.DIG_SPEED;

/**
 * Created by brandon3055 on 2/06/2016.
 */
public abstract class MiningToolBase extends ToolBase {

    protected static final Set SHOVEL_OVERRIDES = Sets.newHashSet(Blocks.CLAY, Blocks.DIRT, Blocks.FARMLAND, Blocks.GRASS, Blocks.GRAVEL, Blocks.MYCELIUM, Blocks.SAND, Blocks.SNOW, Blocks.SOUL_SAND, Blocks.GRASS_PATH);
    protected static final Set PICKAXE_OVERRIDES = Sets.newHashSet(Blocks.ACTIVATOR_RAIL, Blocks.COAL_ORE, Blocks.COBBLESTONE, Blocks.DETECTOR_RAIL, Blocks.DIAMOND_BLOCK, Blocks.DIAMOND_ORE, Blocks.GOLD_BLOCK, Blocks.GOLD_ORE, Blocks.ICE, Blocks.IRON_BLOCK, Blocks.IRON_ORE, Blocks.LAPIS_BLOCK, Blocks.LAPIS_ORE, Blocks.MOSSY_COBBLESTONE, Blocks.NETHERRACK, Blocks.PACKED_ICE, Blocks.RAIL, Blocks.REDSTONE_ORE, Blocks.SANDSTONE, Blocks.RED_SANDSTONE, Blocks.STONE, Blocks.STONE_SLAB, Blocks.STONE_BUTTON, Blocks.STONE_PRESSURE_PLATE, Blocks.OBSIDIAN, Material.ROCK, Material.IRON, Material.ANVIL, Material.GLASS);
    protected static final Set AXE_OVERRIDES = Sets.newHashSet(Blocks.BOOKSHELF, Blocks.CHEST, Blocks.PUMPKIN, Blocks.LADDER, Material.PLANTS, Material.LEAVES, Material.WEB, Material.WOOD, Material.CAKE, Material.PISTON);
//    private static final Set<Block> EFFECTIVE_ON = Sets.newHashSet(Blocks.OAK_PLANKS, Blocks.SPRUCE_PLANKS, Blocks.BIRCH_PLANKS, Blocks.JUNGLE_PLANKS, Blocks.ACACIA_PLANKS, Blocks.DARK_OAK_PLANKS, Blocks.BOOKSHELF, Blocks.OAK_WOOD, Blocks.SPRUCE_WOOD, Blocks.BIRCH_WOOD, Blocks.JUNGLE_WOOD, Blocks.ACACIA_WOOD, Blocks.DARK_OAK_WOOD, Blocks.OAK_LOG, Blocks.SPRUCE_LOG, Blocks.BIRCH_LOG, Blocks.JUNGLE_LOG, Blocks.ACACIA_LOG, Blocks.DARK_OAK_LOG, Blocks.CHEST, Blocks.PUMPKIN, Blocks.CARVED_PUMPKIN, Blocks.JACK_O_LANTERN, Blocks.MELON, Blocks.LADDER, Blocks.SCAFFOLDING, Blocks.OAK_BUTTON, Blocks.SPRUCE_BUTTON, Blocks.BIRCH_BUTTON, Blocks.JUNGLE_BUTTON, Blocks.DARK_OAK_BUTTON, Blocks.ACACIA_BUTTON, Blocks.OAK_PRESSURE_PLATE, Blocks.SPRUCE_PRESSURE_PLATE, Blocks.BIRCH_PRESSURE_PLATE, Blocks.JUNGLE_PRESSURE_PLATE, Blocks.DARK_OAK_PRESSURE_PLATE, Blocks.ACACIA_PRESSURE_PLATE);
//    protected static final Map<Block, Block> BLOCK_STRIPPING_MAP = (new ImmutableMap.Builder<Block, Block>()).put(Blocks.OAK_WOOD, Blocks.STRIPPED_OAK_WOOD).put(Blocks.OAK_LOG, Blocks.STRIPPED_OAK_LOG).put(Blocks.DARK_OAK_WOOD, Blocks.STRIPPED_DARK_OAK_WOOD).put(Blocks.DARK_OAK_LOG, Blocks.STRIPPED_DARK_OAK_LOG).put(Blocks.ACACIA_WOOD, Blocks.STRIPPED_ACACIA_WOOD).put(Blocks.ACACIA_LOG, Blocks.STRIPPED_ACACIA_LOG).put(Blocks.BIRCH_WOOD, Blocks.STRIPPED_BIRCH_WOOD).put(Blocks.BIRCH_LOG, Blocks.STRIPPED_BIRCH_LOG).put(Blocks.JUNGLE_WOOD, Blocks.STRIPPED_JUNGLE_WOOD).put(Blocks.JUNGLE_LOG, Blocks.STRIPPED_JUNGLE_LOG).put(Blocks.SPRUCE_WOOD, Blocks.STRIPPED_SPRUCE_WOOD).put(Blocks.SPRUCE_LOG, Blocks.STRIPPED_SPRUCE_LOG).build();

    protected Set<Object> effectiveBlocks = new HashSet<Object>();
    protected float baseMiningSpeed = 1F;
    protected int baseAOE = 0;

    public MiningToolBase(Properties properties, Set effectiveBlocks) {
        super(properties);
        this.effectiveBlocks.addAll(effectiveBlocks);
    }

    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, CompoundNBT nbt) {
        ICapabilityProvider parent = super.initCapabilities(stack, nbt);
        ItemStackHandler itemHandler = new ItemStackHandler(9 * (getToolTier(stack) + 1)) {
            @Override
            public int getSlotLimit(int slot) {
                return 1;
            }

            @Nonnull
            @Override
            public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
                for (ItemStack check : stacks) {
                    if (stack.isItemEqual(check) && ItemStack.areItemStackTagsEqual(stack, check)) {
                        return stack;
                    }
                }
                return super.insertItem(slot, stack, simulate);
            }
        };
        return new ItemCapProvider(parent, itemHandler);
    }

    public abstract double getBaseMinSpeedConfig();

    public abstract int getBaseMinAOEConfig();

    @Override
    public void loadStatConfig() {
        super.loadStatConfig();
        baseMiningSpeed = (float) getBaseMinSpeedConfig();
        baseAOE = getBaseMinAOEConfig();
    }

    //region Upgrades and Config

    @Override
    public List<String> getValidUpgrades(ItemStack stack) {
        List<String> list = super.getValidUpgrades(stack);
        list.add(DIG_SPEED);
        list.add(DIG_AOE);

        return list;
    }

    @Override
    public ItemConfigFieldRegistry getFields(ItemStack stack, ItemConfigFieldRegistry registry) {
        registry.register(stack, new IntegerConfigField("digSpeed", 100, 1, 100, "config.field.digSpeed.description", SLIDER).setExtension("%"));
        registry.register(stack, new BooleanConfigField("aoeSafeMode", false, "config.field.aoeSafeMode.description"));
        registry.register(stack, new BooleanConfigField("showDigAOE", false, "config.field.showDigAOE.description"));

        int maxAOE = getMaxDigAOE(stack);
        registry.register(stack, new AOEConfigField("digAOE", 0, 0, maxAOE, "config.field.digAOE.description"));

        if (getToolTier(stack) > 0) {
            int depth = getMaxDigDepth(stack);
            registry.register(stack, new IntegerConfigField("digDepth", 0, 0, depth, "config.field.digDepth.description", SLIDER));
        }

        if (!(this instanceof WyvernAxe)) {
//            registry.register(stack, new ExternalConfigField("junkFilter", "config.field.junkFilter.description", DraconicEvolution.instance, GuiHandler.GUIID_JUNK_FILTER, "config.field.junkFilter.button"));
            registry.register(stack, new BooleanConfigField("enableJunkFilter", true, "config.field.enableJunkFilter.description"));
            registry.register(stack, new BooleanConfigField("junkNbtSens", true, "config.field.junkNbtSens.description"));
        }

        addEnchantConfig(stack, registry);

        return registry;
    }

    //endregion

    //region Vanilla Item Mining Code

    @Override
    public boolean onBlockStartBreak(ItemStack stack, BlockPos pos, PlayerEntity player) {
        int rad = getDigAOE(stack);
        int depth = getDigDepth(stack);
        int totalBlocks = rad * depth;

        if (getEnergyStored(stack) < (energyPerOperation * totalBlocks)) {

            return super.onBlockStartBreak(stack, pos, player);
        }

        if (rad > 0 || depth > 0) {
            return breakAOEBlocks(stack, pos, rad, depth, player);
        }

        return super.onBlockStartBreak(stack, pos, player);
    }

    //Note: Called for every AOE block destroyed
    @Override
    public boolean onBlockDestroyed(ItemStack stack, World worldIn, BlockState state, BlockPos pos, LivingEntity entityLiving) {
        modifyEnergy(stack, -energyPerOperation);
        return super.onBlockDestroyed(stack, worldIn, state, pos, entityLiving);
    }

    @Override
    public float getDestroySpeed(ItemStack stack, BlockState state) {
        if (getEnergyStored(stack) < energyPerOperation) {
            return 1F;
        }

        if (isToolEffective(stack, state)) {
            return getEfficiency(stack);
        }
        return 1F;
        //return this.effectiveBlocks.contains(state.getBlock()) ? getEfficiency(stack) : 1.0F;
    }

    @Override
    public boolean canHarvestBlock(ItemStack stack, BlockState state) {
        return isToolEffective(stack, state);
    }

    @Override
    public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment) {
        return super.canApplyAtEnchantingTable(stack, enchantment) && (enchantment.type == EnchantmentType.DIGGER || enchantment.type == EnchantmentType.ALL);
    }

    //endregion

    //region AOE Mining Code

    //region New Code

    public boolean breakAOEBlocks(ItemStack stack, BlockPos pos, int breakRadius, int breakDepth, PlayerEntity player) {
        BlockState blockState = player.world.getBlockState(pos);

        if (!isToolEffective(stack, blockState)) {
            return false;
        }

        InventoryDynamic inventoryDynamic = new InventoryDynamic();

        float refStrength = blockStrength(blockState, player, player.world, pos);

        PairKV<BlockPos, BlockPos> aoe = getMiningArea(pos, player, breakRadius, breakDepth);
        List<BlockPos> aoeBlocks = Lists.newArrayList(BlockPos.getAllInBoxMutable(aoe.getKey(), aoe.getValue()));

        if (ToolConfigHelper.getBooleanField("aoeSafeMode", stack)) {
            for (BlockPos block : aoeBlocks) {
                if (!player.world.isAirBlock(block) && player.world.getTileEntity(block) != null) {
                    if (player.world.isRemote) {
                        player.sendMessage(new TranslationTextComponent("msg.de.baseSafeAOW.txt"));
                    } else {
                        ((ServerPlayerEntity) player).connection.sendPacket(new SChangeBlockPacket(((ServerPlayerEntity) player).world, block));
                    }
                    return true;
                }
            }
        }

        player.world.playEvent(2001, pos, Block.getStateId(blockState));

        for (BlockPos block : aoeBlocks) {
            breakAOEBlock(stack, player.world, block, player, refStrength, inventoryDynamic, random.nextInt(Math.max(5, (breakRadius * breakDepth) / 5)) == 0);
        }


        @SuppressWarnings("unchecked") List<ItemEntity> items = player.world.getEntitiesWithinAABB(ItemEntity.class, new AxisAlignedBB(aoe.getKey(), aoe.getValue().add(1, 1, 1)));
        for (ItemEntity item : items) {
            if (!player.world.isRemote && item.isAlive()) {
                InventoryUtils.insertItem(inventoryDynamic, item.getItem(), false);
                item.remove();
            }
        }

        Set<ItemStack> junkFilter = getJunkFilter(stack);
        if (junkFilter != null) {
            boolean nbtSens = ToolConfigHelper.getBooleanField("junkNbtSens", stack);
            inventoryDynamic.removeIf(check -> {
                for (ItemStack junk : junkFilter) {
                    if (junk.isItemEqual(check) && (!nbtSens || ItemStack.areItemStackTagsEqual(junk, check))) {
                        return true;
                    }
                }
                return false;
            });
        }

        if (!player.world.isRemote) {
            if (DEOldConfig.disableLootCores) {
                for (int i = 0; i < inventoryDynamic.getSizeInventory(); i++) {
                    ItemStack sis = inventoryDynamic.getStackInSlot(i);
                    if (sis != null) {
                        ItemEntity item = new ItemEntity(player.world, player.posX, player.posY, player.posZ, sis);
                        item.setPickupDelay(0);
                        player.world.addEntity(item);
                    }
                }
                player.giveExperiencePoints(inventoryDynamic.xp);
                inventoryDynamic.clear();
            } else {
//                EntityLootCore lootCore = new EntityLootCore(player.world, inventoryDynamic); TODO Entity Stuff
//                lootCore.setPosition(player.posX, player.posY, player.posZ);
//                player.world.addEntity(lootCore);
            }
        }

        return true;
    }

    protected void breakAOEBlock(ItemStack stack, World world, BlockPos pos, PlayerEntity player, float refStrength, InventoryDynamic inventory, boolean breakFX) {
        if (world.isAirBlock(pos)) {
            return;
        }

        BlockState state = world.getBlockState(pos);
        IFluidState fluidState = world.getFluidState(pos);
        Block block = state.getBlock();

        if (!isToolEffective(stack, state)) {
            return;
        }

        float strength = blockStrength(state, player, world, pos);

        if (!ForgeHooks.canHarvestBlock(state, player, world, pos) || refStrength / strength > 10f) {
            return;
        }

        if (player.abilities.isCreativeMode) {
            if (world.isRemote && random.nextInt(10) == 0) {
                world.playEvent(2001, pos, Block.getStateId(state));
            }

            block.onBlockHarvested(world, pos, state, player);
            if (block.removedByPlayer(state, world, pos, player, false, fluidState)) {
                block.onPlayerDestroy(world, pos, state);
            }

            if (!world.isRemote) {
                ((ServerPlayerEntity) player).connection.sendPacket(new SChangeBlockPacket(world, pos));
            }

            return;
        }

        if (!world.isRemote) {
            int xp = ForgeHooks.onBlockBreakEvent(world, ((ServerPlayerEntity) player).interactionManager.getGameType(), (ServerPlayerEntity) player, pos);
            if (xp == -1) {
                ServerPlayerEntity mpPlayer = (ServerPlayerEntity) player;
                mpPlayer.connection.sendPacket(new SChangeBlockPacket(world, pos));
                return;
            }

            stack.onBlockDestroyed(world, state, pos, player);
            BlockToStackHelper.breakAndCollectWithPlayer(world, pos, inventory, player, xp);
        } else {
            if (breakFX) {
                world.playEvent(2001, pos, Block.getStateId(state));
            }
            if (block.removedByPlayer(state, world, pos, player, true, fluidState)) {
                block.onPlayerDestroy(world, pos, state);
            }

            stack.onBlockDestroyed(world, state, pos, player);


            if (Minecraft.getInstance().objectMouseOver instanceof BlockRayTraceResult) {
                Minecraft.getInstance().getConnection().sendPacket(new CPlayerDiggingPacket(CPlayerDiggingPacket.Action.STOP_DESTROY_BLOCK, pos, ((BlockRayTraceResult) Minecraft.getInstance().objectMouseOver).getFace()));
            }
        }
    }

    //endregion

    public PairKV<BlockPos, BlockPos> getMiningArea(BlockPos pos, PlayerEntity player, int breakRadius, int breakDepth) {
        BlockRayTraceResult traceResult = RayTracer.retrace(player);

        if (traceResult != null || traceResult.getType() != RayTraceResult.Type.BLOCK) {
            return new PairKV<>(pos, pos);
        }

        int sideHit = traceResult.getFace().getIndex();

        int xMax = breakRadius;
        int xMin = breakRadius;
        int yMax = breakRadius;
        int yMin = breakRadius;
        int zMax = breakRadius;
        int zMin = breakRadius;
        int yOffset = 0;

        switch (sideHit) {
            case 0:
                yMax = breakDepth;
                yMin = 0;
                zMax = breakRadius;
                break;
            case 1:
                yMin = breakDepth;
                yMax = 0;
                zMax = breakRadius;
                break;
            case 2:
                xMax = breakRadius;
                zMin = 0;
                zMax = breakDepth;
                yOffset = breakRadius - 1;
                break;
            case 3:
                xMax = breakRadius;
                zMax = 0;
                zMin = breakDepth;
                yOffset = breakRadius - 1;
                break;
            case 4:
                xMax = breakDepth;
                xMin = 0;
                zMax = breakRadius;
                yOffset = breakRadius - 1;
                break;
            case 5:
                xMin = breakDepth;
                xMax = 0;
                zMax = breakRadius;
                yOffset = breakRadius - 1;
                break;
        }

        if (breakRadius == 0) {
            yOffset = 0;
        }

        return new PairKV<>(pos.add(-xMin, yOffset - yMin, -zMin), pos.add(xMax, yOffset + yMax, zMax));
    }

    //endregion

    //region Setters, Getters & Helpers

//    public void setHarvestLevel(String toolClass, int level) {
//        if (toolClass.equals("pickaxe") && level >= 0) {
//            effectiveBlocks.addAll(PICKAXE_OVERRIDES);
//        }
//        if (toolClass.equals("shovel") && level >= 0) {
//            effectiveBlocks.addAll(SHOVEL_OVERRIDES);
//        }
//        if (toolClass.equals("axe") && level >= 0) {
//            effectiveBlocks.addAll(AXE_OVERRIDES);
//        }
//        super.setHarvestLevel(toolClass, level);
//    }

    public float getEfficiency(ItemStack stack) {
        float speedSetting = (float) ToolConfigHelper.getIntegerField("digSpeed", stack) / 100F;
        return baseMiningSpeed * (float) (UpgradeHelper.getUpgradeLevel(stack, DIG_SPEED) + 1) * speedSetting;
    }

    public int getDigAOE(ItemStack stack) {
        return ToolConfigHelper.getIntegerField("digAOE", stack);
    }

    public int getMaxDigAOE(ItemStack stack) {
        return baseAOE + UpgradeHelper.getUpgradeLevel(stack, DIG_AOE);
    }

    public void setMiningAOE(ItemStack stack, int value) {
        ToolConfigHelper.setIntegerField("digAOE", stack, value);
    }

    public int getDigDepth(ItemStack stack) {
        return ToolConfigHelper.getIntegerField("digDepth", stack);
    }

    public int getMaxDigDepth(ItemStack stack) {
        return (getMaxDigAOE(stack) * 2) + 1;
    }

    public void setMiningDepth(ItemStack stack, int value) {
        ToolConfigHelper.setIntegerField("digDepth", stack, value);
    }

    public boolean isToolEffective(ItemStack stack, BlockState state) {
        if (getEnergyStored(stack) < energyPerOperation) {
            return false;
        }

        for (ToolType type : stack.getItem().getToolTypes(stack)) {
            if (state.getBlock().isToolEffective(state, type) || effectiveBlocks.contains(state.getBlock()) || effectiveBlocks.contains(state.getMaterial())) {
                return true;
            }
        }

        return false;
    }

    public static float blockStrength(BlockState state, PlayerEntity player, World world, BlockPos pos) {
        float hardness = state.getBlockHardness(world, pos);
        if (hardness < 0.0F) {
            return 0.0F;
        }

        if (!ForgeHooks.canHarvestBlock(state, player, world, pos)) {
            return player.getDigSpeed(state, pos) / hardness / 100F;
        } else {
            return player.getDigSpeed(state, pos) / hardness / 30F;
        }
    }

    public Set<ItemStack> getJunkFilter(ItemStack stack) {
//        if (ToolConfigHelper.getBooleanField("enableJunkFilter", stack) && stack.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null)) {
//            IItemHandler itemHandler = stack.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
//            Set<ItemStack> junkFilter = new HashSet<>();
//
//            if (itemHandler != null) {
//                for (int i = 0; i < itemHandler.getSlots(); i++) {
//                    ItemStack junk = itemHandler.getStackInSlot(i);
//                    if (!junk.isEmpty()) {
//                        junkFilter.add(junk);
//                    }
//                }
//            }
//
//            return junkFilter.isEmpty() ? null : junkFilter;
//        }
        return null;
    }

    //endregion


    private static class ItemCapProvider implements ICapabilitySerializable<CompoundNBT> {
        private ICapabilityProvider parentProvider;
        private ItemStackHandler itemHandler;

        public ItemCapProvider(ICapabilityProvider parentProvider, ItemStackHandler iItemHandler) {
            this.parentProvider = parentProvider;
            this.itemHandler = iItemHandler;
        }

        @Override
        public CompoundNBT serializeNBT() {
            return itemHandler.serializeNBT();
        }

        @Override
        public void deserializeNBT(CompoundNBT nbt) {
            itemHandler.deserializeNBT(nbt);
        }

//        @Override
//        public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable Direction facing) {
//            if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
//                return true;
//            }
//            return (parentProvider != null && parentProvider.hasCapability(capability, facing));
//        }
//
//        @Nullable
//        @Override
//        public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable Direction facing) {
//            if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
//                return CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.cast(itemHandler);
//            }
//            return parentProvider == null ? null : parentProvider.getCapability(capability, facing);
//        }


        @Nonnull
        @Override
        public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
            return LazyOptional.empty();
        }
    }

}
