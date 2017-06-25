package com.brandon3055.draconicevolution.blocks.tileentity;

import com.brandon3055.brandonscore.blocks.TileInventoryBase;
import com.brandon3055.brandonscore.lib.PairKV;
import com.brandon3055.brandonscore.lib.Vec3D;
import com.brandon3055.brandonscore.lib.datamanager.ManagedBool;
import com.brandon3055.brandonscore.utils.FacingUtils;
import com.brandon3055.brandonscore.utils.InventoryUtils;
import com.brandon3055.brandonscore.utils.Teleporter;
import com.brandon3055.brandonscore.utils.Utils;
import com.brandon3055.draconicevolution.DEFeatures;
import com.brandon3055.draconicevolution.blocks.DislocatorReceptacle;
import com.brandon3055.draconicevolution.blocks.Portal;
import com.brandon3055.draconicevolution.items.tools.Dislocator;
import com.brandon3055.draconicevolution.lib.DESoundHandler;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by brandon3055 on 16/07/2016.
 */
public class TileDislocatorReceptacle extends TileInventoryBase implements ITickable {

    public final ManagedBool ACTIVE = register("ACTIVE", new ManagedBool(false)).saveToTile().syncViaTile().trigerUpdate().finish();
    public final ManagedBool CAMO = register("CAMO", new ManagedBool(false)).saveToTile().syncViaTile().trigerUpdate().finish();
    public final ManagedBool LT_REDSTONE = register("LT_REDSTONE", new ManagedBool(false)).saveToTile().syncViaTile().trigerUpdate().finish();
    public boolean igniting = false;
    private List<Entity> teleportQ = new ArrayList<Entity>();
    private Map<Integer, Integer> cooldownMap = new HashMap<>();

    public TileDislocatorReceptacle() {
        setInventorySize(1);
        setShouldRefreshOnBlockChange();
    }

    @Override
    public void update() {
        super.update();
        for (Entity entity : teleportQ) {
            ItemStack stack = getStackInSlot(0);

            if (!(stack.getItem() instanceof Dislocator)) {
                deactivate();
                return;
            }

            Teleporter.TeleportLocation location = ((Dislocator) stack.getItem()).getLocation(stack);

            if (location == null) {
                deactivate();
                return;
            }

            DESoundHandler.playSoundFromServer(entity.world, entity.posX, entity.posY, entity.posZ, DESoundHandler.portal, SoundCategory.PLAYERS, 0.1F, entity.world.rand.nextFloat() * 0.1F + 0.9F, false, 32);
            location.teleport(entity);
            DESoundHandler.playSoundFromServer(entity.world, entity.posX, entity.posY, entity.posZ, DESoundHandler.portal, SoundCategory.PLAYERS, 0.1F, entity.world.rand.nextFloat() * 0.1F + 0.9F, false, 32);
        }

        try {

//            Iterator<Map.Entry<Entity, Integer>> i = cooldownMap.entrySet().iterator();
//            while (i.hasNext()) {
//                Map.Entry<Entity, Integer> entry = i.next();
//
//            }
            List<Integer> toRemove = new ArrayList<>();

            for (Integer key : cooldownMap.keySet()) {
                if (cooldownMap.get(key) > 0) {
                    cooldownMap.put(key, cooldownMap.get(key) - 1);
                }
                else {
                    toRemove.add(key);
                }
            }

            for (Integer i : toRemove) {
                cooldownMap.remove(i);
            }
            toRemove.clear();

        }
        catch (Exception e) {
            e.printStackTrace();
        }

        teleportQ.clear();
    }

    //region Activation & Inventory

    public boolean onBlockActivated(EntityPlayer player) {
        if (world.isRemote) {
            return !LT_REDSTONE.value;
        }

        InventoryUtils.handleHeldStackTransfer(0, this, player);

//        if (getStackInSlot(0) != null) {
//            if (LT_REDSTONE.value) {
//                return true;
//            }
//            if (player.getHeldItemMainhand().isEmpty()) {
//                player.setHeldItem(EnumHand.MAIN_HAND, getStackInSlot(0));
//                setInventorySlotContents(0, ItemStack.EMPTY);
//            }
//            else {
//                world.spawnEntity(new EntityItem(world, player.posX, player.posY, player.posZ, getStackInSlot(0)));
//                setInventorySlotContents(0, ItemStack.EMPTY);
//            }
//            deactivate();
//        }
//        else {
//            ItemStack stack = player.getHeldItemMainhand();
//            if (!stack.isEmpty() && stack.getItem() instanceof Dislocator && ((Dislocator) stack.getItem()).getLocation(stack) != null) {
//                setInventorySlotContents(0, player.getHeldItemMainhand());
//                player.setHeldItem(EnumHand.MAIN_HAND, ItemStack.EMPTY);
//                attemptIgnition();
//            }
//        }

        return true;
    }

    @Override
    public void setInventorySlotContents(int index, ItemStack stack) {
        super.setInventorySlotContents(index, stack);
        if (getStackInSlot(0).isEmpty() && ACTIVE.value) {
            deactivate();
        }
        else if (!getStackInSlot(0).isEmpty()) {
            attemptIgnition();
        }
    }

    @Override
    public boolean isItemValidForSlot(int index, ItemStack stack) {
        return stack.getItem() instanceof Dislocator;
    }

