package com.brandon3055.draconicevolution.blocks.reactor.tileentity;

import com.brandon3055.brandonscore.blocks.TileBCBase;
import com.brandon3055.brandonscore.lib.Vec3I;
import com.brandon3055.brandonscore.network.wrappers.SyncableBool;
import com.brandon3055.brandonscore.network.wrappers.SyncableEnum;
import com.brandon3055.brandonscore.network.wrappers.SyncableString;
import com.brandon3055.brandonscore.network.wrappers.SyncableVec3I;
import com.brandon3055.brandonscore.utils.FacingUtils;
import com.brandon3055.draconicevolution.DraconicEvolution;
import com.brandon3055.draconicevolution.GuiHandler;
import com.brandon3055.draconicevolution.utils.LogHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumFacing.Axis;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;

/**
 * Created by brandon3055 on 6/11/2016.
 */
public class TileReactorCore extends TileBCBase implements ITickable{

    //Invalid position is 0, 0, 0
    private final SyncableVec3I[] componentPositions = new SyncableVec3I[6];
    private final SyncableEnum<Axis> stabilizerAxis = new SyncableEnum<>(Axis.Y, true, false);
    public final SyncableBool structureValid = new SyncableBool(false, true, false);
    public final SyncableEnum<ReactorState> reactorState =  new SyncableEnum<>(ReactorState.COLD, true, false);
    public final SyncableString structureError = new SyncableString("", true, false);

    public TileReactorCore() {
        for (int i = 0; i < componentPositions.length; i++) {
            registerSyncableObject(componentPositions[i] = new SyncableVec3I(new Vec3I(0, 0, 0), true, false));
        }

        registerSyncableObject(stabilizerAxis);
        registerSyncableObject(structureValid);
        registerSyncableObject(reactorState);
        registerSyncableObject(structureError);
    }

    //region Rendering

    @Override
    public boolean shouldRenderInPass(int pass) {
        return true;
    }

    //endregion

    //region Update Logic

    @Override
    public void update() {

    }

    //endregion

    //region ############## User Interaction ###############

    public void onComponentClicked(EntityPlayer player, TileReactorComponent component) {
        if (!worldObj.isRemote) {
            player.openGui(DraconicEvolution.instance, GuiHandler.GUIID_REACTOR, worldObj, pos.getX(), pos.getY(), pos.getZ());
        }
    }

    //endregion ############################################

    //region ################# Multi-block #################

    /**
     * Called when the core is poked by a reactor component.
     * If the structure is already initialized this validates the structure.
     * Otherwise it attempts to initialize the structure.
     * @param component The component that poked the core.
     */
    public void pokeCore(TileReactorComponent component, EnumFacing pokeFrom) {
        LogHelper.dev("Reactor: Core Poked");
        if (structureValid.value) {
            //If the component is an unbound injector and there is no component bound on the same side then bind it.
            if (component instanceof TileReactorEnergyInjector && !component.isBound.value) {
                TileEntity tile = worldObj.getTileEntity(getOffsetPos(componentPositions[pokeFrom.getIndex()].vec));
                if (tile == this) {
                    componentPositions[pokeFrom.getIndex()].vec = getOffsetVec(component.getPos());
                    component.bindToCore(this);
                    LogHelper.dev("Reactor: Injector Added!");
                }
            }

            validateStructure();
        }
        else {
            attemptInitialization();
        }
    }

    /**
     * Called when a component is physically broken
     */
    public void componentBroken(TileReactorComponent component, EnumFacing componentSide) {
        if (!structureValid.value) {
            return;
        }

        if (component instanceof TileReactorEnergyInjector) {
            LogHelper.dev("Reactor: Component broken! (Injector)");
            TileEntity tile = worldObj.getTileEntity(getOffsetPos(componentPositions[componentSide.getIndex()].vec));

            if (tile == component || tile == null) {
                LogHelper.dev("Reactor: -Removed");
                componentPositions[componentSide.getIndex()].vec.set(0, 0, 0);
            }
        }
        else if (reactorState.value != ReactorState.COLD){
            LogHelper.dev("Reactor: Component broken, Structure Invalidated (Unsafe!!!!)");
            //TODO Make big explosion!!!! (If the reactor was running of course)
            structureValid.value = false;
        }
        else {
            LogHelper.dev("Reactor: Component broken, Structure Invalidated (Safe)");
            structureValid.value = false;
        }
    }

    //region Initialization

    /**
     * Will will check if the structure is valid and if so will initialize the structure.
     */
    public void attemptInitialization() {
        LogHelper.dev("Reactor: Attempt Initialization");

        if (!findComponents()) {
            return;
        }

        if (!checkStabilizerAxis()) {
            return;
        }

        if (!bindComponents()) {
            return;
        }

        structureValid.value = true;
        LogHelper.dev("Reactor: Structure Successfully Initialized!\n");
    }

