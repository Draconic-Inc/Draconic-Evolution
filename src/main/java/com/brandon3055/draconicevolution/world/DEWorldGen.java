package com.brandon3055.draconicevolution.world;

import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.structure.templatesystem.BlockMatchTest;
import net.minecraft.world.level.levelgen.structure.templatesystem.RuleTest;

/**
 * Created by brandon3055 on 04/11/2022
 *
 * /fill ~ 0 ~ ~40 20 ~37 minecraft:air replace #minecraft:base_stone_nether
 * /fill ~ -60 ~ ~33 -32 ~32 minecraft:air replace #minecraft:base_stone_overworld
 */
public class DEWorldGen {
    public static final RuleTest END_STONE = new BlockMatchTest(Blocks.END_STONE);
}
