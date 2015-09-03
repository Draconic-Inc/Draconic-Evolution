package com.brandon3055.draconicevolution.common.lib;

/**
 * Created by Brandon on 24/02/2015.
 */
public class VersionHandler
{
	public static final String VERSION 		= "1.0.2";
	public static final int SNAPSHOT		= 3;
	public static final String MCVERSION 	= "1.7.10";

	public static final String FULL_VERSION	= VERSION + (SNAPSHOT > 0 ? "-snapshot_" + SNAPSHOT : "");
}
