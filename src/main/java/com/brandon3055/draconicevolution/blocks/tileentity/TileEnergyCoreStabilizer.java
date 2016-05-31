package com.brandon3055.draconicevolution.blocks.tileentity;

import com.brandon3055.brandonscore.api.IMultiBlock;
import com.brandon3055.brandonscore.blocks.TileBCBase;
import com.brandon3055.brandonscore.client.particle.BCEffectHandler;
import com.brandon3055.brandonscore.lib.Vec3D;
import com.brandon3055.brandonscore.lib.Vec3I;
import com.brandon3055.brandonscore.network.wrappers.SyncableBool;
import com.brandon3055.brandonscore.network.wrappers.SyncableVec3I;
import com.brandon3055.brandonscore.utils.FacingUtils;
import com.brandon3055.draconicevolution.DEFeatures;
import com.brandon3055.draconicevolution.blocks.ParticleGenerator;
import com.brandon3055.draconicevolution.client.DEParticles;
import com.brandon3055.draconicevolution.client.handler.ClientEventHandler;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.LinkedList;

/**
 *  Created by brandon3055 on 30/3/2016.
 */
public class TileEnergyCoreStabilizer extends TileBCBase implements ITickable, IMultiBlock {

    public final SyncableVec3I coreOffset = new SyncableVec3I(new Vec3I(0, -1, 0), true, false, false);
    public final SyncableBool hasCoreLock = new SyncableBool(false, true, false, false);
    public final SyncableBool isCoreActive = new SyncableBool(false, true, false, false);
    public final SyncableBool isValidMultiBlock = new SyncableBool(false, true, false, true);
    public EnumFacing.Axis multiBlockAxis = EnumFacing.Axis.Y;
    public EnumFacing coreDirection = EnumFacing.DOWN;
    public float rotation = 0;
    public float rotationSpeed = 0;

    public TileEnergyCoreStabilizer(){
        registerSyncableObject(coreOffset, true);
        registerSyncableObject(hasCoreLock, true);
        registerSyncableObject(isValidMultiBlock, true);
        registerSyncableObject(isCoreActive, true);
    }

    //region Beam

    @Override
    public void update() {
        detectAndSendChanges();
        if (worldObj.isRemote && hasCoreLock.value && isCoreActive.value){
            rotation = ClientEventHandler.elapsedTicks;
            updateVisual();
           if (isValidMultiBlock.value){
               updateVisual();
           }
        }
    }

    //TODO SERVER TEST!!!!!!
    @SideOnly(Side.CLIENT)
    private void updateVisual() {
        Vec3D spawn = new Vec3D(pos);
        spawn.add(0.5, 0.5, 0.5);
        double rand = worldObj.rand.nextInt(100) / 12D;
        double randOffset = rand * (Math.PI * 2D);
        double offsetX = Math.sin((ClientEventHandler.elapsedTicks / 180D * Math.PI) + randOffset);
        double offsetY = Math.cos((ClientEventHandler.elapsedTicks / 180D * Math.PI) + randOffset);

        if (!isValidMultiBlock.value || worldObj.rand.nextBoolean()) {
            double d = isValidMultiBlock.value ? 1.1 : 0.25;
            double inset = isValidMultiBlock.value ? 1 : 0;
            if (coreDirection.getAxis() == EnumFacing.Axis.Z){
                spawn.add(offsetX * d, offsetY * d, (worldObj.rand.nextBoolean() ? -0.38 : 0.38) * inset);
            }
            else if (coreDirection.getAxis() == EnumFacing.Axis.Y){
                spawn.add(offsetX * d, (worldObj.rand.nextBoolean() ? -0.38 : 0.38) * inset, offsetY * d);
            }
            else if (coreDirection.getAxis() == EnumFacing.Axis.X){
                spawn.add((worldObj.rand.nextBoolean() ? -0.38 : 0.38) * inset, offsetY * d, offsetX * d);
            }
            BCEffectHandler.spawnFX(DEParticles.ENERGY_CORE_FX, worldObj, spawn, new Vec3D(pos).subtract(coreOffset.vec.getPos()).add(0.5, 0.5, 0.5), 1, (int)(randOffset * 100D), isValidMultiBlock.value ? 1 : 0);
        }
        else {
            if (coreDirection.getAxis() == EnumFacing.Axis.Z){
                spawn.add(offsetX * 1.2, offsetY * 1.2, worldObj.rand.nextBoolean() ? -0.38 : 0.38);
            }
            else if (coreDirection.getAxis() == EnumFacing.Axis.Y){
                spawn.add(offsetX * 1.2, worldObj.rand.nextBoolean() ? -0.38 : 0.38, offsetY * 1.2);
            }
            else if (coreDirection.getAxis() == EnumFacing.Axis.X){
                spawn.add(worldObj.rand.nextBoolean() ? -0.38 : 0.38, offsetY * 1.2, offsetX * 1.2);
            }
            BCEffectHandler.spawnFX(DEParticles.ENERGY_CORE_FX, worldObj, spawn, new Vec3D(pos).add(0.5, 0.5, 0.5), 0);
        }
    }

