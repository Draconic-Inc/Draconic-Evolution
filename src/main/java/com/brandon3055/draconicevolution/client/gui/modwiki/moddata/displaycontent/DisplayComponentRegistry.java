package com.brandon3055.draconicevolution.client.gui.modwiki.moddata.displaycontent;

import com.brandon3055.draconicevolution.client.gui.modwiki.GuiModWiki;
import com.brandon3055.draconicevolution.client.gui.modwiki.moddata.guidoctree.TreeBranchRoot;
import com.brandon3055.draconicevolution.utils.LogHelper;
import org.w3c.dom.Element;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by brandon3055 on 8/09/2016.
 */
public class DisplayComponentRegistry {

    public static final Map<String, IDisplayComponentFactory> REGISTRY = new LinkedHashMap<String, IDisplayComponentFactory>();

    static {
        register(new DCHeading.Factory());
        register(new DCTextArea.Factory());
        register(new DCImage.Factory());
        register(new DCLink.Factory());
//        register(new DCRecipe.Factory());
        register(new DCStack.Factory());
        register(new DCVSpacer.Factory());
        register(new DCSplitContainer.Factory());
    }

    private static void register(IDisplayComponentFactory factory) {
        REGISTRY.put(factory.getID(), factory);
    }

    public static DisplayComponentBase createComponent(GuiModWiki guiWiki, String componentID, Element element, TreeBranchRoot branch) {
        if (!REGISTRY.containsKey(componentID)) {
            LogHelper.error("Found Unknown Content Type: %s in branch: %s", componentID, branch.branchID);
//            DCHeading error = new DCHeading(guiWiki, "heading", branch);
//            error.displayString = "[Error]: Broken Display Component...";
//            error.colour = 0xFF0000;
//            error.alignment = EnumAlignment.LEFT;
//            error.setWorldAndResolution(guiWiki.mc, 0, 0);
//            error.element = element;
//            error.posIndex = -1;
            return null;
        }

//        LogHelper.dev("Loading Content: " + componentID);
        DisplayComponentBase displayComponent = REGISTRY.get(componentID).createNewInstance(guiWiki, branch);
        try {
            displayComponent.loadFromXML(element);
        }
        catch (Exception e) {
            LogHelper.error("An error occurred while loading %s element for branch: %s", componentID, branch.branchID);
            e.printStackTrace();
            return null;
        }
        return displayComponent;
    }
}
