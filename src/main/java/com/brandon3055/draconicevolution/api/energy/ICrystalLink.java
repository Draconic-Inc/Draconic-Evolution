package com.brandon3055.draconicevolution.api.energy;

import com.brandon3055.brandonscore.lib.Vec3D;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * Created by brandon3055 on 25/11/2016.
 * Used by energy crystals in DE It should be possible to add custom crystals or blocks that can be linked to crystals using this interface.
 * TODO WAILA support
 * <p>
 * This interface is for tile entities only!
 */
public interface ICrystalLink {

    /**
     * @return the positions of all linked crystals or ICrystalLink's.
     */
    @Nonnull
    List<BlockPos> getLinks();

//     * @param returnLink If true this is a return link call meaning this was called by another crystals binderUsed method.
//     * (This is to avoid a stack overflow from 2 crystals infinitely calling each others binderUsed methods)

    /**
     * Called when a player clicks another block with a binder that is linked to this block.
     *
     * @param player     The player using the binder.
     * @param linkTarget The clicked block pos. (Can be any block. Whatever block the player clicked on)
     * @return true if a successful operation occurred. (Controls hand swing)
     */
    boolean binderUsed(Player player, BlockPos linkTarget, Direction sideClicked);

    /**
     * Used to create a ONE WAY link to another crystal. As one way links between ICrystalLink's are not allowed
     * this should be called for both ICrystalLink. This is used by binderUsed to create a link.
     * At this point we can assume that the crystals are in range of each other and both have atleast 1 free link.
     * Those checks are done by binderUsed.
     *
     * @param otherCrystal The crystal to link to.
     * @return true if the link was successful.
     */
    boolean createLink(ICrystalLink otherCrystal);

    /**
     * Break the link between this crystal and the target crystal.
     * Be sure to call this for both sides of the link to avoid ending up with invalid links!!
     *
     * @param otherCrystal The position of the linked crystal. (Note: The other crystal may not exist!)
     */
    void breakLink(BlockPos otherCrystal);

    /**
     * 0 = Fill - accept all available energy until full.<br>
     * 1 = Balance - balance capacity with connected crystals.<br>
     * 2 = Drain - drain all energy from internal buffer into connected network until empty or network is full.<br><br>
     * <p>
     * This should always be 1 for all types or energy crystal. The only time this would not be 1 is if you were implementing
     * this interface on some other tile that is not a crystal. All crystals even directIO crystals should balance
     */
    int balanceMode();

    /**
     * "Link" Refers to a link between 2 crystals.
     */
    int maxLinks();

    /**
     * The max allowed distance between this block and the block you are attempting to link to.
     * Both crystals need to be within their limit in order to link.
     * The max range is limited to 127 due to offsets being stored as bytes.
     */
    int maxLinkRange();

    /**
     * Returns the amount of energy currently stored.
     */
    long getEnergyStored();

    /**
     * Returns the maximum amount of energy that can be stored.
     */
    long getMaxEnergyStored();

    /**
     * Simply pass this through to you {@link com.brandon3055.brandonscore.api.power.OPStorage} modifyEnergyStored method.
     * If you dont use cofh EnergyStorage then simply copy the behaviour of EnergyStorage.modifyEnergyStored with your energy implementation.
     */
    void modifyEnergyStored(long energy);

    /**
     * Return the point which a beam connecting to this device from the given target device location should end.
     * E.g. in relays this calculates the position around the crystal which the beam connects to.
     *
     * @param linkTo The location of the device at the other end of the link.
     * @return Return the point at which a beam connecting to this device should connect.
     */
    Vec3D getBeamLinkPos(BlockPos linkTo);

    /**
     * @return true if a beam connecting to this device should render a "Termination" particle at the end.
     */
    boolean renderBeamTermination();
}
