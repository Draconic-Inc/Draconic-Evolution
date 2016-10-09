package com.brandon3055.draconicevolution.blocks.tileentity;

import com.brandon3055.brandonscore.blocks.TileInventoryBase;
import com.brandon3055.brandonscore.lib.PairKV;
import com.brandon3055.brandonscore.lib.Vec3D;
import com.brandon3055.brandonscore.network.wrappers.SyncableBool;
import com.brandon3055.brandonscore.utils.FacingUtils;
import com.brandon3055.brandonscore.utils.Teleporter;
import com.brandon3055.brandonscore.utils.Utils;
import com.brandon3055.draconicevolution.DEFeatures;
import com.brandon3055.draconicevolution.blocks.DislocatorReceptacle;
import com.brandon3055.draconicevolution.blocks.Portal;
import com.brandon3055.draconicevolution.items.tools.Dislocator;
import com.brandon3055.draconicevolution.lib.DESoundHandler;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ITickable;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;

import java.util.*;

/**
 * Created by brandon3055 on 16/07/2016.
 */
public class TileDislocatorReceptacle extends TileInventoryBase implements ITickable {

    public final SyncableBool ACTIVE = new SyncableBool(false, true, false, true);
    public final SyncableBool CAMO = new SyncableBool(false, true, false, true);
    public final SyncableBool LT_REDSTONE = new SyncableBool(false, true, false, true);
    public boolean igniting = false;
    private List<Entity> teleportQ = new ArrayList<Entity>();
    private Map<Integer, Integer> cooldownMap = new HashMap<>();

    public TileDislocatorReceptacle() {
        setInventorySize(1);
        setShouldRefreshOnBlockChange();
        registerSyncableObject(ACTIVE, true);
        registerSyncableObject(LT_REDSTONE, true);
        registerSyncableObject(CAMO, true);
    }

