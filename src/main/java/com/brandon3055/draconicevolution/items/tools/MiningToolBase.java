package com.brandon3055.draconicevolution.items.tools;

import codechicken.lib.inventory.InventoryUtils;
import codechicken.lib.raytracer.RayTracer;
import com.brandon3055.brandonscore.inventory.BlockToStackHelper;
import com.brandon3055.brandonscore.inventory.InventoryDynamic;
import com.brandon3055.brandonscore.lib.PairKV;
import com.brandon3055.draconicevolution.DEConfig;
import com.brandon3055.draconicevolution.api.itemconfig.*;
import com.brandon3055.draconicevolution.api.itemupgrade.UpgradeHelper;
import com.brandon3055.draconicevolution.entity.EntityLootCore;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.network.play.server.SPacketBlockChange;
import net.minecraft.network.play.server.SPacketEntityTeleport;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;

import java.util.*;

import static com.brandon3055.draconicevolution.api.itemconfig.IItemConfigField.EnumControlType.SLIDER;
import static com.brandon3055.draconicevolution.items.ToolUpgrade.DIG_AOE;
import static com.brandon3055.draconicevolution.items.ToolUpgrade.DIG_SPEED;

/**
 * Created by brandon3055 on 2/06/2016.
 */
public abstract class MiningToolBase extends ToolBase {

    protected static final Set SHOVEL_OVERRIDES = Sets.newHashSet(Blocks.CLAY, Blocks.DIRT, Blocks.FARMLAND, Blocks.GRASS, Blocks.GRAVEL, Blocks.MYCELIUM, Blocks.SAND, Blocks.SNOW, Blocks.SNOW_LAYER, Blocks.SOUL_SAND, Blocks.GRASS_PATH);
    protected static final Set PICKAXE_OVERRIDES = Sets.newHashSet(Blocks.ACTIVATOR_RAIL, Blocks.COAL_ORE, Blocks.COBBLESTONE, Blocks.DETECTOR_RAIL, Blocks.DIAMOND_BLOCK, Blocks.DIAMOND_ORE, Blocks.DOUBLE_STONE_SLAB, Blocks.GOLDEN_RAIL, Blocks.GOLD_BLOCK, Blocks.GOLD_ORE, Blocks.ICE, Blocks.IRON_BLOCK, Blocks.IRON_ORE, Blocks.LAPIS_BLOCK, Blocks.LAPIS_ORE, Blocks.LIT_REDSTONE_ORE, Blocks.MOSSY_COBBLESTONE, Blocks.NETHERRACK, Blocks.PACKED_ICE, Blocks.RAIL, Blocks.REDSTONE_ORE, Blocks.SANDSTONE, Blocks.RED_SANDSTONE, Blocks.STONE, Blocks.STONE_SLAB, Blocks.STONE_BUTTON, Blocks.STONE_PRESSURE_PLATE, Blocks.OBSIDIAN, Material.ROCK, Material.IRON, Material.ANVIL, Material.GLASS, Material.CIRCUITS);
    protected static final Set AXE_OVERRIDES = Sets.newHashSet(Blocks.PLANKS, Blocks.BOOKSHELF, Blocks.LOG, Blocks.LOG2, Blocks.CHEST, Blocks.PUMPKIN, Blocks.LIT_PUMPKIN, Blocks.MELON_BLOCK, Blocks.LADDER, Blocks.WOODEN_BUTTON, Blocks.WOODEN_PRESSURE_PLATE, Material.PLANTS, Material.LEAVES, Material.WEB, Material.WOOD, Material.CAKE, Material.CLOTH);
    protected Set<Object> effectiveBlocks = new HashSet<Object>();
    protected float baseMiningSpeed = 1F;
    protected int baseAOE = 0;

    public MiningToolBase(double attackDamage, double attackSpeed, Set effectiveBlocks) {
        super(attackDamage, attackSpeed);
        this.effectiveBlocks.addAll(effectiveBlocks);
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
        super.getFields(stack, registry);

        registry.register(stack, new IntegerConfigField("digSpeed", 100, 1, 100, "config.field.digSpeed.description", SLIDER).setExtension("%"));
        registry.register(stack, new BooleanConfigField("aoeSafeMode", false, "config.field.aoeSafeMode.description"));
        registry.register(stack, new BooleanConfigField("showDigAOE", false, "config.field.showDigAOE.description"));

        int maxAOE = baseAOE + UpgradeHelper.getUpgradeLevel(stack, DIG_AOE);
        registry.register(stack, new AOEConfigField("digAOE", 0, 0, maxAOE, "config.field.digAOE.description"));

        if (getToolTier(stack) > 0) {
            int depth = (maxAOE * 2) + 1;
            registry.register(stack, new IntegerConfigField("digDepth", 0, 0, depth, "config.field.digDepth.description", SLIDER));
        }

        return registry;
    }

