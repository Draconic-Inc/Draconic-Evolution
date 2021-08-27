package com.brandon3055.draconicevolution.blocks.tileentity;

import codechicken.lib.vec.Vector3;
import com.brandon3055.brandonscore.api.hud.IHudBlock;
import com.brandon3055.brandonscore.blocks.TileBCore;
import com.brandon3055.brandonscore.inventory.TileItemStackHandler;
import com.brandon3055.brandonscore.lib.*;
import com.brandon3055.brandonscore.lib.datamanager.*;
import com.brandon3055.brandonscore.network.BCoreNetwork;
import com.brandon3055.brandonscore.utils.InventoryUtils;
import com.brandon3055.brandonscore.utils.TargetPos;
import com.brandon3055.brandonscore.utils.Utils;
import com.brandon3055.draconicevolution.blocks.DislocatorReceptacle;
import com.brandon3055.draconicevolution.handlers.DESounds;
import com.brandon3055.draconicevolution.init.DEContent;
import com.brandon3055.draconicevolution.items.tools.Dislocator;
import com.brandon3055.draconicevolution.network.DraconicNetwork;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;

import net.minecraft.util.math.vector.Vector3i;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.items.CapabilityItemHandler;

import javax.annotation.Nullable;
import java.util.*;

import static com.brandon3055.brandonscore.lib.datamanager.DataFlags.SAVE_NBT_SYNC_TILE;
import static com.brandon3055.brandonscore.lib.datamanager.DataFlags.SYNC_TILE;

/**
 * Created by brandon3055 on 16/07/2016.
 */
public class TileDislocatorReceptacle extends TileBCore implements ITickableTileEntity, IInteractTile, IHudBlock/*, ITeleportEndPoint, ICrystalLink, IENetEffectTile*/ {

    public final ManagedPos arrivalPos = register(new ManagedPos("arrival_pos", (BlockPos) null, SAVE_NBT_SYNC_TILE));
    public final ManagedByte ignitionStage = register(new ManagedByte("ignition_stage", (byte) 0, SYNC_TILE));

    public TileItemStackHandler itemHandler = new TileItemStackHandler(1);
    private PortalHelper portalHelper = new PortalHelper(this);
    //A que is used to get around the issue of an entity touching multiple portal blocks simultaneously and getting teleported more than once.
    private List<Entity> teleportQ = new ArrayList<>();

    public TileDislocatorReceptacle() {
        super(DEContent.tile_dislocator_receptacle);
        capManager.setManaged("inventory", CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, itemHandler).saveBoth().syncTile();
        itemHandler.setContentsChangeListener(e -> onInventoryChange());
        itemHandler.setSlotValidator(0, (stack) -> stack.getItem() instanceof Dislocator);
    }

    @Override
    public ActionResultType onBlockUse(BlockState state, PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
        ItemStack stack = player.getItemInHand(hand);
        if (hasRSSignal()) {
            return ActionResultType.PASS;
        }

        if (stack.getItem() == DEContent.infused_obsidian.asItem()) {
            level.setBlockAndUpdate(worldPosition, state.setValue(DislocatorReceptacle.CAMO, !state.getValue(DislocatorReceptacle.CAMO)));
            return ActionResultType.SUCCESS;
        }

        if (!level.isClientSide) {
            InventoryUtils.handleHeldStackTransfer(0, itemHandler, player);
        }

        return ActionResultType.SUCCESS;
    }

    private void onInventoryChange() {
        if (level.isClientSide) return;

        if (portalHelper.isRunning()) {
            portalHelper.abort();
        }

        ItemStack stack = itemHandler.getStackInSlot(0);
        if (stack.isEmpty() && isActive()) {
            deactivate();
        } else if (!stack.isEmpty()) {
            attemptActivation();
        }
    }

    public void attemptActivation() {
        if (level.isClientSide || isActive() || portalHelper.isRunning()) return;
        TargetPos target = getTargetPos();
        if (target != null) {
            portalHelper.startScan();
            ignitionStage.set(1);
        }
    }

    public void deactivate() {
        setActive(false);
        for (BlockPos pos : BlockPos.betweenClosed(getBlockPos().offset(-1, -1, -1), getBlockPos().offset(1, 1, 1))) {
            TileEntity tile = level.getBlockEntity(pos);
            if (tile instanceof TilePortal && ((TilePortal) tile).getControllerPos().equals(getBlockPos())) {
                level.removeBlock(pos, false);
            }
        }
    }

    public void handleEntityTeleport(Entity entity) {
        if (level.isClientSide || teleportQ.contains(entity)) {
            return;
        }

        if (entity.isOnPortalCooldown()) {
            entity.setPortalCooldown();
            return;
        }

        teleportQ.add(entity);
    }

