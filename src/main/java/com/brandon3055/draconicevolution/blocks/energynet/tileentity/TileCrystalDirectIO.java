package com.brandon3055.draconicevolution.blocks.energynet.tileentity;

import com.brandon3055.brandonscore.capability.CapabilityOP;
import com.brandon3055.brandonscore.lib.Vec3D;
import com.brandon3055.brandonscore.lib.datamanager.ManagedBool;
import com.brandon3055.brandonscore.lib.datamanager.ManagedEnum;
import com.brandon3055.brandonscore.utils.EnergyUtils;
import com.brandon3055.draconicevolution.init.DEContent;
import com.brandon3055.brandonscore.api.TechLevel;
import com.brandon3055.draconicevolution.blocks.energynet.EnergyCrystal;
import com.brandon3055.draconicevolution.client.render.effect.CrystalFXIO;
import com.brandon3055.draconicevolution.client.render.effect.CrystalFXBase;
import net.minecraft.block.BlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.List;

import static com.brandon3055.brandonscore.lib.datamanager.DataFlags.SAVE_BOTH_SYNC_TILE;
import static com.brandon3055.brandonscore.lib.datamanager.DataFlags.SAVE_NBT_SYNC_TILE;

/**
 * Created by brandon3055 on 19/11/2016.
 */
public class TileCrystalDirectIO extends TileCrystalBase   {

    public final ManagedEnum<Direction> facing = dataManager.register(new ManagedEnum<>("facing", Direction.DOWN, SAVE_NBT_SYNC_TILE));
    public final ManagedBool outputMode = dataManager.register(new ManagedBool("outputMode", SAVE_BOTH_SYNC_TILE));

    public TileCrystalDirectIO() {
        super(DEContent.tile_crystal_io);
    }

    public TileCrystalDirectIO(TechLevel techLevel) {
        super(DEContent.tile_crystal_io, techLevel);
    }

    //region Update Energy IO

    @Override
    public void tick() {
        super.tick();

        if (level.isClientSide) {
            return;
        }

        TileEntity tile = level.getBlockEntity(worldPosition.relative(facing.get()));

        if (outputMode.get() && tile != null) {
            opStorage.extractOP(EnergyUtils.insertEnergy(tile, opStorage.extractOP(opStorage.getMaxExtract(), true), facing.get().getOpposite(), false), false);
        }
    }

    //endregion

    //region EnergyIO

//    @Override
//    public int receiveEnergy(Direction from, int maxReceive, boolean simulate) {
//        return from != null && !outputMode.get() && from.equals(facing.get()) ? energyStorage.receiveEnergy(maxReceive, simulate) : 0;
//    }
//
//    @Override
//    public int extractEnergy(Direction from, int maxExtract, boolean simulate) {
//        return from != null && outputMode.get() && from.equals(facing.get()) ? energyStorage.extractEnergy(maxExtract, simulate) : 0;
//    }
//
//    @Override
//    public boolean canConnectEnergy(Direction from) {
//        return from != null && from.equals(facing.get());
//    }

    //endregion

    @Override
    public boolean onBlockActivated(BlockState state, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
        if (player.isShiftKeyDown()) {
            outputMode.invert();
            updateRotation(facing.get());
            return true;
        }
        return super.onBlockActivated(state, player, handIn, hit);
    }

    //region Rendering

    @Override
    public EnergyCrystal.CrystalType getCrystalType() {
        return EnergyCrystal.CrystalType.CRYSTAL_IO;
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public CrystalFXBase createStaticFX() {
        return new CrystalFXIO((ClientWorld)level, this);
    }

    @Override
    public Vec3D getBeamLinkPos(BlockPos linkTo) {
        return Vec3D.getCenter(worldPosition);
    }

    @Override
    public boolean renderBeamTermination() {
        return false;
    }

    @Override
    public void addDisplayData(List<ITextComponent> displayList) {
        super.addDisplayData(displayList);
        TextFormatting colour = outputMode.get() ? TextFormatting.GOLD : TextFormatting.DARK_AQUA;
        displayList.add(new TranslationTextComponent("gui.draconicevolution.energy_net.io_output_" + outputMode.get(), colour));
    }

    //endregion

    @Override
    public void onTilePlaced(BlockItemUseContext context, BlockState state) {
        super.onTilePlaced(context, state);
        updateRotation(context.getClickedFace().getOpposite());
    }

    public void updateRotation(Direction newDirection) {
        facing.set(newDirection);
        capManager.remove(CapabilityOP.OP);
        opStorage.setIOMode(!outputMode.get());
        capManager.setSide(CapabilityOP.OP, opStorage, newDirection);
    }

    @Override
    public void readExtraNBT(CompoundNBT compound) {
        super.readExtraNBT(compound);
        updateRotation(facing.get());
    }

    //    public void updateRotation(Direction newDirection) {
//        facing.set(newDirection);
//        updateCapabilityIO();
//    }
//
//    public void updateCapabilityIO() {
//        capManager.remove(CapabilityOP.OP);
//        capManager.setSide(CapabilityOP.OP, new OPIOControl(opStorage).setIOMode(!outputMode.get()), facing.get());
//    }
}