    //endregion

    //region Vanilla Item Mining Code

    @Override
    public boolean onBlockStartBreak(ItemStack stack, BlockPos pos, EntityPlayer player) {
        int rad = getDigAOE(stack);
        int depth = getDigDepth(stack);
        int totalBlocks = rad * depth;

        if (getEnergyStored(stack) < (energyPerOperation * totalBlocks)) {
            return super.onBlockStartBreak(stack, pos, player);
        }

        if (rad > 0 || depth > 0) {
            return breakAOEBlocksNew(stack, pos, rad, depth, player);
        }

        return super.onBlockStartBreak(stack, pos, player);
    }

    //Note: Called for every AOE block destroyed
    @Override
    public boolean onBlockDestroyed(ItemStack stack, World worldIn, IBlockState state, BlockPos pos, EntityLivingBase entityLiving) {
        modifyEnergy(stack, -energyPerOperation);
        return super.onBlockDestroyed(stack, worldIn, state, pos, entityLiving);
    }

    @Override
    public float getStrVsBlock(ItemStack stack, IBlockState state) {
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
    public boolean canHarvestBlock(IBlockState state, ItemStack stack) {
        return isToolEffective(stack, state);
    }

    @Override
    public int getItemEnchantability(ItemStack stack) {
        return 25;
    }

    @Override
    public boolean checkEnchantTypeValid(EnumEnchantmentType type) {
        return type == EnumEnchantmentType.DIGGER || type == EnumEnchantmentType.ALL;
    }

    //endregion

    //region AOE Mining Code

    /*
    * TODO Serious Optimization!
    * Idea: Create a dummy inventory to store all of the drops. (Also stores an XP counter)
    * Create a method that gets the stack dropped from a block.
    * Add each block stack to the dummy inventory using CCL's inventory utils.
    * Add the xp for each block to the inventory's xp counter.
    * Drop the contents of the dummy inventory at the players feet with 0 pickup delay.
    * Add the inventories xp to the player's xp.
    *
    * Note: Do not want to attempt to add the items directly to the players inventory because that may break
    * things like forestry backpacks that require the player pickup item event... Unless i fire that event manually...
    * */

    //region Old Code

    public boolean breakAOEBlocks(ItemStack stack, BlockPos pos, int breakRadius, int breakDepth, EntityPlayer player) {
        //Map<Block, Integer> blockMap = IConfigurableItem.ProfileHelper.getBoolean(stack, References.OBLITERATE, false) ? getObliterationList(stack) : new HashMap<Block, Integer>();

        IBlockState blockState = player.worldObj.getBlockState(pos);

        if (!isToolEffective(stack, blockState)) {
            return false;
        }

        float refStrength = ForgeHooks.blockStrength(blockState, player, player.worldObj, pos);

        PairKV<BlockPos, BlockPos> aoe = getMiningArea(pos, player, breakRadius, breakDepth);
        List<BlockPos> aoeBlocks = Lists.newArrayList(BlockPos.getAllInBox(aoe.getKey(), aoe.getValue()));

        if (ToolConfigHelper.getBooleanField("aoeSafeMode", stack)) {
            for (BlockPos block : aoeBlocks) {
                if (!player.worldObj.isAirBlock(block) && player.worldObj.getTileEntity(block) != null) {
                    if (player.worldObj.isRemote) {
                        player.addChatComponentMessage(new TextComponentTranslation("msg.de.baseSafeAOW.txt"));
                    }
                    else{
                        ((EntityPlayerMP) player).connection.sendPacket(new SPacketBlockChange(((EntityPlayerMP) player).worldObj, block));
                    }
                    return true;
                }
            }
        }

        for (BlockPos block : aoeBlocks) {
            breakExtraBlock(stack, player.worldObj, block, player, refStrength, new HashMap<Block, Integer>());
        }


        @SuppressWarnings("unchecked") List<EntityItem> items = player.worldObj.getEntitiesWithinAABB(EntityItem.class, new AxisAlignedBB(aoe.getKey(), aoe.getValue().add(1, 1, 1)));
        for (EntityItem item : items) {
            if (!player.worldObj.isRemote) {
                item.setPosition(player.posX, player.posY, player.posZ);
                ((EntityPlayerMP) player).connection.sendPacket(new SPacketEntityTeleport(item));
                item.setPickupDelay(0);

                if (DEConfig.rapidDespawnAOEMinedItems) {
                    item.lifespan = 100;
                }
            }
        }

        player.worldObj.playEvent(2001, pos, Block.getStateId(blockState));

        return true;
    }

    protected void breakExtraBlock(ItemStack stack, World world, BlockPos pos, EntityPlayer player, float refStrength, Map<Block, Integer> blockMap) {
        if (world.isAirBlock(pos)) {
            return;
        }

        IBlockState state = world.getBlockState(pos);
        Block block = state.getBlock();

        if (!isToolEffective(stack, state)) {
            return;
        }

        float strength = ForgeHooks.blockStrength(state, player, world, pos);

        if (!ForgeHooks.canHarvestBlock(block, player, world, pos) || refStrength / strength > 10f) {
            return;
        }

        if (player.capabilities.isCreativeMode) {
            block.onBlockHarvested(world, pos, state, player);
            if (block.removedByPlayer(state, world, pos, player, false)) {
                block.onBlockDestroyedByPlayer(world, pos, state);
            }

            if (!world.isRemote) {
                ((EntityPlayerMP) player).connection.sendPacket(new SPacketBlockChange(world, pos));
            }
            else {
                if (itemRand.nextInt(10) == 0) {
                    world.playEvent(2001, pos, Block.getStateId(state));
                }
            }
            return;
        }

        if (!world.isRemote) {
            int xp = ForgeHooks.onBlockBreakEvent(world, ((EntityPlayerMP) player).interactionManager.getGameType(), (EntityPlayerMP) player, pos);
            if (xp == -1) {
                return;
            }

            TileEntity tileEntity = world.getTileEntity(pos);

            if (block.removedByPlayer(state, world, pos, player, true)) {
                block.onBlockDestroyedByPlayer(world, pos, state);
                block.harvestBlock(world, player, pos, state, tileEntity, stack);
                block.dropXpOnBlockBreak(world, pos, xp);
            }

            stack.onBlockDestroyed(world, state, pos, player);

            EntityPlayerMP mpPlayer = (EntityPlayerMP) player;
            mpPlayer.connection.sendPacket(new SPacketBlockChange(world, pos));
        } else {
            if (itemRand.nextInt(10) == 0) {
                world.playEvent(2001, pos, Block.getStateId(state));
            }
            if (block.removedByPlayer(state, world, pos, player, true)) {
                block.onBlockDestroyedByPlayer(world, pos, state);
            }

            stack.onBlockDestroyed(world, state, pos, player);

            Minecraft.getMinecraft().getConnection().sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK, pos, Minecraft.getMinecraft().objectMouseOver.sideHit));
        }
    }

    //endregion

    //region New Code

    public boolean breakAOEBlocksNew(ItemStack stack, BlockPos pos, int breakRadius, int breakDepth, EntityPlayer player) {
        IBlockState blockState = player.worldObj.getBlockState(pos);

        if (!isToolEffective(stack, blockState)) {
            return false;
        }

        InventoryDynamic inventoryDynamic = new InventoryDynamic();

        float refStrength = ForgeHooks.blockStrength(blockState, player, player.worldObj, pos);

        PairKV<BlockPos, BlockPos> aoe = getMiningArea(pos, player, breakRadius, breakDepth);
        List<BlockPos> aoeBlocks = Lists.newArrayList(BlockPos.getAllInBox(aoe.getKey(), aoe.getValue()));

        if (ToolConfigHelper.getBooleanField("aoeSafeMode", stack)) {
            for (BlockPos block : aoeBlocks) {
                if (!player.worldObj.isAirBlock(block) && player.worldObj.getTileEntity(block) != null) {
                    if (player.worldObj.isRemote) {
                        player.addChatComponentMessage(new TextComponentTranslation("msg.de.baseSafeAOW.txt"));
                    }
                    else{
                        ((EntityPlayerMP) player).connection.sendPacket(new SPacketBlockChange(((EntityPlayerMP) player).worldObj, block));
                    }
                    return true;
                }
            }
        }

        for (BlockPos block : aoeBlocks) {
            breakExtraBlockNew(stack, player.worldObj, block, player, refStrength, inventoryDynamic);
        }


        @SuppressWarnings("unchecked") List<EntityItem> items = player.worldObj.getEntitiesWithinAABB(EntityItem.class, new AxisAlignedBB(aoe.getKey(), aoe.getValue().add(1, 1, 1)));
        for (EntityItem item : items) {
            if (!player.worldObj.isRemote && !item.isDead) {
                InventoryUtils.insertItem(inventoryDynamic, item.getEntityItem(), false);
                item.setDead();
            }
        }

        if (!player.worldObj.isRemote) {
            EntityLootCore lootCore = new EntityLootCore(player.worldObj, inventoryDynamic);
            lootCore.setPosition(player.posX, player.posY, player.posZ);
            player.worldObj.spawnEntityInWorld(lootCore);
        }

        player.worldObj.playEvent(2001, pos, Block.getStateId(blockState));

        return true;
    }

    protected void breakExtraBlockNew(ItemStack stack, World world, BlockPos pos, EntityPlayer player, float refStrength, InventoryDynamic inventory) {
        if (world.isAirBlock(pos)) {
            return;
        }

        IBlockState state = world.getBlockState(pos);
        Block block = state.getBlock();

        if (!isToolEffective(stack, state)) {
            return;
        }

        float strength = ForgeHooks.blockStrength(state, player, world, pos);

        if (!ForgeHooks.canHarvestBlock(block, player, world, pos) || refStrength / strength > 10f) {
            return;
        }

        if (player.capabilities.isCreativeMode) {
            block.onBlockHarvested(world, pos, state, player);
            if (block.removedByPlayer(state, world, pos, player, false)) {
                block.onBlockDestroyedByPlayer(world, pos, state);
            }

            if (!world.isRemote) {
                ((EntityPlayerMP) player).connection.sendPacket(new SPacketBlockChange(world, pos));
            }
            else {
                if (itemRand.nextInt(10) == 0) {
                    world.playEvent(2001, pos, Block.getStateId(state));
                }
            }
            return;
        }

        if (!world.isRemote) {
            int xp = ForgeHooks.onBlockBreakEvent(world, ((EntityPlayerMP) player).interactionManager.getGameType(), (EntityPlayerMP) player, pos);
            if (xp == -1) {
                EntityPlayerMP mpPlayer = (EntityPlayerMP) player;
                mpPlayer.connection.sendPacket(new SPacketBlockChange(world, pos));
                return;
            }

            BlockToStackHelper.breakAndCollectWithPlayer(world, pos, inventory, player);
        } else {
            if (itemRand.nextInt(10) == 0) {
                world.playEvent(2001, pos, Block.getStateId(state));
            }
            if (block.removedByPlayer(state, world, pos, player, true)) {
                block.onBlockDestroyedByPlayer(world, pos, state);
            }

            stack.onBlockDestroyed(world, state, pos, player);

            Minecraft.getMinecraft().getConnection().sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK, pos, Minecraft.getMinecraft().objectMouseOver.sideHit));
        }
    }


    //endregion

    public PairKV<BlockPos, BlockPos> getMiningArea(BlockPos pos, EntityPlayer player, int breakRadius, int breakDepth) {
        RayTraceResult traceResult = RayTracer.retrace(player, 4.5);

        if (traceResult == null || traceResult.typeOfHit != RayTraceResult.Type.BLOCK) {
            return new PairKV<>(pos, pos);
        }

        int sideHit = traceResult.sideHit.getIndex();

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

        if (breakRadius == 0){
            yOffset = 0;
        }

        return new PairKV<>(pos.add(-xMin, yOffset - yMin, -zMin), pos.add(xMax, yOffset + yMax, zMax));
    }


    //endregion

    //region Setters, Getters & Helpers

    public void setHarvestLevel(String toolClass, int level) {
        if (toolClass.equals("pickaxe") && level >= 0) {
            effectiveBlocks.addAll(PICKAXE_OVERRIDES);
        }
        if (toolClass.equals("shovel") && level >= 0) {
            effectiveBlocks.addAll(SHOVEL_OVERRIDES);
        }
        if (toolClass.equals("axe") && level >= 0) {
            effectiveBlocks.addAll(AXE_OVERRIDES);
        }
        super.setHarvestLevel(toolClass, level);
    }

    public float getEfficiency(ItemStack stack) {
        float speedSetting = (float) ToolConfigHelper.getIntegerField("digSpeed", stack) / 100F;
        return baseMiningSpeed * (float) (UpgradeHelper.getUpgradeLevel(stack, DIG_SPEED) + 1) * speedSetting;
    }

    public int getDigAOE(ItemStack stack) {
        return ToolConfigHelper.getIntegerField("digAOE", stack);
    }

    public int getDigDepth(ItemStack stack) {
        return ToolConfigHelper.getIntegerField("digDepth", stack);
    }

    public boolean isToolEffective(ItemStack stack, IBlockState state) {
        if (getEnergyStored(stack) < energyPerOperation) {
            return false;
        }

        for (String type : stack.getItem().getToolClasses(stack)) {
            if (state.getBlock().isToolEffective(type, state) || effectiveBlocks.contains(state.getBlock()) || effectiveBlocks.contains(state.getMaterial())) {
                return true;
            }
        }

        return false;
    }

    //endregion

}
