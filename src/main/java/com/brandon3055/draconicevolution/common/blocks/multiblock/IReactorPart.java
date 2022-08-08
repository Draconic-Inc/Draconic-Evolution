package com.brandon3055.draconicevolution.common.blocks.multiblock;

/**
 * Created by Brandon on 23/7/2015.
 */
public interface IReactorPart {
    public static int RMODE_TEMP = 0;
    public static int RMODE_TEMP_INV = 1;
    public static int RMODE_FIELD = 2;
    public static int RMODE_FIELD_INV = 3;
    public static int RMODE_SAT = 4;
    public static int RMODE_SAT_INV = 5;
    public static int RMODE_FUEL = 6;
    public static int RMODE_FUEL_INV = 7;

    public MultiblockHelper.TileLocation getMaster();

    public void shutDown();

    public boolean checkForMaster();

    public boolean isActive();

    public String getRedstoneModeString();

    public void changeRedstoneMode();

    public int getRedstoneMode();
}
