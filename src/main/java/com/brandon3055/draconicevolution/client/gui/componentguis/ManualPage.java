package com.brandon3055.draconicevolution.client.gui.componentguis;

import net.minecraft.util.StatCollector;

import org.apache.commons.io.FilenameUtils;

import com.brandon3055.draconicevolution.common.utills.LogHelper;

/**
 * Created by Brandon on 7/03/2015.
 */
public class ManualPage {

    public String name;
    public String nameL;
    public int meta;
    public String[] imageURLs;
    public String[] content;
    public int scrollOffset = 0;

    public ManualPage(String name, String[] imageURLs, String[] content) {
        this.name = name;
        this.imageURLs = imageURLs;
        this.content = content;
    }

    public ManualPage(String name, String[] imageURLs, String[] content, String nameL, int meta) {
        this(name, imageURLs, content);
        this.nameL = nameL;
        this.meta = meta;
    }

    public String getImageResourceName(String url) {
        // try
        // {
        LogHelper.info(url);
        return FilenameUtils.getName(url);
        // String fileName = new URL(url).getFile();
        // return fileName.substring(fileName.indexOf("/") + 1);
        // }
        // catch (MalformedURLException e)
        // {
        // e.printStackTrace();
        // return "";
        // }
    }

    public String getLocalizedName() {
        if (nameL != null) return nameL;
        return (name.contains("item.") || name.contains("tile.")) ? StatCollector.translateToLocal(name + ".name")
                : name.contains("info.") ? name.substring(name.indexOf("info.") + 5) : "Invalid Name Data";
    }
}