    //endregion

    //region Activation

    public void onTileClicked(World world, BlockPos pos, IBlockState state, EntityPlayer player){
        if (worldObj.isRemote) return;

        TileEnergyStorageCore core = getCore();
        if (core == null){
            core = findCore();
        }

        if (core != null){
            core.onStructureClicked(world, pos, state, player);
        }
        else {
            player.addChatComponentMessage(new TextComponentTranslation("msg.de.coreNotFound.txt").setStyle(new Style().setColor(TextFormatting.DARK_RED)));
        }
    }

    public boolean isStabilizerValid(int coreTier, TileEnergyStorageCore core){
        if (coreTier < 5 && !isValidMultiBlock.value){
            return true;
        }
        else if (coreTier >= 5 && isValidMultiBlock.value){
            BlockPos offset = pos.subtract(core.getPos());
            EnumFacing direction = EnumFacing.getFacingFromVector(offset.getX(), offset.getY(), offset.getZ()).getOpposite();
            return direction.getAxis() == multiBlockAxis;
        }
        return false;
    }

    //endregion

    //region MultiBlock

    public void onPlaced(){
        if (worldObj.isRemote || checkAndFormMultiBlock()){
            return;
        }

        for (EnumFacing facing1 : EnumFacing.VALUES) {
            TileEntity stabilizer = worldObj.getTileEntity(pos.add(facing1.getFrontOffsetX(), facing1.getFrontOffsetY(), facing1.getFrontOffsetZ()));

            if (stabilizer instanceof TileEnergyCoreStabilizer && ((TileEnergyCoreStabilizer)stabilizer).checkAndFormMultiBlock()){
                return;
            }

            for (EnumFacing facing2 : EnumFacing.VALUES){
                if (facing2 != facing1 && facing2 != facing1.getOpposite()){
                    stabilizer = worldObj.getTileEntity(pos.add(facing1.getFrontOffsetX(), facing1.getFrontOffsetY(), facing1.getFrontOffsetZ()));

                    if (stabilizer instanceof TileEnergyCoreStabilizer && ((TileEnergyCoreStabilizer)stabilizer).checkAndFormMultiBlock()) {
                        return;
                    }
                }
            }
        }
    }

    private boolean checkAxisValid(EnumFacing.Axis axis){
        for (BlockPos offset : FacingUtils.getAroundAxis(axis)){
            if (!isAvailable(pos.add(offset))){
                return false;
            }
        }

        return true;
    }

    /**
     * Checks if this block is at the center of a valid multiblock and if so activates the structure.
     * @return true if structure was activated.
     * */
    private boolean checkAndFormMultiBlock(){
        if (hasCoreLock.value && getCore() != null && getCore().active.value){
            return false;
        }

        for (EnumFacing.Axis axis : EnumFacing.Axis.values()){
            if (checkAxisValid(axis)){
                buildMultiBlock(axis);
                return true;
            }
        }

        return false;
    }

    /**
     * @return true if there is a stabilizer at the given pos and it is available for use in a structure.
     * If structure is already formed will check if the block is an invisible tile with this as its master
     * In the case of the structure already formed this should be called from the controller.
     * */
    private boolean isAvailable(BlockPos pos){
        if (isValidMultiBlock.value) {
            TileEntity tile = worldObj.getTileEntity(pos);
            return tile instanceof TileInvisECoreBlock && ((TileInvisECoreBlock)tile).getController() == this;
        }

        TileEntity stabilizer = worldObj.getTileEntity(pos);
        return stabilizer instanceof TileEnergyCoreStabilizer && (!((TileEnergyCoreStabilizer)stabilizer).hasCoreLock.value || ((TileEnergyCoreStabilizer)stabilizer).getCore() == null || !((TileEnergyCoreStabilizer)stabilizer).getCore().active.value);
    }

    private void buildMultiBlock(EnumFacing.Axis axis ){
        worldObj.setBlockState(pos, worldObj.getBlockState(pos).withProperty(ParticleGenerator.TYPE, "stabilizer2"));

        for (BlockPos offset : FacingUtils.getAroundAxis(axis)) {
            worldObj.setBlockState(pos.add(offset), DEFeatures.invisECoreBlock.getDefaultState());
            TileEntity tile = worldObj.getTileEntity(pos.add(offset));

            if (tile instanceof TileInvisECoreBlock){
                ((TileInvisECoreBlock)tile).blockName = "draconicevolution:particleGenerator";
                ((TileInvisECoreBlock)tile).setController(this);
            }
        }

        isValidMultiBlock.value = true;
        multiBlockAxis = axis;
    }