    /**
     * Finds all Reactor Components available to this core and
     * @return true if exactly 4 stabilizers were found.
     */
    public boolean findComponents() {
        LogHelper.dev("Reactor: Find Components");
        int stabilizersFound = 0;
        for (EnumFacing facing : EnumFacing.VALUES) {
            componentPositions[facing.getIndex()].vec.set(0, 0, 0);
            //Check up to 16 blocks in each direction
            for (int i = 1; i < 16; i++) {
                BlockPos searchPos = pos.offset(facing, i);

                if (!worldObj.isAirBlock(searchPos)) {
                    TileEntity searchTile = worldObj.getTileEntity(searchPos);
                    LogHelper.dev("Reactor: -Found: " + searchTile);

                    if (searchTile instanceof TileReactorComponent && ((TileReactorComponent) searchTile).facing.value == facing.getOpposite() && i >= 2) {
                        componentPositions[facing.getIndex()].vec = getOffsetVec(searchPos);
                    }

                    if (searchTile instanceof TileReactorStabilizer) {
                        stabilizersFound++;
                    }

                    break;
                }
            }
        }

        return stabilizersFound == 4;
    }

    /**
     * Checks the layout of the stabilizers and sets the stabilizer axis accordingly.
     * @return true if the stabilizer configuration is valid.
     */
    public boolean checkStabilizerAxis() {
        LogHelper.dev("Reactor: Check Stabilizer Axis");
        for (Axis axis : Axis.values()) {
            boolean axisValid = true;
            for (EnumFacing facing : FacingUtils.getFacingsAroundAxis(axis)) {
                TileEntity tile = worldObj.getTileEntity(getOffsetPos(componentPositions[facing.getIndex()].vec));
                //The facing check should not be needed here but does not heart to be to careful.
                if (!(tile instanceof TileReactorStabilizer && ((TileReactorStabilizer) tile).facing.value == facing.getOpposite())) {
                    axisValid = false;
                    break;
                }
            }

            if (axisValid) {
                stabilizerAxis.value = axis;
                LogHelper.dev("Reactor: -Found Valid Axis: " + axis);
                return true;
            }
        }

        return false;
    }

    /**
     * At this point we know there are at least 4 stabilizers in a valid configuration and possibly some injectors.
     * This method binds them to the core.
     * @return false if failed to bind all 4 stabilizers. //Just in case...
     */
    public boolean bindComponents() {
        LogHelper.dev("Reactor: Binding Components");
        int stabilizersBound = 0;
        for (int i = 0; i < 6; i++) {
            TileEntity tile = worldObj.getTileEntity(getOffsetPos(componentPositions[i].vec));
            if (tile instanceof TileReactorComponent) {
                ((TileReactorComponent) tile).bindToCore(this);

                if (tile instanceof TileReactorStabilizer) {
                    stabilizersBound++;
                }
            }
        }

        return stabilizersBound == 4;
    }

    //endregion

    //region Structure Validation

    /**
     * Checks if the structure is still valid and carries out the appropriate action if it is not.
     */
    public boolean validateStructure() {
        LogHelper.dev("Reactor: Validate Structure");
        for (EnumFacing facing : FacingUtils.getFacingsAroundAxis(stabilizerAxis.value)) {
            BlockPos pos = getOffsetPos(componentPositions[facing.getIndex()].vec);
            if (!worldObj.getChunkFromBlockCoords(pos).isLoaded()) {
                return true;
            }

            TileEntity tile = worldObj.getTileEntity(pos);
            LogHelper.dev("Reactor: Validate Stabilizer: " + tile);
            if (!(tile instanceof TileReactorStabilizer) || !((TileReactorStabilizer) tile).getCorePos().equals(this.pos)) {
                LogHelper.dev("Reactor: Structure Validation Failed!!!");
                return false;
            }
        }

//Dont think i need to do anything with the injectors.
//        for (EnumFacing facing : FacingUtils.getAxisFaces(stabilizerAxis.value)) {
//            TileEntity tile
//        }
        LogHelper.dev("Reactor: Structure Validated!");
        return true;
    }

    //endregion

    //region Getters & Setters

    private BlockPos getOffsetPos(Vec3I vec){
        return pos.subtract(vec.getPos());
    }

    private Vec3I getOffsetVec(BlockPos offsetPos) {
        return new Vec3I(pos.subtract(offsetPos));
    }

    //endregion

    //endregion ############################################

    public enum ReactorState {
        INVALID,
        COLD,
        WARMING_UP,
        AT_TEMP,
        RUNNING,
        COOLING;
    }
}
