package com.brandon3055.draconicevolution.common.core.handler;

import java.io.File;

import net.minecraftforge.common.config.Configuration;

public class ConfigHandler
{
	public static void init(File confFile)
	{
		Configuration config = new Configuration(confFile);
		
		config.load();
		
		disableSunDial = config.get("Misc", "'Disable Sun Dial' 0:Default, 1:Disable recipe, 2:Disable compleatly", 0).getInt(0);
		disableXrayBlock = config.get("Misc", "'Disable Distortion Flame' 0:Default, 1:Disable recipe, 2:Disable compleatly", 0).getInt(0);
		teleporterUsesPerPearl = config.get("Misc", "Charm of Dislocation uses per pearl", 10).getInt(10);
		
		config.save();
	}
	
	public static int disableSunDial;
	public static int teleporterUsesPerPearl;
	public static int disableXrayBlock;

}
