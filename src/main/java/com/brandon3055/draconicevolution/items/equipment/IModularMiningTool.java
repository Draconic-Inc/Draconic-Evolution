package com.brandon3055.draconicevolution.items.equipment;

import codechicken.lib.inventory.InventoryUtils;
import codechicken.lib.raytracer.RayTracer;
import com.brandon3055.brandonscore.inventory.BlockToStackHelper;
import com.brandon3055.brandonscore.inventory.InventoryDynamic;
import com.brandon3055.brandonscore.lib.Pair;
import com.brandon3055.draconicevolution.api.capability.ModuleHost;
import com.brandon3055.draconicevolution.api.capability.PropertyProvider;
import com.brandon3055.draconicevolution.api.modules.ModuleTypes;
import com.brandon3055.draconicevolution.api.modules.data.AOEData;
import com.brandon3055.draconicevolution.init.EquipCfg;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPlayerDiggingPacket;
import net.minecraft.network.play.server.SChangeBlockPacket;
import net.minecraft.util.Util;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import static com.brandon3055.draconicevolution.api.capability.DECapabilities.MODULE_HOST_CAPABILITY;

/**
 * Created by brandon3055 on 16/6/20
 */
public interface IModularMiningTool extends IModularTieredItem {

    Random rand = new Random();

    @Override
    default boolean onBlockStartBreak(ItemStack stack, BlockPos pos, PlayerEntity player) {
        if (getEnergyStored(stack) < EquipCfg.energyHarvest && !player.abilities.isCreativeMode) {
            return false;
        }

        ModuleHost host = stack.getCapability(MODULE_HOST_CAPABILITY).orElseThrow(IllegalStateException::new);
        int aoe = host.getModuleData(ModuleTypes.AOE, new AOEData(0)).getAOE();
        boolean aoeSafe = false;
        if (host instanceof PropertyProvider) {
            if (((PropertyProvider) host).hasInt("mining_aoe")) {
                aoe = ((PropertyProvider) host).getInt("mining_aoe").getValue();
            }
            if (((PropertyProvider) host).hasBool("aoe_safe")) {
                aoeSafe = ((PropertyProvider) host).getBool("aoe_safe").getValue();
            }
        }

        if (aoe > 0) {
            return breakAOEBlocks(stack, pos, aoe, 0, player, aoeSafe);
        }

        extractEnergy(player, stack, EquipCfg.energyHarvest);
        return false;
    }

