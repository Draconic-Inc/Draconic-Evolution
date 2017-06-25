package com.brandon3055.draconicevolution.client.gui.modwiki.moddata;

import org.w3c.dom.Element;

/**
 * Created by brandon3055 on 30/08/2016.
 * Content page is a page of information about some mod content.
 */
@Deprecated //TODO Delete. I am just leaving this here incase i find a use for it
public class ModContentPage {

    public String entryID;
    public String readableName;
    public String category;


    //TODO generalize this do it can be used for both data and sub-data... Unless its easier to add a separate sub-data class... It may be..

    public ModContentPage() {
    }


    //region Save / Load

    public void loadData(Element entry) {
        entryID = entry.getAttribute("entryid");
        readableName = entry.getAttribute("name");
        category = entry.getAttribute("category");

        for (int i = 0; i < entry.getChildNodes().getLength(); i++) {

        }

    }

    public void saveData(Element entry) {

    }

    @Override
    public String toString() {
        return String.format("ModDataEntry: [id: %s, name: %s, category: %s", entryID, readableName, category);
    }

    //endregion
}