    public void deFormStructure(){
        isValidMultiBlock.value = false;
        if (worldObj.getBlockState(pos).getBlock() == DEFeatures.particleGenerator){
            worldObj.setBlockState(pos, DEFeatures.particleGenerator.getDefaultState().withProperty(ParticleGenerator.TYPE, "stabilizer"));
        }

        if (getCore() != null){
            getCore().deactivateCore();
        }

        for (BlockPos offset : FacingUtils.getAroundAxis(multiBlockAxis)) {
            TileEntity tile = worldObj.getTileEntity(pos.add(offset));
            if (tile instanceof TileInvisECoreBlock){
                ((TileInvisECoreBlock)tile).revert();
            }
        }
    }

    @Override
    public boolean validateStructure(){
        if (checkAxisValid(multiBlockAxis)){
            return true;
        }

        deFormStructure();

        return false;
    }

    //region Unused IMultiBlock

    @Override
    public boolean isStructureValid() {
        return isValidMultiBlock.value;
    }

    @Override
    public boolean isController() {
        return true;
    }

    @Override
    public boolean hasSatelliteStructures() {
        return false;
    }

    @Override
    public IMultiBlock getController() {
        return this;
    }

    @Override
    public LinkedList<IMultiBlock> getSatelliteControllers() {
        return null;
    }

    //endregion

    //endregion

    //region Getters & Setters

    public TileEnergyStorageCore findCore(){
        if (getCore() != null) {
            return getCore();
        }

        for (EnumFacing facing : EnumFacing.VALUES) {
            for (int i = 0; i < 16; i++){
                TileEntity tile = worldObj.getTileEntity(pos.add(facing.getFrontOffsetX() * i, facing.getFrontOffsetY() * i, facing.getFrontOffsetZ() * i));
                if (tile instanceof TileEnergyStorageCore){
                    TileEnergyStorageCore core = (TileEnergyStorageCore) tile;
                    core.validateStructure();
                    if (core.active.value){
                        continue;
                    }
                    return core;
                }
            }
        }

        return null;
    }

    public TileEnergyStorageCore getCore(){
        if (hasCoreLock.value){
            TileEntity tile = worldObj.getTileEntity(getCorePos());
            if (tile instanceof TileEnergyStorageCore){
                return (TileEnergyStorageCore)tile;
            }
            else {
                hasCoreLock.value = false;
            }
        }
        return null;
    }

    private BlockPos getCorePos(){
        return pos.subtract(coreOffset.vec.getPos());
    }

    public void setCore(TileEnergyStorageCore core) {
        BlockPos offset = pos.subtract(core.getPos());
        coreOffset.vec = new Vec3I(offset);
        hasCoreLock.value = true;
        coreDirection = EnumFacing.getFacingFromVector(offset.getX(), offset.getY(), offset.getZ()).getOpposite();
        updateBlock();
    }

    //endregion

    //region Save

    @Override
    public Packet<?> getDescriptionPacket() {
        SPacketUpdateTileEntity packet = (SPacketUpdateTileEntity)super.getDescriptionPacket();
        NBTTagCompound compound = packet.nbt;
        compound.setByte("StructureAxis", (byte) multiBlockAxis.ordinal());
        compound.setByte("CoreDirection", (byte) coreDirection.getIndex());
        return packet;
    }

    @Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
        super.onDataPacket(net, pkt);
        NBTTagCompound compound = pkt.getNbtCompound();

        EnumFacing.Axis[] values = EnumFacing.Axis.values();
        int i = compound.getByte("StructureAxis");
        multiBlockAxis = i >= 0 && i < values.length ? values[i] : EnumFacing.Axis.Y;
        coreDirection = EnumFacing.getFront(compound.getByte("CoreDirection"));
    }

    @Override
    public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newSate) {
        if (oldState.getBlock() != newSate.getBlock()){
            return true;
        }

        boolean wasStab = oldState.getValue(ParticleGenerator.TYPE).endsWith("stabilizer") || oldState.getValue(ParticleGenerator.TYPE).endsWith("stabilizer2");
        boolean isStab = newSate.getValue(ParticleGenerator.TYPE).endsWith("stabilizer") || newSate.getValue(ParticleGenerator.TYPE).endsWith("stabilizer2");

        return wasStab != isStab;
    }

    @Override
    public void writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        if (multiBlockAxis != null){
            compound.setByte("StructureAxis", (byte) multiBlockAxis.ordinal());
            compound.setByte("CoreDirection", (byte) coreDirection.getIndex());
        }
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        EnumFacing.Axis[] values = EnumFacing.Axis.values();
        int i = compound.getByte("StructureAxis");
        multiBlockAxis = i >= 0 && i < values.length ? values[i] : EnumFacing.Axis.Y;
        coreDirection = EnumFacing.getFront(compound.getByte("CoreDirection"));
    }

    //endregion
}
