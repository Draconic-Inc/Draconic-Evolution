package com.brandon3055.draconicevolution.blocks.tileentity;

import com.brandon3055.brandonscore.blocks.TileInventoryBase;
import com.brandon3055.brandonscore.lib.ChatHelper;
import com.brandon3055.brandonscore.lib.PairKV;
import com.brandon3055.brandonscore.lib.Vec3D;
import com.brandon3055.brandonscore.lib.Vec3I;
import com.brandon3055.brandonscore.lib.datamanager.ManagedBool;
import com.brandon3055.brandonscore.lib.datamanager.ManagedByte;
import com.brandon3055.brandonscore.lib.datamanager.ManagedEnum;
import com.brandon3055.brandonscore.lib.datamanager.ManagedVec3I;
import com.brandon3055.brandonscore.utils.FacingUtils;
import com.brandon3055.brandonscore.utils.InventoryUtils;
import com.brandon3055.brandonscore.utils.Teleporter;
import com.brandon3055.brandonscore.utils.Utils;
import com.brandon3055.draconicevolution.DEFeatures;
import com.brandon3055.draconicevolution.DraconicEvolution;
import com.brandon3055.draconicevolution.api.ICrystalLink;
import com.brandon3055.draconicevolution.api.IENetEffectTile;
import com.brandon3055.draconicevolution.api.ITeleportEndPoint;
import com.brandon3055.draconicevolution.blocks.DislocatorReceptacle;
import com.brandon3055.draconicevolution.blocks.Portal;
import com.brandon3055.draconicevolution.blocks.energynet.rendering.ENetFXHandler;
import com.brandon3055.draconicevolution.blocks.energynet.rendering.ENetFXHandlerClient;
import com.brandon3055.draconicevolution.blocks.energynet.rendering.ENetFXHandlerServer;
import com.brandon3055.draconicevolution.client.render.effect.CrystalGLFXBase;
import com.brandon3055.draconicevolution.handlers.DEEventHandler;
import com.brandon3055.draconicevolution.handlers.DislocatorLinkHandler;
import com.brandon3055.draconicevolution.integration.funkylocomotion.IMovableStructure;
import com.brandon3055.draconicevolution.items.tools.Dislocator;
import com.brandon3055.draconicevolution.lib.DESoundHandler;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumFacing.Axis;
import net.minecraft.util.ITickable;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.function.Consumer;

import static com.brandon3055.draconicevolution.DEFeatures.dislocatorBound;

/**
 * Created by brandon3055 on 16/07/2016.
 */
public class TileDislocatorReceptacle extends TileInventoryBase implements ITickable, ITeleportEndPoint, IMovableStructure, ICrystalLink, IENetEffectTile {

    //used to update existing portals to the new offset based portal positions
    //TODO change these names to lowercase in 1.13 (this is a breaking change)
    public final ManagedBool NEW_OFFSETS = register("NEW_OFFSETS", new ManagedBool(false)).saveToTile().finish();
    public final ManagedBool ACTIVE = register("ACTIVE", new ManagedBool(false)).saveToTile().syncViaTile().trigerUpdate().finish();
    public final ManagedBool CAMO = register("CAMO", new ManagedBool(false)).saveToTile().syncViaTile().trigerUpdate().finish();
    public final ManagedBool LT_REDSTONE = register("LT_REDSTONE", new ManagedBool(false)).saveToTile().syncViaTile().trigerUpdate().finish();
    public final ManagedVec3I SPAWN_POS = register("SPAWN_POS", new ManagedVec3I(new Vec3I(0, -999, 0))).saveToTile().syncViaTile().finish();
    public final ManagedEnum<Axis> ACTIVE_AXIS = register("ACTIVE_AXIS", new ManagedEnum<>(Axis.X)).syncViaTile().saveToTile().finish();
    public final ManagedBool IS_BOUND = register("IS_BOUND", new ManagedBool(false)).saveToTile().syncViaTile().finish();
    public final ManagedVec3I LINKED_CRYSTAL = register("CRYSTAL_POS", new ManagedVec3I(new Vec3I(0, -999, 0))).saveToTile().syncViaTile().finish();
    public final ManagedByte REMOTE_CRYSTAL_TIER = register("CRYSTAL_POS_TIER", new ManagedByte(0)).saveToTile().syncViaTile().finish();
    public final ManagedByte LINKED_FLOW_RATE = register("LINKED_FLOW_RATE", new ManagedByte(0)).syncViaTile().finish();
    public final ManagedVec3I CRYSTAL_LINK_POS = register("CRYSTAL_LINK_POS", new ManagedVec3I(new Vec3I(0, -999, 0))).saveToTile().syncViaTile().finish();