    @Override
    public void tick() {
        super.tick();
        if (level.isClientSide) return;

        if (portalHelper.isRunning()) {
            int maxSpeed = portalHelper.isBuilding() ? 125 : 384; //ignitionStage.get() == 1 ? 128 : 256;
            int cycles = Utils.scaleToTPS(level, maxSpeed / 8, maxSpeed);
            for (int i = 0; i < cycles && portalHelper.isRunning(); i++) {
                portalHelper.updateTick();
            }
        } else if (!teleportQ.isEmpty()) {
            for (Entity entity : teleportQ) {
                TargetPos target = getTargetPos();
                if (target == null) {
                    deactivate();
                    teleportQ.clear();
                    return;
                }

                BCoreNetwork.sendSound(entity.level, entity.blockPosition(), DESounds.portal, SoundCategory.PLAYERS, 0.1F, entity.level.random.nextFloat() * 0.1F + 0.9F, false);
                entity.setPortalCooldown();
                target.teleport(entity);
                if (entity instanceof ServerPlayerEntity) {
                    //This is a hack. I need to find a better solution.
                    DelayedTask.run(10, () -> DraconicNetwork.sendDislocatorTeleported((ServerPlayerEntity) entity));
                    DelayedTask.run(20, () -> DraconicNetwork.sendDislocatorTeleported((ServerPlayerEntity) entity));
                    DelayedTask.run(30, () -> DraconicNetwork.sendDislocatorTeleported((ServerPlayerEntity) entity));
                }
                entity.setPortalCooldown();
                BCoreNetwork.sendSound(entity.level, entity.blockPosition(), DESounds.portal, SoundCategory.PLAYERS, 0.1F, entity.level.random.nextFloat() * 0.1F + 0.9F, false);
            }
            teleportQ.clear();
        }
    }

    //#################################################################################
    // Portal Helper Call Backs
    //#################################################################################

    public void onScanBlock(BlockPos pos) {
        BCoreNetwork.sendParticle(getLevel(), ParticleTypes.CLOUD, Vector3.fromBlockPosCenter(pos), Vector3.ZERO, true);
    }

    public void onScanComplete(@Nullable Set<BlockPos> result, @Nullable Direction.Axis resultAxis) {
        if (result == null || resultAxis == null) {
            ignitionStage.set(0);
        } else {
            ignitionStage.set(2);
            portalHelper.buildPortal(result, resultAxis);
        }
    }

    public void onBuildSuccess(List<BlockPos> builtList) {
        setActive(true);
        ignitionStage.set(0);
        Map<Integer, List<BlockPos>> levelMap = new HashMap<>();
        builtList.forEach(block -> levelMap.computeIfAbsent(block.getY(), integer -> new ArrayList<>()).add(block));
        LinkedList<Integer> levels = new LinkedList<>(levelMap.keySet());
        levels.sort(Comparator.naturalOrder());

        List<BlockPos> foundValid = new ArrayList<>();
        for (int level : levels) {
            List<BlockPos> blocks = levelMap.get(level);
            for (BlockPos pos : blocks) {
                if (this.level.isEmptyBlock(pos.above()) || this.level.getBlockState(pos.above()).getBlock() == DEContent.portal) {
                    foundValid.add(pos);
                }
            }
            if (!foundValid.isEmpty()) {
                break;
            }
        }

        if (foundValid.isEmpty()) {
            arrivalPos.set(null);
            return;
        }

        Vector3 min = new Vector3().set(60000000);
        Vector3 max = new Vector3().set(-60000000);
        for (BlockPos pos : foundValid) {
            if (pos.getX() < min.x) min.x = pos.getX();
            if (pos.getY() < min.y) min.y = pos.getY();
            if (pos.getZ() < min.z) min.z = pos.getZ();
            if (pos.getX() > max.x) max.x = pos.getX();
            if (pos.getY() > max.y) max.y = pos.getY();
            if (pos.getZ() > max.z) max.z = pos.getZ();
        }
        Vector3 mid = min.copy().add(max.subtract(min).divide(2));
        BlockPos closestPos = foundValid.get(0);
        double closest = Integer.MAX_VALUE;
        for (BlockPos pos : foundValid) {
            double dist = Utils.getDistanceSq(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, mid.x, mid.y, mid.z);
            if (dist < closest) {
                closest = dist;
                closestPos = pos;
            }
        }
        arrivalPos.set(closestPos);
    }

    public void onBuildFail() {
        setActive(false);
        ignitionStage.set(0);
    }

    //#################################################################################

    public boolean isActive() {
        return getBlockState().getValue(DislocatorReceptacle.ACTIVE);
    }

    public void setActive(boolean active) {
        if (level.getBlockState(getBlockPos()).getBlock() == DEContent.dislocator_receptacle) {
            level.setBlockAndUpdate(getBlockPos(), getBlockState().setValue(DislocatorReceptacle.ACTIVE, active));
        }
    }

    private TargetPos getTargetPos() {
        ItemStack stack = itemHandler.getStackInSlot(0);
        if (!(stack.getItem() instanceof Dislocator)) return null;
        return ((Dislocator) stack.getItem()).getTargetPos(stack, level);
    }

    @Override
    public void generateHudText(World world, BlockPos pos, PlayerEntity player, List<ITextComponent> displayList) {
        displayList.add(new StringTextComponent(ignitionStage.get() == 1 ? "Scanning..." : "Activating..."));
    }

    @Override
    public boolean shouldDisplayHudText(World world, BlockPos pos, PlayerEntity player) {
        return ignitionStage.get() > 0;
    }
}