    @Override
    public void update() {
        for (Entity entity : teleportQ){
            ItemStack stack = getStackInSlot(0);

            if (stack == null || !(stack.getItem() instanceof Dislocator)){
                deactivate();
                return;
            }

            Teleporter.TeleportLocation location = ((Dislocator) stack.getItem()).getLocation(stack);

            if (location == null){
                deactivate();
                return;
            }

            DESoundHandler.playSoundFromServer(entity.worldObj, entity.posX, entity.posY, entity.posZ, DESoundHandler.portal, SoundCategory.PLAYERS, 0.1F, entity.worldObj.rand.nextFloat() * 0.1F + 0.9F, false, 32);
            location.teleport(entity);
            DESoundHandler.playSoundFromServer(entity.worldObj, entity.posX, entity.posY, entity.posZ, DESoundHandler.portal, SoundCategory.PLAYERS, 0.1F, entity.worldObj.rand.nextFloat() * 0.1F + 0.9F, false, 32);
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
        catch (Exception e) {e.printStackTrace();}

        teleportQ.clear();
    }

    //region Activation & Inventory

    public boolean onBlockActivated(EntityPlayer player) {
        if (worldObj.isRemote) {
            return !LT_REDSTONE.value;
        }

        if (getStackInSlot(0) != null) {
            if (LT_REDSTONE.value) {
                return true;
            }
            if (player.getHeldItemMainhand() == null) {
                player.setHeldItem(EnumHand.MAIN_HAND, getStackInSlot(0));
                setInventorySlotContents(0, null);
            } else {
                worldObj.spawnEntityInWorld(new EntityItem(worldObj, player.posX, player.posY, player.posZ, getStackInSlot(0)));
                setInventorySlotContents(0, null);
            }
            deactivate();
        }
        else {
            ItemStack stack = player.getHeldItemMainhand();
            if (stack != null && stack.getItem() instanceof Dislocator && ((Dislocator) stack.getItem()).getLocation(stack) != null) {
                setInventorySlotContents(0, player.getHeldItemMainhand());
                player.setHeldItem(EnumHand.MAIN_HAND, null);
                attemptIgnition();
            }
        }

        return true;
    }

    @Override
    public void setInventorySlotContents(int index, ItemStack stack) {
        super.setInventorySlotContents(index, stack);
        if (getStackInSlot(0) == null && !ACTIVE.value) {
            deactivate();
        }
        else if (getStackInSlot(0) != null) {
            attemptIgnition();
        }
    }

    @Override
    public boolean isItemValidForSlot(int index, ItemStack stack) {
        return stack != null && stack.getItem() instanceof Dislocator;
    }

    //endregion

    //region Teleport Handling

    public void handleEntityTeleport(Entity entity) {
        if (worldObj.isRemote || teleportQ.contains(entity) || cooldownMap.containsKey(entity.getEntityId())) {
            return;
        }

        cooldownMap.put(entity.getEntityId(), 10);
        teleportQ.add(entity);
    }

    //endregion

    //region MultiBlock

    public void deactivate() {
        ACTIVE.value = false;

        IBlockState state = worldObj.getBlockState(pos);
        if (state.getBlock() == DEFeatures.dislocatorReceptacle) {
            worldObj.setBlockState(pos, state.withProperty(DislocatorReceptacle.ACTIVE, false));
        }

        for (BlockPos checkPos : BlockPos.getAllInBox(pos.add(-1, -1, -1), pos.add(1, 1, 1))){
            TileEntity tile = worldObj.getTileEntity(checkPos);
            if (tile instanceof TilePortal && ((TilePortal) tile).masterPos.vec.getPos().equals(pos)){
                worldObj.setBlockToAir(tile.getPos());
            }
        }
        updateBlock();
    }

    public boolean attemptIgnition() {
        ItemStack stack = getStackInSlot(0);

        if (stack == null || !(stack.getItem() instanceof Dislocator) || ((Dislocator) stack.getItem()).getLocation(stack) == null){
            return false;
        }

        PairKV<EnumFacing.Axis, List<BlockPos>> portalConfiguration = scanConfigurations();
        if (portalConfiguration != null){
            igniting = true;

            for (BlockPos portalBlock : portalConfiguration.getValue()){
                worldObj.setBlockState(portalBlock, DEFeatures.portal.getDefaultState().withProperty(Portal.AXIS, portalConfiguration.getKey()));
                TileEntity tile = worldObj.getTileEntity(portalBlock);
                if (tile instanceof TilePortal){
                    ((TilePortal) tile).setMasterPos(pos);
                }
            }

            ACTIVE.value = true;

            IBlockState state = worldObj.getBlockState(pos);
            if (state.getBlock() == DEFeatures.dislocatorReceptacle) {
                worldObj.setBlockState(pos, state.withProperty(DislocatorReceptacle.ACTIVE, true));
            }

            updateBlock();
            igniting = false;

            return true;
        }

        return false;
    }

    private PairKV<EnumFacing.Axis, List<BlockPos>> scanConfigurations(){
        List<BlockPos> scanned = new ArrayList<BlockPos>();
        for (BlockPos offset : FacingUtils.AROUND_X) {
            List<BlockPos> portalBlocks = scanFromOrigin(pos.add(offset), EnumFacing.Axis.X, scanned);
            if (portalBlocks != null) {
                return new PairKV<EnumFacing.Axis, List<BlockPos>>(EnumFacing.Axis.X, portalBlocks);
            }
        }

        scanned = new ArrayList<BlockPos>();
        for (BlockPos offset : FacingUtils.AROUND_Y){
            List<BlockPos> portalBlocks = scanFromOrigin(pos.add(offset), EnumFacing.Axis.Y, scanned);
            if (portalBlocks != null) {
                return new PairKV<EnumFacing.Axis, List<BlockPos>>(EnumFacing.Axis.Y, portalBlocks);
            }
        }

        scanned = new ArrayList<BlockPos>();
        for (BlockPos offset : FacingUtils.AROUND_Z){
            List<BlockPos> portalBlocks = scanFromOrigin(pos.add(offset), EnumFacing.Axis.Z, scanned);
            if (portalBlocks != null) {
                return new PairKV<EnumFacing.Axis, List<BlockPos>>(EnumFacing.Axis.Z, portalBlocks);
            }
        }

        return null;
    }

    private List<BlockPos> scanFromOrigin(BlockPos scanOrigin, EnumFacing.Axis scanAxis, List<BlockPos> alreadyScanned) {
        if (!worldObj.isAirBlock(scanOrigin) || alreadyScanned.contains(scanOrigin)){
            return null;
        }

        List<BlockPos> scannedBlocks = new ArrayList<BlockPos>();
        if (scanPortal(scanOrigin, scanOrigin, scanAxis, scannedBlocks, alreadyScanned)) {
            return scannedBlocks;
        }

        return null;
    }

    private boolean scanPortal(BlockPos scanPos, BlockPos origin, EnumFacing.Axis scanAxis, List<BlockPos> scanList, List<BlockPos> blackList) {
        if (Utils.getDistanceAtoB(new Vec3D(scanPos), new Vec3D(origin)) > 100){
            return false;
        }

        scanList.add(scanPos);

        for (EnumFacing facing : FacingUtils.getFacingsAroundAxis(scanAxis)){
            BlockPos nextPos = scanPos.offset(facing);
            if (scanList.contains(nextPos) || isFrame(nextPos)){
                continue;
            }
            else if (worldObj.isAirBlock(nextPos)){
                if (!scanPortal(nextPos, origin, scanAxis, scanList, blackList)){
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
        IBlockState state = worldObj.getBlockState(pos);
        return state.getBlock() == DEFeatures.infusedObsidian || state.getBlock() == DEFeatures.dislocatorReceptacle;
    }

    //endregion
}