    //endregion

    //region Teleport Handling

    public void handleEntityTeleport(Entity entity) {
        if (world.isRemote || teleportQ.contains(entity) || cooldownMap.containsKey(entity.getEntityId())) {
            return;
        }

        cooldownMap.put(entity.getEntityId(), 10);
        teleportQ.add(entity);
    }

    //endregion

    //region MultiBlock

    public void deactivate() {
        ACTIVE.value = false;

        IBlockState state = world.getBlockState(pos);
        if (state.getBlock() == DEFeatures.dislocatorReceptacle) {
            world.setBlockState(pos, state.withProperty(DislocatorReceptacle.ACTIVE, false));
        }

        for (BlockPos checkPos : BlockPos.getAllInBox(pos.add(-1, -1, -1), pos.add(1, 1, 1))) {
            TileEntity tile = world.getTileEntity(checkPos);
            if (tile instanceof TilePortal && ((TilePortal) tile).masterPos.vec.getPos().equals(pos)) {
                world.setBlockToAir(tile.getPos());
            }
        }
        updateBlock();
    }

    public boolean attemptIgnition() {
        ItemStack stack = getStackInSlot(0);

        if (!(stack.getItem() instanceof Dislocator) || ((Dislocator) stack.getItem()).getLocation(stack) == null) {
            return false;
        }

        PairKV<EnumFacing.Axis, List<BlockPos>> portalConfiguration = scanConfigurations();
        if (portalConfiguration != null) {
            igniting = true;

            for (BlockPos portalBlock : portalConfiguration.getValue()) {
                world.setBlockState(portalBlock, DEFeatures.portal.getDefaultState().withProperty(Portal.AXIS, portalConfiguration.getKey()));
                TileEntity tile = world.getTileEntity(portalBlock);
                if (tile instanceof TilePortal) {
                    ((TilePortal) tile).setMasterPos(pos);
                }
            }

            ACTIVE.value = true;

            IBlockState state = world.getBlockState(pos);
            if (state.getBlock() == DEFeatures.dislocatorReceptacle) {
                world.setBlockState(pos, state.withProperty(DislocatorReceptacle.ACTIVE, true));
            }

            updateBlock();
            igniting = false;

            return true;
        }

        return false;
    }

    private PairKV<EnumFacing.Axis, List<BlockPos>> scanConfigurations() {
        List<BlockPos> scanned = new ArrayList<BlockPos>();
        for (BlockPos offset : FacingUtils.AROUND_X) {
            List<BlockPos> portalBlocks = scanFromOrigin(pos.add(offset), EnumFacing.Axis.X, scanned);
            if (portalBlocks != null) {
                return new PairKV<EnumFacing.Axis, List<BlockPos>>(EnumFacing.Axis.X, portalBlocks);
            }
        }

        scanned = new ArrayList<BlockPos>();
        for (BlockPos offset : FacingUtils.AROUND_Y) {
            List<BlockPos> portalBlocks = scanFromOrigin(pos.add(offset), EnumFacing.Axis.Y, scanned);
            if (portalBlocks != null) {
                return new PairKV<EnumFacing.Axis, List<BlockPos>>(EnumFacing.Axis.Y, portalBlocks);
            }
        }

        scanned = new ArrayList<BlockPos>();
        for (BlockPos offset : FacingUtils.AROUND_Z) {
            List<BlockPos> portalBlocks = scanFromOrigin(pos.add(offset), EnumFacing.Axis.Z, scanned);
            if (portalBlocks != null) {
                return new PairKV<EnumFacing.Axis, List<BlockPos>>(EnumFacing.Axis.Z, portalBlocks);
            }
        }

        return null;
    }

    private List<BlockPos> scanFromOrigin(BlockPos scanOrigin, EnumFacing.Axis scanAxis, List<BlockPos> alreadyScanned) {
        if (!world.isAirBlock(scanOrigin) || alreadyScanned.contains(scanOrigin)) {
            return null;
        }

        List<BlockPos> scannedBlocks = new ArrayList<BlockPos>();
        if (scanPortal(scanOrigin, scanOrigin, scanAxis, scannedBlocks, alreadyScanned)) {
            return scannedBlocks;
        }

        return null;
    }

    private boolean scanPortal(BlockPos scanPos, BlockPos origin, EnumFacing.Axis scanAxis, List<BlockPos> scanList, List<BlockPos> blackList) {
        if (Utils.getDistanceAtoB(new Vec3D(scanPos), new Vec3D(origin)) > 100) {
            return false;
        }

        scanList.add(scanPos);

        for (EnumFacing facing : FacingUtils.getFacingsAroundAxis(scanAxis)) {
            BlockPos nextPos = scanPos.offset(facing);
            if (scanList.contains(nextPos) || isFrame(nextPos)) {
                continue;
            }
            else if (world.isAirBlock(nextPos)) {
                if (!scanPortal(nextPos, origin, scanAxis, scanList, blackList)) {
                    return false;
                }
            }
            else {
                return false;
            }
        }

        return true;
    }

    private boolean isFrame(BlockPos pos) {
        IBlockState state = world.getBlockState(pos);
        return state.getBlock() == DEFeatures.infusedObsidian || state.getBlock() == DEFeatures.dislocatorReceptacle;
    }

    //endregion
}