    public boolean igniting = false;
    public boolean frameMoving = false;
    private List<Entity> teleportQ = new ArrayList<Entity>();
    private Map<Integer, Integer> coolDownMap = new HashMap<>();
    private Map<Integer, Integer> arrivalsMap = new HashMap<>();

    public TileDislocatorReceptacle() {
        setInventorySize(1);
        setShouldRefreshOnBlockChange();
        fxHandler = DraconicEvolution.proxy.createENetFXHandler(this);
    }

    @Override
    public void update() {
        super.update();
        fxHandler.update();

        boolean boundCrystals = ACTIVE.value && IS_BOUND.value && LINKED_CRYSTAL.vec.y != -999;
        if (world.isRemote && boundCrystals && REMOTE_CRYSTAL_TIER.detectChanges()) {
            fxHandler.reloadConnections();
        }

        if (!world.isRemote && boundCrystals) {
            if (DEEventHandler.serverTicks % 10 == 0) {
                TileEntity remoteTile = getRemoteReceptacle();
                ICrystalLink remote = getRemoteCrystal();
                if (remoteTile != null && remote instanceof IENetEffectTile) {
                    int i = remote.getLinks().indexOf(remoteTile.getPos());
                    List<Byte> rates = ((IENetEffectTile) remote).getFlowRates();
                    if (i >= 0 && i < rates.size()) {
                        LINKED_FLOW_RATE.value = rates.get(i);
                    }
                    else {
                        LINKED_FLOW_RATE.value = 0;
                    }
                }
                else {
                    LINKED_FLOW_RATE.value = 0;
                }
            }
            if (LINKED_FLOW_RATE.value != 0 && DEEventHandler.serverTicks % 100 == 0) {
                dataManager.forceSync(LINKED_FLOW_RATE);
            }
        }
        else if (!world.isRemote){
            LINKED_FLOW_RATE.value = 0;
        }

        if (!NEW_OFFSETS.value && ACTIVE.value) {
            deactivate();
            attemptIgnition();
            NEW_OFFSETS.value = true;
        }

        if (frameMoving) {
            if (ACTIVE.value) {
                finishMove(pos, new HashSet<>());
            }
            frameMoving = false;
            checkIn();
        }

        for (Entity entity : teleportQ) {
            ItemStack stack = getStackInSlot(0);

            if (!(stack.getItem() instanceof Dislocator)) {
                deactivate();
                return;
            }

            Teleporter.TeleportLocation location = ((Dislocator) stack.getItem()).getLocation(stack, world);
            if (dislocatorBound.isValid(stack) && location != null) {
                location.setYaw(entity.rotationYaw);
                location.setPitch(entity.rotationPitch);
            }


            if (location == null) {
                if (!dislocatorBound.isValid(stack)) {
                    deactivate();
                }
                else {
                    if (dislocatorBound.isPlayer(stack)) {
                        ChatHelper.translate(entity, "info.de.bound_dislocator.cant_find_player", TextFormatting.RED);
                    }
                    else {
                        ChatHelper.translate(entity, "info.de.bound_dislocator.cant_find_target", TextFormatting.RED);
                    }
                }
                return;
            }

            dislocatorBound.notifyArriving(stack, world, entity);
            DESoundHandler.playSoundFromServer(entity.world, entity.posX, entity.posY, entity.posZ, DESoundHandler.portal, SoundCategory.PLAYERS, 0.1F, entity.world.rand.nextFloat() * 0.1F + 0.9F, false, 32);
            location.teleport(entity);
            DESoundHandler.playSoundFromServer(entity.world, entity.posX, entity.posY, entity.posZ, DESoundHandler.portal, SoundCategory.PLAYERS, 0.1F, entity.world.rand.nextFloat() * 0.1F + 0.9F, false, 32);
        }

        try {
            List<Integer> toRemove = new ArrayList<>();

            for (Integer key : coolDownMap.keySet()) {
                if (coolDownMap.get(key) > 0) {
                    coolDownMap.put(key, coolDownMap.get(key) - 1);
                }
                else {
                    toRemove.add(key);
                }
            }

            for (Integer i : toRemove) {
                coolDownMap.remove(i);
            }
            toRemove.clear();

            toRemove = new ArrayList<>();

            for (Integer key : arrivalsMap.keySet()) {
                if (arrivalsMap.get(key) > 0) {
                    arrivalsMap.put(key, arrivalsMap.get(key) - 1);
                }
                else {
                    toRemove.add(key);
                }
            }

            for (Integer i : toRemove) {
                arrivalsMap.remove(i);
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
        return true;
    }

    @Override
    public void setInventorySlotContents(int index, ItemStack stack) {
        super.setInventorySlotContents(index, stack);

        ItemStack prev = getStackInSlot(0);
        if (dislocatorBound.isValid(prev) && !dislocatorBound.isPlayer(prev)) {
            DislocatorLinkHandler.removeLink(world, stack);
        }

        IS_BOUND.value = false;
        if (getStackInSlot(0).isEmpty() && ACTIVE.value) {
            deactivate();
        }
        else if (!getStackInSlot(0).isEmpty()) {
            attemptIgnition();
            checkIn();
        }
    }

    private void checkIn() {
        ItemStack stack = getStackInSlot(0);
        if (dislocatorBound.isValid(stack) && !dislocatorBound.isPlayer(stack)) {
            DislocatorLinkHandler.updateLink(world, stack, pos, world.provider.getDimension());
            IS_BOUND.value = true;
        }
        else {
            IS_BOUND.value = false;
        }
    }

    @Override
    public boolean isItemValidForSlot(int index, ItemStack stack) {
        return stack.getItem() instanceof Dislocator;
    }

    //endregion

    //region Teleport Handling

    public void handleEntityTeleport(Entity entity) {
        if (world.isRemote || teleportQ.contains(entity) || coolDownMap.containsKey(entity.getEntityId())) {
            return;
        }

        if (arrivalsMap.containsKey(entity.getEntityId())) {
            arrivalsMap.put(entity.getEntityId(), 10);
            return;
        }

        coolDownMap.put(entity.getEntityId(), 10);
        teleportQ.add(entity);
    }

    //endregion

    //region MultiBlock

    public void deactivate() {
        if (!world.isRemote) {
            ACTIVE.value = false;
        }

        IBlockState state = world.getBlockState(pos);
        if (state.getBlock() == DEFeatures.dislocatorReceptacle) {
            world.setBlockState(pos, state.withProperty(DislocatorReceptacle.ACTIVE, false));
        }

        for (BlockPos checkPos : BlockPos.getAllInBox(pos.add(-1, -1, -1), pos.add(1, 1, 1))) {
            TileEntity tile = world.getTileEntity(checkPos);
            if (tile instanceof TilePortal && ((TilePortal) tile).getMasterPos().equals(pos)) {
                world.setBlockToAir(tile.getPos());
            }
        }
        updateBlock();
    }

    public boolean attemptIgnition() {
        NEW_OFFSETS.value = true;
        ItemStack stack = getStackInSlot(0);

        if (!(stack.getItem() instanceof Dislocator) || ((Dislocator) stack.getItem()).getLocation(stack, world) == null) {
            return false;
        }

        PairKV<Axis, List<BlockPos>> portalConfiguration = scanConfigurations();
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
            ACTIVE_AXIS.value = portalConfiguration.getKey();

            IBlockState state = world.getBlockState(pos);
            if (state.getBlock() == DEFeatures.dislocatorReceptacle) {
                world.setBlockState(pos, state.withProperty(DislocatorReceptacle.ACTIVE, true));
            }

            updateBlock();
            igniting = false;

            if (dislocatorBound.isValid(stack) && !dislocatorBound.isPlayer(stack)) {
                updateSpawnBlock(portalConfiguration.getValue());
                updateLinkBlock(portalConfiguration.getValue());
            }

            return true;
        }

        return false;
    }

    private void updateSpawnBlock(List<BlockPos> rawBlocks) {
        Map<Integer, List<BlockPos>> levelMap = new HashMap<>();
        rawBlocks.forEach(block -> levelMap.computeIfAbsent(block.getY(), integer -> new ArrayList<>()).add(block));
        LinkedList<Integer> levels = new LinkedList<>(levelMap.keySet());
        levels.sort(Comparator.naturalOrder());

        List<BlockPos> foundValid = new ArrayList<>();
        for (int level : levels) {
            List<BlockPos> blocks = levelMap.get(level);
            for (BlockPos pos : blocks) {
                if (world.isAirBlock(pos.up()) || world.getBlockState(pos.up()).getBlock() == DEFeatures.portal) {
                    foundValid.add(pos);
                }
            }
            if (!foundValid.isEmpty()) {
                break;
            }
        }

        if (foundValid.isEmpty()) {
            SPAWN_POS.vec = new Vec3I(0, -999, 0);
            return;
        }

        int maxX = Integer.MIN_VALUE;
        int maxY = Integer.MIN_VALUE;
        int maxZ = Integer.MIN_VALUE;
        int minX = Integer.MAX_VALUE;
        int minY = Integer.MAX_VALUE;
        int minZ = Integer.MAX_VALUE;

        for (BlockPos pos : foundValid) {
            if (pos.getX() > maxX) maxX = pos.getX();
            if (pos.getY() > maxY) maxY = pos.getY();
            if (pos.getZ() > maxZ) maxZ = pos.getZ();
            if (pos.getX() < minX) minX = pos.getX();
            if (pos.getY() < minY) minY = pos.getY();
            if (pos.getZ() < minZ) minZ = pos.getZ();
        }

        Vec3D center = new Vec3D((minX + ((maxX - minX) / 2)) + 0.5, (minY + ((maxY - minY) / 2)) + 0.5, (minZ + ((maxZ - minZ) / 2)) + 0.5);
//        BCEffectHandler.spawnFX(DEParticles.LINE_INDICATOR, world, center, new Vec3D(), 255, 0, 0, 10000);
        BlockPos closestPos = foundValid.get(0);
        double closest = 10000000;

        for (BlockPos pos : foundValid) {
            double dist = Utils.getDistanceSq(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, center.x, center.y, center.z);
            if (dist < closest) {
                closest = dist;
                closestPos = pos;
            }
        }

        setSpawnPos(closestPos);
    }

    private void updateLinkBlock(List<BlockPos> rawBlocks) {
        if (rawBlocks.isEmpty()) {
            CRYSTAL_LINK_POS.vec = new Vec3I(0, -999, 0);
            return;
        }

        int maxX = Integer.MIN_VALUE;
        int maxY = Integer.MIN_VALUE;
        int maxZ = Integer.MIN_VALUE;
        int minX = Integer.MAX_VALUE;
        int minY = Integer.MAX_VALUE;
        int minZ = Integer.MAX_VALUE;

        for (BlockPos pos : rawBlocks) {
            if (pos.getX() > maxX) maxX = pos.getX();
            if (pos.getY() > maxY) maxY = pos.getY();
            if (pos.getZ() > maxZ) maxZ = pos.getZ();
            if (pos.getX() < minX) minX = pos.getX();
            if (pos.getY() < minY) minY = pos.getY();
            if (pos.getZ() < minZ) minZ = pos.getZ();
        }

        Vec3D center = new Vec3D((minX + ((maxX - minX) / 2)) + 0.5, (minY + ((maxY - minY) / 2)) + 0.5, (minZ + ((maxZ - minZ) / 2)) + 0.5);
//        for (int i = 0; i < 1000; i++)
//        BCEffectHandler.spawnFX(DEParticles.LINE_INDICATOR, world, center, new Vec3D(), 0, 255, 255, 10000);

        BlockPos closestPos = rawBlocks.get(0);
        double closest = 10000000;

        for (BlockPos pos : rawBlocks) {
            double dist = Utils.getDistanceSq(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, center.x, center.y, center.z);
            if (dist < closest) {
                closest = dist;
                closestPos = pos;
            }
        }

        setLinkPos(closestPos);
    }

    private PairKV<Axis, List<BlockPos>> scanConfigurations() {
        List<BlockPos> scanned = new ArrayList<BlockPos>();
        for (BlockPos offset : FacingUtils.AROUND_X) {
            List<BlockPos> portalBlocks = scanFromOrigin(pos.add(offset), Axis.X, scanned);
            if (portalBlocks != null) {
                return new PairKV<Axis, List<BlockPos>>(Axis.X, portalBlocks);
            }
        }

        scanned = new ArrayList<BlockPos>();
        for (BlockPos offset : FacingUtils.AROUND_Y) {
            List<BlockPos> portalBlocks = scanFromOrigin(pos.add(offset), Axis.Y, scanned);
            if (portalBlocks != null) {
                return new PairKV<Axis, List<BlockPos>>(Axis.Y, portalBlocks);
            }
        }

        scanned = new ArrayList<BlockPos>();
        for (BlockPos offset : FacingUtils.AROUND_Z) {
            List<BlockPos> portalBlocks = scanFromOrigin(pos.add(offset), Axis.Z, scanned);
            if (portalBlocks != null) {
                return new PairKV<Axis, List<BlockPos>>(Axis.Z, portalBlocks);
            }
        }

        return null;
    }

    private List<BlockPos> scanFromOrigin(BlockPos scanOrigin, Axis scanAxis, List<BlockPos> alreadyScanned) {
        if (!world.isAirBlock(scanOrigin) || alreadyScanned.contains(scanOrigin)) {
            return null;
        }

        List<BlockPos> scannedBlocks = new ArrayList<BlockPos>();
        if (scanPortal(scanOrigin, scanOrigin, scanAxis, scannedBlocks, alreadyScanned)) {
            return scannedBlocks;
        }

        return null;
    }

    private boolean scanPortal(BlockPos scanPos, BlockPos origin, Axis scanAxis, List<BlockPos> scanList, List<BlockPos> blackList) {
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

    //region Bound portal code

    @Override
    public BlockPos getArrivalPos(String linkID) {
        if (!dislocatorBound.isValid(getStackInSlot(0)) || !dislocatorBound.getLinkID(getStackInSlot(0)).equals(linkID)) {
            return null;
        }

        return SPAWN_POS.vec.y == -999 ? null : getSpawnPos();
    }

    @Override
    public void entityArriving(Entity entity) {
        Entity rootEntity = entity.getLowestRidingEntity();
        PassengerHelper passengerHelper = new PassengerHelper(rootEntity);
        passengerHelper.forEach(e -> arrivalsMap.put(e.getEntityId(), 10));
    }

    public void setSpawnPos(BlockPos spawnPos) {
        SPAWN_POS.vec.set(pos.subtract(spawnPos));
    }

    protected BlockPos getSpawnPos() {
        return pos.subtract(SPAWN_POS.vec.getPos());
    }

    public void setLinkPos(BlockPos spawnPos) {
        CRYSTAL_LINK_POS.vec.set(pos.subtract(spawnPos));
    }

    protected BlockPos getLinkPos() {
        return pos.subtract(CRYSTAL_LINK_POS.vec.getPos());
    }

    private static class PassengerHelper {
        public Entity entity;
        public LinkedList<PassengerHelper> passengers = new LinkedList<>();

        public PassengerHelper(Entity entity) {
            this.entity = entity;
            for (Entity passenger : entity.getPassengers()) {
                passengers.add(new PassengerHelper(passenger));
            }
        }

        public void forEach(Consumer<Entity> action) {
            action.accept(entity);
            passengers.forEach(helper -> helper.forEach(action));
        }
    }

    //endregion

    //region Frame Movement

    @Override
    public Iterable<BlockPos> getBlocksForFrameMove() {
        if (ACTIVE.value) {
            for (BlockPos offset : FacingUtils.getAroundAxis(ACTIVE_AXIS.value)) {
                BlockPos next = pos.add(offset);
                if (world.getBlockState(next).getBlock() == DEFeatures.portal) {
                    HashSet<BlockPos> blocks = new HashSet<>();
                    findActiveBlocksOnAxis(ACTIVE_AXIS.value, next, blocks);
                    return blocks;
                }
            }
        }
        else {
            PairKV<Axis, List<BlockPos>> config = scanConfigurations();
            if (config != null) {
                HashSet<BlockPos> blocks = new HashSet<>();
                for (BlockPos ppos : config.getValue()) {
                    for (BlockPos offset : FacingUtils.getAroundAxis(config.getKey())) {
                        BlockPos next = ppos.add(offset);
                        if (blocks.contains(next)) continue;
                        if (world.getBlockState(next).getBlock() == DEFeatures.infusedObsidian) {
                            blocks.add(next);
                        }
                    }
                }
                return blocks;
            }
        }
        return Collections.emptyList();
    }

    public void findActiveBlocksOnAxis(Axis axis, BlockPos pos, HashSet<BlockPos> blocks) {
        blocks.add(pos);
        for (BlockPos offset : FacingUtils.getAroundAxis(axis)) {
            BlockPos next = pos.add(offset);
            if (blocks.contains(next)) continue;

            IBlockState state = world.getBlockState(next);
            if (state.getBlock() == DEFeatures.portal) {
                findActiveBlocksOnAxis(axis, next, blocks);
            }
            else if (state.getBlock() == DEFeatures.infusedObsidian) {
                blocks.add(next);
            }
        }
    }

    public void finishMove(BlockPos pos, HashSet<BlockPos> blocks) {
        for (EnumFacing facing : FacingUtils.getFacingsAroundAxis(ACTIVE_AXIS.value)) {
            BlockPos np = pos.offset(facing);
            if (blocks.contains(np)) continue;
            IBlockState state = world.getBlockState(np);

            if (state.getBlock() == DEFeatures.portal) {
                TileEntity tile = world.getTileEntity(np);
                if (tile instanceof TilePortal) {
                    ((TilePortal) tile).frameMoving = false;
                }
                blocks.add(np);
                finishMove(np, blocks);
            }
        }
    }

    //endregion

    //region F!@#$%^ Lasers through portals code for Morph

    protected void setCrystalPos(BlockPos crystalPos) {
        LINKED_CRYSTAL.vec.set(pos.subtract(crystalPos));
    }

    protected BlockPos getCrystalPos() {
        return pos.subtract(LINKED_CRYSTAL.vec.getPos());
    }

    private BlockPos remotePosCache = null;
    private int remoteDimCache = 0;
    private int invalidLinkTime = 0;
    protected ENetFXHandler fxHandler;

    private TileDislocatorReceptacle getRemoteReceptacle() {
        return getRemoteReceptacle(false);
    }

    private TileDislocatorReceptacle getRemoteReceptacle(boolean skipRemoteCheck) {
        if (!IS_BOUND.value || !ACTIVE.value) return null;

         if (invalidLinkTime > 0) {
              invalidLinkTime--;
             return null;
         }

        if (remotePosCache == null) {
            ItemStack stack = getStackInSlot(0);
            if (dislocatorBound.isValid(stack) && !dislocatorBound.isPlayer(stack)) {
                TileEntity tile = DislocatorLinkHandler.getTargetTile(world, stack);
                if (tile instanceof TileDislocatorReceptacle) {
                    remotePosCache = tile.getPos();
                    remoteDimCache = tile.getWorld().provider.getDimension();
                }
                else {
                    invalidLinkTime = 100;
                    return null;
                }
            }
            else {
                return null;
            }
        }

        MinecraftServer server = world.getMinecraftServer();
        if (server != null) {
            TileEntity tile = server.getWorld(remoteDimCache).getTileEntity(remotePosCache);
            if (tile instanceof TileDislocatorReceptacle) {
                if (skipRemoteCheck) {
                    return (TileDislocatorReceptacle) tile;
                }
                if (((TileDislocatorReceptacle) tile).ACTIVE.value && ((TileDislocatorReceptacle) tile).getRemoteReceptacle(true) == this) {
                    return (TileDislocatorReceptacle) tile;
                }
            }

            remotePosCache = null;
            return null;
        }
        return null;
    }

    private ICrystalLink getRemoteCrystal() {
        TileDislocatorReceptacle tile = getRemoteReceptacle();
        if (tile != null) {
            MinecraftServer server = world.getMinecraftServer();
            if (server != null && tile.LINKED_CRYSTAL.vec.y != -999) {
                TileEntity crystal = server.getWorld(remoteDimCache).getTileEntity(tile.getCrystalPos());
                if (crystal instanceof IENetEffectTile) {
                    REMOTE_CRYSTAL_TIER.value = (byte) ((IENetEffectTile) crystal).getTier();
                    return (ICrystalLink) crystal;
                }
                return null;
            }
        }
        return null;
    }

    @Nonnull
    @Override
    public List<BlockPos> getLinks() {
        if (LINKED_CRYSTAL.vec.y != -999) {
            return Collections.singletonList(getCrystalPos());
        }
        return Collections.emptyList();
    }

    @Override
    public boolean binderUsed(EntityPlayer player, BlockPos linkTarget, EnumFacing sideClicked) {
        return false;
    }

    @Override
    public boolean createLink(ICrystalLink otherCrystal) {
        setCrystalPos(((TileEntity) otherCrystal).getPos());
        return true;
    }

    @Override
    public void breakLink(BlockPos otherCrystal) {
        LINKED_CRYSTAL.vec = new Vec3I(0, -999, 0);
    }

    @Override
    public int balanceMode() {
        ICrystalLink remote = getRemoteCrystal();
        return remote != null ? remote.balanceMode() : 1;
    }

    @Override
    public int maxLinks() {
        return 1;
    }

    @Override
    public int maxLinkRange() {
        return 32;
    }

    @Override
    public int getEnergyStored() {
        ICrystalLink remote = getRemoteCrystal();
        return remote != null ? remote.getEnergyStored() : 0;
    }

    @Override
    public int getMaxEnergyStored() {
        ICrystalLink remote = getRemoteCrystal();
        return remote != null ? remote.getMaxEnergyStored() : 0;
    }

    @Override
    public void modifyEnergyStored(int energy) {
        ICrystalLink remote = getRemoteCrystal();
        if (remote != null) {
            remote.modifyEnergyStored(energy);
        }
    }

    @Override
    public Vec3D getBeamLinkPos(BlockPos linkTo) {
        double dist = FacingUtils.destanceInDirection(pos, linkTo, FacingUtils.getAxisFaces(ACTIVE_AXIS.value)[0]);
        Vec3D vec = Vec3D.getCenter(getLinkPos());

        EnumFacing facing;
        if (dist > 0) {
            facing = FacingUtils.getAxisFaces(ACTIVE_AXIS.value)[0];
        }
        else {
            facing = FacingUtils.getAxisFaces(ACTIVE_AXIS.value)[1];
        }

        vec.add(facing.getFrontOffsetX() * 0.35, facing.getFrontOffsetY() * 0.35, facing.getFrontOffsetZ() * 0.35);

        return vec;
    }

    //INetEffectTile

    @Override
    public boolean renderBeamTermination() {
        return true;
    }

    @Override
    public ENetFXHandler createServerFXHandler() {
        return new ENetFXHandlerServer(this);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public ENetFXHandler createClientFXHandler() {
        return new ENetFXHandlerClient(this);
    }

    @Override
    public boolean hasStaticFX() {
        return false;
    }

    @Override
    public CrystalGLFXBase createStaticFX() {
        return null;
    }

    @Override
    public LinkedList<Byte> getFlowRates() {
        return new LinkedList<>(Collections.singletonList(LINKED_FLOW_RATE.value));
    }

    @Override
    public int getTier() {
        return REMOTE_CRYSTAL_TIER.value;
    }

    boolean hashCached = false;
    int hashID = 0;

    @Override
    public int getIDHash() {
        if (!hashCached) {
            hashID = pos.hashCode();
            hashCached = true;
        }
        return hashID;
    }

    //endregion
}