    default boolean breakAOEBlocks(ItemStack stack, BlockPos pos, int breakRadius, int breakDepth, PlayerEntity player, boolean aoeSafe) {
        BlockState blockState = player.world.getBlockState(pos);
        if (!isToolEffective(stack, blockState)) {
            return false;
        }

        InventoryDynamic inventoryDynamic = new InventoryDynamic();
        float refStrength = blockStrength(blockState, player, player.world, pos);
        Pair<BlockPos, BlockPos> aoe = getMiningArea(pos, player, breakRadius, breakDepth);
        List<BlockPos> aoeBlocks = BlockPos.getAllInBox(aoe.key(), aoe.value()).map(BlockPos::new).collect(Collectors.toList());

        if (aoeSafe) {
            for (BlockPos block : aoeBlocks) {
                if (!player.world.isAirBlock(block) && player.world.getTileEntity(block) != null) {
                    if (player.world.isRemote) player.sendMessage(new TranslationTextComponent("item_prop.draconicevolution.aoe_safe.blocked"), Util.DUMMY_UUID);
                    else ((ServerPlayerEntity) player).connection.sendPacket(new SChangeBlockPacket(((ServerPlayerEntity) player).world, block));
                    return true;
                }
            }
        }

        aoeBlocks.forEach(block -> breakAOEBlock(stack, player.world, block, player, refStrength, inventoryDynamic, rand.nextInt(Math.max(5, (breakRadius * breakDepth) / 5)) == 0));
        List<ItemEntity> items = player.world.getEntitiesWithinAABB(ItemEntity.class, new AxisAlignedBB(aoe.key(), aoe.value().add(1, 1, 1)));
        for (ItemEntity item : items) {
            if (!player.world.isRemote && item.isAlive()) {
                InventoryUtils.insertItem(inventoryDynamic, item.getItem(), false);
                item.remove();
            }
        }

        //TODO Junk Filter
//        Set<ItemStack> junkFilter = getJunkFilter(stack);
//        if (junkFilter != null) {
//            boolean nbtSens = ToolConfigHelper.getBooleanField("junkNbtSens", stack);
//            inventoryDynamic.removeIf(check -> {
//                for (ItemStack junk : junkFilter) {
//                    if (junk.isItemEqual(check) && (!nbtSens || ItemStack.areItemStackTagsEqual(junk, check))) {
//                        return true;
//                    }
//                }
//                return false;
//            });
//        }

        if (!player.world.isRemote) {
//            if (DEOldConfig.disableLootCores) {
            for (int i = 0; i < inventoryDynamic.getSizeInventory(); i++) {
                ItemStack sis = inventoryDynamic.getStackInSlot(i);
                if (sis != null) {
                    ItemEntity item = new ItemEntity(player.world, player.getPosX(), player.getPosY(), player.getPosZ(), sis);
                    item.setPickupDelay(0);
//                        player.world.addEntity(item);
                }
            }
            player.giveExperiencePoints(inventoryDynamic.xp);
            inventoryDynamic.clear();
//            } else {
//                EntityLootCore lootCore = new EntityLootCore(player.world, inventoryDynamic); TODO Entity Stuff
//                lootCore.setPosition(player.getPosX(), player.getPosY(), player.getPosZ());
//                player.world.addEntity(lootCore);
//            }
        }

        return true;
    }

    static float blockStrength(BlockState state, PlayerEntity player, World world, BlockPos pos) {
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

    default Pair<BlockPos, BlockPos> getMiningArea(BlockPos pos, PlayerEntity player, int breakRadius, int breakDepth) {
        BlockRayTraceResult traceResult = RayTracer.retrace(player);
        if (traceResult.getType() == RayTraceResult.Type.MISS) {
            return new Pair<>(pos, pos);
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

        return new Pair<>(pos.add(-xMin, yOffset - yMin, -zMin), pos.add(xMax, yOffset + yMax, zMax));
    }

    default void breakAOEBlock(ItemStack stack, World world, BlockPos pos, PlayerEntity player, float refStrength, InventoryDynamic inventory, boolean breakFX) {
        if (world.isAirBlock(pos)) {
            return;
        }

        BlockState state = world.getBlockState(pos);
        FluidState fluidState = world.getFluidState(pos);
        Block block = state.getBlock();

        if (!isToolEffective(stack, state)) {
            return;
        }

        float strength = blockStrength(state, player, world, pos);

        if (!ForgeHooks.canHarvestBlock(state, player, world, pos) || refStrength / strength > 10f) {
            return;
        }

        if (player.abilities.isCreativeMode) {
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

            EquipCfg.energyHarvest = 256;
            stack.onBlockDestroyed(world, state, pos, player);
            BlockToStackHelper.breakAndCollectWithPlayer(world, pos, inventory, player, xp);
            extractEnergy(player, stack, EquipCfg.energyHarvest);
        } else {
            if (block.removedByPlayer(state, world, pos, player, true, fluidState)) {
                block.onPlayerDestroy(world, pos, state);
            }

            stack.onBlockDestroyed(world, state, pos, player);

            if (Minecraft.getInstance().objectMouseOver instanceof BlockRayTraceResult) {
                Minecraft.getInstance().getConnection().sendPacket(new CPlayerDiggingPacket(CPlayerDiggingPacket.Action.STOP_DESTROY_BLOCK, pos, ((BlockRayTraceResult) Minecraft.getInstance().objectMouseOver).getFace()));
            }
        }
    }
}
