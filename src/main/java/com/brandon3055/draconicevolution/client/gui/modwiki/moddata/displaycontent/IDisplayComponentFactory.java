package com.brandon3055.draconicevolution.client.gui.modwiki.moddata.displaycontent;

import com.brandon3055.draconicevolution.client.gui.modwiki.GuiModWiki;
import com.brandon3055.draconicevolution.client.gui.modwiki.moddata.guidoctree.TreeBranchRoot;

/**
 * Created by brandon3055 on 8/09/2016.
 */
public interface IDisplayComponentFactory {

    DisplayComponentBase createNewInstance(GuiModWiki guiWiki, TreeBranchRoot branch);

    String getID();
}

