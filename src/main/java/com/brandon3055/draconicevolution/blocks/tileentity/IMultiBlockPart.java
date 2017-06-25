package com.brandon3055.draconicevolution.blocks.tileentity;

@Deprecated
public interface IMultiBlockPart {

    /**
     * @return true if the multiblock structure is valid.
     */
    boolean isStructureValid();

    /**
     * Tells the master to check if the structure is still valid and take appropriate action if the structure is no longer valid.
     *
     * @return true if the structure is still valid.
     */
    boolean validateStructure();

    /**
     * @return the controller for the structure or null if the controller can not be found
     */
    IMultiBlockPart getController();

}