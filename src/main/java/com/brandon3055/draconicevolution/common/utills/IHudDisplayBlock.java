package com.brandon3055.draconicevolution.common.utills;

import net.minecraft.world.World;

import java.util.List;

/**
 * Created by Brandon on 27/01/2015.
 */
public interface IHudDisplayBlock {
	List<String> getDisplayData(World world, int x, int y, int z);
}
