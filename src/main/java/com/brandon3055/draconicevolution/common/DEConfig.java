package com.brandon3055.draconicevolution.common;

import com.brandon3055.brandonscore.common.config.ModConfigProperty;

/**
 * Created by brandon3055 on 24/3/2016.
 * This class holds all of the config values for Draconic Evolution
 */
public class DEConfig {

	@ModConfigProperty(category = "World", name = "enableRetroGen", comment = "If set to true ore will be regenerated in previously generated chunks")
	public static boolean enableRetroGen = false;

	@ModConfigProperty(category = "World", name = "disableOreSpawnOverworld", comment = "Disables draconium ore generation in the overworld")
	public static boolean disableOreSpawnOverworld = false;

	@ModConfigProperty(category = "World", name = "disableOreSpawnEnd", comment = "Disables draconium ore generation in the end")
	public static boolean disableOreSpawnEnd = false;

	@ModConfigProperty(category = "World", name = "disableOreSpawnNether", comment = "Disables draconium ore generation in the nether")
	public static boolean disableOreSpawnNether = false;

	@ModConfigProperty(category = "World", name = "generateEnderComets", comment = "Set to false to disable the generation of Ender Comets")
	public static boolean generateEnderComets = true;

	@ModConfigProperty(category = "World", name = "generateChaosIslands", comment = "Set to false to disable the generation of Chaos Islands")
	public static boolean generateChaosIslands = true;

	@ModConfigProperty(category = "World", name = "cometRarity", comment = "Ender Comets have a 1 in {this number} chance to spawn in each chunk")
	public static int cometRarity = 10000;

	@ModConfigProperty(category = "World", name = "chaosIslandSeparation", comment = "This is the distance between chaos islands")
	public static int chaosIslandSeparation = 10000;
}
