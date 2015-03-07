package com.brandon3055.draconicevolution.client.gui.componentguis;

import net.minecraft.util.StatCollector;

/**
 * Created by Brandon on 7/03/2015.
 */
public class ManualPage {

	public String name;
	public String[] imageURLs;
	public String[] content;

	public ManualPage(String name, String[] imageURLs, String[] content)
	{
		this.name = name;
		this.imageURLs = imageURLs;
		this.content = content;
	}

	public String getLocalizedName()
	{
		return (name.contains("item.") || name.contains("tile.")) ? StatCollector.translateToLocal(name) : name.contains("info.") ? name.substring(name.indexOf("info.") + 5) : "Invalid Name Data";
	}


}
